package Vue;
import javax.swing.JFrame;

public class FenetrePrincipale extends JFrame {
    public FenetrePrincipale() {
        this.setTitle("Basket Duel");
        this.setSize(800, 600); // Dimensions fixes [cite: 8]
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
    }
}