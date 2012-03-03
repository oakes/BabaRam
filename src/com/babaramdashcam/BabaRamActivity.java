package com.babaramdashcam;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import java.lang.Exception;

public class BabaRamActivity extends Activity {
	private CameraPreview mPreview;
	private FrameLayout mLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

	@Override
	public void onResume() {
		super.onResume();

		try {
			mPreview = new CameraPreview(this, 0);
			mLayout = (FrameLayout) findViewById(R.id.camera_preview);
			mLayout.addView(mPreview);
		}
		catch (Exception e) {}
	}
}
