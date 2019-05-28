package com.freshair.android.knestadisticas;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.freshair.android.knestadisticas.database.DataBaseManager;
import com.freshair.android.knestadisticas.dtos.KNChart;
import com.freshair.android.knestadisticas.dtos.KNConfigChart;
import com.freshair.android.knestadisticas.utils.ColorPickerDialog;
import com.freshair.android.knestadisticas.utils.ConstantsAdmin;

public class ConfigChartActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
   	
	private KNChart mChartSeleccionado = null;
	private KNConfigChart mConfig = null;

	private int mColorSeleccionadoBackground = 0;
	private int mColorSeleccionadoLine = 0;
	private int mColorSeleccionadoGrid = 0;
	private int mColorSeleccionadoLabels = 0;
	private int mPointSelected = -1;
	private int mTimeSelected = -1;

	private Button labelBackground = null;
	private Button labelLine = null;
	private Button labelGrid = null;
	private Button labelLabels = null;
	
	private CheckBox checkShowGrid = null;
	private CheckBox checkShowValues = null;


	private Spinner stylePointSpinner = null;
	private Spinner timeFormatSpinner = null;
	
	private static final int COLOR_BACKGROUND_DIALOG_ID = 0;
	private static final int COLOR_LINE_DIALOG_ID = 1;
	private static final int COLOR_GRID_DIALOG_ID = 2;
	private static final int COLOR_LABELS_DIALOG_ID = 3;
	//private ArrayList<Cursor> allMyCursors = null;
	private final int CONFIG_CURSOR = 1;

	// --Commented out by Inspection (27/5/2019 08:15):public static CursorLoader cursorGraficos = null;
