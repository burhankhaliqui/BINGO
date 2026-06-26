package mediaworking.GameSmiths;

import mediaworking.GameSmiths.Model.Board;

import java.util.ArrayList;

public class Winner {

    public static ArrayList<String> checkForWinners(Player... players) {
        ArrayList<String> winners = new ArrayList<>();

        for (Player player : players) {
            if (checkPlayerWin(player)) {
                winners.add(player.getName());
            }
        }

        return winners;
    }

    private static boolean checkPlayerWin(Player player) {
        Board board = player.getBoard();
        int size = board.getSize();
        int completedCount = board.getCompletedCount();

        // If the completed count equals the board size, it's a win
        return completedCount == size;
    }
}