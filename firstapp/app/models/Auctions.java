package models;

import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class Auctions extends Model {
    
    @Id
    public Long aucId;
    
    
    public Long artid;
    public String openDate;
    public String closeDate;
    public Long bidCount;
    public Long currentBid;
    public String haveHighBid;
    
    
    @OneToOne(cascade = CascadeType.ALL)
    public Artworks artwork;
    
    
    
}