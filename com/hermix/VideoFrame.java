
package com.hermix;

/**
 * Clasa care abstractizeaza un frame video.
 * Intr-un frame video se gasestte o singura imagine posibil intr-un format oarecare
 *          Pana acu este folosit jpeg. Compresia poate fi oarecare
 * Normal aici trebuiau definite si constantele respective:(
 * ATENTIE la timestamp! Daca acestea nu sunt corect initializate
 *          (in mod normal ultima milisecuna in care a avut loc captarea)
 *          nu se poate face sinchronizarea cu partea de audio.
 */


public class VideoFrame {
	public int type;
	public int timestamp;
	public int framesize;		//lungimea in octeti a frame-ului
	public int id;			//identificatorul utilizatorului
	public byte user[];			//utilizatorul care a trimis frame-ul
	public String nick;			//nick-ul utilizatorului... string
	public byte[] data;			//datele aferente frameului

	public Object Clone() {
		VideoFrame vf = new VideoFrame();
		vf.type = type;
		vf.timestamp = vf.timestamp;
		vf.framesize = framesize;
		vf.nick = nick;
		vf.user = new byte[20];
		vf.id = -1;
		for (int i = 0; i < 20; i++) {
			vf.user[i] = user[i];
		}
		vf.data = new byte[framesize];
		for (int i = 0; i < framesize; i++) {
			vf.data[i] = data[i];
		}
		return vf;
	}
}