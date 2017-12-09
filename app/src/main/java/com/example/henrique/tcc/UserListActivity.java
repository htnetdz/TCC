package com.example.henrique.tcc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
        BuildList(currentUser, 0);

        final Button reportsButton = (Button) findViewById(R.id.reportsButton);
        reportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuildList(currentUser, 0);
            }
        });

        final Button votesButton = (Button) findViewById(R.id.votesButton);
        reportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuildList(currentUser, 1);
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

    public void BuildList(int userId, int mode){
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
                  Log.d("filtrado", filteredProblems.toString());
                }

                final ListView problemList = (ListView) findViewById(R.id.User_Report_List);
                if (filteredProblems != null) {
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



}
