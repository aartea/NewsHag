package badassapps.aaron.newshag;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class NavDDetailView extends AppCompatActivity {

    TextView body, title;
    ImageView image;
    Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_ddetail_view);

        String detailTitle = getIntent().getStringExtra("title");
        String detailAbstract = getIntent().getStringExtra("abstract");
        String detailThumb = getIntent().getStringExtra("thumbnail");
        String detailUrl = getIntent().getStringExtra("url");

        body = (TextView) findViewById(R.id.body);
        title = (TextView) findViewById(R.id.title);
        image = (ImageView) findViewById(R.id.image);
        button = (Button) findViewById(R.id.button);
        title.setMovementMethod(new ScrollingMovementMethod());
        body.setMovementMethod(new ScrollingMovementMethod());


        body.setMovementMethod(new ScrollingMovementMethod());

        body.setText(detailAbstract);
        title.setText(detailTitle);

        if (detailThumb != null && !detailThumb.equals("")) {
            Picasso.with(NavDDetailView.this).load(detailThumb).into(image);
        }
    }

    public void clickingReadOn(View view) {
        Intent intent = new Intent(NavDDetailView.this, WebViewForTop10.class);
        intent.putExtra("url", getIntent().getStringExtra("url"));
        startActivity(intent);
    }

    public void clickingFavsToAdd(MenuItem item) {

        Toast.makeText(NavDDetailView.this, "Added the story to your favorites!", Toast.LENGTH_SHORT).show();

    }


    public void clickingShare(MenuItem item) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getIntent().getStringExtra("url"));
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.send_intent_title)));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shared_icon, menu);
        inflater.inflate(R.menu.making_fav, menu);
        return true;
    }

}



