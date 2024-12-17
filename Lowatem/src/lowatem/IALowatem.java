package lowatem;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Votre IA pour le jeu Lowatem.
 */
public class IALowatem {

    /**
     * Hôte du grand ordonnateur.
     */
    String hote = null;

    /**
     * Port du grand ordonnateur.
     */
    int port = -1;

    /**
     * Couleur de votre joueur (IA) : 'R'ouge ou 'N'oir.
     */
    final char couleur;

    /**
     * Interface pour le protocole du grand ordonnateur.
     */
    TcpGrandOrdonnateur grandOrdo = null;

    /**
     * Nombre maximal de tours de jeu.
     */
    static final int NB_TOURS_JEU_MAX = 40;

    /**
     * Constructeur.
     *
     * @param hote Hôte.
     * @param port Port.
     * @param uneCouleur Couleur de ce joueur
     */
    public IALowatem(String hote, int port, char uneCouleur) {
        this.hote = hote;
        this.port = port;
        this.grandOrdo = new TcpGrandOrdonnateur();
        this.couleur = uneCouleur;
    }

    /**
     * Connexion au Grand Ordonnateur.
     *
     * @throws IOException exception sur les entrées/sorties
     */
    void connexion() throws IOException {
        System.out.print(
                "Connexion au Grand Ordonnateur : " + hote + " " + port + "...");
        System.out.flush();
        grandOrdo.connexion(hote, port);
        System.out.println(" ok.");
        System.out.flush();
    }

    /**
     * Boucle de jeu : envoi des actions que vous souhaitez jouer, et réception
     * des actions de l'adversaire.
     *
     * @throws IOException exception sur les entrées/sorties
     */
    void toursDeJeu() throws IOException {
        // paramètres
        System.out.println("Je suis le joueur " + couleur + ".");
        // le plateau initial
        System.out.println("Réception du plateau initial...");
        Case[][] plateau = grandOrdo.recevoirPlateauInitial();
        System.out.println("Plateau reçu.");
        // compteur de tours de jeu (entre 1 et 40)
        int nbToursJeu = 1;
        // la couleur du joueur courant (change à chaque tour de jeu)
        char couleurTourDeJeu = Case.CAR_ROUGE;
        // booléen pour détecter la fin du jeu
        boolean fin = false;
        while (!fin) {
            boolean disqualification = false;
            if (couleurTourDeJeu == couleur) {
                // à nous de jouer !
                jouer(plateau, nbToursJeu);
            } else {
                // à l'adversaire de jouer
                disqualification = adversaireJoue(plateau, couleurTourDeJeu);
            }
            if (nbToursJeu == NB_TOURS_JEU_MAX || disqualification) {
                // fini
                fin = true;
            } else {
                // au suivant
                nbToursJeu++;
                couleurTourDeJeu = suivant(couleurTourDeJeu);
            }
        }
    }

    /**
     * Fonction exécutée lorsque c'est à notre tour de jouer. Cette fonction
     * envoie donc l'action choisie au serveur.
     *
     * @param plateau le plateau de jeu
     * @param nbToursJeu numéro du tour de jeu
     * @throws IOException exception sur les entrées / sorties
     */
    void jouer(Case[][] plateau, int nbToursJeu) throws IOException {
        String actionJouee = actionChoisie(plateau, nbToursJeu);
        if (actionJouee != null) {
            // jouer l'action
            System.out.println("On joue : " + actionJouee);
            grandOrdo.envoyerAction(actionJouee);
            mettreAJour(plateau, actionJouee);
        } else {
            // Problème : le serveur vous demande une action alors que vous n'en
            // trouvez plus...
            System.out.println("Aucun action trouvée : abandon...");
            grandOrdo.envoyerAction("ABANDON");
        }
    }

    /**
     * L'action choisie par notre IA.
     *
     * @param plateau le plateau de jeu
     * @param nbToursJeu numéro du tour de jeu
     * @return l'action choisie sous forme de chaîne
     */
    public String actionChoisie(Case[][] plateau, int nbToursJeu) {
        ActionsPossibles attackActions = new ActionsPossibles();
        
        for (int y = 0; y < plateau.length; y++)
        {
            for (int x = 0; x < plateau[y].length; x++)
            {
                
            }
        }
        
        //
        // TODO : ici, on choisit aléatoirement n'importe quelle action possible
        // retournée par votre programme. À vous de faire un meilleur choix...
        //
        MonTacheron monTacheron = new MonTacheron(couleur);
        return monTacheron.actionChoisie(plateau, nbToursJeu);
    }
    
    void addActionsForUnit(Case[][] plateau, Coordonnees src, ActionsPossibles attackActions)
    {
        
    }

