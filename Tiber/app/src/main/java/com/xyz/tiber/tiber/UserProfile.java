package com.xyz.tiber.tiber;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class UserProfile extends Activity {

    private List<Notes> notes;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        Notes user = (Notes)i.getSerializableExtra("User");
        RecyclerView rvNotes = (RecyclerView) findViewById(R.id.rvProfileList);
        notes = new ArrayList<>();
        for(int k = 0; k < 20; k++)
        {
            notes.add(new Notes("test","test","test",user.getUserName(),user.getUserTag(),"ajsdfgashdfgahksdfgajksdfgjasdhjasdgfasdfkj"));

        }
        NotesAdapter adapter = new NotesAdapter(notes,true);
        System.out.println(notes);
        rvNotes.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
    }

}
