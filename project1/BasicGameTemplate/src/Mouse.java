import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

public class Mouse implements MouseListener {
    private static int row      = 0;
    private static int col      = 0;
    private static int gridSize = 40;
    private static int topLeftX = 100;
    private static int topLeftY = 100;

    private static final Mouse instance = new Mouse();

    public Mouse() {
    }

    public static Mouse getInstance() {
        return instance;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        if (y < 100 || y > 899 || x < 100 || x > 899) {
            return;
        }

        row = (y - topLeftY) / gridSize;
        col = (x - topLeftX) / gridSize;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }
}
