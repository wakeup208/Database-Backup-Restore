package com.prof.dbtest.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.prof.dbtest.R;
import com.prof.dbtest.adapter.SongListAdaptr;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {
    private int[] RawNameList = {R.raw.apex, R.raw.aurora, R.raw.bamboo, R.raw.beacon, R.raw.bulletin, R.raw.chimes, R.raw.chord, R.raw.circles, R.raw.circuit, R.raw.complete, R.raw.constellation, R.raw.cosmic, R.raw.crystals, R.raw.hello, R.raw.hillside, R.raw.illuminate, R.raw.input, R.raw.keys, R.raw.night, R.raw.note, R.raw.opening_1st, R.raw.playtime, R.raw.popcorn, R.raw.presto, R.raw.pulse, R.raw.radar, R.raw.radiate, R.raw.reflection, R.raw.ripples, R.raw.seaside, R.raw.sencha, R.raw.signal, R.raw.silk, R.raw.slow, R.raw.stargaze, R.raw.summit, R.raw.synth, R.raw.twinkle, R.raw.uplift, R.raw.waves};
    private String[] ShowNameList = {"Apex", "Aurora", "Bamboo", "Beacon", "Bulletin", "Chimes", "Chord", "Circles", "Circuit", "Complete", "Constellation", "Cosmic", "Crystals", "Hello", "Hillside", "Illuminate", "Input", "Keys", "Night", "Note", "Opening 1st", "Playtime", "Popcorn", "Presto", "Pulse", "Radar", "Radiate", "Reflection", "Ripples", "Seaside", "Sencha", "Signal", "Silk", "Slow", "Stargaze", "Summit", "Synth", "Twinkle", "Uplift", "Waves"};
    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            try {
                if ((MainActivity.this.mediaPlayer != null) && MainActivity.this.mediaPlayer.isPlaying()) {
                    MainActivity mainActivity = MainActivity.this;
                    double unused = mainActivity.startTime = (double) (mainActivity.mediaPlayer.getCurrentPosition() / 1000);
                } else {
                    double unused2 = MainActivity.this.startTime = 0.0d;
                }
                MainActivity.this.tx1.setText(String.format("%d : %d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) MainActivity.this.startTime)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) MainActivity.this.startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) MainActivity.this.startTime)))}));
                MainActivity.this.seekbar.setProgress((int) MainActivity.this.startTime);
                MainActivity.this.myHandler.postDelayed(this, 100);
            } catch (Exception unused3) {
            }
        }
    };
    public int backwardTime = 5000;
    private ImageView backword_btn;
    public double finalTime = 0.0d;
    public int forwardTime = 5000;
    private ImageView fwd_btn;
    private TextView heading_tv2;
    Context mContext;
    public MediaPlayer mediaPlayer;
    public Handler myHandler = new Handler();
    public ImageView play_pause;
    public int positionn = 0;
    public SeekBar seekbar;
    Typeface sf_regular;
    Typeface sf_semibold;
    SongListAdaptr songListAdaptr;
    int songPosition = 0;
    ListView songsNameList;
    public double startTime = 0.0d;
    public TextView tx1;
    private TextView tx2;
    private TextView tx3;

    public MainActivity() {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.activity_main_rings);
        this.mContext = this;
        Window window = getWindow();
        window.addFlags(Integer.MIN_VALUE);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        window.setNavigationBarColor(-1);
        this.heading_tv2 = (TextView) findViewById(R.id.heading_tv2);
        this.backword_btn = (ImageView) findViewById(R.id.backword_btn);
        this.play_pause = (ImageView) findViewById(R.id.play_pause);
        this.fwd_btn = (ImageView) findViewById(R.id.fwd_btn);
        this.tx1 = (TextView) findViewById(R.id.textView2);
        this.tx2 = (TextView) findViewById(R.id.textView3);
        this.tx3 = (TextView) findViewById(R.id.textView4);
        this.songsNameList = (ListView) findViewById(R.id.songsNameList);
        SongListAdaptr songListAdaptr2 = new SongListAdaptr(this.mContext, this.ShowNameList);
        songsNameList.setAdapter(songListAdaptr2);
        this.sf_regular = Typeface.createFromAsset(getAssets(), "sf_regular.otf");
        this.sf_semibold = Typeface.createFromAsset(getAssets(), "sf_bold.otf");
        this.tx1.setTypeface(this.sf_regular);
        this.tx2.setTypeface(this.sf_regular);
        this.tx3.setTypeface(this.sf_regular);
        this.heading_tv2.setTypeface(this.sf_semibold);
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        this.seekbar = seekBar;
        seekBar.setClickable(false);
        this.play_pause.setEnabled(false);
        this.songsNameList.setVisibility(View.VISIBLE);
        this.play_pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (MainActivity.this.mediaPlayer.isPlaying()) {
                    MainActivity.this.mediaPlayer.pause();
                    MainActivity.this.play_pause.setImageResource(R.drawable.play_btn_small);
                    return;
                }
                MainActivity.this.mediaPlayer.start();
                MainActivity.this.play_pause.setImageResource(R.drawable.pause_btn_small);
            }
        });
        this.backword_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (((int) MainActivity.this.startTime) - MainActivity.this.backwardTime > 0) {
                    MainActivity mainActivity = MainActivity.this;
                    double unused = mainActivity.startTime = mainActivity.startTime - ((double) MainActivity.this.backwardTime);
                    MainActivity.this.mediaPlayer.seekTo((int) MainActivity.this.startTime);
                    Toast.makeText(MainActivity.this.getApplicationContext(), "You have Jumped backward 5 seconds", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MainActivity.this.getApplicationContext(), "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
            }
        });
        this.fwd_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (((double) (((int) MainActivity.this.startTime) + MainActivity.this.forwardTime)) <= MainActivity.this.finalTime) {
                    MainActivity mainActivity = MainActivity.this;
                    double unused = mainActivity.startTime = mainActivity.startTime + ((double) MainActivity.this.forwardTime);
                    MainActivity.this.mediaPlayer.seekTo((int) MainActivity.this.startTime);
                    Toast.makeText(MainActivity.this.getApplicationContext(), "You have Jumped forward 5 seconds", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(MainActivity.this.getApplicationContext(), "Cannot jump forward 5 seconds",  Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void playSong(int i) {
        try {
            MediaPlayer mediaPlayer2 = this.mediaPlayer;
            if (mediaPlayer2 != null && mediaPlayer2.isPlaying()) {
                this.mediaPlayer.reset();
            }
            this.play_pause.setImageResource(R.drawable.pause_btn_small);
            this.songPosition = i;
            this.mediaPlayer = MediaPlayer.create(this.mContext, this.RawNameList[i]);
            this.tx3.setText(this.ShowNameList[i]);
            this.mediaPlayer.start();
            this.finalTime = (double) this.mediaPlayer.getDuration();
            this.startTime = (double) this.mediaPlayer.getCurrentPosition();
            this.seekbar.setMax(((int) this.finalTime) / 1000);
            this.tx2.setText(String.format("%d : %d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) this.finalTime)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) this.finalTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) this.finalTime)))}));
            this.tx1.setText(String.format("%d : %d", new Object[]{Long.valueOf(TimeUnit.MILLISECONDS.toMinutes((long) this.startTime)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds((long) this.startTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) this.startTime)))}));
            this.seekbar.setProgress((int) this.startTime);
            this.myHandler.postDelayed(this.UpdateSongTime, 100);
            this.play_pause.setEnabled(true);
        } catch (Exception unused) {
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        try {
            MediaPlayer mediaPlayer2 = this.mediaPlayer;
            if (mediaPlayer2 != null && mediaPlayer2.isPlaying()) {
                Log.d("TAG------->", "player is running");
                this.mediaPlayer.stop();
                Log.d("Tag------->", "player is stopped");
                this.mediaPlayer.release();
                Log.d("TAG------->", "player is released");
            }
        } catch (Exception unused) {
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle((CharSequence) "Like This App").setMessage((CharSequence) "Do you have a few seconds to rate this app? We want to hear your opinion. Thanks").setCancelable(false).setPositiveButton((CharSequence) "EXIT", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (MainActivity.this.mediaPlayer != null) {
                    if (MainActivity.this.mediaPlayer.isPlaying()) {
                        MainActivity.this.mediaPlayer.stop();
                    }
                    MainActivity.this.mediaPlayer.reset();
                    MainActivity.this.mediaPlayer.release();
                }
                MainActivity.this.finish();
            }
        }).setNegativeButton((CharSequence) "RATE 5 STAR", (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + MainActivity.this.getPackageName())));
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }
}
