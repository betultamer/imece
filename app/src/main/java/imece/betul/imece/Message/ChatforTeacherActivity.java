package imece.betul.imece.Message;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import imece.betul.imece.Commons.Common;
import imece.betul.imece.Fragments.ProfileFragment;
import imece.betul.imece.R;
import imece.betul.imece.model.Bagisveren;
import imece.betul.imece.model.Consersation;
import imece.betul.imece.model.Message;


public class ChatforTeacherActivity extends AppCompatActivity implements View.OnClickListener {
    public static int VIEW_TYPE_USER_MESSAGE = 0;
    public static int VIEW_TYPE_FRIEND_MESSAGE = 1;
    public static HashMap<String, Bitmap> bitmapAvataFriend;
    public Bitmap bitmapAvataUser;
    private RecyclerView recyclerChat;
    private ListMessageAdapter adapter;
    private String roomId;
    TextView user;
    private String idFriend,user_name;
    private Consersation consersation;
    private ImageButton btnSend;
    private EditText editWriteMessage;
    private LinearLayoutManager linearLayoutManager;
    CircleImageView userphoto;
    RelativeLayout rel_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_for_teacher);
        Intent intentData = getIntent();
        user= findViewById(R.id.user_name);
        userphoto=findViewById(R.id.profilephoto);
        idFriend = intentData.getStringExtra("idFriend");
        rel_layout= findViewById(R.id.rel_layout);



        roomId = intentData.getStringExtra("idroom");

        consersation = new Consersation();
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);

        editWriteMessage = (EditText) findViewById(R.id.editWriteMessage);
        if (idFriend != null) {
            publisherInfo(userphoto, user, idFriend);
            user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rel_layout.getLayoutParams().height=0;
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("profilefordonorid",idFriend);
                    editor.putString("profileforteacherid","none");
                    editor.apply();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new  ProfileFragment()).commit();
                }
            });
            linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerChat = (RecyclerView) findViewById(R.id.recyclerChat);
            recyclerChat.setLayoutManager(linearLayoutManager);
            adapter = new ListMessageAdapter(this, consersation);
            FirebaseDatabase.getInstance().getReference().child("message/" + roomId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.getValue() != null) {
                        HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                        Message newMessage = new Message();
                        newMessage.idSender = (String) mapMessage.get("idSender");
                        newMessage.idReceiver = (String) mapMessage.get("idReceiver");
                        newMessage.text = (String) mapMessage.get("text");
                        newMessage.timestamp = (long) mapMessage.get("timestamp");
                        consersation.getListMessageData().add(newMessage);
                        adapter.notifyDataSetChanged();
                        linearLayoutManager.scrollToPosition(consersation.getListMessageData().size() - 1);
                    }
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
            recyclerChat.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        result.putExtra("idFriend", idFriend);
        setResult(RESULT_OK, result);
        this.finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnSend) {
            String content = editWriteMessage.getText().toString().trim();
            if (content.length() > 0) {
                editWriteMessage.setText("");
                Message newMessage = new Message();
                newMessage.text = content;
                newMessage.idSender = Common.currentogretmen.getId();
                newMessage.idReceiver = idFriend;
                newMessage.timestamp = System.currentTimeMillis();
                FirebaseDatabase.getInstance().getReference().child("message/" + roomId).push().setValue(newMessage);
            }
        }
    }
    public void publisherInfo(final CircleImageView image_profile, final TextView username, final String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Users").child("Bagisveren").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bagisveren user = dataSnapshot.getValue(Bagisveren.class);
                Glide.with(ChatforTeacherActivity.this).load(user.getImageurl()).into(image_profile);
                username.setText(user.getFullname());



        }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

class ListMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Consersation consersation;


    public ListMessageAdapter(Context context, Consersation consersation) {
        this.context = context;
        this.consersation = consersation;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ChatforTeacherActivity.VIEW_TYPE_FRIEND_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_friend, parent, false);
            return new ItemMessageFriendHolder(view);
        } else if (viewType == ChatforTeacherActivity.VIEW_TYPE_USER_MESSAGE) {
            View view = LayoutInflater.from(context).inflate(R.layout.rc_item_message_user, parent, false);
            return new ItemMessageUserHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ItemMessageFriendHolder) {
            ((ItemMessageFriendHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);
            ((ItemMessageFriendHolder) holder).txtContent.setBackgroundResource(R.color.authui_colorAccent);


        } else if (holder instanceof ItemMessageUserHolder) {
            ((ItemMessageUserHolder) holder).txtContent.setText(consersation.getListMessageData().get(position).text);
            ((ItemMessageUserHolder) holder).txtContent.setBackgroundResource(R.color.authui_colorActivated);

        }


    }


    @Override
    public int getItemViewType(int position) {
        return consersation.getListMessageData().get(position).idSender.equals(Common.currentogretmen.getId()) ? ChatforTeacherActivity.VIEW_TYPE_USER_MESSAGE : ChatforTeacherActivity.VIEW_TYPE_FRIEND_MESSAGE;


    }

    @Override
    public int getItemCount() {
        return consersation.getListMessageData().size();
    }



}

class ItemMessageUserHolder extends RecyclerView.ViewHolder {
    public TextView txtContent;
    public CircleImageView avata;


    public ItemMessageUserHolder(View itemView) {
        super(itemView);
        txtContent = (TextView) itemView.findViewById(R.id.textContentUser);


    }
}

class ItemMessageFriendHolder extends RecyclerView.ViewHolder {
    public TextView txtContent;
    public CircleImageView avata;

    public ItemMessageFriendHolder(View itemView) {
        super(itemView);
        txtContent = (TextView) itemView.findViewById(R.id.textContentFriend);

    }
}
