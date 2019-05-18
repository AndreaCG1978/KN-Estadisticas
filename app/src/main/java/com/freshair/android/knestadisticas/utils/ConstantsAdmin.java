package com.freshair.android.knestadisticas.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.widget.Toast;

import com.freshair.android.knestadisticas.R;
import com.freshair.android.knestadisticas.database.DataBaseManager;
import com.freshair.android.knestadisticas.dtos.KNChart;
import com.freshair.android.knestadisticas.dtos.KNConfigChart;
import com.freshair.android.knestadisticas.dtos.KNItemChart;



public class ConstantsAdmin {
	
	
//	private static DataBaseManager mDBManager = null;
	public static CursorLoader cursorGraficos = null;
	public static CursorLoader cursorConfig = null;

	/*
	public static DataBaseManager getmDBManager() {
		return mDBManager;
	}

	public static void setmDBManager(DataBaseManager mDBManager) {
		ConstantsAdmin.mDBManager = mDBManager;
	}
	*/
    public static void inicializarBD(DataBaseManager mDBManager){
    /*	if(mDBManager == null){
    		mDBManager = new DataBaseManager(act);	
    	}*/
    	mDBManager.open();
    }

	public static void finalizarBD(DataBaseManager mDBManager){
		if(mDBManager != null){
			mDBManager.close();
		}
	}

	public static void upgradeBD(DataBaseManager mDBManager){
    	mDBManager.upgradeDB();
	}

	public static void createBD(DataBaseManager mDBManager){
    	mDBManager.createBD();
    }

    public static void mostrarMensajeAplicacion(Context context, String message){
    	mostrarMensajeAplicacion(context, message, 4);
    }
    
    public static void mostrarMensajeAplicacion(Context context, String message, int duration){
    	Toast t = Toast.makeText(context.getApplicationContext(), message, duration);
		t.show();    	
    }

	public static String querySelectionColumnByValue(String column, Object value) {
		String selection = null;
		if (column != null && !column.equals("")) {
			selection = column + "= '" + value + "'";
		}
		return selection;
	}


	public static KNChart obtenerChartId(Activity context, long idChart, DataBaseManager mDBManager){
    	KNChart chart;
    	Cursor chartCursor;
		inicializarBD(mDBManager);
		chartCursor = mDBManager.fetchChartsForId(idChart);
	//	context.startManagingCursor(chartCursor);
		chart = cursorToChartDto(chartCursor);
    	finalizarBD(mDBManager);
    	return chart;
    }
    
    private static KNChart obtenerChartNamed(Activity context, String chartName, DataBaseManager mDBManager){
    	KNChart chart = null;
    	Cursor chartCursor = null;
		inicializarBD(mDBManager);
		chartCursor = mDBManager.fetchChartsForName(chartName);
	//	context.startManagingCursor(chartCursor);
		if(chartCursor.getCount() > 0){
			chartCursor.moveToFirst();
			chart = cursorToChartDto(chartCursor);
		}
    	finalizarBD(mDBManager);
    	return chart;
    }
    
    
    
    public static KNConfigChart obtenerConfigChart(Activity context, DataBaseManager mDBManager){
    	KNConfigChart config = new KNConfigChart();
    	Cursor c = null;
		inicializarBD(mDBManager);
		c = mDBManager.fetchConfig(1);
		context.startManagingCursor(c);
		config = cursorToConfigDto(c);
    	finalizarBD(mDBManager);
    	return config;
    }
    
    public static long obtenerTablaConfigSize(Activity context, DataBaseManager mDBManager){
    	long result = 0;
    	inicializarBD(mDBManager);
    	result = mDBManager.tablaConfigSize();
    	finalizarBD(mDBManager);
    	return result;
    }
    
    
    public static KNItemChart obtenerItemId(Activity context, long idItem, DataBaseManager mDBManager){
    	KNItemChart item = new KNItemChart();
    	Cursor itemCursor = null;
		inicializarBD(mDBManager);
		itemCursor = mDBManager.fetchItemForId(idItem);
		context.startManagingCursor(itemCursor);
		item = cursorToItemDto(itemCursor);
    	finalizarBD(mDBManager);
    	return item;
    }
    
    public static Cursor obtenerAllChartCursor(Activity context, DataBaseManager mDBManager){
    	Cursor chartCursor = null;
    	inicializarBD(mDBManager);
		chartCursor = mDBManager.fetchChartsForName(null);
		finalizarBD(mDBManager);
		return chartCursor;
    }
    
    public static String[] obtenerAllChartNames(Activity context, DataBaseManager mdbManager){
    	String[] result = null;
    	try {
 
    		List<KNChart> charts = obtenerAllChart(context, mdbManager);
    		Iterator<KNChart> it = charts.iterator();
    		result = new String[charts.size()];
    		KNChart chart = null;
    		int i = 0;
         	while(it.hasNext()){
         		chart = it.next();
    			result[i] = chart.getName();
    			i = i + 1;
    		}
			
		} catch (Exception e) {
			e.getMessage();
		}

    	return result;
    }

    
    public static ArrayList<KNItemChart> obtenerItemsDeChart(KNChart chart, Activity context, DataBaseManager mDBManager){
    	ArrayList<KNItemChart> items = new ArrayList<>();
    	KNItemChart item = null;
    	Cursor itemsCursor = null;
    	inicializarBD(mDBManager);
    	itemsCursor = mDBManager.fetchItemsForChart(chart);
    	context.startManagingCursor(itemsCursor);
    	itemsCursor.moveToFirst();
    	while(!itemsCursor.isAfterLast()){
    		item = cursorToItemDto(itemsCursor);
    		item.setChartId(String.valueOf(chart.getId()));
    		items.add(item);
    		itemsCursor.moveToNext();
    		
    	}
    	return items;
    }
    
