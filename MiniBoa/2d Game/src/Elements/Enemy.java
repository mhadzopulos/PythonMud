package Elements;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.io.*;

public class Enemy {
	private String pathName = "EnemyImages/";
    private String craft = pathName + "alien.png";
    int dx, x,y,width,height, level, hp, stamina;
    int newx,newy,timeCounter,dy, speed, runTime;
    int desx,desy, desdy, desdx, type, maxCharge = 3, charge;
    double fallCounter, aniTime, coolDownTime, z;
    boolean visible, inAir,moving, isRight, canCast = true;
    boolean space,mouse1,mouse2,imageSet, charged;
    boolean canMove;
    private Image image;
    ArrayList<EnemySkillHandler> missiles = new ArrayList<EnemySkillHandler>();
    
    public Enemy(int x, int y) //need to add in EnemyClasses
    {
        ImageIcon ii = new ImageIcon(this.getClass().getResource(craft));
        image = ii.getImage();
        width = image.getWidth(null);
        height = image.getHeight(null);
        visible = true;
        this.x = x;
        this.y = y;
        newx = x;
        newy = y;
        hp = 32;
        canMove = true;
        type = 1;
        speed = 4;
        moving = true;
        inAir = false;
    }
   /* public void checkAnimation()
    {
    	if(imageSet == true)
    		return;
   
    	if(moving == true && inAir == false)
    	{
    		if(aniTime <= 10)
    		{
    			image = im1.getImage();
    		}
    		if((aniTime > 10 && aniTime <= 20) || (aniTime >= 30 && aniTime < 40))
    		{
    			image = im11.getImage();
    		}
    		if(aniTime >= 20 && aniTime < 30)
    		{
    			image = im2.getImage();
    		}	
    	}
    	else if(inAir == true)
    	{
    		image = ima.getImage();
    	}
    	else
    		image = ii.getImage();
    }*/
    
