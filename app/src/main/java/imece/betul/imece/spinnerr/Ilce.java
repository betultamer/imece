package imece.betul.imece.spinnerr;

import java.util.List;

public class Ilce {
    private String ilceadi;
    private List<String> okul;

    public Ilce(String ilceadi , List<String> okul) {
        this.ilceadi = ilceadi;
        this.okul = okul;
    }

    
    public String getCityName() {
        return ilceadi;
    }


    public List<String> getOkul(){
        return okul;
    }

}
