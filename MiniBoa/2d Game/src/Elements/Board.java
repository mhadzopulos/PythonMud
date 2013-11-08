package Elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.image.*;
import java.awt.Image;
import java.io.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Board extends JPanel implements ActionListener  // THIS IS WHERE MAJORITY OF THE ANIMATION TAKES PLACE
{

    private Timer timer;
    private Player player;
    private ArrayList<Player> players = new ArrayList<Player>();
    private ArrayList<Enemy> aliens;
    ArrayList<Damage> Damage = new ArrayList<Damage>();
    ArrayList FallingObjects = new ArrayList();
    //private ArrayList<Area> areas;
    private boolean ingame;
    private int B_WIDTH;
    private int B_HEIGHT;
    private Area l1 = new Area();
    private Wall w1 = new Wall(20, 500, 0, 500);
    private Wall w2 = new Wall(500, 20, 0, 0);
    private Wall w3 = new Wall(500,20,500,0);
    
    public Board() throws IOException{

        addKeyListener(new TAdapter()); //this is to listen for key strokes in the player class
        addMouseListener(new MAdapter());
        setFocusable(true);
        setBackground(Color.black);
        setDoubleBuffered(true);
        ingame = true;

        setSize(800, 600);

        player = new Player();
        
        player.setArea(l1);
        initAliens();
        initArea(l1);
        timer = new Timer(60, this);
        timer.start();
        players.add(player);
    }

    public void addNotify() {
        super.addNotify();
        B_WIDTH = getWidth();
        B_HEIGHT = getHeight();   
    }

    public void initAliens() {
        aliens = new ArrayList<Enemy>();

        {
           aliens.add(new Enemy(450,450)); //this is from old code, just puts a new enemy
            //aliens.add(new Enemy(500, 450));
        }
    }
    public void initArea(Area a) //Initiates areas, this is old code and will need to be updated to load more areas
    {
    	a.wall.add(w1);
    	a.wall.add(w2);
    	a.wall.add(w3);
    }

    public void drawArea(Area a, Graphics2D g) //used in the paint function, draws the walls for the areas
    {
    	if(a.getWall() != null)
    	{
    		ArrayList<Wall>walls = a.getWall();
    		for(Wall w: walls)
    		{
    			g.setPaint(Color.WHITE);
    			g.fillRect(w.x, w.y, w.width, w.height);
    			
    			
    		}
    	}
    }
    public void paint(Graphics g) {
        super.paint(g); //draws the screen

        if (ingame) { //makes sure you didn't game over

            Graphics2D g2d = (Graphics2D)g; //we use the graphics 2d class

            if (player.isVisible()) //draws the player facing right or left
            {
            	if(player.isRight == true)
            		g2d.drawImage(player.makeColorTransparent(Color.white), player.getX(), player.getY(),this);
            	else
            	{
            		BufferedImage i = player.makeColorTransparent(Color.white);
            		g2d.drawImage(player.horizontalflip(i), player.getX(), player.getY(),this);
            	}
                
            }

            ArrayList<SkillHandler> ms = player.getMissiles(); //skills to be drawn
            ArrayList<EnemySkillHandler> mse = new ArrayList<EnemySkillHandler>();
            
            drawArea(player.getArea(), g2d); //draws the area for the player
            
            for (int i = 0; i < ms.size(); i++) //handles each skill to be drawn and computed
            {
                SkillHandler m = (SkillHandler)ms.get(i);
                if(m.breakDown == true) //checks to see if the skill breaks
                {
                	for(int n = 0; n < m.gibs;n++)
                    {
                    	
                    	Gib d = m.rockDestroy(6);
                    	FallingObjects.add(d); //adds to the fallingobjects array
                    }
                	m.isVisible = false; //no longer draws the skill
                	
                }
                if(m.isVisible == false)
                {
                	ms.remove(i);
                	player.imageSet = false; //removes the skill and if the skill has ended, makes the player images change
                }
                else
                	g2d.drawImage(m.makeColorTransparent(Color.white), m.x, m.y, this); // otherwise draw the image to the screen
     
            }
            for(Enemy e: aliens)
            {
            	mse = e.missiles;
	            for (int i = 0; i < mse.size(); i++) //handles each skill to be drawn and computed
	            {
	                EnemySkillHandler m = (EnemySkillHandler)mse.get(i);
	                if(m.breakDown == true) //checks to see if the skill breaks
	                {
	                	for(int n = 0; n < m.gibs;n++)
	                    {
	                    	
	                    	Gib d = m.rockDestroy(6);
	                    	FallingObjects.add(d); //adds to the fallingobjects array
	                    }
	                	m.isVisible = false; //no longer draws the skill
	                	
	                }
	                if(m.isVisible == false)
	                {
	                	mse.remove(i);
	                	 //removes the skill and if the skill has ended, makes the player images change
	                }
	                else
	                	g2d.drawImage(m.makeColorTransparent(Color.white), m.x, m.y, this); // otherwise draw the image to the screen
	     
	            }
            }
            for (int i = 0; i < aliens.size(); i++) { //draws the enemies in the area
                Enemy a = (Enemy)aliens.get(i);
                if (a.isVisible())
                    g2d.drawImage(a.makeColorTransparent(Color.white), a.getX(), a.getY(), this);
            }
           //////////////////////////////////////////////////// 
            
            for(int i = 0; i < FallingObjects.size(); i++) //checks each gib in the array
            {
            	Gib a = (Gib)FallingObjects.get(i);
            	for(Wall w: player.getArea().getWall())
                {
                	
                	if(a.getBottom().intersects(w.wall.getBounds())) 
                	{
                		a.inAir = false;
                		if(a.gibTime > 100)
                			FallingObjects.remove(i); //checks the gib time, if its on the ground, it's not drawn and removed
                	}	
                	else if(a.inAir == true)
                	{
                		if(a.dy < 4)
                			a.dy +=1;
                		a.y = a.y + a.dy; //moves the gib down if its in the air
                	}
                }
            	
            	g2d.drawImage(a.makeColorTransparent(Color.WHITE), a.x, a.y, this); //draws the gib
            }
            
            
            g2d.setColor(Color.WHITE);
            g2d.drawString("Aliens left: " + aliens.size(), 5, 15); //draws text
            g2d.drawString("newx: " + player.newx, 5, 25);
            g2d.drawString("newy: " + player.newy, 5, 35);
            g2d.drawString("x " + player.getX(), 65, 25);
            g2d.drawString("y: " + player.getY(), 65, 35);
            g2d.drawString("dy: " + player.dy, 5, 45);
            g2d.drawString("time: " + player.timeCounter, 55, 45);
            g2d.drawString("fall: " + player.fallCounter, 55, 55);
            g2d.drawString("mousex: " + player.desx, 300, 15);
            g2d.drawString("mousey: " + player.desy, 300, 35);
            g2d.drawString("mousedy: " + player.desdy, 300, 55);
            g2d.drawString("mousex: " + player.desdx, 300, 75); 
            g2d.drawString("z:" + player.z, 300, 95);
            
            if(Damage != null)
            	printDamage(g2d);
            player.drawHUD(g2d);
            
        } 
        
        else {
            String msg = "Game Over";
            Font small = new Font("Helvetica", Font.BOLD, 14);
            FontMetrics metr = this.getFontMetrics(small);

            g.setColor(Color.white);
            g.setFont(small);
            g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2,
                         B_HEIGHT / 2);
        }

        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }
    
    
    public void printDamage(Graphics2D g) //function draws any damage created in the Damage Array
    {
    	for(Damage d: Damage)
    	{
    		if(d.time == 0)
    		{
    			Damage.remove(d);
    			break;
    		}
    		else
    		{
    			Font small = new Font("Helvetica", Font.BOLD, 20);
    			g.setColor(Color.red);
    			g.setFont(small);
    			g.drawString(d.damage.toString(), d.x,d.y);
    			d.y++;
    			d.time--;
    		}
    	}
    }

    public void actionPerformed(ActionEvent e){

        if (player.hp < 1) { //checks to make sure you didn't die
            ingame = false;
        }

        ArrayList<SkillHandler> ms = player.getMissiles();
        ArrayList<EnemySkillHandler> mse = new ArrayList<EnemySkillHandler>();
        
        for (int i = 0; i < ms.size(); i++) { //PLAYER moves each skill by using it's handler function
        	SkillHandler m = (SkillHandler) ms.get(i);
            if (m.isVisible) 
                m.Handler();
            else ms.remove(i);
        }
        for(Enemy q: aliens)
        {
        	mse = q.missiles;
	        for (int i = 0; i < mse.size(); i++) { //ENEMY moves each skill by using it's handler function
	        	EnemySkillHandler m = (EnemySkillHandler) mse.get(i);
	            if (m.isVisible) 
	                m.Handler();
	            else mse.remove(i);
	        }
        }
        for (int i = 0; i < aliens.size(); i++) 
        {
            Enemy a = (Enemy) aliens.get(i); //checks to see if an alien can move
            mse = a.missiles;
            for(SkillHandler sh: ms)
            {
            	if(sh.hitbox != null)
            	{
	            	if(a.getBounds().intersects(sh.getBounds()) && sh.isSolid) //if skill is solid, enemy cant
	            		a.canMove = false;
	            	
            	}
            }
            for(EnemySkillHandler sh: mse)
            {
            	if(sh.hitbox != null)
            	{
	            	if(a.getBounds().intersects(sh.getBounds()) && sh.isSolid) //if skill is solid, enemy cant
	            		a.canMove = false;
	            	
            	}
            }
            if(ms.size() == 0)
            	a.canMove = true;
            if(mse.size() == 0)
            	a.canMove = true;
            try
            {
	            if(a.canMove == true)
	            {
	            	if (a.isVisible()) //moves alien by using the move2 function, move is unoperable
	        			a.move(l1, player);
	        		else 
	        			aliens.remove(i);
	            }    
            }catch (IOException q) {
				// TODO Auto-generated catch block
				q.printStackTrace();
			}
            
            
        }
        
        player.move(player.getArea()); //draws the player by calling the move function within an area
        checkCollisions();
        repaint();  
        
    }

    public void checkCollisions() { //checks collisions between enemies, players, and skills

        Rectangle r3 = player.getBounds();
        Rectangle feet = player.getBottom();

        for (int j = 0; j<aliens.size(); j++) {
            Enemy a = (Enemy) aliens.get(j);
            Rectangle r2 = a.getBounds();

            if (r3.intersects(r2)) //if player intersects with enemy, nothing happens yet
            {
                
            }
        }
        

        ArrayList<SkillHandler> ms = player.getMissiles();
        ArrayList<EnemySkillHandler> mse = new ArrayList<EnemySkillHandler>();
        
        for(Wall w: player.getArea().getWall())
        {
        	Rectangle r2 = w.wall.getBounds();
        	
        	if(feet.intersects(r2)) 
        		player.setAir(false); //if player interesects with a wall, they don't fall

        }
        for (int i = 0; i < ms.size(); i++) 
        {
            SkillHandler m = (SkillHandler) ms.get(i);

            Rectangle r1 = m.getHitBox();
            for(Wall w: player.getArea().getWall())
            {
            	Rectangle r2 = w.wall;
            	if(r1 != null && r1.intersects(r2) && m.isBreakable)
            	{
            		m.isVisible = false;
            		for(int n = 0; n < m.gibs; n++)
            		{
            			Gib g = m.rockDestroy(6); //if a skill intersects a wall, it gibs if it can break
                    	FallingObjects.add(g);
                    	
            		}
            	}
            }
            
            for (int j = 0; j<aliens.size(); j++)
            {
                Enemy a = (Enemy) aliens.get(j);
                Rectangle r2 = a.getBounds();
                for(EnemySkillHandler esh: a.missiles)
                {
                	if(m.hitbox != null && esh.hitbox != null)
                	{
	                	if(esh.hitbox.intersects(m.hitbox) && esh.isBreakable) //needs to do a power check
	                	{
	                		esh.isVisible = false;
	                		for(int n = 0; n < esh.gibs; n++)
	                		{
	                			Gib g = esh.rockDestroy(6); //
	                        	FallingObjects.add(g);
	                        	
	                		}
	                		if(m.isBreakable)
	                		{
	                			m.isVisible = false;
	                    		for(int n = 0; n < m.gibs; n++)
	                    		{
	                    			Gib g = m.rockDestroy(6); //
	                            	FallingObjects.add(g);
	                            	
	                    		}
	                		}
	                	}
                	}
                }
                if (r1 != null && r1.intersects(r2) && m.hitbox != null)  //if a skill hits an enemy
                {
                	Random rand = new Random();
                	int x1 = rand.nextInt(a.width + 10) + a.getX() - 10;
                	Integer dmg = m.s.hit();
                	Damage d = new Damage(dmg, x1, a.getY()-30);
                	Damage.add(d); //do damage calculation and put in the array
                	a.hp = a.hp - dmg;
                	if(a.hp < 1)
                		a.setVisible(false);
                	if(m.isBreakable == true)
                	{
                		m.isVisible = false;
                		m.CheckCollisions(r2);
	                    for(int n = 0; n < m.gibs;n++)
	                    {
	                    	
	                    	Gib g = m.rockDestroy(6); //if the skill breaks, add to gibs
	                    	FallingObjects.add(g);
	                    }
                	}
                }
            }
        }
        for(Enemy q: aliens)
        {
        	mse = q.missiles;
	        for (int i = 0; i < mse.size(); i++) 
	        {
	        	EnemySkillHandler m = (EnemySkillHandler) mse.get(i);
	
	            Rectangle r1 = m.getHitBox();
	            for(Wall w: player.getArea().getWall())
	            {
	            	Rectangle r2 = w.wall;
	            	if(r1 != null && r1.intersects(r2) && m.isBreakable)
	            	{
	            		m.isVisible = false;
	            		
	            		for(int n = 0; n < m.gibs; n++)
	            		{
	            			Gib g = m.rockDestroy(6); //if a skill intersects a wall, it gibs if it can break
	                    	FallingObjects.add(g);
	                    	
	            		}
	            	}
	            }
	            for (int j = 0; j<players.size(); j++) {
	                Player a = (Player) players.get(j);
	                Rectangle r2 = a.getBounds();
	                Rectangle c1 = new Rectangle(a.x - 20, a.y - 20, a.width + 40, a.height + 40);
	                if(a.parrying == true && c1.intersects(r1))
	                {
	                	a.parry(m, q.x, q.y);
	                	m.isVisible = false;
	                	mse.remove(m);
	                	break;
	                }
	                if (r1 != null && r1.intersects(r2))  //if a skill hits an enemy
	                {
	                	Random rand = new Random();
	                	int x1 = rand.nextInt(a.width + 10) + a.getX() - 10;
	                	Integer dmg = m.s.hit();
	                	Damage d = new Damage(dmg, x1, a.getY()-30);
	                	Damage.add(d); //do damage calculation and put in the array
	                	a.hp = a.hp - dmg;
	                	if(a.hp < 1)
	                		a.setVisible(false);
	                	if(m.isBreakable == true)
	                	{
	                		m.isVisible = false;
	                		m.CheckCollisions(r2);
	                		mse.remove(i);
		                    for(int n = 0; n < m.gibs;n++)
		                    {
		                    	
		                    	Gib g = m.rockDestroy(6); //if the skill breaks, add to gibs
		                    	FallingObjects.add(g);
		                    }
	                	}
	                }
	            }
	        }
        }
    }

    private class TAdapter extends KeyAdapter { //if a player uses a key

        public void keyReleased(KeyEvent e) {
            
            try{
            	player.keyReleased(e);
        	}
        	catch (IOException i) {
				// TODO Auto-generated catch block
				i.printStackTrace();
			}
        }

        public void keyPressed(KeyEvent e){
        	try{
        		player.keyPressed(e);
        	}
        	catch (IOException i) {
				// TODO Auto-generated catch block
				i.printStackTrace();
			}
        }
    }
    private class MAdapter extends MouseAdapter //if a player uses a mouse button
    {
    	public void mouseClicked(MouseEvent e)
    	{
    		try{
    			player.mouseClicked(e);
    		}
    		catch (IOException i) {
				// TODO Auto-generated catch block
				i.printStackTrace();
			}
    		
    	}
    	public void mouseRelease(MouseEvent e)
    	{
    			player.mouseReleased(e);
    	}
    }
    
}