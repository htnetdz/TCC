package com.example.henrique.tcc;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Henrique on 23/10/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UserNotificationJob extends JobService {

    JobParameters params;
    CustomTask task;
    private RequestQueue requestQueue;
    private Gson gson;
    private SharedPreferences settings;

    @Override
    public boolean onStartJob(JobParameters params) {
        this.params = params;
        Log.d("RUNNING JOB", params.toString());
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        task = new CustomTask();
        task.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }


    private class CustomTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPostExecute (Void aVoid){
            jobFinished(params, true);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Colocar trabalho no background aqui
            Log.d("INSIDE BG TASK", params.toString());
            SharedPreferences settings = getSharedPreferences("gisUnespSettings", 0);
            final String userIdentification;


            userIdentification = "Bearer"+" "+settings.getString("userToken", "");
            getNotifications(userIdentification);

            return null;
        }

        private final Response.Listener<String> onNotifsLoaded = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("notifResponse", response);
                JsonArray notificationJSON = null;
                JsonElement parsedResponse = new JsonParser().parse(response);
                JsonObject dataObject = parsedResponse.getAsJsonObject();

                if (dataObject.isJsonArray()) {
                    notificationJSON = dataObject.getAsJsonArray("data");

                    List<NotificationObject> unreadNotifications = Arrays.asList(gson.fromJson(notificationJSON, NotificationObject[].class));

                    if (unreadNotifications.isEmpty() == false) {
                        for (NotificationObject notificationToProcess : unreadNotifications) {
                            notifyUser(notificationToProcess);
                        }
                    }
                }
            }
        };

        private final Response.ErrorListener onFetchError = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("PostActivity", error.toString());
            }
        };

        protected void getNotifications (final String user){

            Log.i("insideGetnotif", user);
            String endpoint = "http://104.236.55.88:8000/api/usuario/notificacoes";
            settings = getSharedPreferences("gisUnespSettings", 0);

            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
            requestQueue = Volley.newRequestQueue(getApplicationContext());

            StringRequest requestGet = new StringRequest(Request.Method.GET, endpoint, onNotifsLoaded, onFetchError){

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Accept","application/json");
                    params.put("Authorization","Bearer"+" "+settings.getString("userToken",""));
                    return params;
                }
            };
            requestQueue.add(requestGet);



        }

        protected void notifyUser (NotificationObject notification){

            Log.i("insideNotify", notification.toString());
            int tipoVoto = notification.data.tipo_confirmacao;

            //Teste de notificação SEM INTENT
            String mensagem = "";
            if (tipoVoto == 1) {
                mensagem = "como positivo";
            }
            else if (tipoVoto == 2){
                mensagem = "como negativo";
            }
            else if (tipoVoto == 3){
                mensagem = "como resolvido";
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_priority_high_black_24dp)
                            .setContentTitle("Confirmação")
                            .setContentText("Seu relato foi votado "+mensagem+"!");
            NotificationManager mNotificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(notification.data.confirmacao_id, mBuilder.build());

        }
    }
}
