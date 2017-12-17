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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private Gson gson;
    private List<Problem> problems;
    private String callingList;

    /*Classe responsável pelo controle do conteúdo e aparência
    * da view activity_admin.xml, que contém content_admin.xml*/
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

        //Botão para mostrar relatórios
        final Button showDataButton = (Button) findViewById(R.id.reportsButton);
        showDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapList("reports");
            }
        });

        //Inicialização de fila de requisições Volley
        requestQueue = Volley.newRequestQueue(this);

        //Incialização de Builder JSON para organização de respostas
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        problems = null;
        BuildList("votes");


    }

    /* Função que ativa a troca de views no ViewSwitcher, presente
     em  content_admin.xml */
    public void swapList (String viewToShow){
        //Instância de ViewSwitcher, só pode suportar duas views internas
        ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.adminviewswitcher);

        //Checagem de qual view está sendo exibida
        if (viewToShow.equalsIgnoreCase("problems")) {
            //Se é a view errada, trocar.
            if (switcher.getCurrentView() == findViewById(R.id.reportsLayout)) {
                Log.d("listaproblemas", "in");
                switcher.showPrevious();
            }
        }
        if (viewToShow.equalsIgnoreCase("reports")) {
            //Se é a view errada, trocar.
            if (switcher.getCurrentView() == findViewById(R.id.problemList)) {
                Log.d("relatorios", "in");
                switcher.showNext();
            }
        }


    }


    /* Conta dados a partir da lista problems, construída na função BuildList()  */
    public void generateData () {

        /*O tamanho da lista de problemas representa quantos problemas estão
        cadastrados no sistema*/
        int totalProblems = problems.size();

        //Número de problemas
        int numberSolved= 0;

        //Há quatro tipos de problemas, por enquanto, por isso um vetor de inteiros conta cada tipo
        int[] types = {0,0,0,0};

        //Qual o índice do vetor de tipos tem o maior número
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
        solvedPercent.setText("("+new BigDecimal(String.valueOf(percent)).setScale(2, BigDecimal.ROUND_HALF_UP)+"%"+")");

        //Definindo o tipo máximo no relatório
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

    /*Popula de maneira já ordenada a ListView em content_admin.xml */
    public void BuildList (String orderBy){
        ListView problemList = (ListView) findViewById(R.id.problemList);
        problemList.setAdapter(null);

        //Definição de qual endpoint usar na requisição
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

    /* Criação da requisição em si */
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

            /* Criar a lista de problemas completa, a partir da classe Problem e da resposta*/
            problems = Arrays.asList(gson.fromJson(dataArray, Problem[].class));
            ListView problemList = (ListView) findViewById(R.id.problemList);

            // Cria e associa à lisa um adapter para mostrar os elementos da mesma
            ProblemAdapter adapter = new ProblemAdapter(getApplicationContext(),0, problems);
            problemList.setAdapter(adapter);
            problemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });

            Log.d("Adapter status", adapter.toString());
            Log.d("Lista de problemas", String.valueOf(problems.isEmpty()));

            //Caso há problemas cadastrados, gerar relatórios
            if(problems.isEmpty() == false)
                generateData();

            //Trocar para o view da lista
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
