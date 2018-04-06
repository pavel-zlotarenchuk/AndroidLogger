package gmoby.android.logger;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
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
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by viktord1985 on 3/30/18.
 */

public class AndroidLogger {

    private static final int BUFFER_SIZE = 1024;

    private static boolean isZipable = true;
    private static boolean clearOnFilling = true;
    private static long maxSize = 50;
    private static String PATH = "";
    private static File file;
    private static File directory;
    private static boolean isEnabled = true;
    private static String appFolderName = BuildConfig.APPLICATION_ID;
    private static String filename = "log.dat";
    private static String zipFilename = "/logZip.zip";


    public static int d(String tag, String msg) {
        int result = 0;
        if (isEnabled) {
            configDir();
            file = new File(directory, filename);
            Log.d(tag, PATH);

            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                Log.d(tag, "size " + String.valueOf((double) file.length()));

                if (file.length() > (maxSize * maxSize)) {
                    Log.d(tag, "size " + String.valueOf((double) file.length() / (maxSize * maxSize)) + "Mb");
                    if (clearOnFilling) {
                        new FileOutputStream(file);
                    } else {
                        removeFirstLine(file.getAbsolutePath());
                    }
                }
                String timeLog = new SimpleDateFormat("dd.MM.yy hh:mm:ss").format(new Date());
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
                String line = timeLog + " (" + tag + ")\t" + msg + "\n";
                bw.append(line);
                bw.close();
                result = 1;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return result;
    }

    //log errors
    public static int e(String tag, String msg, String error) {

        int result = 0;
        configDir();
        file = new File(directory, filename);
        Log.d(tag, PATH);

        try {
            if (!file.exists()) {
                file.createNewFile();

            }
            if ((file.length() > (maxSize * maxSize)) && clearOnFilling) {
                Log.d(tag, "size " + String.valueOf((double) file.length() / (maxSize * maxSize)) + "Mb");
                new FileOutputStream(file);
            }
            String timeLog = new SimpleDateFormat("dd.MM.yy hh:mm:ss").format(new Date());
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            String line = timeLog + " (" + tag + ")\t" + msg + "\t" + "[Error]" + error + "\n";
            bw.append(line);
            bw.close();
            result = 1;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static File zipLog() {
        if (isZipable) {
            configDir();
            file = new File(directory, filename);
            return zip(directory + "/" + filename, directory + zipFilename);
        }
        return null;
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

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static void setEnabled(boolean isEnabled) {
        AndroidLogger.isEnabled = isEnabled;
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

    public static boolean isZipable() {
        return isZipable;
    }

    public static void setZipable(boolean isZipable) {
        AndroidLogger.isZipable = isZipable;
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
    public static void sendLog(Context context, String emails[]) {
        configDir();
        File file = null;
        if (isZipable) {
            file = new File(directory, zipFilename);
        } else {
            file = new File(directory, filename);
        }
        Log.d("ss", file.getAbsolutePath());
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
        String appName = BuildConfig.APPLICATION_ID.substring(BuildConfig.APPLICATION_ID.lastIndexOf(".") + 1);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, appName + " " + BuildConfig.VERSION_NAME + " Log report");
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private static void removeFirstLine(String fileName) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
        //Initial write position
        long writePosition = raf.getFilePointer();
        raf.readLine();
        // Shift the next lines upwards.
        long readPosition = raf.getFilePointer();

        byte[] buff = new byte[1024];
        int n;
        while (-1 != (n = raf.read(buff))) {
            raf.seek(writePosition);
            raf.write(buff, 0, n);
            readPosition += n;
            writePosition += n;
            raf.seek(readPosition);
        }
        raf.setLength(writePosition);
        raf.close();
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

}
