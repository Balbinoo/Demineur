import javax.swing.*;

/**
 * Magnifique programme
 * @author  rod
 * @version 0.0
 */
public class App extends JFrame {
    /**
     * @param args
     * @throws Exception 
     */
    private Champ champ;
    private int score;  
    private Gui gui;
    static JMenuBar mb;
    static JMenu x;
    static JMenuItem m1, m2, m3;
    static JFrame f;
 
     App() {
        super("Demineur");
        champ = new Champ();
        int level = 0;
        champ.init(0,0,level) ;
        champ.display();

        gui = new Gui(champ, this, level) ;

        setContentPane(gui);
        pack() ;
        setSize(400,400);
        setVisible(true) ;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Demineur");
        new App() ;

    }
    public void quit() {
        System.out.println("La fin");
        System.exit(0);
    }

    public void newPartie (int level) {
        System.out.println("Selected level" + level);
        score = 0;
        champ.set_height(level);
        champ.set_width(level);
        champ.newPartie(level); 
        gui.newPartie(level);
        gui.majPanelMines();
        setSize(400,400);
    }
    
}
