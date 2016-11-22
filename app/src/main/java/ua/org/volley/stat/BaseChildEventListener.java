package ua.org.volley.stat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

import ua.org.volley.stat.model.TeamPlayer;

/**
 * Created by userXЗ on 22.11.2016.
 */

public abstract class BaseChildEventListener<T> implements ChildEventListener {
    List<String> loaded = new ArrayList<>();
    List<String> loadIds;
    Class<T> itemClass;
    List<T> result = new ArrayList<>();

    public BaseChildEventListener(List<String> loadIds, Class<T> itemClass) {
        this.loadIds = loadIds;
        this.itemClass = itemClass;
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        String key = dataSnapshot.getKey();
        if (
                (loadIds == null || loadIds.isEmpty() || loadIds.contains(key))
                && !loaded.contains(key)){
            loaded.add(key);
            T item = dataSnapshot.getValue(itemClass);
            result.add(item);
        }
    }
    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
