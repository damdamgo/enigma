package damdamgo.enigma;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by Poste on 19/02/2016.
 */
public class SplashScreen extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private BroadcastReceiver connexionReceiver;
    private boolean autorization = false;
    private Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("une connexion internet est necessaire pour continuer")
                .setTitle("erreur")
                .setCancelable(false);
        dialog = builder.create();


        IntentFilter filtreConnectivity = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");

        connexionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = cm.getActiveNetworkInfo();
                Log.w("erzerze","eeeeeeeeeeeeeeee");
                if(info!=null){
                    Log.w("erzerze","eeeeeazaaaaaaeeeeee");
                    if(info.isConnected()==true){
                        Log.w("ee","is connecter");
                        autorization=true;
                        dialog.cancel();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                unregisterReceiver(connexionReceiver);
                                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }, SPLASH_TIME_OUT);
                    }
                    else{
                        dialog.show();
                    }
                }
                else{
                    dialog.show();
                }
            }
        };
        registerReceiver(connexionReceiver,filtreConnectivity);
    }
}
