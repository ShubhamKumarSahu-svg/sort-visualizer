import java.util.concurrent.atomic.AtomicBoolean;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

/**
 * Generates MIDI tones for comparison and swap operations during sorting.
 */
public class ToneGenerator {
  private Synthesizer synth;
  private MidiChannel channel;
  private final Object lock = new Object();
  private boolean available = false;
  private final AtomicBoolean stopRequested = new AtomicBoolean(false);

  public ToneGenerator() {
    try {
      synth = MidiSystem.getSynthesizer();
      synth.open();
      MidiChannel[] channels = synth.getChannels();
      if (channels != null && channels.length > 0) {
        channel = channels[0];
        channel.programChange(0); // Acoustic Grand Piano
        available = true;
      }
    } catch (MidiUnavailableException e) {
      System.err.println("MIDI not available: " + e.getMessage());
      available = false;
    }
  }

  /**
   * Play a tone for comparison operation
   */
  public void playCompare(int v1, int v2) {
    if (!available || stopRequested.get())
      return;
    playPair(v1, v2, 40, 25);
  }

  /**
   * Play a tone for swap operation
   */
  public void playSwap(int v1, int v2) {
    if (!available || stopRequested.get())
      return;
    playPair(v1, v2, 80, 35);
  }

  private void playPair(int v1, int v2, int velocity, int durationMs) {
    if (channel == null || stopRequested.get())
      return;

    int pitch1 = mapToPitch(v1);
    int pitch2 = mapToPitch(v2);

    Thread toneThread = new Thread(() -> {
      synchronized (lock) {
        try {
          if (stopRequested.get())
            return;

          channel.noteOn(pitch1, velocity);
          Thread.sleep(durationMs);
          channel.noteOff(pitch1);

          if (stopRequested.get()) {
            channel.noteOff(pitch1);
            return;
          }

          channel.noteOn(pitch2, velocity);
          Thread.sleep(durationMs);
          channel.noteOff(pitch2);
        } catch (InterruptedException e) {
          // Immediately stop all notes on interruption
          channel.allNotesOff();
          Thread.currentThread().interrupt();
        }
      }
    }, "tone-player");
    toneThread.setDaemon(true);
    toneThread.start();
  }

  /**
   * Map a value to a MIDI pitch (note number)
   * Values are mapped to MIDI notes 36-96 (C2 to C7)
   */
  private int mapToPitch(int value) {
    value = Math.max(1, value);
    double normalized = Math.min(1.0, Math.log(1 + value) / Math.log(1000));
    return 36 + (int) Math.round(normalized * 60);
  }

  /**
   * Stop all currently playing sounds immediately
   */
  public void stopAllSounds() {
    stopRequested.set(true);
    if (channel != null) {
      synchronized (lock) {
        channel.allNotesOff();
      }
    }
    // Small delay to ensure all notes are stopped
    try {
      Thread.sleep(50);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Reset the stop flag to allow sounds again
   */
  public void reset() {
    stopRequested.set(false);
  }

  /**
   * Clean up MIDI resources
   */
  public void close() {
    stopAllSounds();
    if (synth != null && synth.isOpen()) {
      synth.close();
    }
  }

  /**
   * Check if MIDI is available
   */
  public boolean isAvailable() {
    return available;
  }
}
