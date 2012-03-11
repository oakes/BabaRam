package com.babaramdashcam;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.GridView;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;

public class BabaRamImageAdapter extends BaseAdapter {
	private Context mContext;

	public BabaRamImageAdapter(Context c) {
		mContext = c;
	}

	public int getCount() {
		return BabaRamCamera.getThumbsDir().listFiles().length;
	}

	public File getItem(int position) {
		return BabaRamCamera.getThumbsDir().listFiles()[position];
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}

		try {
			File file = BabaRamCamera.getThumbsDir().listFiles()[position];
			Bitmap bitmap = BabaRamCamera.readThumbnail(file);
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
			}
		} catch (Exception e) {}

		return imageView;
	}
}
