package imece.betul.imece.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import imece.betul.imece.Adapter.DonateAdapter;
import imece.betul.imece.Adapter.PostAdapter;
import imece.betul.imece.Adapter.UserAdapter;
import imece.betul.imece.model.Bagisveren;
import imece.betul.imece.model.Donate;
import imece.betul.imece.model.Ogretmen;
import imece.betul.imece.model.Post;
import imece.betul.imece.model.User;
import imece.betul.imece.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerViewPost,recyclerViewDonateIl;
    private RecyclerView recyclerViewil;

    private RecyclerView recyclerView;
    private PostAdapter postAdapter,post2Adapter;
    private List<Post> postList;
    private List<Post> post2List;
    private DonateAdapter donateAdapter,donate2Adapter;
    private List<Donate> donateList,donate2List;

    EditText search_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerViewil=view.findViewById(R.id.recycler_view_il);
        search_bar = view.findViewById(R.id.search_bar);

        recyclerViewil.setHasFixedSize(true);
        recyclerViewil.setLayoutManager( new LinearLayoutManager(getContext()));
        post2List = new ArrayList<>();
        post2Adapter = new PostAdapter(getContext(), post2List);
        recyclerViewil.setAdapter(post2Adapter);


        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);


        recyclerViewPost = view.findViewById(R.id.recycler_view_post);
        recyclerViewPost.setHasFixedSize(true);
        recyclerViewPost.setLayoutManager(new LinearLayoutManager(getContext()));
        donateList = new ArrayList<>();
        donateAdapter = new DonateAdapter(getContext(), donateList);
        recyclerViewPost.setAdapter(donateAdapter);

        recyclerViewDonateIl = view.findViewById(R.id.recycler_view_il2);
        recyclerViewDonateIl.setHasFixedSize(true);
        recyclerViewDonateIl.setLayoutManager(new LinearLayoutManager(getContext()));
        donate2List = new ArrayList<>();
        donate2Adapter = new DonateAdapter(getContext(), donate2List);
        recyclerViewDonateIl.setAdapter(donate2Adapter);

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                    searchUsers(charSequence.toString());
                    searchPost(charSequence.toString());
                    searchIl(charSequence.toString());
                    searchIhtıyacIl(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });

        return view;
    }

    private void searchPost(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("description")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post= snapshot.getValue(Post.class);
                    postList.add(post);

                }

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchUsers(String s){
        Query query = FirebaseDatabase.getInstance().getReference("Donates").orderByChild("istenilen_urun")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                donateList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Donate donate= snapshot.getValue(Donate.class);
                    donateList.add(donate);
                }

                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    public void searchIl(String s) {

        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("il")
                .startAt(s)
                .endAt(s+"\uf8ff");


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                post2List.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Post post2= snapshot.getValue(Post.class);
                    post2List.add(post2);
                }
                post2Adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
}
    public void searchIhtıyacIl(String s) {

        Query query = FirebaseDatabase.getInstance().getReference("Users/"+ "ogretmen").orderByChild("il")
                .startAt(s.toUpperCase())
                .endAt(s.toUpperCase()+"\uf8ff");


        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                  Ogretmen ogretmen= snapshot.getValue(Ogretmen.class);
                  getNrPosts(ogretmen.getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getNrPosts( final String postid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Donates");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                donate2List.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Donate donate = snapshot.getValue(Donate.class);
                    if (donate.getPublisher().equals(postid)) {
                        donate2List.add(donate);
                    }
                }
                donate2Adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}