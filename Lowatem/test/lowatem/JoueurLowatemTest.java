package lowatem;

import static lowatem.JoueurLowatem.getStepHealth;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Tests unitaires de la classe JoueurLowatem.
 */
public class JoueurLowatemTest {

    /**
     * Test de la fonction actionsPossibles. Commentez les appels aux tests des
     * niveaux inférieurs, n'activez que le test du niveau à valider.
     */
    @Test
    public void testActionsPossibles() {
        //testActionsPossibles_niveau1();
        //testActionsPossibles_niveau2();
        //testActionsPossibles_niveau3();
        //testActionsPossibles_niveau4();
        testActionsPossibles_niveau5();
    }

    /**
     * Test de la fonction actionsPossibles, au niveau 1.
     */
    public void testActionsPossibles_niveau1() {
        JoueurLowatem joueur = new JoueurLowatem();
        // un plateau sur lequel on veut tester actionsPossibles()
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU1);
        // on choisit la couleur du joueur
        char couleur = 'R';
        // on choisit le niveau
        int niveau = 1;
        // on lance actionsPossibles
        String[] actionsPossiblesDepuisPlateau
                = joueur.actionsPossibles(plateau, couleur, niveau);
        ActionsPossibles actionsPossibles
                = new ActionsPossibles(actionsPossiblesDepuisPlateau);
        // on peut afficher toutes les actions possibles calculées :
        actionsPossibles.afficher();
        // on peut aussi tester si une action est dans les actions possibles :
        assertTrue(actionsPossibles.contient("gGDgA,9,0"));
        assertTrue(actionsPossibles.contient("gGDgB,9,0"));
        assertTrue(actionsPossibles.contient("gGDgG,9,0"));
        assertTrue(actionsPossibles.contient("gGDgA,9,0"));
        assertTrue(actionsPossibles.contient("gGDaG,9,0"));
        assertTrue(actionsPossibles.contient("gGDnG,9,0"));
        // on peut aussi tester si une action n'est pas dans les actions possibles :
        assertFalse(actionsPossibles.contient("gGDgO,9,0"));
        assertFalse(actionsPossibles.contient("gGDgA,8,0"));
        // vérifions s'il y a le bon nombre d'actions possibles :
        assertEquals(Coordonnees.NB_LIGNES + Coordonnees.NB_COLONNES - 1,
                actionsPossiblesDepuisPlateau.length);
    }

    /**
     * Test de la fonction actionsPossibles, au niveau 2.
     */
    public void testActionsPossibles_niveau2() {
        JoueurLowatem joueur = new JoueurLowatem();
        // un plateau sur lequel on veut tester actionsPossibles()
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU2);
        
        // on choisit la couleur du joueur
        char couleur = 'R';
        
        // on choisit le niveau
        int niveau = 2;
        
        // on lance actionsPossibles
        String[] actionsPossiblesDepuisPlateau = joueur.actionsPossibles(plateau, couleur, niveau);
        ActionsPossibles actionsPossibles = new ActionsPossibles(actionsPossiblesDepuisPlateau);
        
        // on peut afficher toutes les actions possibles calculées :
        actionsPossibles.afficher();
        // on peut aussi tester si une action est dans les actions possibles :
        assertTrue(actionsPossibles.contient("dADdA,9,0"));
        assertTrue(actionsPossibles.contient("dADdN,9,0"));
        assertTrue(actionsPossibles.contient("dADdG,9,0"));
        assertTrue(actionsPossibles.contient("dADaA,9,0"));
        assertTrue(actionsPossibles.contient("dADnA,9,0"));
        
        // on peut aussi tester si une action n'est pas dans les actions possibles :
        assertFalse(actionsPossibles.contient("dADnO,9,0"));
        assertFalse(actionsPossibles.contient("dADdA,8,0"));
        
        // vérifions s'il y a le bon nombre d'actions possibles :
        assertEquals(Coordonnees.NB_LIGNES + Coordonnees.NB_COLONNES - 1,
                actionsPossiblesDepuisPlateau.length);
    }
    
    public void testActionsPossibles_niveau3()
    {
        JoueurLowatem joueur = new JoueurLowatem();
        // un plateau sur lequel on veut tester actionsPossibles()
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU3);
        
        char couleur = 'R';
        
        String[] actionsPossiblesDepuisPlateau = joueur.actionsPossibles(plateau, couleur, 3);
        ActionsPossibles actionsPossibles = new ActionsPossibles(actionsPossiblesDepuisPlateau);
        
        actionsPossibles.afficher();
        
        assertTrue(actionsPossibles.contient("dFDcF,16,0"));
        assertTrue(actionsPossibles.contient("dFDaF,16,0"));
        assertTrue(actionsPossibles.contient("dADdE,16,0"));
        assertTrue(actionsPossibles.contient("dADdG,16,0"));
        
        assertFalse(actionsPossibles.contient("dADdF,16,0"));
        assertFalse(actionsPossibles.contient("dFDdA,16,0"));
    }
    
    public void testActionsPossibles_niveau4()
    {
        JoueurLowatem joueur = new JoueurLowatem();
        // un plateau sur lequel on veut tester actionsPossibles()
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU4);
        
        String[] actionsPossiblesDepuisPlateau = joueur.actionsPossibles(plateau, 'R', 4);
        ActionsPossibles actionsPossiblesRouge = new ActionsPossibles(actionsPossiblesDepuisPlateau);
        
        actionsPossiblesDepuisPlateau = joueur.actionsPossibles(plateau, 'N', 4);
        ActionsPossibles actionsPossiblesNoir = new ActionsPossibles(actionsPossiblesDepuisPlateau);
        
        //dADdE dADdG gGDdG gGDgF
        assertTrue(actionsPossiblesRouge.contient("dADdE,13,3"));
        assertTrue(actionsPossiblesRouge.contient("dADdG,13,3"));
        assertTrue(actionsPossiblesRouge.contient("gGDdG,13,3"));
        assertTrue(actionsPossiblesRouge.contient("gGDgF,13,3"));
        
        
        //dADdF dFDeF cIDcG
        assertFalse(actionsPossiblesRouge.contient("dADdF,13,3"));
        assertFalse(actionsPossiblesRouge.contient("dFDeF,13,3"));
        assertFalse(actionsPossiblesRouge.contient("cIDcG,13,3"));
        
        //dFDdB dFDdG cIDeI
        assertTrue(actionsPossiblesNoir.contient("dFDdB,13,3"));
        assertTrue(actionsPossiblesNoir.contient("dFDdG,13,3"));
        assertTrue(actionsPossiblesNoir.contient("cIDeI,13,3"));
        
        //dADbA gGDdG dFDdA
        assertFalse(actionsPossiblesNoir.contient("dADbA,13,3"));
        assertFalse(actionsPossiblesNoir.contient("gGDdG,13,3"));
        assertFalse(actionsPossiblesNoir.contient("dFDdA,13,3"));
    }
    
    public void testActionsPossibles_niveau5()
    {
        JoueurLowatem joueur = new JoueurLowatem();
        // un plateau sur lequel on veut tester actionsPossibles()
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU5);
        
        String[] actionsPossiblesDepuisPlateau = joueur.actionsPossibles(plateau, 'R', 5);
        ActionsPossibles actionsPossiblesRouge = new ActionsPossibles(actionsPossiblesDepuisPlateau);
        
        actionsPossiblesDepuisPlateau = joueur.actionsPossibles(plateau, 'N', 5);
        ActionsPossibles actionsPossiblesNoir = new ActionsPossibles(actionsPossiblesDepuisPlateau);
        
        //dADdE dADdG gGDdG gGDgF
        assertTrue(actionsPossiblesRouge.contient("dADdE,13,7"));
        assertTrue(actionsPossiblesRouge.contient("dADdG,13,7"));
        assertTrue(actionsPossiblesRouge.contient("gGDdG,13,7"));
        assertTrue(actionsPossiblesRouge.contient("gGDgF,13,7"));
        
        
        //dADdF dFDeF cIDcG
        assertFalse(actionsPossiblesRouge.contient("dADdF,13,7"));
        assertFalse(actionsPossiblesRouge.contient("dFDeF,13,7"));
        assertFalse(actionsPossiblesRouge.contient("cIDcG,13,7"));
        
        //dFDdB dFDdG cIDeI
        assertTrue(actionsPossiblesNoir.contient("dFDdB,13,7"));
        assertTrue(actionsPossiblesNoir.contient("dFDdG,13,7"));
        assertTrue(actionsPossiblesNoir.contient("cIDeI,13,7"));
        
        //dADbA gGDdG dFDdA
        assertFalse(actionsPossiblesNoir.contient("dADbA,13,7"));
        assertFalse(actionsPossiblesNoir.contient("gGDdG,13,7"));
        assertFalse(actionsPossiblesNoir.contient("dFDdA,13,7"));
        
        
        //dADdAAeA dADdEAdF dADdIAcI gGDdGAdF
        assertTrue(actionsPossiblesRouge.contient("dADdAAeA,13,7"));
        assertTrue(actionsPossiblesRouge.contient("dADdEAdF,13,7"));
        assertTrue(actionsPossiblesRouge.contient("dADdIAcI,13,7"));
        assertTrue(actionsPossiblesRouge.contient("gGDdGAdF,13,7"));
        
        //eADeAAdA dFDdBAdA cIDcAAdA
        assertTrue(actionsPossiblesNoir.contient("eADeAAdA,13,7"));
        assertTrue(actionsPossiblesNoir.contient("dFDdBAdA,13,7"));
        assertTrue(actionsPossiblesNoir.contient("cIDcAAdA,13,7"));
    }
    
    public String getDirectionWithHealth(String diractionAttack, Case[][] plateau, NbPointsDeVie totalHP)
    {
        Coordonnees src = Coordonnees.depuisCars(diractionAttack.charAt(0), diractionAttack.charAt(1));
        Coordonnees attack = Coordonnees.depuisCars(diractionAttack.charAt(6), diractionAttack.charAt(7));
        
        Case origUnit = plateau[src.ligne][src.colonne];
        Case attackUnit = plateau[attack.ligne][attack.colonne];
        
        NbPointsDeVie newTotalHealth = JoueurLowatem.getNewTotalHealth(origUnit, attackUnit, totalHP);
        
        return diractionAttack + "," + newTotalHealth.nbPvRouge + "," + newTotalHealth.nbPvNoir;
    }
    
    public String getDirection(String diraction, Case[][] plateau, NbPointsDeVie totalHP)
    {        
        String result;
        
        if (diraction.length() == 8)
        {
            result = getDirectionWithHealth(diraction, plateau, totalHP);
        }
        else
        {
            result = diraction + "," + totalHP.nbPvRouge + "," + totalHP.nbPvNoir;
        }
        
        return result;
    }
    
    @Test
    public void testActionsPossible_niveau6()
    {
        JoueurLowatem joueur = new JoueurLowatem();
        // un plateau sur lequel on veut tester actionsPossibles()
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU6);
        
        NbPointsDeVie totalHP = JoueurLowatem.nbPointsDeVie(plateau);
        
        String[] actionsPossiblesDepuisPlateau = joueur.actionsPossibles(plateau, 'R', 6);
        ActionsPossibles actionsPossiblesRouge = new ActionsPossibles(actionsPossiblesDepuisPlateau);
        
        actionsPossiblesDepuisPlateau = joueur.actionsPossibles(plateau, 'N', 6);
        ActionsPossibles actionsPossiblesNoir = new ActionsPossibles(actionsPossiblesDepuisPlateau);
        
        actionsPossiblesRouge.afficher();
        System.out.println("----------------");
        actionsPossiblesNoir.afficher();
        
        //jADjFAkF jADjIAjJ hKDjKAjJ jMDgMAfM
        assertTrue(actionsPossiblesNoir.contient(getDirection("jADjFAkF", plateau, totalHP)));
        assertTrue(actionsPossiblesNoir.contient(getDirection("jADjIAjJ", plateau, totalHP)));
        assertTrue(actionsPossiblesNoir.contient(getDirection("hKDjKAjJ", plateau, totalHP)));
        assertTrue(actionsPossiblesNoir.contient(getDirection("jMDgMAfM", plateau, totalHP)));
        
        //kFDkAAjA kFDkMAjM
        assertTrue(actionsPossiblesRouge.contient(getDirection("kFDkAAjA", plateau, totalHP)));
        assertTrue(actionsPossiblesRouge.contient(getDirection("kFDkMAjM", plateau, totalHP)));
        
        assertFalse(actionsPossiblesRouge.contient(getDirection("jADjFAkF", plateau, totalHP)));
        assertFalse(actionsPossiblesRouge.contient(getDirection("jADjIAjJ", plateau, totalHP)));
        assertFalse(actionsPossiblesRouge.contient(getDirection("hKDjKAjJ", plateau, totalHP)));
        assertFalse(actionsPossiblesRouge.contient(getDirection("jMDgMAfM", plateau, totalHP)));
    }
    
    @Test
    public void testActionsPossible_niveau7()
    {
        JoueurLowatem joueur = new JoueurLowatem();
        // un plateau sur lequel on veut tester actionsPossibles()
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU7);
        
        NbPointsDeVie totalHP = JoueurLowatem.nbPointsDeVie(plateau);
        
        String[] actionsPossiblesDepuisPlateau = joueur.actionsPossibles(plateau, 'R', 7);
        ActionsPossibles actionsPossiblesRouge = new ActionsPossibles(actionsPossiblesDepuisPlateau);
        
        actionsPossiblesDepuisPlateau = joueur.actionsPossibles(plateau, 'N', 7);
        ActionsPossibles actionsPossiblesNoir = new ActionsPossibles(actionsPossiblesDepuisPlateau);
        
        //gKDjK kGDkJ
        assertTrue(actionsPossiblesRouge.contient(getDirection("gKDjK", plateau, totalHP)));
        assertTrue(actionsPossiblesRouge.contient(getDirection("kGDkJ", plateau, totalHP)));
        
        //gKDkK gKDmK kGDkK kGDkN
        assertFalse(actionsPossiblesRouge.contient(getDirection("gKDkK", plateau, totalHP)));
        assertFalse(actionsPossiblesRouge.contient(getDirection("gKDmK", plateau, totalHP)));
        assertFalse(actionsPossiblesRouge.contient(getDirection("kGDkK", plateau, totalHP)));
        assertFalse(actionsPossiblesRouge.contient(getDirection("kGDkN", plateau, totalHP)));
        
        //dKDjK jHDjK
        assertTrue(actionsPossiblesNoir.contient(getDirection("dKDjK", plateau, totalHP)));
        assertTrue(actionsPossiblesNoir.contient(getDirection("jHDjK", plateau, totalHP)));
        
        //dKDkK dKDnK jHDjL jHDjN
        assertFalse(actionsPossiblesNoir.contient(getDirection("dKDkK", plateau, totalHP)));
        assertFalse(actionsPossiblesNoir.contient(getDirection("dKDnK", plateau, totalHP)));
        assertFalse(actionsPossiblesNoir.contient(getDirection("jHDjL", plateau, totalHP)));
        assertFalse(actionsPossiblesNoir.contient(getDirection("jHDjN", plateau, totalHP)));
    }
    
    @Test
    public void testActionsPossible_niveau8()
    {
        JoueurLowatem joueur = new JoueurLowatem();
        // un plateau sur lequel on veut tester actionsPossibles()
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU8);
        
        String[] actionsPossiblesDepuisPlateau = joueur.actionsPossibles(plateau, 'R', 8);
        ActionsPossibles actionsPossiblesRouge = new ActionsPossibles(actionsPossiblesDepuisPlateau);
        
        actionsPossiblesDepuisPlateau = joueur.actionsPossibles(plateau, 'N', 8);
        ActionsPossibles actionsPossiblesNoir = new ActionsPossibles(actionsPossiblesDepuisPlateau);
        
        actionsPossiblesRouge.afficher();
        System.out.println("------------------------------------");
        actionsPossiblesNoir.afficher();
        
        //aIDnI,73,76 aIDkI,69,76 aIDkIAkJ,65,72
        assertTrue(actionsPossiblesRouge.contient("aIDnI,69,76"));
        assertTrue(actionsPossiblesRouge.contient("aIDkI,69,76"));
        assertTrue(actionsPossiblesRouge.contient("aIDkIAkJ,65,72"));
        
        //aIDnI,72,76 aIDnE,72,76 aIDkIAkJ,69,72
        assertFalse(actionsPossiblesRouge.contient("aIDnI,72,76"));
        assertFalse(actionsPossiblesRouge.contient("aIDnE,72,76"));
        assertFalse(actionsPossiblesRouge.contient("aIDkIAkJ,69,72"));
        
        //nNDaN,72,73 gADgMAgL,68,69 gADgM,72,73
        assertTrue(actionsPossiblesNoir.contient("nNDaN,72,73"));
        assertTrue(actionsPossiblesNoir.contient("gADgM,72,73"));
        assertTrue(actionsPossiblesNoir.contient("gADgMAgL,68,69"));
        
        //nNDaN,72,76
        assertFalse(actionsPossiblesNoir.contient("nNDaN,72,76"));
    }

    @Test
    public void testAjoutDeplDepuis() {
        
        JoueurLowatem joueur = new JoueurLowatem();
        ActionsPossibles actions = new ActionsPossibles();
        NbPointsDeVie nbPv = new NbPointsDeVie(9, 0);
        
        Case[][] plateau = Utils.plateauDepuisTexte(PLATEAU_NIVEAU3);
        joueur.ajoutDeplDepuis(plateau, Coordonnees.depuisCars('f', 'D'), actions, nbPv);
        // les horizontaux avec la case d'origine
        assertTrue(actions.contient("fDDfA,9,0"));
        assertTrue(actions.contient("fDDfB,9,0"));
        assertTrue(actions.contient("fDDfC,9,0"));
        assertTrue(actions.contient("fDDfD,9,0"));
        assertTrue(actions.contient("fDDfF,9,0"));
        assertTrue(actions.contient("fDDfH,9,0"));
        assertTrue(actions.contient("fDDfN,9,0"));
        // les verticaux
        assertTrue(actions.contient("fDDaD,9,0"));
        assertTrue(actions.contient("fDDhD,9,0"));
        // des mauvais
        assertFalse(actions.contient("fDDaF,9,0"));
        assertFalse(actions.contient("fDDaA,9,0"));
        // le bon nombre d'unités
        assertFalse(actions.contient("fDDfA,1,0"));
        // finalement on doit en avoir 1 + 13 + 13
        assertEquals(27, actions.nbActions);
    }

    @Test
    public void testChaineActionDepl() {
        assertEquals("cEDfC,9,0", JoueurLowatem.chaineActionDepl(
                Coordonnees.depuisCars('c', 'E'),
                Coordonnees.depuisCars('f', 'C'),
                new NbPointsDeVie(9, 0)));
        assertEquals("nDDnD,9,0", JoueurLowatem.chaineActionDepl(
                Coordonnees.depuisCars('n', 'D'),
                Coordonnees.depuisCars('n', 'D'),
                new NbPointsDeVie(9, 0)));
    }

    @Test
    public void testNbPointsDeVie() {
        // à décommenter le moment venu...
        // plateau : rouge 9, noir 0
        Case[][] plateau1 = Utils.plateauDepuisTexte(PLATEAU_NIVEAU1);
        NbPointsDeVie nbPv1 = JoueurLowatem.nbPointsDeVie(plateau1);
        assertEquals(9, nbPv1.nbPvRouge);
        assertEquals(0, nbPv1.nbPvNoir);
        // plateau : rouge 9, noir 0
        Case[][] plateau2 = Utils.plateauDepuisTexte(PLATEAU_NIVEAU2);
        NbPointsDeVie nbPv2 = JoueurLowatem.nbPointsDeVie(plateau2);
        assertEquals(9, nbPv2.nbPvRouge);
        assertEquals(0, nbPv2.nbPvNoir);
        // plateau : rouge 14, noir 9
        Case[][] plateauNbPv = Utils.plateauDepuisTexte(PLATEAU_NB_PV);
        NbPointsDeVie nbPv = JoueurLowatem.nbPointsDeVie(plateauNbPv);
        assertEquals(14, nbPv.nbPvRouge);
        assertEquals(9, nbPv.nbPvNoir);
    }

    /**
     * Un plateau de base, sous forme de chaîne. Pour construire une telle
     * chaîne depuis votre sortie.log, déclarez simplement final String
     * MON_PLATEAU = ""; puis copiez le plateau depuis votre sortie.log, et
     * collez-le entre les guillemets. Puis Alt+Shift+f pour mettre en forme.
     */
    final String PLATEAU_NIVEAU1
            = """
                 A   B   C   D   E   F   G   H   I   J   K   L   M   N 
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              a|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              b|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              c|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              d|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              e|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              f|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              g|   |   |   |   |   |   |SR9|   |   |   |   |   |   |   |
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
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              n|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              """;

    final String PLATEAU_NIVEAU2
            = """
                 A   B   C   D   E   F   G   H   I   J   K   L   M   N 
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              a|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              b|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              c|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              d|SR9|   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              e|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              f|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              g|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
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
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              n|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              """;
    
    final String PLATEAU_NIVEAU3
            = """
                 A   B   C   D   E   F   G   H   I   J   K   L   M   N 
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              a|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              b|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              c|   |   |   |   |   |   |   |   |SR2|   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              d|SR9|   |   |   |   |SR1|   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              e|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              f|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              g|   |   |   |   |   |   |SR4|   |   |   |   |   |   |   |
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
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              n|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              """;
    
    final String PLATEAU_NIVEAU4
            = """
                 A   B   C   D   E   F   G   H   I   J   K   L   M   N 
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              a|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              b|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              c|   |   |   |   |   |   |   |   |SN2|   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              d|SR9|   |   |   |   |SN1|   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              e|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              f|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              g|   |   |   |   |   |   |SR4|   |   |   |   |   |   |   |
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
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              n|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              """;
    
    final String PLATEAU_NIVEAU5
            = """
                 A   B   C   D   E   F   G   H   I   J   K   L   M   N 
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              a|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              b|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              c|   |   |   |   |   |   |   |   |SN2|   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              d|SR9|   |   |   |   |SN1|   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              e|SN4|   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              f|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              g|   |   |   |   |   |   |SR4|   |   |   |   |   |   |   |
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
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              n|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              """;
    
    final String PLATEAU_NIVEAU6
            = """
                A   B   C   D   E   F   G   H   I   J   K   L   M   N 
              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
             a|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
             b|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
             c|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
             d|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
             e|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
             f|   |   |   |   |   |   |   |   |   |   |   |   |SR3|   |
              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
             g|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
             h|   |   |   |   |   |   |   |   |   |   |SN3|   |   |   |
              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
             i|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
             j|SN3|   |   |   |   |   |   |   |   |SR3|   |   |SN2|   |
              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
             k|   |   |   |   |   |SR2|   |   |   |   |   |   |   |   |
              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
             l|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
             m|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
             n|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
              +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              """;
    
    final String PLATEAU_NIVEAU7
            = """
                 A   B   C   D   E   F   G   H   I   J   K   L   M   N 
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              a|SR1|SR4|   |   |   |   |   |   |SR2|   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              b|   |   |   |SN4|   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              c|SR4|   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              d|   |   |   |   |   |   |   |   |   |   |SN4|   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              e|   |   |   |   |   |SN4|   |SN6|   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              f|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              g|   |   |   |   |SR4|   |   |   |   |   |SR4|   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              h|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+E--+---+
              i|   |   |   |   |   |   |   |   |   |   |SN2|   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+E--+E--+E--+
              j|SN4|SN2|   |   |   |   |   |SN4|   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+E--+E--+E--+E--+
              k|   |   |   |   |   |   |SR2|SR4|   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+E--+---+---+---+
              l|   |   |SN1|   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              m|   |   |   |   |   |   |SR6|   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              n|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              """;
    
    final String PLATEAU_NIVEAU8
            = """
                 A   B   C   D   E   F   G   H   I   J   K   L   M   N 
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              a|   |   |   |   |   |   |   |   |SR9|   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              b|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              c|   |   |   |   |   |   |   |   |   |   |   |   |SR9|   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              d|   |SN9|   |   |   |   |   |   |SR8|   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              e|   |   |   |SN9|   |   |   |   |   |SN9|   |SR9|   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              f|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              g|SN9|   |   |   |   |   |   |   |   |   |   |SR9|   |   |
               +E--+---+E--+E--+---+---+---+---+---+---+---+---+---+---+
              h|   |   |   |   |   |   |   |   |   |SR9|   |   |   |   |
               +---+E--+E--+E--+---+---+---+---+---+---+---+---+---+---+
              i|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              j|SR1|   |SN5|   |   |   |   |   |   |   |SR9|   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              k|   |   |SN9|   |   |   |   |   |   |SN9|   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              l|   |   |   |   |SR9|   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              m|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              n|   |SN8|   |   |   |   |   |   |   |   |   |   |   |SN9|
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              """;
    

    final String PLATEAU_NB_PV
            = """
                 A   B   C   D   E   F   G   H   I   J   K   L   M   N 
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              a|SN3|   |   |   |   |   |   |   |   |   |   |   |   |SN5|
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              b|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              c|   |   |   |SR4|   |   |   |   |   |   |   |SN1|   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              d|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              e|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              f|   |   |   |   |   |   |   |   |   |   |   |   |   |SR2|
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              g|   |   |   |   |   |   |   |   |   |   |   |   |   |   |
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
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              n|SR3|   |   |   |   |   |   |   |   |   |   |   |   |SR5|
               +---+---+---+---+---+---+---+---+---+---+---+---+---+---+
              """;
}
