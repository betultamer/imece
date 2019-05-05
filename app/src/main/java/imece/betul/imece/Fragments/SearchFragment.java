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
import imece.betul.imece.model.Post;
import imece.betul.imece.model.User;
import imece.betul.imece.R;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private RecyclerView recyclerViewPost;
    private RecyclerView recyclerViewil;

    private RecyclerView recyclerView;
    private PostAdapter postAdapter,post2Adapter;
    private List<Post> postList;
    private List<Post> post2List;
    private DonateAdapter donateAdapter;
    private List<Donate> donateList;
    private UserAdapter userAdapter;
    private List<Bagisveren> userList;

    EditText search_bar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerViewil=view.findViewById(R.id.recycler_view_il);
        recyclerViewil.setHasFixedSize(true);
        LinearLayoutManager mmLayoutManager = new LinearLayoutManager(getContext());
        mmLayoutManager.setReverseLayout(true);
        mmLayoutManager.setStackFromEnd(true);
        recyclerViewil.setLayoutManager(mmLayoutManager);
        post2List = new ArrayList<>();
        post2Adapter = new PostAdapter(getContext(), post2List);
        recyclerViewil.setAdapter(post2Adapter);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerViewPost = view.findViewById(R.id.recycler_view_post);
        recyclerViewPost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerViewPost.setLayoutManager(linearLayoutManager);

        search_bar = view.findViewById(R.id.search_bar);

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);
        donateList = new ArrayList<>();
        donateAdapter = new DonateAdapter(getContext(), donateList);
        recyclerViewPost.setAdapter(donateAdapter);
       // readUsers();
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                postList.clear();
                donateList.clear();
                searchUsers(charSequence.toString());
               searchPost(charSequence.toString());
               searchIl(charSequence.toString());

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

    private void searchIl(String s) {

        Query query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("il")
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
}}
