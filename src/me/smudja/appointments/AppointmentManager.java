package me.smudja.appointments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import me.smudja.gui.HeadGirl;
import me.smudja.utility.LogLevel;
import me.smudja.utility.Reporter;

public class AppointmentManager {
	
	private AppointmentManager() {
		// TODO Auto-generated constructor stub
	}
	
	public static VBox getNode() {
		VBox visitorBox = new VBox(10);
		visitorBox.setId("label");
		visitorBox.setAlignment(Pos.CENTER);
		
		Appointment nextAppointment = getNextAppointment();
		
		if(nextAppointment == null) {
			return null;
		}
		
		Label visitor = new Label("[Next Visitor]");
		Label visitorName = new Label(nextAppointment.getVisitor());
	
		SimpleDateFormat visitorFormat = new SimpleDateFormat("EEE d MMM yyyy 'at' HH:mm");
		Label visitorDate = new Label(visitorFormat.format(nextAppointment.getDate()));
		
		Label message = new Label(nextAppointment.getMessage());
		
		visitorBox.getChildren().addAll(visitor, visitorName, visitorDate, message);
		
		for(Node node : visitorBox.getChildren()) {
			((Label) node).setFont(Font.font(HeadGirl.getFontSize()));
		}
		
		return visitorBox;
	}

	private static Appointment getNextAppointment() {
		List<String> lines;
		ArrayList<Appointment> appointments = new ArrayList<Appointment>();
		
		try {
			Path dir = Paths.get("").toAbsolutePath();
			if (!Files.exists(dir.resolve("appointments.txt"))) {
				Reporter.report("No appointments file found. Not displaying next visitor information", LogLevel.INFO);
				return null;
			} 
			else {
			lines = Files.readAllLines(dir.resolve("appointments.txt"));
			Iterator<String> iterator = lines.iterator();
			while (iterator.hasNext()) {
				String line = iterator.next();
				if(line.startsWith("#")) {
					iterator.remove();
				}
			}
			}
		}
		catch(IOException exc) {
			Reporter.report("Error reading appointments information. Not displaying visitor information", LogLevel.MINOR);
			return null;
		}
		
		if(lines.isEmpty()) {
			return null;
		}
		
		for(String line : lines) {
			Appointment appointment = new Appointment(line);
			if(!appointment.isExpired()) {
				appointments.add(appointment);
			}
		}
		
		if(appointments.isEmpty()) {
			return null;
		}
		
		Appointment nextAppointment = appointments.get(0);
		for(Appointment a : appointments) {
			if(a.getDate().before(nextAppointment.getDate())) {
				nextAppointment = a;
			}
		}
		
		return nextAppointment;
	}

}
