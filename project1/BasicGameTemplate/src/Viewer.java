import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import util.GameObject;


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
public class Viewer extends JPanel {
    private long CurrentAnimationTime = 0;
    private char[][] Level;
    private Model gameworld = new Model();
    private BackgroundLevel gameSpace;
    private boolean setBlackScreen = false;
    private String displayText;
    private boolean endGame     = false;
    private boolean startScreen = true;
    private static int gridSize = 40;

    public Viewer(Model World) {
        this.gameworld = World;
        gameSpace      = new BackgroundLevel();
        gameSpace.setBounds(0, 0, 1000, 1000);
        // TODO Auto-generated constructor stub
    }

    public Viewer(LayoutManager layout) {
        super(layout);
        // TODO Auto-generated constructor stub
    }

    public Viewer(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        // TODO Auto-generated constructor stub
    }

    public Viewer(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        // TODO Auto-generated constructor stub
    }

    public BackgroundLevel getGameSpace() {
        return gameSpace;
    }

    public void setBlackScreen(boolean b) {
        setBlackScreen = b;
    }

    public void setDisplayText(String text, boolean b) {
        displayText = text;
        endGame     = b;
    }

    public void setStartScreen(boolean b) {
        startScreen = b;
    }

    public void updateview() {
        this.repaint();
        // TODO Auto-generated method stub
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        CurrentAnimationTime++;         // runs animation time step

        // for inbetween levels
        if (setBlackScreen) {
            File blackScreen = new File("../res/blackScreen.png");
            try {
                Image myImage = ImageIO.read(blackScreen);
                g.drawImage(myImage, 0, 0, 1000, 1000, null);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            g.setFont(new Font("SansSerif", Font.BOLD, 30));
            g.setColor(Color.WHITE);
            g.drawString(displayText, 300, 300);
            if (endGame) {
                g.setColor(Color.GREEN);
                g.drawString("Congratulations! You beat the game!", 300, 400);
            }
            return;
        }
        //Draw player Game Object
        int    x       = (int)gameworld.getPlayer().getCentre().getX();
        int    y       = (int)gameworld.getPlayer().getCentre().getY();
        int    width   = (int)gameworld.getPlayer().getWidth();
        int    height  = (int)gameworld.getPlayer().getHeight();
        String texture = gameworld.getPlayer().getTexture();

        //Draw background
        drawBackground(g);

        if (startScreen) {
            g.setFont(new Font("SansSerif", Font.BOLD, 30));
            g.setColor(Color.BLACK);
            String title = "The Adventures of Stanley the Seal";
            g.drawString(title, 200, 300);
        } else {
            g.drawImage(gameSpace.background, 0, 0, null);

            drawPlayer(x, y, width, height, texture, g);
        }
    }

    private void drawBackground(Graphics g) {
        File background;

        background = new File("../res/ice.jpg");
        try {
            Image myImage = ImageIO.read(background);
            g.drawImage(myImage, 0, 0, 1000, 1000, null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void drawExit(Graphics g) {
        File exit;

        exit = new File("../res/exit.png");
        try {
            Image myImage = ImageIO.read(exit);
            g.drawImage(myImage, 620, 60, gridSize, gridSize, null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void drawPlayer(int x, int y, int width, int height, String texture, Graphics g) {
        File TextureToLoad = new File(texture);          //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE

        try {
            Image myImage = ImageIO.read(TextureToLoad);
            //The spirte is 32x32 pixel wide and 4 of them are placed together so we need to grab a different one each time
            //remember your training :-) computer science everything starts at 0 so 32 pixels gets us to 31
            int currentPositionInAnimation = ((int)((CurrentAnimationTime % 40) / 10)) * 32;       //slows down animation so every 10 frames we get another frame so every 100ms
            g.drawImage(myImage, x, y, x + width, y + width, currentPositionInAnimation, 0, currentPositionInAnimation + 31, 32, null);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateCell(int row, int col, String imagePath) {
        int x = gridSize * col + 100;
        int y = gridSize * row + 100;

        try {
            Image image = ImageIO.read(new File(imagePath));

            gameSpace.addSquare(image, x, y);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setLevel(char[][] Level) {
        if (Level == null) {
            System.out.println("Level is null ");
            return;
        }
        this.Level = Level;
        gameSpace.clearLevel();
        int x = 100;
        int y = 100;

        File TextureToLoad;

        for (int i = 0; i < Level.length; i++) {
            for (int j = 0; j < Level[i].length; j++) {
                switch (Level[i][j])
                {
                    case 'X':
                        TextureToLoad = new File("../res/iceblock.png");
                        break;

                    case 'G':
                        TextureToLoad = new File("../res/ground.png");
                        break;

                    case 'B':
                        TextureToLoad = new File("../res/boulder.png");
                        break;

                    case 'O':
                        TextureToLoad = new File("../res/hole.png");
                        break;

                    case 'R':
                        TextureToLoad = new File("../res/right_arrow.png");
                        break;

                    case 'U':
                        TextureToLoad = new File("../res/up_arrow.png");
                        break;

                    case 'L':
                        TextureToLoad = new File("../res/left_arrow.png");
                        break;

                    case 'D':
                        TextureToLoad = new File("../res/down_arrow.png");
                        break;

                    case 'r':
                        TextureToLoad = new File("../res/gray_right_arrow.png");
                        break;

                    case 'u':
                        TextureToLoad = new File("../res/gray_up_arrow.png");
                        break;

                    case 'l':
                        TextureToLoad = new File("../res/gray_left_arrow.png");
                        break;

                    case 'd':
                        TextureToLoad = new File("../res/gray_down_arrow.png");
                        break;

                    case 'F':
                        TextureToLoad = new File("../res/exit.png");
                        break;

                    case 'T':
                        TextureToLoad = new File("../res/blank.png");
                        break;

                    default:
                        System.out.println("Level char not recognized ");
                        TextureToLoad = new File("../res/blankSprite.png");
                        break;
                }

                try {
                    Image myImage = ImageIO.read(TextureToLoad);
                    gameSpace.addSquare(myImage, x, y);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                x += gridSize;
            }
            y += gridSize;
            x  = 100;
        }
    }

    static class BackgroundLevel extends JPanel {
        private final static int size = 1000;
        private BufferedImage background;

        public BackgroundLevel() {
            background = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        }

        public void clearLevel() {
            background = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        }

        public void addSquare(Image image, int x, int y) {
            Graphics2D g = (Graphics2D)background.getGraphics();

            g.drawImage(image, x, y, gridSize, gridSize, null);
            repaint();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(background, 0, 0, null);
        }
    }
}
