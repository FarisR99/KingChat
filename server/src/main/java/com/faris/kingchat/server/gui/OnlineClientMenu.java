package com.faris.kingchat.server.gui;

import com.faris.kingchat.server.Client;
import com.faris.kingchat.server.Server;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class OnlineClientMenu extends ContextMenu {

	private final Server server;
	private Client client;

	public OnlineClientMenu(Server server, ObjectProperty<String> itemProperty) {
		super();
		this.server = server;
		this.createMenuItems();

		this.setOnShowing(event -> {
			this.client = this.server.getClient(itemProperty.get());
			if (this.client == null) this.hide();
		});
	}

	private void createMenuItems() {
		MenuItem itemInfo = new MenuItem("Info");
		itemInfo.setOnAction(event -> {
			if (this.client == null) return;
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Client information");
			alert.setHeaderText(client.getName());
			alert.setContentText(client.getInfo());
			alert.show();
		});
		this.getItems().add(itemInfo);

		this.getItems().add(new SeparatorMenuItem());

		MenuItem itemKick = new MenuItem("Kick");
		itemKick.setOnAction(event -> {
			if (this.client == null) return;
			this.server.disconnectClient(this.client.getUniqueId(), 2);
		});
		this.getItems().add(itemKick);

		MenuItem itemBanIP = new MenuItem("Ban IP");
		itemBanIP.setOnAction(event -> {
			if (this.client == null) return;
			this.server.banIP(this.client.getAddress().getHostName());
		});
		this.getItems().add(itemBanIP);
	}

}
