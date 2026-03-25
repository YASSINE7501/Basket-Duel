package Controlleur;

import Modele.Ballon;
import Modele.Bonus;
import Modele.IntelligenceArtificielle;
import Modele.Panier;
import Modele.Terrain;
import java.util.List;

public class ControleurJeu {
    private static final int nb_bonus_par_tour = 3;
    private static final double vitesse_panier_base = 3.0;
    private static final double puissance_max = 1200.0;// puissance maximale autorisée
    private static final double puissance_min = 100.0;// puissance minimale autorisée

    private final Terrain terrain;
    private final Ballon ballon;
    private Panier panier;
    private List<Bonus> bonusList;

    private int scoreJoueur;
    private int scoreIA;
    private int tourCourant;
    private final int toursTotal;

    private boolean partieTerminee;
    private double facteurTrajectoireProchainTir;
    private double facteurVitessePanier;

    // Système de visée (flèche + jauge)

    // Les trois phases du tir.
    public enum PhaseVisee {
        VISEE, PUISSANCE, TIR
    }

    // Phase courante du système de visée.
    private PhaseVisee phaseVisee;

    // Angle courant de la flèche (degrés, 0° = horizontal droit).
    private double angleCourant;

    // Vitesse de rotation de la flèche (degrés/s).
    private static final double VITESSE_ROTATION = 90.0;

    // Puissance courante de la jauge (0.0 à 1.0).
    private double jaugePuissance;

    // Vitesse de remplissage de la jauge (fraction/s, 1.0 = plein en 1 s).
    private static final double VITESSE_JAUGE = 0.7;

    // Sens courant de l'oscillation : +1 ou -1.
    private int sensOscillation;

    // IA qui contrôle le mouvement du panier (1=Facile, 2=Moyen, 3=Difficile).
    private final IntelligenceArtificielle ia;

    public ControleurJeu(int largeurFenetre, int hauteurFenetre, int niveauIA, int toursTotal) {
        this.terrain = new Terrain(largeurFenetre, hauteurFenetre);
        this.ballon = new Ballon(terrain.getXSpawnBallon(), terrain.getYSpawnBallon());
        this.ia = new IntelligenceArtificielle(niveauIA);

        this.scoreJoueur = 0;
        this.scoreIA = 0;
        this.tourCourant = 1;
        this.toursTotal = toursTotal;
        this.partieTerminee = false;

        this.facteurTrajectoireProchainTir = 1.0;
        this.facteurVitessePanier = 1.0;

        // Visée
        this.phaseVisee = PhaseVisee.VISEE;
        this.angleCourant = 10.0;
        this.jaugePuissance = 0.0;
        this.sensOscillation = 1;

        demarrerNouveauTour();// prepare le premier tour
    }

    public void mettreAJour(double dt) {
        if (partieTerminee)
            return;

        // Système de visée (ballon immobile)
        if (!ballon.isEnMouvement()) {
            switch (phaseVisee) {
                case VISEE -> {
                    // La flèche oscille entre 5° et 85°
                    angleCourant += VITESSE_ROTATION * sensOscillation * dt;
                    if (angleCourant >= 85.0) {
                        angleCourant = 85.0;
                        sensOscillation = -1;
                    }
                    if (angleCourant <= 5.0) {
                        angleCourant = 5.0;
                        sensOscillation = 1;
                    }
                }
                case PUISSANCE -> {
                    // La jauge monte et descend en yo-yo
                    jaugePuissance += VITESSE_JAUGE * sensOscillation * dt;
                    if (jaugePuissance >= 1.0) {
                        jaugePuissance = 1.0;
                        sensOscillation = -1;
                    }
                    if (jaugePuissance <= 0.0) {
                        jaugePuissance = 0.0;
                        sensOscillation = 1;
                    }
                }
                case TIR -> {
                } // ballon déjà parti
            }
            return;
        }

        ballon.mettreAJour(dt);// 1. Avancer la physique du ballon

        if (ballon.isEnMouvement()) {
            // 2. Collision ballon et panier -> point marqué !
            if (panier.estTouche(ballon.getX(), ballon.getY(), Ballon.RAYON)) {
                scoreJoueur++;
                System.out.println("Panier ! Score joueur : " + scoreJoueur);
                finDeTir(true);
                return;
            }

            // 3. Collision ballon et bonus
            for (Bonus b : bonusList) {
                if (b.isActif() && b.estTouche(ballon.getX(), ballon.getY(), Ballon.RAYON)) {
                    appliquerBonus(b);
                    b.collecter();
                    System.out.println("Bonus collecté : " + b.getType());
                }
            }

            // 4. Ballon hors terrain -> tir raté
            if (terrain.ballonAtteinSol(ballon) || terrain.ballonHorsLimites(ballon)) {
                System.out.println("Tir raté !");
                finDeTir(false);
                return;
            }

            deplacerPanierIA(dt);// 5. Déplacement du panier par l'IA
        }
    }

