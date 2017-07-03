package com.streetmarket.olga_pc.suppliers.autocomplete;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.streetmarket.olga_pc.suppliers.R;
import com.streetmarket.olga_pc.suppliers.beans.KeyValue;
import com.streetmarket.olga_pc.suppliers.dao.DatabaseHandler;

import java.util.ArrayList;

/**
 * Created by Olga-PC on 5/14/2016.
 */
public class SearchAdapter extends ArrayAdapter<KeyValue> {

    private static final String TAG = "KEY_VALUE_ADAPTER";
    private DatabaseHandler.Master master;
    private Filter filter;

    public SearchAdapter(Context context, int resource, DatabaseHandler.Master master) {
        super(context, resource);
        this.master = master;
    }

    public DatabaseHandler.Master getMaster() {
        return master;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.search_item, parent, false);
        }

        TextView tvKey = (TextView) convertView.findViewById(R.id.search_item_key);
        TextView tvName = (TextView) convertView.findViewById(R.id.search_item_name);

        final KeyValue keyValue = this.getItem(position);
        convertView.setTag(keyValue);
        try {

            CharSequence key = String.valueOf(keyValue.getKey());
            CharSequence value = keyValue.getValue();

            tvKey.setText(key);
            tvName.setText(value);
        } catch (Exception e) {

            Log.i(TAG, e.getMessage());
        }

        return convertView;

    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ValueFilter(this);
        }
        return filter;
    }

    private static class ValueFilter extends Filter {
        private SearchAdapter searchAdapter;
        private ArrayList<KeyValue> list;

        public ValueFilter(SearchAdapter adapter) {
            this.searchAdapter = adapter;
            list = adapter.getMaster().retrive();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults result = new FilterResults();
            String substr = constraint != null ? constraint.toString().toLowerCase() : "";
            // if no constraint is given, return the whole list
            if (substr.length() == 0) {
                result.values = list;
                result.count = list.size();
            } else {
                // iterate over the list of venues and find if the venue matches the constraint. if it does, add to the result list
                final ArrayList<KeyValue> retList = new ArrayList<KeyValue>();
                for (KeyValue venue : list) {
                    try {
                        if (venue.getValue().toLowerCase().contains(constraint)) {
                            retList.add(venue);
                        }
                    } catch (Exception e) {
                        Log.i(TAG, e.getMessage());
                    }
                }
                result.values = retList;
                result.count = retList.size();
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // we clear the adapter and then pupulate it with the new results
            searchAdapter.clear();
            if (results.count > 0) {
                for (KeyValue o : (ArrayList<KeyValue>) results.values) {
                    searchAdapter.add(o);
                }
            }
        }

    }
}
