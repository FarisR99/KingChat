package com.faris.kingchat.core.helper;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.logging.*;

public class PrettyLogger extends Handler {

	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.MEDIUM);

	private final File logsFolder;
	private File logFile;

	public PrettyLogger() {
		this.logsFolder = new File("logs");
		this.logFile = new File(this.logsFolder, "log-" + DATE_TIME_FORMATTER.format(LocalDateTime.now()).replace('/', '-').replace(':', '-').replace(' ', '-') + ".txt");
	}

	@Override
	public void publish(LogRecord record) {
		try {
			boolean isError = record.getThrown() != null;

			StringBuilder sbMessage = new StringBuilder("[");
			sbMessage.append(DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(record.getMillis()).atZone(ZoneId.systemDefault()).toLocalDateTime()));
			sbMessage.append("] [Thread ").append(record.getThreadID()).append('/').append(record.getLevel().getName()).append("] ");

			sbMessage.append(record.getMessage());
			if (isError) sbMessage.append(':');

			String message = sbMessage.toString();
			System.out.println(message);
			if (record.getThrown() != null) {
				record.getThrown().printStackTrace();
			}

			if (this.logFile != null) {
				if (!this.logFile.exists()) {
					try {
						if (!this.logsFolder.exists()) this.logsFolder.mkdirs();
						this.logFile.createNewFile();
					} catch (IOException ex) {
						System.out.println("Failed to create '" + this.logFile.getName() + "':");
						ex.printStackTrace();
						this.logFile = null;
						return;
					}
				}
				try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(this.logFile, true)))) {
					if (isError) {
						message += System.lineSeparator() + Utilities.getThrowableAsString(record.getThrown());
					}
					printWriter.println(message);
				} catch (Exception ignored) {
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws SecurityException {
	}

	public static Logger createLogger(String name) {
		Logger logger = Logger.getLogger(name);
		logger.setUseParentHandlers(false);
		logger.addHandler(new PrettyLogger());
		return logger;
	}

}
