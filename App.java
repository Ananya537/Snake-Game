import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 600;
        int boardHeight = boardWidth;

        JFrame frame = new JFrame("Snake");
        frame.setLayout(new BorderLayout());

        ScorePanel scorePanel = new ScorePanel();
        scorePanel.setLayout(new GridLayout(4, 3, 10, 5));
        scorePanel.setBackground(Color.ORANGE);
        frame.add(scorePanel, BorderLayout.NORTH);
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(frame);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SnakeGame snakeGame = SnakeGame.getInstance(boardWidth, boardHeight);
        frame.add(snakeGame);
        frame.pack();
        snakeGame.requestFocus();
        frame.addKeyListener(snakeGame);
    }
}
