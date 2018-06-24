package com.faris.kingchat.client;

import com.faris.kingchat.core.Constants;
import com.faris.kingchat.core.helper.FXUtilities;
import com.faris.kingchat.core.helper.Utilities;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

public class LoginPane extends VBox {

	private final ClientWindow window;

	private TextField txtName;
	private TextField txtAddress;
	private TextField txtPort;
	private TextField txtPassword;
	private TextField txtProfilePicture;

	public LoginPane(ClientWindow window) {
		this.window = window;

		this.setPadding(new Insets(5, 5, 5, 5));
		this.setAlignment(Pos.CENTER);
		this.setSpacing(10D);

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
		stage.setScene(new Scene(this, 260, 330));

		stage.setOnCloseRequest(event -> {
			Platform.exit();
			System.exit(0);
		});
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
		namePanel.getChildren().addAll(lblName, this.txtName);

		// IP panel

		Label lblAddress = new Label("IP Address:");
		this.txtAddress = new TextField();
		this.txtAddress.setOnKeyPressed(enterKeyListener);

		VBox ipPanel = new VBox();
		ipPanel.setAlignment(Pos.CENTER);
		ipPanel.setSpacing(5D);
		ipPanel.getChildren().addAll(lblAddress, this.txtAddress);

		// Port panel

		Label lblPort = new Label("Port:");
		this.txtPort = new TextField();
		this.txtPort.setOnKeyPressed(enterKeyListener);

		VBox portPanel = new VBox();
		portPanel.setAlignment(Pos.CENTER);
		portPanel.setSpacing(5D);
		portPanel.getChildren().addAll(lblPort, this.txtPort);

		// Password panel

		Label lblPassword = new Label("Password:");
		this.txtPassword = new PasswordField();
		this.txtPassword.setOnKeyPressed(enterKeyListener);

		VBox passwordPanel = new VBox();
		passwordPanel.setAlignment(Pos.CENTER);
		passwordPanel.setSpacing(5D);
		passwordPanel.getChildren().addAll(lblPassword, this.txtPassword);

		// Profile picture panel

		Label lblProfilePic = new Label("Profile picture:");
		this.txtProfilePicture = new TextField();
		this.txtProfilePicture.setOnKeyPressed(enterKeyListener);

		VBox ppPanel = new VBox();
		ppPanel.setAlignment(Pos.CENTER);
		ppPanel.setSpacing(5D);
		ppPanel.getChildren().addAll(lblProfilePic, this.txtProfilePicture);

		// Content pane

		Button btnLogin = new Button("Login");
		btnLogin.setAlignment(Pos.CENTER);
		btnLogin.setOnAction(e -> this.doLogin());

		this.getChildren().add(namePanel);
		this.getChildren().add(ipPanel);
		this.getChildren().add(portPanel);
		this.getChildren().add(passwordPanel);
		this.getChildren().add(ppPanel);
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

								String profilePicURL = this.txtProfilePicture.getText();
								if (profilePicURL.trim().isEmpty() || !((profilePicURL.startsWith("http://") || profilePicURL.startsWith("https://")) && (profilePicURL.endsWith(".png") || profilePicURL.endsWith(".jpg")))) {
									profilePicURL = null;
								} else {
									try {
										Image profilePicture = new Image(profilePicURL);
										if (profilePicture.getWidth() > Constants.PROFILE_PICTURE_SIZE || profilePicture.getHeight() > Constants.PROFILE_PICTURE_SIZE || profilePicture.getWidth() % 8 != 0 || profilePicture.getHeight() % 8 != 0) {
											FXUtilities.createErrorDialog("Profile picture cannot be larger than " + Constants.PROFILE_PICTURE_SIZE + "x" + Constants.PROFILE_PICTURE_SIZE + " and must be a factor of 8x8.", "Error", "Could not set profile picture").showAndWait();
											profilePicURL = null;
										}
									} catch (Exception ex) {
										ex.printStackTrace();
										FXUtilities.createErrorDialog("Failed to load the profile picture from URL:" + System.lineSeparator() + profilePicURL, "Error", "Could not load profile picture", Utilities.getThrowableAsString(ex)).showAndWait();
										profilePicURL = null;
									}
								}

								try {
									ClientPane client = new ClientPane(this.window, name, ipAddress, optionalPort.getAsInt(), password, profilePicURL);
									client.initStage();
									this.window.getStage().centerOnScreen();
								} catch (Exception ex) {
									ex.printStackTrace();
									System.exit(-1);
								}
							} else {
								FXUtilities.createErrorDialog("Please enter a valid port.", "Error", "Invalid port").showAndWait();
							}
						} else {
							FXUtilities.createErrorDialog("Please enter a port.", "Error", "Empty port").showAndWait();
						}
					} else {
						FXUtilities.createErrorDialog("Please enter a valid IP address (host).", "Error", "Invalid IP address").showAndWait();
					}
				} else {
					FXUtilities.createErrorDialog("Please enter a valid (alphanumeric) name.", "Error", "Invalid name").showAndWait();
				}
			} else {
				FXUtilities.createErrorDialog("Name length cannot be greater than 16.", "Error", "Invalid name").showAndWait();
			}
		} else {
			FXUtilities.createErrorDialog("Please enter a name.", "Error", "Empty name").showAndWait();
		}
	}

}
