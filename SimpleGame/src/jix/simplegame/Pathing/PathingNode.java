package jix.simplegame.Pathing;

import java.util.ArrayList;

import jix.simplegame.DrawnLevel;
import jix.simplegame.DrawnMazeNode;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;

public class PathingNode 
{	
	
	DrawnMazeNode originalNode;
	
	/* A star variables */
	public boolean explored = false;
	public float HN;
	public float GN;
	public float tentativeGN;
	public PathingNode prevNode;
	
	public boolean traversable = false;
	
	public int x;
	public int y;
	
	PathingNode left;
	PathingNode topLeft;
	PathingNode top;
	PathingNode topRight;
	PathingNode right;
	PathingNode bottomRight;
	PathingNode bottom;
	PathingNode bottomLeft;
	
	DrawnLevel l;
	
	public PathingNode(DrawnMazeNode n)
	{
		this.originalNode = n;
	}
	
	public ArrayList<PathingNode> getConnectedNeighbours()
	{
		
		PathingNode[] allNodes = {left, topLeft, top, topRight, right, bottomRight, bottom, bottomLeft};
		ArrayList<PathingNode> result = new ArrayList<PathingNode>();
		
		for (PathingNode p : allNodes)
		{
			if (p != null)
			{
				result.add(p);
			}
		}
		return result;
	}
	
	public boolean getDiagonal()
	{
		if (topLeft != null || topRight != null || bottomRight != null || bottomLeft != null)
			return true;
		
		return false;
	}
	
	public float getGNFromP(PathingNode other)
	{
		if (other == topLeft || other == topRight || other == bottomRight || other == bottomLeft)
			return 1.5f;
		else if (other == top || other == left || other == right || other == bottom)
			return 1;
		else
			return -1;
	}
}
