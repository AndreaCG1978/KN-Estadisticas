package com.freshair.android.knestadisticas;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.freshair.android.knestadisticas.database.DataBaseManager;
import com.freshair.android.knestadisticas.dtos.KNChart;
import com.freshair.android.knestadisticas.dtos.KNItemChart;
import com.freshair.android.knestadisticas.utils.ConstantsAdmin;


public class SimpleLinearChartActivity extends Activity {
 
	
  private KNChart mChart = null;
  private GraphicalView mChartView = null;
  private String idChartSelect = null;	
//  private ArrayList<Cursor> allMyCursors = null;
  private List<KNItemChart> items = null;
  private int selectedFormatImport = -1;
  private SimpleLinearChartActivity me = null;
/*
  @Override
	public void startManagingCursor(Cursor c) {
		allMyCursors.add(c);
	    super.startManagingCursor(c);
	}	
  */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      me = this;
   //   allMyCursors = new ArrayList<>();
      this.setContentView(R.layout.chart_view);
      this.guardarChartSeleccionado();
      this.configurarBotones();
  }
  
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
  @Override
  protected void onResume() {
      super.onResume();
      mChart = null;
      mChartView = null;
      this.drawChart();
  }
  
  private void configurarBotones(){
	    ImageView btn = this.findViewById(R.id.btnExport);
	    btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				exportChartPicture();
			}
		});
	    btn = this.findViewById(R.id.btnTxt);
	    btn.setVisibility(View.VISIBLE);
	    btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				exportChartTxt();
			}
		});	    
	    
	    btn = this.findViewById(R.id.btnCsv);
	    btn.setVisibility(View.VISIBLE);
	    btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				exportChartCsv();
			}
		});	    
	    
	    btn = this.findViewById(R.id.btnConfig);
	    btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	openConfigChart();
            }
        });  
	    
  }
  
  private void guardarChartSeleccionado(){
	    idChartSelect = (String)this.getIntent().getExtras().get(ConstantsAdmin.CHART_SELECCIONADO);
  }
  
  private void recuperarChart(){
	  int idChart = Integer.valueOf(idChartSelect);
	  DataBaseManager mDBManager = DataBaseManager.getInstance(this);
	  mChart = ConstantsAdmin.obtenerChartId(this, idChart, mDBManager);
  }
  

  private void drawChart() {
	this.recuperarChart();
	DataBaseManager mDBManager = DataBaseManager.getInstance(this);
    String[] titles = new String[] {mChart.getName()};
    List<Date[]> dates = new ArrayList<>();
    List<double[]> values = new ArrayList<>();
    items = ConstantsAdmin.obtenerItemsDeChart(mChart, this, mDBManager);
    if(items.size()>0){
	    if(items != null && items.size() > 0){
		    Iterator<KNItemChart> it = items.iterator();
		    KNItemChart item;
		    Date[] dateValues = new Date[items.size()];
		    double[] doubleValues = new double[items.size()];
		    int i = 0;
		    double max = Double.MIN_VALUE;
		    double min = Double.MAX_VALUE;
		    double minTime;
		    double maxTime;
		    double val;
		    while(it.hasNext()){
		    	item = it.next();
		    	dateValues[i] = new Date(Integer.valueOf(item.getYear())- 1900, Integer.valueOf(item.getMonth()) - 1, Integer.valueOf(item.getDay()), Integer.valueOf(item.getHour()), Integer.valueOf(item.getMin()));
		    	val = Double.valueOf(item.getValue());
		    	doubleValues[i] = val;
		    	if(max < val){
		    		max = val;
		    	}
		    	if(min > val){
		    		min = val;
		    	}
		    	i++;
		    }
		    dates.add(dateValues);
		    values.add(doubleValues);
		    int[] colors = new int[1];
		    PointStyle[] styles = new PointStyle[1];
		    if(mChart.getLineColor()== null){
		    	colors[0] = Color.GREEN;
		    }else{
		    	colors[0] = Integer.valueOf(mChart.getLineColor());
		    }
		    if(mChart.getPointStyle()== null){
		    	styles[0] = PointStyle.DIAMOND;
		    }else{
		    	styles[0] = ConstantsAdmin.tiposPuntos[Integer.valueOf(mChart.getPointStyle())];
		    }
		    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		    if(mChart.isShowGrid()){
		    	renderer.setShowGrid(true);
		        if(mChart.getGridColor() == null){
		        	renderer.setGridColor(Color.GRAY);
		        }else{
		        	renderer.setGridColor(Integer.valueOf(mChart.getGridColor()));
		        }
		    }else{
		    	renderer.setShowGrid(false);
		    }
		
		    renderer.setApplyBackgroundColor(true);
		    if(mChart.getBackgroundColor() == null){
		    	renderer.setBackgroundColor(Color.DKGRAY);
		    }else{
		    	renderer.setBackgroundColor(Integer.valueOf(mChart.getBackgroundColor()));
		    }
		    int labelColor = Color.WHITE;
		    if(mChart.getLabelColor() != null){
		    	labelColor = Integer.valueOf(mChart.getLabelColor());
		    }
		    String formatTime = ConstantsAdmin.formatTime[0];
		    if(mChart.getFormatTime() != null){
		    	formatTime = mChart.getFormatTime();
		    }
		    min = min * 0.9;
		    max = max * 1.1;
		    minTime = dateValues[0].getTime();
		    maxTime = dateValues[dateValues.length - 1].getTime();
		    setChartSettings(renderer, mChart.getName(), "", mChart.getUnit(), minTime, maxTime, min, max, labelColor, labelColor);
		   	  
		    this.configurarPropiedadesRender(renderer);

		    if(mChart.isShowValue()){
			    int length = renderer.getSeriesRendererCount();
			    for (int o = 0; o < length; o++) {
			      SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(o);
			      seriesRenderer.setDisplayChartValues(true);
			      seriesRenderer.setChartValuesTextSize(15);
			      seriesRenderer.setChartValuesTextAlign(Align.LEFT);
			    }
		    }
		    try {
		    	mChartView = ChartFactory.getTimeChartView(this, buildDateDataset(titles, dates, values),
				        renderer, formatTime);
	    
	    	   
			    LinearLayout layout = findViewById(R.id.chart);
			    layout.removeAllViews();
			    layout.addView(mChartView, new LayoutParams(LayoutParams.WRAP_CONTENT,
			            LayoutParams.WRAP_CONTENT));
			} catch (Exception e) {
				ConstantsAdmin.mostrarMensajeAplicacion(this,this.getString(R.string.mensaje_error_dibujar));
			}
		    
		    
	    }
    }else{
    	ConstantsAdmin.mostrarMensajeAplicacion(this, this.getString(R.string.mensaje_sin_items));
    	this.finish();
    }
   
  }
  
  
  private void configurarPropiedadesRender(XYMultipleSeriesRenderer renderer){
   	  
	    renderer.setYLabels(20);
	    renderer.setXLabels(20);
	    renderer.setLabelsTextSize(13);
	    renderer.setXLabelsAngle(270);
	    renderer.setXLabelsAlign(Align.RIGHT);
	    renderer.setYLabelsAlign(Align.CENTER);
	    if(mChart.getFormatTime().equalsIgnoreCase(ConstantsAdmin.formatTime[0])){
	    	renderer.setMargins(new int[]{30,35,100,20});
	    }else if(mChart.getFormatTime().equalsIgnoreCase(ConstantsAdmin.formatTime[1])){
	    	renderer.setMargins(new int[]{30,35,60,20});
	    }else{
	    	renderer.setMargins(new int[]{30,35,30,20});//ARRIBA-IZQ-ABAJO-DER
	    }
	    renderer.setZoomEnabled(true);
	    renderer.setZoomButtonsVisible(true);
	    renderer.setZoomRate(1.1f);

  }
	
	@Override
  public boolean onCreateOptionsMenu(Menu menu) {
      MenuItem item;
      super.onCreateOptionsMenu(menu);
      item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_CHART,0, R.string.menu_configurar_chart);
      item.setIcon(R.drawable.config_menubar);
      item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_EXPORT_CHART_PICTURE,0, R.string.menu_export_chart);
      item.setIcon(R.drawable.export_menbar);
      item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_EXPORT_CHART_TEXT,0, R.string.menu_export_text);
      item.setIcon(R.drawable.txt_menbar);
      item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_EXPORT_CHART_CSV,0, R.string.menu_export_csv);
      item.setIcon(R.drawable.csv_menbar);      
      return true;
  }
  
	@Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
      switch(item.getItemId()) {
      case ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_CHART:
      	this.openConfigChart();
      	return true;
      case ConstantsAdmin.ACTIVITY_EJECUTAR_EXPORT_CHART_PICTURE:
        	this.exportChartPicture();
        	return true;
	  case ConstantsAdmin.ACTIVITY_EJECUTAR_EXPORT_CHART_TEXT:
	  		this.exportChartTxt();
	  		return true;
    
      case ConstantsAdmin.ACTIVITY_EJECUTAR_EXPORT_CHART_CSV:
			this.exportChartCsv();
			return true;
      }      
      
      return super.onMenuItemSelected(featureId, item);
  }

  private void openConfigChart(){
	    Intent i = new Intent(this, ConfigChartActivity.class);
	    i.putExtra(ConstantsAdmin.CHART_SELECCIONADO, String.valueOf(mChart.getId()));
	    this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_CHART);
  }
  
  
  private void exportChartPicture(){
	  ConstantsAdmin.exportChart(this, mChartView, mChart);
  }
  
  private void exportChartTxt(){
	  ConstantsAdmin.exportTxT(this, items, mChart);
  }
  
  private void exportChartCsv(){
	//  ConstantsAdmin.exportCsv(this, items, mChart);
	  
		final CharSequence[] items = {this.getString(R.string.format_date_time), this.getString(R.string.format_date),this.getString(R.string.format_all_separated)};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(this.getString(R.string.title_select_format));
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
	    		Long[] params = new Long[1];
	    		params[0] = 1L;
	    		selectedFormatImport = item + 1;
	    		dialog.cancel();
	    		new ExportCSVTask().execute(params);
		    }
		});
		AlertDialog alert =  builder.create();
	    alert.show();
  }
  
  private class ExportCSVTask extends AsyncTask<Long, Integer, Integer>{
  	ProgressDialog dialog = null;
      @Override
      protected Integer doInBackground(Long... params) {

          try {
              publishProgress(1);
              ConstantsAdmin.exportarCSV(me,selectedFormatImport, items, mChart);


          } catch (Exception e) {

              e.printStackTrace();
          }
          return 0;

      }

      protected void onProgressUpdate(Integer... progress) {
      	dialog = ProgressDialog.show(me, "", 
                  me.getString(R.string.mensaje_exportando_csv), false);
      }

      @Override
      protected void onPostExecute(Integer result) {
      	dialog.cancel();
   		ConstantsAdmin.mostrarMensajeDialog(me, ConstantsAdmin.mensaje);
      	ConstantsAdmin.mensaje = null;
      }
  }  
  
  protected XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues, List<double[]> yValues) {
	    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	    addXYSeries(dataset, titles, xValues, yValues, 0);
	    return dataset;
	 }

	  private void addXYSeries(XYMultipleSeriesDataset dataset, String[] titles, List<double[]> xValues,
							   List<double[]> yValues, int scale) {
	    int length = titles.length;
	    for (int i = 0; i < length; i++) {
	      XYSeries series = new XYSeries(titles[i], scale);
	      double[] xV = xValues.get(i);
	      double[] yV = yValues.get(i);
	      int seriesLength = xV.length;
	      for (int k = 0; k < seriesLength; k++) {
	        series.add(xV[k], yV[k]);
	      }
	      dataset.addSeries(series);
	    }
	  }

	  /**
	   * Builds an XY multiple series renderer.
	   * 
	   * @param colors the series rendering colors
	   * @param styles the series point styles
	   * @return the XY multiple series renderers
	   */
	  private XYMultipleSeriesRenderer buildRenderer(int[] colors, PointStyle[] styles) {
	    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	    setRenderer(renderer, colors, styles);
	    return renderer;
	  }

	  private void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors, PointStyle[] styles) {
	    renderer.setAxisTitleTextSize(16);
	    renderer.setChartTitleTextSize(20);
	    renderer.setLabelsTextSize(15);
	    renderer.setLegendTextSize(15);
	    renderer.setPointSize(5f);
	    renderer.setMargins(new int[] { 20, 30, 15, 20 });
	    int length = colors.length;
	    for (int i = 0; i < length; i++) {
	      XYSeriesRenderer r = new XYSeriesRenderer();
	      r.setColor(colors[i]);
	      r.setPointStyle(styles[i]);
	      renderer.addSeriesRenderer(r);
	    }
	  }

	  /**
	   * Sets a few of the series renderer settings.
	   * 
	   * @param renderer the renderer to set the properties to
	   * @param title the chart title
	   * @param xTitle the title for the X axis
	   * @param yTitle the title for the Y axis
	   * @param xMin the minimum value on the X axis
	   * @param xMax the maximum value on the X axis
	   * @param yMin the minimum value on the Y axis
	   * @param yMax the maximum value on the Y axis
	   * @param axesColor the axes color
	   * @param labelsColor the labels color
	   */
	  private void setChartSettings(XYMultipleSeriesRenderer renderer, String title, String xTitle,
									String yTitle, double xMin, double xMax, double yMin, double yMax, int axesColor,
									int labelsColor) {
	    renderer.setChartTitle(title);
	    renderer.setXTitle(xTitle);
	    renderer.setYTitle(yTitle);
	    renderer.setXAxisMin(xMin);
	    renderer.setXAxisMax(xMax);
	    renderer.setYAxisMin(yMin);
	    renderer.setYAxisMax(yMax);
	    renderer.setAxesColor(axesColor);
	    renderer.setLabelsColor(labelsColor);
	  }

	  /**
	   * Builds an XY multiple time dataset using the provided values.
	   * 
	   * @param titles the series titles
	   * @param xValues the values for the X axis
	   * @param yValues the values for the Y axis
	   * @return the XY multiple time dataset
	   */
	  private XYMultipleSeriesDataset buildDateDataset(String[] titles, List<Date[]> xValues,
													   List<double[]> yValues) {
	    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
	    int length = titles.length;
	    for (int i = 0; i < length; i++) {
	      TimeSeries series = new TimeSeries(titles[i]);
	      Date[] xV = xValues.get(i);
	      double[] yV = yValues.get(i);
	      int seriesLength = xV.length;
	      for (int k = 0; k < seriesLength; k++) {
	        series.add(xV[k], yV[k]);
	      }
	      dataset.addSeries(series);
	    }
	    return dataset;
	  }


	  protected CategorySeries buildCategoryDataset(String title, double[] values) {
	    CategorySeries series = new CategorySeries(title);
	    int k = 0;
	    for (double value : values) {
	      series.add("Project " + ++k, value);
	    }

	    return series;
	  }


	  protected MultipleCategorySeries buildMultipleCategoryDataset(String title,
	      List<String[]> titles, List<double[]> values) {
	    MultipleCategorySeries series = new MultipleCategorySeries(title);
	    int k = 0;
	    for (double[] value : values) {
	      series.add(2007 + k + "", titles.get(k), value);
	      k++;
	    }
	    return series;
	  }

	  /**
	   * Builds a category renderer to use the provided colors.
	   * 
	   * @param colors the colors
	   * @return the category renderer
	   */
	  protected DefaultRenderer buildCategoryRenderer(int[] colors) {
	    DefaultRenderer renderer = new DefaultRenderer();
	    renderer.setLabelsTextSize(15);
	    renderer.setLegendTextSize(15);
	    renderer.setMargins(new int[] { 20, 30, 15, 0 });
	    for (int color : colors) {
	      SimpleSeriesRenderer r = new SimpleSeriesRenderer();
	      r.setColor(color);
	      renderer.addSeriesRenderer(r);
	    }
	    return renderer;
	  }

