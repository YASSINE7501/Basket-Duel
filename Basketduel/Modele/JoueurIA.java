package Modele;

public class JoueurIA extends Joueur {
    private double niveauErreur;

    public JoueurIA(String nom, double niveauErreur) {
        super();
        this.setNom(nom);
        this.niveauErreur = niveauErreur;
    }

    public void preparerTirAuto(Panier panier) {
        double[] tir = IntelligenceArtificielle.calculerTir(this.getX(), this.getY(), panier.getX(), panier.getY(), niveauErreur);
        this.setTir(tir[0], tir[1]);
    }
}
