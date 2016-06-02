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
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.SearchView;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

        checkFirstRun();

        mAccount = createSyncAccount(this);
        mList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        adapter = new CustomAdapter(this, R.layout.list_items, mList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(MainActivity.this, Top10NewsD.class);
                myIntent.putExtra("position", position);
                Article article = mList.get(position);
                myIntent.putExtra("article", article);
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

    private void NOTIFICATIONisAllowed() {

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //If there is internet connection then the user will be presented with a notification that displays the top story
        if (networkInfo != null && networkInfo.isConnected()) {
            Intent intent1 = new Intent(MainActivity.this, MainActivity.class);

            PendingIntent pendingIntent1 = PendingIntent.getActivity(MainActivity.this, (int) System.currentTimeMillis(), intent1, 0);

            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.news)).build();
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this);
            mBuilder.setSmallIcon(R.drawable.ic_chrome_reader_mode_black_24dp);
            mBuilder.setContentTitle("BREAKING NEWS!");
            mBuilder.setContentText("The News Hag team: Check out the latest story!");
            mBuilder.setContentIntent(pendingIntent1);
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            mBuilder.setStyle(bigPictureStyle);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, bigPictureStyle.build());
            notificationManager.cancel(6);


        } else {
            //If there is no internet connection present, the user is presented with a notification that lasts 30 seconds with the option to go into settings and turn it on via click
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            Intent intent1 = new Intent(Settings.ACTION_WIFI_SETTINGS);

            PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, (int) System.currentTimeMillis(), intent, 0);
            PendingIntent pendingIntent1 = PendingIntent.getActivity(MainActivity.this, (int) System.currentTimeMillis(), intent1, 0);


            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this);
            mBuilder.setSmallIcon(android.R.drawable.stat_sys_warning);
            mBuilder.setContentTitle("No internet connection!");
            mBuilder.setContentText("To use the app, please enable WIFI, Thanks!");
            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            mBuilder.setStyle(bigTextStyle);
            mBuilder.addAction(android.R.drawable.ic_menu_info_details, "Connect WIFI", pendingIntent1);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(6, bigTextStyle.build());
            notificationManager.cancel(1);
        }

        mList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        adapter = new CustomAdapter(this, R.layout.list_items, mList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(MainActivity.this, Top10NewsD.class);
                myIntent.putExtra("position", position);
                Article article = mList.get(position);
                myIntent.putExtra("article", article);
                startActivity(myIntent);
            }
        });

        mList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        adapter = new CustomAdapter(this, R.layout.list_items, mList);
        listView.setAdapter(adapter);

        //Gut this out later!!
        Article article = new Article("www.google.com", "This is the image", "This is the title");
        Article article2 = new Article("www.yahoo.com", "This is the image", "This is the title");
        Article article3 = new Article("www.yahoo.com", "This is the image", "This is the title");
        Article article4 = new Article("www.yahoo.com", "This is the image", "This is the title");
        Article article5 = new Article("www.yahoo.com", "This is the image", "This is the title");
        Article article6 = new Article("www.yahoo.com", "This is the image", "This is the title");


        mList.add(article);

        mList.add(article2);

        mList.add(article3);

        mList.add(article4);

        mList.add(article5);

        mList.add(article6);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(MainActivity.this, Top10NewsD.class);
                myIntent.putExtra("position", position);
                Article article = mList.get(position);
                myIntent.putExtra("article", article);
                startActivity(myIntent);
            }
        });

    }

    public void clickingFavs(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, FavoritesD.class);
        startActivity(intent);
    }

    public class CustomAdapter extends ArrayAdapter {

        Context mContext;
        int mLayoutResource;
        ArrayList<Article> mList;

        public CustomAdapter(Context context, int layoutResource, ArrayList<Article> list) {
            super(context, layoutResource, list);
            mContext = context;
            mLayoutResource = layoutResource;
            mList = list;
            layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                convertView = layoutInflater.inflate(R.layout.list_items, parent, false);
                TextView title = (TextView) convertView.findViewById(R.id.title);
                title.setText(mList.get(position).getTITLE());
                TextView url = (TextView) convertView.findViewById(R.id.url);
                url.setText(mList.get(position).getURL());
                TextView image = (TextView) convertView.findViewById(R.id.image);
                image.setText(mList.get(position).getID());

            }
            return convertView;
        }
    }


    //Will inflate our menu's search functionality
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search, menu);
        inflater.inflate(R.menu.access_db, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }


    public void checkFirstRun() {
        boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun", true);
        if (isFirstRun) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Would you like to recieve the latest news from News Hag?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes please!",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(MainActivity.this, "Great you'll recieve stuff!", Toast.LENGTH_SHORT).show();
                            dialog.cancel();

                            NOTIFICATIONisAllowed();

                            return;


                        }
                    });

            builder1.setNegativeButton(
                    "No thanks.",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(MainActivity.this, "Oh okay, nothing then", Toast.LENGTH_SHORT).show();
                            dialog.cancel();


                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();


            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isFirstRun", false)
                    .apply();
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
//            adapter.swapCursor
//                    (getContentResolver().query(AppContentProvider.CONTENT_URI, null, null,
//                    null, null));
        }
    }
}

