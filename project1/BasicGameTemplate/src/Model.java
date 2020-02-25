import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import util.GameObject;
import util.Point3f;
import util.Vector3f;

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
public class Model {
    private GameObject Player;
    private Viewer canvas;
    private Controller controller = Controller.getInstance();
    private Mouse mouse           = Mouse.getInstance();
    private CopyOnWriteArrayList <GameObject> EnemiesList = new CopyOnWriteArrayList <GameObject>();
    private char[][] Level;
    private int cellSize = 40;
    private int startX   = 86;
    private int startY   = 856;
    private int Score    = 0;
    private Direction playerDirection;
    private int Moves = 0;
    private int targetX;
    private int targetY;
    private boolean finishedLevel = false;
    //private int hole = -1; // want to draw a black screen for 3 frames if the player falls in a hole

    enum Direction {
        LEFT,
        RIGHT,
        UP,
        DOWN,
        STILL
    }

    public Model() {
        //setup game world
        //Player
        Player          = new GameObject("../res/seal.png", 60, 40, new Point3f(startX, startY, 0));
        playerDirection = Direction.STILL;
        resetTargetPosition();
    }

    public void setCanvas(Viewer canvas) {
        this.canvas = canvas;
    }

    public void setLevel(char[][] Level) {
        this.Level = Level;
    }

    // This is the heart of the game , where the model takes in all the inputs ,decides the outcomes and then changes the model accordingly.
    public void gamelogic() {
        checkForMouse();
        checkForMovement();
    }

    private void checkForMouse() {
        // mouse input
        int r = mouse.getRow();
        int c = mouse.getCol();

        switch (Level[r][c])
        {
            case 'R':
                Level[r][c] = 'U';
                mouse.setRow(0);
                mouse.setCol(0);
                canvas.updateCell(r, c, "../res/up_arrow.png");
                break;

            case 'U':
                Level[r][c] = 'L';
                mouse.setRow(0);
                mouse.setCol(0);
                canvas.updateCell(r, c, "../res/left_arrow.png");
                break;

            case 'L':
                Level[r][c] = 'D';
                mouse.setRow(0);
                mouse.setCol(0);
                canvas.updateCell(r, c, "../res/down_arrow.png");
                break;

            case 'D':
                Level[r][c] = 'R';
                mouse.setRow(0);
                mouse.setCol(0);
                canvas.updateCell(r, c, "../res/right_arrow.png");
                break;

            default:
                break;
        }
    }

    private void checkForMovement() {
        // movement key input
        if (getX() == targetX && getY() == targetY) {
            playerDirection = Direction.STILL;
            // check if player is at finish tile or fell in a hole
            int row = ((getY() - 56) / 40) - 1;
            int col = (getX() - startX) / 40;
            if (Level[row][col] == 'F') {
                finishedLevel = true;
            } else if (Level[row][col] == 'O') {
                resetStartPosition();
                resetTargetPosition();
            }
        } else {
            // animation of character is done by moving 40 pixels (to stay in line with the grid) in 5 different time steps (8 pixels each time until the character reaches the target square
            switch (playerDirection)
            {
                case LEFT:
                    Player.getCentre().ApplyVector(new Vector3f(-8, 0, 0));
                    break;

                case RIGHT:
                    Player.getCentre().ApplyVector(new Vector3f(8, 0, 0));
                    break;

                case UP:
                    Player.getCentre().ApplyVector(new Vector3f(0, 8, 0));
                    break;

                case DOWN:
                    Player.getCentre().ApplyVector(new Vector3f(0, -8, 0));
                    break;

                default:        // still
                    break;
            }
        }

        //check for movement if player is not currently moving
        if (playerDirection == Direction.STILL) {
            if (controller.isKeyAPressed()) {
                playerDirection = Direction.LEFT;
                setTargetX();
                if (targetX != getX()) {
                    Moves++;
                }
            } else if (controller.isKeyDPressed()) {
                playerDirection = Direction.RIGHT;
                setTargetX();
                if (targetX != getX()) {
                    Moves++;
                }
            } else if (controller.isKeyWPressed()) {
                playerDirection = Direction.UP;
                setTargetY();
                if (targetY != getY()) {
                    Moves++;
                }
            } else if (controller.isKeySPressed()) {
                playerDirection = Direction.DOWN;
                setTargetY();
                if (targetY != getY()) {
                    Moves++;
                }
            }
        }
    }

