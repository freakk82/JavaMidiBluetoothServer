package com.freakk.MidiBluetoothServer;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

public class Gui {

	protected Shell shlFreakkMidiBridge;
	private Combo MidiOutCombo;
	private static int MidiOutComboIndex;
	private static String ActiveDev;
	private MidiDevice.Info[] info;
	List<MidiDevice> device;
	List<String> deviceDetails;
	private Label MidiDeviceDetailsLabel;
	WaitThread waitThr;
	private static Gui window;
	private final SimpleSynth MidiOut = new SimpleSynth();
	
	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// Load Settings
			Properties loadProps = new Properties();
			try {
				loadProps
						.loadFromXML(new FileInputStream("FrkMidiSettings.xml"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			MidiOutComboIndex = Integer.parseInt(loadProps.getProperty(
					"MidiOutIndex").toString());
			ActiveDev = loadProps.getProperty("MidiOutDevice");
			window = new Gui();
			window.open();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 * 
	 * @throws InterruptedException
	 * @throws MidiUnavailableException
	 */
	public void open() throws InterruptedException, MidiUnavailableException {
		Display display = Display.getDefault();
		createContents();
		Image small = new Image(display,"midi.png");
		shlFreakkMidiBridge.setImage(small);
		shlFreakkMidiBridge.open();
		shlFreakkMidiBridge.layout();
		System.out.println("opened");
		waitThr = new WaitThread(MidiOut);
		Thread waitThread = new Thread(waitThr);
		waitThread.start();
		while (!shlFreakkMidiBridge.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
		System.exit(0);
	}

	/**
	 * Create contents of the window.
	 * 
	 * @throws MidiUnavailableException
	 */
	protected void createContents() throws MidiUnavailableException {
		shlFreakkMidiBridge = new Shell();
		shlFreakkMidiBridge.setSize(311, 300);
		shlFreakkMidiBridge.setText("Freakk Midi Bridge");
		
		// MIDI Out Selection (MIDI loopback driver)
		Label lblMidiOut = new Label(shlFreakkMidiBridge, SWT.NONE);
		lblMidiOut.setBounds(10, 19, 55, 15);
		lblMidiOut.setText("MIDI Out");

		MidiOutCombo = new Combo(shlFreakkMidiBridge, SWT.NONE);

		MidiOutCombo.setBounds(10, 40, 252, 23);
		// Get MIDI device list
		info = MidiSystem.getMidiDeviceInfo();
		device = new ArrayList<MidiDevice>();
		deviceDetails = new ArrayList<String>();
		int j=0;
		for (int i = 0; i < info.length; i++) {
			MidiDevice dev = MidiSystem.getMidiDevice(info[i]);
			if (info[i].getDescription().compareTo("External MIDI Port") == 0 ) {
				deviceDetails.add("Device ID: " + j);
				deviceDetails.set(j, deviceDetails.get(j) + "\nName: " + info[i].getName());
				deviceDetails.set(j, deviceDetails.get(j) + "\nDescription: " + info[i].getDescription());
				device.add(dev);
				deviceDetails.set(j, deviceDetails.get(j) + "\nDevice: " + device.get(j));
				MidiOutCombo.add(info[i].getName());
				j++;
			}
		}

		MidiOutCombo.select(MidiOutComboIndex);

		Button btnStart = new Button(shlFreakkMidiBridge, SWT.NONE);
		btnStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				try {
					if ( !SaveProperties() ) {
						MidiDeviceDetailsLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
						MidiDeviceDetailsLabel.setText("Device Not Compatible");
					}
						
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnStart.setBounds(10, 206, 75, 25);
		btnStart.setText("Start");

		MidiDeviceDetailsLabel = new Label(shlFreakkMidiBridge, SWT.NONE);
		MidiDeviceDetailsLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		MidiDeviceDetailsLabel.setBounds(10, 66, 252, 123);
		// MidiDeviceDetailsLabel.setText("...");
		int index = MidiOutCombo.getSelectionIndex();
		if(index>=0){
			MidiDeviceDetailsLabel.setText(deviceDetails.get(MidiOutCombo.getSelectionIndex()));
		}
		MidiOutCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e){
				int index = MidiOutCombo.getSelectionIndex();
				MidiDeviceDetailsLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
				MidiDeviceDetailsLabel.setText(deviceDetails.get(index));
				MidiOutComboIndex = index;
			}
		});
		/*
		try {
			SaveProperties();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
	}

	private boolean SaveProperties() throws FileNotFoundException, IOException {
		int index = MidiOutCombo.getSelectionIndex();
		try {
			if( MidiOut.SetDevice(device.get(index).toString()) ){
				// Save Settings
				MidiDeviceDetailsLabel.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
				Properties saveProps = new Properties();
				saveProps.setProperty("MidiOutIndex", "" + index);
				saveProps.setProperty("MidiOutDevice", "" + device.get(index));
				saveProps.setProperty("MidiOutDevice", "" + ActiveDev);
				saveProps.storeToXML(new FileOutputStream("FrkMidiSettings.xml"), "");
			} else return false;
		} catch(MidiUnavailableException e) {
		      e.printStackTrace();
		      //System.exit(1);
		      return false;
		    }
		return true;
	}

}
