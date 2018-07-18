package gmoby.android.logger.androidlogger;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import gmoby.android.logger.AndroidLogger;

public class MainActivity extends AppCompatActivity {
    int count = 0;
    Button button;
    Button renameButton;
    Button sendButton;
    final String TAG = "logTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.log_button);
        renameButton = findViewById(R.id.renameLog);
        sendButton = findViewById(R.id.sendLog);
        AndroidLogger.activateLogger(this, new String[]{"exampleemail@gmail.com"}, 5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count++;
                AndroidLogger.log("tag", "message");
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
                AndroidLogger.sendLog(MainActivity.this);
            }
        });
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
