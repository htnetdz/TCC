package com.example.henrique.tcc;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/*import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;*/

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*Classe responsável pelo carregamento e exibição do mapa, principal funcionalidade do sistema*/
public class MapsActivity extends FragmentActivity {

    private MapView mMap;
    private MapController OsmC;
     private RequestQueue requestQueue;
    private Gson gson;
    private FusedLocationProviderClient mFusedLocationClient;
    private int id;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        settings = getSharedPreferences("gisUnespSettings", 0);
        id = settings.getInt("userId", 0);
        //Implementação em OpenStreetMaps
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_test);

        //O botão de adicionar problema deve ser exibido apenas a usuários comuns
        final Button addProblemButton = (Button) findViewById(R.id.addProblem);
        if (id !=0 && settings.getString("userType", "").equalsIgnoreCase("comum")){
            addProblemButton.setVisibility(View.VISIBLE);
        }
        addProblemButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onAddProblemClick();
            }
        });



        //Preparando a fila de requisições ao DB
        Log.i("Request", " antes do builder");
        GsonBuilder gsonBuilder = new GsonBuilder();
        Log.i("Request", " depois do builder");
        gson = gsonBuilder.create();
        Log.i("Request", " depois do create");
        requestQueue = Volley.newRequestQueue(this);
        PrepareMap();


    }

    //
    public void PrepareMap() {

        final GeoPoint uniCenter = new GeoPoint(-22.3492696, -49.0326935);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //Instanciando a MapView
        mMap = (MapView) findViewById(R.id.mapaPrincipal);
        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setMaxZoomLevel(18);
        mMap.setMinZoomLevel(16);
        mMap.setBuiltInZoomControls(false);
        mMap.setMultiTouchControls(true);

        //Instanciando o MapController
        OsmC = (MapController) mMap.getController();
        OsmC.setZoom(500);

        // My Location Overlay, para atualizar a localização em tempo real
        final MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(mMap);
        locationOverlay.enableMyLocation();
        locationOverlay.setDrawAccuracyEnabled(true);

        locationOverlay.runOnFirstFix( new Runnable(){
            @Override
            public void run() {
                OsmC.animateTo(uniCenter);
            }
        });

        mMap.getOverlays().add(locationOverlay);

        GetMarkersDB();
    }

    /* Função que adiciona um marcador por vez, associando eles a um índice no overlay
     * de marcadores, e a um problema para que o objeto de detalhes consiga carregar informações
     * ao clique do usuário*/
    public void AddMarker(GeoPoint ponto, Problem problemToMark, int index){

        //Objetos ProblemMarker estão descritos em ProblemMarker.java, possui
        ProblemMarker newMarker = new ProblemMarker(mMap);
        newMarker.setPosition(ponto);
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        //O ìndice 0 deve ser sempre o marcador de posição atual
        Resources res = getResources();
        if (index == 0){
            newMarker.setIcon(res.getDrawable(R.drawable.person));
            newMarker.setInfoWindow(null);
        }
        //Outros índices indicam marcadores de problema, então precisam ser populados com
        // informações de problemas
        else {
            newMarker.setProblemTitle(problemToMark.titulo);
            newMarker.setIcon(res.getDrawable(R.drawable.ic_place_black_24dp));
            newMarker.setProblemDescription(problemToMark.descricao);
            newMarker.setVotesUp(problemToMark.votos_pos);
            newMarker.setVotesDown(problemToMark.votos_neg);
            newMarker.setProblemId(problemToMark.problema_id);
            newMarker.setSolved(problemToMark.resolvido);
            newMarker.setInfoWindow(new ProblemDetails(mMap));
        }
       /* mMap.getOverlays().clear();*/
        mMap.getOverlays().add(index, newMarker);
        mMap.invalidate();


    }

    //Função que montará a string para fazer a consulta ao serviço
    public void GetMarkersDB(){
        //Montar a URL de consulta ao serviço
        //Chamar a função fetchProblems, mandando o endpoint
        Log.i("Request", "Dentro de get markers db");
        String endpoint = "http://104.236.55.88:8000/api/problemas";//ENDPOINT
        Log.i("Request", endpoint);
        fetchProblems(endpoint);

    }

    //Função que usará componentes do Volley para fazer um request ao serviço
    private void fetchProblems(String endpoint){
        StringRequest request = new StringRequest(Request.Method.GET, endpoint, onProblemsLoaded, onFetchError);
        requestQueue.add(request);
    }

    /*Adiciona o problema em si ao banco de dados no backend*/
    public void addProblemDB(final Problem problemToAdd){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        //Tenativa de ler a última localização do sistema
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {

                        //Em caso de sucesso, montar a requisição
                        if (location != null) {
                            String endpoint = "http://104.236.55.88:8000/api/problema"; //ENDPOINT
                            StringRequest request = new StringRequest(Request.Method.POST, endpoint, new Response.Listener<String>(){
                                @Override
                                public void onResponse(String response) {
                                    Log.d("RESPOSTA POST", response.toString());
                                    //No caso de sucesso da resposta, pegar marcadores novamente
                                    GetMarkersDB();

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }){
                                @Override
                                //Parâmetros da requisição
                                protected Map<String,String> getParams(){
                                    Map<String,String> params = new HashMap<String, String>();
                                    params.put("usuario_id", String.valueOf(id));
                                    params.put("tipo_problema_id", toString().valueOf(problemToAdd.tipo_problema_id));
                                    params.put("titulo", problemToAdd.titulo);
                                    params.put("descricao",problemToAdd.descricao);
                                    params.put("resolvido","false");
                                    params.put("lat",String.valueOf(location.getLatitude())); //POSSÍVEIS MUDANÇAS
                                    params.put("lon",String.valueOf(location.getLongitude()));//POSSÍVEIS MUDANÇAS
                                    return params;
                                }

                                @Override
                                //Headers da requisição, é necessária a chave de usuário
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
                });

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


            List<Problem> problems = Arrays.asList(gson.fromJson(dataArray, Problem[].class));
            Log.d("Lista de problemas", String.valueOf(problems.isEmpty()));
            int index = 1;
            if (!(problems.isEmpty())) {
                for (Problem problem : problems) {
                    /*chamar a função de adicionar marcador para cada ponto encontrado*/
                    Log.d("problema", String.valueOf(problem.lat) + ' ' + String.valueOf(problem.lon) + "\nDescrição " + problem.descricao);
                    point = new GeoPoint(problem.lat, problem.lon);
                    AddMarker(point, problem, index);
                    index++;
                }
            }

        }
    };

    //Callback de fetchProblems, caso dê algo errado
    private final Response.ErrorListener onFetchError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("PostActivity", error.toString());
        }
    };

    //Função chamada ao se pressionar o botão de adicionar problema no mapa
    public void onAddProblemClick(){


        LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.form_fragment, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
        alertDialogBuilder.setView(promptView);

        //Spinner de tipos, o índice escolhido determina qual o tipo de probçema
        final Spinner typeSpinner = (Spinner) promptView.findViewById(R.id.selectorTypes);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.problem_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Os dados devem ser pegos aqui
                        /*GeoPoint userMarker = new GeoPoint();*/
                        Problem newProblem = new Problem();
                        EditText titleField = (EditText) promptView.findViewById(R.id.titleProblem);
                        newProblem.titulo = titleField.getText().toString();
                        Log.d("titulo", newProblem.titulo);
                        EditText descriptorField = (EditText) promptView.findViewById(R.id.descriptionProblem);
                        newProblem.descricao = descriptorField.getText().toString();
                        Log.d("Descrição problema ", newProblem.descricao);
                        newProblem.tipo_problema_id = (typeSpinner.getSelectedItemPosition())+1;

                        addProblemDB(newProblem);
                    }
                })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = alertDialogBuilder.create();

        alert.show();




    }

}


