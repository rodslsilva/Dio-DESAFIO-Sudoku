import java.io.*;
import java.util.*;

public class Sudoku {
    private int[][] board;
    private boolean[][] fixed;
    private final int SIZE = 9;

    public Sudoku() {
        board = new int[SIZE][SIZE];
        fixed = new boolean[SIZE][SIZE];
        generateBoard();
    }

    // Geração de tabuleiro completo e remoção de células
    private void generateBoard() {
        fillDiagonal();
        solveBoard(0, 0);
        removeCells(40); // remove 40 células aleatoriamente
    }

    private void fillDiagonal() {
        for (int i = 0; i < SIZE; i += 3)
            fillBox(i, i);
    }

    private void fillBox(int row, int col) {
        Random rand = new Random();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                int num;
                do {
                    num = rand.nextInt(9) + 1;
                } while (!isUnusedInBox(row, col, num));
                board[row + i][col + j] = num;
                fixed[row + i][col + j] = true;
            }
    }

    private boolean isUnusedInBox(int rowStart, int colStart, int num) {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[rowStart + i][colStart + j] == num)
                    return false;
        return true;
    }

    private boolean solveBoard(int row, int col) {
        if (row == SIZE)
            return true;
        if (col == SIZE)
            return solveBoard(row + 1, 0);
        if (board[row][col] != 0)
            return solveBoard(row, col + 1);

        for (int num = 1; num <= 9; num++) {
            if (isValid(row, col, num)) {
                board[row][col] = num;
                if (solveBoard(row, col + 1))
                    return true;
                board[row][col] = 0;
            }
        }
        return false;
    }

    private void removeCells(int count) {
        Random rand = new Random();
        while (count > 0) {
            int row = rand.nextInt(SIZE);
            int col = rand.nextInt(SIZE);
            if (board[row][col] != 0) {
                board[row][col] = 0;
                fixed[row][col] = false;
                count--;
            }
        }
    }

    public boolean isValid(int row, int col, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num)
                return false;
        }

        int boxRow = row - row % 3;
        int boxCol = col - col % 3;
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[boxRow + i][boxCol + j] == num)
                    return false;

        return true;
    }

    public boolean makeMove(int row, int col, int num) {
        if (!fixed[row][col] && isValid(row, col, num)) {
            board[row][col] = num;
            return true;
        }
        return false;
    }

    public void printBoard() {
        for (int i = 0; i < SIZE; i++) {
            if (i % 3 == 0 && i != 0)
                System.out.println("------+-------+------");
            for (int j = 0; j < SIZE; j++) {
                if (j % 3 == 0 && j != 0)
                    System.out.print("| ");
                System.out.print((board[i][j] == 0 ? "." : board[i][j]) + " ");
            }
            System.out.println();
        }
    }

    public void saveGame(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(board);
            out.writeObject(fixed);
        }
    }

    public void loadGame(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            board = (int[][]) in.readObject();
            fixed = (boolean[][]) in.readObject();
        }
    }

    // Método principal de execução
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        Sudoku game = new Sudoku();

        while (true) {
            System.out.println("\nTabuleiro atual:");
            game.printBoard();
            System.out.println("Digite [j] para jogar, [s] para salvar, [c] para carregar ou [q] para sair:");
            String opcao = sc.next();

            if (opcao.equalsIgnoreCase("q")) break;
            if (opcao.equalsIgnoreCase("s")) {
                System.out.print("Nome do arquivo para salvar: ");
                game.saveGame(sc.next());
                continue;
            }
            if (opcao.equalsIgnoreCase("c")) {
                System.out.print("Nome do arquivo para carregar: ");
                game.loadGame(sc.next());
                continue;
            }
            if (opcao.equalsIgnoreCase("j")) {
                System.out.print("Linha (0-8): ");
                int row = sc.nextInt();
                System.out.print("Coluna (0-8): ");
                int col = sc.nextInt();
                System.out.print("Número (1-9): ");
                int num = sc.nextInt();
                if (!game.makeMove(row, col, num)) {
                    System.out.println("Jogada inválida!");
                }
            }
        }
    }
}
