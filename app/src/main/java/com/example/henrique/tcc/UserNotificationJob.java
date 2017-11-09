package com.example.henrique.tcc;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.AsyncTask;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Henrique on 23/10/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UserNotificationJob extends JobService {

    JobParameters params;
    CustomTask task;

    @Override
    public boolean onStartJob(JobParameters params) {
        task = new CustomTask();
        task.execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    private class CustomTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPostExecute (Void aVoid){
            jobFinished(params, false);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            //Colocar trabalho no background aqui
            /*SharedPreferences settings = getSharedPreferences("gisUnespSettings", 0);
            String userIdentification = String.valueOf(settings.getInt("userId",0));*/

            //Teste de notificação SEM INTENT
            /*String mensagem = "";
            if (voto == 1) {
                mensagem = "como positivo";
            }
            else if (voto == 2){
                mensagem = "como negativo";
            }
            else if (voto == 3){
                mensagem = "como resolvido";
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_priority_high_black_24dp)
                            .setContentTitle("Confirmação")
                            .setContentText("Seu relato foi votado "+mensagem+"!");
            NotificationManager mNotificationManager =
                    (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());*/
            return null;
        }
    }
}
