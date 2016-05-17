package com.pollhub.pollhub;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.models.BarModel;

import java.text.SimpleDateFormat;


/**
 * Created by Noah on 5/15/2016.
 */
public class Poll_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {
    public final static String SUBISSUE = "com.pollhub.issues.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poll_hub__issues_poll);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_poll);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Poll Data");
        Intent intent = getIntent();
        Bundle da = intent.getExtras();
        ParcelablePoll message = da.getParcelable("com.pollhub.issues.MESSAGE2");
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");

        // Setting Text in View
        TextView org = (TextView)findViewById(R.id.orgA);
        org.setText(message.org);
        TextView date = (TextView)findViewById(R.id.dateA);
        String dateText = (formatter.format(message.date));
        date.setText(dateText);
        TextView title = (TextView)findViewById(R.id.titleA);
        title.setText(message.title);


        BarChart mBarChart = (BarChart) findViewById(R.id.barchart);

        boolean colorChoice = false;
        for(String k : message.data.keySet()){
            int color;
            if(colorChoice){
                colorChoice=!colorChoice;
                color = 0xFFFF4081;
            } else {
                colorChoice=!colorChoice;
                color = 0xFF3F51B5;
            }

            String value = (String)message.data.get(k);
            value = value.replace("%", "");
            mBarChart.addBar(new BarModel(k, Float.parseFloat(value),color));
        }

        mBarChart.startAnimation();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_poll);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_poll);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_poll);
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
        } else if (id == R.id.action_search){
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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.candidates) {
            Intent intent = new Intent(this, CandidateSelection.class);
            startActivity(intent);
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