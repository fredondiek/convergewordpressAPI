/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.i2m.converge.plugins.actions.wordpress;

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
        WordPresslServicesClient instance = new WordPresslServicesClient();
        XmlRpcClient expResult = null;
        XmlRpcClient result = instance.getXmlRpcClient();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of exists method, of class WordPresslServicesClient.
     */
    public void testExists() throws Exception {
        System.out.println("exists");
        int blogId = 0;
        WordPresslServicesClient instance = new WordPresslServicesClient();
        boolean expResult = false;
        boolean result = instance.exists(blogId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createNewPost method, of class WordPresslServicesClient.
     */
    public void testCreateNewPost() {
        System.out.println("createNewPost");
        Object[] itemsPostParams = null;
        WordPresslServicesClient instance = new WordPresslServicesClient();
        String expResult = "";
        String result = instance.createNewPost(itemsPostParams);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of retrieveExistingPost method, of class WordPresslServicesClient.
     */
    public void testRetrieveExistingPost() throws Exception {
        System.out.println("retrieveExistingPost");
        int blogId = 0;
        WordPresslServicesClient instance = new WordPresslServicesClient();
        PostInfo expResult = null;
        PostInfo result = instance.retrieveExistingPost(blogId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateExistingPost method, of class WordPresslServicesClient.
     */
    public void testUpdateExistingPost() throws Exception {
        System.out.println("updateExistingPost");
        int blogId = 0;
        Object[] itemsPostParams = null;
        WordPresslServicesClient instance = new WordPresslServicesClient();
        boolean expResult = false;
        boolean result = instance.updateExistingPost(blogId, itemsPostParams);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteExistingPost method, of class WordPresslServicesClient.
     */
    public void testDeleteExistingPost() throws Exception {
        System.out.println("deleteExistingPost");
        int blogId = 0;
        WordPresslServicesClient instance = new WordPresslServicesClient();
        boolean expResult = false;
        boolean result = instance.deleteExistingPost(blogId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of attachFileToPost method, of class WordPresslServicesClient.
     */
    public void testAttachFileToPost() throws Exception {
        System.out.println("attachFileToPost");
        FileInfo fileInfo = null;
        int blogId = 0;
        WordPresslServicesClient instance = new WordPresslServicesClient();
        String expResult = "";
        String result = instance.attachFileToPost(fileInfo, blogId);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of attachFiles method, of class WordPresslServicesClient.
     */
    public void testAttachFiles() throws Exception {
        System.out.println("attachFiles");
        int blogId = 0;
        List<FileInfo> files = null;
        WordPresslServicesClient instance = new WordPresslServicesClient();
        String expResult = "";
        String result = instance.attachFiles(blogId, files);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
