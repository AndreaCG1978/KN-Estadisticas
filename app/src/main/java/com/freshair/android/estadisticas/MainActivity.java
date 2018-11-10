package com.freshair.android.estadisticas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toolbar;

import com.freshair.android.estadisticas.dtos.KNChart;
import com.freshair.android.estadisticas.utils.ConstantsAdmin;

public class MainActivity extends ExpandableListActivity {
    private static final String NAME = "NAME";

    private LayoutInflater layoutInflater = null;
    ArrayList<KNChart> myCharts = null;
    int mGroupSelected = -1;
	private ArrayList<Cursor> allMyCursors = null;
	private MainActivity me = null;
	private int selectedFormatImport = -1;
	
    @Override
	public void startManagingCursor(Cursor c) {
		allMyCursors.add(c);
	    super.startManagingCursor(c);
	}	
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        me = this;
        allMyCursors = new ArrayList<Cursor>();
        this.setContentView(R.layout.main);
        me = this;
        layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    //    this.recargarLista();
        this.getExpandableListView().setDividerHeight(14);


    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	this.resetAllMyCursors();
    	switch (requestCode) {
		case ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_CHART:
			mGroupSelected = -1;
			break;

		default:
			break;
		}    	
    }
    
    
    private void resetAllMyCursors(){
    	Cursor cur = null;
    	Iterator<Cursor> it = allMyCursors.iterator();
    	while(it.hasNext()){
    		cur = it.next();
    		this.stopManagingCursor(cur);
    	}
    	allMyCursors = new ArrayList<Cursor>();
    }
	
    
	private void eliminarChartDialog(int chartPosition){
		try {
			final KNChart chrt = myCharts.get(chartPosition);
			String chrtStr = chrt.getName();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage(chrtStr + getString(R.string.mensaje_borrar_chart))
	    	       .setCancelable(false)
	    	       .setPositiveButton(R.string.label_si, new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	        	   ConstantsAdmin.eliminarChart(chrt.getId(), me);
	    	        	   mGroupSelected = -1;
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
			ConstantsAdmin.mostrarMensajeAplicacion(this, getString(R.string.errorEliminacionChart));
		}
	}

    @Override
	public boolean onCreateOptionsMenu(Menu menu){
        MenuItem item = null;


        super.onCreateOptionsMenu(menu);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_CHART,0, R.string.menu_agregar_chart);
        item.setIcon(R.drawable.add_menubar);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_IMPORT_CSV,0, R.string.menu_import_csv);
        item.setIcon(R.drawable.import_menubar);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_COMPARAR_CHARTS,0, R.string.menu_comparar_charts);
        item.setIcon(R.drawable.compare_charts_menubar);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_COMPARE_CHART,0, R.string.menu_configuracion_comparacion);
        item.setIcon(R.drawable.config_menubar);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_HELP,0, R.string.menu_help);
        item.setIcon(R.drawable.help_menubar);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_ABOUT_ME,0, R.string.menu_about_me);
        item.setIcon(R.drawable.about_us_menubar);

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem item = null;


        super.onCreateContextMenu(menu, v, menuInfo);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_CHART,0, R.string.menu_agregar_chart);
        item.setIcon(R.drawable.add_menubar);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_IMPORT_CSV,0, R.string.menu_import_csv);
        item.setIcon(R.drawable.import_menubar);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_COMPARAR_CHARTS,0, R.string.menu_comparar_charts);
        item.setIcon(R.drawable.compare_charts_menubar);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_COMPARE_CHART,0, R.string.menu_configuracion_comparacion);
        item.setIcon(R.drawable.config_menubar);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_HELP,0, R.string.menu_help);
        item.setIcon(R.drawable.help_menubar);
        item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_ABOUT_ME,0, R.string.menu_about_me);
        item.setIcon(R.drawable.about_us_menubar);

    }

	@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
        case ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_CHART:
        	this.openAltaChart();
        	return true;
        case ConstantsAdmin.ACTIVITY_EJECUTAR_COMPARAR_CHARTS:
        	this.openCompararChart();
        	return true;
        case ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_COMPARE_CHART:
        	this.openConfigurarComparacion();
        	return true;
        case ConstantsAdmin.ACTIVITY_EJECUTAR_HELP:
        	this.openAyuda();
        	return true;
        case ConstantsAdmin.ACTIVITY_EJECUTAR_ABOUT_ME:
        	this.openAboutUs();
        	return true;
        case ConstantsAdmin.ACTIVITY_EJECUTAR_IMPORT_CSV:
        	this.openImportCSV();
        	return true;

        }
        return super.onMenuItemSelected(featureId, item);
    }
	
		     
        private class ImportCSVTask extends AsyncTask<Long, Integer, Integer>{
        	ProgressDialog dialog = null;
            @Override
            protected Integer doInBackground(Long... params) {

                try {
                    publishProgress(1);
                    ConstantsAdmin.importarCSVs(me,selectedFormatImport);


                } catch (Exception e) {

                    e.printStackTrace();
                }
                return 0;

            }

	        protected void onProgressUpdate(Integer... progress) {
	        	dialog = ProgressDialog.show(me, "", 
                        me.getString(R.string.mensaje_importando_csv), false);
	        }

            @Override
            protected void onPostExecute(Integer result) {
            	dialog.cancel();
           		ConstantsAdmin.mostrarMensajeDialog(me, ConstantsAdmin.mensaje);
        		ConstantsAdmin.mensaje = null;
        		recargarLista();
            		
            }
        }

	private void openImportCSV(){
		
		final CharSequence[] items = {this.getString(R.string.format_date_time), this.getString(R.string.format_date),this.getString(R.string.format_all_separated)};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getString(R.string.title_select_format));
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
	    		Long[] params = new Long[1];
	    		params[0] = Long.valueOf(1);
	    		selectedFormatImport = item + 1;
	    		dialog.cancel();
	    		new ImportCSVTask().execute(params);
		    }
		});
		AlertDialog alert =  builder.create();
	    alert.show();
	}
	
	private void openConfigurarComparacion(){
        Intent i = new Intent(this, ConfigChartActivity.class);
	    this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_CHART);		
	}
	
	private void openAyuda(){
        Intent i = new Intent(this, HelpActivity.class);
	    this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_HELP);		
	}

	private void openAboutUs(){
        Intent i = new Intent(this, AboutUsActivity.class);
	    this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ABOUT_ME);		
	}

	
	private void openCompararChart(){
        final Intent i = new Intent(this, ListadoCompararChartsActivity.class);
 
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_COMPARAR_CHARTS);
		
	}
    
    private void recargarLista(){
        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
        myCharts = ConstantsAdmin.obtenerAllChart(this);
        Iterator<KNChart> it = myCharts.iterator();
        KNChart chart = null;
        Map<String, String> curGroupMap = null;
        List<Map<String, String>> children = null;
        Map<String, String> curChildMap = null;
        while(it.hasNext()){
        	chart = it.next();
        	curGroupMap = new HashMap<String, String>();
        	groupData.add(curGroupMap);
        	curGroupMap.put(NAME, chart.getName());
        	
            curChildMap = new HashMap<String, String>();
            children = new ArrayList<Map<String, String>>();
            children.add(curChildMap);
            childData.add(children);
        }
 
        setListAdapter( new SimpleExpandableListAdapter(
                this,
                groupData,
                0,
                null,            // the name of the field data
                new int[] {}, // the text field to populate with the field data
                childData,
                0,
                null,
                new int[] {}
            ) {
                @Override
                public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                    KNChart chartSelected = null;
                    final int grpPos = groupPosition;
                    TextView desc = null;
                	final View v = super.getChildView(groupPosition, childPosition, isLastChild, convertView, parent);
                	chartSelected = myCharts.get(groupPosition);
                	desc = v.findViewById(R.id.textDesc);
                	if(chartSelected.getDescription() != null && !chartSelected.getDescription().equals("")){
                        desc.setText(chartSelected.getDescription());   
                        desc.setVisibility(View.VISIBLE);
                	}else{
                		desc.setVisibility(View.GONE);
                	}
                                    
                    ImageView btn1 = v.findViewById(R.id.btnDraw);
                    btn1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                        	try {
                        		openChart(grpPos);
							} catch (Exception e) {
								e.getMessage();
							}
                        	 
                        }
                    });
                    ImageView btn2 = v.findViewById(R.id.btnAddItem);
                    btn2.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                        	openManagerItemChart(grpPos);
                        }
                    });
                    ImageView btn3 = v.findViewById(R.id.btnEdit);
                    btn3.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                           openEditarChart(grpPos);
                        }
                    });
                    ImageView btn4 = v.findViewById(R.id.btnRemove);
                    btn4.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                        	eliminarChartDialog(grpPos);
                        }
                    });
                    ImageView btn5 = v.findViewById(R.id.btnConfig);
                    btn5.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                        	openConfigChart(grpPos);
                        }
                    });
                    return v;
                }

                @Override
                public View newChildView(boolean isLastChild, ViewGroup parent) {
                     return layoutInflater.inflate(R.layout.chart_row, parent, false);
                }
                
                public View newGroupView(boolean isExpanded, ViewGroup parent){
                	return layoutInflater.inflate(R.layout.chart_row_label, parent, false);
                }
                
                @Override
                public void onGroupExpanded(int groupPosition){
                	super.onGroupExpanded(groupPosition);
                	mGroupSelected = groupPosition;
                }
                
                public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                	final View v = super.getGroupView(groupPosition, isExpanded, convertView, parent);
                	TextView text = v.findViewById(R.id.textName);
                   	KNChart chartSelected = null;
                	chartSelected = myCharts.get(groupPosition);
                	text.setText(chartSelected.getName().toUpperCase() + " - (" + chartSelected.getUnit() + ")");
                	return v;
                }
            }
        );
        if(mGroupSelected != -1 && mGroupSelected < this.getExpandableListAdapter().getGroupCount()){
        	this.getExpandableListView().expandGroup(mGroupSelected);
        }
        
   	
    }
    
    public void openConfigChart(int chrtPos){
    	KNChart chrt = myCharts.get(chrtPos);
	    Intent i = new Intent(this, ConfigChartActivity.class);
	    i.putExtra(ConstantsAdmin.CHART_SELECCIONADO, String.valueOf(chrt.getId()));
	    this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_CHART);
    }
    
    public void openEditarChart(int chrtPos){
    	KNChart chrt = myCharts.get(chrtPos);
	    Intent i = new Intent(this, AltaChartActivity.class);
	    i.putExtra(ConstantsAdmin.CHART_SELECCIONADO, String.valueOf(chrt.getId()));
	    mGroupSelected = chrtPos;
	    this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_EDITAR_CHART);
    }
    
    public void openChart(int chrtPos){
       	KNChart chrt = myCharts.get(chrtPos);
        Intent i = new Intent(this, SimpleLinearChartActivity.class);
        i.putExtra(ConstantsAdmin.CHART_SELECCIONADO, String.valueOf(chrt.getId()));
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_DRAW_CHART);
   	
    }
    
   
    
    protected void openManagerItemChart(int chrtPos) {
    	KNChart chrt = myCharts.get(chrtPos);
        Intent i = new Intent(this, ItemChartManagerActivity.class);
        i.putExtra(ConstantsAdmin.CHART_SELECCIONADO, String.valueOf(chrt.getId()));
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_MANAGER_ITEM_CHART);
    }
        
    
    protected void openAltaChart() {
        Intent i = new Intent(this, AltaChartActivity.class);
        this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_ALTA_CHART);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        this.recargarLista();
    }
    



}