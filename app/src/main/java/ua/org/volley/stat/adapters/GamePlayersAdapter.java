package ua.org.volley.stat.adapters;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ua.org.volley.stat.R;
import ua.org.volley.stat.activity.GameActivity;
import ua.org.volley.stat.dialogs.ScoreDialog;
import ua.org.volley.stat.model.Player;
import ua.org.volley.stat.model.TeamPlayer;

/**
 * Created by userXЗ on 22.11.2016.
 */

public class GamePlayersAdapter extends RecyclerView.Adapter<GamePlayersAdapter.ViewHolder> {

    private final List<TeamPlayer> players = new ArrayList<>();
    private final List<TeamPlayer> sparePlayers = new ArrayList<>();
    private ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new GamePlayersAdapter.ReplaceCallback());

    GameActivity activity;


    public GamePlayersAdapter(GameActivity activity, Collection<TeamPlayer> players, Collection<TeamPlayer> sparePlayers) {
        this.players.addAll(players);
        this.activity = activity;
        this.sparePlayers.addAll(sparePlayers);
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
        //holder.checked.setVisibility(selectedPlayerPos == position?View.VISIBLE:View.GONE);
        //holder.itemView.setSelected(selectedPlayerPos == position);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        itemTouchHelper.attachToRecyclerView(null);
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
            new ScoreDialog(players.get(getAdapterPosition()), activity).show();
        }
    }

    private class ReplaceCallback extends ItemTouchHelper.SimpleCallback
            implements DialogInterface.OnClickListener{

        public ReplaceCallback() {
            super(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        TeamPlayer playerOut;
        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            CharSequence[] dialogPlayers = new CharSequence[sparePlayers.size()];
            for (int i=0; i<sparePlayers.size(); i++){
                dialogPlayers[i] = sparePlayers.get(i).number + " " + sparePlayers.get(i).playerName;
            }
            final int index = viewHolder.getAdapterPosition();
            playerOut = players.get(index);
            players.remove(index);
            notifyItemRemoved(index);

            new AlertDialog.Builder(activity)
                    .setTitle(playerOut.number + " " + playerOut.playerName)
                    .setItems(dialogPlayers, this)
                    .setPositiveButton(R.string.replace, this)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            players.add(index, playerOut);
                            notifyItemInserted(index);
                        }
                    })
                    .show();
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            TeamPlayer playerIn = sparePlayers.get(i);
            if (playerIn != null){
                sparePlayers.remove(playerIn);
                sparePlayers.add(playerOut);
                players.add(playerIn);
                notifyItemInserted(players.indexOf(playerIn));
            }

        }
    }
}
