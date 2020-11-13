package com.sample.conn.ftp;

import com.sample.conn.ftp.utils.FtpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

/**
 * @author winnie
 * @date 2020/10/30
 */
@Slf4j
public class FtpTest {

    public static void main(String[] args) throws ParseException {
        try {
            // 2. ftp 文件保存到本地
            getFtpFile();
            // 3. 本地文件保存到 ftp 服务器
            toFtpFile();
            // 4. 删除 ftp 文件
            deleteFtpFile();
            // 5. 递归循环调用 ftp 所有文件保存到本地
            FTPClient ftpClient = initFtp();
            getFtpAllFiles(ftpClient);
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 1. 初始化 ftp 连接
     *
     * @return FTPClient 对象
     */
    private static FTPClient initFtp() throws IOException {
        String hostname = "192.168.56.1";
        int port = 21;
        String username = "admin";
        String password = "admin";
        FTPClient ftpClient = FtpUtils.initFtpClient(hostname, port, username, password);
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.enterLocalPassiveMode();
        return ftpClient;
    }

    /**
     * 2. 读取 ftp 服务器上的文件，保存到本地
     * 只是当前目录下的文件和文件夹，不读取子文件夹的文件
     */
    private static void getFtpFile() throws IOException {
        // 初始化 ftp 连接
        FTPClient ftpClient = initFtp();
        // 获取根目录下所有文件和文件夹
        FTPFile[] ftpFiles = ftpClient.listFiles();
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isDirectory()) {
                log.info("读取到文件夹：" + ftpFile.getName());
                // 进入 ftpFile 文件目录（默认在根目录下）
                ftpClient.changeWorkingDirectory(ftpFile.getName());
                // 进入上一级目录
                ftpClient.changeToParentDirectory();
                // 本地建立 ftpFile (多级)文件夹
                String localPath = "D:/hand/local/" + ftpFile.getName();
                File file = new File(localPath);
                if (!file.exists()) {
                    boolean b = file.mkdirs();
                    if (b) {
                        log.info("目录 " + localPath + " 创建成功！");
                    }
                }
            } else {
                // 读取的文件保存到本地
                String local = "D:/hand/local/" + ftpFile.getName();
                OutputStream outputStream = new FileOutputStream(local);
                ftpClient.retrieveFile(ftpFile.getName(), outputStream);
                outputStream.flush();
                outputStream.close();
                ftpClient.enterLocalPassiveMode();
                log.info("读取到文件：" + ftpFile.getName() + "，并保存到本地:" + local);
            }
        }
        // 关闭 ftp 连接
        if (ftpClient.isConnected()) {
            ftpClient.disconnect();
        }
    }

    /**
     * 3. 本地文件保存到 ftp 服务器
     */
    private static void toFtpFile() throws IOException {
        // 读取本地文件
        String localFilePath = "D:/hand/local/local.txt";
        File file = new File(localFilePath);
        InputStream in = new FileInputStream(file);
        byte[] bytes = new byte[(int) file.length()];
        int len = in.read(bytes);
        in.close();
        log.info("本地文件长度：" + len);

        // 初始化 ftp 连接
        FTPClient ftpClient = initFtp();
        // 指定上传文件的类型 二进制文件（windows 设置为 GBK，Linux 设置为 UTF-8）
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        ftpClient.setBufferSize(1024);
        ftpClient.setControlEncoding("GBK");
        // 每次数据连接之前，ftp client告诉 ftp server开通一个端口来传输数据
        ftpClient.enterLocalPassiveMode();
        // 进入 ftp 目录（目录不存在时创建）
        String ftpPath = "/local";
        if (!ftpClient.changeWorkingDirectory(ftpPath)) {
            if (!ftpClient.makeDirectory(ftpPath)) {
                log.info("ftp 创建目录: " + ftpPath + " 失败!");
                return;
            }
            if (!ftpClient.changeWorkingDirectory(ftpPath)) {
                log.info("ftp 切换目录: " + ftpPath + " 失败!");
                return;
            } else {
                log.info("ftp 切换目录: " + ftpPath + " 成功!");
            }
        } else {
            log.info("ftp 切换目录: " + ftpPath + " 成功!");
        }
        log.info("ftp 当前目录: " + ftpClient.printWorkingDirectory());

        // 本地文件保存到 ftp 服务器
        ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
        String fileName = new String((file.getName()).getBytes("GBK"), StandardCharsets.ISO_8859_1);
        boolean up = ftpClient.storeFile(fileName, bin);
        bin.close();
        if (up) {
            log.info("保存成功！");
        } else {
            log.error("保存失败！");
        }

        // 关闭 ftp 连接
        if (ftpClient.isConnected()) {
            ftpClient.disconnect();
        }
    }

    /**
     * 4. 删除 ftp 服务器的文件
     */
    private static void deleteFtpFile() throws IOException {
        // 初始化 ftp 连接
        FTPClient ftpClient = initFtp();

        String pathname = "local";
        String filename = "local.txt";

        // 进入需删除的文件目录
        ftpClient.changeWorkingDirectory(pathname);
        ftpClient.dele(filename);
        ftpClient.logout();

        // 关闭 ftp 连接
        if (ftpClient.isConnected()) {
            ftpClient.disconnect();
        }
    }

    /**
     * 5. 定义本地文件夹根目录
     */
    private static String local = "D:/hand/local";
    /**
     * 5. 递归循环读取 ftp 当前目录下的所有文件，保存到本地
     *
     * @param ftpClient FTPClient 对象
     */
    private static void getFtpAllFiles(FTPClient ftpClient) throws IOException {
        // 获取根目录下所有文件和文件夹
        FTPFile[] ftpFiles = ftpClient.listFiles();
        for (FTPFile ftpFile : ftpFiles) {
            if (StringUtils.isBlank(local)) {
                log.info("请设置本地文件夹根目录！");
                return;
            }
            if (ftpFile.isDirectory()) {
                // 进入 ftpFile 文件夹
                log.info("读取到文件夹：" + ftpFile.getName());
                ftpClient.changeWorkingDirectory(ftpFile.getName());
                // 本地建立 ftpFile (多级)文件夹
                local += "/" + ftpFile.getName();
                File file = new File(local);
                if (!file.exists()) {
                    boolean b = file.mkdirs();
                    if (b) {
                        log.info("目录 " + local + " 创建成功！");
                    }
                }
                // 递归调用
                getFtpAllFiles(ftpClient);
            } else {
                // 读取的文件保存到本地
                String localFile = local + "/" + ftpFile.getName();
                OutputStream outputStream = new FileOutputStream(localFile);
                ftpClient.retrieveFile(ftpFile.getName(), outputStream);
                outputStream.flush();
                outputStream.close();
                ftpClient.enterLocalPassiveMode();
                log.info("读取到文件：" + ftpFile.getName() + "，并保存到本地:" + local);
            }
        }
        // 返回上级目录（ ftp 和本地）
        ftpClient.changeToParentDirectory();
        local = local.substring(0, local.lastIndexOf("/"));
        log.info("ftp 切换文件夹：" + ftpClient.printWorkingDirectory());
        log.info("本地切换文件夹：" + local);
    }

}
