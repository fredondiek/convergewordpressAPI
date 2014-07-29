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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.workflow.Edition;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.tika.Tika;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 * API for communicating with WordPress Services. Example of using the client:
 * <code>
 *    WordPressServicesClient client = new WordPressServicesClient("http://mywebsite", "my_endpoint", "my_user", "my_password");
 *    if (client.login()) {
 *       // Logged in
 *    } else {
 *      // Incorrect username and/or password, or incorrect hostname and/or endpoint
 *    }
 * </code>
 *
 * @author <a href="mailto:allan@i2m.dk">Allan Lykke Christensen</a>
 */
public class WordPresslServicesClient {

    /**
     * Header for setting the Cross-script request forgery token.
     *
     * @since 1.1.11
     */
    private static final String HEADER_X_CSRF_TOKEN = "X-CSRF-Token";
    private static final Logger LOG = Logger.getLogger(WordPresslServicesClient.class.getName());
    private String hostname;
    private String endpoint;
    private Integer connectionTimeout = 30000;
    private Integer socketTimeout = 30000;
    private String username;
    private String password;
    private HttpClient httpClient;
    private String sessionId = null;
    private String sessionName = null;
    private String csrfToken = null;

    /**
     * Creates a new instance of {@link DrupalServicesClient}.
     */
    public WordPresslServicesClient() {
        this("", "", "", "");
    }

    /**
     * Creates a new instance of {@link DrupalServicesClient}.
     *
     * @param hostname Host name of the WordPress instance
     * @param endpoint Services endpoint to communicate with
     * @param username Username with privilege to access the endpoint
     * @param password Password matching the {@code username}
     */
    public WordPresslServicesClient(String hostname, String endpoint, String username, String password) {
        this.hostname = hostname;
        this.endpoint = endpoint;
        this.username = username;
        this.password = password;
    }

    /**
     * Creates a new instance of {@link DrupalServicesClient}.
     *
     * @param hostname Host name of the WordPress instance
     * @param endpoint Services endpoint to communicate with
     * @param username Username with privilege to access the endpoint
     * @param password Password matching the {@code username}
     * @param socketTimeout Socket timeout (ms)
     * @param connectionTimeout Connection timeout (ms)
     */
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

    public boolean login() throws WordPressServerConnectionException {
        try {
            URIBuilder builder = new URIBuilder(this.hostname + "/" + this.endpoint + "/xmlrpc.php");
            List<NameValuePair> values = new ArrayList<NameValuePair>();
            values.add(new BasicNameValuePair("username", this.username));
            values.add(new BasicNameValuePair("password", this.password));
            HttpPost method = new HttpPost(builder.build());
            method.setEntity(new UrlEncodedFormEntity(values, Consts.UTF_8));
            method.setHeader("Accept", "application/json");
            HttpResponse response = getHttpClient().execute(method);

            if (response.getStatusLine().getStatusCode() == 200) {
                StringWriter writer = new StringWriter();
                InputStream is = response.getEntity().getContent();
                IOUtils.copy(is, writer);
                EntityUtils.consume(response.getEntity());
                String jsonResponse = writer.toString();
                LOG.log(Level.FINEST, jsonResponse);
                JsonParser parser = new JsonParser();
                try {
                    JsonObject obj = (JsonObject) parser.parse(jsonResponse);
                    this.sessionId = obj.get("sessid").getAsString();
                    this.sessionName = obj.get("session_name").getAsString();
                } catch (JsonSyntaxException ex) {
                    throw new WordPressServerConnectionException("Unknown JSON response. " + jsonResponse, ex);
                } catch (NullPointerException ex) {
                    throw new WordPressServerConnectionException("sessid or session_name missing in JSON response", ex);
                }
                obtainSessionToken();
                return true;
            } else {
                // Examine the status code and determine if an exception should be thrown (e.g. 404 error)
                return false;
            }
        } catch (IOException ex) {
            throw new WordPressServerConnectionException("Could not login", ex);
        } catch (URISyntaxException ex) {
            throw new WordPressServerConnectionException("Could not login. Server URI incorrect.", ex);
        }
    }

