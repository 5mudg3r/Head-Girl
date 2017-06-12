/**
 * 
 */
package me.smudja.gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import me.smudja.updater.Update;
import me.smudja.updater.UpdateManager;
import me.smudja.utility.LogLevel;
import me.smudja.utility.Reporter;

/**
 * Deals with removing expired updates and adding new updates.
 * @author smithl
 *
 */
public enum Updater {
	
	INSTANCE;
	
	/**
	 * updates - stores all current updates
	 */
	private ArrayList<Update> updates;
	
	/**
	 * index of update we need to display
	 */
	public int updateToDisplay = -1;
	
	/**
	 * instance of UpdateManager
	 * 
	 * @see UpdateManager
	 */
	private UpdateManager updateManager = UpdateManager.getInstance();
	
	Updater() {
		updates = new ArrayList<Update>();
	}
	
	public static Updater getInstance() {
		return INSTANCE;
	}

	/**
	 * Updates array with new updates, removes old updates and returns next update node to be displayed
	 * 
	 * @return update node to be displayed
	 */
	public synchronized Node update() {
		
		long currentTime = System.currentTimeMillis();
		
		// remove expired updates
		Iterator<Update> iterator = updates.iterator();
		while (iterator.hasNext()) {
			Update item = iterator.next();
			if((currentTime - item.getRawDate()) > HeadGirl.getMessageLife()) {
				iterator.remove();
			}
		}
		
		// add any new updates
		updates.addAll(Arrays.asList(updateManager.getUpdates()));
			
			/* 
			 * The block of code below checks if there are any messages and if
			 * there are none, sets the messageToDisplay to -1 as it will be incremented
			 * by 1 when there is one in the else clause.
			 * 
			 * the second if statement cycles the messageToDisplay back to array record 0
			 * if it has reached the end of the array.
			 */

		if (updates.size() == 0) {
			updateToDisplay = -1;

			return getDefaultNode();
		} else {
			updateToDisplay += 1;
			if (updateToDisplay >= updates.size()) {
				updateToDisplay = 0;
			}
		}

		return updates.get(updateToDisplay).getNode();
	}

	/**
	 * Creates a node displaying the current system date and next visitor information
	 * @return node containing current system date and next visitor information
	 */
	private Node getDefaultNode() {

		SimpleDateFormat format = new SimpleDateFormat("EEEE dd MMMM yyyy");

		VBox defaultNode = new VBox(60);
		defaultNode.setAlignment(Pos.CENTER);
		defaultNode.setId("center");

		Text text = new Text(format.format(System.currentTimeMillis()));

		text.setTextAlignment(TextAlignment.CENTER);
		
		text.setId("bold-text");
		text.setFont(Font.font(HeadGirl.getFontSize()));
		
		defaultNode.getChildren().add(text);
		
		VBox visitorBox = new VBox(10);
		visitorBox.setId("label");
		visitorBox.setAlignment(Pos.CENTER);
		
		List<String> appointments;
		
		try {
			Path dir = Paths.get("").toAbsolutePath();
			if (!Files.exists(dir.resolve("appointments.txt"))) {
				Reporter.report("No appointments file found. Not displaying next visitor information", LogLevel.INFO);
			}
			appointments = Files.readAllLines(dir.resolve("appointments.txt"));
			Iterator<String> iterator = appointments.iterator();
			while (iterator.hasNext()) {
				String line = iterator.next();
				if(line.startsWith("#")) {
					iterator.remove();
				}
			}
		}
		catch(IOException exc) {
			Reporter.report("Error reading appointments information. Not displaying visitor information", LogLevel.MINOR);
			return defaultNode;
		}
		
		String[] appointmentInfo = appointments.get(0).split("\\|");
		
		Label visitor = new Label("[Next Visitor]");
		Label visitorName = new Label(appointmentInfo[0]);
	
		DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.ENGLISH);
		Date date;
		try {
			date = dateFormat.parse(appointmentInfo[1]);
		} catch (ParseException e) {
			Reporter.report("Unable to parse date from appointments file", LogLevel.MINOR);
			return defaultNode;
		}

		SimpleDateFormat visitorFormat = new SimpleDateFormat("EEE d MMM yyyy 'at' HH:mm");
		Label visitorDate = new Label(visitorFormat.format(date));
		
		visitorBox.getChildren().addAll(visitor, visitorName, visitorDate);
		
		for(Node node : visitorBox.getChildren()) {
			((Label) node).setFont(Font.font(HeadGirl.getFontSize()));
		}
		
		defaultNode.getChildren().add(visitorBox);
		
		return defaultNode;
	}
}
