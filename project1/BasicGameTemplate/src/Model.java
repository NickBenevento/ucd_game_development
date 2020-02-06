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
    private int Score       = 0;
    private int Lives       = 3;
    private boolean sliding = false;
    private Direction playerDirection;
    //private boolean onIce = true;
    private int moves = 0;

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
        Player          = new GameObject("../res/seal.png", 60, 40, new Point3f(500, 500, 0));
        playerDirection = Direction.STILL;
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
        // smoother animation is possible if we make a target position  // done but may try to change things for students

        //check for movement
        if (playerDirection == Direction.STILL) {
            if (Controller.getInstance().isKeyAPressed()) {
                Player.getCentre().ApplyVector(new Vector3f(-3, 0, 0));
                playerDirection = Direction.LEFT;
                moves++;
            } else if (Controller.getInstance().isKeyDPressed()) {
                Player.getCentre().ApplyVector(new Vector3f(3, 0, 0));
                playerDirection = Direction.RIGHT;
                moves++;
            } else if (Controller.getInstance().isKeyWPressed()) {
                Player.getCentre().ApplyVector(new Vector3f(0, 3, 0));
                playerDirection = Direction.UP;
                moves++;
            } else if (Controller.getInstance().isKeySPressed()) {
                Player.getCentre().ApplyVector(new Vector3f(0, -3, 0));
                playerDirection = Direction.DOWN;
                moves++;
            }
        }
        switch (playerDirection)
        {
            case LEFT:      // sliding left
                Player.getCentre().ApplyVector(new Vector3f(-3, 0, 0));
                if (Player.getCentre().getX() == 0) {
                    playerDirection = Direction.STILL;
                }
                break;

            case RIGHT:     // sliding right
                Player.getCentre().ApplyVector(new Vector3f(3, 0, 0));
                if (Player.getCentre().getX() == Player.getCentre().getBoundaryX()) {
                    playerDirection = Direction.STILL;
                }
                break;

            case UP:        // sliding up
                Player.getCentre().ApplyVector(new Vector3f(0, 3, 0));
                if (Player.getCentre().getY() == 0) {
                    playerDirection = Direction.STILL;
                }
                break;

            case DOWN:     // sliding down
                Player.getCentre().ApplyVector(new Vector3f(0, -3, 0));
                if (Player.getCentre().getY() == Player.getCentre().getBoundaryY()) {
                    playerDirection = Direction.STILL;
                }
                break;

            default:
                break;
        }

        // if collision
        //	player direction = still


        // check if the player hit an obstacle
        //if (playerDirection != Direction.STILL && playerIsStopped()) {
        //    playerDirection = Direction.STILL;
        //}
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

    private void CreateBullet() {
        BulletList.add(new GameObject("../res/bullet.png", 32, 64, new Point3f(Player.getCentre().getX(), Player.getCentre().getY(), 0.0f)));
    }

    public GameObject getPlayer() {
        return Player;
    }

    public CopyOnWriteArrayList <GameObject> getEnemies() {
        return EnemiesList;
    }

    public int getScore() {
        return Score;
    }

    public CopyOnWriteArrayList <GameObject> getBullets() {
        return BulletList;
    }

    public int getMoves() {
        return moves;
    }

    public int getLives() {
        return Lives;
    }
}
