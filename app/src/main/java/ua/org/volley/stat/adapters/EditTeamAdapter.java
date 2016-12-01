package ua.org.volley.stat.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.org.volley.stat.R;
import ua.org.volley.stat.activity.GameActivity;
import ua.org.volley.stat.model.Team;
import ua.org.volley.stat.model.TeamPlayer;

/**
 * Created by Oleg on 27.11.2016.
 */

public class EditTeamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int SPARE = 0;
    private static final int EDIT = 1;

    private ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RemoveCallback());
    private GameActivity activity;

    private List<TeamPlayer> players = new ArrayList<>();
    private TeamPlayer spare;
    private Team team;

    public EditTeamAdapter(Team team, GameActivity activity) {
        this.activity = activity;
        this.team = team;
        this.players.addAll(team.players.values());
        spare = new TeamPlayer();
        if (players.size() < 7){
            players.add(spare);
        }else{
            players.add(6, spare);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (players.indexOf(spare) == position)
            return SPARE;
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
            TeamPlayer player = players.get(position);
            EditPlayerVH editHolder = ((EditPlayerVH) holder);
            editHolder.number.setText(player.number);
            editHolder.name.setText(player.playerName);
        }
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public void add(TeamPlayer player) {
        players.add(player);
        notifyItemInserted(players.size()-1);
    }

    public void notifyItemChanged(TeamPlayer player) {
        int pos = players.indexOf(player);
        notifyItemChanged(pos);
    }

    public List<TeamPlayer> getMainPlayers(){
        int pos = players.indexOf(spare);
        return players.subList(0, pos);
    }

    public List<TeamPlayer> getSparePlayrs() {
        int pos = players.indexOf(spare);
        return players.subList(pos+1, players.size());
    }

    private class EditPlayerVH extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView number;
        TextView name;
        EditPlayerVH(View itemView) {
            super(itemView);
            number = (TextView) itemView.findViewById(R.id.number);
            name = (TextView) itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
            itemView.findViewById(R.id.edit).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    itemTouchHelper.startDrag(EditPlayerVH.this);
                    return true;
                }
            });
        }

        @Override
        public void onClick(View view) {
/*
            if (view.getId() == R.id.edit)
                itemTouchHelper.startDrag(this);
            else
*/
                activity.showPlayerDialog(
                        players.get(getAdapterPosition()),
                        team
                );
        }
    }

    private class SpareDividerVH extends RecyclerView.ViewHolder{
        SpareDividerVH(View itemView) {
            super(itemView);
        }
    }



    private class RemoveCallback extends ItemTouchHelper.SimpleCallback{


        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setBackgroundResource(R.drawable.background_checkable);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        RemoveCallback() {
            super(ItemTouchHelper.UP|ItemTouchHelper.DOWN, 0);
        }

        @Override
        public int getDragDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getAdapterPosition() == players.indexOf(spare))
                return 0;

            return super.getDragDirs(recyclerView, viewHolder);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder src, RecyclerView.ViewHolder dst) {
            if (src.getAdapterPosition() == players.indexOf(spare))
                return false;

            TeamPlayer player = players.get(src.getAdapterPosition());
            players.remove(player);
            players.add(dst.getAdapterPosition(), player);
            notifyItemMoved(src.getAdapterPosition(), dst.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int pos = viewHolder.getAdapterPosition();
            players.remove(pos);
            notifyItemRemoved(pos);
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            super.onSelectedChanged(viewHolder, actionState);
            if (viewHolder != null)
                viewHolder.itemView.setBackgroundColor(activity.getResources().getColor(R.color.colorAccentTransparent));

        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        itemTouchHelper.attachToRecyclerView(null);
    }
}
