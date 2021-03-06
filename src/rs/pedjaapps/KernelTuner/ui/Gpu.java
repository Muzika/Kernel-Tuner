/*
* This file is part of the Kernel Tuner.
*
* Copyright Predrag Čokulov <predragcokulov@gmail.com>
*
* Kernel Tuner is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Kernel Tuner is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Kernel Tuner. If not, see <http://www.gnu.org/licenses/>.
*/
package rs.pedjaapps.KernelTuner.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.execution.CommandCapture;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import rs.pedjaapps.KernelTuner.R;

public class Gpu extends SherlockActivity
{

	private int gpu2dcurent;
	private int gpu3dcurent ;
	private int gpu2dmax;
	private int gpu3dmax;
	private String selected2d;

	private String selected3d;


	private static String board;


	private List<String> gpu2d;
	private List<String> gpu3d;
	private List<String> gpu2dHr;
	private List<String> gpu3dHr;
	
	Spinner d2Spinner;
	Spinner d3Spinner;

	private ProgressDialog pd = null;
	private SharedPreferences preferences;
	Context c;

private class changegpu extends AsyncTask<String, Void, Object>
	{
		@Override
		protected Object doInBackground(String... args)
		{
			CommandCapture command = new CommandCapture(0, 
		            "chmod 777 /sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/max_gpuclk",
		            "chmod 777 /sys/devices/platform/kgsl-2d1.1/kgsl/kgsl-2d1/max_gpuclk",
		            "chmod 777 /sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/max_gpuclk",
		            "echo " + selected3d + " > /sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/max_gpuclk",
		            "echo " + selected2d + " > /sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/max_gpuclk",
		            "echo " + selected2d + " > /sys/devices/platform/kgsl-2d1.1/kgsl/kgsl-2d1/max_gpuclk");
			try{
				RootTools.getShell(true).add(command).waitForFinish();
			}
			catch(Exception e){

			}
			return "";
		}

		@Override
		protected void onPostExecute(Object result)
		{
			preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			SharedPreferences.Editor editor = preferences.edit();
	  	    editor.putString("gpu3d", selected3d+"");
	  	    editor.putString("gpu2d", selected2d+"");
	  	    editor.commit();

			Gpu.this.pd.dismiss();
			Gpu.this.finish();

		}

	}

