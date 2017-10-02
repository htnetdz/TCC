package com.example.henrique.tcc;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    private Bundle loginResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    loginResponse = new Bundle();
    loginResponse.putString("UserName", null);
    loginResponse.putInt("UserType", 0);

    final Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult (i, 1, loginResponse);
            }
        });

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
