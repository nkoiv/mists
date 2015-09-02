#Regarding the testing

Due to the nature of the game, doing extensive testing with JUnit was difficult.
In order to test the game during the development, before actual content was made, some special assets were needed. These assets exist both within the code ("TODO: Test asset") and out of it (pathfinder_test.map, tilemaptest.map...).
In general the tests were performed by forcing the game to the desired situation, and playing it through repeatedly. For example monster pathfinding past tight corners revolved around a special map (made out of tight corners) and letting monsters with randomized positions path their way through it

#Pathfinding

##General pathfinding
###Test case: Make sure creatures that have no path to target dont break the game.
* **Setup:** Use the testmap /mists/src/main/resources/pathfinder_test.map. Spawn creatures in random locations. Place player in a fully obstructed area. Use the "creatureAI().pathToPlayer()" to have creatures path to player.
* **Results:** Creatures try to move in the general diretion of the player when no path is found. This results them hugging walls. Creatures do NOT pathfind to a spot as close as possible before just going towards in a straight line, but rather just abandon pathfinding altogether.
* **Actions needed:** Consider if creatures should try to use proper pathfinding to get as close to as possible to target when path is obstructed.

###Test case: Check if creatures get stuck on corners when closing in on target.
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
* **Setup:** Created a zig-zag map sized 200*40 (/mists/src/main/resources/longpath_test.map). Place target (player) on other end and the moving creature on the other. Time the pathfinding.
* **Results:** 50 nodes is a really SHORT default search distance when its node by node. The Manhattan distance can be really short (just across the wall), and the pathfinding still cant find the right path (because it has to go around).
* **Actions needed** Consider having different sort of pathfindings for different map? Could it be decided on by parsing the collisionmap?


