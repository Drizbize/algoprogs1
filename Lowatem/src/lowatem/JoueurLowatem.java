package lowatem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import static lowatem.Direction.EST;
import static lowatem.Direction.NORD;
import static lowatem.Direction.OUEST;
import static lowatem.Direction.SUD;

/**
 * Joueur implémentant les actions possibles à partir d'un plateau, pour un
 * niveau donné.
 */
public class JoueurLowatem implements IJoueurLowatem {

    /**
     * Cette méthode renvoie, pour un plateau donné et un joueur donné, toutes
     * les actions possibles pour ce joueur.
     *
     * @param plateau le plateau considéré
     * @param couleurJoueur couleur du joueur
     * @param niveau le niveau de la partie à jouer
     * @return l'ensemble des actions possibles
     */
    @Override
    public String[] actionsPossibles(Case[][] plateau, char couleurJoueur, int niveau) {
        // afficher l'heure de lancement
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        System.out.println("actionsPossibles : lancement le " + format.format(new Date()));
        
        // se préparer à stocker les actions possibles
        ActionsPossibles actions = new ActionsPossibles();
        
        // calculer les points de vie sur le plateau initial
        
        // déplacements possibles depuis (g,G)
        
        NbPointsDeVie nbPv = nbPointsDeVie(plateau);
        
        actionFireWalls(plateau, actions);
        
        for (int y = 0; y < plateau.length; y++)
        {
            for (int x = 0; x < plateau[y].length; x++)
            {
                if (plateau[y][x].unitePresente() && plateau[y][x].couleurUnite == couleurJoueur)
                {
                    Coordonnees src = new Coordonnees(y, x);
                    
                    checkAddAttack(plateau, src, src, actions, nbPv);
                    ajoutDeplDepuis(plateau, src, actions, nbPv);
                }
            }
        }
        
        
        
        System.out.println("actionsPossibles : fin");
        return actions.nettoyer();
    }
    
    void actionFireWalls(Case[][] plateau, ActionsPossibles actions)
    {
        addActionFireWall(plateau, Direction.EST, actions);
        addActionFireWall(plateau, Direction.NORD, actions);
        addActionFireWall(plateau, Direction.OUEST, actions);
        addActionFireWall(plateau, Direction.SUD, actions); //FS,24,15
    }
    
    void addActionFireWall(Case[][] plateau, Direction dir, ActionsPossibles actions)
    {
        Case[][] newPlateau = clonePlateau(plateau);
        
        boolean isStop;
        switch (dir) {
            case Direction.NORD, Direction.SUD -> {
                for (int x = 0; x < newPlateau[0].length; x++) {
                    isStop = false;
                    int y = switch (dir) {
                        case NORD -> 0;
                        case SUD -> newPlateau.length - 1;
                        default -> 0;
                    };
                    
                    int damage = Utils.FIREWAVE_DAMAGE;
                    
                    while ((y < newPlateau.length && y >= 0) && !isStop && damage > 0)
                    {
                        Case unit = newPlateau[y][x];
                        
                        if (unit.unitePresente())
                        {
                            unit.pointsDeVie = Math.max(unit.pointsDeVie - damage, 0);
                            isStop = true;
                        }
                        else
                        {
                            damage--;
                            switch (dir) {
                                case NORD -> y++;
                                case SUD -> y--;
                            }
                        }
                    }
                }
            }
            case Direction.EST, Direction.OUEST -> {
                for (int y = 0; y < newPlateau.length; y++) {
                    isStop = false;
                    int x = switch (dir) {
                        case OUEST -> 0;
                        case EST -> newPlateau[y].length - 1;
                        default -> 0;
                    };
                    
                    int damage = Utils.FIREWAVE_DAMAGE;
                    
                    while ((x < newPlateau[y].length && x >= 0) && !isStop && damage > 0)
                    {
                        Case unit = newPlateau[y][x];
                        
                        if (unit.unitePresente())
                        {
                            unit.pointsDeVie = Math.max(unit.pointsDeVie - damage, 0);
                            isStop = true;
                        }
                        else
                        {
                            damage--;
                            switch (dir) {
                                case OUEST -> x++;
                                case EST -> x--;
                            }
                        }
                    }
                }
            }
        }
        
        NbPointsDeVie totalHP = nbPointsDeVie(newPlateau);
        
        addFireAction(dir, actions, totalHP);
    }
    
    static Case[][] clonePlateau(Case[][] plateau)
    {
        Case[][] newPlateau = new Case[plateau.length][plateau[0].length];
        for (int y = 0; y < newPlateau.length; y++)
        {
            newPlateau[y] = new Case[plateau[y].length];
            for (int x = 0; x < newPlateau[y].length; x++)
            {
                Case origCase = plateau[y][x];
                newPlateau[y][x] = new Case(
                        origCase.typeUnite,
                        origCase.couleurUnite,
                        origCase.pointsDeVie,
                        origCase.altitude,
                        origCase.nature
                );
            }
        }
        
        return newPlateau;
    }

