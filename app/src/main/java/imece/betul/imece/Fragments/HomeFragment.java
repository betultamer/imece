package imece.betul.imece.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import imece.betul.imece.Adapter.PostAdapter;
import imece.betul.imece.Commons.Common;
import imece.betul.imece.Message.MessageListActivity;
import imece.betul.imece.Phto;
import imece.betul.imece.PostActivity;

import imece.betul.imece.PostMultipleActivity;
import imece.betul.imece.R;
import imece.betul.imece.model.Post;

public class HomeFragment extends Fragment {
    Boolean usertype;
    ProgressBar progress_circular;
    private Context mContext;
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private Button add_post;
    private ImageView search,message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        add_post = view.findViewById(R.id.add_post);
        recyclerView = view.findViewById(R.id.recycler_view);
        message =view.findViewById(R.id.messagebox);
        search =view.findViewById(R.id.search);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);

        readPosts();
        if (Common.currentogretmen == null) {
            add_post.setBackgroundResource(R.drawable.ic_add_black_24dp);

        } else {
            usertype = false;

        }
        if (Common.currentuser != null) {
            add_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), PostMultipleActivity.class));
                }
            });
        }
        progress_circular = view.findViewById(R.id.progress_circular);

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    startActivity(new Intent(getActivity(), MessageListActivity.class));
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchFragment fragment2 = new SearchFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment2);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    private void readPosts() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                        postList.add(post);
                    }
                postAdapter.notifyDataSetChanged();
                progress_circular.setVisibility(View.GONE);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


}
