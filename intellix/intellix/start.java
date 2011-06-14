package intellix.intellix; /*tms*/

import ro.intellisoft.intelliX.IntelliX;

import javax.swing.*;
import java.io.*;
import java.util.zip.CRC32;
import java.util.zip.ZipOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;
import java.awt.*;


/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: Jun 25, 2002
 * @Time: 5:00:24 PM
 */

public class start {

	public static void main(String args[]) {
		/*String data ="		System.out.println(inData.length);"
				+ "CRC32 crc = new CRC32();" + "crc.update(inData);"
				+ "System.out.println(\"crc=\"+crc.getValue());"
				+ "BytesOutputStream bos = new BytesOutputStream();"
				+ "try {                                            "
				+ "	GZIPOutputStream gout = new GZIPOutputStream(bos);"
				+ "	gout.write(inData);                                "
				+ "	gout.flush();                                       "
				+ "	gout.finish();                                       "
				+ "} catch (IOException e) {                               "
				+ "System.out.println(\"e = \" + e);}                     "
				+ "System.out.println(\"inData.length = \" + inData.length);   "
				+ "System.out.println(\"inData = \" + new String(inData));      "
				+ "System.out.println(\"bos.getData().length = \" + bos.getData().length);"
				+ "System.out.println(\"bos.getData() = \" + new String(bos.getData()));   "
				+ "System.out.println(\"=\"+(int)(bos.getData().length*10000.0/inData.length)/100.0+\"%\");"
				+ "//		GZIPInputStream gzipin = new GZIPInputStream();";
		byte[] inData = (data+"u"+data+"h"+data+"j"+data+"k"+data).getBytes();

		System.out.println(inData.length);
		CRC32 crc = new CRC32();
		crc.update(inData);
		System.out.println("crc=" + crc.getValue());

		BytesOutputStream bos = new BytesOutputStream();
		try {
			GZIPOutputStream gout = new GZIPOutputStream(bos);
			ObjectOutputStream oos = new ObjectOutputStream(gout);
			oos.writeObject(inData);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			System.out.println("e = " + e);
		}
		System.out.println("inData.length = " + inData.length);
		byte[] bosData = bos.getData();
		System.out.println("bos.getData().length = " + bosData);
		System.out.println("=" + (bosData.length * 10000 / inData.length) / 100.0 + "%");


		//now to deGZIP it!
		BytesInputStream bis = new BytesInputStream(bosData);
		try {
			GZIPInputStream gin = new GZIPInputStream(bis);
			ObjectInputStream ois = new ObjectInputStream(gin);
			byte[] outData = (byte[])ois.readObject();
			int k = outData.length;
			System.out.println("DeGZIPped size = " + k);
		} catch (IOException e) {
			System.out.println("e = " + e);
		} catch (ClassNotFoundException e) {
			System.out.println("e = " + e);
		}



	//	System.exit(0);*/

		System.out.println("IntelliX vers. 1.0 beta release, (c) 2002 Intelli Soft - www.intellisoft.ro");
		String envError = testEnvironment();
		if (envError != null) {
			JOptionPane.showMessageDialog(null, "The following fatal error appeared:\n" + envError + "\nPlease repair it or contact the IntelliX developement team.\nSorry for inconvenience.\nThank you.", "Error launching IntelliX", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		new IntelliX(args);
	}

	public static String testEnvironment() {
		try {
			Class.forName("sun.tools.java.Constants");
			Class.forName("antlr.ANTLRLexer");
			Class.forName("JavaParser.JavaToken");
			Class.forName("javax.sound.sampled.AudioFormat");
		} catch (java.lang.ClassNotFoundException classNotFoundException) {
			if (classNotFoundException.getMessage().equals("sun.tools.java.Constants")) {
				return "tools.jar not in CLASSPATH";
			} else if (classNotFoundException.getMessage().equals("antlr.ANTLRLexer")) {
				return "antlr.jar not in CLASSPATH";
			} else if (classNotFoundException.getMessage().equals("JavaParser.JavaToken")) {
				return "internal error: cauld not find JavaParser.JavaToken";
			} else if (classNotFoundException.getMessage().equals("javax.sound.sampled.AudioFormat")) {
				return "internal error: could not find Java Sound System";
			}
		}
		return null;
	}
}

/*
class BytesOutputStream extends OutputStream {
	private int RATIO = 1024;
	private byte[] data = new byte[RATIO];
	private int size = 0;

	public void write(int b) throws IOException {
		if (size == data.length) {
			byte[] olddata = data;
			data = new byte[olddata.length + RATIO];
			System.arraycopy(olddata, 0, data, 0, olddata.length);
		}
		data[size++] = (byte) b;
	}


	public byte[] getData() {
		byte[] copy = new byte[size];
		System.arraycopy(data, 0, copy, 0, size);
		return copy;
	}
}

class BytesInputStream extends InputStream{
	private byte[] data = null;
	private int pos = 0;

	public BytesInputStream(byte[] data){
		if (data == null){
			throw new NullPointerException();
		}
        this.data = data;
	}

	public int read() throws IOException {
		if (pos < data.length){
			int k = data[pos++];
			return k<0? k+256 : k;
		} else {
			return -1;
		}
	}

	public synchronized void reset() throws IOException {
		pos = 0;
	}
}
*/