    public void logout() throws WordPressServerConnectionException {
        try {
            HttpPost method = createHttpPost(this.hostname + "/" + this.endpoint + "/xmlrpc.php");
            ResponseHandler<String> handler = new BasicResponseHandler();
            getHttpClient().execute(method, handler);
        } catch (IOException ex) {
            throw new WordPressServerConnectionException("Could not logout.", ex);
        } catch (URISyntaxException ex) {
            throw new WordPressServerConnectionException("Could not logout. URI incorrect", ex);
        }
    }

    /**
     * Determine if a given resource exists.
     *
     * @param resource Name of the resource as defined in the Wordpress Services
     * module
     * @param id Unique identifier of the {@code resource}
     * @return {@code true} if the {@code resource} with the given {@code id}
     * exists, otherwise @code false}
     */
    public boolean exists(String resource, Long id) throws WordPressServerConnectionException {
        try {
            HttpGet method = createHttpGet(this.hostname + "/" + this.endpoint + "/xmlrpc.php");

            HttpResponse response = getHttpClient().execute(method);
            int status = response.getStatusLine().getStatusCode();

            EntityUtils.consume(response.getEntity());

            if (status == 404) {
                return false;
            } else if (status == 200) {
                return true;
            } else {
                throw new WordPressServerConnectionException("Unexpected response from Drupal server: " + status);
            }
        } catch (IOException ex) {
            throw new WordPressServerConnectionException("Could not determine if resource exists", ex);
        } catch (URISyntaxException ex) {
            throw new WordPressServerConnectionException("Could not determine if resource exists. Server URI incorrect. ", ex);
        }
    }

    public String createNewPost(Object[] itemsPostParams) {
        String result = "";
        XmlRpcClient wordpRpcClient = null;
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(this.hostname + "/" + this.endpoint + "/xmlrpc.php"));
            config.setEnabledForExtensions(true);
            config.setEnabledForExceptions(true);
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

    public String retrieveExistingPost(Long id) throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder(this.hostname + "/" + this.endpoint + "/xmlrpc.php");
        HttpGet method = new HttpGet(builder.build());

        ResponseHandler<String> handler = new BasicResponseHandler();

