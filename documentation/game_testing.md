#Regarding the testing

Due to the nature of the game, doing extensive testing with JUnit was difficult. Further testing was therefore needed (and done) by actually playing the game. These tests should be repeated whenever the game is about to reach a stable version.

In general the tests were performed by forcing the game to the desired situation, and playing it through repeatedly. For example monster pathfinding past tight corners revolved around a special map (made out of tight corners) and letting monsters with randomized positions path their way through it

In order to test the game during the development, before actual content was made, some special assets were needed. These assets exist both within the code ("TODO: Test asset") and out of it (pathfinder_test.map, tilemaptest.map...).

#Movement

##Moving in Location

###Test case: Move around a location
* **Last testing:** 18.5.2016
* **Setup:** Create a new game on a random map, moving around with arrow keys
* **Results:** Walls and creatures stop movement as they should. Diagonal movement works. No unusual behaviour observed.
* **Actions needed:** No actions needed.

###Test case: Move around clearing structures
* **Last testing:** 18.5.2016
* **Setup** Create a new game on a random map. Move around and use space to destroy walls
* **Results:** The attack only destroys things that clip with the graphic. Sometimes this means one wall destroyed, sometimes two.
* **Actions needed:** No actions needed.

###Test case: Toggling objects in location
* **Last testing:** 18.5.2016
* **Setup:** Move into a location that has doors and NPCs. Test toggling them.
* **Results:** Doors and NPCs can both be toggled with mouse and E at appropriate range. Locked door gives "Locked" popup, empty containers "Empty" popup. Seems to work fine.
* **Actions needed:** No actions needed.

##Moving on a worldmap

###Test case: Move around on worldmap
* **Last testing:** 18.5.2016
* **Setup:** Create a new game and move to world map. Visit all nodes.
* **Results:** Moving with WASD and arrows works fine, but the lack of movement indicator makes map movement a bit confusing.
* **Actions needed:** Add in movement with a mouse, and arrows telling where each keyboard button gets the player.

#Inventory

###Test case: Pick up and drop items
* **Last testing:** 18.5.2016
* **Setup:** Drop an item from inventory and pick it up again.
* **Results:** Dropping the initial healing potions works fine, picking them up with E or Mouse works the same. Popup on pickup.
* **Actions needed:** No actions needed.

###Test case: Use items from inventory
* **Last testing:** 18.5.2016
* **Setup:** Start a game and use Potion. Equip a weapon.
* **Results:** While equipping and using potions both work, they seem to lag a tad when player is moving simultaneously.
* **Actions needed:** Using items uses Action.USE_ITEM, which goes to same queue as movement and other actions. Moving while attempting to drink a potion might overlap the drink action, causing it to not fire off at the update(use action) -phase of creatures? Actionpoint to further test and see what can be done.

#Combat

###Test case: Find and kill a creature
* **Last testing:** 18.5.2016
* **Setup:** Create a game on a random map. Find creatures to slay and hit them.
* **Results:** Mobs die fine when hit, their HP going down and all. Moving mobs die fine too. No unusual behaviour encountered.
* **Actions needed:** No actions needed.

###Test case: Get player killed
* **Last testing:** 18.5.2016
* **Setup:** Find some monsters and get killed to damage.
* **Results:** Getting to 0 or below hitpoints dimms the screen and gives a "you are dead" popup message. Seems to work fine. Starting new game after it works too.
* **Actions needed:** No actions needed.

#Pathfinding
![](https://github.com/nkoiv/mists/blob/master/documentation/pathfinding_testing1.png "Pathfinding testmap")

##General pathfinding

###Test case: Make sure creatures that have no path to target dont break the game.
* **Last testing:** 18.5.2016
* **Setup:** Use the testmap /mists/src/main/resources/pathfinder_test.map. Spawn creatures in random locations. Place player in a fully obstructed area. Use the "creatureAI().pathToPlayer()" to have creatures path to player.
* **Results:** Creatures try to move in the general diretion of the player when no path is found. This results them hugging walls. Creatures do NOT pathfind to a spot as close as possible before just going towards in a straight line, but rather just abandon pathfinding altogether.
* **Actions needed:** Consider if creatures should try to use proper pathfinding to get as close to as possible to target when path is obstructed.

###Test case: Check if creatures get stuck on corners when closing in on target.
* **Last testing:** 16.5.2016
* **Setup:** Use the testmap /mists/src/main/resources/pathfinder_test.map. Place the player inside a labyrinth of walls. Spawn creatures outside the spiral. See how creatures pathfind to get to the player.
* **Results:** Creatures manouver past walls without problems. Bumping into other creatures occasionally causes creatures to get stuck on tight spots. At least at a T-junction when players arrive from different directions, they can get stuck not realizing one should give way to another.
<pre>
+----------------+
|    xx@xx       | x Wal
|    xx xx       | @ Player
| xxxxx xxxxx    | M Monster
|     M*M        | * Collision point
| xxxxxxxxxxx    |
+----------------+
</pre>
* **Actions needed:** The collisionmap currently uses "StructuresOnly" boolean that removes all the moving creatures from PathFinding. Perhaps they it should be set to false in some situations?

###Test case: Test pathfinding on unusually long paths
* **Last testing:** 18.5.2016
* **Setup:** Created a zig-zag map sized 200*40 (/mists/src/main/resources/longpath_test.map). Place target (player) on other end and the moving creature on the other. Time the pathfinding.
* **Results:** Monsters find the path fine and fast.
* **Actions needed** No actions needed. However it might be worth considering having different sort of pathfindings for different maps? Could the optimal pathfinding be decided on by parsing the collisionmap?

##Test case: Test pathfinding with huge number of mobs
* **Last testing:** 18.5.2016
* **Setup:** Used the Pathfinder test map wtih 200 creatures on it. See if the game lags down.
* **Results:** No FPS drop from 60 with test machine. Some lag to be detected after 500+ creatures, but monitoring threads reveals it's not due to pathfinder but Line of Sight algorithms.
* **Actions needed** Figure out a better way to do Line of Sight, instead of parshing through all the structures to see if they block the way (use collisionmap or raycasting?)


#Audio
###Test case: Changing audio output midgame
* **Last testing:** 18.5.2016
* **Setup:** Start a game with default audio set to headphones. Change default audio from windows to speakers midgame.
* **Results:** Music and sounds swap to speakers as intended
* **Actions needed:** Make options to choose the audio output for the game session

###Test case: Unmute game sounds when system sounds are set to 0
* **Last testing:** 18.5.2016
* **Setup:** Start game, mute sounds. Set windows sound volume to 0. Unmute game sounds.
* **Results:** Sounds stay silent as per expected.
* **Actions needed:** No actions needed.

##Test case: Spam abilities with no cooldown to overload sound channels
* **Last testing:** 18.5.2016
* **Setup:** Remove the cooldown from player Shoot Firebolt ability, start the game and spam it.
* **Results:** Simultanous looping sounds seem to work fine. Was unable to get errors from JavaFX SoundManager despite going over the audio thread pool size.
* **Actions needed:** No actions needed
