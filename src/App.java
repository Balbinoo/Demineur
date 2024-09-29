import javax.swing.*;

/**
 * Magnifique programme
 * @author  Rodrigo Balbino
 * @version 0.0
 */
public class App extends JFrame {
    /**
     * @param args
     * @throws Exception 
     */
    private Case cas;
    private Champ champ;
    private Gui gui;
    static JMenuBar mb;
    static JMenu x;
    static JMenuItem m1, m2, m3;
    static JFrame f;
 
     App() {
        super("Demineur");
        champ = new Champ();
        champ.set_level(0);
        champ.init(0,0,champ.get_level());
        champ.display();

        gui = new Gui(champ, this);

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
        //System.out.println("Selected level" + level);

        champ.set_height(level);
        champ.set_width(level);
        champ.newPartie(level); 
        gui.newPartie(level);
        gui.majPanelMines();
        setSize(400,400);
    }
    
}
