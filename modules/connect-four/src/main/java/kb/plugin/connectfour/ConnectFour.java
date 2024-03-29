package kb.plugin.connectfour;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.text.DecimalFormat;

/**
 * The Connect 4 (Or, Connect N) Game, with a graphical user interface
 *
 * @author Yu Liu
 */
public class ConnectFour extends JComponent {

    // ==========CONSTANT FIELDS==========

    // Game Constants
    private static final int kDefaultRows = 6;
    private static final int kDefaultColumns = 7;
    private static final int kDefaultN = 4;
    private static final String kDefaultFirst = "Red";
    private static final String kDefaultSecond = "Yellow";
    private static final Font kDefaultFont = new Font("Arial", Font.PLAIN, 11);

    // Colour constants
    private static final Color kDarkCyan = new Color(0, 125, 125);
    private static final Color kLightGray = new Color(224, 224, 224);
    private static final Color kDarkYellow = new Color(224, 224, 0);
    private static final Color kDarkRed = new Color(224, 0, 0);

    // Player constants
    private static final int kEmpty = 0;
    private static final int kFirst = 1;
    private static final int kSecond = 2;

    // Game state constants
    private static final int kPlaying = 0;
    private static final int kWin = 1;
    private static final int kDraw = 2;

    // UI Constants
    private static final int kDefaultWidth = 768;
    private static final int kDefaultHeight = 768;

    // ==========INSTANCE FIELDS==========

    // Keep track of the last window size to determine if it has changed
    private int lastWidth = 0;
    private int lastHeight = 0;

    // Values computed on resize that determines the position and size of everything
    private double scale = 0.0;
    private double x = 0.0;
    private double y = 0.0;
    private double width = 0.0;
    private double height = 0.0;

    // The column that the mouse is hovering on, or -1 if not selected
    private int hoverColumn = -1;

    // The coordinates of the last connection (used to display line)
    private int connectedMinRow = 0;
    private int connectedMinCol = 0;
    private int connectedMaxRow = 0;
    private int connectedMaxCol = 0;

    // The font used to display text
    private Font mainFont;

    // The window to display this view on
    private JFrame frame;

    // ==========STATE FIELDS==========

    // The 2D game board array
    private int[][] board;

    // The row size of the board
    private int rows;

    // The row size of the board
    private int columns;

    // Total size of the board
    private int size;

    // The number of pieces that needs to be connected to win
    private int n;

    // The current player number
    private int player;

    // The game state number
    private int state;

    // Data for the first player
    private String firstPlayer;
    private int firstScore;

    // Data for the second player
    private String secondPlayer;
    private int secondScore;

    // Numbers of draws
    private int draws;

    // ==========GAME AI FIELDS==========

    // The number of steps to lookahead
    private int lookahead = 0;

    // The state of the board stack
    private int[] stack;

    // The formatter to print out results
    private DecimalFormat format = new DecimalFormat("###00.00");

    // ==========CONSTRUCTOR==========

    private ConnectFour() {

        // Initialize game
        mainFont = kDefaultFont;
        firstPlayer = kDefaultFirst;
        secondPlayer = kDefaultSecond;
        resetAll(kDefaultRows, kDefaultColumns, kDefaultN);

        // Initialize mouse handlers
        setEventHandlers();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create window
        frame = new JFrame();
        frame.setSize(kDefaultWidth, kDefaultHeight);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create window layout
        BorderLayout layout = new BorderLayout();
        Container container = frame.getContentPane();
        container.setLayout(layout);
        container.add(this, BorderLayout.CENTER);

        // Create menu bar
        frame.setJMenuBar(getMenuBar());

        // Center window on screen
        frame.setLocationRelativeTo(null);

        // Set the title of the window
        invalidateState();

        // Show the window
        frame.setVisible(true);
    }

    // ==========STATIC METHODS==========

    // Main Method; Just initialize the GameView
    public static void main(String[] args) {
        start();
    }

    static void start() {
        new ConnectFour();
    }

    // Read an integer from a BufferedReader
    private static int readInt(BufferedReader br) throws IOException {
        return Integer.parseInt(br.readLine());
    }

    // ==========GAME HELPER METHODS==========

