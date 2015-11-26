/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world.mapgen;

import com.nkoiv.mists.game.Direction;
import com.nkoiv.mists.game.Global;
import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.world.Location;
import com.nkoiv.mists.game.world.TileMap;
import java.util.ArrayList;
import java.util.Random;

/**
 * MapGenerator makes maps for Locations.
 * It uses procedural generation, splitting the given
 * area in smaller and smaller chunks randomly, until 
 * its satisfied with what it has.
 * After the area has been split into segments, its
 * populated by rooms that are joined by corridors.
 * @author nikok
 */
public class DungeonGenerator implements Global{
    private static final int CLEAR = 0;
    private static final int FLOOR = 1;
    private static final int WALL = 2;
    private static final int DOOR = 4;
    private static final Random rnd = new Random();

    public DungeonGenerator() {


    }

    public static TileMap generateDungeon(Location l, int xSize, int ySize) {
            int totalAreas  = 20; //TODO: Make these into parameters
            int minAreaSize = 6;
            boolean treeMode = true;
            ArrayList<BSParea> BSPareas = BSPdungeon(l.getMapGen(), xSize, ySize, totalAreas, 0.3f, 0.7f, minAreaSize, treeMode);
            int[][] randomStructures = dungeonStructures(BSPareas, xSize, ySize, minAreaSize);
            TileMap dungeon =  new TileMap (xSize, ySize, randomStructures);
            //TODO: make the floor uneven, use different tiles and that stuff
            return dungeon;
    }

    public static int[][] dungeonStructures (ArrayList<BSParea> BSPareas, int xSize, int ySize, int roomMinSize) {
            int[][] intMap;
            intMap = drawAreasOnMap (xSize, ySize, BSPareas, roomMinSize);
            intMap = generateCorridorsToAdjacentRooms (intMap, BSPareas, 1.0f);
            Mists.logger.info("IntMap size: "+intMap.length+"x"+intMap[0].length);

            //Clean out the unneeded doors
            int[][] cleanedMap = doorwayCleaner(intMap);
            //Print the map for testing
            Mists.logger.info("Cleaned map size: "+cleanedMap.length+"x"+cleanedMap[0].length);
            System.out.println(mapToString(cleanedMap, xSize, ySize));

            return cleanedMap;
    }


    public static ArrayList<BSParea> BSPdungeon (DungeonGenerator mapGen, int xSize, int ySize, int totalAreas, float minSize, float maxSize, int absMin, boolean tree) {

            float absMax = 0.5f; //Max size for any given area
            ArrayList<BSParea> BSPareas = new ArrayList<BSParea>();
            BSParea mainArea = mapGen.new BSParea(0, 0, xSize, ySize, null);
            BSPareas.add(mainArea);

            Mists.logger.info("Creating a new BSP-style map");
            if (!tree) {
                int loops = 0;
                while (BSPareas.size()<totalAreas && loops<totalAreas*10) {
                    //Select a random area to split
                    BSParea areaToSplit = null;
                    //Make sure no area is over max size
                    for (BSParea area : BSPareas) {
                            if (area.width>(xSize*absMax) || area.height>(ySize*absMax)) {
                                    areaToSplit = area;
                            }
                    }
                    //If no area is at max size, pick one at random
                    if (areaToSplit==null) {
                            areaToSplit = BSPareas.get(rnd.nextInt(BSPareas.size()));
                    }

                    //Split the area
                if(areaToSplit.splitArea(minSize, maxSize, absMin)) {
                    //Splitting succeeds
                    BSPareas.add(areaToSplit.leftChild);
                    BSPareas.add(areaToSplit.rightChild);
                    //Remove parent, so we dont make areas on top of another
                    BSPareas.remove(areaToSplit);
                }
                //TODO: Make sure this loop doesnt get stuck when we should make more but cant
                loops++;
                }
            } else {
                int totalSplits = 0;
                while (totalAreas>0) {
                        totalSplits++;
                        totalAreas=totalAreas/2;
                }
                if(Global.debug)Mists.logger.info("Doing "+totalSplits+" splits");

                for (int splits = 0; splits<totalSplits;splits++) {
                    ArrayList<BSParea> newAreas = new ArrayList<BSParea>();
                    for (BSParea area : BSPareas) {
                            if(area.splitArea(minSize, maxSize, absMin)) {
                            //Splitting succeeded, so adding childs to area
                            newAreas.add(area.leftChild);
                            newAreas.add(area.rightChild);
                    } else {
                            //Splits failed, add the entire area
                            newAreas.add(area);
                    }
                    }
                    BSPareas = newAreas;
                }		
            }		
            return BSPareas;
    }

