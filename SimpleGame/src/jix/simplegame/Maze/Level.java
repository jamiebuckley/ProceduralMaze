package jix.simplegame.Maze;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Level 
{
	public MazeNode[][] data;
	
	public int xN;
	public int yN;
	int blockSize;
	Texture blockTexture;
	float ratio = 1.0f;
	
	ArrayList<MazeNode> deadEnds;
	ArrayList<MazeNode> junctions;
	MazeNode furthestNode;
	
	public Level(int sizex, int sizey, int blockSize)
	{
		data = new MazeNode[sizex][sizey];
		xN = sizex;
		yN = sizey;
		this.blockSize = blockSize;
	}
	
	public void generate()
	{
		
		/*
		 * Generate all mazenodes
		 */
		for (int x = 0; x < xN; x++)
		{
			for (int y = 0; y < yN; y++)
			{	
				data[x][y] = new MazeNode(x, y);
			}
		}
		
		
		/*
		 * Connect all nodes to their neighbours
		 */
		for (int x = 0; x < xN; x++)
		{
			for (int y = 0; y < yN; y++)
			{	
				MazeNode currentNode = data[x][y];
				
				if (x != 0) //if not left
					currentNode.leftNode = data[x-1][y];
				if (y != yN-1) //if not top
					data[x][y].topNode = data[x][y+1];
				if (x != xN-1) //if not right
					data[x][y].rightNode = data[x+1][y];
				if (y != 0) //if not bottom
					data[x][y].bottomNode = data[x][y-1];
			}
		}
		
		
		/*
		 * Create Maze 
		 */
		MazeNode startNode, currentNode;
		startNode = data[0][0]; 
		currentNode = data[0][0];
		
		ArrayList<MazeNode> linkedNodes;
		deadEnds = new ArrayList<MazeNode>();
		
		MazeNode nextNode;
		
		int distanceFromStart = 0;
		while(true)
		{		
			if (!currentNode.explored && currentNode.getFreeNeighbours().size() == 0)
			{
				deadEnds.add(currentNode);
				currentNode.distanceFromStart = distanceFromStart;
			}
			
			currentNode.explored = true;			
			linkedNodes = currentNode.getFreeNeighbours();
			
			//if there aren't any free linked nodes
			if (linkedNodes.size() == 0)
			{					
				currentNode.backtrack = true;
				currentNode = currentNode.prevNode;
				if (currentNode == startNode)
				{
					break;
				}
				distanceFromStart--;
				continue;
			}
			
			distanceFromStart++;
			//there are free linked nodes
			nextNode = currentNode.getRandomFreeNeighbour();
			nextNode.connect(currentNode);
			nextNode.prevNode = currentNode;
			currentNode = nextNode;
		}
		
		
		/* Find furthest nodes */
		int distance = 0;
		furthestNode = null;
		for (MazeNode n : deadEnds)
		{
			if (n.distanceFromStart > distance)
			{
				distance = n.distanceFromStart;
				furthestNode = n;
			}
		}
		
		if (xN > 5 && yN > 5)
		{
			createRooms(xN / 3, 3);
		}
		
		if (roomNodes.size() > 4)
		{
			generateCoins(roomNodes.size() / 2);
			generateEnemies(1);
		}
		
		furthestNode.endPoint = true;
		ArrayList<MazeNode> path = pathTo(data[0][0], furthestNode);
		System.out.println(path.size());
		
		findJunctions();
	}
	
	public void findJunctions()
	{
		junctions = new ArrayList<MazeNode>();
		for (int x = 0; x < xN; x++)
		{
			for (int y = 0; y < yN; y++)
			{
				MazeNode m = data[x][y];
				int nodeConnections = 0;
				for (int i = 0; i < 4; i++)
				{
					nodeConnections+= m.walls[i];
				}
				if (nodeConnections == 0 || nodeConnections == 1)
				{
					junctions.add(m);
				}
			}
		}
	}
	
	ArrayList<MazeNode> roomNodes;
	public void createRooms(int number, int roomSize)
	{
		roomNodes = new ArrayList<MazeNode>();
		Random rnd = new Random();
		
		for (int i = 0; i < number; i++)
		{
			int randomX = rnd.nextInt(xN-6)+3;
			int randomY = rnd.nextInt(yN-6)+3;
			
			for (int x = randomX - roomSize/2; x < randomX+roomSize/2; x++)
			{
				for (int y = randomY - roomSize/2; y < randomY+roomSize/2; y++)
				{
					if (data[x][y] == null)
						continue;
					
					MazeNode m = data[x][y];
					m.connect(m.topNode);
					m.connect(m.leftNode);
					m.connect(m.rightNode);
					m.connect(m.bottomNode);
					m.nodeType = 3;
					roomNodes.add(m);
				}
			}
		}
	}
	
	public void generateCoins(int number)
	{
		Random rnd = new Random();
		for (int i = 0; i < number; i++)
		{
			if (roomNodes.size() == 0)
			{
				break;
			}
			MazeNode m = roomNodes.get(rnd.nextInt(roomNodes.size()));
			m.hasCoin = 1;
			roomNodes.remove(m);
		}
	}
	
	
	public void generateEnemies(int number)
	{
		Random rnd = new Random();
		for (int i = 0; i < number; i++)
		{
			if (roomNodes.size() == 0)
			{
				break;
			}
			MazeNode m = roomNodes.get(rnd.nextInt(roomNodes.size()));
			m.hasEnemy = 1;
			roomNodes.remove(m);
		}
	}
	
	public ArrayList<MazeNode> pathTo(MazeNode start, MazeNode end)
	{
		//System.out.println("Finding path");
		for (int x = 0; x < xN; x++)
		{
			for (int y = 0; y < yN; y++)
			{
				MazeNode m = data[x][y];
				m.prevNode = null;
				m.HN = distanceFrom(m, end);
			}
		}
		
		MazeNode current = start;
		current.GN = 0;
		
		ArrayList<MazeNode> openSet = new ArrayList<MazeNode>();
		ArrayList<MazeNode> closedSet = new ArrayList<MazeNode>();
		openSet.add(start);
		
		while(openSet.size() != 0)
		{	
			current = getShortestFN(openSet);
			if (current == end)
			{
				break;
			}
			
			openSet.remove(current);
			closedSet.add(current);
			
			ArrayList<MazeNode> neighbours = current.getConnectedNeighbours();
			for(MazeNode n : neighbours)
			{
				if (closedSet.contains(n))
					continue;
				
				if (!openSet.contains(n))
				{
					openSet.add(n);
					n.prevNode = current;
					n.GN = current.GN + 1;
				}
				else
				{
					if (current.GN +1 < n.GN)
					{
						n.prevNode = current;
						n.GN = current.GN+1;
					}
				}
			}
		}
		
		if (current != end)
		{
			return null;
		}
		else
		{
			ArrayList<MazeNode> result = new ArrayList<MazeNode>();
			
			MazeNode m = end;
			result.add(m);
			while(m.prevNode != null)
			{
				m = m.prevNode;
				result.add(m);
			}
			return result;
		}
	}
	
	public MazeNode getMazeNode(float x, float y)
	{
		int tx = (int)(x / 24);
		int ty = (int)(y / 24);
		return data[tx][ty];
	}
	
	public MazeNode getShortestFN(ArrayList<MazeNode> array)
	{
		int FN = 10000;
		MazeNode result = null;
		for (MazeNode m : array)
		{
			if ((m.GN + m.HN) < FN)
			{
				FN = m.GN + m.HN;
				result = m;
			}
		}
		return result;
	}
	
	public int distanceFrom(MazeNode a, MazeNode b)
	{
		int xDistance = Math.abs(a.x - b.x);
		int yDistance = Math.abs(a.y - b.y);
		return xDistance + yDistance;
	}
}
