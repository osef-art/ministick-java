package ministick.window.controller;

import ministick.window.SoundName;
import ministick.window.Options;
import ministick.window.Sound;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class OptionsController implements KeyListener {
  // key controller

  @Override
  public void keyPressed(KeyEvent e) {
    switch (e.getKeyCode()) {
      case KeyEvent.VK_B:
        Options.toggleBackground();
        break;
      case KeyEvent.VK_K:
        Options.toggleKeyboard();
        break;
      case KeyEvent.VK_H:
        Options.toggleHitboxes();
        break;
      case KeyEvent.VK_P:
        if (!Options.onPause()) new Sound(SoundName.Pause).play();
        Options.togglePause();
        break;
      case KeyEvent.VK_M:
        Options.toggleMute();
        break;
      case KeyEvent.VK_A:
        System.exit(0);
    }
  }
  @Override
  public void keyReleased(KeyEvent e) {
  }
  @Override
  public void keyTyped(KeyEvent e) {
  }
}
