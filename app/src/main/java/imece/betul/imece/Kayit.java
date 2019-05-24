package imece.betul.imece;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import imece.betul.imece.Commons.Common;
import imece.betul.imece.model.Bagisveren;
import imece.betul.imece.model.User;

public class Kayit extends AppCompatActivity {



    FirebaseAuth auth;
    EditText username, fullname, email, password;
    Button register;
    TextView txt_login;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(Kayit.this);

        setContentView(R.layout.activity_kayit);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.cityArrayList, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        fullname = findViewById(R.id.adsoyad);
        password = findViewById(R.id.parola);
        register = findViewById(R.id.buttonkayit);
        txt_login = findViewById(R.id.aciklama);

        auth = FirebaseAuth.getInstance();
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Kayit.this, MainActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd = new ProgressDialog(Kayit.this);
                pd.setMessage("Please wait...");
                pd.show();
                String  il = spinner.getSelectedItem().toString();
                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_username)  || TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(Kayit.this, "Tüm alanlar zorunlu!", Toast.LENGTH_SHORT).show();
                } else if(str_password.length() < 6){
                    Toast.makeText(Kayit.this, "parola 6 karakterden fazla olmalı", Toast.LENGTH_SHORT).show();
                }else {
                    register(str_username, str_fullname, il , str_email, str_password);
                }
            }
        });
    }

    public void register(final String username, final String fullname, final String il,String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Kayit.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            final String userID = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child("Bagisveren").child(userID);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", userID);
                            map.put("username", username.toLowerCase());
                            map.put("fullname", fullname);
                            map.put("imageurl","https://firebasestorage.googleapis.com/v0/b/imece-a4c52.appspot.com/o/placeholder-image-png-4.png?alt=media&token=2c65d4e9-b04d-4333-984c-ea30e81af4bb");
                            map.put("il", il);
                            map.put("bio", "");
                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        pd.dismiss();
                                        Intent intent = new Intent(Kayit.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Users").child("Bagisveren").child(userID);
                                        reference1.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Bagisveren user = dataSnapshot.getValue(Bagisveren.class);
                                                if(dataSnapshot.exists()) {
                                                    Intent intent = new Intent(Kayit.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    Common.currentuser = user;
                                                    startActivity(intent);
                                                    finish();
                                                }
                                                }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });



                                    }
                                }
                            });
                        } else {
                            pd.dismiss();
                            Toast.makeText(Kayit.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}







