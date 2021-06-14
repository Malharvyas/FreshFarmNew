package com.freshfarm.freshfarmnew.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.freshfarm.freshfarmnew.Model.AreaModel;
import com.freshfarm.freshfarmnew.R;

import java.util.ArrayList;

public class AreaAdapter extends ArrayAdapter<AreaModel> {
    private final String MY_DEBUG_TAG = "CustomerAdapter";
    private ArrayList<AreaModel> items;
    private ArrayList<AreaModel> itemsAll;
    private ArrayList<AreaModel> suggestions;
    private int viewResourceId;

    public AreaAdapter(Context context, int viewResourceId, ArrayList<AreaModel> items) {
        super(context, viewResourceId, items);
        this.items = items;
        this.itemsAll = (ArrayList<AreaModel>) items.clone();
        this.suggestions = new ArrayList<AreaModel>();
        this.viewResourceId = viewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, null);
        }
        AreaModel customer = items.get(position);
        if (customer != null) {
            TextView customerNameLabel = (TextView) v.findViewById(R.id.textView);
            if (customerNameLabel != null) {
//              Log.i(MY_DEBUG_TAG, "getView Customer Name:"+customer.getName());
                customerNameLabel.setText(customer.getAreaName());
            }
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((AreaModel) (resultValue)).getAreaName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (AreaModel customer : itemsAll) {
                    if (customer.getAreaName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(customer);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<AreaModel> filteredList = (ArrayList<AreaModel>) results.values;
            if (results != null && results.count > 0) {
                clear();
                for (AreaModel c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
        }
    };
}
