package Agents;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeMap;

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
			map[x][y] = new SmartNode(-1, -1, 0, 1, false);
		} else {
			if (map[x][y] == null) { // on a node that hasnt been created, maybe
										// never reach this?
				map[x][y] = new SmartNode(x, y, 0, 0, false);
				System.out.println("weird situiation");
			}
			// call functions to check surrounding areas
			// create new nodes or update exisiting ones with new information
			boolean pitStatus = nearPit();
			if (pitStatus && map[x][y].visited == 0) {
				updateNeighbors(x,y);
			}
			else if(!pitStatus && map[x][y].visited == 0){
				map[x][y].notPit();
				//move to a safe one
			}
			else if(!pitStatus && map[x][y].visited > 0){
				//no pit, already been here
			}
			else if(pitStatus && map[x][y].visited > 0){
				//pit, already been here
			}
			
			
			
		}
		//moveForward();
	}
	
	public int move(){
		ArrayList<Integer> possible = new ArrayList<Integer>();

		TreeMap<Integer, SmartNode> pit = new TreeMap<Integer, SmartNode>();
		pit.put(new Integer(map[x][y+1].pitPos), map[x][y+1]);
		pit.put(new Integer(map[x][y-1].pitPos), map[x][y-1]);
		pit.put(new Integer(map[x+1][y].pitPos), map[x+1][y]);
		pit.put(new Integer(map[x-1][y].pitPos), map[x-1][y]);
		
		TreeMap<Integer, SmartNode> visit = new TreeMap<Integer, SmartNode>();
		visit.put(new Integer(map[x][y+1].visited), map[x][y+1]);
		visit.put(new Integer(map[x][y-1].visited), map[x][y-1]);
		visit.put(new Integer(map[x+1][y].visited), map[x+1][y]);
		visit.put(new Integer(map[x-1][y].visited), map[x-1][y]);
		
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
			map[x - 1][y] = new SmartNode(x, y, 0, 0, false);
		}
		if (map[x + 1][y] == null) {
			map[x + 1][y] = new SmartNode(x, y, 0, 0, false);
		}
		if (map[x][y - 1] == null) {
			map[x][y - 1] = new SmartNode(x, y, 0, 0, false);
		}
		if (map[x][y + 1] == null) {
			map[x][y + 1] = new SmartNode(x, y, 0, 0, false);
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
				map[x][y] = new SmartNode(x - 1, y, -1, 1, false);
			} else {
				SmartNode node = map[x][y];
				node.pitPos = -1;
				node.visited++;
				node.preX = x - 1;
				node.preY = y;
			}
		} else if (status == HIT_WALL) {
			if (map[x + 1][y] == null) {
				map[x + 1][y] = new SmartNode(x, y, -1, 1, true);
			}
		}
		return status;
	}

	private int stepWest() {
		int status = moveWest();
		if (status == SAFE || status == HURT) {
			x--;
			if (map[x][y] == null) {
				map[x][y] = new SmartNode(x + 1, y, -1, 1, true);
			} else {
				SmartNode node = map[x][y];
				node.pitPos = -1;
				node.visited++;
				node.preX = x + 1;
				node.preY = y;
			}
		} else if (status == HIT_WALL) {
			if (map[x - 1][y] == null) {
				map[x - 1][y] = new SmartNode(x, y, -1, 1, true);
			}
		}
		return status;
	}

	private int stepNorth() {
		int status = moveEast();
		if (status == SAFE || status == HURT) {
			y++;
			if (map[x][y] == null) {
				map[x][y] = new SmartNode(x, y - 1, -1, 1, true);
			} else {
				SmartNode node = map[x][y];
				node.pitPos = -1;
				node.visited++;
				node.preX = x;
				node.preY = y - 1;
			}
		} else if (status == HIT_WALL) {
			if (map[x][y + 1] == null) {
				map[x][y + 1] = new SmartNode(x, y, -1, 1, true);
			}
		}
		return status;
	}

	private int stepSouth() {
		int status = moveEast();
		if (status == SAFE || status == HURT) {
			y--;
			if (map[x][y] == null) {
				map[x][y] = new SmartNode(x, y + 1, -1, 1, true);
			} else {
				SmartNode node = map[x][y];
				node.pitPos = -1;
				node.visited++;
				node.preX = x;
				node.preY = y + 1;
			}
		} else if (status == HIT_WALL) {
			if (map[x][y - 1] == null) {
				map[x][y - 1] = new SmartNode(x, y, -1, 1, true);
			}
		}
		return status;
	}
}
class pitPosComp implements Comparator{
	public int compare(Object n1, Object n2){
		int n1Pos = ((SmartNode)n1).pitPos;
		int n2Pos = ((SmartNode)n2).pitPos;
		if(n1Pos > n2Pos){
			return 1;
		}
		else if(n1Pos < n2Pos){
			return -1;
		}
		else{
			return 0;
		}
	}
}
class visitComp implements Comparator{
	public int compare(Object n1, Object n2){
		int n1Visit = ((SmartNode)n1).visited;
		int n2Visit = ((SmartNode)n2).visited;
		if(n1Visit > n2Visit){
			return 1;
		}
		else if(n1Visit < n2Visit){
			return -1;
		}
		else{
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
	public ArrayList<SmartNode> dependencies;

	public SmartNode(int preX, int preY, int pitPos, int visited,
			boolean isWall) {
		this.preX = preX;
		this.preY = preY;
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