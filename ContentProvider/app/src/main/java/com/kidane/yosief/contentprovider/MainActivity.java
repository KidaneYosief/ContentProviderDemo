package com.kidane.yosief.contentprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    Button save,show;
    EditText name, phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        save = (Button)findViewById(R.id.save);
        show = (Button)findViewById(R.id.show);
        name = (EditText)findViewById(R.id.name);
        phone = (EditText)findViewById(R.id.phone);
        save.setFocusable(false);
        //show.setFocusable(false);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        name.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                save.setFocusable(true);
                //show.setFocusable(true);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });
        //Initializing NavigationView
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Checking if the item is in checked state or not, if not make it in checked state
                if(menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);
                //Closing drawer on item click
                drawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()){
                    //Replacing the main content with ContentFragment Which is our Inbox View;

                    default:
                        Toast.makeText(getApplicationContext(), "Somethings Wrong", Toast.LENGTH_SHORT).show();
                        return true;
                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };
        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    public void onClickSave(View v){

        ContentValues values = new ContentValues();
        String mName = name.getText().toString();
        String mPhone = phone.getText().toString();
       // Log.i("Checkinmeee", mName);
        if(mName.equals("")){
            Toast.makeText(this,"please Enter name",Toast.LENGTH_LONG).show();
        }else {
           // Toast.makeText(this,"there",Toast.LENGTH_LONG).show();
            values.put(ContactProvider.NAME, mName);
            values.put(ContactProvider.PHONE, mPhone);
            Uri uri = getContentResolver().insert(ContactProvider.CONTENT_URI, values);
            name.setText("");
            phone.setText("");
        }
    }
    public void onClickShow(View v){
        String URL = ContactProvider.URL;
        Uri contacts = Uri.parse(URL);
       // Cursor c = managedQuery(contacts, null, null, null, "name");
        Cursor c = getContentResolver().query(contacts, null, null, null, "name");

        if (c.moveToFirst()) {
            do{
                Toast.makeText(this,
                        c.getString(c.getColumnIndex(ContactProvider._ID)) +
                                ", " +  c.getString(c.getColumnIndex( ContactProvider.NAME)) +
                                ", " + c.getString(c.getColumnIndex( ContactProvider.PHONE)),
                        Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
        }
    }
     public void onClickDele(View v){
         String mName = name.getText().toString();
         String mPhone = phone.getText().toString();
         String[] me = new String[] {mName};
         int mRowDelt = getContentResolver().delete(ContactProvider.CONTENT_URI, ContactProvider.NAME + " LIKE ?", me);
         name.setText("");
         phone.setText("");
    }
   /* public void onClickUpdt(View v){
        String mName = name.getText().toString();
        String mPhone = phone.getText().toString();
        String[] me = new String[] {mName};
        // Defines an object to contain the updated values
        ContentValues mUpdateValues = new ContentValues();

        String mSelectionClause =ContactProvider.NAME +  "LIKE ?";
        String[] mSelectionArgs = {mName};

        mUpdateValues.putNull(ContactProvider.CONTACTS_TABLE_NAME);

        int mRowsUpdated = getContentResolver().update(
                ContactProvider.CONTENT_URI,   // the user dictionary content URI
                mUpdateValues,                       // the columns to update
                mSelectionClause,                    // the column to select on
                mSelectionArgs                      // the value to compare to
        );
        Toast.makeText(this,"did ome",Toast.LENGTH_LONG).show();
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
