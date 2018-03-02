package com.startup.driveschoolclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.startup.driveschoolclient.adapter.GoalsAdapter;
import com.startup.driveschoolclient.util.Config;
import com.startup.driveschoolclient.util.ServerConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GoalsActivity extends AppCompatActivity {

    private RecyclerView goalsRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        goalsRecyclerView = findViewById(R.id.goals_recycler_view);
        goalsRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        goalsRecyclerView.setLayoutManager(layoutManager);


        getGoalDetails();

    }

    private void getGoalDetails(){
        String url = Config.baseUrl + "sessions/goal-details";
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.e("Response","Response is: " + response);
                        ArrayList<Goal> goalsArrayList = new ArrayList<>();
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i=0;i<array.length();i++){
                                JSONObject jsonObject = array.getJSONObject(i);
                                String title = jsonObject.getString("title");
                                String category = jsonObject.getString("category");
                                int progress = jsonObject.getInt("progress");
                                Goal goal = new Goal(title,category,progress);
                                goalsArrayList.add(goal);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter = new GoalsAdapter(goalsArrayList);

                        goalsRecyclerView.setAdapter(adapter);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error",error.getMessage());
//                        mTextView.setText("That didn't work!");
                    }
                }
        );

        ServerConnection.sendMessage(stringRequest);
    }

    public static class Goal{
        private String title;
        private String category;
        private int progress;

        public Goal(String title, String category, int progress) {
            this.title = title;
            this.category = category;
            this.progress = progress;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }
    }
}