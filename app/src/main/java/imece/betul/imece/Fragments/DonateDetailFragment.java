package imece.betul.imece.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import imece.betul.imece.Adapter.DonateAdapter;
import imece.betul.imece.R;
import imece.betul.imece.model.Donate;

import static android.content.Context.MODE_PRIVATE;


public class DonateDetailFragment extends Fragment {

    String donateid;

    private RecyclerView recyclerView;
    private DonateAdapter donateAdapter;
    private List<Donate> donateList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donate_detail, container, false);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        donateid = prefs.getString("donateid", "none");

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        donateList = new ArrayList<>();
        donateAdapter = new DonateAdapter(getContext(), donateList);
        recyclerView.setAdapter(donateAdapter);

        readDonate();

        return view;
    }

    private void readDonate() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Donates").child(donateid);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                donateList.clear();
                Donate donate = dataSnapshot.getValue(Donate.class);
                donateList.add(donate);
                donateAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
