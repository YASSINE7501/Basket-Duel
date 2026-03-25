package Vue;

import Controlleur.ControleurJeu;
import Controlleur.ControleurJeu.PhaseVisee;
import Modele.Ballon;
import Modele.Bonus;
import Modele.Panier;
import Modele.Terrain;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

public class TerrainVue extends JPanel {

    private final ControleurJeu controleur;

    // Couleurs
    private static final Color COULEUR_FOND = new Color(30, 30, 50);
    private static final Color COULEUR_SOL = new Color(80, 50, 20);
    private static final Color COULEUR_BALLON = new Color(220, 100, 30);
    private static final Color COULEUR_PANIER = new Color(255, 200, 0);
    private static final Color COULEUR_FLECHE = new Color(50, 220, 120);
    private static final Color COULEUR_JAUGE_FOND = new Color(60, 60, 80);
    private static final Color COULEUR_JAUGE_REMPLI = new Color(50, 200, 80);
    private static final Color COULEUR_JAUGE_BORD = Color.WHITE;
    private static final Color COULEUR_TEXTE = Color.WHITE;

    // Dimensions de la jauge (pixels)
    private static final int JAUGE_X = 30;
    private static final int JAUGE_Y = 50;
    private static final int JAUGE_LARG = 20;
    private static final int JAUGE_HAUT = 150;

    // Longueur de la flèche de visée (pixels)
    private static final int LONGUEUR_FLECHE = 80;

