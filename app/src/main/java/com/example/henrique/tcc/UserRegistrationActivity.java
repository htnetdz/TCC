package com.example.henrique.tcc;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
/*Classe responsável por processar o registro de usuário
* Ao contrário da atividade de Login, não é baseada numa atividade pronta do Android Studio*/
public class UserRegistrationActivity extends AppCompatActivity {

    private RequestQueue requestQueue;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button signUpButton = (Button) findViewById(R.id.sendNewUserButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Cada campo do formulátio é uma String deste vetor
                String[] inputForm = new String[5];
                final TextView userNameField = (TextView) findViewById(R.id.userNameField);
                final TextView realNameField = (TextView) findViewById(R.id.realNameField);
                final TextView emailField = (TextView) findViewById(R.id.emailField);
                final TextView newPassField = (TextView) findViewById(R.id.newPassField);
                final TextView repeatPassField = (TextView) findViewById(R.id.repeatPassField);

                inputForm[0] = userNameField.getText().toString();
                inputForm[1] = realNameField.getText().toString();
                inputForm[2] = emailField.getText().toString();
                inputForm[3] = newPassField.getText().toString();
                inputForm[4] = repeatPassField.getText().toString();

                sendUser(inputForm);


            }
        });


    }
    //Função que de fato realiza a requisição
    public void sendUser (final String[] userData )  {
        //Rota da requisição
        String endpoint = "http://104.236.55.88:8000/api/usuarios";
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, endpoint, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                Log.d("RESPOSTA SIGNUP", response.toString());
                Context context = getApplicationContext();
                CharSequence text = "Usuário Cadastrado!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                clearForm();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
        //Parâmetros da requisição
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username", userData[0]);
                params.put("name", userData[1]);
                params.put("email", userData[2]);
                params.put("password", userData[3]);
                return params;
            }
            //Header da Requisição, rota não protegida
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(request);

    }
//Função que limpa o formulario em caso de sucesso
    public void clearForm(){
        TextView userNameField = (TextView) findViewById(R.id.userNameField);
        TextView realNameField = (TextView) findViewById(R.id.realNameField);
        TextView emailField = (TextView) findViewById(R.id.emailField);
        TextView newPassField = (TextView) findViewById(R.id.newPassField);
        TextView repeatPassField = (TextView) findViewById(R.id.repeatPassField);

        userNameField.setText("");
        realNameField.setText("");
        emailField.setText("");
        newPassField.setText("");
        repeatPassField.setText("");
    }

}
