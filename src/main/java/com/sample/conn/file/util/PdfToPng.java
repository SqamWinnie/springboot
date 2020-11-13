package com.sample.conn.file.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Pdf to Png
 *
 * @author LVMH Project Utils
 */
public class PdfToPng {

    /**
     * Pdf to Png（每页转换成图片）
     *
     * @param pdfName pdf 文件路径
     */
    public static void pdf2png(String pdfName, int dpi) {
        // 将 pdf装图片 并且自定义图片得格式大小
        if (!pdfName.endsWith(".pdf")) {
            pdfName += ".pdf";
        }
        File file = new File(pdfName);
        try {
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, dpi, ImageType.RGB);
//				BufferedImage srcImage = resize(image, 1239, 1752);// 产生缩略图
                ImageIO.write(image, "PNG", new File(pdfName + i + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pdf to Png（ pdf 的起始页和终止页转换成 png 图片）
     *
     * @param pdfName      pdf 文件路径名称
     * @param indexOfStart 起始页
     * @param indexOfEnd   终止页
     */
    public static void pdf2png(String pdfName, int indexOfStart, int indexOfEnd) {
        // 将 pdf装图片 并且自定义图片得格式大小
        if (!pdfName.endsWith(".pdf")) {
            pdfName += ".pdf";
        }
        File file = new File(pdfName);
        try {
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = indexOfStart; i < indexOfEnd; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 105, ImageType.RGB);
//				BufferedImage srcImage = resize(image, 1239, 1752);// 产生缩略图
                ImageIO.write(image, "PNG", new File(pdfName + i + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pdf to Png（ base 64）
     *
     * @param filename     pdf 文件路径名称
     * @param indexOfStart 起始页
     * @param indexOfEnd   终止页
     * @return 异常
     */
    public static String pdf2base64(String filename, int indexOfStart, int indexOfEnd) {
        // 将 pdf装图片 并且自定义图片得格式大小
        File file = new File(filename + ".pdf");
        try {
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            StringBuffer sb = new StringBuffer();
            for (int i = indexOfStart; i < indexOfEnd; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 105, ImageType.RGB);
//						BufferedImage srcImage = resize(image, 1239, 1752);// 产生缩略图
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "PNG", outputStream);
                BASE64Encoder encoder = new BASE64Encoder();
                String base64Img = encoder.encode(outputStream.toByteArray());
                String img = "<img src= \"data:image/png;base64," + base64Img + "\"/>";
                sb.append(img);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 重置图片大小
     * @param source
     * @param targetW
     * @param targetH
     * @return
     */
	private static BufferedImage resize(BufferedImage source, int targetW, int targetH) {
		int type = source.getType();
		BufferedImage target = null;
		double sx = (double) targetW / source.getWidth();
		double sy = (double) targetH / source.getHeight();
		if (sx > sy) {
			sx = sy;
			targetW = (int) (sx * source.getWidth());
		} else {
			sy = sx;
			targetH = (int) (sy * source.getHeight());
		}
		if (type == BufferedImage.TYPE_CUSTOM) {
			ColorModel cm = source.getColorModel();
			WritableRaster raster = cm.createCompatibleWritableRaster(targetW, targetH);
			boolean alphaPremultiplied = cm.isAlphaPremultiplied();
			target = new BufferedImage(cm, raster, alphaPremultiplied, null);
		} else {
			target = new BufferedImage(targetW, targetH, type);
		}
		Graphics2D g = target.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.drawRenderedImage(source, AffineTransform.getScaleInstance(sx, sy));
		g.dispose();
		return target;
	}


}