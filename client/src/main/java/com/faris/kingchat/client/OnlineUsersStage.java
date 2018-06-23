package com.faris.kingchat.client;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.*;


public class OnlineUsersStage extends Stage {

	private ListView<String> userList = null;

	public OnlineUsersStage() {
		this.setTitle("Online users");
		this.setOnCloseRequest(event -> {
			event.consume();
			this.hide();
		});

		BorderPane contentPane = this.initContentPane();
		this.setScene(new Scene(contentPane, 300, 400));
		this.populateContentPane(contentPane);
	}

	private BorderPane initContentPane() {
		BorderPane contentPane = new BorderPane();
		contentPane.setPadding(new Insets(5));
		return contentPane;
	}

	private void populateContentPane(BorderPane contentPane) {
		this.userList = new ListView<>();
		contentPane.setCenter(this.userList);
	}

	public void updateUsers(String[] users) {
		String[] currentUsers = this.userList.getItems().toArray(new String[0]);
		if (!Arrays.equals(currentUsers, users)) {
			this.userList.getItems().clear();
			this.userList.getItems().addAll(users);
		}
	}

}
