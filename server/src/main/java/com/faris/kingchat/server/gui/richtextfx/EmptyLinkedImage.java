package com.faris.kingchat.server.gui.richtextfx;
import javafx.scene.Node;
import javafx.scene.image.Image;

public class EmptyLinkedImage implements LinkedImage {

	@Override
	public boolean isReal() {
		return false;
	}

	@Override
	public Image getImage() {
		return null;
	}

	@Override
	public Node createNode() {
		throw new AssertionError("Unreachable code");
	}

}