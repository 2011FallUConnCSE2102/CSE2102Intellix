
package com.hermix.event;

/**
 * Interfata care permite constrirea de obiecte care stiu sa cante un frame video
 * care pot fi apelate de catre un videoCache.
 */

import com.hermix.VideoFrame;

public interface hVideoListener {
	public void play_video_frame(VideoFrame vf);
}