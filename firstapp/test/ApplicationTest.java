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
    
    @Test
    public void authenticateTest() { 
        running(fakeApplication(), new Runnable() {
            public void run() {
            
            //define testUser 
            models.Users testUser = new models.Users();
            models.Users testUserError = new models.Users();
            testUser.username = "TestingSubmitBid";
            testUser.password = "test";
            testUser.email = "test@test.com";
            testUserError.username = "TestingSubmitBidError";
            testUserError.password = "testError";
            testUserError.email = "testError@test.com";
            
            
            //////////////////////////////////////////////////////////////////////////
            //test 1: basic test (findByEmail()) 
            //tests that findByEmail returns correct User when 
            //valid email is inputed into the database
            models.Users resultUser = new models.Users();

            
            resultUser = models.Users.findByEmail(testUser.email);
            System.out.println(("\ntest: " + (String) testUser.email) 
                + "\tresult: " + (String) resultUser.email);
            //correct credentials
            assertEquals((String) resultUser.username, (String) testUser.username);
            assertEquals((String) resultUser.password, (String) testUser.password);
            assertEquals((String) resultUser.email, (String) testUser.email);
            //incorrect credentials
            resultUser = models.Users.findByEmail(testUserError.email);
            assertNull(resultUser);
            
            
            //////////////////////////////////////////////////////////////////////////
            //test 2: User.authenticate() method test
            //tests that Users.authenticate() returns correct User when 
            //valid email is inputed into the database
            resultUser = new models.Users();    //reset resultUser
            System.out.println((String) resultUser.email);  //check that it is null
            
            resultUser = models.Users.authenticate(testUser.email, testUser.password);
            System.out.println(("\ntest: " + (String) testUser.email) 
                + "\tresult: " + (String) resultUser.email);
            //correct credentials 
            assertEquals((String) resultUser.username, (String) testUser.username);
            assertEquals((String) resultUser.password, (String) testUser.password);
            assertEquals((String) resultUser.email, (String) testUser.email);
            //incorrect credentials
            resultUser = models.Users.authenticate(testUserError.email, testUserError.password);
            assertNull(resultUser);
            
            
            
            //////////////////////////////////////////////////////////////////////////
            //test 3: form & Application.authenticate() testing
            //tests that Application.authenticate returns the correct 
            //result when a user attempts to log in
            //
            //tests for correct password, incorrect password, and incorrect username
            resultUser = new models.Users();    //reset resultUser

            controllers.Application test = new controllers.Application();
            controllers.Application.Login loginObject = 
                new controllers.Application.Login();
            
            loginObject.email = testUser.email;  //populate email field for matching
            
            Form<controllers.Application.Login> loginForm =
                Form.form(controllers.Application.Login.class);
                
            //check when name is correct
            loginObject.password = testUser.password;    //correct password
            loginForm = loginForm.fill(loginObject);
                

            // //check when form credentials are correct 
            assertTrue(!(loginForm.hasErrors()));
            resultUser = models.Users.authenticate(loginForm.get().email, loginForm.get().password);
            assertEquals((String) resultUser.username, (String) testUser.username);
            assertEquals((String) resultUser.password, (String) testUser.password);
            
            //check when form credentials are not correct
            loginObject.password = testUserError.password;    //incorrect password
            loginForm = loginForm.fill(loginObject);
            assertTrue(!(loginForm.hasErrors()));
            resultUser = models.Users.authenticate(loginForm.get().email, loginForm.get().password);
            assertNull(resultUser);
            
            }
        });
    }
    
    @Test
    public void addUserTest(){
        running(fakeApplication(), new Runnable() {
            public void run() {
                controllers.Application test = new controllers.Application();
                Form<controllers.Application.Register> form = Form.form(controllers.Application.Register.class);
                controllers.Application.Register register = new controllers.Application.Register();
                String email = "test1@test1.com";
                register.email = email;
                register.username = "test1";
                register.password = "test1";
                form = form.fill(register);
                // test.addUser(form); - TODO
                
                models.Users.createUser(form.get().email, form.get().username, form.get().password);
                
                // test that creating a user in the database works
                assertTrue(models.Users.findByEmail(email) != null);
                
                models.Users.findByEmail(email).delete(); // FAILS HERE
    
                // test that adding a user works - TODO
                
                // test that adding a user with empty fields does not work - TODO
    
            }
        });
    }
}
