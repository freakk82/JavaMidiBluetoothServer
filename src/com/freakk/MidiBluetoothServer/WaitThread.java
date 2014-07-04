package com.freakk.MidiBluetoothServer;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.sound.midi.MidiUnavailableException;

public class WaitThread implements Runnable{
	Thread processThread;
	private ProcessConnectionThread procConThr;
	private int deviceIndex;
	private StreamConnection connection;
	private SimpleSynth _dev;
	/** Constructor */
	
	public WaitThread(SimpleSynth s) {
		_dev = s;
	}
	
	@Override
	public void run() {
		try {
			waitForConnection();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	/** Waiting for connection from devices 
	 * @throws InterruptedException */
	private void waitForConnection() throws InterruptedException {
		// retrieve the local Bluetooth device object
		LocalDevice local = null;
		
		StreamConnectionNotifier notifier;
		connection = null;
		
		// setup the server to listen for connection
		try {
			local = LocalDevice.getLocalDevice();
			local.setDiscoverable(DiscoveryAgent.GIAC);
			
			UUID uuid = new UUID("5f79b0e0cc3111e19b230800200c9a66", false);
			System.out.println(uuid.toString());
			
            String url = "btspp://localhost:" + uuid.toString() + ";name=RemoteBluetooth";
            notifier = (StreamConnectionNotifier)Connector.open(url);
		} catch (Exception e) {
            e.printStackTrace();
            return;
        }
		
		// waiting for connection
		while(true) {
			try {
				System.out.println("waiting for connection...");
	            connection = notifier.acceptAndOpen();
	            procConThr = new ProcessConnectionThread(connection, _dev);
	            processThread = new Thread(procConThr);
	            processThread.start();
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			
		}

	}
	
}
