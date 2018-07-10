package com.faris.kingchat.server.gui.richtextfx;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.Image;
import org.fxmisc.richtext.model.Codec;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.*;
import java.util.*;

public interface LinkedImage {
	static <S> Codec<LinkedImage> codec() {
		return new Codec<LinkedImage>() {
			@Override
			public String getName() {
				return "LinkedImage";
			}

			@Override
			public void encode(DataOutputStream os, LinkedImage linkedImage) throws IOException {
				if (linkedImage.isReal()) {
					os.writeBoolean(true);

					BufferedImage bufferedImage = SwingFXUtils.fromFXImage(linkedImage.getImage(), null);
					ByteArrayOutputStream baosImage = new ByteArrayOutputStream();
					ImageIO.write(bufferedImage, "png", baosImage);
					byte[] imgBytes = baosImage.toByteArray();
					os.writeUTF(Base64.getEncoder().encodeToString(imgBytes));
				} else {
					os.writeBoolean(false);
				}
			}

			@Override
			public LinkedImage decode(DataInputStream is) throws IOException {
				if (is.readBoolean()) {
					byte[] imageBytes = Base64.getDecoder().decode(is.readUTF());
					ByteArrayInputStream baisImage = new ByteArrayInputStream(imageBytes);
					BufferedImage bufferedImage = ImageIO.read(baisImage);
					return new RealLinkedImage(SwingFXUtils.toFXImage(bufferedImage, null));
				} else {
					return new EmptyLinkedImage();
				}
			}
		};
	}

	boolean isReal();

	/**
	 * @return The image to render.
	 */
	Image getImage();

	Node createNode();
}