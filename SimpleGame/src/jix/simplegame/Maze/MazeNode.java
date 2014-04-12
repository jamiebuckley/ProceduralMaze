package jix.simplegame.Maze;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MazeNode 
{
	public MazeNode topNode = null;
	public MazeNode leftNode = null;
	public MazeNode rightNode = null;
	public MazeNode bottomNode = null;
	
	public MazeNode prevNode = null;
	public int FN = 1000;
	public int GN = 1000;
	public int HN = 1000;
	
	public boolean explored = false;
	public int nodeType = 0;  //0 = regular corridor, 1 = endpoint, 2 = empty room
	
	public boolean backtrack = false;
	public int distanceFromStart = 0;
	public boolean endPoint = false;
	
	public int hasCoin = -1;
	public int hasEnemy = -1;
	
	public int[] walls = {1, 1, 1, 1};
	
	int x;
	int y;
	
	public float xPos;
	public float yPos;
	
	public MazeNode(int x, int y)
	{
		this.x = x;
		this.y = y;
		
		xPos = x * 24;
		yPos = y * 24;
	}
	
	public ArrayList<MazeNode> getFreeNeighbours()
	{
		ArrayList<MazeNode> result = new ArrayList<MazeNode>();
		
		if (topNode != null && !topNode.explored)
			result.add(topNode);
		if (bottomNode != null && !bottomNode.explored)
			result.add(bottomNode);
		if (leftNode != null && !leftNode.explored)
			result.add(leftNode);
		if (rightNode != null && !rightNode.explored)
			result.add(rightNode);
		
		return result;
	}
	
	public ArrayList<MazeNode> getConnectedNeighbours()
	{
		ArrayList<MazeNode> result = new ArrayList<MazeNode>();
		
		if (topNode != null && walls[1] == 0)
			result.add(topNode);
		if (bottomNode != null && walls[3] == 0)
			result.add(bottomNode);
		if (leftNode != null && walls[0] == 0)
			result.add(leftNode);
		if (rightNode != null && walls[2] == 0)
			result.add(rightNode);
		
		return result;
	}
	
	public MazeNode getRandomFreeNeighbour()
	{
		Random rnd = new Random();
		
		ArrayList<MazeNode> freeNodes = getFreeNeighbours();

		if (freeNodes.size() == 0)
		{
			return null;
		}
		else
		{
			int next = rnd.nextInt(freeNodes.size());
			return freeNodes.get(next);
		}
	}
	
	public void connect(MazeNode other)
	{
		if (other == topNode)
		{
			other.walls[3] = 0;
			this.walls[1] = 0;
		}
		if (other == leftNode)
		{
			other.walls[2] = 0;
			this.walls[0] = 0;
		}
		if (other == rightNode)
		{
			other.walls[0] = 0;
			this.walls[2] = 0;
		}
		if (other == bottomNode)
		{
			other.walls[1] = 0;
			this.walls[3] = 0;
		}
	}
}
