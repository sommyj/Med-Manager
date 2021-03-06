package com.sommy.android.med_manager.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.sommy.android.med_manager.IdlingResource.SimpleIdlingResource;
import com.sommy.android.med_manager.R;
import com.sommy.android.med_manager.model.ExpandedMenuModel;
import com.sommy.android.med_manager.provider.MedicationContract;
import com.sommy.android.med_manager.sync.ReminderUtilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.sommy.android.med_manager.provider.MedicationContract.MedicationEntry.COLUMN_START_DATE;
import static com.sommy.android.med_manager.provider.MedicationContract.MedicationEntry.CONTENT_URI;

public class MainActivity extends AppCompatActivity implements MedicationListAdapter.MedicationListOnClickHandler,
        LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MEDICATION_LOADER_ID = 100;

    private RecyclerView mMedicationRecyclerView;
    private ProgressBar mLoadingIndicator;
    private MedicationListAdapter medicationListAdapter;
    private TextView mTutorialTextView;
    private TextView mNoResultTextView;
    private MenuItem searchItem;

    //Expandable List
    private DrawerLayout mDrawerLayout;
    private ExpandableListAdapter mMenuAdapter;
    private ExpandableListView expandableList;
    private List<ExpandedMenuModel> listDataHeader;
    private HashMap<ExpandedMenuModel, List<String>> listDataChild;

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    private Cursor mCursor;

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

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
        mNoResultTextView = findViewById(R.id.noResult_textView);
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


        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                // Retrieve the id of the task to delete
                final int id = (int) viewHolder.itemView.getTag();


                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(MainActivity.this);
                }
                builder.setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Build appropriate uri with String row id appended
                                String stringId = Integer.toString(id);
                                Uri uri = CONTENT_URI;
                                uri = uri.buildUpon().appendPath(stringId).build();
                                //Delete a single row of data using a ContentResolver
                                getContentResolver().delete(uri, null, null);

                                Log.d(TAG, stringId+"-----------");
                                //Cancel dispatcher
                                ReminderUtilities.cancelDispatcher(stringId);

                                //Restart the loader to re-query for all tasks after a deletion
                                getSupportLoaderManager().restartLoader(MEDICATION_LOADER_ID, null, MainActivity.this);

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Restart the loader to re-query for all tasks
                                getSupportLoaderManager().restartLoader(MEDICATION_LOADER_ID, null, MainActivity.this);
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }).attachToRecyclerView(mMedicationRecyclerView);

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

        //helper method for expandableOnClickListener
        expandableOnClickListener();





        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startAddMedicationActivityIntent = new Intent(MainActivity.this, AddMedicationActivity.class);
                startActivity(startAddMedicationActivityIntent);
            }
        });

        //show loading indicator
        showLoading();

        /*
         * Ensures a loader is initialized and active. If the loader doesn't already exist, one is
         * created and (if the activity is currently started) starts the loader. Otherwise
         * the last created loader is re-used.
         */
        getSupportLoaderManager().initLoader(MEDICATION_LOADER_ID, null, this);

        Toast.makeText(this,getResources().getString(R.string.tutorial_message2), Toast.LENGTH_SHORT).show();

        // Get the IdlingResource instance
        getIdlingResource();
    }

    /**
     * List labels for expandable view
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        ExpandedMenuModel item1 = new ExpandedMenuModel();
        item1.setTitleName("Month Category");
        // Adding data header
        listDataHeader.add(item1);

        // Adding child data
        List<String> heading1 = new ArrayList<String>();
        heading1.add("All");
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

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchItem = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) searchItem
                .getActionView();

        searchView.setOnQueryTextListener(this);
        return true;
    }

    /**
     * Called when the query text is submitted by the user
     * @param query
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        searchfilter(query);
        return true;
    }

    /**
     * Called when the query text is changed by the user.
     * @param newText
     * @return
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        searchfilter(newText);
        return true;
    }

    /**
     * Called when the user submits the query.
     * @param item
     * @return
     */
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
            SearchView searchView = (SearchView) searchItem
                    .getActionView();
            if (!searchView.isIconified()) {
                searchView.setIconified(true);
            }

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

                        // Update the UI based on the item selected
                        int id = item.getItemId();

                        switch (id){
                            case R.id.nav_updateProfile :
                                Intent startUpdateProfileActivityIntent = new Intent(MainActivity.this, UpdateProfileActivity.class);
                                startActivity(startUpdateProfileActivityIntent);
                                break;

                            case R.id.nav_addMedication :
                                Intent startAddMedicationActivityIntent = new Intent(MainActivity.this, AddMedicationActivity.class);
                                startActivity(startAddMedicationActivityIntent);
                                break;

                            case R.id.nav_logout :
                                finish();


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

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }

        switch (loaderId) {

            case MEDICATION_LOADER_ID:
        Uri medication_uri = null;


            medication_uri = CONTENT_URI;
            String selection = null;
            String[] selectionArgs = null;

        return new CursorLoader(this, medication_uri, null, selection, selectionArgs, COLUMN_START_DATE+" DESC");

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

        mCursor =  data;

        List<Integer> id = new ArrayList<>();
        List<String> name = new ArrayList<>();
        List<String> description = new ArrayList<>();
        List<Integer> interval = new ArrayList<>();
        List<Long> startDate = new ArrayList<>();
        List<Long> endDate = new ArrayList<>();

        // Indices for the _id, name, description, interval, start_date and end_date columns
        int idIndex = data.getColumnIndex(MedicationContract.MedicationEntry._ID);
        int nameIndex = data.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_NAME);
        int descriptionIndex = data.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_DESCRIPTION);
        int intervalIndex = data.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_INTERVAL);
        int startDateIndex = data.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_START_DATE);
        int endDateIndex = data.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_END_DATE);

        int cursorCount = mCursor.getCount();

        data.moveToFirst();

        for(int position=0; position < cursorCount; position++) {
            data.moveToPosition(position); // get to the right location in the mCursor

            id.add(data.getInt(idIndex));
            name.add(data.getString(nameIndex));
            description.add(data.getString(descriptionIndex));
            interval.add(data.getInt(intervalIndex));
            startDate.add(data.getLong(startDateIndex));
            endDate.add(data.getLong(endDateIndex));
        }

        //passing data to the adapter
        medicationListAdapter.setUpMedicationData(id, name, description, interval, startDate, endDate);

        if(data.getCount() == 0)
            showTutorialTextView();
        else
        showMedicationDataView();

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
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
        medicationListAdapter.setUpMedicationData(null, null, null, null, null, null);
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
        mTutorialTextView.setVisibility(View.INVISIBLE);
        /* hide the "no result" message */
        mNoResultTextView.setVisibility(View.INVISIBLE);
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
        /* hide the the tutorial message */
        mTutorialTextView.setVisibility(View.INVISIBLE);
         /* hide the "no result" message */
        mNoResultTextView.setVisibility(View.INVISIBLE);
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
         /* hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* hide the "no result" message */
        mNoResultTextView.setVisibility(View.INVISIBLE);
        /* Finally, show the tutorial message */
        mTutorialTextView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the "no result" message visible and hide the medication list View
     <p>
     * Since it is okay to redundantly set the visibility of a View, we don't need to check whether
     * each view is currently visible or invisible.
     */
    private void showNoResultTextView(){
        /* Then, hide the medication data */
        mMedicationRecyclerView.setVisibility(View.INVISIBLE);
         /* hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* hide the the tutorial message */
        mTutorialTextView.setVisibility(View.INVISIBLE);
        /* Finally, show the "no result" message */
        mNoResultTextView.setVisibility(View.VISIBLE);

    }

    //helper method for expandableOnClickListener
    private void expandableOnClickListener(){
        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
                //Log.d("DEBUG", "submenu item clicked");
                Bundle startDateBundle = new Bundle();
                String startDateBundleKey = "startDate";
                if(groupPosition == 0) {
                    //passing month of start date to loader manager.
                    switch (childPosition) {
                        case 0:
                            getSupportLoaderManager().restartLoader(MEDICATION_LOADER_ID, null, MainActivity.this);
                            break;
                        case 1:
                            filterDates(1);
                            break;
                        case 2:
                            filterDates(2);
                            break;
                        case 3:
                            filterDates(3);
                            break;
                        case 4:
                            filterDates(4);
                            break;
                        case 5:
                            filterDates(5);
                            break;
                        case 6:
                            filterDates(6);
                            break;
                        case 7:
                            filterDates(7);
                            break;
                        case 8:
                            filterDates(8);
                            break;
                        case 9:
                            filterDates(9);
                            break;
                        case 10:
                            filterDates(10);
                            break;
                        case 11:
                            filterDates(11);
                            break;
                        case 12:
                            filterDates(12);
                            break;
                        default:
                    }
                }


                mDrawerLayout.closeDrawers();
                return true;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                //Log.d("DEBUG", "heading clicked");
                return false;
            }
        });

    }

    /**
     * This method filters the recycler view according to the month the medication was registered(startdate)
     * @param monthSelected
     */
    public void filterDates(int monthSelected){
        int cursorCount = mCursor.getCount();

        List<Integer> id = new ArrayList<>();
        List<String> name = new ArrayList<>();
        List<String> description = new ArrayList<>();
        List<Integer> interval = new ArrayList<>();
        List<Long> startDate = new ArrayList<>();
        List<Long> endDate = new ArrayList<>();

        // Indices for the _id, name, description, interval, start_date and end_date columns
        int idIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry._ID);
        int nameIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_NAME);
        int descriptionIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_DESCRIPTION);
        int intervalIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_INTERVAL);
        int startDateIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_START_DATE);
        int endDateIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_END_DATE);

        for(int position=0; position < cursorCount; position++){
            mCursor.moveToPosition(position); // get to the right location in the mCursor


            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("M", Locale.ENGLISH);
            Date dateDate = new Date(mCursor.getLong(startDateIndex));
            String startDateMonth = simpleDateFormat.format(dateDate);


            // Determine the values of the wanted data
            if(monthSelected == Integer.parseInt(startDateMonth) ) {

                id.add(mCursor.getInt(idIndex));
                name.add(mCursor.getString(nameIndex));
                description.add(mCursor.getString(descriptionIndex));
                interval.add(mCursor.getInt(intervalIndex));
                startDate.add(mCursor.getLong(startDateIndex));
                endDate.add(mCursor.getLong(endDateIndex));

                showMedicationDataView();
            }

        }

        if(id.isEmpty() && name.isEmpty() && description.isEmpty() && interval.isEmpty()&& startDate.isEmpty() && endDate.isEmpty()){
            showNoResultTextView();
        }

        //passing data to the adapter
        medicationListAdapter.setUpMedicationData(id, name, description, interval, startDate, endDate);
    }

    /**
     * This method performs the search operation on the recycler view
     * @param searchValue
     */
    public void searchfilter(String searchValue){

        int cursorCount = mCursor.getCount();

        List<Integer> id = new ArrayList<>();
        List<String> name = new ArrayList<>();
        List<String> description = new ArrayList<>();
        List<Integer> interval = new ArrayList<>();
        List<Long> startDate = new ArrayList<>();
        List<Long> endDate = new ArrayList<>();

        // Indices for the _id, name, description, interval, start_date and end_date columns
        int idIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry._ID);
        int nameIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_NAME);
        int descriptionIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_DESCRIPTION);
        int intervalIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_INTERVAL);
        int startDateIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_START_DATE);
        int endDateIndex = mCursor.getColumnIndex(MedicationContract.MedicationEntry.COLUMN_END_DATE);

        for(int position=0; position < cursorCount; position++){
            mCursor.moveToPosition(position); // get to the right location in the mCursor

            String searchName = mCursor.getString(nameIndex);
            String searchDescription = mCursor.getString(descriptionIndex);

            // Determine the values of the wanted data
            if(searchName.toLowerCase().contains(searchValue.toLowerCase()) ||
                    searchDescription.toLowerCase().contains(searchValue.toLowerCase())) {

                id.add(mCursor.getInt(idIndex));
                name.add(mCursor.getString(nameIndex));
                description.add(mCursor.getString(descriptionIndex));
                interval.add(mCursor.getInt(intervalIndex));
                startDate.add(mCursor.getLong(startDateIndex));
                endDate.add(mCursor.getLong(endDateIndex));

                showMedicationDataView();
            }

        }

        if(id.isEmpty() && name.isEmpty() && description.isEmpty() && interval.isEmpty()&& startDate.isEmpty() && endDate.isEmpty()){
            showNoResultTextView();
        }

        //passing data to the adapter
        medicationListAdapter.setUpMedicationData(id, name, description, interval, startDate, endDate);
    }

}
