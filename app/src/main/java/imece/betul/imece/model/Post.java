package imece.betul.imece.model;

public class Post {
    private String postid;
    private String postimage;
    private String description;
    private String publisher;
    private String il;

    public Post(String postid, String postimage, String description, String publisher, String il) {
        this.postid = postid;
        this.postimage = postimage;
        this.description = description;

        this.publisher = publisher;
        this.il = il;
    }

    public Post() {
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
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
}
