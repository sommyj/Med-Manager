package com.sommy.android.med_manager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.sommy.android.med_manager.model.ExpandedMenuModel;
import com.sommy.android.med_manager.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MedicationListAdapter.MedicationListOnClickHandler {

    private RecyclerView mMedicationRecyclerView;
    private MedicationListAdapter medicationListAdapter;

    //Expandable List
    private DrawerLayout mDrawerLayout;
    private ExpandableListAdapter mMenuAdapter;
    private ExpandableListView expandableList;
    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<String>> listDataChild;

     String[] a = {"cofflin", "lonart", "Amartem"};
     String[] b = {"2 hrs", "2 hrs", "3 hrs"};
     String[] c = {"lorem pepsin, trun, fjfjjf kfkfkf losem kfffmfm,fmffkfkf fkkrkrkrkkkfkkflfk kkfkfk",
            "lorem pepsin, trun, fjfjjf kfkfkf losem kfffmfm,fmffkfkf fkkrkrkrkkkfkfklflfk kkfkfk",
            "lorem pepsin, trun, fjfjjf kfkfkf losem kfffmfm,fmffkfkf fkkrkrkrkkkfklflkflfk kkfkfk"};
     String[] d = {"2 hrs", "2 days", "3 days"};
    String[] e = {"4 hrs", "5 days", "6 days"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mMedicationRecyclerView = findViewById(R.id.medication_recyclerView);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mMedicationRecyclerView.setLayoutManager(layoutManager);

        mMedicationRecyclerView.setHasFixedSize(true);
        medicationListAdapter = new MedicationListAdapter(this, this, a,b,c,d,e);

        mMedicationRecyclerView.setAdapter(medicationListAdapter);

        //Expandable List
        expandableList = findViewById(R.id.navigationMenu);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);

        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        prepareListData();
        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild, expandableList);

        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);

        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                //Log.d("DEBUG", "submenu item clicked");
                    mDrawerLayout.closeDrawers();
                return true;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                //Log.d("DEBUG", "heading clicked");
                if(groupPosition == 2) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    return true;
                }
                return false;
            }
        });




        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startAddMedicationActivityIntent = new Intent(MainActivity.this, AddMedictionActivity.class);
                startActivity(startAddMedicationActivityIntent);
            }
        });


    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        ExpandedMenuModel item1 = new ExpandedMenuModel();
        item1.setTitleName("Month Category");
        // Adding data header
        listDataHeader.add(item1);

        // Adding child data
        List<String> heading1 = new ArrayList<String>();
        heading1.add("January");
        heading1.add("February");
        heading1.add("March");
        heading1.add("April");
        heading1.add("May");
        heading1.add("June");
        heading1.add("July");
        heading1.add("August");
        heading1.add("September");
        heading1.add("October");
        heading1.add("November");
        heading1.add("December");

        listDataChild.put(listDataHeader.get(0), heading1);// Header, Child data

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.app_bar_search) {
            return true;
        }
        else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        // set item as selected to persist highlight
                        item.setChecked(true);

                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        int id = item.getItemId();

                        switch (id){
                            case R.id.nav_updateProfile :
                                Intent startUpdateProfileActivityIntent = new Intent(MainActivity.this, UpdateProfileActivity.class);
                                startActivity(startUpdateProfileActivityIntent);
                                break;

                            case R.id.nav_addMedication :
                                Intent startAddMedicationActivityIntent = new Intent(MainActivity.this, AddMedictionActivity.class);
                                startActivity(startAddMedicationActivityIntent);
                                break;

                            case R.id.nav_logout :
                                Snackbar.make(findViewById(android.R.id.content), "Relax it is coming", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();


                        }

                        return true;
                    }
                }
        );
    }

    /**
     * If an item is clicked it launches you to the child class(MedicationDetailsActivity.java).
     * and it passes some info with the use of intent.
     * @param strings The Medication data to be displayed.
     */
    @Override
    public void onClick(String[] strings) {
        Intent startMedicationDetailsActivityIntent = new Intent(MainActivity.this, MedicationDetailsActivity.class);
        startMedicationDetailsActivityIntent.putExtra(Intent.EXTRA_TEXT, strings);
        startActivity(startMedicationDetailsActivityIntent);
    }

    /**
     * If drawer is opened close it before exiting the application
     */
    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
        }else{
            super.onBackPressed();
        }

    }
}
