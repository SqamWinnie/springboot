package com.sample.conn.ftp.utils;

import org.apache.commons.net.ftp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * @author *
 */
public class FtpUtils {
    private static final Logger logger = LoggerFactory.getLogger(FtpUtils.class);

    public static FTPClient ftpClient = null;

    public static String FILE_SPLIT = "/";

    /**
     * 初始化 ftp 连接信息
     */
    public static FTPClient initFtpClient(String hostname, Integer port, String username, String password) {
        logger.info("connecting ftp:" + hostname + ":" + port);
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        try {
            ftpClient.setConnectTimeout(100 * 60 * 1000);
            //连接 ftp 服务器
            ftpClient.connect(hostname, port);
            //登录 ftp 服务器
            ftpClient.login(username, password);
            //是否成功登录服务器
            int replyCode = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                logger.error("error ftp:" + hostname + ":" + port);
            } else {
                logger.info("succeed ftp:" + hostname + ":" + port);
            }
        } catch (IOException e) {
            logger.error("error " + e.getMessage());
            e.printStackTrace();
        }
        return ftpClient;
    }

    /**
     * 初始化 ftps 连接信息
     */
    public static FTPSClient ftpConnection(String host, int port, String userName, String passWord) {
        FTPSClient ftpsClient = null;
        try {
            ftpsClient = new FTPSClient("TLS", true);
            //连接 ftps 服务器
            ftpsClient.connect(host, port);
            //登录 ftps 服务器
            ftpsClient.login(userName, passWord);
            ftpsClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpsClient.setDataTimeout(18000);
            ftpsClient.execPROT("P");
            ftpsClient.enterLocalPassiveMode();
            logger.info("FTP_SERVICE_ftpConnection: ftp connection success");
        } catch (Exception e) {
            logger.error("FTP_SERVICE_ftpConnection: ftp connection Exception:", e);
            e.printStackTrace();
        }
        return ftpsClient;
    }

    /**
     * 断开 ftp 连接
     */
    public static void closeFtp(FTPClient ftpClient) {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                logger.info("ftp断开连接...");
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 断开 ftps 连接
     */
    public static void closeFtp(FTPSClient ftpsClient) {
        if (ftpsClient != null && ftpsClient.isConnected()) {
            try {
                logger.info("ftps断开连接...");
                ftpsClient.logout();
                ftpsClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 导出到目标FTP服务器
     *
     * @param bytes     文件内容
     * @param filename  文件名
     * @param path      目标路径（FTP 服务器）
     * @param ftpClient ftp对象
     */
    public static void outToFile(byte[] bytes, String filename, String path, FTPSClient ftpClient) {
        try {
            boolean flag = ftpClient.changeWorkingDirectory(path);
            if (!flag) {
                createDirectory(ftpClient, path);
            }
            // 指定上传路径
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(path);
            // 指定上传文件的类型 二进制文件
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("GBK");

            ByteArrayInputStream in = new ByteArrayInputStream(bytes);

            String name = new String((filename).getBytes("GBK"), StandardCharsets.ISO_8859_1);

            ftpClient.storeFile(name, in);

            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != ftpClient) {
                closeFtp(ftpClient);
            }
        }
    }

    /**
     * 导出到目标FTP服务器
     *
     * @param bytes     文件内容
     * @param filename  文件名
     * @param path      目标路径（FTP 服务器）
     * @param ftpClient ftp对象
     */
    public static void outToFile(byte[] bytes, String filename, String path, FTPClient ftpClient) {
        try {

            boolean flag = ftpClient.changeWorkingDirectory(path);
            if (!flag) {
                createDirectory(ftpClient, path);
            }
            // 指定上传路径
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(path);
            // 指定上传文件的类型 二进制文件
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("GBK");

            ByteArrayInputStream in = new ByteArrayInputStream(bytes);

            String name = new String((filename).getBytes("GBK"), StandardCharsets.ISO_8859_1);

            ftpClient.storeFile(name, in);

            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != ftpClient) {
                closeFtp(ftpClient);
            }
        }
    }

    /**
     * 导出到目标 FTP 服务器
     *
     * @param file      文件
     * @param path      目标路径（FTP 服务器）
     * @param ftpClient ftp对象
     */
    public static void outToFile(File file, String path, FTPClient ftpClient) {
        try {
            boolean flag = ftpClient.changeWorkingDirectory(path);
            if (!flag) {
                createDirectory(ftpClient, path);
            }
            // 指定上传路径
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(path);
            // 指定上传文件的类型 二进制文件
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("GBK");

            FileInputStream fis = new FileInputStream(file);
            logger.info("开始上传文件" + file.getName());
            ftpClient.storeFile(file.getName(), fis);
            logger.info("文件上传成功");
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != ftpClient) {
                closeFtp(ftpClient);
            }
        }
    }

    /**
     * 切换到文件路径（ftp 服务器上）
     *
     * @param ftpClient ftp 对象
     * @param directory 文件路径
     * @return 是否切换成功
     */
    public static boolean changeWorkingDirectory(FTPClient ftpClient, String directory) {
        boolean flag = true;
        try {
            flag = ftpClient.changeWorkingDirectory(directory);
            if (flag) {
                logger.debug("进入文件夹" + directory + " 成功！");

            } else {
                logger.debug("进入文件夹" + directory + " 失败！");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return flag;
    }

    /**
     * 创建文件目录（ftp 服务器上）
     *
     * @param ftpClient ftp对象
     * @param dir       目录
     * @return 是否创建成功
     */
    public static boolean makeDirectory(FTPClient ftpClient, String dir) {
        boolean flag = true;
        try {
            flag = ftpClient.makeDirectory(dir);
            if (flag) {
                logger.debug("创建文件夹" + dir + " 成功！");
            } else {
                logger.debug("创建文件夹" + dir + " 失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 创建多层目录文件，如果有 ftp 服务器已存在该文件，则不创建，如果无，则创建
     *
     * @param ftpClient ftp对象
     * @param remote    创建的文件目录（ftp 服务器上）
     * @throws IOException 异常
     */
    public static void createDirectory(FTPClient ftpClient, String remote) throws IOException {
        String directory = remote + FILE_SPLIT;
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase(FILE_SPLIT) && !changeWorkingDirectory(ftpClient, new String(directory))) {
            int start = 0;
            int end = 0;
            if (directory.startsWith(FILE_SPLIT)) {
                start = 1;
            }
            end = directory.indexOf(FILE_SPLIT, start);
            String path = "";
            StringBuilder paths = new StringBuilder();
            do {
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), StandardCharsets.ISO_8859_1);
                path = path + FILE_SPLIT + subDirectory;
                if (!existFile(ftpClient, path)) {
                    if (makeDirectory(ftpClient, subDirectory)) {
                        changeWorkingDirectory(ftpClient, subDirectory);
                    } else {
                        logger.debug("创建目录[" + subDirectory + "]失败");
                        changeWorkingDirectory(ftpClient, subDirectory);
                    }
                } else {
                    changeWorkingDirectory(ftpClient, subDirectory);
                }
                paths.append(FILE_SPLIT).append(subDirectory);
                start = end + 1;
                end = directory.indexOf(FILE_SPLIT, start);
                // 检查所有目录是否创建完毕
            } while (end > start);
            logger.info(String.valueOf(paths));
        }
    }

    /**
     * 创建多层目录文件，如果 ftp 服务器已存在该文件，则不创建，如果无，则创建
     *
     * @param ftpClient ftp对象
     * @param remote    创建的文件目录（ftp 服务器上）
     * @throws IOException 异常
     */
    public static void createDirectory(FTPSClient ftpClient, String remote) throws IOException {
        String directory = remote + FILE_SPLIT;
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase(FILE_SPLIT) && !changeWorkingDirectory(ftpClient, new String(directory))) {
            int start = 0;
            int end = 0;
            if (directory.startsWith(FILE_SPLIT)) {
                start = 1;
            }
            end = directory.indexOf(FILE_SPLIT, start);
            String path = "";
            StringBuilder paths = new StringBuilder();
            do {
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), StandardCharsets.ISO_8859_1);
                path = path + FILE_SPLIT + subDirectory;
                if (!existFile(ftpClient, path)) {
                    if (makeDirectory(ftpClient, subDirectory)) {
                        changeWorkingDirectory(ftpClient, subDirectory);
                    } else {
                        logger.debug("创建目录[" + subDirectory + "]失败");
                        changeWorkingDirectory(ftpClient, subDirectory);
                    }
                } else {
                    changeWorkingDirectory(ftpClient, subDirectory);
                }

                paths.append(FILE_SPLIT).append(subDirectory);
                start = end + 1;
                end = directory.indexOf(FILE_SPLIT, start);
                // 检查所有目录是否创建完毕
            } while (end > start);
            logger.info(String.valueOf(paths));
        }
    }

    /**
     * 判断 ftp 服务器文件是否存在
     *
     * @param ftpClient ftp对象
     * @param path      文件目录（ftp 服务器上）
     * @return 文件是否存在
     * @throws IOException 异常
     */
    public static boolean existFile(FTPSClient ftpClient, String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 判断 ftp 服务器文件是否存在
     *
     * @param ftpClient ftp对象
     * @param path      文件目录（ftp 服务器上）
     * @return 文件是否存在
     * @throws IOException 异常
     */
    public static boolean existFile(FTPClient ftpClient, String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    /**
     * 删除 ftp 服务器文件
     *
     * @param ftpClient ftp对象
     * @param pathname  文件目录
     * @param filename  文件名
     * @return 是否删除成功
     */
    public static boolean deleteFile(FTPClient ftpClient, String pathname, String filename) {
        boolean flag = false;
        try {
            System.out.println("开始删除文件");
            // 切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.dele(filename);
            ftpClient.logout();
            flag = true;
            System.out.println("删除文件成功");
        } catch (Exception e) {
            System.out.println("删除文件失败");
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除 ftp 服务器文件
     *
     * @param ftpClient ftp对象
     * @param pathname  文件目录
     * @param filename  文件名
     * @return 是否删除成功
     */
    public static boolean deleteFile(FTPSClient ftpClient, String pathname, String filename) {
        boolean flag = false;
        try {
            System.out.println("开始删除文件");
            // 切换FTP目录
            ftpClient.changeWorkingDirectory(pathname);
            ftpClient.dele(filename);
            ftpClient.logout();
            flag = true;
            System.out.println("删除文件成功");
        } catch (Exception e) {
            System.out.println("删除文件失败");
            e.printStackTrace();
        }
        return flag;
    }


}
