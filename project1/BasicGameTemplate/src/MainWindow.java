/* Nick Benevento
 * 19207773
 */

import java.awt.FlowLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
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
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;

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
    private static JFrame frame = new JFrame("The Adventures of Stanley the Seal");
    private static Scoreboard scoreboard;                                           // high scores for the game
    private static JPanel LevelPicker = new JPanel();
    private JButton hideLevelPicker;
    private static Model gameworld   = new Model();
    private static Viewer canvas     = new Viewer(gameworld);
    private KeyListener Controller   = new Controller();
    private MouseListener Mouse      = new Mouse();
    private static int TargetFPS     = 100;
    private static boolean startGame = false;

    private JButton newGame;
    private JButton load;                       // button to load a previous game
    private JButton pause;                      // button to resume the game
    private JButton save;
    private JButton quit;
    private JButton chooseLevel;
    private ButtonListener buttonListener;          // listener for pause/play button

    LevelMaker levelMaker;                          // handles level creation
    char[][][] Levels             = new char[10][][];
    private int currentLevel      = 0;
    private int maxLevelCompleted = 0;
    private int lastLevel         = 5;

    private Timer timer;
    private static TimerListener timerListener;
    private int cycleTime = 10;
    private String levelDisplayText;

    public MainWindow() {
        setUpLevels();

        frame.setSize(1000, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        canvas.setBounds(0, 0, 1000, 1000);

        canvas.setLayout(new FlowLayout(FlowLayout.CENTER, 200, 500));

        newGame = new JButton("New Game");        // start button
        newGame.setPreferredSize(new Dimension(200, 40));
        load = new JButton("Load Game");
        load.setPreferredSize(new Dimension(200, 40));

        buttonListener = new ButtonListener();
        load.addActionListener(buttonListener);
        newGame.addActionListener(buttonListener);

        canvas.add(newGame);
        canvas.add(load);
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

        Toolkit.getDefaultToolkit().sync();
    }

    class TimerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            // load the next level if the current one is completed
            if (gameworld.finishedLevel()) {
                loadLevel(currentLevel + 1, true);
            }
            gameloop();
        }
    }

    class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            Object e = event.getSource();

            if (e == newGame) {
                // start a new game
                setUpGame();
            } else if (e == load) {
                // load a save file
                loadGame();
            } else if (e == pause) {
                // pause the game
                if (pause.getText().equals("Pause")) {
                    pauseGame();
                    pause.setText("Play");
                } else {
                    pause.setText("Pause");
                    resumeGame();
                }
            } else if (e == save) {
                if (gameIsPaused()) {
                    return;
                }
                // save the game
                pauseGame();
                // player can only save while they are still --> prevents exploits from saving/loading
                if (gameworld.getDirection() == Model.Direction.STILL) {
                    try {
                        BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
                        int            result = JOptionPane.showConfirmDialog(null, "There is already a save file. Are you sure you want to overwrite it?", "Save Prompt", JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.YES_OPTION) {
                            saveGame();
                        }
                    } catch (IOException exc) {
                        // automatically save if there is no save file
                        saveGame();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "You can't save the game while moving!", "Save Error!", JOptionPane.ERROR_MESSAGE);
                }
                resumeGame();
            } else if (e == quit) {
                //if (gameIsPaused()) {
                //    return;
                //}
                // quitting the game
                pauseGame();
                int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit? Any progress not saved will be lost.", "Quit Prompt", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    frame.setVisible(false);
                    System.exit(0);
                } else {
                    resumeGame();
                }
                pause.setText("Pause");
            } else if (e == chooseLevel) {
                if (gameIsPaused()) {
                    return;
                }
                pauseGame();
                LevelPicker.setVisible(true);
                resumeGame();
            }
        }
    }

    public void endgame() {
        canvas.setDisplayText(levelDisplayText, true);

        canvas.updateview();

        int result = JOptionPane.showConfirmDialog(null, "Would you like to restart from the beginning? Your progress will be saved.\n(Choosing 'No' will close the game)", "End Game Prompt", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            levelDisplayText = "Level " + (currentLevel + 1) + " completed!";
            currentLevel     = 0;
            canvas.setLevel(Levels[currentLevel]);
            gameworld.setLevel(Levels[currentLevel]);
            scoreboard.setLevel(currentLevel);
            gameworld.reset();
            saveGame();
        } else {
            System.exit(0);
        }
    }

    public void pauseGame() {
        timer.stop();
    }

    public void resumeGame() {
        timer.start();
    }

    public boolean gameIsPaused() {
        return pause.getText().equals("Play");
    }

    public void setUpGame() {
        levelDisplayText = "Level " + (currentLevel + 1) + " completed!";
        canvas.setStartScreen(false);
        LevelPicker.setLayout(new FlowLayout(0, 30, 30));
        LevelPicker.setBounds(1020, 550, 300, 300);
        LevelPicker.setVisible(true);
        addLevels();

        save = new JButton("Save Game");
        save.addActionListener(buttonListener);

        save.setFocusable(false);

        // remove the start menu buttons
        newGame.setVisible(false);
        load.setVisible(false);

        canvas.setVisible(true);
        canvas.addKeyListener(Controller); //adding the controller to the Canvas
        canvas.addMouseListener(Mouse);
        canvas.requestFocusInWindow();     // making sure the Canvas is in focus for keyboard input

        canvas.setLevel(Levels[currentLevel]);
        gameworld.setLevel(Levels[currentLevel]);
        gameworld.setCanvas(canvas);

        scoreboard = new Scoreboard();
        scoreboard.setLayout(new GridLayout(0, 1, 80, 40));
        scoreboard.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 80));
        scoreboard.setLevel(currentLevel);

        pause = new JButton("Pause");
        pause.setFocusable(false);
        pause.addActionListener(buttonListener);

        quit = new JButton("Quit Game");
        quit.setFocusable(false);
        quit.addActionListener(buttonListener);

        chooseLevel = new JButton("Choose Level");
        chooseLevel.setFocusable(false);
        chooseLevel.addActionListener(buttonListener);

        timerListener = new TimerListener();
        timer         = new Timer(cycleTime, timerListener); // timer listener will fire every 10 milliseconds
        timer.start();
        scoreboard.setBounds(1000, 0, 300, 500);
        scoreboard.add(pause);
        scoreboard.add(save);
        scoreboard.add(quit);
        scoreboard.add(chooseLevel);
        frame.add(LevelPicker);

        frame.setSize(1300, 1000);
        frame.add(scoreboard);

        startGame = true;
    }

    public void loadLevel(int levelToLoad, boolean showCompletedScreen) {
        // the current level the player is on is the one we want to load
        currentLevel = levelToLoad;
        pauseGame();

        // make sure the levelToLoad exists
        if (levelToLoad <= lastLevel) {
            canvas.setLevel(Levels[levelToLoad]);
            gameworld.setLevel(Levels[levelToLoad]);
            scoreboard.setLevel(levelToLoad);
            gameworld.reset();
        }

        // only provide the save option if the level hasn't been completed before
        if (levelToLoad > maxLevelCompleted) {
            canvas.setBlackScreen(true);
            levelDisplayText = "Level " + levelToLoad + " completed!";
            canvas.setDisplayText(levelDisplayText, false);
            canvas.updateview();

            // if the player completed the last level
            if (levelToLoad > lastLevel) {
                endgame();
            } else {
                maxLevelCompleted = levelToLoad;
                addLevels();
                // Lets the player save after completing the level
                int result = JOptionPane.showConfirmDialog(null, "Do you want to save your progress?", "Save Prompt", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    saveGame();
                }
            }
            canvas.setBlackScreen(false);
            canvas.updateview();

            if (levelToLoad == 3) {
                JOptionPane.showMessageDialog(null, "Use the mouse to click on the directional tiles");
            }
        }

        resumeGame();
    }

    public void loadGame() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("save.txt"));
            setUpGame();
            String str;

            // get current level
            str          = reader.readLine();
            currentLevel = Integer.parseInt(str);
            gameworld.setLevel(Levels[currentLevel]);
            canvas.setLevel(Levels[currentLevel]);
            canvas.setDisplayText(levelDisplayText, false);
            scoreboard.setLevel(currentLevel);

            // get max level completed
            str = reader.readLine();
            maxLevelCompleted = Integer.parseInt(str);
            addLevels();

            // get player x coordinate
            str = reader.readLine();
            gameworld.getPlayer().getCentre().setX(Integer.parseInt(str));

            // get player y coordinate
            str = reader.readLine();
            gameworld.getPlayer().getCentre().setY(Integer.parseInt(str));

            gameworld.resetTargetPosition();

            reader.close();
        } catch (IOException exc) {
            // if no save file was found
            JOptionPane.showMessageDialog(null, "No save file found!", "Load Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveGame() {
        int x = gameworld.getX();
        int y = gameworld.getY();

        String filename = "save.txt";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(currentLevel + "\n");
            writer.write(maxLevelCompleted + "\n");
            writer.write(x + "\n");
            writer.write(y + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("The file could not be written");
        }
    }

    public void setUpLevels() {
        levelMaker = new LevelMaker();
        levelMaker.makeLevels(Levels);
    }

    public void addLevels() {
        LevelPicker.removeAll();

        for (int i = 0; i <= lastLevel; i++) {
            JButton button = new JButton("" + (i + 1));
            button.setPreferredSize(new Dimension(50, 50));
            button.setFocusable(false);
            // add a listener for each button
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (pause.getText().equals("Play")) {
                        return;
                    }
                    if (gameworld.getDirection() != Model.Direction.STILL) {
                        pauseGame();
                        JOptionPane.showMessageDialog(null, "You can't load a level while moving!", "Level Error!", JOptionPane.ERROR_MESSAGE);
                        resumeGame();
                        return;
                    }
                    // get the number of the button that was clicked and load that level
                    JButton temp = (JButton)e.getSource();
                    int level    = Integer.parseInt(temp.getText()) - 1;
                    loadLevel(level, false);
                }
            });
            // make sure the player can't load levels they haven't reached yet
            if (i > maxLevelCompleted) {
                button.setEnabled(false);
            }
            LevelPicker.add(button);
        }

        hideLevelPicker = new JButton("Hide Level Picker");
        hideLevelPicker.setFocusable(false);

        hideLevelPicker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameIsPaused()) {
                    return;
                }
                LevelPicker.setVisible(false);
            }
        });

        LevelPicker.add(hideLevelPicker);
    }
}
