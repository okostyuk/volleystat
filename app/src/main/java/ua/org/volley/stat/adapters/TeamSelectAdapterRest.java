package ua.org.volley.stat.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.org.volley.stat.DBWrapper;
import ua.org.volley.stat.R;
import ua.org.volley.stat.model.Player;
import ua.org.volley.stat.model.Team;
import ua.org.volley.stat.model.TeamPlayer;

/**
 * Created by userXЗ on 22.11.2016.
 */

public class TeamSelectAdapterRest extends RecyclerView.Adapter<TeamSelectAdapterRest.VH> {

    public String teamOneId = "none1";
    public String teamTwoId = "none2";

    private final List<Team> teams = new ArrayList<>();

    public TeamSelectAdapterRest(List<Team> teams) {

        this.teams.add(new Team("Добавить команду"));
        this.teams.addAll(teams);
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
            if (getAdapterPosition() == 0){
                Context context = view.getContext();
                final EditText teamnameText = new EditText(context);
                teamnameText.setLayoutParams(new ViewGroup.LayoutParams(-1, -2));
                teamnameText.setHint("Введите название");
                new AlertDialog.Builder(context)
                        .setTitle("Новая команда")
                        .setView(teamnameText)
                        .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int btnId) {
                                String teamName = teamnameText.getText().toString();
                                new CreateTeamTask().execute(teamName);
                            }
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
                return;
            }
            Team team = getItem(getAdapterPosition());
            if (team.id.equals(teamOneId)){
                teamOneId = "none1";
                teamName.setChecked(false);
                return;
            }

            if (team.id.equals(teamTwoId)){
                teamTwoId = "none2";
                teamName.setChecked(false);
                return;
            }

            if (teamOneId.equals("none1")){
                teamOneId = team.id;
                teamName.setChecked(true);
                return;
            }

            if (teamTwoId.equals("none2")){
                teamTwoId  = team.id;
                teamName.setChecked(true);
                return;
            }
        }
    }

    class CreateTeamTask extends AsyncTask<String, Void, Team> {

        @Override
        protected Team doInBackground(String... strings) {
            Team team = DBWrapper.createDefaultTeam(strings[0]);

            return team;
        }

        @Override
        protected void onPostExecute(Team team) {
            teams.add(1, team);
            notifyItemInserted(teams.indexOf(team));
        }
    }
}