    /**
     * Nombre de points de vie de chaque joueur sur le plateau.
     *
     * @param plateau le plateau
     * @return le nombre de pions de cette couleur sur le plateau
     */
    static NbPointsDeVie nbPointsDeVie(Case[][] plateau) {
        // TODO il y en aura besoin à un moment !
        
        int totalRouge = 0;
        int totalNoir = 0;
        
        for (Case[] plateau1 : plateau) {
            for (Case cas : plateau1) {
                if (cas.couleurUnite == Case.CAR_ROUGE) {
                    totalRouge += cas.pointsDeVie;
                }
                else if (cas.couleurUnite == Case.CAR_NOIR) {
                    totalNoir += cas.pointsDeVie;
                }
                
            }
        }
        
        return new NbPointsDeVie(totalRouge, totalNoir);
    }

    /**
     * Ajouter tous les déplacements depuis une case donnée.
     *
     * @param coord coordonnées de la case d'origine
     * @param actions ensemble des actions possibles, à compléter
     * @param nbPv nombre de points de vie de chaque joueur sur le plateau
     * initial
     */
    void ajoutDeplDepuis(Case[][] plateau, Coordonnees coord, ActionsPossibles actions, NbPointsDeVie nbPv) {
        // on part dans chacune des 4 directions
        for (Direction dir : Direction.toutes())
        {
            ajoutDeplDansDirection(plateau, dir, coord, actions, nbPv);
        }
        // on ajoute le déplacement "sur place"
        ajoutDepl(coord, coord, actions, nbPv);
    }

    /**
     * Ajouter tous les déplacements depuis une case donnée, dans une direction
     * donnée.
     *
     * @param dir direction à suivre
     * @param src coordonnées de la case d'origine
     * @param actions ensemble des actions possibles, à compléter
     * @param nbPv nombre de points de vie de chaque joueur sur le plateau
     * initial
     */
    void ajoutDeplDansDirection(Case[][] plateau, Direction dir, Coordonnees src, ActionsPossibles actions, NbPointsDeVie nbPv) {
        Case origUnit = plateau[src.ligne][src.colonne];
        Coordonnees dst = src.suivantes(dir);
        
        int steps = 0;
        boolean isRunning = true;
        while (dst.estDansPlateau() && isRunning)
        {
            steps++;
            
            Case unite = plateau[dst.ligne][dst.colonne];
            if (!Utils.canGo(origUnit.typeUnite, unite.nature))
            {
                isRunning = false;
            }
            else if (!unite.unitePresente())
            {
                int currHealth = getStepHealth(steps, origUnit.pointsDeVie, origUnit.typeUnite);
                if (currHealth > 0)
                {
                    NbPointsDeVie newTotalHealth = new NbPointsDeVie(nbPv);
                    int origHealth = origUnit.pointsDeVie;
                    
                    if (origUnit.couleurUnite == Case.CAR_ROUGE)
                    {
                        newTotalHealth.nbPvRouge = (nbPv.nbPvRouge - origUnit.pointsDeVie) + currHealth;
                    }
                    else
                    {
                        newTotalHealth.nbPvNoir = (nbPv.nbPvNoir - origUnit.pointsDeVie) + currHealth;
                    }
                    
                    origUnit.pointsDeVie = currHealth;
                    ajoutDepl(src, dst, actions, newTotalHealth);
                    checkAddAttack(plateau, src, dst, actions, newTotalHealth);
                    
                    origUnit.pointsDeVie = origHealth;
                }
                else
                {
                    isRunning = false;
                }
            }
            
            dst = dst.suivantes(dir);
        }
    }
    
    void checkAddAttack(Case[][] plateau, Coordonnees src, Coordonnees dst, ActionsPossibles actions, NbPointsDeVie nbPv)
    {
        Case origUnit = plateau[src.ligne][src.colonne];
        for (Direction attackDir : Direction.toutes())
        {
            int steps = 0;
            int maxSteps = Utils.getAttackSteps(origUnit.typeUnite);
            
            //TODO: change to recursive function
            Coordonnees forwardPos = new Coordonnees(dst.ligne, dst.colonne);
            while (steps < maxSteps)
            {
                steps++;
                
                forwardPos = forwardPos.suivantes(attackDir);
                checkAddAttackAt(plateau, src, dst, forwardPos, actions, nbPv);
                
                Direction turnDir = Utils.turnClockwise(attackDir);
                Coordonnees turnedPos = new Coordonnees(forwardPos.ligne, forwardPos.colonne);
                
                int turnedSteps = steps;
                while (turnedSteps < maxSteps)
                {
                    turnedSteps++;
                    
                    turnedPos = turnedPos.suivantes(turnDir);
                    checkAddAttackAt(plateau, src, dst, turnedPos, actions, nbPv);
                }
            }            
        }
    }
    
