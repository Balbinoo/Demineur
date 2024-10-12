import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

class Case extends JPanel implements MouseListener {

    private static Champ champ;
    private static App app;
    private static Compteur comp;
    private static int countCase = 0;
    private final static int DIM = 60; 
    private String txt;  
    private int row;
    private int col;
    private boolean leftClicked = false;
    private boolean rightClicked = false;
    private Gui gui;

    public Case() {}

    public Case(Gui gui, int row, int col, Champ champ, App app, Compteur comp) {
        this.gui = gui;
        this.champ = champ;
        this.app = app;
        this.comp = comp;
        this.txt = " ";
        this.row = row;
        this.col = col;

        caseGeneralConfig();        
    }

    public Case(int row, int col, Champ champ, App app, boolean leftClicked) {
        this.champ = champ;
        this.app = app;
        this.txt = " ";
        this.row = row;
        this.col = col;
        this.leftClicked = leftClicked;

        caseGeneralConfig();        
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public void setTxt(String newTxt) {
        txt = newTxt;
    }

    public String getTxt() {
        return txt;
    }

    public void countCases() {
        countCase++;
    }

    public int get_countCase() {
        return countCase;
    }

    public void resetCountCases() {
        countCase = 0;
    }

    public void caseGeneralConfig() {
        setBackground(Color.green); 
        setBorder(BorderFactory.createLineBorder(Color.black));
        setPreferredSize(new Dimension(DIM, DIM)); 
        addMouseListener(this); 
    }

    @Override
    public void paintComponent(Graphics gc) {     
        super.paintComponent(gc);  
        gc.setFont(new Font("Arial", Font.BOLD, 20));  
        FontMetrics fm = gc.getFontMetrics();
        int textWidth = fm.stringWidth(getTxt());
        int textHeight = fm.getAscent();         
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() + textHeight) / 2 - fm.getDescent(); 
    
        if (leftClicked) 
            paintLeftClicked(gc,x,y);
        if (rightClicked)   
            paintRightClicked(gc, x, y);
        else if (!rightClicked && !leftClicked)
            paintSecondRightClicked();        
    }

    public void paintLeftClicked(Graphics gc, int x, int y) {
        if (champ.isMine(row, col)) 
            paintIsMine(gc);
        else 
            isNotMine(gc, getRow(), getCol());
        
        gc.drawString(getTxt(), x, y); 
    }

    public void paintIsMine(Graphics gc) {
        champ.setRevealed(row, col);
        setTxt("M");
        setBackground(Color.red);  
        gc.setColor(Color.white); 
    }

    public void isNotMine(Graphics gc, int row, int col) {
        int numberOfMines = champ.nbMinesAround(row, col);        
        champ.setRevealed(row, col);

        if (numberOfMines == 0) {
            gc.setColor(Color.white);
            paintIsNotMine(gc, numberOfMines);   
            propagation(row, col);  // Continue propagation for zero mines
        } else {
            paintIsNotMine(gc, numberOfMines);    
        }
    }

    public void paintIsNotMine(Graphics gc, int numberOfMines) {
        setBackground(Color.lightGray);  
        gc.setColor(Color.black);

        if(numberOfMines == 0)
            setTxt(" ");
        else
            setTxt(Integer.toString(numberOfMines));
    }

    public void paintRightClicked(Graphics gc, int x, int y) {
        setBackground(Color.magenta);  
        setTxt("F");
        gc.setColor(Color.white);  
        gc.drawString(getTxt(), x, y); 
    }

    public void paintSecondRightClicked() {
        setBackground(Color.green);    
        setTxt(" ");
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (!leftClicked) {  
                if (get_countCase() == 0) {
                    if(app.isServerOn()){
                        System.out.println("First click Client");
                        app.sendtoServer(row,col);
                    }else{
                        System.out.println("First click");
                        champ.init(row, col, champ.get_level());        
                        champ.display();  
                    }
                } else {
                    if(app.isServerOn()){
                        //paintCaseServeur();
                        System.out.println("New click Client");
                        app.sendtoServer(row,col);
                    }                 
                    
                }
                countCases(); 
                

                if (champ.nbMinesAround(row, col) == 0 && !app.isServerOn()) {
                    propagation(row, col);  // Propagate if zero mines around            
                }

                leftClicked = true;  
                rightClicked = false;
                
                repaint();

                verifyGameStatus(gui);
            }  
        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (!leftClicked) {  
                rightClicked = !rightClicked; 
                repaint();
            }
        }
    }

    public void verifyGameStatus(Gui gui) {
        if (champ.isMine(row, col)) 
            champ.game_over(comp, app, gui, this);
        if (countCase == freeCases())                   
            champ.game_won(comp, app, gui, this);
    }

    public int freeCases() {
        return (champ.get_width() * champ.get_height()) - champ.get_numeroMines(champ.get_level());
    }

    public void propagation(int row, int col) {

        if (row < 0 || row >= champ.get_width() || col < 0 || col >= champ.get_height() || champ.isRevealed(row, col)) {
            return;
        }
    
        if (champ.isMine(row, col)) {
            return;
        }
    
        Case currentCase = gui.getCase(row, col);
        currentCase.leftClicked = true;
        champ.setRevealed(row, col);

        int minesAround = champ.nbMinesAround(row, col);

        currentCase.setBackground(Color.lightGray);
        currentCase.setTxt(Integer.toString(minesAround));
        currentCase.repaint();
    
        if (minesAround == 0) {
            for (int i = row - 1; i <= row + 1; i++) {
                for (int j = col - 1; j <= col + 1; j++) {
                    if (i != row || j != col) {  
                        
                        propagation(i, j);
                    }
                }
            }
        }

        countCases();
    }

    public void paintCaseServeur(int x, int y){
        Case currentCase = gui.getCase(x, y);
        currentCase.leftClicked = true;
        champ.setRevealed(x, y);

        int minesAround = champ.nbMinesAround(x, y);

        //currentCase.setBackground(Color.ORANGE);
        currentCase.setTxt(Integer.toString(minesAround));
        currentCase.repaint();
    }

    public void mouseEntered(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}    
    public void mouseExited(MouseEvent e) {}
}
