package models;

import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class Auctions extends Model {
    
    @Id
    public Long aucId;
    
    public String openDate;
    public String closeDate;
    public Long bidCount;
    public Long currentBid;
    public int ended;
    
    
    @OneToOne(cascade = CascadeType.ALL)
    public Artworks artwork;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userWithHighBid")
    public Users userWithHighBid;
    
}