    /**
     * DrawAreasOnMap takes in a list of BSPareas, and turns them into 
     * an int array.
     * @param mapWidth Width of the map to create
     * @param mapHeight Height ofthe map to create
     * @param arealist BSP areas to create map from
     * @param roomMinSize minimum room size to generate inside the BPS areas
     * @return int[][] that has the map
     */

    public static int[][] drawAreasOnMap(int mapWidth, int mapHeight, ArrayList<BSParea> arealist, int roomMinSize) {
            int[][] intMap = new int[mapWidth][mapHeight];

            //int areaID = 0; //ID is for painting area with the number for testing
            for (BSParea area : arealist) {
                //areaID++;
                area.generateRoom(roomMinSize);
                area.room.fillRoom(FLOOR); //Replace FLOOR with areaID for testing
                area.room.createSquareWalls();
                //Check if there already is a room in the area
                if (area.room==null) continue;
                //If there is a room, paint it on the big map
                for (int row = 0;row<area.room.height;row++){
                    for (int position = 0;position<area.room.width;position++) {
                            intMap[area.xPos+position+area.room.xOffset][area.yPos+row+area.room.yOffset]
                                            = area.room.roomMap[position][row];
                    }
                }	
            }
            return intMap;
    }

    public static int[][] generateCorridorsToAdjacentRooms (int[][] intMap, ArrayList<BSParea> arealist, float doorChance) {
            int mapWidth = intMap[0].length;
            int mapHeight = intMap.length;
            boolean makeDoor = false;

            //Create a corridor from each room
            Direction previousDirection = Direction.STAY;
            Direction direction = Direction.STAY;

            for (int i = 0; i<arealist.size();i++) {
                //See if area is next to another area
                //Go through all the areas but the last one (size-1) - last one needs no extra corridors
                BSParea currentArea = arealist.get(i);
                BSParea previousArea = arealist.get(0);
                if (i>1) previousArea=arealist.get(i-1);
                BSParea adjacentArea = arealist.get(0);
                if (i<arealist.size()-1) adjacentArea = arealist.get(i+1);
                if(debug)Mists.logger.info("Doing area "+i);
                //Check the direction of the adjacent area
                int areaXOffset = 
                                (adjacentArea.xPos+adjacentArea.room.xOffset+(adjacentArea.room.width/2))
                                -(currentArea.xPos+currentArea.room.xOffset+(currentArea.room.width/2));
                int areaYOffset = 
                                (adjacentArea.yPos+adjacentArea.room.yOffset+(adjacentArea.room.height/2))
                                -(currentArea.yPos+currentArea.room.yOffset+(currentArea.room.height/2));

                direction = getDirection(areaXOffset, areaYOffset);

                //Door is set at the center of the side of the room
                int doorwayX;
                int doorwayY;

                if (direction == Direction.RIGHT){
                    //Adjacent area is to the right
                    doorwayX =  currentArea.room.width-1;
                    doorwayY = currentArea.room.height/2;
                } else if (direction == Direction.LEFT){
                    //Adjacent area is to the left
                    doorwayX = 0;
                    doorwayY = currentArea.room.height/2;						
                } else if (direction == Direction.UP){
                    //Adjacent area is above us
                    doorwayX = currentArea.room.width/2;
                    doorwayY = 0;
                } else { 
                    //Adjacent area is below us
                    doorwayX = currentArea.room.width/2;
                    doorwayY = currentArea.room.height-1;
                }

                previousDirection = direction;

                //Add a door on the map
                int doorwayXPosOnMap=currentArea.xPos+currentArea.room.xOffset+doorwayX;
                int doorwayYPosOnMap=currentArea.yPos+currentArea.room.yOffset+doorwayY;
                intMap[doorwayXPosOnMap][doorwayYPosOnMap] = DOOR;
                if(debug)Mists.logger.info("Door added, drawing corridor...");

                //Select a target for the corridor
                int destinationX = adjacentArea.xPos+adjacentArea.room.xOffset+(adjacentArea.room.width/2);
                int destinationY = adjacentArea.yPos+adjacentArea.room.yOffset+(adjacentArea.room.height/2);
                if(debug)Mists.logger.info("Destination of the corridor: "+destinationX+", "+destinationY);

                //Start drawing the corridor
                int currentTile = 0;
                int corridorXPosOnMap=doorwayXPosOnMap;
                int corridorYPosOnMap=doorwayYPosOnMap;
                //Count the distance
                int distanceX = destinationX - doorwayXPosOnMap;
                int distanceY = destinationY - doorwayYPosOnMap;
                if(debug)Mists.logger.info("Distance: "+distanceX+", "+distanceY);
                boolean digging = true;
                int tilesDug = 0;
                while (digging && tilesDug <= (Math.abs(distanceX)+Math.abs(distanceY)+2)) {
                    if (rnd.nextFloat()<doorChance) makeDoor=true;
                    //Move corridor by one to the desired direction
                    //Check how much distance we have left to cover
                    distanceX = destinationX - corridorXPosOnMap;
                    distanceY = destinationY - corridorYPosOnMap;

                    if(direction==Direction.UP && corridorYPosOnMap>0) {
                        if (distanceY!=0) corridorYPosOnMap=corridorYPosOnMap-1;
                        else { //If distanceY is zero, we need to move on X (results in L turn)
                            if (distanceX>0) corridorXPosOnMap=corridorXPosOnMap+1;
                            if (distanceX<0) corridorXPosOnMap=corridorXPosOnMap-1;
                        }
                        //If corridor is above AND there is no wall below us, or makedoor is false, dont add a door
                        if (intMap[corridorXPosOnMap-1][corridorYPosOnMap]!=WALL || intMap[corridorXPosOnMap+1][corridorYPosOnMap]!=WALL
                                        || makeDoor!=true) makeDoor=false;
                    }
                    if(direction==Direction.RIGHT && corridorXPosOnMap<mapWidth) {
                        if (distanceX!=0) corridorXPosOnMap=corridorXPosOnMap+1;
                        else {
                            if (distanceY>0) corridorYPosOnMap=corridorYPosOnMap+1;
                            if (distanceY<0) corridorYPosOnMap=corridorYPosOnMap-1;
                        }
                        if (intMap[corridorXPosOnMap][corridorYPosOnMap+1]!=WALL || intMap[corridorXPosOnMap][corridorYPosOnMap-1]!=WALL
                                        || makeDoor!=true) makeDoor=false;
                    }
                    if(direction==Direction.DOWN && corridorYPosOnMap<mapHeight) {
                        if (distanceY!=0) corridorYPosOnMap=corridorYPosOnMap+1;
                        else {
                            if (distanceX>0) corridorXPosOnMap=corridorXPosOnMap+1;
                            if (distanceX<0) corridorXPosOnMap=corridorXPosOnMap-1;
                        }
                        if (intMap[corridorXPosOnMap-1][corridorYPosOnMap]!=WALL || intMap[corridorXPosOnMap+1][corridorYPosOnMap]!=WALL
                                        || makeDoor!=true) makeDoor=false;
                    }
                    if(direction==Direction.LEFT && corridorXPosOnMap>0) {
                        if (distanceX!=0) corridorXPosOnMap=corridorXPosOnMap-1;
                        else {
                            if (distanceY>0) corridorYPosOnMap=corridorYPosOnMap+1;
                            if (distanceY<0) corridorYPosOnMap=corridorYPosOnMap-1;
                        }
                        if (intMap[corridorXPosOnMap][corridorYPosOnMap+1]!=WALL || intMap[corridorXPosOnMap][corridorYPosOnMap-1]!=WALL
                                        || makeDoor!=true) makeDoor=false;
                    }

                    //Mists.logger.info("Corridoring at "+corridorXPosOnMap+", "+corridorYPosOnMap);
                    //Leave the loop if the corridor would lead out of the map
                    if (corridorXPosOnMap<1 || corridorYPosOnMap<1 || corridorXPosOnMap==mapWidth-1 || corridorYPosOnMap==mapHeight-1) {
                        if(debug)Mists.logger.info("Outside map area, interrupting corridor");
                        currentTile=-1;
                        digging=false;
                    } else {
                        currentTile = intMap[corridorXPosOnMap][corridorYPosOnMap];
                    }
                    //Draw corridor at the current tile
                    if (digging) {
                        //Draw walls at the sides if they're 0s
                        //(Done by drawing 3x3 on the corridor, where 0s become walls)
                        for (int nY=-1;nY<2;nY++) {
                            for (int nX=-1;nX<2;nX++) {
                                if (intMap[corridorXPosOnMap+nX][corridorYPosOnMap+nY]==CLEAR) {
                                        intMap[corridorXPosOnMap+nX][corridorYPosOnMap+nY]=WALL;
                                }
                            }
                            }

                            if (intMap[corridorXPosOnMap][corridorYPosOnMap]==WALL && makeDoor) {
                                //Make a door if we dug into a wall
                                //Make sure there's enough room around the door for moving
                                int openTiles=0;
                                for (int nY=-1;nY<2;nY++) {
                                    for (int nX=-1;nX<2;nX++) {
                                        if (intMap[corridorXPosOnMap+nX][corridorYPosOnMap+nY]==FLOOR) {
                                                openTiles++;
                                        }
                                    }
                                }
                                if (openTiles>2) {
                                    intMap[corridorXPosOnMap][corridorYPosOnMap]=DOOR;
                                } else {
                                    intMap[corridorXPosOnMap][corridorYPosOnMap]=FLOOR;
                                }
                            } else { //Tile is empty
                                intMap[corridorXPosOnMap][corridorYPosOnMap]=FLOOR;
                            }
                            //Mists.logger.info("Corridor drawn");



                    }
                    if (corridorXPosOnMap==destinationX&&corridorYPosOnMap==destinationY){
                        if(debug)Mists.logger.info("Reached the destination");
                        digging=false;
                    }
                tilesDug++;
                }
            }
           return intMap;
    }

