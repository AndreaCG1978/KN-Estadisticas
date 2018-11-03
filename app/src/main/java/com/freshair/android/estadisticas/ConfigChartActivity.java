package com.freshair.android.estadisticas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.freshair.android.estadisticas.dtos.KNChart;
import com.freshair.android.estadisticas.dtos.KNConfigChart;
import com.freshair.android.estadisticas.utils.ColorPickerDialog;
import com.freshair.android.estadisticas.utils.ConstantsAdmin;

public class ConfigChartActivity extends Activity {
   	
	private KNChart mChartSeleccionado = null;
	KNConfigChart mConfig = null;
	
	ConfigChartActivity me = null;
	int mColorSeleccionadoBackground = 0;
	int mColorSeleccionadoLine = 0;
	int mColorSeleccionadoGrid = 0;
	int mColorSeleccionadoLabels = 0;
	int mPointSelected = -1;
	int mTimeSelected = -1;

	Button labelBackground = null;
	Button labelLine = null;
	Button labelGrid = null;
	Button labelLabels = null;
	
	CheckBox checkShowGrid = null;
	CheckBox checkShowValues = null;
	

	ArrayAdapter<String> stylePointAdapter = null;
	ArrayAdapter<String> timeFormatAdapter = null;
	Spinner stylePointSpinner = null;
	Spinner timeFormatSpinner = null;
	
	static final int COLOR_BACKGROUND_DIALOG_ID = 0;
	static final int COLOR_LINE_DIALOG_ID = 1;
	static final int COLOR_GRID_DIALOG_ID = 2;
	static final int COLOR_LABELS_DIALOG_ID = 3;
	private ArrayList<Cursor> allMyCursors = null;
	
    @Override
	public void startManagingCursor(Cursor c) {
		allMyCursors.add(c);
	    super.startManagingCursor(c);
	}	
    
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    	this.resetAllMyCursors();
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
	

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allMyCursors = new ArrayList<Cursor>();
        me = this;
       	this.setContentView(R.layout.chart_config);
        this.registrarWidgets();
        this.guardarChartSeleccionado(this.getIntent());
       
