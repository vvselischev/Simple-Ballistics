package ru.videniya239.simpleballistics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;


public class Slider
{
    public Rect sliderRect;
    private Rect cursorRect;
    private int maxValue;
    private int minValue;
    private Paint paint;
    private boolean horz;
    private int value;
    private Collider sliderCollider;
    private int offsetX;
    private int offsetY;

    private Vector2 centre;
    public float angle;

    public boolean firstUp;

    public boolean tapped;
    private float velocityKoeff;

    private Vector2 tapPosition;
    private Vector2 startPoint;
    private Vector2 radiusSlider;

    public Slider(Rect sliderRect, int minValue, int maxValue, int startValue, int startAngle,
                  Vector2 centre, Vector2 startPoint, Vector2 radiusSlider)
    {
        this.sliderRect = sliderRect;

        this.minValue = minValue;
        this.maxValue = maxValue;

        paint = new Paint();
        this.centre = centre;
        this.startPoint = startPoint;
        this.value = startValue;
        this.angle = startAngle;
        this.radiusSlider = radiusSlider;

        sliderCollider = new Collider(sliderRect);
        velocityKoeff = GameController.screenWidth / 150000;
        tapPosition = centre;
    }


    public void draw(Canvas canvas)
    {
        paint.setColor(Color.BLACK);
        canvas.save();
        canvas.rotate(angle, centre.x, centre.y);
        canvas.scale(value / (float)maxValue, 1, startPoint.x, startPoint.y);
        canvas.drawBitmap(MainActivity.arrow, null, sliderRect, paint);
        canvas.restore();
    }

    public void Activate()
    {
    }

    public int getValue()
    {
        return value;
    }

    public void onUp()
    {
        if (firstUp)
        {
            firstUp = false;
        }
        else
        {
            LevelManager.GetInstance().GetCurrentLevel().Shoot();
        }
    }

    public boolean update(Vector2 movePosition)
    {
        {
            tapped = true;
            {
                Vector2 direction = movePosition.minus(centre);
                value = (int)((direction.len() - radiusSlider.x)/ sliderRect.width() * maxValue);

                if (value > maxValue)
                {
                    value = maxValue;
                }

                if (value < minValue)
                {
                    value = minValue;
                }

                angle = (float)Math.toDegrees(Math.atan2(direction.y, direction.x));

                if (angle < -80)
                {
                    angle = -80;
                }
                if (angle > 0)
                {
                    angle = 0;
                }

                tapPosition = movePosition;
                updateCursorRect();
            }
            return true;
        }
    }
}
