package plants.identify;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import plants.identify.R;
import plants.identify.Results;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.VideoEncoder;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class Record extends Activity implements SurfaceHolder.Callback, MediaRecorder.OnInfoListener{
    
	private Camera camera;
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private MediaRecorder mediaRecorder;
	private Button startBtn, sendBtn, removeBtn;
	private ProgressBar progressBar; 
	private ListView lv;
	private final int frameRate = 25;
	private final int cMaxRecordDurationInMs = 30000;
	private final long cMaxFileSizeInBytes = 5000000;
	private String lVideoFileFullPath = "";
	private final String cVideoFilePath = "/sdcard/video";
	private File prRecordedFile;
	private SurfaceHolder prSurfaceHolder;
	private boolean recordInProcess = false;
	private static boolean opened = false;
	private Context mContext = this;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        startBtn = (Button) findViewById(R.id.recordButon);
        sendBtn = (Button) findViewById(R.id.sendButton);
        removeBtn = (Button) findViewById(R.id.removeButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        lv = (ListView)findViewById(R.id.listView2);
        progressBar.setVisibility(View.GONE);
        sendBtn.setVisibility(View.GONE);
    	removeBtn.setVisibility(View.GONE);
        startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	
            	takePicture();
            	
            }

        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				sendBtn.setClickable(false);
				removeBtn.setClickable(false);
				progressBar.setVisibility(View.VISIBLE);
				sendPicture();
				progressBar.setVisibility(View.GONE);
            	sendBtn.setVisibility(View.GONE);
            	removeBtn.setVisibility(View.GONE);
            	camera.setDisplayOrientation(90);
            	camera.startPreview();
            	startBtn.setVisibility(View.VISIBLE);
			}
		});
        removeBtn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				removePicture();
				
            	sendBtn.setVisibility(View.GONE);
            	removeBtn.setVisibility(View.GONE);
            	camera.setDisplayOrientation(90);
            	camera.startPreview();
            	startBtn.setVisibility(View.VISIBLE);
			}
		});
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        prSurfaceHolder = surfaceView.getHolder();
        prSurfaceHolder.addCallback(this);
        prSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //mediaRecorder = new MediaRecorder();
        
        
        
        
        
    }
    
    private boolean sendPicture(){
    	JSONArray jArray = Identify.identify("/sdcard/image.jpg", mContext);
    	if(jArray != null){
    		
    		Intent intent = new Intent(getBaseContext(), Results.class);
    		intent.putExtra("RESULTS", jArray.toString());
    		startActivity(intent);
    		
    	}
    	return true;
    }
    
    private boolean removePicture(){
    	return true;
    }
    
    private boolean takePicture() {
    	Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
			
			public void onShutter() {
				// TODO Auto-generated method stub
				
			}
		}; 
		final Camera.PictureCallback jpeg = new Camera.PictureCallback() {
			
			public void onPictureTaken(byte[] imageData, Camera camera) {
				if (imageData != null) {

	                Intent mIntent = new Intent();

	                StoreByteImage(mContext, imageData, 100, "ImageName");

	                setResult(0, mIntent);
	                sendBtn.setClickable(true);
					removeBtn.setClickable(true);
	            	startBtn.setVisibility(View.GONE);
	            	sendBtn.setVisibility(View.VISIBLE);
	            	removeBtn.setVisibility(View.VISIBLE);

	            }
				
			}
		};
		Camera.AutoFocusCallback autoFocus = new Camera.AutoFocusCallback() {
			
			public void onAutoFocus(boolean success, Camera camera) {
				if(success){
					camera.takePicture(null, null, jpeg);
				}
				
			}
		};
		camera.autoFocus(autoFocus);
    	
    	
    	return true;
    }
    
   
    public static boolean StoreByteImage(Context mContext, byte[] imageData,
            int quality, String expName) {

        File sdImageMainDirectory = new File("/sdcard");
        FileOutputStream fileOutputStream = null;
        String nameFile;
        try {

            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inSampleSize = 5;

            Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0,
                    imageData.length,options);


            fileOutputStream = new FileOutputStream(
                    sdImageMainDirectory.toString() +"/image.jpg");


            BufferedOutputStream bos = new BufferedOutputStream(
                    fileOutputStream);

            myImage.compress(CompressFormat.JPEG, quality, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
        	new AlertDialog.Builder(mContext).setTitle("Error").setMessage("FNE").setNeutralButton("Close", null).show();
        	e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        	new AlertDialog.Builder(mContext).setTitle("Error").setMessage("IOE").setNeutralButton("Close", null).show();
            e.printStackTrace();
        }

        return true;
    }
    
    public void surfaceCreated(SurfaceHolder arg0) {
		camera = Camera.open();
		Camera.Parameters parameters = camera.getParameters();
		parameters.set("orientation", "portrait");
		camera.setDisplayOrientation(0);
		List<Size> size = parameters.getSupportedPreviewSizes();
        parameters.setPreviewSize(size.get(0).width, size.get(0).height);
		camera.setParameters(parameters);
		if (camera == null) {
			
			Toast.makeText(this.getApplicationContext(), "Camera is not available!", Toast.LENGTH_SHORT).show();
			finish();
		}
		camera.startPreview();
	}

	public void surfaceChanged(SurfaceHolder holder, int arg1, int arg2, int arg3) {
		Camera.Parameters params = camera.getParameters();


		camera.setParameters(params);
			try {
				camera.setPreviewDisplay(holder);
				
				//prPreviewRunning = true;
			} catch (IOException _le) {
				_le.printStackTrace();
			}
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		if (camera != null) {
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
	    }
	}
	
	public void onInfo(MediaRecorder mr, int what, int extra) { 
	      //TODO
		return;
	}
}