    public static int[][] generateRandomCorridors (int[][] intMap, ArrayList<BSParea> arealist, float corridorChance) {
        int mapWidth = intMap[0].length;
        int mapHeight = intMap.length;


        //Make a corridor from each room
        for (BSParea area : arealist) {
            if (rnd.nextFloat()>corridorChance) continue;
            int direction = rnd.nextInt(3);
            int doorwayX;
            int doorwayY;
            //If we're going up or down
            if (direction==0||direction==2){
                //Doorway cant be at the very edge (there's wall there)
                doorwayX =  1+rnd.nextInt(area.room.width-2);
                if (direction==0) { //Door made at upper edge
                    doorwayY = 0;
                } else { //Otherwise its the lower edge
                    doorwayY = area.room.height-1;
                }				
            } else {
                doorwayY = 1+rnd.nextInt(area.room.height-2);
                if (direction==1) {	//Door to the right
                    doorwayX = area.room.width-1;
                } else { //Door to the left
                    doorwayX = 0;
                }			
            }
            //Place a door on the map
            int doorwayXPosOnMap=area.xPos+area.room.xOffset+doorwayX;
            int doorwayYPosOnMap=area.yPos+area.room.yOffset+doorwayY;
            intMap[doorwayXPosOnMap][doorwayYPosOnMap] = DOOR;
            if(debug)Mists.logger.info("Door added, drawing corridor...");
            //Start drawing the corridor
            int nextTile = 0;
            int corridorXPosOnMap=doorwayXPosOnMap;
            int corridorYPosOnMap=doorwayYPosOnMap;
            while (nextTile == 0) {
                if(debug)Mists.logger.info("Direction: "+direction);
                //Move the corridor by one towards the desired direction

                if(direction==0) corridorYPosOnMap=corridorYPosOnMap-1;
                if(direction==1) corridorXPosOnMap=corridorXPosOnMap+1;
                if(direction==2) corridorYPosOnMap=corridorYPosOnMap+1;
                if(direction==3) corridorXPosOnMap=corridorXPosOnMap-1;

                if(debug)Mists.logger.info("Corridoring at "+corridorXPosOnMap+", "+corridorYPosOnMap);
                //Exit loop if we've reached the map edges
                if (corridorXPosOnMap<1 || corridorYPosOnMap<1 || corridorXPosOnMap==mapWidth-1 || corridorYPosOnMap==mapHeight-1) {
                        if(debug)Mists.logger.info("Out of map, interrupt the corridor");
                        nextTile=-1;
                } else {
                        nextTile = intMap[corridorXPosOnMap][corridorYPosOnMap];
                        if(debug)Mists.logger.info("In the tile "+nextTile);
                }
                //If tile is clear, draw corridor on it
                if (nextTile==0) {
                        intMap[corridorXPosOnMap][corridorYPosOnMap]=FLOOR;
                        if(debug)Mists.logger.info("Drawing corridor");
                        //Draw walls along the corridor
                        if (direction==0 || direction==2) {
                            //If we're going up or down...
                            intMap[corridorXPosOnMap+1][corridorYPosOnMap]=WALL;
                            intMap[corridorXPosOnMap-1][corridorYPosOnMap]=WALL;
                        }

                        if (direction==1 || direction==3) {
                            //Left or right...
                            intMap[corridorXPosOnMap][corridorYPosOnMap+1]=WALL;
                            intMap[corridorXPosOnMap][corridorYPosOnMap-1]=WALL;
                        }
                }
                //If the corridor hit a wall, make a door there (if there's space behind the wall)
                if (nextTile==11) {
                    doorwayXPosOnMap=corridorXPosOnMap;
                    doorwayYPosOnMap=corridorYPosOnMap;
                    if(direction==0) corridorYPosOnMap=corridorYPosOnMap-1;
                    if(direction==1) corridorXPosOnMap=corridorXPosOnMap+1;
                    if(direction==2) corridorYPosOnMap=corridorYPosOnMap+1;
                    if(direction==3) corridorXPosOnMap=corridorXPosOnMap-1;
                    nextTile = intMap[corridorXPosOnMap][corridorYPosOnMap];
                    if (nextTile==88) {
                            intMap[doorwayXPosOnMap][doorwayYPosOnMap]=DOOR;
                    }
                }
            }
        }
        return intMap;
    }

