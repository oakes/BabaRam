package com.babaramdashcam;

import android.app.Activity;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import java.lang.Exception;

public class CameraPreview extends SurfaceView
	implements SurfaceHolder.Callback
{
	private static final String TAG = "CameraPreview";
	private Camera mCamera;
	private Activity mAct;
	private int mCameraId;
	private SurfaceHolder mHolder;

	public CameraPreview(Context context, int id) {
		super(context);

		mAct = (Activity) context;
		mCameraId = id;

		mHolder = getHolder();
		mHolder.addCallback(this);
		// deprecated setting, but required on Android versions prior to 3.0
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera = Camera.open(mCameraId);
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (Exception e) {
			Log.d(TAG, "Error setting camera preview: " + e.getMessage());
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		if (mHolder.getSurface() == null || mCamera == null) {
			return;
		}

		// Stop preview and change orientation.
		try {
			mCamera.stopPreview();
		} catch (Exception e) {}

		setCameraDisplayOrientation();

		// Start preview with new settings.
		try {
			mCamera.setPreviewDisplay(mHolder);
			mCamera.startPreview();
		} catch (Exception e) {
			Log.d(TAG, "Error starting camera preview: " + e.getMessage());
		}
	}

	private void setCameraDisplayOrientation() {
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
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;
		}
		else {
			result = (info.orientation - degrees + 360) % 360;
		}
		mCamera.setDisplayOrientation(result);
	}
}
