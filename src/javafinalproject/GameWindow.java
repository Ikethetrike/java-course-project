package javafinalproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

 //Main gameplay window (JFrame) that manages level state, keyboard input, win detection

public class GameWindow extends JFrame {
    //Main Gameplay window
    private final int TILE_SIZE = 64;
    private final int GRID_SIZE = 10;
    private final int STATUS_BAR_HEIGHT = 40;

    // Initial level configuration 
    private final int levelNumber;
    private final Position playerStartPos;
    private final List<Position> initialBoxPositions;

    // Active game state objects for the current level. 
    private BoardContract board;
    private Player player;
    private List<Entity> entities;
    private EntityManager entityManager;
    private GameTimer gameTimer;
    private GamePanel canvas;

    private int moveCount = 0;
    private boolean levelCompleted = false;

    
    //Initializes and displays the game window for a given level.
     
    public GameWindow(int levelNumber, BoardContract board, Position playerStartPos, List<Position> boxStartPositions) {
        this.levelNumber = levelNumber;
        this.board = board;
        this.playerStartPos = playerStartPos;
        this.initialBoxPositions = new ArrayList<>(boxStartPositions);

        setTitle("Escape Room - Level " + levelNumber);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        setupLevelState();

        // Refresh the canvas once per second so the elapsed timer stays up to date
        this.gameTimer = new GameTimer(() -> {
            if (canvas != null) {
                canvas.repaint();
            }
        });
        this.gameTimer.start();

        this.canvas = new GamePanel();
        add(canvas);
        pack();
        setLocationRelativeTo(null);

        // movement, undo/redo, reset, and returning to the main menu
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        setFocusable(true);
    }

    /** Creates fresh player and box entities and resets counters for the level. */
    private void setupLevelState() {
        this.player = new Player(new Position(playerStartPos.getRow(), playerStartPos.getCol()));
        this.entities = new ArrayList<>();
        for (Position boxPos : initialBoxPositions) {
            this.entities.add(new PushableBox(new Position(boxPos.getRow(), boxPos.getCol())));
        }
        this.entityManager = new EntityManager(player, entities);
        this.moveCount = 0;
        this.levelCompleted = false;
    }

    //Restarts the current level 
    private void resetLevel() {
        setupLevelState();
        gameTimer.reset();
        gameTimer.start();
        canvas.repaint();
    }

    //Stops the level and returns to the main menu
    private void returnToMainMenu() {
        gameTimer.stop();
        dispose();
        MainGameLauncher.showMainMenu();
    }

