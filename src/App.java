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
    private Compteur compt;
    private Gui gui;
    static JMenuBar mb;
    static JMenu x;
    static JMenuItem m1, m2, m3;
    static JFrame f;
    private Serveur serv;
    private Client cli;

     App() {
        super("Demineur");
        compt = new Compteur(1);  
        champ = new Champ();
        cas = new Case();
        cli = new Client();
        gui = new Gui(cas, compt, champ, cli, this);
        serv = new Serveur(champ, gui, cas);
        //serv.main(null);
        revalidate();  
        repaint();     
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

    public void newPartie (int level, Compteur comp) {
        comp.resetScore();
        comp.startCompteurBackground(gui);
        champ.set_level(level);

        champ.set_height(level);
        champ.set_width(level);
        
        champ.newPartie(); 
        gui.newPartie(level);
        gui.majPanelMines();
    }
    
    public void newCustomPartie (int level, int mines, int width, int height, Compteur comp) {
        comp.resetScore();
        comp.startCompteurBackground(gui);
        champ.set_level(level);

        champ.set_CustomHeight(height);
        champ.set_CustomWidth(width);
        champ.setCustomTabNMines(mines);

        champ.newPartie(); 
        gui.newPartie(level);
        gui.majPanelMines();
    }

}
