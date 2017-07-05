package me.smudja.appointments;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Appointment {
	
	private String visitor;
	
	private Date time;
	
	private AppointmentType type;
	
	private String message;
	
	private boolean isExpired;

	public Appointment(String line) {
		String[] appointmentInfo = line.split("\\|");
		
		visitor = appointmentInfo[0];
			
		DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.ENGLISH);
		try {
			time = dateFormat.parse(appointmentInfo[1]);
		} catch (ParseException e) {}
		
		isExpired = time.before(new Date());
		
		type = AppointmentType.getType(appointmentInfo[2]);
		
		message = appointmentInfo[3];
	}
	
	public String getVisitor() {
		return visitor;
	}
	
	public Date getDate() {
		return time;
	}
	
	public AppointmentType getType() {
		return type;
	}
	
	public String getMessage() {
		return message;
	}
	
	public boolean isExpired() {
		return isExpired;
	}

}
