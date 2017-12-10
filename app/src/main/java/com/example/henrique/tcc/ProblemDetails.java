package com.example.henrique.tcc;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.valueOf;

/**
 * Created by Henrique on 19/09/2017.
 */

public class ProblemDetails extends MarkerInfoWindow {

    private ProblemMarker attachedMarker;
    private Problem[] toUpdate;
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
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        requestQueue = Volley.newRequestQueue(getView().getContext());

        getProblem(attachedMarker.problemId);


    }

    public void prepareDetail(){
        TextView title = (TextView) (mView.findViewById(R.id.detailsTitleLabel));
        title.setText(toUpdate[0].titulo);
        TextView description = (TextView) (mView.findViewById(R.id.detailsDescription));
        description.setText(toUpdate[0].descricao);
        final TextView voteCountUp = (TextView) (mView.findViewById(R.id.detailsVotesUp));
        voteCountUp.setText(String.valueOf(toUpdate[0].votos_pos));
        final TextView voteCountDown = (TextView) (mView.findViewById(R.id.detailsVotesDown));
        voteCountDown.setText(String.valueOf(toUpdate[0].votos_neg));
        final TextView userQuery = (TextView) mView.findViewById(R.id.userQuery);
        final TextView problemStatus = (TextView) mView.findViewById(R.id.solvedDesc);


        if (attachedMarker != null){
            if (toUpdate[0].titulo != null)
                title.setText(attachedMarker.problemTitle);

            if (toUpdate[0].descricao != null)
                title.setText(attachedMarker.problemDescription);

            if(toUpdate[0].resolvido == true){
                userQuery.setText("Este problema foi mesmo resolvido?");
                problemStatus.setText("RESOLVIDO");
                problemStatus.setTextColor(Color.parseColor("#ff669900"));
            }
            else{
                userQuery.setText("Este relato está correto?");
                problemStatus.setText("PENDENTE");
                problemStatus.setTextColor(Color.parseColor("#ffcc0000"));
            }
            voteCountUp.setText(valueOf(toUpdate[0].votos_pos));
            voteCountDown.setText(valueOf(toUpdate[0].votos_neg));

        }

        title.setText(toUpdate[0].titulo);

        String problemParam = toString().valueOf(toUpdate[0].problema_id);
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
            userQuery.setVisibility(View.INVISIBLE);
        }
        else{
            if (settings.getString("userType", "").equals("comum")) {
                voteUp.setVisibility(View.VISIBLE);
                voteDown.setVisibility(View.VISIBLE);
                voteCountYes.setVisibility(View.VISIBLE);
                voteCountNo.setVisibility(View.VISIBLE);
                solvedBtn.setVisibility(View.VISIBLE);
                userQuery.setVisibility(View.VISIBLE);
            }
            else{
                voteUp.setVisibility(View.GONE);
                voteDown.setVisibility(View.GONE);
                voteCountYes.setVisibility(View.VISIBLE);
                voteCountNo.setVisibility(View.VISIBLE);
                solvedBtn.setVisibility(View.VISIBLE);
            }

        }

        voteUp.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                sendVote(1, endpoint);

            }
        });

        voteDown.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                sendVote(2, endpoint);
            }
        });

        solvedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solveProblem(attachedMarker.problemId);
            }
        });
    }


    public void solveProblem (int problemId){
        String solveEndpoint = "http://104.236.55.88:8000/api/problema/"+String.valueOf(problemId)+"/resolve";

        StringRequest request = new StringRequest(Request.Method.PATCH, solveEndpoint,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Do something with the response
                Log.d("Solve Success", response);
                JsonElement parsedResponse = new JsonParser().parse(response);
                JsonObject dataObject = parsedResponse.getAsJsonObject();
                JsonObject jsonProblem= dataObject.getAsJsonObject("data");
                final TextView userQuery = (TextView) mView.findViewById(R.id.userQuery);
                final TextView problemStatus = (TextView) mView.findViewById(R.id.solvedDesc);


                Problem updatedProblem = gson.fromJson(jsonProblem, Problem.class);
                if(updatedProblem.resolvido){
                    userQuery.setText("Este problema foi mesmo resolvido?");
                    problemStatus.setText("RESOLVIDO");
                    problemStatus.setTextColor(Color.parseColor("#ff669900"));
                }
                else{
                    userQuery.setText("Este relato está correto?");
                    problemStatus.setText("PENDENTE");
                    problemStatus.setTextColor(Color.parseColor("#ffcc0000"));
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Log.d("Solve Fail", error.toString());
                    }
                }){
            @Override

            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
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

    public void sendVote(final int voto, String endpoint){
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

    public void getProblem (int id){
        String endpoint = "http://104.236.55.88:8000/api/problema/"+String.valueOf(id);
        StringRequest request = new StringRequest(Request.Method.GET, endpoint, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d("getProblems", response);
                JsonElement parsedResponse = new JsonParser().parse(response);
                JsonObject dataObject = parsedResponse.getAsJsonObject();
                JsonArray problemJSON = dataObject.getAsJsonArray("data");
                Problem[] updatedProblem = gson.fromJson(problemJSON, Problem[].class);

                toUpdate = updatedProblem;
                prepareDetail();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override

            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

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
