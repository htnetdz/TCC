package com.example.henrique.tcc;

import android.content.Context;

import org.osmdroid.views.MapView;

/**
 * Created by Henrique on 18/09/2017.
 */

public class ProblemMarker extends org.osmdroid.views.overlay.Marker {
    public ProblemMarker(MapView mapView, Context resourceProxy) {
        super(mapView, resourceProxy);
    }
    public ProblemMarker(MapView mapView) {
        super(mapView);
    }

    public String problemTitle;
    public String problemDescription;
    public int votesUp;
    public int votesDown;
    public int problemId;

    public String getProblemTitle() {
        return problemTitle;
    }

    public void setProblemTitle(String problemTitle) {
        this.problemTitle = problemTitle;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public int getVotesUp() {
        return votesUp;
    }

    public void setVotesUp(int votesUp) {
        this.votesUp = votesUp;
    }

    public int getVotesDown() {
        return votesDown;
    }

    public void setVotesDown(int votesDown) {
        this.votesDown = votesDown;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }
}
