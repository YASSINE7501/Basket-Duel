package Modele;

/**
 * Types:
 * BONUS (effets positifs): POINTS_BONUS, TRAJECTOIRE_AIDE
 * MALUS (effets négatifs): POINTS_MALUS, TRAJECTOIRE_GENE, VITESSE_PANIER,
 * MURE_OBSTACLE
 */
public class Bonus {

    /** Rayon de la hitbox circulaire du bonus (pixels). */
    public static final int RAYON = 18;

    // Enumération des types de bonus et malus
    /**
     * Tous les types de bonus et malus du jeu.
     * Ordre: d'abord les BONUS positifs, puis les MALUS négatifs.
     */
    public enum TypeBonus {
        // Bonus positifs
        /** Donne des points supplémentaires au joueur. */
        POINTS_BONUS,
        /** Améliore la trajectoire du ballon. */
        TRAJECTOIRE_AIDE,
        /** Ralentit le panier (avantage indirect pour le tireur). */
        RALENTISSEMENT_PANIER,

        // Malus négatifs
        /** Retire des points au joueur. */
        POINTS_MALUS,
        /** Perturbe la trajectoire du ballon. */
        TRAJECTOIRE_GENE,
        /** Augmente la vitesse de déplacement du panier. */
        VITESSE_PANIER,
        /** Fait apparaître un mur obstacle temporaire sur la trajectoire. */
        MUR_OBSTACLE;

        // Compteurs statiques
        /** Nombre de types de BONUS positifs. */
        public static int nbrBonus() {
            return 3; // POINTS_BONUS, TRAJECTOIRE_AIDE, RALENTISSEMENT_PANIER
        }

        /** Nombre de types de MALUS. */
        public static int nbrMalus() {
            return TypeBonus.values().length - nbrBonus();
        }

        /** return true si ce type est un malus. */
        public boolean estMalus() {
            return this.ordinal() >= nbrBonus();
        }
    }

    /** Coordonnée X du centre du bonus. */
    private double x;

    /** Coordonnée Y du centre du bonus. */
    private double y;

    /** Type de cet objet bonus/malus. */
    private final TypeBonus type;

    /**
     * true tant que le bonus n'a pas encore été collecté (touché par le ballon).
     */
    private boolean actif;

    // Constructeur
    /**
     * Crée un bonus à la position et du type spécifiés.
     *
     * x: Coordonnée X du centre du bonus (pixels).
     * y: Coordonnée Y du centre du bonus (pixels).
     * type: Type de cet objet (bonus ou malus).
     */
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
     * xBallon: Coordonnée X du centre du ballon.
     * yBallon: Coordonnée Y du centre du ballon.
     * rayonBallon: Rayon du ballon en pixels.
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
    /**
     * Renvoie la valeur de points à ajouter (positive) ou retirer (négative)
     * lorsque le ballon collecte ce bonus.
     * Le Contrôleur sera responsable d'appliquer la valeur au score.
     * return Modificateur de score: +1 pour POINTS_BONUS, -1 pour POINTS_MALUS, 0
     * sinon.
     */
    public int getModificateurScore() {
        return switch (type) {
            case POINTS_BONUS -> 1;
            case POINTS_MALUS -> -1;
            default -> 0;
        };
    }

    /**
     * Renvoie le facteur multiplicatif de perturbation de la trajectoire.
     * Le Contrôleur applique ce facteur à la puissance ou à l'angle du tir suivant.
     * 
     * return: Facteur de trajectoire : 0.8 (réduction) pour TRAJECTOIRE_GENE, 1.2
     * (amélioration) pour TRAJECTOIRE_AIDE, 1.0 (aucun effet) sinon.
     */
    public double getFacteurTrajectoire() {
        return switch (type) {
            case TRAJECTOIRE_AIDE -> 1.2;
            case TRAJECTOIRE_GENE -> 0.8;
            default -> 1.0;
        };
    }

    /**
     * Renvoie le facteur multiplicatif de vitesse du panier.
     * return: Facteur de vitesse : 1.5 pour VITESSE_PANIER (panier accéléré), 0.6
     * pour RALENTISSEMENT_PANIER (panier ralenti), 1.0 sinon.
     */
    public double getFacteurVitessePanier() {
        return switch (type) {
            case VITESSE_PANIER -> 1.5;
            case RALENTISSEMENT_PANIER -> 0.6;
            default -> 1.0;
        };
    }

    /**
     * Indique si ce bonus génère un mur obstacle sur la trajectoire.
     * Le Contrôleur crée le mur en conséquence.
     * return: true si le type est MUR_OBSTACLE.
     */
    public boolean genereUnMur() {
        return type == TypeBonus.MUR_OBSTACLE;
    }

    // Getters et Setters
    /**
     * Marque le bonus comme collecté (désactive sa hitbox).
     * Appelé par le Contrôleur dès qu'une collision est confirmée.
     */
    public void collecter() {
        this.actif = false;
    }

    /** return: Coordonnée X du centre du bonus. */
    public double getX() {
        return x;
    }

    /** return: Coordonnée Y du centre du bonus. */
    public double getY() {
        return y;
    }

    /** return: Type de cet objet bonus/malus. */
    public TypeBonus getType() {
        return type;
    }

    /** return: true si le bonus est encore disponible (non collecté). */
    public boolean isActif() {
        return actif;
    }

    @Override
    public String toString() {
        return String.format("Bonus[type=%s, x=%.1f, y=%.1f, actif=%b]",
                type, x, y, actif);
    }
}
