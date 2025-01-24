package lowatem;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

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
        //SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        //System.out.println("actionsPossibles : lancement le " + format.format(new Date()));
        
        // se préparer à stocker les actions possibles
        ActionsPossibles actionsDepl = new ActionsPossibles();
        ActionsPossibles actionsAttacks = new ActionsPossibles();
        
        // calculer les points de vie sur le plateau initial
        
        for (int y = 0; y < plateau.length; y++)
        {
            for (int x = 0; x < plateau[y].length; x++)
            {
                if (plateau[y][x].unitePresente() && plateau[y][x].couleurUnite == couleurJoueur)
                {
                    Coordonnees src = new Coordonnees(y, x);
                    
                    ajoutDeplAttackDepuis(plateau, src, actionsDepl, actionsAttacks);
                }
            }
        }
        
        String[] first = actionsDepl.nettoyer();
        String[] second = actionsAttacks.nettoyer();
        
        String[] both = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, both, first.length, second.length);
        
        //System.out.println("actionsPossibles : fin");
        return both;
    }

    /**
     * Makes a copy of the map `plateau`
     * @param plateau a map for copy
     * @return a copy
     */
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
    static void ajoutDeplAttackDepuis(Case[][] plateau, Coordonnees coord, ActionsPossibles actionsDepl, ActionsPossibles actionsAttack) {
        // on part dans chacune des 4 directions
        for (Direction dir : Direction.toutes())
        {
            ajoutDeplDansDirection(plateau, dir, coord, actionsDepl, actionsAttack);
        }
        // on ajoute le déplacement "sur place"
        checkAddAttack(plateau, coord, coord, actionsAttack);
        ajoutDepl(coord, coord, actionsDepl);
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
    static void ajoutDeplDansDirection(Case[][] plateau, Direction dir, Coordonnees src, ActionsPossibles actionsDepl, ActionsPossibles actionsAttack) {
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
                    int origHealth = origUnit.pointsDeVie;
                    
                    origUnit.pointsDeVie = currHealth;
                    ajoutDepl(src, dst, actionsDepl);
                    checkAddAttack(plateau, src, dst, actionsAttack);
                    
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
    
    static void checkAddAttack(Case[][] plateau, Coordonnees src, Coordonnees dst, ActionsPossibles actions)
    {
        Case origUnit = plateau[src.ligne][src.colonne];
        for (Direction attackDir : Direction.toutes())
        {
            Coordonnees nearby = dst.suivantes(attackDir);
            if (nearby.estDansPlateau())
            {
                Case uniteNear = plateau[nearby.ligne][nearby.colonne];
                if (uniteNear.unitePresente() && uniteNear.couleurUnite != origUnit.couleurUnite)
                {
                    //NbPointsDeVie newTotalHealth = getNewTotalHealth(origUnit, uniteNear, nbPv);
                    ajoutAttack(src, dst, nearby, actions);
                }
            }
        }
    }
    
//    static NbPointsDeVie getNewTotalHealth(Case origUnit, Case attackUnit, NbPointsDeVie totalHP)
//    {
//        NbPointsDeVie oldHealths = new NbPointsDeVie();
//        oldHealths.nbPvRouge = origUnit.couleurUnite == Case.CAR_ROUGE ? origUnit.pointsDeVie : attackUnit.pointsDeVie;
//        oldHealths.nbPvNoir = origUnit.couleurUnite == Case.CAR_NOIR ? origUnit.pointsDeVie : attackUnit.pointsDeVie;
//
//        NbPointsDeVie healths = getAttackedHealth(origUnit.pointsDeVie, attackUnit.pointsDeVie, origUnit);
//
//        NbPointsDeVie newTotalHealth = new NbPointsDeVie();
//        newTotalHealth.nbPvRouge = totalHP.nbPvRouge - (oldHealths.nbPvRouge - healths.nbPvRouge);
//        newTotalHealth.nbPvNoir = totalHP.nbPvNoir - (oldHealths.nbPvNoir - healths.nbPvNoir);
//        
//        return newTotalHealth;
//    }
//    
//    static NbPointsDeVie getAttackedHealth(int oldPvAttacker, int oldPvAttack, Case unit)
//    {   
//        int resultAttacker = oldPvAttacker - Utils.getAttackerDamage(unit.typeUnite) - (int)((oldPvAttack - 5) / 2);
//        int resultAttack = oldPvAttack - Utils.getAttackedDamage(unit.typeUnite) - (int)((oldPvAttacker - 5) / 2);
//        
//        if (resultAttacker < 0)
//            resultAttacker = 0;
//        
//        if (resultAttack < 0)
//            resultAttack = 0;
//        
//        NbPointsDeVie nbPv = new NbPointsDeVie();
//        
//        nbPv.nbPvRouge = unit.couleurUnite == Case.CAR_ROUGE ? resultAttacker : resultAttack;
//        nbPv.nbPvNoir = unit.couleurUnite == Case.CAR_NOIR ? resultAttacker : resultAttack;
//        
//        return nbPv;
//    }


    
    static int getDamagedHealth(Case unit, int healthEnemy)
    {   
        int resultAttacker = unit.pointsDeVie - Utils.getAttackerDamage(unit.typeUnite) - (int)((healthEnemy - 5) / 2);
        
        if (resultAttacker < 0)
            resultAttacker = 0;
        
        return resultAttacker;
    }
    
    static int getAttackedHealth(Case unit, int healthEnemy)
    {
        int resultAttack = healthEnemy - Utils.getAttackedDamage(unit.typeUnite) - (int)((unit.pointsDeVie - 5) / 2);
        
        if (resultAttack < 0)
            resultAttack = 0;
        
        return resultAttack;
    }
    
    static int getStepHealth(int step, int origHealth, char typeUnit)
    {
        return Math.max(origHealth - ((int)(Utils.getStepDamageCoef(typeUnit) * step)), 0);
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
    static void ajoutDepl(Coordonnees src, Coordonnees dst, ActionsPossibles actions) {
        actions.ajouterAction(chaineActionDepl(src, dst));
    }
    
    static void ajoutAttack(Coordonnees src, Coordonnees dst, Coordonnees attackPos, ActionsPossibles actions) {
        actions.ajouterAction(chaineActionAttack(src, dst, attackPos));
    }

    /**
     * Chaîne de caractères correspondant à une action-mesure de déplacement.
     *
     * @param src coordonnées de la case à l'origine du déplacement
     * @param dst coordonnées de la case destination du déplacement
     * @param nbPv nombre de points de vie de chaque joueur après l'action
     * @return la chaîne codant cette action-mesure
     */
    static String chaineActionDepl(Coordonnees src, Coordonnees dst) {
        return "" + src.carLigne() + src.carColonne()
                + "D" + dst.carLigne() + dst.carColonne();
    }
    
    static String chaineActionAttack(Coordonnees src, Coordonnees dst, Coordonnees attackPos)
    {
        return "" + src.carLigne() + src.carColonne()
                + "D" + dst.carLigne() + dst.carColonne()
                + 'A' + attackPos.carLigne() + attackPos.carColonne();
    }
}
