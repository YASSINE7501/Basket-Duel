package Modele;

import java.util.Random;

public class IntelligenceArtificielle {

    private int niveau; // 1 = Facile, 2 = Moyen, 3 = Difficile
    
    public IntelligenceArtificielle(int diff) {
        this.niveau = diff;
    }

    // Calcul de l'angle et de la force pour que l'IA tire
    public double[] calculerTir(double xJoueur, double yJoueur, double xPanier, double yPanier) {
        Random rand = new Random();
        
        // distance entre joueur et panier
        double distanceX = Math.abs(xPanier - xJoueur);
        // attention l'axe Y est inversé sur l'écran par rapport aux maths
        double distanceY = yJoueur - yPanier; 
        
        double direction = 1.0;
        if (xPanier < xJoueur) {
            direction = -1.0; // on tire vers la gauche
        }

        // On choisi un angle au pif entre 45 et 80 degrés
        // pour que la courbe en l'air soit jolie
        double angleRad = Math.toRadians(45 + rand.nextInt(35));
        
        // Formule de la trajectoire parabolique pour trouver la vitesse (v0)
        // v0 = sqrt( (g * x^2) / (2 * cos^2(angle) * (x * tan(angle) - y)) )
        double g = Ballon.GRAVITE; // on recupere la gravite du physicien
        double cosA = Math.cos(angleRad);
        double tanA = Math.tan(angleRad);
        
        double leBas = 2 * (cosA * cosA) * (distanceX * tanA - distanceY);
        
        double forceTir = 0;
        if (leBas > 0) {
            forceTir = Math.sqrt((g * distanceX * distanceX) / leBas);
        } else {
            forceTir = distanceX; // securite au cas ou
        }

        // On rajoute de l'erreur selon la difficulte pour pas que l'ordinateur soit invincible
        // plus le niveau est grand, plus on divise l'erreur (donc IA plus forte)
        double erreurForce = (rand.nextDouble() * 30) / niveau;
        double erreurAngle = (rand.nextDouble() * 10) / niveau;

        forceTir += erreurForce;
        
        double angleFinal = Math.toDegrees(angleRad) + erreurAngle;
        
        // si le panier est à gauche, on inverse l'angle pour tirer derriere
        if (direction < 0) {
            angleFinal = 180 - angleFinal;
        }

        double[] resultat = new double[2];
        resultat[0] = angleFinal;
        resultat[1] = forceTir;
        
        return resultat;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }
}