/*
    @Override
	public void startManagingCursor(Cursor c) {
		allMyCursors.add(c);
	    super.startManagingCursor(c);
	}	
    */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	super.onActivityResult(requestCode, resultCode, intent);
    //	this.resetAllMyCursors();
    }
    
   /* private void resetAllMyCursors(){
    	Cursor cur;
		for (Cursor allMyCursor : allMyCursors) {
			cur = allMyCursor;
			this.stopManagingCursor(cur);
		}
    	allMyCursors = new ArrayList<>();
    }
	*/

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   //     allMyCursors = new ArrayList<>();
	//	ConfigChartActivity me = this;
		this.cargarLoaders();
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
    	TextView text = this.findViewById(R.id.titleChartName);
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
    /*
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
    }*/
    
    private void configurarSpinnerStylePoint(){
    	List<String> stylePoints = new ArrayList<>();
    	stylePoints.add(this.getString(R.string.label_circulo));
        stylePoints.add(this.getString(R.string.label_diamante));
        stylePoints.add(this.getString(R.string.label_punto));
        stylePoints.add(this.getString(R.string.label_cuadrado));
        stylePoints.add(this.getString(R.string.label_triangulo));
        stylePoints.add(this.getString(R.string.label_x));


		ArrayAdapter<String> stylePointAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, stylePoints);
    	stylePointSpinner.setAdapter(stylePointAdapter);
    	
    	OnItemSelectedListener spinnerListener = new seleccionSpinnerStylePointListener();
    	stylePointSpinner.setOnItemSelectedListener(spinnerListener);
    	if(mChartSeleccionado.getPointStyle() != null){
    		stylePointSpinner.setSelection(Integer.valueOf(mChartSeleccionado.getPointStyle()));
    	}
    }
    
    private void configurarSpinnerTimeFormat(){
    	List<String> timeFormats = new ArrayList<>();
    	String tf;
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
		ArrayAdapter<String> timeFormatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, timeFormats);
    	timeFormatSpinner.setAdapter(timeFormatAdapter);
  	
    	OnItemSelectedListener spinnerListener = new seleccionSpinnerTimeFormatListener();
    	timeFormatSpinner.setOnItemSelectedListener(spinnerListener);
    	if(j != -1){
    		timeFormatSpinner.setSelection(j);
    	}
    }

	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		CursorLoader cl = null;
		switch(id) {
			case CONFIG_CURSOR:
				cl = mDBManager.cursorLoaderConfig(this, -1);
				ConstantsAdmin.cursorConfig = cl;
				break; // optional
			default : // Optional
				// Statements
		}

		return cl;

	}

	private void cargarLoaders() {
		this.getSupportLoaderManager().initLoader(CONFIG_CURSOR, null, this);

	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {

	}

	class seleccionSpinnerStylePointListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {
        	mPointSelected = pos;
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }    

    class seleccionSpinnerTimeFormatListener implements OnItemSelectedListener {
        public void onItemSelected(AdapterView<?> parent, View v, int pos, long row) {
        	mTimeSelected = pos;
        }

        public void onNothingSelected(AdapterView<?> parent) {

        }
    }   
    
	private final ColorPickerDialog.OnColorChangedListener mColorListenerBackground =
        new ColorPickerDialog.OnColorChangedListener() {

			@Override
			public void colorChanged(int color) {
				mColorSeleccionadoBackground = color;
				labelBackground.setBackgroundColor(mColorSeleccionadoBackground);
			}
    };
    
	private final ColorPickerDialog.OnColorChangedListener mColorListenerLine =
        new ColorPickerDialog.OnColorChangedListener() {

			@Override
			public void colorChanged(int color) {
				mColorSeleccionadoLine = color;
				labelLine.setBackgroundColor(mColorSeleccionadoLine);
				
			}
    };
    
	private final ColorPickerDialog.OnColorChangedListener mColorListenerGrid =
        new ColorPickerDialog.OnColorChangedListener() {

			@Override
			public void colorChanged(int color) {
				mColorSeleccionadoGrid = color;
				labelGrid.setBackgroundColor(mColorSeleccionadoGrid);
				
			}
    };
    
    
	private final ColorPickerDialog.OnColorChangedListener mColorListenerLabels =
        new ColorPickerDialog.OnColorChangedListener() {

			@Override
			public void colorChanged(int color) {
				mColorSeleccionadoLabels = color;
				labelLabels.setBackgroundColor(mColorSeleccionadoLabels);
				
			}
    };
    
    
    private void configurarBotonGuardar(){
    	Button b = this.findViewById(R.id.buttonGuardarChart);
	    b.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	guardarChart();
	        	finish();
	        }
	    });
    }
    
    private void guardarChart(){
    	try {
			DataBaseManager mDBManager = DataBaseManager.getInstance(this);
    		if(mChartSeleccionado.getId() != -1){
        		mChartSeleccionado.setBackgroundColor(String.valueOf(mColorSeleccionadoBackground));
    	    	mChartSeleccionado.setLineColor(String.valueOf(mColorSeleccionadoLine));
    	    	mChartSeleccionado.setGridColor(String.valueOf(mColorSeleccionadoGrid));
    	    	mChartSeleccionado.setLabelColor(String.valueOf(mColorSeleccionadoLabels));
    	    	mChartSeleccionado.setFormatTime(ConstantsAdmin.formatTime[mTimeSelected]);
    	    	mChartSeleccionado.setPointStyle(String.valueOf(mPointSelected));
    	    	ConstantsAdmin.agregarChart(mChartSeleccionado, mDBManager);
        	}else{
        		mConfig.setBackground(String.valueOf(mColorSeleccionadoBackground));
        		mConfig.setGrid(String.valueOf(mColorSeleccionadoGrid));
        		mConfig.setLabel(String.valueOf(mColorSeleccionadoLabels));
        		mConfig.setPoint(String.valueOf(mPointSelected));
        		mConfig.setTime(ConstantsAdmin.formatTime[mTimeSelected]);
        		mConfig.setShowGrid(checkShowGrid.isChecked());
        		mConfig.setShowValue(checkShowValues.isChecked());
        		mConfig.setTime(ConstantsAdmin.formatTime[mTimeSelected]);
        		ConstantsAdmin.agregarConfigChart(mConfig, mDBManager);
        	}
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensajeAplicacion(this, this.getString(R.string.mensaje_error_guardar));
		}
    	
    }
    
    
    
	private void registrarWidgets(){
		labelBackground = this.findViewById(R.id.labelBackground);
		labelLine = this.findViewById(R.id.labelLine);
		labelGrid = this.findViewById(R.id.labelGrid);
		labelLabels = this.findViewById(R.id.labelLabels);
		stylePointSpinner = this.findViewById(R.id.spinnerStylePoint);
		timeFormatSpinner = this.findViewById(R.id.spinnerTimeFormat);
		checkShowGrid = this.findViewById(R.id.checkShowGrid);
		checkShowValues = this.findViewById(R.id.checkShowValues);
		
	}
	
	private void configurarMostrarBackground(){
	/*	labelBackground.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	              showDialog(COLOR_BACKGROUND_DIALOG_ID);
	        	 
	         }
	     });
*/

		labelBackground.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				ColorPickerDialog cpd = new ColorPickerDialog(ConfigChartActivity.this,
						mColorListenerLabels,
						mColorSeleccionadoLabels);
				cpd.show();
			}
		});













	}
	
	private void configurarMostrarLine(){
		if(mChartSeleccionado.getId() != -1){
		/*	labelLine.setOnClickListener(new View.OnClickListener() {
		         public void onClick(View v) {
		             showDialog(COLOR_LINE_DIALOG_ID);
		         }
		     });*/

			labelLine.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					ColorPickerDialog cpd = new ColorPickerDialog(ConfigChartActivity.this,
							mColorListenerLine,
							mColorSeleccionadoLine);
					cpd.show();
				}
			});






		}else{
			labelLine.setVisibility(View.GONE);
			this.findViewById(R.id.labelColorLine).setVisibility(View.GONE);
		}
		
		
	}
	
	private void configurarMostrarGrid(){
		/*labelGrid.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	             showDialog(COLOR_GRID_DIALOG_ID);
	         }
	     });
*/

		labelGrid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				ColorPickerDialog cpd = new ColorPickerDialog(ConfigChartActivity.this,
						mColorListenerGrid,
						mColorSeleccionadoGrid);
				cpd.show();
			}
		});
	}
	
	private void configurarMostrarLabels(){
	/*	labelLabels.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	             showDialog(COLOR_LABELS_DIALOG_ID);
	         }
	     });*/

		labelLabels.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				ColorPickerDialog cpd = new ColorPickerDialog(ConfigChartActivity.this,
						mColorListenerLabels,
						mColorSeleccionadoLabels);
				cpd.show();
			}
		});
		
	}
    
	private void guardarChartSeleccionado(Intent intent){
		String idChartString;
		if(intent.hasExtra(ConstantsAdmin.CHART_SELECCIONADO)){
			idChartString = (String)intent.getExtras().get(ConstantsAdmin.CHART_SELECCIONADO);
			long idChart = Long.valueOf(idChartString);
			this.recargarChart(idChart);
		}else{
			this.recuperarConfig();
			
		}
		this.recuperarColoresDeChart();

 	}
	
	private void recuperarConfig(){
		mChartSeleccionado = new KNChart();
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		mConfig = ConstantsAdmin.obtenerConfigChart(this, mDBManager);
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
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		mChartSeleccionado = ConstantsAdmin.obtenerChartId(idChart, mDBManager);
	}

}
