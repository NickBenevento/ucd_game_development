import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.JOptionPane;

import java.io.BufferedReader;
import java.io.FileReader;
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

    private JButton startMenuButton;
    private JButton load;                       // button to load a previous game
    private JButton pause;                      // button to resume the game
    private JButton save;
    private JButton quit;
    private ButtonListener buttonListener;          // listener for pause/play button

    char[][][] Levels        = new char[10][][];
    private int currentLevel = 0;

    private Timer timer;
    private static TimerListener timerListener;
    private int cycleTime = 10;
    private long startTime;
    private long pauseTime = 0;

    public MainWindow() {
        setUpLevels();

        frame.setSize(1300, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.add(canvas);
        canvas.setBounds(0, 0, 1000, 1000);

        canvas.setVisible(false);                           // this will become visible after you press the key.

        //loading background image
        File BackroundToLoad = new File("../res/startscreen.png");          //should work okay on OSX and Linux but check if you have issues depending your eclipse install or if your running this without an IDE
        //try {
        //    BufferedImage myPicture = ImageIO.read(BackroundToLoad);
        //    BackgroundImageForStartMenu = new JLabel(new ImageIcon(myPicture));
        //    BackgroundImageForStartMenu.setBounds(0, 0, 1000, 1000);
        //    frame.add(BackgroundImageForStartMenu);
        //}  catch (IOException e) {
        //    e.printStackTrace();
        //}

        buttonListener  = new ButtonListener();
        startMenuButton = new JButton("New Game");        // start button
        startMenuButton.addActionListener(buttonListener);

        load = new JButton("Load Game");
        load.addActionListener(buttonListener);
        load.setBounds(300, 500, 200, 40);
        startMenuButton.setBounds(600, 500, 200, 40);

        frame.setVisible(true);
        frame.add(load);
        frame.add(startMenuButton);
    }

    public static void main(String[] args) {
        MainWindow hello = new MainWindow(); //sets up environment

        //while (true) {                       //not nice but remember we do just want to keep looping till the end.  // this could be replaced by a thread but again we want to keep things simple
        //    //swing has timer class to help us time this but I'm writing my own, you can of course use the timer, but I want to set FPS and display it

        //    int  TimeBetweenFrames = 1000 / TargetFPS;
        //    long FrameCheck        = System.currentTimeMillis() + (long)TimeBetweenFrames;

        //    //wait till next time step
        //    while (FrameCheck > System.currentTimeMillis()) {
        //    }


        //    if (startGame) {
        //        gameloop();
        //    }

        //    //UNIT test to see if framerate matches
        //    //UnitTests.CheckFrameRate(System.currentTimeMillis(), FrameCheck, TargetFPS);
        //}
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

        //try {
        //    Thread.sleep(5);
        //} catch (InterruptedException e) {
        //    System.out.println("Interruped Exception!");
        //}

        scoreboard.setMoves(gameworld.getMoves());
        Toolkit.getDefaultToolkit().sync();
    }

    class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            //Object e = event.getSource();
            if (gameworld.finishedLevel()) {
                currentLevel++;
                loadNextLevel();
            }
            //scoreboard.setTime((System.nanoTime() - startTime - pauseTime) / 1000000000.0);
            scoreboard.setTime((System.currentTimeMillis() - startTime - pauseTime) / 1000.0);
            //scoreboard.updateTime(((double)cycleTime) / 100.0);

            gameloop();
            //Toolkit.getDefaultToolkit().sync();
        }
    }

    class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            Object e = event.getSource();

            if (e == startMenuButton) {
                setUpGame();
            } else if (e == load) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
                    setUpGame();
                    String str;
                    /* reads the scores into the arraylist */
                    str          = reader.readLine();
                    currentLevel = Integer.parseInt(str);
                    gameworld.setLevel(Levels[currentLevel]);
                    canvas.setLevel(Levels[currentLevel]);
                    canvas.setCurrentLevel(currentLevel);
                    scoreboard.setLevel(currentLevel);

                    str = reader.readLine();
                    int moves = Integer.parseInt(str);
                    scoreboard.setMoves(moves);
                    gameworld.setMoves(moves);

                    str = reader.readLine();
                    scoreboard.setTime(Double.parseDouble(str));

                    str = reader.readLine();
                    gameworld.getPlayer().getCentre().setX(Integer.parseInt(str));

                    str = reader.readLine();
                    gameworld.getPlayer().getCentre().setY(Integer.parseInt(str));

                    gameworld.resetTargetPosition();

                    reader.close();
                } catch (IOException exc) {
                    //System.out.println("No save file found");
                    JOptionPane.showMessageDialog(null, "No save file found!", "Load Error!", JOptionPane.ERROR_MESSAGE);
                }
            } else if (e == pause) {
                /* if the button was clicked and the game is not paused, pause the game */
                if (pause.getText().equals("Pause")) {
                    pauseGame();
                } else {
                    resumeGame();
                }
            } else if (e == save) {
                // the player can only save while they are still --> prevents exploits from saving/loading
                if (gameworld.getDirection() == Model.Direction.STILL) {
                    pauseGame();
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
                        int            result = JOptionPane.showConfirmDialog(null, "There is already a save file. Are you sure you want to overwrite it?", "Save Prompt", JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            saveGame();
                        }
                    } catch (IOException exc) {
                        saveGame();
                    }
                    resumeGame();
                } else {
                    pauseGame();
                    JOptionPane.showMessageDialog(null, "You can't save the game while sliding!", "Save Error!", JOptionPane.ERROR_MESSAGE);
                    resumeGame();
                }
            }
            /* if they clicked the endgame button, stop the timer and exit */
            else {
                pauseGame();
                int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit? Any progress not saved will be lost.", "Quit Prompt", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    frame.setVisible(false);
                    System.exit(0);
                } else {
                    resumeGame();
                }
            }
        }
    }

    public void pauseGame() {
        //pauseTime = System.nanoTime();
        pauseTime = System.currentTimeMillis();
        pause.setText("Play");
        timer.stop();
    }

    public void resumeGame() {
        pause.setText("Pause");
        long temp = pauseTime;
        pauseTime = System.currentTimeMillis() - temp;
        timer.start();
    }

    public void setUpGame() {
        save = new JButton("Save Game");
        save.addActionListener(buttonListener);

        save.setFocusable(false);

        startMenuButton.setVisible(false);
        //BackgroundImageForStartMenu.setVisible(false);
        canvas.setVisible(true);
        canvas.addKeyListener(Controller);                                       //adding the controller to the Canvas
        canvas.requestFocusInWindow();                                           // making sure that the Canvas is in focus so keyboard input will be taking in .
        canvas.setLevel(Levels[currentLevel]);

        gameworld.setLevel(Levels[currentLevel]);

        scoreboard = new Scoreboard();

        scoreboard.setLevel(currentLevel);
        pause = new JButton("Pause");

        pause.setFocusable(false);
        pause.addActionListener(buttonListener);

        quit = new JButton("Quit Game");
        quit.setFocusable(false);
        quit.addActionListener(buttonListener);

        timerListener = new TimerListener();
        timer         = new Timer(cycleTime, timerListener);         // timer listener will fire every 100 milliseconds
        timer.start();
        scoreboard.setBounds(1000, 0, 300, 500);
        scoreboard.add(pause);
        scoreboard.add(save);
        scoreboard.add(quit);

        frame.add(scoreboard);

        //startTime = System.nanoTime();
        startTime = System.currentTimeMillis();
        startGame = true;
    }

    public void loadNextLevel() {
        pauseGame();
        canvas.setBlackScreen(true);

        canvas.setCurrentLevel(currentLevel);
        canvas.updateview();
        canvas.setLevel(Levels[currentLevel]);

        gameworld.setLevel(Levels[currentLevel]);
        scoreboard.setLevel(currentLevel);
        gameworld.reset();

        // Lets the player save after completing the level
        int result = JOptionPane.showConfirmDialog(null, "Do you want to save your progress?", "Save Prompt", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            saveGame();
        }
        canvas.setBlackScreen(false);
        resumeGame();
    }

    public void saveGame() {
        int x = gameworld.getX();
        int y = gameworld.getY();

        scoreboard.saveGame(x, y);
    }

    public void setUpLevels() {
        Levels[0] = makeLevel1();
        Levels[1] = makeLevel2();
    }

    public char[][] makeLevel1() {
        // T for transparent, X for ice, B for boulder, O for hole, F for finish (exit)
        char[][] level = {
            { 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'F', 'T', 'T', 'T', 'T', 'T', 'T' },
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
            { 'X', 'X', 'B', 'B', 'B', 'B', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'B', 'B', 'B', 'X', 'X', 'B', 'B', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'G', 'G', 'G', 'X', 'X', 'X', 'G', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'B', 'X', 'X', 'X', 'X', 'X' },
            { 'G', 'G', 'G', 'B', 'B', 'B', 'B', 'X', 'X', 'X', 'X', 'X', 'X', 'B', 'X', 'X', 'X', 'X', 'X', 'X' }
        };
        return level;
    }

    public char[][] makeLevel2() {
        // T for transparent, X for ice, B for boulder, O for hole, F for finish (exit)
        char[][] level = {
            { 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'F', 'T', 'T', 'T', 'T', 'T', 'T', 'T', 'T' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'B', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'B', 'X', 'B', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'O', 'X', 'B', 'X', 'X', 'X', 'X', 'X', 'X', 'B', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'O', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'B', 'X', 'X', 'X', 'X' },
            { 'B', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'B', 'X', 'X', 'X', 'B', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'O' },
            { 'X', 'O', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'B', 'B', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'B', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'B', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'B', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'B', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'B', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'B', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'B', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' },
            { 'B', 'B', 'B', 'B', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'B', 'X', 'X', 'X', 'X', 'X' },
            { 'G', 'X', 'X', 'X', 'X', 'B', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X', 'X' }
        };
        return level;
    }
}
