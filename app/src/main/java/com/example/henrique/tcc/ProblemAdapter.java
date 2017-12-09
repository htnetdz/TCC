package com.example.henrique.tcc;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static com.example.henrique.tcc.R.drawable.ic_local_movies_black_24dp;

/**
 * Created by Henrique on 06/09/2017.
 */

public class ProblemAdapter extends ArrayAdapter<Problem> implements Filterable{
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
        TextView voteUpCounter = (TextView) convertView.findViewById(R.id.listVoteUpCount);
        TextView voteDownCounter = (TextView) convertView.findViewById(R.id.listVotedDownCount);
        ImageView thumbUpIcon = (ImageView) convertView.findViewById(R.id.thumbUp);
        ImageView thumbDownIcon = (ImageView) convertView.findViewById(R.id.thumbDown);

        // Populate the data into the template view using the data object
        problemTitle.setText(toString().valueOf(problem.titulo));
        problemDescription.setText(problem.descricao);
        problemIcon.setImageResource(R.drawable.ic_announcement_black_24dp);
        thumbUpIcon.setImageResource(R.drawable.ic_thumb_up_black_24dp);
        thumbDownIcon.setImageResource(R.drawable.ic_thumb_down_black_24dp);
        voteUpCounter.setText(toString().valueOf(problem.votos_pos));
        voteDownCounter.setText(toString().valueOf(problem.votos_neg));

        // Return the completed view to render on screen
        return convertView;
    }


}
