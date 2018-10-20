package com.xyz.tiber.tiber;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView notesImage;
        public TextView notesUserName;
        public TextView notesUserTag;
        public TextView notesUserMessage;

        public ViewHolder(View itemView)
        {
            super(itemView);
            notesUserName = (TextView) itemView.findViewById(R.id.userName);
            notesUserTag = (TextView) itemView.findViewById(R.id.userTag);
            notesUserMessage = (TextView) itemView.findViewById(R.id.userMessage);
            notesImage = (ImageView) itemView.findViewById(R.id.imageView);
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
        viewHolder.notesUserName.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), pos+ "", Toast.LENGTH_LONG).show();
                Intent i = new Intent(v.getContext(),ProfileInterface.class);
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken auth=session.getAuthToken();
                i.putExtra("auth",auth.token);
                i.putExtra("auth_token",auth.secret);
                i.putExtra("User",note);
                v.getContext().startActivity(i);
            }
        });

        viewHolder.notesUserTag.setText(note.getUserTag());
        viewHolder.notesUserTag.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), pos+ "", Toast.LENGTH_LONG).show();
                Intent i = new Intent(v.getContext(),ProfileInterface.class);
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken auth=session.getAuthToken();
                i.putExtra("auth",auth.token);
                i.putExtra("auth_token",auth.secret);
                i.putExtra("User",note);
                v.getContext().startActivity(i);
            }
        });


        viewHolder.notesUserMessage.setText(note.getMessage());
        viewHolder.notesUserMessage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(v.getContext(), ExpandedMessage.class);
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken auth=session.getAuthToken();
                i.putExtra("auth",auth.token);
                i.putExtra("auth_token",auth.secret);
                i.putExtra("Note",note);
                v.getContext().startActivity(i);
            }
        });

        Picasso.get().load(note.getUserImageUrl()).into(viewHolder.notesImage);

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }
}
