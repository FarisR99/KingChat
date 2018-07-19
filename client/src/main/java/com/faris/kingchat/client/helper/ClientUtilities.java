package com.faris.kingchat.client.helper;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;

public class ClientUtilities {

	private static final List<String> AVAILABLE_FONTS = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());

	private ClientUtilities() {
	}

	public static List<String> getAvailableFonts(List<String> fontNames) {
		return fontNames.stream().filter(AVAILABLE_FONTS::contains).collect(Collectors.toList());
	}

	public static BufferedImage resizeImage(BufferedImage img, int newWidth, int newHeight) {
		Image tmp = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	public static void setFontSize(JTextComponent textComponent, float fontSize) {
		Font font = textComponent.getFont();
		textComponent.setFont(font.deriveFont(Math.abs(fontSize)));
	}

}
