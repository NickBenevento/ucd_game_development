import java.awt.FlowLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
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
    private MouseListener Mouse      = new Mouse();
    private static int TargetFPS     = 100;
    private static boolean startGame = false;
    private String levelDisplayText;

    private JButton startMenuButton;
    private JButton load;                       // button to load a previous game
    private JButton pause;                      // button to resume the game
    private JButton save;
    private JButton quit;
    private ButtonListener buttonListener;          // listener for pause/play button

    LevelMaker levelMaker;                          // handles level creation
    char[][][] Levels        = new char[10][][];
    private int currentLevel = 0;
    private int lastLevel    = 5;

    private Timer timer;
    private static TimerListener timerListener;
    private int cycleTime = 10;
    //private long startTime;
    //private long pauseTime      = 0;
    //private long totalPauseTime = 0;


    public MainWindow() {
        setUpLevels();

        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        canvas.setBounds(0, 0, 1000, 1000);

        canvas.setLayout(new FlowLayout(FlowLayout.CENTER, 200, 500));

        //canvas.updateview();

        startMenuButton = new JButton("New Game");        // start button
        startMenuButton.setPreferredSize(new Dimension(200, 40));
        load = new JButton("Load Game");
        load.setPreferredSize(new Dimension(200, 40));

        buttonListener = new ButtonListener();
        load.addActionListener(buttonListener);
        startMenuButton.addActionListener(buttonListener);

        canvas.add(startMenuButton);
        canvas.add(load);
        //frame.add(load);
        //frame.add(startMenuButton);
        frame.add(canvas);
        canvas.setVisible(true);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        MainWindow hello = new MainWindow(); //sets up environment
    }

    //Basic Model-View-Controller pattern
    private static void gameloop() {
        // GAMELOOP

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
            //scoreboard.setTime((System.currentTimeMillis() - startTime - totalPauseTime) / 1000.0);

            gameloop();
        }
    }

    class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            Object e = event.getSource();

            if (e == startMenuButton) {
                setUpGame();
            } else if (e == load) {
                loadGame();
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

    public void endgame() {
        canvas.setDisplayText(levelDisplayText, true);

        canvas.updateview();

        int result = JOptionPane.showConfirmDialog(null, "Would you like to restart from the beginning?", "End Game Prompt", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            currentLevel     = 0;
            levelDisplayText = "Level " + (currentLevel + 1) + " completed!";
            //startTime        = System.currentTimeMillis();
            //pauseTime      = 0;
            //totalPauseTime = 0;
            gameworld.setMoves(0);
        } else {
            System.exit(0);
        }
    }

    public void pauseGame() {
        //pauseTime = System.currentTimeMillis();
        pause.setText("Play");
        timer.stop();
    }

    public void resumeGame() {
        pause.setText("Pause");
        //long temp = pauseTime;
        //pauseTime       = System.currentTimeMillis() - temp;
        //totalPauseTime += pauseTime;
        timer.start();
    }

    public void setUpGame() {
        levelDisplayText = "Level " + (currentLevel + 1) + " completed!";
        canvas.setStartScreen(false);

        save = new JButton("Save Game");
        save.addActionListener(buttonListener);

        save.setFocusable(false);

        // remove the start menu buttons
        startMenuButton.setVisible(false);
        load.setVisible(false);

        canvas.setVisible(true);
        canvas.addKeyListener(Controller); //adding the controller to the Canvas
        canvas.addMouseListener(Mouse);
        canvas.requestFocusInWindow();     // making sure that the Canvas is in focus so keyboard input will be taking in .

        canvas.setLevel(Levels[currentLevel]);
        gameworld.setLevel(Levels[currentLevel]);
        gameworld.setCanvas(canvas);

        scoreboard = new Scoreboard();
        scoreboard.setLevel(currentLevel);

        pause = new JButton("Pause");
        pause.setFocusable(false);
        pause.addActionListener(buttonListener);

        quit = new JButton("Quit Game");
        quit.setFocusable(false);
        quit.addActionListener(buttonListener);

        timerListener = new TimerListener();
        timer         = new Timer(cycleTime, timerListener); // timer listener will fire every 10 milliseconds
        timer.start();
        scoreboard.setBounds(1000, 0, 300, 500);
        scoreboard.add(pause);
        scoreboard.add(save);
        scoreboard.add(quit);

        frame.setSize(1300, 1000);
        frame.add(scoreboard);

        //startTime = System.currentTimeMillis();
        startGame = true;
    }

    public void loadNextLevel() {
        canvas.setBlackScreen(true);

        levelDisplayText = "Level " + (currentLevel) + " completed!";
        canvas.setDisplayText(levelDisplayText, false);
        canvas.updateview();

        if (currentLevel == lastLevel) {
            endgame();
        }

        pauseGame();
        canvas.setLevel(Levels[currentLevel]);

        gameworld.setLevel(Levels[currentLevel]);
        scoreboard.setLevel(currentLevel);
        gameworld.reset();

        // Lets the player save after completing the level
        if (currentLevel != 0) {
            int result = JOptionPane.showConfirmDialog(null, "Do you want to save your progress?", "Save Prompt", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                saveGame();
            }
        }
        canvas.setBlackScreen(false);
        resumeGame();
    }

    public void loadGame() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
            setUpGame();
            String str;
            str          = reader.readLine();
            currentLevel = Integer.parseInt(str);
            gameworld.setLevel(Levels[currentLevel]);
            canvas.setLevel(Levels[currentLevel]);
            canvas.setDisplayText(levelDisplayText, false);
            scoreboard.setLevel(currentLevel);

            str = reader.readLine();
            int moves = Integer.parseInt(str);
            scoreboard.setMoves(moves);
            gameworld.setMoves(moves);

            //str = reader.readLine();
            //scoreboard.setTime(Double.parseDouble(str));
            //startTime = Long.parseLong(str);

            //str            = reader.readLine();
            //totalPauseTime = Long.parseLong(str);

            str = reader.readLine();
            gameworld.getPlayer().getCentre().setX(Integer.parseInt(str));

            str = reader.readLine();
            gameworld.getPlayer().getCentre().setY(Integer.parseInt(str));

            gameworld.resetTargetPosition();

            reader.close();
        } catch (IOException exc) {
            JOptionPane.showMessageDialog(null, "No save file found!", "Load Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveGame() {
        int x = gameworld.getX();
        int y = gameworld.getY();

        scoreboard.saveGame(x, y);
    }

    public void setUpLevels() {
        levelMaker = new LevelMaker();
        levelMaker.makeLevels(Levels);
    }
}
