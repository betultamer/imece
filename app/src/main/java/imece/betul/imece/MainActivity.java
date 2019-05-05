package imece.betul.imece;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import imece.betul.imece.Commons.Common;
import imece.betul.imece.Fragments.BagisFragment;
import imece.betul.imece.Fragments.HomeFragment;
import imece.betul.imece.Fragments.ProfileFragment;


public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottom_navigation;
    Fragment selectedfragment = null;
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    switch (item.getItemId()) {

                        case R.id.nav_bagis:
                            selectedfragment = new HomeFragment();
                            break;
                        case R.id.nav_okul:
                            selectedfragment = new BagisFragment();
                            break;
                        case R.id.nav_profil:
                            if (Common.currentuser != null) {
                                SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                                editor.putString("profilefordonorid",Common.currentuser.getId());
                                editor.putString("profileforteacherid", "none");
                                editor.apply();
                                selectedfragment = new ProfileFragment();
                            }
                            else{
                                if (Common.currentogretmen!=null) {
                                    SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                                    editor.putString("profileforteacherid", Common.currentogretmen.getId());
                                    editor.putString("profilefordonorid","none");
                                    editor.apply();
                                    selectedfragment = new ProfileFragment();

                                }
                            }
                            break;

                    }
                    if (selectedfragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedfragment).commit();
                    }

                    return true;
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottom_navigation = findViewById(R.id.bottom_nav);
        bottom_navigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);


        Bundle intent = getIntent().getExtras();
        if (intent != null) {

            String publisher = intent.getString("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
       //     editor.putString("profileforteacherid", publisher);
            editor.apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ProfileFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }

    }
}
