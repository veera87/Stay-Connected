
package game;

import intelligence.GameTree;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;

public class GameUI extends Frame {

    public static int WIN_WIDTH = 650;
    public static int WIN_HEIGHT = 650;
    public static int SQUARE_WIDTH = 65;
    public static int BOARD_LEFT = 65;
    public static int BOARD_TOP = 65;
    public int board_size;
    private static int NO_CLICK = 0;
    private static int SINGLE_CLICK = 1;
    private static String ERROR_MESSAGE = "INVALID MOVE";
    private static String COMP_WIN_MESSAGE = "COMPUTER WINS!!";
    private static String USER_WIN_MESSAGE = "PLAYER WINS!!";
    private static String DRAW_MESSAGE = "GAME DRAW!!";
    private static String THINKING_MESSAGE = "THINKING....";
    private GameState gs;
    private int click;
    private int i1,  j1,  won = 4;
    private boolean valid_move = true;
    private boolean gameover = false;
    private Image arrows[];
    public boolean play = true;

    public GameUI(GameState g) {
        gs = g;
        board_size = g.getBoardSize();
        Toolkit tk = Toolkit.getDefaultToolkit();
        MediaTracker mt = new MediaTracker(this);

        arrows = new Image[8];
        int k = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i != 1 || j != 1) {
                    arrows[k] = tk.getImage("" + ((i * 10) + j) + ".gif");
                    mt.addImage(arrows[k], 1);
                    k++;
                }
            }
        }
        for (int i = 0; i < arrows.length; i++) {
            try {
                mt.waitForID(i);
            } catch (InterruptedException ex) {
                System.err.println("Error in loading image");
            }
        }

        addWindowListener(new MyWindowAdapter(this));
        addMouseListener(new MyMouseAdapter(this));        
        setBounds(50, 50, WIN_WIDTH, WIN_HEIGHT);
        setResizable(false);
        setTitle("Test");
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        drawBoard(g);
        drawGameState(g);
        drawHighlight(g);
        g.setColor(Color.BLACK);
        if (!valid_move) {
            drawMessage(g, ERROR_MESSAGE);
        }
        valid_move = true;
        if (gameover) {
            if (won == GameState.COMP_WON) {
                drawMessage(g, COMP_WIN_MESSAGE);
            } else if (won == GameState.USER_WON) {
                drawMessage(g, USER_WIN_MESSAGE);
            } else if (won == GameState.DRAW) {
                drawMessage(g, DRAW_MESSAGE);
            }
        } else if (!play) {
            drawMessage(g, THINKING_MESSAGE);
        }
    }
    
    public void drawMessage(Graphics g, String str) {
        g.setFont(new Font(Font.SERIF, Font.BOLD, 20));
        g.drawString(str, BOARD_LEFT + 200, BOARD_TOP + board_size * SQUARE_WIDTH + 20);
    }

    public void setGameOver(boolean a) {
        gameover = a;
    }

    public void setWon(int a) {
        won = a;
    }

    private void drawBoard(Graphics g) {
        g.setColor(Color.BLACK);
        for (int i = 0; i < board_size; i++) {
            for (int j = 0; j < board_size; j++) {
                g.drawRect(BOARD_LEFT + (i * SQUARE_WIDTH), BOARD_TOP + (j * SQUARE_WIDTH), SQUARE_WIDTH, SQUARE_WIDTH);
            }
        }
    }

    private void drawGameState(Graphics g) {
        for (int i = 0; i < board_size; i++) {
            for (int j = 0; j < board_size; j++) {
                if (gs.getCoinAt(i, j) == GameState.USER_COIN) {
                    g.setColor(Color.RED);
                    if (click == SINGLE_CLICK && i == i1 && j == j1) {
                        g.fillOval(BOARD_LEFT + (i * SQUARE_WIDTH) + 20, BOARD_TOP + (j * SQUARE_WIDTH) + 20, SQUARE_WIDTH - 40, SQUARE_WIDTH - 40);
                    } else {
                        g.fillOval(BOARD_LEFT + (i * SQUARE_WIDTH) + 10, BOARD_TOP + (j * SQUARE_WIDTH) + 10, SQUARE_WIDTH - 20, SQUARE_WIDTH - 20);
                    }
                } else if (gs.getCoinAt(i, j) == GameState.COMP_COIN) {
                    g.setColor(Color.GREEN);
                    if (click == SINGLE_CLICK && i == i1 && j == j1) {
                        g.fillOval(BOARD_LEFT + (i * SQUARE_WIDTH) + 20, BOARD_TOP + (j * SQUARE_WIDTH) + 20, SQUARE_WIDTH - 40, SQUARE_WIDTH - 40);
                    } else {
                        g.fillOval(BOARD_LEFT + (i * SQUARE_WIDTH) + 10, BOARD_TOP + (j * SQUARE_WIDTH) + 10, SQUARE_WIDTH - 20, SQUARE_WIDTH - 20);
                    }
                }
            }
        }
    }

    private void drawHighlight(Graphics g) {
        if (click == SINGLE_CLICK) {
            g.setColor(new Color(0, 0, 255, 150));
            int x = BOARD_LEFT + (i1 * SQUARE_WIDTH);
            int y = BOARD_TOP + (j1 * SQUARE_WIDTH);
            int k = 0;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (i != 1 || j != 1) {                        
                        g.drawImage(arrows[k], x + i * SQUARE_WIDTH / 3 + 2, y + j * SQUARE_WIDTH / 3 + 2, SQUARE_WIDTH / 3 - 4, SQUARE_WIDTH / 3 - 4, null);
                        k++;
                    }
                }
            }
        }
    }

    public void mouseClicked(int x, int y) {
        if (!gameover && play) {            
            if (click == NO_CLICK) {
                int i = (x - GameUI.BOARD_TOP) / GameUI.SQUARE_WIDTH;
                int j = (y - GameUI.BOARD_LEFT) / GameUI.SQUARE_WIDTH;
                if (i >= 0 && i < board_size && j >= 0 && j < board_size) {                    
                    if (gs.getCoinAt(i, j) == GameState.USER_COIN) {                        
                        click = SINGLE_CLICK;
                        i1 = i;
                        j1 = j;
                    }
                }
            } else if (click == SINGLE_CLICK) {
                int x1 = (x - (BOARD_LEFT + (i1 * SQUARE_WIDTH))) / (SQUARE_WIDTH / 3);
                int y1 = (y - (BOARD_TOP + (j1 * SQUARE_WIDTH))) / (SQUARE_WIDTH / 3);
                if (x1 >= 0 && x1 <= 2 && y1 >= 0 && y1 <= 2 && !(x1 == 1 && y1 == 1)) {                    
                    valid_move = gs.move(i1, j1, (x1 * 10) + y1);
                    won = gs.whoWon();
                    if (won != GameState.NO_WIN) {
                        gameover = true;
                        if (won == GameState.COMP_WON) {
                            JOptionPane.showMessageDialog(this, "COMPUTER WINS !!");
                        } else if (won == GameState.USER_WON) {
                            JOptionPane.showMessageDialog(this, "PLAYER WINS !!");
                        } else if (won == GameState.DRAW) {
                            JOptionPane.showMessageDialog(this, "GAME DRAW !!");
                        }
                    } else if (valid_move) {
                        play = false;
                        new GameTreeThread(this);
                    }
                }
                click = NO_CLICK;
            }
        }

    }

    public static void main(String args[]) {
        GameUI gui = new GameUI(new GameState(8));
    }

    public GameState getGameState() {
        return gs;
    }

    public void setGameState(GameState a) {
        gs = a;
    }
}

