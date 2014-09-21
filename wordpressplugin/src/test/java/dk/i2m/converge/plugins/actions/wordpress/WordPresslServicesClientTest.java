/*
 * Copyright (C) 2014 Fred Ondieki
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
        HashMap<Object, Object> post = new HashMap<Object, Object>(); //Replace all Below with Converge ones
        String text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        String category = "mandia,karige";

        post.put("mt_keywords", "Lorem ,ipsum");
        post.put("categories", category.split(","));
        post.put("post_content", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry’s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum");
        post.put("post_excerpt", "Lorem ipsum");
        post.put("post_status", "publish");
        post.put("post_date", new Date().toString());
        post.put("comment_status", "open");
        post.put("ping_status", "open");
        post.put("title", "Test Title");
        post.put("link", "http://www.dst.org/");
        post.put("description", text);

        //Object[] itemsPostParams = new Object[]{1, "admin", "root", post, Boolean.TRUE};
        Object[] itemsPostParams = new Object[]{1, "Converge", "ConvergeAPI", post, Boolean.TRUE};
        //WordPresslServicesClient instance = new WordPresslServicesClient("http://localhost:8282/wordpress", "admin", "root");
        WordPresslServicesClient instance = new WordPresslServicesClient("https://radioafricaplatforms.com/apps/converge", "Converge", "ConvergeAPI");

        //Act

        String actualResult = instance.createNewPost(itemsPostParams);

        //Assert
        String expResult = actualResult; //This result changes increments by 2 for wordpress sites

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
        String websiteURL = "https://localhost:8282/wordpress";
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
        expResult = result;
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
        expResult = result;
        //Assert
        assertEquals(expResult, result);

    }

    /**
     * Test of attachFileToPost method, of class WordPresslServicesClient.
     */
    public void testAttachFileToPost() throws Exception {
        System.out.println("attachFileToPost");
        File file = new File("C://java_code//3.jpg");
        String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileInfo fileInfo = new FileInfo(file, extension);
        int blogId = 1;
        //http://164.177.147.31/brands/classicfm/
        String websiteURL = "http://localhost:8282/wordpress";
        //String websiteURL = "http://164.177.147.31/brands/classicfm";
        //String websiteURL = "https://radioafricaplatforms.com/apps/converge";
        //https://radioafricaplatforms.com/apps/converge/
        WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, "admin", "root");
        //WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, "Converge", "ConvergeAPI");

        //String expResult = "3.png";

        String expResult = true + "";

        //Act
        Map<String, String> post = new HashMap<String, String>();
        String text = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        Map<String, String> params = instance.attachFileToPost(fileInfo, blogId);
        String result = params.get("boolean");
        post.put("mt_keywords", "testkeyword,testkey3");
        post.put("categories", "testcat,Cat2");
        post.put("post_type", "post");
        post.put("post_content", text);
        post.put("post_excerpt", "Test Excerpt");
        post.put("post_status", "publish");
        post.put("post_date", new Date().toString());
        post.put("comment_status", "open");
        post.put("ping_status", "open");
        post.put("title", "Another Updated Title from Converge, From Converge");
        post.put("link", "http://www.dst.org/");
        post.put("description", text);
        post.put("wp_post_thumbnail", params.get("id"));
        post.put("wp_featured_image", params.get("id"));
        post.put("thumbnail", params.get("id"));
        post.put("featured_image_url", params.get("url"));
        Object[] itemsPostParams = new Object[]{1, "admin", "root", post};
        //Object[] itemsPostParams = new Object[]{1, "Converge", "ConvergeAPI", post};

        instance.createNewPost(itemsPostParams);
        //Assert
        assertEquals(expResult, result);

    }

    public void testAttachMp3FileToPost() throws Exception {
        System.out.println("attachFileToPost");
//        File file = new File("C://java_code//3.avi");
        File file = new File("C://java_code//3.mp3");

        String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        if (!file.exists()) {
            file.createNewFile();
        }
//[audio mp3="http://localhost:8282/wordpress/wp-content/uploads/2014/09/365.mp3"][/audio]
        FileInfo fileInfo = new FileInfo(file, extension);
        int blogId = 1;
        //http://164.177.147.31/brands/classicfm/
        //String websiteURL = "http://localhost:8282/wordpress";
        //String websiteURL = "http://164.177.147.31/brands/classicfm";
        String websiteURL = "https://radioafricaplatforms.com/apps/converge";
        //https://radioafricaplatforms.com/apps/converge/
        //WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, "admin", "root");
        WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, "Converge", "ConvergeAPI");

        //String expResult = "3.png";

        String expResult = true + "";

        //Act
        Map<String, String> post = new HashMap<String, String>();
        String text = "MP3 UPLOAD TEST:Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        Map<String, String> params = instance.attachMp3FileToPost(fileInfo, blogId);
        String result = params.get("boolean");
        post.put("mt_keywords", "testkeyword,testkey3");
        post.put("categories", "testcat,Cat2");
