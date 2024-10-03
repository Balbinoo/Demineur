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
        if (!running) {  // Only start if not already running
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
                System.out.println("Tempo: " + score + " segundos"); 
                
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() {
        running = false;
        System.out.println("Score final"+ getScore());
    }

    public int getScore() {
        return score;
    }

    public int resetScore() {
        return score=0;
    }

}
