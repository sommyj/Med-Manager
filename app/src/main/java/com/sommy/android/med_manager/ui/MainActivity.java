package com.sommy.android.med_manager.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sommy.android.med_manager.R;
import com.sommy.android.med_manager.model.ExpandedMenuModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.sommy.android.med_manager.provider.MedicationContract.MedicationEntry.COLUMN_START_DATE;
import static com.sommy.android.med_manager.provider.MedicationContract.MedicationEntry.CONTENT_URI;

public class MainActivity extends AppCompatActivity implements MedicationListAdapter.MedicationListOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final int MEDICATION_LOADER_ID = 100;

    private RecyclerView mMedicationRecyclerView;
    private ProgressBar mLoadingIndicator;
    private MedicationListAdapter medicationListAdapter;
    private TextView mTutorialTextView;

    //Expandable List
    private DrawerLayout mDrawerLayout;
    private ExpandableListAdapter mMenuAdapter;
    private ExpandableListView expandableList;
    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        mTutorialTextView = findViewById(R.id.tutorial_textView);
        mMedicationRecyclerView = findViewById(R.id.medication_recyclerView);

        /*
        The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mMedicationRecyclerView.setLayoutManager(layoutManager);

        mMedicationRecyclerView.setHasFixedSize(true);
        medicationListAdapter = new MedicationListAdapter(this, this);

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

        showLoading();

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(MEDICATION_LOADER_ID, null, this);
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
     * @param string The Medication data to be passed to MedicationDetailsActivity.
     */
    @Override
    public void onClick(String string) {
        Intent startMedicationDetailsActivityIntent = new Intent(MainActivity.this, MedicationDetailsActivity.class);
        startMedicationDetailsActivityIntent.putExtra(Intent.EXTRA_TEXT, string);
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

    /**
     * This loader will return medication data as a Cursor or null if an error occurs.
     *
     * Implements the required callbacks to take care of loading data at all stages of loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId) {

            case MEDICATION_LOADER_ID:
        Uri medication_uri = null;


            medication_uri = CONTENT_URI;

            if(args != null)
            medication_uri = medication_uri.buildUpon().appendPath(args.getString("")).build();

        return new CursorLoader(this, medication_uri, null, null, null, COLUMN_START_DATE+" DESC");

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    /**
     * Called when a previously created loader has finished its load.
     *
     * @param loader The Loader that has finished.
     * @param data The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        medicationListAdapter.swapCursor(data);

        if(data.getCount() == 0)
            showTutorialTextView();


        showMedicationDataView();
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        medicationListAdapter.swapCursor(null);
    }

    /**
     * This method will make the View for the medication data visible and hide the tutorial message and
     * loading indicator.
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showMedicationDataView() {
        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Second, hide the tutorial message */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Finally, make sure the medication data is visible */
        mMedicationRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the loading indicator visible and hide the medication list View
     * <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showLoading() {
        /* Then, hide the medication data */
        mMedicationRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the tutorial message visible and hide the medication list View
     <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showTutorialTextView(){
        /* Then, hide the medication data */
        mMedicationRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the tutorial message */
        mTutorialTextView.setVisibility(View.VISIBLE);
    }
}
