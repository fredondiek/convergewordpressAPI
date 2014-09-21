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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.i2m.converge.plugins.actions.wordpress.util.Utils;
import java.io.FileInputStream;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
import java.security.*;
import java.security.cert.*;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

public class WordPresslServicesClient {

    private static final Logger LOG = Logger.getLogger(WordPresslServicesClient.class.getName());
    private String hostname;
    private String endpoint;
    private Integer connectionTimeout = 30000;
    private Integer socketTimeout = 30000;
    private String websiteUrl;
    private String username;
    private String password;
    private Integer replyTimeOut = 60000;

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

    public WordPresslServicesClient(String websiteUrl, String username, String password, String connectionTimeout) {
        this.websiteUrl = websiteUrl;
        this.username = username;
        this.password = password;
        this.connectionTimeout = Integer.parseInt(connectionTimeout);
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
        PostInfo blog;
        Gson gson = new Gson();
        JsonObject jsonObject;
        JsonParser jsonParser = new JsonParser();
        String json;
        boolean exists = false;
        int count = 0;
        //loadKeyStore();//CHANGE ME
        try {
            Utils.sslHanshake();
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
//Sections  //category  
            labelsearch:
            for (Object o : result) {
                //Map m = (Map) o;
                json = gson.toJson((HashMap<String, String>) o);
                System.out.println(json + " " + count++);
                blog = new PostInfo();
                Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.INFO, json);
                JsonElement jsonElement = jsonParser.parse(json);
                if (jsonElement.isJsonObject()) {
                    jsonObject = jsonElement.getAsJsonObject();
                    blog.setBlogId(jsonObject.get("postid").getAsString());
                    System.out.println(blog.getBlogId());
                    blog.setBlogTitle(jsonObject.get("title").getAsString());
                    blog.setBlogDescription(jsonObject.get("description").getAsString());
                    blog.setPassword(password);
                    blog.setUserName(username);

                }
                if (Integer.parseInt(blog.getBlogId()) == blogId) {
                    exists = true;
                    System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa0");
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
        try {
            Map<String, String> resultstr = null;
            XmlRpcClient wordpRpcClient;
            boolean created = false;
            //loadKeyStore();//CHANGE ME

            resultstr = new HashMap<String, String>();
            Utils.sslHanshake();
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(this.websiteUrl + "/xmlrpc.php"));
            config.setEnabledForExtensions(true);
            config.setEnabledForExceptions(true);
            config.setConnectionTimeout(connectionTimeout);
            config.setConnectionTimeout(this.replyTimeOut);
            wordpRpcClient = getXmlRpcClient();
            wordpRpcClient.setConfig(config);
            result = (String) wordpRpcClient.execute("metaWeblog.newPost", itemsPostParams);//wp.newPost//metaWeblog.newPost
            System.out.println(result + "@@@@@@@@@@@@@@@@@@@@@@@@@");
            //    {"postid":"1092","wp_post_thumbnail":"1091","mt_allow_comments":1,"permaLink":"http://localhost:8282/wordpress/?p\u003d1092","post_status":"publish","link":"http://localhost:8282/wordpress/?p\u003d1092","mt_text_more":"","userid":"1","mt_allow_pings":1,"mt_keywords":"testkey3, testkeyword","title":"Another Updated Title from Converge, From Converge","date_modified_gmt":"Sep 10, 2014 6:48:09 AM","wp_more_text":"","description":"Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry\u0027s standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.","wp_password":"","wp_author_display_name":"admin","custom_fields":[],"date_modified":"Sep 10, 2014 6:48:09 AM","mt_excerpt":"","wp_post_format":"standard","sticky":false,"date_created_gmt":"Sep 10, 2014 6:48:09 AM","dateCreated":"Sep 10, 2014 6:48:09 AM","categories":["Uncategorized"],"wp_slug":"another-updated-title-from-converge-from-converge-92","wp_author_id":"1"} 6
            resultstr.put("post_id", result);
            resultstr.put("created", true + "");
            resultstr.put("postid", result);
            if (Integer.parseInt(result) != 0) {
                created = true;
                resultstr.put("post_id", result);
                resultstr.put("created", true + "");
                //return result;
            } else {
            }
            return result;
        } catch (MalformedURLException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XmlRpcException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;

    }

    public PostInfo retrieveExistingPost(int blogId) throws IOException {
        HashMap<String, String> resultstr;
        PostInfo blog = null;
        Gson gson = new Gson();
        JsonObject jsonObject;
        JsonParser jsonParser = new JsonParser();
        String json;
        //loadKeyStore();//CHANGE ME
        try {
            Utils.sslHanshake();
            XmlRpcClient wordpRpcClient;
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(this.websiteUrl + "/xmlrpc.php"));
            config.setEnabledForExtensions(true);
            config.setEnabledForExceptions(true);
            config.setConnectionTimeout(connectionTimeout);
            config.setConnectionTimeout(this.replyTimeOut);
            wordpRpcClient = getXmlRpcClient();
            wordpRpcClient.setConfig(config);
            resultstr = (HashMap<String, String>) wordpRpcClient.execute("metaWeblog.getPost", new Object[]{blogId, "admin", "root"});
            json = gson.toJson(resultstr);
            if (resultstr != null) {
                blog = new PostInfo();
                Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.INFO, json);
                JsonElement jsonElement = jsonParser.parse(json);
                if (jsonElement.isJsonObject()) {
                    jsonObject = jsonElement.getAsJsonObject();
                    blog.setBlogId(jsonObject.get("postid").getAsString());
                    blog.setBlogTitle(jsonObject.get("title").getAsString());
                    blog.setBlogDescription(jsonObject.get("description").getAsString());
                    blog.setPassword(password);
                    blog.setUserName(username);
                }
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
        //loadKeyStore();//CHANGE ME
        try {
            Utils.sslHanshake();
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
        //loadKeyStore();//CHANGE ME
        try {
            Utils.sslHanshake();
            URL wordpresssite = new URL(this.websiteUrl + "/xmlrpc.php");
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(wordpresssite);
            config.setEnabledForExtensions(true);
            config.setEnabledForExceptions(true);
            config.setConnectionTimeout(connectionTimeout);
            config.setConnectionTimeout(this.replyTimeOut);
            wordpRpcClient = getXmlRpcClient();
            wordpRpcClient.setConfig(config);
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

    public Map<String, String> attachFileToPost(FileInfo fileInfo, int blogId) throws WordPressServerConnectionException {
        XmlRpcClient worRpcClientclient;
        String result = "";
        Gson gson = new Gson();
        JsonObject jsonObject;
        JsonParser jsonParser = new JsonParser();
        String json;
        HashMap<String, String> resultstr;
        HashMap<String, String> resultMap = new HashMap<String, String>();

        boolean isId = false;
        String fileId;
        //loadKeyStore();//CHANGE ME
        try {
            Utils.sslHanshake();

            worRpcClientclient = getXmlRpcClient();
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(this.websiteUrl + "/xmlrpc.php"));
            config.setEnabledForExtensions(true);
            config.setEnabledForExceptions(true);
            config.setConnectionTimeout(connectionTimeout);
            config.setConnectionTimeout(this.replyTimeOut);
            worRpcClientclient.setConfig(config);
            //File file = fileInfo.getFile();
            byte[] bytes = new byte[(int) fileInfo.getFile().length()];
            FileInputStream fin = new FileInputStream(fileInfo.getFile());
            fin.read(bytes);
            fin.close();

            Map<Object, Object> fileData = new HashMap<Object, Object>();
            fileData.put("name", fileInfo.getFile().getName());
            fileData.put("type", "image/" + fileInfo.getFile().getName().substring(fileInfo.getFile().getName().lastIndexOf(".") + 1));
            System.out.println(fileInfo.getFile().getName().substring(fileInfo.getFile().getName().lastIndexOf(".") + 1));
            fileData.put("bits", bytes);
            fileData.put("overwrite", Boolean.FALSE);
            Object[] params = new Object[]{blogId, username, password, fileData};//wp.uploadFile//wp.uploadFile
            Object uploadResult = worRpcClientclient.execute("metaWeblog.newMediaObject", params);//newMediaObject//metaWeblog.newMediaObject//metaWeblog.newMediaObject
            result = uploadResult.toString();//metaWeblog.uploadFile
            resultstr = (HashMap<String, String>) uploadResult;
            json = gson.toJson(uploadResult);
            JsonElement jsonElement = jsonParser.parse(json);
            if (jsonElement.isJsonNull()) {
                uploadResult = worRpcClientclient.execute("wp.uploadFile", params);
                if (jsonElement.isJsonObject()) {
                    FileInfo file = new FileInfo(null, json);
                    jsonObject = jsonElement.getAsJsonObject();
                    //{"id":"453","file":"3.png","type":"png","url":"http://localhost:8282/wordpress/wp-content/uploads/2014/08/352.png"}
                    System.out.println(jsonObject.get("file"));
                    result = jsonObject.get("file").toString().replace("\"", ""); //More logic needed here
                    fileId = jsonObject.get("id").toString().replace("\"", "");
                    isId = Utils.isInteger(fileId);
                    resultMap.put("boolean", isId + "");
                    resultMap.put("id", fileId);
                    resultMap.put("url", jsonObject.get("url").toString());
                    resultMap.put("type", fileInfo.getFile().getName().substring(fileInfo.getFile().getName().lastIndexOf(".") + 1));
                }
            } else {
                if (jsonElement.isJsonObject()) {
                    FileInfo file = new FileInfo(null, json);
                    jsonObject = jsonElement.getAsJsonObject();
                    //{"id":"453","file":"3.png","type":"png","url":"http://localhost:8282/wordpress/wp-content/uploads/2014/08/352.png"}
                    System.out.println(jsonObject.get("file"));
                    result = jsonObject.get("file").toString().replace("\"", ""); //More logic needed here
                    fileId = jsonObject.get("id").toString().replace("\"", "");
                    isId = Utils.isInteger(fileId);
                    resultMap.put("boolean", isId + "");
                    resultMap.put("id", fileId);
                    resultMap.put("url", jsonObject.get("url").toString());
                    resultMap.put("type", fileInfo.getFile().getName().substring(fileInfo.getFile().getName().lastIndexOf(".") + 1));


                }
            }
            LOG.log(Level.FINER, "Attach file response: {0}", uploadResult.toString());

        } catch (XmlRpcException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            throw new WordPressServerConnectionException("Could not attach files.", ex);
        }
        return resultMap;
    }

    public Map<String, String> attachVideoFileToPost(FileInfo fileInfo, int blogId) throws WordPressServerConnectionException {
        XmlRpcClient worRpcClientclient;
        String result = "";
        Gson gson = new Gson();
        JsonObject jsonObject;
        JsonParser jsonParser = new JsonParser();
        String json;
        HashMap<String, String> resultstr;
        HashMap<String, String> resultMap = new HashMap<String, String>();

        boolean isId = false;
        String fileId;
        //loadKeyStore();//CHANGE ME

        try {
            Utils.sslHanshake();

            worRpcClientclient = getXmlRpcClient();
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(this.websiteUrl + "/xmlrpc.php"));
            config.setEnabledForExtensions(true);
            config.setEnabledForExceptions(true);
            config.setConnectionTimeout(connectionTimeout);
            config.setConnectionTimeout(this.replyTimeOut);
            worRpcClientclient.setConfig(config);
            //File file = fileInfo.getFile();
            byte[] bytes = new byte[(int) fileInfo.getFile().length()];
            FileInputStream fin = new FileInputStream(fileInfo.getFile());
            fin.read(bytes);
            fin.close();

            Map<Object, Object> fileData = new HashMap<Object, Object>();
            fileData.put("name", fileInfo.getFile().getName());
//          fileData.put("type", "video/" + fileInfo.getFile().getName().substring(fileInfo.getFile().getName().lastIndexOf(".") + 1));
            fileData.put("type", "video/" + fileInfo.getFile().getName().substring(fileInfo.getFile().getName().lastIndexOf(".") + 1));

            System.out.println(fileInfo.getFile().getName().substring(fileInfo.getFile().getName().lastIndexOf(".") + 1));
            fileData.put("bits", bytes);
            //fileData.put("post_id", blogId);
            fileData.put("overwrite", Boolean.TRUE);

            Object[] params = new Object[]{blogId, username, password, fileData};//wp.uploadFile//wp.uploadFile
            //Object uploadResult = worRpcClientclient.execute("mw_newMediaObject", params);//newMediaObject//metaWeblog.newMediaObject//metaWeblog.newMediaObject

//            Object uploadResult = worRpcClientclient.execute("metaWeblog.newMediaObject", params);
            Object uploadResult = worRpcClientclient.execute("wp.uploadFile", params);
            System.out.println(uploadResult + "::::::::::::::::::::::");
            result = uploadResult.toString();//metaWeblog.uploadFile
            resultstr = (HashMap<String, String>) uploadResult;
            json = gson.toJson(uploadResult);
            JsonElement jsonElement = jsonParser.parse(json);
            if (jsonElement.isJsonNull()) {
                uploadResult = worRpcClientclient.execute("wp.uploadFile", params);
                if (jsonElement.isJsonObject()) {
                    FileInfo file = new FileInfo(null, json);
                    jsonObject = jsonElement.getAsJsonObject();
                    //{"id":"453","file":"3.png","type":"png","url":"http://localhost:8282/wordpress/wp-content/uploads/2014/08/352.png"}
                    System.out.println(jsonObject.get("file"));
                    result = jsonObject.get("file").toString().replace("\"", ""); //More logic needed here
                    fileId = jsonObject.get("id").toString().replace("\"", "");
                    isId = Utils.isInteger(fileId);
                    resultMap.put("boolean", isId + "");
                    resultMap.put("id", fileId);
                    resultMap.put("url", jsonObject.get("url").toString());
                    resultMap.put("type", fileInfo.getFile().getName().substring(fileInfo.getFile().getName().lastIndexOf(".") + 1));
                }
            } else {
                if (jsonElement.isJsonObject()) {
                    FileInfo file = new FileInfo(null, json);
                    jsonObject = jsonElement.getAsJsonObject();
                    //{"id":"453","file":"3.png","type":"png","url":"http://localhost:8282/wordpress/wp-content/uploads/2014/08/352.png"}
                    System.out.println(jsonObject.get("file"));
                    result = jsonObject.get("file").toString().replace("\"", ""); //More logic needed here
                    fileId = jsonObject.get("id").toString().replace("\"", "");
                    isId = Utils.isInteger(fileId);
                    resultMap.put("boolean", isId + "");
                    resultMap.put("id", fileId);
                    resultMap.put("url", jsonObject.get("url").toString());
                    resultMap.put("type", fileInfo.getFile().getName().substring(fileInfo.getFile().getName().lastIndexOf(".") + 1));


                }
            }
            LOG.log(Level.FINER, "Attach file response: {0}", uploadResult.toString());

        } catch (XmlRpcException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            throw new WordPressServerConnectionException("Could not attach files.", ex);
        }
        return resultMap;
    }

    public Map<String, String> attachMp3FileToPost(FileInfo fileInfo, int blogId) throws WordPressServerConnectionException {
        XmlRpcClient worRpcClientclient;
        String result = "";
        Gson gson = new Gson();
        JsonObject jsonObject;
        JsonParser jsonParser = new JsonParser();
        String json;
        HashMap<String, String> resultstr;
        HashMap<String, String> resultMap = new HashMap<String, String>();

        boolean isId = false;
        String fileId;
        //loadKeyStore();//CHANGE ME

        try {
            Utils.sslHanshake();

            worRpcClientclient = getXmlRpcClient();
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(this.websiteUrl + "/xmlrpc.php"));
            config.setEnabledForExtensions(true);
            config.setEnabledForExceptions(true);
            config.setConnectionTimeout(connectionTimeout);
            config.setConnectionTimeout(this.replyTimeOut);
            worRpcClientclient.setConfig(config);
            //File file = fileInfo.getFile();
            byte[] bytes = new byte[(int) fileInfo.getFile().length()];
            FileInputStream fin = new FileInputStream(fileInfo.getFile());
            fin.read(bytes);
            fin.close();

            Map<Object, Object> fileData = new HashMap<Object, Object>();
            fileData.put("name", fileInfo.getFile().getName());
//          fileData.put("type", "video/" + fileInfo.getFile().getName().substring(fileInfo.getFile().getName().lastIndexOf(".") + 1));
            fileData.put("type", "audio/" + fileInfo.getFile().getName().substring(fileInfo.getFile().getName().lastIndexOf(".") + 1));

            System.out.println(fileInfo.getFile().getName().substring(fileInfo.getFile().getName().lastIndexOf(".") + 1));
            fileData.put("bits", bytes);
            fileData.put("overwrite", Boolean.FALSE);
            Object[] params = new Object[]{blogId, username, password, fileData};//wp.uploadFile//wp.uploadFile
            //Object uploadResult = worRpcClientclient.execute("mw_newMediaObject", params);//newMediaObject//metaWeblog.newMediaObject//metaWeblog.newMediaObject
            Object uploadResult = worRpcClientclient.execute("metaWeblog.newMediaObject", params);
            System.out.println(uploadResult + ">>>>>>>>>>>>>>>>>>>>>>>>>.");
            result = uploadResult.toString();//metaWeblog.uploadFile
            resultstr = (HashMap<String, String>) uploadResult;
            json = gson.toJson(uploadResult);
            JsonElement jsonElement = jsonParser.parse(json);
            if (jsonElement.isJsonNull()) {
                uploadResult = worRpcClientclient.execute("metaWeblog.newMediaObject", params);
                if (jsonElement.isJsonObject()) {
                    FileInfo file = new FileInfo(null, json);
                    jsonObject = jsonElement.getAsJsonObject();
                    //{"id":"453","file":"3.png","type":"png","url":"http://localhost:8282/wordpress/wp-content/uploads/2014/08/352.png"}
                    //"[audio mp3=\"https://radioafricaplatforms.com/apps/converge/wp-content/uploads/2014/09/3.mp3\"][/audio]");
                    System.out.println(jsonObject.get("file"));
                    result = jsonObject.get("file").toString().replace("\"", ""); //More logic needed here
                    fileId = jsonObject.get("id").toString().replace("\"", "");
                    isId = Utils.isInteger(fileId);
                    resultMap.put("boolean", isId + "");
                    resultMap.put("id", fileId);
                    resultMap.put("type", fileInfo.getFile().getName().substring(fileInfo.getFile().getName().lastIndexOf(".") + 1));
                    resultMap.put("url", jsonObject.get("url").toString());

                }
            } else {
                if (jsonElement.isJsonObject()) {
                    FileInfo file = new FileInfo(null, json);
                    jsonObject = jsonElement.getAsJsonObject();
                    //{"id":"453","file":"3.png","type":"png","url":"http://localhost:8282/wordpress/wp-content/uploads/2014/08/352.png"}
                    System.out.println(jsonObject.get("file"));
                    result = jsonObject.get("file").toString().replace("\"", ""); //More logic needed here
                    fileId = jsonObject.get("id").toString().replace("\"", "");
                    isId = Utils.isInteger(fileId);
                    resultMap.put("boolean", isId + "");
                    resultMap.put("id", fileId);
                    resultMap.put("url", jsonObject.get("url").toString());

                }
            }
            LOG.log(Level.FINER, "Attach file response: {0}", uploadResult.toString());

        } catch (XmlRpcException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            throw new WordPressServerConnectionException("Could not attach files.", ex);
        }
        return resultMap;
    }

    public boolean attachFiles(int blogId, List<FileInfo> files) throws WordPressServerConnectionException {
        String result = "";
        boolean fileattached = false;
        Gson gson = new Gson();
        JsonObject jsonObject;
        JsonParser jsonParser = new JsonParser();
        String json;
        //loadKeyStore();//CHANGE ME

        //
        try {
            Utils.sslHanshake();

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

                json = gson.toJson(uploadResult);
                JsonElement jsonElement = jsonParser.parse(json);
                if (jsonElement.isJsonObject()) {
                    FileInfo file_ = new FileInfo(null, json);
                    jsonObject = jsonElement.getAsJsonObject();
                    //{"id":"453","file":"3.png","type":"png","url":"http://localhost:8282/wordpress/wp-content/uploads/2014/08/352.png"}
                    System.out.println(jsonObject.get("file"));
                    result = jsonObject.get("file").toString().replace("\"", ""); //More logic needed here
                    if (!result.substring(result.lastIndexOf(".") + 1).equalsIgnoreCase("")) {
                        fileattached = true;
                    }

                }
            }
            LOG.log(Level.FINER, "Attach file response: {0}", result);
        } catch (XmlRpcException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            throw new WordPressServerConnectionException("Could not attach files.", ex);
        }
        return fileattached;
    }

    public void loadKeyStore() {
        try {

            // KEYSTORE with self signed client certificate
//            KeyStore ksClient = KeyStore.getInstance("jks");
            KeyStore ksClient = KeyStore.getInstance(KeyStore.getDefaultType());

            ksClient.load(new FileInputStream("C:/Program Files/Java/jdk1.7.0_60/jre/lib/security/key.pem"), "changeit".toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ksClient, "changeit".toCharArray());

            // TRUSTSTORE with server public certificate
            KeyStore ksCACert = KeyStore.getInstance(KeyStore.getDefaultType());
            ksCACert.load(new FileInputStream("C:/Program Files/Java/jdk1.7.0_60/jre/lib/security/cacerts"), "changeit".toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(ksCACert);
            // SSL CONTEXT
            SSLContext context = SSLContext.getInstance("TLS");
            SSLContext.setDefault(context);

            // Load the Keystore and Truststore for the server
            context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(hv);

        } catch (KeyManagementException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnrecoverableKeyException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CertificateException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (KeyStoreException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

//    public boolean setKeyStore() {
//        String home="";
//        String keypath="//converge";
//        try {
//            
//            home =System.getProperty("user.home");
//// Meaningful variable names for the arguments
//            
//            String keyStoreFileName = args[0];
//            String certificateChainFileName = args[1];
//            String privateKeyFileName = args[2];
//            String entryAlias = args[3];
//
//// Get the password for the keystore.
//            System.out.println("Keystore password:  ");
//            String keyStorePassword = (new BufferedReader( new InputStreamReader(System.in))).readLine();
//
//// Load the keystore
//            KeyStore keyStore = KeyStore.getInstance("jks");
//            FileInputStream keyStoreInputStream = new FileInputStream(keyStoreFileName);
//            keyStore.load(keyStoreInputStream, keyStorePassword.toCharArray());
//            keyStoreInputStream.close();
//
//// Load the certificate chain (in X.509 DER encoding).
//            FileInputStream certificateStream = new FileInputStream(certificateChainFileName);
//            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//// Required because Java is STUPID.  You can't just cast the result
//// of toArray to Certificate[].
//            java.security.cert.Certificate[] chain = {};
//            chain = certificateFactory.generateCertificates(certificateStream).toArray(chain);
//            certificateStream.close();
//
//// Load the private key (in PKCS#8 DER encoding).
//            File keyFile = new File(privateKeyFileName);
//            byte[] encodedKey = new byte[(int) keyFile.length()];
//            FileInputStream keyInputStream = new FileInputStream(keyFile);
//            keyInputStream.read(encodedKey);
//            keyInputStream.close();
//            KeyFactory rSAKeyFactory = KeyFactory.getInstance("RSA");
//            PrivateKey privateKey = rSAKeyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
//
//// Add the new entry
//            System.out.println("Private key entry password:  ");
//
//            String privateKeyEntryPassword = (new BufferedReader(new InputStream() {}Reader(System.in))).readLine();
//            keyStore.setEntry(entryAlias,new KeyStore.PrivateKeyEntry(privateKey, chain), new KeyStore.PasswordProtection(privateKeyEntryPassword.toCharArray()));
//
//// Write out the keystore
//            FileOutputStream keyStoreOutputStream = new FileOutputStream(keyStoreFileName);
//            keyStore.store(keyStoreOutputStream, keyStorePassword.toCharArray());
//            keyStoreOutputStream.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.exit(1);
//        }
//        return true;
//    }
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
