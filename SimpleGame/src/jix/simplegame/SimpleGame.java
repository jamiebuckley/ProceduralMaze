package jix.simplegame;
import jix.simplegame.Maze.Level;
import jix.simplegame.UI.HudText;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class SimpleGame implements ApplicationListener 
{
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Texture blockTexture;
	private Texture endTex;
	private Texture shipTexture;
	private Texture exitTexture;
	
	Texture wallTex;
	Texture whiteTex;
	Texture coinTex;
	
	Level l;
	DrawnLevel level;
	
	Ship s;
	
	float ltime = 0;
	
	int levelSize = 7;
	
	BitmapFont font;
	boolean showFullLevel = false;
	
	Stage stage;
	HudText scoreText;
	HudText timeText;
	
	float time  = 0;
	
	@Override
	public void create() 
	{		
		Gdx.graphics.getWidth();
		Gdx.graphics.getHeight();
		
		camera = new OrthographicCamera(1000, 700);
		batch = new SpriteBatch();
		
		blockTexture = new Texture(Gdx.files.internal("Block.png"));
		endTex = new Texture(Gdx.files.internal("Endtex.png"));
		shipTexture = new Texture(Gdx.files.internal("Ship.png"));
		exitTexture = new Texture(Gdx.files.internal("Exit.png"));
		whiteTex = new Texture(Gdx.files.internal("White.png"));
		wallTex = new Texture(Gdx.files.internal("Black.png"));
		coinTex = new Texture(Gdx.files.internal("Coin.png"));
		
		font = new BitmapFont(Gdx.files.internal("Font.fnt"));
		font.setColor(Color.BLACK);
		
		
		s = new Ship(shipTexture);
		s.position = new Vector2(10, 10);
		generateLevel();
		//showFullLevel=true;
		
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		scoreText = new HudText(font);
		scoreText.setPosition(20, Gdx.graphics.getHeight() - 20);
		
		timeText = new HudText(font);
		timeText.setPosition(20, Gdx.graphics.getHeight() - 50);
		
		stage.addActor(scoreText);
		stage.addActor(timeText);
	}

	@Override
	public void dispose() 
	{
		batch.dispose();
		blockTexture.dispose();
		shipTexture.dispose();
		exitTexture.dispose();
	}
	
	public void update()
	{
		float delta = Gdx.graphics.getDeltaTime();
		level.update(delta);
		if (!showFullLevel)
		{
			s.update(delta, level);
		}
		
		if (s.won)
		{
			s.position = new Vector2(10, 10);
			s.score += 100;
			 generateLevel();
			 showFullLevel = true;
		}
	}

	
	int showFullLevelTimer = 0;
	@Override
	public void render() 
	{		
		update();
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		/*
		if (showFullLevel && showFullLevelTimer < 170)
		{
			showFullLevelTimer++;
			camera.setToOrtho(false, 1000, 700);
			camera.position.x = 200;
			camera.position.y = 200;
			camera.update();
			
			if(showFullLevelTimer >= 170)
			{
				showFullLevelTimer = 0;
				showFullLevel = false;
			}
		}
		else
		{
		*/
			camera.setToOrtho(false, 100, 70);
			camera.position.x = s.position.x;
			camera.position.y = s.position.y;
			camera.update();
		//}
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();		
		level.draw(batch);
		s.draw(batch);
		batch.end();
		
		
		time+= Gdx.graphics.getDeltaTime();
		
		batch.begin();
		scoreText.setText("Score: " + s.score);
		timeText.setText("Time:  " + Math.round(time));
		
		stage.draw();
		batch.end();
	}
	
	public void generateLevel()
	{
		levelSize+=3;
		s.won = false;
		l = new Level(levelSize, levelSize, 16);
		l.generate();
		
		if (levelSize > 9)
		{
			//l.createRooms(2, 3);
			//l.generateCoins(5);
		}
		
		level = new DrawnLevel(l, blockTexture, wallTex, exitTexture, coinTex, shipTexture, s);
	}

	@Override
	public void resize(int width, int height) 
	{
		
	}

	@Override
	public void pause() 
	{
		
	}
	
	@Override
	public void resume() 
	{
		
	}
}