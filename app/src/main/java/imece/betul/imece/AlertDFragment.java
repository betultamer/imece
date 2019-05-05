package imece.betul.imece;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import imece.betul.imece.spinnerr.ogretmen_kayit;

public class AlertDFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())

                .setTitle("Kayıt ol")
                // Set Dialog Message
                .setMessage("Kullanıcı Türü Belirleyiniz")

                // Positive button
                .setPositiveButton("Öğretmen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent kayit = new Intent(getActivity(), ogretmen_kayit.class);
                        startActivity(kayit);
                    }
                })

                // Negative Button
                .setNegativeButton("Bağışveren", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent kayit = new Intent(getActivity(), Kayit.class);
                        startActivity(kayit);
                    }
                }).create();
    }
}