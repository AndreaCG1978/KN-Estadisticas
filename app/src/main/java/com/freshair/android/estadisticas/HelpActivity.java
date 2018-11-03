package com.freshair.android.estadisticas;

import android.app.Activity;
import android.os.Bundle;

public class HelpActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(R.string.title_help);
        this.setContentView(R.layout.help);
          
    }

}
