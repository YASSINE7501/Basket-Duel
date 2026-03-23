package Modele;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Terrain {

    /** Constante de gravité (pixels/s²). Identique à Ballon.GRAVITE. */
    public static final double GRAVITE = 9.81;

    // Dimensions du terrain
    /** Largeur totale de la fenêtre de jeu (pixels). */
    private final int largeur;

    /** Hauteur totale de la fenêtre de jeu (pixels). */
    private final int hauteur;

    // Limites physiques
    /** Ordonnée Y du sol (basse limite du terrain). */
    private final int ySol;

    /** Ordonnée Y du plafond (haute limite du terrain). */
    private final int yPlafond;

    /** Abscisse X du mur gauche. */
    private final int xMurGauche;

    /** Abscisse X du mur droit. */
    private final int xMurDroit;

    // Coordonnées de spawn du ballon
    /**
     * Position X fixe de spawn du ballon (côté gauche).
     */
    private final int xSpawnBallon;

    /** Position Y fixe de spawn du ballon. */
    private final int ySpawnBallon;

    // Zone réservée au panier
    /**
     * Le panier est toujours placé dans la moitié droite de l'écran
     * pour garantir que le tir reste physiquement possible.
     */
    private final int xMinPanier;

    // Marge de sécurité entre entités
    /**
     * Distance minimale (pixels) entre le panier et un bonus pour éviter le
     * chevauchement.
     */
    private static final int MARGE_SECURITE = 60;

    // Générateur aléatoire
    private final Random random;

    // Constructeur
    /**
     * Crée le terrain avec les dimensions de la fenêtre de jeu.
     *
     * largeur: Largeur de la fenêtre de jeu (pixels).
     * hauteur: Hauteur de la fenêtre de jeu (pixels).
     */
    public Terrain(int largeur, int hauteur) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.yPlafond = 0;
        this.ySol = hauteur - 10; // 10px de marge en bas
        this.xMurGauche = 0;
        this.xMurDroit = largeur;
        this.xSpawnBallon = largeur / 8; // approximativement 12% depuis la gauche
        this.ySpawnBallon = hauteur / 2; // mi-hauteur
        this.xMinPanier = largeur / 2; // moitié droite uniquement
        this.random = new Random();
    }

    // Génération aléatoire du Panier
    /**
     * Génère un Panier à une position aléatoire dans la moitié droite
     * de l'écran, à une hauteur atteignable.
     *
     * Limites de déplacement du panier transmises en même temps
     *
     * return: Un nouveau Panier prêt à être utilisé pour ce tour.
     */
    public Panier genererPanier() {
        int marge = Panier.RAYON + 10;
        int xMin = xMinPanier + marge;
        int xMax = xMurDroit - marge;
        int yMin = yPlafond + marge + 40; // espace sous le plafond
        int yMax = ySol - marge - 60; // espace au-dessus du sol

        double x = xMin + random.nextInt(xMax - xMin);
        double y = yMin + random.nextInt(yMax - yMin);

        return new Panier(
                x, y,
                xMinPanier + marge, xMurDroit - marge, // limites horizontales
                yPlafond + marge, ySol - marge // limites verticales
        );
    }

    // Génération aléatoire des Bonus
    /**
     * Génère une liste de bonus répartis aléatoirement dans l'espace
     * aérien situé entre le joueur et le panier.
     *
     * Règle de sécurité : aucun bonus ne peut apparaître à moins de MARGE_SECURITE
     * pixels du centre du panier.
     *
     * panier: Le panier déjà généré pour ce tour.
     * nombreMax: Nombre maximum de bonus à générer.
     * return: Liste de Bonus prêts à être utilisés.
     */
    public List<Bonus> genererBonus(Panier panier, int nombreMax) {
        List<Bonus> liste = new ArrayList<>();

        int xMin = xSpawnBallon + 50; // À droite du spawn ballon
        int xMax = (int) panier.getX() - Panier.RAYON - MARGE_SECURITE;
        int yMin = yPlafond + Bonus.RAYON + 20;
        int yMax = ySol - Bonus.RAYON - 20;

        if (xMax <= xMin || yMax <= yMin)
            return liste; // zone trop petite

        int tentativesMax = nombreMax * 10;
        int tentatives = 0;

        while (liste.size() < nombreMax && tentatives < tentativesMax) {
            tentatives++;

            double bx = xMin + random.nextInt(xMax - xMin);
            double by = yMin + random.nextInt(yMax - yMin);

            // Vérification de non-chevauchement avec le panier
            double dx = bx - panier.getX();
            double dy = by - panier.getY();
            if (Math.sqrt(dx * dx + dy * dy) < MARGE_SECURITE)
                continue;

            // Vérification de non-chevauchement avec les autres bonus
            boolean chevauchement = false;
            for (Bonus b : liste) {
                double ddx = bx - b.getX();
                double ddy = by - b.getY();
                if (Math.sqrt(ddx * ddx + ddy * ddy) < MARGE_SECURITE) {
                    chevauchement = true;
                    break;
                }
            }
            if (chevauchement)
                continue; // on passe au bonus suivant

            // Choix aléatoire du type (2/4 bonus, 2/4 malus)
            Bonus.TypeBonus type = (random.nextInt(4) < 2)
                    ? Bonus.TypeBonus.values()[random.nextInt(Bonus.TypeBonus.nbrBonus())]
                    : Bonus.TypeBonus.values()[Bonus.TypeBonus.nbrBonus() + random.nextInt(Bonus.TypeBonus.nbrMalus())];

            liste.add(new Bonus(bx, by, type));
        }

        return liste;
    }

    // Détection collision avec le sol
    /**
     * Vérifie si le ballon a atteint le sol (tir raté).
     * Condition : le bas du ballon (y + rayon) dépasse ySol.
     *
     * ballon: Le ballon en cours de vol.
     * return: true si le ballon a touché ou dépassé le sol.
     */
    public boolean ballonAtteinSol(Ballon ballon) {
        return (ballon.getY() + Ballon.RAYON) >= ySol;
    }

    /**
     * Vérifie si le ballon a quitté les limites latérales du terrain.
     *
     * ballon: Le ballon en cours de vol.
     * return: true si le ballon est sorti par la gauche ou la droite.
     */
    public boolean ballonHorsLimites(Ballon ballon) {
        return ballon.getX() - Ballon.RAYON < xMurGauche
                || ballon.getX() + Ballon.RAYON > xMurDroit
                || ballon.getY() + Ballon.RAYON < yPlafond;
    }

    // Getters
    /** return: Largeur du terrain (pixels). */
    public int getLargeur() {
        return largeur;
    }

    /** return: Hauteur du terrain (pixels). */
    public int getHauteur() {
        return hauteur;
    }

    /** return: Ordonnée Y du sol. */
    public int getYSol() {
        return ySol;
    }

    /** return: Ordonnée Y du plafond. */
    public int getYPlafond() {
        return yPlafond;
    }

    /** return: Abscisse X du mur gauche. */
    public int getXMurGauche() {
        return xMurGauche;
    }

    /** return: Abscisse X du mur droit. */
    public int getXMurDroit() {
        return xMurDroit;
    }

    /** return: Coordonnée X de spawn du ballon. */
    public int getXSpawnBallon() {
        return xSpawnBallon;
    }

    /** return: Coordonnée Y de spawn du ballon. */
    public int getYSpawnBallon() {
        return ySpawnBallon;
    }

    @Override
    public String toString() {
        return String.format("Terrain[%dx%d | sol=%d | spawn=(%d,%d)]",
                largeur, hauteur, ySol, xSpawnBallon, ySpawnBallon);
    }
}
