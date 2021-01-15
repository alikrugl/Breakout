import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.util.RandomGenerator;
import com.shpp.cs.a.graphics.WindowProgram;

import java.awt.*;
import java.awt.event.MouseEvent;

/** File: Breakout.java
 *
 * Breakout game
 *
 * More details in Task #4 - breakout
 *
 */
public class Breakout extends WindowProgram {
    /**
     * Width and height of application window in pixels
     */
    public static final int APPLICATION_WIDTH = 400;
    public static final int APPLICATION_HEIGHT = 600;

    /**
     * Dimensions of game board (usually the same)
     */
    private static final int WIDTH = APPLICATION_WIDTH;
    private static final int HEIGHT = APPLICATION_HEIGHT;

    /**
     * The amount of time to pause between frames (60 fps).
     */
    private static final double PAUSE_TIME = 1000.0 / 60;

    /**
     * Dimensions of the paddle
     */
    private static final int PADDLE_WIDTH = 60;
    private static final int PADDLE_HEIGHT = 10;

    /**
     * Offset of the paddle up from the bottom
     */
    private static final int PADDLE_Y_OFFSET = 30;

    /**
     * Number of bricks per row
     */
    private static final int NBRICKS_PER_ROW = 10;

    /**
     * Number of rows of bricks
     */
    private static final int NBRICK_ROWS = 10;

    /**
     * Separation between bricks
     */
    private static final int BRICK_SEP = 4;