// --Commented out by Inspection START (14/11/18 19:16):
//	  /**
//	   * Builds a bar multiple series dataset using the provided values.
//	   *
//	   * @param titles the series titles
//	   * @param values the values
//	   * @return the XY multiple bar dataset
//	   */
//	  protected XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> values) {
//	    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
//	    int length = titles.length;
//	    for (int i = 0; i < length; i++) {
//	      CategorySeries series = new CategorySeries(titles[i]);
//	      double[] v = values.get(i);
//	      int seriesLength = v.length;
//	      for (int k = 0; k < seriesLength; k++) {
//	        series.add(v[k]);
//	      }
//	      dataset.addSeries(series.toXYSeries());
//	    }
//	    return dataset;
//	  }
// --Commented out by Inspection STOP (14/11/18 19:16)

	  /**
	   * Builds a bar multiple series renderer to use the provided colors.
	   * 
	   * @param colors the series renderers colors
	   * @return the bar multiple series renderer
	   */
	  protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
	    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
	    renderer.setAxisTitleTextSize(16);
	    renderer.setChartTitleTextSize(20);
	    renderer.setLabelsTextSize(15);
	    renderer.setLegendTextSize(15);
	    int length = colors.length;
		  for (int color : colors) {
			  SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			  r.setColor(color);
			  renderer.addSeriesRenderer(r);
		  }
	    return renderer;
	  }
  
}