    /**
     * Doorwaycleaner takes in a map and removes redundant doors from it.
     * Extra doors might be generated when corridors overlap or when they
     * hit just between areas. There's also no point in having doors on
     * the edge of the map
     * @param intMap the map to clean
     * @return cleaned version of the map
     */
    public static int[][] doorwayCleaner (int[][] intMap) {
            int mapWidth = intMap.length;
            int mapHeight = intMap[0].length;
            //Go through the tiles, and remove doors that dont have floor between them
            int[][] cleanMap = new int[mapWidth][mapHeight];
            if(Global.debug)Mists.logger.info("Cleaning doorways from the "+intMap.length+"x"+intMap[0].length +" map");
            for (int yPos=0;yPos<mapHeight-1;yPos++) {
                for (int xPos=0;xPos<mapWidth-1;xPos++) {
                    //If there's a door...
                    if (intMap[xPos][yPos]==DOOR) {
                        //And it's at the edge of the map, make it a wall
                        if (xPos==0||xPos==mapWidth-1||yPos==0||yPos==mapHeight-1) {
                                intMap[xPos][yPos]=WALL;
                        } else { 
                            //Make sure there's a door in a doorway
                            //if there's wall to the Left AND Right of it
                            if (intMap[xPos-1][yPos]==WALL && intMap[xPos+1][yPos]==WALL) {
                                if(cleanMap[xPos][yPos-1]==DOOR || cleanMap[xPos][yPos+1]==DOOR) {
                                    //Dont put a door here if there's already a door next to this
                                    cleanMap[xPos][yPos] = FLOOR;
                                } else {
                                    cleanMap[xPos][yPos] = intMap[xPos][yPos];
                                }
                            //Or if there's wall to Above it AND Below it
                            } else if (intMap[xPos][yPos-1]==WALL && intMap[xPos][yPos+1]==WALL) {
                                if(cleanMap[xPos-1][yPos]==DOOR || cleanMap[xPos+1][yPos]==DOOR) {
                                    //Dont put a door here if there's already a door next to this
                                    cleanMap[xPos][yPos] = FLOOR;
                                } else {
                                    cleanMap[xPos][yPos] = intMap[xPos][yPos];
                                }
                            } else {
                                    //If the door is unneeded, replace it with corridor
                                    cleanMap[xPos][yPos] = FLOOR;
                            }
                            //Clean doors that are next to other doors

                        }
                    } else {
                        //If there's no door, use whatever was there originally
                        cleanMap[xPos][yPos] = intMap[xPos][yPos];
                    }	
                }
            }


            return cleanMap;
    }