    // Appelée à chaque appui du joueur (espace).
    // - 1er appui : verrouille l'angle, démarre la jauge de puissance.
    // - 2e appui : verrouille la puissance et tire.
    public void appuyer() {
        if (ballon.isEnMouvement() || partieTerminee)
            return;

        switch (phaseVisee) {
            case VISEE -> {
                // Angle verrouillé, on passe à la jauge
                phaseVisee = PhaseVisee.PUISSANCE;
                jaugePuissance = 0.0;
                sensOscillation = 1;
                System.out.printf("Angle verrouillé : %.1f°%n", angleCourant);
            }
            case PUISSANCE -> {
                // Puissance verrouillée
                double puissanceChoisie = puissance_min
                        + jaugePuissance * (puissance_max - puissance_min);
                double puissanceFinale = puissanceChoisie * facteurTrajectoireProchainTir;
                puissanceFinale = Math.max(puissance_min, Math.min(puissance_max, puissanceFinale));

                ballon.tirer(angleCourant, puissanceFinale);
                facteurTrajectoireProchainTir = 1.0;
                phaseVisee = PhaseVisee.TIR;
                System.out.printf("Tir : angle=%.1f°  puissance=%.1f%n", angleCourant, puissanceFinale);
            }
            case TIR -> {
            } // on attend que le ballon retombe
        }
    }

    // Gestion du panier (IA)

    // Demande à l'IA de calculer le prochain tir.
    public void tirerIA() {
        if (ballon.isEnMouvement() || partieTerminee)
            return;

        double[] tir = ia.calculerTir(
                ballon.getX(), ballon.getY(),
                panier.getX(), panier.getY());
        double angle = tir[0];
        double puissance = tir[1];

        ballon.tirer(angle, puissance);
        System.out.printf("IA tire : angle=%.1f°  puissance=%.1f%n", angle, puissance);
    }

    // Déplace le panier selon la logique de l'IA à chaque tick.
    private void deplacerPanierIA(double dt) {
        double vitesse = vitesse_panier_base * facteurVitessePanier;

        double bx = ballon.getX();
        double by = ballon.getY();
        double px = panier.getX();
        double py = panier.getY();

        // L'IA essaie de s'éloigner horizontalement du ballon
        if (bx < px) {
            panier.deplacerDroite(vitesse);
        } else {
            panier.deplacerGauche(vitesse);
        }

        // L'IA essaie aussi de s'éloigner verticalement
        if (by < py) {
            panier.deplacerBas(vitesse);
        } else {
            panier.deplacerHaut(vitesse);
        }
    }

    // Application des bonus

