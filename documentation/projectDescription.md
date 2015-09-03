**Overview:** Mists is a realtime roguelike game with adventure elements.
Unlike in most roguelikes, the player character in Mists is accompanied by a helpful creature. Together these two travel throughout various procedurally generated locales, solving puzzles and vanquishing foes.

**Keywords:** Graphic interface, helpful companion, procedurally generated maps

**Users:** Mists is a single player game. On the broad spectrum, the player should be able to:
* Start a new game
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
* Open game menu: Escape
* Moving around: Arrow keys
* Using ability (Melee attack): Space
* Activate/Deactivate creatures: Shift

##Program structure
The game is built loosely on MVC principles, where everything the user sees and does is passed
through a controller. Various gamestates govern the main areas of the game, and the principle is that a new gamestate is only added when desired gameplay differs wildly from what
existing gamestates can provide.
The accompanied [UML Class diagram](https://github.com/nkoiv/mists/blob/master/documentation/mists_classchart.jpg) is good reference for this.

###Main loop

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

###MainMenu
From the main menu a player can either start a new game, load an existing one,
edit game options, or close the game.

###Location
Locations house the bulk of the adventure. They're built on a map, have structures blocking players path, and
contain various monsters and puzzles to face. The maps come in two main variations: BGMaps and TileMaps. The former
are based on a single image (hence the "BG", background), wheras the latter (TileMaps) are built from small tiles.
TileMaps can also be randomly generated via the MapGenerator-class.

On top of the maps we have MapObjects. Anything that can block, hurt or affect the player or other creatures in any
way is a MapObject. They can be either Creatures, Structures or Effects. Creatures have behaviours via AIs, Structures
tend to be more static. Effects are temporary by definition, though their duration could obviously be infinite.

Doing things (Actions) in the game is generally done with the aid of Effects. An effect is generated when an ability is
used, and the targets of the ability are picked based on what the effect manages to intersect. An arrow lands on the
first target it hits, etc.

###WorldMap
TODO, probably cut out

###Town
TODO, probably cut out

##Combat

![](https://github.com/nkoiv/mists/blob/master/documentation/combat_action.png "Actions in combat")

Combat happens by invoking (combat)actions. Creatures use these actions to do combat with oneanother. Triggering an action generally spawns an effect on the map. This effect then passes the actions trigger on whatever it touches. This chain of effects is modeled in the [actions and effects sequence diagram](https://github.com/nkoiv/mists/blob/master/documentation/sequence_diagrams/actions_and_effects.jpg).
Everything involved in the combat should implement the "Combant" interface. As combat Actions only affect classes implementing the Combatant, this ensures that everything in the combat is capable of dealing with damage, death, etc.

###Combat mechanics
TODO: Plan and implement mechanics for how damage is calculated. Is there armour? Can mobs dodge/parry attacks?

##Testing
Testing the game is done from two directions: Unit tests inside the Maven project, performing GUI testing by playing the game.
* Unit tests are enhanced by PIT mutation, documentation for which can be found under the [mists/documentation/pit-reports/ -folder](https://github.com/nkoiv/mists/tree/master/documentation/pit-reports).
* GUI testing is documented in [/mists/documentation/game_testing.md](https://github.com/nkoiv/mists/blob/master/documentation/game_testing.md)

##Project plan

###Weekly plan
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

##Licenced assets in use

Some game sprites are from:
Humble Bundle pack:
Old School Modern 2
[Copyright](C) Jason Perry

Some game tiles are from
Humble Bundle -pack:
Adventure Tile Starter - Celianna
[Copyright](C) Celianna

Game sounds are from OpenGameArt.org
Weapon blow, (c) spookymodem

The Game music is from:
JDB Artists Humble Bundle -pack:
MADNESS - ROYALTY FREE MUSIC BY JDB ARTIST
www.jdbartist.net
[Copyright](C) JDB Artist

And:
Joe Steudler Music Sampler Humble Bundle:
[Copyright](C) 2014 DEGICA Co., LTD; Enterbrain, INC;
Artist: Joel Steudler


