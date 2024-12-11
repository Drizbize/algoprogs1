package odomo;

import static odomo.Chauffage.creneau1;
import static odomo.Chauffage.creneau2;
import static odomo.Chauffage.temperEco;
import static odomo.Chauffage.temperNormal;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests des méthodes de la classe Chauffage.
 */
public class ChauffageTest {
    
    @Test
    public void testInitialiser() {
        Chauffage.initialiser();
        assertEquals(7, Chauffage.creneau1.length);
        assertEquals(7, Chauffage.creneau2.length);
        
        for (int[] creneau3 : Chauffage.creneau1) {
            for (int x = 0; x < creneau3.length; x++) {
                assertEquals(-1, creneau3[x]);
            }
        }
        
        for (int[] creneau3 : Chauffage.creneau2) {
            for (int x = 0; x < creneau3.length; x++) {
                assertEquals(-1, creneau3[x]);
            }
        }
        
        assertEquals(35., Chauffage.temperEco, 0.01);
        assertEquals(45., Chauffage.temperNormal, 0.01);
    }
    
    @Test
    public void testMatriceCreneaux() {
        Chauffage.initialiser();
        
        /*
        ###____
        #####___
        ###__###
        _###__###
        */
        Chauffage.creneau1[0][0] = 0;
        Chauffage.creneau1[0][1] = 2;
        
        Chauffage.creneau1[1][0] = 0;
        Chauffage.creneau1[1][1] = 2;
        Chauffage.creneau1[1][0] = 3;
        Chauffage.creneau1[1][1] = 4;
        
        Chauffage.creneau1[2][0] = 0;
        Chauffage.creneau1[2][1] = 2;
        Chauffage.creneau1[2][0] = 5;
        Chauffage.creneau1[2][1] = 7;
        
        Chauffage.creneau1[3][0] = 1;
        Chauffage.creneau1[3][1] = 3;
        Chauffage.creneau1[3][0] = 6;
        Chauffage.creneau1[3][1] = 8;
        
        boolean[][] res = Chauffage.matriceCreneaux();
        
        for (int i = 0; i < 4; i++)
        {
            for (int x = 0; x < res[i].length; x++)
            {
                assertTrue(res[i][x] == ((creneau1[i][0] <= x && x <= creneau1[i][1]) || (creneau2[i][0] <= x && x <= creneau2[i][1])));
            }
        }
    }
}