    // Applique l'effet d'un bonus collecté à l'état du jeu.
    private void appliquerBonus(Bonus b) {
        // Modificateur de score
        scoreJoueur += b.getModificateurScore();
        if (scoreJoueur < 0)
            scoreJoueur = 0; // pas de score négatif

        // Facteur de trajectoire pour le prochain tir
        facteurTrajectoireProchainTir *= b.getFacteurTrajectoire();

        // Facteur de vitesse du panier (effet immédiat sur le déplacement)
        facteurVitessePanier *= b.getFacteurVitessePanier();

        // Mur obstacle : pour l'instant on signale juste (la Vue peut l'afficher)
        if (b.genereUnMur()) {
            System.out.println("[Bonus] Mur obstacle généré !");
            // TODO: créer un objet Mur dans le Modèle et le transmettre à la Vue
        }
    }

    // Fin de tir et gestion des tours

    // Appelée après chaque tir (réussi ou raté).
    // Remet le ballon en position de spawn et démarre le tour suivant.
    private void finDeTir(boolean panierMarque) {
        ballon.reinitialiser(terrain.getXSpawnBallon(), terrain.getYSpawnBallon());
        facteurVitessePanier = 1.0;

        // Réinitialise le système de visée pour le prochain tour
        phaseVisee = PhaseVisee.VISEE;
        angleCourant = 10.0;
        jaugePuissance = 0.0;
        sensOscillation = 1;

        if (tourCourant >= toursTotal) {
            partieTerminee = true;
            afficherResultatFinal();
        } else {
            tourCourant++;
            demarrerNouveauTour();
        }
    }

    // Prépare un nouveau tour : génère un nouveau panier et de nouveaux bonus.
    private void demarrerNouveauTour() {
        this.panier = terrain.genererPanier();
        this.bonusList = terrain.genererBonus(panier, nb_bonus_par_tour);
        System.out.println("=== Tour " + tourCourant + "/" + toursTotal + " ===");
        System.out.println("Panier : " + panier);
        System.out.println("Bonus générés : " + bonusList.size());
    }

    // Affiche le résultat final de la partie dans la console.
    private void afficherResultatFinal() {
        System.out.println("=== PARTIE TERMINÉE ===");
        System.out.println("Score joueur : " + scoreJoueur);
        System.out.println("Score IA     : " + scoreIA);
        if (scoreJoueur > scoreIA)
            System.out.println("Résultat : VICTOIRE du joueur !");
        else if (scoreJoueur < scoreIA)
            System.out.println("Résultat : VICTOIRE de l'IA !");
        else
            System.out.println("Résultat : ÉGALITÉ !");
    }

    // Getters (pour la Vue)

    // Le terrain de jeu (dimensions et limites).
    public Terrain getTerrain() {
        return terrain;
    }

    // Le ballon en cours de jeu.
    public Ballon getBallon() {
        return ballon;
    }

    // Le panier cible du tour courant.
    public Panier getPanier() {
        return panier;
    }

    // La liste des bonus/malus actifs.
    public List<Bonus> getBonusList() {
        return bonusList;
    }

    // Score actuel du joueur humain.
    public int getScoreJoueur() {
        return scoreJoueur;
    }

    // Score actuel de l'IA.
    public int getScoreIA() {
        return scoreIA;
    }

    // Numéro du tour courant.
    public int getTourCourant() {
        return tourCourant;
    }

    // Nombre total de tours de la partie.
    public int getToursTotal() {
        return toursTotal;
    }

    // true si la partie est terminée.
    public boolean isPartieTerminee() {
        return partieTerminee;
    }

    // Facteur de trajectoire qui sera appliqué au prochain tir.
    public double getFacteurTrajectoireProchainTir() {
        return facteurTrajectoireProchainTir;
    }

    // --- Getters système de visée (utilisés par TerrainVue) ---

    // Phase courante de visée (VISEE, PUISSANCE, TIR).
    public PhaseVisee getPhaseVisee() {
        return phaseVisee;
    }

    // Angle courant de la flèche de visée (degrés).
    public double getAngleCourant() {
        return angleCourant;
    }

    // Puissance courante de la jauge (0.0 à 1.0).
    public double getJaugePuissance() {
        return jaugePuissance;
    }
}
