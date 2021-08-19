package com.example.covid19notifier;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Formatter;

public class Details extends AppCompatActivity {
    RequestQueue Queue;
    ListView listView1;
    ArrayList<String> List;
    ArrayAdapter Adapter;
    ActionBar actionBar;
    int x;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        listView1= findViewById(R.id.listView);

         List=new ArrayList<String>();
        Adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,List);
        Queue= Volley.newRequestQueue(this);
        Intent intent = getIntent();
         x=intent.getIntExtra("position",0);
         actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if(x==0)
        {
            String url2="https://api.covid19api.com/summary";
            JsonObjectRequest globalreq=new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject globe=response.getJSONObject("Global");
                        int newconfirmed=globe.getInt("NewConfirmed");
                        int totaldeaths=globe.getInt("TotalDeaths");
                        int totalrec=globe.getInt("TotalRecovered");
                        int newdeaths=globe.getInt("NewDeaths");
                        int newrec=globe.getInt("NewRecovered");
                        int activecases=globe.getInt("TotalConfirmed")-totalrec;
                        String recoveryrate= Double.toString(totalrec/globe.getDouble("TotalConfirmed")*100);
                        String deathsrate=Double.toString(totaldeaths/globe.getDouble("TotalConfirmed")*100);
                        String activecasesrate=Double.toString(activecases/globe.getDouble("TotalConfirmed")*100);
                        List.add("Active cases = "+activecases);
                        List.add("New Confirmed = "+ newconfirmed);
                        List.add("Total Deaths = "+ totaldeaths);
                        List.add("Total Recovered = "+ totalrec);
                        List.add("New Deaths = "+ newdeaths);
                        List.add("New Recovered = "+newrec);
                        List.add("Active Rate = " +activecasesrate.substring(0,5)+"%");
                        List.add("Death Rate = " +deathsrate.substring(0,5)+"%");
                        List.add("Recovery Rate = " +recoveryrate.substring(0,5)+"%");
                        listView1.setAdapter(Adapter);
                        actionBar.setTitle("World Stats");
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
            Queue.add(globalreq);
        }
        else
        {
            String url3="https://api.covid19india.org/data.json";
            JsonObjectRequest req=new JsonObjectRequest(Request.Method.GET, url3, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray Array=response.getJSONArray("statewise");
                        JSONObject state;
                        if(x>10)
                        {
                            state=Array.getJSONObject(x);
                        }
                        else
                        {
                            state=Array.getJSONObject(x-1);
                        }

                            int deaths=state.getInt("deaths");
                            int rec=state.getInt("recovered");
                            int deldeaths=state.getInt("deltadeaths");
                            int delrec=state.getInt("deltarecovered");
                            int active=state.getInt("active");
                            int delconfirm=state.getInt("deltaconfirmed");
                            String lastupdate=state.getString("lastupdatedtime");
                            String statename=state.getString("state");
                            if(statename.equals("Total"))
                            {
                                actionBar.setTitle("India Stats");
                            }
                            else
                            {
                                actionBar.setTitle(statename+" Stats");
                            }
                            Double recrate,deathrate,activerate;
                            recrate= (rec/state.getDouble("confirmed")*100);
                            deathrate=(deaths/state.getDouble("confirmed")*100);
                            activerate=(active/state.getDouble("confirmed"))*100;
                            DecimalFormat df = new DecimalFormat("#.###");
                            df.setRoundingMode(RoundingMode.CEILING);
                            List.add("Active cases= "+ active);
                            List.add("Deaths = "+ deaths);
                            List.add("Recovered = "+ rec);
                            List.add("Active Rate = " +df.format(activerate)+"%");
                            List.add("Death Rate = " +df.format(deathrate)+"%");
                            List.add("Recovery Rate = " +df.format(recrate)+"%");
                            List.add("Last updated Time : "+lastupdate);
                        listView1.setAdapter(Adapter);
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
            Queue.add(req);
        }

    }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    // app icon in action bar clicked; go home
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }


    }
