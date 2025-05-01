import java.io.*;
import java.util.Scanner;

public class ScoreManager {
    private static final String HIGH_SCORE_FILE = "highscore.txt";
    private int highScore;

    public ScoreManager() {
        loadHighScore();
    }

    public int getHighScore() {
        return highScore;
    }

    public void saveHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            try (PrintWriter writer = new PrintWriter(new FileWriter(HIGH_SCORE_FILE))) {
                writer.println(highScore);
            } catch (IOException e) {
                System.out.println("Error saving high score: " + e.getMessage());
            }
        }
    }

    private void loadHighScore() {
        try (Scanner scanner = new Scanner(new FileReader(HIGH_SCORE_FILE))) {
            if (scanner.hasNextInt()) {
                highScore = scanner.nextInt();
            }
        } catch (FileNotFoundException e) {
            highScore = 0; // No high score file found, start with 0
        }
    }
}
