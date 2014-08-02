/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.i2m.converge.plugins.actions.wordpress;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;
import org.apache.xmlrpc.client.XmlRpcClient;

/**
 *
 * @author fred
 */
public class WordPresslServicesClientTest extends TestCase {

    public WordPresslServicesClientTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getXmlRpcClient method, of class WordPresslServicesClient.
     */
    public void testGetXmlRpcClient() {
        System.out.println("getXmlRpcClient");
        String websiteURL = "http://localhost:8282/wordpress";
        String username = "admin";
        String password = "root";

        WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, username, password);

//        //Act
        XmlRpcClient result = instance.getXmlRpcClient();

//        //Assert
        assertNotNull(result);
    }

    /**
     * Test of exists method, of class WordPresslServicesClient.
     */
//    public void testExists() throws Exception {
//        System.out.println("exists");
//        int blogId = 0;
//        WordPresslServicesClient instance = new WordPresslServicesClient();
//        boolean expResult = false;
//        boolean result = instance.exists(blogId);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of createNewPost method, of class WordPresslServicesClient.
     */
    public void testCreateNewPost() {
        System.out.println("createNewPost");
        HashMap<String, String> post = new HashMap<String, String>(); //Replace all Below with Converge ones
        post.put("mt_keywords", "Lorem ipsum");
        post.put("categories", "cat1,Cat2");
        post.put("post_content", "Lorem ipsum");
        post.put("post_excerpt", "Lorem ipsum");
        post.put("post_status", "publish");
        post.put("post_date", new Date().toString());
        post.put("comment_status", "open");
        post.put("ping_status", "open");
        post.put("title", "Lorem ipsum");
        post.put("link", "http://www.dst.org/");
        Object[] itemsPostParams = new Object[]{1, "admin", "root", post, Boolean.TRUE};
        WordPresslServicesClient instance = new WordPresslServicesClient("http://localhost:8282/wordpress", "admin", "root");

        //Act

        String actualResult = instance.createNewPost(itemsPostParams);

        //Assert
        String expResult = "48"; //This result changes increments by 2 for wordpress sites
        assertEquals(expResult, actualResult);
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");





    }

    /**
     * Test of retrieveExistingPost method, of class WordPresslServicesClient.
     */
    public void testRetrieveExistingPost() throws Exception {
        System.out.println("retrieveExistingPost");
        int blogId = 62;
        String websiteURL = "http://localhost:8282/wordpress";
        WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, "admin", "root");

        //Act
        PostInfo result = instance.retrieveExistingPost(blogId);

        //Assert
        PostInfo expResult = new PostInfo("admin", "root", blogId + "");
        assertEquals(expResult, result);
        // fail("The test case is a prototype.");


    }
//
//    /**
//     * Test of updateExistingPost method, of class WordPresslServicesClient.
//     */
//    public void testUpdateExistingPost() throws Exception {
//        System.out.println("updateExistingPost");
//        int blogId = 0;
//        Object[] itemsPostParams = null;
//        WordPresslServicesClient instance = new WordPresslServicesClient();
//        boolean expResult = false;
//        boolean result = instance.updateExistingPost(blogId, itemsPostParams);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
    /**
     * Test of deleteExistingPost method, of class WordPresslServicesClient.
     */
//    public void testDeleteExistingPost() throws Exception {
//        System.out.println("deleteExistingPost");
//        int blogId = 0;
//        WordPresslServicesClient instance = new WordPresslServicesClient();
//        boolean expResult = false;
//        boolean result = instance.deleteExistingPost(blogId);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of attachFileToPost method, of class WordPresslServicesClient.
//     */
//    public void testAttachFileToPost() throws Exception {
//        System.out.println("attachFileToPost");
//        FileInfo fileInfo = null;
//        int blogId = 0;
//        WordPresslServicesClient instance = new WordPresslServicesClient();
//        String expResult = "";
//        String result = instance.attachFileToPost(fileInfo, blogId);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of attachFiles method, of class WordPresslServicesClient.
//     */
//    public void testAttachFiles() throws Exception {
//        System.out.println("attachFiles");
//        int blogId = 0;
//        List<FileInfo> files = null;
//        WordPresslServicesClient instance = new WordPresslServicesClient();
//        String expResult = "";
//        String result = instance.attachFiles(blogId, files);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
}
