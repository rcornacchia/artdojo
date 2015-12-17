package models;

import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;
import play.data.format.*;
import play.data.validation.Constraints.*;


@Entity
public class Users extends Model {

    @Id
    public Long uid;


    public String username;
    public String password;
    public String email;

    @OneToMany(cascade = CascadeType.ALL)
    public List<Artworks> artworks;

    public static Finder<Long,Users> find = new Finder<Long,Users>(Long.class, Users.class);


    public static Users findByEmail(String email) {
        return find.where().eq("email", email).findUnique();
    }

    public static Users findByUid(Long uid) {
        return find.where().eq("uid", uid).findUnique();
    }




    public static Users authenticate(String email, String password) {
        System.out.print("Email: ");
        System.out.println(email);

        System.out.print("Password: ");
        System.out.println(password);
        Long id = 1L;

        Users x = find.where().eq("email", email)
        		.eq("password", password).findUnique();
        System.out.println(x);
        return x;
    }

    public static Users createUser(String email, String username, String password) {
        Users newUser = new Users();
        newUser.email = email;
        newUser.username = username;
        newUser.password = password;
        newUser.save();
        return newUser;
    }

}