    // Get the name of the current player
    private String getPlayerName() {
        if (player == kFirst) {
            return firstPlayer;
        } else if (player == kSecond) {
            return secondPlayer;
        }
        throw new IllegalStateException();
    }

    // Change to the winning state
    private void setWin() {
        if (player == kFirst) {
            firstScore++;
        } else if (player == kSecond) {
            secondScore++;
        }
        state = kWin;
    }

    // Change to the playing state
    private void setPlaying() {
        state = kPlaying;
    }

    // Change to the draw state
    private void setDraw() {
        draws++;
        state = kDraw;
    }

    // Reset the score and the board with size params
    private void resetAll(int rows, int columns, int n) {
        this.rows = rows;
        this.columns = columns;
        this.n = n;
        this.size = rows * columns;
        firstScore = 0;
        secondScore = 0;
        draws = 0;
        board = new int[rows][];
        restartBoard();
    }

    // Restart the game without resetting everything
    private void restartBoard() {
        for (int i = 0; i < rows; i++) {
            board[i] = new int[columns];
            for (int j = 0; j < columns; j++) {
                board[i][j] = 0;
            }
        }
        player = kFirst;
        setPlaying();
    }

    // ==========GAME LOGIC METHODS==========

    // Check if a board move is valid (i.e. the top row of the column is empty)
    // Column index is constrained elsewhere
    private boolean isValidMove(int[][] board, int column) {
        return board[rows - 1][column] == kEmpty;
    }

    // Check if the board is filled; if it is, signal a draw
    // It is filled when there are no valid moves available for every column
    private void checkBoardFilled() {
        for (int i = 0; i < columns; i++) {
            if (isValidMove(board, i)) {
                return;
            }
        }
        setDraw();
    }

    // Check for the number of pieces connected horizontally to the specified position
    // Returns true if the count is greater than n
    private boolean checkHorizontal(int row, int column, int[][] board, int player) {
        int count = 1;

        // Check in the negative direction
        int cMin = column;
        while (cMin > 0 && cMin < columns && board[row][cMin - 1] == player) {
            cMin--;
            count++;
        }

        // Check in the positive direction
        int cMax = column;
        while (cMax >= 0 && cMax < columns - 1 && board[row][cMax + 1] == player) {
            cMax++;
            count++;
        }

        if (count >= n) {
            // Cache the start and endpoint for indicator
            connectedMinRow = row;
            connectedMaxRow = row;
            connectedMinCol = cMin;
            connectedMaxCol = cMax;
            return true;
        }

        return false;
    }

    // Check for the number of pieces connected vertically to the specified position
    // Returns true if the count is greater than n
    private boolean checkVertical(int row, int column, int[][] board, int player) {
        int count = 1;

        // Check in the negative direction
        int rMin = row;
        while (rMin > 0 && rMin < rows && board[rMin - 1][column] == player) {
            rMin--;
            count++;
        }

        // Check in the positive direction
        int rMax = row;
        while (rMax >= 0 && rMax < rows - 1 && board[rMax + 1][column] == player) {
            rMax++;
            count++;
        }

        if (count >= n) {
            // Cache the start and endpoint for indicator
            connectedMinRow = rMin;
            connectedMaxRow = rMax;
            connectedMinCol = column;
            connectedMaxCol = column;
            return true;
        }

        return false;
    }

    // Check for the number of pieces connected diagonally to the specified position
    // Returns true if the count is greater than n
    private boolean checkDiagonal(int row, int column, int[][] board, int player) {
        int count = 1;

        // Check in the negative direction
        int rMin = row;
        int cMin = column;
        while (rMin > 0 && rMin < rows && cMin > 0 && cMin < columns && board[rMin - 1][cMin - 1] == player) {
            rMin--;
            cMin--;
            count++;
        }

        // Check in the positive direction
        int rMax = row;
        int cMax = column;
        while (rMax >= 0 && rMax < rows - 1 && cMax >= 0 && cMax < columns - 1 && board[rMax + 1][cMax + 1] == player) {
            rMax++;
            cMax++;
            count++;
        }

        if (count >= n) {
            // Cache the start and endpoint for indicator
            connectedMinRow = rMin;
            connectedMaxRow = rMax;
            connectedMinCol = cMin;
            connectedMaxCol = cMax;
            return true;
        }

        return false;
    }

