package com.prof.dbtest.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.prof.dbtest.R;
import com.prof.dbtest.models.RingtoneInfo;
import com.prof.dbtest.utils.Utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;


public class ApplyActivity extends AppCompatActivity {
    private int[] RawNameList = {R.raw.apex, R.raw.aurora, R.raw.bamboo, R.raw.beacon, R.raw.bulletin, R.raw.chimes, R.raw.chord, R.raw.circles, R.raw.circuit, R.raw.complete, R.raw.constellation, R.raw.cosmic, R.raw.crystals, R.raw.hello, R.raw.hillside, R.raw.illuminate, R.raw.input, R.raw.keys, R.raw.night, R.raw.note, R.raw.opening_1st, R.raw.playtime, R.raw.popcorn, R.raw.presto, R.raw.pulse, R.raw.radar, R.raw.radiate, R.raw.reflection, R.raw.ripples, R.raw.seaside, R.raw.sencha, R.raw.signal, R.raw.silk, R.raw.slow, R.raw.stargaze, R.raw.summit, R.raw.synth, R.raw.twinkle, R.raw.uplift, R.raw.waves};
    private String[] ShowNameList = {"Apex", "Aurora", "Bamboo", "Beacon", "Bulletin", "Chimes", "Chord", "Circles", "Circuit", "Complete", "Constellation", "Cosmic", "Crystals", "Hello", "Hillside", "Illuminate", "Input", "Keys", "Night", "Note", "Opening 1st", "Playtime", "Popcorn", "Presto", "Pulse", "Radar", "Radiate", "Reflection", "Ripples", "Seaside", "Sencha", "Signal", "Silk", "Slow", "Stargaze", "Summit", "Synth", "Twinkle", "Uplift", "Waves"};
    Activity mContext;
    /* access modifiers changed from: private */
    private MediaPlayer mediaPlayer;
    String name;
    private String path;
    int pos;
    public RingtoneInfo ringtoneInfo;
    int s_pos = 0;
    private String[] saveNameList = {"apex", "aurora", "bamboo", "beacon", "bulletin", "chimes", "chord", "circles", "circuit", "complete", "constellation", "cosmic", "crystals", "hello", "hillside", "illuminate", "input", "keys", "night", "note", "opening_1st", "playtime", "popcorn", "presto", "pulse", "radar", "radiate", "reflection", "ripples", "seaside", "sencha", "signal", "silk", "slow", "stargaze", "summit", "synth", "twinkle", "uplift", "waves"};
    Typeface sf_regular;
    private TextView songName;

