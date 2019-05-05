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

public class CityAdapter extends ArrayAdapter<Ilce> {
        private List<Ilce> ilceList = new ArrayList<>();

        CityAdapter(@NonNull Context context, int resource, int spinnerText, @NonNull List<Ilce> ilceList) {
                super(context, resource, spinnerText, ilceList);
                this.ilceList = ilceList;
        }

        @Override
        public Ilce getItem(int position) {
                return ilceList.get(position);
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
                Ilce state = getItem(position);
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.city_list, null);
                TextView textView =  v.findViewById(R.id.citySpinnerText);
                textView.setText(state.getCityName());
                return v;

        }
}