    // Check for the number of pieces connected inverse diagonally to the specified position
    // Returns true if the count is greater than n
    private boolean checkInverseDiagonal(int row, int column, int[][] board, int player) {
        int count = 1;

        // Check in the negative direction
        int rMax = row;
        int cMin = column;
        while (rMax >= 0 && rMax < rows - 1 && cMin > 0 && cMin < columns && board[rMax + 1][cMin - 1] == player) {
            rMax++;
            cMin--;
            count++;
        }

        // Check in the positive direction
        int rMin = row;
        int cMax = column;
        while (rMin > 0 && rMin < rows && cMax >= 0 && cMax < columns - 1 && board[rMin - 1][cMax + 1] == player) {
            rMin--;
            cMax++;
            count++;
        }

        if (count >= n) {
            // Cache the start and endpoint for indicator
            connectedMinRow = rMax;
            connectedMaxRow = rMin;
            connectedMinCol = cMin;
            connectedMaxCol = cMax;
            return true;
        }

        return false;
    }

    // Inverts the player number
    private int invertPlayer(int player) {
        if (player == kFirst) return kSecond;
        if (player == kSecond) return kFirst;
        throw new IllegalStateException();
    }

    // Make a move at the specified column
    // This method assumes that the column is already a valid move
    // The player piece is moved to the bottom of the column, and is
    // then checked in all four directions for connections. If any
    // is true, change the state to win; Otherwise check for draw
    // If all checks fail, advance to the next player
    private void move(int column) {
        int row = rows - 1;
        while (row > 0 && board[row - 1][column] == kEmpty) {
            row--;
        }
        board[row][column] = player;

        // This is a short-circuit evaluation to keep efficiency
        if (checkHorizontal(row, column, board, player) ||
                checkVertical(row, column, board, player) ||
                checkDiagonal(row, column, board, player) ||
                checkInverseDiagonal(row, column, board, player)) {
            setWin();
        } else {
            checkBoardFilled();
        }

        // Advance to the next player
        if (state == kPlaying) {
            player = invertPlayer(player);
        }
    }

    // ==========GAME COMPUTER AI METHODS==========

    // Generate a computer move; then play it
    private void computerMove() {

        int k = size;

        // Create the stack
        stack = new int[columns];
        for (int i = 0; i < columns; i++) {
            int j = rows;
            while (j > 0 && board[j - 1][i] == kEmpty) {
                j--;
            }
            // Find # of steps already taken
            k = k - (rows - j);
            stack[i] = j;
        }

        // Calculate the lookahead steps
        lookahead = Math.min(size, k + 8);

        // Find the max score between [-1, 1]
        double maxScore = Double.NEGATIVE_INFINITY;

        // Keep track of the best column
        int best = -1;
        for (int i = 0; i < columns; i++) {
            // Make sure there is space available
            if (stack[i] < rows) {
                // Push the stack
                board[stack[i]][i] = this.player;
                stack[i]++;

                // Calculate next score and print it out
                double score = calculateScore(i, k + 1, this.player);

                System.out.println(i + " - " + format.format(score * 100) + "% ");

                // Pop the stack
                stack[i]--;
                board[stack[i]][i] = kEmpty;

                // Compare with max score
                if (score > maxScore) {
                    maxScore = score;
                    best = i;
                }
            }
        }

        System.out.println();

        // Move if there is a best column
        if (best != -1) {
            move(best);
        }
    }

    // Calculate a score in the range of [-1, 1] for a specified position
    private double calculateScore(int col, int k, int player) {

        // Get the previous move
        int row = stack[col] - 1;

        // Check win state
        if (checkHorizontal(row, col, board, player) ||
                checkVertical(row, col, board, player) ||
                checkDiagonal(row, col, board, player) ||
                checkInverseDiagonal(row, col, board, player)) {

            // If winning, return the highest score
            return 1.0;
        }

        // Recursion end condition
        if (k > lookahead) {
            return 0.0;
        }

        // Otherwise return an average of scores in the next move
        double sum = 0;

        boolean win = true;

        for (int i = 0; i < columns; i++) {
            int si = stack[i];
            if (si < rows) {
                // Push the stack
                board[si][i] = player;
                stack[i]++;

                // Calculate next score
                double score = -calculateScore(i, k + 1, invertPlayer(player));

                // Pop the stack
                stack[i]--;
                board[si][i] = kEmpty;

                if (score <= -1) {
                    return -1;
                }
                if (score < 1) {
                    win = false;
                }

                // Add to rolling count
                sum += score;
            }
        }

        if (win) {
            return 1;
        }

        // Return the average
        return sum / columns;
    }

