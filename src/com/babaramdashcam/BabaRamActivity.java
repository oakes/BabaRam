package com.babaramdashcam;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.view.View;
import android.content.Intent;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;

import java.lang.CharSequence;

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
		ImageButton storeBtn = (ImageButton) findViewById(R.id.storage_button);
		storeBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(0);
			}
		});
	}

	public void goToGallery() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("content://media/external/images/media"));
		startActivity(intent);
	}

	protected Dialog onCreateDialog(int id) {
		// Define the labels and cooresponding sizes.
		final CharSequence[] labels = {
			getResources().getString(R.string.infinite),
			getResources().getString(R.string.one_thousand),
			getResources().getString(R.string.seven_fifty),
			getResources().getString(R.string.five_hundred),
			getResources().getString(R.string.two_fifty),
			getResources().getString(R.string.one_hundred)
		};
		final int[] sizes = {-1, 1000, 750, 500, 250, 100};

		// Get the index of the currently-stored storage size.
		SharedPreferences sp = getPreferences(MODE_PRIVATE);
		int maxHistory = sp.getInt("maxHistory", 500);
		int index = 0;
		for (int i = 0; i < sizes.length; i++) {
			if (sizes[i] == maxHistory) {
				index = i;
				break;
			}
		}

		// Create the dialog.
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.how_much_to_store);
		builder.setSingleChoiceItems(labels, index,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					SharedPreferences sp = getPreferences(MODE_PRIVATE);
					int maxHistory = sizes[item];
					SharedPreferences.Editor ed = sp.edit();
					ed.putInt("maxHistory", maxHistory);
					ed.commit();
					((AlertDialog) dialog).hide();
				}
			}
		);

		return builder.create();
	}
}
