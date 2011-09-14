package com.analyzepic;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class AnalyzePic {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			// get original image
			Image img = ImageIO.read(new File("c:/validateCode.jpg"));
			int srcWidth = img.getWidth(null);
			int srcHeight = img.getHeight(null);
			int tgtWidth = srcWidth - 9;
			// int tgtWidth=7;
			int tgtHeight = srcHeight - 7;

			BufferedImage tag = new BufferedImage(tgtWidth, tgtHeight,
					BufferedImage.TYPE_INT_RGB);
			Graphics g = tag.getGraphics();
			g.drawImage(img, -7, -5, srcWidth, srcHeight, null);
			g.dispose();
			int[][] pixel = new int[tgtWidth][tgtHeight];
			for (int y = 0; y < tgtHeight; y++) {
				for (int x = 0; x < tgtWidth; x++) {
					//pixel[x][y]=(tag.getRGB(x, y) & 0xffffff);
					//pixel[x][y]=Integer.valueOf((tag.getRGB(x, y) >> 20) & 0xf);
					pixel[x][y] = (int) (Integer
							.valueOf((tag.getRGB(x, y) >> 4) & 0xf) + Integer
							.valueOf(((tag.getRGB(x, y) >> 12) & 0xf)
									+ Integer.valueOf((tag.getRGB(x, y) >> 20) & 0xf)));
					if (pixel[x][y] > 24)
						pixel[x][y] = 0;
					else
						pixel[x][y] = 1;
					//pixel[x][y]=pixel[x][y]>8?0:1;
				}
				// System.out.println();
			}
			for (int y = 0; y < tgtHeight; y++) {
				for (int x = 0; x < tgtWidth; x++) {
					System.out.print(pixel[x][y]);
				}
				System.out.println();
			}
			
//			for (int y = 0; y < tgtHeight; y++) {
//				for (int x = 0; x < tgtWidth; x++) {
//					System.out.print(Integer.toHexString(pixel[x][y])+" ");
//				}
//				System.out.println();
//			}
			
			System.out.println(getResult(pixel, tgtWidth, tgtHeight));

			// System.out.println(Integer.toHexString((pixel[0]& 16711680) >>
			// 16));
			// 输出为文件
			// ImageIO.write(tag, "JPEG", new File("c:/new.jpg"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getStartX(int[][] pixel, int offsetX, int Width,
			int Height) {
		for (int x = offsetX; x < Width; x++)
			for (int y = 0; y < Height; y++) {
				if (1 == pixel[x][y]&&isPointAround(pixel,x,y))
					return x;
			}
		return Width;
	}

	public static boolean isPointAround(int[][] pixel,int x, int y)
	{
		if(x-1>=0&& 1==pixel[x-1][y])
			return true;
		if(x+1<=pixel.length&&1==pixel[x+1][y])
			return true;
		if(y-1>=0&&1==pixel[x][y-1])
			return true;
		if(y+1<=pixel[0].length&&1==pixel[x][y+1])
			return true;
		return false;
	}
	public static int getEndX(int[][] pixel, int offsetX, int Width, int Height) {
		int x, y;
		for (x = offsetX; x < Width; x++) {
			for (y = 0; y < Height; y++) {
				if (1 == pixel[x][y])
					break;
			}
			if (y == Height && x-offsetX>5)
				return x;
		}
		return Width;
	}

	public static String getCharacter(int[][] pixel, int Width, int Height,
			int cursorX) {

		int startX = getStartX(pixel, cursorX, Width, Height);
		int endX = getEndX(pixel, startX, Width, Height);
		String Character = transferToCharacter(pixel, Height, startX, endX);
		if ("" != Character) {
			Character += getCharacter(pixel, Width, Height, endX);
		}

		return Character;
	}

	public static String transferToCharacter(int[][] pixel, int Height,
			int startX, int endX) {
		String[] character = new String[12];
		character[0] = "0011110001111110011001101100001111000011110000111100001111000011011001100111111000111100";
		character[1] = "001100111100111100001100001100001100001100001100001100111111111111";
		character[2] = "01111101111111100001100000110000110000110000110000110000110000011111111111111";
		character[3] = "0111110011111111100000110000001100111110001111100000011100000011100001111111111001111100";
		character[4] = "0001111000011110001101100011011001100110110001101111111111111111000001100000011000000110";
		character[5] = "111111111111111111000000110000001111100111111100000011100000011100001111111111001111100";
		character[6] = "0001111001111111011000011110000011011100111111111100001111000011111000110111111000111100";
		character[7] = "11111111111111000001100001100001100000110000110000110000011000011000001100000";
		character[8] = "0011111001111111110000111110001001111100011111101100011111000011111000110111111000111100";
		character[9] = "0011110001111110110001111100001111000011011111110011101100000011100001101111111001111000";
		character[10] = "000000000000000000000010000000010000000010000000010000111111111000010000000010000000010000000010000";
		character[11] = "00000000000000000000000000000000000111111111111110000000000000000000000000000";
		String tgtString = "";
		int Similarity = 0;
		for (int y = 0; y < Height; y++) {
			for (int x = startX; x < endX; x++) {
				tgtString += String.valueOf(pixel[x][y]);
			}
		}
		int i;
		for (i = 0; i < character.length; i++) {
			int length=((tgtString.length()>=character[i].length())?character[i].length():tgtString.length());
			for (int j = 0; j <length ; j++)
				if (tgtString.getBytes()[j] == (character[i].getBytes()[j]))
					Similarity += 1;
			if ((float)Similarity /(float)length> 0.85)
				return String.valueOf(i) + " ";
			else
				Similarity=0;
		}
		return "";
	}

	public static int getResult(int[][] pixel, int Width, int Height) {

		String Formula = getCharacter(pixel, Width, Height, 0);
		String[] FormulaArray = Formula.split(" ");
		int FirstDigit = 0;
		int SecondDigit = 0;
		int Result = 0;

		if (Formula.contains("10")) {
			for (int i = 0; i < FormulaArray.length; i++)
				if (!FormulaArray[i].equals("10")) {
					Result = Result * 10 + Integer.valueOf(FormulaArray[i]);
				} else {
					FirstDigit = Result;
					Result = 0;
				}
			SecondDigit = Result;
			Result = FirstDigit + SecondDigit;
		}
		if (Formula.contains("11")) {
			for (int i = 0; i < FormulaArray.length; i++)
				if (!FormulaArray[i].equals("11")) {
					Result = Result * 10 + Integer.valueOf(FormulaArray[i]);
				} else {
					FirstDigit = Result;
					Result = 0;
				}
			SecondDigit = Result;
			Result = FirstDigit - SecondDigit;
		}
		return Result;
	}
}
