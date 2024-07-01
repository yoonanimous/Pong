package Pong;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Font;

public class PongPanel extends JPanel implements ActionListener, KeyListener {
	
	private final static Color BACKGROUND_COLOUR = Color.black;
	private final static int TIMER_DELAY = 5;
	private final static int BALL_MOVEMENT_SPEED = 2;
	private final static int POINTS_TO_WIN = 3;
	int player1Score = 0, player2Score = 0;
	Player gameWinner;
	private final static int SCORE_TEXT_X = 100;
	private final static int SCORE_TEXT_Y = 100;
	private final static int SCORE_FONT_SIZE = 50;
	private final static String SCORE_FONT_FAMILY = "Serif";
	private final static int WINNER_TEXT_X = 200;
	private final static int WINNER_TEXT_Y = 200;
	private final static int WINNER_FONT_SIZE = 40;
	private final static String WINNER_FONT_FAMILY = "Serif";
	private final static String WINNER_TEXT = "WIN!";

	GameState gameState = GameState.INITIALISING;
	
	Ball ball;
	Paddle paddle1, paddle2;
	
	public PongPanel() {
		setBackground(BACKGROUND_COLOUR);
		Timer timer = new Timer(TIMER_DELAY, this);
		timer.start();
		addKeyListener(this);
		setFocusable(true);
	}

	public void createObjects() {
		ball = new Ball(getWidth(), getHeight());
		paddle1 = new Paddle(Player.One, getWidth(), getHeight());
		paddle2 = new Paddle(Player.Two, getWidth(), getHeight());
	}
	
	public void update() {
		switch(gameState) {
			case INITIALISING: {
				createObjects();
				gameState = GameState.PLAYING;
				ball.setxVelocity(BALL_MOVEMENT_SPEED);
				ball.setyVelocity(BALL_MOVEMENT_SPEED);
				break;
			}
			case PLAYING: {
				moveObject(paddle1);
				moveObject(paddle2);
				moveObject(ball);
				checkWallBounce();
				checkPaddleBounce();
				checkWin();
				break;
			}
			case GAMEOVER: {
				break;
			}
		}
	}
	
	private void paintDottedLine(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
		g2d.setStroke(dashed);
		g2d.setPaint(Color.WHITE);
		g2d.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
		g2d.dispose();
	}
	 
	private void paintSprite(Graphics g, Sprite sprite) {
		g.setColor(sprite.getColour());
		g.fillRect(sprite.getxPosition(), sprite.getyPosition(), sprite.getWidth(), sprite.getHeight());
	}

	@Override
	public void keyPressed(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.VK_W) {
            paddle1.setyVelocity(-1);
        } else if(event.getKeyCode() == KeyEvent.VK_S) {
            paddle1.setyVelocity(1);
        }
		if(event.getKeyCode() == KeyEvent.VK_UP) {
			paddle2.setyVelocity(-1);
		} else if(event.getKeyCode() == KeyEvent.VK_DOWN) {
			paddle2.setyVelocity(1);
		}
	}

	@Override
	public void keyReleased(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.VK_W || event.getKeyCode() == KeyEvent.VK_S) {
            paddle1.setyVelocity(0);
        }
		if(event.getKeyCode() == KeyEvent.VK_UP || event.getKeyCode() == KeyEvent.VK_DOWN) {
			paddle2.setyVelocity(0);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		update();
		repaint();
		
	}
	
	private void paintScores(Graphics g) { 
		Font scoreFont = new Font(SCORE_FONT_FAMILY, Font.BOLD, SCORE_FONT_SIZE);
		String leftScore = Integer.toString(player1Score);
		String rightScore = Integer.toString(player2Score);
		g.setFont(scoreFont);
		g.drawString(leftScore, SCORE_TEXT_X, SCORE_TEXT_Y);
		g.drawString(rightScore, getWidth()-SCORE_TEXT_X, SCORE_TEXT_Y);
	}
	
	private void paintWinner(Graphics g) {
		if(gameWinner != null) {
			Font winnerFont = new Font(WINNER_FONT_FAMILY, Font.BOLD, WINNER_FONT_SIZE);
			g.setFont(winnerFont);
			int xPosition = getWidth() / 2;
		if(gameWinner == Player.One) {
			xPosition -= WINNER_TEXT_X;
		} else if(gameWinner == Player.Two) {
			xPosition += WINNER_TEXT_X;
		}
		g.drawString(WINNER_TEXT, xPosition, WINNER_TEXT_Y);
		}
	}   
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintDottedLine(g);
		if(gameState != GameState.INITIALISING) {
			paintSprite(g, ball);
			paintSprite(g, paddle1);
			paintSprite(g, paddle2);
			paintScores(g);
			paintWinner(g);
		}
	}
	
	private void moveObject(Sprite obj) {
		obj.setXPosition(obj.getxPosition() + obj.getxVelocity(),getWidth());
		obj.setYPosition(obj.getyPosition() + obj.getyVelocity(),getHeight());
	}
	
	private void checkWin() {
		if(player1Score >= POINTS_TO_WIN) {
			gameWinner = Player.One;
			gameState = GameState.GAMEOVER;
		} else if(player2Score >= POINTS_TO_WIN) {
			gameWinner = Player.Two;
			gameState = GameState.GAMEOVER;
		}
	}
	     
	private void addScore(Player player) {
		if(player == Player.One) {
			player1Score++;
		} else if(player == Player.Two) {
			player2Score++;
		}
	}
     
	private void checkWallBounce() {
		if(ball.getxPosition() <= 0) {
		// Hit left side of screen
		ball.setxVelocity(-ball.getxVelocity());
		addScore(Player.Two);
		resetBall();
		} else if(ball.getxPosition() >= getWidth() - ball.getWidth()) {
		// Hit right side of screen
		ball.setxVelocity(-ball.getxVelocity());
		addScore(Player.One);
		resetBall();
		}
		if(ball.getyPosition() <= 0 || ball.getyPosition() >= getHeight() - ball.getHeight()) {
		// Hit top or bottom of screen
		ball.setyVelocity(-ball.getyVelocity());
		}
	}
	
	private void resetBall() {
		ball.resetToInitialPosition();
	}
	
	private void checkPaddleBounce() {
		if(ball.getxVelocity() < 0 && ball.getRectangle().intersects(paddle1.getRectangle())) {
			ball.setxVelocity(BALL_MOVEMENT_SPEED);
		}
		if(ball.getxVelocity() > 0 && ball.getRectangle().intersects(paddle2.getRectangle())) {
			ball.setxVelocity(-BALL_MOVEMENT_SPEED);
		}
	}
}