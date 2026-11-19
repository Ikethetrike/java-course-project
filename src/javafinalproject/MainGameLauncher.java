package javafinalproject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MainGameLauncher {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Ask the player which level they want to play
            String[] options = {"Easy (Level 1)", "Medium (Level 2)", "Hard (Level 3)"};
            int choice = JOptionPane.showOptionDialog(
                null, 
                "Select difficulty for your Escape Room:", 
                "Level Select", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                options, 
                options[0]
            );

            // Default to Easy if closed or cancelled
            int selectedLevel = (choice >= 0) ? choice + 1 : 1;

            // Build board and entities based on chosen level
            BoardContract board = new MockBoard(selectedLevel);
            
            // Give playerStartPos a default value so Java is happy
            Position playerStartPos = new Position(1, 1);
            
            if (selectedLevel == 1) {
                playerStartPos = new Position(4, 7);
            } else if (selectedLevel == 2) {
                playerStartPos = new Position(1, 1);
            } else if (selectedLevel == 3) {
                playerStartPos = new Position(4, 7);
            }
            
            Player player = new Player(playerStartPos);
            
            List<Entity> entities = new ArrayList<>();

            if (selectedLevel == 1) {
                // 1 Box for Easy
            	entities.add(new PushableBox(new Position(2, 5)));
                entities.add(new PushableBox(new Position(7, 2)));
                entities.add(new PushableBox(new Position(7, 7)));
                entities.add(new PushableBox(new Position(1, 7)));
            } else if (selectedLevel == 2) {
                // 2 Boxes for Medium
                entities.add(new PushableBox(new Position(4, 4)));
                entities.add(new PushableBox(new Position(5, 5)));
            } else {
                // 4 Boxes for Hard (matching the 4 corner targets)
            	entities.add(new PushableBox(new Position(4, 5)));
                entities.add(new PushableBox(new Position(5, 6)));
                entities.add(new PushableBox(new Position(6, 6)));
                entities.add(new PushableBox(new Position(5, 4)));
                
            }

            EntityManager manager = new EntityManager(player, entities);
            GameWindow window = new GameWindow(manager, board);
            window.setVisible(true);
        });
    }
}