package com.faris.kingchat.client;

import com.faris.kingchat.core.helper.FXUtilities;
import com.faris.kingchat.core.helper.Utilities;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.*;

public class LoginPane extends VBox {

	private final ClientWindow window;

	private TextField txtName;
	private TextField txtAddress;
	private TextField txtPort;
	private TextField txtPassword;

	public LoginPane(ClientWindow window) {
		this.window = window;

		this.setPadding(new Insets(5, 5, 5, 5));
		this.setAlignment(Pos.CENTER);

		this.populateContentPane(event -> {
			if (event.getCode() == KeyCode.ENTER) doLogin();
		});
	}

	public void initStage() {
		Stage stage = this.window.getStage();
		stage.setTitle("Login");
		stage.setResizable(false);
		stage.setMinWidth(0D);
		stage.setMinHeight(0D);
		stage.setScene(new Scene(this, 260, 310));

		stage.setOnCloseRequest(null);
		stage.setOnHiding(null);
	}

	private void populateContentPane(EventHandler<KeyEvent> enterKeyListener) {
		// Name panel

		Label lblName = new Label("Name:");
		this.txtName = new TextField();
		this.txtName.setOnKeyPressed(enterKeyListener);

		VBox namePanel = new VBox();
		namePanel.setAlignment(Pos.CENTER);
		namePanel.setSpacing(5D);
		namePanel.getChildren().add(lblName);
		namePanel.getChildren().add(this.txtName);

		// IP panel

		Label lblAddress = new Label("IP Address:");
		this.txtAddress = new TextField();
		this.txtAddress.setOnKeyPressed(enterKeyListener);

		VBox ipPanel = new VBox();
		ipPanel.setAlignment(Pos.CENTER);
		ipPanel.setSpacing(5D);
		ipPanel.getChildren().add(lblAddress);
		ipPanel.getChildren().add(this.txtAddress);

		// Port panel

		Label lblPort = new Label("Port:");
		this.txtPort = new TextField();
		this.txtPort.setOnKeyPressed(enterKeyListener);

		VBox portPanel = new VBox();
		portPanel.setAlignment(Pos.CENTER);
		portPanel.setSpacing(5D);
		portPanel.getChildren().add(lblPort);
		portPanel.getChildren().add(this.txtPort);

		// Password panel

		Label lblPassword = new Label("Password:");
		this.txtPassword = new TextField();
		this.txtPassword.setOnKeyPressed(enterKeyListener);

		VBox passwordPanel = new VBox();
		passwordPanel.setAlignment(Pos.CENTER);
		passwordPanel.setSpacing(5D);
		passwordPanel.getChildren().add(lblPassword);
		passwordPanel.getChildren().add(this.txtPassword);

		// Content pane

		Button btnLogin = new Button("Login");
		btnLogin.setAlignment(Pos.CENTER);
		btnLogin.setOnAction(e -> this.doLogin());

		this.getChildren().add(new Rectangle(0, 10));
		this.getChildren().add(namePanel);
		this.getChildren().add(new Rectangle(0, 20));
		this.getChildren().add(ipPanel);
		this.getChildren().add(new Rectangle(0, 20));
		this.getChildren().add(portPanel);
		this.getChildren().add(new Rectangle(0, 20));
		this.getChildren().add(passwordPanel);
		this.getChildren().add(new Rectangle(0, 15));
		this.getChildren().add(btnLogin);
	}

	public void doLogin() {
		String name = this.txtName.getText().trim();
		String ipAddress = this.txtAddress.getText().trim();
		String port = this.txtPort.getText().trim();
		String password = this.txtPassword.getText();
		if (!name.isEmpty()) {
			if (name.length() <= 16) {
				if (Utilities.VALID_USERNAME_PATTERN.matcher(name).matches()) {
					if (ipAddress.isEmpty()) {
						ipAddress = "localhost";
						this.txtAddress.setText("localhost");
					}
					if (!ipAddress.contains(" ")) {
						if (!port.isEmpty()) {
							OptionalInt optionalPort = Utilities.parseInt(port);
							if (optionalPort.isPresent()) {
								if (password.isEmpty()) password = null;
								try {
									ClientPane client = new ClientPane(this.window, name, ipAddress, optionalPort.getAsInt(), password);
									client.initStage();
									this.window.getStage().centerOnScreen();
								} catch (Exception ex) {
									ex.printStackTrace();
									System.exit(-1);
								}
							} else {
								FXUtilities.createErrorDialog("Please enter a valid port.", "Invalid port").showAndWait();
							}
						} else {
							FXUtilities.createErrorDialog("Please enter a port.", "Empty port").showAndWait();
						}
					} else {
						FXUtilities.createErrorDialog("Please enter a valid IP address (host).", "Invalid IP address").showAndWait();
					}
				} else {
					FXUtilities.createErrorDialog("Please enter a valid (alphanumeric) name.", "Invalid name").showAndWait();
				}
			} else {
				FXUtilities.createErrorDialog("Name length cannot be greater than 16.", "Invalid name").showAndWait();
			}
		} else {
			FXUtilities.createErrorDialog("Please enter a name.", "Empty name").showAndWait();
		}
	}

}
