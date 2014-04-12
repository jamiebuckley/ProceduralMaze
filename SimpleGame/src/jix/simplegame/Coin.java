package jix.simplegame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Coin 
{
	Texture texture;
	Vector2 position;
	
	boolean isDead = false;
	
	float width = 3.0f;
	float height = 3.0f;
	
	public Coin(Texture t, Vector2 position)
	{
		this.texture = t;
		this.position = position;
	}
	
	public void update(float delta, Ship s)
	{
		if (this.getRect().overlaps(s.getRect()))
		{
			this.isDead = true;
			s.score += 10;
		}
	}
	
	public void draw(SpriteBatch batch)
	{
		batch.draw(texture, position.x,  position.y, width, height);
	}
	
	public Rectangle getRect()
	{
		return new Rectangle(this.position.x, this.position.y, width, height);
	}
}
