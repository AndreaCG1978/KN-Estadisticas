package com.freshair.android.knestadisticas;

import java.util.ArrayList;
import java.util.Date;
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
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.freshair.android.knestadisticas.database.DataBaseManager;
import com.freshair.android.knestadisticas.dtos.KNChart;
import com.freshair.android.knestadisticas.dtos.KNConfigChart;
import com.freshair.android.knestadisticas.dtos.KNItemChart;
import com.freshair.android.knestadisticas.utils.ConstantsAdmin;

/**
 * Sales comparison demo chart.
 */
public class ComparisonChartActivity extends Activity {

	
	private List<Integer> myColors = null;
	private boolean sobrePuestos = false;
	private GraphicalView mChartView = null;
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
  //  	this.resetAllMyCursors();
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
	
  private void configurarColores(){
	    myColors = new ArrayList<Integer>();
		myColors.add(Color.CYAN);
		myColors.add(Color.GREEN);
		myColors.add(Color.MAGENTA);
		myColors.add(Color.RED);
		myColors.add(Color.YELLOW);
		myColors.add(Color.BLUE);
  }
  
  private void configurarTipoGrafico(){
      sobrePuestos = !ConstantsAdmin.tipoComparacionSelected.equals(ConstantsAdmin.COMPARACION_EN_TIEMPO);
  }
  
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      //allMyCursors = new ArrayList<Cursor>();
      this.setContentView(R.layout.chart_view);
      this.configurarBotones();
      this.configurarColores();
      this.configurarTipoGrafico();
  }
  
  @Override
  protected void onResume() {
      super.onResume();
      this.drawChart();
  }
  
  private void configurarBotones(){
	    ImageView btn = this.findViewById(R.id.btnExport);
	    btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				exportChart();
			}
		});
	    btn = this.findViewById(R.id.btnConfig);
	    btn.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v) {
          	openConfigChart();
          }
      });  
	    
  }
  
  private void exportChart(){
	  ConstantsAdmin.exportComparison(this, mChartView);
  }
  
  private void openConfigChart(){
	    Intent i = new Intent(this, ConfigChartActivity.class);
	    this.startActivityForResult(i, ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_CHART);
}
  
  private void drawChart() {
	    KNChart chrt;
	  	DataBaseManager mDBManager = DataBaseManager.getInstance(this);//{ PointStyle.POINT, PointStyle.POINT };
	    KNConfigChart config = ConstantsAdmin.obtenerConfigChart(this, mDBManager);
	    KNItemChart item;
	    List<KNItemChart> items;
	    String[] titles = new String[ConstantsAdmin.chartsParaComparar.size()];
	    List<double[]> x = new ArrayList<double[]>();
	    List<Date[]> dates = new ArrayList<Date[]>();
	    List<double[]> values = new ArrayList<double[]>();
	    int length;
	    double max = Double.MIN_VALUE;
	    double min = Double.MAX_VALUE;
	    double minX = Long.MAX_VALUE;
	    double maxX = Long.MIN_VALUE;
	    double val;
	    double valX;
    	int a = 0;
    	StringBuilder unitString = new StringBuilder();
	    int[] colors = new int[ConstantsAdmin.chartsParaComparar.size()]; //{ Color.BLUE, Color.GREEN };
	    PointStyle[] styles = new PointStyle[ConstantsAdmin.chartsParaComparar.size()];

	  	for (KNChart aChartsParaComparar : ConstantsAdmin.chartsParaComparar) {
		  chrt = aChartsParaComparar;
		  colors[a] = myColors.get(a);
		  titles[a] = chrt.getName();
		  unitString.append("-").append(chrt.getUnit());
		  styles[a] = ConstantsAdmin.tiposPuntos[Integer.valueOf(config.getPoint())];
		  items = ConstantsAdmin.obtenerItemsDeChart(chrt, this, mDBManager);
		  if (!sobrePuestos) {
			  dates.add(new Date[items.size()]);
		  } else {
			  x.add(new double[items.size()]);
		  }
		  values.add(new double[items.size()]);
		  for (int k = 0; k < items.size(); k++) {
			  item = items.get(k);
			  if (!sobrePuestos) {

			  	

				  dates.get(a)[k] = new Date(Integer.valueOf(item.getYear()) - 1900, Integer.valueOf(item.getMonth()) - 1, Integer.valueOf(item.getDay()), Integer.valueOf(item.getHour()), Integer.valueOf(item.getMin()));
				  valX = dates.get(a)[k].getTime();

			  } else {
				  x.get(a)[k] = k + 1;
				  valX = x.get(a)[k];
			  }
			  if (maxX < valX) {
				  maxX = valX;
			  }
			  if (minX > valX) {
				  minX = valX;
			  }
			  val = Double.valueOf(item.getValue());
			  values.get(a)[k] = val;
			  if (max < val) {
				  max = val;
			  }
			  if (min > val) {
				  min = val;
			  }
		  }
		  a++;
	  }

	    XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
    	String labelX= "";
	    setChartSettings(renderer, this.getString(R.string.label_comparacion), labelX, unitString.toString(), minX,
	        maxX, min, max, Integer.valueOf(config.getLabel()),  Integer.valueOf(config.getLabel()));
	    this.configurarPropiedadesRender(renderer, config);
	    renderer.setApplyBackgroundColor(true);
	    renderer.setBackgroundColor(Integer.valueOf(config.getBackground()));
	    if(config.isShowGrid()){
	    	renderer.setShowGrid(true);
		    renderer.setGridColor(Integer.valueOf(config.getGrid()));
	    }else{
	    	renderer.setShowGrid(false);
	    }
	    
	    length = renderer.getSeriesRendererCount();
	    if(config.isShowValue()){
		    for (int i = 0; i < length; i++) {
		      SimpleSeriesRenderer seriesRenderer = renderer.getSeriesRendererAt(i);
		      seriesRenderer.setDisplayChartValues(true);
		      seriesRenderer.setChartValuesTextSize(15);
		      seriesRenderer.setChartValuesTextAlign(Align.LEFT);
		    }
	    }
	   
	    try {
	    	if(sobrePuestos){
	    		mChartView = ChartFactory.getLineChartView(this,buildDataset(titles, x, values),
	    		        renderer);
	    	}else{
	    		 mChartView = ChartFactory.getTimeChartView(this, buildDateDataset(titles, dates, values),
	    			        renderer, config.getTime());
	    	}
	    	LinearLayout layout = findViewById(R.id.chart);
	 	    layout.removeAllViews();
	 	    layout.addView(mChartView, new LayoutParams(LayoutParams.WRAP_CONTENT,
	 	            LayoutParams.WRAP_CONTENT));
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensajeAplicacion(this, this.getString(R.string.mensaje_error_dibujar));
		}

	  }
  
	@Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	      MenuItem item;
	      super.onCreateOptionsMenu(menu);
	      item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_CHART,0, R.string.menu_configurar_chart);
	      item.setIcon(R.drawable.config_menubar);
	      item = menu.add(0, ConstantsAdmin.ACTIVITY_EJECUTAR_EXPORT_CHART_PICTURE,0, R.string.menu_export_chart);
	      item.setIcon(R.drawable.export_menbar);
	      return true;
	  }
	  
		@Override
	  public boolean onMenuItemSelected(int featureId, MenuItem item) {
	      switch(item.getItemId()) {
	      case ConstantsAdmin.ACTIVITY_EJECUTAR_CONFIG_CHART:
	      		this.openConfigChart();
	      		return true;
	      case ConstantsAdmin.ACTIVITY_EJECUTAR_EXPORT_CHART_PICTURE:
	        	this.exportChart();
	        	return true;
	      }
	      
	      return super.onMenuItemSelected(featureId, item);
	  }

  
  private void configurarPropiedadesRender(XYMultipleSeriesRenderer renderer, KNConfigChart config){
   	  
	    renderer.setYLabels(20);
	    renderer.setXLabels(20);
	    renderer.setLabelsTextSize(13);
	    renderer.setXLabelsAngle(270);
	    renderer.setXLabelsAlign(Align.RIGHT);
	    renderer.setYLabelsAlign(Align.CENTER);
	    if(sobrePuestos){
	    	renderer.setMargins(new int[]{30,35,25,20});
	    }else{
		    if(config.getTime().equalsIgnoreCase(ConstantsAdmin.formatTime[0])){
		    	renderer.setMargins(new int[]{30,35,100,20});
		    }else if(config.getTime().equalsIgnoreCase(ConstantsAdmin.formatTime[1])){
		    	renderer.setMargins(new int[]{30,35,60,20});
		    }else{
		    	renderer.setMargins(new int[]{30,35,30,20});
		    }
	    }
	  //  renderer.setMargins(new int[]{30,35,75,20});//ARRIBA-IZQ-ABAJO-DER
	    renderer.setZoomEnabled(true);
	    renderer.setZoomButtonsVisible(true);
	    renderer.setZoomRate(1.1f);

  }
  
	 private XYMultipleSeriesDataset buildDataset(String[] titles, List<double[]> xValues, List<double[]> yValues) {
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

		  /**
		   * Builds a multiple category series using the provided values.
		   * 
		   * @param titles the series titles
		   * @param values the values
		   * @return the category series
		   */
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

// --Commented out by Inspection START (14/11/18 19:14):
//		  /**
//		   * Builds a bar multiple series dataset using the provided values.
//		   *
//		   * @param titles the series titles
//		   * @param values the values
//		   * @return the XY multiple bar dataset
//		   */
//		  protected XYMultipleSeriesDataset buildBarDataset(String[] titles, List<double[]> values) {
//		    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
//		    int length = titles.length;
//		    for (int i = 0; i < length; i++) {
//		      CategorySeries series = new CategorySeries(titles[i]);
//		      double[] v = values.get(i);
//		      int seriesLength = v.length;
//		      for (int k = 0; k < seriesLength; k++) {
//		        series.add(v[k]);
//		      }
//		      dataset.addSeries(series.toXYSeries());
//		    }
//		    return dataset;
//		  }
// --Commented out by Inspection STOP (14/11/18 19:14)

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
