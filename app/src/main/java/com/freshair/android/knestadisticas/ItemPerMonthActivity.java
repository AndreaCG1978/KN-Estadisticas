package com.freshair.android.knestadisticas;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.freshair.android.knestadisticas.database.DataBaseManager;
import com.freshair.android.knestadisticas.dtos.KNChart;
import com.freshair.android.knestadisticas.dtos.KNItemChart;
import com.freshair.android.knestadisticas.utils.ConstantsAdmin;
import com.freshair.android.knestadisticas.utils.KNItemChartArrayAdapter;
import com.freshair.android.knestadisticas.utils.KNListFragment;

public class ItemPerMonthActivity extends KNListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private String mYearSelected = null;
	private String mMonthSelecetd = null;
	private String idChartSelected = null;
	private long mItemIdSelect = -1;
	private KNChart mChartSeleccionado = null;
	private final int ITEM_CHART_CURSOR = 1;
//	private ArrayList<Cursor> allMyCursors = null;

	/*
    @Override
	public void startManagingCursor(Cursor c) {
		allMyCursors.add(c);
	    super.startManagingCursor(c);
	}	
    */
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
     //   allMyCursors = new ArrayList<>();
		this.cargarLoaders();
        this.setContentView(R.layout.list_items);
        this.guardarDatosSeleccionado(this.getIntent());
        this.configurarList(getListView()); 
        this.actualizarWidgets();
        this.configurarBotones();
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

	private void configurarBotones(){
		ImageView btn = this.findViewById(R.id.btnAddItem);
		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openAltaItemChart();
            }
        });
		btn = this.findViewById(R.id.btnDrw);
		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openChart();
            }
        });
		btn = this.findViewById(R.id.btnConfig);
		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openConfigChart();
            }
        });
		
	}
	
    private void openConfigChart(){
	    Intent i = new Intent(this, ConfigChartActivity.class);
	    i.putExtra(ConstantsAdmin.CHART_SELECCIONADO, String.valueOf(mChartSeleccionado.getId()));
	    this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_CHART);
    }
    
	
    private void openChart(){
    	try {
            Intent i = new Intent(this, SimpleLinearChartActivity.class);
            i.putExtra(ConstantsAdmin.CHART_SELECCIONADO, String.valueOf(mChartSeleccionado.getId()));
            this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_DRAW_CHART);
	
		} catch (Exception e) {
			e.getMessage();// TODO: handle exception
		}
    	
    }
	
	private void configurarList(ListView listView){
       listView.setItemsCanFocus(false);
       listView.setFastScrollEnabled(true);
       listView.setDividerHeight(3);

//       this.recargarLista();
	}
	
	private void actualizarWidgets(){
		TextView text = this.findViewById(R.id.titleChartName);
		text.setText(this.getString(R.string.label_detalle_items) + " (" + mChartSeleccionado.getName().toUpperCase()+ ")" );
		text = this.findViewById(R.id.descYear);
		text.setText(mYearSelected);
		text = this.findViewById(R.id.descMonth);
		text.setText(mMonthSelecetd);

	}
	
	private void recargarChart(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		mChartSeleccionado = ConstantsAdmin.obtenerChartId(Long.valueOf(idChartSelected), mDBManager);
	}
	
	private void guardarDatosSeleccionado(Intent intent){
		idChartSelected = (String)intent.getExtras().get(ConstantsAdmin.CHART_SELECCIONADO);
		mYearSelected = (String)intent.getExtras().get(ConstantsAdmin.YEAR_SELECCIONADO);
		mMonthSelecetd = (String)intent.getExtras().get(ConstantsAdmin.MONTH_SELECCIONADO);
		this.recargarChart();
 	}
	
	
	private void recargarLista(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.inicializarBD(mDBManager);
        List<KNItemChart> items = ConstantsAdmin.obtenerItemsDeChart(idChartSelected, mYearSelected, mMonthSelecetd, this, mDBManager);
        ConstantsAdmin.finalizarBD(mDBManager);
        if(items != null && items.size() > 0){
        	setListAdapter(new KNItemChartArrayAdapter(this, R.layout.row_item, items));	
        }else{
        	this.finish();
        }
        
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item;
    	super.onCreateOptionsMenu(menu);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_DRAW_CHART,0, R.string.menu_dibujar_chart);
        item.setIcon(R.drawable.draw_chart_menubar);
    	item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_ITEM_CHART,0, R.string.menu_agregar_item_chart);
        item.setIcon(R.drawable.add_menubar);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_CHART,0, R.string.menu_configurar_chart);
        item.setIcon(R.drawable.config_menubar);

        
        return true;
    }
    	
	@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case ConstantsAdmin.ACTIVITY_EJECUTAR_DRAW_CHART:
        	this.openChart();
        	return true;
        case ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_ITEM_CHART:
        	this.openAltaItemChart();
        	return true;
        case ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_CHART:
        	this.openConfigChart();
        	return true;
        	
        }
        return super.onMenuItemSelected(featureId, item);
    }	   
	
    private void openAltaItemChart() {
        Intent i = new Intent(this, AltaItemChartActivity.class);
        i.putExtra(ConstantsAdmin.CHART_SELECCIONADO, String.valueOf(mChartSeleccionado.getId()));
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_ITEM_CHART);
    }
    
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	//this.resetAllMyCursors();
    	switch (requestCode) {
		case ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_ITEM_CHART:
			if(!(String.valueOf(ConstantsAdmin.mYear).equals(mYearSelected) && ConstantsAdmin.mMonth.equals(mMonthSelecetd))&& ConstantsAdmin.guardoItem){
		      	this.finish();
	        }
			break;
			
		case ConstantsAdmin.ACTIVITY_EJECUTAR_EDITAR_ITEM_CHART:
			if(!(String.valueOf(ConstantsAdmin.mYear).equals(mYearSelected) && ConstantsAdmin.mMonth.equals(mMonthSelecetd))&& ConstantsAdmin.guardoItem){
		      	this.finish();
	        }
			break;
			

		default:
			break;
		}    	
    }
    
    public void eliminarItemDialog(int pos){
    	mItemIdSelect = pos;
    	this.eliminarItemDialog();
    }
    
    
	private void eliminarItemDialog(){
		try {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage(getString(R.string.mensaje_borrar_item))
	    	       .setCancelable(false)
	    	       .setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   eliminarItemSeleccionado();
	    	        	   recargarLista();
	    	           }
	    	       })
	    	       .setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	                dialog.cancel();
	    	           }
	    	       });
	    	builder.show();
			
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensajeAplicacion(this, getString(R.string.errorEliminacionItem));
		}
	}
	
	private void eliminarItemSeleccionado(){
		KNItemChart itemSelect = (KNItemChart)this.getListAdapter().getItem(Integer.valueOf(String.valueOf(mItemIdSelect)));
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.inicializarBD(mDBManager);
		ConstantsAdmin.eliminarItemChart(itemSelect.getId(), mDBManager);
		ConstantsAdmin.finalizarBD(mDBManager);
	}
	
   
    public void openEditItemChart(int pos) {
    	KNItemChart item = (KNItemChart) this.getListAdapter().getItem(pos);
        Intent i = new Intent(this, AltaItemChartActivity.class);
        i.putExtra(ConstantsAdmin.CHART_SELECCIONADO, idChartSelected);
        i.putExtra(ConstantsAdmin.ITEM_CHART_SELECCIONADO, String.valueOf(item.getId()));
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_EDITAR_ITEM_CHART);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.recargarChart();
        this.recargarLista();
    }

	private void cargarLoaders() {
		this.getSupportLoaderManager().initLoader(ITEM_CHART_CURSOR, null, this);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		CursorLoader cl = null;
		switch(id) {
			case ITEM_CHART_CURSOR:
				cl = mDBManager.cursorLoaderItemChart(this, null);
			//	ConstantsAdmin.cursorItemChart = cl;
				break; // optional
			default : // Optional
				// Statements
		}

		return cl;
	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {

	}
}