    void checkAddAttackAt(Case[][] plateau, Coordonnees src, Coordonnees dst, Coordonnees attackPos, ActionsPossibles actions, NbPointsDeVie nbPv)
    {
        Case origUnit = plateau[src.ligne][src.colonne];
        if (attackPos.estDansPlateau())
        {
            Case uniteNear = plateau[attackPos.ligne][attackPos.colonne];
            if (uniteNear.unitePresente() && uniteNear.couleurUnite != origUnit.couleurUnite)
            {
                NbPointsDeVie newTotalHealth = getNewTotalHealth(origUnit, uniteNear, nbPv);
                actions.ajouterAction(chaineActionAttack(src, dst, attackPos, newTotalHealth));
            }
        }
    }
    
    static NbPointsDeVie getNewTotalHealth(Case origUnit, Case attackUnit, NbPointsDeVie totalHP)
    {
        NbPointsDeVie oldHealths = new NbPointsDeVie();
        oldHealths.nbPvRouge = origUnit.couleurUnite == Case.CAR_ROUGE ? origUnit.pointsDeVie : attackUnit.pointsDeVie;
        oldHealths.nbPvNoir = origUnit.couleurUnite == Case.CAR_NOIR ? origUnit.pointsDeVie : attackUnit.pointsDeVie;

        NbPointsDeVie healths = getAttackedHealth(origUnit.pointsDeVie, attackUnit.pointsDeVie, origUnit);

        NbPointsDeVie newTotalHealth = new NbPointsDeVie();
        newTotalHealth.nbPvRouge = totalHP.nbPvRouge - (oldHealths.nbPvRouge - healths.nbPvRouge);
        newTotalHealth.nbPvNoir = totalHP.nbPvNoir - (oldHealths.nbPvNoir - healths.nbPvNoir);
        
        return newTotalHealth;
    }
    
    static NbPointsDeVie getAttackedHealth(int oldPvAttacker, int oldPvAttack, Case unit)
    {   
        int resultAttacker = oldPvAttacker - Utils.getAttackerDamage(unit.typeUnite) - (int)((oldPvAttack - 5) / 2);
        int resultAttack = oldPvAttack - Utils.getAttackedDamage(unit.typeUnite) - (int)((oldPvAttacker - 5) / 2); // 3 - 4 + 1
        
        if (resultAttacker < 0)
            resultAttacker = 0;
        
        if (resultAttack < 0)
            resultAttack = 0;
        
        NbPointsDeVie nbPv = new NbPointsDeVie();
        
        nbPv.nbPvRouge = unit.couleurUnite == Case.CAR_ROUGE ? resultAttacker : resultAttack;
        nbPv.nbPvNoir = unit.couleurUnite == Case.CAR_NOIR ? resultAttacker : resultAttack;
        
        return nbPv;
    }
    
    static int getStepHealth(int step, int origHealth, char typeUnit)
    {
        return origHealth - (int)(Utils.getStepDamageCoef(typeUnit) * step);
    }

    /**
     * Ajout d'une action de déplacement dans l'ensemble des actions possibles.
     *
     * @param src coordonnées de la case à l'origine du déplacement
     * @param dst coordonnées de la case destination du déplacement
     * @param actions l'ensemble des actions possibles (en construction)
     * @param nbPv nombre de points de vie de chaque joueur sur le plateau
     * initial
     */
    void ajoutDepl(Coordonnees src, Coordonnees dst, ActionsPossibles actions, NbPointsDeVie nbPv) {
        actions.ajouterAction(chaineActionDepl(src, dst, nbPv));
    }

    /**
     * Chaîne de caractères correspondant à une action-mesure de déplacement.
     *
     * @param src coordonnées de la case à l'origine du déplacement
     * @param dst coordonnées de la case destination du déplacement
     * @param nbPv nombre de points de vie de chaque joueur après l'action
     * @return la chaîne codant cette action-mesure
     */
    static String chaineActionDepl(Coordonnees src, Coordonnees dst, NbPointsDeVie nbPv) {
        return "" + src.carLigne() + src.carColonne()
                + "D" + dst.carLigne() + dst.carColonne()
                + "," + nbPv.nbPvRouge + "," + nbPv.nbPvNoir;
    }
    
    static String chaineActionAttack(Coordonnees src, Coordonnees dst, Coordonnees attackPos, NbPointsDeVie nbPv)
    {
        return "" + src.carLigne() + src.carColonne()
                + "D" + dst.carLigne() + dst.carColonne()
                + 'A' + attackPos.carLigne() + attackPos.carColonne()
                + "," + nbPv.nbPvRouge + "," + nbPv.nbPvNoir;
    }
    
    static void addFireAction(Direction dir, ActionsPossibles actions, NbPointsDeVie nbPv)
    {
        actions.ajouterAction(
            "F" +
            switch (dir)
            {
                case Direction.EST -> 'E';
                case Direction.NORD -> 'N';
                case Direction.OUEST -> 'O';
                case Direction.SUD -> 'S';
                default -> ' ';
            } + ',' + nbPv.nbPvRouge + ',' + nbPv.nbPvNoir
        );
    }
}
