package Modele;

public class Joueur {
   private String nom;
   private int score;
   private boolean estActif;
   private Ballon ballon;
   private double x;
   private double y;
   private double angle;
   private double puissance;

   public Joueur(String var1, double var2, double var4) {
      this.nom = var1;
      this.x = var2;
      this.y = var4;
      this.score = 0;
      this.estActif = false;
      this.angle = (double)0.0F;
      this.puissance = (double)0.0F;
      this.ballon = new Ballon(var2, var4);
   }

   public void setActif(boolean var1) {
      this.estActif = var1;
   }

   public void marquerPanier(int var1) {
      this.score += var1;
   }

   public int getScore() {
      return this.score;
   }

   public boolean isActif() {
      return this.estActif;
   }

   public void setTir(double var1, double var3) {
      this.angle = var1;
      this.puissance = var3;
   }

   public String getNom() {
      return this.nom;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getAngle() {
      return this.angle;
   }

   public double getPuissance() {
      return this.puissance;
   }

   public Ballon getBallon() {
      return this.ballon;
   }
}
