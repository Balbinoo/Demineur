import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

/**
 * Graphical User Interface (View)
 * @author Rodrigo Balbino
 * @version 0.0
 */

class Case extends JPanel implements MouseListener {
    private String txt;  
    private int row;
    private int col;
    private final static int DIM = 60; 
    private boolean leftClicked = false;
    private boolean rightClicked = false;
    private final Font customFont = new Font("Arial", Font.BOLD, 20);
    private static Champ champ;
    private static App app;
    private static int countCase = 0;

    public Case(int row, int col, Champ champ, App app) {
        this.champ = champ;
        this.app = app;
        this.txt = " ";
        this.row = row;
        this.col = col;

        caseGeneralConfig();        
    }

    public void countCases(){
        countCase++;
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
        gc.setFont(customFont);  
        FontMetrics fm = gc.getFontMetrics();
        int textWidth = fm.stringWidth(txt);
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
            paintLeftClickedIsMine(gc);
        else 
            paintLeftClickedIsNotMine(gc, x, y);
        
        gc.drawString(txt, x, y); 
    }


    public void propagation(int row, int col){


    }

    public void paintLeftClickedIsMine(Graphics gc){
        txt = "M";  
        setBackground(Color.red);  
        gc.setColor(Color.white); 
    }

    public void paintLeftClickedIsNotMine(Graphics gc, int x, int y){
        int numberOfMines = champ.nbMinesAround(row, col);
        txt = Integer.toString(numberOfMines); 

        //if(numberOfMines == 0)
        //propagation(x, y);
        setBackground(Color.lightGray);  
        gc.setColor(Color.black);
    }

    public void paintRightClicked(Graphics gc, int x, int y){
        setBackground(Color.magenta);  
        txt = "F";  
        gc.setColor(Color.white);  
        gc.drawString(txt, x, y); 
    }

    public void paintSecondRightClicked(){
        setBackground(Color.green);  
        txt = "";  
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (!leftClicked) {  
                leftClicked = true;  
                rightClicked = false;
                countCases();
                
                repaint();

                verifyGameOver();   

                verifyGameWon();
            }  

        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (!leftClicked) {  
                rightClicked = !rightClicked; 
                repaint();
            }
        }
    }

    public void verifyGameOver(){
        if (champ.isMine(row, col)) 
            champ.game_over(app, this);
    }

    public void verifyGameWon(){
        if(countCase == freeCases())                   
            champ.game_won(app,this);
    }

    public int freeCases(){
        return (champ.get_width()*champ.get_height()) - champ.get_numeroMines(champ.get_level());
    }

    public void mouseEntered (MouseEvent e) {}
    public void mouseReleased (MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}    
    public void mouseExited(MouseEvent e) {}
}
