package com.sample.conn.file;

import com.sample.conn.file.util.ExcelToPdf;
import com.sample.conn.file.util.FileUtil;
import com.sample.conn.file.util.PdfToPng;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.List;


/**
 * 文件的读写和转换（本地磁盘路径）
 *
 * @author winnie
 * @date 2020/11/9
 */
@Slf4j
public class FileConvert {
    public static void main(String[] args) {
        try {
            // 1. Excel 文件的读写
//            String excelFile = "D:\\posFile\\123.xlsx";
//            readWriteExcel(excelFile);

            // 2. TXT 文件的读写（文件夹下所有文件的读写）
            /*String txtPath = "D:\\posFile\\";
            try {
                File txtDir = new File(txtPath);
                if (!txtDir.exists()) {
                    return;
                }
                File[] files = txtDir.listFiles();
                if (files == null) {
                    return;
                }
                for (File txt : files) {
                    if (txt.getName().endsWith("txt")) {
                        readWriteTxt(txtPath + "\\" + txt.getName());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            // 3. Zip 文件的压缩解压
//            String zipFile = "D:\\posFile\\1.txt";
//            readWriteZip(zipFile);

            // 4.

            // 5. excel 转 pdf
//            String excelFile2 = "D:\\posFile\\pos.xls";
//            excelToPdf(excelFile2);

            // 6. pdf 转 png（一页为一个图片）
//            String pdfPath = "D:\\posFile\\";
//            pdfToPng(pdfPath);

        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    /**
     * 1. Excel 文件的读写（修改）
     *
     * @param excelPath excel 完整路径
     */
    private static void readWriteExcel(String excelPath) {
        File file = new File(excelPath);
        // Excel文件存在则读取
        if (file.exists()) {
            try {
                // 读取本地磁盘文件
                XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
                XSSFSheet sheet1 = workbook.getSheetAt(0);
                XSSFRow row1 = sheet1.getRow(0);
                XSSFCell cell1 = row1.getCell(0);
                cell1.setCellValue("设置值1");

                // 隐藏 sheet 页
                workbook.setSheetHidden(0, true);
                // 刷新公式
                workbook.setForceFormulaRecalculation(true);
                // 删除 Excel 文件
                FileUtil.deleteFile(excelPath);
                //  Excel 文件保存到本地磁盘
                FileOutputStream os = new FileOutputStream(excelPath);
                workbook.write(os);
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 2. TXT 文件的读写（覆盖；windows中文件编码格式为 ANSI，否则会乱码）
     *
     * @param txtPath txt 完整路径
     */
    private static void readWriteTxt(String txtPath) throws IOException {
        // 读取 txt 文件
        List<String> readLines = FileUtils.readLines(new File(txtPath), "GBK");
        log.info("========== 读取到文件 " + txtPath + "，文件内容如下 =============");
        for (String str : readLines) {
            log.info(str);
        }
        // 删除 txt 文件
        FileUtil.deleteFile(txtPath);
        //  txt 文件保存到本地磁盘（即使不删除，文件也会被覆盖掉）
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(txtPath), "GBK"));
        writer.write("这是一个 java 生成的 txt 文件！=============");
        writer.newLine();
        writer.write("txt 文件换行了！=============");
        writer.close();
    }

    /**
     * 3. Zip 文件的压缩解压
     *
     * @param filePath 文件完整路径
     */
    private static void readWriteZip(String filePath) throws IOException, ZipException {
        File file = new File(filePath);
        //  文件存在时，将文件压缩
        if (file.exists()) {
            // 定义压缩文件路径和名称
            String zipPath = filePath.substring(0, filePath.lastIndexOf(".")) + ".zip";
            ZipFile zipFile = new ZipFile(zipPath);
            ZipParameters parameters = new ZipParameters();
            // 压缩方式
            parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
            // 压缩级别
            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
            // 加密压缩（不加密可以注释掉）
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            parameters.setPassword("123456");
            // 将文件压缩
            zipFile.addFile(file, parameters);

            // 解压文件
            zipFile = new ZipFile(zipPath);
            // 设置解压密码
            if (zipFile.isEncrypted()) {
                zipFile.setPassword("123456");
            }
            // 指定解压文件（参数：D:/posFile/1）将 1.zip 解压到 1/ 文件夹下
            zipFile.extractAll(filePath.substring(0, filePath.lastIndexOf(".")));

        }
    }

    // TODO 4. Word 文件的读写

    /**
     * 5. excel 转 pdf
     *
     * @param excelPath excel 文件完整路径
     */
    private static void excelToPdf(String excelPath) {
        String pdfPath = excelPath.substring(0, excelPath.lastIndexOf(".")) + ".pdf";
        ExcelToPdf.convert(excelPath, pdfPath);
    }

    /**
     * 6. pdf 转 png（一页为一个图片）
     *
     * @param pdfPath pdf文件完整路径
     */
    private static void pdfToPng(String pdfPath) {
        File txtDir = new File(pdfPath);
        if (!txtDir.exists()) {
            return;
        }
        File[] files = txtDir.listFiles();
        if (files == null) {
            return;
        }
        for (File pdf : files) {
            if (pdf.getName().endsWith("pdf")) {
                PdfToPng.pdf2png(pdfPath + "\\" + pdf.getName(), 105);
            }
        }

    }

    // TODO word 转 pdf
    // TODO pdf 解析
    // TODO png 解析

}
