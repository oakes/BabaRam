package com.babaramdashcam;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Button;
import android.view.View;
import android.content.Intent;

public class BabaRamActivity extends Activity {
	private BabaRamCamera mCamera;
	private FrameLayout mLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Create the camera view and begin recording.
		mCamera = new BabaRamCamera(this, 0);
		mLayout = (FrameLayout) findViewById(R.id.camera_preview);
		mLayout.addView(mCamera);

		// Bind actions to the buttons.
		Button historyBtn = (Button) findViewById(R.id.history_button);
		historyBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				goToHistory();
			}
		});
		Button flipBtn = (Button) findViewById(R.id.flip_button);
		flipBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mCamera.flip();
			}
		});
    }

	public void goToHistory() {
		Intent intent = new Intent(this, BabaRamHistoryActivity.class);
		startActivity(intent);
	}
}