        this.configurarMostrarBackground();
        this.configurarMostrarLine();
        this.configurarMostrarGrid();
        this.configurarMostrarLabels();
        this.configurarBotonGuardar();
        this.configurarSpinnerStylePoint();
        this.configurarSpinnerTimeFormat();
        this.configurarChecksBox();
        this.configurarTitulo();
       
    }
    
    private void configurarTitulo(){
    	TextView text = (TextView) this.findViewById(R.id.titleChartName);
		text.setText(this.getString(R.string.label_configuracion).toUpperCase());	
    	if(!(mChartSeleccionado.getName() == null || mChartSeleccionado.getName().equals(""))){
    		text.setText(text.getText() + " (" + mChartSeleccionado.getName().toUpperCase() + ")");
    	}else{
    		text.setText(text.getText() + " (" + this.getString(R.string.label_comparacion)+ ")");
    	}
    	
    }
    
    private void configurarChecksBox(){
       checkShowGrid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mChartSeleccionado.setShowGrid(isChecked);
				if(isChecked){
					labelGrid.setEnabled(true);
				}else{
					labelGrid.setEnabled(false);
				}
			}
		});
       checkShowValues.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mChartSeleccionado.setShowValue(isChecked);
			}
		});       
    }
    
    protected Dialog onCreateDialog(int id) {
    	
        switch (id) {
        case COLOR_BACKGROUND_DIALOG_ID:
            return new ColorPickerDialog(this,
            		mColorListenerBackground,
            		mColorSeleccionadoBackground);
        case COLOR_GRID_DIALOG_ID:
            return new ColorPickerDialog(this,
            		mColorListenerGrid,
            		mColorSeleccionadoGrid);
        case COLOR_LINE_DIALOG_ID:
            return new ColorPickerDialog(this,
            		mColorListenerLine,
            		mColorSeleccionadoLine);
        case COLOR_LABELS_DIALOG_ID:
            return new ColorPickerDialog(this,
            		mColorListenerLabels,
            		mColorSeleccionadoLabels);


        	}
        return null;
    }
    
    private void configurarSpinnerStylePoint(){
    	List<String> stylePoints = new ArrayList<String>();
    	stylePoints.add(this.getString(R.string.label_circulo));
        stylePoints.add(this.getString(R.string.label_diamante));
        stylePoints.add(this.getString(R.string.label_punto));
        stylePoints.add(this.getString(R.string.label_cuadrado));
        stylePoints.add(this.getString(R.string.label_triangulo));
        stylePoints.add(this.getString(R.string.label_x));
        
        
    	stylePointAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, stylePoints);
    	stylePointSpinner.setAdapter(stylePointAdapter);
    	
    	OnItemSelectedListener spinnerListener = new seleccionSpinnerStylePointListener();
    	stylePointSpinner.setOnItemSelectedListener(spinnerListener);
    	if(mChartSeleccionado.getPointStyle() != null){
    		stylePointSpinner.setSelection(new Integer(mChartSeleccionado.getPointStyle()));
    	}
    }
    
    private void configurarSpinnerTimeFormat(){
    	List<String> timeFormats = new ArrayList<String>();
    	String tf = null;
    	int j = -1;
    	for (int i = 0; i < ConstantsAdmin.formatTime.length; i++) {
    		tf = ConstantsAdmin.formatTime[i];
    		if(mChartSeleccionado.getFormatTime()!= null && mChartSeleccionado.getFormatTime().toUpperCase().equals(tf.toUpperCase())){
    			j = i;
    		}
		}
    	timeFormats.add(getString(R.string.label_fecha_hora).toUpperCase());
    	timeFormats.add(getString(R.string.label_fecha).toUpperCase());
		timeFormats.add(getString(R.string.label_hora).toUpperCase());
    	timeFormatAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, timeFormats);
    	timeFormatSpinner.setAdapter(timeFormatAdapter);
  	
    	OnItemSelectedListener spinnerListener = new seleccionSpinnerTimeFormatListener();
    	timeFormatSpinner.setOnItemSelectedListener(spinnerListener);
    	if(j != -1){
    		timeFormatSpinner.setSelection(j);
    	}
    }
    
    public class seleccionSpinnerStylePointListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {
        	mPointSelected = pos;
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }    

    public class seleccionSpinnerTimeFormatListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {
        	mTimeSelected = pos;
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }   
    
	private ColorPickerDialog.OnColorChangedListener mColorListenerBackground =
        new ColorPickerDialog.OnColorChangedListener() {

			@Override
			public void colorChanged(int color) {
				mColorSeleccionadoBackground = color;
				labelBackground.setBackgroundColor(mColorSeleccionadoBackground);
			}
    };
    
	private ColorPickerDialog.OnColorChangedListener mColorListenerLine =
        new ColorPickerDialog.OnColorChangedListener() {

			@Override
			public void colorChanged(int color) {
				mColorSeleccionadoLine = color;
				labelLine.setBackgroundColor(mColorSeleccionadoLine);
				
			}
    };
    
	private ColorPickerDialog.OnColorChangedListener mColorListenerGrid =
        new ColorPickerDialog.OnColorChangedListener() {

			@Override
			public void colorChanged(int color) {
				mColorSeleccionadoGrid = color;
				labelGrid.setBackgroundColor(mColorSeleccionadoGrid);
				
			}
    };
    
    
	private ColorPickerDialog.OnColorChangedListener mColorListenerLabels =
        new ColorPickerDialog.OnColorChangedListener() {

			@Override
			public void colorChanged(int color) {
				mColorSeleccionadoLabels = color;
				labelLabels.setBackgroundColor(mColorSeleccionadoLabels);
				
			}
    };
    
    
    private void configurarBotonGuardar(){
    	Button b = (Button) this.findViewById(R.id.buttonGuardarChart);
	    b.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	guardarChart();
	        	finish();
	        }
	    });
    }
    
    private void guardarChart(){
    	try {
    		if(mChartSeleccionado.getId() != -1){
        		mChartSeleccionado.setBackgroundColor(String.valueOf(mColorSeleccionadoBackground));
    	    	mChartSeleccionado.setLineColor(String.valueOf(mColorSeleccionadoLine));
    	    	mChartSeleccionado.setGridColor(String.valueOf(mColorSeleccionadoGrid));
    	    	mChartSeleccionado.setLabelColor(String.valueOf(mColorSeleccionadoLabels));
    	    	mChartSeleccionado.setFormatTime(ConstantsAdmin.formatTime[mTimeSelected]);
    	    	mChartSeleccionado.setPointStyle(String.valueOf(mPointSelected));
    	    	ConstantsAdmin.agregarChart(mChartSeleccionado, this);
        	}else{
        		mConfig.setBackground(String.valueOf(mColorSeleccionadoBackground));
        		mConfig.setGrid(String.valueOf(mColorSeleccionadoGrid));
        		mConfig.setLabel(String.valueOf(mColorSeleccionadoLabels));
        		mConfig.setPoint(String.valueOf(mPointSelected));
        		mConfig.setTime(ConstantsAdmin.formatTime[mTimeSelected]);
        		mConfig.setShowGrid(checkShowGrid.isChecked());
        		mConfig.setShowValue(checkShowValues.isChecked());
        		mConfig.setTime(ConstantsAdmin.formatTime[mTimeSelected]);
        		ConstantsAdmin.agregarConfigChart(mConfig, this);
        	}
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensajeAplicacion(this, this.getString(R.string.mensaje_error_guardar));
		}
    	
    }
    
    
    
	private void registrarWidgets(){
		labelBackground = (Button) this.findViewById(R.id.labelBackground);
		labelLine = (Button) this.findViewById(R.id.labelLine);
		labelGrid = (Button) this.findViewById(R.id.labelGrid);
		labelLabels = (Button) this.findViewById(R.id.labelLabels);
		stylePointSpinner = (Spinner) this.findViewById(R.id.spinnerStylePoint);
		timeFormatSpinner = (Spinner) this.findViewById(R.id.spinnerTimeFormat);
		checkShowGrid = (CheckBox) this.findViewById(R.id.checkShowGrid);
		checkShowValues = (CheckBox) this.findViewById(R.id.checkShowValues);
		
	}
	
	private void configurarMostrarBackground(){
		labelBackground.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	              showDialog(COLOR_BACKGROUND_DIALOG_ID);
	        	 
	         }
	     });
	}
	
	private void configurarMostrarLine(){
		if(mChartSeleccionado.getId() != -1){
			labelLine.setOnClickListener(new View.OnClickListener() {
		         public void onClick(View v) {
		             showDialog(COLOR_LINE_DIALOG_ID);
		         }
		     });
		}else{
			labelLine.setVisibility(View.GONE);
			this.findViewById(R.id.labelColorLine).setVisibility(View.GONE);
		}
		
		
	}
	
	private void configurarMostrarGrid(){
		labelGrid.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	             showDialog(COLOR_GRID_DIALOG_ID);
	         }
	     });
		
	}
	
	private void configurarMostrarLabels(){
		labelLabels.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	             showDialog(COLOR_LABELS_DIALOG_ID);
	         }
	     });
		
	}
    
	private void guardarChartSeleccionado(Intent intent){
		String idChartString = null;
		if(intent.hasExtra(ConstantsAdmin.CHART_SELECCIONADO)){
			idChartString = (String)intent.getExtras().get(ConstantsAdmin.CHART_SELECCIONADO);
			long idChart = new Long(idChartString);
			this.recargarChart(idChart);
		}else{
			this.recuperarConfig();
			
		}
		this.recuperarColoresDeChart();

 	}
	
	private void recuperarConfig(){
		mChartSeleccionado = new KNChart();
		mConfig = ConstantsAdmin.obtenerConfigChart(this);
		mChartSeleccionado.setBackgroundColor(mConfig.getBackground());
		mChartSeleccionado.setFormatTime(mConfig.getTime());
		mChartSeleccionado.setGridColor(mConfig.getGrid());
		mChartSeleccionado.setLabelColor(mConfig.getLabel());
		mChartSeleccionado.setPointStyle(mConfig.getPoint());
		mChartSeleccionado.setShowGrid(mConfig.isShowGrid());
		mChartSeleccionado.setShowValue(mConfig.isShowValue());
	}
	
	private void recuperarColoresDeChart(){
		if(mChartSeleccionado.getBackgroundColor()!= null){
			mColorSeleccionadoBackground = Integer.valueOf(mChartSeleccionado.getBackgroundColor());
		}else{
			mColorSeleccionadoBackground = Color.BLACK;
		}
		labelBackground.setBackgroundColor(mColorSeleccionadoBackground);
		if(mChartSeleccionado.getLineColor()!= null){
			mColorSeleccionadoLine = Integer.valueOf(mChartSeleccionado.getLineColor());
		}else{
			mColorSeleccionadoLine = Color.WHITE;
		}
		labelLine.setBackgroundColor(mColorSeleccionadoLine);
		if(mChartSeleccionado.getGridColor()!= null){
			mColorSeleccionadoGrid = Integer.valueOf(mChartSeleccionado.getGridColor());
		}else{
			mColorSeleccionadoGrid = Color.GRAY;
		}
		labelGrid.setBackgroundColor(mColorSeleccionadoGrid);
		if(mChartSeleccionado.getLabelColor()!= null){
			mColorSeleccionadoLabels = Integer.valueOf(mChartSeleccionado.getLabelColor());
		}else{
			mColorSeleccionadoLabels = Color.WHITE;
		}
		labelLabels.setBackgroundColor(mColorSeleccionadoLabels);
		if(mChartSeleccionado.isShowGrid()){
			checkShowGrid.setChecked(true);
			labelGrid.setEnabled(true);
		}else{
			checkShowGrid.setChecked(false);
			labelGrid.setEnabled(false);
		}
		if(mChartSeleccionado.isShowValue()){
			checkShowValues.setChecked(true);
		}else{
			checkShowValues.setChecked(false);
		}
			
	}
	

 	
	private void recargarChart(long idChart){
		mChartSeleccionado = ConstantsAdmin.obtenerChartId(this, idChart);
	}

}