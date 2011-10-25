package com.easycrawler;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

public class Pic {

	private final static float SIMILARITYLIMIT = 0.9f;
	private static int[][] matrix;
	// 0-9 and 10: +, 11: -
	private final static String[] model = {
			"00111100" + "01111110" + "01100110" + "11000011" + "11000011"
					+ "11000011" + "11000011" + "11000011" + "01100110"
					+ "01111110" + "00111100",

			"001100" + "111100" + "111100" + "001100" + "001100" + "001100"
					+ "001100" + "001100" + "001100" + "111111" + "111111",

			"0111110" + "1111111" + "1000011" + "0000011" + "0000110"
					+ "0001100" + "0011000" + "0110000" + "1100000" + "1111111"
					+ "1111111",

			"01111100" + "11111111" + "10000011" + "00000011" + "00111110"
					+ "00111110" + "00000111" + "00000011" + "10000111"
					+ "11111110" + "01111100",

			"00011110" + "00011110" + "00110110" + "00110110" + "01100110"
					+ "11000110" + "11111111" + "11111111" + "00000110"
					+ "00000110" + "00000110",

			"11111111" + "11111111" + "11000000" + "11000000" + "11111000"
					+ "11111110" + "00000111" + "00000011" + "10000111"
					+ "11111110" + "01111100",

			"00011110" + "01111111" + "01100001" + "11100000" + "11011100"
					+ "11111111" + "11000011" + "11000011" + "11100011"
					+ "01111110" + "00111100",

			"1111111" + "1111111" + "0000011" + "0000110" + "0001100"
					+ "0001100" + "0011000" + "0110000" + "0110000" + "1100000"
					+ "1100000",

			"00111110" + "01111111" + "11000011" + "11100010" + "01111100"
					+ "01111110" + "11000111" + "11000011" + "11100011"
					+ "01111110" + "00111100",

			"00111100" + "01111110" + "11000111" + "11000011" + "11000011"
					+ "01111111" + "00111011" + "00000011" + "10000110"
					+ "11111110" + "01111000",

			"000000000" + "000000000" + "000010000" + "000010000" + "000010000"
					+ "000010000" + "111111111" + "000010000" + "000010000"
					+ "000010000" + "000010000",

			"0000000" + "0000000" + "0000000" + "0000000" + "0000000"
					+ "1111111" + "1111111" + "0000000" + "0000000" + "0000000"
					+ "0000000" };

	/**
	 * @args[0] - Image full path and file name
	 */
	public static void main(String[] args) {

		// System.out.println("The answer is " + new
		// AnalyzePic().getResult(""));
	}

