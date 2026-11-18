package javafinalproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Escape Room - Maze Moving Blocks Game");
            MazeBlockGame gamePanel = new MazeBlockGame();
            frame.add(gamePanel);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setVisible(true);
        });
    }

    // =========================================================================
    // 1. TILE TYPE ENUM
    // =========================================================================
    public enum TileType {
        WALL('#', false),
        FLOOR(' ', true),
        PRESSURE_PLATE('.', true),
        EXIT('E', true);

        private final char symbol;
        private final boolean walkable;

        TileType(char symbol, boolean walkable) {
            this.symbol = symbol;
            this.walkable = walkable;
        }

        public boolean isWalkable() {
            return walkable;
        }

        public static TileType fromChar(char c) {
            for (TileType type : values()) {
                if (type.symbol == c) return type;
            }
            if (c == '*') return PRESSURE_PLATE;
            return FLOOR;
        }
    }

    // =========================================================================
    // 2. PLAYER CLASS (With Directional Eyes & Move/Push Counters)
    // =========================================================================
    public static class Player {
        private int row;
        private int col;
        private final int startRow;
        private final int startCol;

        private int facingRow = 1;
        private int facingCol = 0;

        private int movesCount = 0;
        private int pushesCount = 0;

        public Player(int startRow, int startCol) {
            this.startRow = startRow;
            this.startCol = startCol;
            this.row = startRow;
            this.col = startCol;
        }

        public void move(int dRow, int dCol, boolean pushedBlock) {
            this.row += dRow;
            this.col += dCol;
            this.facingRow = dRow;
            this.facingCol = dCol;
            this.movesCount++;
            if (pushedBlock) {
                this.pushesCount++;
            }
        }

        public void setFacing(int dRow, int dCol) {
            if (dRow != 0 || dCol != 0) {
                this.facingRow = dRow;
                this.facingCol = dCol;
            }
        }

        public void reset() {
            this.row = startRow;
            this.col = startCol;
            this.facingRow = 1;
            this.facingCol = 0;
            this.movesCount = 0;
            this.pushesCount = 0;
        }

        public void draw(Graphics2D g2d, int tileSize) {
            int px = col * tileSize;
            int py = row * tileSize;

            // Drop shadow
            g2d.setColor(new Color(0, 0, 0, 80));
            g2d.fillOval(px + 8, py + 10, tileSize - 16, tileSize - 16);

            // Cyan Body
            g2d.setColor(new Color(139, 233, 253));
            g2d.fillOval(px + 6, py + 6, tileSize - 12, tileSize - 12);

            // Outline
            g2d.setColor(new Color(40, 42, 54));
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawOval(px + 6, py + 6, tileSize - 12, tileSize - 12);

            // Directional Eyes
            int centerX = px + tileSize / 2;
            int centerY = py + tileSize / 2;
            int eyeOffsetX = facingCol * 5;
            int eyeOffsetY = facingRow * 5;

            g2d.setColor(new Color(40, 42, 54));
            if (facingCol != 0) {
                g2d.fillOval(centerX + eyeOffsetX - 3, centerY - 7, 6, 6);
                g2d.fillOval(centerX + eyeOffsetX - 3, centerY + 1, 6, 6);
            } else {
                g2d.fillOval(centerX - 8, centerY + eyeOffsetY - 3, 6, 6);
                g2d.fillOval(centerX + 2, centerY + eyeOffsetY - 3, 6, 6);
            }
        }

        public int getRow() { return row; }
        public int getCol() { return col; }
        public int getMovesCount() { return movesCount; }
        public int getPushesCount() { return pushesCount; }
        public void setPosition(int row, int col) {
            this.row = row;
            this.col = col;
        }
        public void decrementCounters(boolean pushedBlock) {
            if (movesCount > 0) movesCount--;
            if (pushedBlock && pushesCount > 0) pushesCount--;
        }
    }

    // =========================================================================
    // 3. BOARD & LEVEL LOADER
    // =========================================================================
    public static class Board {
        private final int rows;
        private final int cols;
        private final TileType[][] grid;
        private final List<Point> blocks;
        private final Player player;

        public Board(TileType[][] grid, Player player, List<Point> initialBlocks) {
            this.rows = grid.length;
            this.cols = grid[0].length;
            this.grid = grid;
            this.player = player;
            this.blocks = new ArrayList<>();
            for (Point b : initialBlocks) {
                this.blocks.add(new Point(b.x, b.y));
            }
        }

        public static Board fromStringArray(String[] layout) {
            int rows = layout.length;
            int cols = layout[0].length();
            TileType[][] grid = new TileType[rows][cols];
            Player player = new Player(1, 1);
            List<Point> blocks = new ArrayList<>();

            for (int r = 0; r < rows; r++) {
                String rowStr = layout[r];
                for (int c = 0; c < cols; c++) {
                    char ch = rowStr.charAt(c);
                    grid[r][c] = TileType.fromChar(ch);
                    if (ch == '@') {
                        player = new Player(r, c);
                    } else if (ch == '$' || ch == '*') {
                        blocks.add(new Point(c, r));
                    }
                }
            }
            return new Board(grid, player, blocks);
        }

        public boolean isWithinBounds(int r, int c) {
            return r >= 0 && r < rows && c >= 0 && c < cols;
        }

        public boolean isWalkable(int r, int c) {
            if (!isWithinBounds(r, c)) return false;
            if (!grid[r][c].isWalkable()) return false;
            if (grid[r][c] == TileType.EXIT && !areAllPlatesCovered()) return false;
            return true;
        }

        public boolean canPushBlockTo(int r, int c) {
            if (!isWithinBounds(r, c)) return false;
            TileType t = grid[r][c];
            return t != TileType.WALL && t != TileType.EXIT && getBlockAt(r, c) == null;
        }

        public Point getBlockAt(int r, int c) {
            for (Point b : blocks) {
                if (b.y == r && b.x == c) return b;
            }
            return null;
        }

        public boolean areAllPlatesCovered() {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (grid[r][c] == TileType.PRESSURE_PLATE && getBlockAt(r, c) == null) {
                        return false;
                    }
                }
            }
            return true;
        }

        public TileType getTile(int r, int c) { return grid[r][c]; }
        public int getRows() { return rows; }
        public int getCols() { return cols; }
        public Player getPlayer() { return player; }
        public List<Point> getBlocks() { return blocks; }
    }

    // =========================================================================
    // 4. UNDO / REDO MANAGER
    // =========================================================================
    public static class UndoRedoManager {
        public static class MoveRecord {
            final int pFromR, pFromC, pToR, pToC;
            final boolean blockMoved;
            final int bFromR, bFromC, bToR, bToC;

            public MoveRecord(int pFromR, int pFromC, int pToR, int pToC,
                              boolean blockMoved, int bFromR, int bFromC, int bToR, int bToC) {
                this.pFromR = pFromR; this.pFromC = pFromC;
                this.pToR = pToR; this.pToC = pToC;
                this.blockMoved = blockMoved;
                this.bFromR = bFromR; this.bFromC = bFromC;
                this.bToR = bToR; this.bToC = bToC;
            }
        }

        private final Deque<MoveRecord> undoStack = new ArrayDeque<>();
        private final Deque<MoveRecord> redoStack = new ArrayDeque<>();

        public void recordMove(MoveRecord record) {
            undoStack.push(record);
            redoStack.clear();
        }

        public boolean undo(Board board) {
            if (undoStack.isEmpty()) return false;
            MoveRecord rec = undoStack.pop();
            Player p = board.getPlayer();
            p.setPosition(rec.pFromR, rec.pFromC);
            p.decrementCounters(rec.blockMoved);

            if (rec.blockMoved) {
                Point b = board.getBlockAt(rec.bToR, rec.bToC);
                if (b != null) b.setLocation(rec.bFromC, rec.bFromR);
            }
            redoStack.push(rec);
            return true;
        }

        public boolean redo(Board board) {
            if (redoStack.isEmpty()) return false;
            MoveRecord rec = redoStack.pop();
            if (rec.blockMoved) {
                Point b = board.getBlockAt(rec.bFromR, rec.bFromC);
                if (b != null) b.setLocation(rec.bToC, rec.bToR);
            }
            Player p = board.getPlayer();
            p.move(rec.pToR - rec.pFromR, rec.pToC - rec.pFromC, rec.blockMoved);
            undoStack.push(rec);
            return true;
        }

        public void clear() {
            undoStack.clear();
            redoStack.clear();
        }
    }

    // =========================================================================
    // 5. GAME TIMER
    // =========================================================================
    public static class GameTimer {
        private int elapsedSeconds = 0;
        private boolean running = false;
        private final Timer swingTimer;

        public GameTimer(Runnable onTick) {
            this.swingTimer = new Timer(1000, e -> {
                if (running) {
                    elapsedSeconds++;
                    if (onTick != null) onTick.run();
                }
            });
        }

        public void start() { running = true; swingTimer.start(); }
        public void stop() { running = false; swingTimer.stop(); }
        public void reset() { stop(); elapsedSeconds = 0; }
        public int getElapsedSeconds() { return elapsedSeconds; }

        public String getFormattedTime() {
            return String.format("%02d:%02d", elapsedSeconds / 60, elapsedSeconds % 60);
        }
    }

    // =========================================================================
    // 6. ACCOUNT MANAGER (Saves Best Times to File)
    // =========================================================================
    public static class AccountManager {
        private final Path saveFile = Paths.get("accounts_best_times.txt");
        private String currentUser = "Guest";
        private final Map<String, Map<Integer, Integer>> profiles = new HashMap<>();

        public AccountManager() {
            load();
        }

        public void login(String username) {
            if (username != null && !username.trim().isEmpty()) {
                currentUser = username.trim();
            }
            profiles.putIfAbsent(currentUser, new HashMap<>());
            save();
        }

        public boolean recordTime(int level, int seconds) {
            profiles.putIfAbsent(currentUser, new HashMap<>());
            Map<Integer, Integer> scores = profiles.get(currentUser);
            int best = scores.getOrDefault(level, Integer.MAX_VALUE);
            if (seconds < best) {
                scores.put(level, seconds);
                save();
                return true;
            }
            return false;
        }

        public String getBestTimeStr(int level) {
            Map<Integer, Integer> scores = profiles.get(currentUser);
            if (scores == null || !scores.containsKey(level)) return "--";
            return scores.get(level) + "s";
        }

        public String getCurrentUser() { return currentUser; }

        private void load() {
            if (!Files.exists(saveFile)) return;
            try (BufferedReader br = Files.newBufferedReader(saveFile)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(":", 2);
                    if (parts.length < 2) continue;
                    Map<Integer, Integer> map = new HashMap<>();
                    for (String entry : parts[1].split(",")) {
                        String[] kv = entry.split("=");
                        if (kv.length == 2) {
                            map.put(Integer.parseInt(kv[0]), Integer.parseInt(kv[1]));
                        }
                    }
                    profiles.put(parts[0], map);
                }
            } catch (Exception ignored) {}
        }

        private void save() {
            try (BufferedWriter bw = Files.newBufferedWriter(saveFile)) {
                for (var entry : profiles.entrySet()) {
                    List<String> pairs = new ArrayList<>();
                    for (var s : entry.getValue().entrySet()) {
                        pairs.add(s.getKey() + "=" + s.getValue());
                    }
                    bw.write(entry.getKey() + ":" + String.join(",", pairs));
                    bw.newLine();
                }
            } catch (IOException ignored) {}
        }
    }

    // =========================================================================
    // 7. MAIN GAME PANEL (MazeBlockGame)
    // =========================================================================
    public static class MazeBlockGame extends JPanel {
        private static final int TILE_SIZE = 48;

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
        private Board board;
        private final UndoRedoManager undoRedo = new UndoRedoManager();
        private final AccountManager accounts = new AccountManager();
        private final GameTimer timer;
        private boolean gameWon = false;

        public MazeBlockGame() {
            setBackground(new Color(25, 25, 35));
            setFocusable(true);

            String user = JOptionPane.showInputDialog(null, "Enter player name:", "Player Login", JOptionPane.PLAIN_MESSAGE);
            accounts.login(user);

            timer = new GameTimer(this::repaint);
            loadLevel(currentLevel);

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    handleInput(e.getKeyCode());
                }
            });
        }

        private void loadLevel(int levelIndex) {
            board = Board.fromStringArray(LEVEL_DATA[levelIndex]);
            undoRedo.clear();
            timer.reset();
            timer.start();

            setPreferredSize(new Dimension(board.getCols() * TILE_SIZE, board.getRows() * TILE_SIZE + 45));
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
            if (keyCode == KeyEvent.VK_Z) {
                if (undoRedo.undo(board)) repaint();
                return;
            }
            if (keyCode == KeyEvent.VK_Y) {
                if (undoRedo.redo(board)) repaint();
                return;
            }

            int dRow = 0, dCol = 0;
            switch (keyCode) {
                case KeyEvent.VK_UP, KeyEvent.VK_W -> dRow = -1;
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> dRow = 1;
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> dCol = -1;
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> dCol = 1;
                default -> { return; }
            }

            attemptMove(dRow, dCol);
            repaint();
        }

        private void attemptMove(int dRow, int dCol) {
            Player player = board.getPlayer();
            player.setFacing(dRow, dCol);

            int curRow = player.getRow();
            int curCol = player.getCol();
            int nextRow = curRow + dRow;
            int nextCol = curCol + dCol;

            if (!board.isWalkable(nextRow, nextCol)) return;

            Point block = board.getBlockAt(nextRow, nextCol);
            boolean pushedBlock = false;
            int bFromR = 0, bFromC = 0, bToR = 0, bToC = 0;

            if (block != null) {
                int pushRow = nextRow + dRow;
                int pushCol = nextCol + dCol;

                if (!board.canPushBlockTo(pushRow, pushCol)) return;

                bFromR = block.y; bFromC = block.x;
                bToR = pushRow; bToC = pushCol;
                block.setLocation(pushCol, pushRow);
                pushedBlock = true;
            }

            player.move(dRow, dCol, pushedBlock);
            undoRedo.recordMove(new UndoRedoManager.MoveRecord(
                curRow, curCol, nextRow, nextCol,
                pushedBlock, bFromR, bFromC, bToR, bToC
            ));

            if (board.getTile(player.getRow(), player.getCol()) == TileType.EXIT && board.areAllPlatesCovered()) {
                timer.stop();
                accounts.recordTime(currentLevel + 1, timer.getElapsedSeconds());
                advanceLevel();
            }
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

            boolean exitUnlocked = board.areAllPlatesCovered();

            for (int r = 0; r < board.getRows(); r++) {
                for (int c = 0; c < board.getCols(); c++) {
                    int x = c * TILE_SIZE;
                    int y = r * TILE_SIZE;
                    TileType tile = board.getTile(r, c);

                    g2d.setColor(new Color(40, 42, 54));
                    g2d.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                    g2d.setColor(new Color(50, 52, 64));
                    g2d.drawRect(x, y, TILE_SIZE, TILE_SIZE);

                    if (tile == TileType.WALL) {
                        g2d.setColor(new Color(98, 114, 164));
                        g2d.fillRoundRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4, 8, 8);
                    } else if (tile == TileType.PRESSURE_PLATE) {
                        g2d.setColor(new Color(255, 85, 85, 120));
                        g2d.fillOval(x + 12, y + 12, TILE_SIZE - 24, TILE_SIZE - 24);
                        g2d.setColor(new Color(255, 85, 85));
                        g2d.drawOval(x + 12, y + 12, TILE_SIZE - 24, TILE_SIZE - 24);
                    } else if (tile == TileType.EXIT) {
                        g2d.setColor(exitUnlocked ? new Color(80, 250, 123) : new Color(100, 100, 110));
                        g2d.fillRoundRect(x + 6, y + 6, TILE_SIZE - 12, TILE_SIZE - 12, 12, 12);
                    }
                }
            }

            // Draw Movable Blocks
            for (Point b : board.getBlocks()) {
                int x = b.x * TILE_SIZE;
                int y = b.y * TILE_SIZE;
                boolean onTarget = (board.getTile(b.y, b.x) == TileType.PRESSURE_PLATE);

                g2d.setColor(onTarget ? new Color(80, 250, 123) : new Color(255, 184, 108));
                g2d.fillRoundRect(x + 6, y + 6, TILE_SIZE - 12, TILE_SIZE - 12, 10, 10);
                g2d.setColor(new Color(30, 30, 40));
                g2d.drawRoundRect(x + 6, y + 6, TILE_SIZE - 12, TILE_SIZE - 12, 10, 10);
            }

            // Draw Player with Directional Eyes
            board.getPlayer().draw(g2d, TILE_SIZE);

            // Draw Bottom Status Bar
            int barY = board.getRows() * TILE_SIZE;
            g2d.setColor(new Color(30, 31, 41));
            g2d.fillRect(0, barY, getWidth(), 45);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            if (gameWon) {
                g2d.setColor(new Color(80, 250, 123));
                g2d.drawString("ALL LEVELS CLEARED! Press 'R' to play again.", 15, barY + 27);
            } else {
                Player p = board.getPlayer();
                String status = String.format(
                    "%s | Lvl %d/%d | Time: %s (Best: %s) | Moves: %d | [Z] Undo [Y] Redo [R] Reset",
                    accounts.getCurrentUser(), currentLevel + 1, LEVEL_DATA.length,
                    timer.getFormattedTime(), accounts.getBestTimeStr(currentLevel + 1), p.getMovesCount()
                );
                g2d.drawString(status, 12, barY + 27);
            }
        }
    }
}