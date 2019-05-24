package imece.betul.imece.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import imece.betul.imece.Adapter.PostAdapter;
import imece.betul.imece.R;
import imece.betul.imece.model.Post;

import static android.content.Context.MODE_PRIVATE;

public class IlFragment extends Fragment {

    private RecyclerView recyclerViewil;

    private PostAdapter post2Adapter;
     private List<Post> post2List;
    String il;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_il, container, false);
        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        il =prefs.getString("il", "none");
        if (!il.equals("none")) {
            recyclerViewil = view.findViewById(R.id.recycler_view_il);
            recyclerViewil.setHasFixedSize(true);
            recyclerViewil.setLayoutManager(new LinearLayoutManager(getContext()));
            post2List = new ArrayList<>();
            post2Adapter = new PostAdapter(getContext(), post2List);
            recyclerViewil.setAdapter(post2Adapter);
            searchIl(il);
        }
        return view;

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
}
