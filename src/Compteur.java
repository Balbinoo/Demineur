public class Compteur implements Runnable {
    private Thread processScores; 
    private boolean running;      
    private int score;            
    private int nbJoueurs;        

    Compteur(int nbJoueurs) {
        this.nbJoueurs = nbJoueurs;  
        this.score = 0;              
        this.running = true;        
        processScores = new Thread(this);
        processScores.start();           
    }

    public void run() {
        while (running) {  
            try {
                Thread.sleep(1000);  
                score++;           
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
