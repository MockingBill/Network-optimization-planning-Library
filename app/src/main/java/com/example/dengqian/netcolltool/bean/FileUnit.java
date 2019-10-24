package com.example.dengqian.netcolltool.bean;

import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;

public class FileUnit {

    public static void writeTxtToFile(String strcontent, String filePath, String fileName) throws Exception{
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        File file = new File(strFilePath);
        if (!file.exists()) {
            Log.d("TestFile", "Create the file:" + strFilePath);
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
        raf.seek(file.length());
        raf.write(strContent.getBytes());
        raf.close();

    }

    // 生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    //删除指定txt文件   通过路径
    public void deleteFile(String filePath, String fileName) {
        File f = new File(filePath + fileName);  // 输入要删除的文件位置
        if (f.exists()) {
            f.delete();
        }

    }
}
