package com.freakk.MidiBluetoothServer;

import java.io.InputStream;

import javax.microedition.io.StreamConnection;
import javax.sound.midi.InvalidMidiDataException;

public class ProcessConnectionThread implements Runnable{

	private StreamConnection mConnection;
	//Constant that indicate command from devices
	private static final int EXIT_CMD = -1;
	private SimpleSynth MidiOut;
	
	public ProcessConnectionThread(StreamConnection connection, SimpleSynth s)
	{
		mConnection = connection;
		MidiOut =  s;
	}
	
	@Override
	public void run() {
		try {
			
			// prepare to receive data
			InputStream inputStream = mConnection.openInputStream();
			System.out.println("waiting for input");
			
	        while (true) {
	        	
//	        	int command = inputStream.read();
	        	int[] command= new int[3];
	        	command[0] = inputStream.read(); //status byte
	        	command[1] = inputStream.read(); //noteOn byte
	        	command[2] = inputStream.read(); //velocity byte
	        	
	        	if (command[0] == EXIT_CMD)
	        	{	
	        		System.out.println("finish process");
	        		break;
	        	}
	        	
	        	processCommand(command);
	        	
        	}
        } catch (Exception e) {
    		e.printStackTrace();
    	}
		
	}
	
	/**
	 * Process the command from client
	 * @param command the command code
	 */
	private void processCommand(int[] command) throws InvalidMidiDataException{
		try {
	    		System.out.println("MIDI!");
	    		MidiOut.Play(command);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
