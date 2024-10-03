public class Compteur implements Runnable {
    private boolean running;      
    private int score;            
    private int nbJoueurs;        

    Compteur(int nbJoueurs) {
        this.nbJoueurs = nbJoueurs;  
        this.score = 0;              
        this.running = false;        
    }

    public void startCompteurBackground(Gui gui){
        if (!running) {
            running = true;
            new Thread(() -> run(gui)).start();
        }
    }

    public void run(Gui gui) {
        while (running) {  
            try {
                Thread.sleep(1000);  
                score++;           
                gui.setLabelScore(getScore());
                //gui.revalidate();  
                //gui.repaint();                 
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
    }

    public int getScore() {
        return score;
    }

    public int resetScore() {
        return score=0;
    }

}
