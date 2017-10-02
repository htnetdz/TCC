package com.example.henrique.tcc;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import static java.lang.String.valueOf;

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
        title.setText(attachedMarker.problemTitle);
        TextView description = (TextView) (mView.findViewById(R.id.detailsDescription));
        description.setText(attachedMarker.problemDescription);
        TextView voteCountUp = (TextView) (mView.findViewById(R.id.detailsVotesUp));
        voteCountUp.setText(String.valueOf(attachedMarker.votesUp));
        TextView voteCountDown = (TextView) (mView.findViewById(R.id.detailsVotesDown));
        voteCountDown.setText(String.valueOf(attachedMarker.votesDown));

        if (attachedMarker != null){
            if (attachedMarker.problemTitle != null)
                title.setText(attachedMarker.problemTitle);

            if (attachedMarker.problemDescription != null)
                title.setText(attachedMarker.problemDescription);

            voteCountUp.setText(valueOf(attachedMarker.votesUp));
            voteCountDown.setText(valueOf(attachedMarker.votesDown));

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
