/*
 * Copyright (C) 2012 - 2014 Interactive Media Management
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

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class WordPresslServicesClient {

    private static final Logger LOG = Logger.getLogger(WordPresslServicesClient.class.getName());
    private String hostname;
    private String endpoint;
    private Integer connectionTimeout = 30000;
    private Integer socketTimeout = 30000;
    private String websiteUrl;
    private String username;
    private String password;
    private Integer replyTimeOut=60000;

    /**
     * Creates a new instance of {@link WordPresslServicesClient}.
     */
    public WordPresslServicesClient() {
        this("", "", "", "");
    }

    /**
     * Creates a new instance of {@link WordPresslServicesClient}.
     *
     * @param hostname Host name of the WordPress instance
     * @param endpoint Services endpoint to communicate with
     * @param username Username with privilege to access the endpoint
     * @param password Password matching the {@code username}
     */
    public WordPresslServicesClient(String websiteUrl, String username, String password) {
        this.websiteUrl = websiteUrl;
        this.username = username;
        this.password = password;
    }
    
    public WordPresslServicesClient(String websiteUrl, String username, String password,String connectionTimeout) {
        this.websiteUrl = websiteUrl;
        this.username = username;
        this.password = password;
        this.connectionTimeout=Integer.parseInt(connectionTimeout);
    }

    public WordPresslServicesClient(String websiteUrl, String username, String password, Integer socketTimeout, Integer connectionTimeout) {
        this.websiteUrl = websiteUrl;
        this.username = username;
        this.password = password;
    }

   
    

    public WordPresslServicesClient(String hostname, String endpoint, String username, String password, Integer socketTimeout, Integer connectionTimeout) {
        this.hostname = hostname;
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
        this.socketTimeout = socketTimeout;
        this.connectionTimeout = connectionTimeout;
    }

    public XmlRpcClient getXmlRpcClient() {
        XmlRpcClient client = new XmlRpcClient();
        return client;
    }

    public boolean exists(int blogId) throws WordPressServerConnectionException {
        Object[] result;
        XmlRpcClient wordpRpcClient;
        boolean exists = false;
        // Object[] obj = (Object[]) client.execute("wp.getUsersBlogs", params);
        try {
            URL wordpresssite = new URL(this.websiteUrl + "/xmlrpc.php");
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(wordpresssite);
            config.setEnabledForExtensions(true);
            config.setEnabledForExceptions(true);
            config.setConnectionTimeout(connectionTimeout);
            config.setConnectionTimeout(this.replyTimeOut);
            wordpRpcClient = getXmlRpcClient();
            wordpRpcClient.setConfig(config);
            
            result = (Object[]) wordpRpcClient.execute("metaWeblog.getRecentPosts", new Object[]{9999, this.username, this.password});
            labelsearch:
            for (Object o : result) {
                Map m = (Map) o;
                String blogID = (String) m.get("blogid");
                String blogName = (String) m.get("blogName");
                String xmlRpcUrl = (String) m.get("xmlrpc");
                System.out.println(blogID + " >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>......");
                if (blogID.equalsIgnoreCase(blogId + "")) {
                    exists = true;
                    break labelsearch;
                }
            }
        } catch (XmlRpcException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return exists;
    }

    public String createNewPost(Object[] itemsPostParams) {
        String result = "";
        XmlRpcClient wordpRpcClient;
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
//            config.setServerURL(new URL(this.hostname + "/" + this.endpoint + "/xmlrpc.php"));
            config.setServerURL(new URL(this.websiteUrl + "/xmlrpc.php"));
            config.setEnabledForExtensions(true);
            config.setEnabledForExceptions(true);
            config.setConnectionTimeout(connectionTimeout);
            config.setConnectionTimeout(this.replyTimeOut);
            wordpRpcClient = getXmlRpcClient();
            wordpRpcClient.setConfig(config);
            result = (String) wordpRpcClient.execute("metaWeblog.newPost", itemsPostParams);
        } catch (MalformedURLException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XmlRpcException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public PostInfo retrieveExistingPost(int blogId) throws IOException {
        Object result;
        HashMap<String, String> resultstr;
//        XmlRpcClient wordpRpcClient;
//        boolean exists = false;
//        PostInfo blog = null;
//        try {
//
//            URL wordpresssite = new URL(this.websiteUrl + "/xmlrpc.php");
//            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
//            config.setServerURL(wordpresssite);
//            config.setEnabledForExtensions(true);
//            config.setEnabledForExceptions(true);           
//            wordpRpcClient = getXmlRpcClient();
//            wordpRpcClient.setConfig(config);
//            
//              // result = (Object) wordpRpcClient.execute("metaWeblog.getPost", new Object[]{99, "admin", "root"});
//            result = (Object) wordpRpcClient.execute("metaWeblog.getPost", new Object[]{blogId,this.username, this.password});
//
//            if (result != null) {
//                final Map m = (Map) result;
//                final String url = (String) m.get("url");
//                final String blogID = (String) m.get("blogid");
//                final String blogName = (String) m.get("blogName");
//                final String description = (String) m.get("description");
//                final Date dateCreated = (Date) m.get("dateCreated");
//                //final Object[] categories = (Object[]) m.get("categories");
//                final String title = (String) m.get("title");
//                final String mt_keywords = (String) m.get("mt_keywords");
//                final String xmlRpcUrl = (String) m.get("xmlrpc");
//                //if (Integer.parseInt(blogID) == blogId) {
//                blog = new PostInfo();
//                blog.setBlogId(blogID);
//                blog.setPassword(password);
//                blog.setUserName(username);
//                   // exists = true;
//                   // return blog;
//                //}
//            }
        PostInfo blog = null;
        try {
            XmlRpcClient wordpRpcClient;

            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
//            config.setServerURL(new URL(this.hostname + "/" + this.endpoint + "/xmlrpc.php"));
            config.setServerURL(new URL(this.websiteUrl + "/xmlrpc.php"));
            config.setEnabledForExtensions(true);
            config.setEnabledForExceptions(true);
            config.setConnectionTimeout(connectionTimeout);
            config.setConnectionTimeout(this.replyTimeOut);
            wordpRpcClient = getXmlRpcClient();
            wordpRpcClient.setConfig(config);
            resultstr = (HashMap<String, String>) wordpRpcClient.execute("metaWeblog.getPost", new Object[]{blogId, "admin", "root"});
            if (resultstr != null) {
//                final Map<String,String> m = (Map<String,String>) result;
//                final String url = (String) m.get("url");
//                final String blogID = (String) m.get("blogid");
//                final String blogName = (String) m.get("blogName");
//                final String description = (String) m.get("description");
//                //final Date dateCreated = (Date) m.get("dateCreated");
//                //final Object[] categories = (Object[]) m.get("categories");
//                final String title = (String) m.get("title");
//                final String mt_keywords = (String) m.get("mt_keywords");
//                final String xmlRpcUrl = (String) m.get("xmlrpc");

                // String[] arrValue = resultstr.split(",");
                Map<String, String> valueMap = new HashMap<String, String>();
//                for (String string : arrValue) {
//                    String[] mapPair = string.split("=");
//                    valueMap.put(mapPair[0], mapPair[1]);
//                }
//                ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(arrValue));
//                for(String  item:arrayList){
//                int equalpos=item.indexOf("=");
//                String key=item.substring(0,equalpos);
//                String value =item.substring(equalpos+1);
//                valueMap.put(key, value);
                //}
                blog = new PostInfo();
//                for(int i=0;i<arrValue.length;i++){
//                String curr = arrValue[i];
//                int equalpos=curr.indexOf("=");
//                String key=curr.substring(0,equalpos);
//                String value =curr.substring(equalpos+1);
//                if(key.equalsIgnoreCase("blogid")){
////               / blog.setBlogId(value);
//                }
//                }
//                blog = new PostInfo();
                blog.setBlogId(resultstr.get("blogid"));
                blog.setPassword(password);
                blog.setUserName(username);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XmlRpcException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return blog;
    }

    public boolean updateExistingPost(int blogId, Object[] itemsPostParams) throws URISyntaxException, IOException {
        Object result;
        XmlRpcClient wordpRpcClient;
        boolean edited = false;
        try {
            URL wordpresssite = new URL(this.websiteUrl + "/xmlrpc.php");
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(wordpresssite);
            config.setEnabledForExtensions(true);
            config.setEnabledForExceptions(true);
            config.setConnectionTimeout(connectionTimeout);
            config.setConnectionTimeout(this.replyTimeOut);
            wordpRpcClient = getXmlRpcClient();
            wordpRpcClient.setConfig(config);
            result = (Object) wordpRpcClient.execute("metaWeblog.editPost", new Object[]{itemsPostParams});
            if (result != null) {
                //CHECK THE RETURNED STRING FURTHER LOGIC NEEDED HERE
                edited = true;
            }

        } catch (XmlRpcException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return edited;
    }

    public boolean deleteExistingPost(int blogId) throws URISyntaxException, IOException {
        Object result;
        XmlRpcClient wordpRpcClient;
        boolean deleted = false;
        PostInfo blog = null;
        try {
            URL wordpresssite = new URL(this.websiteUrl + "/xmlrpc.php");
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(wordpresssite);
            config.setEnabledForExtensions(true);
            config.setEnabledForExceptions(true);
            config.setConnectionTimeout(connectionTimeout);
            config.setConnectionTimeout(this.replyTimeOut);
            wordpRpcClient = getXmlRpcClient();
            wordpRpcClient.setConfig(config);
            //Object  result = (Object) client.execute("metaWeblog.deletePost", new Object[]{1,"32", "admin","root"});

            result = wordpRpcClient.execute("metaWeblog.deletePost", new Object[]{1, blogId, this.username, this.password});
            if (result != null) {
                //CHECK THE RETURNED STRING FURTHER LOGIC NEEDED HERE
                deleted = true;
            }

        } catch (XmlRpcException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return deleted;

    }

    public String attachFileToPost(FileInfo fileInfo, int blogId) throws WordPressServerConnectionException {
        XmlRpcClient worRpcClientclient;
        String result = "";
        try {
            worRpcClientclient = getXmlRpcClient();
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(this.websiteUrl + "/xmlrpc.php"));
            config.setEnabledForExtensions(true);
            config.setEnabledForExceptions(true);
            config.setConnectionTimeout(connectionTimeout);
            config.setConnectionTimeout(this.replyTimeOut);
            worRpcClientclient.setConfig(config);

            byte[] bytes = new byte[(int) fileInfo.getFile().length()];
            FileInputStream fin = new FileInputStream(fileInfo.getFile());
            fin.read(bytes);
            fin.close();

            Map<Object, Object> fileData = new HashMap<Object, Object>();
            fileData.put("name", fileInfo.getFile().getName());
            fileData.put("type", fileInfo.getFile().getName().substring(fileInfo.getFile().getName().lastIndexOf(".") + 1));
            fileData.put("bits", bytes);
            fileData.put("overwrite", Boolean.FALSE);
            Object[] params = new Object[]{blogId, username, password, fileData};//wp.uploadFile
            Object uploadResult = worRpcClientclient.execute("wp.uploadFile", params);//newMediaObject
            result = uploadResult.toString();//metaWeblog.uploadFile

            LOG.log(Level.FINER, "Attach file response: {0}", uploadResult.toString());
            return result;
        } catch (XmlRpcException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            throw new WordPressServerConnectionException("Could not attach files.", ex);
        }
        return result;
    }

    public String attachFiles(int blogId, List<FileInfo> files) throws WordPressServerConnectionException {
        String result = "";
        try {
            int i = 0;
            XmlRpcClient worRpcClientclient;
            for (FileInfo file : files) {
                i++;
                worRpcClientclient = getXmlRpcClient();
                XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                config.setServerURL(new URL(this.websiteUrl + "/xmlrpc.php"));
                config.setConnectionTimeout(connectionTimeout);
                config.setConnectionTimeout(this.replyTimeOut);
                worRpcClientclient.setConfig(config);
                worRpcClientclient = getXmlRpcClient();
                worRpcClientclient.setConfig(config);
                byte[] bytes = new byte[(int) file.getFile().length()];
                FileInputStream fin = new FileInputStream(file.getFile());
                fin.read(bytes);
                fin.close();

                Map<Object, Object> fileData = new HashMap<Object, Object>();
                fileData.put("name", file.getFile().getName());
                fileData.put("type", file.getFile().getName().substring(file.getFile().getName().lastIndexOf(".") + 1));
                fileData.put("bits", bytes);
                fileData.put("overwrite", Boolean.TRUE);
                Object[] params = new Object[]{blogId, username, password, fileData};//wp.uploadFile
                Object uploadResult = worRpcClientclient.execute("wp.uploadFile", params);
                result = uploadResult.toString();//metaWeblog.uploadFile
            }

            LOG.log(Level.FINER, "Attach file response: {0}", result);
            return result;
        } catch (XmlRpcException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            throw new WordPressServerConnectionException("Could not attach files.", ex);
        }
        return result;
    }

    public static enum PostType {

        publish(true), draft(false);
        private final boolean value;

        PostType(boolean value) {
            this.value = value;
        }

        public boolean booleanValue() {
            return value;
        }
    }
}
