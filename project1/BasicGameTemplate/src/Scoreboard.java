import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import javax.swing.JButton;
import java.util.ArrayList;
import java.io.*;
import java.text.DecimalFormat;
import util.GameObject;

public class Scoreboard extends JPanel {
    private JLabel Moves;
    //private JLabel Time;
    private JLabel levelDisplay;
    //private double elapsedTime;
    private int moves;
    private int level;
    DecimalFormat d2 = new DecimalFormat("0.0");

    public Scoreboard() {
        /* start with 0 */
        //elapsedTime = 0;
        /* sets the font */
        Font  scores = new Font("SansSerif", Font.BOLD, 28);
        Color back   = getBackground();

        levelDisplay = new JLabel("Level " + (level + 1));
        levelDisplay.setFont(new Font("SansSerif", Font.BOLD, 30));

        moves = 0;
        Moves = new JLabel("Moves: " + moves);
        Moves.setPreferredSize(new Dimension(150, 50));
        Moves.setAlignmentX(CENTER_ALIGNMENT);
        //Time = new JLabel("Time: " + d2.format(elapsedTime));
        //Time.setPreferredSize(new Dimension(150, 100));
        //Time.setAlignmentX(CENTER_ALIGNMENT);
        /* setting the font */
        Font font = new Font("SansSerif", Font.BOLD, 25);
        Moves.setFont(font);
        //Time.setFont(font);

        /* setting the layout and adding the lables to the panel */
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(levelDisplay);
        add(Moves);
        //add(Time);
        //add(save);
    }

    //public void updateTime(double time) {
    //    elapsedTime += time;
    //    Time.setText("Time: " + d2.format(elapsedTime) + "s");
    //}

    public void setLevel(int level) {
        this.level = level;
        levelDisplay.setText("Level " + (level + 1));
    }

    public void setMoves(int moves) {
        this.moves = moves;
        Moves.setText("Moves: " + this.moves);
    }

    //public double getTime() {
    //    return elapsedTime;
    //}

    //public void setTime(double time) {
    //    elapsedTime = time;
    //    Time.setText("Time: " + d2.format(elapsedTime));
    //}

    public int getMoves() {
        return moves;
    }

    public void saveGame(int x, int y) {
        String filename = "save.txt";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(level + "\n");
            writer.write(moves + "\n");
            writer.write(x + "\n");
            writer.write(y + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("The file could not be written");
        }
    }
}
