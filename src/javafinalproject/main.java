package javafinalproject;

public class main {
	import javafx.application.Application;
	import javafx.scene.Scene;
	import javafx.scene.control.Button;
	import javafx.scene.layout.VBox;
	import javafx.stage.Stage;

	public class Main extends Application {

	    @Override
	    public void start(Stage primaryStage) {
	        primaryStage.setTitle("JavaFX Application");

	        // Create UI elements (like a button)
	        Button btn = new Button("Click Me");
	        
	        // Put elements into a layout pane
	        VBox root = new VBox(10);
	        root.getChildren().add(btn);

	        // Set up the scene and stage (window)
	        Scene scene = new Scene(root, 400, 300);
	        primaryStage.setScene(scene);
	        primaryStage.show();
	    }

	    public static void main(String[] args) {
	        // Launches the JavaFX runtime environment
	        launch(args);
	    }

		private static void launch(String[] args) {
			// TODO Auto-generated method stub
			
		}
	}
	import javax.swing.*;
	import java.awt.*;
	import java.awt.event.KeyAdapter;
	import java.awt.event.KeyEvent;
	import java.util.ArrayList;
	import java.util.List;

	public class MazeBlockGame extends JPanel {

	    // Tile Constants
	    private static final int EMPTY = 0;
	    private static final int WALL = 1;
	    private static final int TARGET = 2;
	    private static final int EXIT = 3;

	    private static final int TILE_SIZE = 48;

	    // Level Designs
	    // # = Wall, . = Target Pad, @ = Player Start, $ = Movable Block, * = Block starting on Target, E = Level Exit
	    private final String[][] LEVEL_DATA = {
	        { // Level 1: The Basics
	            "##########",
	            "#@   #   #",
	            "# $$ # . #",
	            "#    # . #",
	            "####   ###",
	            "#      E #",
	            "##########"
	        },
	        { // Level 2: Narrow Corridors
	            "############",
	            "#@  #      #",
	            "# $ # $  . #",
	            "#   ## ### #",
	            "###    .   #",
	            "#   #### $ #",
	            "# .      #E#",
	            "############"
	        },
	        { // Level 3: The Warehouse Maze
	            "##############",
	            "#   #    #   #",
	            "# @ $ .  $ . #",
	            "### #### # ###",
	            "#   #    $   #",
	            "# . # ###### #",
	            "#            #",
	            "########### E#",
	            "##############"
	        }
	    };

	    private int currentLevel = 0;
	    private int rows, cols;
	    private int[][] baseGrid;
	    private int playerRow, playerCol;
	    private List<Point> blocks;
	    private boolean levelComplete = false;
	    private boolean gameWon = false;

	    public MazeBlockGame() {
	        setBackground(new Color(25, 25, 35));
	        setFocusable(true);
	        loadLevel(currentLevel);

	        addKeyListener(new KeyAdapter() {
	            @Override
	            public void keyPressed(KeyEvent e) {
	                handleInput(e.getKeyCode());
	            }
	        });
	    }

	    private void loadLevel(int levelIndex) {
	        String[] layout = LEVEL_DATA[levelIndex];
	        rows = layout.length;
	        cols = layout[0].length();
	        baseGrid = new int[rows][cols];
	        blocks = new ArrayList<>();
	        levelComplete = false;

	        for (int r = 0; r < rows; r++) {
	            String rowStr = layout[r];
	            for (int c = 0; c < cols; c++) {
	                char ch = rowStr.charAt(c);
	                switch (ch) {
	                    case '#':
	                        baseGrid[r][c] = WALL;
	                        break;
	                    case '.':
	                        baseGrid[r][c] = TARGET;
	                        break;
	                    case '@':
	                        playerRow = r;
	                        playerCol = c;
	                        baseGrid[r][c] = EMPTY;
	                        break;
	                    case '$':
	                        blocks.add(new Point(c, r));
	                        baseGrid[r][c] = EMPTY;
	                        break;
	                    case '*':
	                        blocks.add(new Point(c, r));
	                        baseGrid[r][c] = TARGET;
	                        break;
	                    case 'E':
	                        baseGrid[r][c] = EXIT;
	                        break;
	                    default:
	                        baseGrid[r][c] = EMPTY;
	                        break;
	                }
	            }
	        }

	        setPreferredSize(new Dimension(cols * TILE_SIZE, rows * TILE_SIZE + 40));
	        revalidate();
	        repaint();
	    }

