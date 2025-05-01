import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;

public class MemoryMatchGame extends JFrame {
    private JButton[] buttons;
    private ArrayList<Card> cards;
    private Card selectedCard1, selectedCard2;
    private int matchesFound = 0;
    private int difficulty; // 4 for easy, 6 for medium, 8 for hard
    private Timer timer;
    private int timeLimit = 5; // Time limit in seconds
    private ScoreManager scoreManager; // New ScoreManager instance
    private JLabel highScoreLabel; // Label to display high score
    private JLabel currentScoreLabel; // Label to display current score
    private final int pointsPerMatch = 10; // Points for each match
    private int currentScore = 0; // Current score

    public MemoryMatchGame() {
        setTitle("Memory Match Game");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Difficulty selection
        String[] options = {"Easy (4x4)", "Medium (6x6)", "Hard (8x8)"};
        JComboBox<String> difficultySelector = new JComboBox<>(options);
        difficultySelector.addActionListener(e -> {
            String selected = (String) difficultySelector.getSelectedItem();
            if (selected != null) {
                switch (selected) {
                    case "Easy (4x4)":
                        difficulty = 4;
                        break;
                    case "Medium (6x6)":
                        difficulty = 6;
                        break;
                    case "Hard (8x8)":
                        difficulty = 8;
                        break;
                }
                startGame();
            }
        });

        add(difficultySelector, BorderLayout.NORTH);

        // Initialize ScoreManager and score labels
        scoreManager = new ScoreManager();
        highScoreLabel = new JLabel("High Score: " + scoreManager.getHighScore());
        currentScoreLabel = new JLabel("Current Score: " + currentScore);
        JPanel scorePanel = new JPanel();
        scorePanel.add(highScoreLabel);
        scorePanel.add(currentScoreLabel);
        add(scorePanel, BorderLayout.SOUTH);

        // Add window listener to save high score on exit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Save the high score if the current score is higher
                scoreManager.saveHighScore(currentScore);
                JOptionPane.showMessageDialog(null, "Exiting the game.\nYour Current Score: " + currentScore + "\nHigh Score: " + scoreManager.getHighScore());
                System.exit(0);
            }
        });
    }

    private void startGame() {
        if (buttons != null) {
            removeButtons(); // Clear previous buttons if any
        }

        matchesFound = 0;
        currentScore = 0; // Reset current score
        currentScoreLabel.setText("Current Score: " + currentScore); // Update score display

        cards = new ArrayList<>();
        for (int i = 0; i < (difficulty * difficulty) / 2; i++) {
            cards.add(new Card(i));
            cards.add(new Card(i));
        }
        Collections.shuffle(cards);

        buttons = new JButton[difficulty * difficulty];
        JPanel buttonPanel = new JPanel(new GridLayout(difficulty, difficulty));
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton("?");
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 50));
            buttons[i].addActionListener(new ButtonClickListener(i));
            buttonPanel.add(buttons[i]);
        }
        add(buttonPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void removeButtons() {
        for (JButton button : buttons) {
            remove(button);
        }
    }

    private class ButtonClickListener implements ActionListener {
        private int index;

        public ButtonClickListener(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (selectedCard1 == null) {
                selectedCard1 = cards.get(index);
                buttons[index].setText(String.valueOf(selectedCard1.getValue()));
                buttons[index].setEnabled(false);
                startTimer();
            } else if (selectedCard2 == null && index != cards.indexOf(selectedCard1)) {
                selectedCard2 = cards.get(index);
                buttons[index].setText(String.valueOf(selectedCard2.getValue()));
                buttons[index].setEnabled(false);

                Timer timer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        checkForMatch();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(1000, new ActionListener() {
            private int timeRemaining = timeLimit;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (timeRemaining > 0) {
                    System.out.println("Time remaining: " + timeRemaining + " seconds");
                    timeRemaining--;
                } else {
                    // Time's up, flip the card back
                    buttons[cards.indexOf(selectedCard1)].setText("?");
                    buttons[cards.indexOf(selectedCard1)].setEnabled(true);
                    selectedCard1 = null;
                    selectedCard2 = null;
                    timer.stop();
                }
            }
        });
        timer.start();
    }

    private void checkForMatch() {
        if (selectedCard1.getValue() == selectedCard2.getValue()) {
            matchesFound++;
            currentScore += pointsPerMatch; // Update current score for a match
        } else {
            // If not a match, simply flip the cards back
            buttons[cards.indexOf(selectedCard1)].setText("?");
            buttons[cards.indexOf(selectedCard2)].setText("?");
            buttons[cards.indexOf(selectedCard1)].setEnabled(true);
            buttons[cards.indexOf(selectedCard2)].setEnabled(true);
        }

        // Update current score label
        currentScoreLabel.setText("Current Score: " + currentScore);

        // Check if all matches are found
        if (matchesFound == (difficulty * difficulty) / 2) {
            // Save high score if all matches found
            scoreManager.saveHighScore(currentScore);
            JOptionPane.showMessageDialog(this, "Congratulations! You've found all matches!\nYour Score: " + currentScore);
            highScoreLabel.setText("High Score: " + scoreManager.getHighScore());
            System.exit(0); // Exit the game after completion
        }

        selectedCard1 = null;
        selectedCard2 = null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MemoryMatchGame game = new MemoryMatchGame();
            game.setVisible(true);
        });
    }
}
