package Modele;

/**
 * BONUS (effets positifs): POINTS_BONUS, TRAJECTOIRE_AIDE
 * MALUS (effets négatifs): POINTS_MALUS, TRAJECTOIRE_GENE, VITESSE_PANIER, MURE_OBSTACLE
 */
public class Bonus {

    /** Rayon de la hitbox circulaire du bonus (pixels). */
    public static final int RAYON = 18;

    // Enumération des types de bonus et malus
    public enum TypeBonus {
        // Bonus positifs
        POINTS_BONUS,
        TRAJECTOIRE_AIDE,
        RALENTISSEMENT_PANIER,

        // Malus négatifs
        POINTS_MALUS,
        TRAJECTOIRE_GENE,
        VITESSE_PANIER,
        MUR_OBSTACLE;

        // Compteurs statiques
        public static int nbrBonus() {
            return 3; // POINTS_BONUS, TRAJECTOIRE_AIDE, RALENTISSEMENT_PANIER
        }

        public static int nbrMalus() {
            return TypeBonus.values().length - nbrBonus();
        }

        public boolean estMalus() {
            return this.ordinal() >= nbrBonus();
        }
    }

    //Coordonnée X du centre du bonus.
    private double x;

    //Coordonnée Y du centre du bonus.
    private double y;

    private final TypeBonus type;

    //true tant que le bonus n'a pas encore été collecté (touché par le ballon).
    private boolean actif;

    // Constructeur
    public Bonus(double x, double y, TypeBonus type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.actif = true;
    }

    // Détection de collision
    /**
     * Vérifie si le ballon touche ce bonus (collision circulaire).
     * Renvoie false si le bonus a déjà été collecté.
     *
     * d = sqrt((xB - xBonus)² + (yB - yBonus)²)
     * collision si d ≤ rayonBallon + RAYON
     *
     * return: true si collision active détectée.
     */
    public boolean estTouche(double xBallon, double yBallon, int rayonBallon) {
        if (!actif)
            return false;
        double dx = xBallon - this.x;
        double dy = yBallon - this.y;
        double distance = Math.sqrt(dx * dx + dy * dy);
        return distance <= (rayonBallon + RAYON);
    }

    // Application de l'effet
    public int getModificateurScore() {
        return switch (type) {
            case POINTS_BONUS -> 1;
            case POINTS_MALUS -> -1;
            default -> 0;
        };
    }

    public double getFacteurTrajectoire() {
        return switch (type) {
            case TRAJECTOIRE_AIDE -> 1.2;
            case TRAJECTOIRE_GENE -> 0.8;
            default -> 1.0;
        };
    }

    public double getFacteurVitessePanier() {
        return switch (type) {
            case VITESSE_PANIER -> 1.5;
            case RALENTISSEMENT_PANIER -> 0.6;
            default -> 1.0;
        };
    }

    public boolean genereUnMur() {
        return type == TypeBonus.MUR_OBSTACLE;
    }

    public void collecter() {
        this.actif = false;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public TypeBonus getType() {
        return type;
    }

    public boolean isActif() {
        return actif;
    }

    @Override
    public String toString() {
        return String.format("Bonus[type=%s, x=%.1f, y=%.1f, actif=%b]",
                type, x, y, actif);
    }
}
