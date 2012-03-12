package com.babaramdashcam;

import android.app.Activity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.view.View;
import android.content.Intent;
import android.net.Uri;

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
		ImageButton galleryBtn =
			(ImageButton) findViewById(R.id.gallery_button);
		galleryBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				goToGallery();
			}
		});
		ImageButton flipBtn = (ImageButton) findViewById(R.id.flip_button);
		flipBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mCamera.flip();
			}
		});
    }

	public void goToGallery() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("content://media/external/images/media"));
		startActivity(intent);
	}
}
