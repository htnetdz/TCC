package com.example.henrique.tcc;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences settings;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences("gisUnespSettings", 0);

        Log.d("Prefeituras", settings.getAll().toString());
        TextView topText = (TextView) findViewById(R.id.sample_text);
        topText.setText("Bem vindo, "+settings.getString("userName","ao GISUnesp"));

        Button loginButton = (Button) findViewById(R.id.loginButton);
        if (settings.getInt("userId", 0) == 0){
            logOutUiChanges();
        }
        else{
            logInUiChanges();
        }

    Button mapButton = (Button) findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                /*Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity (i);*/
               CheckPermissionMain();
            }
        });

    Button adminButton = (Button) findViewById(R.id.adminButton);
        adminButton.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                Intent i = new Intent(getApplicationContext(), AdminActivity.class);
                startActivity (i);
            }
        });


//    // Example of a call to a native method
//    TextView tv = (TextView) findViewById(R.id.sample_text);
//    tv.setText(stringFromJNI());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            Log.d("LOGINSUCESS", "Dentro de onActvivity result");
            logInUiChanges();
           /*registerJob();*/
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void registerJob (){


        Log.i("REGISTERINGJOB", "Registrando o job");
        JobScheduler jobScheduler = (JobScheduler) this.getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(this,UserNotificationJob.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            JobInfo jobInfo =  new JobInfo.Builder(1,componentName)
                    /*.setPeriodic(180000)*/
                    .setPeriodic(10000)
                    .setBackoffCriteria(3000, JobInfo.BACKOFF_POLICY_LINEAR)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            jobScheduler.schedule(jobInfo);
            }

    }

    /*@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override protected void onRestart() {

        super.onRestart();

        settings = getSharedPreferences("gisUnespSettings", 0);

        TextView topText = (TextView) findViewById(R.id.sample_text);
        topText.setText("Bem vindo, "+settings.getString("userName","ao GISUnesp"));

        if (settings.getInt("userId", 0) == 0){
            logOutUiChanges();
        }
        else{
            logInUiChanges();
        }

    }*/

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override protected void onResume() {

        super.onResume();
        settings = getSharedPreferences("gisUnespSettings", 0);

        Log.d("On Resume", settings.getAll().toString());
        TextView topText = (TextView) findViewById(R.id.sample_text);
        topText.setText("Bem vindo, "+settings.getString("userName","ao GISUnesp"));
        logInUiChanges();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void unregisterJob(){
        JobScheduler jobScheduler = (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(1);
    }

    public void logInUiChanges(){

        setContentView(R.layout.activity_main);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setText("Log Out");
        String userType = settings.getString("userType", "");
        Log.d("tipousuario", userType);
        Button adminButton = (Button) findViewById(R.id.adminButton);
        Button userButton = (Button) findViewById(R.id.userButton);
        boolean userState = userType.equalsIgnoreCase("comum");
        Log.d("userState", String.valueOf(userState));

            if (userType.equalsIgnoreCase("admin")) {
                Log.d("loginuichanges admin","");
                adminButton.setVisibility(View.VISIBLE);
                adminButton.setOnClickListener(new View.OnClickListener(){
                    public void onClick (View v){
                        Intent i = new Intent(getApplicationContext(), AdminActivity.class);
                        startActivity (i);
                    }
                });
                adminButton.invalidate();
                userButton.setVisibility(View.INVISIBLE);
                userButton.invalidate();
            }
            else if (userType.equalsIgnoreCase("comum")) {
                Log.d("loginuichanges comum","");
                adminButton.setVisibility(View.INVISIBLE);
                adminButton.invalidate();
                userButton.setVisibility(View.VISIBLE);
                userButton.invalidate();
            }

        Button mapButton = (Button) findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                /*Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity (i);*/
                CheckPermissionMain();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick (View v){
                logOutUiChanges();
            }
        });
        TextView topText = (TextView) findViewById(R.id.sample_text);
        topText.setText("Bem vindo, "+settings.getString("userName","ao GISUnesp"));



    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void logOutUiChanges(){

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userName", null);
        editor.putString("userToken", null);
        editor.putString("userType", null);
        editor.putInt("userId", 0);
        editor.commit();

        /*unregisterJob();*/


        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setText("Log In");
        loginButton.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick (View v){
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult (i, 1);
            }
        });

        Button adminButton = (Button) findViewById(R.id.adminButton);
        Button userButton = (Button) findViewById(R.id.userButton);
        adminButton.setVisibility(View.INVISIBLE);
        adminButton.invalidate();
        userButton.setVisibility(View.INVISIBLE);
        userButton.invalidate();

    }

    private void CheckPermissionMain(){
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
        } else {
            //Checar se há internet ou GPS antes de tentar preparar o mapa
            CheckConnectionsMain();

        }
    }
    private void CheckConnectionsMain(){


        LocationManager gpsStatus = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netStatus = manager.getActiveNetworkInfo();

        //Checando e pedindo por GPS
        if(!gpsStatus.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //Diálogo para pedir GPS
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Falta de GPS");
            builder.setMessage("O GPS parece estar desligado, é necessário ativar para que o mapa seja carregado");
            builder.setPositiveButton("Ativar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Mostra as configurações para que o usuário possa habililtar o GPS
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                }
            });
            builder.setNegativeButton("Agora Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

        else if (netStatus == null || !(netStatus.isConnectedOrConnecting()))
        {
            //Diálogo Para Avisar o Usuário de falta de internet
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Sem Internet");
            builder.setMessage("O Wifi parece estar desligado, é necessário ativar para que o mapa seja carregado");
            builder.setPositiveButton("Ativar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Mostra as configurações para que o usuário possa habililtar o GPS
                    Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(i);
                }
            });
            builder.setNegativeButton("Agora Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
        else{
            Intent i = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity (i);
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