    /*public void checkCool()
    {
    	if(coolDownTime > 0)
    	{
    		coolDownTime--;
    	}
    }*/
    public void setImage(String n)
    {
    	ImageIcon i =new ImageIcon(this.getClass().getResource(n));
    	image = i.getImage();
    	width = image.getWidth(null);
        height = image.getHeight(null);
    }
    public void move2()
    {
    	if(x < 0)
    		x = 300;
    	else
    		x -= 1;
    }
    public void move(Area a, Player p) throws IOException
    {
    	//checkCool();
    	///checkAnimation();
    	if(canMove == false && inAir == false)
    		return;
    	
    	if(inAir== true)
    	{
    		timeCounter++;
    	}
    	else
    		timeCounter = 0;
    	
    	if(dx < 0)
    		isRight = false;
    	if(dx > 0)
    		isRight = true;
    	
    	
    	fallCounter = timeCounter%2;
    	
    	
    	if((inAir == true) && ((fallCounter >=0)) && (fallCounter < 1))
    	{
    		if(dy < 16)
    			dy = dy + 2;
    		newy += dy;
    	}
    	else
    		newy += dy;
        
    	if(type == 1) //finding out movement for earth types, dx is speed
    	{
    		if(p.x - 200 > x)
    			dx = speed;
    		if(p.x + 200 < x)
    			dx = -speed;
    		if(p.x > x - 200 && p.x < x + 200)
    		{
    			dx = 0;
    			if(missiles.size() == 0)
    				canCast = true;
    			if(canCast == true)
    				handleSkills(p);
    		}
    	}
    	newx += dx;
        for(Wall w: a.wall)
        {
        	if(this.checkCollisions(w.wall) != true)
        	{
        		x = newx;
        		y = newy;
        		inAir = true;
        		moving = true;
        	}
        	if(this.getBottom().intersects(w.wall) == true)
        	{
        		inAir = false;
        		if(dx != 0)
        			x = newx;
 
        		if(dy < 0)
        		{
        			y = newy;
        			inAir = true;
        		}
        		if(dy > 0)
        		{
        			newy = w.wall.y - height;
        			dy = 0;
        		}
        		
        	}	
        	if(this.getTop().intersects(w.wall) == true)
        	{
        		if(dx != 0)
        			x = newx;
        		if(dy < 0)
        		{
        			newy = w.wall.y+ w.wall.height;
        			dy = 0;
        		}
        	}
        	if(this.getLeft().intersects(w.wall) == true)
        	{
        		if(dx > 0)
        			x = newx;
        		if(dy != 0)
        			y = newy;
        		if(dx < 0)
        		{
        			newx = w.wall.x + w.wall.width;
        			dx = 0;
        			moving = false;
        		}
        		
        	}
        	if(this.getRight().intersects(w.wall) == true)
        	{
        		if(dx < 0)
        			x = newx;
        		if(dy != 0)
        			y = newy;
        		if(dx > 0)
        		{
        			newx = w.wall.x - width;
        			dx = 0;
        			moving = false;
        		}
        	}
        }
        for(EnemySkillHandler sh: missiles)
	        {
        		if (sh.isSolid)
		        {
		        	if(this.checkCollisions(sh.getBounds()) != true)
		        	{
		        		x = newx;
		        		y = newy;
		        		inAir = true;
		        		moving = true;
		        	}
		        	if(this.getBottom().intersects(sh.getBounds()) == true)
		        	{
		        		inAir = false;
		        		if(dx != 0)
		        			x = newx;
		 
		        		if(dy < 0)
		        		{
		        			y = newy;
		        			inAir = true;
		        		}
		        		if(dy > 0)
		        		{
		        			newy = sh.y - height;
		        			dy = 0;
		        		}
		        		
		        	}	
		        	if(this.getTop().intersects(sh.getBounds()) == true)
		        	{
		        		if(dx != 0)
		        			x = newx;
		        		if(dy < 0)
		        		{
		        			newy = sh.y+ sh.height;
		        			dy = 0;
		        		}
		        	}
		        	if(this.getLeft().intersects(sh.getBounds()) == true)
		        	{
		        		if(dx > 0)
		        			x = newx;
		        		if(dy != 0)
		        			y = newy;
		        		if(dx < 0)
		        		{
		        			newx = sh.x + sh.width;
		        			dx = 0;
		        			moving = false;
		        		}
		        		
		        	}
		        	if(this.getRight().intersects(sh.getBounds()) == true)
		        	{
		        		if(dx < 0)
		        			x = newx;
		        		if(dy != 0)
		        			y = newy;
		        		if(dx > 0)
		        		{
		        			newx = sh.x - width;
		        			dx = 0;
		        			moving = false;
		        		}
		        	}
		        }
        }
        if (x < 1) {
            newx = 1;
        }
        if (y < 1) {
            newy = 1;
            dy = 0;
        }
    }
    public void handleSkills(Player p) throws IOException
    {
    	if(type == 1)
    	{
    		Random rand = new Random();
    		Skill s = new Skill(1);
    		s.desx = p.x + p.width/2;
    		s.desy = p.y + p.height/2;
    		useSkill(s);
    		
    		
    	}
    		
    }
    public void useSkill(Skill s) 
    {
    	
	    	EnemySkillHandler k = new EnemySkillHandler(this, s);
	    	desdy = (int)k.dy;
	    	desdx = (int)k.dx;
	    	z = k.z;
	    	missiles.add(k);
    	
    }
    public boolean checkCollisions(Rectangle r)
    {
    	if(this.getBounds().intersects(r))
    	{
    		return true;
    	}
    	else
    		return false;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public boolean isVisible() {
        return visible;
    }
    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Image getImage() {
        return image;
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
    public BufferedImage horizontalflip(BufferedImage img) 
    {  
    	int w = img.getWidth();  
        int h = img.getHeight();  
        BufferedImage dimg = new BufferedImage(w, h, img.getType());  
        Graphics2D g = dimg.createGraphics();  
        g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);  
        g.dispose();  
        return dimg;  
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    public Rectangle getBottom()
    {
    	return new Rectangle((newx + width/2), y + height, 1, 1);
    }
    public Rectangle getTop()
    {
    	return new Rectangle(newx + width/2,newy,1,1);
    }
    public Rectangle getLeft()
    {
    	return new Rectangle(newx, newy + height/2, 1, 1);
    }
    public Rectangle getRight()
    {
    	return new Rectangle(newx + width, newy + height/2, 1, 1);
    }
}