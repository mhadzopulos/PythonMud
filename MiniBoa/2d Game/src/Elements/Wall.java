package Elements;
import java.awt.Rectangle;

public class Wall 
{
	int height, width, x, y;
	Rectangle wall;
	boolean visible;
	
	public Wall(int h, int w,int x,int y)
	{
		wall = new Rectangle(x,y,w,h);
		this.x = x;
		this.y = y;
		this.height = h;
		this.width = w;
		visible = true;
	}
	
}
