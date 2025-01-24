package lowatem;

import java.util.Random;

/**
 * Une IA qui choisit aléatoirement une action parmi celles renvoyées par
 * JoueurLowatem.actionsPossibles().
 */
class MonTacheron {

    /**
     * Couleur de mon joueur Tacheron.
     */
    char couleur;

    /**
     * Constructeur.
     *
     * @param uneCouleur couleur du tacheron
     */
    MonTacheron(char uneCouleur) {
        couleur = uneCouleur;
    }

    /**
     * L'action choisie par cette IA : au hasard parmi les actions possibles.
     *
     * @param plateau le plateau de jeu
     * @param nbToursJeu numéro du tour de jeu
     * @return l'action choisie sous forme de chaîne
     */
    String actionChoisie(Case[][] plateau, int nbToursJeu) {
        // on instancie votre implémentation
        JoueurLowatem joueurLowatem = new JoueurLowatem();
        // choisir aléatoirement une action possible
        String[] actionsPossibles = ActionsPossibles.nettoyerTableau(
                joueurLowatem.actionsPossibles(plateau, couleur, 5));
        String actionJouee = null;
        if (actionsPossibles.length > 0) {
            Random r = new Random();
            int indiceAleatoire = r.nextInt(actionsPossibles.length);
            actionJouee = ActionsPossibles.enleverPointsDeVie(
                    actionsPossibles[indiceAleatoire]);
        }
        return actionJouee;
    }

    /**
     * Boucle de jeu : envoi des actions que vous souhaitez jouer, et réception
     * des actions de l'adversaire.
     *
     * @param couleurIA couleur de l'IA
     * @param couleurTacheron couleur du tacheron
     * @param plateau le plateau initial, modifié pendant le jeu
     */
    static void toursDeJeu(char couleurIA, char couleurTacheron, Case[][] plateau) {
        // paramètres
        System.out.println("IALowatem est " + couleurIA + ".");
        // instantiation de mon IA
        IALowatem monIA = new IALowatem("", -1, couleurIA);
        MonTacheron monTacheron = new MonTacheron(couleurTacheron);
        // compteur de tours de jeu (entre 1 et 40)
        int nbToursJeu = 1;
        // la couleur du joueur courant (change à chaque tour de jeu)
        char couleurTourDeJeu = Case.CAR_NOIR;
        // booléen pour détecter la fin du jeu
        boolean fin = false;
        while (!fin) {
            // choisir l'action
            String actionChoisie;
            String joueur;
            if (couleurTourDeJeu == couleurIA) {
                actionChoisie = monIA.actionChoisie(plateau, nbToursJeu);
                joueur = "IALowatem";
            } else {
                actionChoisie = monTacheron.actionChoisie(plateau, nbToursJeu);
                joueur = "MonTacheron";
            }
            System.out.println(joueur + " joue : " + actionChoisie);
            // mettre à jour le plateau
            IALowatem.mettreAJour(plateau, actionChoisie);
            if (nbToursJeu == IALowatem.NB_TOURS_JEU_MAX) {
                // fini
                fin = true;
            } else {
                // au suivant
                nbToursJeu++;
                couleurTourDeJeu = IALowatem.suivant(couleurTourDeJeu);
            }
        }
    }

    /**
     * Lancer une partie entre votre IA et votre tacheron.
     *
     * @param args arguments de la ligne de commande (inutilisés)
     */
    public static void main(String[] args) {
        // choisir la couleur de l'IA
        char couleurIA = Case.CAR_NOIR;
        // monTacheron prend l'autre couleur
        char couleurTacheron = IALowatem.suivant(couleurIA);
        // plateau initial
        
       
        
        Case[][] plateauInitial = Utils.plateauDepuisTexte(PLATEAU_INITIAL); //
        NbPointsDeVie totalBegin = JoueurLowatem.nbPointsDeVie(plateauInitial);
        // lancement
        toursDeJeu(couleurIA, couleurTacheron, plateauInitial);
        
        NbPointsDeVie totalEnd = JoueurLowatem.nbPointsDeVie(plateauInitial);
        
        System.out.println("--Begin--");
        System.out.println("Total red: " + totalBegin.nbPvRouge + " | Total black: " + totalBegin.nbPvNoir);
        System.out.println("--End--");
        System.out.println("Total red: " + totalEnd.nbPvRouge + " | Total black: " + totalEnd.nbPvNoir);
        
    }

    final static String PLATEAU_INITIAL
       = """
            A   B   C   D   E   F   G   H   I   J   K   L   M   N 
          +E--+E--+E--+E--+---+---+---+---+---+---+---+---+---+---+
         a|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
          +E--+E--+E--+E--+---+---+---+---+---+---+---+---+---+---+
         b|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
          +E--+E--+---+---+---+---+---+---+---+---+---+---+---+---+
         c|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
          +E--+---+---+---+---+---+---+---+---+---+---+---+---+---+
         d|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
          +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         e|   |   |   |   |   |   |   |   |SN1|   |   |   |   |   |
          +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         f|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
          +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         g|   |LR2|   |   |   |   |   |   |   |   |   |   |   |SN1|
          +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         h|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
          +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         i|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
          +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         j|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
          +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         k|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
          +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         l|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
          +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         m|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
          +---+E--+---+---+---+---+---+---+---+---+---+---+---+---+
         n|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
          +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
         """;
}
