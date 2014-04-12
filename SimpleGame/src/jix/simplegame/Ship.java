package jix.simplegame;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Ship 
{
	Texture t;
	Vector2 position;
	Vector2 speed;
	
	boolean left;
	boolean up;
	boolean right;
	boolean down;
	
	public float width = 2.0f;
	public float height = 2.0f;

	float mvspeed = 4.0f;
	float maxSpeed = 40.0f;
	int score = 0;
	
	boolean won = false;
	
	public float health = 50;
	public boolean isDead = false;
	
	public Vector2 facing = new Vector2();
	
	public Ship(Texture t)
	{
		this.t = t;
		position = new Vector2();
		speed = new Vector2();
	}
	
	public void takeDamage(float damage)
	{
		this.health -= damage;
		if (health < 0)
		{
			this.isDead = true;
			System.out.println("You dead");
		}
	}
	
	public void getKeys()
	{
		left = Gdx.input.isKeyPressed(Keys.LEFT);
		right = Gdx.input.isKeyPressed(Keys.RIGHT);
		
		up = Gdx.input.isKeyPressed(Keys.UP);
		down = Gdx.input.isKeyPressed(Keys.DOWN);
	}
	
	public void updateSpeed(float delta)
	{
		if (up && !down && speed.y < maxSpeed)
			speed.y += mvspeed * delta;
		
		if (down && !up && speed.y > -maxSpeed)
			speed.y += -mvspeed * delta;
		
		if (left && !right && speed.x > -maxSpeed)
			speed.x += -mvspeed * delta;
		
		if (right && !left && speed.x < maxSpeed)
			speed.x += mvspeed * delta;
		
		//if (up && down)
			//speed.y = 0.0f;
		
		//if (left && right)
			//speed.x = 0.0f;
		
		speed.x *= 0.9f;
		speed.y *= 0.9f;
		
		if (!left && !right)
		{
			//speed.x = 0;
			if (Math.abs(speed.x) < 0.01f)
				speed.x = 0;
		}
		
		if (!up && !down)
		{
			//speed.y = 0;
			if (Math.abs(speed.y) < 0.01f)
				speed.y = 0;
		}
	}
	
	public void update(float delta, DrawnLevel level)
	{
		getKeys();
		updateSpeed(delta);
		
		
		if (this.getRect().overlaps(level.endRect))
		{
			won = true;
		}
		
		float minX = (speed.x < 0)? position.x + speed.x : position.x;
		float maxX = position.x + speed.x + width;
		float minY = position.y;
		float maxY = position.y + height;
		
		ArrayList<CollisionObject> rects = level.getRects(minX, maxX, minY, maxY);
		
		for (CollisionObject c : rects)
		{
			if (c.type == 4)
			{
				//level.data[c.x][c.y] = 0;
				//score += 10;
			}
		}
		
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
		
		if (speed.x > 0 || speed.y > 0)
		{
			facing.x = speed.x;
			facing.y = speed.y;
			facing.nor();
		}
	}
	
	public void draw(SpriteBatch batch)
	{
		batch.draw(t, position.x, position.y, width, height);
	}
	
	public Rectangle getRect()
	{
		return new Rectangle(position.x, position.y, width, height);
	}
}
