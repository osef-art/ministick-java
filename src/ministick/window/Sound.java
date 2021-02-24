package ministick.window;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import java.io.File;

public class Sound {
  private final String path;

  public Sound(SoundName sound) {
    path = "data/sounds/" + sound + sound.randomVariant() + ".wav";
  }

  /* son */
  public void play() {
    if (Options.isMuted()) return;
    try {
      AudioInputStream stream;
      DataLine.Info info;
      Clip clip;

      stream = AudioSystem.getAudioInputStream(new File(path));
      info = new DataLine.Info(Clip.class, stream.getFormat());
      clip = (Clip) AudioSystem.getLine(info);
      clip.open(stream);
      clip.start();
    }
    catch (Exception ignored) {}
  }
}