    /* calculating target position based on the type of ground
     * in front of the player and the direction they want to move in
     */
    private void setTargetX() {
        int row = ((getY() - 56) / 40) - 1;
        int col = (getX() - startX) / 40;

        int i = row;

        if (playerDirection == Direction.LEFT) {
            // bounds checking
            if (col == 0 || Level[row][col - 1] == 'B' || Level[row][col - 1] == 'T') {
                return;
            }

            do {
                targetX -= 40;
                col--;
                // boulder check; want to stop 1 space before
                if (col == 0) {
                    break;
                }
                if (Level[row][col - 1] == 'B' || Level[row][col - 1] == 'T') {
                    break;
                }
            } while (Level[row][col] == 'X');
        } else {
            // Direction = RIGHT
            // bounds checking
            if (col + 1 == Level[row].length || Level[row][col + 1] == 'B' || Level[row][col + 1] == 'T') {
                return;
            }
            do {
                targetX += 40;
                col++;
                if (col == Level[row].length - 1) {
                    break;
                }
                // boulder check; want to stop 1 space before
                if (Level[row][col + 1] == 'B' || Level[row][col + 1] == 'T') {
                    break;
                }
            } while (Level[row][col] == 'X');
        }
    }

    private void setTargetY() {
        int row = (((int)Player.getCentre().getY() - 56) / cellSize) - 1;
        int col = ((int)Player.getCentre().getX() - startX) / cellSize;

        int i = row;

        if (playerDirection == Direction.UP) {
            // bounds checking
            if (row == 0 || Level[row - 1][col] == 'B' || Level[row - 1][col] == 'T') {
                return;
            }

            do {
                targetY -= 40;
                row--;
                // boulder check; want to stop 1 space before
                if (row == 0) {
                    break;
                }
                if (Level[row - 1][col] == 'B' || Level[row - 1][col] == 'T') {
                    break;
                }
            } while (Level[row][col] == 'X');
        } else {
            // Direction: DOWN
            // bounds checking
            if (row + 1 == Level.length || Level[row + 1][col] == 'B' || Level[row + 1][col] == 'T') {
                return;
            }
            do {
                targetY += 40;
                row++;
                if (row == Level.length - 1) {
                    break;
                }
                // boulder check; want to stop 1 space before
                if (Level[row + 1][col] == 'B' || Level[row + 1][col] == 'T') {
                    break;
                }
            } while (Level[row][col] == 'X');
        }
    }

    public void reset() {
        resetStartPosition();
        resetTargetPosition();
        finishedLevel = false;
    }

    public void resetTargetPosition() {
        targetX = getX();
        targetY = getY();
    }

    public void resetStartPosition() {
        Player.getCentre().setX(startX);
        Player.getCentre().setY(startY);
    }

    public boolean finishedLevel() {
        return finishedLevel;
    }

    public GameObject getPlayer() {
        return Player;
    }

    public Direction getDirection() {
        return playerDirection;
    }

    public CopyOnWriteArrayList <GameObject> getEnemies() {
        return EnemiesList;
    }

    public int getScore() {
        return Score;
    }

    public int getMoves() {
        return Moves;
    }

    public int getX() {
        return (int)Player.getCentre().getX();
    }

    public int getY() {
        return (int)Player.getCentre().getY();
    }

    public int getTargetX() {
        return targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public void setMoves(int moves) {
        this.Moves = moves;
    }
}
