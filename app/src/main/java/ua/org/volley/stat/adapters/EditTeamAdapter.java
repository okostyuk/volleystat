package ua.org.volley.stat.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Oleg on 27.11.2016.
 */

public class EditTeamAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class PlayerVH extends RecyclerView.ViewHolder{

        public PlayerVH(View itemView) {
            super(itemView);
        }
    }

    class AddPlayerVH extends RecyclerView.ViewHolder{

        public AddPlayerVH(View itemView) {
            super(itemView);
        }
    }
}
