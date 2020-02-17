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
    private char[][] grid;
    Model gameworld = new Model();
    BackgroundGrid gameSpace;

    public Viewer(Model World) {
        this.gameworld = World;
        gameSpace      = new BackgroundGrid();
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

    public void updateview() {
        this.repaint();
        // TODO Auto-generated method stub
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        CurrentAnimationTime++;         // runs animation time step

        //Draw player Game Object
        int    x       = (int)gameworld.getPlayer().getCentre().getX();
        int    y       = (int)gameworld.getPlayer().getCentre().getY();
        int    width   = (int)gameworld.getPlayer().getWidth();
        int    height  = (int)gameworld.getPlayer().getHeight();
        String texture = gameworld.getPlayer().getTexture();

        //Draw background
        drawBackground(g);

        //drawExit(g);
        //Draw player

        g.drawImage(gameSpace.background, 0, 0, null);

        drawPlayer(x, y, width, height, texture, g);
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
            g.drawImage(myImage, 620, 60, 40, 40, null);
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

    public void setLevel(char[][] grid) {
        this.grid = grid;

        int x = 100;
        int y = 100;

        File TextureToLoad;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == 'X') {
                    //TextureToLoad = new File("../res/ice_block.png");
                    TextureToLoad = new File("../res/iceblock.png");
                } else if (grid[i][j] == 'G') {
                    TextureToLoad = new File("../res/ground.png");
                } else if (grid[i][j] == 'B') {
                    TextureToLoad = new File("../res/boulder.png");
                } else if (grid[i][j] == 'O') {
                    TextureToLoad = new File("../res/hole.png");
                } else if (grid[i][j] == 'F') {
                    TextureToLoad = new File("../res/exit.png");
                } else if (grid[i][j] == 'T') {
                    TextureToLoad = new File("../res/blank.png");
                } else {
                    System.out.println("grid char not recognized");
                    TextureToLoad = new File("../res/blankSprite.png");
                }

                try {
                    Image myImage = ImageIO.read(TextureToLoad);
                    gameSpace.addSquare(myImage, x, y);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                x += 40;
            }
            y += 40;
            x  = 100;
        }
    }

    static class BackgroundGrid extends JPanel {
        private final static int size = 1000;
        private BufferedImage background;

        public BackgroundGrid() {
            background = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        }

        public void addSquare(Image image, int x, int y) {
            Graphics2D g = (Graphics2D)background.getGraphics();

            g.drawImage(image, x, y, 40, 40, null);
            repaint();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.drawImage(background, 0, 0, null);
        }
    }
}
