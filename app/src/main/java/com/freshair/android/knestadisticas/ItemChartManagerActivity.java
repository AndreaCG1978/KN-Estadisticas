package com.freshair.android.knestadisticas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.freshair.android.knestadisticas.database.DataBaseManager;
import com.freshair.android.knestadisticas.dtos.KNChart;
import com.freshair.android.knestadisticas.dtos.KNItemChart;
import com.freshair.android.knestadisticas.utils.ConstantsAdmin;
import com.freshair.android.knestadisticas.utils.ExpandableListFragment;

public class ItemChartManagerActivity extends ExpandableListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private KNChart mChartSeleccionado = null;
	// --Commented out by Inspection (14/11/18 19:16):List<KNItemChart> myItems = null;
	private final String MONTH = "MONTH";
	private final String YEAR = "YEAR";
	private String mMonthSelected = null;
	private String mYearSelected = null;
	private int mGroupSelected = -1;
	private ExpandableListAdapter mAdapter = null;
    private LayoutInflater layoutInflater = null;
    private TextView mNameChart = null;
    private TextView mDescChart = null;
    private Activity me = null;
    private Map<String,Map<String,List<KNItemChart>>> allItems = null;
//	private ArrayList<Cursor> allMyCursors = null;

	private final int GRAFICOS_CURSOR = 1;
	private final int ITEM_CHART_CURSOR = 2;

	
 /*   @Override
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
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.cargarLoaders();
        //allMyCursors = new ArrayList<>();
        this.setContentView(R.layout.items_manager);
        this.registrarWidgets();
        me = this;
        layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.guardarChartSeleccionado(this.getIntent());
        this.configurarList(getExpandableListView()); 
        this.registrarBotones();
		getActionBar().setDisplayHomeAsUpEnabled(true);
    }
	
	private void registrarWidgets(){
		mNameChart = this.findViewById(R.id.nameChartItemManager);
		mDescChart = this.findViewById(R.id.descChartItemManager);
	}
	
	private void configurarList(ExpandableListView listView){
       listView.setItemsCanFocus(false);
       listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
       listView.setFastScrollEnabled(true);
       listView.setScrollbarFadingEnabled(true);
       listView.setVerticalScrollBarEnabled(true);
      // this.recargarLista();
		
	}
	
	private void actualizarWidgets(){
		this.recargarChart(mChartSeleccionado.getId());
		mNameChart.setText(this.getString(R.string.label_listado_items) + " (" + mChartSeleccionado.getName().toUpperCase()+ ")" );
		if(mChartSeleccionado.getDescription() != null && !mChartSeleccionado.getDescription().equals("")){
			mDescChart.setText(mChartSeleccionado.getDescription());   
            mDescChart.setVisibility(View.VISIBLE);
    	}else{
    		mDescChart.setVisibility(View.GONE);
    	}
	}
	
	private void registrarBotones(){
		ImageView btn = this.findViewById(R.id.btnDrw);
		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	 openChart();
            }
        });
		btn = this.findViewById(R.id.btnAddItem);
		btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openAltaItemChart();
            }
        });
        btn = this.findViewById(R.id.btnEdit);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               openEditarChart();
            }
        });
        btn = this.findViewById(R.id.btnRemove);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	eliminarChartDialog();
            }
        });
        btn = this.findViewById(R.id.btnConfig);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openConfigChart();
            }
        });        
	}

    private void recargarLista(){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
    	List<KNItemChart> items = ConstantsAdmin.obtenerItemsDeChartChartManager(mChartSeleccionado, this, mDBManager);
    	List<Map<String, String>> groupData = new ArrayList<>();
        List<List<Map<String, String>>> childData = new ArrayList<>();
        Iterator it = items.iterator();
        KNItemChart item;

        allItems = new TreeMap<>();
        List<KNItemChart> listTemp;
        Map<String, List<KNItemChart>> map;
        while(it.hasNext()){
        	item = (KNItemChart) it.next();
        	listTemp = new ArrayList<>();
        	if (allItems.containsKey(item.getYear())) {
        		listTemp = allItems.get(item.getYear()).get(item.getMonth());
        		if(listTemp == null){
        			listTemp = new ArrayList<>();
        		}
        		listTemp.add(item);
        		allItems.get(item.getYear()).put(item.getMonth(), listTemp);
        		
        	} else {
        		listTemp.add(item);
        		map = new TreeMap<>();
        		map.put(item.getMonth(), listTemp);
        		allItems.put(item.getYear(), map);
        	}
        	
        }
        
              	
        for (String key : allItems.keySet()) {
        	map = allItems.get(key);
            Map<String, String> curGroupMap = new HashMap<>();
            groupData.add(curGroupMap);
            curGroupMap.put(YEAR, key);
                 
            List<Map<String, String>> children = new ArrayList<>();
            
            for (String month : map.keySet()){
            	listTemp = map.get(month);
            	Map<String, String> curChildMap = new HashMap<>();
                children.add(curChildMap);
                curChildMap.put(MONTH, month);
                curChildMap.put(YEAR, key);
            }
            
            childData.add(children);
        }

        mAdapter = new SimpleExpandableListAdapter(
                this,
                groupData,
                0,
                null,
                new int[] {},
                childData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {MONTH},
                new int[] { android.R.id.text1}
                ){
        	
        	
        	
        	@Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
               	final View v = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
                TextView text = v.findViewById(R.id.monthDesc);
                final String year = (String)allItems.keySet().toArray()[groupPosition];
                final String mes = (String) allItems.get(year).keySet().toArray()[childPosition];
                int cantItems = allItems.get(year).get(mes).size();
                final int gp = groupPosition;
                final int cp = childPosition;

                text.setText(mes);                   
                text = v.findViewById(R.id.cantDiasRegistrados);
                text.setText(getString(R.string.label_cant_dias_reg) + " " + cantItems);
                ImageView btn = v.findViewById(R.id.btnRemove);
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                    	eliminarMonthDialog(year, mes);
                    }
                });             
                btn = v.findViewById(R.id.btnEdit);
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                    	openManagerItem(gp,cp);
                    }
                });
                return v;
            }
            
        	@Override
            public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            	final View v = super.getGroupView(groupPosition, isExpanded, convertView, parent);
            	TextView text = v.findViewById(R.id.yearDesc);
                final String year = (String)allItems.keySet().toArray()[groupPosition];
                int cantMeses = allItems.get(year).size();
                text.setText(year);                   
                text = v.findViewById(R.id.cantMesesRegistrados);
                text.setText(getString(R.string.label_cant_meses_reg) + " " + cantMeses);
                ImageView btn = v.findViewById(R.id.btnRemove);
                btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                    	eliminarYearDialog(year);
                    }
                });
                
                return v;
            }

            @Override
            public View newChildView(boolean isLastChild, ViewGroup parent) {
            	return layoutInflater.inflate(R.layout.month_row, parent, false);
            }
            

            @Override
            public View newGroupView(boolean isLastChild, ViewGroup parent) {
            	return layoutInflater.inflate(R.layout.year_row, parent, false);
            }
            
            @Override
            public void onGroupExpanded(int groupPosition){
            	super.onGroupExpanded(groupPosition);
            	mGroupSelected = groupPosition;
            }
            
            @Override
            public void onGroupCollapsed(int groupPosition){
            	super.onGroupCollapsed(groupPosition);
            	mGroupSelected = -1;
            }
        };
        setListAdapter(mAdapter);
        if(mGroupSelected != -1 && mGroupSelected < this.getExpandableListAdapter().getGroupCount()){
        	this.getExpandableListView().expandGroup(mGroupSelected);
        }
       
   	
    }
    
    private void openManagerItem(int group, int child){
    	HashMap<String, String> result = (HashMap<String, String>)mAdapter.getChild(group, child);
    	mMonthSelected = result.get(MONTH);
    	mYearSelected = result.get(YEAR);
    	openItemPerMonthView();    	
    }
    
	private void eliminarYearDialog(String year){
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final String y = year;
	    	builder.setMessage(year + ": " + getString(R.string.mensaje_borrar_year))
	    	       .setCancelable(false)
	    	       .setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   eliminarYearItems(y);
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
			ConstantsAdmin.mostrarMensajeAplicacion(this, getString(R.string.errorEliminacionYear));
		}
	}
	
	private void eliminarMonthDialog(String year, String month){
		try {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			final String y = year;
			final String m = month;
	    	builder.setMessage(year + "-" + month + ": " + getString(R.string.mensaje_borrar_month))
	    	       .setCancelable(false)
	    	       .setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   eliminarMonthItems(y,m);
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
			ConstantsAdmin.mostrarMensajeAplicacion(this, getString(R.string.errorEliminacionMonth));
		}
	}	
	
	private void eliminarYearItems(String year){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.eliminarItemsCharts(String.valueOf(mChartSeleccionado.getId()), year, mDBManager);
	}
	
	private void eliminarMonthItems(String year, String month){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		ConstantsAdmin.eliminarItemsCharts(String.valueOf(mChartSeleccionado.getId()), year, month, mDBManager);
	}
    
    private void openItemPerMonthView(){
        Intent i = new Intent(this, ItemPerMonthActivity.class);
        i.putExtra(ConstantsAdmin.CHART_SELECCIONADO, String.valueOf(mChartSeleccionado.getId()));
        i.putExtra(ConstantsAdmin.YEAR_SELECCIONADO, mYearSelected);
        i.putExtra(ConstantsAdmin.MONTH_SELECCIONADO, mMonthSelected);
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_VIEW_ITEMS);
    	
    }
    
	private void guardarChartSeleccionado(Intent intent){
		String idChartString;
		idChartString = (String)intent.getExtras().get(ConstantsAdmin.CHART_SELECCIONADO);
		long idChart = Long.valueOf(idChartString);
		this.recargarChart(idChart);

 	}
	
	private void recargarChart(long idChart){
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		mChartSeleccionado = ConstantsAdmin.obtenerChartId(idChart, mDBManager);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item;
    	super.onCreateOptionsMenu(menu);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_DRAW_CHART,0, R.string.menu_dibujar_chart);
        item.setIcon(R.drawable.draw_chart_menubar);
    	item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_ITEM_CHART,0, R.string.menu_agregar_item_chart);
        item.setIcon(R.drawable.add_menubar);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_EDITAR_CHART,0, R.string.menu_editar_chart);
        item.setIcon(R.drawable.edit_chart_menubar);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_BORRAR_CHART,0, R.string.menu_borrar_chart);
        item.setIcon(R.drawable.delete_chart_menubar);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_CHART,0, R.string.menu_configurar_chart);
        item.setIcon(R.drawable.config_menubar);
        return true;
    }
    
	@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_ITEM_CHART:
        	this.openAltaItemChart();
        	return true;
        case ConstantsAdmin.ACTIVITY_EJECUTAR_DRAW_CHART:
        	this.openChart();
        	return true;
        case ConstantsAdmin.ACTIVITY_EJECUTAR_BORRAR_CHART:
        	this.eliminarChartDialog();
        	return true;
        case ConstantsAdmin.ACTIVITY_EJECUTAR_EDITAR_CHART:
        	this.openEditarChart();
        	return true;
        case ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_CHART:
        	this.openConfigChart();
        	return true;
        }
        
        return super.onMenuItemSelected(featureId, item);
    }

    private void openEditarChart(){
	    Intent i = new Intent(this, AltaChartActivity.class);
	    i.putExtra(ConstantsAdmin.CHART_SELECCIONADO, String.valueOf(mChartSeleccionado.getId()));
	    this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_EDITAR_CHART);
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
    
    
    
	private void eliminarChartDialog(){
		try {
			final DataBaseManager mDBManager = DataBaseManager.getInstance(this);
			String chrtStr = mChartSeleccionado.getName();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage(chrtStr + getString(R.string.mensaje_borrar_chart))
	    	       .setCancelable(false)
	    	       .setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   ConstantsAdmin.eliminarChart(mChartSeleccionado.getId(), mDBManager);
	    	        	   finish();
	    	           }
	    	       })
	    	       .setNegativeButton(R.string.label_no, new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	                dialog.cancel();
	    	           }
	    	       });
	    	builder.show();
			
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensajeAplicacion(this, getString(R.string.errorEliminacionChart));
		}
	}

    
    private void openAltaItemChart() {
        Intent i = new Intent(this, AltaItemChartActivity.class);
        i.putExtra(ConstantsAdmin.CHART_SELECCIONADO, String.valueOf(mChartSeleccionado.getId()));
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_ITEM_CHART);
    }
    
  
    @Override
    protected void onResume() {
        super.onResume();
        this.recargarLista();
        this.actualizarWidgets();
    }

	private void cargarLoaders() {
		this.getSupportLoaderManager().initLoader(GRAFICOS_CURSOR, null, this);
		this.getSupportLoaderManager().initLoader(ITEM_CHART_CURSOR, null, this);

	}


	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		CursorLoader cl = null;
		switch(id) {
			case GRAFICOS_CURSOR:
				cl = mDBManager.cursorLoaderGraficosPorNombre(null, this);
				ConstantsAdmin.cursorGraficos = cl;
				break; // optional
			case ITEM_CHART_CURSOR:
				cl = mDBManager.cursorLoaderItemChart(this, null);
				ConstantsAdmin.cursorItemChart = cl;
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
