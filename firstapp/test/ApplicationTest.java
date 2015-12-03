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


/**
*
* Simple (JUnit) tests that can call all parts of a play app.
* If you are interested in mocking a whole application, see the wiki for more details.
*
*/
public class ApplicationTest {

    @Test
    public void simpleCheck() {
        int a = 1 + 1;
        assertEquals(2, a);
    }
    
    @Test
    public void submitBitTest(){ //should do this with in memory so don't change db???
       // Form<controllers.Application.Index> indexForm = new Form(); 
        //indexForm = indexForm.fill(new controllers.Application.Index());
        Long bid=45L;
        Long artId=2L;
        controllers.Application.Index index= new controllers.Application.Index();
        index.bid=bid;
        index.artId=artId;
        //controllers.Application.Index.artId=artId;
        Form<controllers.Application.Index> indexForm=Form.form(controllers.Application.Index.class);
        indexForm = indexForm.fill(index);
        assertEquals((Long) indexForm.get().bid,(Long) 55L);
        /*Artwork Art = get an artwork from DB (same as Form one)
        Long originalBid=Art.highBid()
        User originalUser=Art.userWithHighBid
        User biddingUser= get a user from DB
                //check bid below high bid
                Long newBid = 5;
                Form<Index> IndexForm= Somehow create form that sends bid
                submitBid(Art,user,IndexForm);
                assert that Art.highBid==orignalBid and Art.user==originalUser
                
                // check bid equal to high bid
                Same as above but newBid=originalBid;
                
                //check non-sensical bid (eg sloppy joe)
                String bid="Sloppy Joe"
                run it, should be error though? idk 
                //check bid above high bid
                Same as above but newBid=100000;
                Assert biddingUser and bid the new artwork stuff*/
                
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
