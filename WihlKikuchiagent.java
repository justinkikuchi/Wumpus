package Agents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import BotEnvironment.SearchBot.*;

public class WihlKikuchiagent extends WumpusAgent {
	private Node node;
	private SmartNode[][] map = new SmartNode[100][100];
	private int steps = 0;
	private int x;
	private int y;
	private int risk = 0;

	public WihlKikuchiagent() {
		super();
		setDeveloperName("JUSTIN BRIAN");
	}

	public void step() {
		// start condition
		if (steps == 0) {
			node = getCurrentNode();
			x = node.getX();
			y = node.getY();
			map[x][y] = new SmartNode(-1, -1, x, y, 0, 1, false);
		} else {
			if (map[x][y] == null) { // on a node that hasnt been created, maybe
										// never reach this?
				map[x][y] = new SmartNode(x, y, x, y, 0, 0, false);
				System.out.println("weird situiation");
			}
			// call functions to check surrounding areas
			// create new nodes or update exisiting ones with new information
			boolean pitStatus = nearPit();
			if (pitStatus && map[x][y].visited == 0) {
				updateNeighbors(x, y);
			} else if (!pitStatus && map[x][y].visited == 0) {
				map[x][y].notPit();
				// move to a safe one
			} else if (!pitStatus && map[x][y].visited > 0) {
				// no pit, already been here
			} else if (pitStatus && map[x][y].visited > 0) {
				// pit, already been here
			}

		}
		// moveForward();
	}

	public int move() {
		int lowRisk = 0;
		int lowVisted = 0;
		boolean allVisitedSame = true;
		int dirOfGoal;

		ArrayList<SmartNode> possible = new ArrayList<SmartNode>();
		possible.add(map[x][y + 1]);
		possible.add(map[x][y - 1]);
		possible.add(map[x - 1][y]);
		possible.add(map[x + 1][y]);

		Collections.sort(possible, new pitPosComp());
		lowRisk = possible.get(possible.size()).pitPos;
		for (int i = 0; i < possible.size(); i++) {
			if (possible.get(i).pitPos > 0 && possible.get(i).pitPos < risk) {
				if (possible.get(i).pitPos <= lowRisk) {
					lowRisk = possible.get(i).pitPos;
				} else {
					possible.remove(i);
				}
			} else if (possible.get(i).pitPos > 0) {
				possible.remove(i);
			}
		}

		Collections.sort(possible, new visitComp());
		int lowVisit = possible.get(possible.size()).visited;
		for (int i = 0; i < possible.size(); i++) {
			if (possible.get(i).visited <= lowVisit) {
				lowVisit = possible.get(i).visited;
			} else {
				allVisitedSame = false;
				possible.remove(i);
			}
		}
		
		dirOfGoal = getDirectionOfGoal();
		if(allVisitedSame){
			risk++;
		}
		
		if(dirOfGoal == EAST){
			if(possible.contains(map[x+1][y])){
				return stepEast();
			}
		}
		if(dirOfGoal == WEST){
			if(possible.contains(map[x+1][y])){
				return stepWest();
			}
		}
		if(dirOfGoal == NORTH){
			if(possible.contains(map[x+1][y])){
				return stepNorth();
			}
		}
		if(dirOfGoal == SOUTH){
			if(possible.contains(map[x+1][y])){
				return stepSouth();
			}
		}
		
		if(possible.get(0).x == x-1){
			return stepWest();
		}
		if(possible.get(0).x == x+1){
			return stepEastt();
		}
		if(possible.get(0).y == y-1){
			return stepSouth();
		}
		if(possible.get(0).y == y+1){
			return stepNorth();
		}
		

		return 0;
	}

	public void reset() {
		super.reset();
	}

	private void updateNeighbors(int x, int y) {
		boolean north = false;
		boolean south = false;
		boolean east = false;
		boolean west = false;
		if (map[x - 1][y] == null) {
			map[x - 1][y] = new SmartNode(x, y, x - 1, y, 0, 0, false);
		}
		if (map[x + 1][y] == null) {
			map[x + 1][y] = new SmartNode(x, y, x + 1, y, 0, 0, false);
		}
		if (map[x][y - 1] == null) {
			map[x][y - 1] = new SmartNode(x, y, x, y - 1, 0, 0, false);
		}
		if (map[x][y + 1] == null) {
			map[x][y + 1] = new SmartNode(x, y, x, y + 1, 0, 0, false);
		}
		if (map[x + 1][y].visited > 0) {
			east = true;
		}
		if (map[x - 1][y].visited > 0) {
			west = true;
		}
		if (map[x][y - 1].visited > 0) {
			south = true;
		}
		if (map[x][y + 1].visited > 0) {
			north = true;
		}
		if (!east) {
			if (!west)
				map[x + 1][y].dependencies.add(map[x - 1][y]);
			else
				map[x + 1][y].pitPos++;
			if (!south)
				map[x + 1][y].dependencies.add(map[x][y - 1]);
			else
				map[x + 1][y].pitPos++;
			if (!north)
				map[x + 1][y].dependencies.add(map[x][y + 1]);
			else
				map[x + 1][y].pitPos++;
		}
		if (!west) {
			if (!east)
				map[x - 1][y].dependencies.add(map[x + 1][y]);
			else
				map[x - 1][y].pitPos++;
			if (!south)
				map[x - 1][y].dependencies.add(map[x][y - 1]);
			else
				map[x - 1][y].pitPos++;
			if (!north)
				map[x - 1][y].dependencies.add(map[x][y + 1]);
			else
				map[x - 1][y].pitPos++;
		}
		if (!north) {
			if (!west)
				map[x][y + 1].dependencies.add(map[x - 1][y]);
			else
				map[x][y + 1].pitPos++;
			if (!east)
				map[x][y + 1].dependencies.add(map[x + 1][y]);
			else
				map[x][y + 1].pitPos++;
			if (!south)
				map[x][y + 1].dependencies.add(map[x][y - 1]);
			else
				map[x][y + 1].pitPos++;
		}
		if (!south) {
			if (!west)
				map[x][y - 1].dependencies.add(map[x - 1][y]);
			else
				map[x][y - 1].pitPos++;
			if (!east)
				map[x][y - 1].dependencies.add(map[x + 1][y]);
			else
				map[x][y - 1].pitPos++;
			if (!north)
				map[x][y - 1].dependencies.add(map[x][y + 1]);
			else
				map[x][y - 1].pitPos++;
		}
	}

