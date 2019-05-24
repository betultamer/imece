package imece.betul.imece.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import imece.betul.imece.Adapter.DonateAdapter;
import imece.betul.imece.Commons.Common;
import imece.betul.imece.DonateActivity;
import imece.betul.imece.Message.MessageListActivity;
import imece.betul.imece.R;
import imece.betul.imece.model.Donate;

public class BagisFragment extends Fragment {
    ProgressBar progress_circular;
    private RecyclerView recyclerView;
    private DonateAdapter donateAdapter;
    private List<Donate> donateList;
    private Button add_donate;
    private ImageView message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bagis, container, false);
        add_donate = view.findViewById(R.id.bagis_iste);
        recyclerView = view.findViewById(R.id.recycler_view2);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        donateList = new ArrayList<>();
        donateAdapter = new DonateAdapter(getContext(), donateList);
        recyclerView.setAdapter(donateAdapter);
        message = view.findViewById(R.id.messagebox);


        if (Common.currentogretmen != null) {
            add_donate.setBackgroundResource(R.drawable.ic_add_black_24dp);


            add_donate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), DonateActivity.class));
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
        readBagis();
        return view;
    }


    private void readBagis() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Donates");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                donateList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Donate donate = snapshot.getValue(Donate.class);


                    donateList.add(donate);

                }


                donateAdapter.notifyDataSetChanged();
                progress_circular.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
