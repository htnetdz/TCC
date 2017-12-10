package com.example.henrique.tcc;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.List;
import java.util.Map;

/**
 * Created by Henrique on 10/12/2017.
 */

public class NotifAdapter extends ArrayAdapter<NotificationInfo>{

    private String title;
    private RequestQueue requestQueue;
    private Gson gson;
    private SharedPreferences settings;
        public NotifAdapter(Context context, int resource, List objects) {
            super(context, 0, objects);
        }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        requestQueue = Volley.newRequestQueue(getContext());
        settings = getContext().getSharedPreferences("gisUnespSettings", 0);

        NotificationInfo notif  = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.problem_item, parent, false);
        }
        // Lookup view for data population

        getProblem(notif, convertView);
        // Populate the data into the template view using the data object


        // Return the completed view to render on screen
        return convertView;
    }

    public void getProblem(final NotificationInfo notif, View view){

        final NotificationInfo referenceNotif = notif;
        final View referenceView = view;
        String endpoint = "http://104.236.55.88:8000/api/problema/"+String.valueOf(notif.problema_id);
        StringRequest request = new StringRequest(Request.Method.GET, endpoint, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d("getProblems", response);
                JsonElement parsedResponse = new JsonParser().parse(response);
                JsonObject dataObject = parsedResponse.getAsJsonObject();
                JsonArray problemJSON = dataObject.getAsJsonArray("data");
                Problem[] updatedProblem = gson.fromJson(problemJSON, Problem[].class);

                TextView notifTitle = (TextView) referenceView.findViewById(R.id.notifTitle);
                notifTitle.setText(updatedProblem[0].titulo);

                TextView notifType = (TextView) referenceView.findViewById(R.id.notifType);
                if (referenceNotif.tipo_confirmacao == 1){
                    notifType.setText("Sim");
                    notifType.setTextColor(Color.parseColor("#ff669900"));
                }
                else if (referenceNotif.tipo_confirmacao == 2){
                    notifType.setText("Não");
                    notifType.setTextColor(Color.parseColor("#ffcc0000"));
                }

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

}
