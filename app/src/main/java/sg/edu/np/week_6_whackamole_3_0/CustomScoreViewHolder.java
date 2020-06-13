package sg.edu.np.week_6_whackamole_3_0;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class CustomScoreViewHolder extends RecyclerView.ViewHolder {

    private static final String FILENAME = "CustomScoreViewHolder.java";
    private static final String TAG = "Whack-A-Mole3.0!";

    public TextView score;
    public TextView level;

    public CustomScoreViewHolder(final View itemView, final CustomScoreAdaptor.OnItemClickListener listener){
        super(itemView);

        this.score = itemView.findViewById(R.id.score_display);
        this.level = itemView.findViewById(R.id.level_display);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        listener.onItemClick(position);
                    }
                }
            }
        });
    }
}
