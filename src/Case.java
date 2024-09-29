import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

class Case extends JPanel implements MouseListener {
    private String txt = "";  
    private int row;
    private int col;
    private final static int DIM = 40; 
    private boolean leftClicked = false;
    private boolean rightClicked = false;
    private final Font customFont = new Font("Arial", Font.BOLD, 20);
    private static Champ champ;
    private static App app;

    public Case(String string, int row, int col, Champ champ, App app) {
        this.champ = champ;
        this.app = app;
        this.txt = string;
        this.row = row;
        this.col = col;
        
        blockGeneralConfig();        
    }


    public void blockGeneralConfig(){
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
        if (champ.isMine(row, col)) {
            txt = "M";  
            setBackground(Color.red);  
            gc.setColor(Color.white); 
            
        } else {
            txt = Integer.toString(champ.nbMinesAround(row, col)); 
            setBackground(Color.lightGray);  
            gc.setColor(Color.black);
        }
        gc.drawString(txt, x, y); 
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
                repaint();

                if (champ.isMine(row, col)) {
                    champ.game_over(app, this);
                }             
            }  

        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (!leftClicked) {  
                rightClicked = !rightClicked; 
                repaint();
            }
        }
    }

    public void mouseEntered (MouseEvent e) {}
    public void mouseReleased (MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}    
    public void mouseExited(MouseEvent e) {}
}