    public static ArrayList<KNItemChart> obtenerItemsDeChart(String idChartSelected, String yearSelected, String monthSelected, Activity context, DataBaseManager mDBManager){
    	ArrayList<KNItemChart> items = new ArrayList<>();
    	KNItemChart item = null;
    	Cursor itemsCursor = null;
    	inicializarBD(mDBManager);
    	itemsCursor = mDBManager.fetchItemsForChart(idChartSelected, yearSelected, monthSelected);
    	context.startManagingCursor(itemsCursor);
    	itemsCursor.moveToFirst();
    	while(!itemsCursor.isAfterLast()){
    		item = cursorToItemDto(itemsCursor);
    		item.setChartId(idChartSelected);
    		items.add(item);
    		itemsCursor.moveToNext();
    		
    	}
    	return items;
    }
    
    public static ArrayList<KNItemChart> obtenerItemsDeChartOrdenadosPorAnioYMes(KNChart chart, Activity context, DataBaseManager mDBManager){
    	ArrayList<KNItemChart> items = new ArrayList<>();
    	KNItemChart item = null;
    	Cursor itemsCursor = null;
    	inicializarBD(mDBManager);
    	itemsCursor = mDBManager.fetchItemsForChartOrderBYearAndMonth(chart);
    	context.startManagingCursor(itemsCursor);
    	itemsCursor.moveToFirst();
    	while(!itemsCursor.isAfterLast()){
    		item = cursorToItemDto(itemsCursor);
    		item.setChartId(String.valueOf(chart.getId()));
    		items.add(item);
    		itemsCursor.moveToNext();
    		
    	}
    	return items;
    }
    
    public static ArrayList<KNChart> obtenerAllChart(Activity context, DataBaseManager mDBManager){
    	ArrayList<KNChart> allChart = new ArrayList<>();

    	/*


    	KNChart chart = null;
    	Cursor chartCursor = null;
		inicializarBD(mDBManager);
		chartCursor = mDBManager.fetchChartsForName(null);
		context.startManagingCursor(chartCursor);
		chartCursor.moveToFirst();
        while(!chartCursor.isAfterLast()){
        	chart = cursorToChartDto(chartCursor);
        	allChart.add(chart);
        	chartCursor.moveToNext();
          }

	  	finalizarBD(mDBManager);

*/
		KNChart chart = null;
		Cursor chartCursor = null;

		CursorLoader cursorLoader = null;
		cursorLoader = mDBManager.cursorLoaderGraficosPorNombre(null, context);
			//		cursor = mDBManager.fetchCategoriasActivasPorNombre(null);
		if(cursorLoader != null){
			//	startManagingCursor(cursor);
			chartCursor = cursorLoader.loadInBackground();
			chartCursor.moveToFirst();
			while(!chartCursor.isAfterLast()){
				chart = cursorToChartDto(chartCursor);
				allChart.add(chart);
				chartCursor.moveToNext();
			}

		}

        return allChart;
    }
    
    public static void eliminarChart(long idChart, Activity context, DataBaseManager mDBManager){
    	inicializarBD(mDBManager);
    	mDBManager.removeChart(idChart);
    	finalizarBD(mDBManager);
    }
    
    public static void eliminarItemChart(long idItem, Activity context, DataBaseManager mDBManager){
    	inicializarBD(mDBManager);
    	mDBManager.removeItemChart(idItem);
    	finalizarBD(mDBManager);
    }
    
    public static void eliminarItemsCharts(String idChart, String year, Activity context, DataBaseManager mDBManager){
    	inicializarBD(mDBManager);
    	mDBManager.removeItemsCharts(idChart, year);
    	finalizarBD(mDBManager);
    }
    
    public static void eliminarItemsCharts(String idChart, String year, String month, Activity context, DataBaseManager mDBManager){
    	inicializarBD(mDBManager);
    	mDBManager.removeItemsCharts(idChart, year, month);
    	finalizarBD(mDBManager);
    }
    
    public static long agregarChart(KNChart chartSelec, Activity context, DataBaseManager mDBManager){
    	long id = -1;
		ConstantsAdmin.inicializarBD(mDBManager);
		id = mDBManager.createOrUpdateChart(chartSelec);
		ConstantsAdmin.finalizarBD(mDBManager);
		return id;
    }
    
    public static void agregarConfigChart(KNConfigChart config, Activity context, DataBaseManager mDBManager){
		ConstantsAdmin.inicializarBD(mDBManager);
		mDBManager.createOrUpdateConfigChart(config);
		ConstantsAdmin.finalizarBD(mDBManager);
    }

