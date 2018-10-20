package com.xyz.tiber.tiber;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ProfileInterface extends Activity {

    private List<Notes> notes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_interface);

        Intent i = getIntent();

        Notes user = (Notes)i.getSerializableExtra("User");

        RecyclerView rvNotes = (RecyclerView) findViewById(R.id.rvProfileList);
        notes = new ArrayList<>();
        for(int k = 0; k < 20; k++)
        {
            notes.add(new Notes("test","test","test",user.getUserName(),"@test"+k,"ajsdfgashdfgahksdfgajksdfgjasdhjasdgfasdfkj"));

        }
        NotesAdapter adapter = new NotesAdapter(notes,true);
        System.out.println(notes);
        rvNotes.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        rvNotes.setLayoutManager(new LinearLayoutManager(this));
    }

}
