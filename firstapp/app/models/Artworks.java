package models;

import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class Artworks extends Model {
    
    @Id
    public Long artid;
    
    
    public Long uid;
    public String filePath;
    public String title;
    public int votes;
    public int votedOn;
    
    //@OneToMany(cascade = CascadeType.ALL)
    //public List<Users> users;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "uid")
    public Users user;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "aucId")
    public Auctions auction;
    
     public static Finder<Long,Artworks> find = new Finder<Long,Artworks>(
    Long.class, Artworks.class
  ); 
    
}
