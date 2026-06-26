package mediaworking.GameSmiths.Model;

import java.util.ArrayList;
import java.util.Collections;

public class Board {
    private ArrayList<Integer> playerCard;
    private int size;

    public Board(int size) {
        this.size = size;
        this.playerCard = new ArrayList<>(size * size);
        generateBoard();
    }

    private void generateBoard() {
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= size * size; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        playerCard.addAll(numbers);
    }

    public void markNumber(int number) {
        for (int i = 0; i < playerCard.size(); i++) {
            if (playerCard.get(i) == number) {
                playerCard.set(i, 0);
                break;
            }
        }
    }

    public boolean isMarked(int index) {
        if (index < 0 || index >= playerCard.size()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index);
        }
        return playerCard.get(index) == 0;
    }

    public void displayBoard() {
        System.out.println(this); // Leverage the toString() method
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int number = playerCard.get(i * size + j);
                builder.append(String.format("%-4s", (number == 0 ? "X" : number))); // Replace 0 with 'X'
            }
            builder.append("\n"); // Add newline after each row
        }
        return builder.toString();
    }

    public int checkCompletedLines() {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (checkRow(i)) count++;
        }
        for (int i = 0; i < size; i++) {
            if (checkColumn(i)) count++;
        }
        count += checkDiagonals();
        return count;
    }

    private boolean checkRow(int row) {
        for (int j = 0; j < size; j++) {
            if (playerCard.get(row * size + j) != 0) return false;
        }
        return true;
    }

    private boolean checkColumn(int col) {
        for (int i = 0; i < size; i++) {
            if (playerCard.get(i * size + col) != 0) return false;
        }
        return true;
    }

    private int checkDiagonals() {
        int count = 0;
        boolean leftDiagonal = true;
        for (int i = 0; i < size; i++) {
            if (playerCard.get(i * size + i) != 0) {
                leftDiagonal = false;
                break;
            }
        }
        if (leftDiagonal) count++;
        boolean rightDiagonal = true;
        for (int i = 0; i < size; i++) {
            if (playerCard.get(i * size + (size - 1 - i)) != 0) {
                rightDiagonal = false;
                break;
            }
        }
        if (rightDiagonal) count++;
        return count;
    }

    public int getSize() {
        return size;
    }

    public int getCompletedCount() {
        return checkCompletedLines();
    }

    public ArrayList<Integer> getCard() {
        return playerCard;
    }
}