    public static void agregarItem(KNItemChart item, Activity context, DataBaseManager mDBManager){
    	KNItemChart auxItem = new KNItemChart();
    	Cursor cur = null;
		inicializarBD(mDBManager);
		cur = mDBManager.fetchItemsForChartAndDateTime(item.getChartId(), item.getYear(), item.getMonth(), item.getDay(), item.getHour(), item.getMin());
		if(cur.getCount() > 0){
			cur.moveToFirst();
			auxItem = cursorToItemDto(cur);
		}
		if(auxItem.getId() == -1){
			mDBManager.createOrUpdateItemChart(item);
		}else{
			auxItem.setValue(item.getValue());
			mDBManager.createOrUpdateItemChart(auxItem);
		}
		                    
		ConstantsAdmin.finalizarBD(mDBManager);
    }

    
    private static KNConfigChart cursorToConfigDto(Cursor c){
    	String temp = null;
    	long id = -1;
    	KNConfigChart config = new KNConfigChart();
    	if(c != null){
	 //   	context.stopManagingCursor(perCursor);
    		id = c.getLong(c.getColumnIndex(ConstantsAdmin.KEY_ROWID));
	        config.setId(id);
        
	        temp = c.getString(c.getColumnIndex(ConstantsAdmin.KEY_CONFIG_BACKGROUND));
	        config.setBackground(temp);
	        temp = c.getString(c.getColumnIndex(ConstantsAdmin.KEY_CONFIG_TIME));
	        config.setTime(temp);
	        temp = c.getString(c.getColumnIndex(ConstantsAdmin.KEY_CONFIG_GRID));
	        config.setGrid(temp);
	        temp = c.getString(c.getColumnIndex(ConstantsAdmin.KEY_CONFIG_LABEL));
	        config.setLabel(temp);
	        temp = c.getString(c.getColumnIndex(ConstantsAdmin.KEY_CONFIG_LINE));
	        config.setLine(temp);
	        temp = c.getString(c.getColumnIndex(ConstantsAdmin.KEY_CONFIG_POINT));
	        config.setPoint(temp);
	        int val = c.getInt(c.getColumnIndex(ConstantsAdmin.KEY_CONFIG_SHOWGRID));
	        if(val == 0){
	        	config.setShowGrid(false);
	        }else{
	        	config.setShowGrid(true);
	        }
	        val = c.getInt(c.getColumnIndex(ConstantsAdmin.KEY_CONFIG_SHOWVALUES));
	        if(val == 0){
	        	config.setShowValue(false);
	        }else{
	        	config.setShowValue(true);
	        }
    	}
    	return config;
    }
  
    
    private static KNChart cursorToChartDto(Cursor chartCursor){
    	String temp = null;
    	int val = 0;
    	KNChart chart = new KNChart();
    	if(chartCursor != null){
	 //   	context.stopManagingCursor(perCursor);
    		temp = chartCursor.getString(chartCursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
	        chart.setId(Long.valueOf(temp));
	        temp = chartCursor.getString(chartCursor.getColumnIndex(ConstantsAdmin.KEY_CHART_NAME));
	        chart.setName(temp);
	        temp = chartCursor.getString(chartCursor.getColumnIndex(ConstantsAdmin.KEY_CHART_DESCRIPTION));
	        chart.setDescription(temp);
	        temp = chartCursor.getString(chartCursor.getColumnIndex(ConstantsAdmin.KEY_CHART_UNIT));
	        chart.setUnit(temp);
	        
	        temp = chartCursor.getString(chartCursor.getColumnIndex(ConstantsAdmin.KEY_CHART_BACKGROUND_COLOR));
	        chart.setBackgroundColor(temp);
	        temp = chartCursor.getString(chartCursor.getColumnIndex(ConstantsAdmin.KEY_CHART_FORMAT_TIME));
	        chart.setFormatTime(temp);
	        temp = chartCursor.getString(chartCursor.getColumnIndex(ConstantsAdmin.KEY_CHART_GRID_COLOR));
	        chart.setGridColor(temp);
	        temp = chartCursor.getString(chartCursor.getColumnIndex(ConstantsAdmin.KEY_CHART_LABEL_COLOR));
	        chart.setLabelColor(temp);
	        temp = chartCursor.getString(chartCursor.getColumnIndex(ConstantsAdmin.KEY_CHART_LINE_COLOR));
	        chart.setLineColor(temp);
	        temp = chartCursor.getString(chartCursor.getColumnIndex(ConstantsAdmin.KEY_CHART_POINT_STYLE));
	        chart.setPointStyle(temp);
	        
	        val = chartCursor.getInt(chartCursor.getColumnIndex(ConstantsAdmin.KEY_CHART_SHOW_GRID));
	        if(val == 0){
	        	chart.setShowGrid(false);
	        }else{
	        	chart.setShowGrid(true);
	        }
	        val = chartCursor.getInt(chartCursor.getColumnIndex(ConstantsAdmin.KEY_CHART_SHOW_VALUE));
	        if(val == 0){
	        	chart.setShowValue(false);
	        }else{
	        	chart.setShowValue(true);
	        }
    	}
    	return chart;
    }
    
    private static KNItemChart cursorToItemDto(Cursor itemsCursor){
    	String temp = null;
    	long id = -1;
    	KNItemChart item = new KNItemChart();
    	if(itemsCursor != null){
	 //   	context.stopManagingCursor(perCursor);
	        id = itemsCursor.getLong(itemsCursor.getColumnIndex(ConstantsAdmin.KEY_ROWID));
	        item.setId(id);
	        temp = itemsCursor.getString(itemsCursor.getColumnIndex(ConstantsAdmin.KEY_ITEMCHART_DAY));
	        item.setDay(temp);
	        temp = itemsCursor.getString(itemsCursor.getColumnIndex(ConstantsAdmin.KEY_ITEMCHART_MONTH));
	        item.setMonth(temp);
	        temp = itemsCursor.getString(itemsCursor.getColumnIndex(ConstantsAdmin.KEY_ITEMCHART_YEAR));
	        item.setYear(temp);
	        temp = itemsCursor.getString(itemsCursor.getColumnIndex(ConstantsAdmin.KEY_ITEMCHART_VALUE));
	        item.setValue(temp);
	        temp = itemsCursor.getString(itemsCursor.getColumnIndex(ConstantsAdmin.KEY_ITEMCHART_HOUR));
	        item.setHour(temp);
	        temp = itemsCursor.getString(itemsCursor.getColumnIndex(ConstantsAdmin.KEY_ITEMCHART_MIN));
	        item.setMin(temp);
	        temp = itemsCursor.getString(itemsCursor.getColumnIndex(ConstantsAdmin.KEY_ITEMCHART_CHARTID));
	        item.setChartId(temp);
    	}
    	return item;
    }
    
    
//  NOMBRE DE LA BASE DE DATOS    
	public static final String DATABASE_NAME = "FreshAir_ChartApp";
	public static final int DATABASE_VERSION = 1;
	public static final String TAG = "DataBaseManager";

    
//  NOMBRES DE TABLAS	
    public static final String TABLE_CHART = "tablaChart";
    public static final String TABLE_ITEM_CHART = "tablaItemChart";
    public static final String TABLE_CONFIG = "tablaConfig";
    
//  NOMBRE DE ATRIBUTOS
    public static final String KEY_ROWID = "_id";
    public static final String KEY_CHART_NAME = "name";
    public static final String KEY_CHART_DESCRIPTION = "description";
    public static final String KEY_CHART_UNIT = "unit"; 
    public static final String KEY_CHART_POINT_STYLE = "pointStyle"; 
    public static final String KEY_CHART_LINE_COLOR = "lineColor"; 
    public static final String KEY_CHART_LABEL_COLOR = "labelColor";
    public static final String KEY_CHART_GRID_COLOR = "gridColor";
    public static final String KEY_CHART_BACKGROUND_COLOR = "backgroundColor";
    public static final String KEY_CHART_FORMAT_TIME = "formatTime";
    public static final String KEY_CHART_SHOW_GRID = "showGrid";
    public static final String KEY_CHART_SHOW_VALUE = "showValue";

    public static final String KEY_ITEMCHART_DAY = "day";
    public static final String KEY_ITEMCHART_MONTH = "month";
    public static final String KEY_ITEMCHART_YEAR = "year";
    public static final String KEY_ITEMCHART_VALUE = "value";
    public static final String KEY_ITEMCHART_CHARTID = "chartID";    
    public static final String KEY_ITEMCHART_HOUR = "hour"; 
    public static final String KEY_ITEMCHART_MIN = "min";  
    
    public static final String KEY_CONFIG_BACKGROUND = "background";
    public static final String KEY_CONFIG_LINE = "line";
    public static final String KEY_CONFIG_LABEL = "label";
    public static final String KEY_CONFIG_GRID = "grid";
    public static final String KEY_CONFIG_TIME = "time";
    public static final String KEY_CONFIG_POINT = "point";
    public static final String KEY_CONFIG_SHOWGRID = "showGrid";
    public static final String KEY_CONFIG_SHOWVALUES = "showValue";
    
    
//	OPCIONES DE MENU
    
    public static final int ACTIVITY_EJECUTAR_ALTA_CHART=1;
    public static final int ACTIVITY_EJECUTAR_EDITAR_CHART=2;
    public static final int ACTIVITY_EJECUTAR_MANAGER_ITEM_CHART=3;
    public static final int ACTIVITY_EJECUTAR_ALTA_ITEM_CHART=4;
    public static final int ACTIVITY_EJECUTAR_EDITAR_ITEM_CHART=5;
    // --Commented out by Inspection (14/11/18 19:15):public static final int ACTIVITY_EJECUTAR_ELIMINAR_ITEM_CHART=6;
    public static final int ACTIVITY_EJECUTAR_VIEW_ITEMS=7;
    public static final int ACTIVITY_EJECUTAR_DRAW_CHART=8;
    public static final int ACTIVITY_EJECUTAR_BORRAR_CHART=9;
    public static final int ACTIVITY_EJECUTAR_COMPARAR_CHARTS=10;
    public static final int ACTIVITY_EJECUTAR_CONFIG_CHART=11;
    public static final int ACTIVITY_EJECUTAR_CONFIG_COMPARE_CHART=12;
    public static final int ACTIVITY_EJECUTAR_EXPORT_CHART_PICTURE=13;
    public static final int ACTIVITY_EJECUTAR_MOSTRAR_COMPARACION=14;
    public static final int ACTIVITY_EJECUTAR_HELP=15;
    public static final int ACTIVITY_EJECUTAR_ABOUT_ME=16;
    public static final int ACTIVITY_EJECUTAR_EXPORT_CHART_TEXT=17;
    // VARIOS
    
    public static final String CHART_SELECCIONADO = "chartSeleccionado";
    public static final String ITEM_CHART_SELECCIONADO  = "itemChartSeleccionado";
    public static final String YEAR_SELECCIONADO  = "yearSeleccionado";
    public static final String MONTH_SELECCIONADO  = "monthSeleccionado";
    public static final String TIPO_COMPARACION  = "tipoComparacion";
    public static final String COMPARACION_EN_TIEMPO  = "comparacionEnTiempo";
    public static final String COMPARACION_SOBREPUESTA  = "comparacionSobrepuesta";

    // CONFIGURACION DE CHARTS
    
    public static final PointStyle[] tiposPuntos = {PointStyle.CIRCLE,PointStyle.DIAMOND, PointStyle.POINT, PointStyle.SQUARE, PointStyle.TRIANGLE, PointStyle.X };
    public static final String[] formatTime = {"yyyy/MM/dd hh:mm", "yyyy/MM/dd","hh:mm"};
    
    // FECHA HORA ANTERIOR
     
    public static int mYear = 2000;
    public static String mMonth = null;
    public static String mDay = null;
    public static String mHour = null;
    public static String mMin = null;
    
    public static boolean guardoItem = false;
    


    private static final String pictureExtension  = ".jpg";
    private static final String textExtension  = ".txt";

    private static final String comparisonName  = "comparisonChart";
    private static final String chartName  = "kngraphit";
    
 
    
    private static Asociacion comprobarSDCard(Activity context){
    	Asociacion map = null;
        String auxSDCardStatus = Environment.getExternalStorageState();
        boolean sePuede = false;
        String msg = null;

		switch (auxSDCardStatus) {
			case Environment.MEDIA_MOUNTED:
				sePuede = true;
				break;
			case Environment.MEDIA_MOUNTED_READ_ONLY:
				msg = context.getString(R.string.mensaje_error_tarjeta_solo_lectura);
				sePuede = false;
				break;
			case Environment.MEDIA_NOFS:
				msg = context.getString(R.string.mensaje_error_tarjeta_no_formato);
				sePuede = false;
				break;
			case Environment.MEDIA_REMOVED:
				msg = context.getString(R.string.mensaje_error_tarjeta_no_montada);
				sePuede = false;
				break;
			case Environment.MEDIA_SHARED:
				msg = context.getString(R.string.mensaje_error_tarjeta_shared);
				sePuede = false;
				break;
			case Environment.MEDIA_UNMOUNTABLE:
				msg = context.getString(R.string.mensaje_error_tarjeta_unmountable);
				sePuede = false;
				break;
			case Environment.MEDIA_UNMOUNTED:
				msg = context.getString(R.string.mensaje_error_tarjeta_unmounted);
				sePuede = false;
				break;
		}
        map = new Asociacion(sePuede, msg);
        
        return map;
    }
    
/*    
    
    private static boolean canStoreFile(){
  	  boolean mExternalStorageAvailable = false;
  	  boolean mExternalStorageWriteable = false;

  	  String state = Environment.getExternalStorageState();
  	  if (Environment.MEDIA_MOUNTED.equals(state)) {
  	      mExternalStorageAvailable = mExternalStorageWriteable = true;
  	  } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
  	      mExternalStorageAvailable = true;
  	      mExternalStorageWriteable = false;
  	  } else {
  	      mExternalStorageAvailable = mExternalStorageWriteable = false;
  	  }
  	  return mExternalStorageAvailable && mExternalStorageWriteable;
    }
  */
    
    
    public static void exportChart(Activity context, GraphicalView chartView, KNChart chart){
     	exportChartPrivado(context, chartView, folderPicture, chart.getName() + pictureExtension);
    }
    
    public static void exportComparison(Activity context, GraphicalView chartView){
    	exportChartPrivado(context, chartView, folderComparison,  comparisonName + pictureExtension);
    }
    
    
    public static void exportTxT(Activity context, List<KNItemChart> listaItems, KNChart chart){
    	Asociacion canStore = null;
    	Boolean boolValue = false;
    	String msg = null;
    	String body = null;
        try
        {
        	canStore = comprobarSDCard(context);
     		boolValue = (Boolean)canStore.getKey();
     		msg = (String) canStore.getValue();
     		if(boolValue){
     			body = obtenerTxtDeItems(listaItems, chart, context);
     			almacenarArchivo(folderTxt, chart.getName() + textExtension , body);
     			mostrarMensajeDialog(context, context.getString(R.string.mensaje_exito_exportar_txt));
     		}else{
     			mostrarMensajeDialog(context, msg);
     		}
     		

		 } catch (FileNotFoundException e) {
			 mostrarMensajeDialog(context, context.getString(R.string.error_exportar_txt)); 
	  		  //mostrarMensajeAplicacion(context, e.toString(), 11);
	  	 } catch (IOException e) {
	  		  try {
	  			  almacenarArchivo(folderTxt, chartName + textExtension, body);
	  			  mostrarMensajeDialog(context, context.getString(R.string.mensaje_exito_exportar_txt));
	  		  } catch (IOException e2) {
	  			  mostrarMensajeDialog(context, context.getString(R.string.error_exportar_txt)); 
	  		  }
	  	  }catch (Exception e) {
				 mostrarMensajeDialog(context, context.getString(R.string.error_exportar_txt)); 
		  }
    }       
    
   
    private static void exportChartPrivado(Activity context, GraphicalView chartView, String nombreDirectorio, String nombreArchivo){
  	  //View v1 = relativeView.getRootView();
  	  chartView.setDrawingCacheEnabled(true);
  	  Bitmap bm = chartView.getDrawingCache();
  	  Asociacion canStore = null;
  	  Boolean boolValue = false;
  	  String msg = null;
  	  try {
  		  canStore = comprobarSDCard(context);
  		  boolValue = (Boolean)canStore.getKey();
  		  msg = (String) canStore.getValue();
  		  if(boolValue){
  			  almacenarImagen(context, nombreDirectorio, nombreArchivo, bm);
  		//      mostrarMensajeDialog(context,context.getString(R.string.mensaje_exito_exportar_chart) + nombreArchivo);
  		  }else{
  			  mostrarMensajeDialog(context, msg);
  		  }
  		      //    MediaStore.Images.Media.insertImage(getContentResolver(), bm, barcodeNumber + ".jpg Card Image", barcodeNumber  + ".jpg Card Image");
  	  } catch (FileNotFoundException e) {
  		  mostrarMensajeDialog(context, context.getString(R.string.mensaje_error_exportar_chart));
  		  //mostrarMensajeAplicacion(context, e.toString(), 11);
  	  } catch (IOException e) {
  		  try {
			  almacenarImagen(context, nombreDirectorio, chartName + pictureExtension, bm);
  		  } catch (IOException e2) {
  			  mostrarMensajeDialog(context, context.getString(R.string.mensaje_error_exportar_chart));
  		  }
  		  
		  //mostrarMensajeAplicacion(context, e.toString(), 11);
  	  }catch (Exception e) {
  		  mostrarMensajeDialog(context, context.getString(R.string.mensaje_error_exportar_chart));
		//  mostrarMensajeAplicacion(context, e.getMessage(), 11);
	  }
  	 
    }
    

    
    private static void almacenarImagen(Activity context, String nombreDirectorio, String nombreArchivo, Bitmap bm) throws IOException {
    	  String path = obtenerPath(nombreDirectorio);
	      OutputStream fOut = null;
	      File dir = new File(path);
	      dir.mkdirs();
	      
	      File file = new File(dir.getPath(), nombreArchivo);
	      if(!file.exists()){
	    	  file.createNewFile();
	      }
	      fOut = new FileOutputStream(file);
	      bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
	 //     bm.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
	      fOut.flush();
	      fOut.close();

	      MediaStore.Images.Media.insertImage(context.getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
	      mostrarMensajeDialog(context,context.getString(R.string.mensaje_exito_exportar_chart) + file.getPath());
    }
    

    private static void almacenarArchivo(String nombreDirectorio, String nombreArchivo, String body) throws IOException {
    	  String path = obtenerPath(nombreDirectorio);
	//      path = path + File.separator + nombreDirectorio;
	      File dir = new File(path);
	     // dir.mkdirs();
	      dir.mkdirs();
	      
	      File file = new File(dir.getPath(), nombreArchivo);
	      if(!file.exists()){
	    	  file.createNewFile();
	      }
	      PrintWriter  writer = new PrintWriter(file);
	      writer.append(body);
	      writer.flush();
	      writer.close();
    }
    
    
    
    public static void mostrarMensajeDialog(Activity context, String message){
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
    	builder.setMessage(message)
    	       .setCancelable(true)
    	       .setPositiveButton(R.string.label_ok, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	        	    dialog.toString();       				
    	           }
    	       });
    	builder.show(); 
    }
    
