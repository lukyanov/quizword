package com.lingvapps.quizword;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class CardActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_card, menu);
        return true;
    }
}
