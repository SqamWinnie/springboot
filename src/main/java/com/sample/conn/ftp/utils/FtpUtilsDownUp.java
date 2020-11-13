package com.sample.conn.ftp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class FtpUtilsDownUp {

    private static final Logger log = LoggerFactory.getLogger(FtpUtilsDownUp.class);

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    /**
     * 初始化构造函数
     * @param host ip
     * @param port 端口号
     * @param username 用户名
     * @param password 密码
     */
    public FtpUtilsDownUp(String host, String port, String username, String password) {
        this.host = host;
        this.port = Integer.parseInt(port);
        this.username = username;
        this.password = password;
    }

    /**
     * 下载文件
     * @param remotePath 文件路径（ftp 服务器）
     * @param remoteFileName 文件名称（ftp 服务器）
     * @param localPath 本地文件路径
     * @param localFileName 本地文件名称
     * @return 文件
     */
    public File downloadFile(String remotePath, String remoteFileName, String localPath, String localFileName) {
        FTPClient ftpClient = new FTPClient();
        FileOutputStream outputStream = null;
        File file = null;
        try {
            log.info(">>>>>>>>FTP-->downloadFile--登录开始>>>>>>>>>>>>>");

            ftpClient.connect(host, port);
            //设置ftp连接模式 被动模式
            ftpClient.enterLocalPassiveMode();

            boolean login = ftpClient.login(username, password);

            if (login) {
                log.info(">>>>>>>>FTP-->downloadFile--登录成功>>>>>>>>>>>>>");
            } else {
                log.info(">>>>>>>>FTP-->downloadFile--登录失败>>>>>>>>>>>>>");
                throw new RuntimeException("FTP登陆失败,请检查用户信息！");
            }

            boolean isDownload = false;

            file = new File(localPath + localFileName);
            if (file.exists()) {
                boolean delete = file.delete();
                log.info(">>>>>>>>file delete: " + delete + ">>>>>>>>>>>>>");
            }
            boolean newFile = file.createNewFile();
            log.info(">>>>>>>>file create new file: " + newFile + ">>>>>>>>>>>>>");

            //切换文件路径
            ftpClient.makeDirectory(remotePath);
            ftpClient.changeWorkingDirectory(remotePath);

            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpFile.getName().equals(remoteFileName)) {
                    outputStream = new FileOutputStream(file);
                    isDownload = ftpClient.retrieveFile(ftpFile.getName(), outputStream);
                }
            }

            if (isDownload) {
                log.info(">>>>>>>>FTP-->downloadFile--文件下载成功！本地路径：" + file);
            } else {
                throw new RuntimeException("FTP-->downloadFile--文件下载失败！请检查！");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 上传文件
     * @param remotePath 文件路径（ftp 服务器）
     * @param remoteFileName 文件名称（ftp 服务器）
     * @param localPath 本地文件路径
     * @param localFileName 本地文件名称
     */
    public void uploadFile(String remotePath, String remoteFileName, String localPath, String localFileName) {
        FTPClient ftpClient = new FTPClient();
        FileInputStream inputStream = null;
        log.info(">>>>>>>>FTP-->uploadFile--登录开始>>>>>>>>>>>>>");

        ftpClient.enterLocalPassiveMode();//设置成被动FTP模式
        try {
            boolean login = ftpClient.login(username, password);
            if (login) {
                log.info(">>>>>>>>FTP-->uploadFile--登录成功>>>>>>>>>>>>>");
            } else {
                log.info(">>>>>>>>FTP-->uploadFile--登录失败>>>>>>>>>>>>>");
                throw new RuntimeException("FTP登陆失败,请检查用户信息！");
            }

            //切换文件路径
            ftpClient.makeDirectory(remotePath);
            ftpClient.changeWorkingDirectory(remotePath);
            inputStream = new FileInputStream(new File(localPath + localFileName));
            //可上传多文件
            boolean isUpload = ftpClient.storeFile(remoteFileName, inputStream);

            if (isUpload) {
                log.info(">>>>>>>>FTP-->uploadFile--文件上传成功!");
            } else {
                log.info(">>>>>>>>FTP-->uploadFile--文件上传失败!");
                throw new RuntimeException("文件上传失败!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}