    // ==========GAME IO METHODS==========

    // Convert data to a printable string
    public String toString() {
        StringBuilder buffer = new StringBuilder("Current Board: \n");

        // Append the headers
        for (int i = 1; i <= columns; i++) {
            buffer.append(i);
            buffer.append(i > 9 ? " " : "  ");
        }
        buffer.append('\n');

        // Append board data
        for (int i = board.length - 1; i >= 0; i--) {
            for (int j = 0; j < board[i].length; j++) {
                int it = board[i][j];
                // Append piece character
                if (it == kEmpty) buffer.append('.');
                else if (it == kFirst) buffer.append('r');
                else buffer.append('b');

                buffer.append("  ");
            }
            buffer.append("\n");
        }

        // Append game state
        String player = getPlayerName();
        buffer.append("Game State: ");

        if (state == kPlaying) buffer.append(player).append("'s turn");
        else if (state == kWin) buffer.append(player).append(" won the game");
        else buffer.append("The game is a draw");

        // Append score
        buffer.append('\n')
                .append("Score: ")
                .append(firstPlayer)
                .append('-')
                .append(firstScore)
                .append('|')
                .append(secondPlayer)
                .append('-')
                .append(secondScore)
                .append("|Draw-")
                .append(draws)
                .append('\n');

        return buffer.toString();
    }

    // Serialize this object into a string to be processed later
    private String serialize() {
        StringBuilder buffer = new StringBuilder();

        // Add metadata
        buffer
                .append(firstPlayer).append('\n')
                .append(firstScore).append('\n')
                .append(secondPlayer).append('\n')
                .append(secondScore).append('\n')
                .append(draws).append('\n')
                .append(rows).append('\n')
                .append(columns).append('\n')
                .append(n).append('\n')
                .append(player).append('\n')
                .append(state).append('\n');

        // Add board data
        for (int[] rows : board) {
            for (int value : rows) {
                buffer.append(value);
                buffer.append(',');
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }

    // Save the object into a file by writing the serialized text
    private void save(File file) throws IOException {
        String s = serialize();
        // Create a writer
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
        writer.print(s);
        writer.close();
    }

    // Load a file by trying to deserialize it
    // Reset to defaults if there are any errors
    private void load(File file) {
        try {
            deserializeFile(file);
        } catch (IOException e) {
            this.firstPlayer = "RED";
            this.secondPlayer = "BLUE";
            resetAll(kDefaultRows, kDefaultColumns, kDefaultN);
        }
    }

    // Deserialize the file and read into this object
    private void deserializeFile(File file) throws IOException {
        // Create a reader
        BufferedReader br = new BufferedReader(new FileReader(file));
        // Read all the metadata
        firstPlayer = br.readLine();
        firstScore = readInt(br);
        secondPlayer = br.readLine();
        secondScore = readInt(br);
        draws = readInt(br);
        rows = readInt(br);
        columns = readInt(br);
        board = new int[rows][];
        n = readInt(br);
        player = readInt(br);
        state = readInt(br);

        // Read the board using the dimensions given above
        int row = 0;
        String line;
        while ((line = br.readLine()) != null && row < rows) {
            // Split the input
            String[] data = line.split(",");

            // Check data size
            if (data.length != columns) throw new IllegalArgumentException("Data has wrong size");

            // Create new row array
            board[row] = new int[columns];
            for (int column = 0; column < columns; column++) {
                // Parse each column of the row
                board[row][column] = Integer.parseInt(data[column]);
            }
            row++;
        }

        // Check data size again
        if (row != rows) throw new IllegalArgumentException("Data has wrong size");
        br.close();
    }

    // ==========USER INTERFACE METHODS==========

    // Add mouse handlers to the Component
    private void setEventHandlers() {
        MouseMotionListener mouseMotionListener = new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
            }

            public void mouseMoved(MouseEvent e) {
                // Find the column where the mouse is; -1 by default
                int newCol = -1;

                // Check if the current state allows for hovering
                if (scale != 0.0 && state == kPlaying) {
                    int mouseX = e.getX();

                    // Calculate the min and max of the mouse range using columns
                    double min = x + 2 * scale;
                    double max = min + columns * 12 * scale;

                    // Check if mouse in range
                    if (mouseX >= min && mouseX <= max) {
                        // Interpolate to get the column number
                        newCol = (int) ((mouseX - min) / (12 * scale));
                    }
                }

                // Repaint only if the new column is not the current hoverColumn
                if (newCol != hoverColumn) {
                    hoverColumn = newCol;
                    repaint();
                }
            }
        };
        addMouseMotionListener(mouseMotionListener);

        MouseListener mouseListener = new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                // Check if the current state allows for clicking
                if (hoverColumn == -1 || state != kPlaying) {
                    return;
                }

                // Check that the move is valid before making the move
                if (isValidMove(board, hoverColumn)) {
                    move(hoverColumn);
                    repaint();
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        };
        addMouseListener(mouseListener);
    }

