package com.babaramdashcam;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.AdapterView;
import android.view.View;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

public class BabaRamHistoryActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);

		GridView gridview = (GridView) findViewById(R.id.gridview);
		gridview.setAdapter(new BabaRamImageAdapter(this));

		gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick
				(AdapterView<?> parent, View v, int position, long id)
			{
				File video = ((BabaRamImageAdapter) parent.getAdapter())
					.getItem(position);

				Intent playVideo = new Intent(Intent.ACTION_VIEW);
				playVideo.setDataAndType(Uri.fromFile(video), "video/*");
				startActivity(playVideo);
			}
		});
	}
}