    /** Processes keyboard shortcuts and directional movement keys. */
    private void handleKeyPress(int keyCode) {
        if (keyCode == KeyEvent.VK_M || keyCode == KeyEvent.VK_ESCAPE) {
            returnToMainMenu();
            return;
        }

        if (keyCode == KeyEvent.VK_R) {
            resetLevel();
            return;
        }

        // Prevent further moves once the puzzle has been solved
        if (levelCompleted) {
            return;
        }

        if (keyCode == KeyEvent.VK_Z) {
            if (entityManager.getUndoRedoManager().undo(player)) {
                moveCount = Math.max(0, moveCount - 1);
                canvas.repaint();
            }
            return;
        }

        if (keyCode == KeyEvent.VK_Y) {
            if (entityManager.getUndoRedoManager().redo(player)) {
                moveCount++;
                checkWinCondition();
                canvas.repaint();
            }
            return;
        }

        Direction dir = null;
        if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) dir = Direction.UP;
        else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) dir = Direction.DOWN;
        else if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) dir = Direction.LEFT;
        else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) dir = Direction.RIGHT;

        if (dir != null) {
            boolean moved = entityManager.tryMovePlayer(dir, board);
            if (moved) {
                moveCount++;
                canvas.repaint();
                checkWinCondition();
            }
        }
    }

    //Checks whether there is at least one box and every box is on a pressure plate.
    private boolean areAllBoxesOnPlates() {
        long boxCount = 0;
        long boxesOnPlates = 0;

        for (Entity e : entityManager.getEntities()) {
            if (e instanceof PushableBox) {
                boxCount++;
                if (board.isPressurePlate(e.getPosition())) {
                    boxesOnPlates++;
                }
            }
        }
        return boxCount > 0 && boxCount == boxesOnPlates;
    }

    //Ends the level once all boxes are on pressure plates 
    private void checkWinCondition() {
        if (areAllBoxesOnPlates()) {
            levelCompleted = true;
            gameTimer.stop();
            JOptionPane.showMessageDialog(
                this,
                "Level " + levelNumber + " Complete!\nTime: " + gameTimer.getFormattedTime() + "\nMoves: " + moveCount,
                "Victory!",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /** Custom panel that draws the board tiles, pushable boxes, player, and HUD. */
    private class GamePanel extends JPanel {
        public GamePanel() {
            setPreferredSize(new Dimension(GRID_SIZE * TILE_SIZE, GRID_SIZE * TILE_SIZE + STATUS_BAR_HEIGHT));
            setBackground(new Color(30, 30, 35));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            //Draw 10x10 Grid Tiles (floor, walls, and pressure plates)
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    Position pos = new Position(row, col);
                    int x = col * TILE_SIZE;
                    int y = row * TILE_SIZE;

                    if (board.isWalkable(pos)) {
                        g2d.setColor(new Color(235, 235, 240));
                    } else {
                        g2d.setColor(new Color(55, 60, 70));
                    }

                    if (board.isPressurePlate(pos)) {
                        g2d.setColor(new Color(245, 195, 60));
                    }

                    g2d.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                    g2d.setColor(new Color(200, 200, 205, 80));
                    g2d.drawRect(x, y, TILE_SIZE, TILE_SIZE);
                }
            }

            //Draw Pushable Boxes (turn green when resting on a pressure plate)
            for (Entity e : entityManager.getEntities()) {
                if (e instanceof PushableBox) {
                    Position p = e.getPosition();
                    int bx = p.getCol() * TILE_SIZE + 6;
                    int by = p.getRow() * TILE_SIZE + 6;
                    int bSize = TILE_SIZE - 12;

                    boolean onPlate = board.isPressurePlate(p);
                    g2d.setColor(onPlate ? new Color(70, 190, 105) : new Color(190, 105, 35));
                    g2d.fillRoundRect(bx, by, bSize, bSize, 12, 12);

                    g2d.setColor(new Color(40, 40, 45));
                    g2d.drawRoundRect(bx, by, bSize, bSize, 12, 12);
                    g2d.drawLine(bx + 6, by + 6, bx + bSize - 6, by + bSize - 6);
                    g2d.drawLine(bx + bSize - 6, by + 6, bx + 6, by + bSize - 6);
                }
            }

            //Draw Player
            Position playerPos = entityManager.getPlayer().getPosition();
            int px = playerPos.getCol() * TILE_SIZE + 8;
            int py = playerPos.getRow() * TILE_SIZE + 8;
            int pSize = TILE_SIZE - 16;

            g2d.setColor(new Color(40, 110, 235));
            g2d.fillOval(px, py, pSize, pSize);
            g2d.setColor(new Color(20, 60, 150));
            g2d.drawOval(px, py, pSize, pSize);

            //Draw Status Bar
            int barY = GRID_SIZE * TILE_SIZE;
            g2d.setColor(new Color(28, 30, 38));
            g2d.fillRect(0, barY, getWidth(), STATUS_BAR_HEIGHT);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            String statusText = String.format(
                "Lvl %d | Time: %s | Moves: %d | [Z] Undo [Y] Redo [R] Reset [M] Menu",
                levelNumber, gameTimer.getFormattedTime(), moveCount
            );
            g2d.drawString(statusText, 12, barY + 25);
        }
    }
}