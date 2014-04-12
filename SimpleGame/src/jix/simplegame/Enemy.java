package jix.simplegame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import jix.simplegame.Maze.Level;
import jix.simplegame.Maze.MazeNode;
import jix.simplegame.Pathing.PathingNode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy 
{
	Texture texture;
	Vector2 position;
	Vector2 startPosition;
	Vector2 speed;
	
	float width = 3;
	float height = 3;
	
	DrawnLevel level;
	Stack<PathingNode> wayPoints;
	
	float chaseTimer = 0;
	boolean followingEnemy = false;
	
	//delete this shit afterwards
	ArrayList<Vector2> list = new ArrayList<Vector2>();
	
	ArrayList<EnemyBullet> bulletList = new ArrayList<EnemyBullet>();
	
	boolean canFire = true;
	float fireTimer = 0;
	
	/* Fuck tha police */
	public Enemy(Texture t, Vector2 position, DrawnLevel l)
	{
		this.texture = t;
		this.position = position;
		startPosition = new Vector2();
		startPosition.x = position.x;
		startPosition.y = position.y;
		
		this.level = l;
		this.speed = new Vector2();
		
		wayPoints = new Stack<PathingNode>();
	}
	
	float randomTimer = 0;
	public void updateSpeed(float delta, Ship s)
	{
		Vector2 ray = rayToTarget(s.position, 10);
		
		//can see player
		if (ray == null && position.dst(s.position) < 30)
		{
			followingEnemy = true;
			chaseTimer = 0;
			
			speed.x *= 0.9f;
			speed.y *= 0.9f;
			wayPoints.clear();
			
			//fire
			
			float xDiff = s.position.x - position.x;
			float yDiff = s.position.y - position.y;
			
			if (canFire)
			{
			EnemyBullet b = new EnemyBullet(texture, new Vector2(position));
			canFire = false;
			
			b.speed = new Vector2(xDiff, yDiff);
			b.speed.nor();
			bulletList.add(b);
			}
			else
			{
				fireTimer += delta;
				if (fireTimer > 0.1f)
				{
					fireTimer = 0;
					canFire = true;
				}
			}
			
			return;
		}
		else if (ray == null && position.dst(s.position) >= 30 && position.dst(s.position) <= 50)
		{
			followingEnemy = true;
			chaseTimer = 0;
			
			float xDiff = s.position.x - position.x;
			float yDiff = s.position.y - position.y;
			speed.x = xDiff * delta;
			speed.y = yDiff * delta;
			wayPoints.clear();
		}
		
		if (ray != null || position.dst(s.position) > 50)
		{
			if (!followingEnemy)
			{
				
				if (position.dst(startPosition) < 5)
				{
					speed.x = 0;
					speed.y = 0;
					wayPoints.clear();
					
					//at home
					return;
				}
				
				if (wayPoints.size() == 0)
				{
					wayPoints.addAll(level.pathGrid.pathTo(this.position, startPosition));
				}

				Vector2 wpPos = new Vector2(wayPoints.lastElement().x * 8 + 2, wayPoints.lastElement().y * 8 + 2);
				
				float xDiff = wpPos.x - position.x;
				float yDiff = wpPos.y - position.y;
				
				if (wpPos.dst(position) < 3)
				{
					wayPoints.remove(wayPoints.lastElement());
				}
				
				speed.x = xDiff;
				speed.y = yDiff;
				speed.nor();
				
				speed.x *= 0.3f;
				speed.y *= 0.3f;

				return;
			}
			
			chaseTimer += delta;
			
			if (chaseTimer > 3)
			{
				wayPoints.clear();
				chaseTimer = 0;
				followingEnemy = false;
				return;
			}
			
			if (wayPoints == null)
				return;
			
			if (wayPoints.size() == 0)
			{
				wayPoints.addAll(level.pathGrid.pathTo(this.position, s.position));
				wayPoints.pop();
			}
			else
			{
				
				Vector2 wpPos = new Vector2(wayPoints.lastElement().x * 8 + 2, wayPoints.lastElement().y * 8 + 2);
				
				float xDiff = wpPos.x - position.x;
				float yDiff = wpPos.y - position.y;
				
				if (wpPos.dst(position) < 3)
				{
					wayPoints.remove(wayPoints.lastElement());
				}
				
				speed.x = xDiff;
				speed.y = yDiff;
				speed.nor();
				
				speed.x *= 0.3f;
				speed.y *= 0.3f;
				
				return;
			}
		}
	}
	
	public void randomSpeed()
	{
		speed.x = (float)(Gdx.graphics.getDeltaTime() * ((Math.random() * 10)-5));
		speed.y = (float)(Gdx.graphics.getDeltaTime() * ((Math.random() * 10)-5));
	}
	
	public void update(DrawnLevel level, Ship s)
	{
		
		updateSpeed(Gdx.graphics.getDeltaTime(), s);
		
		
		float minX = (speed.x < 0)? position.x + speed.x : position.x;
		float maxX = position.x + speed.x + width;
		float minY = position.y;
		float maxY = position.y + height;
		
		ArrayList<CollisionObject> rects = level.getRects(minX, maxX, minY, maxY);
		
		Rectangle playerXSpeedRect = new Rectangle(minX, minY, width + speed.x, height);
		
		for (CollisionObject r : rects)
		{
			if (playerXSpeedRect.overlaps(r.rectangle))
			{
				
				if (r.type != 1)
					continue;
				
				if (speed.x > 0)
				{
					this.position.x = r.rectangle.x - width;
					speed.x = 0;
				}
				else if (speed.x < 0)
				{
					this.position.x = r.rectangle.x + 8;
					speed.x = 0;
				}
			}
		}
		
		minX = position.x;
		maxX = position.x + width;
		minY = (speed.y < 0)? position.y + speed.y : position.y;
		maxY = position.y + speed.y + height;
		
		
		
		rects = level.getRects(minX, maxX, minY, maxY);
		Rectangle playerYSpeedRect = new Rectangle(minX, minY, width, (maxY - minY));
		
		for (CollisionObject r : rects)
		{
			if (playerYSpeedRect.overlaps(r.rectangle))
			{
				if (r.type != 1)
					continue;
				
				if (speed.y > 0)
				{
					this.position.y = r.rectangle.y - height;
					speed.y = 0;
				}
				else if (speed.y < 0)
				{
					this.position.y = r.rectangle.y + 8;
					speed.y = 0;
				}
			}
		}
		position.x += speed.x;
		position.y += speed.y;
		
		ArrayList<EnemyBullet> deleteList = new ArrayList<EnemyBullet>();
		for (EnemyBullet b : bulletList)
		{
			b.update(Gdx.graphics.getDeltaTime(), level, s);
			if (b.isDead)
				deleteList.add(b);
		}
		
		for (EnemyBullet b : deleteList)
		{
			bulletList.remove(b);
		}
	}
	
	public Rectangle getRect()
	{
		return new Rectangle(position.x, position.y, width, height);
	}
	
	public void draw(SpriteBatch batch)
	{
		batch.setColor(Color.WHITE);
		batch.draw(texture, position.x, position.y, width, height);	
		
		batch.setColor(Color.YELLOW);
		for (Vector2 l : list)
		{
			batch.draw(texture, l.x, l.y, width, height);
		}
		batch.setColor(Color.WHITE);
		
		for (EnemyBullet b : bulletList)
		{
			b.draw(batch);
		}
		
		list.clear();
	}
	
	//returns null if no obstructions, otherwise returns the position of the first obstruction
	public Vector2 rayToTarget(Vector2 target, int resolution)
	{
		float xDiff = target.x - position.x;
		float yDiff = target.y - position.y;
		
		float minX = (target.x < position.x)? target.x : position.x;
		float minY = (target.y < position.y)? target.y : position.y;
		
		float maxX = (target.x > position.x)? target.x + 2.0f : position.x + width;
		float maxY = (target.y > position.y)? target.y + 2.0f : position.y + width;
		
		ArrayList<CollisionObject> colls = level.getRects(minX, maxX, minY, maxY);
		
		for(int i = 1; i < resolution * 0.98f; i++)
		{
			float thisX = position.x + i * (xDiff / resolution);
			float thisY = position.y + i * (yDiff / resolution);
			
			Rectangle thisRect = new Rectangle(thisX, thisY, width, height);
			//list.add(new Vector2(thisX, thisY));
			//System.out.println(colls.size());
			for (CollisionObject c : colls)
			{
				if (c.type != 1)
					continue;
				
				if (thisRect.overlaps(c.rectangle))
				{
					return new Vector2(thisX, thisY);
				}
			}
		}
		
		return null;
	}
}
