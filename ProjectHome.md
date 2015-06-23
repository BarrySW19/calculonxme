JavaME version of CalculonX Chess Engine.

Play chess on your mobile!

Status: Basic chess application is already working - it can play a game of chess on a mobile but the interface is still quite rudimentary. I particularly need help in testing and porting this to a range of mobile devices.

LATEST: The new bitboard code has been ported to the ME version which has speeded up the engine by an order of magnitude! It now moves within about 10 secs max on my Nokia N82 (compared to a couple of minutes before). This means that the mobile version now plays at a strength of around 1400 - 1500 (based on FICS/ICC ratings).

To test the current version, download the JAD/JAR from the snapshot directory in the trunk folder of the source. Usage is:

'1' - Flip board
'2' - Ask computer to make current move.
'3' - Reset the game

Arrow keys and 'fire' to move the cursor and select piece to move or destination square.