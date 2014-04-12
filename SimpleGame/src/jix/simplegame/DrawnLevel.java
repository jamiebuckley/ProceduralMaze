package jix.simplegame;

import java.util.ArrayList;

import jix.simplegame.Maze.Level;
import jix.simplegame.Maze.MazeNode;
import jix.simplegame.Pathing.PathGrid;
import jix.simplegame.Pathing.PathingNode;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class DrawnLevel 
{
	int xNum;
	int yNum;
	
	public DrawnMazeNode[][] data;
	
	public PathGrid pathGrid;
	
	Texture wallTexture;
	Texture floorTexture;
	Texture endTexture;
	Texture coinTexture;
	Texture enemyTexture;
	Rectangle endRect;
	Level level;
	
	ArrayList<Enemy> enemyList = new ArrayList<Enemy>();
	ArrayList<Coin> coinList = new ArrayList<Coin>();
	
	Ship ship;
	
	
	
	ArrayList<PathingNode> path;
	
	public DrawnLevel(Level l, Texture floorTex, Texture wallTex, Texture endTex, Texture coinTex, Texture enemyTex, Ship ship)
	{
		level = l;
		
		xNum = l.xN * 3;
		yNum = l.yN * 3;
		
		data = new DrawnMazeNode[xNum][yNum];
		
		wallTexture = wallTex;
		floorTexture = floorTex;
		endTexture = endTex;
		coinTexture = coinTex;
		enemyTexture = enemyTex;
		
		this.ship = ship;
		
		for (int x = 0; x < xNum; x+= 3)
		{
			for (int y = 0; y < yNum; y+=3)
			{
				MazeNode n = l.data[x/3][y/3];
				
				if (n.hasEnemy > 0)
				{
					Enemy enemy = new Enemy(enemyTex, new Vector2(x * 8 + 4, y * 8 + 4), this);
					enemyList.add(enemy);
				}
				
				if (n.hasCoin > 0)
				{
					Coin coin = new Coin(coinTexture, new Vector2(x * 8 + 4, y * 8 + 4));
					coinList.add(coin);
				}
				
				/* if its the endpoint create the end collision rectangle */
				if (n.endPoint)
				{
					endRect = new Rectangle(x * 8 +8, y * 8 + 8, 8, 8);
				}
				
				/* If the nodetyle is a room */
				if (n.nodeType == 3)
				{
					for (int xP = x; xP < x + 3; xP++)
					{
						for (int yP = y; yP < y + 3; yP++)
						{
							data[xP][yP] = new DrawnMazeNode(floorTex, xP, yP, 3);
						}
					}
					continue;
				}
				
				/* if it's a regular maze corridor */
				//top layer
			    data[x][y] = new DrawnMazeNode(wallTex, x, y, 1);
				data[x+1][y] = (n.walls[3] == 1)? new DrawnMazeNode(wallTex, x+1, y, 1) : new DrawnMazeNode(floorTex, x+1, y, n.nodeType);
				data[x+2][y] = new DrawnMazeNode(wallTex, x+2, y, 1);
				
				//middle layer
				data[x][y+1] = (n.walls[0] == 1)? new DrawnMazeNode(wallTex, x, y+1, 1) : new DrawnMazeNode(floorTex, x, y+1, n.nodeType);
				data[x+1][y+1] = new DrawnMazeNode(floorTex, x+1, y+1, n.nodeType);
				if (n.nodeType == 5)
					data[x+1][y+1] = new DrawnMazeNode(coinTex, x+1, y+1, n.nodeType);
				data[x+2][y+1] = (n.walls[2] == 1)? new DrawnMazeNode(wallTex, x+2, y+1, 1) : new DrawnMazeNode(floorTex, x+2, y+1, n.nodeType);
				
				//bottom layer
				data[x][y+2] = new DrawnMazeNode(wallTex, x, y+2, 1);
				data[x+1][y+2] = (n.walls[1] == 1)? new DrawnMazeNode(wallTex, x+1, y+2, 1) : new DrawnMazeNode(floorTex, x+1, y+2, n.nodeType);
				data[x+2][y+2] = new DrawnMazeNode(wallTex, x+2, y+2, 1);
			}
		}
		
		pathGrid = new PathGrid(xNum, yNum, this);
		path = pathGrid.pathTo(pathGrid.nodes[1][1], pathGrid.nodes[xNum-2][yNum-2]);
	}
	
	/**
	 * 
	 * @param minx The minimum x position of the rectangle
	 * @param maxx The maximum x position of the rectangle
	 * @param miny The minimum y position of the rectangle
	 * @param maxy The maximum y position of the rectangle
	 * @return
	 */
	public ArrayList<CollisionObject> getRects(float minx, float maxx, float miny, float maxy)
	{
		int blockXMin = (int)(minx/8);
		int blockXMax = (int)(maxx/8);
		int blockYMin = (int)(miny/8);
		int blockYMax = (int)(maxy/8);
		
		ArrayList<CollisionObject> result = new ArrayList<CollisionObject>();
		
	//	System.out.println(blockXMin + " " + blockXMax + " " + blockYMin + " " + blockYMax);
		
		if (blockXMin < 0 && blockXMax < 0)
			return result;
		
		if (blockYMin < 0 && blockYMax < 0)
			return result;
		
		if (blockXMin >= xNum && blockXMax >= xNum)
			return result;
		
		if (blockYMin >= xNum && blockYMax >= yNum)
			return result;
		
		
		blockXMin = (blockXMin < 0)? 0 : blockXMin;
		blockYMin = (blockYMin < 0)? 0 : blockYMin;
		
		blockYMin = (blockYMin > yNum-1)? yNum-1 : blockYMin;
		blockXMax = (blockXMax > xNum-1)? xNum-1 : blockXMax;
		blockYMax = (blockYMax > yNum-1)? yNum-1 : blockYMax;
		
		//System.out.println(blockXMin + " " + blockXMax + " " + blockYMin + " " + blockYMax);
		
		for (int x = blockXMin; x <= blockXMax; x++)
		{
			for (int y = blockYMin; y <= blockYMax; y++)
			{
				if (data[x][y] == null) 
					continue;
				
				Rectangle rect = new Rectangle(x * 8, y * 8, 8, 8);
				CollisionObject c = new CollisionObject();
				c.rectangle = rect;
				c.type = data[x][y].type;
				c.x = x;
				c.y = y;
				result.add(c);
			}
		}
		
		return result;
	}
	
	public void update(float delta)
	{
		for(Enemy e : enemyList)
			e.update(this, ship);
		
		ArrayList<Coin> removeList = new ArrayList<Coin>();
		for(Coin c : coinList)
		{
			c.update(delta, ship);
			if (c.isDead)
				removeList.add(c);
		}
		
		for (Coin c : removeList)
		{
			coinList.remove(c);
		}
	}
	
	public void draw(SpriteBatch batch)
	{
		for (int x = 0; x < xNum; x++)
		{
			for (int y = yNum-1; y >=0; y--)
			{
				if (data[x][y] == null)
					continue;
				
				data[x][y].draw(batch);
			}
		}
		
		batch.draw(endTexture, endRect.x, endRect.y, endRect.width, endRect.height);
		
		for(Enemy e : enemyList)
			e.draw(batch);
		
		for(Coin c : coinList)
			c.draw(batch);
	}
}
