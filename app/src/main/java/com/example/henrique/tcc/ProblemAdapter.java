package com.example.henrique.tcc;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static com.example.henrique.tcc.R.drawable.ic_local_movies_black_24dp;

/**
 * Created by Henrique on 06/09/2017.
 */

public class ProblemAdapter extends ArrayAdapter<Problem> {
    public ProblemAdapter(Context context, int resource, List objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Problem problem  = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.problem_item, parent, false);
        }
        // Lookup view for data population
        TextView problemTitle = (TextView) convertView.findViewById(R.id.problemTitle);
        TextView  problemDescription= (TextView) convertView.findViewById(R.id.problemDescription);
        ImageView problemIcon = (ImageView) convertView.findViewById(R.id.problemIcon);
        // Populate the data into the template view using the data object
        problemTitle.setText(problem.id);
        problemDescription.setText(problem.descricao);
        problemIcon.setImageResource(R.drawable.ic_local_movies_black_24dp);
        // Return the completed view to render on screen
        return convertView;
    }

}
