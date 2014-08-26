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
import java.io.File;
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
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author fred
 */
@OutletAction
public class WordPressEditionAction implements EditionAction {

    public static final int MEDIA_ITEM_TITLE_LENGTH = 1024;
    private String website;
    private String hostname;
    private String endpoint;
    private String username;
    private String password;
    private String connectionTimeout;
    private String socketTimeout;
    private int errors = 0;
    private WordPresslServicesClient wordPressServiceClient;

    private enum Property {

        SITE_URL,
        CONNECTION_TIMEOUT,
        IMAGE_RENDITION,
        PASSWORD,
        PUBLISH_DELAY,
        PUBLISH_IMMEDIATELY,
        PUBLISHED,
        POSTID,
        POST_STATUS,
        ALLOW_COMMENTS,
        BLOG_ID,
        POST_TYPE,
        KEYWORDS,
        CATEGORIES,
        CUSTOME_FIELDS,
        SOCKET_TIMEOUT,
        USERNAME,
        TAG
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
    private String keywords;

    private void init(OutletEditionAction action) {

        Map<String, String> properties = action.getPropertiesAsMap();
        StringBuilder mapBuilder = new StringBuilder();
        mappings = mapBuilder.toString();
        publishDelay = properties.get(Property.PUBLISH_DELAY.name());
        publishImmediately = properties.get(Property.PUBLISH_IMMEDIATELY.name());
        renditionName = properties.get(Property.IMAGE_RENDITION.name());
        published = properties.get(Property.PUBLISHED.name());
        postId = properties.get(Property.POSTID.name());
        post_status = properties.get(Property.POST_STATUS.name());
        blog_id = properties.get(Property.BLOG_ID.name());
        category = properties.get(Property.CATEGORIES.name());
        tag = properties.get(Property.TAG.name());
        keywords = properties.get(Property.KEYWORDS.name());
        this.username = properties.get(Property.USERNAME.name());
        this.password = properties.get(Property.PASSWORD.name());
        this.connectionTimeout = properties.get(Property.CONNECTION_TIMEOUT.name());
        this.socketTimeout = properties.get(Property.SOCKET_TIMEOUT.name());
        this.website = properties.get(Property.SITE_URL.name());


        if (tag == null) {
            tag = "";
        }
        if (category == null) {
            category = "";
        }
        if (postId == null) {
            postId = "1";
        }
        if (blog_id == null) {
            blog_id = "1";
        }
        if (keywords == null) {
            keywords = "";
        }
        if (username == null) {
            throw new IllegalArgumentException("'username' cannot be null");
        } else if (password == null) {
            throw new IllegalArgumentException("'password' cannot be null");
        }
        if (website == null) {
            throw new IllegalArgumentException("'website address' cannot be null");
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

//        this.wordPressServiceClient = new WordPresslServicesClient(hostname, endpoint, username, password, Integer.valueOf(socketTimeout), Integer.valueOf(connectionTimeout));
        this.wordPressServiceClient = new WordPresslServicesClient(website, username, password, connectionTimeout);

    }

    @Override
    public void execute(PluginContext ctx, Edition edition, OutletEditionAction action) {
        LOG.log(Level.INFO, "Executing WordPressEditionAction on Edition #{0}", edition.getId());
        init(action);
        this.errors = 0;
        LOG.log(Level.INFO, "Number of items in Edition #{0}: {1}", new Object[]{edition.getId(), edition.getNumberOfPlacements()});
        for (NewsItemPlacement nip : edition.getPlacements()) {
            LOG.log(Level.INFO, "Executing the News Plasement");
            processPlacement(ctx, nip);
        }
        LOG.log(Level.WARNING, "{0} errors encounted", new Object[]{this.errors});
        LOG.log(Level.INFO, "Finishing action. Edition #{0}", new Object[]{edition.getId()});
    }

    @Override
    public void executePlacement(PluginContext ctx, NewsItemPlacement placement, Edition edition, OutletEditionAction action) {
        LOG.log(Level.INFO, "Executing WordPressEditionAction for NewsItem #{0} in Edition #{1}", new Object[]{placement.getNewsItem().getId(), edition.getId()});
        init(action);
        this.errors = 0;
        processPlacement(ctx, placement);
        LOG.log(Level.WARNING, "{0} errors encounted", new Object[]{this.errors});
        LOG.log(Level.INFO, "Finishing action. Edition #{0}", new Object[]{edition.getId()});
        LOG.log(Level.INFO, "Executing WordPressEditionAction on Edition #{0}", edition.getId());
        LOG.log(Level.INFO, "Number of items in Edition #{0}: {1}", new Object[]{edition.getId(), edition.getNumberOfPlacements()});
    }

    private void processPlacement(PluginContext ctx, NewsItemPlacement nip) {
        HashMap post;
        Edition edition = nip.getEdition();
        NewsItem newsItem = nip.getNewsItem();
        // Ignore NewsItem if it hasn't reached the end state of the workflow
        if (!newsItem.isEndState()) {
            return;
        }
        boolean update = false;
//        try {
//            // determine if the news item is already uploaded    //Uncomment Me after tests
//            if (this.wordPressServiceClient.exists(Integer.parseInt(blog_id)) == true) {
//                update = this.wordPressServiceClient.exists(Integer.parseInt(blog_id));
//            }
//            // update = this.wordPressServiceClient.exists(Integer.parseInt(blog_id));
//
//        } catch (WordPressServerConnectionException ex) {
//            LOG.log(Level.SEVERE, "Could not determine if NewsItem #{0} is already update. {1}", new Object[]{newsItem.getId(), ex.getMessage()});
//            LOG.log(Level.FINEST, null, ex);
//            errors++;
//            return;
//        }
        List<FileInfo> mediaItems = getMediaItems(newsItem);

        if (update == false) { //tessting to not considering the update to fix l there has to be a acheck so that the item is not repulbished this pice should be removed
            try {
                post = new HashMap<String, String>(); //Replace all Below with Converge ones
                post.put("mt_keywords", keywords);
                post.put("categories", category);
                post.put("post_content", newsItem.getStory());
                post.put("post_excerpt", newsItem.getBrief());
                post.put("post_status", "publish");
                post.put("post_date", new Date().toString());
                post.put("comment_status", "open");
                post.put("ping_status", "open");
                post.put("title", newsItem.getTitle());
                post.put("link", "http://www.dst.org/");
                post.put("description", newsItem.getSlugline());
                //  Object[] params = new Object[]{this.username, this.password, post, Boolean.TRUE};
                Object[] params = new Object[]{blog_id, this.username, this.password, post, Boolean.TRUE};
                // this.wordPressServiceClient.updateExistingPost(Integer.parseInt(blog_id), params);
                //this.wordPressServiceClient.createNewPost(params);
                if (mediaItems.isEmpty()) {
                    this.wordPressServiceClient.createNewPost(params);
                } else {
                    if (mediaItems.size() == 1) {
                        Map<String, String> paramz = wordPressServiceClient.attachFileToPost(mediaItems.get(0), Integer.parseInt(blog_id));
                        post.put("wp_post_thumbnail", paramz.get("id"));
                        this.wordPressServiceClient.createNewPost(params);
                    } else if (mediaItems.size() > 1) {
                        for (int i = 0; i < mediaItems.size(); i++) {
                            Map<String, String> paramz = wordPressServiceClient.attachFileToPost(mediaItems.get(0), Integer.parseInt(blog_id));
                            post.put("wp_post_thumbnail", paramz.get("id"));
                            this.wordPressServiceClient.createNewPost(params);
                        }

                    }

                }

            } catch (Exception ex) {
                this.errors++;
                LOG.log(Level.SEVERE, ex.getMessage());
                LOG.log(Level.FINEST, "", ex);
            }
        } else {
            LOG.log(Level.INFO, "Creating new Node for NewsItem #{0} & {1} image(s)", new Object[]{newsItem.getId(), mediaItems.size()});
            NewsItemEditionState status = ctx.addNewsItemEditionState(edition.getId(), newsItem.getId(), STATUS_LABEL, UPLOADING.toString());
            NewsItemEditionState nid = ctx.addNewsItemEditionState(edition.getId(), newsItem.getId(), NID_LABEL, null);
            NewsItemEditionState uri = ctx.addNewsItemEditionState(edition.getId(), newsItem.getId(), URI_LABEL, null);
            NewsItemEditionState submitted = ctx.addNewsItemEditionState(edition.getId(), newsItem.getId(), DATE, null);

            try {
                post = new HashMap<String, String>();
                post.put("mt_keywords", keywords);
                post.put("categories", category);
                post.put("post_content", newsItem.getStory());
                post.put("post_excerpt", newsItem.getBrief());
                post.put("post_status", "publish");
                post.put("post_date", new Date().toString());
                post.put("comment_status", "open");
                post.put("ping_status", "open");
                post.put("title", newsItem.getTitle());
                post.put("link", "http://www.dst.org/");
                post.put("description", newsItem.getSlugline());
                Object[] params = new Object[]{blog_id, this.username, this.password, post, Boolean.TRUE}; //to instantiate using the other Constructor
                this.wordPressServiceClient.createNewPost(params);
                if (mediaItems.size() > 0) {
                    this.wordPressServiceClient.attachFiles(Integer.parseInt(blog_id), mediaItems);
                } else if (mediaItems.size() == 1) {
                    this.wordPressServiceClient.attachFileToPost(mediaItems.get(0), Integer.parseInt(blog_id));
                } else {
                }

            } catch (WordPressServerConnectionException ex) {
                this.errors++;
                status.setValue(FAILED.toString());
                LOG.log(Level.SEVERE, ex.getMessage());
                LOG.log(Level.FINEST, "", ex);

                ctx.updateNewsItemEditionState(status);
                ctx.updateNewsItemEditionState(nid);
                ctx.updateNewsItemEditionState(uri);
                ctx.updateNewsItemEditionState(submitted);
            }

            ctx.updateNewsItemEditionState(status);
            ctx.updateNewsItemEditionState(nid);
            ctx.updateNewsItemEditionState(uri);
            ctx.updateNewsItemEditionState(submitted);
        }
    }

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