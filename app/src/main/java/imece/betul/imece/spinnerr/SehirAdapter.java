package imece.betul.imece.spinnerr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import imece.betul.imece.R;

/**
 * Created by Abhi on 04 Jan 2018 004.
 */

public class SehirAdapter extends ArrayAdapter<Sehir> {
    private List<Sehir> sehirList = new ArrayList<>();

    public SehirAdapter(@NonNull Context context, int resource, int spinnerText, @NonNull List<Sehir> sehirList) {
        super(context, resource, spinnerText, sehirList);
        this.sehirList = sehirList;
    }

    @Override
    public Sehir getItem(int position) {
        return sehirList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position);

    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position);
    }

    /**
     * Gets the state object by calling getItem and
     * Sets the state name to the drop-down TextView.
     *
     * @param position the position of the item selected
     * @return returns the updated View
     */
    private View initView(int position) {
        Sehir sehir = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.state_list, null);
        TextView textView =  v.findViewById(R.id.spinnerText);
        textView.setText(sehir.getSehirAdi());
        return v;

    }
}
