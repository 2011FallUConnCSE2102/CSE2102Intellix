/**
 * User: Administrator
 * @Author: qurtach@intellisoft.ro
 * @Date: May 21, 2002
 * @Time: 5:02:06 PM
 */

package ro.intellisoft.intelliX;

public class BackgroundParser extends Thread {

	/***delay to begin re-parsing the modified file(s);*/
	private int delay = 500;

	IntelliX parent = null;

	/**tells the thread to stop*/
	private boolean stop = false;
	/**tells the object to if any modification happend so a parse to be invoked*/
	private boolean armed = false;

	public BackgroundParser(IntelliX parent, int delay) {
		this.parent = parent;
		this.delay = delay;
	}

	public void run() {
		while (!stop) {
			try {
				sleep(delay);
			} catch (InterruptedException e) {
			}
			if (armed) {
				parent.updateClassNavigationTree();
				armed = false;
			}
		}
	}

	public void arm() {
		armed = true;
	}

	public boolean isArmed() {
		return armed;
	}

	/**this is how we stop the thread*/
	public void stopThread() {
		stop = true;
	}

	/**just a little verification before setting the delay will be done*/
	public void setDelay(int delay) {
		if (delay < 20000 && delay > 200) {
			this.delay = delay;
		}
	}
}
