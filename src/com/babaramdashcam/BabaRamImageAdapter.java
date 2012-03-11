package com.babaramdashcam;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.GridView;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import java.io.File;

public class BabaRamImageAdapter extends BaseAdapter {
	private Context mContext;

	public BabaRamImageAdapter(Context c) {
		mContext = c;
	}

	public int getCount() {
		return BabaRamCamera.getFiles(false).length;
	}

	public File getItem(int position) {
		return BabaRamCamera.getFiles(false)[position];
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(128, 128));
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setPadding(8, 8, 8, 8);
		} else {
			imageView = (ImageView) convertView;
		}

		try {
			File file = BabaRamCamera.getFiles(false)[position];
			Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(
				file.toString(), MediaStore.Video.Thumbnails.MICRO_KIND
			);
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
			}
		} catch (Exception e) {}

		return imageView;
	}
}
