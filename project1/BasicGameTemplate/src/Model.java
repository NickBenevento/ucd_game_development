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
    private Controller controller = Controller.getInstance();
    private CopyOnWriteArrayList <GameObject> EnemiesList = new CopyOnWriteArrayList <GameObject>();
    private CopyOnWriteArrayList <GameObject> BulletList  = new CopyOnWriteArrayList <GameObject>();
    private char[][] Level;
    private int startX = 86;
    private int startY = 856;
    private int Score  = 0;
    private int Lives  = 3;
    //private boolean sliding = false;
    private Direction playerDirection;
    private int Moves = 0;
    private int targetX;
    private int targetY;

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
        targetX         = startX;
        targetY         = startY;
    }

    public void setLevel(char[][] Level) {
        this.Level = Level;
    }

    // This is the heart of the game , where the model takes in all the inputs ,decides the outcomes and then changes the model accordingly.
    public void gamelogic() {
        // Player Logic first
        playerLogic();
        // Enemy Logic next
        enemyLogic();
        // interactions between objects
        gameLogic();
    }

    private void gameLogic() {
        // this is a way to increment across the array list data structure


        //see if they hit anything
        // using enhanced for-loop style as it makes it alot easier both code wise and reading wise too
        for (GameObject temp : EnemiesList) {
            for (GameObject Bullet : BulletList) {
                if (Math.abs(temp.getCentre().getX() - Bullet.getCentre().getX()) < temp.getWidth() &&
                    Math.abs(temp.getCentre().getY() - Bullet.getCentre().getY()) < temp.getHeight()) {
                    EnemiesList.remove(temp);
                    BulletList.remove(Bullet);
                    Score++;
                }
            }

            if (Math.abs(temp.getCentre().getX() - Player.getCentre().getX()) < temp.getWidth() &&
                Math.abs(temp.getCentre().getY() - Player.getCentre().getY()) < temp.getHeight()) {
                EnemiesList.remove(temp);
                Score = -1;
            }
        }
    }

    private void enemyLogic() {
        // TODO Auto-generated method stub
        for (GameObject temp : EnemiesList) {
            // Move enemies

            temp.getCentre().ApplyVector(new Vector3f(0, -1, 0));
        }
    }

    private void playerLogic() {
        if (Player.getCentre().getX() == targetX && Player.getCentre().getY() == targetY) {
            playerDirection = Direction.STILL;
        } else {
            // animation of character is done by moving 40 pixels (to stay in line with the grid) in 10 different time steps (4 pixels each time until the character reaches the target square
            switch (playerDirection)
            {
                case LEFT:     // sliding left
                    Player.getCentre().ApplyVector(new Vector3f(-4, 0, 0));
                    if (Player.getCentre().getX() == 0) {
                        playerDirection = Direction.STILL;
                    }
                    break;

                case RIGHT:     // sliding right
                    Player.getCentre().ApplyVector(new Vector3f(4, 0, 0));
                    if (Player.getCentre().getX() == Player.getCentre().getBoundaryX()) {
                        playerDirection = Direction.STILL;
                    }
                    break;

                case UP:     // sliding up
                    Player.getCentre().ApplyVector(new Vector3f(0, 4, 0));
                    if (Player.getCentre().getY() == 0) {
                        playerDirection = Direction.STILL;
                    }
                    break;

                case DOWN:     // sliding down
                    Player.getCentre().ApplyVector(new Vector3f(0, -4, 0));
                    if (Player.getCentre().getY() == Player.getCentre().getBoundaryY()) {
                        playerDirection = Direction.STILL;
                    }
                    break;

                default:        // still
                    break;
            }
        }

        //check for movement if player is not currently moving
        if (playerDirection == Direction.STILL) {
            if (Controller.getInstance().isKeyAPressed()) {
                playerDirection = Direction.LEFT;
                setTargetX(Direction.LEFT);
                //targetX         = (int)Player.getCentre().getX() - 40;
                Moves++;
            } else if (Controller.getInstance().isKeyDPressed()) {
                playerDirection = Direction.RIGHT;
                setTargetX(Direction.RIGHT);
                //targetX         = (int)Player.getCentre().getX() + 40;
                Moves++;
            } else if (Controller.getInstance().isKeyWPressed()) {
                playerDirection = Direction.UP;
                setTargetY(Direction.UP);
                //targetY         = (int)Player.getCentre().getY() - 40;
                Moves++;
            } else if (Controller.getInstance().isKeySPressed()) {
                playerDirection = Direction.DOWN;
                setTargetY(Direction.DOWN);
                //targetY         = (int)Player.getCentre().getY() + 40;
                Moves++;
            }
        }

        // if collision
        //	player direction = still


        // check if the player hit an obstacle
        //if (playerDirection != Direction.STILL && playerIsStopped()) {
        //    playerDirection = Direction.STILL;
        //}
    }

    /* calculating target position based on the type of ground
     * in front of the player and the direction they want to move in
     */
    private void setTargetX(Direction d) {
        int row = (((int)Player.getCentre().getY() - 56) / 40) - 1;
        int col = ((int)Player.getCentre().getX() - startX) / 40;

        int i = row;

        if (d == Direction.LEFT) {
            // bounds checking
            if (col == 0) {
                return;
            }

            do {
                targetX -= 40;
                col--;
                // boulder check; want to stop 1 space before
                if (col == 0) {
                    break;
                }
                if (Level[row][col - 1] == 'B') {
                    break;
                }
            } while (Level[row][col] == 'X');
        } else {
            // Direction = RIGHT
            // bounds checking
            if (col + 1 == Level.length) {
                return;
            }
            do {
                targetX += 40;
                col++;
                if (col == Level.length - 1) {
                    break;
                }
                // boulder check; want to stop 1 space before
                if (Level[row][col + 1] == 'B') {
                    break;
                }
            } while (Level[row][col] == 'X');
        }
    }

    private void setTargetY(Direction d) {
        int row = (((int)Player.getCentre().getY() - 56) / 40) - 1;
        int col = ((int)Player.getCentre().getX() - startX) / 40;

        int i = row;

        if (d == Direction.UP) {
            // bounds checking
            if (row == 0) {
                return;
            }

            do {
                targetY -= 40;
                row--;
                // boulder check; want to stop 1 space before
                if (row == 0) {
                    break;
                }
                if (Level[row - 1][col] == 'B') {
                    break;
                }
            } while (Level[row][col] == 'X');
        } else {
            // Direction: DOWN
            // bounds checking
            if (row + 1 == Level.length) {
                return;
            }
            do {
                targetY += 40;
                row++;
                if (row == Level.length - 1) {
                    break;
                }
                // boulder check; want to stop 1 space before
                if (Level[row + 1][col] == 'B') {
                    break;
                }
            } while (Level[row][col] == 'X');
        }
    }

    private void resetTargetPosition() {
        targetX = (int)Player.getCentre().getX();
        targetY = (int)Player.getCentre().getY();
    }

    //private boolean playerIsStopped() {
    //    float x        = Player.getCentre().getX();
    //    float y        = Player.getCentre().getY();
    //    int   boundary = Player.getCentre().getBoundary();

    //    if (x == boundary || y == boundary || x == 0 || y == 0) {
    //        //playerDirection = Direction.STILL;
    //        return true;
    //    }
    //    return false;
    //}

    public GameObject getPlayer() {
        return Player;
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

    public int getLives() {
        return Lives;
    }
}
