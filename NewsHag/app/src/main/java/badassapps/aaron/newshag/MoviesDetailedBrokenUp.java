package badassapps.aaron.newshag;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MoviesDetailedBrokenUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_detailed_broken_up);
    }

    public void clickingReadOnBook(View view) {
        Intent intent = new Intent(MoviesDetailedBrokenUp.this, WebViewMovies.class);
        startActivity(intent);
    }
}
