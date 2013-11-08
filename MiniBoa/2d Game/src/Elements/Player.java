package Elements;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import java.awt.image.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import java.io.*;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.MouseInfo;

public class Player {
	private String pathName = "PlayerImages/";
    private String still = pathName + "player.png";
    private String move1 = pathName +"playerm1.png";
    private String move2 = pathName +"playerm2.png";
    private String Air = pathName +"playera.png";
    private String move11 = pathName +"playerm11.png";
    private ImageIcon ii = new ImageIcon(this.getClass().getResource(still));
    private ImageIcon im1 = new ImageIcon(this.getClass().getResource(move1));
    private ImageIcon im2 = new ImageIcon(this.getClass().getResource(move2));
    private ImageIcon ima = new ImageIcon(this.getClass().getResource(Air));
    private ImageIcon im11 = new ImageIcon(this.getClass().getResource(move11));
    private Image image;
    
    private ArrayList<SkillHandler> missiles;
    private Area area;
    
    int dx, x,y,width,height, level, hp, stamina;
    int newx,newy,timeCounter,dy, speed, runTime, chargeCount;
    int desx,desy, desdy, desdx, type, maxCharge = 3, charge;
    String name;
    double fallCounter, aniTime, coolDownTime, z;
    boolean visible, inAir,moving, isRight, canCast =true;
    boolean space,mouse1,mouse2,imageSet, charged;
    
    int iconCounter1, iconCounter2, iconCounter3, iconPointer;
    boolean showIcon, parrying = false, parried = false;
    
	Timer fall = new Timer();
	ArrayList<Skill> oneSkills = new ArrayList<Skill>();
	ArrayList<Skill> twoSkills = new ArrayList<Skill>();
	ArrayList<Skill> threeSkills = new ArrayList<Skill>();
	int iconList[][];
    //DX = speedx
    //DY = speedy