	    private void handleInput(int keyCode) {
	        if (gameWon) {
	            if (keyCode == KeyEvent.VK_R) {
	                currentLevel = 0;
	                gameWon = false;
	                loadLevel(currentLevel);
	            }
	            return;
	        }

	        if (keyCode == KeyEvent.VK_R) {
	            loadLevel(currentLevel);
	            return;
	        }

	        int dRow = 0, dCol = 0;
	        switch (keyCode) {
	            case KeyEvent.VK_UP:
	            case KeyEvent.VK_W:
	                dRow = -1;
	                break;
	            case KeyEvent.VK_DOWN:
	            case KeyEvent.VK_S:
	                dRow = 1;
	                break;
	            case KeyEvent.VK_LEFT:
	            case KeyEvent.VK_A:
	                dCol = -1;
	                break;
	            case KeyEvent.VK_RIGHT:
	            case KeyEvent.VK_D:
	                dCol = 1;
	                break;
	            default:
	                return;
	        }

	        attemptMove(dRow, dCol);
	        repaint();
	    }

	    private void attemptMove(int dRow, int dCol) {
	        int nextRow = playerRow + dRow;
	        int nextCol = playerCol + dCol;

	        // 1. Check wall collision
	        if (baseGrid[nextRow][nextCol] == WALL) {
	            return;
	        }

	        // 2. Check locked exit collision
	        if (baseGrid[nextRow][nextCol] == EXIT && !areAllTargetsCovered()) {
	            return; // Exit acts like a wall until all blocks are placed on targets
	        }

	        // 3. Check if pushing a block
	        Point block = getBlockAt(nextRow, nextCol);
	        if (block != null) {
	            int pushRow = nextRow + dRow;
	            int pushCol = nextCol + dCol;

	            // Blocks cannot be pushed into walls, other blocks, or the exit portal
	            if (baseGrid[pushRow][pushCol] == WALL || 
	                baseGrid[pushRow][pushCol] == EXIT || 
	                getBlockAt(pushRow, pushCol) != null) {
	                return;
	            }

	            // Move the block
	            block.setLocation(pushCol, pushRow);
	        }

	        // Move the player
	        playerRow = nextRow;
	        playerCol = nextCol;

	        // 4. Check if player stepped onto the unlocked exit
	        if (baseGrid[playerRow][playerCol] == EXIT && areAllTargetsCovered()) {
	            advanceLevel();
	        }
	    }

	    private Point getBlockAt(int row, int col) {
	        for (Point b : blocks) {
	            if (b.y == row && b.x == col) {
	                return b;
	            }
	        }
	        return null;
	    }

	    private boolean areAllTargetsCovered() {
	        for (int r = 0; r < rows; r++) {
	            for (int c = 0; c < cols; c++) {
	                if (baseGrid[r][c] == TARGET && getBlockAt(r, c) == null) {
	                    return false;
	                }
	            }
	        }
	        return true;
	    }

	    private void advanceLevel() {
	        currentLevel++;
	        if (currentLevel >= LEVEL_DATA.length) {
	            gameWon = true;
	        } else {
	            loadLevel(currentLevel);
	            Window window = SwingUtilities.getWindowAncestor(this);
	            if (window != null) {
	                window.pack();
	                window.setLocationRelativeTo(null);
	            }
	        }
	    }

	    @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        Graphics2D g2d = (Graphics2D) g;
	        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	        boolean exitUnlocked = areAllTargetsCovered();

