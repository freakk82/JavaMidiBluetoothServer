package com.freakk.MidiBluetoothServer;


import javax.sound.midi.*;

public class SimpleSynth {

	Synthesizer synth;
	Receiver rcvr;
	Transmitter trns;
	int MidiDevIndex;
	MidiDevice device;
	MidiDevice.Info[] MidiDevices;
	MidiDevice.Info[] info;
	
	public SimpleSynth() {

  }
	
	public synchronized boolean SetDevice( String dev ) throws MidiUnavailableException{
		try {

	      info = MidiSystem.getMidiDeviceInfo();
	      for (int i = 0; i < info.length; i++) {
	    	  
	    	  device = MidiSystem.getMidiDevice(info[i]);
	    	  if(device.toString().compareTo(dev) == 0 ){
	    		  device.open();
	    		  rcvr = device.getReceiver();
	    	  }
	      }
	      
		} catch(MidiUnavailableException e) {
		      e.printStackTrace();
		      //System.exit(1);
		      return false;
		    }
		return true;
	}

	private MidiMessage getNoteOnMessage(int note) {
		return getMessage(ShortMessage.NOTE_ON, note);
	}

	private MidiMessage getNoteOffMessage(int note) {
		return getMessage(ShortMessage.NOTE_OFF, note);
	}

	private MidiMessage getMessage(int cmd, int note) {
		try {
			ShortMessage msg = new ShortMessage();
			msg.setMessage(cmd, 0, note, 60);

			return (MidiMessage) msg;
		} catch (InvalidMidiDataException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	public void Play(int[] msg) throws InvalidMidiDataException {

		ShortMessage out = new ShortMessage();
		System.out.println("Playing " + msg[0] + msg[1] + msg[2]);
		out.setMessage(msg[0], msg[1], msg[2]);
		//
		rcvr.send((MidiMessage) out, 0);
		
	}

	public static final byte[] intToByteArray(int value) {
		return new byte[] {
				// (byte)(value >>> 24),
				(byte) (value >>> 16), (byte) (value >>> 8), (byte) value };
	}

}