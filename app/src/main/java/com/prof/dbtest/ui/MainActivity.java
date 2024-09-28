/*
 *   Copyright 2016 Marco Gomiero
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.prof.dbtest.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
// Import the androidx Toolbar
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prof.dbtest.AppContext;
import com.prof.dbtest.db.DBHelper;
import com.prof.dbtest.data.Exam;
import com.prof.dbtest.data.Student;
import com.prof.dbtest.R;
import com.prof.dbtest.backup.LocalBackup;
import com.prof.dbtest.backup.RemoteBackup;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.prof.dbtest.backup.LocalBackup.getExternalFilesDirPath;
import static com.prof.dbtest.db.DBHelper.getDatabaseVersion;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Google Drive Activity";

    public static final int REQUEST_CODE_SIGN_IN = 0;
    public static final int REQUEST_CODE_OPENING = 1;
    public static final int REQUEST_CODE_CREATION = 2;
    public static final int REQUEST_CODE_PERMISSIONS = 2;

    private static final int REQUEST_CODE_RESOLUTION = 3;
    public static DriveFile mfile;
    private static GoogleApiClient mGoogleApiClient;
    private static final String DATABASE_PATH = "/data/user/0/" + "com.prof.dbtest" + "/databases/" + "studentsManager";
    private static final File DATA_DIRECTORY_DATABASE =
            new File(getExternalFilesDirPath(AppContext.getContext()) + "/data/" + "com.prof.dbtest" + "/databases/" + "studentsManager");
    private static final String MIME_TYPE = "application/x-sqlite-3";

    //variable for decide if i need to do a backup or a restore.
    //True stands for backup, False for restore
    private boolean isBackup = true;

    private MainActivity activity;

    private RemoteBackup remoteBackup;
    private LocalBackup localBackup;
    private Context mContext;

    String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.stoolbar);
        setSupportActionBar(toolbar);

        Log.i("Thuc","onCrate = " + getExternalFilesDirPath(AppContext.getContext()));
        ActivityCompat.requestPermissions(this, permissions, 1234);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API).addScope(Drive.SCOPE_FILE).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void saveFileToDrive() {
        // Start by creating a new contents, and setting a callback.
        Log.i(TAG, "Creating new contents.");
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                if (!result.getStatus().isSuccess()) {
                    return;
                }

                String mimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(MIME_TYPE);
                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle("studentsManager") // Google Drive File name
                        .setMimeType(mimeType)
                        .setStarred(true).build();
                // create a file on root folder
                Drive.DriveApi.getRootFolder(mGoogleApiClient)
                        .createFile(mGoogleApiClient, changeSet, result.getDriveContents()).setResultCallback((ResultCallback<? super DriveFolder.DriveFileResult>) backupFileCallback);

            }
        });
    }

    public static void doGDriveBackup() {
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(backupContentsCallback);

    }

    static final private ResultCallback<DriveApi.DriveContentsResult> backupContentsCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        return;
                    }
                    String mimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(MIME_TYPE);
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("studentsManager") // Google Drive File name
                            .setMimeType(mimeType)
                            .setStarred(true).build();
                    // create a file on root folder
                    Drive.DriveApi.getRootFolder(mGoogleApiClient)
                            .createFile(mGoogleApiClient, changeSet, result.getDriveContents())
                            .setResultCallback(backupFileCallback);
                }
            };

    static final private ResultCallback<DriveFolder.DriveFileResult> backupFileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        return;
                    }
                    mfile = result.getDriveFile();
                    mfile.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, new DriveFile.DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesDownloaded, long bytesExpected) {
                        }
                    }).setResultCallback(backupContentsOpenedCallback);
                }
            };

    static final private ResultCallback<DriveApi.DriveContentsResult> backupContentsOpenedCallback = new
            ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        return;
                    }
//            DialogFragment_Sync.setProgressText("Backing up..");
                    DriveContents contents = result.getDriveContents();
                    BufferedOutputStream bos = new BufferedOutputStream(contents.getOutputStream());
                    byte[] buffer = new byte[1024];
                    int n;

                    try {
                        FileInputStream is = new FileInputStream(DATA_DIRECTORY_DATABASE);
                        BufferedInputStream bis = new BufferedInputStream(is);

                        while ((n = bis.read(buffer)) > 0) {
                            bos.write(buffer, 0, n);
//                    DialogFragment_Sync.setProgressText("Backing up...");
                        }
                        bos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    contents.commit(mGoogleApiClient, null).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
//                    DialogFragment_Sync.setProgressText("Backup completed!");
//                    mToast(act.getResources().getString(R.string.backupComplete));
//                    DialogFragment_Sync.dismissDialog();
                        }
                    });
                }
            };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1234) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, proceed with writing to shared storage
                remoteBackup = new RemoteBackup(this);
                localBackup = new LocalBackup(this);

                final DBHelper db = new DBHelper(AppContext.getContext());
                setupUI(db);
                db.closeDB();
            } else {
                // Permissions denied, handle accordingly
            }
        }
    }

    public void setupUI(DBHelper db) {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> showMultichoice());


        //button that shows student, exam and clears the view
        Button showStud = findViewById(R.id.button);
        Button showExam = findViewById(R.id.button2);
        Button clear = findViewById(R.id.button3);

        showStud.setOnClickListener(v -> {
            ArrayList<Student> students = db.getAllStudent();

            TableLayout table = findViewById(R.id.table);
            //table customization
            TableLayout.LayoutParams layoutParamsT = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsT.setMargins(30, 20, 40, 0);
            table.removeAllViews();

            TableRow row = new TableRow(getApplicationContext());
            TableRow.LayoutParams rLayoutParamsTR = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            rLayoutParamsTR.setMargins(30, 20, 40, 0);
            row.removeAllViews();

            //row population
            TextView tvIdTitle = new TextView(getApplicationContext());
            TextView tvNameTitle = new TextView(getApplicationContext());
            TextView tvSurnameTitle = new TextView(getApplicationContext());
            TextView tvDateTItle = new TextView(getApplicationContext());

            tvIdTitle.setText("ID");
            tvIdTitle.setTextSize(20);
            tvIdTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            tvNameTitle.setText("Name");
            tvNameTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            tvNameTitle.setTextSize(20);
            tvSurnameTitle.setText("Surname");
            tvSurnameTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            tvSurnameTitle.setTextSize(20);
            tvDateTItle.setText(String.valueOf("Birth"));
            tvDateTItle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            tvDateTItle.setTextSize(20);

            row.addView(tvIdTitle, rLayoutParamsTR);
            row.addView(tvNameTitle, rLayoutParamsTR);
            row.addView(tvSurnameTitle, rLayoutParamsTR);
            row.addView(tvDateTItle, rLayoutParamsTR);

            table.addView(row, layoutParamsT);

            for (Student stud : students) {

                TableRow rowEl = new TableRow(getApplicationContext());
                //table customization
                TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(30, 20, 40, 20);
                TableRow.LayoutParams rLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                rLayoutParams.setMargins(30, 20, 40, 0);

                //table population
                int id = stud.getId();
                String name = stud.getName();
                String surname = stud.getSurname();
                long millis = stud.getBorn();

                TextView tvId = new TextView(getApplicationContext());
                TextView tvName = new TextView(getApplicationContext());
                TextView tvSurname = new TextView(getApplicationContext());
                TextView tvDate = new TextView(getApplicationContext());

                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String dateString = formatter.format(new Date(millis));

                tvId.setText(String.valueOf(id));
                tvId.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                tvName.setText(name);
                tvName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                tvSurname.setText(surname);
                tvSurname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                tvDate.setText(dateString);
                tvDate.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                rowEl.addView(tvId, rLayoutParams);
                rowEl.addView(tvName, rLayoutParams);
                rowEl.addView(tvSurname, rLayoutParams);
                rowEl.addView(tvDate, rLayoutParams);

                table.addView(rowEl, layoutParams);
            }
        });

        showExam.setOnClickListener(v -> {
            ArrayList<Exam> exams = db.getAllExam();

            TableLayout table = findViewById(R.id.table);
            //table customization
            TableLayout.LayoutParams layoutParamsT = new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsT.setMargins(30, 20, 40, 0);
            TableRow.LayoutParams rLayoutParamsTR = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
            rLayoutParamsTR.setMargins(30, 20, 40, 0);
            table.removeAllViews();
            TableRow row = new TableRow(getApplicationContext());
            row.removeAllViews();

            //table population
            TextView tvIdTitle = new TextView(getApplicationContext());
            tvIdTitle.setTextSize(20);
            tvIdTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            TextView tvNameTitle = new TextView(getApplicationContext());
            tvNameTitle.setTextSize(20);
            tvNameTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            TextView tvSurnameTitle = new TextView(getApplicationContext());
            tvSurnameTitle.setTextSize(20);
            tvSurnameTitle.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

            tvIdTitle.setText("Exam ID");
            tvNameTitle.setText("Student ID");
            tvSurnameTitle.setText("Mark");

            row.addView(tvIdTitle, rLayoutParamsTR);
            row.addView(tvNameTitle, rLayoutParamsTR);
            row.addView(tvSurnameTitle, rLayoutParamsTR);

            table.addView(row, layoutParamsT);

            for (Exam e : exams) {
                TableRow rowEl = new TableRow(getApplicationContext());

                //table customization
                TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(30, 20, 40, 20);
                TableRow.LayoutParams rLayoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                rLayoutParams.setMargins(30, 20, 40, 0);

                //table population
                int id = e.getId();
                int stud = e.getStudent();
                int eval = e.getEvaluation();

                TextView tvId = new TextView(getApplicationContext());
                TextView tvName = new TextView(getApplicationContext());
                TextView tvSurname = new TextView(getApplicationContext());

                tvId.setText(String.valueOf(id));
                tvId.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                tvName.setText(String.valueOf(stud));
                tvName.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                tvSurname.setText(String.valueOf(eval));
                tvSurname.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                rowEl.addView(tvId, rLayoutParams);
                rowEl.addView(tvName, rLayoutParams);
                rowEl.addView(tvSurname, rLayoutParams);

                table.addView(rowEl, layoutParams);
            }
        });

        clear.setOnClickListener(v -> {
            TableLayout table = findViewById(R.id.table);
            table.removeAllViews();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private static final String MESSAGE_EXTERNAL_FOLDER
            = getExternalFilesDirPath(AppContext.getContext()) + File.separator;
    private static final String FOLDER_NAME_VERIFY_DB = "DBTest" + File.separator;
    private static final String VERIFY_DATABASE_PATH =
            MESSAGE_EXTERNAL_FOLDER + FOLDER_NAME_VERIFY_DB;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        final DBHelper db = new DBHelper(getApplicationContext());

        switch (id) {
            case R.id.action_backup:
                String outFileName = VERIFY_DATABASE_PATH; //Environment.getExternalStorageDirectory() + File.separator + getResources().getString(R.string.app_name) + File.separator;
                localBackup.performBackup(db, outFileName);
                break;
            case R.id.action_import:
                localBackup.performRestore(db);
                break;
            case R.id.action_backup_Drive:
                isBackup = true;
                //remoteBackup.connectToDrive(isBackup);
                saveFileToDrive();
                break;
            case R.id.action_import_Drive:
                isBackup = false;
                remoteBackup.connectToDrive(isBackup);
                break;
            case R.id.action_delete_all:
                //reinitialize the backup
                SQLiteDatabase database = db.getWritableDatabase();
                db.onUpgrade(database, getDatabaseVersion(), getDatabaseVersion());
                TableLayout table = findViewById(R.id.table);
                table.removeAllViews();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Thuc", "Sign in request code" + resultCode);


        if (resultCode == Activity.RESULT_OK) {
            saveFileToDrive();
        }

        switch (requestCode) {

            case REQUEST_CODE_SIGN_IN:
                Log.i("Thuc", "Sign in request code" + resultCode);
                // Called after user is signed in.
                if (resultCode == RESULT_OK) {
                    remoteBackup.connectToDrive(isBackup);
                }
                break;

            case REQUEST_CODE_CREATION:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i("Thuc", "Backup successfully saved.");
                    Toast.makeText(this, "Backup successufly loaded!", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_CODE_OPENING:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    remoteBackup.mOpenItemTaskSource.setResult(driveId);
                } else {
                    remoteBackup.mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                }

        }
    }


    private void showMultichoice() {
        AlertDialog.Builder builderChoose = new AlertDialog.Builder(activity);
        final CharSequence[] items = {"Add student", "Add Exam"};


        builderChoose
                .setItems(items, (dialog, which) -> {

                    switch (which) {
                        case 0:
                            Intent addStud = new Intent(MainActivity.this, AddStudent.class);
                            startActivity(addStud);
                            break;

                        case 1:
                            Intent addExam = new Intent(MainActivity.this, AddExam.class);
                            startActivity(addExam);
                            break;

                        default:
                            break;
                    }


                });
        builderChoose.show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //saveFileToDrive();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}

