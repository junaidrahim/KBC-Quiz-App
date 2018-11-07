package com.junaidrahim.kbcquiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OptionAdapter extends ArrayAdapter<String>{

    public OptionAdapter(Context context, String[] options) {
        super(context, R.layout.options_list, options);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater mainInflater = LayoutInflater.from(getContext());
        View mainView = mainInflater.inflate(R.layout.options_list, parent, false);

        TextView optionText = (TextView) mainView.findViewById(R.id.optionText);
        optionText.setText(getItem(position));

        return mainView;
    }
}
