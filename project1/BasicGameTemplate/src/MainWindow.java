import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import util.UnitTests;

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



public class MainWindow {
    private static JFrame frame = new JFrame("Game");           // Change to the name of your game
    private static Scoreboard scoreboard;                       // high scores for the game
    private static Model gameworld   = new Model();
    private static Viewer canvas     = new Viewer(gameworld);
    private KeyListener Controller   = new Controller();
    private static int TargetFPS     = 100;
    private static boolean startGame = false;
    private JLabel BackgroundImageForStartMenu;
    private ButtonListener buttons;    // listener for pause/play button
    private JButton pause;             // button to resume the game
    private boolean isPaused = false;  // keep track of if the game is paused
    private Timer timer;
    private static TimerListener timerListener;

    public MainWindow() {
        char[][] Level1 = makeLevel();

        frame.setSize(1300, 1000);                            // you can customise this later and adapt it to change on size.
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //If exit // you can modify with your way of quitting , just is a template.
        frame.setLayout(null);
        //frame.setLayout(BorderLayout);
        frame.add(canvas);
        canvas.setBounds(0, 0, 1000, 1000);

        //canvas.setBackground(new Color(255, 255, 255)); //white background  replaced by Space background but if you remove the background method this will draw a white screen
        canvas.setVisible(false);                            // this will become visible after you press the key.

        JButton startMenuButton = new JButton("Start Game"); // start button
        startMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMenuButton.setVisible(false);
                BackgroundImageForStartMenu.setVisible(false);
                canvas.setVisible(true);
                //canvas.background.setVisible(true);
                canvas.addKeyListener(Controller);                               //adding the controller to the Canvas
                canvas.requestFocusInWindow();                                   // making sure that the Canvas is in focus so keyboard input will be taking in .
                canvas.setLevel(Level1);


                scoreboard = new Scoreboard();
                //buttons    = new ButtonListener();
                //pause      = new JButton("Pause");

                //pause.setFocusable(false);
                //pause.addActionListener(buttons);
                //frame.add(canvas.background);
                //pause.setPreferredSize(new Dimension(100, 30));
                //canvas.add(background);
                //background.setBounds(0, 0, 1000, 1000);
                //frame.add(background);
                //canvas.add(pause, BorderLayout.WEST);
                //canvas.add(scoreboard);
                frame.add(scoreboard);
                timerListener = new TimerListener();
                timer         = new Timer(100, timerListener); // timer listener will fire every 100 milliseconds
                timer.start();
                scoreboard.setBounds(1000, 0, 300, 500);

                startGame = true;
            }
        });
        startMenuButton.setBounds(400, 500, 200, 40);

        //loading background image
        File BackroundToLoad = new File("../res/startscreen.png");          //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
        try {
            BufferedImage myPicture = ImageIO.read(BackroundToLoad);
            BackgroundImageForStartMenu = new JLabel(new ImageIcon(myPicture));
            BackgroundImageForStartMenu.setBounds(0, 0, 1000, 1000);
            frame.add(BackgroundImageForStartMenu);
        }  catch (IOException e) {
            e.printStackTrace();
        }

        frame.add(startMenuButton);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        MainWindow hello = new MainWindow(); //sets up environment

        while (true) {                       //not nice but remember we do just want to keep looping till the end.  // this could be replaced by a thread but again we want to keep things simple
            //swing has timer class to help us time this but I'm writing my own, you can of course use the timer, but I want to set FPS and display it

            int  TimeBetweenFrames = 1000 / TargetFPS;
            long FrameCheck        = System.currentTimeMillis() + (long)TimeBetweenFrames;

            //wait till next time step
            while (FrameCheck > System.currentTimeMillis()) {
            }


            if (startGame) {
                gameloop();
            }

            //Toolkit.getDefaultToolkit().sync();
            //UNIT test to see if framerate matches
            //UnitTests.CheckFrameRate(System.currentTimeMillis(), FrameCheck, TargetFPS);
        }
    }

    //Basic Model-View-Controller pattern
    private static void gameloop() {
        // GAMELOOP

        // controller input  will happen on its own thread
        // So no need to call it explicitly

        // model update
        gameworld.gamelogic();
        // view update

        canvas.updateview();
        //background.repaint();

        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            System.out.println("Interruped Exception!");
        }
        if (gameworld.getLives() <= 0) {
            System.exit(0);
        }

        scoreboard.updateMoves(gameworld.getMoves());
        // Both these calls could be setup as  a thread but we want to simplify the game logic for you.
        //score update
        //frame.setTitle("Score =  " + gameworld.getScore());
        //frame.setTitle("Moves =  " + gameworld.getScore());
    }

    public char[][] makeLevel() {
        char[][] level = {
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' }
        };
        return level;
    }

    class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            //Object e = event.getSource();

            scoreboard.updateTime(.10);
            //Toolkit.getDefaultToolkit().sync();
        }
    }

    class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            Object e = event.getSource();

            if (e == pause) {
                /* if the button was clicked and the game is not paused, pause the game */
                if (!isPaused) {
                    pause.setText("Play");
                    isPaused = true;                     /* game is now paused */
                } else {
                    pause.setText("Pause");
                    isPaused = false;                     /* otherwise resume the game */
                }
            }
            /* if they clicked the endgame button, stop the timer and exit */
            //else {
            //    //timer.stop();
            //    setVisible(false);
            //    dispose();
            //}
        }
    }
}
