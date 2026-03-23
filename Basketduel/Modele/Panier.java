package Modele;

/**
 * Détection de collision : utilise la distance euclidienne entre le
 * centre du ballon et le centre du panier.
 */
public class Panier {

    // Rayon de la hitbox
    /** Rayon de la hitbox circulaire du panier en pixels. */
    public static final int RAYON = 30;

    // Position
    /** Coordonnée X du centre du panier. */
    private double x;

    /** Coordonnée Y du centre du panier. */
    private double y;

    // Limites de déplacement
    /** Borne gauche de déplacement horizontale du panier (pixels). */
    private double limiteGauche;

    /** Borne droite de déplacement horizontale du panier (pixels). */
    private double limiteDroite;

    /** Borne haute de déplacement verticale du panier (pixels). */
    private double limiteHaute;

    /** Borne basse de déplacement verticale du panier (pixels). */
    private double limiteBasse;

    // Constructeur
    /**
     * Crée un panier à la position donnée avec ses limites de déplacement.
     * x: Coordonnée X initiale du centre du panier.
     * y: Coordonnée Y initiale du centre du panier.
     * limiteGauche: Borne gauche du déplacement horizontal.
     * limiteDroite: Borne droite du déplacement horizontal.
     * limiteHaute: Borne haute du déplacement vertical.
     * limiteBasse: Borne basse du déplacement vertical.
     */
    public Panier(double x, double y, double limiteGauche, double limiteDroite, double limiteHaute,
            double limiteBasse) {
        this.x = x;
        this.y = y;
        this.limiteGauche = limiteGauche;
        this.limiteDroite = limiteDroite;
        this.limiteHaute = limiteHaute;
        this.limiteBasse = limiteBasse;
    }

    // Déplacement du panier (appelé par le Contrôleur / IA)
    /**
     * Déplace le panier vers le haut d'un pas donné, en respectant la
     * limite haute du terrain.
     * pas: Nombre de pixels de déplacement.
     */
    public void deplacerHaut(double pas) {
        y = Math.max(limiteHaute + RAYON, y - pas);
    }

    /**
     * Déplace le panier vers le bas d'un pas donné, en respectant la
     * limite basse du terrain.
     * pas: Nombre de pixels de déplacement.
     */
    public void deplacerBas(double pas) {
        y = Math.min(limiteBasse - RAYON, y + pas);
    }

    /**
     * Déplace le panier vers la gauche d'un pas donné, en respectant la
     * limite gauche du terrain.
     * pas: Nombre de pixels de déplacement.
     */
    public void deplacerGauche(double pas) {
        x = Math.max(limiteGauche + RAYON, x - pas);
    }

    /**
     * Déplace le panier vers la droite d'un pas donné, en respectant la
     * limite droite du terrain.
     * pas: Nombre de pixels de déplacement.
     */
    public void deplacerDroite(double pas) {
        x = Math.min(limiteDroite - RAYON, x + pas);
    }

    // Détection de collision
    /**
     * Vérifie si un ballon (décrit par son centre et son rayon) entre en collision
     * avec le panier.
     * Formule : d = sqrt((xB-xP)² + (yB-yP)²)
     * Collision si d ≤ rayonBallon + RAYON
     * xBallon: Coordonnée X du centre du ballon.
     * yBallon: Coordonnée Y du centre du ballon.
     * rayonBallon: Rayon du ballon en pixels.
     * return: true si le ballon touche le panier.
     */
    public boolean estTouche(double xBallon, double yBallon, int rayonBallon) {
        double dx = xBallon - this.x;
        double dy = yBallon - this.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= (rayonBallon + RAYON);
    }

    // Getters / Setters

    /** return: Coordonnée X du centre du panier. */
    public double getX() {
        return x;
    }

    /** return: Coordonnée Y du centre du panier. */
    public double getY() {
        return y;
    }

    /** Repositionne directement le panier. */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /** return: Borne gauche de déplacement. */
    public double getLimiteGauche() {
        return limiteGauche;
    }

    /** return: Borne droite de déplacement. */
    public double getLimiteDroite() {
        return limiteDroite;
    }

    /** return: Borne haute de déplacement. */
    public double getLimiteHaute() {
        return limiteHaute;
    }

    /** return: Borne basse de déplacement. */
    public double getLimiteBasse() {
        return limiteBasse;
    }

    @Override
    public String toString() {
        return String.format("Panier[x=%.1f, y=%.1f, rayon=%d]", x, y, RAYON);
    }
}
