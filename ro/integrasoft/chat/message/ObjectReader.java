///////////////////////////////////////////////////////////////////////////////////////////////////
// ObjectReader.java
// Author		: Farkas Lajos-Mihaly
// Date		: 3.I.1998
// Copyright	: 1998 (c) Integrasoft SRL,Romania
// Last Modified: 17.VII.1998 by Farkas Lajos
///////////////////////////////////////////////////////////////////////////////////////////////////

package ro.integrasoft.chat.message;

import java.io.*;

/**
 * Aceasta clasa citeste un obiect dintr-un stream de intrare.
 * Datorita faptului ca pot aparea erori la citirea datelor de pe Stream-uri
 * de intrare asignate socketurilor, nu se citesc direct date de tip Object,
 * ci se citesc date de tip byteArray.
 * Practic fluxul de date este urmatorul:
 *	lungime data lungime data ... lungime data
 * unde
 *      -lungime este lungimea obiectului care urmeaza.
 *	-data	objectul, sub forma de byteArray
 *
 * OBSERVATII:
 *-------------
 * Deoarece nu se poate converti direct byteArray-ul in Object-ul care a fost
 * recurgem la o smecherie....Avind byteArray-ul, deschidem un
 * ByteArrayInputStream, care citeste datele din acest byteArray, apoi deschidem
 * un ObjectInputStream, care citeste datele din ByteArrayInputStream, dupa care
 * citim obiectul din ObjectInputStream.
 */

class ObjectReader extends Thread {
	private DataInputStream is;
	private static int inst1 = 0;
	private int inst;
	private int objects = 0;
	private boolean stopp;

	/**
	 * constructorul clasei
	 * @param iis streamul de intrare de pe care se citesc datele
	 */
	public ObjectReader(InputStream iis) throws IOException {
		super();
		inst1++;
		inst = inst1;
		stopp = false;
		this.is = new DataInputStream(iis);
	}

	/**
	 * returneaza urmatorul obiect din bufer, daca este... daca nu, asteapta pina apare unul
	 */
	public synchronized Object nextObject(IntHolder objlen) throws Exception {
		Object o;
		int size = 0;
		size = is.readInt();
		byte[] a = new byte[size];
		try {
			is.readFully(a);
		} catch (java.net.SocketException se) {
			System.out.println("***" + se.getMessage() + "***");
			throw se;
		}
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(a));
		o = ois.readObject();
		objects++;
		objlen.setValue(size);
		return o;
	}

	/**
	 * Returneaza numarul deobiecte care au fost citie din acest stream de intrare.
	 */
	public int getObjectsCount() {
		return objects;
	}

	public void stopThread() {
		stopp = true;
	}
}