package controllers;

import play.*;
import play.mvc.*;
import models.*;
import play.data.*;
import java.util.*;
import views.html.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.io.File;
import play.data.validation.Constraints.*;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;


public class Application extends Controller {


    public Result index() {
        List<Artworks> arts = Artworks.find.where().orderBy("votes desc").setMaxRows(9).findList();
        return ok(index.render(arts, Form.form(Index.class)));
    }

    public Result secureIndex(String email) {
            List<Artworks> arts = Artworks.find.where().orderBy("votes desc").setMaxRows(9).findList();
            for (int i=0; i< arts.size(); i++){
                String auctionEndDate = arts.get(i).auction.closeDate;
                DateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
                try{
                    Date result = df.parse(auctionEndDate);
                    Date today = new Date();
                    if(arts.get(i).auction.ended==0 && today.after(result)){
                        arts.get(i).auction.ended=1;
                        arts.get(i).auction.save();
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
            return ok(secureIndex.render(arts, Form.form(Index.class), Users.findByEmail(email)));
   }

   public Result artistIndex() {
            Form<Artist> artistForm = Form.form(Artist.class).bindFromRequest();
            Long uid = artistForm.get().uid;
            System.out.println("uid: "+ uid);
            Users artist = Users.findByUid(uid);

            List<Artworks> arts = Artworks.find.where().eq("uid", uid).orderBy("votes desc").setMaxRows(9).findList();
            for (int i=0; i< arts.size(); i++){
                String auctionEndDate = arts.get(i).auction.closeDate;
                System.out.println(auctionEndDate);

                DateFormat df = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);
                try{
                    Date result = df.parse(auctionEndDate);
                    Date today = new Date();
                    if(arts.get(i).auction.ended==0 && today.after(result)){
                        arts.get(i).auction.ended=1;
                        arts.get(i).auction.save();
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
            return ok(artistIndex.render(arts, Form.form(Index.class), artist));

    }

	public Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
            if (loginForm.hasErrors()) {
                System.out.println("Failure");
                return badRequest(login.render(loginForm));
            }
            else {
            session().clear();
            Users x = Users.authenticate(loginForm.get().email, loginForm.get().password);
            System.out.println(x.email);
            System.out.println(x.username);
            System.out.println("Success");

            return redirect(
                routes.Application.secureIndex(loginForm.get().email)
            );
        }
    }

    public Result login() {
        return ok(
            login.render(Form.form(Login.class))
        );
    }

    public Result register() {
        return ok(
            register.render(Form.form(Register.class))
        );
    }

    public Result uploadPage() {
        return ok(
            upload.render(Form.form(Upload.class))
        );
    }

    public static class Upload {
        public String title;
        public File picture;
    }

    public Result upload() {
        Form<Upload> uploadForm = Form.form(Upload.class).bindFromRequest();
        File file = uploadForm.get().picture;
        // get title
        // upload to s3
        // add artwork filepath and title to db
        System.out.println(file);

        return ok("File uploaded");
    }

    public static class Artist {
        public long uid;
	    public String email;
	    public String password;
	    public String username;
    }

    public static class Register {
        @Required
        public String email;
        @Required
        public String username;
        @Required
	    public String password;
        public String validate() {
            System.out.println(email);
            if (Users.findByEmail(email) == null && email != null) {
                return null;
            }
            return "Account with email already exists";
        }
	}

    public static class Login {

	    public String email;
	    public String password;
	    public String username;
        public String validate() {
            if (Users.authenticate(email, password) == null) {
                return "Invalid user or password";
            }
            return null;
        }
	}

	public static class Index {
	    public Long bid;
	    public Long artId;
	}

    public Result click(String flag){
        Form<Index> indexForm = Form.form(Index.class).bindFromRequest();
        Long artId = indexForm.get().artId;
        Artworks art = Artworks.find.byId(artId);
        Users user = Users.findByEmail(session("email"));
        if (flag.equals("upvote")){
            return upvote(art, user);
        } else {
            return submitBid(art, user, indexForm);
        }
    }

    public Result upvote(Artworks art, Users user){
        if (!art.users.contains(user)){
            art.votes++;
            art.users.add(user);
            art.save();
        } else {
            art.votes--;
            art.users.remove(user);
            art.save();
        }
        return redirect(
                routes.Application.secureIndex(user.email)
        );
    }

    public Result submitBid(Artworks art, Users user, Form<Index> indexForm){
        Long bid = indexForm.get().bid;
        if (art.auction.currentBid<bid){
            art.auction.currentBid=bid;
            art.auction.userWithHighBid=user;
            art.auction.bidCount++;
            art.auction.save();
        }
        return redirect(
                routes.Application.secureIndex(user.email)
        );
    }

    public Result addUser() {
        Form<Register> registrationForm = Form.form(Register.class).bindFromRequest();
        if (registrationForm.hasErrors()) {
            System.out.println("Failure");
            return badRequest(register.render(registrationForm));
        } else {
            Users.createUser(registrationForm.get().email, registrationForm.get().username, registrationForm.get().password);
            System.out.println(registrationForm.get().email);
            System.out.println(registrationForm.get().username);
            System.out.println(registrationForm.get().password);

            System.out.println("Success");

            return redirect(
                routes.Application.index()
            );
        }
    }
}
