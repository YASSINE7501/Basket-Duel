package Modele;

public class Joueur {
    private String nom;
    private int score;
    private double x;
    private double y;
    private boolean estActif;
    private Ballon ballon;
    private double angle;
    private double puissance;

    public Joueur() {
        this.nom = "Joueur";
        this.score = 0;
        this.x = 0;
        this.y = 0;
        this.estActif = false;
        this.angle = 0;
        this.puissance = 0;
        this.ballon = new Ballon(0, 0);
    }
    public void setActif(boolean actif) {
        this.estActif = actif;
    }
    public void marquerPanier(int points) {
        this.score += points;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    public int getScore() { 
        return score; 
    }

    public boolean isActif() {
        return estActif;
    }

    public void setTir(double angle, double puissance) {
        this.angle = angle;
        this.puissance = puissance;
    }

    public String getNom() {
        return nom;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }

    public double getPuissance() {
        return puissance;
    }

    public Ballon getBallon() {
        return ballon;
    }
}