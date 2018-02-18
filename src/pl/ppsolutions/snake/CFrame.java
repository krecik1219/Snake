package pl.ppsolutions.snake;


import javax.swing.JFrame;

public class CFrame extends JFrame {
    public CFrame() {
        super("Snake by PP Solutions");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        board = new CBoard(this);
        add(board);
        pack();
        setLocationRelativeTo(null);
    }

    private CBoard board;

}
