package com.freshair.android.knestadisticas;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;

import com.freshair.android.knestadisticas.database.DataBaseManager;
import com.freshair.android.knestadisticas.dtos.KNConfigChart;
import com.freshair.android.knestadisticas.utils.ConstantsAdmin;

public class InitActivity extends Activity {
	
    private boolean _active = true;
    private final int _splashTime = 1200;
    private Activity me = null;  
//	private ArrayList<Cursor> allMyCursors = null;
/*
    @Override
	public void startManagingCursor(Cursor c) {
		allMyCursors.add(c);
	    super.startManagingCursor(c);
	}	
    */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    //	this.resetAllMyCursors();
    }
    /*
    private void resetAllMyCursors(){
    	Cursor cur;
        for (Cursor allMyCursor : allMyCursors) {
            cur = allMyCursor;
            this.stopManagingCursor(cur);
        }
    	allMyCursors = new ArrayList<>();
    }
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   allMyCursors = new ArrayList<>();
                try{
               	
                	inicializarBD();
                	this.setContentView(R.layout.splash);
                	me = this;
               	
                	Thread splashTread = new Thread() {
                       @Override
                       public void run() {
                           try {
                               int waited = 0;
                               while(_active && (waited < _splashTime)) {
                                   sleep(100);
                                   if(_active) {
                                       waited += 100;
                                   }
                               }
                           } catch(InterruptedException e) {
                               
                           } finally {
                               finish();
                               try {
                               		Intent i = new Intent(me, MainActivity.class);
                            	    startActivity(i);	
       						} catch (Exception e2) {
       							e2.getMessage();
       						}
                               

                           }
                       }
                   };
                   splashTread.start();
               }catch (Exception e) {
       				ConstantsAdmin.mostrarMensajeAplicacion(this, getString(R.string.errorInicioAplicacion));
       			}
               
               
    	}
    
	private void inicializarBD(){
		long configSize = 0;
        DataBaseManager mDBManager = DataBaseManager.getInstance(this);
    	ConstantsAdmin.inicializarBD(mDBManager);
    	ConstantsAdmin.createBD(mDBManager);
    	configSize = ConstantsAdmin.obtenerTablaConfigSize(mDBManager);
    	if(configSize == 0){
    		this.createConfigChart();
    	}
    	ConstantsAdmin.finalizarBD(mDBManager);
	}
	
	private void createConfigChart(){
		KNConfigChart config = new KNConfigChart();
        DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		config.setBackground(String.valueOf(Color.DKGRAY));
		config.setGrid(String.valueOf(Color.LTGRAY));
		config.setLabel(String.valueOf(Color.LTGRAY));
		config.setPoint("2");
		config.setLine(String.valueOf(Color.BLUE));
		config.setTime(ConstantsAdmin.formatTime[0]);
		ConstantsAdmin.agregarConfigChart(config, mDBManager);
	}
	
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            _active = false;
        }
        return true;
    }

}