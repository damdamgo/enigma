package damdamgo.enigma;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Poste on 12/02/2016.
 */
public class ClassementGlobal extends Fragment {

    private RecyclerView mRecyclerView;
    private ClassementGlobalAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ClassementInfo> classementInfos = new ArrayList<ClassementInfo>();
    private boolean firstDisplay = false;
    private View root;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        attention fragment_classement_general doit avoir comme parent le recycle view il
        ne doit pas etre un enfant sinon error The specified child already has a parent. You must call removeView() on the child's parent first
        c'est pour ca u'il faut faire un include
        */
        View rootView = inflater.inflate(R.layout.fragment_classement_general, container, false);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_classement_general);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new ClassementGlobalAdapter(classementInfos);
        mRecyclerView.setAdapter(mAdapter);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) (rootView).findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getInformation();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    root = rootView;
    return rootView;
}

    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible && firstDisplay==false) {
            firstDisplay=true;
            getInformation();
        }
        if(visible){
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public void getInformation(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url ="http://151.80.149.128/engime/phpgetClassementGeneral.php";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,url,new JSONObject(),new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("tableau");
                    Log.w("eee",response.toString());
                    classementInfos = new ArrayList<ClassementInfo>();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        classementInfos.add(new ClassementInfo(jsonObject.getString("pseudo"),jsonObject.getString("score")));
                    }
                    mAdapter.updateArray(classementInfos);
                    root.findViewById(R.id.linearProgressData).setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast t = Toast.makeText(getActivity(), "un probleme est survenu verifier voter connexion internet",
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

}
