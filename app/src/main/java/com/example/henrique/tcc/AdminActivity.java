package com.example.henrique.tcc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

import org.osmdroid.util.GeoPoint;

import java.util.Arrays;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private Gson gson;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        //Instância da lista
        ListView problemList = (ListView) findViewById(R.id.problemList);


        //Botão para organizar por melhor votados
        final Button sortByVoteButton = (Button) findViewById(R.id.sortByVoteButton);
        sortByVoteButton.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                BuildList("votes");
            }
        });

        //Botão para organizar por mais antigos
        final Button sortByOlderButton = (Button) findViewById(R.id.sortByOlderButton);
        sortByOlderButton.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                BuildList("older");
            }
        });

    }

    public void BuildList (String orderBy){

        String endpoint;
        if (orderBy == "votes"){
           // endpoint = /*INSERIR ENDPOINT AQUI*/;
        }
        if (orderBy == "older"){
            // endpoint = /*INSERIR ENDPOINT AQUI*/;
        }
        //fetchProblems(endpoint);
    }

    private void fetchProblems(String endpoint){
        //Método GET usado como exemplo, suporta outros
        StringRequest request = new StringRequest(Request.Method.GET, endpoint, onProblemsLoaded, onFetchError);
        requestQueue.add(request);
    }

    //Callback de fetchProblems, caso dê tudo certo
    private final Response.Listener<String> onProblemsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.i("resposta",response.toString());
            GeoPoint point;
            /*Fazer parsing do JSON*/
            JsonElement parsedResponse = new JsonParser().parse(response);
            JsonObject dataObject = parsedResponse.getAsJsonObject();
            JsonArray dataArray = dataObject.getAsJsonArray("data");

            /*chamar a função de adicionar marcador para cada ponto encontrado*/
            List<Problem> problems = Arrays.asList(gson.fromJson(dataArray, Problem[].class));
            ProblemAdapter adapter = new ProblemAdapter(getApplicationContext(),0, problems);



            Log.d("Lista de problemas", String.valueOf(problems));


        }
    };

    //Callback de fetchProblems, caso dê algo errado
    private final Response.ErrorListener onFetchError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }
    };

}
