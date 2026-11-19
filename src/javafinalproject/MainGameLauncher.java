//Isaac
package javafinalproject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

//Launches the menu, selects the level, and starts the game window
public class MainGameLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGameLauncher::showMainMenu);
    }

    //Builds and displays the main menu with level selection and exit
    public static void showMainMenu() {
        JFrame menuFrame = new JFrame("2D Escape Room - Main Menu");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(400, 400);
        menuFrame.setLocationRelativeTo(null);
        menuFrame.setResizable(false);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(40, 44, 52));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("ESCAPE ROOM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sokoban Puzzle Edition");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(170, 175, 185));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalStrut(45));

        JButton levelSelectButton = createStyledButton("Select Level");
        JButton exitButton = createStyledButton("Exit Game");

        //Gives the user the option to pick level, then launches the game
        levelSelectButton.addActionListener(e -> {
            String[] options = {"Easy (Level 1)", "Medium (Level 2)", "Hard (Level 3)"};
            int choice = JOptionPane.showOptionDialog(
                menuFrame, "Choose Difficulty:", "Level Select",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]
            );
            if (choice >= 0) {
                menuFrame.dispose();
                launchGame(choice + 1);
            }
        });

        exitButton.addActionListener(e -> System.exit(0));

        panel.add(levelSelectButton);
        panel.add(Box.createVerticalStrut(18));
        panel.add(exitButton);

        menuFrame.add(panel);
        menuFrame.setVisible(true);
    }

    //Creates menu buttons
    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 45));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    //Configures the board, player start, and box positions for the chosen level, then opens the window
    private static void launchGame(int level) {
        BoardContract board = new MockBoard(level);

        Position playerStartPos = new Position(1, 1);
        List<Position> boxPositions = new ArrayList<>();

        if (level == 1) {
            playerStartPos = new Position(1, 1);
            boxPositions.add(new Position(5, 5));
        } else if (level == 2) {
            playerStartPos = new Position(1, 1);
            boxPositions.add(new Position(4, 4));
            boxPositions.add(new Position(5, 5));
        } else {
            playerStartPos = new Position(4, 7);
            boxPositions.add(new Position(5, 6));
            boxPositions.add(new Position(6, 6));
            boxPositions.add(new Position(4, 5));
            boxPositions.add(new Position(5, 4));
        }

        GameWindow window = new GameWindow(level, board, playerStartPos, boxPositions);
        window.setVisible(true);
    }
}