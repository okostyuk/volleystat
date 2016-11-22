package ua.org.volley.stat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import java.util.List;

import ua.org.volley.stat.R;
import ua.org.volley.stat.model.Team;

/**
 * Created by userXЗ on 22.11.2016.
 */

public class TeamSelectAdapterRest extends RecyclerView.Adapter<TeamSelectAdapterRest.VH> {

    public String teamOneId = null;
    public String teamTwoId = null;

    private final List<Team> teams;

    public TeamSelectAdapterRest(List<Team> teams) {
        this.teams = teams;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_team_selectable, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Team team = getItem(position);
        holder.teamName.setText(team.name);
        holder.teamName.setChecked(
                team.id.equals(teamOneId)||team.id.equals(teamTwoId)
        );
    }

    private Team getItem(int position) {
        return teams.get(position);
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    class VH extends RecyclerView.ViewHolder implements View.OnClickListener {

        public VH(View itemView) {
            super(itemView);
            teamName = (CheckedTextView) itemView.findViewById(R.id.teamName);
            itemView.setOnClickListener(this);
        }

        CheckedTextView teamName;

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
