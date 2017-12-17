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
import android.graphics.Typeface;
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

/*Atividade principal, rodando assim que o aplicativo é carregado*/
public class MainActivity extends AppCompatActivity {

    //Instância de SharedPreferences para consultar usuário logado anteriormente
    private SharedPreferences settings;

    //Constantes para os pedidos de permissão do sistema
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences("gisUnespSettings", 0);

        TextView topText = (TextView) findViewById(R.id.sample_text);
        //Define o texto de boas vindas caso o usuário esteja logado anteriormente
        topText.setText("Bem vindo "+settings.getString("userName","ao GISUnesp"));

        Button loginButton = (Button) findViewById(R.id.loginButton);

        //Se há usuário logado ou não, exibir os botões apropriados
        if (settings.getInt("userId", 0) == 0){
            logOutUiChanges();
        }
        else{
            logInUiChanges();
        }

    //As permissões e conexões devem estar me ordem para exibir o mapa
    Button mapButton = (Button) findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
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

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    //Depois de receber a resposta de Login, ajustar interface e registrar job de notificações
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            Log.d("LOGINSUCESS", "Dentro de onActvivity result");
            logInUiChanges();
           registerJob();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    /*Manipula uma instância de JobInfo que usará uma instância da classe UserNotificationJob
     * para processar as notificações do sistema, geradas pelas interações de outros usuários */
    public void registerJob (){


        Log.i("REGISTERINGJOB", "Registrando o job");
        JobScheduler jobScheduler = (JobScheduler) this.getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(this,UserNotificationJob.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            JobInfo jobInfo =  new JobInfo.Builder(1,componentName)
                    .setPeriodic(180000)
                    .setBackoffCriteria(3000, JobInfo.BACKOFF_POLICY_LINEAR)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            jobScheduler.schedule(jobInfo);

            Log.i("REGISTERINGJOB", "Job Registrado");
            }

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override protected void onResume() {

        super.onResume();
        settings = getSharedPreferences("gisUnespSettings", 0);

        Log.d("On Resume", settings.getAll().toString());

        //Qaundo a Tela Principal é recarregada, deve-se checar o usuário para ajusta-la
        TextView topText = (TextView) findViewById(R.id.sample_text);
        Typeface face=Typeface.createFromAsset(getAssets(),"fonts/SwissBold.ttf");
        topText.setTypeface(face);

        if (settings.getInt("userId", 0) != 0)
            logInUiChanges();
    }

    //Função usada para parar o job criado, assim que o usuário fizer logout
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void unregisterJob(){
        JobScheduler jobScheduler = (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(1);
    }

    //Chamada sempre que a interface precisa mudar baseada no usuário logado
    public void logInUiChanges(){

        setContentView(R.layout.activity_main);

        //Mudar texto do botão de login
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setText("Log Out");
        String userType = settings.getString("userType", "");
        Log.d("tipousuario", userType);

        //Botões usados apenas por usuário logados
        Button adminButton = (Button) findViewById(R.id.adminButton);
        TextView adminHelp = (TextView) findViewById(R.id.adminAreaButtonHelp);
        Button userButton = (Button) findViewById(R.id.userButton);
        TextView userHelp = (TextView) findViewById(R.id.userAreaButtonHelp);

        boolean userState = userType.equalsIgnoreCase("comum");
            //Se usuário é administrados
            if (userType.equalsIgnoreCase("admin")) {
                Log.d("loginuichanges admin","");

                //Botão da área do administrador deve estar disponível
                adminButton.setVisibility(View.VISIBLE);
                adminButton.setOnClickListener(new View.OnClickListener(){
                    public void onClick (View v){
                        Intent i = new Intent(getApplicationContext(), AdminActivity.class);
                        startActivity (i);
                    }
                });
                adminHelp.setVisibility(View.VISIBLE);
                adminButton.invalidate();

                //Botão da área de usuário comum deve estar indisponível
                userButton.setVisibility(View.INVISIBLE);
                adminHelp.invalidate();
                userHelp.setVisibility(View.INVISIBLE);
                userHelp.invalidate();
                userButton.invalidate();
            }
            //Se usuário é do tipo comum
            else if (userType.equalsIgnoreCase("comum")) {
                Log.d("loginuichanges comum","");

                //Botão de área do administrador deve estar indusponível
                adminButton.setVisibility(View.INVISIBLE);
                adminButton.invalidate();
                adminHelp.setVisibility(View.INVISIBLE);

                //Botão de área do usuário deve estar disponível
                userButton.setVisibility(View.VISIBLE);
                adminHelp.invalidate();
                userHelp.invalidate();
                userButton.setVisibility(View.VISIBLE);
                userButton.setOnClickListener(new View.OnClickListener(){
                    public void onClick (View v){
                        Intent i = new Intent(getApplicationContext(), UserListActivity.class);
                        startActivity (i);
                    }
                });
                userButton.invalidate();
            }

        //Botão de mapa sempre deve estar com sua chamada correta
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
    //Ao fazer logout, chamar esta função
    public void logOutUiChanges(){

        //Editar as preferências, tornar tudo nulo
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userName", null);
        editor.putString("userToken", null);
        editor.putString("userType", null);
        editor.putInt("userId", 0);
        editor.commit();


        TextView adminHelp = (TextView) findViewById(R.id.adminAreaButtonHelp);
        TextView userHelp = (TextView) findViewById(R.id.userAreaButtonHelp);

        //Cancelar o job de notificações
        unregisterJob();


        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setText("Log In");
        loginButton.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick (View v){
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult (i, 1);
            }
        });

        //Tornar os botões que necessitam de autenticação invisíveis, e mudar o texto de bem vindo
        Button adminButton = (Button) findViewById(R.id.adminButton);
        Button userButton = (Button) findViewById(R.id.userButton);
        adminButton.setVisibility(View.INVISIBLE);
        adminButton.invalidate();
        userButton.setVisibility(View.INVISIBLE);
        userButton.invalidate();
        userHelp.setVisibility(View.INVISIBLE);
        userHelp.invalidate();
        adminHelp.setVisibility(View.INVISIBLE);
        adminHelp.invalidate();
        TextView topText = (TextView) findViewById(R.id.sample_text);
        topText.setText("Bem vindo ao GISUnesp");

    }

    //O mapa não deve ser carregado sem as devidas permissões
    private void CheckPermissionMain(){

        //Checando permissão de armazenamento, para o cache das tiles do mapa
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {


            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);


            }
        }

        //Permissões de acesso a localização
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

    //O mapa necessita de rede e GPS para ser carregado, esta função checa ambos
    private void CheckConnectionsMain(){


        LocationManager gpsStatus = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netStatus = manager.getActiveNetworkInfo();

        //Checando e pedindo por GPS
        if(!gpsStatus.isProviderEnabled(LocationManager.GPS_PROVIDER) && !gpsStatus.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
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

        //Checando por conexão com a internet
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
