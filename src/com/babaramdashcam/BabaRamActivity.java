package com.babaramdashcam;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import java.lang.Exception;

public class BabaRamActivity extends Activity {
	private BabaRamCamera mCamera;
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
			mCamera = new BabaRamCamera(this, 0);
			mLayout = (FrameLayout) findViewById(R.id.camera_preview);
			mLayout.addView(mCamera);
		}
		catch (Exception e) {}
	}
}
