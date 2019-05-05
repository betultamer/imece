package imece.betul.imece.spinnerr;

import java.util.List;

/**
 * Created by Abhi on 03 Jan 2018 003.
 */

public class Sehir {
    private String sehirAdi;
    private List<Ilce> ilceler;

    public Sehir(String sehirAdi, List<Ilce> ilceler) {
        this.sehirAdi = sehirAdi;
        this.ilceler = ilceler;
    }



    public String getSehirAdi() {
        return sehirAdi;
    }

    public List<Ilce> getIlceler() {
        return ilceler;
    }
}