        String output = getHttpClient().execute(method, handler);
        return output;
    }

    public String updateExistingPost(Long id, UrlEncodedFormEntity entity) throws URISyntaxException, IOException {
        HttpPut method = createHttpPut(this.hostname + "/" + this.endpoint + "/xmlrpc.php");
        method.setEntity(entity);

        ResponseHandler<String> handler = new BasicResponseHandler();
        String response = getHttpClient().execute(method, handler);
        return response;
    }

    public boolean deleteExistingPost(String resource, Long id) throws URISyntaxException, IOException {
        URIBuilder builder = new URIBuilder(this.hostname + "/" + this.endpoint + "/xmlrpc.php");
        LOG.log(Level.FINE, "Deleting: {0}", builder.build());
        HttpDelete method = new HttpDelete(builder.build());

        HttpResponse response = getHttpClient().execute(method);
        int status = response.getStatusLine().getStatusCode();
        StringWriter writer = new StringWriter();
        IOUtils.copy(response.getEntity().getContent(), writer);
        System.out.println(writer.toString());
        EntityUtils.consume(response.getEntity());
        if (status == 200) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Attaches one or more files to an existing node.
     *
     * @param id Unique identifier of the node ({@code nid})
     * @param fieldName Name of the field used for storing files
     * @param files {@link List} of files to attach
     * @return Response from attaching the files
     * @throws DrupalServerConnectionException If an unexpected result is
     * returned from the Drupal service
     */
    public String attachFileToPost(FileInfo fileInfo) throws WordPressServerConnectionException {
        XmlRpcClient worRpcClientclient = null;
        String result = "";
        try {
            worRpcClientclient = getXmlRpcClient();
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(this.hostname + "/" + this.endpoint + "/xmlrpc.php"));
            config.setEnabledForExtensions(true);
            config.setEnabledForExceptions(true);
            worRpcClientclient.setConfig(config);

            byte[] bytes = new byte[(int) fileInfo.getFile().length()];
            FileInputStream fin = new FileInputStream(fileInfo.getFile());
            fin.read(bytes);
            fin.close();

            Map<Object, Object> fileData = new HashMap<Object, Object>();
            fileData.put("name", fileInfo.getFile().getName());
            fileData.put("type", fileInfo.getFile().getName().substring(fileInfo.getFile().getName().lastIndexOf(".")));
            fileData.put("bits", bytes);
            fileData.put("overwrite", Boolean.TRUE);
            Object[] params = new Object[]{new Integer(0), username, password, fileData};
            Object uploadResult = worRpcClientclient.execute("metaWeblog.uploadFile", params);//newMediaObject
            result = uploadResult.toString();

            LOG.log(Level.FINER, "Attach file response: {0}", uploadResult.toString());
            return result;
        } catch (XmlRpcException ex) {
            Logger.getLogger(WordPresslServicesClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            throw new WordPressServerConnectionException("Could not attach files.", ex);
        }
        return result;
    }

    public String attachFile(Long id, String fieldName, List<FileInfo> files) throws WordPressServerConnectionException {
        String result = "";
        try {
            int i = 0;
            XmlRpcClient worRpcClientclient = null;
            for (FileInfo file : files) {
                i++;
                worRpcClientclient = getXmlRpcClient();
                XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                config.setServerURL(new URL(this.hostname + "/" + this.endpoint + "/xmlrpc.php"));
                worRpcClientclient.setConfig(config);
                worRpcClientclient = getXmlRpcClient();
                worRpcClientclient.setConfig(config);
                byte[] bytes = new byte[(int) file.getFile().length()];
                FileInputStream fin = new FileInputStream(file.getFile());
                fin.read(bytes);
                fin.close();

                Map<Object, Object> fileData = new HashMap<Object, Object>();
                fileData.put("name", file.getFile().getName());
                fileData.put("type", file.getFile().getName().substring(file.getFile().getName().lastIndexOf(".")));
                fileData.put("bits", bytes);
                fileData.put("overwrite", Boolean.TRUE);
                Object[] params = new Object[]{new Integer(0), username, password, fileData};
                Object uploadResult = worRpcClientclient.execute("metaWeblog.uploadFile", params);
                result = uploadResult.toString();
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

    /**
     * Removes all the files from an existing node.
     *
     * @param id Unique identifier of the node
     * @param fieldName Name of the field used for storing files
     * @return Response from removing all the files
     * @throws DrupalServerConnectionException If an unexpected result is
     * returned from the WordPress service
     */
//    public String removeFilesFromPost(Long id, String fieldName) throws WordPressServerConnectionException {
//        return attachFileToPost(id, fieldName, new ArrayList<FileInfo>());
//    }
    /**
     * Gets the ID of the session with the WordPress instance.
     *
     * @return ID of the session with the specified WordPress instance. If no
     * session has been initiated {@code null} is returned.
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Gets the name of the session with the WordPress instance.
     *
     * @return Name of the session with the specified WordPress instance. If no
     * session has been initiated {@code null} is returned.
     */
    public String getSessionName() {
        return sessionName;
    }

    /**
     * Gets the session cookie for authenticated communication with the
     * WordPress instance after login.
     *
     * @return Cookie to use to identify the authenticate session initiated upon
     * logging in
     */
    public String getSessionCookie() {
        return getSessionName() + "=" + getSessionId();
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }

    private HttpClient getHttpClient() {
        if (this.httpClient == null) {
            LOG.log(Level.FINER, "Creating a HttpClient");
            BasicHttpParams params = new BasicHttpParams();
            params.setParameter(AllClientPNames.CONNECTION_TIMEOUT, this.connectionTimeout)
                    .setParameter(AllClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH)
                    .setParameter(AllClientPNames.HTTP_CONTENT_CHARSET, HTTP.UTF_8)
                    .setParameter(AllClientPNames.SO_TIMEOUT, this.socketTimeout);
            this.httpClient = new DefaultHttpClient(params);
        }
        return this.httpClient;
    }

    /**
     * Gets the CSRF Session Token and store it in {@link #csrfToken}.
     *
     * @throws WordPress If an invalid response was received from the WordPress
     * service
     */
    private void obtainSessionToken() throws WordPressServerConnectionException {
        try {
            URIBuilder builder = new URIBuilder(this.hostname + "/services/session/token");
            HttpGet method = new HttpGet(builder.build());
            HttpResponse response = getHttpClient().execute(method);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new WordPressServerConnectionException("Unexpected response (" + response.getStatusLine().getStatusCode() + ") from token service");
            } else {
                StringWriter writer = new StringWriter();
                InputStream is = response.getEntity().getContent();
                IOUtils.copy(is, writer);
                EntityUtils.consume(response.getEntity());
                this.csrfToken = writer.toString();
            }

        } catch (URISyntaxException ex) {
            throw new WordPressServerConnectionException("Incorrect URI: " + ex.getMessage());
        } catch (IOException ex) {
            throw new WordPressServerConnectionException("Unexpected response (" + ex.getMessage() + ") from token service");
        }
    }

    private HttpPost createHttpPost(String url) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        HttpPost method = new HttpPost(builder.build());
        method.setHeader(HEADER_X_CSRF_TOKEN, getCsrfToken());
        return method;
    }

    private HttpGet createHttpGet(String url) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        HttpGet method = new HttpGet(builder.build());
        method.setHeader(HEADER_X_CSRF_TOKEN, getCsrfToken());
        return method;
    }

    private HttpPut createHttpPut(String url) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(url);
        HttpPut method = new HttpPut(builder.build());
        method.setHeader(HEADER_X_CSRF_TOKEN, getCsrfToken());
        return method;
    }

    @Override
    protected void finalize() throws Throwable {
        getHttpClient().getConnectionManager().shutdown();
        super.finalize();
    }
