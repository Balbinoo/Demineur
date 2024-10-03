import java.util.Random;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * Magnifique programme
 * @author  Rodrigo Balbino
 * @version 0.0
 */
public class Champ {

    private int score;  
    static boolean [][] tabMines ;
    private static  int def_width  = 0;
    private static  int def_height  = 0;
    //private final static  int DEF_NDMINES = 2 ;
    //private Compteur compt;
    private int level = 0;

    Random random = new Random();

    private int [] tabSize = {5,10,30} ;
    private int [] tabNMines = {3,7,9} ;

    public Champ( ) {
        tabMines = new boolean[get_height()][get_width()];
    }

    public void countScore(){
        score++;    
    }

    public int getCountScore(){
        return score;    
    }

    public int get_height(){
            return tabSize[def_height];
    }

    public int get_numeroMines(int level){
        return tabNMines[level];
    }

    public int get_width(){
            return  tabSize[def_width];
    }

    public void set_height(int level){
        def_height = level;
    }

    public void set_width(int level){
        def_width = level;
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
        
        for (int n = tabNMines[level] ; n != 0; ) {  
            int x = random.nextInt(tabSize[level]);         
            int y = random.nextInt(tabSize[level]);    

            if (!(tabMines[x][y] || (x == startX && y == startY))) {
                tabMines[x][y] = true;
                n--;
            }
        }
    }

    void display() {
        for (int i = 0; i < get_width(); i++) {
            for (int j = 0; j < get_height(); j++) {  
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
        
        int choice = gui.showOptionDialog(cas, "You Won!", "Good Job", "info");

        if (choice == JOptionPane.YES_OPTION) 
            app.newPartie(this.get_level(), comp);
            
        else if (choice == JOptionPane.NO_OPTION) 
            app.quit();
         
    }

    public void game_over(Compteur comp, App app, Gui gui, Case cas) {

        resetGame( gui, comp, cas);

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

    boolean isMine (int x , int y){
        return tabMines[x][y] ;
    }

    void newPartie(int level) {
            tabMines = new boolean[get_width()][get_height()] ;  
    }

}