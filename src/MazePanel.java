import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MazePanel extends JPanel implements KeyListener, ActionListener {

	static final int cellSize = 12;
	static final int topDistanceY = 50;
	static final int textX = 10;
	static final int textY = 10;
	static final int fontSize = 15;
	static final int textSpace = 0;
	static final int generalDelay = 5;
	static final int flashDelay = 80;
	static final int maxFlash = 5;
	static final int wallWidth = cellSize; // rom ar udrides, eg varianti ar mushaobs, gadasaketebeli iqneba
	static final int theseusSize = (int) cellSize / 4 * 3;
	static final int shortestPath = MazeBack.getShortestPath();
	static final Color backgroundColor = Color.white;
	static final Color wallColor = Color.gray;
	static final Color theseusColor = Color.black;
	static final Color theseusHitColor = Color.red;
	static final Color systemColor = Color.blue;
	static final Color hintColor = Color.red;	
	static final Font font = new Font("Calibri", Font.BOLD, fontSize);

	static int panelXSize, panelYSize;

	Graphics2D g2D;
	Timer timer;
	int i, j;
	int steps, hits;
	boolean hit = false;
	int blinks = 0;
	int hint; //0 - not used, 1 - hint appears, 2 - hint disappears, 3 - hint used

	public MazePanel() {
		panelXSize = (MazeBack.getMazeSize() + 2) * cellSize;
		panelYSize = (MazeBack.getMazeSize() + 2) * cellSize + topDistanceY;

		timer = new Timer(generalDelay, this);
		timer.start();
		this.setPreferredSize(new Dimension(panelXSize, panelYSize));
		addKeyListener(this);
		this.setFocusable(true);
		this.setFocusTraversalKeysEnabled(false);

		newGame();
	}

	private void newGame() {
		steps = 0;
		hits = 0;
		hint=0;
	}

	public void paint(Graphics g) {
		g2D = (Graphics2D) g;
		g2D.setColor(backgroundColor);
		g2D.fillRect(0, 0, panelXSize, panelYSize);
		g2D.setFont(font);
		g2D.setColor(systemColor);
		g2D.drawString("Restart - Space | One Hint - F1", textX, textY);
		g2D.drawString(
				"Steps - " + Integer.toString(steps) + " ("
						+ Integer.toString(MazeBack.getShortestPath() + Main.extraSteps) + ") | hits - "
						+ Integer.toString(hits) + " (" + Integer.toString(Main.maxHits) + ")",
				textX, textY + fontSize + textSpace);

		drawMaze();

		if (hit) {
			if (blinks % 2 == 1)
				drawTheseus(MazeBack.getiT(), MazeBack.getjT(), theseusColor);
			else
				drawTheseus(MazeBack.getiT(), MazeBack.getjT(), theseusHitColor);
			blinks++;
		} else
			drawTheseus(MazeBack.getiT(), MazeBack.getjT(), theseusColor);
		if (blinks == maxFlash) {
			hit = false;
			blinks = 0;
			timer.setDelay(generalDelay);
		}
		
		if ((hint < maxFlash) & (hint > 0)) {
			if (hint % 2 == 1) 
				drawHint(hintColor);
			else 
				timer.setDelay(generalDelay);
			hint++;
		}
	}

	private void drawMaze() {
		g2D.setColor(wallColor);
		int size = MazeBack.getMazeSize();
		for (i = 0; i < size; i++)
			for (j = 0; j < size; j++) {
				if (MazeBack.getMatrixElement(i, j) == -1) {
					// outer walls
					if (i == size - 1)
						g2D.fillRect((i + 1) * cellSize - wallWidth, j * cellSize + topDistanceY, wallWidth, cellSize);
					if (j == size - 1)
						g2D.fillRect(i * cellSize, (j + 1) * cellSize + topDistanceY - wallWidth, cellSize, wallWidth);
					if (j == 0)
						g2D.fillRect(i * cellSize, j * cellSize + topDistanceY, cellSize, wallWidth);
					if (i == 0)
						g2D.fillRect(i * cellSize, j * cellSize + topDistanceY, wallWidth, cellSize);

					// inner edges
					if (!((i == size - 1) || (j == size - 1) || (j == 0) || (i == 0))) {
						if ((MazeBack.getMatrixElement(i - 1, j) != -1) & (MazeBack.getMatrixElement(i + 1, j) != -1))
							g2D.fillRect(i * cellSize, j * cellSize + topDistanceY, wallWidth, cellSize);
						else
							g2D.fillRect(i * cellSize, j * cellSize + topDistanceY, cellSize, wallWidth);

					}
				}
			}
	}

	private void drawTheseus(int i, int j, Color c) {
		g2D.setColor(c);
		g2D.fillOval((int) ((i + 0.5) * cellSize - theseusSize / 2),
				(int) ((j + 0.5) * cellSize - theseusSize / 2) + topDistanceY, theseusSize, theseusSize);
	}

	private void drawHint(Color c) {
		g2D.setColor(c);
		int size = MazeBack.getMazeSize();
		for (i = 0; i < size; i++)
			for (j = 0; j < size; j++) {
				if (MazeBack.getMatrixElement(i, j) == 1) {
					// exit
					if (i == size - 1)
						g2D.fillRect((i + 1) * cellSize - wallWidth, j * cellSize + topDistanceY, cellSize, wallWidth);
					if (j == size - 1)
						g2D.fillRect(i * cellSize, (j + 1) * cellSize + topDistanceY - wallWidth, wallWidth, cellSize);
					if (j == 0)
						g2D.fillRect(i * cellSize, j * cellSize + topDistanceY, wallWidth, cellSize);
					if (i == 0)
						g2D.fillRect(i * cellSize, j * cellSize + topDistanceY, cellSize, wallWidth);

					// inner route
					if (!((i == size - 1) || (j == size - 1) || (j == 0) || (i == 0))) {
						if ((MazeBack.getMatrixElement(i - 1, j) != -1) & (MazeBack.getMatrixElement(i + 1, j) != -1))
							g2D.fillRect(i * cellSize, j * cellSize + topDistanceY, wallWidth, cellSize);
						else
							g2D.fillRect(i * cellSize, j * cellSize + topDistanceY, cellSize, wallWidth);

					}
				}
			}
	}

	private void checkStatus() {
		if ((MazeBack.getiT() == 0) || (MazeBack.getiT() == MazeBack.getMazeSize() - 1) || (MazeBack.getjT() == 0)
				|| (MazeBack.getjT() == MazeBack.getMazeSize() - 1)) {
			gameOverRestart("YOU WON!! Press OK to restart", true);
		} else if (steps == MazeBack.getShortestPath() + Main.extraSteps) {
			gameOverRestart("YOU LOST!! Max steps reached. Restart game?", true);
		} else if (hits == Main.maxHits) {
			gameOverRestart("YOU LOST!! Max hits reached. Restart game?", true);
		}
	}

	private void gameOverRestart(String msg, boolean restartOption) {
		if (restartOption) {
			int result = JOptionPane.showConfirmDialog(this, msg, "", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				Main.maze.setupMaze();
				newGame();
			}
		} else {
			JOptionPane.showMessageDialog(this, msg);
			Main.maze.setupMaze();
			newGame();
		}

	}

	@Override
	public void keyPressed(KeyEvent e) {
		boolean move = false;
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			gameOverRestart("Restart game?", true);
			return;
		}
		if ((MazeBack.getiT() == 0) || (MazeBack.getiT() == MazeBack.getMazeSize() - 1) || (MazeBack.getjT() == 0)
				|| (MazeBack.getjT() == MazeBack.getMazeSize() - 1))
			return;
		int i = MazeBack.getiT(), j = MazeBack.getjT();
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			i++;
			move = true;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			i--;
			move = true;
		} else if (e.getKeyCode() == KeyEvent.VK_UP) {
			j--;
			move = true;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			j++;
			move = true;
		} else if ((e.getKeyCode() == KeyEvent.VK_F1) & (hint == 0)) {
			hint=1;
			timer.setDelay(flashDelay);
		}

		if (MazeBack.getMatrixElement(i, j) == -1) {
			hits++;
			hit = true;
			timer.setDelay(flashDelay);
		} else if (move) {
			steps++;
			MazeBack.setiT(i);
			MazeBack.setjT(j);
		}
		checkStatus();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
