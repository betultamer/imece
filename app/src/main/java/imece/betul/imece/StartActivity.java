package imece.betul.imece;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import imece.betul.imece.Commons.Common;
import imece.betul.imece.model.Bagisveren;
import imece.betul.imece.model.Ogretmen;


public class StartActivity extends FragmentActivity {

    FragmentManager fm = getSupportFragmentManager();
    EditText email, password;
    Button login;
    TextView txt_signup, txt_output;

    FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from activity_main.xml
        setContentView(R.layout.activity_start);

        email = findViewById(R.id.username);
        password = findViewById(R.id.pwd);
        login = findViewById(R.id.login);
        txt_output = findViewById(R.id.output);
        auth = FirebaseAuth.getInstance();
        Common.currentogretmen=null;
        Common.currentuser=null;

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog pd = new ProgressDialog(StartActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                    txt_output.setText("All fields are required!");
                    pd.dismiss();
                } else {

                    auth.signInWithEmailAndPassword(str_email, str_password)
                            .addOnCompleteListener(StartActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Users").child("ogretmen")
                                                .child(auth.getCurrentUser().getUid());
                                        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("Users").child("Bagisveren")
                                                .child(auth.getCurrentUser().getUid());

                                        reference1.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                pd.dismiss();

                                                Ogretmen user = dataSnapshot.getValue(Ogretmen.class);

                                                if (dataSnapshot.exists()) {

                                                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    Common.currentogretmen = user;
                                                    startActivity(intent);
                                                    finish();

                                                }

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                pd.dismiss();
                                            }
                                        });


                                        reference2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                pd.dismiss();
                                                Bagisveren user = dataSnapshot.getValue(Bagisveren.class);
                                                if (dataSnapshot.exists()) {

                                                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    Common.currentuser = user;
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                pd.dismiss();
                                            }
                                        });
                                    } else {
                                        pd.dismiss();
                                        txt_output.setText("Email veya şifre yanlış.Lütfen tekrar dene");
                                    }
                                }
                            });
                }
            }
        });


        Button alertdfragbutton = (Button) findViewById(R.id.kayitbutton);


        // Capture button clicks
        alertdfragbutton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                AlertDFragment alertdFragment = new AlertDFragment();
                // Show Alert DialogFragment
                alertdFragment.show(fm, "Kayıt");
            }
        });
    }
}



