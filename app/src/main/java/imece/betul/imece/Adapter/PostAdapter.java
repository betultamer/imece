package imece.betul.imece.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;


import imece.betul.imece.Commons.Common;
import imece.betul.imece.Fragments.IlFragment;
import imece.betul.imece.Fragments.PostDetailFragment;
import imece.betul.imece.Fragments.ProfileFragment;
import imece.betul.imece.Fragments.SearchFragment;
import imece.betul.imece.Message.ChatforTeacherActivity;
import imece.betul.imece.R;
import imece.betul.imece.model.Bagisveren;
import imece.betul.imece.model.Image;
import imece.betul.imece.model.Post;

import static android.content.Context.MODE_PRIVATE;
import static java.security.AccessController.getContext;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ImageViewHolder> {
    public String user_name;
    private Context mContext;
    private List<Post> mPosts;
    String image;
    List<String> images;
    ViewPagerAdapter viewPagerAdapter;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> posts) {
        mContext = context;
        mPosts = posts;
    }

    @NonNull
    @Override
    public PostAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ImageViewHolder holder, final int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = mPosts.get(position);
        LayoutInflater inflater=LayoutInflater.from(mContext);
        images =new ArrayList<>();
        viewPagerAdapter =new ViewPagerAdapter(mContext,images,post.getPostid());

        if (post.getPostimage() !=null ) {

            for (int i = 0; i < post.getPostimage().size(); i++) {
                image = post.getPostimage().get("link"+i).toString();
                images.add(image);
            }
            holder.vp.setAdapter(viewPagerAdapter);


        }
          else{
            Toast.makeText(mContext,"error",Toast.LENGTH_SHORT).show();
        }
        holder.il.setText(post.getIl());


        if (post.getDescription().equals("")) {
            holder.description.setVisibility(View.GONE);
        } else {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
        }

        publisherInfo(holder.image_profile, holder.username, post.getPublisher());
        if (Common.currentogretmen != null) {
            holder.sendmessage.setBackgroundResource(R.drawable.ic_inbox);
            holder.sendmessage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CheckFriend(post.getPublisher());

        }
    }); } else{
                holder.sendmessage.setBackgroundResource(R.color.transparent);
        }

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profilefordonorid", post.getPublisher());
                editor.putString("profileforteacherid","none");
                editor.apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

//


        holder.vp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("postid", post.getPostid());
                editor.apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new PostDetailFragment()).commit();
            }
        });
        holder.il.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("il", post.getIl());
                editor.apply();
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new IlFragment()).commit();

            }
        });


        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.sendMessage:




                            case R.id.delete:
                                final String id = post.getPostid();
                                FirebaseDatabase.getInstance().getReference("Posts")
                                        .child(post.getPostid()).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    deleteNotifications(id, firebaseUser.getUid());
                                                }
                                            }
                                        });
                                return true;
                            case R.id.report:
                                Toast.makeText(mContext, "Reported clicked!", Toast.LENGTH_SHORT).show();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popupMenu.inflate(R.menu.post_menu);
                if (!post.getPublisher().equals(firebaseUser.getUid())) {
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }


    });}



    private void sendMessage(final String userid) {

        String  idRoom = userid.compareTo(Common.currentogretmen.getId()) > 0 ? (Common.currentogretmen.getId().hashCode()+ userid.hashCode()) + "" : "" + (userid.hashCode() + Common.currentogretmen.getId().hashCode());
        Intent intent = new Intent(mContext, ChatforTeacherActivity.class);
        intent.putExtra("idFriend", userid);
        intent.putExtra("idroom", idRoom);
        intent.putExtra("friendid", userid);
        mContext.startActivity(intent);



    }
    private void CheckFriend(final String idFriend){


        FirebaseDatabase.getInstance().getReference("friend").child(Common.currentogretmen.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getValue().equals(null)) {
                    HashMap mapRecord = (HashMap) dataSnapshot.getValue();
                    Iterator listKey = mapRecord.keySet().iterator();
                    while (listKey.hasNext()) {
                        String key = listKey.next().toString();
                        if (mapRecord.get(key).toString()== idFriend){  sendMessage(idFriend);
                        }
                        else{ addFriend(idFriend, false);}
                    }

                } else {
                    addFriend(idFriend, true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }
    private void addFriend(final String idFriend, boolean isIdFriend) {
        if (idFriend != null) {
            if (isIdFriend) {
                FirebaseDatabase.getInstance().getReference("friend").child(Common.currentogretmen.getId()).push().setValue(idFriend)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    addFriend(idFriend, false);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            } else {
                FirebaseDatabase.getInstance().getReference("friend").child(idFriend).push().setValue(Common.currentogretmen.getId()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            addFriend(null, false);
                            sendMessage(idFriend);
                        }
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
            }
        } else {

        }
    }
    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    private void editPost(final String postid) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Edit Post");

        final EditText editText = new EditText(mContext);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        editText.setLayoutParams(lp);
        alertDialog.setView(editText);

        getText(postid, editText);

        alertDialog.setPositiveButton("Edit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("description", editText.getText().toString());

                        FirebaseDatabase.getInstance().getReference("Posts")
                                .child(postid).updateChildren(hashMap);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        alertDialog.show();
    }

    private void deleteNotifications(final String postid, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("postid").getValue().equals(postid)) {
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(mContext, "Deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void publisherInfo(final ImageView image_profile, final TextView username, final String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Bagisveren").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bagisveren user = dataSnapshot.getValue(Bagisveren.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getFullname());
                user_name=user.getFullname();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getText(String postid, final EditText editText) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts")
                .child("Bagisveren").child(postid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(Post.class).getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile,  like, sendmessage, more;
        public TextView username, likes, description, comments, il;
        public LinearLayout gallery;
        public RecyclerView post_image;
        public ViewPager vp;

                public ImageViewHolder(View itemView) {
            super(itemView);
            il = itemView.findViewById(R.id.locations);
            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            post_image = itemView.findViewById(R.id.recyclerview);
            sendmessage=itemView.findViewById(R.id.sendMessage);
            description = itemView.findViewById(R.id.description);
            more = itemView.findViewById(R.id.more);
           // gallery =itemView.findViewById(R.id.gallery);
            vp= itemView.findViewById(R.id.view_pager);
        }
    }


        }



