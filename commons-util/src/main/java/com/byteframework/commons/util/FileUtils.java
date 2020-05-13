package com.byteframework.commons.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class FileUtils {

    public static String generateFilePath() {
        Date currDate = new Date();
        //路径以当前日期为基准，格式：yyyy/yyyyMMdd
        String path = DateUtils.format(currDate, "yyyy") + "/" + DateUtils.format(currDate, "yyyyMMdd");
        return path;
    }

    public static void copyFile(File src, File dest) throws IOException {
        FileInputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(dest);
        int c;
        byte buffer[] = new byte[1024];
        while ((c = in.read(buffer)) != -1) {
            out.write(buffer, 0, c);
        }
        in.close();
        out.close();
    }

    public static void copyFile(String src, String dest) throws IOException {
        copyFile(new File(src), new File(dest));
    }

    public static String getFileName() {
        //文件名用UUID
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        return uuid + ".jpg";
    }


    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        //目录此时为空，可以删除
        return dir.delete();
    }
}
