package jix.simplegame.Pathing;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import jix.simplegame.DrawnLevel;
import jix.simplegame.DrawnMazeNode;
import jix.simplegame.Maze.MazeNode;

public class PathGrid 
{
	public PathingNode[][] nodes;
	
	int xN;
	int yN;
	
	public PathGrid(int xN, int yN, DrawnLevel l)
	{
		this.xN = xN;
		this.yN = yN;
		
		nodes = new PathingNode[xN][yN];
		
		for (int x = 0; x < xN; x++)
		{
			for (int y = 0; y < yN; y++)
			{
				nodes[x][y] = new PathingNode(l.data[x][y]);
				nodes[x][y].x = x;
				nodes[x][y].y = y;
			}
		}
		
		connectGrid();
	}
	
	public void connectGrid()
	{
		for (int x = 0; x < xN; x++)
		{
			for (int y = 0; y < yN; y++)
			{
				PathingNode p = nodes[x][y];
				connectNeighbours(p);
			}
		}
	}
	
	public void connectNeighbours(PathingNode p)
	{
		//It's a wall, not connected to anything
		if (p.originalNode.type == 1)
			return;
		
		PathingNode left = nodes[p.x-1][p.y];
		PathingNode topLeft = nodes[p.x-1][p.y-1];
		PathingNode top = nodes[p.x][p.y-1];
		PathingNode topRight = nodes[p.x+1][p.y-1];
		PathingNode right = nodes[p.x+1][p.y];
		PathingNode bottomRight = nodes[p.x+1][p.y+1];
		PathingNode bottom = nodes[p.x][p.y+1];
		PathingNode bottomLeft = nodes[p.x-1][p.y+1];
		
		if (checkNeighbors(p, left))
			p.left = left;
		
		if (checkNeighbors(p, left, topLeft, top))
			p.topLeft = topLeft;
		
		if(checkNeighbors(p, top))
			p.top = top;
		
		if(checkNeighbors(p, top, topRight, right))
			p.topRight = topRight;
		
		if(checkNeighbors(p, right))
			p.right = right;
		
		if(checkNeighbors(p, right, bottomRight, bottom))
			p.bottomRight = bottomRight;
		
		if(checkNeighbors(p, bottom))
			p.bottom = bottom;
		
		if(checkNeighbors(p, bottom, bottomLeft, left))
			p.bottomLeft = bottomLeft;
	}
	
	public boolean checkNeighbors(PathingNode thisNode, PathingNode...nodes)
	{
		for(PathingNode p : nodes)
		{
			if (p == null || p.originalNode.type == 1)
				return false;
		}
		
		return true;
	}
	
	public void prepareForPath(PathingNode endPoint)
	{
		for (int x = 0; x < xN; x++)
		{
			for (int y = 0; y < yN; y++)
			{
				PathingNode p = nodes[x][y];
				p.GN = 10000;
				p.HN = Math.abs(endPoint.x - p.x) + Math.abs(endPoint.y - p.y);
				p.explored = false;
				p.prevNode = null;
			}
		}
	}
	
	public ArrayList<PathingNode> pathTo(Vector2 a, Vector2 b)
	{
		int xStart = (int)(a.x / 8);
		int yStart = (int)(a.y / 8);
		
		int xEnd = (int)(b.x / 8);
		int yEnd = (int)(b.y / 8);
		
		return pathTo(nodes[xStart][yStart], nodes[xEnd][yEnd]);
	}
	
	public ArrayList<PathingNode> pathTo(PathingNode start, PathingNode end)
	{
		ArrayList<PathingNode> result = new ArrayList<PathingNode>();
		
		ArrayList<PathingNode> openList = new ArrayList<PathingNode>();
		ArrayList<PathingNode> closedList = new ArrayList<PathingNode>();
		
		PathingNode current = start;
		current.GN = 0;
		
		openList.add(current);
		
		int i = 0;
		while(openList.size() != 0)
		{
			current = getShortestFN(openList);
			
			if (current == end)
				break;
			
			openList.remove(current);
			closedList.add(current);
			
			ArrayList<PathingNode> neighbours = current.getConnectedNeighbours();
			for (PathingNode p : neighbours)
			{				
				if (closedList.contains(p))
					continue;
				
				if (!openList.contains(p))
				{
					openList.add(p);
					p.GN = current.GN + current.getGNFromP(p);
					p.prevNode = current;
				}
				
				float GNfromP = current.getGNFromP(p);
				//if this neighbour's GN is less than the current GN + the GN from the current
				if ((current.GN + GNfromP) < p.GN)
				{
					p.GN = current.GN + current.getGNFromP(p);
					p.prevNode = current;
				}
			}
		}
		
		if (current != end)
			return null;
		
		else
		{
			result = new ArrayList<PathingNode>();
			PathingNode m = end;
			result.add(m);
			
			while(m.prevNode != null)
			{
				m = m.prevNode;
				result.add(m);
				
				if (m.GN == 0)
					break;
			}
			return result;
		}
	}
	
	public PathingNode getShortestFN(ArrayList<PathingNode> array)
	{
		float FN = 10000000;
		PathingNode result = null;
		for (PathingNode m : array)
		{
			if ((m.GN + m.HN) < FN)
			{
				FN = m.GN + m.HN;
				result = m;
			}
		}
		return result;
	}
	
	public void drawDebug(SpriteBatch batch, Texture t)
	{
		for (int x = 0; x < xN; x++)
		{
			for (int y = 0; y < yN; y++)
			{
				PathingNode p = nodes[x][y];
				if (p.getDiagonal())
				{
					batch.draw(t, p.x * 8 + 4, p.y * 8 + 4, 2, 2);
				}
			}
		}
	}
}
