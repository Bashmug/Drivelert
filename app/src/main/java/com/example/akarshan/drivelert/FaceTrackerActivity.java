package com.example.akarshan.drivelert;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.cuboid.cuboidcirclebutton.CuboidButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.example.akarshan.drivelert.ui.camera.CameraSourcePreview;
import com.example.akarshan.drivelert.ui.camera.GraphicOverlay;

import java.io.IOException;

/**
 * Activity for the face tracker app.  This app detects faces with the rear facing camera, and draws
 * overlay graphics to indicate the position, size, and ID of each face.
 */
public final class FaceTrackerActivity extends AppCompatActivity {
    private static final String TAG = "FaceTracker";
    public static FaceTrackerActivity instance = null;
    private CameraSource mCameraSource = null;
    private CuboidButton endButton;
    private CuboidButton noPreviewButton;
    private CuboidButton statusButton;
    public MediaPlayer mediaPlayer;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private long startTime;
    private long stopTime;
    private long endTime;
    private double thresholdTime;
    boolean isHandsFreeMode;
    int sound;
    private final Object lock = new Object();
    public boolean blockFlag;
    private static int count;

    //==============================================================================================
    // Activity Methods
    //==============================================================================================

    /**
     * Initializes the UI and initiates the creation of a face detector.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.face_tracker);
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        endButton = (CuboidButton) findViewById(R.id.button_end);
        statusButton = (CuboidButton) findViewById(R.id.button_status);
        startTime = System.currentTimeMillis();
        thresholdTime = getIntent().getDoubleExtra("time", 0.5);
        isHandsFreeMode = getIntent().getBooleanExtra("handsfreemode", false);
        sound = getIntent().getIntExtra("sound", 0);
        count = 0;
        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (volume == 0)
            Toast.makeText(getApplicationContext(), "Volume is MUTE", Toast.LENGTH_LONG).show();
        View decorview = getWindow().getDecorView(); //hide navigation bar
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorview.setSystemUiVisibility(uiOptions);

        endButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent next = new Intent(FaceTrackerActivity.this, EndActivity.class);
                next.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(next);
                FaceTrackerActivity.this.finish();
                return false;
            }
        });

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();

    }

    private void createCameraSource() {

        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Toast.makeText(getApplicationContext(), "Dependencies are not yet available. ", Toast.LENGTH_LONG).show();
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(45.0f)
                .build();

    }


    @Override
    protected void onResume() {
        super.onResume();
        instance = this;
        startCameraSource();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ALERT")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    // Graphic Face Tracker

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }


        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
            new MonitorTask().execute(face);
        }

        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
            statusButton.setCircle_color(R.color.yellow);
        }

        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(FaceTrackerActivity.this, EndActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        FaceTrackerActivity.this.finish();
    }

    private class MonitorTask extends AsyncTask<Face, Void, Void> {
        double deltaTime;
        boolean isEyeOpen;

        @Override
        protected Void doInBackground(Face... params) {
            Face face = params[0];
            blockFlag = false;
            float leftEyeOpenProbability = face.getIsLeftEyeOpenProbability();
            float rightEyeOpenProbability = face.getIsRightEyeOpenProbability();
            isEyeOpen = leftEyeOpenProbability > 0.50 && rightEyeOpenProbability > 0.50;
            if (!isEyeOpen) {
                deltaTime = (System.currentTimeMillis() - startTime) / 1000.0;
            } else
                startTime = System.currentTimeMillis();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (deltaTime > thresholdTime) {
                if (mediaPlayer == null && !blockFlag) {
                    if (sound == 0) {
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
                        mediaPlayer.start();
                    } else if (sound == 1) {
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm2);
                        mediaPlayer.start();
                    } else if (sound == 2) {
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm3);
                        mediaPlayer.start();
                    }
                    if (!isHandsFreeMode) {
                        blockFlag = true;
                    }
                    incrementer();
                }
            }
            if (isHandsFreeMode) {
                if (isEyeOpen) {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                            mediaPlayer = null;
                        }
                    }
                }
            } else {
                if (mediaPlayer != null) {
                    if (blockFlag) {
                        ViewDialog alert = new ViewDialog();
                        alert.showDialog(instance);
                    }
                }
            }

            if (count < 4)
                statusButton.setCircle_color(R.color.green);
            else
                statusButton.setCircle_color(R.color.red);
        }

    }

    private static void incrementer() {
        count++;
    }

}



