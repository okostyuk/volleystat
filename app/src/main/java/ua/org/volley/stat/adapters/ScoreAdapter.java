package ua.org.volley.stat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ua.org.volley.stat.R;
import ua.org.volley.stat.model.ScoreRecord;

/**
 * Created by userXЗ on 30.11.2016.
 */

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.VH> {

    private final List<ScoreRecord> items;
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public ScoreAdapter(List<ScoreRecord> items) {
        this.items = items;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_score, parent, false);
        return new VH(itemView);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        ScoreRecord item = items.get(position);
        Integer[] scores = new Integer[2];
        item.score.values().toArray(scores);
        holder.setScore.setText(scores[0]+" - " +scores[1]);
        holder.time.setText(timeFormat.format(new Date(item.time)));

        if(item.teamPlayer != null){
            holder.player.setText(item.teamPlayer.number + " " + item.teamPlayer.playerName);
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class VH extends RecyclerView.ViewHolder{
        TextView setScore, time, action, player;
        public VH(View itemView) {
            super(itemView);

            setScore = (TextView) itemView.findViewById(R.id.setScore);
            time = (TextView) itemView.findViewById(R.id.time);
            action = (TextView) itemView.findViewById(R.id.action);
            player = (TextView) itemView.findViewById(R.id.player);
        }
    }
}
