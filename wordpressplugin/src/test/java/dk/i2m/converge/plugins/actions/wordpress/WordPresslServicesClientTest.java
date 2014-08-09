/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.i2m.converge.plugins.actions.wordpress;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.commons.io.FileUtils;

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
    public void testExists() throws Exception {
        int blogId = 159;
        String websiteURL = "http://localhost:8282/wordpress";
        WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, "admin", "root");

        boolean expResult = true;
        //Act
        boolean result = instance.exists(blogId);
        //Assert

        assertEquals(expResult, result);
    }

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
        String expResult = "161"; //This result changes increments by 2 for wordpress sites
        assertEquals(expResult, actualResult);

    }

    /**
     * Test of retrieveExistingPost method, of class WordPresslServicesClient.
     */
    public void testRetrieveExistingPost() throws Exception {
        int blogId = 155;
        String websiteURL = "http://localhost:8282/wordpress";
        WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, "admin", "root");

        //Act
        PostInfo result = instance.retrieveExistingPost(blogId);

        //Assert
        PostInfo expResult = new PostInfo("admin", "root", blogId + "");
        assertEquals(expResult.getBlogId(), result.getBlogId());


    }

    /**
     * Test of updateExistingPost method, of class WordPresslServicesClient.
     */
    public void testUpdateExistingPost() throws Exception {
        int blogId = 155;
        String websiteURL = "http://localhost:8282/wordpress";
        WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, "admin", "root");
        Map<String, String> post = new HashMap<String, String>();
        String text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";

        post.put("mt_keywords", "testkeyword");
        post.put("categories", "testcat,Cat2");
        post.put("post_content", text);
        post.put("post_excerpt", "Test Excerpt");
        post.put("post_status", "publish");
        post.put("post_date", new Date().toString());
        post.put("comment_status", "open");
        post.put("ping_status", "open");
        post.put("title", "Another Updated Title from Converge, From Converge");
        post.put("link", "http://www.vonhagen.org/");
        post.put("description", text);

        Object[] itemsPostParams = new Object[]{157, "admin", "root", post};
        boolean expResult = true;

        //Act
        boolean result = instance.updateExistingPost(blogId, itemsPostParams);

        //Assert
        assertEquals(expResult, result);

    }

    /**
     * Test of deleteExistingPost method, of class WordPresslServicesClient.
     */
    public void testDeleteExistingPost() throws Exception {
        int blogId = 34;
        String websiteURL = "http://localhost:8282/wordpress";
        WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, "admin", "root");
        boolean expResult = true;

        //Act
        boolean result = instance.deleteExistingPost(blogId);

        //Assert
        assertEquals(expResult, result);

    }

    /**
     * Test of attachFileToPost method, of class WordPresslServicesClient.
     */
    public void testAttachFileToPost() throws Exception {
        System.out.println("attachFileToPost");

        File file = new File("C://java_code//3.png");
        if (!file.exists()) {
            file.createNewFile();
        };
        System.out.println(file);
        FileInfo fileInfo = new FileInfo(file, "png");
        // List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
        int blogId = 179;
        String websiteURL = "http://localhost:8282/wordpress";
        WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, "admin", "root");
        String expResult = "";


        //Act
        String result = instance.attachFileToPost(fileInfo, blogId);
        //Assert
        assertEquals(expResult, result);

    }

    /**
     * Test of attachFiles method, of class WordPresslServicesClient.
     */
    public void testAttachFiles() throws Exception {
        System.out.println("attachFiles");
        int blogId = 179;
        List<FileInfo> files = new ArrayList<FileInfo>();
        File file = new File("C://java_code//3.png");
        if (!file.exists()) {
            file.createNewFile();
        }
        System.out.println(file.getName());
        FileInfo fileInfo = new FileInfo(file, "png");
        files.add(fileInfo);
        String websiteURL = "http://localhost:8282/wordpress";
        WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, "admin", "root");

        String expResult = "";
        //Act
        String result = instance.attachFiles(blogId, files);

        //Assert
        assertEquals(expResult, result);
    }
}