	private int stepEast() {
		int status = moveEast();
		if (status == SAFE || status == HURT) {
			x++;
			if (map[x][y] == null) {
				map[x][y] = new SmartNode(x - 1, y, x, y, -1, 1, false);
			} else {
				SmartNode node = map[x][y];
				node.pitPos = -1;
				node.visited++;
				node.preX = x - 1;
				node.preY = y;
			}
		} else if (status == HIT_WALL) {
			if (map[x + 1][y] == null) {
				map[x + 1][y] = new SmartNode(x, y, x + 1, y, -1, 1, true);
			}
		}
		return status;
	}

	private int stepWest() {
		int status = moveWest();
		if (status == SAFE || status == HURT) {
			x--;
			if (map[x][y] == null) {
				map[x][y] = new SmartNode(x + 1, y, x, y, -1, 1, true);
			} else {
				SmartNode node = map[x][y];
				node.pitPos = -1;
				node.visited++;
				node.preX = x + 1;
				node.preY = y;
			}
		} else if (status == HIT_WALL) {
			if (map[x - 1][y] == null) {
				map[x - 1][y] = new SmartNode(x, y, x - 1, y, -1, 1, true);
			}
		}
		return status;
	}

	private int stepNorth() {
		int status = moveEast();
		if (status == SAFE || status == HURT) {
			y++;
			if (map[x][y] == null) {
				map[x][y] = new SmartNode(x, y - 1, x, y, -1, 1, true);
			} else {
				SmartNode node = map[x][y];
				node.pitPos = -1;
				node.visited++;
				node.preX = x;
				node.preY = y - 1;
			}
		} else if (status == HIT_WALL) {
			if (map[x][y + 1] == null) {
				map[x][y + 1] = new SmartNode(x, y, x, y + 1, -1, 1, true);
			}
		}
		return status;
	}

	private int stepSouth() {
		int status = moveEast();
		if (status == SAFE || status == HURT) {
			y--;
			if (map[x][y] == null) {
				map[x][y] = new SmartNode(x, y + 1, x, y, -1, 1, true);
			} else {
				SmartNode node = map[x][y];
				node.pitPos = -1;
				node.visited++;
				node.preX = x;
				node.preY = y + 1;
			}
		} else if (status == HIT_WALL) {
			if (map[x][y - 1] == null) {
				map[x][y - 1] = new SmartNode(x, y, x, y - 1, -1, 1, true);
			}
		}
		return status;
	}
}

class pitPosComp implements Comparator {
	public int compare(Object n1, Object n2) {
		int n1Pos = ((SmartNode) n1).pitPos;
		int n2Pos = ((SmartNode) n2).pitPos;
		if (n1Pos > n2Pos) {
			return 1;
		} else if (n1Pos < n2Pos) {
			return -1;
		} else {
			return 0;
		}
	}
}

class visitComp implements Comparator {
	public int compare(Object n1, Object n2) {
		int n1Visit = ((SmartNode) n1).visited;
		int n2Visit = ((SmartNode) n2).visited;
		if (n1Visit > n2Visit) {
			return 1;
		} else if (n1Visit < n2Visit) {
			return -1;
		} else {
			return 0;
		}
	}
}

class distComp implements Comparator {
	public int compare(Object n1, Object n2) {
		int n1Dist = ((SmartNode) n1).distToGoal;
		int n2Dist = ((SmartNode) n2).distToGoal;
		if (n1Dist > n2Dist) {
			return 1;
		} else if (n1Dist < n2Dist) {
			return -1;
		} else {
			return 0;
		}
	}
}

class SmartNode {
	public int pitPos;
	public int visited;
	public boolean isWall;
	public int preX;
	public int preY;
	public int x;
	public int y;
	public int distToGoal;
	public ArrayList<SmartNode> dependencies;

	public SmartNode(int preX, int preY, int x, int y,
			int pitPos, int visited, boolean isWall) {
		this.preX = preX;
		this.preY = preY;
		this.x = x;
		this.y = y;;
		this.pitPos = pitPos;
		this.visited = visited;
		this.isWall = isWall;
	}

	public void clearDependency(SmartNode smartNode) {
		if (!dependencies.remove(smartNode))
			System.out.println("didnt not find it");
		pitPos++;
	}

	public void notPit() {
		for (int i = 0; i < dependencies.size(); i++) {
			SmartNode node = dependencies.get(i);
			node.clearDependency(this);
		}
	}
}
