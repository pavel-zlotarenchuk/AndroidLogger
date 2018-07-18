package gmoby.android.logger;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AndroidLogger {

    private static final int BUFFER_SIZE = 1024;

    private static boolean clearOnFilling;
    private static long maxSize = 5;
    private static String PATH = "";
    private static File file;
    private static File directory;
    private static String appFolderName = BuildConfig.APPLICATION_ID;
    private static String filename = "log.txt";
    private static String zipFilename = "/logZip.zip";
    private static AndroidLogger instance;
    private static String[] emails;

    public AndroidLogger(String emails[], long sizeFileInMB) {
        this.emails = emails;
        this.maxSize = sizeFileInMB;
    }

    public static void activateLogger(Context context, String emails[], long sizeFileInMB) {
        if (instance == null) {
            instance = new AndroidLogger(emails, sizeFileInMB);
        }

        if (instance.checkPermissions(context)) {
            System.out.println("AndroidLogger: Save logs allow");
        } else {
            System.out.println("AndroidLogger: Save logs is not allowed");
        }

        configDir();
        file = new File(directory, filename);

        Log.d("Path save logs: ", PATH);
    }

    public static void log(String tag, String msg) {
        instance.saveToFile(tag + ": " + msg);
    }

    public static void v(String msg) {
        instance.saveToFile("Verbose: " + msg);
    }

    public static void d(String msg) {
        instance.saveToFile("Debug: " + msg);
    }

    public static void i(String msg) {
        instance.saveToFile("Info: " + msg);
    }

    public static void w(String msg) {
        instance.saveToFile("Warn: " + msg);
    }

    public static void e(String msg) {
        instance.saveToFile("Error: " + msg);
    }

    private void saveToFile(String stringLog) {
        Log.d("AndroidLogger", stringLog);
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            if ((file.length() > (maxSize * maxSize)) && clearOnFilling) {
                System.out.println("size " + String.valueOf((double) file.length() / (maxSize * maxSize)) + "Mb");
                new FileOutputStream(file);
            }
            String timeLog = new SimpleDateFormat("dd.MM.yy hh:mm:ss").format(new Date());
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            String line = timeLog + ": " + stringLog + "\n";
            bw.append(line);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static File zipLog() {
        configDir();
        file = new File(directory, filename);
        return zip(directory + "/" + filename, directory + zipFilename);
    }

    private static File zip(String file, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[BUFFER_SIZE];

            FileInputStream fi = new FileInputStream(file);
            origin = new BufferedInputStream(fi, BUFFER_SIZE);

            ZipEntry entry = new ZipEntry(file.substring(file.lastIndexOf("/") + 1));
            out.putNextEntry(entry);
            int count;

            while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new File(zipFileName);
    }

    public static String getFilename() {
        return filename;
    }

    public static void setFilename(String filename) {
        configDir();
        File oldFile = new File(directory, AndroidLogger.filename);
        File latestname = new File(directory, filename);
        oldFile.renameTo(latestname);
        AndroidLogger.filename = filename;
    }

    public static long getMaxSize() {
        return maxSize;
    }

    public static void setMaxSize(long maxSize) {
        AndroidLogger.maxSize = maxSize;
    }

    public static boolean isClearOnFilling() {
        return clearOnFilling;
    }

    public static void setClearOnFilling(boolean clearOnFilling) {
        AndroidLogger.clearOnFilling = clearOnFilling;
    }

    //setting up directory
    private static void configDir() {
        PATH = Environment.getExternalStorageDirectory().getPath() + "/" + appFolderName + "/";
        directory = new File(PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    //sending log by email
    public static void sendLog(Context context) {
        configDir();
        File file = zipLog();

        Uri path = Uri.fromFile(file);
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        // set the type to 'email'
        emailIntent.setType("vnd.android.cursor.dir/email");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emails);
        // the attachment
        if (file.exists()) {
            emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        }
        // the mail subject
        String appName = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, appName + " " + BuildConfig.VERSION_NAME);
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private static void removeFirstLine(String fileName) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
        //Initial write position
        long writePosition = randomAccessFile.getFilePointer();
        randomAccessFile.readLine();
        // Shift the next lines upwards.
        long readPosition = randomAccessFile.getFilePointer();

        byte[] buff = new byte[1024];
        int n;
        while (-1 != (n = randomAccessFile.read(buff))) {
            randomAccessFile.seek(writePosition);
            randomAccessFile.write(buff, 0, n);
            readPosition += n;
            writePosition += n;
            randomAccessFile.seek(readPosition);
        }
        randomAccessFile.setLength(writePosition);
        randomAccessFile.close();
    }

    public static boolean clearLog() {
        boolean result = false;
        configDir();
        file = new File(directory, filename);
        if (file.exists()) {
            result = file.delete();
        }
        return result;
    }

    private boolean checkPermissions(Context context) {
        String[] permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
        };

        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }
}