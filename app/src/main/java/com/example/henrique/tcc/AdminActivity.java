package com.example.henrique.tcc;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

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
    private List<Problem> problems;
    private String callingList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

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

        final Button showDataButton = (Button) findViewById(R.id.reportsButton);
        showDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapList("reports");
            }
        });

        requestQueue = Volley.newRequestQueue(this);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        problems = null;
        BuildList("votes");


    }

    public void swapList (String viewToShow){
        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.adminviewswitcher);

        if (viewToShow.equalsIgnoreCase("problems")) {
            if (switcher.getCurrentView() == findViewById(R.id.reportsLayout)) {
                Log.d("listaproblemas", "in");
                switcher.showPrevious();
            }
        }
        if (viewToShow.equalsIgnoreCase("reports")) {
            if (switcher.getCurrentView() == findViewById(R.id.problemList)) {
                Log.d("relatorios", "in");
                switcher.showNext();
            }
        }


    }

    public void generateData () {
        int totalProblems = problems.size();
        int numberSolved= 0;
        int[] types = {0,0,0,0};
        int maxType = 0;

        for (Problem eachProblem : problems){
            //Define de qual tipo é o problema, aumenta o contador daquele tipo
            /*types[eachProblem.tipo_problema_id-1]++;

            //Define qual o tipo com maior número de problemas
            if (types[eachProblem.tipo_problema_id-1] > maxType){
                maxType = eachProblem.problema_id-1;
            }*/

            //Define quantos problemas estão resolvidos
            if(eachProblem.resolvido){
                numberSolved++;
            }
        }

        //Escrevendo os Relatórios
        TextView total = (TextView) findViewById(R.id.problemNumberValue);
        TextView solved = (TextView) findViewById(R.id.solvedNumberValue);
        TextView solvedPercent = (TextView) findViewById(R.id.solvedNumberPercent);
        TextView type = (TextView) findViewById(R.id.numberTypeValue);

        Double percent = ((numberSolved*1.0)/(totalProblems*1.0))*100;

        total.setText(String.valueOf(totalProblems));
        solved.setText(String.valueOf(numberSolved));
        solvedPercent.setText("("+String.valueOf(percent)+"%"+")");

        /*if (maxType ==0){
            type.setText("Acesso");
        }
        if (maxType == 1){
            type.setText("Água e Abastecimento");
        }
        if (maxType == 2){
            type.setText("Luz e Equipamentos Elétricos");
        }
        if (maxType == 3){
            type.setText("Segurança");
        }*/

    }

    public void BuildList (String orderBy){
        ListView problemList = (ListView) findViewById(R.id.problemList);
        problemList.setAdapter(null);
        String endpoint = null;
        if (orderBy == "votes"){
            callingList = "problems";
           endpoint = "http://104.236.55.88:8000/api/problemas?order_votos_pos=true";
        }
        if (orderBy == "older"){
            callingList = "problems";
            endpoint = "http://104.236.55.88:8000/api/problemas?order_antigos=true";
        }
        fetchProblems(endpoint);
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

            /*Fazer parsing do JSON*/
            JsonElement parsedResponse = new JsonParser().parse(response);
            JsonObject dataObject = parsedResponse.getAsJsonObject();
            JsonArray dataArray = dataObject.getAsJsonArray("data");

            /*chamar a função de adicionar marcador para cada ponto encontrado*/
            problems = Arrays.asList(gson.fromJson(dataArray, Problem[].class));
            ListView problemList = (ListView) findViewById(R.id.problemList);
            ProblemAdapter adapter = new ProblemAdapter(getApplicationContext(),0, problems);
            problemList.setAdapter(adapter);
            problemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });

            Log.d("Adapter status", adapter.toString());
            Log.d("Lista de problemas", String.valueOf(problems.isEmpty()));

            if(problems.isEmpty() == false)
                generateData();

            swapList(callingList);

        }
    };

    //Callback de fetchProblems, caso dê algo errado
    private final Response.ErrorListener onFetchError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }
    };

    public void OpenProblemDetails (Problem toDetail){

    }

}
