package com.freshair.android.knestadisticas.database;

import com.freshair.android.knestadisticas.utils.ConstantsAdmin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


class DataBaseHelper extends SQLiteOpenHelper{
	
	 
	private static final String DATABASE_CREATE_CHART = "create table if not exists " + ConstantsAdmin.TABLE_CHART + 
        "(" + ConstantsAdmin.KEY_ROWID +" integer primary key autoincrement, "
        + ConstantsAdmin.KEY_CHART_NAME + " text not null, "
        + ConstantsAdmin.KEY_CHART_DESCRIPTION + " text, "
        + ConstantsAdmin.KEY_CHART_POINT_STYLE + " text, "
        + ConstantsAdmin.KEY_CHART_LINE_COLOR + " text, "
        + ConstantsAdmin.KEY_CHART_LABEL_COLOR + " text, "
        + ConstantsAdmin.KEY_CHART_GRID_COLOR + " text, "
        + ConstantsAdmin.KEY_CHART_FORMAT_TIME + " text, "
        + ConstantsAdmin.KEY_CHART_BACKGROUND_COLOR + " text, "
        + ConstantsAdmin.KEY_CHART_SHOW_GRID + " integer not null default 1, "
        + ConstantsAdmin.KEY_CHART_SHOW_VALUE + " integer not null default 1, "
        + ConstantsAdmin.KEY_CHART_UNIT + " text);";
	
  
    private static final String DATABASE_CREATE_ITEM_CHART = "create table if not exists " + ConstantsAdmin.TABLE_ITEM_CHART + 
    "(" + ConstantsAdmin.KEY_ROWID +" integer primary key autoincrement, "
    + ConstantsAdmin.KEY_ITEMCHART_CHARTID + " text not null, "
    + ConstantsAdmin.KEY_ITEMCHART_DAY + " text not null, "
    + ConstantsAdmin.KEY_ITEMCHART_MONTH + " text not null, "
    + ConstantsAdmin.KEY_ITEMCHART_YEAR + " text not null, "
    + ConstantsAdmin.KEY_ITEMCHART_HOUR + " text, "
    + ConstantsAdmin.KEY_ITEMCHART_MIN + " text, "
    + ConstantsAdmin.KEY_ITEMCHART_VALUE + " text not null);";
    
    
    private static final String DATABASE_CREATE_CONFIG = "create table if not exists " + ConstantsAdmin.TABLE_CONFIG + 
    "(" + ConstantsAdmin.KEY_ROWID +" integer primary key autoincrement, "
    + ConstantsAdmin.KEY_CONFIG_BACKGROUND + " text not null, "
    + ConstantsAdmin.KEY_CONFIG_GRID + " text not null, "
    + ConstantsAdmin.KEY_CONFIG_LABEL + " text not null, "
    + ConstantsAdmin.KEY_CONFIG_LINE + " text not null, "
    + ConstantsAdmin.KEY_CONFIG_POINT + " text not null, "
    + ConstantsAdmin.KEY_CONFIG_SHOWGRID + " integer not null default 1, "
    + ConstantsAdmin.KEY_CONFIG_SHOWVALUES + " integer not null default 1, "
    + ConstantsAdmin.KEY_CONFIG_TIME + " text not null);";

    public static final String SIZE_ITEM_CHART = "select count(" + ConstantsAdmin.KEY_ROWID +") from " + ConstantsAdmin.TABLE_ITEM_CHART + "  where " + ConstantsAdmin.KEY_ROWID + " > 0";
    
    public static final String SIZE_CHART = "select count(" + ConstantsAdmin.KEY_ROWID +") from " + ConstantsAdmin.TABLE_CHART + "  where " + ConstantsAdmin.KEY_ROWID + " > 0";
    
    public static final String SIZE_CONFIG = "select count(" + ConstantsAdmin.KEY_ROWID +") from " + ConstantsAdmin.TABLE_CONFIG + "  where " + ConstantsAdmin.KEY_ROWID + " > 0";
    
    
    public DataBaseHelper(Context context) {
         super(context, ConstantsAdmin.DATABASE_NAME, null, ConstantsAdmin.DATABASE_VERSION);
    }

	 @Override
     public void onCreate(SQLiteDatabase db) {
		try {
	         db.execSQL(DATABASE_CREATE_CHART);
	         db.execSQL(DATABASE_CREATE_ITEM_CHART);
	         db.execSQL(DATABASE_CREATE_CONFIG);
		} catch (Exception e) {
			// TODO: handle exception
			e.getMessage();
		}
     }

     @Override
     public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         Log.w(ConstantsAdmin.TAG, "Upgrading database from version " + oldVersion + " to "
                 + newVersion + ", which will destroy all old data");
         db.execSQL("DROP TABLE IF EXISTS " + ConstantsAdmin.TABLE_CHART);
         db.execSQL("DROP TABLE IF EXISTS " + ConstantsAdmin.TABLE_ITEM_CHART);
         db.execSQL("DROP TABLE IF EXISTS " + ConstantsAdmin.TABLE_CONFIG);
         onCreate(db);
     }
	 
	

}
