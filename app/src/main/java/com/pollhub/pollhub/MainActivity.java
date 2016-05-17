package com.pollhub.pollhub;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public final static String SUBISSUE = "com.pollhub.issues.MESSAGE";
    public final static String POLL = "com.pollhub.issues.MESSAGE2";
    private ListView list;
    private PollsAdapter adapter;
    private JSONParser parser;
    private String finalUrl = "http://tonyliang.com/CMSC436/pollhub.json";
    private ArrayList<Poll> polls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        list = (ListView) findViewById(R.id.list);
        parseList();
        setOnClick();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void parseList() {
        parser = new JSONParser(finalUrl);
        parser.fetchJSON();
        while (parser.parsingComplete) ;
        HashMap<String, Object> data = parser.getMap();
        ArrayList<HashMap<String, Object>> pollData = (ArrayList<HashMap<String, Object>>) data.get("polls");

        polls = new ArrayList<Poll>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        for(int i = 0; i < pollData.size(); i++){
            Poll p = new Poll(pollData.get(i));
            for(String s : prefs.getAll().keySet()){
                if(prefs.getBoolean(s, true) == true && s.equals(p.org))
                    polls.add(p);
            }
        }

        Collections.sort(polls, new Comparator<Poll>() {
            @Override
            public int compare(Poll poll1, Poll poll2) {
                return poll2.date.compareTo(poll1.date);
            }
        });
        adapter = new PollsAdapter(this, polls);
        list.setAdapter(adapter);
    }

    private void setOnClick() {
        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {
                        Poll p = (Poll) list.getItemAtPosition(position);
                        Intent i = new Intent(MainActivity.this,Poll_Activity.class);
                        i.putExtra(POLL, new ParcelablePoll(p));
                        startActivity(i);
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pollhub_appbar_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(android.R.id.content, new SettingsFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else if (id == R.id.action_search){
            Intent i = new Intent(this,Poll_Search.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            this.onBackPressed();
        } else if (id == R.id.candidates) {
            Intent intent = new Intent(this,CandidateSelection.class);
            startActivity(intent);
        } else if (id == R.id.issues) {
            Intent intent = new Intent(this,PollHub_Issues.class);
            startActivity(intent);
        } else if (id == R.id.sub_defense) {
            Intent intent = new Intent(this,PollHub_Issues_SubIssue.class);
            String message = "Defense";
            intent.putExtra(SUBISSUE,message);
            startActivity(intent);
        } else if (id == R.id.sub_econ) {
            Intent intent = new Intent(this,PollHub_Issues_SubIssue.class);
            String message = "Economy";
            intent.putExtra(SUBISSUE,message);
            startActivity(intent);
        } else if (id == R.id.sub_pol) {
            Intent intent = new Intent(this,PollHub_Issues_SubIssue.class);
            String message = "Current Policy";
            intent.putExtra(SUBISSUE,message);
            startActivity(intent);
        }
        return true;
    }
}
