///*
// * Copyright (C) 2014 Fred Ondieki
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package dk.i2m.converge.plugins.actions.wordpress;
//
//import dk.i2m.converge.core.content.NewsItemPlacement;
//import dk.i2m.converge.core.plugin.PluginContext;
//import dk.i2m.converge.core.workflow.Edition;
//import dk.i2m.converge.core.workflow.OutletEditionAction;
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.ResourceBundle;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import junit.framework.TestCase;
//
///**
// *
// * @author fred
// */
//public class WordPressEditionActionTest extends TestCase {
//
//    public WordPressEditionActionTest(String testName) {
//        super(testName);
//    }
//
//    @Override
//    protected void setUp() throws Exception {
//        super.setUp();
//    }
//
//    @Override
//    protected void tearDown() throws Exception {
//        super.tearDown();
//    }
//
////    /**
////     * Test of execute method, of class WordPressEditionAction.
////     */
////    public void testExecute() {
////        System.out.println("execute");
////        PluginContext ctx = null;
////        Edition edition = null;
////        OutletEditionAction action = null;
////        WordPressEditionAction instance = new WordPressEditionAction();
////        instance.execute(ctx, edition, action);
////        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
////    }
//
//    /**
//     * Test of executePlacement method, of class WordPressEditionAction.
//     */
////    public void testExecutePlacement() {
////        System.out.println("executePlacement");
////        PluginContext ctx = null;
////        NewsItemPlacement placement = new NewsItemPlacement();
////        Edition edition = new Edition();
////        OutletEditionAction action = new OutletEditionAction();
////        WordPressEditionAction instance = new WordPressEditionAction();
////        instance.executePlacement(ctx, placement, edition, action);
////        
////        
////        
////    }
//    /**
//     * Test of isSupportEditionExecute method, of class WordPressEditionAction.
//     */
//    public void testIsSupportEditionExecute() {
//        System.out.println("isSupportEditionExecute");
//        WordPressEditionAction instance = new WordPressEditionAction();
//        boolean expResult = true;
//        
//        //Act
//        boolean result = instance.isSupportEditionExecute();
//
//        //Assert
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of isSupportPlacementExecute method, of class
//     * WordPressEditionAction.
//     */
//    public void testIsSupportPlacementExecute() {
//        System.out.println("isSupportPlacementExecute");
//        WordPressEditionAction instance = new WordPressEditionAction();
//        boolean expResult = true;
//
//        //Act
//        boolean result = instance.isSupportPlacementExecute();
//
//        //Assert
//        assertEquals(expResult, result);
//
//    }
//
//    /**
//     * Test of getAvailableProperties method, of class WordPressEditionAction.
//     */
//    public void testGetAvailableProperties() {
//        System.out.println("getAvailableProperties");
//        WordPressEditionAction instance = new WordPressEditionAction();
//        Map<String, String> expResult = new HashMap<String, String>();
//        expResult.put("PLUGIN_ABOUT", "Plug-in for copying stories to a WORDPRESS installation using the the WORDPRESS XML-RPC module.");
//        expResult.put("PLUGIN_DESCRIPTION", "Client API for interacting with the WORDPRESS XML-RPC module.");
//        expResult.put("PLUGIN_NAME", "WordPress Services Client");
//        expResult.put("PLUGIN_VENDOR", "<a href=\"http://www.dst.com\" target=\"_blank\">Distributed Systems Kenya LTD.</a>");
//        expResult.put("CONNECTION_TIMEOUT", "Connection Timeout");
//        expResult.put("CONNECTION_TIMEOUT_HELP", "Connection timeout value, in milliseconds. (30 seconds)");
//        expResult.put("IMAGE_RENDITION", "Image Rendition");
//        expResult.put("IMAGE_RENDITION_HELP", "Rendition to upload.");
//        expResult.put("PASSWORD", "Logon PassWord credentials for the Remote WordPress Site");
//        expResult.put("PASSWORD_HELP", "Login password machine name");
//        expResult.put("PUBLISH_DELAY", "Publish Delay");
//        expResult.put("PUBLISHED", "Published");
//        expResult.put("POSTID", "The Post Id, Optional");
//        expResult.put("POST_STATUS", "Whether the Post is Published or NOT cannot be null");
//        expResult.put("PUBLISH_DELAY_HELP", "Delay in hours to wait before publishing. Cannot be <= 0.");
//        expResult.put("PUBLISH_IMMEDIATELY", "Publish Immediately");
//        expResult.put("PUBLISH_IMMEDIATELY_HELP", "Can be anything. If set, overrides \"Publish Delay\".");
//        expResult.put("SOCKET_TIMEOUT", "Socket Timeout");
//        expResult.put("SOCKET_TIMEOUT_HELP", "Socket timeout value, in milliseconds. (30 seconds)");
//        expResult.put("SITE_URL", "The website link site is hosted");
//        expResult.put("SITE_URL_HELP", "The website link site is hosted:format= {http://}{www}.{websitename} ");
//        expResult.put("USERNAME", "Username To Log on to the Remote WordPress Site");
//        expResult.put("USERNAME_HELP", "Username To Log on to the Remote WordPress Site.");
//        expResult.put("POST_TYPE", "The Post type on the Wordpress e.g post");
//        expResult.put("KEYWORDS", "Particular KeyWords");
//        expResult.put("CATEGORIES", "The Categories on the Wordpress Site");
//        expResult.put("CUSTOME_FIELDS", "The Custom Fields on the WordPress Site");
//        expResult.put("ALLOW_COMMENTS", "Allow Comments on the Wordpress Site 1 to Allow Comments");
//        expResult.put("BLOG_ID", "The Blog Id that you want to Post to.");
//        expResult.put("TAG", "The Tag to this Post");
//
//
//        //Act
//        Map<String, String> result = instance.getAvailableProperties();
//
//        //Assert
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of getName method, of class WordPressEditionAction.
//     */
//    public void testGetName() {
//        System.out.println("getName");
//        WordPressEditionAction instance = new WordPressEditionAction();
//        String expResult = "WordPress Services Client";
//
//        //Act
//        String result = instance.getName();
//
//        //Assert
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of getDescription method, of class WordPressEditionAction.
//     */
//    public void testGetDescription() {
//        System.out.println("getDescription");
//        WordPressEditionAction instance = new WordPressEditionAction();
//        String expResult = "Client API for interacting with the WORDPRESS XML-RPC module.";
//
//        //Act
//        String result = instance.getDescription();
//
//        //Assert
//        assertEquals(expResult, result);
//
//    }
//
//    /**
//     * Test of getVendor method, of class WordPressEditionAction.
//     */
//    public void testGetVendor() {
//        System.out.println("getVendor");
//        WordPressEditionAction instance = new WordPressEditionAction();
//        String expResult = "<a href=\"http://www.dst.com\" target=\"_blank\">Distributed Systems Kenya LTD.</a>";
//
//        //Act
//        String result = instance.getVendor();
//
//        //Assert
//        assertEquals(expResult, result);
//
//    }
//
//    /**
//     * Test of getDate method, of class WordPressEditionAction.
//     */
//    public void testGetDate() {
//        try {
//            System.out.println("getDate");
//            WordPressEditionAction instance = new WordPressEditionAction();
//
//            ResourceBundle bundle = ResourceBundle.getBundle("dk.i2m.converge.plugins.wordpress.Messages");
//            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date expResult = sdf.parse(bundle.getString("PLUGIN_BUILD_TIME"));
//
//            //Act
//            Date result = instance.getDate();
//
//            //Assert
//            assertEquals(expResult, result);
//        } catch (ParseException ex) {
//            Logger.getLogger(WordPressEditionActionTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//    }
////
////    /**
////     * Test of getBundle method, of class WordPressEditionAction.
////     */
////    public void testGetBundle() {
////        System.out.println("getBundle");
////        WordPressEditionAction instance = new WordPressEditionAction();
////        ResourceBundle expResult = null;
////        ResourceBundle result = instance.getBundle();
////        assertEquals(expResult, result);
////        // TODO review the generated test code and remove the default call to fail.
////        fail("The test case is a prototype.");
////    }
////
//
//    /**
//     * Test of getAbout method, of class WordPressEditionAction.
//     */
//    public void testGetAbout() {
//        System.out.println("getAbout");
//        WordPressEditionAction instance = new WordPressEditionAction();
//        String expResult = "Plug-in for copying stories to a WORDPRESS installation using the the WORDPRESS XML-RPC module.";
//
//        //Act
//        String result = instance.getAbout();
//
//        //Assert
//        assertEquals(expResult, result);
//
//    }
//}
