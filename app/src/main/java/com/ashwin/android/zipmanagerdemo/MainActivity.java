package com.ashwin.android.zipmanagerdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String FILES_DIR = "filetest";
    private static final String NESTED_DIR = "nestedtest";
    private static final String ZIP_DIR = "ziptest";
    private static final String ZIP_FILE = "files.zip";
    private static final String UNZIP_DIR = "unziptest";
    private static final String FILE_1 = "file1.txt";
    private static final String FILE_2 = "file2.txt";
    private static final String FILE_3 = "file3.xml";
    private static final String FILE_1_CONTENT = "This is file 1 for testing.";
    private static final String FILE_2_CONTENT = "This is file 2 for testing.";
    private static final String FILE_3_CONTENT = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
            + "\n<body>"
            + "\n  <p>This is file 3 for testing.</p>"
            + "\n</body>";

    private static final int PERMISSIONS_REQUEST_CODE = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button zipButton = (Button) findViewById(R.id.zip_button);
        zipButton.setOnClickListener(this);

        Button unzipButton = (Button) findViewById(R.id.unzip_button);
        unzipButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            createFiles();
        } else {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void createFiles() {
        createTextFiles(FILES_DIR, FILE_1, FILE_1_CONTENT);
        createTextFiles(FILES_DIR, FILE_2, FILE_2_CONTENT);
        createTextFiles(FILES_DIR, FILE_3, FILE_3_CONTENT);
        createTextFiles(FILES_DIR + "/" + NESTED_DIR, FILE_1, FILE_1_CONTENT);
    }

    public boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = this.checkSelfPermission(permission);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermission(String permission) {
        try {
            ActivityCompat.requestPermissions((Activity) this, new String[]{permission}, PERMISSIONS_REQUEST_CODE);
        } catch (Exception e) {
            Log.e(ZipManager.TAG, "Exception while requesting permission", e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    createFiles();
                }
                break;
            }
        }
    }

    private void createTextFiles(String dirName, String fileName, String fileContent) {
        FileWriter writer = null;
        try {
            File root = new File(Environment.getExternalStorageDirectory(), dirName);
            if (!root.exists()) {
                root.mkdirs();
            }

            File file1 = new File(root, fileName);
            writer = new FileWriter(file1);
            writer.append(fileContent);
        } catch (Exception e) {
            Log.e(ZipManager.TAG, "Exception while creating test text files", e);
        } finally {
            try {
                writer.flush();
                writer.close();
            } catch (Exception e) {
                Log.e(ZipManager.TAG, "Exception while closing file-write", e);
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.zip_button:
                startZip();
                break;

            case R.id.unzip_button:
                startUnzip();
                break;
        }
    }

    private void startZip() {
        try {
            File filesDir = new File(Environment.getExternalStorageDirectory(), FILES_DIR);
            File outputDir = new File(Environment.getExternalStorageDirectory(), ZIP_DIR);
            File outFile = new File(outputDir, ZIP_FILE);
            ZipManager.zip(filesDir.getAbsolutePath(),  outFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(ZipManager.TAG, "Exception while zipping files", e);
        } finally {
            Toast.makeText(MainActivity.this, "Zip complete!", Toast.LENGTH_LONG).show();
        }
    }

    private void startUnzip() {
        try {
            File zipDir = new File(Environment.getExternalStorageDirectory(), ZIP_DIR);
            File zipFile = new File(zipDir, ZIP_FILE);
            File outputDir = new File(Environment.getExternalStorageDirectory(), UNZIP_DIR);
            ZipManager.unzip(zipFile.getAbsolutePath(), outputDir.getAbsolutePath());
        } catch (IOException e) {
            Log.e(ZipManager.TAG, "Exception while unzipping files", e);
        } finally {
            Toast.makeText(MainActivity.this, "Unzip complete!", Toast.LENGTH_LONG).show();
        }
    }
}
