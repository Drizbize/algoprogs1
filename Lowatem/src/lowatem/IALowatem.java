package lowatem;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;

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
    
    void setActions(Case[][] plateau, char playerColor, ActionsPossibles deplActions, ActionsPossibles attackActions)
    {
        for (int y = 0; y < plateau.length; y++)
        {
            for (int x = 0; x < plateau[y].length; x++)
            {
                if (plateau[y][x].unitePresente() && plateau[y][x].couleurUnite == playerColor)
                {
                    Coordonnees src = new Coordonnees(y, x);
                    
                    //JoueurLowatem.checkAddAttack(plateau, src, src, attackActions);
                    JoueurLowatem.ajoutDeplAttackDepuis(plateau, src, deplActions, attackActions);
                }
            }
        }
    }
    
    static Coordonnees[] getAllPlrPositions(Case[][] plateau, char playerColor)
    {
        Coordonnees[] positions = new Coordonnees[plateau.length * plateau[0].length];
        
        int i = 0;
        for (int y = 0; y < plateau.length; y++)
        {
            for (int x = 0; x < plateau[y].length; x++)
            {
                if (plateau[y][x].unitePresente() && plateau[y][x].couleurUnite == playerColor)
                {
                    positions[i] = new Coordonnees(y, x);
                    i++;
                }
            }
        }
        
        return Arrays.copyOf(positions, i);
    }

    //  ------------------ weights --------------------
    // ATTACKS
    final float STEP_ATTACK = 3.0f;
    
    final int GLOBAL_HEALTH_DIFF_TO_RUNAWAY = 15;
    final int GL_HEALTH_DIFF_TO_ATTACK = 10;
    final int GL_HEALTH_DIFF_TO_AVOID = -10;
    
    final int ATTACK_ENEMY_DIED = 6;
    final float ATTACK_LOST_PLAYER_HEALTH = 1.5f;
    final int ATTACK_PLAYER_NOT_LOST_HEALTH = 10;
    final int AFTER_ATTACK_DIED = -7;
    final int AFTER_ATTACK_DIED_AND_ENEMY_DIED = 2;
    final int AFTER_ATTACK_DIED_AND_LESS_THEN_ENEMY = 5;
    
    // WALK
    final int STEP_GO = 10;
    
    final float FEAR_OF_LOST_HEALTH_BE_ATTACKED = 1.f;
    final int FEAR_OF_DEATH_BE_ATTACKED = 8;
    final int BE_ATTACKED_ENEMY_DIED = 3;
     
    /**
     * L'action choisie par notre IA.
     *
     * @param plateau le plateau de jeu
     * @param nbToursJeu numéro du tour de jeu
     * @return l'action choisie sous forme de chaîne
     */
    public String actionChoisie(Case[][] plateau, int nbToursJeu) {
        ActionsPossibles deplActions = new ActionsPossibles();
        ActionsPossibles attackActions = new ActionsPossibles();
        
        setActions(plateau, couleur, deplActions, attackActions);
        
        ActionsPossibles enemyAttackActions = new ActionsPossibles();
        setActions(plateau, suivant(couleur), new ActionsPossibles(), enemyAttackActions);
        
        String[] deplaces = deplActions.nettoyer();
        String[] attacks = attackActions.nettoyer();
        
        String action = getBestAction(plateau, deplaces, attacks, enemyAttackActions.nettoyer());
        
//        if (action == null)
//        {
//        }
            
        //System.out.println("My AI action: " + action);
        
        return action;
    }
    
    String getBestAction(Case[][] plateau, String[] deplActions, String[] attackActions, String[] enemyAttackActions)
    {
        if (attackActions.length + deplActions.length == 0)
            return null;
        
        int[] coefs = new int[attackActions.length + deplActions.length];
        
        int totalHealth = getTotalHealth(plateau, couleur);
        int totalHealthEnemy = getTotalHealth(plateau, suivant(couleur));
        
        for (int i = 0; i < attackActions.length; i++) {
            String action = attackActions[i];
            
            coefs[i] = getAttackCoef(plateau, action, enemyAttackActions, totalHealth, totalHealthEnemy);
            //System.out.printf("%s: %d\n", action, coefs[i]);
        }
        
        for (int i = 0; i < deplActions.length; i++) {
            String action = deplActions[i];
            Coordonnees origPos = getSrcFromAction(action);
            Coordonnees distPos = getDistFromAction(action);
            
            Case playerUnit = plateau[origPos.ligne][origPos.colonne];
            
            coefs[i + attackActions.length] = 0;
            
            int coefSafe;
            if (attackActions.length == 0)
                coefSafe = getCoefBeAttackedFuture(plateau, action, true);
            else
                coefSafe = getCoefBeAttackedFuture(plateau, action, false);
            coefs[i + attackActions.length] -= coefSafe; // If place is safe
            
            coefs[i + attackActions.length] += STEP_GO * (getStepDamagedHealth(playerUnit, origPos, distPos) - playerUnit.pointsDeVie); // By lose health of steps
            coefs[i + attackActions.length] += totalHealthEnemy > totalHealth ? 0 : 3;
            //System.out.printf("%s: %d\n", action, coefs[i + attackActions.length]);
        }
        
        int index = getIndexBestCoef(coefs);
        
        if (index < attackActions.length)
        {
            return attackActions[index];
        }
        else
        {
            Coordonnees[] enemyPositions = getAllPlrPositions(plateau, suivant(couleur));
            
            if (enemyPositions != null && enemyPositions.length != 0)
            {
                //Random rnd = new Random();
                
                
                for (int i = 0; i < deplActions.length; i++) {
                    String action = deplActions[i];
                    Coordonnees origPos = getSrcFromAction(action);
                    Coordonnees dstPos = getDistFromAction(action);
                    
                    Coordonnees farestEnemy = getFarestPosition(origPos, enemyPositions);//enemyPositions[rnd.nextInt(enemyPositions.length)];
                    
                    coefs[i + attackActions.length] -= 2;
                    coefs[i + attackActions.length] -= dstPos.equals(origPos) ? 2 : 0;
                    coefs[i + attackActions.length] += getMagnitude(new Coordonnees(origPos.ligne - farestEnemy.ligne, origPos.colonne - farestEnemy.colonne));
                    coefs[i + attackActions.length] -= getMagnitude(new Coordonnees(dstPos.ligne - farestEnemy.ligne, dstPos.colonne - farestEnemy.colonne));
                    //coefs[i + attackActions.length] -= getCoefBeAttackedFuture(plateau, action, false) * 5;
                    //System.out.printf("%s: %d\n", action, coefs[i + attackActions.length]);
                }
                
                int coef = coefs[attackActions.length];
                index = attackActions.length;
                for (int i = attackActions.length; i < deplActions.length + attackActions.length; i++) {
                    if (coefs[i] > coef)
                    {
                        coef = coefs[i];
                        index = i;
                    }
                }
            }
            
            return deplActions[index - attackActions.length];
        }
    }
    
    Coordonnees getFarestPosition(Coordonnees origPos, Coordonnees[] positions)
    {
        if (positions == null || positions.length == 0)
            return new Coordonnees(0, 0);
        
        Coordonnees closest = positions[0];
        
        for (Coordonnees position : positions) {
            Coordonnees vector = new Coordonnees(origPos.ligne - position.ligne, origPos.colonne - position.colonne);
            
            if (getMagnitude(vector) > getMagnitude(closest))
                closest = position;
        }
        
        return closest;
    }
    
    double getMagnitude(Coordonnees vector)
    {
        return Math.sqrt(vector.ligne * vector.ligne + vector.colonne * vector.colonne);
    }
    
    int getIndexBestCoef(int[] coefs)
    {
        int coef = coefs[0];
        int index = 0;
        for (int i = 0; i < coefs.length; i++) {
            if (coefs[i] > coef)
            {
                coef = coefs[i];
                index = i;
            }
        }
        
        return index;
    }
    
    int getAttackCoef(Case[][] plateau, String action, String[] enemyAttackActions, int totalHealth, int totalHealthEnemy)
    {
        int coef = 0;
        
        Coordonnees origPos = getSrcFromAction(action);
        Coordonnees distPos = getDistFromAction(action);
        //Coordonnees attackPos = getAttackPosFromAction(action);
        
        Case playerUnit = plateau[origPos.ligne][origPos.colonne];
        
        coef += Utils.getUnitCoef(playerUnit.typeUnite); // By type
        coef += STEP_ATTACK * (getStepDamagedHealth(playerUnit, origPos, distPos) - playerUnit.pointsDeVie); // By lose health of steps
        coef += getCoefOfBeAttacked(plateau, origPos, enemyAttackActions, totalHealth, totalHealthEnemy, false); // By risk of be attacked of died
        coef += getCoefAttacked(plateau, action, totalHealth, totalHealthEnemy); // By attacking and see the future coef of risk
        coef += totalHealthEnemy + GLOBAL_HEALTH_DIFF_TO_RUNAWAY > totalHealth ? GL_HEALTH_DIFF_TO_ATTACK : GL_HEALTH_DIFF_TO_AVOID; // If total enemy health is greater
        
        return coef;
    }
    
    static int getStepDamagedHealth(Case playerUnit, Coordonnees origPos, Coordonnees distPos)
    {
        int steps = 0;
        
        if (origPos.ligne == distPos.ligne)
            steps = Math.abs(origPos.colonne - distPos.colonne);
        else if (origPos.colonne == distPos.colonne)
            steps = Math.abs(origPos.ligne - distPos.ligne);
        
        return JoueurLowatem.getStepHealth(steps, playerUnit.pointsDeVie, playerUnit.typeUnite);
    }
    
    int getCoefOfBeAttacked(Case[][] plateau, Coordonnees origPos, String[] enemyAttackActions, int totalHealth, int totalHealthEnemy, boolean isRunAway)
    {
        Case playerUnit = plateau[origPos.ligne][origPos.colonne];
        
        int coef = 0;
        for (String action : enemyAttackActions) {
            Coordonnees enemySrcPos = getSrcFromAction(action);
            Coordonnees enemyDstPos = getDistFromAction(action);
            Coordonnees enemyAttackPos = getAttackPosFromAction(action);
            
            Case enemyUnit = plateau[enemySrcPos.ligne][enemySrcPos.colonne];
            
            if (enemyAttackPos.equals(origPos)) {
                int oldEnemyHealth = enemyUnit.pointsDeVie;
                enemyUnit.pointsDeVie = getStepDamagedHealth(enemyUnit, enemySrcPos, enemyDstPos);
                
                int newUnitHealth = JoueurLowatem.getAttackedHealth(enemyUnit, playerUnit.pointsDeVie);
                
                int lostHealth = playerUnit.pointsDeVie - newUnitHealth;
                coef += (int)(lostHealth * FEAR_OF_LOST_HEALTH_BE_ATTACKED);
                coef -= JoueurLowatem.getAttackedHealth(enemyUnit, playerUnit.pointsDeVie) == 0 ? BE_ATTACKED_ENEMY_DIED : 0;
                coef -= enemyUnit.pointsDeVie - JoueurLowatem.getDamagedHealth(enemyUnit, playerUnit.pointsDeVie);
                
                //int newtotalHealth = totalHealth - lostHealth;
                
                if (newUnitHealth == 0) {
                    coef += FEAR_OF_DEATH_BE_ATTACKED; // coef of death
                }
                
                enemyUnit.pointsDeVie = oldEnemyHealth;
            }
        }
        
        return coef;
    }
    
    int getCoefAttacked(Case[][] plateau, String action, int totalHealth, int totalHealthEnemy)
    {   
        Coordonnees origPos = getSrcFromAction(action);
        Coordonnees distPos = getDistFromAction(action);
        Coordonnees attackPos = getAttackPosFromAction(action);
        
        Case playerUnit = plateau[origPos.ligne][origPos.colonne];
        Case enemyUnit = plateau[attackPos.ligne][attackPos.colonne];
        
        int oldHealth = playerUnit.pointsDeVie;
        
        int coef = 0;
        
        playerUnit.pointsDeVie = getStepDamagedHealth(playerUnit, origPos, distPos);
        
        int enemyNewHealth = enemyUnit.pointsDeVie - JoueurLowatem.getAttackedHealth(playerUnit, enemyUnit.pointsDeVie);
        coef += enemyUnit.pointsDeVie - enemyNewHealth;
        if (enemyNewHealth == 0)
            coef += ATTACK_ENEMY_DIED;
        
        int playerNewHealth = JoueurLowatem.getDamagedHealth(playerUnit, enemyUnit.pointsDeVie);
        coef -= (int)(ATTACK_LOST_PLAYER_HEALTH * (playerUnit.pointsDeVie - playerNewHealth));
        playerUnit.pointsDeVie = oldHealth;
        
        if (playerNewHealth == playerUnit.pointsDeVie)
            coef += ATTACK_PLAYER_NOT_LOST_HEALTH;

        int newtotalHealth = totalHealth - (playerUnit.pointsDeVie - playerNewHealth);
        int newtotalHealthEnemy = totalHealthEnemy - (enemyNewHealth - enemyUnit.pointsDeVie);
        
        if (newtotalHealth <= 0 && newtotalHealthEnemy > 0)
            coef -= 100;
        
        if (playerNewHealth <= 0)
        {
            coef += AFTER_ATTACK_DIED;
            
            if (enemyNewHealth == 0)
            {
                coef += AFTER_ATTACK_DIED_AND_ENEMY_DIED;
                if (playerUnit.pointsDeVie < enemyUnit.pointsDeVie)
                    coef += AFTER_ATTACK_DIED_AND_LESS_THEN_ENEMY;
            }
        }
        else
        {
            coef -= getCoefBeAttackedFuture(plateau, action, false);
        }
        
        //System.out.println(coef);
        return coef;
    }

    int getCoefBeAttackedFuture(Case[][] plateau, String action, boolean isRunAway)
    {
        //Coordonnees origPos = getSrcFromAction(action);
        Coordonnees distPos = getDistFromAction(action);
        
        Case[][] plateauClone = JoueurLowatem.clonePlateau(plateau);
        mettreAJour(plateauClone, action);
        
        ActionsPossibles fEnemyAttackActions = new ActionsPossibles();
        setActions(plateauClone, suivant(couleur), new ActionsPossibles(), fEnemyAttackActions);
        
        return getCoefOfBeAttacked(plateauClone, distPos, fEnemyAttackActions.nettoyer(), getTotalHealth(plateauClone, couleur), getTotalHealth(plateauClone, suivant(couleur)), isRunAway);
    }
    
    Coordonnees getSrcFromAction(String action) {
        return Coordonnees.depuisCars(action.charAt(0), action.charAt(1));
    }
    Coordonnees getDistFromAction(String action) {
        return Coordonnees.depuisCars(action.charAt(3), action.charAt(4));
    }
    Coordonnees getAttackPosFromAction(String action) {
        return Coordonnees.depuisCars(action.charAt(6), action.charAt(7));
    }
    
    int getTotalHealth(Case[][] plateau, char color)
    {
        int total = 0;
        
        for (Case[] plateau1 : plateau) {
            for (Case cas : plateau1) {
                if (cas.couleurUnite == color) {
                    total += cas.pointsDeVie;
                }
            }
        }
        
        return total;
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
            deplacerUnite(src, dst, new Coordonnees(coordSrc.ligne, coordSrc.colonne), new Coordonnees(coordDst.ligne, coordDst.colonne));
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
    static void deplacerUnite(Case src, Case dst, Coordonnees origPos, Coordonnees distPos) {
        int health = getStepDamagedHealth(src, origPos, distPos);
        if (health > 0)
        {
            dst.typeUnite = src.typeUnite;
            dst.couleurUnite = src.couleurUnite;
            dst.pointsDeVie = health;
        }
        
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
        dst.pointsDeVie = oldPvAttaquant - Utils.getAttackerDamage(dst.typeUnite) - (int) ((oldPvAttaque - 5) / 2);
        atq.pointsDeVie = oldPvAttaque - Utils.getAttackedDamage(dst.typeUnite) - (int) ((oldPvAttaquant - 5) / 2);
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
