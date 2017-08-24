package damdamgo.enigma;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Poste on 14/02/2016.
 */
public class ClassementGlobalAdapter extends RecyclerView.Adapter<ClassementGlobalAdapter.ViewHolder> {

    private ArrayList<ClassementInfo> classementInfos = new ArrayList<ClassementInfo>();

    public ClassementGlobalAdapter(ArrayList<ClassementInfo> classementInfos) {
        this.classementInfos = classementInfos;
    }

    @Override
    public ClassementGlobalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_classement, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ClassementGlobalAdapter.ViewHolder holder, int position) {
        holder.pseudo.setText(classementInfos.get(position).getPseudo());
        holder.score.setText(classementInfos.get(position).getScore());
        holder.classement.setText(String.valueOf(position+1));
    }

    @Override
    public int getItemCount() {
        return classementInfos.size();
    }

    public void updateArray(ArrayList<ClassementInfo> classementInfos) {
        this.classementInfos=classementInfos;
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView pseudo;
        public TextView score;
        public TextView classement;
        public ViewHolder(View v) {
            super(v);
            pseudo = (TextView)v.findViewById(R.id.textViewPseudoClassement);
            score = (TextView) v.findViewById(R.id.textViewScoreClassement);
            classement = (TextView) v.findViewById(R.id.textViewClassement);
        }

    }



}
