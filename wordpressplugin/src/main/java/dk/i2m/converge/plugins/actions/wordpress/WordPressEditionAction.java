/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.i2m.converge.plugins.actions.wordpress;

import dk.i2m.converge.core.annotations.OutletAction;
import dk.i2m.converge.core.content.NewsItem;
import dk.i2m.converge.core.content.NewsItemActor;
import dk.i2m.converge.core.content.NewsItemEditionState;
import dk.i2m.converge.core.content.NewsItemMediaAttachment;
import dk.i2m.converge.core.content.NewsItemPlacement;
import dk.i2m.converge.core.content.catalogue.MediaItem;
import dk.i2m.converge.core.content.catalogue.MediaItemRendition;
import dk.i2m.converge.core.content.catalogue.RenditionNotFoundException;
import dk.i2m.converge.core.plugin.EditionAction;
import dk.i2m.converge.core.plugin.PluginContext;
import dk.i2m.converge.core.workflow.Edition;
import dk.i2m.converge.core.workflow.OutletEditionAction;
import dk.i2m.converge.core.workflow.OutletEditionActionProperty;
import dk.i2m.converge.core.workflow.Section;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

/**
 *
 * @author fred
 */
@OutletAction
public class WordPressEditionAction implements EditionAction {

    public static final int MEDIA_ITEM_TITLE_LENGTH = 1024;
    private String hostname;
    private String endpoint;
    private String username;
    private String password;
    private String connectionTimeout;
    private String socketTimeout;
    private int errors = 0;
   private WordPresslServicesClient wordPressServiceClient;
    private enum Property {
        CONNECTION_TIMEOUT,
        IMAGE_RENDITION,
        NODE_LANGUAGE,
        NODE_TYPE,
        PASSWORD,
        PUBLISH_DELAY,
        PUBLISH_IMMEDIATELY,
        PUBLISHED,
        POSTID,
        POST_STATUS,
        SECTION_MAPPING,
        ALLOW_COMMENTS,
        BLOG_ID,
        POST_TYPE,
        KEYWORDS,
        CATEGORIES,
        CUSTOME_FIELDS,
        IGNORED_MAPPING,
        SERVICE_ENDPOINT,
        SOCKET_TIMEOUT,
        URL,
        USERNAME,
        TAG,    
        
    }
    private static final Logger LOG = Logger.getLogger(WordPressEditionAction.class.getName());
    private static final String UPLOADING = "UPLOADING";
    private static final String UPLOADED = "UPLOADED";
    private static final String FAILED = "FAILED";
    private static final String DATE = "date";
    private static final String NID_LABEL = "nid";
    private static final String URI_LABEL = "uri";
    private static final String STATUS_LABEL = "status";
    private ResourceBundle bundle = ResourceBundle.getBundle("dk.i2m.converge.plugins.wordpress.Messages");
    private Map<String, String> availableProperties;
    private Map<Long, Long> sectionMapping;
    private DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String publishDelay;
    private String publishImmediately;
    private String published;
    private String renditionName;
    private String nodeLanguage;
    private String nodeType;
    private String mappings;
    private String postId;
    private String post_status;
    private String blog_id;
    private int allow_comments;
    private String category;
    private String custom_field;
    private int ignored_ping;
    private String tag;  
    
    
    
    
    
