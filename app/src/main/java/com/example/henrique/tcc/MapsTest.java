package com.example.henrique.tcc;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/*import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;*/

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

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.Arrays;
import java.util.List;


public class MapsTest extends FragmentActivity{

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private MapView mMap;
    private MapController OsmC;
    private FusedLocationProviderClient locationClient;
    private RequestQueue requestQueue;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //Implementação em OpenStreetMaps
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_test);

        final Button addProblemButton = (Button) findViewById(R.id.addProblem);
        addProblemButton.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                OnAddProblem();
            }
        });
        //Checando permissões

        //Armazenamento, para as tiles poderem carregar
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {


            } else {

               ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);


            }
        }


        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {


            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);


            }
        }

        else {
            PrepareMap();
        }
        //Preparando a fila de requisições ao DB
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson= gsonBuilder.create();
        requestQueue = Volley.newRequestQueue(this);
        GetMarkersDB();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        return;
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        PrepareMap();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void PrepareMap ()
    {
        mMap = (MapView) findViewById(R.id.mapaPrincipal);
        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setBuiltInZoomControls(false);
        mMap.setMultiTouchControls(true);
        OsmC = (MapController)mMap.getController();
        OsmC.setZoom(100);


        GeoPoint startPoint = new GeoPoint(-22.346106, -49.034391);
        OsmC.animateTo(startPoint);
        AddMarker(startPoint);
    }

    public void AddMarker(GeoPoint ponto){
        Marker newMarker = new Marker(mMap);
        newMarker.setPosition(ponto);
        newMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        mMap.getOverlays().clear();
        mMap.getOverlays().add(newMarker);
        mMap.invalidate();

    }

    //Função que montará a string para fazer a consulta ao serviço
    public void GetMarkersDB(){
        //Montar a URL de consulta ao serviço
        //Chamar a função fetchProblems, mandando o endpoint

        /*String endpoint;
        fetchProblems(endpoint);*/

    }

    //Funcção que usará componentes do Volley para fazer um request ao serviço
    private void fetchProblems(String endpoint){
        //Método GET usado como exemplo, suporta outros
        StringRequest request = new StringRequest(Request.Method.GET, endpoint, onProblemsLoaded, onFetchError);
        requestQueue.add(request);
    }

    //Callback de fetchProblems, caso dê tudo certo
    private final Response.Listener<String> onProblemsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            /*Fazer parsing do JSON*/
            /*chamar a função de adicionar marcador para cada ponto encotnrado*/
            /*List<Problem> problems = Arrays.asList(gson.fromJson(response, Problem[].class));

            for (Problem problem : problems) {
            }  */
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
    public void OnAddProblem (){

        LayoutInflater layoutInflater = LayoutInflater.from(MapsTest.this);
        View promptView = layoutInflater.inflate(R.layout.form_fragment, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsTest.this);
        alertDialogBuilder.setView(promptView);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Os dados devem ser pegos aqui
                        /*GeoPoint userMarker = new GeoPoint();*/
                        dialog.cancel();
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


