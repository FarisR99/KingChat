package com.faris.kingchat.server;

import com.faris.kingchat.core.Constants;
import com.faris.kingchat.core.helper.PrettyLogger;
import com.faris.kingchat.core.helper.Utilities;
import com.faris.kingchat.server.gui.OnlineClientMenu;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.*;

public class ServerGUI extends Application {

	private Stage stage = null;

	private ServerWindow serverWindow = null;
	private TextArea txtTerminal = null;
	private TextField txtInput = null;

	private ListView<String> lstUsers = null;

	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		stage.setOnCloseRequest(event -> {
			if (this.serverWindow != null) {
				this.serverWindow.getServer().destroyServer();
			}
			ServerGUI.this.stage = null;
			Platform.exit();
			System.exit(0);
		});

		stage.setTitle(Constants.NAME + " Server");
		stage.setMinWidth(330);
		stage.setMinHeight(225);

		stage.setScene(new Scene(this.createWindow(), 880, 550));

		stage.show();
		stage.centerOnScreen();

		List<String> rawParameters = this.getParameters().getRaw();
		int port = this.fetchPort(rawParameters);
		String password = rawParameters.size() > 2 ? rawParameters.get(2) : null;
		this.initServer(port, password);
	}

	private BorderPane createWindow() {
		BorderPane scenePane = new BorderPane();

		BorderPane contentPane = new BorderPane();
		contentPane.setPadding(new Insets(10));

		this.populateContentPane(scenePane, contentPane);
		this.txtInput.requestFocus();
		return scenePane;
	}

	private void populateContentPane(BorderPane scenePane, BorderPane contentPane) {
		this.txtTerminal = new TextArea();
		this.txtTerminal.setWrapText(true);
		this.txtTerminal.setEditable(false);
		this.txtTerminal.setContextMenu(new TextPopupMenu(this.txtTerminal));
		ScrollPane scrollPane = new ScrollPane(this.txtTerminal);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);
		contentPane.setCenter(scrollPane);

		BorderPane bottomBar = new BorderPane();
		bottomBar.setPadding(new Insets(5, 0, 0, 0));

		this.txtInput = new TextField();
		this.txtInput.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				this.doSend();
			}
		});
		bottomBar.setCenter(this.txtInput);
		BorderPane.setMargin(this.txtInput, new Insets(0, 5, 0, 0));

		Button btnSend = new Button("Send");
		btnSend.setOnAction(event -> {
			this.doSend();
		});
		bottomBar.setRight(btnSend);
		BorderPane.setMargin(btnSend, new Insets(0, 0, 0, 5));

		contentPane.setBottom(bottomBar);

		this.lstUsers = new ListView<>();
		this.lstUsers.setMaxWidth(125D);
		this.lstUsers.setEditable(false);
		this.lstUsers.setCellFactory(param -> {
			ListCell<String> cell = new ListCell<String>() {
				private ImageView imageView = new ImageView();

				{
					this.imageView.setFitWidth(Constants.PROFILE_PICTURE_SIZE);
					this.imageView.setFitHeight(Constants.PROFILE_PICTURE_SIZE);
					this.imageView.setPreserveRatio(true);
					this.imageView.setSmooth(true);
				}

				@Override
				protected void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						this.setText(null);
						this.setGraphic(null);
					} else {
						this.setText(item);
						Client client = serverWindow.getServer().getClient(item);
						if (client != null) {
							this.imageView.setImage(client.getProfilePicture());
							if (client.getProfilePicture() != null) {
								this.setGraphic(this.imageView);
								return;
							}
						} else {
							this.imageView.setImage(null);
						}
						this.setGraphic(null);
					}
				}
			};

			OnlineClientMenu onlineClientMenu = new OnlineClientMenu(this.serverWindow.getServer(), cell.itemProperty());
			cell.setContextMenu(onlineClientMenu);
			return cell;
		});
		this.lstUsers.setTooltip(new Tooltip("Online users."));

		contentPane.setRight(this.lstUsers);

		// Menu bar

		// Edit item

		MenuItem itemPassword = new MenuItem("Password");
		itemPassword.setOnAction(event -> {
			TextInputDialog dialogPassword = new TextInputDialog(this.serverWindow.getServer().getConfigManager().getPassword());
			dialogPassword.setTitle("Password");
			dialogPassword.setHeaderText("Please enter the new password");
			dialogPassword.setContentText("");

			Optional<String> optionalPassword = dialogPassword.showAndWait();
			if (optionalPassword.isPresent()) {
				String password = optionalPassword.get();
				if (password.isEmpty()) password = null;
				this.serverWindow.getServer().getConfigManager().setPassword(password);
				System.out.println("Set password to '" + password + "'.");
				this.appendLine("Set password to '" + password + "'.");
			}
		});

		Menu menuEdit = new Menu("Edit");
		menuEdit.getItems().add(itemPassword);

		// Exit item

		MenuItem itemExit = new MenuItem("Exit");
		itemExit.setOnAction(event -> {
			this.serverWindow.getServer().shutdown();
		});

		Menu menuWindow = new Menu("Window");
		menuWindow.getItems().add(itemExit);

		scenePane.setTop(new MenuBar(menuEdit, menuWindow));
		scenePane.setCenter(contentPane);
	}

	private void initServer(int port, String password) throws Exception {
		this.serverWindow = new ServerWindow(port, password, this);
		this.serverWindow.getLogger().addHandler(new Handler() {
			@Override
			public void publish(LogRecord record) {
				try {
					boolean isError = record.getThrown() != null;

					StringBuilder sbMessage = new StringBuilder("[");
					sbMessage.append(PrettyLogger.DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(record.getMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime()));
					sbMessage.append("] ");

					sbMessage.append(record.getMessage());
					if (isError) sbMessage.append(':');

					String message = sbMessage.toString();
					appendLine(message);
					if (record.getThrown() != null) {
						appendLine(Utilities.getThrowableAsString(record.getThrown()));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void flush() {
				clearLog();
			}

			@Override
			public void close() throws SecurityException {
			}
		});
		String serverIconURL = this.serverWindow.getServer().getConfigManager().getServerIconURL();
		if (serverIconURL != null && ((serverIconURL.startsWith("http://") || serverIconURL.startsWith("https://")) && (serverIconURL.endsWith(".png") || serverIconURL.endsWith(".jpg")))) {
			this.stage.getIcons().clear();
			try {
				this.stage.getIcons().add(new Image(serverIconURL));
			} catch (Exception ex) {
				this.serverWindow.getLogger().log(Level.WARNING, "Failed to set server icon to '" + serverIconURL + "'", ex);
			}
		}
	}

	public void addUser(Client client) {
		Platform.runLater(() -> {
			this.lstUsers.getItems().add(client.getName());
		});
	}

	public void append(String text) {
		this.txtTerminal.setText(this.txtTerminal.getText() + text);
	}

	public void appendLine(String text) {
		this.txtTerminal.setText(this.txtTerminal.getText() + text + System.lineSeparator());
	}

	public void clearLog() {
		this.txtTerminal.setText("");
	}

	public void close() {
		this.stage.close();
	}

	private void doSend() {
		String message = this.txtInput.getText().trim();
		this.txtInput.setText("");
		this.serverWindow.getServer().processInput(message);
	}

	private int fetchPort(List<String> parameters) {
		int port = Integer.parseInt(parameters.get(0));
		if (!Boolean.valueOf(parameters.get(1))) {
			OptionalInt optionalPort = this.showPortDialog();
			port = optionalPort.isPresent() ? optionalPort.getAsInt() : Constants.DEFAULT_PORT;
		}
		if (port < 0) port = Constants.DEFAULT_PORT;
		return port;
	}

	public void removeUser(Client client) {
		Platform.runLater(() -> {
			this.lstUsers.getItems().remove(client.getName());
		});
	}

	public void updateUser(Client client) {
		Platform.runLater(() -> {
			if (this.lstUsers.getItems().contains(client.getName())) {
				int clientIndex = this.lstUsers.getItems().indexOf(client.getName());
				this.lstUsers.getItems().remove(clientIndex);
				this.lstUsers.getItems().add(clientIndex, client.getName());
			}
		});
	}

	private OptionalInt showPortDialog() {
		TextInputDialog portInputDialog = new TextInputDialog(String.valueOf(Constants.DEFAULT_PORT));
		portInputDialog.setTitle("Port");
		portInputDialog.setHeaderText("Please enter the port for the server");
		portInputDialog.setContentText("");

		OptionalInt optionalInt = OptionalInt.empty();
		while (!optionalInt.isPresent()) {
			Optional<String> result = portInputDialog.showAndWait();
			if (result.isPresent()) {
				optionalInt = Utilities.parseInt(result.get());
			} else {
				break;
			}
		}
		return optionalInt;
	}

	// Subclasses

	private static class TextPopupMenu extends ContextMenu {
		private final TextInputControl parentComponent;

		TextPopupMenu(TextInputControl textComponent) {
			this.parentComponent = textComponent;
			this.createItems();
		}

		private void createItems() {
			this.createCopyItem();
			this.createClearItem();
			this.createSelectAllItem();
		}

		private void createCopyItem() {
			MenuItem copyItem = new MenuItem("Copy");
			copyItem.setOnAction(event -> {
				this.parentComponent.copy();
			});
			this.getItems().add(copyItem);
		}

		private void createClearItem() {
			MenuItem clearItem = new MenuItem("Clear");
			clearItem.setOnAction(event -> {
				this.parentComponent.clear();
			});
			this.getItems().add(clearItem);
		}

		private void createSelectAllItem() {
			this.getItems().add(new SeparatorMenuItem());
			MenuItem selectItem = new MenuItem("Select all");
			selectItem.setOnAction(event -> {
				this.parentComponent.selectAll();
			});
			this.getItems().add(selectItem);
		}
	}

}
