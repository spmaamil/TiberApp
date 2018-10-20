package com.xyz.tiber.tiber;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostNote extends AppCompatActivity {

    final PostNote context = this;
    private String USER_AUTH;
    private String USER_AUTH_TOKEN;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_note);

        if (TwitterCore.getInstance().getSessionManager().getActiveSession() != null) {
            TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
            TwitterAuthToken authToken = session.getAuthToken();
            USER_AUTH = authToken.token;
            USER_AUTH_TOKEN = authToken.secret;
        }
        Intent in = getIntent();
        final User user = (User)in.getSerializableExtra("User");

        ((TextView)findViewById(R.id.userName)).setText(user.getUserName());
        Log.i("TEST","fine1");
        ((TextView)findViewById(R.id.userTag)).setText(user.getUserTag());
        Log.i("TEST","fine2");
        Picasso.get().load(user.getUserImageUrl()).into((ImageView)context.findViewById(R.id.imageView));

        final Button b = (Button)findViewById(R.id.sendNote);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String message = ((EditText)findViewById(R.id.editText)).getText().toString();
                b.setClickable(false);
                sendTweet(message);
            }
        });


    }

    private void closePostNote()
    {
        Intent i = new Intent(this,UserProfile.class);
        startActivity(i);
    }

    private void sendTweet(String message)
    {
        try
        {
            PostNotePoster post = new PostNotePoster(this, message);
            post.execute();
        }
        catch(Exception e)
        {

        }
    }
    private class PostNotePoster extends AsyncTask<String, Void, Void>
    {
        private PostNote context;
        private String message;
        public PostNotePoster (PostNote c, String message)
        {
            context = c;
            this.message = message;

        }

        @Override
        protected Void doInBackground(String... strings)
        {
            try
            {
                URL url = new URL("http://cislinux.cs.ksu.edu:5000/tweet");



                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("access_token",USER_AUTH);
                jsonParam.put("access_token_secret",USER_AUTH_TOKEN);
                jsonParam.put("message",message);

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
            context.closePostNote();
        }
    }

}
