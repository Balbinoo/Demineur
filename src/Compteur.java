public class Compteur implements Runnable {
    private boolean running;      
    private int score;            
    private int nbJoueurs;        
    private Thread thread;  // Reference to the thread running this Compteur

    Compteur(int nbJoueurs) {
        this.nbJoueurs = nbJoueurs;  
        this.score = 0;              
        this.running = false;        
    }

    public void startCompteurBackground(Gui gui){
        if (!running) {
            running = true;
            thread = new Thread(() -> run(gui));  // Assign the thread reference
            thread.start();  // Start the new thread
        }
    }

    public void run(){}

    public void run(Gui gui) {
        while (running) {  
            try {
                Thread.sleep(1000);  
                score++;           
                gui.setLabelScore(getScore());           
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } 
    }

    public void stop() {
        running = false; 
        if (thread != null && thread.isAlive()) {
            try {
                thread.join();  
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public int getScore() {
        return score;
    }

    public int resetScore() {
        return score=0;
    }

}
