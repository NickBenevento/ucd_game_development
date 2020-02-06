/**
 * CSCI 2113 - Project 2 - Alien Attack
 *
 * @author Nick Benevento
 *
 */
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.io.*;
import java.text.DecimalFormat;

public class Scoreboard extends JPanel {
    private JTextArea high_scores; /* displays the top 10 scores of all time */
    private JLabel Moves;
    private JLabel Time;
    //private int currentScore;
    //private int currentMoves;
    private double elapsedTime;
    ArrayList <String> score_list;
    DecimalFormat d2 = new DecimalFormat("0.0");

    public Scoreboard() {
        /* start with 0 */
        //currentScore = 0;
        elapsedTime = 0;
        score_list  = new ArrayList <String>();
        /* sets the font */
        Font scores = new Font("SansSerif", Font.BOLD, 28);
        //high_scores = new JTextArea("High Scores:\n\n");
        //updateScores();
        //high_scores.setFont(scores);
        //high_scores.setPreferredSize(new Dimension(200, 100));
        //high_scores.setEditable(false);
        Color back = getBackground();
        //high_scores.setBackground(back);

        //Score = new JLabel("Score: " + currentScore);
        Moves = new JLabel("Moves: ");
        Moves.setPreferredSize(new Dimension(150, 50));
        Moves.setAlignmentX(CENTER_ALIGNMENT);
        Time = new JLabel("Time: " + d2.format(elapsedTime));
        Time.setPreferredSize(new Dimension(150, 100));
        Time.setAlignmentX(CENTER_ALIGNMENT);
        /* setting the font */
        Font font = new Font("SansSerif", Font.BOLD, 25);
        Moves.setFont(font);
        Time.setFont(font);

        /* setting the layout and adding the lables to the panel */
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        //add(high_scores);
        add(Moves);
        add(Time);
    }

    //public void updateScores() {
    //    /* clears the arraylist from previous scores */
    //    score_list.clear();
    //    readScores(); /* reads in the new scores */
    //    high_scores.setText("High Scores:\n\n");
    //    /* displays the new high scores */
    //    for (int i = 0; i < score_list.size(); i++) {
    //        high_scores.setText(high_scores.getText() + score_list.get(i) + "\n");
    //    }
    //}

    public void updateTime(double time) {
        elapsedTime += time;
        Time.setText("Time: " + d2.format(elapsedTime) + "s");
    }

    //public void addPoints(int points) {
    //    currentScore += points;
    //    Score.setText("Score: " + currentScore);
    //}

    public void updateMoves(int moves) {
        Moves.setText("Moves: " + moves);
    }

    public void addTime(double time) {
        elapsedTime += time;
        Time.setText("Time: " + d2.format(elapsedTime));
    }

    public double getTime() {
        return elapsedTime;
    }

    public void setTime(int time) {
        elapsedTime = time;
        Time.setText("Time: " + d2.format(elapsedTime));
    }

    //public int getScore() {
    //    return currentScore;
    //}

    //public void setScore(int score) {
    //    currentScore = score;
    //    Score.setText("Score: " + currentScore);
    //}

    public void readScores() {
        String filename = "high_scores.txt";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String         str;
            /* reads the scores into the arraylist */
            while ((str = reader.readLine()) != null) {
                score_list.add(str);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("the file could not be read");
        }
    }

    //public void putScore() {
    //    String filename = "high_scores.txt";
    //    int    index    = -1;
    //    int    count    = 0;
    //    int    check    = 0; /* makes sure the score is put in the top-most position of the list */

    //    for (int i = 0; i < score_list.size(); i++) {
    //        int array_score = Integer.parseInt(score_list.get(i));
    //        /* checks if the current score is greater than the score in the file */
    //        if (currentScore > array_score && check != 1) {
    //            index = count;         /* set the index */
    //            check = 1;
    //        }
    //        count++;
    //    }
    //    /* if it is the first score, just add it to the file */
    //    if (score_list.size() == 0) {
    //        index = 0;
    //    }
    //    /* if the scores are less than 10, add it to the end */
    //    else if (count < 10) {
    //        index = count;
    //    }

    //    /* if the score needs to be added to the list */
    //    if (index != -1) {
    //        try{
    //            JOptionPane.showMessageDialog(null, "You made it on the leaderboard!", "Congratulations!", JOptionPane.OK_OPTION);
    //            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
    //            /* if the lsit is full, remove the lowest score */
    //            if (score_list.size() == 10) {
    //                score_list.remove(score_list.size() - 1);
    //            }
    //            /* add the score to the list */
    //            score_list.add(index, Integer.toString(currentScore));
    //            /* write the new scores to the file */
    //            for (int i = 0; i < score_list.size(); i++) {
    //                writer.write(score_list.get(i) + "\n");
    //            }
    //            writer.close();
    //        } catch (IOException e) {
    //            System.out.println("The file could not be written");
    //        }
    //    }
    //}
}
