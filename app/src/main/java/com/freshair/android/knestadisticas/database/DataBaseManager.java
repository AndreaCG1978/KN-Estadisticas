package com.freshair.android.knestadisticas.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.support.v4.content.CursorLoader;

import com.freshair.android.knestadisticas.dtos.KNChart;
import com.freshair.android.knestadisticas.dtos.KNConfigChart;
import com.freshair.android.knestadisticas.dtos.KNItemChart;
import com.freshair.android.knestadisticas.utils.ConstantsAdmin;

public class DataBaseManager {
   
    
	 private DataBaseHelper mDbHelper;
	 private SQLiteDatabase mDb;
	 private Context mCtx;
	 private boolean isOpened = false;

	 private static final DataBaseManager instanciaUnica = new DataBaseManager();
	 
	 public DataBaseManager(Context ctx) {
	 	this.mCtx = ctx;
	 }

	private DataBaseManager() {
		super();
	}

	public void open() throws SQLException {
		if(!this.isOpened) {
			mDbHelper = new DataBaseHelper(mCtx);
			mDb = mDbHelper.getWritableDatabase();
			this.isOpened = true;
		}
	}

	public void close() {
		if(this.isOpened) {
			mDbHelper.close();
			this.isOpened = false;
		}
	}

	public static DataBaseManager getInstance(Context ctx) {
		instanciaUnica.setmCtx(ctx);
		return instanciaUnica;
	}

	private void setmCtx(Context mCtx) {
		this.mCtx = mCtx;
	}


	public CursorLoader cursorLoaderGraficosPorNombre(Object value, Context context) {
	 	return cursorLoaderGraficosPorCampo(ConstantsAdmin.KEY_CHART_NAME, value, context);
	}


	public CursorLoader cursorLoaderItemChart(Context context, Object value){
		String selection = null;
		if(value != null){
			selection = ConstantsAdmin.querySelectionColumnByValue(ConstantsAdmin.KEY_ROWID, value);
		}
		return new CursorLoader( context, null, null, selection, null, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				Cursor c = null;
				if(mDb.isOpen()){
					c = mDb.query(ConstantsAdmin.TABLE_ITEM_CHART, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
					if (c != null) {
						c.moveToFirst();
					}
				}
				return c;
			}
		};

	}


