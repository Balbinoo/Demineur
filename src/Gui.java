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
    private Client cli;
    private JButton butQuit, butNew, butConnexion, butBack;
    private JComboBox levelComboBox;
    private Case cas[][];
    private JPanel panelCentre = new JPanel();
    private JPanel panelNorth = new JPanel();
    private JPanel panelSouth = new JPanel();
    private JMenu menuPartie = new JMenu("MenuBar");
    private JMenu menuAbout = new JMenu("About");
    private JMenuBar menuBar = new JMenuBar();
    private JLabel label = new JLabel("Score");
    private JLabel labelScore = new JLabel("0");
    private JPanel panelMines = new JPanel();
    private JMenuItem mQuitter, mNiveau, mBack, mGame;

    Gui(Compteur comp, Champ champ, Client cli, App app) {
        this.app = app;
        this.champ = champ;
        this.cli = cli;
        this.compt = comp;

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
            remove(panelCentre); 
            add(panelMines, BorderLayout.CENTER); 
            app.newPartie(levelComboBox.getSelectedIndex(), compt);
            revalidate();  
            repaint();        

        } else if(e.getSource() == butConnexion){
            System.out.println("Conexion button clicked");
            cli.connectServerBackground();

        }  else if(e.getSource() == butBack || e.getSource() == mBack){
            System.out.println("Clicked back button");
            //compt.stop();
            remove(panelMines); 
            add(panelCentre, BorderLayout.CENTER);         
            revalidate();  
            repaint(); 

        } else if(e.getSource() == mGame){
            String message = "The Minesweeper game was created by Rodrigo Balbino \n         for his final projet in Advanced Java Class";
            JOptionPane.showMessageDialog(this, message, "Game Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void configPainelNorth(JLabel label){
        // add combobox and score
        levelComboBox = new JComboBox<>(Level.values());
        panelNorth.add(label);    
        panelNorth.add(labelScore);
        panelNorth.add(levelComboBox);
        System.out.println("level choisis "+levelComboBox);
        add(panelNorth, BorderLayout.NORTH);
    }

/*
    public void configPainelCentre() {
        // add conexion button
        panelCentre.setLayout(new FlowLayout(FlowLayout.CENTER)); 
        butConnexion = new JButton("Connexion");
        butConnexion.addActionListener(this);    
        panelCentre.add(butConnexion);  
        add(panelCentre, BorderLayout.CENTER);  

        String message = " Welcome to the Demineur";
    }*/

    public void configPainelCentre() {
        // Set up panelCentre with BoxLayout to stack components vertically
        panelCentre.setLayout(new BoxLayout(panelCentre, BoxLayout.Y_AXIS));
    
        // Create a JLabel for the message
        JLabel welcomeLabel = new JLabel("Welcome to the Demineur");
        welcomeLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT); // Center the label horizontally
    
        // Create the Connexion button
        butConnexion = new JButton("Connexion");
        butConnexion.setAlignmentX(JButton.CENTER_ALIGNMENT); // Center the button horizontally
        butConnexion.addActionListener(this);
    
        // Add some vertical spacing between the message and the button
        panelCentre.add(Box.createVerticalStrut(20));  // 20px space before the label
        panelCentre.add(welcomeLabel);  // Add the message
        panelCentre.add(Box.createVerticalStrut(10));  // 10px space between the label and the button
        panelCentre.add(butConnexion);  // Add the button
    
        // Add panelCentre to the frame
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

    public void configMenuPartie(){
        
        // add Menu option Quit
        mQuitter = new JMenuItem("Quitter", KeyEvent.VK_Q);
        menuPartie.add(mQuitter) ;
        mQuitter.addActionListener(this);        

        // add Menu option New
        mNiveau = new JMenuItem("New", KeyEvent.VK_W);
        menuPartie.add(mNiveau);
        mNiveau.addActionListener(this);    

        // add Menu option Back
        mBack = new JMenuItem("Back", KeyEvent.VK_E);
        menuPartie.add(mBack);
        mBack.addActionListener(this);  

    }

    public void configMenuAbout(){
        // add Menu option Game
        mGame = new JMenuItem("Game", KeyEvent.VK_R);
        menuAbout.add(mGame) ;
        mGame.addActionListener(this);  
    }

    public void configMenuBar(){
        menuBar.add(menuPartie);
        menuBar.add(menuAbout);

        app.setJMenuBar(menuBar);
    }

    public void newPartie(int level){
        //System.out.println("level value = " + level);
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
    
    public Case[][] getCas(){
        return cas;
    }

    public void setLabelScore(int score){
        labelScore.setText(String.valueOf(score));
        add(panelNorth, BorderLayout.NORTH);
        app.pack();
    }

}