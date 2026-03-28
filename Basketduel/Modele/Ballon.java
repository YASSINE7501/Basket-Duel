package Modele;

/*
 * Modèle mathématique :
 *   vx = v0 * cos(angle)
 *   vy = v0 * sin(angle)
 *   x(t) = x0 + vx * t
 *   y(t) = y0 + vy * t - 0.5 * g * t²
 */
public class Ballon {

    // Constante de gravité. Peut être ajustée selon l'échelle.
    public static final double GRAVITE = 9.81;

    // Rayon du ballon, utilisé pour la détection de collision.
    public static final int RAYON = 20;

    // Position X,Y courante du centre du ballon.
    private double x;
    private double y;

    private double xInitial;
    private double yInitial;

    // Composante horizontale de la vitesse: vx = v0 * cos(angle).
    private double vx;

    // Composante verticale de la vitesse: vy = v0 * sin(angle).
    // Négatif pour aller "vers le haut" (convention écran Java).
    private double vy;

    // Temps écoulé depuis le début du tir .
    private double t;

    private boolean enMouvement;

    // Constructeur
    public Ballon(double xSpawn, double ySpawn) {
        this.x = xSpawn;
        this.y = ySpawn;
        this.xInitial = xSpawn;
        this.yInitial = ySpawn;
        this.vx = 0;
        this.vy = 0;
        this.t = 0;
        this.enMouvement = false;
    }

    // Méthode pour initialiser un tir
    /**
     * Initialise un tir parabolique à partir de l'angle (en degrés) et
     * de la puissance (vitesse initiale v0).
     *
     * Décomposition du vecteur vitesse :
     * vx = v0 * cos(angle_rad)
     * vy = -v0 * sin(angle_rad) (signe négatif : axe Y écran vers le bas)
     *
     * angleDegres: Angle de tir en degrés (0° = horizontal droit).
     * puissance: Vitesse initiale v0 en pixels/seconde.
     */
    public void tirer(double angleDegres, double puissance) {
        double angleRad = Math.toRadians(angleDegres);
        this.xInitial = this.x;
        this.yInitial = this.y;
        this.vx = puissance * Math.cos(angleRad);
        this.vy = -puissance * Math.sin(angleRad); // Vers le haut sur l'écran
        this.t = 0;
        this.enMouvement = true;
    }

    // Méthode pour mettre à jour la position du ballon
    /**
     * Met à jour la position du ballon pour un incrément de temps.
     *
     * Équations horaires appliquées :
     * x(t) = x0 + vx * t
     * y(t) = y0 + vy * t + 0.5 * g * t² (g positif car axe Y écran vers le bas)
     *
     * dt: Durée du tick en secondes (ex : 0.016 pour ~60 FPS).
     */
    public void mettreAJour(double dt) {
        if (!enMouvement)
            return;
        t += dt;
        x = xInitial + vx * t;
        y = yInitial + vy * t + 0.5 * GRAVITE * t * t;
    }

    // Méthode pour calculer la distance entre le ballon et une cible
    /**
     * Calcule la distance euclidienne entre le centre du ballon et un point cible
     * (centre du panier ou d'un bonus).
     *
     * d = sqrt( (x2-x1)² + (y2-y1)² )
     *
     * xCible: Coordonnée X du centre de la cible.
     * yCible: Coordonnée Y du centre de la cible.
     * On retourne la distance en pixels entre les deux centres.
     */
    public double distanceA(double xCible, double yCible) {
        double dx = xCible - this.x;
        double dy = yCible - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Méthode pour detecter les collisions
    /**
     * Indique si le ballon est en collision avec une cible circulaire.
     * La collision est avérée si la distance entre les centres est
     * inférieure ou égale à la somme des rayons.
     */
    public boolean estEnCollisionAvec(double xCible, double yCible, int rayonCible) {
        return distanceA(xCible, yCible) <= (RAYON + rayonCible);
    }

    // Méthode pour reinitialiser le ballon
    /**
     * Remet le ballon à sa position de spawn et stoppe son mouvement.
     * Appelé après chaque fin de tir (panier marqué, sol atteint, etc.)
     */
    public void reinitialiser(double xSpawn, double ySpawn) {
        this.x = xSpawn;
        this.y = ySpawn;
        this.xInitial = xSpawn;
        this.yInitial = ySpawn;
        this.vx = 0;
        this.vy = 0;
        this.t = 0;
        this.enMouvement = false;
    }

    /** Getters et Setters */
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public double getT() {
        return t;
    }

    public boolean isEnMouvement() {
        return enMouvement;
    }

    //Force l'arrêt du mouvement du ballon (ex : collision avec le sol).
    public void setEnMouvement(boolean enMouvement) {
        this.enMouvement = enMouvement;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    //Sert a debuger et a tracer le comportement du code
    @Override
    public String toString() {
        return String.format("Ballon[x=%.1f, y=%.1f, vx=%.2f, vy=%.2f, t=%.3f, enMouvement=%b]",
                x, y, vx, vy, t, enMouvement);
    }
}
