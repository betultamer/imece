package imece.betul.imece.model;

public abstract class User {
    private String id;
    private String username;
    private String fullname;
    private String imageurl;
    private String bio;
    private String il;
    public Message message;

    public User(String id, String username, String fullname, String imageurl, String bio, String il) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.imageurl = imageurl;
        this.bio = bio;
        this.il = il;
    }

    public User() {
        message = new Message();
        message.idReceiver = "0";
        message.idSender = "0";
        message.text = "";
        message.timestamp = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getIl() {
        return il;
    }

    public void setIl(String il) {
        this.il = il;
    }


}
