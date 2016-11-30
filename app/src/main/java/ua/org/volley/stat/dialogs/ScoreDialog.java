package ua.org.volley.stat.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.org.volley.stat.DBWrapper;
import ua.org.volley.stat.R;
import ua.org.volley.stat.activity.GameActivity;
import ua.org.volley.stat.model.StatRecord;
import ua.org.volley.stat.model.StatRecordType;
import ua.org.volley.stat.model.TeamPlayer;

/**
 * Created by userXЗ on 30.11.2016.
 */

public class ScoreDialog implements View.OnClickListener {
    Dialog dialog;
    List<RadioButton> actionButtons = new ArrayList<>();
    Listener listener;
    TeamPlayer player;


    public ScoreDialog(TeamPlayer player, GameActivity context) {
        dialog = new Dialog(context);
        this.player = player;
        listener = context;
        dialog.setTitle(player.number + " " + player.playerName);
        dialog.setContentView(R.layout.dialog_score);

        RadioButton serveBtn = (RadioButton) dialog.findViewById(R.id.serve);
        RadioButton passBtn = (RadioButton) dialog.findViewById(R.id.pass);
        RadioButton attackBtn = (RadioButton) dialog.findViewById(R.id.attack);
        RadioButton digBtn = (RadioButton) dialog.findViewById(R.id.dig);
        RadioButton setBtn = (RadioButton) dialog.findViewById(R.id.set);
        RadioButton blockBtn = (RadioButton) dialog.findViewById(R.id.block);

        serveBtn.setOnClickListener(this);
        passBtn.setOnClickListener(this);
        attackBtn.setOnClickListener(this);
        digBtn.setOnClickListener(this);
        setBtn.setOnClickListener(this);
        blockBtn.setOnClickListener(this);

        dialog.findViewById(R.id.neg).setOnClickListener(this);
        dialog.findViewById(R.id.neutral).setOnClickListener(this);
        dialog.findViewById(R.id.pos).setOnClickListener(this);

        actionButtons.add(serveBtn);
        actionButtons.add(passBtn);
        actionButtons.add(attackBtn);
        actionButtons.add(digBtn);
        actionButtons.add(setBtn);
        actionButtons.add(blockBtn);
    }

    StatRecordType selectedType = null;

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.attack:
            case R.id.dig:
            case R.id.block:
            case R.id.set:
            case R.id.serve:
            case R.id.pass:
                for (RadioButton btn : actionButtons){
                    btn.setChecked(false);
                }
                ((RadioButton)view).setChecked(true);
                selectedType = btnIdToActionType(view.getId());
                break;
            case R.id.pos:
            case R.id.neg:
            case R.id.neutral:
                int value = btnIdToScore(view.getId());
                StatRecord statRecord = createStatRecord(selectedType, value);
                selectedType = null;
                listener.onStatCreated(statRecord);
                dialog.dismiss();
                break;
        }
    }

    private StatRecordType btnIdToActionType(int btnId) {
        switch (btnId){
            case R.id.serve: return StatRecordType.SERVE;
            case R.id.pass: return StatRecordType.PASS;
            case R.id.set: return StatRecordType.SET;
            case R.id.block: return StatRecordType.BLOCK;
            case R.id.attack: return StatRecordType.ATTACK;
            case R.id.dig: return StatRecordType.DIG;
            default: return null;
        }
    }

    private int btnIdToScore(int btnId){
        switch (btnId){
            case R.id.pos: return 1;
            case R.id.neutral: return 0;
            case R.id.neg: return -1;
        }
        return 0;
    }

    private StatRecord createStatRecord(StatRecordType type, int statRecordValue) {
        StatRecord statRecord = new StatRecord();
        statRecord.actionType = type.name();
        statRecord.value = statRecordValue;
        statRecord.playerId = player.playerId;
        return statRecord;
    }

    public void show(){
        dialog.show();
    }

    public interface Listener {
        void onStatCreated(StatRecord statRecord);
    }
}
