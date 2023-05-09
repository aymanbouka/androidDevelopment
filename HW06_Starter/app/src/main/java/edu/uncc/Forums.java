package edu.uncc;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;

public class Forums implements Serializable {
    /*
     data.put("title", title);
                    data.put("description", desc);
                    data.put("ownerId", mAuth.getCurrentUser().getUid());
                    data.put("ownerName", mAuth.getCurrentUser().getDisplayName());
                    data.put("time", FieldValue.serverTimestamp());
                    data.put("docId", documentReference.getId());
     */

    public String title, description,ownerId,ownerName,docId;

    public ArrayList<String> likes;
    public Timestamp time;

    public Forums() {
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public Forums(String title, String description, String ownerId, String ownerName, String docId, Timestamp time) {
        this.title = title;
        this.description = description;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.docId = docId;
        this.time = time;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Forums{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", time='" + time + '\'' +
                ", docId='" + docId + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }



    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