    private void init(OutletEditionAction action) {
        Map<String, String> properties = action.getPropertiesAsMap();


        StringBuilder mapBuilder = new StringBuilder();
        for (OutletEditionActionProperty actionProperty : action.getProperties()) {
            if (actionProperty.getKey().equalsIgnoreCase(Property.SECTION_MAPPING.name())) {
                if (mapBuilder.length() > 0) {
                    mapBuilder.append(";");
                }
                mapBuilder.append(actionProperty.getValue());
            }
        }
        
    mappings = mapBuilder.toString();
    nodeType = properties.get(Property.NODE_TYPE.name());
    nodeLanguage = properties.get(Property.NODE_LANGUAGE.name());
    publishDelay = properties.get(Property.PUBLISH_DELAY.name());
    publishImmediately = properties.get(Property.PUBLISH_IMMEDIATELY.name());
    renditionName = properties.get(Property.IMAGE_RENDITION.name());
    publishImmediately = properties.get(Property.PUBLISH_IMMEDIATELY.name());       
    published=properties.get(Property.PUBLISHED.name());    
    postId=properties.get(Property.POSTID.name());
    post_status=properties.get(Property.POST_STATUS.name());
    blog_id =properties.get(Property.BLOG_ID.name());
    category = properties.get(Property.CATEGORIES.name());
    custom_field= properties.get(Property.CUSTOME_FIELDS.name());
    tag=properties.get(Property.TAG.name());    
    sectionMapping = new HashMap<Long, Long>();
        
        

        this.hostname = properties.get(Property.URL.name());
        this.endpoint = properties.get(Property.SERVICE_ENDPOINT.name());
        this.username = properties.get(Property.USERNAME.name());
        this.password = properties.get(Property.PASSWORD.name());
        this.connectionTimeout = properties.get(Property.CONNECTION_TIMEOUT.name());
        this.socketTimeout = properties.get(Property.SOCKET_TIMEOUT.name());

        if (hostname == null) {
            throw new IllegalArgumentException("'hostname' cannot be null");
        } else if (endpoint == null) {
            throw new IllegalArgumentException("'endpoint' cannot be null");
        } else if (username == null) {
            throw new IllegalArgumentException("'username' cannot be null");
        } else if (password == null) {
            throw new IllegalArgumentException("'password' cannot be null");
        }

        if (nodeType == null) {
            throw new IllegalArgumentException("'nodeType' cannot be null");
        } else if (mappings == null) {
            throw new IllegalArgumentException("'mappings' cannot be null");
        }

        if (publishImmediately == null && publishDelay == null) {
            throw new IllegalArgumentException("'publishImmediately' or 'publishDelay' cannot be null");
        } else if (publishImmediately == null && publishDelay != null) {
            if (!isInteger(publishDelay)) {
                throw new IllegalArgumentException("'publishDelay' must be an integer");
            } else if (Integer.valueOf(publishDelay) <= 0) {
                throw new IllegalArgumentException("'publishDelay' cannot be <= 0");
            }
        }

        if (connectionTimeout == null) {
            connectionTimeout = "30000"; // 30 seconds
        } else if (!isInteger(connectionTimeout)) {
            throw new IllegalArgumentException("'connectionTimeout' must be an integer");
        }

        if (socketTimeout == null) {
            socketTimeout = "30000"; // 30 seconds
        } else if (!isInteger(socketTimeout)) {
            throw new IllegalArgumentException("'socketTimeout' must be an integer");
        }
        
                this.wordPressServiceClient = new WordPresslServicesClient(hostname, endpoint, username, password, Integer.valueOf(socketTimeout), Integer.valueOf(connectionTimeout));

    }

    @Override
    public void execute(PluginContext ctx, Edition edition, OutletEditionAction action) {
        LOG.log(Level.INFO, "Executing WordPressEditionAction on Edition #{0}", edition.getId());
        init(action);
        this.errors = 0;
        WordPresslServicesClient wordPresslServicesClient = new WordPresslServicesClient();
        Map<String, String> post = null;
        XmlRpcClient client  = new XmlRpcClient();
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
        String result = "";

        try {
            //config.setServerURL(new URL("http://localhost:8282/wordpress/xmlrpc.php"));
            config.setServerURL(new URL("http://www.classic105.com/xmlrpc.php"));            
            client.setConfig(config);
            LOG.log(Level.INFO, "Number of items in Edition #{0}: {1}", new Object[]{edition.getId(), edition.getNumberOfPlacements()});

            for (NewsItemPlacement nip : edition.getPlacements()) {
            // processPlacement(ctx, nip);
                post = new HashMap<String, String>();
                post.put("mt_keywords", nip.getNewsItem().getTitle());
                post.put("categories", "cat1,cat2");
                post.put("post_content", "This is the trivial test Content");
                post.put("post_excerpt", "Test Excerpt");
                post.put("post_status", "publish");
                post.put("post_date", new Date().toString());
                post.put("comment_status", "open");
                post.put("ping_status", "open");
                post.put("title", "NEW BLOG , Blog! CONVERGE CONVERGE");
                post.put("link", "http://www.converge.org/");
                post.put("description", "This is the content of a trivial post.");
                //Object[] params = new Object[]{"1", "Converge", "@converge14!", post, Boolean.TRUE};
                Object[] params = new Object[]{"1", "admin", "root", post, Boolean.TRUE};
                result = wordPresslServicesClient.postEdition(params, client);

            }
            //0724667601==tomas okaris                                 //
            

        } catch (MalformedURLException ex) {
            Logger.getLogger(WordPressEditionAction.class.getName()).log(Level.SEVERE, null, ex);
        }

        LOG.log(Level.WARNING, "{0} errors encounted", new Object[]{this.errors});
        LOG.log(Level.INFO, "Finishing action. Edition #{0}", new Object[]{edition.getId()});
    }