	public CursorLoader cursorLoaderItemChart(Context context, Object idChart, Object year, Object month){
		String sortOrder = ConstantsAdmin.KEY_ITEMCHART_YEAR + ", " + ConstantsAdmin.KEY_ITEMCHART_MONTH + ", " + ConstantsAdmin.KEY_ITEMCHART_DAY + ", " + ConstantsAdmin.KEY_ITEMCHART_HOUR + ", " + ConstantsAdmin.KEY_ITEMCHART_MIN  + " COLLATE LOCALIZED ASC";
		Cursor result = null;
		String selection = ConstantsAdmin.KEY_ITEMCHART_CHARTID + "= '" + idChart + "' AND " + ConstantsAdmin.KEY_ITEMCHART_YEAR + " = '" + year + "' AND " + ConstantsAdmin.KEY_ITEMCHART_MONTH + " = '" + month + "'";
		return new CursorLoader( context, null, null, selection, null, sortOrder)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				Cursor c = null;
				if(mDb.isOpen()){
					c = mDb.query(ConstantsAdmin.TABLE_ITEM_CHART, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
					if (c != null) {
						c.moveToFirst();
					}
				}
				return c;
			}
		};

	}

	private CursorLoader cursorLoaderGraficosPorCampo(String column, Object value, Context context) {
		String selection = null;
		if(value != null){
			selection = ConstantsAdmin.querySelectionColumnByValue(column, value);
		}

		return new CursorLoader( context, null, null, selection, null, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				Cursor c = null;
				if(mDb.isOpen()){
					c = mDb.query(ConstantsAdmin.TABLE_CHART, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
					if (c != null) {
						c.moveToFirst();
					}
				}
				return c;
			}
		};

	}


	public Cursor fetchConfig(long id) {
		Cursor result = null;
		try{
			result = mDb.query(ConstantsAdmin.TABLE_CONFIG, null, ConstantsAdmin.KEY_ROWID  + "= '" + id + "'", null, null, null, null);
			if (result != null) {
				result.moveToFirst();
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
		}


		return result;
	}

	public CursorLoader cursorLoaderConfig(Context context, long id) {
		final String selection = ConstantsAdmin.KEY_ROWID  + "= '" + id + "'";

		return new CursorLoader( context, null, null, selection, null, null)
		{
			@Override
			public Cursor loadInBackground()
			{
				// You better know how to get your database.
				// You can use any query that returns a cursor.
				Cursor result = null;
				try{
					result = mDb.query(ConstantsAdmin.TABLE_CONFIG, null, selection, null, null, null, null);
					if (result != null) {
						result.moveToFirst();
					}
				}catch (Exception e) {
					// TODO: handle exception
					e.getMessage();
				}


				return result;
			}
		};

	}


	public long createOrUpdateChart(KNChart chart) {
    	 long returnValue = -1;
    	 int showGrid = 1;
    	 int showValue = 1;
         ContentValues initialValues = new ContentValues();
         initialValues.put(ConstantsAdmin.KEY_CHART_NAME, chart.getName());
         initialValues.put(ConstantsAdmin.KEY_CHART_DESCRIPTION, chart.getDescription());
         initialValues.put(ConstantsAdmin.KEY_CHART_UNIT, chart.getUnit());
         
         initialValues.put(ConstantsAdmin.KEY_CHART_BACKGROUND_COLOR, chart.getBackgroundColor());
         initialValues.put(ConstantsAdmin.KEY_CHART_FORMAT_TIME, chart.getFormatTime());
         initialValues.put(ConstantsAdmin.KEY_CHART_GRID_COLOR, chart.getGridColor());
         initialValues.put(ConstantsAdmin.KEY_CHART_LABEL_COLOR, chart.getLabelColor());
         initialValues.put(ConstantsAdmin.KEY_CHART_LINE_COLOR, chart.getLineColor());
         initialValues.put(ConstantsAdmin.KEY_CHART_POINT_STYLE, chart.getPointStyle());
         if(!chart.isShowGrid()){
        	 showGrid = 0;
         }
         if(!chart.isShowValue()){
        	 showValue = 0;
         }
         initialValues.put(ConstantsAdmin.KEY_CHART_SHOW_GRID, showGrid);
         initialValues.put(ConstantsAdmin.KEY_CHART_SHOW_VALUE, showValue);         
         
         try {
        	 if(chart.getId() == -1 ){
        		 returnValue= mDb.insert(ConstantsAdmin.TABLE_CHART, null, initialValues);
        	 }else{
        		 mDb.update(ConstantsAdmin.TABLE_CHART, initialValues, ConstantsAdmin.KEY_ROWID + "=" + chart.getId() , null);
        		 returnValue = chart.getId();
        	 }
         } catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
         }
         return returnValue;
         
     }
     
     public void createOrUpdateConfigChart(KNConfigChart config) {
    //	 long returnValue = -1;
    	 int showGrid = 1;
    	 int showValue = 1;
         ContentValues initialValues = new ContentValues();
         
         initialValues.put(ConstantsAdmin.KEY_CONFIG_BACKGROUND, config.getBackground());
         initialValues.put(ConstantsAdmin.KEY_CONFIG_TIME, config.getTime());
         initialValues.put(ConstantsAdmin.KEY_CONFIG_GRID, config.getGrid());
         initialValues.put(ConstantsAdmin.KEY_CONFIG_LABEL, config.getLabel());
         initialValues.put(ConstantsAdmin.KEY_CONFIG_LINE, config.getLine());
         initialValues.put(ConstantsAdmin.KEY_CONFIG_POINT, config.getPoint());
         if(!config.isShowGrid()){
        	 showGrid = 0;
         }
         if(!config.isShowValue()){
        	 showValue = 0;
         }
         initialValues.put(ConstantsAdmin.KEY_CONFIG_SHOWGRID, showGrid);
         initialValues.put(ConstantsAdmin.KEY_CONFIG_SHOWVALUES, showValue);    
         
         try {
        	 if(config.getId() == -1 ){
        		 mDb.insert(ConstantsAdmin.TABLE_CONFIG, null, initialValues);
        	 }else{
        		 mDb.update(ConstantsAdmin.TABLE_CONFIG, initialValues, ConstantsAdmin.KEY_ROWID + "=" + config.getId() , null);

        	 }
         } catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
         }

     }
     
     public void removeChart(long idChart){
         mDb.delete(ConstantsAdmin.TABLE_CHART, ConstantsAdmin.KEY_ROWID + "=" + String.valueOf(idChart), null);
     }
     
     public void removeItemChart(long idItem){
         mDb.delete(ConstantsAdmin.TABLE_ITEM_CHART, ConstantsAdmin.KEY_ROWID + "=" + String.valueOf(idItem), null);
     }
     
     public void removeItemsCharts(String idChart, String year){
         mDb.delete(ConstantsAdmin.TABLE_ITEM_CHART, ConstantsAdmin.KEY_ITEMCHART_CHARTID + " = " + idChart + " AND " + ConstantsAdmin.KEY_ITEMCHART_YEAR + " = " + year, null);
     }
     
     public void removeItemsCharts(String idChart, String year, String month){
    	// int val = -1;
    	 mDb.delete(ConstantsAdmin.TABLE_ITEM_CHART, ConstantsAdmin.KEY_ITEMCHART_CHARTID + " = '" + idChart + "' AND " + ConstantsAdmin.KEY_ITEMCHART_YEAR + " = '" +  year + "' AND " + ConstantsAdmin.KEY_ITEMCHART_MONTH + " = '" +  month + "'", null);
     }
     
