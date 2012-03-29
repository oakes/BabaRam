package com.babaramdashcam;

import android.app.Activity;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Environment;
import android.os.StatFs;
import android.media.MediaRecorder;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.util.Log;
import android.widget.Toast;
import android.net.Uri;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileInputStream;
import java.lang.Exception;

public class BabaRamCamera extends SurfaceView
	implements SurfaceHolder.Callback
{
	private static final String TAG = "BabaRamCamera";
	private static final String MP4 = ".mp4";
	private static final long MAXFILESIZE = 100 * 1000 * 1024;
	private static final int MINDURATION = 5000;
	private Camera mCamera = null;
	private MediaRecorder mRecorder = null;
	private Activity mAct;
	private int mCameraId;
	private SurfaceHolder mHolder;

	public BabaRamCamera(Context context, int id) {
		super(context);

		mAct = (Activity) context;
		mCameraId = id;

		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		deleteOldVideos();
		start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		stop();
		deleteOldVideos();
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	}

	public void start() {
		if (mCamera == null) {
			try {
				mCamera = Camera.open(mCameraId);
			} catch (Exception e) {
				stop();
				Toast.makeText(mAct,
					getResources().getString(R.string.camera_error),
					Toast.LENGTH_SHORT
				).show();
				return;
			}

			try {
				mCamera.setDisplayOrientation(getCameraOrientation(true));
			} catch (Exception e) {}

			mCamera.unlock();
		}

		startRecorder();
	}

	public void startRecorder() {
		if (mRecorder == null) {
			// Initialize.
			mRecorder = new MediaRecorder();
			mRecorder.setCamera(mCamera);

			// Set sources.
			mRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

			// Set profile.
			mRecorder.setProfile(CamcorderProfile.get(
				mCameraId,
				CamcorderProfile.QUALITY_HIGH
			));

			// Set the rest of the properties.
			mRecorder.setOrientationHint(getCameraOrientation(false));
			mRecorder.setOutputFile(getOutputFile().toString());
			mRecorder.setPreviewDisplay(mHolder.getSurface());
			mRecorder.setMaxFileSize(MAXFILESIZE);
			mRecorder.setOnInfoListener(
				new MediaRecorder.OnInfoListener() {
					public void onInfo(MediaRecorder mr, int w, int ex) {
						if (w == MediaRecorder.
							MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED)
						{
							stop();
							deleteOldVideos();
							start();
						}
					}
				}
			);
			mRecorder.setOnErrorListener(
				new MediaRecorder.OnErrorListener() {
					public void onError(MediaRecorder mr, int w, int ex) {
						stop();
						deleteOldVideos();
						start();
					}
				}
			);

			// Begin recording.
			try {
				mRecorder.prepare();
				mRecorder.start();
			} catch (Exception e) {
				Log.d(TAG, "Recorder start: " + e.getMessage());
				stop();
			}
		}
	}

	public void stop() {
		stopRecorder();

		if (mCamera != null) {
			mCamera.lock();
			mCamera.release();
			mCamera = null;
		}
	}

	public void stopRecorder() {
		if (mRecorder != null) {
			try {
				mRecorder.stop();
				mRecorder.reset();
				mRecorder.release();
			} catch (Exception e) {
				Log.d(TAG, "Recorder stop: " + e.getMessage());
			}
			mRecorder = null;
		}
	}

	public void flip() {
		if (Camera.getNumberOfCameras() < 2) {
			return;
		}

		mCameraId = (mCameraId == 0) ? 1 : 0;
		stop();
		start();
	}

	public static File[] getFiles(boolean isAscending) {
		File dir = getOutputDir();
		if (dir == null) {
			return null;
		}

		File[] files = dir.listFiles();
		if (isAscending) {
			Arrays.sort(files, new Comparator<File>(){
				public int compare(File f1, File f2) {
					return Long.valueOf(f1.lastModified())
							.compareTo(f2.lastModified());
				}
			});
		}
		else {
			Arrays.sort(files, new Comparator<File>(){
				public int compare(File f1, File f2) {
					return Long.valueOf(f2.lastModified())
						.compareTo(f1.lastModified());
				}
			});
		}
		return files;
	}

	private void deleteOldVideos() {
		// Get all the files in the directory and sort in ascending order.
		File[] files = getFiles(true);
		if (files == null || files.length == 0) {
			return;
		}

		// Iterate over each file.
		long totalFileSize = 0;
		for (int i = 0; i < files.length; i++) {
			try {
				// Get the duration of this video.
				MediaMetadataRetriever mmr = new MediaMetadataRetriever();
				FileInputStream fis = new FileInputStream(files[i]);
				mmr.setDataSource(fis.getFD());
				String duration = mmr.extractMetadata(
					MediaMetadataRetriever.METADATA_KEY_DURATION
				);

				// Delete file if it's too short, and alert the user.
				if (Long.parseLong(duration) <= MINDURATION) {
					files[i].delete();
					Toast.makeText(mAct,
						getResources().getString(R.string.too_short),
						Toast.LENGTH_SHORT
					).show();
				}

				// Otherwise, add the file size to the total.
				else {
					totalFileSize += files[i].length();
				}
			} catch (Exception e) {
				files[i].delete();
			}
		}

		// Delete a video if there isn't much free space left.
		if (getFreeSpace() < MAXFILESIZE) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].exists()) {
					totalFileSize -= files[i].length();
					files[i].delete();
					break;
				}
			}
		}

		// Delete one or more videos if we've exceeded our max storage size.
		SharedPreferences sp = mAct.getPreferences(Context.MODE_PRIVATE);
		int maxHistory = sp.getInt("maxHistory", 500);
		if (maxHistory >= 0) {
			for (int i = 0; i < files.length; i++) {
				if (totalFileSize >= (maxHistory * 1000 * 1024)) {
					if (files[i].exists()) {
						totalFileSize -= files[i].length();
						files[i].delete();
					}
				}
			}
		}

		// Refresh the gallery to make sure it is accurately reflected.
		for (int i = 0; i < files.length; i++) {
			if (files[i].exists()) {
				new SingleMediaScanner(mAct, files[i]);
			}
		}
	}

	private long getFreeSpace() {
		StatFs stat = new StatFs(
			Environment.getExternalStorageDirectory().getPath()
		);
		return (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
	}

	private int getCameraOrientation(boolean display) {
		android.hardware.Camera.CameraInfo info =
			new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(mCameraId, info);
		int rotate = mAct.getWindowManager().getDefaultDisplay().getRotation();

		int degrees = 0;
		switch (rotate) {
			case Surface.ROTATION_0: degrees = 0; break;
			case Surface.ROTATION_90: degrees = 90; break;
			case Surface.ROTATION_180: degrees = 180; break;
			case Surface.ROTATION_270: degrees = 270; break;
		}

		int result;
		if ((display || degrees != 0) &&
			info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
		{
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;
		}
		else {
			result = (info.orientation - degrees + 360) % 360;
		}

		return result;
	}

	public static File getOutputDir() {
		File mediaStorageDir = new File(
			Environment.getExternalStorageDirectory(),
			"BabaRam"
		);
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}
		return mediaStorageDir;
	}

	private File getOutputFile() {
		File mediaStorageDir = getOutputDir();
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
			.format(new Date());
		File mediaFile = new File(mediaStorageDir, timeStamp + MP4);
		return mediaFile;
	}

	private class SingleMediaScanner implements MediaScannerConnectionClient {
		private MediaScannerConnection mConn;
		private File mFile;

		public SingleMediaScanner(Context context, File f) {
			mFile = f;
			mConn = new MediaScannerConnection(context, this);
			mConn.connect();
		}

		public void onMediaScannerConnected() {
			mConn.scanFile(mFile.getAbsolutePath(), null);
		}

		public void onScanCompleted(String path, Uri uri) {
			mConn.disconnect();
		}
	}
}