    public ApplyActivity() {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.apply_activity);
        this.mContext = this;
        this.sf_regular = Typeface.createFromAsset(getAssets(), "sf_regular.otf");
        TextView textView = (TextView) findViewById(R.id.songName);
        this.songName = textView;
        textView.setTypeface(this.sf_regular);
        int intExtra = getIntent().getIntExtra("position", 0);
        this.pos = intExtra;
        this.mediaPlayer = MediaPlayer.create(this.mContext, this.RawNameList[intExtra]);
        String[] strArr = this.saveNameList;
        int i = this.pos;
        this.name = strArr[i];
        this.songName.setText(this.ShowNameList[i]);
        this.path = Utils.getRootDirPath(this);
        Class<R.raw> cls = R.raw.class;
        this.ringtoneInfo = new RingtoneInfo();
        try {
            String name2 = cls.getField(this.name).getName();
            if (!name2.contains("consumer_onesignal_keep") && !name2.contains("keep") && !name2.equals("ringtones")) {
                this.ringtoneInfo.setFileName(name2 + ".mp3");
                this.ringtoneInfo.setAudioResource(cls.getField(name2).getInt(name2));
            }
        } catch (Exception unused) {
        }
        findViewById(R.id.ringTuneImg).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ApplyActivity.this.setRingAskPermission(0);
            }
        });
        findViewById(R.id.back_img).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ApplyActivity.this.finish();
            }
        });
        findViewById(R.id.contactTuneImg).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(ApplyActivity.this.mContext, "Coming Soon", Toast.LENGTH_LONG).show();
            }
        });
        findViewById(R.id.notificationImg).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ApplyActivity.this.setRingAskPermission(3);
            }
        });
        findViewById(R.id.alarmImg).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ApplyActivity.this.setRingAskPermission(1);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        MediaPlayer mediaPlayer2 = this.mediaPlayer;
        if (mediaPlayer2 != null && mediaPlayer2.isPlaying()) {
            this.mediaPlayer.release();
        }
    }


    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (Settings.System.canWrite(this)) {
            Log.d("TAG", "MainActivity.CODE_WRITE_SETTINGS_PERMISSION success");
            setRing(this.s_pos);
        }
    }

    public void setRingAskPermission(final int i) {
        int checkSelfPermission = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE");
        if (Build.VERSION.SDK_INT >= 29) {
            if (checkSelfPermission == 0) {
                DialogInterface.OnClickListener onClickListener = null;
                new AlertDialog.Builder(this).setTitle("Allow access").setMessage("To use this content, This app needs access to device storage permission").setPositiveButton(17039379, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ApplyActivity.this.permission(i);
                    }
                }).setNegativeButton(android.R.string.yes, (DialogInterface.OnClickListener) null).show();
            } else if (Settings.System.canWrite(this)) {
                setRing(i);
            } else {
                askManageWritePermissionDialog(i);
            }
        } else if (Settings.System.canWrite(this)) {
            setRing(i);
        } else {
            askManageWritePermissionDialog(i);
        }
    }

    public void askManageWritePermissionDialog(final int i) {
        DialogInterface.OnClickListener onClickListener = null;
        new AlertDialog.Builder(this).setTitle("Allow access").setMessage("To set sounds, This app needs permission to modify system settings").setPositiveButton(17039379, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ApplyActivity.this.doWritePermission(i);
            }
        }).setNegativeButton(android.R.string.yes, (DialogInterface.OnClickListener) null).show();
    }

    public void doWritePermission(int i) {
        if (Settings.System.canWrite(this)) {
            setRing(i);
            return;
        }
        this.s_pos = i;
        Intent intent = new Intent("android.settings.action.MANAGE_WRITE_SETTINGS");
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    public void permission(final int i) {
        Dexter.withActivity(this).withPermissions("android.permission.WRITE_EXTERNAL_STORAGE").withListener(new MultiplePermissionsListener() {
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    if (Settings.System.canWrite(ApplyActivity.this.mContext)) {
                        ApplyActivity.this.setRing(i);
                    } else {
                        ApplyActivity.this.askManageWritePermissionDialog(i);
                    }
                }
            }

            public void onPermissionRationaleShouldBeShown(List list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public void setRing(int i) {
        if (i == 0) {
            setDefaultRingtone(this.ringtoneInfo);
        } else if (i == 1) {
            setDefaultAlarm(this.ringtoneInfo);
        } else if (i == 3) {
            setDefaultNotice(this.ringtoneInfo);
        }
    }

    private void setDefaultRingtone(RingtoneInfo ringtoneInfo2) {
        File file = new File(this.path + "/" + ringtoneInfo2.getFileName());
        Uri parse = Uri.parse("android.resource://" + getPackageName() + "/raw/" + ringtoneInfo2.getAudioResource());
        ContentResolver contentResolver = getContentResolver();
        try {
            byte[] bArr = new byte[1024];
            FileInputStream createInputStream = contentResolver.openAssetFileDescriptor(parse, "r").createInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            while (true) {
                int read = createInputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                fileOutputStream.write(bArr, 0, read);
            }
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e("get in catch", "IOException exception " + e.getMessage());
        }
        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE"));
        setAsRingtoneAndroid(file, contentResolver, "Ringtone");
    }

    public void setAsRingtoneAndroid(File file, ContentResolver contentResolver, String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", file.getName());
        contentValues.put("mime_type", getMIMEType(file.getAbsolutePath()));
        contentValues.put("_size", Long.valueOf(file.length()));
        contentValues.put("artist", Integer.valueOf(R.string.app_name));
        contentValues.put("is_ringtone", true);
        if (str.equals("Alarm")) {
            contentValues.put("is_alarm", true);
        }
        if (Build.VERSION.SDK_INT >= 29) {
            Uri insert = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
            try {
                OutputStream openOutputStream = getContentResolver().openOutputStream(insert);
                int length = (int) file.length();
                byte[] bArr = new byte[length];
                try {
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                    bufferedInputStream.read(bArr, 0, length);
                    bufferedInputStream.close();
                    openOutputStream.write(bArr);
                    openOutputStream.close();
                    openOutputStream.flush();
                } catch (IOException unused) {
                }
                if (openOutputStream != null) {
                    openOutputStream.close();
                }
            } catch (Exception unused2) {
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
            RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), 1, insert);
            Toast.makeText(this, "Ringtone set", Toast.LENGTH_SHORT).show();
            if (str.equals("IS_ALARM")) {
                RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), 4, insert);
                Settings.System.putString(contentResolver, "alarm_alert", insert.toString());
                return;
            }
            return;
        }
        contentValues.put("_data", file.getAbsolutePath());
        Uri contentUriForPath = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
        assert contentUriForPath != null;
        getContentResolver().delete(contentUriForPath, "_data=\"" + file.getAbsolutePath() + "\"", (String[]) null);
        RingtoneManager.setActualDefaultRingtoneUri(this, 1, getContentResolver().insert(contentUriForPath, contentValues));
        ContentResolver contentResolver2 = getContentResolver();
        Uri contentUriForPath2 = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
        Objects.requireNonNull(contentUriForPath2);
        contentResolver2.insert(contentUriForPath2, contentValues);
    }

    public void setDefaultAlarm(RingtoneInfo ringtoneInfo2) {
        File file = new File(this.path + "/" + ringtoneInfo2.getFileName());
        Uri parse = Uri.parse("android.resource://" + getPackageName() + "/raw/" + ringtoneInfo2.getAudioResource());
        ContentResolver contentResolver = getContentResolver();
        try {
            byte[] bArr = new byte[1024];
            FileInputStream createInputStream = contentResolver.openAssetFileDescriptor(parse, "r").createInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            while (true) {
                int read = createInputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                fileOutputStream.write(bArr, 0, read);
            }
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e("get in catch", "IOException exception " + e.getMessage());
        }
        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE"));
        setAsAlarmAndroid(file, contentResolver);
    }


    private void setAsAlarmAndroid(File r10, ContentResolver r11) {
    }

    private void setDefaultNotice(RingtoneInfo ringtoneInfo2) {
        File file = new File(this.path + "/" + ringtoneInfo2.getFileName());
        Log.d("ContentValues", "setDefaultRingtone: " + file.getPath());
        Uri parse = Uri.parse(new StringBuilder("android.resource://").append(getPackageName()).append("/raw/").append(ringtoneInfo2.getAudioResource()).toString());
        Log.d("URI ", "setRingtoneTes: " + parse.getPath());
        Log.d("URI ", new StringBuilder("setRingtoneTes: ").append(parse.toString()).toString());
        try {
            byte[] bArr = new byte[1024];
            FileInputStream createInputStream = getContentResolver().openAssetFileDescriptor(parse, "r").createInputStream();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            while (true) {
                int read = createInputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                fileOutputStream.write(bArr, 0, read);
            }
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e("get in catch", "IOException exception " + e.getMessage());
        }
        sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE"));
        setAsNotificationAndroid(file);
    }

    private void setAsNotificationAndroid(File file) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", file.getName());
        contentValues.put("mime_type", getMIMEType(file.getAbsolutePath()));
        contentValues.put("_size", Long.valueOf(file.length()));
        contentValues.put("artist", Integer.valueOf(R.string.app_name));
        contentValues.put("is_notification", true);
        if (Build.VERSION.SDK_INT >= 29) {
            Uri insert = getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
            try {
                OutputStream openOutputStream = getContentResolver().openOutputStream(insert);
                int length = (int) file.length();
                byte[] bArr = new byte[length];
                try {
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                    bufferedInputStream.read(bArr, 0, length);
                    bufferedInputStream.close();
                    openOutputStream.write(bArr);
                    openOutputStream.close();
                    openOutputStream.flush();
                } catch (IOException unused) {
                }
                if (openOutputStream != null) {
                    openOutputStream.close();
                }
            } catch (Exception unused2) {
            } catch (Throwable th) {
                th.addSuppressed(th);
            }
            RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(), 2, insert);
            Toast.makeText(this, "Notification set", Toast.LENGTH_SHORT).show();
            return;
        }
        contentValues.put("_data", file.getAbsolutePath());
        Uri contentUriForPath = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());
        String[] strArr = null;
        getContentResolver().delete(contentUriForPath, "_data=\"" + file.getAbsolutePath() + "\"", (String[]) null);
        RingtoneManager.setActualDefaultRingtoneUri(this, 2, getContentResolver().insert(contentUriForPath, contentValues));
        getContentResolver().insert(MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath()), contentValues);
        Toast.makeText(this, "Notification set", Toast.LENGTH_SHORT).show();
    }

    public static String getMIMEType(String str) {
        String fileExtensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(str);
        if (fileExtensionFromUrl != null) {
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtensionFromUrl);
        }
        return null;
    }
}
