package imece.betul.imece.model;

public class Donate {
    private String id;
    private String istenilen_urun;
    private String publisher;

    public Donate(String id, String istenilen_urun, String publisher) {
        this.id = id;
        this.istenilen_urun = istenilen_urun;
        this.publisher = publisher;
    }

    public Donate() {

    }

    public String getIstenilen_urun() {
        return istenilen_urun;
    }

    public void setIstenilen_urun(String istenilen_urun) {
        this.istenilen_urun = istenilen_urun;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
