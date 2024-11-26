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
                    ajoutDeplDepuis(plateau, new Coordonnees(y, x), actions, nbPv);
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
            if (!plateau[dst.ligne][dst.colonne].unitePresente())
            {
                ajoutDepl(src, dst, actions, nbPv);
            }
            
            dst = dst.suivantes(dir);
        }
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
}