//     private UrlEncodedFormEntity toUrlEncodedFormEntity(NewsItemPlacement nip, String publishOn) {
//        Edition edition = nip.getEdition();
//        List<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair("type", this.nodeType));
//        params.add(new BasicNameValuePair("publish_on", publishOn));
//        params.add(new BasicNameValuePair("date", sdf.format(edition.getPublicationDate().getTime())));
//        params.add(new BasicNameValuePair("title", StringUtils.left(StringEscapeUtils.escapeHtml(nip.getNewsItem().getTitle()), 255)));
//        params.add(new BasicNameValuePair("language", "und"));
//        //params.add(new BasicNameValuePair("body[und][0][summary]", nip"This is the summary"));
//        params.add(new BasicNameValuePair("body[und][0][value]", nip.getNewsItem().getStory()));
//        params.add(new BasicNameValuePair("body[und][0][format]", "full_html"));
//        params.add(new BasicNameValuePair("field_author[und][0][value]", getAuthor(nip.getNewsItem())));
//        params.add(new BasicNameValuePair("field_newsitem[und][0][value]", "" + nip.getNewsItem().getId()));
//        params.add(new BasicNameValuePair("field_edition[und][0][value]", "" + nip.getEdition().getId()));
//        try {
//            params.add(new BasicNameValuePair("field_section[und][0]", getSection(nip)));
//        } catch (UnmappedSectionException ex) {
//            // Section not mapped
//        }
//        if (nip.getStart() != null) {
//            LOG.log(Level.FINE, "NewsItemPlacement # {0}. Setting Placement Start (" + nip.getStart() + ")", nip.getId());
//            params.add(new BasicNameValuePair("field_placement_start[und][0]", nip.getStart().toString()));
//        } else {
//            LOG.log(Level.FINE, "NewsItemPlacement # {0}. Skipping Placement Start (null)", nip.getId());
//        }
//
//        if (nip.getPosition() != null) {
//            LOG.log(Level.FINE, "NewsItemPlacement # {0}. Setting Placement Position (" + nip.getPosition() + ")", nip.getId());
//            params.add(new BasicNameValuePair("field_placement_position[und][0]", nip.getPosition().toString()));
//        } else {
//            LOG.log(Level.FINE, "NewsItemPlacement # {0}. Skipping Placement Position (null)", nip.getId());
//        }
//
//        return new UrlEncodedFormEntity(params, Charset.defaultCharset());
//    }
}
