/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.i2m.converge.plugins.actions.wordpress;

/**
 *
 * @author fred
 */
public class PostInfo {

    private String apiKey;
    private String userName;
    private String password;
    private String blogId;
    private String blogTitle;
    private String description;
    

    public PostInfo() {
    }

    public PostInfo(String userName, String password, String blogId) {
        this.userName = userName;
        this.password = password;
        this.blogId = blogId;
    }

    public PostInfo(String apiKey, String userName, String password, String blogId) {
        this.apiKey = apiKey;
        this.userName = userName;
        this.password = password;
        this.blogId = blogId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getBlogId() {
        return blogId;
    }
    
    public String getBlogDescription() {
        return description;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }
    
    public String getBlogTitle(){
      return blogTitle;
    }

    /**
     * @param apiKey the apiKey to set
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param blogId the blogId to set
     */
    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }
    
    public void setBlogTitle(String title){
        this.blogTitle=title;
    }
    
    public void setBlogDescription(String description){
        this.description=description;
    }
}