    @Override
    public void executePlacement(PluginContext ctx, NewsItemPlacement placement, Edition edition, OutletEditionAction action) {
        LOG.log(Level.INFO, "Executing WordPressEditionAction for NewsItem #{0} in Edition #{1}", new Object[]{placement.getNewsItem().getId(), edition.getId()});
        init(action);
        this.errors = 0;
        //processPlacement(ctx, placement);
        LOG.log(Level.WARNING, "{0} errors encounted", new Object[]{this.errors});
        LOG.log(Level.INFO, "Finishing action. Edition #{0}", new Object[]{edition.getId()});        
        LOG.log(Level.INFO, "Executing WordPressEditionAction on Edition #{0}", edition.getId());
        this.errors = 0;
        //WordPresslServicesClient wordPresslServicesClient = new WordPresslServicesClient();
        Map<String, String> post = null;
        XmlRpcClient client  = new XmlRpcClient();
        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl(); 
        String result="";
          
        //
        Edition editionItem = placement.getEdition();
        NewsItem newsItem = placement.getNewsItem();
//        if (!newsItem.isEndState()) {
//            return;
//        }
            
            LOG.log(Level.INFO, "Number of items in Edition #{0}: {1}", new Object[]{edition.getId(), edition.getNumberOfPlacements()});         
                // processPlacement(ctx, nip);
                post = new HashMap<String, String>();
                post.put("mt_keywords", "hhhhhhhhhhhhhhhhhhhhhh");
                post.put("categories", "cat1,Cat2");
                post.put("post_content", "This is the trivial test Content");
                post.put("post_excerpt", "Test Excerpt");
                post.put("post_status", "publish");
                post.put("post_date", new Date().toString());
                post.put("comment_status", "open");
                post.put("ping_status", "open");
                post.put("title", "NEW BLOG , Blog! CONVERGE CONVERGE");
                post.put("link", "http://www.converge.org/");
                post.put("description", "This is the content of a trivial post.");
                
                Object[] params = new Object[]{"1", "admin", "root", post, Boolean.TRUE};
                result = wordPressServiceClient.postEdition(params, client);
                
                
//            ctx.updateNewsItemEditionState(status);
//            ctx.updateNewsItemEditionState(nid);
//            ctx.updateNewsItemEditionState(uri);
//            ctx.updateNewsItemEditionState(submitted);
                
       
    }
    
    
    
    
    
//    /**
//     * Process a single {@link NewsItemPlacement}. The processing includes
//     * creating or updating a corresponding node in Drupal.
//     *
//     * @param ctx {@link PluginContext}
//     * @param nip {@link NewsItemPlacement} to process
//     */
//    private void processPlacement(PluginContext ctx, NewsItemPlacement nip) {
//
//        Edition edition = nip.getEdition();
//
//        NewsItem newsItem = nip.getNewsItem();
//
//        // Ignore NewsItem if it hasn't reached the end state of the workflow
//        if (!newsItem.isEndState()) {
//            return;
//        }
//
//        // Ignore NewsItem if the section of the NewsItemPlacement is not mapped
////        try {
////            getSection(nip);
////        } catch (UnmappedSectionException usex) {
////            return;
////        }
//
//        boolean update = false;
////        try {
////            // determine if the news item is already uploaded
////            update = this.drupalServiceClient.exists("newsitem", nip.getNewsItem().getId());
////        } catch (DrupalServerConnectionException ex) {
////            LOG.log(Level.SEVERE, "Could not determine if NewsItem #{0} is already update. {1}", new Object[]{newsItem.getId(), ex.getMessage()});
////            LOG.log(Level.FINEST, null, ex);
////            errors++;
////            return;
////        }
//
//        //UrlEncodedFormEntity entity = toUrlEncodedFormEntity(nip, getPublishOn(edition));
//        List<FileInfo> mediaItems = getMediaItems(newsItem);
//
//        if (update) {
//            try {
//                //Long nodeId = wordPressServiceClient.retrieveNodeIdFromResource("newsitem", nip.getNewsItem().getId());
//                //LOG.log(Level.INFO, "Updating Node #{0} with NewsItem #{1} & {2} image(s)", new Object[]{nodeId, newsItem.getId(), mediaItems.size()});
//               // wordPressServiceClient.updateNode(nodeId, entity);
//                //wordPressServiceClient.attachFile(nodeId, "field_image", mediaItems);
//            } catch (Exception ex) {
//                this.errors++;
//                LOG.log(Level.SEVERE, ex.getMessage());
//                LOG.log(Level.FINEST, "", ex);
//            }
//        } else {
//            LOG.log(Level.INFO, "Creating new Node for NewsItem #{0} & {1} image(s)", new Object[]{newsItem.getId(), mediaItems.size()});
//
//            NewsItemEditionState status = ctx.addNewsItemEditionState(edition.getId(), newsItem.getId(), STATUS_LABEL, UPLOADING.toString());
//            NewsItemEditionState nid = ctx.addNewsItemEditionState(edition.getId(), newsItem.getId(), NID_LABEL, null);
//            NewsItemEditionState uri = ctx.addNewsItemEditionState(edition.getId(), newsItem.getId(), URI_LABEL, null);
//            NewsItemEditionState submitted = ctx.addNewsItemEditionState(edition.getId(), newsItem.getId(), DATE, null);
//
//            try {
////                NodeInfo newNode = drupalServiceClient.createNode(entity);
////                wordPressServiceClient.attachFile(newNode.getId(), "field_image", mediaItems);
//
//                nid.setValue(newNode.getId().toString());
//                uri.setValue(newNode.getUri().toString());
//                submitted.setValue(new Date().toString());
//                status.setValue(UPLOADED.toString());
//            } catch (WordPressServerConnectionException ex) {
//                this.errors++;
//                status.setValue(FAILED.toString());
//                LOG.log(Level.SEVERE, ex.getMessage());
//                LOG.log(Level.FINEST, "", ex);
//
//                ctx.updateNewsItemEditionState(status);
//                ctx.updateNewsItemEditionState(nid);
//                ctx.updateNewsItemEditionState(uri);
//                ctx.updateNewsItemEditionState(submitted);
//            }
//
//            ctx.updateNewsItemEditionState(status);
//            ctx.updateNewsItemEditionState(nid);
//            ctx.updateNewsItemEditionState(uri);
//            ctx.updateNewsItemEditionState(submitted);
//        }
//    }

    
    private List<FileInfo> getMediaItems(NewsItem newsItem) {
        List<FileInfo> mediaItems = new ArrayList<FileInfo>();

        for (NewsItemMediaAttachment nima : newsItem.getMediaAttachments()) {
            MediaItem mediaItem = nima.getMediaItem();

            // Verify that the item exist and any renditions are attached
            if (mediaItem == null || !mediaItem.isRenditionsAttached()) {
                continue;
            } else {
                try {
                    MediaItemRendition rendition = mediaItem.findRendition(this.renditionName);
                    String abbreviatedCaption = StringUtils.abbreviate(nima.getCaption(), MEDIA_ITEM_TITLE_LENGTH);
                    mediaItems.add(new FileInfo(new File(rendition.getFileLocation()), abbreviatedCaption));
                    LOG.log(Level.FINE, "Adding Rendition #{0} Located at: {1} with Caption: {2} Capped to: {3}", new Object[]{rendition.getId(), rendition.getFileLocation(), nima.getCaption(), abbreviatedCaption});
                } catch (RenditionNotFoundException ex) {
                    LOG.log(Level.INFO, "Rendition ''{0}'' missing for MediaItem #{1}. MediaItem #{1} will not be uploaded.", new Object[]{renditionName, mediaItem.getId()});
                    continue;
                }
            }
        }

        return mediaItems;
    }
    @Override
    public boolean isSupportEditionExecute() {
        return true;
    }

