package com.babaramdashcam;

import android.app.Activity;
import android.os.Bundle;
import android.hardware.Camera;
import android.widget.FrameLayout;
import java.lang.Exception;

public class BabaRamActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		//int num = Camera.getNumberOfCameras();

		Camera c = null;
		CameraPreview cp = null;
		FrameLayout fl = null;

		try {
			c = Camera.open(0);
			cp = new CameraPreview(this, c, 0);
			fl = (FrameLayout) findViewById(R.id.camera_preview);
			fl.addView(cp);
		}
		catch (Exception e) {}
    }
}
