package com.pollhub.pollhub;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CandidateComparison extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {

    public final static String POLL = "com.pollhub.issues.MESSAGE2";
    public final static String SUBISSUE = "com.pollhub.issues.MESSAGE";
    private String[] candidates;
    private HashMap<String, String> fullNames;
    private JSONParser parser;
    private String finalUrl = "http://tonyliang.com/CMSC436/pollhub.json";
    private ListView pollList;
    private ArrayList<Poll> polls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate__comparison);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_comparison);
        setSupportActionBar(toolbar);
        pollList = (ListView)findViewById(R.id.comparison_polls_list);

        parseList();
        setOnClick();
        initializeFullNames();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_comparison);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_comparison);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void parseList() {
        parser = new JSONParser(finalUrl);
        parser.fetchJSON();
        while (parser.parsingComplete) ;
        HashMap<String, Object> data = parser.getMap();
        ArrayList<HashMap<String, Object>> pollData = (ArrayList<HashMap<String, Object>>) data.get("polls");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            candidates = extras.getStringArray("CANDIDATES");
        }

        polls = new ArrayList<Poll>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        for(int i = 0; i < pollData.size(); i++){
            Poll p = new Poll(pollData.get(i));
            boolean relevantPoll = true;
            for (String candidate : candidates) {
                System.out.println(candidates);
                if(! p.data.containsKey(candidate) ) {
                    relevantPoll = false;
                    break;
                }
            }
            if (relevantPoll) {
                for(String st : prefs.getAll().keySet()){
                    if(prefs.getBoolean(st, true) == true && st.equals(p.org))
                        polls.add(p);
                }
            }
        }

        Collections.sort(polls, new Comparator<Poll>() {
            @Override
            public int compare(Poll poll1, Poll poll2) {
                return poll2.date.compareTo(poll1.date);
            }
        });

        PollsAdapter adapter = new PollsAdapter(this, polls);
        pollList.setAdapter(adapter);

    }

    private void setOnClick() {
        pollList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,
                                            int position, long id) {
                        Poll p = (Poll) pollList.getItemAtPosition(position);
                        System.out.println(p.title);
                        Intent i = new Intent(CandidateComparison.this, Poll_Activity.class);
                        i.putExtra(POLL, new ParcelablePoll(p));
                        startActivity(i);
                    }
                }
        );
    }

    private ArrayList<Poll> getRelevantPolls(String[] candidates, JSONParser parser) {
        return new ArrayList<Poll>();
    }

    private void initializeFullNames() {
        fullNames = new HashMap<String, String>();
        fullNames.put("sanders", "Bernie Sanders");
        fullNames.put("clinton", "Hillary Clinton");
        fullNames.put("cruz", "Ted Cruz");
        fullNames.put("trump", "Donald Trump");
        fullNames.put("kasich", "John Kasich");
        fullNames.put("rubio", "Marco Rubio");

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_comparison);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.candidates) {
            this.onBackPressed();
        } else if (id == R.id.issues) {
            Intent intent = new Intent(this, PollHub_Issues.class);
            startActivity(intent);
        } else if (id == R.id.sub_defense) {
            String message = "Defense";
            if(!(this.getIntent().hasExtra(SUBISSUE) && message.equals(this.getIntent().getParcelableExtra(SUBISSUE)))){
                Intent intent = new Intent(this, PollHub_Issues_SubIssue.class);
                intent.putExtra(SUBISSUE, message);
                startActivity(intent);
            }
        } else if (id == R.id.sub_econ) {
            String message = "Economy";
            if(!(this.getIntent().hasExtra(SUBISSUE) && message.equals(this.getIntent().getParcelableExtra(SUBISSUE)))){
                Intent intent = new Intent(this, PollHub_Issues_SubIssue.class);
                intent.putExtra(SUBISSUE, message);
                startActivity(intent);
            }
        } else if (id == R.id.sub_pol) {
            String message = "Current Policy";
            if(!(this.getIntent().hasExtra(SUBISSUE) && message.equals(this.getIntent().getParcelableExtra(SUBISSUE)))){
                Intent intent = new Intent(this, PollHub_Issues_SubIssue.class);
                intent.putExtra(SUBISSUE, message);
                startActivity(intent);
            }
        }
        return true;
    }
}
