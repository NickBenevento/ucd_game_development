/* Nick Benevento
 * 19207773
 */
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;

/*
 * Created by Abraham Campbell on 15/01/2020.
 *   Copyright (c) 2020  Abraham Campbell
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * (MIT LICENSE ) e.g do what you want with this :-)
 */

//Singeton pattern
public class Controller implements KeyListener {
    private static boolean keyRightPressed = false;
    private static boolean keyLeftPressed  = false;
    private static boolean keyUpPressed    = false;
    private static boolean keyDownPressed  = false;

    private static final Controller instance = new Controller();

    public Controller() {
    }

    public static Controller getInstance() {
        return instance;
    }

    @Override
    // Key pressed , will keep triggering
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (Character.toLowerCase(e.getKeyChar()))
        {
            case 'a': //setKeyAPressed(true); break;
                setKeyLeftPressed(true);
                break;

            case 's': //setKeySPressed(true); break;
                setKeyDownPressed(true);
                break;

            case 'w': //setKeyWPressed(true); break;
                setKeyUpPressed(true);
                break;

            case 'd': //setKeyDPressed(true); break;
                setKeyRightPressed(true);
                break;

            default:
                break;
        }

        switch (e.getKeyCode())
        {
            case KeyEvent.VK_UP:
                setKeyUpPressed(true);
                break;

            case KeyEvent.VK_DOWN:
                setKeyDownPressed(true);
                break;

            case KeyEvent.VK_LEFT:
                setKeyLeftPressed(true);
                break;

            case KeyEvent.VK_RIGHT:
                setKeyRightPressed(true);
                break;

            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (Character.toLowerCase(e.getKeyChar()))
        {
            case 'a': //setKeyAPressed(true); break;
                setKeyLeftPressed(false);
                break;

            case 's': //setKeySPressed(true); break;
                setKeyDownPressed(false);
                break;

            case 'w': //setKeyWPressed(true); break;
                setKeyUpPressed(false);
                break;

            case 'd': //setKeyDPressed(true); break;
                setKeyRightPressed(false);
                break;

            default:
                //System.out.println("Controller test:  Unknown key pressed");
                break;
        }

        switch (e.getKeyCode())
        {
            case KeyEvent.VK_UP:
                setKeyUpPressed(false);
                break;

            case KeyEvent.VK_DOWN:
                setKeyDownPressed(false);
                break;

            case KeyEvent.VK_LEFT:
                setKeyLeftPressed(false);
                break;

            case KeyEvent.VK_RIGHT:
                setKeyRightPressed(false);
                break;

            default:
                break;
        }
    }

    public boolean isKeyLeftPressed() {
        return keyLeftPressed;
    }

    public boolean isKeyRightPressed() {
        return keyRightPressed;
    }

    public boolean isKeyUpPressed() {
        return keyUpPressed;
    }

    public boolean isKeyDownPressed() {
        return keyDownPressed;
    }

    public void setKeyRightPressed(boolean b) {
        keyRightPressed = b;
    }

    public void setKeyLeftPressed(boolean b) {
        keyLeftPressed = b;
    }

    public void setKeyUpPressed(boolean b) {
        keyUpPressed = b;
    }

    public void setKeyDownPressed(boolean b) {
        keyDownPressed = b;
    }
}
