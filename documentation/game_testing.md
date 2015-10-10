#Regarding the testing

Due to the nature of the game, doing extensive testing with JUnit was difficult. Further testing was therefore needed (and done) by actually playing the game. These tests should be repeated whenever the game is about to reach a stable version.

In general the tests were performed by forcing the game to the desired situation, and playing it through repeatedly. For example monster pathfinding past tight corners revolved around a special map (made out of tight corners) and letting monsters with randomized positions path their way through it

In order to test the game during the development, before actual content was made, some special assets were needed. These assets exist both within the code ("TODO: Test asset") and out of it (pathfinder_test.map, tilemaptest.map...).

#Movement

##Moving in Location

###Test case: Move around a location
* **Last testing:** 8.10.2015
* **Setup:** Create a new game on a random map, moving around with arrow keys
* **Results:** Walls and creatures stop movement as they should. Diagonal movement works. No unusual behaviour observed.
* **Actions needed:** No actions needed.

###Test case: Move around clearing structures
* **Last testing:** 8.10.2015
* **Setup** Create a new game on a random map. Move around and use space to destroy walls
* **Results:** The attack only destroys things that clip with the graphic. Sometimes this means one wall destroyed, sometimes two.
* **Actions needed:** No actions needed.

#Combat

###Test case: Find and kill a creature
* **Last testing:** 8.10.2015
* **Setup:** Create a game on a random map. Find creatures to slay and hit them.
* **Results:** Mobs die fine when hit, their HP going down and all. Moving mobs die fine too. No unusual behaviour encountered.
* **Actions needed:** No actions needed.

#Pathfinding
![](https://github.com/nkoiv/mists/blob/master/documentation/pathfinding_testing1.png "Pathfinding testmap")

##General pathfinding

###Test case: Make sure creatures that have no path to target dont break the game.
* **Last testing:** 08.10.2015
* **Setup:** Use the testmap /mists/src/main/resources/pathfinder_test.map. Spawn creatures in random locations. Place player in a fully obstructed area. Use the "creatureAI().pathToPlayer()" to have creatures path to player.
* **Results:** Creatures try to move in the general diretion of the player when no path is found. This results them hugging walls. Creatures do NOT pathfind to a spot as close as possible before just going towards in a straight line, but rather just abandon pathfinding altogether.
* **Actions needed:** Consider if creatures should try to use proper pathfinding to get as close to as possible to target when path is obstructed.

###Test case: Check if creatures get stuck on corners when closing in on target.
* **Last testing:** 08.10.2015
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
* **Last testing:** 08.10.2015
* **Setup:** Created a zig-zag map sized 200*40 (/mists/src/main/resources/longpath_test.map). Place target (player) on other end and the moving creature on the other. Time the pathfinding.
* **Results:** 50 nodes is a really SHORT default search distance when its node by node. The Manhattan distance can be really short (just across the wall), and the pathfinding still cant find the right path (because it has to go around).
* **Actions needed** Consider having different sort of pathfindings for different map? Could it be decided on by parsing the collisionmap?

#Audio
###Test case: Changing audio output midgame
* **Last testing:** 2.9.2015
* **Setup:** Start a game with default audio set to headphones. Change default audio from windows to speakers midgame.
* **Results:** Music and sounds swap to speakers as intended
* **Actions needed:** Make options to choose the audio output for the game session

###Test case: Unmute game sounds when system sounds are set to 0
* **Last testing:** 2.9.2015
* **Setup:** Start game, mute sounds. Set windows sound volume to 0. Unmute game sounds.
* **Results:** Sounds stay silent as per expected.
* **Actions needed:** No actions needed.
* 