    // Creates the menu bar, with menu items and shortcuts
    private JMenuBar getMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Get the shortcut key from the toolkit
        int shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();

        JMenu menu = new JMenu("Menu");

        JMenuItem setPlayer1_ = new JMenuItem("Set Red Player");
        setPlayer1_.setAccelerator(KeyStroke.getKeyStroke('1'));
        setPlayer1_.addActionListener(e -> {
            // Ask for the name of the first player
            String s = JOptionPane.showInputDialog("Set Red Player", firstPlayer);
            // Set the first player if it's valid
            if (s.length() > 0 && !s.equals(firstPlayer)) {
                firstPlayer = s;
                invalidateState();
                repaint();
            }
        });
        menu.add(setPlayer1_);

        JMenuItem setPlayer2_ = new JMenuItem("Set Yellow Player");
        setPlayer2_.setAccelerator(KeyStroke.getKeyStroke('2'));
        setPlayer2_.addActionListener(e -> {
            // Ask for the name of the second player
            String s = JOptionPane.showInputDialog("Set Yellow Player", secondPlayer);
            // Set the second player if it's valid
            if (s.length() > 0 && !s.equals(secondPlayer)) {
                secondPlayer = s;
                invalidateState();
                repaint();
            }
        });
        menu.add(setPlayer2_);

        JMenuItem save_ = new JMenuItem("Save");
        save_.setAccelerator(KeyStroke.getKeyStroke('s'));

        save_.addActionListener(e -> {
            // Create a file chooser for saving
            FileDialog fileDialog = new FileDialog(frame, "Save", FileDialog.SAVE);

            fileDialog.setFile("*.txt");
            fileDialog.setVisible(true);

            String filename = fileDialog.getFile();
            String dir = fileDialog.getDirectory();

            // Check that file is seleted
            if (filename != null && dir != null) {
                // Try saving to the file
                try {
                    save(new File(dir, filename));
                } catch (IOException ignored) {
                }
            }
        });
        menu.add(save_);

        JMenuItem load_ = new JMenuItem("Load");
        load_.setAccelerator(KeyStroke.getKeyStroke('o'));

        load_.addActionListener(e -> {
            // Create a file chooser for loading
            FileDialog fileDialog = new FileDialog(frame, "Save", FileDialog.LOAD);
            fileDialog.setFile("*.txt");
            fileDialog.setVisible(true);
            String filename = fileDialog.getFile();
            String dir = fileDialog.getDirectory();

            // Check that file is seleted
            if (filename != null && dir != null) {
                // Try loading it into the file
                load(new File(dir, filename));
                invalidateState();
                repaint();
            }
        });
        menu.add(load_);

        JMenuItem restart_ = new JMenuItem("Restart");
        restart_.setAccelerator(KeyStroke.getKeyStroke('r'));

        restart_.addActionListener(e -> {
            // Restarts the board
            restartBoard();
            repaint();
        });
        menu.add(restart_);

