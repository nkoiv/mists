/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LocationTests;

import TestTools.CompareTools;
import TestTools.JavaFXThreadingRule;
import com.nkoiv.mists.game.world.BGMap;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.TileMap;
import com.nkoiv.mists.game.world.mapgen.DungeonGenerator;
import java.util.ArrayList;
import java.util.Random;
import javafx.scene.image.Image;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author nikok
 */
public class MapGeneratorTest {
    private static Location testLocation;
    private static DungeonGenerator testMapGen;
    
    @Rule 
    public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();
    /*    
    @BeforeClass
    public static void setUpClass() {
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    */
    @Before
    public void setUp() {
        testLocation = new Location("TestLocation", new BGMap(new Image("/images/pocmap.png")));
        testMapGen = new DungeonGenerator();
        testLocation.setMapGen(testMapGen);
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void generatedMapIsOfTheDesiredSize() {
        Random rng = new Random();
        int randomXSize = rng.nextInt(30)+30;
        int randomYSize = rng.nextInt(30)+30;
        TileMap generatedTilemap = DungeonGenerator.generateDungeon(testLocation.getMapGen(), randomXSize, randomYSize);
        System.out.println("Randomed size was "+randomXSize+","+randomYSize);
        
        assertTrue(generatedTilemap.getWidth() == randomXSize*generatedTilemap.getTileSize() && generatedTilemap.getHeight()==randomYSize*generatedTilemap.getTileSize()); 
    }
    
    @Test
    public void bspAreasAreLargerThanGiveMinimumSize() {
        Random rng = new Random();
        int randomXSize = rng.nextInt(30)+30;
        int randomYSize = rng.nextInt(30)+30;
        int absMinSize = 5;
        ArrayList<DungeonGenerator.BSParea> testBSP = DungeonGenerator.BSPdungeon(testMapGen, randomXSize, randomYSize, 20, 0.3f, 0.7f, absMinSize, true);
        for (DungeonGenerator.BSParea a : testBSP) {
            assertTrue(CompareTools.isGreaterThan(a.width * a.height, absMinSize));
        } 
    }
       
    @Test
    public void bspAreasCanBeGeneratedOutOfTree() {
        Random rng = new Random();
        int randomXSize = rng.nextInt(30)+30;
        int randomYSize = rng.nextInt(30)+30;
        int absMinSize = 5;
        ArrayList<DungeonGenerator.BSParea> testBSP = DungeonGenerator.BSPdungeon(testMapGen, randomXSize, randomYSize, 20, 0.3f, 0.7f, absMinSize, false);
        assertTrue(testBSP.size() > 1);
    }
    
}
