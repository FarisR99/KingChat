package com.faris.kingchat.core.helper;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;

public class FXUtilities {

	private FXUtilities() {
	}

	private static Alert createDialog(Alert.AlertType alertType, String message, String title, String header, String expandableContent) {
		if (message == null) throw new IllegalArgumentException("message cannot be null");
		Alert alert = new Alert(alertType);
		if (title != null) alert.setTitle(title);
		if (header != null) alert.setHeaderText(header);
		alert.setContentText(message);
		if (expandableContent != null) {
			TextArea txtExpandable = new TextArea(expandableContent);
			txtExpandable.setEditable(false);
			txtExpandable.setWrapText(true);
			BorderPane borderPane = new BorderPane();
			borderPane.setCenter(txtExpandable);
			alert.getDialogPane().setExpandableContent(borderPane);
		}
		return alert;
	}

	public static Alert createErrorDialog(String message) {
		return createErrorDialog(message, null, null);
	}

	public static Alert createErrorDialog(String message, String title) {
		return createErrorDialog(message, title, null);
	}

	public static Alert createErrorDialog(String message, String title, String header) {
		return createErrorDialog(message, title, header, null);
	}

	public static Alert createErrorDialog(String message, String title, String header, String expandableContent) {
		return createDialog(Alert.AlertType.ERROR, message, title, header, expandableContent);
	}

	public static Alert createMessageDialog(String message) {
		return createMessageDialog(message, null, null);
	}

	public static Alert createMessageDialog(String message, String title) {
		return createMessageDialog(message, title, null);
	}

	public static Alert createMessageDialog(String message, String title, String header) {
		return createMessageDialog(message, title, header, null);
	}

	public static Alert createMessageDialog(String message, String title, String header, String expandableContent) {
		return createDialog(Alert.AlertType.INFORMATION, message, title, header, expandableContent);
	}

}