        JMenuItem addRow_ = new JMenuItem("Add Row");
        addRow_.setAccelerator(KeyStroke.getKeyStroke('T', shortcut));

        addRow_.addActionListener(e -> {
            // Add 1 to rows and restart
            resetAll(rows + 1, columns, n);
            invalidateState();
            repaint();
        });
        menu.add(addRow_);

        JMenuItem addCol_ = new JMenuItem("Add Column");
        addCol_.setAccelerator(KeyStroke.getKeyStroke('Y', shortcut));

        addCol_.addActionListener(e -> {
            // Add 1 to columns and restart
            resetAll(rows, columns + 1, n);
            invalidateState();
            repaint();
        });
        menu.add(addCol_);

        JMenuItem addN_ = new JMenuItem("Add N");
        addN_.setAccelerator(KeyStroke.getKeyStroke('N', shortcut));

        addN_.addActionListener(e -> {
            // Add 1 to N and restart
            resetAll(rows, columns, n + 1);
            invalidateState();
            repaint();
        });
        menu.add(addN_);

        JMenuItem resetAll_ = new JMenuItem("Reset All");
        resetAll_.setAccelerator(KeyStroke.getKeyStroke('0', shortcut));

        resetAll_.addActionListener(e -> {
            // Reset to original and restart
            resetAll(kDefaultRows, kDefaultColumns, kDefaultN);
            invalidateState();
            repaint();
        });
        menu.add(resetAll_);

        JMenuItem print_ = new JMenuItem("Print to Standard Output");
        print_.setAccelerator(KeyStroke.getKeyStroke('p'));

        print_.addActionListener(e -> {
            // Print this object
            System.out.println(ConnectFour.this);
        });
        menu.add(print_);

        JMenuItem badComputer_ = new JMenuItem("Bad Computer Move");
        badComputer_.setAccelerator(KeyStroke.getKeyStroke('b'));

        badComputer_.addActionListener(e -> {
            // Check the current state of the game
            if (state != kPlaying) return;

            // Finds an array of available moves
            int[] available = new int[columns];
            int i = 0;
            for (int j = 0; j < columns; j++) {
                if (isValidMove(board, j)) {
                    available[i] = j;
                    i++;
                }
            }

            // Randomly select a new move
            int newMove = (int) (Math.random() * i);

            // Move there
            move(available[newMove]);
            repaint();
        });
        menu.add(badComputer_);

        JMenuItem goodComputer_ = new JMenuItem("Good Computer Move");
        goodComputer_.setAccelerator(KeyStroke.getKeyStroke('g'));

        goodComputer_.addActionListener(e -> {
            // Check the current state of the game
            if (state != kPlaying) return;

            try {
                // Runs the computation on a new thread
                // Mouse hover will sync graphics to show permutations
                new Thread(() -> {
                    computerMove();
                    SwingUtilities.invokeLater(this::repaint);
                }).start();

            } catch (Exception ex) {
                System.err.println(ConnectFour.this);
                ex.printStackTrace();
            }
        });
        menu.add(goodComputer_);

