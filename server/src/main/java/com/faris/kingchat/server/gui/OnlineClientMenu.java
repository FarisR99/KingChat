package com.faris.kingchat.server.gui;

import com.faris.kingchat.core.helper.FXUtilities;
import com.faris.kingchat.server.Client;
import com.faris.kingchat.server.Server;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class OnlineClientMenu extends ContextMenu {

	private final Server server;
	private Client client;

	private MenuItem itemMuteIP = new MenuItem("Mute IP");
	private MenuItem itemBanIP = new MenuItem("Ban IP");

	public OnlineClientMenu(Server server, ObjectProperty<String> itemProperty) {
		super();
		this.server = server;
		this.createMenuItems();

		this.setOnShowing(event -> {
			this.client = this.server.getClient(itemProperty.get());
			if (this.client == null) this.hide();
			String ipAddress = this.client.getAddress().getHostName();
			if (this.server.getConfigManager().isMuted(ipAddress)) {
				this.itemMuteIP.setText("Unmute IP");
			} else {
				this.itemMuteIP.setText("Mute IP");
			}
			if (this.server.getConfigManager().isBanned(ipAddress)) {
				this.itemBanIP.setText("Unban IP");
			} else {
				this.itemBanIP.setText("Ban IP");
			}
		});
	}

	private void createMenuItems() {
		MenuItem itemInfo = new MenuItem("Info");
		itemInfo.setOnAction(event -> {
			if (this.client == null) return;
			FXUtilities.createMessageDialog(client.getInfo(), "Client information", client.getName()).show();
		});
		this.getItems().add(itemInfo);

		this.getItems().add(new SeparatorMenuItem());

		MenuItem itemKick = new MenuItem("Kick");
		itemKick.setOnAction(event -> {
			if (this.client == null) return;
			this.server.disconnectClient(this.client.getUniqueId(), 2);
		});
		this.getItems().add(itemKick);

		this.itemMuteIP.setOnAction(event -> {
			if (this.client == null) return;
			String ipAddress = this.client.getAddress().getHostName();
			if (!this.server.getConfigManager().isMuted(ipAddress)) {
				this.server.muteIP(ipAddress);
			} else {
				this.server.unmuteIP(ipAddress);
			}
		});
		this.getItems().add(this.itemMuteIP);

		this.itemBanIP.setOnAction(event -> {
			if (this.client == null) return;
			String ipAddress = this.client.getAddress().getHostName();
			if (!this.server.getConfigManager().isBanned(ipAddress)) {
				this.server.banIP(ipAddress);
			} else {
				this.server.getConfigManager().unbanIP(ipAddress);
			}
		});
		this.getItems().add(this.itemBanIP);
	}

}
