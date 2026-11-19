package javafinalproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class GameWindow extends JFrame {
    private EntityManager entityManager;
    private BoardContract board;
    private final int TILE_SIZE = 64;
    private final int GRID_SIZE = 10; // Updated for the 7x7 map

    public GameWindow(EntityManager entityManager, BoardContract board) {
        this.entityManager = entityManager;
        this.board = board;

        setTitle("2D Escape Room - Sokoban Puzzle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel canvas = new GamePanel();
        add(canvas);
        pack();
        setLocationRelativeTo(null);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Direction dir = null;
                int key = e.getKeyCode();

                if (key == KeyEvent.VK_UP || key == KeyEvent.VK_W) dir = Direction.UP;
                else if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) dir = Direction.DOWN;
                else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) dir = Direction.LEFT;
                else if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) dir = Direction.RIGHT;

                if (dir != null) {
                    boolean moved = entityManager.tryMovePlayer(dir, board);
                    if (moved) {
                        canvas.repaint();
                        
                        // Check Win Condition: Are ALL boxes on pressure plates?
                        if (checkWinCondition()) {
                            JOptionPane.showMessageDialog(GameWindow.this, "Puzzle Solved! All boxes on targets!");
                        }
                    }
                }
            }
        });
        
        setFocusable(true);
    }

    private boolean checkWinCondition() {
        List<Entity> entities = entityManager.getEntities();
        long boxCount = 0;
        long boxesOnPlates = 0;

        for (Entity e : entities) {
            if (e instanceof PushableBox) {
                boxCount++;
                if (board.isPressurePlate(e.getPosition())) {
                    boxesOnPlates++;
                }
            }
        }
        return boxCount > 0 && boxCount == boxesOnPlates;
    }

    private class GamePanel extends JPanel {
        public GamePanel() {
            setPreferredSize(new Dimension(GRID_SIZE * TILE_SIZE, GRID_SIZE * TILE_SIZE));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // 1. Draw 7x7 Grid
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    Position pos = new Position(row, col);
                    
                    if (board.isWalkable(pos)) {
                        g2d.setColor(new Color(220, 220, 220));
                    } else {
                        g2d.setColor(new Color(60, 60, 60));
                    }
                    
                    if (board.isPressurePlate(pos)) {
                        g2d.setColor(new Color(230, 200, 80)); // Yellow target square
                    }

                    g2d.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }

            // 2. Draw Entities (Boxes)
            for (Entity e : entityManager.getEntities()) {
                Position p = e.getPosition();
                if (e instanceof PushableBox) {
                    g2d.setColor(new Color(210, 120, 50));
                    g2d.fillRect(p.getCol() * TILE_SIZE + 8, p.getRow() * TILE_SIZE + 8, TILE_SIZE - 16, TILE_SIZE - 16);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(p.getCol() * TILE_SIZE + 8, p.getRow() * TILE_SIZE + 8, TILE_SIZE - 16, TILE_SIZE - 16);
                }
            }

            // 3. Draw Player
            Position playerPos = entityManager.getPlayer().getPosition();
            g2d.setColor(new Color(50, 100, 220));
            g2d.fillOval(playerPos.getCol() * TILE_SIZE + 10, playerPos.getRow() * TILE_SIZE + 10, TILE_SIZE - 20, TILE_SIZE - 20);
            g2d.setColor(Color.BLACK);
            g2d.drawOval(playerPos.getCol() * TILE_SIZE + 10, playerPos.getRow() * TILE_SIZE + 10, TILE_SIZE - 20, TILE_SIZE - 20);
        }
    }
}