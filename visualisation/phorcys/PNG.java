package visualisation.phorcys;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import javax.imageio.ImageIO;

public class PNG {
	int[] img;
	BufferedImage imgFile;

	String[] labels;
	double width = 1000;
	double height = 0.0;
	double scaleW = 1.0;
	double scaleH = 1.0;;

	public PNG(String location) {
		try {
			imgFile = ImageIO.read(new File(location));
			width = imgFile.getWidth();
			height = imgFile.getHeight();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public PNG(int w, int h) {
		width = w;
		height = h;
		img = new int[(int) width * (int) height];
		// golTEST();
	}

	public void setScale(double w, double h) {
		scaleW = w;
		scaleH = h;
	}

	public void createImage() {
		int incr = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				img[incr] = rgbToInt(Utilities.generateRandomRangeInteger(0, 255),
						Utilities.generateRandomRangeInteger(0, 255), Utilities.generateRandomRangeInteger(0, 255));
				incr++;
			}
		}
		imgFile = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
		imgFile.setRGB(0, 0, (int) width, (int) height, img, 0, (int) width);
	}

	public void addElementToImage(int x, int y, Color col) {
		//TODO: Add torus drawing ability
		
		if (x < 0) {
			//TODO
			x = (int)width + x;
		}
		
		if (y < 0) {
			//TODO
			y = (int)height + y;
		}
		
		
		img[(((int) height - 1 - y) * (int) width) + x] = rgbToInt(col.getRed(), col.getGreen(), col.getBlue());
	}

	public void newImage() {
		img = new int[(int) width * (int) height];
	}

	public void prepImage() {
		imgFile = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
		imgFile.setRGB(0, 0, (int) width, (int) height, img, 0, (int) width);
	}

	public void golTEST() {
		int incr = 0;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				if (Utilities.generateCoinFlip()) {
					img[incr] = rgbToInt(0, 0, 0);
				} else {
					img[incr] = rgbToInt(255, 255, 255);
				}
				incr++;
			}
		}
		imgFile = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
		imgFile.setRGB(0, 0, (int) width, (int) height, img, 0, (int) width);
	}

	public int rgbToInt(int r, int g, int b) {
		return (r << 16) | (g << 8) | b;
	}

	public PNG(BufferedImage img) {
		this.imgFile = img;
	}

	public void setLabels(String... labels) {
		this.labels = labels;
	}

	public JsonValue toJson() {
		JsonObject jobj = new JsonObject();
		String imgString = "data:image/png;base64," + PNG.imgToBase64String(imgFile, "png");
		jobj.add("src", imgString);

		// Add labels
		if (labels != null) {
			if (labels.length > 0) {
				JsonArray jsonLabels = new JsonArray();
				for (int i = 0; i < labels.length; i++) {
					jsonLabels.add(labels[i]);
				}
			} else {
				jobj.add("labels", "null");
			}
		} else {
			jobj.add("labels", "null");
		}

		if (width > 0) {
			jobj.add("width", width * scaleW);
		} else {
			jobj.add("width", 250);
		}

		if (height > 0) {
			jobj.add("height", height * scaleH);
		} else {
			jobj.add("height", 250);
		}

		return jobj;
	}

	// Pinched from SO
	// http://stackoverflow.com/questions/7178937/java-bufferedimage-to-png-format-base64-string
	public static String imgToBase64String(final RenderedImage img, final String formatName) {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ImageIO.write(img, formatName, Base64.getEncoder().wrap(os));
			return os.toString(StandardCharsets.US_ASCII.name());
		} catch (final IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}
	// Pinched from SO
	// http://stackoverflow.com/questions/7178937/java-bufferedimage-to-png-format-base64-string
	/*
	 * public static BufferedImage base64StringToImg(final String base64String) {
	 * try { return ImageIO.read(new
	 * ByteArrayInputStream(Base64.getDecoder().decode(base64String))); } catch
	 * (final IOException ioe) { throw new UncheckedIOException(ioe); } }
	 */

	// TODO
	/*
	 * public void encode(){
	 * 
	 * }
	 * 
	 * public int[][][] normalize(int[][][] img, int[] minmax){ int min; int max; if
	 * (minmax != null) { if (minmax[0] < minmax[1]) { min = minmax[0]; max =
	 * minmax[1]; } else { min = Integer.MAX_VALUE; max = Integer.MIN_VALUE; for
	 * (int i = 0; i < img[0][0].length; i++){ for (int j = 0; j < img[0].length;
	 * j++){ for (int k = 0; k < img.length; k++){ if (img[k][j][i] < min){ min =
	 * img[k][j][i]; } else if (img[k][j][i] > max){ max = img[k][j][i]; } } } } } }
	 * else { min = Integer.MAX_VALUE; max = Integer.MIN_VALUE; for (int i = 0; i <
	 * img[0][0].length; i++){ for (int j = 0; j < img[0].length; j++){ for (int k =
	 * 0; k < img.length; k++){ if (img[k][j][i] < min){ min = img[k][j][i]; } else
	 * if (img[k][j][i] > max){ max = img[k][j][i]; } } } } }
	 * 
	 * //Apply the normalisation for (int i = 0; i < img[0][0].length; i++){ for
	 * (int j = 0; j < img[0].length; j++){ for (int k = 0; k < img.length; k++){
	 * img[k][j][i] = ((img[k][j][i] - min) * (255/(max - min))); } } }
	 * 
	 * 
	 * 
	 * return img; }
	 * 
	 * 
	 * public int[][][] toRGB(int[][][] img){ return null; }
	 */
}
