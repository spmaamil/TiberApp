package com.xyz.tiber.tiber;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UserProfile extends Activity {

    private List<Notes> notes;
    private RecyclerView rvProfileList;
    private NotesAdapter adapter;
    private UserProfile context;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        context = this;
        //Intent i = getIntent();
        //Notes user = (Notes)i.getSerializableExtra("User");

        rvProfileList = (RecyclerView) findViewById(R.id.rvProfileList);

        notes = new ArrayList<>();
        notes.add(new Notes("","","","","",""));

        adapter = new NotesAdapter(notes,true);
        Log.i("ADAPTER",adapter.getItemCount() + "");

        rvProfileList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        rvProfileList.setLayoutManager(new LinearLayoutManager(this));

        callServer();

    }

    public void updateNoteFeed(String[] resp)
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


                //notes.add(new Notes(authorImageLink,authorId,authorName,authorHandle,message,id));
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
            UserProfile.Task task = new UserProfile.Task(this);
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
        private UserProfile context;
        public Task(UserProfile c)
        {
            context = c;
        }
        @Override
        protected Void doInBackground(String... strings)
        {
            resp = new String[2];
            try
            {

                URL url = new URL("http://cislinux.cs.ksu.edu:5000/me");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("access_token","1053482017134911488-AcVB7TcyjWJFcWUoT0qW7mW8OUkS1P");
                jsonParam.put("access_token_secret","x4QhDcxLXJncbNHBEYBKTU5kXcZcmIxI3Hab9ufc8Il9B");

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