class MyWindowAdapter extends WindowAdapter {

    GameUI gui;

    MyWindowAdapter(GameUI g) {
        gui = g;
    }

    @Override
    public void windowClosing(WindowEvent we) {
        System.exit(0);
    }
}

class MyMouseAdapter extends MouseAdapter {

    GameUI gui;

    MyMouseAdapter(GameUI g) {
        gui = g;
    }

    @Override
    public void mousePressed(MouseEvent me) {
        gui.mouseClicked(me.getX(), me.getY());
        gui.repaint();
    }
}

class GameTreeThread implements Runnable {

    Thread t;
    GameState final_state;
    GameUI gui;

    GameTreeThread(GameUI g) {
        gui = g;
        final_state = null;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        GameTree gt = new GameTree(gui.getGameState());
        final_state = gt.getNextGameState();
        gui.setGameState(final_state);
        gui.play = true;
        int won = gui.getGameState().whoWon();
        gui.repaint();
        if (won != GameState.NO_WIN) {
            gui.setGameOver(true);
            gui.setWon(won);
            if (won == GameState.COMP_WON) {
                JOptionPane.showMessageDialog(gui, "COMPUTER WINS !!");
            } else if (won == GameState.USER_WON) {
                JOptionPane.showMessageDialog(gui, "PLAYER WINS !!");
            } else if (won == GameState.DRAW) {
                JOptionPane.showMessageDialog(gui, "GAME DRAW !!");
            }
        }        
    }
}