        menuBar.add(menu);
        return menuBar;
    }

    // Invalidates computed state of the view
    private void invalidateState() {
        // Reset the width and height so it will be picked up on paint
        lastWidth = 0;
        lastHeight = 0;
        hoverColumn = -1;

        // Reset the title of the window
        frame.setTitle("Connect " + n + " | " +
                rows + " by " + columns + " | " +
                firstPlayer + " vs. " + secondPlayer);
    }

    // Paints the game view onto the screen
    public void paint(Graphics g) {

        // Get a casted Graphics2D
        Graphics2D g2 = (Graphics2D) g;

        // Set rendering parameters
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Get view dimensions
        int viewWidth = getWidth();
        int viewHeight = getHeight();

        // Clear the background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, viewWidth, viewHeight);

        // Check if the window has resized; if so, recompute coordinates
        if (viewWidth != lastWidth || viewHeight != lastHeight) {

            // Find the min and max of viewport dimensions
            double minWidth = columns * 12 + 4.0;
            double maxWidth = 8 * minWidth;

            double minHeight = (rows + 1) * 12 + 4.0;
            double maxHeight = 8 * minHeight;

            // return if window is too small to draw properly
            if (viewWidth < minWidth || viewHeight < minHeight) {
                scale = 0.0;
                return;
            }

            // Compute the based on the view dimensions
            scale = Math.min(viewHeight / minHeight, viewWidth / minWidth);

            // Limit scale if view dimensions exceed the max dimensions
            if (viewWidth > maxWidth) {
                scale = Math.min(scale, 8);
            }
            if (viewHeight > maxHeight) {
                scale = Math.min(scale, 8);
            }

            // Compute viewport dimensions using scale
            width = scale * minWidth;
            height = scale * minHeight;

            // Find the offset translation so that the viewport is centered
            x = (viewWidth - width) / 2.0;
            y = (viewHeight - height) / 2.0;

            // Derive the font size according to the scale
            mainFont = mainFont.deriveFont((float) (int) (scale * 3.5));

            lastWidth = viewWidth;
            lastHeight = viewHeight;
        }

        // Draw the gray background
        RoundRectangle2D bg1 = new RoundRectangle2D.Double();
        bg1.setRoundRect(x, y, width, height, scale * 4, scale * 4);

        g.setColor(kLightGray);
        g2.fill(bg1);

        // Create the message
        String message;
        if (state == kPlaying) message = getPlayerName() + "'s turn";
        else if (state == kWin) message = getPlayerName() + " won the game!!!";
        else message = "The game is a draw";

        message += " | " + firstPlayer + ":" + secondPlayer + ":Draws = " + firstScore + ":" + secondScore + ":" + draws;

        // Use measured bounds to center the message
        Rectangle2D bounds = mainFont.getStringBounds(message, g2.getFontRenderContext());

        g.setColor(Color.BLACK);
        g.setFont(mainFont);

        g.drawString(message, (int) (x + width / 2 - bounds.getWidth() / 2.0),
                (int) (y + scale * 6 + bounds.getHeight() / 2.0));

        // Draw the dark cyan background for the board, offsetting all sides by 1 * scale
        RoundRectangle2D bg2 = new RoundRectangle2D.Double();

        bg2.setRoundRect(x + scale, y + scale * (12 + 1),
                scale * (columns * 12 + 2), scale * (rows * 12 + 2),
                scale * 4, scale * 4);

        g.setColor(kDarkCyan);
        g2.fill(bg2);

        // Draw all the checker pieces
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                Ellipse2D ellipse = new Ellipse2D.Double();

                // Compute the absolute position of the piece
                ellipse.setFrame(x + scale * (12 * i + 3), y + scale * (12 * j + 12 + 3),
                        scale * 10, scale * 10);

                // Get the piece using inverted index to switch direction
                int piece = board[rows - 1 - j][i];

                // Resolve the color of the piece
                g.setColor(piece == kEmpty ? Color.WHITE : (piece == kFirst ? kDarkRed : kDarkYellow));
                g2.fill(ellipse);
            }
        }

        // Draw an indicater if a column is being hovered
        if (hoverColumn != -1) {
            int row = rows - 1;
            if (board[row][hoverColumn] == 0) {
                while (row > 0 && board[row - 1][hoverColumn] == 0) {
                    row--;
                }

                // Resolve the color of the piece
                g.setColor(player == kFirst ? kDarkRed :
                        (player == kSecond ? kDarkYellow : Color.BLUE));

                Ellipse2D hovierIndicator = new Ellipse2D.Double();

                // Compute the absolute position of the indicater
                hovierIndicator.setFrame(x + scale * (12 * hoverColumn + 3 + 3.5),
                        y + scale * (12 * (rows - 1 - row) + 12 + 3 + 3.5), scale * 3, scale * 3);

                g2.fill(hovierIndicator);
            }
        }

        // Draw a line showing the connected pieces if winning
        if (state == kWin) {
            g2.setStroke(new BasicStroke((float) scale));
            g.setColor(Color.PINK);
            g2.draw(new Line2D.Double(x + scale * (12 * connectedMinCol + 3 + 5),
                    y + scale * (12 * (rows - 1 - connectedMinRow) + 12 + 3 + 5),
                    x + scale * (12 * connectedMaxCol + 3 + 5),
                    y + scale * (12 * (rows - 1 - connectedMaxRow) + 12 + 3 + 5)));
        }
    }
}