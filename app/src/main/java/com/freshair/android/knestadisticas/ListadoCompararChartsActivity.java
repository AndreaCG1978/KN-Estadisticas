package com.freshair.android.knestadisticas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.freshair.android.knestadisticas.database.DataBaseManager;
import com.freshair.android.knestadisticas.dtos.KNChart;
import com.freshair.android.knestadisticas.dtos.KNItemChart;
import com.freshair.android.knestadisticas.utils.ConstantsAdmin;

public class ListadoCompararChartsActivity extends ListActivity {
	
	private List<Boolean> posSelected = new ArrayList<>();
	
	// --Commented out by Inspection (14/11/18 19:16):String tipoCompSelected = null;
	private int cantSeleccionados = 0;
//	private ArrayList<Cursor> allMyCursors = null;
	
  /*  @Override
	public void startManagingCursor(Cursor c) {
		allMyCursors.add(c);
	    super.startManagingCursor(c);
	}	
    */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	//this.resetAllMyCursors();
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
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //allMyCursors = new ArrayList<>();
        this.setContentView(R.layout.list_comparar_charts);
        this.configurarList(getListView());    
        this.configurarWidgets();
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
	
	private void configurarWidgets(){
		ImageView btn = this.findViewById(R.id.btnDrw);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openComparatorChart();
            }
        });    
        btn = this.findViewById(R.id.btnConfig);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openConfigurarComparacion();
            }
        });     

	}
	

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item;
    	super.onCreateOptionsMenu(menu);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_COMPARE_CHART,0, R.string.menu_configuracion_comparacion);
        item.setIcon(R.drawable.config_menubar);
        return true;
    }
    
	@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_COMPARE_CHART:
        	this.openConfigurarComparacion();
        	return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
	
	
	private void openConfigurarComparacion(){
        Intent i = new Intent(this, ConfigChartActivity.class);
	    this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_CHART);		
	}

	
	private void configurarList(ListView listView){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.inicializarBD(mDBManager);
		List<KNChart> charts = ConstantsAdmin.obtenerAllChart(this, mDBManager);
		ConstantsAdmin.finalizarBD(mDBManager);
		this.filtrarChartsSinItems(charts);
		if(charts.size() > 1){
			posSelected = new ArrayList<>();
	        listView.setItemsCanFocus(false);
	        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	        listView.setFastScrollEnabled(true);
			
			
			for (int i = 0; i < charts.size(); i++) {
				posSelected.add(false);
			}
			
	        setListAdapter(new ArrayAdapter(this,
		                android.R.layout.simple_list_item_multiple_choice, charts));			
		}else{
			ConstantsAdmin.mostrarMensajeAplicacion(this, this.getString(R.string.mensaje_pocos_chart), 10);
			this.finish();
		}


        		
	}
	
	private void filtrarChartsSinItems(List<KNChart> charts){
		Iterator<KNChart> it = charts.iterator();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
        List<KNItemChart> items;
		KNChart chrt;
		while(it.hasNext()){
			chrt = it.next();
			items = ConstantsAdmin.obtenerItemsDeChartListadoComparar(chrt, this, mDBManager);
			if(items.size() == 0){
				it.remove();
			}
		}
	}
	
	private List<KNChart> obtenerChartsSeleccionados(){
		List<KNChart> result;
		result = new ArrayList<>();
		Iterator<Boolean> it = posSelected.iterator();
		Boolean val;
		int i = 0;
		while(it.hasNext()){
			val = it.next();
			if(val){
				result.add((KNChart)this.getListAdapter().getItem(i));
			}	
			i++;
		}
		return result;
	}
	
	private void openComparatorChart(){
		
    	try {
    		final List<KNChart> charts = this.obtenerChartsSeleccionados();
    		if(charts.size() > 1){
    			ConstantsAdmin.chartsParaComparar = charts;
    	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	    	builder.setMessage(R.string.mensaje_tipo_comparacion)
    	    	       .setCancelable(true)
    	    	       .setPositiveButton(R.string.label_comparacion_tiempo, new DialogInterface.OnClickListener() {
    	    	           public void onClick(DialogInterface dialog, int id) {
    	    	        	   ConstantsAdmin.tipoComparacionSelected = ConstantsAdmin.COMPARACION_EN_TIEMPO;
    		    	           drawComparacion();
    	    				
    	    	        }
    	    	       })
    	    	       .setNegativeButton(R.string.label_comparacion_sobrepuesta, new DialogInterface.OnClickListener() {
    	    	           public void onClick(DialogInterface dialog, int id) {
    	    	        	   ConstantsAdmin.tipoComparacionSelected = ConstantsAdmin.COMPARACION_SOBREPUESTA;
    	    	        	   drawComparacion();
    	    	           }
    	    	       });
    	    	builder.show();    	
    		}else{
    			ConstantsAdmin.mostrarMensajeAplicacion(this, this.getString(R.string.mensaje_min_comparacion));
    		}
    			

		} catch (Exception e) {
			e.getMessage();// TODO: handle exception
		}
    	
    }
	
	
	private void drawComparacion(){
		Intent i = new Intent(this, ComparisonChartActivity.class);
	    this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_MOSTRAR_COMPARACION);

	}
	
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        boolean val;
        val = posSelected.get(position);
        val = !val;
        if(!val){
        	posSelected.set(position, val);
        	cantSeleccionados--;
        }else if(val && cantSeleccionados < ConstantsAdmin.cantMaxComparacion){
	    	posSelected.set(position, val);
	    	if(val){
	    		cantSeleccionados++;
	    	}else{
	    		cantSeleccionados--;
	    	}
        }else{
        	l.setItemChecked(position, false);
        	ConstantsAdmin.mostrarMensajeAplicacion(this, this.getString(R.string.mensaje_max_comparacion), 6);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        this.resetAllChecked();
    }
    
    private void resetAllChecked(){
    	for (int i = 0; i < this.getListAdapter().getCount(); i++) {
    		this.getListView().setItemChecked(i, false);
       		posSelected.set(i, false);
		}
    	cantSeleccionados = 0;

    }
	

}
