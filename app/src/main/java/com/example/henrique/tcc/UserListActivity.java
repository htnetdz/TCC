package com.example.henrique.tcc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/*Classe responsável pela tela de usuário, exibindo e montando as listas necessárias
* Muito parecida com a classe AdminActivity*/
public class UserListActivity extends AppCompatActivity {

    private SharedPreferences settings;
    private RequestQueue requestQueue;
    private Gson gson;
    private int currentUser;
    private List<Problem> problems;

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

        /*Os botões Relatos e Votos da interface devem abrir a mesma view, porém filtradas de
        * maneiras diferentes, usando o ProblemAdapter*/
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

        /*O botão de notificações deve montar a view como uma lista diferente, usando
        o NotifAdapter*/
        final Button notifsButton = (Button) findViewById(R.id.notificationsButton);
        notifsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildNotifList();
            }
        });


    }

    //Filtra a lista dada
    public void filterList (List<Problem> listToFilter, int mode, int user){
        List<Problem> resultList = new ArrayList<Problem>();
        //Filtering by vote, not by problems created by the user
       if (mode == 0){
            /*Se um problema não foi criado pelo usuário logado, porém está nesta lista,
            representa um voto, não um relato*/
           if (listToFilter.isEmpty() == false) {
               for (Problem problem : listToFilter) {

                   if (problem.usuario_id == user)
                       resultList.add(problem);

               }
           }
       }

        if (mode == 1) {
           if (listToFilter.isEmpty() == false) {
               for (Problem problem : listToFilter) {

                   if (problem.usuario_id != user)
                       resultList.add(problem);

               }
           }
       }

        Log.d ("Lista filtrada", resultList.toString());
        final ListView problemList = (ListView) findViewById(R.id.User_Report_List);

        ProblemAdapter adapter = new ProblemAdapter(getApplicationContext(), 0, resultList);
        problemList.setAdapter(adapter);
        problemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object listItem = problemList.getItemAtPosition(position);
                Log.d("clicando", listItem.toString());

            }
        });


    }

    public void buildProblemList(final int userId, int mode){

        final int modeFilter = mode;
        final int userFilter = userId;
        //Esta requisição pede pelo Problem de cada NotificationInfo, via problema_id
        //Popula o título do problema
        String endpoint = "http://104.236.55.88:8000/api/problemas/usuario/";
        StringRequest request = new StringRequest(Request.Method.GET, endpoint, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d("RESPOSTA POST", response.toString());
                /*Fazer parsing do JSON*/
                JsonElement parsedResponse = new JsonParser().parse(response);
                JsonObject dataObject = parsedResponse.getAsJsonObject();
                JsonArray dataArray = dataObject.getAsJsonArray("data");
                problems = Arrays.asList(gson.fromJson(dataArray, Problem[].class));
                filterList(problems, modeFilter, userId);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
        //Headers da requisição, rota protegida
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

    //Requisição neste caso é um método GET, sem parâmetros, o usuário está contido na Token
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

                if (dataObject.isJsonObject()){
                    Log.d("Parsing", dataObject.toString());
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

            //Header da requisição
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
