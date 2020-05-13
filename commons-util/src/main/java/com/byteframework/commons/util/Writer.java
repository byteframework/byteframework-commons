package com.byteframework.commons.util;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Writer {
    public static String writing(String url, String resp) {
        String htPath = "D:/htDatas/";
        BufferedWriter output = null;
        String fileName = getFileName(url);
        try {

            if (StringUtils.isBlank(fileName)) {
                System.out.println("文件名为空");
                return null;
            }
            File file = new File(htPath + fileName);
            output = new BufferedWriter(new FileWriter(file));
            output.write(resp);
        } catch (IOException e) {
            System.out.println("写入文件出错：" + e);
        } finally {
            if (output != null)
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return fileName;
    }

    private static String getFileName(String url) {
        if (StringUtils.isBlank(url)) return null;
        String fileName = "";
        if (url.indexOf("?") > 0) {
            fileName = url.substring(url.indexOf("?") + 1, url.length());
            fileName = MD5Util.md5(fileName) + ".txt";
        }
        return fileName;
    }


}
