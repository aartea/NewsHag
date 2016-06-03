package badassapps.aaron.newshag;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class FavoritesD extends AppCompatActivity {

    ArrayList<Article> mList;
    CustomAdapter adapter;
    ListView listView;
    LayoutInflater layoutInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_d);

        if (isFirstTime()) {
            deleteOnLongPress();
        }


        mList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView2);
        adapter = new CustomAdapter(this, R.layout.fav_items, mList);
        listView.setAdapter(adapter);


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mList.remove(position);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIntent = new Intent(FavoritesD.this, WebViewForFavorites.class);
                myIntent.putExtra("position", position);
                String nameOfListPrior = mList.get(position).toString();
                myIntent.putExtra("nameOfListPrior", nameOfListPrior);
                startActivity(myIntent);
            }
        });

    }

    public void clickingInfo(MenuItem item) {
        deleteOnLongPress();
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
                image.setText(mList.get(position).getIMAGE());

            }
            return convertView;
        }
    }

    public void deleteOnLongPress() {

        AlertDialog.Builder favoritesDeleteDiag = new AlertDialog.Builder(this);
        favoritesDeleteDiag.setMessage("In order to delete items from your favorites list, just long press on them!" + "\n" + "\n" + "**Keep in mind it removes them from top to bottom.");
        favoritesDeleteDiag.setIcon(R.mipmap.ic_news);
        favoritesDeleteDiag.setCancelable(true);


        favoritesDeleteDiag.setPositiveButton(
                "Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        dialog.cancel();


                        return;


                    }
                });


        AlertDialog alertDialog = favoritesDeleteDiag.create();
        alertDialog.show();
    }

    private boolean isFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.infobutton, menu);


        return true;
    }


}
