package jix.simplegame;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class EnemyBullet 
{
	Vector2 position;
	Vector2 speed;
	
	Texture texture;
	
	float width = 1;
	float height = 1;
	
	boolean isDead=false;
	
	public EnemyBullet(Texture t, Vector2 position)
	{
		texture = t;
		this.position = position;
	}
	
	public void update(float delta, DrawnLevel l, Ship s)
	{
		
		if (this.getRect().overlaps(s.getRect()))
		{
			this.isDead = true;
			s.takeDamage(1.0f);
		}
		
		ArrayList<CollisionObject> colls = l.getRects(this.position.x, this.position.x + 1, this.position.y, this.position.y+1);
		
		for (CollisionObject r : colls)
		{
			if (this.getRect().overlaps(r.rectangle))
			{
				if (r.type != 1)
					continue;
				
				this.isDead = true;
			}
		}
		
		
		position.x += speed.x;
		position.y += speed.y;
	}
	
	public void draw(SpriteBatch batch)
	{
		batch.draw(texture, position.x, position.y, 1f, 1f);
	}
	
	public Rectangle getRect()
	{
		return new Rectangle(this.position.x, this.position.y, width, height);
	}
}
