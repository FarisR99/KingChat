package com.faris.kingchat.client;

import javafx.application.Application;
import javafx.stage.Stage;

public class ClientWindow extends Application {

	private Stage stage = null;

	@Override
	public void start(Stage stage) {
		this.stage = stage;

		LoginPane loginPane = new LoginPane(this);
		loginPane.initStage();

		stage.show();
		stage.centerOnScreen();
	}

	public Stage getStage() {
		return this.stage;
	}

	public static void main(String[] args) {
		launch(args);
	}

}
