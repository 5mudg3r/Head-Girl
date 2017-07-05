package me.smudja.appointments;

public enum AppointmentType {
	
	RECURRING,
	SINGLE;
	
	public static AppointmentType getType(String appointmentInfo) {
		if(appointmentInfo.compareTo("r") == 0) {
			return RECURRING;
		}
		else if(appointmentInfo.compareTo("s") == 0) {
			return SINGLE;
		}
		else {
			return null;
		}
	}
}
