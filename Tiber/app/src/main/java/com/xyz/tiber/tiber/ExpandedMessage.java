package com.xyz.tiber.tiber;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExpandedMessage extends AppCompatActivity
{

    private String USER_AUTH;
    private String USER_AUTH_TOKEN;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_message);
        
        if (TwitterCore.getInstance().getSessionManager().getActiveSession() != null) {
            TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
            TwitterAuthToken authToken = session.getAuthToken();
            USER_AUTH = authToken.token;
            USER_AUTH_TOKEN = authToken.secret;
        }
        Intent i = getIntent();
        final Notes note = (Notes)i.getSerializableExtra("Note");

        ((TextView)findViewById(R.id.userName)).setText(note.getUserName());
        ((TextView)findViewById(R.id.userTag)).setText(note.getUserTag());
        ((TextView)findViewById(R.id.userMessage)).setText(note.getMessage());
        Picasso.get().load(note.getUserImageUrl()).into((ImageView)findViewById(R.id.imageView));

        final Button b = (Button)findViewById(R.id.repostButton);
        b.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                reTweet(note.getNoteId());
            }
        });

    }
    private void reTweet(String message)
    {
        try
        {
            ExpandedMessagePoster post = new ExpandedMessagePoster(this, message);
            post.execute();
        }
        catch(Exception e)
        {

        }
    }
    private class ExpandedMessagePoster extends AsyncTask<String, Void, Void>
    {
        private ExpandedMessage context;
        private String message;
        public ExpandedMessagePoster (ExpandedMessage c, String message)
        {
            context = c;
            this.message = message;

        }

        @Override
        protected Void doInBackground(String... strings)
        {
            try
            {
                URL url = new URL("http://cislinux.cs.ksu.edu:5000/retweet");



                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("access_token",USER_AUTH);
                jsonParam.put("access_token_secret",USER_AUTH_TOKEN);
                jsonParam.put("tid",message);

                Log.i("JSON",jsonParam.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();
                Log.i("STATUS",String.valueOf(conn.getResponseCode()));
                Log.i("MSG",conn.getResponseMessage());

                conn.disconnect();

            }
            catch(Exception e)
            {
                Log.i("CRASH",e.getMessage());
            }
            return null;
        }
        protected void onPostExecute(Void res)
        {
            Log.i("TEST","this is a test in the post exececute method");
            //context.closePostNote();
        }
    }
}
