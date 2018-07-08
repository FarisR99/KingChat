package com.faris.kingchat.client.helper;

import java.awt.*;
import java.awt.image.*;

public class ClientUtilities {

	private ClientUtilities() {
	}

	public static BufferedImage resizeImage(BufferedImage img, int newWidth, int newHeight) {
		Image tmp = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

}
