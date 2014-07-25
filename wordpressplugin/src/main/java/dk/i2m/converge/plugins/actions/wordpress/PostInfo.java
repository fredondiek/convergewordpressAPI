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
    
    @SerializedName("pid")
    private Long id;
    @SerializedName("uri")
    private String uri;

    public PostInfo() {
        this(0L, "");
    }

    public PostInfo(Long id, String uri) {
        this.id = id;
        this.uri = uri;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    
}
