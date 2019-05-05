package imece.betul.imece.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import imece.betul.imece.Adapter.MyDonatesAdapter;
import imece.betul.imece.Adapter.MyFotosAdapter;
import imece.betul.imece.MainActivity;
import imece.betul.imece.OptionsActivity;
import imece.betul.imece.R;
import imece.betul.imece.model.Bagisveren;
import imece.betul.imece.model.Donate;
import imece.betul.imece.model.Ogretmen;
import imece.betul.imece.model.Post;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    ImageView image_profile, options,logo;
    TextView posts, fullname, bio;


    FirebaseUser firebaseUser;
    String profilefordonorid,profileforteacherid;
    String pid,asd;
    private RecyclerView recyclerView;
    private MyFotosAdapter myFotosAdapter;
    private List<Post> postList;

    private List<Donate> donateList;
    private MyDonatesAdapter myDonatesAdapter;

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        logo=view.findViewById(R.id.logo);


        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        profilefordonorid =prefs.getString("profilefordonorid", "none");
        profileforteacherid = prefs.getString("profileforteacherid", "none");
        image_profile = view.findViewById(R.id.image_profile);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        options = view.findViewById(R.id.options);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });

             if (!profilefordonorid.equals("none")){

                 postList = new ArrayList<>();
                 LinearLayoutManager mLayout = new GridLayoutManager(getContext(), 3);
                 recyclerView.setLayoutManager(mLayout);
                 myFotosAdapter = new MyFotosAdapter(getContext(), postList);
                 recyclerView.setAdapter(myFotosAdapter);
                 userInfo(profilefordonorid);
                 myFotos(profilefordonorid);
             }

        if (!profileforteacherid.equals("none")) {
            donateList = new ArrayList<>();
            LinearLayoutManager mLayoutMana = new GridLayoutManager(getContext(), 1);
            recyclerView.setLayoutManager(mLayoutMana);
            myDonatesAdapter = new MyDonatesAdapter(getContext(), donateList);
            recyclerView.setAdapter(myDonatesAdapter);
            ogretmenInfo(profileforteacherid);
            getNrPosts(profileforteacherid);

        }
    if (profileforteacherid.equals("none") && profilefordonorid.equals("none")){
        return null;

    }

        recyclerView.setVisibility(View.VISIBLE);





        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), OptionsActivity.class));
            }
        });


        return view;
    }


    private void userInfo(String id) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Bagisveren").child(id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if (getContext() == null) {
                        return;
                    }
                    Bagisveren user = dataSnapshot.getValue(Bagisveren.class);

                    Glide.with(getContext()).load(user.getImageurl()).into(image_profile);

                    fullname.setText(user.getFullname());
                    bio.setText(user.getBio());


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }

    private void ogretmenInfo(String oid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("ogretmen").child(oid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if (getContext() == null) {
                        return;
                    }
                    Ogretmen user = dataSnapshot.getValue(Ogretmen.class);

                    Glide.with(getContext()).load(user.getImageurl()).into(image_profile);

                    fullname.setText(user.getFullname());
                    bio.setText(user.getBio());
                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }


    private void getNrPosts(final String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Donates");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    donateList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Donate donate = snapshot.getValue(Donate.class);

                    if (donate.getPublisher().equals(postid)) {
                        donateList.add(donate);
                    }
                }
                Collections.reverse(donateList);
                myDonatesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void myFotos(final String fotoid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(fotoid)) {
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myFotosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
