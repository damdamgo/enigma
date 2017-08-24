package damdamgo.enigma;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Poste on 17/02/2016.
 */
public class Enigme implements Jeu {

    private String answer;
    private String enigme;
    private LinearLayout parent;
    private LinearLayout editLayout;
    private Activity activity;

    public Enigme(String enigme, String answer, Activity activity){
        this.answer = answer;
        this.enigme = enigme;
        this.activity = activity;
        displayView(activity);
    }

    public void displayView(final Activity activity){
        parent = (LinearLayout) activity.findViewById(R.id.conteneur_game);
        LinearLayout child = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.layout_enigme, null);
        TextView textView = (TextView) child.findViewById(R.id.textViewEnigmeText);
        textView.setText(enigme);
        LinearLayout button = (LinearLayout) child.findViewById(R.id.button_check_answer);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String answerJoueur = ((EditText)activity.findViewById(R.id.editTextEnigme)).getText().toString();
                isGoodAnswer(answerJoueur);
            }
        });
        editLayout = (LinearLayout)child.findViewById(R.id.layout_edit) ;
        parent.addView(child);
    }

    public void isGoodAnswer(String answerJoueur) {
        final boolean accept = check(answerJoueur);
        if(accept){
            View view = activity.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
            LinearLayout child = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.game_success, null);
            parent.removeAllViews();
            parent.addView(child);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
            String pseudo = sharedPreferences.getString(QuickstartPreferences.PSEUDO,null);
            RequestQueue queue = Volley.newRequestQueue(activity);
            String url ="http://151.80.149.128/engime/phpBonneReponse.php";
            Log.w("eee",pseudo);
            Map<String, String> postParam= new HashMap<String, String>();
            postParam.put("pseudo", pseudo);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,new JSONObject(postParam),new Response.Listener<JSONObject>(){
                @Override
                public void onResponse(JSONObject response) {
                    Log.w("ee",response.toString());
                }
            },new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast t = Toast.makeText(activity, "un probleme est survenu verifier voter connexion internet",
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
        else{

            TranslateAnimation translateAnimation = new TranslateAnimation(0.0f,50.0f,0.0f,0.0f);
            translateAnimation.setDuration(200);
            translateAnimation.setRepeatCount(5);
            translateAnimation.setRepeatMode(2);
            translateAnimation.setFillAfter(true);
            editLayout.startAnimation(translateAnimation);
            ((EditText)editLayout.findViewById(R.id.editTextEnigme)).setText("");
            Toast t = Toast.makeText(activity, "verifier bien l'orthographe",
                    Toast.LENGTH_LONG);
            t.setGravity(Gravity.TOP,0,0);
            t.show();
        }
    }

    private boolean check(String answerJoueur){
        if(answerJoueur.length()==0)return false;
        String good = answer;
        String answer = answerJoueur;
        //mettre en minuscule
        answer= answer.toLowerCase();
        //enleverles accents
        answer =  Normalizer.normalize(answer, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        //enlever les espaces debut et fin
        answer = answer.trim();
        //enlever les un la le des
        answer=answer.replaceAll("^(la |le |un |les |des |une |du )", "");
        //enlever les espaces debut et fin
        answer = answer.trim();
        //enlever les s à la fin
        if(answer.charAt(answer.length()-1)=='s')answer=answer.substring(0,answer.length()-1);
        //lire les lettre si il oublie une lettre ca passe ou deux
        //exemple il ecrit arbe au lieu de arbre ca passe
        boolean acceptFinal = true;
        //permet de fixer le nombre de lettre oublie
        int indWrong = 0;
        int indG=0;
        int indA=0;
        int ind = 0;
        int nbLong;
        int checkG,checkA;
        boolean answerMoreLong=false;
        if(answer.length()>good.length()){
            nbLong = answer.length()-1;
            answerMoreLong=true;
        }
        else nbLong =good.length()-1;
        while(ind<=nbLong){
            if(answer.length()<=indA+1)break;
            if(good.length()<=indG+1)break;
            if(answer.charAt(indA)==good.charAt(indG)){
                indG++;
                indA++;
                ind++;
            }
            else{
                indWrong++;
                if(good.charAt(indG+1)==answer.charAt(indA)){
                    indG+=2;
                    indA++;
                    if(answerMoreLong)ind++;
                    else ind+=2;
                }
                else if(good.charAt(indG)==answer.charAt(indA+1)){
                    indG++;
                    indA+=2;
                    if(answerMoreLong)ind+=2;
                    else ind++;
                }
                else if(good.charAt(indG+1)==answer.charAt(indA+1)){
                    indG+=2;
                    indA+=2;
                    if(answerMoreLong)ind+=2;
                    else ind+=2;
                }
                else if(good.charAt(indG+1)!=answer.charAt(indA)){
                    indA++;
                    if(answerMoreLong)ind++;
                }
            }
        }
        if(answer.charAt(indA)==good.charAt(indG))indG++;
        indWrong = indWrong+(Math.abs(good.length()-1-indG));
        if(indWrong<3)return true;
        else return false;
    }
}