	public String getResult(InputStream content) {
		Image img = null;
		try {
			img = ImageIO.read(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (img != null) {
			transferImageToMatrix(img);
			return String.valueOf(calcResult());
		}
		return "0";
	}

	public String getResult(String url, HttpHelper httpHelper) {
		return getResult(httpHelper.getResponseAsStream(url));
	}

	public String getResult(HttpHelper httpHelper) {
		String url = "http://www.miibeian.gov.cn/validateCode";
		return getResult(httpHelper.getResponseAsStream(url));
	}

	public int getResult(URL fileName) {
		Image img = null;
		// get original image
		try {
			img = ImageIO.read(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (img != null) {
			transferImageToMatrix(img);
			return calcResult();
		}
		return 0;
	}

	private BufferedImage cropImage(Image img, Point startPoint,
			Point cropLength) {
		int srcWidth = 0, srcHeight = 0, tgtWidth = 0, tgtHeight = 0;
		srcWidth = img.getWidth(null);
		srcHeight = img.getHeight(null);
		tgtWidth = srcWidth + cropLength.x;
		// int tgtWidth=7;
		tgtHeight = srcHeight + cropLength.y;
		BufferedImage bi = new BufferedImage(tgtWidth, tgtHeight,
				BufferedImage.TYPE_INT_RGB);
		// crop
		Graphics g = bi.getGraphics();
		g.drawImage(img, startPoint.x, startPoint.y, srcWidth, srcHeight, null);
		g.dispose();
		return bi;
	}

	private void transferImageToMatrix(Image img) {
		BufferedImage bi = cropImage(img, new Point(-7, -5), new Point(-9, -7));
		int width = 0, height = 0;
		width = bi.getWidth();
		height = bi.getHeight();
		matrix = new int[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				matrix[x][y] = (int) (Integer
						.valueOf((bi.getRGB(x, y) >> 4) & 0xf) + Integer
						.valueOf(((bi.getRGB(x, y) >> 12) & 0xf)
								+ Integer.valueOf((bi.getRGB(x, y) >> 20) & 0xf)));
				if (matrix[x][y] > 24)
					matrix[x][y] = 0;
				else
					matrix[x][y] = 1;
			}
			// System.out.println();
		}

		// debug code
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				System.out.print(matrix[x][y]);
			}
			System.out.println();
		}
	}

	/**
	 * Check if there is any point around (x,y).
	 * 
	 * @param x
	 *            - coordinate x
	 * @param y
	 *            - coordinate y
	 * @return true if there is
	 */
	private boolean isPointAround(int x, int y) {
		if (x - 1 >= 0 && 1 == matrix[x - 1][y])
			return true;
		if (x + 1 < matrix.length && 1 == matrix[x + 1][y])
			return true;
		if (y - 1 >= 0 && 1 == matrix[x][y - 1])
			return true;
		if (y + 1 < matrix[0].length && 1 == matrix[x][y + 1])
			return true;
		return false;
	}

	/**
	 * Get Character start location X
	 * 
	 * @param offsetX
	 *            - Offset location X to start searching
	 * @return the start location X
	 */
	private int getCharStartX(int offsetX) {
		int width = matrix.length;
		int height = matrix[0].length;
		for (int x = offsetX; x < width; x++)
			for (int y = 0; y < height; y++) {
				if (1 == matrix[x][y] && isPointAround(x, y))
					return x;
			}
		return width;
	}

	/**
	 * Get Character end location X
	 * 
	 * @param offsetX
	 *            - Offset location X to start searching
	 * @return the end location X
	 */
	private int getCharEndX(int offsetX) {
		int width = matrix.length;
		int height = matrix[0].length;
		int x, y;
		for (x = offsetX; x < width; x++) {
			for (y = 0; y < height; y++) {
				if (1 == matrix[x][y] && isPointAround(x, y))
					break;
			}
			if (y == height && x - offsetX > 5)
				return x;
		}
		return width;
	}

	/**
	 * Get the formula as showed in image
	 * 
	 * @return sample - image: 22+14=, formula: 2 2 11 1 4 sample - image:
	 *         22-14=, formula: 2 2 10 1 4
	 */
	private String getFormula() {
		int startX = 0;
		int endX = 0;
		String character = "";
		String formula = "";
		while (endX < matrix.length) {
			startX = getCharStartX(endX);
			endX = getCharEndX(startX);
			character = transferModelToCharacter(startX, endX);
			formula += character;
		}
		return formula;
	}

	/**
	 * Transfer model matrix to characters
	 */
	private String transferModelToCharacter(int startX, int endX) {
		String tgtString = "";
		int similarity = 0;
		int height = matrix[0].length;

		for (int y = 0; y < height; y++) {
			for (int x = startX; x < endX; x++) {
				tgtString += String.valueOf(matrix[x][y]);
			}
		}
		int i;
		for (i = 0; i < model.length; i++) {
			int length = ((tgtString.length() >= model[i].length()) ? model[i]
					.length() : tgtString.length());
			for (int j = 0; j < length; j++)
				if (tgtString.getBytes()[j] == (model[i].getBytes()[j]))
					similarity += 1;
			if ((float) similarity / (float) length > SIMILARITYLIMIT)
				return String.valueOf(i) + " ";
			else
				similarity = 0;
		}
		return "";
	}

	private int calcResult() {
		String formula = getFormula();
		String[] formulaArray = formula.split(" ");
		int firstDigit = 0;
		int secondDigit = 0;
		int result = 0;

		if (formula.contains("10")) {
			for (int i = 0; i < formulaArray.length; i++)
				if (!formulaArray[i].equals("10")) {
					result = result * 10 + Integer.valueOf(formulaArray[i]);
				} else {
					firstDigit = result;
					result = 0;
				}
			secondDigit = result;
			result = firstDigit + secondDigit;
		}
		if (formula.contains("11")) {
			for (int i = 0; i < formulaArray.length; i++)
				if (!formulaArray[i].equals("11")) {
					result = result * 10 + Integer.valueOf(formulaArray[i]);
				} else {
					firstDigit = result;
					result = 0;
				}
			secondDigit = result;
			result = firstDigit - secondDigit;
		}
		System.out.println("The answer is " + result);
		return result;
	}

}
