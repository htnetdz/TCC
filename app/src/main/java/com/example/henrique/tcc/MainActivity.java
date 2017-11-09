package com.example.henrique.tcc;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences("gisUnespSettings", 0);
        TextView topText = (TextView) findViewById(R.id.sample_text);
        topText.setText("Bem vindo, "+settings.getString("userName","ao GISUnesp"));

        Button loginButton = (Button) findViewById(R.id.loginButton);
        if (settings.getInt("userId", 0) == 0){

            loginButton.setText("Log In");
            loginButton.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick (View v){
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult (i,1);
            }
            });

        }
        else{
            loginButton.setText("Log Out");
            loginButton.setOnClickListener(new View.OnClickListener(){
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                public void onClick (View v){
                    logOutUiChanges();
                }
            });
        }

    final Button mapButton = (Button) findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity (i);
            }
        });

    final Button adminButton = (Button) findViewById(R.id.adminButton);
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
        if (resultCode == RESULT_OK){
            logInUiChanges();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void registerJob (int user){
        if (user != 0){
        JobScheduler jobScheduler = (JobScheduler) getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(getApplicationContext(),UserNotificationJob.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            JobInfo jobInfo =  new JobInfo.Builder(1,componentName)
                    /*.setPeriodic(180000)*/
                    .setPeriodic(60000)
                    .setBackoffCriteria(30000, JobInfo.BACKOFF_POLICY_EXPONENTIAL)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .build();
            jobScheduler.schedule(jobInfo);
            }
        }
    }

    public void logInUiChanges(){
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setText("Log Out");

        loginButton.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick (View v){
                logOutUiChanges();
            }
        });
        TextView topText = (TextView) findViewById(R.id.sample_text);
        topText.setText("Bem vindo, "+settings.getString("userName","ao GISUnesp"));
    }

    public void logOutUiChanges(){
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("userName", null);
        editor.putInt("userId", 0);
        editor.commit();

        TextView topText = (TextView) findViewById(R.id.sample_text);
        topText.setText("Bem vindo, "+settings.getString("userName","ao GISUnesp"));


        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setText("Log In");
        loginButton.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onClick (View v){
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult (i, 1);
            }
        });
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
