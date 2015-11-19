package models;

import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class Comments extends Model {
    
    @Id
    public Long commentID;
    
    
    public Long uid;
    public Long artid;
    public String content;

    
    
}
