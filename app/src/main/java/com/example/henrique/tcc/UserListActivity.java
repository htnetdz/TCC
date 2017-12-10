package com.example.henrique.tcc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserListActivity extends AppCompatActivity {

    private SharedPreferences settings;
    private RequestQueue requestQueue;
    private Gson gson;
    private int currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settings = getSharedPreferences("gisUnespSettings", 0);
        currentUser = settings.getInt("userId", 0);

        requestQueue = Volley.newRequestQueue(this);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        buildProblemList(currentUser, 0);

        final Button reportsButton = (Button) findViewById(R.id.reportsButton);
        reportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildProblemList(currentUser, 0);
            }
        });

        final Button votesButton = (Button) findViewById(R.id.votesButton);
        votesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildProblemList(currentUser, 1);
            }
        });

        final Button notifsButton = (Button) findViewById(R.id.notificationsButton);
        notifsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildNotifList();
            }
        });


    }

    public List<Problem> filterList (List<Problem> listToFilter, int mode, int user){
        List<Problem> resultList = null;

        //Filtering by vote, not by problems created by the user
       if (mode == 1) {
           if (!(listToFilter.isEmpty())) {
               for (Problem problem : listToFilter) {

                   if (problem.usuario_id != user)
                       resultList.add(problem);

               }
           }
       }


        return resultList;
    }

    public void buildProblemList(int userId, int mode){
        final int filterMode = mode;
        final int filterId = userId;
        String endpoint = "http://104.236.55.88:8000/api/problemas/usuario/"+String.valueOf(filterId);
        StringRequest request = new StringRequest(Request.Method.GET, endpoint, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d("RESPOSTA POST", response.toString());
                /*Fazer parsing do JSON*/
                JsonElement parsedResponse = new JsonParser().parse(response);
                JsonObject dataObject = parsedResponse.getAsJsonObject();
                JsonArray dataArray = dataObject.getAsJsonArray("data");
                List<Problem> problems = Arrays.asList(gson.fromJson(dataArray, Problem[].class));
                List<Problem> filteredProblems = problems;

                if (filterMode !=0) {
                  filteredProblems = filterList(problems, filterMode, filterId);
                }

                final ListView problemList = (ListView) findViewById(R.id.User_Report_List);
                if (filteredProblems != null) {
                    Log.d("filtrado", filteredProblems.toString());
                    ProblemAdapter adapter = new ProblemAdapter(getApplicationContext(), 0, filteredProblems);
                    problemList.setAdapter(adapter);
                    problemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Object listItem = problemList.getItemAtPosition(position);
                            Log.d("clicando", listItem.toString());

                        }
                    });
                }
                else{
                    problemList.setAdapter(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

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

    public void buildNotifList(){
        String endpoint = "http://104.236.55.88:8000/api/usuario/notificacoes";
        StringRequest request = new StringRequest(Request.Method.GET, endpoint, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d("RESPOSTA POST", response.toString());
                /*Fazer parsing do JSON*/
                JsonElement parsedResponse = new JsonParser().parse(response);
                JsonObject dataObject = parsedResponse.getAsJsonObject();
                final ListView notifsList = (ListView) findViewById(R.id.User_Report_List);

                if (dataObject.isJsonArray()){
                    JsonArray dataArray = dataObject.getAsJsonArray("data");
                    List<NotificationObject> notifs = Arrays.asList(gson.fromJson(dataArray, NotificationObject[].class));
                    List<NotificationInfo> notifData = new ArrayList<>();


                if (notifs != null) {
                    Log.d("filtrado", notifs.toString());

                    for (NotificationObject notif : notifs){
                        notifData.add(notif.data);
                    }


                    NotifAdapter adapter = new NotifAdapter(getApplicationContext(), 0, notifData);
                    notifsList.setAdapter(adapter);
                    notifsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Object listItem = notifsList.getItemAtPosition(position);
                            Log.d("clicando", listItem.toString());

                        }
                    });
                }
                else{
                    Log.d("notifications", "null");
                    notifsList.setAdapter(null);
                }
            }
            else{
                    Log.d("notifications", "null");
                    notifsList.setAdapter(null);
                }
        }}, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

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
