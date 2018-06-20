package com.faris.kingchat.client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class OnlineUsersGUI extends JFrame {

	private JList<String> userList = null;

	public OnlineUsersGUI() {
		this.setTitle("Online users");
		this.setPreferredSize(new Dimension(200, 320));
		this.pack();
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(null);

		JPanel contentPane = this.initContentPane();
		this.populateContentPane(contentPane);
	}

	private JPanel initContentPane() {
		JPanel contentPane = (JPanel) this.getContentPane();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		return contentPane;
	}

	private void populateContentPane(JPanel contentPane) {
		this.userList = new JList<>();
		contentPane.add(new JScrollPane(this.userList), BorderLayout.CENTER);
	}

	public void updateUsers(String[] users) {
		this.userList.setListData(users);
	}

}