    private static String obtenerTxtDeItems(List<KNItemChart> list, KNChart chart, Activity context){
        StringBuilder result = null;
        Iterator<KNItemChart> it = list.iterator();
        KNItemChart item = null;
        String tab1, tab2, tab3 = null;
        tab1 = "\t";
        tab2 = "\t\t";
        tab3 = "\t\t\t";
        result = new StringBuilder(chart.getName().toUpperCase());
        result.append(ENTER).append(ENTER);
        item = it.next();
        String anio, mes, dia, anioAnt, mesAnt, diaAnt = null;
        anio = item.getYear();
        anioAnt = item.getYear();
        mes = item.getMonth();
        mesAnt = item.getMonth();
        dia = item.getDay();
        diaAnt = item.getDay();
        result.append(context.getString(R.string.label_year).toUpperCase()).append(": ").append(item.getYear()).append(ENTER);
        result.append(tab1).append(context.getString(R.string.label_month).toUpperCase()).append(": ").append(item.getMonth()).append(ENTER);
        result.append(tab2).append(context.getString(R.string.label_day).toUpperCase()).append(": ").append(item.getDay()).append(ENTER);
        result.append(tab3).append(context.getString(R.string.label_hora).toUpperCase()).append(": ").append(item.getHourMin()).append(" - ").append(context.getString(R.string.label_valor).toUpperCase()).append(": ").append(item.getValue());
        while(it.hasNext()){
            result.append(ENTER);
            item = it.next();
            anioAnt = anio;
            mesAnt = mes;
            diaAnt = dia;
            anio = item.getYear();
            mes = item.getMonth();
            dia = item.getDay();
            if(anio.equals(anioAnt)){
                result.append(tab1);
                if(mes.equals(mesAnt)){
                    result.append(tab1);
                    if(dia.equals(diaAnt)){
                        result.append(tab1).append(context.getString(R.string.label_hora).toUpperCase()).append(": ").append(item.getHourMin()).append(" - ").append(context.getString(R.string.label_valor).toUpperCase()).append(": ").append(item.getValue());
                    }else{
                        result.append(context.getString(R.string.label_day).toUpperCase()).append(": ").append(item.getDay()).append(ENTER);
                        result.append(tab3).append(context.getString(R.string.label_hora).toUpperCase()).append(": ").append(item.getHourMin()).append(" - ").append(context.getString(R.string.label_valor).toUpperCase()).append(": ").append(item.getValue());
                    }
                }else{
                    result.append(context.getString(R.string.label_month).toUpperCase()).append(": ").append(item.getMonth()).append(ENTER);
                    result.append(tab2).append(context.getString(R.string.label_day).toUpperCase()).append(": ").append(item.getDay()).append(ENTER);
                    result.append(tab3).append(context.getString(R.string.label_hora).toUpperCase()).append(": ").append(item.getHourMin()).append(" - ").append(context.getString(R.string.label_valor).toUpperCase()).append(": ").append(item.getValue());
                }
            }else{
                result.append(context.getString(R.string.label_year).toUpperCase()).append(": ").append(item.getYear()).append(ENTER);
                result.append(tab1).append(context.getString(R.string.label_month).toUpperCase()).append(": ").append(item.getMonth()).append(ENTER);
                result.append(tab2).append(context.getString(R.string.label_day).toUpperCase()).append(": ").append(item.getDay()).append(ENTER);
                result.append(tab3).append(context.getString(R.string.label_hora).toUpperCase()).append(": ").append(item.getHourMin()).append(" - ").append(context.getString(R.string.label_valor).toUpperCase()).append(": ").append(item.getValue());
            }
        }
        result.append(ENTER);

        return result.toString();

    }
    
