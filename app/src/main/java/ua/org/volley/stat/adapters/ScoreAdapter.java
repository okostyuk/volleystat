package ua.org.volley.stat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ua.org.volley.stat.R;
import ua.org.volley.stat.model.ScoreRecord;

/**
 * Created by userXЗ on 30.11.2016.
 */

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.VH> {

    List<ScoreRecord> items;

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
        holder.setScore.setText(item.score);
        holder.time.setText(item.time);
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
