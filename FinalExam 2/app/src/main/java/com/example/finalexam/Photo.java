package com.example.finalexam;

import android.graphics.drawable.Icon;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Photo implements Serializable {

    String created_at,description,username,small,thumb,url,profile_image,photo_id,docId;

    public Photo() {
    }



    public Photo(JSONObject json) throws JSONException {
    this.created_at = json.getString("created_at");
    this.description = json.getString("description");
    this.username = json.getJSONObject("user").getString("username");
    this.thumb = json.getJSONObject("urls").getString("thumb");
    this.profile_image = json.getJSONObject("user").getJSONObject("profile_image").getString("small");
    this.photo_id = json.getString("id");
    }

    public String getId() {
        return photo_id;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public void setId(String id) {
        this.photo_id = id;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSmall() {
        return small;
    }

    public void setSmall(String small) {
        this.small = small;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