    /**
     * Width of a brick
     */
    private static final int BRICK_WIDTH =
            (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

    /**
     * Height of a brick
     */
    private static final int BRICK_HEIGHT = 8;

    /**
     * Radius of the ball in pixels
     */
    private static final int BALL_RADIUS = 10;

    /**
     * Diameter of the ball in pixels
     */
    private static final int BALL_DIAMETER = BALL_RADIUS * 2;

    /**
     * Offset of the top brick row from the top
     */
    private static final int BRICK_Y_OFFSET = 70;

    /**
     * Number of turns
     */
    private static final int NTURNS = 3;


    /* Paddle object  */
    private GRect paddle;
    /* horizontal and vertical velocities */
    private double vx, vy;
    /* counts remaining bricks, player wins at 0 */
    private int brickCounter = NBRICKS_PER_ROW * NBRICK_ROWS;


    /**
     * Runs the Breakout full game.
     */
    public void run() {
        addMouseListeners();
        setField();
        playFullGame();
        checkTheResult();
    }

    /**
     * Draw field with paddle and 10 rows of rainbow bricks (each 2 rows have the same color)
     */
    private void setField() {
        paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
        paddle.setFilled(true);
        paddle.setFillColor(Color.BLACK);

        /* Location of y coordinate of paddle*/
        double paddleYLocation = getHeight() - PADDLE_Y_OFFSET;

        add(paddle,
                (getWidth() - PADDLE_WIDTH) / 2.0,
                paddleYLocation);

        /* Draw rainbow bricks*/
        for (int i = 0; i < NBRICK_ROWS; i++) {
            for (int j = 0; j < NBRICKS_PER_ROW; j++) {

                /* the width of the whole row */
                double bricksAndSpaces = BRICK_WIDTH * NBRICKS_PER_ROW + (NBRICKS_PER_ROW - 1) * BRICK_SEP;


                double x = (getWidth() - bricksAndSpaces) / 2.0 + j * (BRICK_WIDTH + BRICK_SEP);
                double y = BRICK_Y_OFFSET + i * (BRICK_HEIGHT + BRICK_SEP);

                /* set color to each row of bricks*/
                GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
                brick.setFilled(true);
                if (i <= 1) {
                    brick.setColor(Color.RED);
                    brick.setFilled(true);
                } else if (i <= 3) {
                    brick.setColor(Color.ORANGE);
                    brick.setFilled(true);
                } else if (i <= 5) {
                    brick.setColor(Color.YELLOW);
                    brick.setFilled(true);
                } else if (i <= 7) {
                    brick.setColor(Color.GREEN);
                    brick.setFilled(true);
                } else {
                    brick.setColor(Color.CYAN);
                    brick.setFilled(true);
                }
                add(brick);
            }
        }
    }

    /**
     * Move center of paddle using the mouse. Paddle did not move out the active frame/
     */
    public void mouseMoved(MouseEvent mouse) {

        /* right limit of the mouse*/
        double rightLimitOfMouse = WIDTH - PADDLE_WIDTH / 2.0;

        /* Location of y coordinate of paddle*/
        double paddleYLocation = getHeight() - PADDLE_Y_OFFSET;

        if (mouse.getX() <= rightLimitOfMouse && mouse.getX() >= PADDLE_WIDTH / 2.0) {
            paddle.setLocation(mouse.getX() - PADDLE_WIDTH / 2.0, paddleYLocation);

        } else if (mouse.getX() > rightLimitOfMouse) {
            paddle.setLocation(WIDTH - PADDLE_WIDTH, paddleYLocation);
        } else {
            paddle.setLocation(0, paddleYLocation);
        }
    }

    /**
     * Start the breakout game. Player has only 3 attempts. Every attempt game waits for the mouse clicked.
     */
    private void playFullGame() {
        for (int i = 0; i < NTURNS; i++) {
            waitForUser();
            playBreakout();
            if (brickCounter == 0)
                break;
        }
    }

    /**
     * Waiting for the user clicked the mouse to start the game or start next attempt
     */
    private void waitForUser() {
        GLabel clickToStart = new GLabel("Click to play!");
        clickToStart.setFont("London-40");
        clickToStart.setColor(Color.CYAN);
        clickToStart.setLocation(WIDTH / 2.0 - clickToStart.getWidth() / 2.0,
                HEIGHT / 2.0 - clickToStart.getHeight() / 2.0);
        add(clickToStart);
        waitForClick();
        remove(clickToStart);
    }

    /**
     * Start one round of breakout game. Creating ball and move it according to the logic of the game
     */
    private void playBreakout() {

        /* Main ball that will moves and collide with walls, bricks and paddle */
        GOval ball = new GOval(getWidth() / 2.0 - BALL_RADIUS,
                getHeight() / 2.0 - BALL_RADIUS,
                BALL_DIAMETER,
                BALL_DIAMETER);
        ball.setFilled(true);
        paddle.setFillColor(Color.BLACK);
        add(ball);

        RandomGenerator rgen = RandomGenerator.getInstance();
        /* x and y velocities of the ball
         * vy is constant that equals to 3
         * vx is random double between 1 and 3
         * and with 50% probability is negative
         */
        vy = 3.0;
        vx = rgen.nextDouble(1.0, 3.0);
        if (rgen.nextBoolean(0.5))
            vx = -vx;

        /* ball moves while it above bottom border */

        while ((ball.getY() < getHeight()) && (brickCounter != 0)) {
            moveBall(ball);
            checkCollision(ball);
            pause(PAUSE_TIME);
        }
        if (ball.getY() > (paddle.getY() + paddle.getHeight()))
            remove(ball);
    }

    /**
     * Move ball by set velocities and bounce if collide right or left wall and ceiling
     *
     * @param ball the object that will move
     */
    private void moveBall(GOval ball) {
        ball.move(vx, vy);
        if (ball.getX() + BALL_DIAMETER >= getWidth()) vx = -vx;  /* bounce right wall */
        if (ball.getX() <= 0) vx = -vx;                    /* bounce left wall */
        if (ball.getY() <= 0) vy = -vy;                    /* bounce ceiling */

    }

    /**
     * If the ball collides the paddle or bricks it bounce but in the second case brick is removed
     *
     * @param ball to check if the object ball touches the edges of the paddle
     */
    private void checkCollision(GOval ball) {
        /* object the ball collides with */
        GObject collider = getCollidingObject(ball);
        if (collider != null) {
            if (collider == paddle) {

                vy = -vy;

                /* if the ball hits the left edge of the paddle change the direction to opposite */
                if (ball.getX()  <= collider.getX()) {
                    if (vx > 0) vx = -vx;
                }
                /* if the ball hits the right edge of the paddle change the direction to opposite */
                else if (ball.getX() + BALL_DIAMETER >= collider.getX() + PADDLE_WIDTH) {
                    if (vx < 0) vx = -vx;
                }
                /* if ball is stuck in paddle then push ball up*/
                else if ((ball.getY() + BALL_DIAMETER) > (getHeight() - PADDLE_Y_OFFSET)) {
                    /* distance that we lift the ball that locates under or inside the paddle */
                    double moveUp = (ball.getY() + BALL_DIAMETER) - (getHeight() - PADDLE_Y_OFFSET);
                    ball.move(0, -moveUp);
                    vx = -vx;
                }

            }
            /* collider == brick */
            else {
                vy = -vy;
                remove(collider);
                brickCounter--;
            }
        }
    }

    /**
     * @param ball to check with which object the ball collides
     * @return object that the ball collides (paddle or brick)
     */
    private GObject getCollidingObject(GOval ball) {
        /* point (x,y)  upper right */
        if (getElementAt(ball.getX(), ball.getY()) != null)
            return getElementAt(ball.getX(), ball.getY());
            /* point (x + 2r, y)  upper left */
        else if (getElementAt(ball.getX() + BALL_DIAMETER, ball.getY()) != null)
            return getElementAt(ball.getX() + BALL_DIAMETER, ball.getY());
            /* point (x, y + 2r)  bottom left */
        else if (getElementAt(ball.getX(), ball.getY() + BALL_DIAMETER) != null)
            return getElementAt(ball.getX(), ball.getY() + BALL_DIAMETER);
            /* point (x + 2r, y + 2r)  bottom right */
        else if (getElementAt(ball.getX() + BALL_DIAMETER, ball.getY() + BALL_DIAMETER) != null)
            return getElementAt(ball.getX() + BALL_DIAMETER, ball.getY() + BALL_DIAMETER);
        else return null;
    }


    /**
     * Print the result of the game ( if the user won or lost )
     */
    private void checkTheResult() {

        GLabel resultLabel = new GLabel("");
        if (brickCounter == 0) {
            resultLabel.setLabel("You won!");
        } else {
            resultLabel.setLabel("You lost!");
        }

        resultLabel.setFont("London-40");
        resultLabel.setColor(Color.BLACK);
        resultLabel.setLocation((WIDTH - resultLabel.getWidth()) / 2.0,
                (HEIGHT - resultLabel.getHeight()) / 2.0);
        add(resultLabel);
    }

}