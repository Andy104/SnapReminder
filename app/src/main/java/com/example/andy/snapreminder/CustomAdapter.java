package com.example.andy.snapreminder;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<SetOfData> {
    public CustomAdapter(Context context, ArrayList<SetOfData> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SetOfData data = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        TextView displayedID = (TextView) convertView.findViewById(R.id.numer);
        TextView displayedTitle = (TextView) convertView.findViewById(R.id.tytul);
        TextView displayedDescription = (TextView) convertView.findViewById(R.id.opis);
        TextView displayedTimeDate = (TextView) convertView.findViewById(R.id.czas);

        displayedID.setText(data.id);
        displayedTitle.setText(data.tytul);
        displayedDescription.setText(data.opis);
        displayedTimeDate.setText(data.czas);

        return convertView;
    }
}
