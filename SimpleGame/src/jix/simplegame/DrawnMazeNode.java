package jix.simplegame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class DrawnMazeNode 
{
	Texture texture;
	int x;
	int y;
	Vector2 position;
	public int type;
	
	int Fn; //distance from endpoint
	
	public DrawnMazeNode(Texture t, int x, int y, int type)
	{
		this.texture = t;
		this.x = x;
		this.y = y;
		this.position = new Vector2(x * 8, y * 8);
		this.type = type;
	}
	
	public void draw(SpriteBatch batch)
	{
		batch.draw(texture, position.x, position.y, 8, 8);
	}
}
