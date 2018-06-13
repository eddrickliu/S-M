package com.example.eddrickliu.sm;

public class Posts {
    public String uid, time, date, postpic, description, profilepic, fullname;

    public Posts(){
        this.uid = "";
        this.time = "";
        this.date = "";
        this.postpic = "";
        this.description = "";
        this.profilepic = "";
        this.fullname = "";
    }

    public Posts(String uid, String time, String date, String postpic, String description, String profilepic, String fullname) {
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.postpic = postpic;
        this.description = description;
        this.profilepic = profilepic;
        this.fullname = fullname;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostpic() {
        return postpic;
    }

    public void setPostpic(String postpic) {
        this.postpic = postpic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }
}
