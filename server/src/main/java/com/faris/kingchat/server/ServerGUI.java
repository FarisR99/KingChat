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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

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
			// System.exit(0);
		});

		stage.setTitle(Constants.NAME + " Server");
		stage.setMinWidth(330);
		stage.setMinHeight(225);

		stage.setScene(new Scene(this.createWindow(), 880, 550));

		stage.show();
		stage.centerOnScreen();

		int port = this.fetchPort();
		this.initServer(port);
	}

	private BorderPane createWindow() {
		BorderPane contentPane = new BorderPane();
		contentPane.setPadding(new Insets(10));
		this.populateContentPane(contentPane);
		this.txtInput.requestFocus();
		return contentPane;
	}

	private void populateContentPane(BorderPane contentPane) {
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
		this.lstUsers.setMaxWidth(100D);
		this.lstUsers.setEditable(false);
		Callback<ListView<String>, ListCell<String>> listCellFactory = this.lstUsers.getCellFactory();
		this.lstUsers.setCellFactory(param -> {
			ListCell<String> cell = null;
			if (listCellFactory != null) {
				cell = listCellFactory.call(param);
			}
			if (cell == null) {
				cell = new ListCell<>();
				cell.textProperty().bind(cell.itemProperty());
			}
			OnlineClientMenu onlineClientMenu = new OnlineClientMenu(this.serverWindow.getServer(), cell.itemProperty());
			cell.setContextMenu(onlineClientMenu);
			return cell;
		});
		this.lstUsers.setTooltip(new Tooltip("Online users."));

		contentPane.setRight(this.lstUsers);
	}

	private void initServer(int port) throws Exception {
		this.serverWindow = new ServerWindow(port, this);
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

	private int fetchPort() {
		List<String> rawParameters = this.getParameters().getRaw();
		int port = Integer.parseInt(rawParameters.get(0));
		if (!Boolean.valueOf(rawParameters.get(1))) {
			OptionalInt optionalPort = this.showPortDialog();
			port = optionalPort.isPresent() ? optionalPort.getAsInt() : 8192;
		}
		if (port < 0) port = 8192;
		return port;
	}

	public void removeUser(Client client) {
		Platform.runLater(() -> {
			this.lstUsers.getItems().remove(client.getName());
		});
	}

	private OptionalInt showPortDialog() {
		TextInputDialog portInputDialog = new TextInputDialog("8192");
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
