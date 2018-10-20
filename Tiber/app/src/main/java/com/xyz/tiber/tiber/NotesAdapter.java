package com.xyz.tiber.tiber;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {


    public class ViewHolder extends RecyclerView.ViewHolder {
        public Image notesImage;
        public TextView notesUserName;
        public TextView notesUserTag;
        public TextView notesUserMessage;

        public ViewHolder(View itemView)
        {
            super(itemView);
            notesUserName = (TextView) itemView.findViewById(R.id.userName);
            notesUserTag = (TextView) itemView.findViewById(R.id.userTag);
            notesUserMessage = (TextView) itemView.findViewById(R.id.userMessage);

        }
    }

    private List<Notes> notes;
    private NotesAdapter thisAdapter = this;
    private boolean large;
    public NotesAdapter(List<Notes> notes, boolean large)
    {
        this.notes = notes;
        this.large = large;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View notes;
        notes = inflater.inflate(R.layout.activity_large_note_layout,viewGroup,false);

        ViewHolder viewHolder = new ViewHolder(notes);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i)
    {
        final int pos = i;

        final Notes note = notes.get(i);

        viewHolder.notesUserName.setText(note.getUserName());
        viewHolder.notesUserTag.setText(note.getUserTag());
        viewHolder.notesUserMessage.setText(note.getMessage());

        viewHolder.notesUserName.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), pos+ "", Toast.LENGTH_LONG).show();
                Intent i = new Intent(v.getContext(),ProfileInterface.class);
                i.putExtra("User",note);
                v.getContext().startActivity(i);
            }
        });

        /*userTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserInterface,ProfileInterface.class);
            }
        });
        TextView userMessage = viewHolder.notesUserMessage;
        userMessage.setText(note.getMessage());*/
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
