package com.babaramdashcam;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.AdapterView;
import android.view.View;

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
			}
		});
	}
}
