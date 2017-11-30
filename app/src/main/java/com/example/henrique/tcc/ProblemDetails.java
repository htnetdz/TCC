package com.example.henrique.tcc;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;

/**
 * Created by Henrique on 19/09/2017.
 */

public class ProblemDetails extends MarkerInfoWindow {

    private ProblemMarker attachedMarker;
    private RequestQueue requestQueue;
    private Gson gson;

    public ProblemDetails(MapView mapView) {
        super(R.layout.problem_details, mapView);
    }

    private SharedPreferences settings;
    @Override
    public void onOpen(Object item){
        attachedMarker = (ProblemMarker) item;
        settings = getView().getContext().getSharedPreferences("gisUnespSettings", 0);
        TextView title = (TextView) (mView.findViewById(R.id.detailsTitle));
        title.setText(attachedMarker.problemTitle);
        TextView description = (TextView) (mView.findViewById(R.id.detailsDescription));
        description.setText(attachedMarker.problemDescription);
        final TextView voteCountUp = (TextView) (mView.findViewById(R.id.detailsVotesUp));
        voteCountUp.setText(String.valueOf(attachedMarker.votesUp));
        final TextView voteCountDown = (TextView) (mView.findViewById(R.id.detailsVotesDown));
        voteCountDown.setText(String.valueOf(attachedMarker.votesDown));


        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        requestQueue = Volley.newRequestQueue(getView().getContext());

        if (attachedMarker != null){
            if (attachedMarker.problemTitle != null)
                title.setText(attachedMarker.problemTitle);

            if (attachedMarker.problemDescription != null)
                title.setText(attachedMarker.problemDescription);

            voteCountUp.setText(valueOf(attachedMarker.votesUp));
            voteCountDown.setText(valueOf(attachedMarker.votesDown));

        }

            title.setText(attachedMarker.problemTitle);

        String problemParam = toString().valueOf(attachedMarker.problemId);
        final String endpoint = "http://104.236.55.88:8000/api/problema/"+problemParam+"/confirmacao";

        Button voteUp = (Button) (mView.findViewById(R.id.detailsVoteUpButton));
        Button voteDown = (Button) (mView.findViewById(R.id.detailsVoteDownButton));
        Button solvedBtn = (Button) (mView.findViewById(R.id.resolver_button));
        TextView voteCountYes = (TextView) (mView.findViewById(R.id.detailsVotesUp));
        TextView voteCountNo = (TextView) (mView.findViewById(R.id.detailsVotesDown));

        if (settings.getInt("userId",0) == 0){
           voteUp.setVisibility(View.INVISIBLE);
           voteDown.setVisibility(View.INVISIBLE);
           voteCountYes.setVisibility(View.INVISIBLE);
           voteCountNo.setVisibility(View.INVISIBLE);
           solvedBtn.setVisibility(View.INVISIBLE);
        }
        else{
            if (settings.getString("userType", "").equals("comum")) {
                voteUp.setVisibility(View.VISIBLE);
                voteDown.setVisibility(View.VISIBLE);
                voteCountYes.setVisibility(View.VISIBLE);
                voteCountNo.setVisibility(View.VISIBLE);
                solvedBtn.setVisibility(View.INVISIBLE);
            }
            else{
                voteUp.setVisibility(View.INVISIBLE);
                voteDown.setVisibility(View.INVISIBLE);
                voteCountYes.setVisibility(View.VISIBLE);
                voteCountNo.setVisibility(View.VISIBLE);
                solvedBtn.setVisibility(View.VISIBLE);
            }

        }

        voteUp.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mandarVoto(1, endpoint);

            }
        });

        voteDown.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                mandarVoto(2, endpoint);
            }
        });

        solvedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mandarVoto(3, endpoint);
            }
        });


    }

    public void mandarVoto (final int voto, String endpoint){
        final SharedPreferences settings = getView().getContext().getSharedPreferences("gisUnespSettings", 0);
        StringRequest request = new StringRequest(Request.Method.POST, endpoint, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {


                JsonElement parsedResponse = new JsonParser().parse(response);
                JsonObject dataObject = parsedResponse.getAsJsonObject();
                JsonArray problemJSON = dataObject.getAsJsonArray("data");

                Problem[] updatedProblem = gson.fromJson(problemJSON, Problem[].class);
                final TextView voteCountUp = (TextView) (mView.findViewById(R.id.detailsVotesUp));
                voteCountUp.setText(String.valueOf(updatedProblem[0].votos_pos));
                final TextView voteCountDown = (TextView) (mView.findViewById(R.id.detailsVotesDown));
                voteCountDown.setText(String.valueOf(updatedProblem[0].votos_neg));

                Context context = getView().getContext();
                CharSequence text = "Voto Computado!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override

            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                params.put("tipo_confirmacao", toString().valueOf(voto));

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Accept","application/json");
                params.put("Authorization","Bearer"+" "+settings.getString("userToken",""));
                return params;
            }
        };


        requestQueue.add(request);



    }


    public void fetchVotes (String endpoint) {
        StringRequest request = new StringRequest(Request.Method.GET, endpoint, onFetchSuccess, onFetchError);
        requestQueue.add(request);
    }

    private final Response.Listener<String> onFetchSuccess = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            /*Fazer parsing do JSON*/
            JsonElement parsedResponse = new JsonParser().parse(response);
            JsonObject dataObject = parsedResponse.getAsJsonObject();

        }
    };

    private final Response.ErrorListener onFetchError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }
    };

}
