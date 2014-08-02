/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.i2m.converge.plugins.actions.wordpress;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author fred
 */
public class PostInfo {

    private String apiKey;
    private String userName;
    private String password;
    private String blogId;

    public PostInfo(String apiKey, String userName,
            String password, String blogId) {
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

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }
}
