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

    public static class Register {

	    public String email;
        public String username;
	    public String password;
        public String validate() {
            System.out.println(email);
            if (Users.findByEmail(email) == null) {
                return null;
            }
            return "Account with email already exists";
        }

	}

    public static class Login {
	    public String email;
	    public String password;
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

            session("email", registrationForm.get().email);
            return redirect(
                routes.Application.index()
            );
        }

    }
    

    public Result authenticate() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
            if (loginForm.hasErrors()) {
                System.out.println("Failure");
                return badRequest(login.render(loginForm));
            } else {
            session().clear();
            Users x = Users.authenticate(loginForm.get().email, loginForm.get().password);
            System.out.println(x.email);
            System.out.println(x.username);
            System.out.println(x.password);

            System.out.println("Success");

            session("email", loginForm.get().email);
            return redirect(
                routes.Application.secureIndex(loginForm.get().email)
            );
        }
    }
}