    public static List<KNChart> chartsParaComparar = null;
    public static String tipoComparacionSelected = null;
    public static final int cantMaxComparacion = 6;

    
    public static final String UrlBoxico = "http://www.boxico.com.ar";
    

    
    private static String obtenerPath(String nombreDirectorio){
    	String path = Environment.getExternalStorageDirectory().toString();
    	return path + File.separator + folderKN + File.separator + nombreDirectorio;
    }
    
   
    public static void exportarCSV(Activity context, int formatInput, List<KNItemChart> listaItems, KNChart chart){
    	Asociacion canStore = null;
    	Boolean boolValue = false;
    	String msg = null;
    	String body = null;
        try
        {
        	canStore = comprobarSDCard(context);
     		boolValue = (Boolean)canStore.getKey();
     		msg = (String) canStore.getValue();
     		if(boolValue){
     			body = obtenerCsvDeItems(listaItems, formatInput);
     			almacenarArchivo(folderCSV, chart.getName() + csvExtension , body);
     			mensaje = context.getString(R.string.mensaje_exito_exportar_csv);
     		}else{
    			mensaje = msg;
     		}
     		

		 } catch (FileNotFoundException e) {
	  		  mensaje = context.getString(R.string.error_exportar_csv);
	  		  //mostrarMensajeAplicacion(context, e.toString(), 11);
	  	 } catch (IOException e) {
	  		  try {
	  			  body = null;
	  			  body = obtenerCsvDeItems(listaItems, formatInput);
	  			  almacenarArchivo(folderCSV, chartName + csvExtension, body);
	  			  mensaje = context.getString(R.string.mensaje_exito_exportar_csv);
	  		  } catch (IOException e2) {
	  			  mensaje = context.getString(R.string.error_exportar_csv);
	  		  }
	  	  }catch (Exception e) {
	  		mensaje = context.getString(R.string.error_exportar_csv);
	  	  }    	
    	
    }
    
