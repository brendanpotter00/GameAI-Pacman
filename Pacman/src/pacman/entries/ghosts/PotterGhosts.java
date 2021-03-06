
package pacman.entries.ghosts;

import static pacman.game.Constants.GHOST_SPEED_REDUCTION;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Random;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.internal.Ghost;
import pacman.game.internal.Maze;
import pacman.game.internal.Node;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getActions() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., game.entries.ghosts.mypackage).
 */
public class PotterGhosts extends Controller<EnumMap<GHOST,MOVE>>
{
	
	private EnumMap<GHOST, MOVE> myMoves=new EnumMap<GHOST, MOVE>(GHOST.class);
	Random rnd=new Random();
	
	
	public EnumMap<GHOST, MOVE> getMove(Game g, long timeDue)
	{
		myMoves.clear();
		GameView gv = new GameView(g);
		boolean hasPacmanDiedVar = false;
		//target scatter Nodes for lvl 1
		int pinkyScatterNode = 0;
		int blinkyScatterNode = 78;
		int inkyScatterNode = 1290;
		int sueScatterNode = 1200;
		int blinkyX = 0;
		int blinkyY = 0;
		int pillsTillElroy = 10;
		
		//setting elroy pills 
		elroyPills(g, pillsTillElroy);
		
		//for loop for all ghosts
		for(GHOST ghost : GHOST.values()) 
		{
			boolean ghostsOut = areAllGhostsOutOfLair(g);
			//System.out.println(g.getGhostLairTime(ghost));
			//System.out.println(ghostsOut);
			
			//setting the scatter nodes based on the level
			Maze maze = g.getCurrentMaze();
			Node [] nodeLst = maze.graph;
			int mazeNum = determiningMap(g);
			
			//map a 
			if (mazeNum == 1)
			{
				pinkyScatterNode = 0;
				blinkyScatterNode = 78;
				inkyScatterNode = 1290;
				sueScatterNode = 1200;
			} 
			//map b
			if (mazeNum == 2) 
			{
				pinkyScatterNode = 135;
				blinkyScatterNode = 219;
				inkyScatterNode = 1308;
				sueScatterNode = 1221;
			}
			//map c
			if (mazeNum == 3) 
			{
				pinkyScatterNode = 0;
				blinkyScatterNode = 78;
				inkyScatterNode = 1290;
				sueScatterNode = 1200;
			}
			//map d 
			if (mazeNum == 4) 
			{
				pinkyScatterNode = 0;
				blinkyScatterNode = 78;
				inkyScatterNode = 1290;
				sueScatterNode = 1200;
			}
			
			 
			
			
			//frightened mode 
			if (g.getGhostEdibleTime(ghost) > 0)
			{
				MOVE[] possibleMoves=g.getPossibleMoves(g.getGhostCurrentNodeIndex(ghost),g.getGhostLastMoveMade(ghost));
				myMoves.put(ghost,possibleMoves[rnd.nextInt(possibleMoves.length)]);
			}
			//System.out.println(g.getCurrentLevelTime());
			else if (g.doesGhostRequireAction(ghost)) 
			{
				/*
				myMoves.put(ghost, g.getNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), 5, DM.PATH));
				
				if (ghost.equals(ghost.BLINKY))
				{
					myMoves.put(ghost, g.getNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), 50, DM.PATH));
				}
				gv.addPoints(g, Color.WHITE, 5);
				*/
				
				//blinky current position for inky behavior
				if (ghost.equals(ghost.BLINKY)) 
				{
					//System.out.println("blinky location found");
					int blinkyNodeIndex = g.getGhostCurrentNodeIndex(ghost);
					Node blinkyNode = nodeLst[blinkyNodeIndex];
					blinkyX = blinkyNode.x;
					blinkyY = blinkyNode.y;
					
				}
				
				
				//SCATTER MODE
				if (chaseOrScatterTeller(g) == 1 || chaseOrScatterTeller(g) == 2) 
				{
					System.out.println("scatter mode activated");
					if (ghost.equals(ghost.PINKY))
					{
						myMoves.put(ghost, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), pinkyScatterNode, g.getGhostLastMoveMade(ghost), DM.EUCLID));
						gv.addPoints(g, Color.PINK, pinkyScatterNode);
					}
					
					if (ghost.equals(ghost.BLINKY))
					{
						
						//cruise elroy mode
						if (g.getNumberOfActivePills() <= pillsTillElroy)
						{
							//add going to normal if pacman is eaten during elroy mode until all ghosts are out 
							if (ghostsOut) 
							{
								//normal scatter behavior 
								//System.out.println("this is working yay");
								myMoves.put(ghost, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), blinkyScatterNode, g.getGhostLastMoveMade(ghost), DM.EUCLID));
								gv.addPoints(g, Color.RED, blinkyScatterNode);
								//speed decrease here
							}
							
							int pacManCurrentPos = g.getPacmanCurrentNodeIndex();
							gv.addPoints(g, Color.RED, pacManCurrentPos);
							myMoves.put(ghost, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), pacManCurrentPos, g.getGhostLastMoveMade(ghost), DM.EUCLID));
							//speed increase goes here
							
						} else  
						{
							//normal scatter mode
							myMoves.put(ghost, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), blinkyScatterNode, g.getGhostLastMoveMade(ghost), DM.EUCLID));
							gv.addPoints(g, Color.RED, blinkyScatterNode);
						}
					}
					
					if (ghost.equals(ghost.INKY))
					{
						myMoves.put(ghost, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), inkyScatterNode, g.getGhostLastMoveMade(ghost), DM.EUCLID));
						gv.addPoints(g, Color.CYAN, inkyScatterNode);
					}
					
					if (ghost.equals(ghost.SUE))
					{
						myMoves.put(ghost, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), sueScatterNode, g.getGhostLastMoveMade(ghost), DM.EUCLID));
						gv.addPoints(g, Color.ORANGE, sueScatterNode);
					}
				}
				//chase mode 
				else if (chaseOrScatterTeller(g) == 3 || chaseOrScatterTeller(g) == 4) 
				{
					/*
					//default chase mode for all ghosts to go to top right corner
					System.out.println("Chase mode activated");
					myMoves.put(ghost, g.getNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), 5, DM.PATH));
					gv.addPoints(g, Color.GRAY, 5);
					*/
					
					 System.out.println("Chase mode activated");
					//BLINKY CHASE 
					if (ghost.equals(ghost.BLINKY)) 
					{
						if(g.getNumberOfActivePills() == 20)
						{
							//speed increase 
						}
						
						int pacManCurrentPos = g.getPacmanCurrentNodeIndex();
						myMoves.put(ghost, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), pacManCurrentPos, g.getGhostLastMoveMade(ghost), DM.EUCLID));
						gv.addPoints(g, Color.RED, pacManCurrentPos);
						
					}
					//PINKY CHASE
					if (ghost.equals(ghost.PINKY))
					{
						//System.out.println("pinky is chasing");
						myMoves.put(ghost, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), pinkyTargetNode(g), g.getGhostLastMoveMade(ghost), DM.EUCLID));
						gv.addPoints(g, Color.PINK, pinkyTargetNode(g));
					}
					//INKY CHASE
					if (ghost.equals(ghost.INKY))
					{
						//myMoves.put(ghost, g.getNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), 5, DM.EUCLID));
						//gv.addPoints(g, Color.GRAY, 5);
						
						
						int secondNodeIndexPM = secondNodeInFrontOfPacman(g);
						Node secondNodePM = nodeLst[secondNodeIndexPM];
						int secondNodePMX = secondNodePM.x;
						int secondNodePMY = secondNodePM.y;
						boolean foundNode = false;
						
						int targetNodeX = secondNodePMX - (blinkyX - secondNodePMX);
						int targetNodeY = secondNodePMY - (blinkyY - secondNodePMY);
						int targetNodeIndex = 1;
						
						int pacManCurrentNode = g.getPacmanCurrentNodeIndex();
						int pmCurrPosX = g.getNodeXCood(pacManCurrentNode);
						int pmCurrPosY = g.getNodeYCood(pacManCurrentNode);
						
						while(!foundNode) 
						{
							for (int i = 0; i < nodeLst.length; i++)
							{
								if (nodeLst[i].x == targetNodeX && nodeLst[i].y == targetNodeY)
								{
									foundNode = true;
									targetNodeIndex = nodeLst[i].nodeIndex;
								}
							}
							
							if (targetNodeX > pmCurrPosX) 
							{
								targetNodeX = targetNodeX - 1;
							} 
							else 
								if (targetNodeX < pmCurrPosX)
								{
									targetNodeX = targetNodeX + 1;
								}
							if (targetNodeY > pmCurrPosY)
							{
								targetNodeY = targetNodeY - 1;
							} 
							else
								if (targetNodeY < pmCurrPosY)
								{
									targetNodeY = targetNodeY + 1;
								}
						}

						myMoves.put(ghost, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), targetNodeIndex, g.getGhostLastMoveMade(ghost), DM.EUCLID));
						gv.addPoints(g, Color.CYAN, targetNodeIndex);
					}
					//SUE CHASE
					if (ghost.equals(ghost.SUE))
					{
						//System.out.println("sue is chasing");
						//checking the proximity of sue and adjusting targetNode accordingly
						double proximity = g.getEuclideanDistance(g.getGhostCurrentNodeIndex(ghost), g.getPacmanCurrentNodeIndex());
						int sueTarget = -1;
						
						//changed from 8 to 35 because 8 was too close to pacman
						if (proximity < 35) 
						{
							sueTarget = inkyScatterNode;
						} 
						else
						{
							sueTarget = g.getPacmanCurrentNodeIndex();
						}
						myMoves.put(ghost, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), sueTarget, g.getGhostLastMoveMade(ghost), DM.EUCLID));
						gv.addPoints(g, Color.ORANGE, sueTarget);
					}
					
				}
			}
		}
		
		return myMoves;
	}
	
	
	//HELPER FUNCTIONS
	
	public int elroyPills(Game g, int pills)
	{
		int lvl = g.getCurrentLevel();
		pills = 10;
		
		if (lvl == 0)
		{
			pills = 10;
		} else if (lvl == 1)
		{
			pills = 15;
		} else if (lvl == 2) 
		{
			pills = 20;
		} else if (lvl == 3) 
		{
			pills = 30;
		}
		else pills = 60;
		
		return pills;
	}
	
	
	public int determiningMap(Game g)
	{
		Maze maze = g.getCurrentMaze();
		String name = maze.name;
		if (name.equalsIgnoreCase("a"))
		{
			return 1;
		} 
		else if (name.equalsIgnoreCase("b"))
		{
			return 2;
		} else if (name.equalsIgnoreCase("c"))
		{
			return 3;
		} else if (name.equalsIgnoreCase("d"))
		{
			return 4;
		}
		else return 1;
	}
	
	
	//function that finds the node 2 tiles in front of pacman
	public int secondNodeInFrontOfPacman(Game g) 
	{
		Maze maze = g.getCurrentMaze();
		Node [] nodeLst = maze.graph;
		
		
		int pacManCurrentNode = g.getPacmanCurrentNodeIndex();
		int pmCurrPosX = g.getNodeXCood(pacManCurrentNode);
		int pmCurrPosY = g.getNodeYCood(pacManCurrentNode);
		MOVE pmLastMove = g.getPacmanLastMoveMade();
		
		int distanceFromPM = 2;
		int xShift = 0;
		int yShift = 0;
		boolean foundMove = false;
		//Node targetNode = null;
		int targetInt = 0;
		
		if (g.getPacmanLastMoveMade() == MOVE.UP)
		{
			xShift = 0;
			yShift = -1;
		}
		if (g.getPacmanLastMoveMade() == MOVE.DOWN)
		{
			xShift = 0;
			yShift = 1;
		}
		if (g.getPacmanLastMoveMade() == MOVE.RIGHT)
		{
			xShift = -1;
			yShift = 0;
		}
		if (g.getPacmanLastMoveMade() == MOVE.LEFT)
		{
			xShift = -1;
			yShift = 0;
		}
		
		while(!foundMove)
		{
			for (int i = 0; i < nodeLst.length; i++)
			{
				if (nodeLst[i].x == (pmCurrPosX + (distanceFromPM * xShift)) && 
					nodeLst[i].y == (pmCurrPosY + (distanceFromPM * yShift)))
				{
					targetInt = nodeLst[i].nodeIndex;
					foundMove = true;
				}
			}
			distanceFromPM = distanceFromPM - 1;
		}
		
		return targetInt;
	}
	

	//getting the maze, making a list of 4 nodes, checking nodes for validity, then returning a node index
	public int pinkyTargetNode(Game g) 
	{
		Maze maze = g.getCurrentMaze();
		Node [] nodeLst = maze.graph;
		
		
		int pacManCurrentNode = g.getPacmanCurrentNodeIndex();
		int pmCurrPosX = g.getNodeXCood(pacManCurrentNode);
		int pmCurrPosY = g.getNodeYCood(pacManCurrentNode);
		MOVE pmLastMove = g.getPacmanLastMoveMade();
		
		int distanceFromPM = 4;
		int xShift = 0;
		int yShift = 0;
		boolean foundMove = false;
		//Node targetNode = null;
		int targetInt = 0;
		
		if (pmLastMove == MOVE.UP)
		{
			xShift = -1;
			yShift = -1;
		}
		if (pmLastMove == MOVE.DOWN)
		{
			xShift = 0;
			yShift = 1;
		}
		if (pmLastMove == MOVE.RIGHT)
		{
			xShift = -1;
			yShift = 0;
		}
		if (pmLastMove == MOVE.LEFT)
		{
			xShift = -1;
			yShift = 0;
		}
		
		while(!foundMove)
		{
			for (int i = 0; i < nodeLst.length; i++)
			{
				if (nodeLst[i].x == (pmCurrPosX + (distanceFromPM * xShift)) && 
					nodeLst[i].y == (pmCurrPosY + (distanceFromPM * yShift)))
				{
					targetInt = nodeLst[i].nodeIndex;
					foundMove = true;
				}
			}
			distanceFromPM = distanceFromPM - 1;
		}
		return targetInt;
	}
	
	
	//has pacman died during the current level
	public boolean hasPacmanDied(Game g, boolean hasPacmanDiedFinal)
		{
			boolean wasPacmanEverEatenDuringLevel = false;
			int LvlTime = g.getCurrentLevelTime();
			boolean levelReset = false;
			
			if (LvlTime == 0)
			{
				levelReset = true;
			}
			
			if (g.wasPacManEaten() && LvlTime > 0)
			{
				wasPacmanEverEatenDuringLevel = true;
			}
			
			if(levelReset)
			{
				hasPacmanDiedFinal = false;
			}
			//if pacman has died during the level and the ghosts are still inside the lair then return true and do normal scatter behavior
			
			if (wasPacmanEverEatenDuringLevel && !areAllGhostsOutOfLair(g))
				hasPacmanDiedFinal = true;
			
			return hasPacmanDiedFinal;
		}
	
	
	//returns true if all the ghosts are outside of the lair 
	public boolean areAllGhostsOutOfLair(Game g) 
	{
		int global = 0;
		for(GHOST ghost : GHOST.values()) 
		{
			if (g.getGhostLairTime(ghost) > 0)
			{
				global ++;
			}
		}
		if (global == 4) 
		{
			return false;
		}
		else return true;
	}
	
	
	//returns an int to tell if ghosts should be in scatter or chase mode and for how long
	//based on first level waves in "game internals"
	public int chaseOrScatterTeller (Game g)
	{
		//scatter divided
		//chase multiplied 
		//1: 7 second scatter
		//2: 5 second scatter
		//3: 20 second chase
		//4: permanent chase
		
		//temporarily in seconds 
		//use in getMove*
		int div = g.getCurrentLevel() + 1;
		int scatterMult = (int)(25 / div);
		int chaseMult = (int)(25 * div); 
		int ongoingTime = g.getCurrentLevelTime();
		int temp = 0;
		int currLvl = 1;
		if (g.wasPacManEaten()) 
		{
			temp = g.getCurrentLevelTime();
		} else if (currLvl != g.getCurrentLevel())
		{
			temp = 0;
			currLvl = g.getCurrentLevel();
		}
		
		int t = ongoingTime - temp;
		
		//scatter if's
		if (ongoingTime <= 7 * scatterMult)
		{
			return 1; 
		}
		else if (((7 * scatterMult) + (20 * chaseMult)) < t && t <= ((14 * scatterMult) + (20 * chaseMult)))
		{
			return 1;
		}
		else if (((14 * scatterMult) + (40 * chaseMult)) < t && t <= ((19 * scatterMult) + (40 * chaseMult))) 
		{
			return 2;
		}
		else if (((19 * scatterMult) + (60 * chaseMult)) < t && t <= ((24 * scatterMult) + (60 * chaseMult)))
		{
			return 2;
		}
		//chase if's
		else if ((7 * scatterMult) < t && t <= ((7 * scatterMult) + (20 * chaseMult))) 
		{
			return 3;
		}
		else if (((14 * scatterMult) + (20 * chaseMult)) < t && t <= ((14 * scatterMult) + (40 * chaseMult)))
		{
			return 3;
		}
		else if (((19 * scatterMult) + (40 * chaseMult)) < t && t <= ((19 * scatterMult) + (60 * chaseMult)))
		{
			return 3;
		}
		else return 4;
	}
	
	
	//old blink scatter behavior 
	/*
	//cruise elroy mode
	if (g.getNumberOfActivePills() == pillsTillElroy)
	{
		//add going to normal if pacman is eaten during elroy mode until all ghosts are out 
		if (hasPacmanDied(g, hasPacmanDiedVar)) 
		{
			//normal scatter behavior 
			System.out.println("this is working yay");
			myMoves.put(ghost, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), blinkyScatterNode, g.getGhostLastMoveMade(ghost), DM.EUCLID));
			gv.addPoints(g, Color.RED, blinkyScatterNode);
			//speed decrease here
		}
		
		int pacManCurrentPos = g.getPacmanCurrentNodeIndex();
		gv.addPoints(g, Color.RED, pacManCurrentPos);
		myMoves.put(ghost, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), pacManCurrentPos, g.getGhostLastMoveMade(ghost), DM.EUCLID));
		//speed increase goes here
		
	} else  
	{
		//normal scatter mode
		myMoves.put(ghost, g.getApproximateNextMoveTowardsTarget(g.getGhostCurrentNodeIndex(ghost), blinkyScatterNode, g.getGhostLastMoveMade(ghost), DM.EUCLID));
		gv.addPoints(g, Color.RED, blinkyScatterNode);
	}
	*/
}