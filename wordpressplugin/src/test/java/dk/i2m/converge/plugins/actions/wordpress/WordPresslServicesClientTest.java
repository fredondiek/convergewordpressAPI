/*
 * Copyright (C) 2014 Fred Ondieki, Allan Lykke Christensen
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package dk.i2m.converge.plugins.actions.wordpress;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public void testExists() throws Exception {
        int blogId = 280;
        String websiteURL = "http://localhost:8282/wordpress";

        WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, "admin", "root");

        boolean expResult = false;//return to true
        //Act
        boolean result = instance.exists(blogId);
        //Assert
        assertEquals(expResult, result);
    }

    /**
     * Test of createNewPost method, of class WordPresslServicesClient.
     */
    public void testCreateNewPost() {
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

        boolean actualResult = instance.createNewPost(itemsPostParams);

        //Assert
        boolean expResult = true; //This result changes increments by 2 for wordpress sites
        assertEquals(expResult, actualResult);

    }

    /**
     * Test of retrieveExistingPost method, of class WordPresslServicesClient.
     */
    public void testRetrieveExistingPost() throws Exception {
        int blogId = 1;
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
        int blogId = 250;
        String websiteURL = "http://localhost:8282/wordpress";
        WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, "admin", "root");
        boolean expResult = false;


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
        }

        FileInfo fileInfo = new FileInfo(file, "png");
        int blogId = 179;
        String websiteURL = "http://localhost:8282/wordpress";
        WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, "admin", "root");
        String expResult = "3.png";

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

        boolean expResult = true;
        //Act
        boolean result = instance.attachFiles(blogId, files);

        //Assert
        assertEquals(expResult, result);
    }
}
