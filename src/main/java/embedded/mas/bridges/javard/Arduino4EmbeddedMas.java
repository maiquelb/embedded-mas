/**
 * This class is specialization of the Arduino classe from the Javard package.
 * While the superclasse reads and writes in an Arduino device, this class decodes the read messages according to a predefined protocol that includes 
 * the preamble of the message, as well as the symbols of starting and ending of the message. 
 * 
 */

package embedded.mas.bridges.javard;

import arduino.Arduino;
import embedded.mas.bridges.jacamo.IPhysicalInterface;

import com.fazecast.jSerialComm.*;

public class Arduino4EmbeddedMas extends Arduino implements IPhysicalInterface{

	private String preamble = "==";
	private String startMessage = "::";
	private String endMessage = "--";


	public Arduino4EmbeddedMas(String portDescription, int baud_rate) {
		super(portDescription, baud_rate);
	}
	
	@Override
	public String read() {       
		return this.serialRead();
	}
	
	
	@Override
	public boolean write(String s) {
		try {
			serialWrite(s);
			return true;
		}catch (Exception e) {
			return false;
		}		
	}
	
	/**
	 * While the superclasse reads and writes in an Arduino device, this class decodes the read messages according to a predefined protocol that includes 
	 * the preamble of the message, as well as the symbols of starting and ending of the message. 
	 * 
	 * The result of the reading is the payload of the whole message gotten from the Arduino.	 
	 */
	@Override
	public String serialRead() {
		String s = "";
		String start = "";
		String end = "";

		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		byte[] b = new byte[1];

		comPort.readBytes(b, 1);
		while (!start.equals(preamble)) {
			if((char)b[0] == preamble.charAt(start.length())) {
				start = start + (char)b[0];
			}else {
				start = "";
			}
			comPort.readBytes(b, 1);
		}

		while (!end.equals(endMessage)) {
			if((char)b[0] == endMessage.charAt(end.length())) {
				end = end + (char)b[0];
			}else {
				s = s + end + (char)b[0];
				end = "";
			}
			comPort.readBytes(b, 1);
		}

		String[] strings = s.split(startMessage);
		int number = Integer.parseInt(strings[0]);
		String message = strings[1];

		if(number == message.length()) {
			return message;
		}
		else {
			return "Message conversation error";
		}
		
	}


}
