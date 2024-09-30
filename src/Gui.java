import java.awt.event.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.*;

/**
 * Graphical User Interface (View)
 * @author Rodrigo Balbino
 * @version 0.0
 */

public class Gui extends JPanel implements ActionListener {

    private App app;
    private Champ champ;
    private JButton butQuit, butNew;
    private JComboBox levelComboBox;
    private Case cas[][];
    private JPanel panelNorth = new JPanel();
    private JPanel panelSouth = new JPanel();
    private JMenu menuPartie = new JMenu("MenuBar");
    private JMenuBar menuBar = new JMenuBar();
    private JLabel label = new JLabel("Score");
    private JLabel labelScore = new JLabel("0");
    private JPanel panelMines = new JPanel();
    private JMenuItem mQuitter;
    private JMenuItem mNiveau;

    Gui(Champ champ, App app) {

        this.app = app;
        this.champ = champ;

        setLayout(new BorderLayout());

        // MenuBar
        configMenuBar();

        // MenuOption
        configMenuOption();
  
        // panel north
        configPainelNorth(label);

        // panel centre
        add(panelMines, BorderLayout.CENTER);

        // panel south
        configPainelSouth();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == butQuit || e.getSource() == mQuitter) {
            app.quit();
            //System.out.println("La taille du champ est : " + champ.get_width());
        } else if (e.getSource() == butNew || e.getSource() == mNiveau) {
            app.newPartie(levelComboBox.getSelectedIndex());
            //System.out.println("New game started!");
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void configPainelNorth(JLabel label){
        levelComboBox = new JComboBox<>(Level.values());
        panelNorth.add(label);    
        panelNorth.add(labelScore);
        panelNorth.add(levelComboBox);
        System.out.println("level choisis "+levelComboBox);
        add(panelNorth, BorderLayout.NORTH);
    }

    public void configPainelSouth(){
        panelSouth.setLayout(new FlowLayout()); 
        // Button quit
        butQuit = new JButton("Quit");
        butQuit.addActionListener(this);
        panelSouth.add(butQuit);
        // Button new
        butNew = new JButton("New");
        butNew.addActionListener(this);
        panelSouth.add(butNew);
        add(panelSouth, BorderLayout.SOUTH);
    }

    public void configMenuOption(){
        // add Menu option Quit
        mQuitter = new JMenuItem("Quitter", KeyEvent.VK_Q);
        menuPartie.add(mQuitter) ;
        mQuitter.addActionListener(this);        

        // add Menu option New
        mNiveau = new JMenuItem("New", KeyEvent.VK_Q);
        menuPartie.add(mNiveau);
        mNiveau.addActionListener(this);    
    }

    public void configMenuBar(){
        menuBar.add(menuPartie);
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
                    cas[i][j] = new Case(this, i, j, champ, app);  
                
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
        //add(panelNorth, BorderLayout.NORTH);
    }

}