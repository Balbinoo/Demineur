import java.util.Random;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*; 

/**
 * Magnifique programme
 * @author  Rodrigo Balbino
 * @version 0.0
 */
public class Champ implements Serializable {

    private static final long serialVersionUID = 1L;
    private int score;  
    static boolean [][] tabMines ;
    static boolean [][] tabRevealed;
    private static  int def_width  = 0;
    private static  int def_height  = 0;
    private static  int def_customWidth  = 0;
    private static  int def_customHeight  = 0;

    //private final static  int DEF_NDMINES = 2 ;
    //private Compteur compt;
    private int level = 0;

    Random random = new Random();

    private int [] tabSize = {5,10,20} ;
    private int [] tabNMines = {4,15,40,30} ;

    public Champ( ) {
        tabMines = new boolean[get_height()][get_width()]; // do not erase this
        tabRevealed = new boolean[get_height()][get_width()]; // do not erase this
    }

    public void setCustomTabNMines(int mines){
        tabNMines[3] = mines;
    }

    public boolean isRevealed(int x, int y) {
        return tabRevealed[x][y];  
    }

    public void setRevealed(int x, int y) {
        tabRevealed[x][y] = true; 
    }

    public void countScore(){
        score++;    
    }

    public int getCountScore(){
        return score;    
    }

    public int get_width(){
        return (this.get_level() == 3) ? get_CustomWidth() : tabSize[def_width];
}

    public int get_height(){
      return (this.get_level() == 3) ? get_CustomHeight() : tabSize[def_height];
    }

    public int get_numeroMines(int level){
        return tabNMines[level];
    }

    public void set_height(int level){
        def_height = level;
    }

    public void set_width(int level){
        def_width = level;
    }


    public void set_CustomHeight(int customheight){
        def_customHeight = customheight;
    }

    public void set_CustomWidth(int customWidth){
        def_customWidth = customWidth;
    }

    public int get_CustomHeight(){
        return def_customHeight;
    }

    public int get_CustomWidth(){
        return def_customWidth;
    }

    public int get_level(){
        return level;
    }

    public void set_level(int newLevel){
        level = newLevel;
    }

    public void set_levelComboBox(int levelComboBox){
        def_height = levelComboBox;
        def_width = levelComboBox;
    }

     void init(int startX, int startY, int level) {
        //System.out.println("what level?"+level);

        //System.out.println("get_numeroMines(level)"+this.get_numeroMines(level));

            for (int n = this.get_numeroMines(level) ; n != 0; ) {  
                int x = random.nextInt(get_width());         
                int y = random.nextInt(get_height());    
    
                if (!(tabMines[x][y] || (x == startX && y == startY))) {
                    tabMines[x][y] = true;
                    n--;
                }
            }

        System.out.println("INIT - Did it make to the end?");
    }

    void display() {

        for (int i = 0; i < this.get_width(); i++) {
            for (int j = 0; j < this.get_height(); j++) {  
                if (isMine(i, j))
                    System.out.print('x');
                else 
                    System.out.print(nbMinesAround(i, j));
            }
            System.out.println();
        }
    }

    int nbMinesAround(int x, int y) {
        int n = 0; 
        for (int i = x - 1; i <= x + 1; i++) {
            for (int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i < get_width() && j >= 0 && j < get_height()) {
                    if (i == x && j == y)
                        continue;                    
                    if (isMine(i, j)) 
                        n++;                
                }
            }
        }
        return n;  
    }
    

    public void game_won(Compteur comp, App app, Gui gui, Case cas) {
          
        resetGame( gui, comp, cas);
        System.out.println("GAMEWON - countCase: "+cas.get_countCase());
        int choice = gui.showOptionDialog(cas, "You Won!", "Good Job", "info");

        if (choice == JOptionPane.YES_OPTION) 
            app.newPartie(this.get_level(), comp);
            
        else if (choice == JOptionPane.NO_OPTION) 
            app.quit();
         
    }

    public void game_over(Compteur comp, App app, Gui gui, Case cas) {

        resetGame(gui, comp, cas);
        System.out.println("GAMEOVER - countCase: "+cas.get_countCase());

        int choice = gui.showOptionDialog(cas,  "Game Over","You're a Loser!", "Lose");
        
        if (choice == JOptionPane.YES_OPTION)
            app.newPartie(this.get_level(), comp);
            
        else if (choice == JOptionPane.NO_OPTION) 
            app.quit();
        
    }

    public void resetGame(Gui gui, Compteur comp, Case cas){
        comp.stop();
        cas.resetCountCases();
        gui.setLabelScore(comp.getScore());
        gui.revalidate();  
        gui.repaint(); 
    }

    public boolean isMine (int x , int y){
        return tabMines[x][y] ;
    }

    public boolean[][] get_tabMines(){
        return tabMines;
    }

    public boolean[][] get_tabRevealed(){
        return tabRevealed;
    }

    public void set_tabMines(boolean [][]tabMinesFromServer){
        tabMines = tabMinesFromServer;
        System.out.println("CHAMP CLIENT - It did setMines");
    }

    public void set_tabRevealed(boolean [][]tabRevealedFromServer){
        tabRevealed = tabRevealedFromServer;
        System.out.println("CHAMP CLIENT - It did setRevealed");
    }

    public void newPartie() {
        tabMines = new boolean[get_width()][get_height()] ;  
        tabRevealed = new boolean[get_width()][get_height()];
    }
}