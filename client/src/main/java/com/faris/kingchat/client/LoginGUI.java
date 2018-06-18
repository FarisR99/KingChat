package com.faris.kingchat.client;

import com.faris.kingchat.core.helper.Utilities;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class LoginGUI extends JFrame {

	private JTextField txtName;
	private JTextField txtAddress;
	private JTextField txtPort;

	public LoginGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		this.setTitle("Login");
		this.setPreferredSize(new Dimension(260, 280));
		this.setResizable(false);
		this.pack();
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);

		JPanel contentPane = this.initContentPane();
		KeyListener enterKeyListener = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
			}
		};
		this.populateContentPane(contentPane, enterKeyListener);
	}

	private JPanel initContentPane() {
		JPanel contentPane = (JPanel) this.getContentPane();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		// contentPane.setLayout(null);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		return contentPane;
	}

	private void populateContentPane(JPanel contentPane, KeyListener enterKeyListener) {
		// Name panel

		JLabel lblName = new JLabel();
		lblName.setText("Name:");
		lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.txtName = new JTextField();
		this.txtName.addKeyListener(enterKeyListener);
		this.txtName.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel namePanel = new JPanel();
		namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
		namePanel.add(lblName);
		namePanel.add(Box.createVerticalStrut(5));
		namePanel.add(this.txtName);

		// IP panel

		JLabel lblAddress = new JLabel();
		lblAddress.setText("IP Address:");
		lblAddress.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.txtAddress = new JTextField();
		this.txtAddress.addKeyListener(enterKeyListener);
		this.txtAddress.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel ipPanel = new JPanel();
		ipPanel.setLayout(new BoxLayout(ipPanel, BoxLayout.Y_AXIS));
		ipPanel.add(lblAddress);
		ipPanel.add(Box.createVerticalStrut(5));
		ipPanel.add(this.txtAddress);

		// Port panel

		JLabel lblPort = new JLabel();
		lblPort.setText("Port:");
		lblPort.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.txtPort = new JTextField();
		this.txtPort.addKeyListener(enterKeyListener);
		this.txtPort.setAlignmentX(Component.CENTER_ALIGNMENT);

		JPanel portPanel = new JPanel();
		portPanel.setLayout(new BoxLayout(portPanel, BoxLayout.Y_AXIS));
		portPanel.add(lblPort);
		portPanel.add(Box.createVerticalStrut(5));
		portPanel.add(this.txtPort);

		// Content pane

		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(e -> this.doLogin());
		btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

		contentPane.add(Box.createVerticalStrut(20));
		contentPane.add(namePanel);
		contentPane.add(Box.createVerticalStrut(20));
		contentPane.add(ipPanel);
		contentPane.add(Box.createVerticalStrut(20));
		contentPane.add(portPanel);
		contentPane.add(Box.createVerticalStrut(20));
		contentPane.add(btnLogin);
		contentPane.add(Box.createVerticalStrut(20));
	}

	public void doLogin() {
		String name = this.txtName.getText().trim();
		String ipAddress = this.txtAddress.getText().trim();
		String port = this.txtPort.getText().trim();
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
								this.dispose();
								Client client = new Client(name, ipAddress, optionalPort.getAsInt());
								client.setVisible(true);
							} else {
								JOptionPane.showMessageDialog(null, "Please enter a valid port.", "Invalid port", JOptionPane.ERROR_MESSAGE);
							}
						} else {
							JOptionPane.showMessageDialog(null, "Please enter a port.", "Empty port", JOptionPane.ERROR_MESSAGE);
						}
					} else {
						JOptionPane.showMessageDialog(null, "Please enter a valid IP address (host).", "Invalid IP address", JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Please enter a valid (alphanumeric) name.", "Invalid name", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(null, "Name length cannot be greater than 16.", "Invalid name", JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Please enter a name.", "Empty name", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				LoginGUI loginGUI = new LoginGUI();
				loginGUI.setVisible(true);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

}
