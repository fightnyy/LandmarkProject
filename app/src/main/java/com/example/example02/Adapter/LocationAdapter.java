package com.example.example02.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import com.example.example02.R;

public class LocationAdapter extends BaseAdapter {
    private ArrayList<String> mapInfo = new ArrayList<>();

    public LocationAdapter() {
    }

    @Override
    public int getCount() {
        return mapInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return mapInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_location_item, parent, false);
        }

        TextView text = (TextView) convertView.findViewById(R.id.txt_name);

        text.setText(mapInfo.get(position));

        return convertView;
    }

    public void addItem(String text){
        mapInfo.add(text);
    }
}
