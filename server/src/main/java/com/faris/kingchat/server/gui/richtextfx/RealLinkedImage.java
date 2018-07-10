package com.faris.kingchat.server.gui.richtextfx;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * A custom object which contains a file path to an image file.
 * When rendered in the rich text editor, the image is loaded from the
 * specified file.
 */
public class RealLinkedImage implements LinkedImage {

	private final Image image;

	/**
	 * Creates a new linked image object.
	 *
	 * @param image The image.
	 */
	public RealLinkedImage(Image image) {
		this.image = image;
	}

	@Override
	public boolean isReal() {
		return true;
	}

	@Override
	public Image getImage() {
		return this.image;
	}

	@Override
	public Node createNode() {
		return new ImageView(this.image);
	}

}