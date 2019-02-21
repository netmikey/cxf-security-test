package org.example;

import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class TestAppender extends AppenderBase<LoggingEvent> {
	static List<LoggingEvent> events = new ArrayList<>();

	@Override
	protected void append(LoggingEvent e) {
		events.add(e);
	}

	public static boolean contains(String messagePart) {
		for(LoggingEvent e : events) {
			if (e.getMessage().contains(messagePart)) {
				return true;
			}
		}
		return false;
	}
}