    private static String obtenerCsvDeItemsAllSeparated(List<KNItemChart> list){
    	String result = "";
        Iterator<KNItemChart> it = list.iterator();
        KNItemChart item = null;
        while(it.hasNext()){
            item = it.next();
            result = result + item.getYear()+ COMA + item.getMonth() + COMA + item.getDay() + COMA + item.getHour() + COMA + item.getMin() + COMA + item.getValue() + ENTER;
        }
    	return result;
    }
    
    private static String obtenerCsvDeItemsDateTime(List<KNItemChart> list){
    	String result = "";
        Iterator<KNItemChart> it = list.iterator();
        KNItemChart item = null;
        while(it.hasNext()){
            item = it.next();
            result = result + item.getYear()+ SEPARADOR_FECHA + item.getMonth() + SEPARADOR_FECHA + item.getDay() + " " + item.getHourMin() + COMA + item.getValue() + ENTER;
        }
    	return result;
    }
    
    private static String obtenerCsvDeItemsDate(List<KNItemChart> list){
    	String result = "";
        Iterator<KNItemChart> it = list.iterator();
        KNItemChart item = null;
        while(it.hasNext()){
            item = it.next();
            result = result + item.getYear()+ SEPARADOR_FECHA + item.getMonth() + SEPARADOR_FECHA + item.getDay() + COMA + item.getValue() + ENTER;
        }
    	return result;
    }
    
