import java.awt.event.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.*;
import java.util.Random;

/**
 * Graphical User Interface (View)
 * @author Rodrigo Balbino
 * @version 0.0
 */

public class Gui extends JPanel implements ActionListener {

    private App app;
    private Champ champ;
    private Compteur compt;
    private Case ca;
    private Client cli;
    private JButton butQuit, butNew, butConnexion, butBack;
    private JComboBox levelComboBox;
    private Case cas[][];
    private JPanel panelCentre = new JPanel();
    private JPanel panelNorth = new JPanel();
    private JPanel panelSouth = new JPanel();
    private JMenu menuPartie = new JMenu("Menu");
    private JMenu menuAbout = new JMenu("About");
    private JMenuBar menuBar = new JMenuBar();
    private JLabel label = new JLabel("Score");
    private JLabel labelScore = new JLabel("0");
    private JPanel panelMines = new JPanel();
    private JMenuItem mQuitter, mNiveau, mBack, mGame;

    Gui(Case cases, Compteur comp, Champ champ, Client cli, App app) {

        this.app = app;
        this.champ = champ;
        this.cli = cli;
        this.compt = comp;
        this.ca = cases;

        setLayout(new BorderLayout());

        // MenuBar
        configMenuBar();

        // MenuOptionPartie
        configMenuPartie();
        
        //MenuOptionAbout
        configMenuAbout();
  
        // panel north
        configPainelNorth(label);

        // panel centre
        configPainelCentre();

        // panel south
        configPainelSouth();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == butQuit || e.getSource() == mQuitter) {
            app.quit();

        } else if (e.getSource() == butNew || e.getSource() == mNiveau) { 
            actionButtonNew();

        } else if(e.getSource() == butConnexion){
            cli.connectServerBackground();

        }  else if(e.getSource() == butBack || e.getSource() == mBack){
            actionButtonBack();
            
        } else if(e.getSource() == mGame){
            actionAbout();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void actionButtonNew() {
        // Reset the score and the GUI
        resetGui();
    
        // Remove combobox, panel centre, and add panelMines
        remove(panelCentre);
        panelNorth.remove(levelComboBox);
        add(panelMines, BorderLayout.CENTER);
    
        // Start a new game and compteur
        app.newPartie(levelComboBox.getSelectedIndex(), compt);
    }

    public void actionButtonBack(){
            // Reset the score and the GUI
            resetGui();
            // remove panelMines, add panel North and panel Centre
            remove(panelMines); 
            panelNorth.add(levelComboBox);
            add(panelCentre, BorderLayout.CENTER);    
    }

    public void actionAbout(){
        String message = "The Minesweeper game was created by Rodrigo Balbino \n         for his final projet in Advanced Java Class";
        JOptionPane.showMessageDialog(this, message, "Game Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public void resetGui() {
        compt.stop();        
        compt.resetScore();    
        labelScore.setText("0"); 
        ca.resetCountCases();  
        revalidate();
        repaint();
    }

    public void configPainelNorth(JLabel label){
        // add combobox and score
        levelComboBox = new JComboBox<>(Level.values());
        panelNorth.add(label);    
        panelNorth.add(labelScore);
        panelNorth.add(levelComboBox);
        System.out.println("level choisis "+levelComboBox.getSelectedItem());
        add(panelNorth, BorderLayout.NORTH);
    }


    public void configPainelCentre() {

        panelCentre.setLayout(new BoxLayout(panelCentre, BoxLayout.Y_AXIS));
        // add Message
        JLabel welcomeLabel = new JLabel("Welcome to the Demineur");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24)); 
        welcomeLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT); 
    
        // add button connexion
        butConnexion = new JButton("Connexion");
        butConnexion.setAlignmentX(JButton.CENTER_ALIGNMENT);
        butConnexion.addActionListener(this);
    
        // add space between items
        panelCentre.add(Box.createVerticalStrut(60));  
        panelCentre.add(welcomeLabel); 
        panelCentre.add(Box.createVerticalStrut(50));  
        panelCentre.add(butConnexion);  
    
        add(panelCentre, BorderLayout.CENTER);
    }
    
    
    public void configPainelSouth(){
        panelSouth.setLayout(new FlowLayout()); 
        // Button back
        butBack = new JButton("Back");
        butBack.addActionListener(this);
        panelSouth.add(butBack);

        // Button new
        butNew = new JButton("New");
        butNew.addActionListener(this);
        panelSouth.add(butNew);

        // Button quit
        butQuit = new JButton("Quit");
        butQuit.addActionListener(this);
        panelSouth.add(butQuit);

        add(panelSouth, BorderLayout.SOUTH);
    }

    public void configMenuBar(){
        menuBar.add(menuPartie);
        menuBar.add(menuAbout);

        app.setJMenuBar(menuBar);
    }

    public void configMenuPartie(){
        
        // add Menu option New
        mNiveau = new JMenuItem("New", KeyEvent.VK_W);
        menuPartie.add(mNiveau);
        mNiveau.addActionListener(this);    

        // add Menu option Back
        mBack = new JMenuItem("Back", KeyEvent.VK_E);
        menuPartie.add(mBack);
        mBack.addActionListener(this);  

        // add Menu option Quit
        mQuitter = new JMenuItem("Quitter", KeyEvent.VK_Q);
        menuPartie.add(mQuitter) ;
        mQuitter.addActionListener(this);        

    }

    public void configMenuAbout(){
        // add Menu option Game
        mGame = new JMenuItem("Game", KeyEvent.VK_R);
        menuAbout.add(mGame) ;
        mGame.addActionListener(this);  
    }


    public void newPartie(int level){
        labelScore.setText("0");
        panelMines.removeAll();
        app.pack();
    }

    public void majPanelMines() {
        cas = new Case[champ.get_width()][champ.get_height()];
    
        panelMines.setLayout(new GridLayout(champ.get_height(), champ.get_width()));
    
        for (int i = 0; i < champ.get_width(); i++) {
            for (int j = 0; j < champ.get_height(); j++) {
                    cas[i][j] = new Case(this, i, j, champ, app, compt);  
                
                panelMines.add(cas[i][j]);
            }
        }
        app.pack();
    }
    
    public Case getCase(int row, int col) {
        if (row >= 0 && row < cas.length && col >= 0 && col < cas[0].length) {
            return cas[row][col]; 
        }
        return null;  
    }

    public void setLabelScore(int score){
        labelScore.setText(String.valueOf(score));
    }

    public int showOptionDialog(Case cas, String title, String message, String type){
        return JOptionPane.showOptionDialog(cas, message, title,JOptionPane.YES_NO_OPTION, type.equals("info") ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE, null, new String[]{"New Game", "Quit"}, "New Game");
    }
}