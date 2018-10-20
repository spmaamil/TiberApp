package com.xyz.tiber.tiber;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserProfile extends Activity
{

    private List<Notes> notes;
    private RecyclerView rvProfileList;
    private NotesAdapter adapter;
    private UserProfile context;
    private User user;
    private String USER_AUTH;// = "1053700578130493440-WQwiPr1Bfgb2qIMPJtrjQLcxACH9qO";
    private String USER_AUTH_TOKEN;// = "NfFi7l8Da5PKK2zueuPQopm4OVsdXMOJMRTgy8to8TAkc";
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        context = this;
        if (TwitterCore.getInstance().getSessionManager().getActiveSession() != null) {
            TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
            TwitterAuthToken authToken = session.getAuthToken();
            USER_AUTH = authToken.token;
            USER_AUTH_TOKEN = authToken.secret;
        }

        //Notes user = (Notes)i.getSerializableExtra("User");

        rvProfileList = (RecyclerView) findViewById(R.id.rvProfileList);

        notes = new ArrayList<>();
        //notes.add(new Notes("","","","","",""));

        adapter = new NotesAdapter(notes,true);
        Log.i("ADAPTER",adapter.getItemCount() + "");

        rvProfileList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        rvProfileList.setLayoutManager(new LinearLayoutManager(this));

        getUserInfo();

        final Button b = (Button)findViewById(R.id.makeNote);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(b.getContext(),PostNote.class);
                i.putExtra("User",user);
                b.getContext().startActivity(i);
            }
        });
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.navigation_home)
                {
                    Intent i = new Intent(bottomNavigationView.getContext(),UserInterface.class);

                    bottomNavigationView.getContext().startActivity(i);
                    return true;
                }

                return false;
            }
        });

        Date date = new Date();
        date.setTime(date.getTime());
        long timeDiff = ((date.getTime() - AppProperties.getInstance().start)/1000);

        ((TextView)findViewById(R.id.textTime)).setText("Time On App: " + ((timeDiff > 60)? (timeDiff/60 + " Minutes"):(timeDiff + " Seconds")));

    }

    public void updateUserInfo(String[] resp)
    {

        if(resp != null)
        {
            Log.i("CONF", resp[1]);
            try
            {
                JSONObject obj = new JSONObject(resp[1]);
                //Log.i("NAME",obj.getString("author"));
                String authorImageLink = obj.getString("photo");
                String authorId = obj.getString("author");
                String authorName = obj.getString("authorUser");
                String authorHandle = obj.getString("handle");
                String message = obj.getString("description");

                ((TextView)context.findViewById(R.id.userName)).setText(authorName);
                Log.i("TEST","fine1");
                ((TextView)context.findViewById(R.id.userTag)).setText(authorHandle);
                Log.i("TEST","fine2");
                ((TextView)context.findViewById(R.id.userDescription)).setText(message);
                Log.i("TEST","fine3");
                Picasso.get().load(authorImageLink).into((ImageView)context.findViewById(R.id.imageView));

                user = new User(authorImageLink,authorId,authorName,authorHandle,message);
                //notes.add(new Notes(authorImageLink,authorId,authorName,authorHandle,message,id));
            }
            catch(Exception e)
            {
                Log.i("CRASH",e.getMessage());
            }

            adapter.notifyDataSetChanged();

        }
        getUserFeed();
    }

    public void updateUserFeed(String[] resp)
    {
        if(resp != null)
        {
            try
            {
                JSONArray jsonArray = new JSONArray(resp[1]);
                Log.i("LEN",jsonArray.length() + "");
                for(int k = 0; k < jsonArray.length(); k++)
                {
                    //Log.i("TEST","right above creating json obj");
                    JSONObject obj = new JSONObject(jsonArray.getString(k));
                    //Log.i("NAME",obj.getString("author"));
                    String authorImageLink = obj.getString("photo");
                    String authorId = obj.getString("author");
                    String authorName = obj.getString("authorUser");
                    String authorHandle = obj.getString("handle");
                    String message = obj.getString("message");
                    String id = obj.getString("id");

                    notes.add(new Notes(authorImageLink,authorId,authorName,authorHandle,message,id));
                }
                adapter.notifyDataSetChanged();
            }
            catch(Exception e)
            {
                Log.i("CRASH",e.getMessage());
            }
        }
    }

    public void getUserFeed()
    {
        try
        {
            Log.i("TEST","Getting user info");
            UserInfo task = new UserInfo(this);
            task.setArgs(user.getUserId());
            task.execute();

        }
        catch(Exception e)
        {

        }
    }

    public void getUserInfo()
    {
        try
        {
            UserInfo task = new UserInfo(this);
            task.execute();
        }
        catch(Exception e)
        {

        }
    }
    private class UserInfo extends AsyncTask<String, Void, Void >
    {
        private String[] resp;
        private String[] args;
        private UserProfile context;
        public UserInfo(UserProfile c)
        {
            context = c;

        }

        public void setArgs(String s)
        {
            args = new String[1];
            args[0] = s;
        }

        @Override
        protected Void doInBackground(String... strings)
        {
            resp = new String[2];
            try
            {
                URL url;
                if(args != null)
                {
                    url = new URL("http://cislinux.cs.ksu.edu:5000/profilet");
                }
                else
                {
                    url = new URL("http://cislinux.cs.ksu.edu:5000/me");

                }

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("access_token",USER_AUTH);
                jsonParam.put("access_token_secret",USER_AUTH_TOKEN);
                if(args != null)
                    jsonParam.put("tid",args[0]);

                Log.i("JSON",jsonParam.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();
                Log.i("STATUS",String.valueOf(conn.getResponseCode()));
                Log.i("MSG",conn.getResponseMessage());
                if(conn.getResponseMessage().equals("OK"))
                {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String output;
                    while ((output = br.readLine()) != null)
                        sb.append(output);
                    Log.i("RESPONSE", sb.toString());
                    resp[0] = "true";
                    resp[1] = sb.toString();
                    //resp[2] = args[0];
                    Log.i("INFO",sb.toString());
                }
                else
                {
                    resp = null;
                }

                conn.disconnect();

            }
            catch(Exception e)
            {
                Log.i("CRASH",e.getMessage());
                resp = null;
            }
            return null;
        }
        protected void onPostExecute(Void res)
        {
            Log.i("TEST","this is a test in the post exececute method");
            if(args == null) {
                context.updateUserInfo(resp);
            }
            else
            {
                context.updateUserFeed(resp);
            }
        }
    }

}
