package Controlleur;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ReseauManager {

    private ServerSocket serveur;
    private Socket client;
    private DataOutputStream sortie;
    private DataInputStream entree;

    // Pour créer une partie (ordinateur 1)
    public boolean hebergerActif(int port) {
        try {
            serveur = new ServerSocket(port);
            System.out.println("Attente de connexion du joueur adverse sur le port " + port);
            
            client = serveur.accept(); // on attend que le joueur 2 se connecte (le jeu est bloqué ici)
            System.out.println("Joueur 2 est connecté !");
            
            sortie = new DataOutputStream(client.getOutputStream());
            entree = new DataInputStream(client.getInputStream());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Pour rejoindre une partie deja créée (ordinateur 2)
    public boolean rejoindrePartie(String ip, int port) {
        try {
            client = new Socket(ip, port);
            System.out.println("Connecté au joueur 1 !");
            
            sortie = new DataOutputStream(client.getOutputStream());
            entree = new DataInputStream(client.getInputStream());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Le controleur jeu appelle ca quand on la boule part
    public void envoyerTir(double a, double f) {
        try {
            sortie.writeDouble(a);
            sortie.writeDouble(f);
            System.out.println("On vient d'envoyer le tir : angle=" + a + " force=" + f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Reception des infos du tir adverse (bloquant aussi)
    public double[] recevoirTir() {
        try {
            System.out.println("Attente du tir de l'autre joueur...");
            double angle = entree.readDouble();
            double force = entree.readDouble();
            
            double[] tir = new double[2];
            tir[0] = angle;
            tir[1] = force;
            System.out.println("Tir ennemi reçu : angle=" + angle + " force=" + force);
            return tir;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Fermeture propre pour eviter les problemes de ports dejà utilisés
    public void fermerTout() {
        try {
            if (entree != null) entree.close();
            if (sortie != null) sortie.close();
            if (client != null) client.close();
            if (serveur != null) serveur.close();
        } catch (Exception e) {
            // on ignore l'erreur
        }
    }
}
