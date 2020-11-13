package com.sample.conn.file.util;

import com.aspose.cells.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Excel to Pdf
 *
 * @author LVMH Project Utils
 */
public class ExcelToPdf {
    public static boolean getLicense() {
        boolean result = false;
        try {
            //  license.xml应放在..\WebRoot\WEB-INF\classes路径下
            InputStream is = ExcelToPdf.class.getClassLoader().getResourceAsStream("xlsxlicense.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Excel 转换成 Pdf (隐藏的sheet页默认不会被转换)
     *
     * @param inPath   输入路径
     * @param outPath  输出路径
     * @param sheetNum 除去 sheet 第几页
     */
    public static void convert(String inPath, String outPath, int sheetNum) {
        // 验证 License 若不验证则转化出的 pdf文档会有水印产生
        if (!getLicense()) {
            return;
        }

        try {
            String pdfPath = outPath.split("\\.")[0] + ".pdf";
            // 输出路径
            File pdfFile = new File(pdfPath);
            // 原始 excel路径
            Workbook wb = new Workbook(inPath);
            int size = wb.getWorksheets().getCount();
            if (sheetNum < size) {
                wb.getWorksheets().removeAt(sheetNum);
            }
            FileOutputStream fileOs = new FileOutputStream(pdfFile);
            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            pdfSaveOptions.setOnePagePerSheet(true);

            wb.save(fileOs, pdfSaveOptions);
            fileOs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Excel 转换成 Pdf (隐藏的sheet页默认不会被转换)
     *
     * @param inPath   输入路径
     * @param outPath  输出路径
     */
    public static void convert(String inPath, String outPath) {
        // 验证 License 若不验证则转化出的pdf文档会有水印产生
        if (!getLicense()) {
            return;
        }

        try {
            String pdfPath = outPath.split("\\.")[0] + ".pdf";
            // 输出路径
            File pdfFile = new File(pdfPath);
            // 原始 excel路径
            Workbook wb = new Workbook(inPath);
            FileOutputStream fileOs = new FileOutputStream(pdfFile);
            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            pdfSaveOptions.setOnePagePerSheet(true);

            wb.save(fileOs, pdfSaveOptions);
            fileOs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void excelTopdf(String inPath,String outPath,int sheetNum) {
        // 验证License 若不验证则转化出的pdf文档会有水印产生
        if (!getLicense()) {
            return;
        }

        try {
            String pdfPath = outPath.split("\\.")[0]+".pdf";
            // 输出路径
            File pdfFile = new File(pdfPath);
            // 原始excel路径
            Workbook wb = new Workbook(inPath);
            int size = wb.getWorksheets().getCount();
            for(int i=sheetNum;i<size;i++){
                wb.getWorksheets().removeAt(sheetNum);
            }
            FileOutputStream fileOS = new FileOutputStream(pdfFile);

            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            pdfSaveOptions.setOnePagePerSheet(true);

            wb.save(fileOS, pdfSaveOptions);
            fileOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void excelTopdf(String inPath,String outPath,int sheetNum,int aloneDeleSheet) {
        // 验证License 若不验证则转化出的pdf文档会有水印产生
        if (!getLicense()) {
            return;
        }

        try {

            String pdfPath = outPath.split("\\.")[0]+".pdf";
            // 输出路径
            File pdfFile = new File(pdfPath);
            // 原始excel路径
            Workbook wb = new Workbook(inPath);

            int size = wb.getWorksheets().getCount();
            for(int i=sheetNum;i<size;i++){
                wb.getWorksheets().removeAt(sheetNum);
            }

            wb.getWorksheets().removeAt(aloneDeleSheet);
            FileOutputStream fileOS = new FileOutputStream(pdfFile);

            PdfSaveOptions pdfSaveOptions = new PdfSaveOptions();
            pdfSaveOptions.setOnePagePerSheet(true);
            pdfSaveOptions.setAllColumnsInOnePagePerSheet(true);

            wb.save(fileOS, pdfSaveOptions);
            fileOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}