// --Commented out by Inspection START (14/11/18 19:15):
//     public long tablaChartSize(){
////    	 Cursor cur = null;
//    	 long result = 0;
//    	 SQLiteStatement s = mDb.compileStatement(DataBaseHelper.SIZE_CHART);
//    	 result = s.simpleQueryForLong();
//    	 return result;
//     }
// --Commented out by Inspection STOP (14/11/18 19:15)

     public long tablaConfigSize(){
//    	 Cursor cur = null;
    	 long result;
    	 SQLiteStatement s = mDb.compileStatement(DataBaseHelper.SIZE_CONFIG);
    	 result = s.simpleQueryForLong();
    	 return result;
     }
     
     
     public long tablaItemChartSize(){
//       VER COMO AGREGAR EL WHERE
    	 long result = 0;
    	 SQLiteStatement s = mDb.compileStatement(DataBaseHelper.SIZE_ITEM_CHART);
    	 result = s.simpleQueryForLong(); 
    	 return result;
     }
     
     public void createOrUpdateItemChart(KNItemChart item){
    	// long returnValue = -1;
    	 ContentValues initialValues = new ContentValues();
         initialValues.put(ConstantsAdmin.KEY_ITEMCHART_DAY, item.getDay());
         initialValues.put(ConstantsAdmin.KEY_ITEMCHART_MONTH, item.getMonth());
         initialValues.put(ConstantsAdmin.KEY_ITEMCHART_YEAR, item.getYear());
         initialValues.put(ConstantsAdmin.KEY_ITEMCHART_VALUE, item.getValue());
         initialValues.put(ConstantsAdmin.KEY_ITEMCHART_CHARTID, item.getChartId());
         initialValues.put(ConstantsAdmin.KEY_ITEMCHART_HOUR, item.getHour());
         initialValues.put(ConstantsAdmin.KEY_ITEMCHART_MIN, item.getMin());

         try {
        	 if(item.getId() == -1 ){
        		 mDb.insert(ConstantsAdmin.TABLE_ITEM_CHART, null, initialValues);
        	 }else{
        		 mDb.update(ConstantsAdmin.TABLE_ITEM_CHART, initialValues, ConstantsAdmin.KEY_ROWID + "=" + item.getId() , null);

        	 }
         } catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
         }

     }
     
     public void createBD(){
    	 mDbHelper.onCreate(mDb);
     }
     public void upgradeDB(){
    	 mDbHelper.onUpgrade(mDb, 1, 2);
     }
     
     public Cursor fetchItemsForChart(KNChart chart) {
    	 // ver aca de poder ordenar por varis campos (year, month, day)
    	 String sortOrder = ConstantsAdmin.KEY_ITEMCHART_YEAR + ", " + ConstantsAdmin.KEY_ITEMCHART_MONTH + ", " + ConstantsAdmin.KEY_ITEMCHART_DAY + ", " + ConstantsAdmin.KEY_ITEMCHART_HOUR + ", " + ConstantsAdmin.KEY_ITEMCHART_MIN  + " COLLATE LOCALIZED ASC";
    	 Cursor result = null;
    	 try{
    		 result = mDb.query(ConstantsAdmin.TABLE_ITEM_CHART, null, ConstantsAdmin.KEY_ITEMCHART_CHARTID + "= " + chart.getId(), null, null, null, sortOrder);
    	 }catch (SQLiteException e) {
			e.getMessage();
		}
    	 return result;
    	 
     }
     
     public Cursor fetchItemsForChart(String idChartSelected, String yearSelected, String monthSelected){
       	 String sortOrder = ConstantsAdmin.KEY_ITEMCHART_YEAR + ", " + ConstantsAdmin.KEY_ITEMCHART_MONTH + ", " + ConstantsAdmin.KEY_ITEMCHART_DAY + ", " + ConstantsAdmin.KEY_ITEMCHART_HOUR + ", " + ConstantsAdmin.KEY_ITEMCHART_MIN  + " COLLATE LOCALIZED ASC";
    	 Cursor result = null;
    	 try{
    		 result = mDb.query(ConstantsAdmin.TABLE_ITEM_CHART, null, ConstantsAdmin.KEY_ITEMCHART_CHARTID + "= '" + idChartSelected + "' AND " + ConstantsAdmin.KEY_ITEMCHART_YEAR + " = '" + yearSelected + "' AND " + ConstantsAdmin.KEY_ITEMCHART_MONTH + " = '" + monthSelected + "'" , null, null, null, sortOrder);
    	 }catch (SQLiteException e) {
			e.getMessage();
		 }
    	 return result;   	  
     }
     
     public Cursor fetchItemsForChartAndDateTime(String idChart, String year, String month, String day, String hour, String min){
    	 Cursor result = null;
    	 try{
    		 result = mDb.query(ConstantsAdmin.TABLE_ITEM_CHART, null, ConstantsAdmin.KEY_ITEMCHART_CHARTID + "= '" + idChart + "' AND " + ConstantsAdmin.KEY_ITEMCHART_YEAR + " = '" + year + "' AND " + ConstantsAdmin.KEY_ITEMCHART_MONTH + " = '" + month + "' AND " + ConstantsAdmin.KEY_ITEMCHART_DAY + " = '" + day + "' AND " + ConstantsAdmin.KEY_ITEMCHART_HOUR + " = '" + hour + "' AND " + ConstantsAdmin.KEY_ITEMCHART_MIN + " = '" + min + "'", null, null, null, null);
    	 }catch (SQLiteException e) {
			e.getMessage();
		 }
    	 return result;   	  
     }

     
     public Cursor fetchItemsForChartOrderBYearAndMonth(KNChart chart) {
    	 // ver aca de poder ordenar por varis campos (year, month, day)
    	 String sortOrder = ConstantsAdmin.KEY_ITEMCHART_YEAR + ", " + ConstantsAdmin.KEY_ITEMCHART_MONTH + ", " + ConstantsAdmin.KEY_ITEMCHART_DAY + ", " + ConstantsAdmin.KEY_ITEMCHART_HOUR + ", " + ConstantsAdmin.KEY_ITEMCHART_MIN  + " COLLATE LOCALIZED ASC";
    	 String groupBy = ConstantsAdmin.KEY_ITEMCHART_YEAR + ", " + ConstantsAdmin.KEY_ITEMCHART_MONTH;
    	 Cursor result = null;
    	 try{
    		 result = mDb.query(ConstantsAdmin.TABLE_ITEM_CHART, null, ConstantsAdmin.KEY_ITEMCHART_CHARTID + "= " + chart.getId(), null, groupBy, null, sortOrder);
    	 }catch (SQLiteException e) {
			e.getMessage();
		}
    	 return result;
    	 
     }

    
     public Cursor fetchChartsForName(String paramNombre) {
    	 Cursor result = null;
    	 if(paramNombre != null && !paramNombre.equals("")){
    		 result = mDb.query(ConstantsAdmin.TABLE_CHART, null, ConstantsAdmin.KEY_CHART_NAME + " LIKE '%" + paramNombre + "%'", null, null, null, null);
    	 }else{
    		 result = mDb.query(ConstantsAdmin.TABLE_CHART,  null, null, null, null, null, null);
    	 }
         return result;
     }
     

     
     public Cursor fetchChartsForId(long id) {
    	 Cursor result = null;
		 try{
			 result = mDb.query(ConstantsAdmin.TABLE_CHART, null, ConstantsAdmin.KEY_ROWID  + "= '" + id + "'", null, null, null, null);
			 if (result != null) {
				 result.moveToFirst();
			 }
		 }catch (Exception e) {
			// TODO: handle exception
			 e.getMessage();
		 }
    	
    	 
         return result;
     }



     public Cursor fetchItemForId(long id) {
    	 Cursor result = null;
		 try{
			 result = mDb.query(ConstantsAdmin.TABLE_ITEM_CHART, null, ConstantsAdmin.KEY_ROWID  + "= '" + id + "'", null, null, null, null);
			 if (result != null) {
				 result.moveToFirst();
			 }
		 }catch (Exception e) {
			// TODO: handle exception
			 e.getMessage();
		 }
    	
    	 
         return result;
     }
     
}
