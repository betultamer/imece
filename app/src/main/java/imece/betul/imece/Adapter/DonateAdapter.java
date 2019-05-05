package imece.betul.imece.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import imece.betul.imece.Commons.Common;
import imece.betul.imece.Fragments.DonateDetailFragment;
import imece.betul.imece.Fragments.ProfileFragment;
import imece.betul.imece.Message.ChatForDonorActivity;
import imece.betul.imece.Message.ChatforTeacherActivity;
import imece.betul.imece.R;
import imece.betul.imece.model.Donate;
import imece.betul.imece.model.Ogretmen;

import static android.content.Context.MODE_PRIVATE;

public class DonateAdapter extends RecyclerView.Adapter<imece.betul.imece.Adapter.DonateAdapter.ImageViewHolder> {
    private Context mContext;
    private List<Donate> mDonates;

    private FirebaseUser firebaseUser;

    public DonateAdapter(Context context, List<Donate> donates) {
        mContext = context;
        mDonates = donates;
    }

    @NonNull
    @Override
    public DonateAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.donate_item, parent, false);
        return new DonateAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DonateAdapter.ImageViewHolder holder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        final Donate donate = mDonates.get(i);

        holder.isteklistesi.setVisibility(View.VISIBLE);
        holder.isteklistesi.setText(donate.getIstenilen_urun());


        publisherInfo(holder.image_profile, holder.okul, holder.il, holder.username, donate.getPublisher());


        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("profileforteacherid", donate.getPublisher());
                editor.putString("profilefordonorid","none");

                editor.apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
            }
        });

        if (Common.currentuser != null) {
            holder.sendMessage.setBackgroundResource(R.drawable.ic_inbox);
            holder.sendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckFriend(donate.getPublisher());

                }
            }); } else{
            holder.sendMessage.setBackgroundResource(R.color.transparent);
        }

        holder.isteklistesi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("donateid", donate.getId());
                editor.apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new DonateDetailFragment()).commit();
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

                            case R.id.delete:
                                final String id = donate.getId();
                                FirebaseDatabase.getInstance().getReference("Donates")
                                        .child(donate.getId()).removeValue()
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
                if (!donate.getPublisher().equals(firebaseUser.getUid())) {
                    popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                    popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
                }
                popupMenu.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return mDonates.size();
    }

    private void deleteNotifications(final String donateid, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("id").getValue().equals(donateid)) {
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

    private void publisherInfo(final ImageView image_profile, final TextView okul, final TextView il, final TextView username, final String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("ogretmen").child(userid);


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Ogretmen user = dataSnapshot.getValue(Ogretmen.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getFullname());
                il.setText(user.getIl());
                okul.setText(user.getOkul());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void sendMessage(final String userid) {

        String  idRoom = userid.compareTo(Common.currentuser.getId()) > 0 ? (Common.currentuser.getId().hashCode()+ userid.hashCode()) + "" : "" + (userid.hashCode() + Common.currentuser.getId().hashCode());
        Intent intent = new Intent(mContext, ChatForDonorActivity.class);
        intent.putExtra("idFriend", userid);
        intent.putExtra("idroom", idRoom);
        intent.putExtra("friendid", userid);
        mContext.startActivity(intent);



    }
    private void CheckFriend(final String idFriend){


        FirebaseDatabase.getInstance().getReference("friend").child(Common.currentuser.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    HashMap mapRecord = (HashMap) dataSnapshot.getValue();
                    Iterator listKey = mapRecord.keySet().iterator();
                    while (listKey.hasNext()) {
                        String key = listKey.next().toString();
                        if (mapRecord.get(key).toString().equals(idFriend)){
                            sendMessage(idFriend);
                        }
                        else{
                            addFriend(idFriend, false);
                        }

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
                FirebaseDatabase.getInstance().getReference().child("friend/" + Common.currentuser.getId()).push().setValue(idFriend)
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
                FirebaseDatabase.getInstance().getReference().child("friend/" + idFriend).push().setValue(Common.currentuser.getId()).addOnCompleteListener(new OnCompleteListener<Void>() {
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



    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_profile, more,sendMessage;
        public TextView username, il, okul, isteklistesi;

        public ImageViewHolder(View itemView) {
            super(itemView);
            okul = itemView.findViewById(R.id.okul);
            sendMessage = itemView.findViewById(R.id.sendMessage);
            il = itemView.findViewById(R.id.locations);
            image_profile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            isteklistesi = itemView.findViewById(R.id.istenilenliste);
            more = itemView.findViewById(R.id.more);
        }

    }
    private void getText(String donateid, final EditText editText){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Donates")
                .child("ogretmen").child(donateid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                editText.setText(dataSnapshot.getValue(Donate.class).getIstenilen_urun());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