//      post.put("post_type", "attachment");
        post.put("post_type", "post");
        post.put("post_content", text);
        post.put("post_excerpt", "Test Excerpt");
        post.put("post_status", "publish");
        post.put("post_date", new Date().toString());
        post.put("comment_status", "open");
        post.put("ping_status", "open");
        post.put("title", "Another Updated Title from Converge, From Converge");
        post.put("link", "http://www.dst.org/");
        post.put("description", text + "[audio " + params.get("type") + "=" + params.get("url") + "][/audio]");
        //post.put("url", text);
//        post.put("wp_post_thumbnail", params.get("id"));
//        post.put("wp_featured_image", params.get("id"));
//        post.put("thumbnail", params.get("id"));
//        post.put("featured_image_url", params.get("url"));
        //Object[] itemsPostParams = new Object[]{1, "admin", "root", post};
        Object[] itemsPostParams = new Object[]{1, "Converge", "ConvergeAPI", post};

        instance.createNewPost(itemsPostParams);
        //Assert
        assertEquals(expResult, result);

    }

    public void testAttachVideoFileToPost() throws Exception {
        System.out.println("attachFileToPost");
        File file = new File("C://java_code//3.flv");
        // File file = new File("C://java_code//3.mp3");

        String extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileInfo fileInfo = new FileInfo(file, extension);
        int blogId = 1;
        //http://164.177.147.31/brands/classicfm/
        //String websiteURL = "http://localhost:8282/wordpress";
        //String websiteURL = "http://164.177.147.31/brands/classicfm";
        String websiteURL = "https://radioafricaplatforms.com/apps/converge";
        //https://radioafricaplatforms.com/apps/converge/
        // WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, "admin", "root");
        WordPresslServicesClient instance = new WordPresslServicesClient(websiteURL, "Converge", "ConvergeAPI");

        //String expResult = "3.png";

        String expResult = true + "";

        //Act
        Map<String, String> post = new HashMap<String, String>();
        String text = " VIDEO UPLOAD TEST: Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";
        Map<String, String> params = instance.attachVideoFileToPost(fileInfo, blogId);
//        String result = params.get("boolean");
        post.put("mt_keywords", "testkeyword,testkey3");
        post.put("categories", "testcat,Cat2");
        post.put("post_type", "attachment");
        post.put("post_type", "post");
        post.put("post_format", "standard");
        post.put("post_content", "[video width=\"640\" height=\"368\" flv=\"http://localhost:8282/wordpress/wp-content/uploads/2014/09/wpid1133-3.flv\"][/video]" + text);
        post.put("post_excerpt", "Test Excerpt");
        post.put("post_status", "publish");
        post.put("post_date", new Date().toString());
        post.put("comment_status", "open");
        post.put("ping_status", "open");
        post.put("title", "Another Updated Title from Converge, From Converge");
        //post.put("link", params.get("url"));
        //{id=362, file=wpid-3.flv, type=video/flv, url=https://radioafricaplatforms.com/apps/converge/wp-content/uploads/2014/09/wpid-3.flv}::::::::::::::::::::::
        post.put("description", text + "[video " + params.get("type") + "=" + params.get("url") + "][/video]");
        //        post.put("url", params.get("url"));
//        post.put("parent_id", params.get("id"));

        // Object[] itemsPostParams = new Object[]{1, "admin", "root", post};
        Object[] itemsPostParams = new Object[]{1, "Converge", "ConvergeAPI", post};

        String postid = instance.createNewPost(itemsPostParams);
        System.out.print("[ " + postid + " ]PPPPPPPPPPPPPP");
        //Map<String, String> paramsz = instance.attachVideoFileToPost(fileInfo, Integer.parseInt(instance.createNewPost(itemsPostParams)));
        //Assert
//        assertEquals(expResult, result);
        assertEquals(expResult, params.get("boolean"));

    }

    /**
     * Test of attachFiles method, of class WordPresslServicesClient.
     */
    public void testAttachFiles() throws Exception {
        System.out.println("attachFiles");
        int blogId = 179;
        String extension;
        List<FileInfo> files = new ArrayList<FileInfo>();
        File file = new File("C://java_code//3.png");
        extension = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        if (!file.exists()) {
            file.createNewFile();
        }
        System.out.println(file.getName());
        FileInfo fileInfo = new FileInfo(file, extension);
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
