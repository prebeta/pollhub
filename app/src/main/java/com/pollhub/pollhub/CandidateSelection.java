package com.pollhub.pollhub;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

public class CandidateSelection extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {

    public final static String SUBISSUE = "com.pollhub.issues.MESSAGE";
    GridView gridView;
    private ArrayList<String> selectedCandidates;
    TextView newText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate__selection);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_selection);
        setSupportActionBar(toolbar);
        loadApps();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        selectedCandidates = new ArrayList<String>();
        ImageButtonAdapter adapter = new ImageButtonAdapter(this);
        newText = (TextView)findViewById(R.id.text);
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(adapter);
        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                String candidate = getCandidate(position);
                if (selectedCandidates.contains(candidate)) {
                    selectedCandidates.remove(candidate);
                } else if (selectedCandidates.size() == 3) {
                    Toast toast = Toast.makeText(CandidateSelection.this, "Only up to three candidates may be selected", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    selectedCandidates.add(candidate);
                }

                if (selectedCandidates.size() == 0) {
                    newText.setText("No candidates selected");
                } else {
                    String text = selectedCandidates.get(0);
                    for (int index = 1; index < selectedCandidates.size(); index++) {
                        text += " and " + selectedCandidates.get(index);
                    }
                    text += " selected";
                    newText.setText(text);
                }


            }
        });
        /*ImageButton clinton_button = (ImageButton) findViewById(R.id.clinton_button);
        clinton_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        */
        Button next_button = (Button) findViewById(R.id.next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedCandidates.size()== 0) {
                    Toast toast = Toast.makeText(CandidateSelection.this, "Please select at least one candidate", Toast.LENGTH_SHORT);
                    //toast.show();
                } else {
                    Intent i = new Intent(getApplicationContext(), CandidateComparison.class);
                    Toast toast = Toast.makeText(CandidateSelection.this, ""+ (selectedCandidates.toArray()), Toast.LENGTH_SHORT);
                    //toast.show();
                    String[] candidatesArray = new String[selectedCandidates.size()];
                    for (int index = 0; index < selectedCandidates.size(); index++) {
                        candidatesArray[index] = selectedCandidates.get(index);
                    }

                    i.putExtra("CANDIDATES", candidatesArray);

                    startActivity(i);
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_selection);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_selection);
        navigationView.setNavigationItemSelectedListener(this);

    }


    private List<ResolveInfo> mApps;

    private void loadApps() {

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
    }

    private String getCandidate(int position){
        String[] ids = {"Clinton", "Trump", "Sanders", "Cruz", "Rubio", "Kasich"};
        return ids[position];
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


    public class MultiChoiceModeListener implements
            GridView.MultiChoiceModeListener {
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Select Items");
            mode.setSubtitle("One item selected");
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return true;
        }

        public void onDestroyActionMode(ActionMode mode) {
        }

        public void onItemCheckedStateChanged(ActionMode mode, int position,
                                              long id, boolean checked) {
            int selectCount = gridView.getCheckedItemCount();
            switch (selectCount) {
                case 1:
                    mode.setSubtitle("One item selected");
                    break;
                default:
                    mode.setSubtitle("" + selectCount + " items selected");
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_selection);
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
