package lowatem;

/**
 * Quelques fonctions utiles au projet. Vous devez comprendre ce que font ces
 * méthodes (voir leur documentation), mais pas comment elles le font (leur
 * code).
 *
 * À faire évoluer en fonction des nouvelles natures de case, de nouveaux types
 * d'unités, etc.
 */
public class Utils {

    /**
     * Indice de la dernière COLONNE.
     */
    public final static int NUM_COLONNE_MAX = 13;
    
    // type unites
    public final static char CAR_TANK = 'C';
    public final static char CAR_LANCE_MISSILES = 'L';
    public final static char CAR_PLANE = 'A';
    public final static char CAR_BATTLE_SHIP = 'N';
    
    
    public final static char CAR_EAU = 'E';
    
    static boolean canGo(char typeUnit, char nature)
    {
        boolean can = true;
        
        switch (typeUnit) {
            case Case.CAR_SOLDATS, CAR_TANK, CAR_LANCE_MISSILES -> {
                can = nature == Case.CAR_TERRE;
            }
            case CAR_BATTLE_SHIP -> {
                can = nature == CAR_EAU;
            }
        }
        
        return can;
    }
    
    static int getAttackedDamage(char typeUnit)
    {
        return switch (typeUnit) {
            case Case.CAR_SOLDATS -> 4;
            case CAR_TANK -> 8;
            case CAR_LANCE_MISSILES -> 6;
            case CAR_PLANE, CAR_BATTLE_SHIP -> 7;
            default -> 0;
        };
    }
    
    static int getAttackerDamage(char typeUnit)
    {
        return switch (typeUnit) {
            case Case.CAR_SOLDATS -> 2;
            case CAR_TANK -> 3;
            case CAR_LANCE_MISSILES -> 3;
            case CAR_PLANE, CAR_BATTLE_SHIP -> 2;
            default -> 0;
        };
    }
    
    static double getStepDamageCoef(char typeUnit)
    {
        return switch (typeUnit) {
            case Case.CAR_SOLDATS -> 0.3;
            case CAR_TANK -> 0.7;
            case CAR_LANCE_MISSILES -> 0.4;
            case CAR_PLANE -> 0.7;
            case CAR_BATTLE_SHIP -> 0.6;
            default -> 0;
        };
    }

    /**
     * Construit un plateau à partir de sa représentation sour forme texte,
     * comme renvoyé par formatTexte(), avec coordonnées et séparateurs.
     *
     * @param texteOriginal le texte du plateau
     * @return le plateau
     */
    public static Case[][] plateauDepuisTexte(final String texteOriginal) {
        final Case[][] plateau = new Case[Coordonnees.NB_LIGNES][Coordonnees.NB_COLONNES];
        final String[] lignes = texteOriginal.split("\n");
        for (int lig = 0; lig < Coordonnees.NB_LIGNES; lig++) {
            final String ligne1 = lignes[2 * lig + 1];
            final String ligne2 = lignes[2 * lig + 2];
            for (int col = 0; col < Coordonnees.NB_COLONNES; col++) {
                final String codageLigne1 = ligne1.substring(2 + 4 * col, 2 + 4 * col + 3);
                final String codageLigne2 = ligne2.substring(2 + 4 * col, 2 + 4 * col + 3);
                plateau[lig][col] = caseDepuisCodage(codageLigne1, codageLigne2);
            }
        }
        return plateau;
    }

    /**
     * Construit une case depuis son codage.
     *
     * @param ligne1 codage de la case, première ligne
     * @param ligne2 codage de la case, deuxième ligne
     * @return case correspondante
     */
    public static Case caseDepuisCodage(final String ligne1, final String ligne2) {
        // vérification des arguments
        if (ligne1.length() != 3 || ligne2.length() != 3) {
            throw new IllegalArgumentException(
                    "Un codage de ligne doit être sur 3 caractères par ligne.");
        }
        Case laCase = new Case(Case.CAR_VIDE, Case.CAR_ROUGE, 0, 0, Case.CAR_TERRE);
        //
        // ligne 1
        //
        // 1er caractère : nature
        char carNature = ligne1.charAt(0);
        if (carNature == '-') {
            laCase.nature = Case.CAR_TERRE;
        } else {
            laCase.nature = carNature;
        }
        // 2ème caractère : rien
        // 3ème caractère : altitude
        char carAltitude = ligne1.charAt(2);
        if (carAltitude == '-') {
            laCase.altitude = 0;
        } else {
            laCase.altitude = Integer.parseInt("" + carAltitude);
        }
        //
        // ligne 2
        //
        // 1er caractère : type d'unité
        laCase.typeUnite = ligne2.charAt(0);
        // 2ème caractère : couleur
        char carCouleur = ligne2.charAt(1);
        if (laCase.typeUnite == Case.CAR_VIDE) {
            if (carCouleur != Case.CAR_VIDE) {
                throw new IllegalArgumentException("Cette case ne contient pas d'unité,"
                        + " donc ne devrait pas avoir de couleur associée.");
            }
            carCouleur = Case.CAR_NOIR;
        } else {
            if (carCouleur != Case.CAR_NOIR && carCouleur != Case.CAR_ROUGE) {
                throw new IllegalArgumentException(
                        "Caractère couleur non admis : " + carCouleur);
            }
        }
        laCase.couleurUnite = carCouleur;
        // 3ème caractère : points de vie
        char carPdV = ligne2.charAt(2);
        if (laCase.typeUnite == Case.CAR_VIDE) {
            if (carPdV != Case.CAR_VIDE) {
                throw new IllegalArgumentException("Cette case ne contient pas d'unité,"
                        + " donc ne devrait pas avoir de points de vie associés.");
            }
            laCase.pointsDeVie = 0;
        } else {
            laCase.pointsDeVie = Integer.parseInt("" + carPdV);
        }
        return laCase;
    }
}
