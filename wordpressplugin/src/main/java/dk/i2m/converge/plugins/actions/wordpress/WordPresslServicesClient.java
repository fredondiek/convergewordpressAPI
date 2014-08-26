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

    public boolean createNewPost(Object[] itemsPostParams) {
        String result;
        XmlRpcClient wordpRpcClient;
        boolean created = false;
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(this.websiteUrl + "/xmlrpc.php"));
            config.setEnabledForExtensions(true);
            config.setEnabledForExceptions(true);
            config.setConnectionTimeout(connectionTimeout);
            config.setConnectionTimeout(this.replyTimeOut);
            wordpRpcClient = getXmlRpcClient();
            wordpRpcClient.setConfig(config);
            result = (String) wordpRpcClient.execute("metaWeblog.newPost", itemsPostParams);
            System.out.println(result);
            if (Integer.parseInt(result) != 0) {
                created = true;
            } else {
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (XmlRpcException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return created;
    }

    public PostInfo retrieveExistingPost(int blogId) throws IOException {
        HashMap<String, String> resultstr;
        PostInfo blog = null;
        Gson gson = new Gson();
        JsonObject jsonObject;
        JsonParser jsonParser = new JsonParser();
        String json;

        try {
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
            resultstr = (HashMap<String, String>) uploadResult;
            json = gson.toJson(uploadResult);
            JsonElement jsonElement = jsonParser.parse(json);
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
