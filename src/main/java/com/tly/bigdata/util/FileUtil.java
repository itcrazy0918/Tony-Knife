package com.tly.bigdata.util;

import com.tly.bigdata.component.PropertiesWrapper;
import com.tly.bigdata.exception.CommonRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * <pre>
 * 文件操作辅助工具。
 * </pre>
 * @author tly  1170382650@qq.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:    修改人：  修改日期:     修改内容:
 * </pre>
 */
public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 创建一个 Dir File, 并验证是否 Dir
     * 
     * @param pathname
     * @param errMsg
     * @return
     */
    public static File createDir(String pathname, String errMsg) {
        File dir = new File(pathname);
        if (!dir.isDirectory()) {
            throw new CommonRuntimeException(errMsg);
        }
        return dir;
    }

    /**
     * 确保 filename所在的目录 是一个合法的目录
     * 
     * @param filename
     * @return filename 对应的File对象
     */
    public static File ensureValidDirOfFilename(String filename) {
        File file = new File(filename);
        ensureValidDir(file.getParent());
        return file;
    }

    /**
     * 确保 path 是一个合法的目录
     * 
     * @param path
     * @return path 对应的File对象
     */
    public static File ensureValidDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.isDirectory()) {
            String msg = String.format("ensureValidDirPath.path=%s be not directory", path);
            throw new CommonRuntimeException(msg);
        }
        return dir;
    }

    /**
     * 在 classes 下面读取一个文件的 所有内容
     * 
     * @param filename
     * @param excludeNewLine
     *            是否排除换行符(\n)
     * @return
     */
    public static String loadString(String filename, boolean excludeNewLine) {
        StringBuilder text = new StringBuilder(1024 * 8);

        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        try {
            is = loadStream(filename);
            isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);

            String line = null;
            while ((line = reader.readLine()) != null) {
                text.append(line);
                if (!excludeNewLine) {
                    text.append("\n");
                }
            }

            return text.toString();
        }
        catch (IOException e) {
            throw new CommonRuntimeException(e, "FileUtil.loadString: " + e.getMessage());
        }
        finally {
            close(reader, isr, is);
        }
    }

    /**
     * 在 classes 下面读取一个文件的 PropertiesWrapper
     * 
     * @param filename
     * @return
     */
    public static PropertiesWrapper loadProperties(String filename) {
        Properties p = new Properties();

        InputStream is = null;
        try {
            is = loadStream(filename);
            p.load(is);
        }
        catch (IOException e) {
            throw new CommonRuntimeException(e, "FileUtil.loadProperties: " + e.getMessage());
        }
        finally {
            close(is);
        }

        return new PropertiesWrapper(p);
    }

    /**
     * 在 classes 下面读取一个文件的 InputStream
     * 
     * @param filename
     * @return
     */
    public static InputStream loadStream(String filename) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        URL url = loader.getResource(filename);
        if (url == null) {
            throw new CommonRuntimeException("FileUtil.loadStream: '" + filename + "' can not be found.");
        }

        try {
            return url.openStream();
        }
        catch (IOException e) {
            throw new CommonRuntimeException(e, "FileUtil.loadStream: " + e.getMessage());
        }
    }

    /**
     * 把一个文件分成几等分 (按照行数)
     * 
     * @param count 需要分成几等分
     * @throws Exception
     */
    public static void splitFileLine(String filePath, int count) {
        FileReader read = null;
        BufferedReader br = null;
        try {
            read = new FileReader(filePath);
            br = new BufferedReader(read);
            String row;
            List<FileWriter> flist = new ArrayList<FileWriter>();
            for (int i = 1; i <= count; i++) {
                flist.add(new FileWriter(filePath + "_" + i));
            }
            int rownum = 1;// 计数器
            while ((row = br.readLine()) != null) {
                flist.get(rownum % count).append(row + "\r\n");
                rownum++;
            }
            for (int i = 0; i < flist.size(); i++) {
                close(flist.get(i));
            }
        }
        catch (FileNotFoundException e) {
            throw new CommonRuntimeException(e, "FileUtil.splitFileLine: " + e.getMessage());
        }
        catch (IOException e) {
            throw new CommonRuntimeException(e, "FileUtil.splitFileLine: " + e.getMessage());
        }
        finally {
            close(br, read);
        }
    }

    /**
     * 批量关闭 java.io.Closeable
     * 
     * @param objs
     */
    public static void close(Closeable... objs) {
        for (Closeable obj : objs) {
            if (obj != null) {
                try {
                    obj.close();
                }
                catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public static void main(String[] args) {
        ensureValidDirOfFilename("/data/abc/d/123.txt");

        System.out.println("Done");
    }
}
