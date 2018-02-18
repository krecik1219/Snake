package pl.ppsolutions.snake;


import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class CBoard extends JPanel implements KeyListener {
    public CBoard(CFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.started = false;
        setBackground(BOARD_COLOR);
        playButton = new JButton("Play!");
        setLayout(new GridBagLayout());
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
                playButton.setVisible(false);
                playButton = null;
            }
        });
        playButton.setPreferredSize(START_BUTTON_SIZE);
        add(playButton, new GridBagConstraints());
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        addKeyListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D)g;
        if(started) {
            g2d.setColor(FOOD_COLOR);
            g2d.fill(FOOD);
            g2d.draw(FOOD);

            List<Rectangle2D> snakeBody = SNAKE.getBody();
            Iterator<Rectangle2D> it = snakeBody.iterator();
            while(it.hasNext()) {
                Rectangle2D block = it.next();
                g2d.setColor(CSnake.SNAKE_COLOR);
                g2d.fill(block);
                g2d.draw(block);
            }
        }
    }

    private void gameLoop() {
        movePossible = false;
        wallPassing();
        SNAKE.move();
        if(checkSelfEaten())
            gameOver = true;
        if(checkEaten()) {
            SNAKE.increaseLength();
            generateFood();
        }

        repaint();
        movePossible = true;
    }

    private void generateFood() {
        int x = GENERATOR.nextInt(WIDTH - FOOD_WIDTH + 1);
        int y = GENERATOR.nextInt(HEIGHT - FOOD_HEIGHT + 1);

        FOOD.setRect(x, y, FOOD_WIDTH, FOOD_HEIGHT);
    }

    public void startGame() {
        setFocusable(true);
        requestFocus();
        started = true;
        generateFood();
        repaint();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(!gameOver) {
                        Thread.sleep(100 - CSnake.VELOCITY);
                        gameLoop();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                gameFinished();
            }
        }).start();
    }

    private void gameFinished() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame finFrame = new JFrame("Game over!");
                finFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                finFrame.setResizable(false);
                finFrame.setVisible(true);
                finFrame.setSize(FIN_FRAME_DIM);
                JLabel endText = new JLabel("Game over. Score: " + score);
                JLabel askText = new JLabel("Try again?");
                JButton yesBut = new JButton("Yes");
                JButton noBut = new JButton("No");
                finFrame.setLayout(new FlowLayout());
                finFrame.add(endText);
                finFrame.add(askText);
                finFrame.add(yesBut);
                finFrame.add(noBut);
                finFrame.setLocationRelativeTo(parentFrame);

                yesBut.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        restartGame();
                        finFrame.dispose();
                    }
                });

                noBut.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) { ;
                        finFrame.dispose();
                        parentFrame.dispose();
                    }
                });
            }
        });
    }

    private void restartGame() {
        SNAKE.reset(WIDTH/2,HEIGHT/2);
        gameOver = false;
        score = 0;
        movePossible=true;
        startGame();
    }

    private boolean checkEaten() {
        boolean eaten = false;
        Rectangle2D head = SNAKE.getBody().get(SNAKE.getBody().size() - 1);
        if((Math.abs(head.getX() - FOOD.getX()) <= 7) && (Math.abs(head.getY() - FOOD.getY()) <= 7)) {
            eaten = true;
            ++score;
        }

        return eaten;
    }

    private boolean checkSelfEaten() {
        boolean selfEaten = false;
        Rectangle2D head = SNAKE.getBody().get(SNAKE.getBody().size() - 1);

        List<Rectangle2D> snakeBody = SNAKE.getBody();
        Iterator<Rectangle2D> it = snakeBody.iterator();
        Rectangle2D block;
        while(it.hasNext() && !selfEaten) {
            block = it.next();
            if((block != head) && (Math.abs(head.getX() - block.getX()) < 5) && (Math.abs(head.getY() - block.getY()) < 5))
                selfEaten = true;
        }

        return selfEaten;
    }

    private void wallPassing() {
        Rectangle2D head = SNAKE.getBody().get(SNAKE.getBody().size() - 1);
        if(head.getX() >= WIDTH ) {
            head.setRect(CSnake.HEAD_WIDTH, head.getY(), CSnake.HEAD_WIDTH, CSnake.HEAD_HEIGHT);
        }
        else if(head.getX() <= 0) {
            head.setRect(WIDTH - CSnake.HEAD_WIDTH, head.getY(), CSnake.HEAD_WIDTH, CSnake.HEAD_HEIGHT);
        }
        else if(head.getY() >= HEIGHT) {
            head.setRect(head.getX(), CSnake.HEAD_HEIGHT, CSnake.HEAD_WIDTH, CSnake.HEAD_HEIGHT);
        }
        else if(head.getY() <= 0) {
            head.setRect(head.getX(), HEIGHT - CSnake.HEAD_HEIGHT, CSnake.HEAD_WIDTH, CSnake.HEAD_HEIGHT);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        //nothing
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(movePossible) {
            CSnake.Direction dir = null;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP:
                    dir = CSnake.Direction.UP;
                    break;
                case KeyEvent.VK_DOWN:
                    dir = CSnake.Direction.DOWN;
                    break;
                case KeyEvent.VK_LEFT:
                    dir = CSnake.Direction.LEFT;
                    break;
                case KeyEvent.VK_RIGHT:
                    dir = CSnake.Direction.RIGHT;
                    break;
            }
            movePossible = false;
            if (dir != null)
                SNAKE.changeDirection(dir);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        //nothing
    }

    private boolean movePossible = true;
    private int score = 0;
    private boolean gameOver = false;
    private boolean started;
    private JButton playButton;
    private final CFrame parentFrame;
    private static final Color BOARD_COLOR = Color.YELLOW;
    private static final Color FOOD_COLOR = Color.RED;
    private static final Dimension START_BUTTON_SIZE = new Dimension(150, 50);
    private static final int WIDTH = 400;
    private static final int HEIGHT = WIDTH;
    private static final int FOOD_WIDTH = 10;
    private static final int FOOD_HEIGHT = FOOD_WIDTH;
    private static final Rectangle2D FOOD = new Rectangle2D.Double(0, 0, FOOD_WIDTH, FOOD_HEIGHT);
    private static final Random GENERATOR = new Random();
    private static final CSnake SNAKE = new CSnake(WIDTH/2, HEIGHT/2);
    private static final Dimension FIN_FRAME_DIM = new Dimension(250, 110);
}
