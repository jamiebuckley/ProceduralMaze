package jix.simplegame.UI;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class HudText extends Actor 
{
	BitmapFont font;
	String text = "";
	
	public HudText(BitmapFont font)
	{
		this.font = font;
		this.font.setColor(Color.RED);
	}

	public void setText(String text)
	{
		this.text = text;
	}
	
    @Override
    public void draw (SpriteBatch batch, float parentAlpha) 
    {
        font.draw(batch, text, this.getX(), this.getY());
    }
}
