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
    private JLabel label = new JLabel();
    JLabel labelScore = new JLabel("0");
    JPanel panelMines = new JPanel();
    private JMenuItem mQuitter;
    private JMenuItem mNiveau;

    Gui(Champ champ, App app, int level) {

        JMenuBar menuBar = new JMenuBar();
        JMenu menuPartie = new JMenu("MenuBar");
        JLabel label = new JLabel("Score");

        this.app = app;
        this.champ = champ;

        menuBar.add(menuPartie);
        app.setJMenuBar(menuBar);
        cas = new Case[champ.get_width()][champ.get_height()];

        setLayout(new BorderLayout());

        // add Menu option Quit
        mQuitter = new JMenuItem("Quitter", KeyEvent.VK_Q);
        menuPartie.add(mQuitter) ;
        mQuitter.addActionListener(this);        

        // add button option Niveau
        mNiveau = new JMenuItem("Niveau", KeyEvent.VK_Q);
        menuPartie.add(mNiveau) ;
        mNiveau.addActionListener(this);        
  
        // panel north
        JPanel panelNorth = new JPanel();
        levelComboBox = new JComboBox<>(Level.values());
        panelNorth.add(label);    
        panelNorth.add(labelScore);
        panelNorth.add(levelComboBox);
        add(panelNorth, BorderLayout.NORTH);

        // panel centre
        add(panelMines, BorderLayout.CENTER);

        // panel south
        JPanel panelSouth = new JPanel();
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == butQuit || e.getSource() == mQuitter) {
            app.quit();
            System.out.println("La taille du champ est : " + champ.get_width());
        } else if (e.getSource() == butNew || e.getSource() == mNiveau) {
            app.newPartie(levelComboBox.getSelectedIndex());
            System.out.println("New game started!");
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void newPartie(int level){
        System.out.println("level value = " + level);
        labelScore.setText("0");
        panelMines.removeAll();
        app.pack();
    }

    public void majPanelMines() {
        cas = new Case[champ.get_width()][champ.get_height()];
    
        panelMines.setLayout(new GridLayout(champ.get_height(), champ.get_width()));
    
        for (int i = 0; i < champ.get_width(); i++) {
            for (int j = 0; j < champ.get_height(); j++) {
                    cas[i][j] = new Case(i, j, champ, app);  
                
                panelMines.add(cas[i][j]);
            }
        }
        app.pack();
    }
    
}