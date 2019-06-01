package com.freshair.android.knestadisticas;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class HelpActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(R.string.title_help);
        this.setContentView(R.layout.help);
        getActionBar().setDisplayHomeAsUpEnabled(true);
          
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
