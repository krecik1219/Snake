package pl.ppsolutions.snake;


import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class CSnake {
    public CSnake(int x, int y) {
        body = new LinkedList<Rectangle2D>();
        reset(x,y);
    }

    public void increaseLength() {
        Rectangle2D last = body.get(0);
        body.add(0, new Rectangle2D.Double(last.getX(), last.getY(), HEAD_WIDTH, HEAD_HEIGHT));
    }

    public void move() {
        Direction moveDirection = currentDir;
        Iterator<Rectangle2D> it = body.iterator();
        Rectangle2D current = it.next();
        Rectangle2D next;
        while(it.hasNext()) {
            next = it.next();
            current.setRect(next.getX(), next.getY(), HEAD_WIDTH, HEAD_HEIGHT);
            current = next;
        }
        current.setRect(current.getX() + moveDirection.x, current.getY() + moveDirection.y, HEAD_WIDTH, HEAD_HEIGHT);
    }

    public void changeDirection(Direction direction) {
        if(currentDir != direction.getOpposite())
            currentDir = direction;
    }

    public List<Rectangle2D> getBody() {
        return body;
    }

    public void reset(int x, int y) {
        body.clear();
        body.add(new Rectangle2D.Double(x, y, HEAD_WIDTH, HEAD_HEIGHT));
        currentDir = Direction.RIGHT;
    }

    static enum Direction {

        UP(0, -HEAD_HEIGHT),
        DOWN(0, HEAD_HEIGHT),
        LEFT(-HEAD_WIDTH, 0),
        RIGHT(HEAD_WIDTH, 0);

        private Direction(int x, int y){
            this.x = x;
            this.y = y;
        }

        private Direction getOpposite() {
            Direction opposite = null;

            switch(this) {
                case UP:
                    opposite = DOWN;
                    break;
                case DOWN:
                    opposite = UP;
                    break;
                case LEFT:
                    opposite = RIGHT;
                    break;
                case RIGHT:
                    opposite = LEFT;
                    break;
            }
            return opposite;
        }

        private int x;
        private int y;
    }

    private Direction currentDir;
    private List<Rectangle2D> body;
    static final int VELOCITY = 30;
    static final int HEAD_WIDTH = 10;
    static final int HEAD_HEIGHT = HEAD_WIDTH;
    static final Color SNAKE_COLOR = Color.BLUE;
}
