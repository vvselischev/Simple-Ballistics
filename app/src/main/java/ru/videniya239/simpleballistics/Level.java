package ru.videniya239.simpleballistics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;

public class Level //implements Menu
{
    private Cannon cannon;
    private Bullet bullet;
    private Paint paint;
    private BackTexture background;
    private Bitmap levelMap;
    //private Button shootButton;

    private final int checkAngle = 60;



    private boolean bulletFlying;
    private float windVelocity;

    public static ArrayList<Vector2> traectory;

    public void Init(Bitmap background, Bitmap levelMap, float windVelocity, Rect carriageRect, Rect cannonRect, Vector2 nail, int id)
    {
        this.background = new BackTexture(background);
        this.levelMap = levelMap;

        this.windVelocity = windVelocity;
        traectory = new ArrayList<>();
        paint = new Paint();
        paint.setColor(Color.WHITE);

        cannon = new Cannon(45, nail);//, cannonRect, carriageRect);

        if (id == 1) {
            cannon.velocitySlider.firstUp = true;
        }
    }
    public void Init(Bitmap background, Bitmap levelMap, float windVelocity, Vector2 trans, int id)
    {
        this.background = new BackTexture(background);
        this.levelMap = levelMap;

        this.windVelocity = windVelocity;
        traectory = new ArrayList<>();
        paint = new Paint();
        paint.setColor(Color.WHITE);

        cannon = new Cannon(45, trans);

        if (id == 1)
        {
            cannon.velocitySlider.firstUp = true;
        }
    }
    public void Start()
    {
        cannon.Activate();
    }

    public void Shoot()
    {

        if (!bulletFlying)
        {
            bullet = cannon.CreateBullet(windVelocity);
            LifeManager.getInstance().changeLives(-1);
            bulletFlying = true;
        }
    }

    public void Draw(Canvas canvas)
    {
        background.draw(canvas);
        LifeManager.getInstance().Draw(canvas);
        cannon.DrawCannon(canvas);
        if (!bulletFlying)
        {
            cannon.Draw(canvas);
        }
        if (bulletFlying)
        {
            if (bullet != null)
            {
                paint.setColor(Color.WHITE);

                for (Vector2 position : traectory) {
                    paint.setColor(Color.GRAY);
                    canvas.drawCircle(position.x, position.y, bullet.drawRadius / 2, paint);
                }
                bullet.Draw(canvas);
            }
        }
    }

    public void Update(float deltaT)
    {
        if (!bulletFlying)
        {
            if (cannon != null)
            {
                cannon.Update();
            }
        }
        else
        {
            if (bullet != null)
            {
                for (int i = 0; i < 360 / checkAngle; i++)
                {
                    Vector2 checkPoint = getCheckCoord(checkAngle * i);
                    if (insideScreen(checkPoint.x, checkPoint.y))
                    {
                        if (!CheckColor(checkPoint))
                        {
                            bullet.Update(deltaT);
                            break;
                        } else
                        {
                            break;
                        }
                    } else {
                        miss();
                        break;
                    }
                }
            }
        }
    }

    private Vector2 getCheckCoord(int angle)
    {
        return new Vector2(bullet.getPosX() + (float)Math.cos(angle * Math.PI / 180) * bullet.drawRadius,
                bullet.getPosY() + (float)Math.sin(angle * Math.PI / 180) * bullet.drawRadius);
    }

    private boolean CheckColor(Vector2 pos)
    {
        if (cannon.cannonRect.contains((int)pos.x, (int)pos.y) || (cannon.carriageRect.contains((int)pos.x, (int)pos.y)))
        {
            miss();
            return true;
        }

        if (Color.red(getMapPixel((int)pos.x, (int)pos.y)) >= 200) {
            onTarget();
            return true;
        }
        if (Color.green(getMapPixel((int)pos.x, (int)pos.y)) > 200) {
            miss();
            return true;
        }
        else
        {
             return false;
        }
    }

    private void miss()
    {
        bulletFlying = false;
        traectory.clear();
        bullet = null;
        if (LifeManager.getInstance().getLives() == 0)
        {
            LevelManager.GetInstance().lose = true;
            cannon.Deactivate();
            Log.d("level", "level finished: " + LevelManager.GetInstance().GetCurrentLevelNumber());
            //GameController.DetachButton(shootButton);
            GameController.DetachSlider(cannon.velocitySlider);
            GameController.setGamePhase(GameState.PHASE_RESULT);
        }
    }

    private void onTarget()
    {
        bulletFlying = false;
        traectory.clear();
        bullet = null;
        cannon.Deactivate();
        Log.d("level", "level finished: " + LevelManager.GetInstance().GetCurrentLevelNumber());
        //GameController.DetachButton(shootButton);
        GameController.DetachSlider(cannon.velocitySlider);
        GameController.setGamePhase(GameState.PHASE_PLAY);
    }

    private boolean insideScreen(float posX, float posY)
    {
        return ((posX > 0) && (posX < GameController.screenWidth) &&
                (posY > 0) && (posY < GameController.screenHeight));
    }

    private int getMapPixel(int x, int y)
    {
        return levelMap.getPixel(x * levelMap.getWidth() / (int)GameController.screenWidth,
                                 y * levelMap.getHeight() / (int)GameController.screenHeight);
    }

    /*@Override
    public void updateButtons(ButtonName b)
    {
        if (b == ButtonName.ShootButton)
        {
            if (!bulletFlying) //&& !cannon.velocitySlider.tapped)
                Shoot();
        }
    }

    @Override
    public void show()
    {

    }*/
}