    /**
     * MapToStrinp converts a map into easily printable format,
     * either for testing or saving into a file.
     * @param intMap the map to print
     * @param xSize width of the map
     * @param ySize height of the map
     * @return String describing the map
     */

    public static String mapToString(int[][] intMap, int xSize, int ySize) {
        Mists.logger.info("Generating a String printout for the "+xSize+","+ySize+" map. IntMap is the size of " +intMap.length+"x"+intMap[0].length);
        String mapString = "";
            for (int y = 0; y<ySize;y++) {
                    for (int x=0;x<xSize;x++) {
                            if (intMap[x][y]<10) {
                                    mapString = mapString + ("0"+intMap[x][y]);	
                            } else {
                                    mapString = mapString + (intMap[x][y]);	
                            }
                    }
                    mapString = mapString + "\n";
            }
            return mapString;
    }


    /**
     * BSPareas are areas inside the map that house the rooms.
     * Each area has a parent and (up to) two children.
     * BSP maps are generated by splitting these areas further and
     * further, until they're of the desired side. After
     * that the rooms can be generated into them.
     */
    public class BSParea {
        //http://roguebasin.roguelikedevelopment.org/index.php?title=Basic_BSP_Dungeon_generation
        public int width, height;
        public int xPos, yPos;
        public BSParea leftChild, rightChild;
        public BSParea parent;
        public Room room; //If this area has a room


