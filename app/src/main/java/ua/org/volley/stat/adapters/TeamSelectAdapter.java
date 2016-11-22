package ua.org.volley.stat.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.google.firebase.database.Query;


import ua.org.volley.stat.R;
import ua.org.volley.stat.model.Team;

/**
 * Created by userXЗ on 21.11.2016.
 */

public class TeamSelectAdapter extends FirebaseRecyclerAdapter<TeamSelectAdapter.ViewHolder, Team> {

    public String teamOneId = null;
    public String teamTwoId = null;

    public TeamSelectAdapter(Query query, Class<Team> itemClass) {
        super(query, itemClass, null, null);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_team_selectable, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Team team = getItem(position);
        holder.teamName.setText(getItem(position).name);
        holder.teamName.setChecked(
                team.id.equals(teamOneId)||team.id.equals(teamTwoId)
        );
    }

    @Override
    protected void itemAdded(Team item, String key, int position) {

    }

    @Override
    protected void itemChanged(Team oldItem, Team newItem, String key, int position) {

    }

    @Override
    protected void itemRemoved(Team item, String key, int position) {

    }

    @Override
    protected void itemMoved(Team item, String key, int oldPosition, int newPosition) {

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CheckedTextView teamName;

        public ViewHolder(View itemView) {
            super(itemView);
            teamName = (CheckedTextView) itemView.findViewById(R.id.teamName);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            Team team = getItem(getAdapterPosition());
            if (team.id.equals(teamOneId)){
                teamOneId = null;
                teamName.setChecked(false);
                return;
            }

            if (team.id.equals(teamTwoId)){
                teamTwoId = null;
                teamName.setChecked(false);
                return;
            }

            if (teamOneId == null){
                teamOneId = team.id;
                teamName.setChecked(true);
                return;
            }

            if (teamTwoId == null){
                teamTwoId  = team.id;
                teamName.setChecked(true);
                return;
            }
        }
    }
}
