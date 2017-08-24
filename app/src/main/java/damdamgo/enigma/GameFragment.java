package damdamgo.enigma;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Poste on 12/02/2016.
 */
public class GameFragment extends Fragment {

    private boolean firstDisplay = false;
    private View rootView;
    private LayoutInflater inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_game, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getInformation();
    }


    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && firstDisplay==false) {
            firstDisplay=true;
        }
    }

    public void getInformation(){

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url ="http://151.80.149.128/engime/tellMeNewGame.php";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String pseudo = sharedPreferences.getString(QuickstartPreferences.PSEUDO,null);
        Map<String, String> postParam= new HashMap<String, String>();
        postParam.put("pseudo", pseudo);
        postParam.put("token", RegistrationIntentService.tokenRecord);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,new JSONObject(postParam),new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Log.w("ee",response.toString());
                LinearLayout parent = (LinearLayout) rootView.findViewById(R.id.conteneur_game);
                parent.removeAllViews();
                try {
                    int result = response.getInt("authorization");
                    switch( result ){
                        case 1 : displaySuccess();
                            break;
                        case 2 : new Enigme(response.getString("enigmeText"),response.getString("enigmeAnswer"),getActivity());
                            break;
                        case 3 : displayQuestionQuatreChoix();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w("ee",error.toString());
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                2000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }


    public void displaySuccess(){
        LinearLayout parent = (LinearLayout) getActivity().findViewById(R.id.conteneur_game);
        LinearLayout child = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.game_success, null);
        parent.addView(child);
    }

    public void displayQuestionQuatreChoix(){

    }
}
