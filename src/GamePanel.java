import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

    static final int GAME_WIDTH = 1000;
    static final int GAME_HEIGHT = (int)(GAME_WIDTH * 0.5555);
    static final Dimension WINDOW_SIZE = new Dimension(GAME_WIDTH, GAME_HEIGHT);
    static final int BALL_SIZE = 20;
    static final int PADDLE_WIDTH = 25;
    static final int PADDLE_HEIGHT = 100;

    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddleLeft;
    Paddle paddleRight;
    Ball ball;
    Score score;

    GamePanel() {
        newPaddles();
        newBall();
        score = new Score(GAME_WIDTH, GAME_HEIGHT);
        this.setFocusable(true);
        this.addKeyListener(new AL());
        this.setPreferredSize(WINDOW_SIZE);

        gameThread = new Thread(this);
        gameThread.start();

    }

    public void newBall() {
        random = new Random();
        ball = new Ball((GAME_WIDTH / 2 - BALL_SIZE / 2), random.nextInt(GAME_HEIGHT - BALL_SIZE),
                BALL_SIZE, BALL_SIZE);
    }
    public void newPaddles() {
        paddleLeft = new Paddle(
                0, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 1);
        paddleRight = new Paddle(
                GAME_WIDTH - PADDLE_WIDTH, (GAME_HEIGHT / 2) - (PADDLE_HEIGHT / 2), PADDLE_WIDTH, PADDLE_HEIGHT, 2);
    }
    public void paint(Graphics g) {
        image = createImage(getWidth(), getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image, 0, 0, this);
    }
    public void draw(Graphics g) {
        paddleLeft.draw(g);
        paddleRight.draw(g);
        ball.draw(g);
        score.draw(g);
    }
    public void move() {
        paddleLeft.move();
        paddleRight.move();
        ball.move();
    }
    public void checkCollision() {
        System.out.println("ball.x = " + ball.x);
        // bounce ball from paddles
        if(ball.intersects(paddleLeft)){
            System.out.println("Left bounce");
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++; // optional for more difficulty
            if(ball.yVelocity > 0) ball.yVelocity++;    // optional for more difficulty
            else ball.yVelocity--;
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }
        if(ball.intersects(paddleRight)){
            System.out.println("Right bounce");
            System.exit(0);
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++; // optional for more difficulty
            if(ball.yVelocity > 0)
                ball.yVelocity++;    // optional for more difficulty
            else
                ball.yVelocity--;
            ball.setXDirection(-ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }

        // bounce ball off window edges
        if (ball.y <= 0) ball.setYDirection(-ball.yVelocity);
        if (ball.y >= GAME_HEIGHT - BALL_SIZE) ball.setYDirection(-ball.yVelocity);


        // stops paddles at window edges
        if (paddleLeft.y <= 0) paddleLeft.y = 0;
        if(paddleLeft.y >= GAME_HEIGHT - paddleLeft.height) paddleLeft.y = GAME_HEIGHT - paddleLeft.height;
        if (paddleRight.y <= 0) paddleRight.y = 0;
        if(paddleRight.y >= GAME_HEIGHT - paddleRight.height) paddleRight.y = GAME_HEIGHT - paddleRight.height;

        // ball out
        // give a player 1 point and create new ball and paddles
        if(ball.x <= 0) {
            score.player2Score++;
            newPaddles();
//            newBall();
            System.out.println("Player Two Score is " + score.player2Score);
            System.out.println("ball.x = " + ball.x);
        }
        if(ball.x >= GAME_WIDTH - BALL_SIZE) {
            score.player1Score++;
            newPaddles();
//            newBall();
            System.out.println("Player One Score is " + score.player1Score);
        }
    }
    public void run() {
        // game loop
        long lastTime = System.nanoTime();
        double amountOfTicks = 60;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while(true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                move();
                checkCollision();
                repaint();
                delta--;
            }
        }
    }

    public class AL extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            paddleLeft.keyPressed(e);
            paddleRight.keyPressed(e);
        }
        public void keyReleased(KeyEvent e) {
            paddleLeft.keyReleased(e);
            paddleRight.keyReleased(e);
        }
    }

}