    /**
     * L'adversaire joue : on récupère son action, met à jour le plateau, et
     * signale toute disqualification.
     *
     * @param plateau le plateau de jeu
     * @param couleurAdversaire couleur de l'adversaire
     * @return l'action choisie sous forme de chaîne
     */
    boolean adversaireJoue(Case[][] plateau, char couleurAdversaire) {
        boolean disqualification = false;
        System.out.println("Attente de réception action adversaire...");
        String actionAdversaire = grandOrdo.recevoirAction();
        System.out.println("Action adversaire reçue : " + actionAdversaire);
        if ("Z".equals(actionAdversaire)) {
            System.out.println("L'adversaire est disqualifié.");
            disqualification = true;
        } else {
            System.out.println("L'adversaire joue : "
                    + actionAdversaire + ".");
            mettreAJour(plateau, actionAdversaire);
        }
        return disqualification;
    }

    /**
     * Calcule la couleur du prochain joueur.
     *
     * @param couleurCourante la couleur du joueur courant
     * @return la couleur du prochain joueur
     */
    static char suivant(char couleurCourante) {
        return couleurCourante == Case.CAR_ROUGE ? Case.CAR_NOIR : Case.CAR_ROUGE;
    }

    /**
     * Mettre à jour le plateau suite à une action, supposée valide.
     *
     * @param plateau le plateau
     * @param action l'action à appliquer
     */
    static void mettreAJour(Case[][] plateau, String action) {
        // vérification des arguments
        if (plateau == null || action == null || action.length() < 5
                || action.charAt(2) != 'D') {
            return;
        }
        // déplacement
        Coordonnees coordSrc = Coordonnees.depuisCars(action.charAt(0), action.charAt(1));
        Coordonnees coordDst = Coordonnees.depuisCars(action.charAt(3), action.charAt(4));
        Case src = plateau[coordSrc.ligne][coordSrc.colonne];
        Case dst = plateau[coordDst.ligne][coordDst.colonne];
        if (!coordSrc.memeLigne(coordDst) || !coordSrc.memeColonne(coordDst)) {
            deplacerUnite(src, dst);
        }
        // attaque, le cas échéant
        if (action.length() == 8 && action.charAt(5) == 'A') {
            Coordonnees coordAtq = Coordonnees.depuisCars(action.charAt(6), action.charAt(7));
            Case atq = plateau[coordAtq.ligne][coordAtq.colonne];
            attaquerUnite(atq, dst);
        }
    }

    /**
     * Déplacer une unité, d'une case à une autre.
     *
     * @param src la case source
     * @param dst la case destination
     */
    static void deplacerUnite(Case src, Case dst) {
        dst.typeUnite = src.typeUnite;
        dst.couleurUnite = src.couleurUnite;
        dst.pointsDeVie = src.pointsDeVie;
        retirerUnite(src);
    }

    /**
     * Appliquer une attaque.
     *
     * @param atq l'unité attaquée
     * @param dst l'unité menant l'attaque
     */
    static void attaquerUnite(Case atq, Case dst) {
        int oldPvAttaquant = dst.pointsDeVie;
        int oldPvAttaque = atq.pointsDeVie;
        dst.pointsDeVie = oldPvAttaquant - 2 - (int) ((oldPvAttaque - 5) / 2);
        atq.pointsDeVie = oldPvAttaque - 4 - (int) ((oldPvAttaquant - 5) / 2);
        if (dst.pointsDeVie <= 0) {
            retirerUnite(dst);
        }
        if (atq.pointsDeVie <= 0) {
            retirerUnite(atq);
        }
    }

    /**
     * Retirer une unité d'une case.
     *
     * @param laCase la case dont on doit retirer l'unité
     */
    static void retirerUnite(Case laCase) {
        laCase.typeUnite = Case.CAR_VIDE;
        laCase.couleurUnite = Case.CAR_NOIR;
        laCase.pointsDeVie = 0;
    }

    /**
     * Programme principal. Il sera lancé automatiquement, ce n'est pas à vous
     * de le lancer.
     *
     * @param args Arguments.
     */
    public static void main(String[] args) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        System.out.println("Démarrage le " + format.format(new Date()));
        System.out.flush();
        // « create » du protocole du grand ordonnateur.
        final String USAGE
                = System.lineSeparator()
                + "\tUsage : java " + IALowatem.class.getName()
                + " <hôte> <port> <ordre>";
        if (args.length != 3) {
            System.out.println("Nombre de paramètres incorrect." + USAGE);
            System.out.flush();
            System.exit(1);
        }
        String hote = args[0];
        int port = -1;
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Le port doit être un entier." + USAGE);
            System.out.flush();
            System.exit(1);
        }
        int ordre = -1;
        try {
            ordre = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println("L'ordre doit être un entier." + USAGE);
            System.out.flush();
            System.exit(1);
        }
        try {
            char couleurJoueur = (ordre == 1 ? 'R' : 'N');
            IALowatem iaLowatem = new IALowatem(hote, port, couleurJoueur);
            iaLowatem.connexion();
            iaLowatem.toursDeJeu();
        } catch (IOException e) {
            System.out.println("Erreur à l'exécution du programme : \n" + e);
            System.out.flush();
            System.exit(1);
        }
    }
}
