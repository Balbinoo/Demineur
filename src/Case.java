import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

/**
 * Graphical User Interface (View)
 * @author Rodrigo Balbino
 * @version 0.0
 */

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

    public Case(){}

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

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public void setTxt(String newTxt){
        txt = newTxt;
    }

    public String getTxt(){
        return txt;
    }

    public void countCases(){
        countCase++;
    }

    public int get_countCase(){
        return countCase;
    }

    public void resetCountCases(){
        countCase = 0;
    }

    public void caseGeneralConfig(){
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

    public void paintLeftClicked(Graphics gc, int x, int y){
        if (champ.isMine(row, col)) 
            paintIsMine(gc);
        else 
            isNotMine(gc, getRow(), getCol());
        
        gc.drawString(getTxt(), x, y); 
    }

    public void paintIsMine(Graphics gc){
        setTxt("M");
        setBackground(Color.red);  
        gc.setColor(Color.white); 
    }

    public void isNotMine(Graphics gc, int row, int col){
        //System.out.println("Inside Paint 0 x :" + x + " y: " + y);
        int numberOfMines = champ.nbMinesAround(row, col);

        if(numberOfMines == 0)
            propagation(getRow(), getCol(), gc);
        else
            paintIsNotMine(gc, numberOfMines);    
    }

    public void paintIsNotMine(Graphics gc, int numberOfMines){
        setBackground(Color.lightGray);  
        gc.setColor(Color.black);
        setTxt(Integer.toString(numberOfMines));
    }

    public void propagation(int row, int col, Graphics gc){
        //System.out.println("Do the propagation");
        Case currentCase = this;
        paintIsNotMine(gc, 0);  

        for (int i = row - 1; i <= row + 1; i++) {
            for (int j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < champ.get_width() && j >= 0 && j < champ.get_height()) {
                    if (champ.isMine(i, j))
                        continue;
                        
                        //Case currentCase2 = getCas();
                       // Case currentCase = champ.cas[i][j];  // Assuming you have a 2D array of `Case` objects
                            
                // If there are no mines around this case, change its appearance
                if (0 == champ.nbMinesAround(i, j)) {
                    currentCase.setBackground(Color.lightGray);  // Change the background
                    currentCase.setTxt("0");  // Update the text of the case
                    currentCase.repaint();  // Request a repaint to reflect the changes
                }
              }
            }
        }
    }

    public void paintRightClicked(Graphics gc, int x, int y){
        setBackground(Color.magenta);  
        setTxt("F");
        gc.setColor(Color.white);  
        gc.drawString(getTxt(), x, y); 
    }

    public void paintSecondRightClicked(){
        setBackground(Color.green);    
        setTxt(" ");
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (!leftClicked) {  

                if(get_countCase() == 0){
                    champ.init(row,col,champ.get_level());        
                    champ.display();  
                    System.out.println("First click");
                }

                leftClicked = true;  
                rightClicked = false;
                countCases();
                repaint();
                //verifyGameOver(gui);   
                //verifyGameWon(gui);
                verifyGameStatus(gui);
            }  

        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (!leftClicked) {  
                rightClicked = !rightClicked; 
                repaint();
            }
        }
    }

    public void verifyGameStatus(Gui gui){
        if (champ.isMine(row, col)) 
            champ.game_over(comp, app, gui, this);
        if(countCase == freeCases())                   
            champ.game_won(comp, app, gui, this);
    }

    public int freeCases(){
        return (champ.get_width()*champ.get_height()) - champ.get_numeroMines(champ.get_level());
    }

    public void mouseEntered (MouseEvent e) {}
    public void mouseReleased (MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}    
    public void mouseExited(MouseEvent e) {}
}
