**Overview:** Mists is a realtime roguelike game with adventure elements.
Unlike in most roguelikes, the player character in Mists is accompanied by a helpful creature. Together these two travel throughout various procedurally generated locales, solving puzzles and vanquishing foes.

**Keywords:** Graphic interface, helpful companion, procedurally generated maps

**Users:** Mists is a ~~single~~multiplayer game. On the broad spectrum, the player should be able to:
* Start a new game
* Join existing games
* Play the game
 (Move around)
 (Attack enemies)
 (Toggle objects)
* Save a game to pause progress
* Resume a saved game

##Playing the game
So far only the Location -level of the game has been developed.
Further down the road, there should be a world map to travel from a location to another.

***Location controls (POC)***

Controls are displayed in game menu (Esc in a Location)
* Open game menu: Escape
* Open console: F1
* Moving around: Arrow keys / WASD
* Opening doors: E
* Selecting different door to open: R
* Using ability (Melee attack): Space
* Activate/Deactivate creatures: Shift
* Teleport to a location: Right mousebutton
* Target a map object: Left mousebutton


***Some useful console commands (POC)***
* "createLoc testmap" //Creates and moves the game to testmap
* "addCreature" //Creates a random small critter
* "addCreature blob" //creates a large blob
* "toggleFlag <flag>" //toggles a flag on the location ("testFlag" and "drawPaths" being good examples)
* "clearanceMap" //Print clearance map in console
* "collisionMap" //Print collision map in console


