import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.*;

import play.mvc.*;
import play.test.*;
import play.data.DynamicForm;
import play.data.validation.ValidationError;
import play.data.validation.Constraints.RequiredValidator;
import play.i18n.Lang;
import play.libs.F;
import play.libs.F.*;
import play.twirl.api.Content;

import play.*;
import play.mvc.*;
import play.data.*;
import java.util.*;
import views.html.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;

import static play.test.Helpers.*;
import static org.junit.Assert.*;

import play.db.Database;
import play.db.Databases;
import play.db.evolutions.*;
import java.sql.Connection;
import com.google.common.collect.ImmutableMap;

/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {
    
    @Test
    public void submitBidTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                models.Auctions auction = new models.Auctions();
                controllers.Application test = new controllers.Application();

                models.Artworks art = models.Artworks.find.byId(1L);
                Long originalBid = art.auction.currentBid;
                Long originalBidCount = art.auction.bidCount;
                models.Users originalHighBidder = art.auction.userWithHighBid;
                
                models.Users newBidder=models.Users.findByEmail("test@test.com");
                
                controllers.Application.Index index = new controllers.Application.Index();
                index.artId = art.artid;
                Form<controllers.Application.Index> indexForm = Form.form(controllers.Application.Index.class);

                //check bid below high bid
                index.bid=0L;
                indexForm = indexForm.fill(index);
        
                test.submitBid(art,newBidder,indexForm);
 
                assertEquals((Long) art.auction.currentBid,(Long) originalBid);
                assertEquals((Long) art.auction.bidCount,(Long) originalBidCount);
                assertEquals((models.Users) art.auction.userWithHighBid, (models.Users) originalHighBidder);
                
                
                
                //check bid equal to high bid
                index.bid=originalBid;
                indexForm = indexForm.fill(index);
        
                test.submitBid(art,newBidder,indexForm);
 
                assertEquals((Long) art.auction.currentBid,(Long) originalBid);
                assertEquals((Long) art.auction.bidCount,(Long) originalBidCount);
                assertEquals((models.Users) art.auction.userWithHighBid, (models.Users) originalHighBidder);
                
                
                
                //check bid above high bid
                index.bid = originalBid + 25;
                indexForm = indexForm.fill(index);
        
                test.submitBid(art,newBidder,indexForm);
 
                assertEquals((Long) art.auction.currentBid,(Long) (originalBid+25));
                assertEquals((Long) art.auction.bidCount,(Long) (originalBidCount+1));
                assertEquals((models.Users) art.auction.userWithHighBid, (models.Users) newBidder);
                
                art.auction.bidCount = originalBidCount;
                art.auction.userWithHighBid = originalHighBidder;
                art.save();
            }
        });
    }
    
    @Test
    public void upvoteTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                controllers.Application test = new controllers.Application();
                models.Artworks art = models.Artworks.find.byId(1L);
                models.Users user = models.Users.findByEmail("test@test.com");
                
                // test initial conditions are correct
                assertEquals(art.votes, 11);
                assertTrue(!art.users.contains(user));
                assertEquals(user.username, "TestingSubmitBid");
                
                test.upvote(art, user);
                
                // test that upvote works
                assertEquals(art.votes, 12);
                assertTrue(art.users.contains(user));
                
                test.upvote(art, user);
                
                // test that removing upvote works
                assertEquals(art.votes, 11);
                assertTrue(!art.users.contains(user));
            }
        });
    }
    
   // @Test
   // public void testCallIndex() {
   //     Result result = callAction(controllers.routes.ref.Application.index(),new FakeRequest(GET, "/"));
  //      assertThat(status(result)).isEqualTo(OK);
  //  }

//    @Test
//    public void renderTemplate() {
  //      Content html = views.html.index.render();
 //       assertEquals("text/html", contentType(html));
//        assertTrue(contentAsString(html).contains("Your new application is ready."));
//    }

}
