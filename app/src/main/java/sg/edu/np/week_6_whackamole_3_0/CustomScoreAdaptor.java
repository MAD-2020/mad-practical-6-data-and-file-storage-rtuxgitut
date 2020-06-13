package sg.edu.np.week_6_whackamole_3_0;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class CustomScoreAdaptor extends RecyclerView.Adapter<CustomScoreViewHolder> {

    UserData userData;
    ArrayList<Integer> difficultyList;
    ArrayList<Integer> pointList;
    Context context;
    private static final String FILENAME = "CustomScoreAdaptor.java";
    private static final String TAG = "Whack-A-Mole3.0!";
    private CustomScoreAdaptor.OnItemClickListener scoreListener;
    static View viewer;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(CustomScoreAdaptor.OnItemClickListener listener){
        this.scoreListener = listener;
    }

    public CustomScoreAdaptor(Context context, UserData userdata){
        this.userData = userdata;
        this.context = context;
        this.difficultyList = userdata.getLevels();
        this.pointList = userdata.getScores();
    }

    public CustomScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        viewer = LayoutInflater.from(parent.getContext()).inflate(R.layout.level_select,null);

        return new CustomScoreViewHolder(viewer, scoreListener);
    }

    public void onBindViewHolder(CustomScoreViewHolder holder, final int position){

        String score = String.valueOf(pointList.get(position));
        String level = String.valueOf(difficultyList.get(position));

        holder.level.setText(level);
        holder.score.setText(score);

        Log.v(TAG, FILENAME + " Displaying levels " + level + " with highest score: " + score);

    }

    public int getItemCount(){
        return difficultyList.size();
    }

}