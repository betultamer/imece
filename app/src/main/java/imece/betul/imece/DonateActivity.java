package imece.betul.imece;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import imece.betul.imece.Commons.Common;

public class DonateActivity extends AppCompatActivity {
    ImageView close;
    TextView post;
    EditText ihtiyac;
    private Button buttonView;
    private LinearLayout parentLayout;
    private int hint = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        ihtiyac = findViewById(R.id.ihtiyac);
        close = findViewById(R.id.close);
        post = findViewById(R.id.share);
        //    description = findViewById(R.id.description);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DonateActivity.this, MainActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                uploadImage_10();
            }
        });


    }


    private void uploadImage_10() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Paylaşılıyor");
        pd.show();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Donates");
        String donateid = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();

        if (ihtiyac.toString().length() > 0) {

            hashMap.put("id", donateid);
            hashMap.put("istenilen_urun", ihtiyac.getText().toString());
            hashMap.put("publisher", Common.currentogretmen.getId());

            reference.child(donateid).setValue(hashMap);

            pd.dismiss();

            startActivity(new Intent(DonateActivity.this, MainActivity.class));
            finish();

        } else {
            Toast.makeText(DonateActivity.this, "İhtiyaç Listesi boş geçilemez", Toast.LENGTH_SHORT).show();
        }
    }

}