    private static String obtenerCsvDeItems(List<KNItemChart> list, int formatInput){
    	String result = null;
    	if(formatInput == FORMAT_DATETIME){
    		result = obtenerCsvDeItemsDateTime(list);
    	}else if(formatInput == FORMAT_DATE){
    		result = obtenerCsvDeItemsDate(list);
    	}else if(formatInput == FORMAT_ALLSEPARATE){
    		result = obtenerCsvDeItemsAllSeparated(list);
    	}
    	return result;
    }
    
    public static void importarCSVs(Activity context, int formatInput, DataBaseManager mdbManager){
        String body = null;
        File file = null;
        KNChart chart = null;
        KNItemChart item = null;
        List<KNItemChart> items = null;
        int cantChartsCompletos = 0;
        int cantChartsIncompletos = 0;
        try {
        	List<File> files = obtenerFilesCSVs(context);
        	if(files != null){
	            Iterator<File> it = files.iterator();
	            Iterator<KNItemChart> itItems = null;
	
	            while(it.hasNext()){
	                file = it.next();
	                body = obtenerContenidoArchivo(file, context);
	                chart = crearChartDesdeArchivo(file.getName(), context, mdbManager);
	                items = obtenerItemsDeString(body, chart, formatInput);
	                itItems = items.iterator();
	                if(items.size() > 0){
	                	cantChartsCompletos ++;
	                }else{
	                	cantChartsIncompletos ++;
	                }
	                while (itItems.hasNext()){
	                    item = itItems.next();
	                    //ACA GUARDAR EN LA BASE EL ITEM, ACORDATE DE MODIFICAR EL ALTA PARA QUE SI COINCIDE EN FECHA Y HORA SOBREESCRIBA
	                    agregarItem(item, context, mdbManager);
	                }
	
	            }
	            if(cantChartsCompletos > 0 || cantChartsIncompletos > 0){
	            	mensaje = context.getString(R.string.mensaje_exito_importar_csv);
	            }else{
	            	mensaje = context.getString(R.string.mensaje_nada_importar);
	            }
            }
		} catch (Exception e) {
			mensaje = context.getString(R.string.error_importar_csv);
		}

    }

    private static KNChart crearChartDesdeArchivo(String filename, Activity context, DataBaseManager mdbM){
        KNChart chart = new KNChart();
        KNChart oldChart = null;
        long idNuevoChart = -1;
        String[] parts = filename.split(PUNTO);
        chart.setName(parts[parts.length -2]);
        chart.setUnit("X");
        // ACA TRUNCAR EL NOMBRE CON EL SIZE MAXIMO DE NOMBRE DE CHART
		int maxChartNameLength = 15;
		if(chart.getName().length() > maxChartNameLength){
            chart.setName(chart.getName().substring(0, maxChartNameLength - 1));
        }
        // ACA TENGO QUE RECUPERAR DESDE LA BASE EL CHART CON ESTE NAME
        oldChart = obtenerChartNamed(context, chart.getName(), mdbM);
        if(oldChart == null){
            idNuevoChart = agregarChart(chart, context, mdbM);
//             ACA REGISTRAR EL NUEVO CHART
            chart.setId(idNuevoChart);
        }else{
            chart = oldChart;
        }
        return chart;

    }



