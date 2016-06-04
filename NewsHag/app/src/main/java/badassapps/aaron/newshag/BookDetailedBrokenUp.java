package badassapps.aaron.newshag;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class BookDetailedBrokenUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detailed_broken_up);
    }

    public void clickingReadOnBook(View view) {
        Intent intent = new Intent(BookDetailedBrokenUp.this, WebViewBooks.class);
        startActivity(intent);
    }
}
