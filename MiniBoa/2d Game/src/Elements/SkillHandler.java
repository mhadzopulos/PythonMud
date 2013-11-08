package Elements;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class SkillHandler 
{
	private Player player;
	private Enemy e;
	private Timer aniTimer = new Timer();
	
	int aniTime = 0,width, height, speed;
	int x,y,k, f = 0, baseY,gibs, gibTime;
	double z,m,dx,dy;
	Rectangle hitbox;
	Area A;
	Image image;
	Skill s;
	
	boolean isVisible, isBreakable, right, inAir = true;
	boolean hitRight, hitLeft, hitUp, hitDown;
	boolean breakDown, isSolid;
	
	ImageIcon i =new ImageIcon(this.getClass().getResource("SkillImages/blank.png"));
	
	public SkillHandler(Player p, Skill s, Area A)
	{
		this.A = A;
		this.s = s;
		image = i.getImage();
		player = p;
		if(s.id > 0)
			setImage("SkillImages/" + s.name + ".png");
		
		aniTimer.schedule(new animate(), 0,10);
		isVisible = s.isVisible;
		width = image.getWidth(null);
        height = image.getHeight(null);
        isSolid = s.isSolid;
        switch(s.hitbox)
        {
	        case 0: hitbox = null;
	        	break;
	        case 1: hitbox = getTop();
	        	break;
	        case 2: hitbox = getBottom();
	       		break;
	        case 3: hitbox = getLeft();
	        	break;
	        case 4: hitbox = getRight();
	        	break;
	        case 5: hitbox = getBounds();
	        	break;
        	
        }
        right = player.isRight;
        speed = s.speed;
        gibs = s.gibs;
        if(s.id == 1)
        	isBreakable =false;
        else
        	isBreakable = s.isBreakable;
        
        if(s.id == 0)
        {
        	Random rand = new Random();
        	Integer a = rand.nextInt(4)+1 ;
        	setImage("SkillImages/RockB" + a.toString() + ".png");
        	y = player.getY() + player.height - height - 5;
        	s.desy = rand.nextInt(player.height/2) + player.getY();
        	if(right)
        		x = player.getX() + player.width;
        	else
        		x = player.getX() - width;
        }
        else if(s.id == 2)
		{
			y = player.getY() + player.height;
			x = s.desx;
			height = 1;
			boolean foundGround = false;
			while(foundGround == false)
			{
				for(Wall w: A.wall) //this is to make sure the spire spawns from the nearest ground
				{
					hitbox = getTop();
					if(w.wall.intersects(hitbox))
						foundGround = true;
					
				}
				y++;
			}
			s.desy = y - image.getHeight(null);
		}
        else if(s.id == 1)
        {
        	if(right)
        		x = player.getX() + player.width + 4;
        	else
        		x = player.getX() - 4;
			y = player.getY() + player.height - height - 4;
        }
        else if(s.id == 4)
        {
        	y = player.getY() + player.height;
        	if(right)
        		x = player.getX() + player.width;
        	else
        		x = player.getX() - width;
			height = 1;
			s.desy = player.getY() + player.height - image.getHeight(null);
        }
        else if(s.id == 5)
        {
        	height = 1;
        	width = 1;
        	x = player.x;
        	y = player.y;
        }
        
		
		
		baseY = y;
		dx = s.desx - x;
		dy = s.desy - y;
		z = Math.sqrt((dx*dx)+(dy*dy));
		k = (int)z/s.speed;
		dy =((dy/k));
		dx =((dx/k));
	}
	public SkillHandler(Player p, Skill s, Image i)
	{
		this.s = s;
		player = p;
		image = i;
		
		aniTimer.schedule(new animate(), 0,10);
		isVisible = s.isVisible;
		width = image.getWidth(null);
        height = image.getHeight(null);
        right = player.isRight;
        speed = s.speed;
        gibs = s.gibs;
        isBreakable = s.isBreakable;
        
		if(s.id == 3)
        {
        	x = s.x;
        	y = s.y;
        }
		if(s.id > 99)
		{
			x = p.x + 5;
			y = p.y ;
		}
		baseY = y;
		dx = s.desx - x;
		dy = s.desy - y;
		z = Math.sqrt((dx*dx)+(dy*dy));
		k = (int)z/s.speed;
		dy =((dy/k));
		dx =((dx/k));
		switch(s.hitbox)
        {
	        case 0: hitbox = null;
	        	break;
	        case 1: hitbox = getTop();
	        	break;
	        case 2: hitbox = getBottom();
	       		break;
	        case 3: hitbox = getLeft();
	        	break;
	        case 4: hitbox = getRight();
	        	break;
	        case 5: hitbox = getBounds();
	        	break;
        	
        }
		
	}
	public void setImage(String n)
    {
    	ImageIcon i =new ImageIcon(this.getClass().getResource(n));
    	image = i.getImage();
    	width = image.getWidth(null);
        height = image.getHeight(null);
    }
	public Rectangle getBounds()
	{
		return new Rectangle(x,y,width,height);
	}
	public void Handler()
	{
		if(s.id == 0)//summon rock
		{
			if(aniTime < 15)
			{
				player.inAir = false;
				player.moving=false;
				player.setImage("PlayerImages/playerE1.png");
				player.imageSet = true;
				player.canCast = false;
			}
			if(aniTime < 30 && aniTime > 15)
			{
				player.inAir = false;
				player.moving=false;
				player.setImage("PlayerImages/playerE2.png");
				player.imageSet = true;
			}
			if(aniTime > 30)
			{
				player.canCast = true;	//player can cast another shittle
				player.imageSet = false; //player image can change
				if(y > s.desy)
					y -= 4;
				if(right)
	        		x = player.getX() + player.width;
	        	else
	        		x = player.getX() - width;
				if(player.inAir == true)
					y = y + player.dy + 1;
				
			}
		}
		if(s.id == 1 )
		{
			if(aniTime < 20)
			{
				player.inAir = false;
				player.moving=false;
				player.setImage("PlayerImages/playerE1.png");
				player.imageSet = true;
				player.canCast = false;
				
			}
			if(aniTime < 90 && aniTime >= 20)
			{
				y -= 1;
				player.inAir = false;
				player.moving = false;
				player.setImage("PlayerImages/playerE2.png");

				
			}
			if(aniTime >=90  && aniTime < 100)
			{	isBreakable = true;
				player.inAir = false;
				player.moving = false;
				player.setImage("PlayerImages/playerEp.png");
				if(dx > 0){
					player.newx +=5;
					player.x += 5;
				}
				else
				{
					player.newx +=5;
					player.x -= 5;
				}
				if(f <= Math.abs(z))
				{
					y = (int)(y+dy);
					x = (int)(x+dx);
					f+=speed;
				}
					hitbox = getBounds(); //updates hitbox
			}
			if(aniTime >= 100 && aniTime < 110 || s.isVisible == false)
			{
				player.imageSet = false;
				player.canCast = true;
			}
			if(aniTime > 100)
			{
				if(f <= Math.abs(z))
				{
					y = (int)(y+dy);
					x = (int)(x+dx);
					f+=speed;
				}
				else
				{
					x = (int)(x+dx);
					dy += 1;
					y = (int)(y+dy);
				}
				hitbox = getBounds();
			}
			
		}
		if(s.id == 2)
		{
			if(aniTime < 26)
			{
				player.inAir = false;
				player.moving=false;
				player.setImage("PlayerImages/playerE1.png");
				player.imageSet = true;
				player.canCast = false;
			}
			if(aniTime < 150 && aniTime > 25)
			{
				player.inAir = false;
				player.moving=false;
				player.setImage("PlayerImages/playerE2.png");
				player.imageSet = true;
				player.canCast = false;
			}
			else if(aniTime > 150 && aniTime < 400)
			{
				if(height < image.getHeight(null) - 9)
				{
					height +=10;
					y -= 10;
				}
				else
				{
					height = image.getHeight(null);
					y = s.desy;
					player.canCast = true;
				}
				hitbox = getTop();
				player.imageSet = false;	
			}
			else if( aniTime > 400)
			{
				breakDown = true;
			}
		}
		if(s.id == 4)
		{
			if(aniTime < 26)
			{
				player.inAir = false;
				player.moving=false;
				player.setImage("PlayerImages/playerE1.png");
				player.imageSet = true;
				player.canCast = false;
			}
			if(aniTime < 100 && aniTime > 25)
			{
				player.inAir = false;
				player.moving=false;
				player.setImage("PlayerImages/playerE2.png");
				player.imageSet = true;
			}
			else if(aniTime > 100 && aniTime < 200)
			{
				if(height < image.getHeight(null) - 9)
				{
					height +=10;
					y -= 10;
				}
				else
				{
					height = image.getHeight(null);
					y = s.desy;
					player.canCast = true;
				}
				
				player.imageSet = false;	
			}
			else if(aniTime > 200 && getBounds().intersects(player.getBottom()))
			{
				x += player.dx;
			}
		}
		
		if(s.id == 3)
		{
			if(aniTime < 10)
			{
				player.inAir = false;
				player.moving = false;
				player.setImage("PlayerImages/playerEp.png");
				player.imageSet = true;
				player.canCast = false;
			}
			else if(aniTime > 10 && aniTime < 20)
			{
				player.imageSet = false;
				player.canCast = true;
			}
			if(f <= Math.abs(z))
			{
				y = (int)(y+dy);
				x = (int)(x+dx);
				f+=speed;
				hitbox = getBounds();
			}
			else
			{
				x = (int)(x+dx);
				dy += 1;
				y = (int)(y+dy);
				hitbox = getBounds();
			}
		}
		if(s.id == 5)
		{
			if(aniTime < 30)
			{
				
			}
			else if(aniTime > 30)
			{
				y = player.getY();
	        	x = player.getX();
			}
        	
		}
		if(s.id == 102)
		{
			if(f <= Math.abs(z))
			{
				y = (int)(y+dy);
				x = (int)(x+dx);
				f+=speed;
				hitbox = getBounds();
			}
			else
			{
				x = (int)(x+dx);
				dy += 1;
				y = (int)(y+dy);
				hitbox = getBounds();
			}
		}
		
		
	}
	public void CheckCollisions(Rectangle r)
	{
		if(getRight().intersects(r))
			hitRight = true;
		if(getLeft().intersects(r))
			hitLeft = true;
		if(getTop().intersects(r))
			hitUp = true;
		if(getBottom().intersects(r))
			hitDown = true;
	}
	private class animate extends TimerTask
	{
		public void run()
		{
			aniTime++;
		if(inAir == false)
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
    public Gib rockDestroy(int a)
    {
    		Random rand = new Random();
    		Integer rockID = rand.nextInt(a) + 1;
    		int x1, h;
    		
	    	h = rand.nextInt(height) + y;
	    	x1 = rand.nextInt(width) + x;

    		if(hitRight)
    		{
    			x1 = rand.nextInt(width) + x - 10;
    		}
    		ImageIcon ii = new ImageIcon(this.getClass().getResource("SkillImages/RockB" + rockID + ".png"));
    		return new Gib(ii.getImage(), x1, h) ;
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
    public Rectangle getHitBox()
    {
    	return hitbox;
    }
}
