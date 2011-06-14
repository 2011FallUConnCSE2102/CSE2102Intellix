
package com.hermix.event;

public interface hFileListener {
	void f_received_data(String file, String id, byte[] data, int size, int progress);

	void f_sent_data(String file, String id, byte[] data, int size, int progress);

	void f_transfer_error(String file, String id, int errorcode);
}