package ua.org.volley.stat.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.org.volley.stat.DBWrapper;
import ua.org.volley.stat.R;
import ua.org.volley.stat.model.Player;
import ua.org.volley.stat.model.Team;
import ua.org.volley.stat.model.TeamPlayer;

/**
 * Created by Oleg on 27.11.2016.
 */

public class EditTeamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SPARE = 0;
    private static final int EDIT = 1;
    int SPARE_DIVIDER_POS = 6;
    private ItemTouchHelper changeTouchHelper = new ItemTouchHelper(new RemoveCallback());

    private List<TeamPlayer> players = new ArrayList<>();
    private Team team;

    public EditTeamAdapter(Team team) {
        this.team = team;
        players.add(new TeamPlayer(team, null, null));//space for add player item
        this.players.addAll(1, team.players.values());
    }

    @Override
    public int getItemViewType(int position) {
        if (players.size() < 7){
            if (position == players.size()-1)
                return SPARE;
        }else if(position == SPARE_DIVIDER_POS){
            return SPARE;
        }
        return EDIT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int itemRes = viewType==SPARE?R.layout.item_spare_divider:R.layout.item_player_edit;
        View itemView = LayoutInflater.from(parent.getContext())
                  .inflate(itemRes, parent, false);

        return viewType==SPARE?new SpareDividerVH(itemView):new EditPlayerVH(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == EDIT){
            EditPlayerVH editHolder = ((EditPlayerVH) holder);
            editHolder.number.setText(players.get(position).number);
            editHolder.name.setText(players.get(position).playerName);
        }
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    private class EditPlayerVH extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView number;
        TextView name;
        EditPlayerVH(View itemView) {
            super(itemView);
            number = (TextView) itemView.findViewById(R.id.number);
            name = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            showDialog(view.getContext(), players.get(getAdapterPosition()));
        }
    }

    private class SpareDividerVH extends RecyclerView.ViewHolder implements View.OnClickListener{
        SpareDividerVH(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            showDialog(view.getContext(), new TeamPlayer(team, null, null));
        }
    }

    private void showDialog(Context context, final TeamPlayer player){
        LinearLayout view = new LinearLayout(context);
        view.setPadding(8, 8, 8, 8);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setLayoutParams(new ViewGroup.LayoutParams(-2,-2));
        final EditText number = new EditText(context);
        final EditText name = new EditText(context);
        number.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
        name.setLayoutParams(new LinearLayout.LayoutParams(-1,-2));
        name.setHint(R.string.name);
        number.setHint(R.string.number);
        number.setInputType(InputType.TYPE_CLASS_NUMBER);
        name.setLines(1);

        view.addView(number);
        view.addView(name);

        if (player.playerId != null){
            number.setText(player.number);
            number.setEnabled(false);
            name.setText(player.playerName);
        }else{

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setView(view)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (name.getText().length() == 0 || number.getText().length() == 0 )
                            return;

                        player.playerName = name.getText().toString();
                        player.number = number.getText().toString();

                        if (player.playerId != null){
                            player.id = "n"+player.number;
                            DBWrapper.updateFirebaseRecord(
                                    player,
                                    "teams/"+team.id+"/players/");
                            notifyItemChanged(players.indexOf(player));
                        }else{
                            Player newPlayer = new Player();
                            newPlayer.name = player.playerName;
                            newPlayer.teams.add(team);
                            team.addPlayer(player);

                            DBWrapper.addPlayer(newPlayer);
                            player.playerId = newPlayer.id;
                            DBWrapper.updateFirebaseRecord(team, DBWrapper.TEAMS);
                            players.add(1, player);
                            notifyItemInserted(1);
                        }
                    }
                });
        if (player.playerId == null){
            builder.setTitle(R.string.add_player);
            name.setText(player.playerName);
            number.setText(player.number);
        }else{
            builder.setTitle(R.string.edit_player);
        }
        builder.show();

    }

    private class RemoveCallback extends ItemTouchHelper.SimpleCallback{

        RemoveCallback() {
            super(0, ItemTouchHelper.UP|ItemTouchHelper.DOWN);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder src, RecyclerView.ViewHolder dst) {
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int pos = viewHolder.getAdapterPosition();
            players.remove(pos);
            notifyItemRemoved(pos);
        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        changeTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        changeTouchHelper.attachToRecyclerView(null);
    }
}
