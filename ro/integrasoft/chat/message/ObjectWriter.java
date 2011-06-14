///////////////////////////////////////////////////////////////////////////////////////////////////
// ObjectWriter.java
// Author		: Farkas Lajos-Mihaly
// Date		: 3.I.1998
// Copyright	: 1998 (c) Integrasoft SRL,Romania
// Last Modified: 17.VII.1998 by Farkas Lajos
///////////////////////////////////////////////////////////////////////////////////////////////////

package ro.integrasoft.chat.message;

import java.io.*;

/**
 * complementara la ObjectReader
 */
class ObjectWriter extends Thread {
	private static int inst1 = 0;
	private BufferedOutputStream os;
	private int inst;
	private boolean closed = false;
	private int objects = 0;
	private int flush = 0;
	private boolean stopp;

	/**
	 * constructorul clasei
	 * @param oos streamul de iesire
	 */
	public ObjectWriter(OutputStream oos) throws IOException {
		super();
		inst1++;
		inst = inst1;
		os = new BufferedOutputStream(oos, 1);
		stopp = false;
	}

	/**
	 * adauga un obiect la buffer
	 * @param o obiectul de adaugat
	 * @return nr de octeti pe care a fost scris obiectul
	 */
	public synchronized int addObject(Object o) throws IOException {
		//ByteArrayOutputStream bos=new ByteArrayOutputStream();
		//ObjectOutputStream oos=new ObjectOutputStream( bos );
		//oos.writeObject( o );
		//bos.flush();
		//os.writeInt( bos.size() );
		//bos.writeTo( os );
		//os.flush();


		//toate kestia care urmeaza numai ca sa aflu dimensiunea
		//in octeti a obiectului
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(o);
		bos.flush();
		int size = bos.size();

		//acu ca stim dimensiunea transmitem datele...
		bos = new ByteArrayOutputStream();
		DataOutputStream d = new DataOutputStream(bos);
		d.writeInt(size);
		bos.flush();
		oos = new ObjectOutputStream(bos);
		oos.writeObject(o);
		bos.flush();
		bos.writeTo(os);
		os.flush();

		objects++;
		//returnam nr. de octeti scrisi in total:
		return size + (" " + size).length();
	}

	/**
	 * returneaza numarul de obiecte care au fos trimise de acest thread
	 */
	public int getObjectsCount() {
		return objects;
	}

	public void stopIt() {
		stopp = true;
	}
}