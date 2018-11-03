package com.freshair.android.estadisticas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.freshair.android.estadisticas.dtos.KNChart;
import com.freshair.android.estadisticas.dtos.KNItemChart;
import com.freshair.android.estadisticas.utils.ConstantsAdmin;
import com.freshair.android.estadisticas.utils.KNItemChartArrayAdapter;

public class ItemPerMonthActivity extends ListActivity {
	
	String mYearSelected = null;
	String mMonthSelecetd = null;
	String idChartSelected = null;
	private long mItemIdSelect = -1;
	KNChart mChartSeleccionado = null;
	private ArrayList<Cursor> allMyCursors = null;
	
    @Override
	public void startManagingCursor(Cursor c) {
		allMyCursors.add(c);
	    super.startManagingCursor(c);
	}	
    
    
    private void resetAllMyCursors(){
    	Cursor cur = null;
    	Iterator<Cursor> it = allMyCursors.iterator();
    	while(it.hasNext()){
    		cur = (Cursor) it.next();
    		this.stopManagingCursor(cur);
    	}
    	allMyCursors = new ArrayList<Cursor>();
    }
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allMyCursors = new ArrayList<Cursor>();
        this.setContentView(R.layout.list_items);
        this.guardarDatosSeleccionado(this.getIntent());
        this.configurarList(getListView()); 
        this.actualizarWidgets();
        this.configurarBotones();
    }
	
	private void configurarBotones(){
		ImageView btn = (ImageView) this.findViewById(R.id.btnAddItem);
		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openAltaItemChart();
            }
        });
		btn = (ImageView) this.findViewById(R.id.btnDrw);
		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openChart();
            }
        });
		btn = (ImageView) this.findViewById(R.id.btnConfig);
		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openConfigChart();
            }
        });
		
	}
	
    public void openConfigChart(){
	    Intent i = new Intent(this, ConfigChartActivity.class);
	    i.putExtra(ConstantsAdmin.CHART_SELECCIONADO, String.valueOf(mChartSeleccionado.getId()));
	    this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_CHART);
    }
    
	
    public void openChart(){
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
		TextView text = (TextView) this.findViewById(R.id.titleChartName);
		text.setText(this.getString(R.string.label_detalle_items) + " (" + mChartSeleccionado.getName().toUpperCase()+ ")" );
		text = (TextView) this.findViewById(R.id.descYear);
		text.setText(mYearSelected);
		text = (TextView) this.findViewById(R.id.descMonth);
		text.setText(mMonthSelecetd);

	}
	
	private void recargarChart(){
		mChartSeleccionado = ConstantsAdmin.obtenerChartId(this, Long.valueOf(idChartSelected));
	}
	
	private void guardarDatosSeleccionado(Intent intent){
		idChartSelected = (String)intent.getExtras().get(ConstantsAdmin.CHART_SELECCIONADO);
		mYearSelected = (String)intent.getExtras().get(ConstantsAdmin.YEAR_SELECCIONADO);
		mMonthSelecetd = (String)intent.getExtras().get(ConstantsAdmin.MONTH_SELECCIONADO);
		this.recargarChart();
 	}
	
	
	private void recargarLista(){
        List<KNItemChart> items = ConstantsAdmin.obtenerItemsDeChart(idChartSelected, mYearSelected, mMonthSelecetd, this);
        if(items != null && items.size() > 0){
        	setListAdapter(new KNItemChartArrayAdapter(this, R.layout.row_item, items));	
        }else{
        	this.finish();
        }
        
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = null;
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
	
    protected void openAltaItemChart() {
        Intent i = new Intent(this, AltaItemChartActivity.class);
        i.putExtra(ConstantsAdmin.CHART_SELECCIONADO, String.valueOf(mChartSeleccionado.getId()));
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_ITEM_CHART);
    }
    
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	this.resetAllMyCursors();
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
		ConstantsAdmin.inicializarBD(this);
		ConstantsAdmin.eliminarItemChart(itemSelect.getId(), this);
		ConstantsAdmin.finalizarBD();
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



}
