package com.example.akarshan.drivelert;

import android.app.Activity;
import android.app.Dialog;
import android.media.MediaPlayer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by akarshan on 9/11/17.
 */

public class ViewDialog {

    public void showDialog(Activity activity){
        final FaceTrackerActivity faceTrackerActivity = (FaceTrackerActivity) activity;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (faceTrackerActivity.mediaPlayer != null && faceTrackerActivity.mediaPlayer.isPlaying()) {
                    faceTrackerActivity.mediaPlayer.stop();
                    faceTrackerActivity.mediaPlayer = null;
                    faceTrackerActivity.blockFlag = false;
                }
            }
        });
        dialog.show();

    }
}