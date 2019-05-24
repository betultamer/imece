package imece.betul.imece.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Post {
    private String postid;
    private String description;
    private String publisher;
    private String il;
    Map<String, Object> postimage;
    public Post(String postid, Map<String, Object> postimage, String description, String publisher, String il) {
        this.postid = postid;
        this.postimage = postimage;
        this.description = description;
        this.publisher = publisher;
        this.il = il;
    }

    public Post(){}
    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIl() {
        return il;
    }

    public void setIl(String il) {
        this.il = il;
    }
    public void setPostimage(Map<String, Object> postimage)
    {
        this.postimage = postimage;
    }
    public Map<String, Object> getPostimage()
    {
        return this.postimage;
}}
