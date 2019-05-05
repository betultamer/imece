package imece.betul.imece.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import imece.betul.imece.Adapter.ListFriendsAdapter;
import imece.betul.imece.Commons.Common;
import imece.betul.imece.R;


import imece.betul.imece.model.Friend;




public class MessagelistFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String ACTION_DELETE_FRIEND = "imece.betul.imece.DELETE_FRIEND";
    private RecyclerView recyclerListFrends;
    private ListFriendsAdapter adapter;
     List<Friend>  dataListFriend ;
    private BroadcastReceiver deleteFriendReceiver;




    @Override

    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_people, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), +LinearLayoutManager.VERTICAL, false);
        recyclerListFrends = (RecyclerView) layout.findViewById(R.id.recycleListFriend);
        recyclerListFrends.setLayoutManager(linearLayoutManager);

            dataListFriend=new ArrayList<>();
            adapter = new ListFriendsAdapter(getContext(), dataListFriend);
            recyclerListFrends.setAdapter(adapter);

        if (Common.currentuser!=null) {
            getListFriendUId(Common.currentuser.getId());
        }else{
            getListFriendUId(Common.currentogretmen.getId());
        }

        deleteFriendReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String idDeleted = intent.getExtras().getString("idFriend");
                for (Friend friend : dataListFriend) {
                    if(idDeleted.equals(friend.id)){
                        List<Friend> friends = dataListFriend;
                        friends.remove(friend);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        };

        IntentFilter intentFilter = new IntentFilter(ACTION_DELETE_FRIEND);
        getContext().registerReceiver(deleteFriendReceiver, intentFilter);



        return layout;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getContext().unregisterReceiver(deleteFriendReceiver);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            ListFriendsAdapter.mapMark.put(data.getStringExtra("idFriend"), false);

    }


    private void getListFriendUId(final String uid) {

        FirebaseDatabase.getInstance().getReference("friend").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    HashMap mapRecord = (HashMap) dataSnapshot.getValue();
                    Iterator listKey = mapRecord.keySet().iterator();
                    while (listKey.hasNext()) {
                        dataListFriend.clear();
                        Friend user = new Friend();
                        String key = listKey.next().toString();
                        String id = mapRecord.get(key).toString();
                        String idRoom = id.compareTo(uid) > 0 ? (uid.hashCode() + id.hashCode()) + "" : "" + (id.hashCode() +uid.hashCode());
                        user.id = id;
                        user.idRoom = idRoom;
                        dataListFriend.add(user);

                    } adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



    @Override
    public void onRefresh() {
        if (Common.currentuser!=null) {
            getListFriendUId(Common.currentuser.getId());
        }else{
            getListFriendUId(Common.currentogretmen.getId());
        }


    }
}