    @Override
	public void onCreate(Bundle savedInstanceState)
	{
		c = this;
    	board = android.os.Build.DEVICE;
    	preferences = PreferenceManager.getDefaultSharedPreferences(c);
		
		String theme = preferences.getString("theme", "light");
		
		if(theme.equals("light")){
			setTheme(R.style.Theme_Sherlock_Light_Dialog_NoTitleBar);
		}
		else if(theme.equals("dark")){
			setTheme(R.style.Theme_Sherlock_Dialog_NoTitleBar);
			
		}
		else if(theme.equals("light_dark_action_bar")){
			setTheme(R.style.Theme_Sherlock_Light_Dialog_NoTitleBar);
			
		}
		else if (theme.equals("miui_dark")) 
		{
			setTheme(R.style.Theme_Sherlock_Dialog_NoTitleBar);
		} 
		else if (theme.equals("sense5")) 
		{
			setTheme(R.style.Theme_Sherlock_Dialog_NoTitleBar);
		}

		super.onCreate(savedInstanceState);
		   
		setContentView(R.layout.gpu);
		Button apply = (Button)findViewById(R.id.apply);
		d2Spinner = (Spinner) findViewById(R.id.spinner2);
		d3Spinner = (Spinner) findViewById(R.id.bg);
		gpu2dmax = readFile("/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/max_gpuclk");
		gpu3dmax = readFile("/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/max_gpuclk");
		gpu2dcurent = readFile("/sys/devices/platform/kgsl-2d0.0/kgsl/kgsl-2d0/gpuclk");
		gpu3dcurent = readFile("/sys/devices/platform/kgsl-3d0.0/kgsl/kgsl-3d0/gpuclk");
		List<String> adreno220 = Arrays.asList(new String[] {"shooter", "shooteru", "pyramid", "tenderloin", "vigor", "rider", "nozomi", "LT26i", "hikari", "doubleshot", "su640","SHV-E160S" ,"SHV-E160L", "SHV-E120L", "holiday"});
		List<String> adreno225 = Arrays.asList(new String[] {"evita", "ville", "jewel", "d2spr", "d2tmo" });
		List<String> adreno320 = Arrays.asList(new String[]{"mako","dlx"});
		if (adreno220.contains(board)/*board.equals("shooter") || board.equals("shooteru") || board.equals("pyramid") || board.equals("tenderloin") || board.equals("vigor") || board.equals("raider") || board.equals("nozomi") || board.equals("LT26i") || board.equals("hikari")*/)
		{
			gpu2dHr = Arrays.asList(new String[]{"160Mhz", "200Mhz", "228Mhz", "266Mhz"});
			gpu3dHr = Arrays.asList(new String[]{"200Mhz", "228Mhz", "266Mhz", "300Mhz", "320Mhz"});
			gpu2d = Arrays.asList(new String[]{"160000000", "200000000", "228571000", "266667000"});
			gpu3d = Arrays.asList(new String[]{"200000000", "228571000", "266667000", "300000000", "320000000"});
				createSpinners();
		}
		else if (adreno225.contains(board))
		{
			gpu2dHr = Arrays.asList(new String[]{"320Mhz", "266Mhz", "228Mhz", "200Mhz", "160Mhz", "96Mhz", "27Mhz"});
			gpu3dHr = Arrays.asList(new String[]{"512Mhz", "400Mhz", "320Mhz", "300Mhz", "266Mhz", "228Mhz", "200Mhz", "177Mhz", "27Mhz"});
			gpu2d = Arrays.asList(new String[]{"320000000", "266667000", "228571000", "200000000", "160000000", "96000000", "27000000"});
			gpu3d = Arrays.asList(new String[]{"512000000", "400000000", "320000000", "300000000", "266667000", "228571000", "200000000", "177778000", "27000000"});
			createSpinners();
		}
		else if (adreno320.contains(board))
		{
			gpu2dHr = Arrays.asList(new String[]{"27Mhz", "48Mhz", "55Mhz", "64Mhz", 
					 "76Mhz", 
					 "96Mhz",
					"128Mhz", 
					"145Mhz", 
					"160Mhz", 
					"177Mhz", 
					"200Mhz",
					"266Mhz",
					"300Mhz"});
			gpu3dHr = Arrays.asList(new String[]{"27Mhz","48Mhz","54Mhz","64Mhz","76Mhz","96Mhz","128Mhz","145Mhz",
					"160Mhz",
					"177Mhz",
					"200Mhz",
					"228Mhz",
					"266Mhz",
					"300Mhz",
					"320Mhz",
					"400Mhz",
					"450Mhz",
					"500Mhz"});
			gpu2d = Arrays.asList(new String[]{"27000000", "48000000", "54857000", "64000000", 
					 "76800000", 
					 "96000000",
					"128000000", 
					"145455000", 
					"160000000", 
					"177778000", 
					"200000000",
					"266667000",
					"300000000"});
			gpu3d = Arrays.asList(new String[]{"27000000",
					"48000000",
					"54857000",
					"64000000",
					"76800000",
					"96000000",
					"128000000",
					"145455000",
					"160000000",
					"177778000",
					"200000000",
					"228571000",
					"266667000",
					"300000000",
					"320000000",
					"400000000",
					"450000000",
					"500000000"});
			createSpinners();
		}
		else{
			d2Spinner.setEnabled(false);
			d3Spinner.setEnabled(false);
			apply.setEnabled(false);
		}
		TextView tv5 = (TextView)findViewById(R.id.textView5);
		TextView tv2 = (TextView)findViewById(R.id.textView7);
		tv5.setText((gpu3dcurent/1000000) + "Mhz");
		tv2.setText((gpu2dcurent/1000000) + "Mhz");
		Button cancel = (Button)findViewById(R.id.cancel);
		apply.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Gpu.this.pd = ProgressDialog.show(c, null, getResources().getString(R.string.applying_settings), true, false);
				new changegpu().execute();
			}
			
		});
		cancel.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
			finish();
			}
			
		});

	}

    private void createSpinners()
	{
		ArrayAdapter<String> d2Adapter = new ArrayAdapter<String>(c,   android.R.layout.simple_spinner_item, gpu2dHr);
		d2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
		d2Spinner.setAdapter(d2Adapter);

		d2Spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
				{
					selected2d = gpu2d.get(pos);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent)
				{
					//do nothing
				}
			});
		
		int p = gpu2d.indexOf(gpu2dmax+"");
     	if(p != -1){
		int d2Position = d2Adapter.getPosition(gpu2dHr.get(p));
		d2Spinner.setSelection(d2Position);
        }
	
		ArrayAdapter<String> d3Adapter = new ArrayAdapter<String>(c,   android.R.layout.simple_spinner_item, gpu3dHr);
		d3Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
		d3Spinner.setAdapter(d3Adapter);

		d3Spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
				{
					selected3d = gpu3d.get(pos);
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent)
				{
					//do nothing
				}
			});
       
		int p1 = gpu3d.indexOf(gpu3dmax+"");
		if(p1 != -1){
		int d3Position = d3Adapter.getPosition(gpu3dHr.get(p1));
		d3Spinner.setSelection(d3Position);
		}
	}

    private Integer readFile(String path)
	{
		try
		{
			return Integer.parseInt(FileUtils.readFileToString(new File(path)).trim());
		}
		catch (Exception e)
		{
			return 0;
				}
	}

}
