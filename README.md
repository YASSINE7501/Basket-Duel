# 🏀 Basket Duel

> **Jeu de sport compétitif en 2D au tour par tour.**
> *Projet Universitaire - L3 MIAGE - 2025-2026*

## 📝 Description du projet
**Basket Duel** est une simulation de tir au panier développée en **Java**. L'objectif est d'affronter un adversaire sur un terrain vu de profil, en marquant des paniers grâce à une gestion précise de la physique.

Les joueurs doivent faire preuve de stratégie en calculant la trajectoire idéale (combinaison **Angle/Puissance**) tout en s'adaptant à la gravité et aux obstacles. Le projet met l'accent sur une conception orientée objet rigoureuse et une architecture réseau fonctionnelle.

### 🌟 Fonctionnalités principales
Basé sur notre cahier des charges et nos modélisations UML :

* **Modes de jeu variés :**
    * 👤 **Solo vs IA :** Affrontez une intelligence artificielle (algorithme de décision).
    * 🤝 **Local (1v1) :** Duel sur la même machine.
    * 🌐 **Réseau :** Multijoueur distant via architecture Client/Serveur.
* **Moteur Physique :** Simulation réaliste de la gravité et calcul de trajectoires paraboliques.
* **Gameplay Stratégique :**
    * Apparition de **Bonus aléatoires** (impactant le score ou la trajectoire).
    * Condition de victoire au score cible (ex: premier à 100 points).

## 👥 Membres du groupe
* **TER Ilyas** (20233771)
* **BENAMMAR Ahmed** (20233849)
* **CIVANESWARAN Rathissan** (20230223)
* **MESTOUR Alicia** (20245443)

## 🛠️ Stack Technique
* **Langage :** Java (JDK 17+)
* **Conception & Modélisation :**
    * Diagrammes UML (Classes, Séquence, Activité) réalisés sur *Draw.io* & *Mermaid*.
    * Gestion de projet collaborative.
* **Versionning :** Git & GitHub

## 📂 Structure du dépôt
L'architecture du projet respecte les standards définis lors de la phase de conception :
* 📁 `/src` : Code source Java (Packages `Modele`, `Vue`, `Controleur`).
* 📁 `/doc` : Documentation technique (Diagrammes UML complets, Cahier des charges).
* 📁 `/assets` : Ressources graphiques (Sprites, Images).

## 🚀 Installation et Lancement
Pour tester le projet en local :

1. **Cloner le dépôt :**
   ```bash
   git clone [https://github.com/votre-groupe/basket-duel.git](https://github.com/votre-groupe/basket-duel.git)

```

2. **Accéder au répertoire :**
```bash
cd basket-duel

```


3. **Compiler et lancer (exemple) :**
```bash
javac -d bin src/*.java
java -cp bin Main

```



## 📊 État d'avancement

*Objectif : Rendu final du projet.*

* [x] **Phase 1 : Conception** (Cahier des charges, Modélisation UML, Architecture)
* [ ] **Phase 2 : Moteur Physique** (Trajectoires, Collisions)
* [ ] **Phase 3 : Gameplay** (Tours de jeu, Scores, Bonus)
* [ ] **Phase 4 : Réseau & IA** (Sockets, Algorithme adverse)
* [ ] **Phase 5 : Interface Graphique** (GUI)

---

*Ce projet est réalisé dans le cadre du module de Projet Informatique.*
