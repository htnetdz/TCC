package com.example.henrique.tcc;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Button;

/*import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;*/

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapsTest extends Activity{

    private MapView mMap;
    private MapController OsmC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

       // Implementação em GoogleMapsAPI
        /* super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_test);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);*/

        //Implementação em OpenStreetMaps
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_test);

        mMap = (MapView) findViewById(R.id.mapaPrincipal);
        mMap.setTileSource(TileSourceFactory.MAPNIK);
        mMap.setBuiltInZoomControls(true);
        mMap.setMultiTouchControls(true);
        OsmC = (MapController)mMap.getController();
        OsmC.setZoom(12);

        GeoPoint start = new GeoPoint( -22.373122, -48.381831);
        OsmC.animateTo(start);

    }

    public void AddMarker(GeoPoint ponto){
        Marker marcador = new Marker(mMap);
        marcador.setPosition(ponto);
        marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        mMap.getOverlays().clear();
        mMap.getOverlays().add(marcador);
        mMap.invalidate();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    /*@Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }*/
}