#Program structure
The game is built loosely on MVC principles, where everything the user sees and does is passed
through a controller. Various gamestates govern the main areas of the game, and the principle is that a new gamestate is only added when desired gameplay differs wildly from what
existing gamestates can provide.
The accompanied [UML Class diagram](https://github.com/nkoiv/mists/blob/master/documentation/mists_classchart.jpg) is good reference for this.

##Main loop

Game main loop is done by the [Mists.java](https://github.com/nkoiv/mists/blob/master/mists-game/src/main/java/com/nkoiv/mists/game/Mists.java). The loop is separate from the actual game, so that its easier to port to different platforms. Loop calls Tick() and Render() functions, asking the game to update whats happenig (tick) and show it the the player(render). As describe above, the game itself is divided into GameStates that answer to these tick() and render() -calls. In practice this means that the game uses "gamestates" to relay commands into the game while also calling for renders back into the main stage.

The main loop can be summed rougly into the following:
1. (Optional) User does input via keyboard/mouse 
2. Game relays the input to the current GameState
3. GameState parses the input and does things (a "tick" in the code)
4. GameState renders all its components (UI, map, creatures...) and passes it back to the game
5. Game updates view for player
6. Back to 1.

Current list gamestates, ticking and rendering:
* GameState for MainMenu
Selfexplanatory, displayed at launch.
* GameState for Locations 
Contains the bulk of the gameplay. These are top down areas where the player can move around, exploring and combating adversaries.
* GameState for WorldMap
WorldMap is used for traveling between Locations, but it's a lot more limited as far
as action is considered.
* GameState for Town
Towns are mainly composed of menus (taverns, shops, etc). MainMenu is selfexplanatory.

###Loading screens

While not a GameState itself, LoadingScreen sort of behaves like one. Called with a javafx.concurrent.Task, LoadingScreen is rendered instead of the normal GameState graphics whenever considerable background work is done. LoadingScreen should be instantiated and discarded on per-case basis. 


##MainMenu
From the main menu a player can either start a new game, load an existing one,
edit game options, or close the game.

##Location
Locations house the bulk of the adventure. They're built on a map, have structures blocking players path, and contain various monsters and puzzles to face. The maps come in two main variations: BGMaps and TileMaps. The former are based on a single image (hence the "BG", background), wheras the latter (TileMaps) are built from small tiles. TileMaps can also be randomly generated via the MapGenerator-class.

On top of the maps we have MapObjects. Anything that can block, hurt or affect the player or other creatures in any way is a MapObject. They can be either Creatures, Structures or Effects. Creatures have behaviours via AIs, Structures tend to be more static. Effects are temporary by definition, though their duration could obviously be infinite.

Doing things (Actions) in the game is generally done with the aid of Effects. An effect is generated when an ability is used, and the targets of the ability are picked based on what the effect manages to intersect. An arrow lands on the first target it hits, etc.

###MapObjects and Sprites

![](https://github.com/nkoiv/mists/blob/master/documentation/mapobjects.png "MapObjects and their relation to Sprites")

MapObjects, or MOBs for short, are a myriad bunch. However when it comes to representing them on the screen, they all default to Sprites. A MapObject itself has no position or appearance, as that's all for the Sprite to handle. If a mapobject has no sprite, it can't be seen or directly interacted with. All movement and render calls pass through the MOB to its Sprite.

###Tasks and Actions

Tasks are the main thing dictating how Creatures (themselves a type of MapObject) move and act. When it is a creatures turn to act (called in generally by the Location.tick()), it consumes the next task it has, acting accordingly. Generally the PlayerCharacter has it's NextTask set by keyboard and mouse input, whereas computer controlled Creatures get their Tasks from their AI routine. The reason Creatures are given Tasks rather than allowed to act directly, is because Tasks are easy to serialize and transport over network in a multiplayer game. In general most creatures have access to most tasks, and the list of available tasks is maintained in ([GenericTasks.java](https://github.com/nkoiv/mists/blob/master/mists-game/src/main/java/com/nkoiv/mists/game/actions/GenericTasks.java)).

A list of some of the triggers, as example:
<pre>
ID_IDLE = 0; //no arguments
ID_CONTINUE_MOVEMENT = 1; //no arguments
ID_MOVE_TOWARDS_DIRECTION = 2; //1 argument: the direction
ID_MOVE_TOWARDS_TARGET = 3; // 1 argument: targetID
ID_MOVE_TOWARDS_COORDINATES = 4; //2 arguments: x and y coordinates
ID_CHECK_COORDINATES = 8; //2 arguments: x and y coordinates
ID_STOP_MOVEMENT = 9; //no arguments
ID_TURN_TOWARDS_MOB = 11; //1 argument: MobID
ID_USE_MELEE_TOWARDS_MOB = 21; // 1 argument: MobID
ID_USE_MELEE_TOWARDS_COORDINATES = 22; // 2 argument: Mob X and Y
ID_USE_MELEE_TOWARDS_DIRECTION = 23; // 1 argument: direction number
ID_DROP_ITEM = 31; //1 argument: inventoryslotID of the actor dropping the item
ID_TAKE_ITEM = 32; //2 arguments: inventoryholder ID and inventoryslotID
ID_EQUIP_ITEM = 33; //1 arguments: inventoryslotID
ID_USE_ITEM = 34; //1 arguments: inventoryslotID
ID_USE_TRIGGER = 41; //1 argument: id of the mapobject to toggle
</pre>


Actions are more complicated and generally more focused than tasks. They're also instantiable and thus customizable per owner. While Tasks govern basic things such as moving and handling items, Actions contain various combat manouvres, spells and the sort. Available Actions per Creature are stored with the creature, along with their cooldowns, resource costs etc. Using an Action is a Task itself.

###Pathfinding

PathFinder.java is the class governing the general pathfinding.
The Constructor takes in a [CollisionMap] (https://github.com/nkoiv/mists/blob/master/mists-game/src/main/java/com/nkoiv/mists/game/world/pathfinding/CollisionMap.java), an integer specifying the MaxSearchDistance for paths and a boolean dictating whether or not diagonal movement is allowed when searching paths. Since pathfinder ties itself to a collisionmap (the map it routes paths on), it's effectively hardlinked to a location. While a pathfinder could exist without a location, there would be no point in it.

![](https://github.com/nkoiv/mists/blob/master/documentation/pathfinding_chart.png "Pathfinding in the project structure")

####Collisionmaps
Accessed and updated via the location the pathfinder is tied to, the collisionmap is a 2d grid of nodes ([Node.java](https://github.com/nkoiv/mists/blob/master/mists-game/src/main/java/com/nkoiv/mists/game/world/pathfinding/Node.java)). What CollisionMap does is that it takes all the map objects (mobs) from its location and converts them to simple collision values. Effectively any node a mob touches gets the collision value of the mob. This map is updated every time Location ticks (as mobs can move), and it's done by updateCollisionLevels(). The update completes in O(n), n being the number of mobs on a map.

![](https://github.com/nkoiv/mists/blob/master/documentation/collisiongrid.png "Collision grid derived from objects")
<pre>
[ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ]
[ ][9][9][ ][ ][ ]
[ ][9][9][ ][ ][ ]
[ ][9][9][ ][ ][ ]
[ ][ ][ ][ ][7][7]
[ ][ ][ ][ ][ ][ ]
[ ][ ][ ][ ][ ][ ]
</pre>

####Movement cost
Movement costs are used when calculating the value of the path. They're stored per node, in an 10-spot array of "cost types". By default the array is empty, meaning that all all movement types cost default multiplier of 1 when crossing the node.
If a mover has movement speedups in some cost types, the Nodes can be supplied with the list of those types (movement capabilities, boolean[] with True for each ability the mob has), and the node returns the "fastest" way the creature can move through it.

When estimating a cost from A to B, before actually checking the nodes in between, the PathFinder has the help of a [MoveCostCalculator](https://github.com/nkoiv/mists/blob/master/mists-game/src/main/java/com/nkoiv/mists/game/world/pathfinding/MoveCostCalculator.java). Every PathFinder has one of its own, so that it consistently uses the set cost type (Manhattan, Diagonal or Euclidean.)

* Manhattan: Sum up X and Y diffence from Start to Goal to get the cost
* Diagonal: Diagonal movement is allowed, so cost is Math.max(X difference, Y difference)
* Euclidean: Moving diagonally is calculated with ((AC = sqrt(AB^2 + BC^2))), where AB = x2 - x1 and BC = y2 - y1 and AC will be [x3, y3]. In practice this means that diagonal movement costs ~1.41 times the vertical movement.

Currently Locations default to allowDiagonal = false, so the MoveCostCalculator is also set on Manhattan by default.

####Clearance map

Clearance Maps are used for seeing what size creatures can pass through which openings. If walls surround a doorway, only the creatures that can fit in through the door should path through it - otherwise they'll get stuck when actually trying to navigate the given path.

Assume the following map (X means wall, G is goal, S is Start and .'s are path)
<pre>
[ ][ ][G][.][ ][ ][X][ ]
[ ][ ][ ][.][ ][ ][X][ ]
[ ][ ][X][.][X][X][X][ ]
[ ][ ][X][.][ ][ ][ ][ ]
[ ][ ][ ][.][.][S][ ][ ]
[ ][ ][ ][ ][ ][ ][ ][ ]
</pre>

If the creature going from S to G is the size of a node or smaller. everything is fine. However if the mob is larger than a node, it should circle around the small doorway, and take the following path:
<pre>
[.][.][G][G][ ][ ][X][ ]
[.][ ][G][G][ ][ ][X][ ]
[.][ ][X][ ][X][X][X][ ]
[.][ ][X][ ][ ][ ][ ][ ]
[.][.][.][.][.][S][S][ ]
[ ][ ][ ][ ][ ][S][S][ ]
</pre>

This is implemented by ClearanceMaps, calculated from the Collision Map. In effect "Clearance" means "how many free slots are there below and beside this one". If a creature of size N moves in the node so that it's Top Left corner is at the node, it should fit - assuming it's the Clearance Size or smaller. Clearance map for the area above would be:
<pre>
[2][2][2][2][2][1][X][1]
[2][2][2][2][2][1][X][1]
[2][1][X][1][X][X][X][1]
[2][1][X][3][3][3][2][1]
[2][2][2][2][2][2][2][1]
[2][2][2][2][2][2][2][1]
</pre>

Because ClearanceMap is generated by moving down from the top left corner and updating everything behind you as you encounter empty tiles, it's relatively slow to run. In the worst case scenario (empty map), there's 1+2+3...+N or  updates on a row, where N is the width of the row.
Due to this, Clearance is updated only when requested. In practice this works so that the Location the pathfinder "mapOutOfDate(true)" whenever a *structure* (not creature, they're not included in clearance maps at the moment) is added or destroyed. If the pathfinder sees this flag is up, it calls for a refresh of the ClearanceMap.

###Pathfinding Algorithms

The interface "PathfinderAlgorithm" lets the pathfinder work with various algorithms. The main focus is current on the A* ([AStarPathfinder.java](https://github.com/nkoiv/mists/blob/master/mists-game/src/main/java/com/nkoiv/mists/game/world/pathfinding/AStarPathfinder.java)), but for example BellmanFord might be worth implementing for maps with lots of low-intelligence creatures.

#####A* Pathfinding

The current A* works very much based on the same principles as outlayed in the [wikipedia article](https://en.wikipedia.org/wiki/A*_search_algorithm):
<pre>
function A*(start,goal)
    ClosedSet := {}    	  // The set of nodes already evaluated.
    OpenSet := {start}    // The set of tentative nodes to be evaluated, initially containing the start node
    Came_From := the empty map    // The map of navigated nodes.
 
    g_score := map with default value of Infinity
    g_score[start] := 0    // Cost from start along best known path.
    // Estimated total cost from start to goal through y.
    f_score := map with default value of Infinity
    f_score[start] := g_score[start] + heuristic_cost_estimate(start, goal)
     
    while OpenSet is not empty
        current := the node in OpenSet having the lowest f_score[] value
        if current = goal
            return reconstruct_path(Came_From, goal)
         
        OpenSet.Remove(current)
        ClosedSet.Add(current)
        for each neighbor of current
            if neighbor in ClosedSet	
                continue		// Ignore the neighbor which is already evaluated.
            tentative_g_score := g_score[current] + dist_between(current,neighbor) // length of this path.
            if neighbor not in OpenSet	// Discover a new node
                OpenSet.Add(neighbor)
            else if tentative_g_score >= g_score[neighbor] 
                continue		// This is not a better path.

            // This path is the best until now. Record it!
            Came_From[neighbor] := current
            g_score[neighbor] := tentative_g_score
            f_score[neighbor] := g_score[neighbor] + heuristic_cost_estimate(neighbor, goal)

    return failure

function reconstruct_path(Came_From,current)
    total_path := [current]
    while current in Came_From.Keys:
        current := Came_From[current]
        total_path.append(current)
    return total_path
</pre>

The nodes are handled with the previously explained [Node.java](https://github.com/nkoiv/mists/blob/master/mists-game/src/main/java/com/nkoiv/mists/game/world/pathfinding/Node.java). The Open and Closed Sets are done with a [Minheap](https://github.com/nkoiv/mists/blob/master/mists-game/src/main/java/com/nkoiv/mists/game/world/util/MinHeap.java) to minimize the load on sorting the sets. Both the Open and Closed nodes are also represented in a two dimensional array of CLEAR, OPEN and CLOSED values, making it fast and easy to check the status of a node. This saves us from doing costly search operations to the MinHeap.

Most of the deviation from basic A* happen during the searching of Neighbours. This is done with the findNeighbours (and possibly findDiagonalNeighbours, if that's enabled), where the pathfinder also checks the encircling nodes for Clearance and Collision levels.

A neighbouring node is added to the list of current neighbours if and only if:
* The node is within map dimensions (no moving outside the map)
* The node is returns FALSE from isBlocked(movementAbilityOfTheMover, node coordinates)
* The moving object hasClearance (size of the mover, movement ability of the mover, target node) on the node.

Once the node gets on the list of possible neighbours, it can be ranked and considered as per normal. A path is constructed and returned at the end, though it's somewhat redundant considering the nodes themselves have the capability to act as a linked list (getNextNode()).

##WorldMap

WorldMap is the part of the game where player(s) moves between locations. In essence this means that worldmap is a network of mapnodes, mosts containing a transition to the location it represents. Some nodes can also serve other purposes, such as simply provide an empty space to move through. The actual map behind the nodes is purely graphical and serves no purpose beyond aesthetics.

WorldMap, as opposed to the Location, is a turn-based affair. Things happen on worldmap after player presses a button, not before.

A small worldmap might look something like this:
<pre>
[CASTLE]----[MOUNTAINS]---[LAIR]
 |
[ ]--[TOWN]
 |
[HARBOR]--[SHIP]
</pre>

Each mapnode contains a list of adjacent nodes, which governs the possible movement routes. A node cannot be hopped over with normal movement, and each node is entered and exited individually. These enter and exit -functions provide an opportunity for turn-based events. The adjacent nodes are stored in an array which also denotes the direction they are from the given node. This is both to allow movement via keyboard commands and to make it possible to do non-euclidean maps. The X and Y position the node has on the map are in no way related to the "direction" it is from other nodes.
	

##Town
TODO, probably cut out

##Combat

![](https://github.com/nkoiv/mists/blob/master/documentation/combat_action.png "Actions in combat")

Combat happens by invoking (combat)actions. Creatures use these actions to do combat with oneanother. Triggering an action generally spawns an effect on the map. This effect then passes the actions trigger on whatever it touches. This chain of effects is modeled in the [actions and effects sequence diagram](https://github.com/nkoiv/mists/blob/master/documentation/sequence_diagrams/actions_and_effects.jpg).
Everything involved in the combat should implement the "Combant" interface. As combat Actions only affect classes implementing the Combatant, this ensures that everything in the combat is capable of dealing with damage, death, etc.

###Combat mechanics
TODO: Plan and implement mechanics for how damage is calculated. Is there armour? Can mobs dodge/parry attacks?

##Asset Libraries
Libraries are used to store templates of game assets. With the exception of Graphics Library (which just houses images in various formats), each library stores a number of asset templates in a HashMap, allowing the program to create additional copies of those on demand. The methods "addTemplate(object)", "getTemplate(id)" and "create(id)" are the bread and butter of asset libraries.

While templates can be added and modified on the fly (via the addTemplate() and getTemplate()), most of them are loaded from an external file. These files are mainly stored in YAML-format, for human readability and easy editing. Esoteric Softwares [YamlBeans](https://github.com/EsotericSoftware/yamlbeans) (full licence at the LICENCE.md) is used to facilitate the saving and loading of the assets. These files are mainly stored in /resources/libdata and /resources/mapdata, but additional assets should be loadable from an external folder (as with the music and sound effects) for modding purposes (TODO).

###Graphics Library
Graphics Library is an exception as far as game libraries are concerned. While individual map objects have their sprite images stored within the template-objects, generic graphics such as UI elements, lightmaps, etc are kept in a separate library.

Graphics Library does not currently utilize YAML for loading its content. It's built entirely by the LibLoader class upon launching the game.

###MapObject Library
MapObjects are handled by MobLibrary and the classes that extend it. Due to similiarities between structures, creatures, and effects, they can be mostly handled with the same methods. This does require the YAML objects to store the object type however, as that's how various types of mobs can be differentiated.

In YAML, all MapObjects have the folowing fields:
<pre>
name: "name of the mob" #ie. "Goblin" or "DungeonWall"
type: "MapObject type of the mob" #ie. "GenericStructure", "Wall" or "Creature"
</pre>

####Creature Library

Example creature from creatures.yml:
<pre>
---
#"monsterID" is mandatory and unique identifier
monsterID: 1
#"name" is mandatory, but doesn't need to be unique (though probably it should be)
name: "Worm"
#"type" is mandatory and used to differentiate various MapObject types.
type: "Creature"
#"aiType" can be left blank, resulting in a creature that doesnt move
aiType: "monster"
#"spriteType" can be either "spritesheet" or "static". 
#Spritesheets have four rows: Down, Left, Right and Up.
#Frames per direction may vary.
spriteType: "spritesheet"
spritesheet: "/images/monster_small.png"
spritesheetParameters: [3, 0, 0, 4, 0, 36, 32]
#Unless attributes are specified, they default to 1.
attributes:
 Strength: 1
 Dexterity: 2
 Intelligence: 1
 Speed: 50
 MaxHealth: 120
#Default flags for creatures are:
#visible: 1
#collisionLevel: 5
flags:
 collisionLevel: 5
---
</pre>

####Structure Library

Example strucutre from structures.yml
<pre>
---
name: "Tree"
type: "GenericStructure"
image: "/images/tree_stump.png"
collisionLevel: 1
extras:
 tree:
  image: "/images/tree.png"
  xOffset: -35
  yOffset: -96
---
</pre>

###Dialogue Library
Example of dialogue from dialogueTest.yml
<pre>
---
#"dialogueID" is both mandatory and unique. Duplicate dialogueIDs are overwritten.
dialogueID: 1
dialogueName: "Chat with Himmu"
#"cards" is mandatory. Without cards, Dialogue would be empty
cards:
 #"id" is a mandatory field, used to interlink the cards in dialogue
 - id: 1
   #"text" is a mandatory field - it's what's displayed on the card
   text: "Hello, TALKER_NAME, my name is OWNER_NAME"
   #links are stored in a set
   #if no "links" are supplied, a static "end conversation" link is added
   links:
    - linkText: "Hi OWNER_NAME! Do you like hamburgers?"
      linkDestination: 2
    - linkText: "OWNER_NAME? What a weird name! Where are we?"   
      linkDestination: 3
 - id: 2
   text: "I love hamburgers! Should we go look for some?"
   links:
    - linkText: "Did I say hamburgers? Let me rephrase myself."
      linkDestination: 1
    - linkText: "Sounds awesome, but where would that lead us?"
      linkDestination: 3
 - id: 3
   text: "Well TALKER_NAME, we're currently at the LOCATION_NAME. I guess we could head onwards and try to find a dungeon entrance?"
   links:
    - linkText: "Let's talk a little bit more first..."
      linkDestination: 1
    - linkText: "Sure, lets go! [End of Dialogue]"
      linkDestination: -1
</pre>
###Action Library

###Item Library

###Location Library

Example of a StructCodes YAML:
<pre>
---
symbol: "#"
structure: "DungeonWall"
---
symbol: "+"
structure: "DungeonDoor"
---
symbol: "^"
structure: "DungeonStairs"
---
symbol: "T"
structure: "Tree1"
</pre>

###WorldMap Library


##Dialogue
![](https://github.com/nkoiv/mists/blob/master/documentation/dialogue_ingame.png "Conversing via dialogue")
The dialogue system in the game is handled with dialogue maps, composed of Cards and Links.
Each individual piece of conversation is handled by presenting the user with (A) dialogue text and (B) a number of choices with which to navigate the dialogue.

<pre>
+---------+
| TALKER  |
+---------------------------------------+
| (A) Hello PLAYER, it is nice to meet  |
|     you! Would you like to learn the  |
|     game basics?                      |
|                                       |
| (B) 1: Please tell me more            |
| (B) 2: Goodbye! [END CONVERSATION]    |
+---------------------------------------+
</pre>

Cards are stored in a map in the dialogue. Each card has an ID, which is referred to when a player selects an option to move forward in the dialogue. The choices are loaded from a list of Links that reside in with the card. Each link contains not only the next card to move to, but also the requirements for the player to choose that link and the effects of choosing it.

Variables in text are handled by a set list of conversion rules, all of which reside in the LocalizableText.java. Since a fresh localization of the text is called every time a participant in the dialogue changes, it's possible to reuse the same dialogue in different environments.

The dialogues are stored in YAML, loaded with the libloader class.

##Multiplayer
Multiplayer is done by client-server model, where one player hosts the game and others join in on it. The player hosting the game handles all the AI-routines and random elements, and relays the deterministic results to the connected players. The actual networking is handled by KryoNet (https://github.com/EsotericSoftware/kryonet), for which the licence information is included in the LICENCE.txt

Players (including the Server-player) can assign Tasks to their character (fex. "Move right" or "Toggle object with ID 10001"), which the server the validates, mimics and performs. To reduce input latency, the tasks players do are executed before validation, and the rollbacked if the server refuses to perform them. In practice this might result in some "ghosting" or "rubberbanding" if for example player on a slow connection thinks he can move through a doorway that just got blocked by another creature.

All the sent objects are registered for Kryo inside the LocationNetwork.java. On rough level they're mainly split between various game objects (which the server sends to client when it spawns or updates one) and actions, mostly composed of Task-class objects. Identifying map objects and tasks is done with LocationID identifier, which is also the key for handling Location specific objects in general (location.getMapObject(int locationID), etc).


##Testing
Testing the game is done from two directions: Unit tests inside the Maven project, performing GUI testing by playing the game.
* Unit tests are enhanced by PIT mutation, documentation for which can be found under the [mists/documentation/pit-reports/ -folder](https://github.com/nkoiv/mists/tree/master/documentation/pit-reports).
* GUI testing is documented in [/mists/documentation/game_testing.md](https://github.com/nkoiv/mists/blob/master/documentation/game_testing.md)

##Original project plan for the school project:

###Weekly plan (TiraLab)
* Week 1: Plan the tiralab angle to the project (Pathfinding)
* Week 2: Modify the UI to better suit pathfinding optimization and testing
* Week 3: Clean the original pathfinding to use no Collections and optimize the lists
* Week 4: Create a minHeap to use with the nodes
* Week 5: Unit tests and PIT
* Week 6: Prepare the project for presentation

###Weekly plan (JavaLab)
* Week 1: Create a window with sprites to move around
* Weel 2: Generate the base structure for the codebase
* Week 3: Actions, triggers, effects
* Week 4: Pathfinding and random map generation
* Week 5: UI
* Week 6: Creature AI

##Cut out for future
* World map
* Inventory-system
* Libraries for MOBs
* Random game generation (beyond maps)
