package com.example.henrique.tcc;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

/**
 * Created by Henrique on 19/09/2017.
 */

public class ProblemDetails extends MarkerInfoWindow {

    private ProblemMarker attachedMarker;

    public ProblemDetails(MapView mapView) {
        super(R.layout.problem_details, mapView);
    }

    @Override
    public void onOpen(Object item){
        attachedMarker = (ProblemMarker) item;

        TextView title = (TextView) (mView.findViewById(R.id.detailsTitle));
        TextView description = (TextView) (mView.findViewById(R.id.detailsDescription));
        TextView voteCountUp = (TextView) (mView.findViewById(R.id.detailsVotesUp));
        TextView voteCountDown = (TextView) (mView.findViewById(R.id.detailsVotesDown));

        if (attachedMarker != null){
            if (attachedMarker.problemTitle != null)
                title.setText(attachedMarker.problemTitle);

            if (attachedMarker.problemDescription != null)
                title.setText(attachedMarker.problemDescription);

            voteCountUp.setText(String.valueOf(attachedMarker.votesUp));
            voteCountDown.setText(String.valueOf(attachedMarker.votesDown));

        }

            title.setText(attachedMarker.problemTitle);


        Button voteUp = (Button) (mView.findViewById(R.id.detailsVoteUpButton));
        Button voteDown = (Button) (mView.findViewById(R.id.detailsVoteDownButton));

        voteUp.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

            }
        });
    }
}
