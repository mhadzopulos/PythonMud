package Elements;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

public class Gib 
{
	int width, height, x,y, gibTime;
	int dx,dy;
	boolean inAir =true;
	private Timer aniTimer = new Timer();
	Image image;
	
	public Gib(Image i, int x, int y)
	{
		image = i;
		this.x = x;
		this.y = y;
		aniTimer.schedule(new Counter(), 0, 1);
		width = image.getWidth(null);
        height = image.getHeight(null);
		
	}
	private class Counter extends TimerTask
	{
		public void run()
		{
			gibTime++;
		}
	}
	public BufferedImage makeColorTransparent(Color color) 
    {    
        BufferedImage dimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dimg.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(image,0,0,null);
        for(int i = 0; i < dimg.getHeight(); i++) {  
            for(int j = 0; j < dimg.getWidth(); j++) {  
                if(dimg.getRGB(j, i) == color.getRGB()) {  
                dimg.setRGB(j, i, AlphaComposite.CLEAR);  
                }  
            }  
        }  
        return dimg;  
    }
    public BufferedImage horizontalflip(BufferedImage img) {  
        
    	int w = img.getWidth();  
        int h = img.getHeight();  
        BufferedImage dimg = new BufferedImage(w, h, img.getType());  
        Graphics2D g = dimg.createGraphics();  
        g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);  
        g.dispose();  
        return dimg;  
    }
    public Rectangle getBottom()
    {
    	return new Rectangle((x + width/2), y + height, 1, 1);
    }
    public Rectangle getTop()
    {
    	return new Rectangle(x + width/2,y,1,1);
    }
    public Rectangle getLeft()
    {
    	return new Rectangle(x,y + height/2, 1, 1);
    }
    public Rectangle getRight()
    {
    	return new Rectangle(x + width, y + height/2, 1, 1);
    }
}
