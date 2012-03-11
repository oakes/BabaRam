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
import android.widget.ProgressBar;
import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;

public class BabaRamImageAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Bitmap> thumbs = new ArrayList<Bitmap>();

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
		if (thumbs.size() < position + 1) {
			thumbs.add(null);
		}

		if (thumbs.get(position) == null) {
			new CreateThumbTask().execute(new Integer(position));
			ProgressBar pb = new ProgressBar(mContext);
			pb.setLayoutParams(new GridView.LayoutParams(200, 200));
			pb.setPadding(50, 50, 50, 50);
			return pb;
		}

		ImageView imageView;
		imageView = new ImageView(mContext);
		imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setPadding(2, 2, 2, 2);
		imageView.setImageBitmap(thumbs.get(position));

		return imageView;
	}

	private class CreateThumbTask extends AsyncTask<Integer, Void, ThumbPair> {
		protected ThumbPair doInBackground(Integer... positions) {
			int position = positions[0].intValue();
			File file = BabaRamCamera.getFiles(false)[position];
			Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(
				file.toString(), MediaStore.Video.Thumbnails.MICRO_KIND
			);
			return new ThumbPair(bitmap, position);
		}

		protected void onPostExecute(ThumbPair result) {
			thumbs.set(result.position, result.bitmap);
			notifyDataSetChanged();
		}
	}

	private class ThumbPair {
		public Bitmap bitmap;
		public int position;
		public ThumbPair (Bitmap b, int p) {
			bitmap = b;
			position = p;
		}
	}
}
