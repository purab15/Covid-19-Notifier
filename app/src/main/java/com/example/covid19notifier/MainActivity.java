package com.example.covid19notifier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    ListView listView;
    SearchView searchView;
    RequestQueue requestQueue;
    SwipeRefreshLayout swiperefresh;
    ArrayList<String> arrayList;
    ArrayAdapter arrayAdapter;
    final String channelid = "channel1";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swiperefresh = findViewById(R.id.swiperefresh);
        listView = findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        requestQueue = Volley.newRequestQueue(this);
        String url1 = "https://api.covid19api.com/summary";
        final JsonObjectRequest globalrequest = new JsonObjectRequest(Request.Method.GET, url1, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject global = response.getJSONObject("Global");
                    int globalcount = global.getInt("TotalConfirmed");
                    arrayList.add(0, "World = " + globalcount);
                    listView.setAdapter(arrayAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(globalrequest);
        String url = "https://api.covid19india.org/data.json";
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("statewise");
                    JSONObject states = jsonArray.getJSONObject(0);
                    int count = states.getInt("confirmed");
                    arrayList.add("India = " + count);
                    for (int i = 1; i < jsonArray.length(); i++) {
                        states = jsonArray.getJSONObject(i);
                        count = states.getInt("confirmed");
                        String state = states.getString("state");
                        if(state.equals("State Unassigned"))
                        {
                            continue;
                        }
                        arrayList.add(state + " = " + count);
                    }
                    listView.setAdapter(arrayAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String y=listView.getAdapter().getItem(position).toString();
                boolean z=arrayList.contains(y);
                if(z)
                {
                    position=arrayList.indexOf(y);

                }
                Intent intent = new Intent(MainActivity.this, Details.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        swiperefresh.setColorSchemeColors(getResources().getColor(android.R.color.holo_red_dark));
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                arrayList.clear();
                requestQueue.add(globalrequest);
                requestQueue.add(request);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swiperefresh.setRefreshing(false);
                    }
                }, 2 * 1000);
            }
        });



        SearchView searchView = (SearchView) findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

               arrayAdapter.getFilter().filter(newText);
                return true;
            }
        });
        searchView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode==KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN)
                {
                    InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        





        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "channel1")
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle("Covid'19 Update")
                .setContentText("New update in the number of cases")
                .setColor(getResources().getColor(android.R.color.holo_red_dark))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(0, builder.build());*/


    }
    }