    @Override
    public boolean isSupportPlacementExecute() {
        return true;
    }

    @Override
    public Map<String, String> getAvailableProperties() {
        if (availableProperties == null) {
            availableProperties = new LinkedHashMap<String, String>();

            for (Property p : Property.values()) {
                availableProperties.put(bundle.getString(p.name()), p.name());
            }
        }

        return availableProperties;
    }

    @Override
    public String getName() {
        return bundle.getString("PLUGIN_NAME");
    }

    @Override
    public String getDescription() {
        return bundle.getString("PLUGIN_DESCRIPTION");
    }

    @Override
    public String getVendor() {
        return bundle.getString("PLUGIN_VENDOR");
    }

    @Override
    public Date getDate() {
        try {
            return sdf.parse(bundle.getString("PLUGIN_BUILD_TIME"));
        } catch (Exception e) {
            return new Date();
        }
    }

    @Override
    public ResourceBundle getBundle() {
        return bundle;
    }

    @Override
    public String getAbout() {
        return bundle.getString("PLUGIN_ABOUT");
    }

    /**
     * Decodes the section mappings and adding each mapping to
     * {@link #sectionMapping}.
     *
     * @param mapping mapping to set
     */
    private void setSectionMapping(String mapping) {
        String[] values = mapping.split(";");

        for (int i = 0; i < values.length; i++) {
            String[] value = values[i].split(":");
            Long convergeId = Long.valueOf(value[0].trim());
            Long drupalId = Long.valueOf(value[1].trim());
            sectionMapping.put(convergeId, drupalId);
            LOG.log(Level.INFO, "Mapping Converge Section #{0} to WordPress Section #{1}", new Object[]{convergeId, drupalId});
        }

        LOG.log(Level.INFO, "Found {0} Section mapping(s)", sectionMapping.size());
    }

