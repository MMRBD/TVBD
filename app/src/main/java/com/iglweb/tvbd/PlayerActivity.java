package com.iglweb.tvbd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.yqritc.scalablevideoview.ScalableType;

public class PlayerActivity extends AppCompatActivity {

    private VideoView vidView;
    // TextView tvChanelInfo;
    ProgressDialog pDialog;
    int position = 0;
    String[] urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_player);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        vidView = findViewById(R.id.video);
        // tvChanelInfo = findViewById(R.id.tv_chanel_info);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        urls = intent.getStringArrayExtra("chURLS");
        String[] infos = intent.getStringArrayExtra("chINFO");
        Log.e("Position 10", String.valueOf(position));

        final String[] vidAddress = {urls[position]};
        Log.e("Position 10", String.valueOf(position));
        //tvChanelInfo.setText(info);

        final Uri vidUri = Uri.parse(vidAddress[0]);


//        mediaController = new MediaController(this);
//        mediaController.setAnchorView(vidView);
//        mediaController.setMediaPlayer(vidView);
//
//        vidView.setMediaController(mediaController);
//        vidView.setVideoURI(vidUri);
//        vidView.start();


        pDialog = new ProgressDialog(this);
        // Set progressbar title
        pDialog.setTitle("TVBD");
        // Set progressbar message
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
        Log.e("Position 11", String.valueOf(position));

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(this);
            mediacontroller.setAnchorView(vidView);
            // Get the URL from String VideoURL
            vidView.setMediaController(mediacontroller);
            vidView.setVideoURI(vidUri);


            mediacontroller.setPrevNextListeners(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    pDialog.show();
                    Log.e("Position 1", String.valueOf(position));
                    if (position < urls.length - 1) {
                        vidAddress[0] = urls[position++];
                        Log.e("Position 2", String.valueOf(position));
                        vidView.setVideoURI(vidUri);
                    } else {
                        pDialog.dismiss();
                        Toast.makeText(PlayerActivity.this, "THIS IS THE LAST CHANEL.", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pDialog.show();
                    // 0 < 6
                    if (position - 1 < 0) {
                        pDialog.dismiss();
                        Toast.makeText(PlayerActivity.this, "THIS IS THE FIRST CHANEL.", Toast.LENGTH_SHORT).show();
                    } else {
                        vidAddress[0] = urls[position--];
                        vidView.setVideoURI(vidUri);
                    }
                }
            });
            mediacontroller.show(10000);


        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        vidView.requestFocus();
        vidView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {
                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mp.setVolume(50f, 50f);
                pDialog.dismiss();

                vidView.start();
            }

        });

    }
}
