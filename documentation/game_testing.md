#Regarding the testing

Due to the nature of the game, doing extensive testing with JUnit was difficult.
In order to test the game during the development, before actual content was made, some special assets were needed. These assets exist both within the code ("TODO: Test asset") and out of it (pathfinder_test.map, tilemaptest.map...).
In general the tests were performed by forcing the game to the desired situation, and playing it through repeatedly. For example monster pathfinding past tight corners revolved around a special map (made out of tight corners) and letting monsters with randomized positions path their way through it

#Pathfinding

##General pathfinding
###Test case: Make sure creatures that have no path to target dont break the game.
* **Setup:** Spawn creatures in random locations. Place player in a fully obstructed area. Use the "creatureAI().pathToPlayer()" to have creatures path to player.
* **Results:** Creatures try to move in the general diretion of the player when no path is found. This results them hugging walls. Creatures do NOT pathfind to a spot as close as possible before just going towards in a straight line, but rather just abandon pathfinding altogether.
* **Actions needed:** Consider if creatures should try to use proper pathfinding to get as close to as possible to target when path is obstructed.

###Test case: Check if creatures get stuck on corners when closing in on target.
* **Setup:** Place the player inside a labyrinth of walls. Spawn creatures outside the spiral. See how creatures pathfind to get to the player.
* **Results:** Creatures manouver past walls without problems. Bumping into other creatures occasionally causes creatures to get stuck on tight spots. At least at a T-junction when players arrive from different directions, they can get stuck not realizing one should give way to another.
+----------------+
|    xx@xx       | x Wall
|    xx xx       | @ Player
| xxxxx xxxxx    | M Monster
|     M*M        | * Collision point
| xxxxxxxxxxx    |
+----------------+
* **Actions needed:** The collisionmap currently uses "StructuresOnly" boolean that removes all the moving creatures from PathFinding. Perhaps they it should be set to false in some situations?

###Test case: Test pathfinding on unusually long paths
* **Setup:** Created a zig-zag map sized 200*40 (/mists/src/main/resources/longpath_test.map). Place target (player) on other end and the moving creature on the other. Time the pathfinding.
* **Results:** 50 nodes is a really SHORT default search distance when its node by node. The Manhattan distance can be really short (just across the wall), and the pathfinding still cant find the right path (because it has to go around).
* **Actions needed** Consider having different sort of pathfindings for different map? Could it be decided on by parsing the collisionmap?


Testmap used (/mists/src/main/resources/pathfinder_test.map)
(X is a wall, 0 is free to move floor)
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
X000X000000000000000000000000X00000000000000000000000000000X
X000X0000000000000XXXX0000000X00000000000000000000000000000X
X000X0000000000000000XX000000X00XXXXXXX0XXXXXX0000XXX000XXXX
X000X00XXXXX0000X00X000X00000X0000XX0000X00000000X000X0000XX
X000X000000X00000X00X000X0000X0000XX0000XXXXXX0000XX000000XX
XX00XXXXXX0XX00000X00X00X000000000XX0000X00000000000XX0000XX
X0000000000X0000000X00X0X000000000XX0000XXXXXX000XXXX00000XX
X00000000000000X0000X00XX0000X00000000000000000000000000000X
X000X0000000000X0000000000000X0000000000X000000000000000000X
X0XXXX00000X000XXXXXXXXXXXX00X00XX00000X00X0000000000000000X
X00000000XXXX00X0000000000000X00XX0000X00000X0000X000000000X
X000000000X0000X00XXXXXXXXXXXX0000000X00000000X00000X000000X
X0000000000000XX0000000000000X000000X000000X00000000X000000X
X00XXXXXXXX0000X0000000000000X00000X000X000000000000X000000X
X00X00000000000XXXXXXXXXXXX00X0000X0000000000X000000X000000X
X00X000XXXXXXXXX0X00000000000000000000000X00000000000000000X
X00X00XX00000000000X000000000000000X00000000XXXXX0000000000X
X00X00X000XXXXXXX000XXXXXXXXXX000000XXXX0000000X000XXXX0000X
X00X00X00X0000000X000X0000000X00000000XXX0000000000X00X0000X
X00X00X00X00000000X00X0000000X00XX0XX00000XXX000000X00X0000X
X00X00X000XXXXX000X00X0000000X00000X00XX000X0000000XXXX0000X
X00X00X0000000000XX00X0000000X0000000XXXX000000000000000000X
X00X00XX000000000XX00X0000000X00000000000000000000000000000X
X00X000XXXXXXXXXXX000X0000000X000000000XXX00000000000000000X
X00X00000000000000000X0000000X00000XX0000000000000000000000X
X00X00000000000000000X000000000000XXX0000000000000000000000X
X00XXXXXX00XXXXXXXXXXX0000000000000000000XXXX00000000000000X
X0000000000000000000000000000X00000000000000000000000000000X
X0000000000000000000000000000X00000000000000000000000000000X
XXXXXXXXXXXXX00XXXXXXXXXXXXXXX00000000000000000000000000000X
X0000000000000000000000000000000000000000000000000000000000X
XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX