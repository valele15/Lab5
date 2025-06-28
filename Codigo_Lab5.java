import java.util.*;

class BST<Key extends Comparable<Key>, Value> {
    private Node root;
    private class Node {
        Key key;
        Value val;
        Node left, right;

        Node(Key key, Value val) {
            this.key = key;
            this.val = val;
        }
    }
    public void put(Key key, Value val) {
        root = put(root, key, val);
    }
    private Node put(Node x, Key key, Value val) {
        if (x == null) return new Node(key, val);
        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = put(x.left, key, val);
        else if (cmp > 0) x.right = put(x.right, key, val);
        else x.val = val;

        return x;
    }

    public void delete(Key key) {
        root = delete(root, key);
    }

    private Node delete(Node x, Key key) {
        if (x == null) return null;

        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = delete(x.left, key);
        else if (cmp > 0) x.right = delete(x.right, key);
        else {
            if (x.right == null) return x.left;
            if (x.left == null) return x.right;

            Node t = x;
            x = min(t.right);
            x.right = deleteMin(t.right);
            x.left = t.left;
        }
        return x;
    }

    private Node min(Node x) {
        if (x.left == null) return x;
        return min(x.left);
    }

    private Node deleteMin(Node x) {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        return x;
    }

    public Value get(Key key) {
        return get(root, key);
    }

    private Value get(Node x, Key key) {
        if (x == null) return null;

        int cmp = key.compareTo(x.key);
        if (cmp < 0) return get(x.left, key);
        else if (cmp > 0) return get(x.right, key);
        else return x.val;
    }

    public Iterable<Key> keys() {
        List<Key> keys = new ArrayList<>();
        inorder(root, keys);
        return keys;
    }

    private void inorder(Node x, List<Key> keys) {
        if (x == null) return;
        inorder(x.left, keys);
        keys.add(x.key);
        inorder(x.right, keys);
    }

    public Key ceiling(Key key) {
        Node x = ceiling(root, key);
        if (x == null) return null;
        return x.key;
    }

    private Node ceiling(Node x, Key key) {
        if (x == null) return null;

        int cmp = key.compareTo(x.key);
        if (cmp == 0) return x;
        if (cmp > 0) return ceiling(x.right, key);

        Node t = ceiling(x.left, key);
        return (t != null) ? t : x;
    }
}

class HashST<Key, Value> {
    private static final int INIT_CAPACITY = 16;
    private Node[] st;
    private int n;

    private static class Node {
        Object key, val;
        Node next;

        Node(Object key, Object val, Node next) {
            this.key = key;
            this.val = val;
            this.next = next;
        }
    }

    public HashST() {
        this(INIT_CAPACITY);
    }

    public HashST(int capacity) {
        st = new Node[capacity];
        n = 0;
    }

    public void put(Key key, Value val) {
        if (key == null) return;
        int i = hash(key);

        for (Node x = st[i]; x != null; x = x.next) {
            if (key.equals(x.key)) {
                x.val = val;
                return;
            }
        }

        st[i] = new Node(key, val, st[i]);
        n++;
    }

    public Value get(Key key) {
        if (key == null) return null;
        int i = hash(key);

        for (Node x = st[i]; x != null; x = x.next) {
            if (key.equals(x.key)) return (Value) x.val;
        }

        return null;
    }

    public boolean contains(Key key) {
        return get(key) != null;
    }
    private int hash(Key key) {
        return Math.abs(key.hashCode()) % st.length;
    }
}
class Player implements Comparable<Player> {
    private String playerName;
    private int wins, draws, losses;

    public Player(String playerName) {
        this.playerName = playerName;
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
    }

    public void addWin() { wins++; }
    public void addDraw() { draws++; }
    public void addLoss() { losses++; }

    public double winRate() {
        int total = wins + draws + losses;
        return total == 0 ? 0 : (double) wins / total;
    }

    public String getPlayerName() { return playerName; }
    public int getWins() { return wins; }

    @Override
    public int compareTo(Player other) {
        return Integer.compare(this.wins, other.wins);
    }

    @Override
    public String toString() {
        return playerName + " - Wins: " + wins + ", Draws: " + draws + ", Losses: " + losses +
                ", Win rate: " + (winRate() * 100) + "%";
    }
}
class Scoreboard {
    private BST<Integer, List<String>> winTree = new BST<>();
    private HashST<String, Player> players = new HashST<>();
    private int playedGames = 0;

    public void registerPlayer(String playerName) {
        if (!players.contains(playerName)) {
            Player p = new Player(playerName);
            players.put(playerName, p);
            addToWinTree(p);
        }
    }

    public void addGameResult(String winner, String loser, boolean draw) {
        playedGames++;

        Player p1 = players.get(winner);
        Player p2 = players.get(loser);

        removeFromWinTree(p1);
        removeFromWinTree(p2);

        if (draw) {
            p1.addDraw();
            p2.addDraw();
        } else {
            p1.addWin();
            p2.addLoss();
        }

        addToWinTree(p1);
        addToWinTree(p2);
    }

    private void addToWinTree(Player p) {
        int wins = p.getWins();
        List<String> list = winTree.get(wins);

        if (list == null) {
            list = new ArrayList<>();
            winTree.put(wins, list);
        }

        list.add(p.getPlayerName());
    }

    private void removeFromWinTree(Player p) {
        int wins = p.getWins();
        List<String> list = winTree.get(wins);

        if (list != null) {
            list.remove(p.getPlayerName());
            if (list.isEmpty()) winTree.delete(wins);
        }
    }
}
class ConnectFour {
    private char[][] grid = new char[6][7]; // Tablero 6x7
    private char currentSymbol = 'X'; // Turno actual (X o O)
    private boolean gameOver = false;
    private char winner = ' ';

    public ConnectFour() {
        // Inicializa el tablero con espacios vacíos
        for (int fila = 0; fila < 6; fila++) {
            for (int col = 0; col < 7; col++) {
                grid[fila][col] = ' ';
            }
        }
    }

    public boolean makeMove(int col) {
        if (col < 0 || col >= 7 || gameOver) {
            return false;
        }
        for (int fila = 5; fila >= 0; fila--) {
            if (grid[fila][col] == ' ') {
                grid[fila][col] = currentSymbol;

                // Verifica si alguien ganó después de este movimiento
                if (checkWin(fila, col)) {
                    gameOver = true;
                    winner = currentSymbol;
                } else if (isFull()) {
                    gameOver = true; // Tablero lleno = empate
                } else {
                    // Cambia el turno
                    currentSymbol = (currentSymbol == 'X') ? 'O' : 'X';
                }
                return true;
            }
        }
        return false;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public char getWinner() {
        return winner;
    }

    public char getCurrentSymbol() {
        return currentSymbol;
    }

    public void printBoard() {
        System.out.println(" 0 1 2 3 4 5 6");
        System.out.println("---------------");

        for (int fila = 0; fila < 6; fila++) {
            System.out.print("|");
            for (int col = 0; col < 7; col++) {
                System.out.print(grid[fila][col] + "|");
            }
            System.out.println();
            System.out.println("---------------");
        }
    }

    private boolean checkWin(int fila, int col) {
        char simbolo = grid[fila][col];

        // Verifica en todas las direcciones posibles
        boolean horizontal = countInDirection(fila, col, 0, 1, simbolo) + countInDirection(fila, col, 0, -1, simbolo) + 1 >= 4;
        boolean vertical = countInDirection(fila, col, 1, 0, simbolo) + countInDirection(fila, col, -1, 0, simbolo) + 1 >= 4;
        boolean diagonal1 = countInDirection(fila, col, 1, 1, simbolo) + countInDirection(fila, col, -1, -1, simbolo) + 1 >= 4;
        boolean diagonal2 = countInDirection(fila, col, 1, -1, simbolo) + countInDirection(fila, col, -1, 1, simbolo) + 1 >= 4;

        return horizontal || vertical || diagonal1 || diagonal2;
    }

    private int countInDirection(int fila, int col, int deltaFila, int deltaCol, char simbolo) {
        int contador = 0;
        int f = fila + deltaFila;
        int c = col + deltaCol;

        while (f >= 0 && f < 6 && c >= 0 && c < 7 && grid[f][c] == simbolo) {
            contador++;
            f += deltaFila;
            c += deltaCol;
        }

        return contador;
    }

    private boolean isFull() {
        for (int col = 0; col < 7; col++) {
            if (grid[0][col] == ' ') {
                return false;
            }
        }
        return true;
    }
}

class Game {
    private String playerA, playerB;
    private ConnectFour cf = new ConnectFour();
    private Scanner sc = new Scanner(System.in);

    public Game(String playerA, String playerB) {
        this.playerA = playerA;
        this.playerB = playerB;
    }
    public void play() {
        System.out.println("Nueva partida: " + playerA + " (X) vs " + playerB + " (O)");

        while (!cf.isGameOver()) {
            cf.printBoard();

            String current = (cf.getCurrentSymbol() == 'X') ? playerA : playerB;
            System.out.print(current + " (" + cf.getCurrentSymbol() + "), elige columna (0-6): ");

            try {
                int col = Integer.parseInt(sc.nextLine());
                if (!cf.makeMove(col)) {
                    System.out.println("invalido, Intenta de nuevo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Debes ingresar un número entre 0 y 6.");
            }
        }

        cf.printBoard();
        if (cf.getWinner() == 'X') {
            System.out.println(playerA + " gana");
        } else if (cf.getWinner() == 'O') {
            System.out.println(playerB + " gana");
        } else {
            System.out.println("¡Empate!");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Nombre Jugador 1 ");
        String p1 = sc.nextLine();

        System.out.print("Nombre Jugador 2 ");
        String p2 = sc.nextLine();
        new Game(p1, p2).play();
    }
}
