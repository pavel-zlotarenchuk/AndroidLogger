package gmoby.android.logger.androidlogger;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import gmoby.android.logger.logger.AndroidLogger;

public class MainActivity extends AppCompatActivity {
    String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    int count = 0;
    Button button;
    Button zipButton;
    Button renameButton;
    Button sendButton;
    final String TAG = "logTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.log_button);
        zipButton = findViewById(R.id.zipLog);
        renameButton = findViewById(R.id.renameLog);
        sendButton = findViewById(R.id.sendLog);
        AndroidLogger.setClearOnFilling(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
//                for (int i = 0; i < 2; i++) {
//                    Logger.d(TAG, "clicked " + String.valueOf(count) + " times.");
//                }
                //Logger.e(TAG, "Error occured", "generated error");
                AndroidLogger.d(TAG, "sssssssss");
            }
        });
        zipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidLogger.zipLog();
            }
        });
        renameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidLogger.setFilename("newFile.dat");
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AndroidLogger.sendLog(getApplicationContext(), new String[]{"goldenhead777@gmail.com"}, "Logs");
            }
        });
    }


    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
            }
            return;
        }
    }

}