    private static List<File> obtenerFilesCSVs(Activity context){
        // LEVANTAR TODOS LOS FILES QUE ESTAN EN LA CARPETA KN-CSVfiles
    	  ArrayList<File> files = null;
    	  File[] arrayFiles = null;
    	  File f = null;
    	  boolean boolValue = true;
    	  Asociacion canStore = null;
    	  canStore = comprobarSDCard(context);
   		  boolValue = (Boolean)canStore.getKey();
   		  String msg = (String)canStore.getValue();
   		  if(boolValue){
   		 	  String path = obtenerPath(folderCSV);
   		      File dir = new File(path);
   		      dir.mkdirs();
   		      
   		      arrayFiles = dir.listFiles();

   
   		      files = new ArrayList<>();
   		      if(arrayFiles != null){
				  for (File arrayFile : arrayFiles) {
					  f = arrayFile;
					  if (hasExtension(f.getName(), csvExtension)) {
						  files.add(f);
					  }

				  }
   		      }
   		  }else{
  			  mensaje = msg;
  		  }
   		  return files;
    }
    
    private static boolean hasExtension(String filename, String ext){
    	return filename.toLowerCase().endsWith(ext.toLowerCase());
    }

    private static List<KNItemChart> obtenerItemsDeString(String body, KNChart chart, int inputFormat){
    	List<KNItemChart> items = null;
    	if(inputFormat == FORMAT_ALLSEPARATE){
    		items = obtenerItemsDeStringAllSeparateFormat(body, chart);
    	}else if(inputFormat == FORMAT_DATETIME){
    		items = obtenerItemsDeStringDateTimeFormat(body, chart);
    	}else if(inputFormat == FORMAT_DATE){
    		items = obtenerItemsDeStringDateFormat(body, chart);
    	}
    	
    	return items;
    }
    
    private static List<KNItemChart> obtenerItemsDeStringDateFormat(String body, KNChart chart){
        // PONER EN CONSTANTE EL ENTER Y LA COMA
        KNItemChart item = null;
        String[] items = body.split(ENTER);
        String[] campos = null;
        boolean esValido = false;
        List<KNItemChart> itemList = new ArrayList<>();
		for (String item1 : items) {
			campos = item1.split(COMA);
			if (campos.length == 2) {
				item = new KNItemChart();
				item.setDate(campos[0]);
				item.setValue(campos[1]);
				item.setChartId(String.valueOf(chart.getId()));
				esValido = item.validoDatos();
				if (esValido) {
					itemList.add(item);
				}
			}
		}
        return itemList;
    }
    
    private static List<KNItemChart> obtenerItemsDeStringDateTimeFormat(String body, KNChart chart){
        // PONER EN CONSTANTE EL ENTER Y LA COMA
        KNItemChart item = null;
        String[] items = body.split(ENTER);
        String[] campos = null;
        boolean esValido = false;
        List<KNItemChart> itemList = new ArrayList<>();
		for (String item1 : items) {
			campos = item1.split(COMA);
			if (campos.length == 2) {
				item = new KNItemChart();
				item.setDateTime(campos[0]);
				item.setValue(campos[1]);
				item.setChartId(String.valueOf(chart.getId()));
				esValido = item.validoDatos();
				if (esValido) {
					itemList.add(item);
				}
			}
		}
        return itemList;
    }
    
    private static List<KNItemChart> obtenerItemsDeStringAllSeparateFormat(String body, KNChart chart){
        // PONER EN CONSTANTE EL ENTER Y LA COMA
        KNItemChart item = null;
        String[] items = body.split(ENTER);
        String[] campos = null;
        boolean esValido = false;
        List<KNItemChart> itemList = new ArrayList<>();
		for (String item1 : items) {
			campos = item1.split(COMA);
			if (campos.length == 6) {
				item = new KNItemChart();
				item.setYear(campos[0]);
				item.setMonth(campos[1]);
				item.setDay(campos[2]);
				item.setHour(campos[3]);
				item.setMin(campos[4]);
				item.setValue(campos[5]);
				item.setChartId(String.valueOf(chart.getId()));
				esValido = item.validoDatos();
				if (esValido) {
					itemList.add(item);
				}
			}


		}
        return itemList;


    }


    private static String obtenerContenidoArchivo(File file, Activity context)throws IOException{
        // ACA DEBERIA CARGAR EL CONTENIDO DEL ARCHIVO PASADO COMO PARAMETRO, HACER LOS CONTROLES DE LECTURA
    	String line = null;
    	Asociacion canStore = comprobarSDCard(context);
    	boolean boolValue = (Boolean)canStore.getKey();
    	String msg = (String) canStore.getValue();
    	String result = "";
    	if(boolValue){
    		BufferedReader input =  new BufferedReader(new FileReader(file));
    		line = input.readLine();
    		while(line != null){
    			if(!line.equals("")){
    				result = result + line + ENTER;
    			}
    			
    			line = input.readLine();
    		}
    		
    	}else{
			  mensaje = msg;
    	}
    	
        return result;

    }
    
    private static final String csvExtension  = ".csv";
	private static final String folderCSV = "CSV";
    public static final int ACTIVITY_EJECUTAR_IMPORT_CSV=18;
    public static final int ACTIVITY_EJECUTAR_EXPORT_CHART_CSV=19;
	private static final int FORMAT_DATETIME = 1;
	private static final int FORMAT_DATE = 2;
	private static final int FORMAT_ALLSEPARATE = 3;
	private static final String ENTER = "\n";
	private static final String COMA = ",";
	private static final String PUNTO = "\\.";
	public static final String SEPARADOR_FECHA = "/";
	public static final String SEPARADOR_HORA = ":";
	public static final String SEPARADOR_FECHA_HORA = " ";
	public static String mensaje = null;
    private static final String folderKN = "KNGraphIt";
    private static final String folderTxt = "TXT";
    private static final String folderPicture = "Pics";
    private static final String folderComparison = "Comparison";
}
