package com.kidane.yosief.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by Exchange on 10/22/2015.
 */
public class ContactProvider extends ContentProvider {
    public static final String LOGCAT = "Provider";
    static final String PROVIDER_NAME = "com.kidane.yosief.contentprovider.Contacts";
    static final String URL = "content://" + PROVIDER_NAME + "/contacts";
    static final Uri CONTENT_URI = Uri.parse(URL);


    static final String _ID = "id";
    static final String NAME = "name";
    static final String PHONE = "phone";
    private static HashMap<String, String> CONTACTS_PROJECTION_MAP;
    static final int CONTACTS = 1;
    static final int CONTACT_ID = 2;
    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "contacts", CONTACTS);
        uriMatcher.addURI(PROVIDER_NAME, "contacts/#", CONTACT_ID);
    }
    private SQLiteDatabase db;
    static final String DATABASE_NAME = "PhoneBook";
    static final String CONTACTS_TABLE_NAME = "contacts";
    static final int DATABASE_VERSION = 1;
    static final String CREATE_DB_TABLE =
            " CREATE TABLE " + CONTACTS_TABLE_NAME + " ( " +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    NAME + " TEXT NOT NULL, " +
                   PHONE + " TEXT NOT NULL);";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_DB_TABLE);
            Log.i(LOGCAT, "Sqlite Created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " +  CONTACTS_TABLE_NAME);
            onCreate(db);
            Log.i(LOGCAT, "Sqlite Updated");
        }
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        Log.i(LOGCAT, "provider Created");
        /**
         * Create a write able database which will trigger its
         * creation if it doesn't already exist.
         */
        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CONTACTS_TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case CONTACTS:
                qb.setProjectionMap(CONTACTS_PROJECTION_MAP);
                break;

            case CONTACT_ID:
                qb.appendWhere( _ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on student names
             */
            sortOrder = NAME;
        }
        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);

        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }


        @Override
    public String getType(Uri uri) {
            switch (uriMatcher.match(uri)){
                /**
                 * Get all student records
                 */
                case CONTACTS:
                    return "vnd.android.cursor.dir/vnd.example.students";

                /**
                 * Get a particular student
                 */
                case CONTACT_ID:
                    return "vnd.android.cursor.item/vnd.example.students";

                default:
                    throw new IllegalArgumentException("Unsupported URI: " + uri);
            }
        }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /**
         * Add a new student record
         */
        long rowID = db.insert(	CONTACTS_TABLE_NAME, "", values);

        /**
         * If record is added successfully
         */

        if (rowID > 0)
        {
            Toast.makeText(getContext(),"Done",Toast.LENGTH_LONG).show();
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to add a record into " + uri);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case CONTACTS:
                count = db.delete(CONTACTS_TABLE_NAME, selection, selectionArgs);
                break;

            case CONTACT_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete( CONTACTS_TABLE_NAME, _ID +  " = " + id +
                        (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        Toast.makeText(getContext(),"deleted",Toast.LENGTH_LONG).show();
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all student records
             */
            case CONTACTS:
                return 0;//"vnd.android.cursor.dir/vnd.example.students";

            /**
             * Get a particular student
             */
            case CONTACT_ID:
                return 0;//"vnd.android.cursor.item/vnd.example.students";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
}
