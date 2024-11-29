package lowatem;

import java.text.SimpleDateFormat;
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
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        System.out.println("actionsPossibles : lancement le " + format.format(new Date()));
        
        // se préparer à stocker les actions possibles
        ActionsPossibles actions = new ActionsPossibles();
        
        // calculer les points de vie sur le plateau initial
        
        // déplacements possibles depuis (g,G)
        
        NbPointsDeVie nbPv = nbPointsDeVie(plateau);
        
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
        Coordonnees dst = src.suivantes(dir);
        
        while (dst.estDansPlateau())
        {
            Case unite = plateau[dst.ligne][dst.colonne];
            
            if (!unite.unitePresente())
            {
                ajoutDepl(src, dst, actions, nbPv);
                
                checkAddAttack(plateau, src, dst, actions, nbPv);
            }
            
            dst = dst.suivantes(dir);
        }
    }
    
    void checkAddAttack(Case[][] plateau, Coordonnees src, Coordonnees dst, ActionsPossibles actions, NbPointsDeVie nbPv)
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
                    NbPointsDeVie newTotalHealth = getNewTotalHealth(origUnit, uniteNear, nbPv);
                    actions.ajouterAction(chaineActionAttack(src, dst, nearby, newTotalHealth));
                }
            }
        }
    }
    
    static NbPointsDeVie getNewTotalHealth(Case origUnit, Case attackUnit, NbPointsDeVie totalHP)
    {
        NbPointsDeVie oldHealths = new NbPointsDeVie();
        oldHealths.nbPvRouge = origUnit.couleurUnite == Case.CAR_ROUGE ? origUnit.pointsDeVie : attackUnit.pointsDeVie;
        oldHealths.nbPvNoir = origUnit.couleurUnite == Case.CAR_NOIR ? origUnit.pointsDeVie : attackUnit.pointsDeVie;

        NbPointsDeVie healths = setHealth(origUnit.pointsDeVie, attackUnit.pointsDeVie, origUnit.couleurUnite);

        NbPointsDeVie newTotalHealth = new NbPointsDeVie();
        newTotalHealth.nbPvRouge = totalHP.nbPvRouge - (oldHealths.nbPvRouge - healths.nbPvRouge);
        newTotalHealth.nbPvNoir = totalHP.nbPvNoir - (oldHealths.nbPvNoir - healths.nbPvNoir);
        
        return newTotalHealth;
    }
    
    static NbPointsDeVie setHealth(int oldPvAttacker, int oldPvAttack, char attackerColor)
    {   
        int resultAttacker = oldPvAttacker - 2 - (int)((oldPvAttack - 5) / 2);
        int resultAttack = oldPvAttack - 4 - (int)((oldPvAttacker - 5) / 2); // 3 - 4 + 1
        
        if (resultAttacker < 0)
            resultAttacker = 0;
        
        if (resultAttack < 0)
            resultAttack = 0;
        
        NbPointsDeVie nbPv = new NbPointsDeVie();
        
        nbPv.nbPvRouge = attackerColor == Case.CAR_ROUGE ? resultAttacker : resultAttack;
        nbPv.nbPvNoir = attackerColor == Case.CAR_NOIR ? resultAttacker : resultAttack;
        
        return nbPv;
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
}