    public TerrainVue(ControleurJeu controleur) {
        this.controleur = controleur;
        setPreferredSize(new Dimension(
                controleur.getTerrain().getLargeur(),
                controleur.getTerrain().getHauteur()));
        setBackground(COULEUR_FOND);
        setFocusable(true);

        // Touche ESPACE → appuyer()
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    controleur.appuyer();
                }
            }
        });
    }

    // paintComponent sert a dessiner le terrain
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        dessinerSol(g2);
        dessinerBonus(g2);
        dessinerPanier(g2);
        dessinerBallon(g2);

        // Flèche et jauge uniquement quand le ballon est immobile
        if (!controleur.getBallon().isEnMouvement()) {
            dessinerFlecheVisee(g2);
            if (controleur.getPhaseVisee() == PhaseVisee.PUISSANCE) {
                dessinerJaugePuissance(g2);
            }
        }

        dessinerHUD(g2);
    }

    // dessiner le sol
    private void dessinerSol(Graphics2D g2) {
        Terrain t = controleur.getTerrain();
        g2.setColor(COULEUR_SOL);
        g2.fillRect(0, t.getYSol(), t.getLargeur(), t.getHauteur() - t.getYSol());
    }

    // dessiner le ballon
    private void dessinerBallon(Graphics2D g2) {
        Ballon b = controleur.getBallon();
        int r = Ballon.RAYON;
        int bx = (int) b.getX() - r;
        int by = (int) b.getY() - r;

        g2.setColor(COULEUR_BALLON);
        g2.fillOval(bx, by, r * 2, r * 2);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(bx, by, r * 2, r * 2);
    }

    // dessiner le panier
    private void dessinerPanier(Graphics2D g2) {
        Panier p = controleur.getPanier();
        int r = Panier.RAYON;
        int px = (int) p.getX() - r;
        int py = (int) p.getY() - r;

        g2.setColor(COULEUR_PANIER);
        g2.setStroke(new BasicStroke(4));
        g2.drawOval(px, py, r * 2, r * 2);

        // Petit filet symbolique
        g2.setStroke(new BasicStroke(2));
        g2.drawLine((int) p.getX() - r / 2, (int) p.getY() + r,
                (int) p.getX(), (int) p.getY() + r + 20);
        g2.drawLine((int) p.getX() + r / 2, (int) p.getY() + r,
                (int) p.getX(), (int) p.getY() + r + 20);
    }

    // dessiner les bonus/malus
    private void dessinerBonus(Graphics2D g2) {
        for (Bonus b : controleur.getBonusList()) {
            if (!b.isActif())
                continue;
            int bx = (int) b.getX();
            int by = (int) b.getY();
            int r = Bonus.RAYON;

            Color couleur = b.getType().estMalus()
                    ? new Color(220, 60, 60) // rouge : malus
                    : new Color(60, 180, 220); // bleu : bonus

            g2.setColor(couleur);
            g2.fillOval(bx - r, by - r, r * 2, r * 2);
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(bx - r, by - r, r * 2, r * 2);

            // Lettre indicatrice
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            String lettre = b.getType().estMalus() ? "M" : "B";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(lettre,
                    bx - fm.stringWidth(lettre) / 2,
                    by + fm.getAscent() / 2);
        }
    }

    // dessiner la flèche de visée
    private void dessinerFlecheVisee(Graphics2D g2) {
        Ballon b = controleur.getBallon();
        double angle = controleur.getAngleCourant(); // degrés
        double rad = Math.toRadians(angle); // l'axe Y écran est vers le bas

        int origX = (int) b.getX();
        int origY = (int) b.getY();
        int tipX = origX + (int) (LONGUEUR_FLECHE * Math.cos(rad));
        int tipY = origY - (int) (LONGUEUR_FLECHE * Math.sin(rad)); // inversion Y

        g2.setColor(COULEUR_FLECHE);
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(origX, origY, tipX, tipY);

        // Pointe de flèche
        dessinerPointe(g2, origX, origY, tipX, tipY);

        // Affichage de l'angle
        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.setColor(COULEUR_TEXTE);
        g2.drawString(String.format("%.0f°", angle), tipX + 8, tipY - 5);
    }

    /** Dessine la pointe triangulaire d'une flèche. */
    private void dessinerPointe(Graphics2D g2, int ox, int oy, int tx, int ty) {
        double angle = Math.atan2(ty - oy, tx - ox);
        int taillePointe = 12;
        int x1 = (int) (tx - taillePointe * Math.cos(angle - 0.4));
        int y1 = (int) (ty - taillePointe * Math.sin(angle - 0.4));
        int x2 = (int) (tx - taillePointe * Math.cos(angle + 0.4));
        int y2 = (int) (ty - taillePointe * Math.sin(angle + 0.4));
        g2.fillPolygon(new int[] { tx, x1, x2 }, new int[] { ty, y1, y2 }, 3);
    }

    // dessiner la jauge de puissance
    private void dessinerJaugePuissance(Graphics2D g2) {
        double ratio = controleur.getJaugePuissance(); // 0.0 à 1.0
        int niveau = (int) (JAUGE_HAUT * ratio);

        // Fond de la jauge
        g2.setColor(COULEUR_JAUGE_FOND);
        g2.fillRect(JAUGE_X, JAUGE_Y, JAUGE_LARG, JAUGE_HAUT);

        // Remplissage (du bas vers le haut)
        g2.setColor(COULEUR_JAUGE_REMPLI);
        g2.fillRect(JAUGE_X, JAUGE_Y + JAUGE_HAUT - niveau, JAUGE_LARG, niveau);

        // Bordure
        g2.setColor(COULEUR_JAUGE_BORD);
        g2.setStroke(new BasicStroke(2));
        g2.drawRect(JAUGE_X, JAUGE_Y, JAUGE_LARG, JAUGE_HAUT);

        // Texte "PUISSANCE" au-dessus
        g2.setFont(new Font("Arial", Font.BOLD, 11));
        g2.setColor(COULEUR_TEXTE);
        g2.drawString("PWR", JAUGE_X, JAUGE_Y - 5);

        // Pourcentage
        g2.drawString(String.format("%d%%", (int) (ratio * 100)), JAUGE_X, JAUGE_Y + JAUGE_HAUT + 15);
    }

    private void dessinerHUD(Graphics2D g2) {
        g2.setFont(new Font("Arial", Font.BOLD, 16));
        g2.setColor(COULEUR_TEXTE);

        // Scores
        g2.drawString("Joueur : " + controleur.getScoreJoueur(), 10, 20);
        g2.drawString("IA : " + controleur.getScoreIA(), 150, 20);
        g2.drawString("Tour : " + controleur.getTourCourant()
                + "/" + controleur.getToursTotal(), 280, 20);

        // Instruction selon la phase
        g2.setFont(new Font("Arial", Font.ITALIC, 13));
        g2.setColor(new Color(200, 200, 200));
        String instruction = switch (controleur.getPhaseVisee()) {
            case VISEE -> "ESPACE : verrouiller l'angle";
            case PUISSANCE -> "ESPACE : verrouiller la puissance";
            case TIR -> "Ballon en vol...";
        };
        if (!controleur.isPartieTerminee()) {
            g2.drawString(instruction,
                    controleur.getTerrain().getLargeur() / 2 - 80,
                    controleur.getTerrain().getHauteur() - 10);
        } else {
            g2.setFont(new Font("Arial", Font.BOLD, 22));
            g2.setColor(Color.YELLOW);
            g2.drawString("PARTIE TERMINÉE  —  Joueur : "
                    + controleur.getScoreJoueur() + "  IA : " + controleur.getScoreIA(),
                    50, controleur.getTerrain().getHauteur() / 2);
        }
    }
}
