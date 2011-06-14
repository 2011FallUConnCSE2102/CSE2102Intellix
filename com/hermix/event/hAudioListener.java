
package com.hermix.event;

/**
 * Interfata care permite constrirea de obiecte care stiu sa cante un fraem audiosi
 * care pot fi apelate de catre un AudioCache.
 */

import com.hermix.AudioFrame;

public interface hAudioListener {
	public void play_audio_frame(AudioFrame af);
}