package com.sample.conn.file.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @date 2020/11/9
 */
@Slf4j
public class FileUtil {

    /**
     * 删除文件
     * @param filePath 文件路径
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                boolean delete = file.delete();
                if (delete) {
                    log.info("=========== 删除文件成功！路径：" + filePath + " =============");
                } else {
                    log.info("=========== 删除文件失败！路径：" + filePath + " =============");
                }
            } catch (Exception e) {
                log.error("=========== 删除文件失败！路径：" + filePath + " =============");
                e.printStackTrace();
            }
        } else {
            log.info("=========== 删除文件失败！文件路径不存在：" + filePath + " =============");
        }
    }

}