    /**
     * Get Publish on text value.
     *
     * @return "YYYY-MM-DD HH:MM:SS" or ""
     */
    private String getPublishOn(Edition edition) {
        if (publishImmediately != null) {
            return null;
        }

        Calendar calendar = (Calendar) edition.getPublicationDate().clone();
        calendar.add(Calendar.HOUR_OF_DAY, Integer.valueOf(publishDelay));

        return sdf.format(calendar.getTime());
    }

    /**
     * Get Author text field.
     *
     * @param newsItem {@link NewsItem}
     * @return By-line to use for the story when published on WordPress
     */
    private String getAuthor(NewsItem newsItem) {
        if (newsItem.isUndisclosedAuthor()) {
            return "N/A";
        } else {
            if (StringUtils.isBlank(newsItem.getByLine())) {
                // No by-line specified in the news item. 
                // Generate by-line from actors on news item

                StringBuilder sb = new StringBuilder();

                // Iterate through actors specified on the news item
                boolean firstActor = true;
                for (NewsItemActor actor : newsItem.getActors()) {

                    // If the actor has the role from the initial state of the 
                    // workflow, he is the author of the story
                    if (actor.getRole().equals(newsItem.getOutlet().getWorkflow().getStartState().getActorRole())) {
                        if (!firstActor) {
                            sb.append(", ");
                        } else {
                            firstActor = false;
                        }

                        sb.append(actor.getUser().getFullName());
                    }
                }

                return sb.toString();
            } else {
                // Return the "by-line" of the NewsItem
                return newsItem.getByLine();
            }
        }
    }

    private boolean isInteger(String input) {
        try {
            Integer.valueOf(input);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
