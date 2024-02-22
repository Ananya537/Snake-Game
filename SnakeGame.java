import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

//Panel for score and level options
class ScorePanel extends JPanel {
    static JLabel scoreLabel;
    static JLabel bestscoreLabel;

    public ScorePanel() {
        JLabel gameNameLabel = new JLabel("Snake Game");
        gameNameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gameNameLabel.setHorizontalAlignment(JLabel.CENTER);
        add(new JLabel());
        add(gameNameLabel);
        add(new JLabel());

        scoreLabel = new JLabel("Score: 0");
        bestscoreLabel = new JLabel("Best Score: 0");
        JLabel levelLabel = new JLabel("Medium Level");
        levelLabel.setHorizontalAlignment(JLabel.CENTER);
        levelLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        JButton easyButton = new JButton("Easy");
        JButton mediumButton = new JButton("Medium");
        JButton difficultButton = new JButton("Difficult");
        JButton resetButton = new JButton("Reset");
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        bestscoreLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SnakeGame.resetGame();
            }
        });

        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SnakeGame.setDifficulty("Easy");
                levelLabel.setText("Easy Level");
            }
        });

        mediumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SnakeGame.setDifficulty("Medium");
                levelLabel.setText("Medium Level");
            }
        });

        difficultButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SnakeGame.setDifficulty("Difficult");
                levelLabel.setText("Difficult Level");
            }
        });

        add(scoreLabel);
        add(levelLabel);
        add(bestscoreLabel);
        add(easyButton);
        add(mediumButton);
        add(difficultButton);
        add(new JLabel()); 
        add(resetButton);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
}



public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private static SnakeGame instance;

    private static class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static int boardWidth;
    static int boardHeight;
    static int tileSize = 25;
    static int score;
    static int bestScore = 0;
    
    //snake
    static Tile snakeHead;
    static ArrayList<Tile> snakeBody;

    //food
    static Tile food;
    static Random random;

    //game logic
    int velocityX;
    int velocityY;
    Timer gameLoop;

    boolean gameOver = false;
    
    private static String difficulty = "Medium";

    public static void setDifficulty(String level) {
        difficulty = level;
        if (instance.gameLoop != null) {
            instance.gameLoop.stop();
            instance.gameLoop = new Timer(getTimerInterval(), instance);
            instance.gameLoop.start();
        }
        bestScore = 0;
        resetGame();
    }

    private static int getTimerInterval() {
        switch (difficulty) {
            case "Easy":
                return 150;
            case "Medium":
                return 100;
            case "Difficult":
                return 50;
            default:
                return 100;
        }
    }

    private SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.DARK_GRAY);
        addKeyListener(this);
        setFocusable(true);
        requestFocus();

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 1;
        velocityY = 0;

        //game timer
        gameLoop = new Timer(100, this);  
        gameLoop.start();
    }

    public static SnakeGame getInstance(int boardWidth, int boardHeight) {
        if (instance == null) {
            instance = new SnakeGame(boardWidth, boardHeight);
        }
        return instance;
    }

    public static void resetGame() {
    	ScorePanel.scoreLabel.setForeground(Color.BLACK);
        instance.gameOver = false;
        instance.snakeBody.clear();
        instance.score = 0;
        placeFood();
        instance.snakeHead = new Tile(5, 5);
        instance.velocityX = 1;
        instance.velocityY = 0;
        instance.gameLoop.start();

        instance.requestFocus(); 
    }


    
    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g) {

        //Food
        g.setColor(Color.red);
        g.fillOval(food.x*tileSize, food.y*tileSize, tileSize, tileSize);

        //Snake Head
        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x*tileSize, snakeHead.y*tileSize, tileSize, tileSize, true);
        
        //Snake Body
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);
            g.fill3DRect(snakePart.x*tileSize, snakePart.y*tileSize, tileSize, tileSize, true);
		}
        
        //Score
        score = Integer.parseInt(String.valueOf(snakeBody.size()));
        ScorePanel.scoreLabel.setText("Score: " + String.valueOf(snakeBody.size()));
        if (gameOver) {
        	ScorePanel.scoreLabel.setText("Game Over: " + score);
        	ScorePanel.scoreLabel.setForeground(Color.RED);
        }
        else {
        	ScorePanel.scoreLabel.setText("Score: " + score);
        }
        if (score > bestScore) {
        	bestScore = score;
        }
        ScorePanel.bestscoreLabel.setText("Best Score: " + bestScore);
	}

    public static void placeFood(){
        food.x = random.nextInt(boardWidth/tileSize);
		food.y = random.nextInt(boardHeight/tileSize);
	}

    public void move() {
        //eat food
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        //move snake body
        for (int i = snakeBody.size()-1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) { 
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            }
            else {
                Tile prevSnakePart = snakeBody.get(i-1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }
        //move snake head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        //game over conditions
        for (int i = 0; i < snakeBody.size(); i++) {
            Tile snakePart = snakeBody.get(i);

            //collide with snake head
            if (collision(snakeHead, snakePart)) {
                gameOver = true;
            }
        }

        if (snakeHead.x*tileSize < 0 || snakeHead.x*tileSize > boardWidth || 
            snakeHead.y*tileSize < 0 || snakeHead.y*tileSize > boardHeight ) {  
            gameOver = true;
        }
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) { 
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }  

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