        public BSParea (int xPos, int yPos, int width, int height, BSParea parent) {
                this.xPos = xPos;
                this.yPos = yPos;
                this.width = width;
                this.height = height;
        }

        /**
         * Split this area into two children
         * @param minSize Minimum size of a child (% of the area size)
         * @param maxSize Maximum size of a child (% of the area size)
         * @param absMin Absolute minimum size off a child, in tiles
         * @return Return true if the area was split succesfully
         */
        public boolean splitArea(float minSize, float maxSize, int absMin) {
                if (leftChild!=null || rightChild!=null) {
                        //Dont split, because it has been split already
                        return false;
                }
                //Figure out which way to split
                boolean splitHorizontal = rnd.nextBoolean();
                //Figure the largest splittable child
                int maxAreaSize = (splitHorizontal ? height : width) - absMin;
                if (maxAreaSize < absMin) {
                        //If the largest area would be smaller than the minimum...
                        return false;
                }
                //Make the split and ensure it falls between max and minSize
                int splitPosition = rnd.nextInt((int)((splitHorizontal ? height : width)*maxSize));
                if(debug)Mists.logger.info("Splitting at position "+splitPosition);
                if (splitPosition < (splitHorizontal ? height : width)*minSize) splitPosition = (int)((splitHorizontal ? height : width)*minSize);
                //Create the sub-areas
                if(splitHorizontal) {
                    //Above area
                    leftChild = new BSParea(xPos, yPos, width, splitPosition, this);
                    //Below area
                    rightChild = new BSParea(xPos, yPos+splitPosition, width, height-splitPosition, this);
                } else {
                    leftChild = new BSParea(xPos, yPos, splitPosition, height, this);
                    rightChild = new BSParea(xPos+splitPosition, yPos, width-splitPosition, height, this);
                }
            return true; //split successful
            }
        
