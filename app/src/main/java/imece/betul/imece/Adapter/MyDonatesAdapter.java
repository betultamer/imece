package imece.betul.imece.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import imece.betul.imece.Fragments.DonateDetailFragment;
import imece.betul.imece.R;
import imece.betul.imece.model.Donate;

import static android.content.Context.MODE_PRIVATE;


public class MyDonatesAdapter extends RecyclerView.Adapter<MyDonatesAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Donate> mDonates;

    public MyDonatesAdapter(Context context, List<Donate> donates) {
        mContext = context;
        mDonates = donates;
    }

    @NonNull
    @Override
    public MyDonatesAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.donatess_item, parent, false);
        return new MyDonatesAdapter.ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyDonatesAdapter.ImageViewHolder holder, final int position) {

        final Donate donate = mDonates.get(position);

        holder.donate.setText(donate.getIstenilen_urun());
        holder.donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                editor.putString("donateid", donate.getId());
                editor.apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new DonateDetailFragment()).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDonates.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        public TextView donate;


        public ImageViewHolder(View itemView) {
            super(itemView);

            donate = itemView.findViewById(R.id.istenilenliste);

        }
    }
}