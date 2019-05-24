package imece.betul.imece.spinnerr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import imece.betul.imece.Commons.Common;
import imece.betul.imece.Kayit;
import imece.betul.imece.MainActivity;
import imece.betul.imece.R;
import imece.betul.imece.model.Ogretmen;
import imece.betul.imece.model.User;

public class ogretmen_kayit extends AppCompatActivity {
    private static final String KEY_SEHIR = "ILADI";
    FirebaseAuth auth;
    EditText username, fullname, email, password;
    Button register;
    TextView txt_login;
    DatabaseReference reference;
    ProgressDialog pd;
    String il,ilce,okul;
    Spinner sehirSpinner;
    Spinner ilceSpinner;
    Spinner okulSpinner;
    private ProgressDialog pDialog;
    String cities_url = "https://raw.githubusercontent.com/betultamer/imece/master/deneme.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ogretmen_kayit);
        sehirSpinner = findViewById(R.id.stateSpinner);
        ilceSpinner = findViewById(R.id.citiesSpinner);
        okulSpinner =findViewById(R.id.okulSpinner);
        displayLoader();
        loadStateCityDetails();




        FirebaseApp.initializeApp(ogretmen_kayit.this);



        username = findViewById(R.id.tc);
        email = findViewById(R.id.email);
        fullname = findViewById(R.id.fullname);
        password = findViewById(R.id.password);
        register = findViewById(R.id.kayit);
        txt_login = findViewById(R.id.aciklama);

        auth = FirebaseAuth.getInstance();
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ogretmen_kayit.this, MainActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sehir state = (Sehir) sehirSpinner.getSelectedItem();
                Ilce city = (Ilce) ilceSpinner.getSelectedItem();
                String okul = okulSpinner.getSelectedItem().toString();
                il=state.getSehirAdi();
                ilce=city.getCityName();


                pd = new ProgressDialog(ogretmen_kayit.this);
                pd.setMessage("Lütfen bekleyiniz...");
                pd.show();
                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){
                    Toast.makeText(ogretmen_kayit.this, "Lütfen tüm alanları doldurunuz!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }   else if(str_password.length() < 6){
                    Toast.makeText(ogretmen_kayit.this, "Parola 6 karakterden fazla olmalıdır!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                } else {
                    register(str_username, str_fullname,ilce, il,okul, str_email, str_password);
                }
            }
        });
    }

    public void register(final String username, final String fullname,final String city, final String  state , final String okul, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(ogretmen_kayit.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            final String userID = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child("ogretmen").child(userID);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("id", userID);
                            map.put("username", username);
                            map.put("fullname", fullname);
                            map.put("il", state);
                            map.put("ilce", city);
                            map.put("okul", okul);
                            map.put("imageurl","https://firebasestorage.googleapis.com/v0/b/imece-a4c52.appspot.com/o/placeholder-image-png-4.png?alt=media&token=2c65d4e9-b04d-4333-984c-ea30e81af4bb");


                            map.put("bio", "");
                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        pd.dismiss();
                                        Intent intent = new Intent(ogretmen_kayit.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Users").child("ogretmen").child(userID);
                                        reference1.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Ogretmen user = dataSnapshot.getValue(Ogretmen.class);
                                                if(dataSnapshot.exists()) {
                                                    Intent intent = new Intent(ogretmen_kayit.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    Common.currentogretmen = user;
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
                            Toast.makeText(ogretmen_kayit.this, "Bu email ve parola ile üye olamazsınız", Toast.LENGTH_SHORT).show();
                        }
                    }
                });






    }

    private void displayLoader() {
        pDialog = new ProgressDialog(ogretmen_kayit.this);
        pDialog.setMessage("Loading Data.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    /**
     * Helps in downloading the state and city details
     * and populating the spinner
     */
    private void loadStateCityDetails() {
        final List<Sehir> sehirList = new ArrayList<>();



        JsonArrayRequest jsArrayRequest = new JsonArrayRequest
                (Request.Method.GET, cities_url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray responseArray) {
                        pDialog.dismiss();
                        try {
                            //Parse the JSON response array by iterating over it

                            for (int i = 1; i < responseArray.length(); i++) {
                                final   List<Ilce> ilceList =new ArrayList<>();
                                JSONObject response = responseArray.getJSONObject(i);
                                String sehir = response.getString(KEY_SEHIR);
                                JSONArray c = response.getJSONArray("ILCELERI");
                                for(int k=0; k<c.length(); k++) {
                                    JSONObject obj = c.getJSONObject(k);
                                    String ilceler= obj.getString("ilceadı");

                                    JSONArray okul = obj.getJSONArray("okullari");

                                    List<String> okulList = new ArrayList<>();

                                    for (int j = 0; j < okul.length(); j++) {
                                        okulList.add(okul.getString(j));
                                    }
                                    ilceList.add(new Ilce(ilceler,okulList)); }
                                sehirList.add(new Sehir(sehir, ilceList));



                            }
                            final SehirAdapter sehirAdapter = new SehirAdapter(ogretmen_kayit.this,
                                    R.layout.state_list, R.id.spinnerText, sehirList);
                            sehirSpinner.setAdapter(sehirAdapter);

                            sehirSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    //Populate City list to the second spinner when
                                    // a state is chosen from the first spinner
                                    Sehir cityDetails = sehirAdapter.getItem(position);
                                    List<Ilce> ilceListe = cityDetails.getIlceler();

                                    final CityAdapter ilceAdapter = new CityAdapter(ogretmen_kayit.this,
                                            R.layout.city_list, R.id.citySpinnerText, ilceListe);
                                    ilceSpinner.setAdapter(ilceAdapter);

                                    ilceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                                        public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
                                            Ilce okulDetails= ilceAdapter.getItem(p);
                                            List<String> okulList = okulDetails.getOkul();
                                            ArrayAdapter okulAdapter = new ArrayAdapter(ogretmen_kayit.this,
                                                    R.layout.city_list, R.id.citySpinnerText, okulList);
                                            okulSpinner.setAdapter(okulAdapter);
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });

                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();

                        //Display error message whenever an error occurs
                        Toast.makeText(getApplicationContext(),
                                error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }
}