package com.byteframework.commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ffmpeg工具类
 *
 * <p>
 * 封装调用Ffmpeg进程的方法
 * 进程的输出信息一定要读出（由convertStreamToString处理）
 * </p>
 *
 * @author sa
 * @date 2019-10-24
 */
public class Ffmpeg {
    Logger logger = LoggerFactory.getLogger(Ffmpeg.class);

    public static Map<String, Process> processMap = new HashMap<>();

    /**
     * 调用ffmpeg进程，执行命令
     *
     * @param command
     */
    public void exec(List<String> command) {
        if (CollectionUtils.isEmpty(command)) {
            return;
        }
        String key = "";
        for (String s : command) {
            key += s;
        }
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        InputStream in = null;
        Process process = null;
        try {
            builder.redirectErrorStream(true);
            process = builder.start();
            processMap.put(key, process);
            in = process.getInputStream();
            logger.info("调用ffmpeg进程，执行：" + command);
            convertStreamToString(in);
            InputStream errorStream = process.getErrorStream();
            if (errorStream != null && errorStream.read() > 0) {
                logger.error("执行ffmpeg命令出现异常：");
                convertStreamToString(errorStream);
            }

        } catch (IOException e) {
            logger.error("ffmpeg异常信息：" + e.getMessage());
        } finally {
            try {
                in.close();
                process.destroy();
                for (String s : processMap.keySet()) {
                    System.out.println("processMap -> key: " + s + " -> value: " + processMap.get(s));
                }
                processMap.remove(key);
                System.out.println("processMap -> size :" + processMap.size());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * 读取流的输出信息
     *
     * @param is
     * @return
     */
    private void convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

