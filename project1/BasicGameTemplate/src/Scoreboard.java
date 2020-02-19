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
    //private GameObject player;
    private Model gameworld;
    private JButton save;
    private ButtonListener saveListener;
    private JTextArea high_scores; /* displays the top 10 scores of all time */
    private JLabel Moves;
    private JLabel Time;
    private double elapsedTime;
    private int moves;
    private int level;
    ArrayList <String> score_list;
    DecimalFormat d2 = new DecimalFormat("0.0");

    public Scoreboard(Model gameworld) {
        this.gameworld = gameworld;

        save         = new JButton("Save Game");
        saveListener = new ButtonListener();
        save.addActionListener(saveListener);


        /* start with 0 */
        elapsedTime = 0;
        score_list  = new ArrayList <String>();
        /* sets the font */
        Font  scores = new Font("SansSerif", Font.BOLD, 28);
        Color back   = getBackground();

        moves = 0;
        Moves = new JLabel("Moves: " + moves);
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
        add(Moves);
        add(Time);
        add(save);
    }

    public void updateTime(double time) {
        elapsedTime += time;
        Time.setText("Time: " + d2.format(elapsedTime) + "s");
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setMoves(int moves) {
        this.moves = moves;
        Moves.setText("Moves: " + this.moves);
    }

    public double getTime() {
        return elapsedTime;
    }

    public void setTime(double time) {
        elapsedTime = time;
        Time.setText("Time: " + d2.format(elapsedTime));
    }

    public int getMoves() {
        return moves;
    }

    public void saveGame() {
        String filename = "save.txt";
        int    x        = (int)gameworld.getPlayer().getCentre().getX();
        int    y        = (int)gameworld.getPlayer().getCentre().getY();

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(level + "\n");
            writer.write(moves + "\n");
            writer.write(d2.format(elapsedTime) + "\n");
            writer.write(x + "\n");
            writer.write(y + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("The file could not be written");
        }
    }

    public void loadGame() {
    }

    class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            //Object e = event.getSource();
            // the player can only save while they are still --> prevents exploits from saving/loading
            if (gameworld.getDirection() == Model.Direction.STILL) {
                saveGame();
            } else {
                JOptionPane.showMessageDialog(null, "You can't save the game while sliding!", "Save Error!", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
