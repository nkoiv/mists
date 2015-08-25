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

**Playing the game**
So far only the Location -level of the game has been developed.
Further down the road, there should be a world map to travel from a location to another.

***Location controls (POC)***
* Open game menu: Escape
* Moving around: Arrow keys
* Using ability (Melee attack): Space
* Activate/Deactivate creatures: Shift

**Program structure**
The game is built loosely on MVC principles, where everything the user sees and does is passed
through a controller. In practice this means that the game uses "gamestates" to relay commands into the game
while also calling for renders back into the main stage. Various gamestates govern the main areas of the game,
and the principle is that a new gamestate is only added when desired gameplay differs wildly from what
existing gamestates can provide.
The accompanied UML Class diagram is good reference for this, but the rought structure is as follows:

* Gamestate for MainMenu
* Gamestate for Locations 
* GameState for WorldMap
* GameState for Town

Bulk of the gameplay resides at Locations. These are top down areas where the player can move around, exploring
and combating adversaries. WorldMap is used for traveling between Locations, but it's a lot more limited as far
as action is considered. Towns are mainly composed of menus (taverns, shops, etc). MainMenu is selfexplanatory.

***MainMenu***
Game starts at the main menu. From the main menu a player can either start a new game, load an existing one,
edit game options, or close the game.

***Locations***
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

***WorldMap***
TODO, probably cut out

***Town***
TODO, probably cut out

**Project plan**

***Weekly plan***
* Week 1: Create a window with sprites to move around
* Weel 2: Generate the base structure for the codebase
* Week 3: Actions, triggers, effects
* Week 4: Pathfinding and random map generation
* Week 5: UI
* Week 6: Creature AI

***Cut out for future***
* World map
* Inventory-system
* Libraries for MOBs
* Random game generation (beyond maps)

**Licenced assets in use**

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


