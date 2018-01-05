package base.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * Jan 10, 2017
 * 
 * @author VuD
 * 
 **/

public class ImageUtils {

	public static BufferedImage resizeImage(BufferedImage originalImage, int type, int newwidth, int newhigth) {
		BufferedImage resizedImage = new BufferedImage(newwidth, newhigth, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, newwidth, newhigth, null);
		g.dispose();

		return resizedImage;
	}

	public static BufferedImage resizeImage(byte[] data, int newwidth, int newhigth) throws IOException {
		InputStream is = new ByteArrayInputStream(data);
		BufferedImage originalImage = ImageIO.read(is);
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		BufferedImage resizedImage = new BufferedImage(newwidth, newhigth, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, newwidth, newhigth, null);
		g.dispose();
		return resizedImage;
	}

	public static byte[] resizeImage2Byte(byte[] data, int newwidth, int newhigth, String fomartname)
			throws IOException {
		InputStream is = new ByteArrayInputStream(data);
		BufferedImage originalImage = ImageIO.read(is);
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		BufferedImage resizedImage = new BufferedImage(newwidth, newhigth, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, newwidth, newhigth, null);
		g.dispose();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(resizedImage, fomartname, baos);
		baos.flush();
		return baos.toByteArray();
	}

	public static String getImageType(byte[] data) throws IOException {
		ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(data));
		Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
		String resut = null;
		while (readers.hasNext()) {
			ImageReader imageReader = (ImageReader) readers.next();
			resut = imageReader.getFormatName();
		}
		return resut;
	}

	public static void main(String[] args) {
		try {
			// String path = "C:\\Users\\VuD\\Desktop\\yeuquai.jpg";
			// FileInputStream fis = new FileInputStream(new File(path));
			// byte[] data = new byte[fis.available()];
			// fis.read(data);
			//
			// ImageInputStream iis = ImageIO.createImageInputStream(new
			// ByteArrayInputStream(data));
			// Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
			// while (readers.hasNext()) {
			// ImageReader imageReader = (ImageReader) readers.next();
			// System.out.println(imageReader.getFormatName());
			// }
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
