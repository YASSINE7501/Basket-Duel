package Modele;

public class Partie {
    private Joueur j1;
    private Joueur j2;

    public Partie(Joueur j1, Joueur j2) {
        this.j1 = j1;
        this.j2 = j2;
        this.j1.setActif(true);
        this.j2.setActif(false);
    }

    public void switchTour() {
        if (j1.isActif()) {
            j1.setActif(false);
            j2.setActif(true);
        } else {
            j1.setActif(true);
            j2.setActif(false);
        }
    }

    public void executerTir() {
        Joueur actuel;
        if (j1.isActif()) {
            actuel = j1;
        } else {
            actuel = j2;
        }
        actuel.getBallon().setPosition(actuel.getX(), actuel.getY());
        actuel.getBallon().tirer(actuel.getAngle(), actuel.getPuissance()); 
        System.out.println(actuel.getNom() + " effectue son tir !");
    }

    public boolean verifierwiner(Joueur j, int score) {
        if (j.getScore() >= score) {
            return true;
        }
        return false;
    }

    public void setpseudoJoueur(String pseudo) {
        j1.setNom(pseudo);
    }


}
