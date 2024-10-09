import java.awt.event.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.*;
import java.util.Random;
import java.util.StringJoiner;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Graphical User Interface (View)
 * @author Rodrigo Balbino
 * @version 0.0
 */

public class Gui extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;

    //entites
    private App app;
    private Champ champ;
    private Compteur compt;
    private Serveur server;
    private Case ca;
    private Case cas[][];

    //Panels
    private JPanel panelCustom = new JPanel();
    private JPanel panelCentre = new JPanel();
    private JPanel panelNorth = new JPanel();
    private JPanel panelSouth = new JPanel();
    private JPanel panelLeft = new JPanel();
    private JPanel panelInput = new JPanel();

    //Menus
    private JMenu menuPartie = new JMenu("Menu");
    private JMenu menuAbout = new JMenu("About");
    private JMenuBar menuBar = new JMenuBar();
    private JMenuItem mQuitter, mNiveau, mBack, mGame;

    //Labels
    private JLabel setCustomMines = new JLabel();
    private JLabel setCustomLabel = new JLabel();
    private JLabel labelName = new JLabel();
    private JLabel label = new JLabel("Score");
    private JLabel labelScore = new JLabel("0");
    private JPanel panelMines = new JPanel();
    private JLabel enterName = new JLabel("Enter your name: ");

    //Buttons
    private JButton butQuit, butNew, butConnexion, butBack, butCustom;
    private JComboBox levelComboBox;

    private JTextField nameField = new JTextField(15);
    private JButton submitButton = new JButton("Submit");

    private JTextField setWidth = new JTextField(5);
    private JTextField setHeight = new JTextField(5);
    private JTextField setMines = new JTextField(5);
    private StringJoiner joiner;

    // Add a JTextArea to display player names
    private JTextArea playerNamesArea = new JTextArea(10, 20); // 10 rows, 20 columns

    
    Gui(){}

    Gui(Serveur ser, Case cases, Compteur comp, Champ champ, App app) {

        this.app = app;
        this.champ = champ;
        this.compt = comp;
        this.ca = cases;
        this.server = ser;

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
            actionButtonConnexion();

        }  else if(e.getSource() == butBack || e.getSource() == mBack){
            actionButtonBack();
            
        } else if(e.getSource() == mGame){
            actionAbout();

        } else if(e.getSource() == submitButton){
            actionButtonConnexionSubmit();

        } else if(e.getSource() == butCustom){ 
            actionButtonCustom();
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void actionButtonNew() {
        // Reset the score and the GUI
        resetGui();
        
        //System.out.println("LEvelcombobox"+ levelComboBox.getSelectedIndex());
        // Remove combobox, panel centre, and add panelMines

        remove(panelInput); 
        remove(panelCustom); 
        remove(panelCentre);
        panelNorth.remove(levelComboBox);

        if(levelComboBox.getSelectedIndex()==3){
            actionSelectedCustom();
        }else{
            add(panelMines, BorderLayout.CENTER);
            // Start a new game and compteur
            app.newPartie(levelComboBox.getSelectedIndex(), compt);
        }
    }
    public void actionButtonConnexionSubmit() {
        String playerName = nameField.getText();
        if (!playerName.isEmpty()) {
            app.connectPlayer(playerName);
    
            System.out.println("Gui - Player name submitted: " + playerName);
    
            // Start a new game if this method is intended to do so
            actionButtonNew();
    
            /* Add the submitted player name to the JTextArea
            playerNamesArea.append(playerName + "\n");  // Append the name with a newline
            playerNamesArea.setEditable(false);  // Ensure JTextArea is read-only
    
            // Clear and update panelLeft to include playerNamesArea
            panelLeft.removeAll();
            panelLeft.add(playerNamesArea);
            
            // Add panelLeft to the main layout if not already added
            add(panelLeft, BorderLayout.WEST);
    
            // Refresh the panel to display the updated names
            panelLeft.revalidate();
            panelLeft.repaint();*/
        }
    }
    

    public void setconfigLeftPanel(List<String> playerNames){
        System.out.println("Inside setconfigLeftPanel");
    
        // Create a StringJoiner to join player names with newline
        joiner = new StringJoiner("\n");
        for (String name : playerNames) {
            //System.out.println("GUI LOOP LEFT PANEL - Add name = " + name);
            joiner.add(name);
        }
    
        // Set the joined names to playerNamesArea
        playerNamesArea.setText("");  // Clear previous names

        // Append the new player name to the JTextArea
        playerNamesArea.setText(joiner.toString());
        playerNamesArea.setEditable(false);  // Make JTextArea read-only
        

        // Add playerNamesArea to panelLeft and update it
        //panelLeft.removeAll();  // Clear any existing components
        panelLeft.add(playerNamesArea);  // Add playerNamesArea to panelLeft
        panelLeft.revalidate();
        panelLeft.repaint();
        
        add(panelLeft, BorderLayout.WEST);
    }
    

    public void actionButtonCustom(){
        int width = Integer.parseInt(setWidth.getText());
        int height = Integer.parseInt(setHeight.getText());
        int mines = Integer.parseInt(setMines.getText());

        remove(panelInput); 
        remove(panelCustom); 
        remove(panelCentre);
        panelNorth.remove(levelComboBox);

        //add(panelNorth, BorderLayout.CENTER);
        //add(panelSouth, BorderLayout.CENTER);
        add(panelMines, BorderLayout.CENTER);

        // Start a new game and compteur

        app.newCustomPartie(levelComboBox.getSelectedIndex(),mines,width, height,compt);

    }

    public void actionSelectedCustom() {
        panelCustom.removeAll();
        panelCustom.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
    
        setCustomLabel = new JLabel("Add Grid Size (Width x Height): ");
        setWidth = new JTextField(3); 
        setHeight = new JTextField(3);
        setCustomMines = new JLabel("Number of Mines: ");
        setMines = new JTextField(3);
        butCustom = new JButton("Send Values");
    
        // Set custom panel border for padding
        panelCustom.setBorder(BorderFactory.createTitledBorder("Custom Game Settings"));
    
        // Layout configuration: Position each component with grid positioning
    
        // First row: "Grid Size (Width x Height):" label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelCustom.add(setCustomLabel, gbc);
    
        // Second row: Width text field
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panelCustom.add(new JLabel("Width:"), gbc);
    
        gbc.gridx = 1;
        panelCustom.add(setWidth, gbc);
    
        // Third row: Height text field
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelCustom.add(new JLabel("Height:"), gbc);
    
        gbc.gridx = 1;
        panelCustom.add(setHeight, gbc);
    
        // Fourth row: "Number of Mines:" label and text field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panelCustom.add(setCustomMines, gbc);
    
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panelCustom.add(setMines, gbc);
    
        // Last row: Send button centered
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panelCustom.add(butCustom, gbc);
    
        // Add the custom panel to the main layout
        add(panelCustom, BorderLayout.CENTER);
        //revalidate();
        //repaint();
    
        // Attach action listener to the button
        butCustom.addActionListener(this);
    }
    


    public void actionButtonConnexion(){
        // Connect to the server in the background
        app.connectClient();
        app.setServerOn();
        // Create a panel for input
        panelInput.setLayout(new FlowLayout());

        // Add label and text field to the panel
        panelInput.add(enterName);
        panelInput.add(nameField);
        panelInput.add(submitButton);
    
        // Add the input panel to the center of the window
        remove(panelCentre); 
        remove(panelNorth);
        add(panelInput, BorderLayout.CENTER);
        revalidate();
        repaint();

        submitButton.addActionListener(this);
    }
    
    public void actionButtonBack(){
            // Reset the score and the GUI
            resetGui();
            // remove panelMines, add panel North and panel Centre
            remove(panelLeft);
            remove(panelInput); 
            remove(panelCustom); 
            remove(panelMines); 
            remove(panelInput);
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

        //System.out.println("champ.get_width()= "+champ.get_width());
        //System.out.println("champ.get_height()= "+champ.get_height());

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