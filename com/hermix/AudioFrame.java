
package com.hermix;

/**
 * Clasa care abstractizeaza un frame audio.
 * Intr-un frame audio pot fi un numar variabuil de ms de informatie sonora
 *          in formate diferite. (Pana acu numai RTP-GSM)
 * Normal aici trebuiau definite si constantele respective:(
 * ATENTIE la timestamp! Daca acestea nu sunt corect initializate
 *          (in mod normal ultima milisecuna in care a aavut loc captarea)
 *          nu se poate face sinchronizarea cu partea de video.
 */

public class AudioFrame {
	public int type;
	public int packet;
	public int length;
	public int timestamp;
	public byte[] data;

	public int framesize;		//lungimea in octeti a frame-ului
	public int id;			//identificatorul utilizatorului
	public byte user[];			//utilizatorul care a trimis frame-ul
	public String nick;			//nick-ul utilizatorului... string

}