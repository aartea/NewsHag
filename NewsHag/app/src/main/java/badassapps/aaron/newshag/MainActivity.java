package badassapps.aaron.newshag;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import android.support.v4.content.Loader;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.SearchView;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity{

    ArrayList<Article> mList;
    CustomAdapter adapter;
    ListView listView;
    LayoutInflater layoutInflater;

    // Constants
    // Content provider authority
    public static final String AUTHORITY = "badassapps.aaron.newshag.AppContentProvider";
    // Account type
    public static final String ACCOUNT_TYPE = "example.com";
    // Account
    public static final String ACCOUNT = "default_account";

    Account mAccount;

    // Global variables
    // A content resolver for accessing the provider
    ContentResolver mResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAccount = createSyncAccount(this);

        mList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        final Cursor cursor = getContentResolver().query(AppContentProvider.CONTENT_URI,null,null,null,null);
        adapter = new CustomAdapter(this, cursor, 0);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(MainActivity.this, Top10NewsD.class);
                cursor.moveToPosition(position);

                myIntent.putExtra("title", cursor.getString(cursor.getColumnIndex(NewsDBOpenHelper
                        .COL_TITLE)));
                myIntent.putExtra("abstract", cursor.getString(cursor.getColumnIndex(NewsDBOpenHelper
                        .COL_ABSTRACT)));
                myIntent.putExtra("thumbnail", cursor.getString(cursor.getColumnIndex(NewsDBOpenHelper
                        .COL_THUMBNAIL)));
                myIntent.putExtra("url", cursor.getString(cursor.getColumnIndex(NewsDBOpenHelper
                        .COL_URL)));
                startActivity(myIntent);
            }
        });


        //Step 1 (for content resolver)
        //new Handler
        getContentResolver().registerContentObserver(AppContentProvider.CONTENT_URI,true,new
                NewsContentObserver
                (new Handler()));

        //Performs manual sync
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */

        //REQUESTS A SYNC FOR THE ACCOUNT
        //i.e. if there's no cache, or app hasn't been used for several days...
        ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);

        ContentResolver.setSyncAutomatically(mAccount,AUTHORITY,true);
        ContentResolver.addPeriodicSync(
                mAccount,
                AUTHORITY,
                Bundle.EMPTY,
                60);
    }

    public void clickingFavs(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, FavoritesD.class);
        startActivity(intent);
    }

    //CustomAdapter for our Cursor
    public class CustomAdapter extends CursorAdapter {
        private LayoutInflater cursorInflater;

        public CustomAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
            cursorInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.list_items, parent, false);
        }
        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            // Find fields to populate in inflated template
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView abstract1 = (TextView) view.findViewById(R.id.abstract1);
            ImageView image = (ImageView) view.findViewById(R.id.image);

            // Extract properties from cursor
//            String urlString = cursor.getString(cursor.getColumnIndexOrThrow("url"));
            String titleString = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String imageString = cursor.getString(cursor.getColumnIndexOrThrow("thumbnail_" +
                    "standard"));
            String abstractString = cursor.getString(cursor.getColumnIndexOrThrow("abstract"));

            // Populate fields with extracted properties
            abstract1.setText(abstractString);
            if (imageString != null && ! imageString.equals("")){ Picasso.with(MainActivity.this)
                    .load
                    (imageString).into
                    (image);
                title.setText(titleString);}
        }
    }


    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account createSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                ACCOUNT, ACCOUNT_TYPE);
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
        } else {
            /*
             * The account exists or some other error occurred. Log this, report it,
             * or handle it internally.
             */
        }
        return newAccount;
    }

    public class NewsContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public NewsContentObserver(Handler handler) {
            super(handler);
        }


        @Override
        public void onChange(boolean selfChange, Uri uri) {
            //do stuff on UI thread
            Cursor cursor = adapter.swapCursor(getContentResolver().query(AppContentProvider
                    .CONTENT_URI,
                    null, null,
                    null, null));
//            if (cursor != null) {
//                cursor.close();
//                }
        }
    }
}

