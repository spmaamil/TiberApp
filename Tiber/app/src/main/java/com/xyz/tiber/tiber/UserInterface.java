package com.xyz.tiber.tiber;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class UserInterface extends AppCompatActivity
{
    private List<Notes> notes;
    private RecyclerView rvNotes;
    private NotesAdapter adapter;
    private UserInterface context;
    private String USER_AUTH;// = "1053700578130493440-WQwiPr1Bfgb2qIMPJtrjQLcxACH9qO";
    private String USER_AUTH_TOKEN;// = "NfFi7l8Da5PKK2zueuPQopm4OVsdXMOJMRTgy8to8TAkc";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interface);


        Intent i = getIntent();
        if (TwitterCore.getInstance().getSessionManager().getActiveSession() != null) {
            TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
            TwitterAuthToken authToken = session.getAuthToken();
            USER_AUTH = authToken.token;
            USER_AUTH_TOKEN = authToken.secret;
        }

        context = this;

        rvNotes = (RecyclerView) findViewById(R.id.rvNotes);
        notes = new ArrayList<>();
        /*for(int k = 0; k < 20; k++)
        {
            notes.add(new Notes("Test"+k,"@test"+k,"ajsdfgashdfgahksdfgajksdfgjasdhjasdgfasdfkj"));

        }*/
        adapter = new NotesAdapter(notes,true);
        System.out.println(notes);
        rvNotes.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        rvNotes.setLayoutManager(new LinearLayoutManager(this));

        callServer();


        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if(menuItem.getItemId() == R.id.navigation_dashboard)
                {
                    Intent i = new Intent(bottomNavigationView.getContext(),UserProfile.class);
                    bottomNavigationView.getContext().startActivity(i);
                    return true;
                }

                return false;
            }
        });


//        Toast.makeText(this, json, Toast.LENGTH_LONG);


        /*BottomNavigationView bottomNav = (BottomNavigationView)findViewById(R.id.bottomNavigationView);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        });*/
    }

    public void updateNoteFeed(String[] resp)
    {

        if(resp != null)
        {
            Log.i("CONF", resp[1]);
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
                Log.i("NAME",notes.get(0).getUserName());
            }
            catch(Exception e)
            {

                Log.i("CRASH",e.getMessage());
            }

            adapter.notifyDataSetChanged();

        }
    }

    public String[] callServer()
    {
        try
        {
            Task task = new Task(this);
            task.execute();
        }
        catch(Exception e)
        {

        }
        return null;
    }
    private class Task extends AsyncTask<String, Void, Void >
    {
        private String[] resp;
        private String[] args;
        private UserInterface context;
        Task(UserInterface c, String ... args)
        {
            context = c;
            this.args = args;
        }
        @Override
        protected Void doInBackground(String... strings)
        {
            resp = new String[2];
            try
            {

                URL url = new URL("http://cslinux.cs.ksu.edu:5000/home");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("access_token",USER_AUTH);
                jsonParam.put("access_token_secret",USER_AUTH_TOKEN);

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
            context.updateNoteFeed(resp);
        }
    }




}
