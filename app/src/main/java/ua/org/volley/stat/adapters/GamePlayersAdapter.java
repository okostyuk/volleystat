package ua.org.volley.stat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import ua.org.volley.stat.R;
import ua.org.volley.stat.model.Player;
import ua.org.volley.stat.model.TeamPlayer;

/**
 * Created by userXЗ on 22.11.2016.
 */

public class GamePlayersAdapter extends RecyclerView.Adapter<GamePlayersAdapter.ViewHolder> {
    private final List<TeamPlayer> players;
    static int selectedPlayerPos = -1;

    public GamePlayersAdapter(List<TeamPlayer> players) {
        this.players = players;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_player, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TeamPlayer player = players.get(position);
        holder.number.setText(String.valueOf(player.number));
        holder.name.setText(player.playerName);
        holder.checked.setVisibility(selectedPlayerPos == position?View.VISIBLE:View.GONE);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public TeamPlayer getSelectedPlayer() {
        if (selectedPlayerPos == -1)
            return null;
        return players.get(selectedPlayerPos);
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView number, name;
        private View checked;

        public ViewHolder(View itemView) {
            super(itemView);
            number = (TextView) itemView.findViewById(R.id.number);
            name = (TextView) itemView.findViewById(R.id.name);
            checked = itemView.findViewById(R.id.checked);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int oldSelectedPos = selectedPlayerPos;
            selectedPlayerPos = getAdapterPosition();
            notifyItemChanged(oldSelectedPos);
            notifyItemChanged(selectedPlayerPos);
        }
    }
}