	        // Draw Maze Grid
	        for (int r = 0; r < rows; r++) {
	            for (int c = 0; c < cols; c++) {
	                int x = c * TILE_SIZE;
	                int y = r * TILE_SIZE;

	                // Floor tile
	                g2d.setColor(new Color(40, 42, 54));
	                g2d.fillRect(x, y, TILE_SIZE, TILE_SIZE);
	                g2d.setColor(new Color(50, 52, 64));
	                g2d.drawRect(x, y, TILE_SIZE, TILE_SIZE);

	                if (baseGrid[r][c] == WALL) {
	                    g2d.setColor(new Color(98, 114, 164));
	                    g2d.fillRoundRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4, 8, 8);
	                } else if (baseGrid[r][c] == TARGET) {
	                    g2d.setColor(new Color(255, 85, 85, 120));
	                    g2d.fillOval(x + 12, y + 12, TILE_SIZE - 24, TILE_SIZE - 24);
	                    g2d.setColor(new Color(255, 85, 85));
	                    g2d.drawOval(x + 12, y + 12, TILE_SIZE - 24, TILE_SIZE - 24);
	                } else if (baseGrid[r][c] == EXIT) {
	                    if (exitUnlocked) {
	                        g2d.setColor(new Color(80, 250, 123)); // Glowing Green Unlocked Portal
	                    } else {
	                        g2d.setColor(new Color(100, 100, 110)); // Grey Locked Door
	                    }
	                    g2d.fillRoundRect(x + 6, y + 6, TILE_SIZE - 12, TILE_SIZE - 12, 12, 12);
	                }
	            }
	        }

	        // Draw Movable Blocks
	        for (Point b : blocks) {
	            int x = b.x * TILE_SIZE;
	            int y = b.y * TILE_SIZE;
	            boolean onTarget = (baseGrid[b.y][b.x] == TARGET);

	            if (onTarget) {
	                g2d.setColor(new Color(80, 250, 123)); // Turns green when placed on target
	            } else {
	                g2d.setColor(new Color(255, 184, 108)); // Default orange crate
	            }
	            g2d.fillRoundRect(x + 6, y + 6, TILE_SIZE - 12, TILE_SIZE - 12, 10, 10);

	            g2d.setColor(new Color(30, 30, 40));
	            g2d.drawRoundRect(x + 6, y + 6, TILE_SIZE - 12, TILE_SIZE - 12, 10, 10);
	        }

	        // Draw Player
	        int px = playerCol * TILE_SIZE;
	        int py = playerRow * TILE_SIZE;
	        g2d.setColor(new Color(139, 233, 253));
	        g2d.fillOval(px + 8, py + 8, TILE_SIZE - 16, TILE_SIZE - 16);

	        // Draw Status Bar
	        int barY = rows * TILE_SIZE;
	        g2d.setColor(new Color(30, 31, 41));
	        g2d.fillRect(0, barY, getWidth(), 40);

	        g2d.setColor(Color.WHITE);
	        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
	        if (gameWon) {
	            g2d.setColor(new Color(80, 250, 123));
	            g2d.drawString("YOU BEAT ALL LEVELS! Press 'R' to play again.", 15, barY + 25);
	        } else {
	            String status = "Level: " + (currentLevel + 1) + "/" + LEVEL_DATA.length +
	                            "  |  Move: WASD / Arrows  |  Restart: R  |  " +
	                            (exitUnlocked ? "EXIT UNLOCKED!" : "Push blocks to red targets");
	            g2d.drawString(status, 15, barY + 25);
	        }
	    }

	    public static void main(String[] args) {
	        SwingUtilities.invokeLater(() -> {
	            JFrame frame = new JFrame("Maze Moving Blocks Game");
	            MazeBlockGame gamePanel = new MazeBlockGame();
	            frame.add(gamePanel);
	            frame.pack();
	            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	            frame.setLocationRelativeTo(null);
	            frame.setResizable(false);
	            frame.setVisible(true);
	        });
	    }
	}
