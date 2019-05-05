package imece.betul.imece.model;

public class Ogretmen extends User {
    public static User ogretmen;
    private String ilce;
    private String okul;

    public Ogretmen( User ogretmen, String ilce, String okul) {
        this.ogretmen=ogretmen;
        this.ilce = ilce;
        this.okul = okul;
    }
public Ogretmen(){}

    public String getIlce() {
        return ilce;
    }

    public void setIlce(String ilce) {
        this.ilce = ilce;
    }

    public String getOkul() {
        return okul;
    }

    public void setOkul(String okul) {
        this.okul = okul;
    }
}