    public Player() throws IOException{
        image = ii.getImage();
        width = image.getWidth(null);
        height = image.getHeight(null);
        missiles = new ArrayList<SkillHandler>();
        visible = true;
        x = 40;
        y = 300;
        newx = x;
        newy = y;
        area = null;
        inAir = true;
        fall.schedule(new animation(), 0,10);
        timeCounter = 0;	hp = 100; stamina = 100;
        aniTime = 0;
        speed = 4;
        isRight = true;
        type = 1;
        
		FileReader getData = new FileReader("src/Elements/Database/CharacterSheet.txt");
		BufferedReader gd = new BufferedReader(getData);

		name = gd.readLine();
        type = Integer.valueOf(gd.readLine());
        
        if(type == 1)
        {
        	for(int i = 0; i < 6; i++) //5 is the amount of earth skills right now
        	{
        		Skill s = new Skill(i);
        		System.out.println("skillLoaded");
        		if(s.typeA != 0)
        		{
        			if(s.typeA == 1)
        				oneSkills.add(s);
        			if(s.typeA == 2)
        				twoSkills.add(s);
        			if(s.typeA == 3)
        				threeSkills.add(s);
        		}	
        	}
        	Skill s = new Skill(100);
        	threeSkills.add(s);
        	s = new Skill(101);
        	threeSkills.add(s);
        	s = new Skill(102);
        	threeSkills.add(s);
        }
       
    }
    
    
    public void checkAnimation()
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
    }
    public void checkCool()
    {
    	if(coolDownTime > 0)
    	{
    		coolDownTime--;
    	}
    }
    public void setImage(String n)
    {
    	ImageIcon i =new ImageIcon(this.getClass().getResource(n));
    	image = i.getImage();
    	width = image.getWidth(null);
        height = image.getHeight(null);
    }
    public void move(Area a) 
    {
    	checkCool();
    	checkAnimation();
    	if(moving == false && inAir == false)
    		return;
    	if(missiles.size() == 0)
    		canCast = true;
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
        
    	newx += dx;
        for(Wall w: a.wall)
        {
        	if(this.checkCollisions(w.wall) != true)
        	{
        		x = newx;
        		y = newy;
        		inAir = true;
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
        for(SkillHandler sh: missiles)
	        {
        		if (sh.isSolid)
		        {
		        	if(this.checkCollisions(sh.getBounds()) != true)
		        	{
		        		x = newx;
		        		y = newy;
		        		inAir = true;
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
    public boolean checkCollisions(Rectangle r)
    {
    	if(this.getBounds().intersects(r))
    	{
    		return true;
    	}
    	else
    		return false;
    }
    public Area getArea()
    {
    	return area;
    }
    public boolean getAir()
    {
    	return inAir;
    }
    public void setAir(boolean a)
    {
    	inAir = a;
    }
    public void setArea(Area a)
    {
    	area = a;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void setX(int x)
    {
    	this.x = x;
    }
    public void setY(int y)
    {
    	this.y = y;
    }
    public Image getImage() {
        return image;
    }
    public ArrayList<SkillHandler> getMissiles() {
        return missiles;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public boolean isVisible() {
        return visible;
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
    public void mouseClicked(MouseEvent e)throws IOException
    {
    	int click = e.getButton();
    	if(click == MouseEvent.BUTTON1)
    	{
    		if(charged == true && canCast == true)
        	{
    			Point p = e.getPoint();
    			if(p.x < x && isRight == true)
    				isRight = false;
    			if(p.x > x && isRight == false)
    				isRight = true;
        		boolean found = false;
    			Skill s = oneSkills.get(iconCounter1);
    			
        		if (s.id == 3 && inAir == true)
        			return;
        		for(int i = 0; i < missiles.size(); i++)
        		{
        			SkillHandler sh = (SkillHandler)missiles.get(i);
        			if(sh.s.id == 0 && sh.aniTime > 40)
        			{
        				
                		s.desx = p.x;
                    	s.desy = p.y;
                    	desx = p.x;
                    	desy = p.y;
                    	s.x = sh.x;
                    	s.y = sh.y;
                    	useSkill(s, sh.image);
        				found = true;
        				sh.isVisible = false;
        				missiles.remove(i);
        				charge--;
        				if(charge == 0)
        					charged = false;
        				stamina -= s.castCost;
        			}
        			if(found == true)
        				break;
        		}
        	}
    	}
    	if(click == MouseEvent.BUTTON3)
    	{
    		Point p = e.getPoint();
			if(p.x < x && isRight == true)
				isRight = false;
			if(p.x > x && isRight == false)
				isRight = true;
    		if(canCast == true)
        	{
    			Skill s = twoSkills.get(iconCounter2);
        		if (inAir == true)
        			return;
        		s.desx = p.x;
            	s.desy = p.y;
            	desx = p.x;
            	desy = p.y;
            	mouse2 = true;
            	useSkill(s);
            	//coolDownTime = s.castTime;
        	}
    	}
    }
    public void mouseReleased(MouseEvent e)
    {
    	int click = e.getButton();
    	
    	if(click == MouseEvent.BUTTON1)
    		mouse1 = false;
    	if(click == MouseEvent.BUTTON3)
    		mouse2 = false;
    }
    public void keyPressed(KeyEvent e) throws IOException{
        int key = e.getKeyCode();
        
        if(key == KeyEvent.VK_1)
        {
        	if(showIcon == true)
        		iconCounter1++;
        	if(iconCounter1 > oneSkills.size()-1)
        		iconCounter1 = 0;
        	iconPointer = 1;
        	showIcon = true;
        }
        if(key == KeyEvent.VK_2) //need to draw in the icon for choosing skills
        {
        	if(showIcon == true)
        		iconCounter2++;
        	if(iconCounter2 > twoSkills.size()-1)
        		iconCounter2 = 0;
        	iconPointer = 2;
        	showIcon = true;
        }
        if(key == KeyEvent.VK_3) //need to draw in the icon for choosing skills
        {
        	if(showIcon == true)
        		iconCounter3++;
        	if(iconCounter3 > threeSkills.size()-1)
        		iconCounter3 = 0;
        	iconPointer = 3;
        	showIcon = true;
        }
        
        if (key == KeyEvent.VK_SPACE) 
        {		
        }
        if(key == KeyEvent.VK_F)
        {
        	Skill s = threeSkills.get(iconCounter3);
        	if(s.id > 99)
        		parrying = true;
        	
        }
        if(key == KeyEvent.VK_E)
        {
        	if(inAir == false )
        	{
	        	boolean found = false;
	        	for(SkillHandler h: missiles)
	        	{
	        		if(h.s.id ==4)
	        			found = true;
	        	}
	        	if(found == false)
	        	{
	        		Skill s = new Skill(4);
	        		useSkill(s);
	        	}
        	}
        	
        }
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A)
        {
        	if(runTime < 40)
        		dx = -speed;
        	else
        		dx = -speed*2;
            moving = true;
        }

        if (key == KeyEvent.VK_RIGHT|| key == KeyEvent.VK_D) 
        {
        	
        	if(runTime < 40)
        		dx = speed;
        	else
        		dx = speed*2;
            moving = true;
        }

        if (key == KeyEvent.VK_UP|| key == KeyEvent.VK_W) 
        {
        	if(inAir != true)
        		jump();
        }
    }
    public void keyReleased(KeyEvent e) throws IOException{
        int key = e.getKeyCode();
        
        if(key == KeyEvent.VK_1)
        {	
        }
        if(key == KeyEvent.VK_2)
        {
        }
        if(key == KeyEvent.VK_F)
        {
        	parrying = false;
        }
        if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
        	
            dx = 0;
            moving = false;
        }

        if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
            dx = 0;
            moving = false;
        }
        if(key== KeyEvent.VK_SPACE)
        	if(charge < maxCharge && inAir == false)
        	{
        		charged = true;
        		Skill s = new Skill(0);
        		useSkill(s);
        		charge++;
        		
        	}

        
    }
  
    public void parry(EnemySkillHandler esh, int xe, int ye)
    {
    	Skill s = threeSkills.get(iconCounter3);
    	parried = true;
    	if(s.id == 102)
    	{
    		s = esh.s;
    		s.id = 102;
    		s.desx = xe;
    		s.desy = ye+10;
    		SkillHandler k = new SkillHandler(this, s, esh.image);
    		
    		missiles.add(k);
        	desdy = (int)k.dy;
        	desdx = (int)k.dx;
        	z = k.z;
        	
    	}
    	if(s.id == 100)
    	{
    		if(s.isBreakable)
    		{
    			
    		}
    	}
    	if(s.id == 101)
    	{
    		
    	}
    }
    public void useSkill(Skill s) 
    {
    	
	    	SkillHandler k = new SkillHandler(this, s, area);
	    	desdy = (int)k.dy;
	    	desdx = (int)k.dx;
	    	z = k.z;
	    	missiles.add(k);
    	
    }
    public void useSkill(Skill s, Image i) 
    {
    	SkillHandler k = new SkillHandler(this, s, i);
    	desdy = (int)k.dy;
    	desdx = (int)k.dx;
    	z = k.z;
    	missiles.add(k);
    }
    public void jump()
    {
    		dy = -16;
    		inAir = true;
    }

    private class animation extends TimerTask
    {
    	public void run()
    	{
    		if(moving == true)
    		{
    			aniTime++;
    			if(inAir == false)
    				runTime++;
    		}
    		else
    		{
    			aniTime = 0;
    			runTime = 0;
    		}
    		if(aniTime > 40)
    			aniTime = 0;
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
    
    
    public void drawIcon(Graphics2D d)
    {
    	for(int i = 0; i < 4; i++)
    	{
    		if(i == 1)
    		{
    			for(int u = 0; u < oneSkills.size(); u++)
    			{
    				int x1 = 1;
			        int y1 = 0;
    				Skill s = oneSkills.get(u);
    				if(iconPointer == 1 && iconCounter1 == u)
    				{
    					ImageIcon ii =new ImageIcon(this.getClass().getResource("IconImages/" +s.name + ".png"));
    			    	Image Iconimage = ii.getImage();
    			    	width = image.getWidth(null);
    			        height = image.getHeight(null);
    			        x1 = x1 + 0;
    			        y1 = y1 + height;
    			        d.drawImage(Iconimage,x1,y1,null);
    				}
    				else
    				{
    					ImageIcon ii =new ImageIcon(this.getClass().getResource("IconImages/notIcon.png"));
    			    	Image Iconimage = ii.getImage();
    			    	width = image.getWidth(null);
    			        height = image.getHeight(null);
    			        y1 = y1 + height;
    			        d.drawImage(Iconimage,x1,y1,null);
    				}
    			}
    		}
    		if(i == 2)
    		{
    			int x1 = 68+2;
		        int y1 = 0;
    			for(int u = 0; u < twoSkills.size(); u++)
    			{
    				Skill s = twoSkills.get(u);
    				if(iconPointer == 2 && iconCounter2 == u)
    				{
    					ImageIcon ii =new ImageIcon(this.getClass().getResource("IconImages/" +s.name + ".png"));
    			    	Image Iconimage = ii.getImage();
    			    	width = image.getWidth(null);
    			        height = image.getHeight(null);
    			        y1 = y1 + height;
    			        d.drawImage(Iconimage,x1,y1,null);
    				}
    				else
    				{
    					ImageIcon ii =new ImageIcon(this.getClass().getResource("IconImages/notIcon.png"));
    					Image Iconimage = ii.getImage();
    			    	width = image.getWidth(null);
    			        height = image.getHeight(null);
    			        y1 = y1 + height;
    					d.drawImage(Iconimage, x1,y1,null);
    				}
    			}
    		}
    		if(i == 3)
    		{
    			int x1 = 68*2+2;
		        int y1 = 0;
    			for(int u = 0; u < threeSkills.size(); u++)
    			{
    				Skill s = threeSkills.get(u);
    				if(iconPointer == 3 && iconCounter3 == u)
    				{
    					ImageIcon ii =new ImageIcon(this.getClass().getResource("IconImages/" +s.name + ".png"));
    					Image Iconimage = ii.getImage();
    			    	width = image.getWidth(null);
    			        height = image.getHeight(null);
    			        y1 = y1 + height;
    			        d.drawImage(Iconimage,x1,y1,null);
    				}
    				else
    				{
    					ImageIcon ii =new ImageIcon(this.getClass().getResource("IconImages/notIcon.png"));
    					Image Iconimage = ii.getImage();
    			    	width = image.getWidth(null);
    			        height = image.getHeight(null);
    			        y1 = y1 + height;
    					d.drawImage(Iconimage, x1,y1,null);
    				}
    			}
    		}
    	}
    }
    public void drawHUD(Graphics2D d)
    {
    	double perc = ((double)hp/100.0)*150;
    	int c = (int)perc;
    	{
    		d.setPaint(Color.red);
        	if(hp > 0)
        		d.fillRect(50, 525, c, 10);
    		d.drawRect(50, 525, 150, 10);
    		if(showIcon == true)
            	drawIcon(d);
    		
    		d.setColor(Color.WHITE);
    		Font small = new Font("Ariel", Font.BOLD, 14);
    		d.setFont(small);
    		d.drawString("HP: " + hp, 205, 535);
    	}
    	
    	{
    		perc = ((double)stamina/100.0)*150;
        	c = (int)perc;
    		d.setPaint(Color.blue);
        	if(stamina > 0)
        		d.fillRect(50, 550, c, 10);
    		d.drawRect(50, 550, 150, 10);
    		if(showIcon == true)
            	drawIcon(d);
    		
    		d.setColor(Color.WHITE);
    		Font small = new Font("Ariel", Font.BOLD, 14);
    		d.setFont(small);
    		d.drawString("ST: " + stamina, 205, 565);
    	}
    	
    }
}