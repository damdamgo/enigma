package damdamgo.enigma;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //1 pour connexion 2 pour inscription
    private int typeRequest = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ((RelativeLayout)findViewById(R.id.cache_relative)).setVisibility(View.INVISIBLE);


        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));


        ((Button)findViewById(R.id.buttonInscription)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RelativeLayout)findViewById(R.id.cache_relative)).setVisibility(View.VISIBLE);
                typeRequest = 2 ;
                if(checkPlayServices()){
                    View view = MainActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }
                    Intent intent = new Intent(MainActivity.this, RegistrationIntentService.class);
                    startService(intent);
                }
            }
        });
        ((Button)findViewById(R.id.buttonConnexion)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RelativeLayout)findViewById(R.id.cache_relative)).setVisibility(View.VISIBLE);
                typeRequest = 1;
                if(checkPlayServices()){
                    View view = MainActivity.this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
                    }
                    Intent intent = new Intent(MainActivity.this, RegistrationIntentService.class);
                    startService(intent);
                }
            }
        });


    }

   private BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(context);
            boolean sentToken = sharedPreferences
                    .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
            if (sentToken) {
                String passWord = ((EditText)findViewById(R.id.editTextPassWOrd)).getText().toString();
                String pseudo = ((EditText)findViewById(R.id.editTextPseudo)).getText().toString();
                if(typeRequest==2)FirstConnexion(pseudo,passWord);
                else{
                    Connexion(pseudo,passWord);
                }
            } else {
                Toast t = Toast.makeText(MainActivity.this, "un probleme est survenu veuillez verifier votre connexion internet",
                        Toast.LENGTH_LONG);
                t.setGravity(Gravity.TOP,0,0);
                t.show();
            }
        }
    };


    /**
     * on regarde si le mobile possede google play service APK
     * @return
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("probleme", "cette apareil n'est pas supporté");
                finish();
            }
            return false;
        }
        return true;
    }


    public void FirstConnexion(String pseudo,String passWord){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://151.80.149.128/engime/phpInscription.php";

        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("pseudo", pseudo);
        postParam.put("motDePasse", passWord);
        postParam.put("token", RegistrationIntentService.tokenRecord);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,new JSONObject(postParam),new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.w("ee",response.toString());
                try {
                    int authorization = response.getInt("authorization");
                    resultConnexion(authorization);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

                Toast t = Toast.makeText(MainActivity.this, "un probleme est survenu verifier voter connexion internet",
                        Toast.LENGTH_LONG);
                t.setGravity(Gravity.TOP,0,0);
                t.show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                2000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                2000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);

    }


    public void Connexion(String pseudo,String passWord){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://151.80.149.128/engime/phpConnexion.php";

        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("pseudo", pseudo);
        postParam.put("motDePasse", passWord);
        postParam.put("token", RegistrationIntentService.tokenRecord);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,new JSONObject(postParam),new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.w("ee",response.toString());
                try {
                    int authorization = response.getInt("authorization");
                    resultConnexion(authorization);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast t = Toast.makeText(MainActivity.this, "un probleme est survenu verifier voter connexion internet",
                        Toast.LENGTH_LONG);
                t.setGravity(Gravity.TOP,0,0);
                t.show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                2000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);

    }



    public void resultConnexion(int authorization){
        if(authorization==1){
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String pseudo = ((EditText)findViewById(R.id.editTextPseudo)).getText().toString();
            pseudo=pseudo.trim();
            sharedPreferences.edit().putString(QuickstartPreferences.PSEUDO,pseudo).apply();
            Intent myIntent = new Intent(MainActivity.this, ActivityGame.class);
            MainActivity.this.startActivity(myIntent);
            finish();
        }
        else if(authorization==-2){
            Toast t = Toast.makeText(this, "le pseudo et le mot de passe doivent comporter au moins 5 caracteres et ne pas depasser 30 caracteres",
                    Toast.LENGTH_LONG);
            t.setGravity(Gravity.TOP,0,0);
            t.show();
            ((RelativeLayout)findViewById(R.id.cache_relative)).setVisibility(View.INVISIBLE);
        }
        else if(authorization == -5){
            Toast t = Toast.makeText(this, "impossible de se connecter avec ces informations",
                    Toast.LENGTH_LONG);
            t.setGravity(Gravity.TOP,0,0);
            t.show();
            ((RelativeLayout)findViewById(R.id.cache_relative)).setVisibility(View.INVISIBLE);
        }
        else if(authorization == -1){
            Toast t = Toast.makeText(this, "ce pseudo est deja pris",
                    Toast.LENGTH_LONG);
            t.setGravity(Gravity.TOP,0,0);
            t.show();
            ((RelativeLayout)findViewById(R.id.cache_relative)).setVisibility(View.INVISIBLE);
        }
    }

    public boolean isOk(){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        boolean tokenSend = sharedPreferences
                .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
        String pseudoSend = sharedPreferences
                .getString(QuickstartPreferences.PSEUDO, null);
        if(tokenSend && pseudoSend!=null)return true;
        else return false;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(isOk()){
            Intent myIntent = new Intent(MainActivity.this, ActivityGame.class);
            MainActivity.this.startActivity(myIntent);
            finish();
        }
    }
}