        /**
         * Generate a room inside the area
         * @param minSize Minimum size of the room, in tiles
         */
        public void generateRoom(int minSize) {
            //Give the room a random size
            float roomSize = rnd.nextFloat();
            //Room size should never be less than 80% of the area
            if (roomSize<0.8f) roomSize=0.8f;
            if (width<=minSize|height<=minSize) roomSize=1.0f;

            int roomWidth = (int)(width*roomSize);
            int roomHeight = (int)(height*roomSize);
            int xOffsetMax = (int) (this.width-(this.width*roomSize));
            int yOffsetMax = (int) (this.height-(this.height*roomSize));
            //System.out.println("OffsetMax: "+xOffsetMax+", "+yOffsetMax);
            int xOffset = 0;
            int yOffset = 0;
            if (xOffsetMax>0) {
                    xOffset = rnd.nextInt(xOffsetMax);
            }
            if (yOffsetMax>0) {
                    yOffset = rnd.nextInt(yOffsetMax);
            }

            this.room = new Room(roomWidth,roomHeight, xOffset, yOffset);

        }
    }

    /**
    * Dungeons are composed of rooms.
    * Making variable rooms should be done by
    * extending this base Room class.
    */
    private class Room {
        int width, height;
        int xOffset, yOffset; //Offsets control wherein the area the room is located
        int[][] roomMap; //contents of the room

        public Room (int width, int height, int xOffset, int yOffset) {
                this.width = width;
                this.height = height;
                this.xOffset = xOffset;
                this.yOffset = yOffset;
                this.roomMap = new int[width][height];
        }

        private void clearRoom() {
                //Paint the room with zero
                for (int y=0;y < height;y++) {
                        for (int x=0;x<width;x++) {
                                roomMap[x][y] = FLOOR;
                        }
                }
        }

        /**
         * Fill the room with tilenumber.
         * Note that this does not fill the AREA the room is in, but the actual room.
         * @param number tilenumber to fill the room with
         */
        private void fillRoom(int number) {
            //Paint the room with the given number
            for (int y=0;y < height;y++) {
                for (int x=0;x<width;x++) {
                        roomMap[x][y] = number;
                }
            }
        }

        /**
         * Make simple square walls around the room
         * (inside the room area)
         */
        private void createSquareWalls() {
            for (int y=0;y < height;y++) {
                for (int x=0;x<width;x++) {
                    //If we're on the highest or the lowest row
                    //or at a side...
                    if (y==0 || y==height-1 || x==0 || x==width-1) {
                            roomMap[x][y] = WALL;	
                    }
                }
            }
        }

    }

    /**
     * Return a Direction(enum) towards target, given the X and Y
     * distance. Used in pathing corridors
     * @param xDist Distance towards the target
     * @param yDist Distance towards the target
     * @return Direction towards the target
     */
    public static Direction getDirection (int xDist, int yDist) {
        Direction direction = Direction.STAY;
        int xDistAbs;
        int yDistAbs;
        if (xDist>=0) {
                xDistAbs = xDist;
        } else {
                xDistAbs = -xDist;
        }
        if (yDist>=0) {
                yDistAbs = yDist;
        } else {
                yDistAbs = -yDist;
        }

        //Up
        if (yDist<=0 && yDistAbs >= xDistAbs) {
                direction = Direction.UP;
        }

        //Right
        if (xDist>0 && xDistAbs >= yDistAbs) {
                direction = Direction.RIGHT;
        }

        //Down
        if (yDist>=0 && yDistAbs >= xDistAbs) {
                direction = Direction.DOWN;
        }

        //Left
        if (xDist<0 && xDistAbs >= yDistAbs) {
                direction = Direction.LEFT;
        }

        return direction;
    }

}

