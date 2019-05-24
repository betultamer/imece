package imece.betul.imece.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import imece.betul.imece.Commons.Common;
import imece.betul.imece.Fragments.MessagelistFragment;
import imece.betul.imece.Message.ChatForDonorActivity;
import imece.betul.imece.Message.ChatforTeacherActivity;
import imece.betul.imece.R;
import imece.betul.imece.data.StaticConfig;
import imece.betul.imece.model.Bagisveren;
import imece.betul.imece.model.Friend;
import imece.betul.imece.model.ListFriend;
import imece.betul.imece.model.Ogretmen;

public class ListFriendsAdapter extends RecyclerView.Adapter<ListFriendsAdapter.ItemFriendViewHolder> {

    List<Friend> listFriend;
    private Context context;
    public static Map<String, Query> mapQuery;
    public static Map<String, DatabaseReference> mapQueryOnline;
    public static Map<String, ChildEventListener> mapChildListener;
    public static Map<String, ChildEventListener> mapChildListenerOnline;
    public static Map<String, Boolean> mapMark;


    public ListFriendsAdapter(Context context, List<Friend> listFriend) {
        this.listFriend = listFriend;
        this.context = context;
        mapQuery = new HashMap<>();
        mapChildListener = new HashMap<>();
        mapMark = new HashMap<>();
        mapChildListenerOnline = new HashMap<>();
        mapQueryOnline = new HashMap<>();


    }

    @Override
    public ListFriendsAdapter.ItemFriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_friend, parent, false);
        return new ItemFriendViewHolder(context, view);
    }



    @Override
    public void onBindViewHolder(final  ListFriendsAdapter.ItemFriendViewHolder holder, final int position) {

        final String id = listFriend.get(position).id;
        final String idRoom = listFriend.get(position).idRoom;
        if (Common.currentogretmen ==null ) {
            PublisherInfo(holder.avata, holder.txtName,id);
            ((View) holder.txtName.getParent().getParent().getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    holder.txtName.setTypeface(Typeface.DEFAULT);
                    Intent intent = new Intent(context, ChatForDonorActivity.class);
                    intent.putExtra("idFriend", id);
                    intent.putExtra("idroom", idRoom);
                    intent.putExtra("friendid", id);
                    context.startActivity(intent);
                    mapMark.put(id, null);
                }
            });

        }else{ if (Common.currentuser ==null ){
            bagisverenInfo(holder.avata, holder.txtName,id);
            ((View) holder.txtName.getParent().getParent().getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    holder.txtName.setTypeface(Typeface.DEFAULT);
                    Intent intent = new Intent(context, ChatforTeacherActivity.class);
                    intent.putExtra("idFriend", id);
                    intent.putExtra("idroom", idRoom);
                    intent.putExtra("friendid", id);
                    context.startActivity(intent);
                    mapMark.put(id, null);
                }
            });

        }
        }



        ((View) holder.txtName.getParent().getParent().getParent())
                .setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        String friendName = (String) holder.txtName.getText();

                        new AlertDialog.Builder(context)
                                .setTitle("Delete Friend")
                                .setMessage("Are you sure want to delete "+friendName+ "?")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        final String idFriendRemoval = listFriend.get(position).id;

                                        deleteFriend(idFriendRemoval);
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).show();

                        return true;
                    }
                });


        if (listFriend.get(position).message.text.length() > 0) {
            holder.txtMessage.setVisibility(View.VISIBLE);
            holder.txtTime.setVisibility(View.VISIBLE);
            holder.txtMessage.setText(listFriend.get(position).message.text);
            holder.txtMessage.setTypeface(Typeface.DEFAULT_BOLD);
            holder.txtName.setTypeface(Typeface.DEFAULT_BOLD);

            String time = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(listFriend.get(position).message.timestamp));
            String today = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(System.currentTimeMillis()));
            if (today.equals(time)) {
                holder.txtTime.setText(new SimpleDateFormat("HH:mm").format(new Date(listFriend.get(position).message.timestamp)));
            } else {
                holder.txtTime.setText(new SimpleDateFormat("MMM d").format(new Date(listFriend.get(position).message.timestamp)));
            }
        } else {
            holder.txtMessage.setVisibility(View.GONE);
            holder.txtTime.setVisibility(View.GONE);
            if (mapQuery.get(id) == null && mapChildListener.get(id) == null) {
                mapQuery.put(id, FirebaseDatabase.getInstance().getReference().child("message/" + idRoom).limitToLast(1));
                mapChildListener.put(id, new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                        if (mapMark.get(id) != null) {
                            if (!mapMark.get(id)) {
                                listFriend.get(position).message.text = id + mapMessage.get("text");
                            } else {
                                listFriend.get(position).message.text = (String) mapMessage.get("text");
                            }
                            notifyDataSetChanged();
                            mapMark.put(id, false);
                        } else {
                            listFriend.get(position).message.text = (String) mapMessage.get("text");
                            notifyDataSetChanged();
                        }
                        listFriend.get(position).message.timestamp = (long) mapMessage.get("timestamp");
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mapQuery.get(id).addChildEventListener(mapChildListener.get(id));
                mapMark.put(id, true);
            } else {
                mapQuery.get(id).removeEventListener(mapChildListener.get(id));
                mapQuery.get(id).addChildEventListener(mapChildListener.get(id));
                mapMark.put(id, true);
            }
        }





    }

    @Override
    public int getItemCount() {
        return listFriend.size();
    }


    private void deleteFriend(final String idFriend) {
        if (idFriend != null) {
            String uid = null;
            if ( Common.currentuser != null){  uid = Common.currentuser.getId();}
            else  {if ( Common.currentogretmen!= null){  uid = Common.currentogretmen.getId();}}
            final String finaluid=uid;
            FirebaseDatabase.getInstance().getReference("friend").child(finaluid)
                    .orderByValue().equalTo(idFriend).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    String idRemoval = ((HashMap) dataSnapshot.getValue()).keySet().iterator().next().toString();
                    FirebaseDatabase.getInstance().getReference("friend")
                            .child(finaluid).child(idRemoval).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {



                                    Intent intentDeleted = new Intent(MessagelistFragment.ACTION_DELETE_FRIEND);

                                    intentDeleted.putExtra("idFriend", idFriend);
                                    context.sendBroadcast(intentDeleted);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                }


                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
        } }

    private void PublisherInfo(final ImageView image_profile, final TextView username, final String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("ogretmen").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ogretmen user = dataSnapshot.getValue(Ogretmen.class);
                Glide.with(context).load(user.getImageurl()).into(image_profile);
                username.setText(user.getFullname());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void bagisverenInfo(final ImageView image_profile, final TextView username, final String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child("Bagisveren").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bagisveren bagisveren = dataSnapshot.getValue(Bagisveren.class);
                Glide.with(context).load(bagisveren.getImageurl()).into(image_profile);
                username.setText(bagisveren.getFullname());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class ItemFriendViewHolder extends RecyclerView.ViewHolder{
        public ImageView avata;
        public TextView txtName, txtTime, txtMessage;
        private Context context;

        public  ItemFriendViewHolder(Context context, View itemView) {
            super(itemView);
            avata = itemView.findViewById(R.id.icon_avata);
            txtName = itemView.findViewById(R.id.txtName);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            this.context = context;
        }
    }}