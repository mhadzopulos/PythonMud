package Elements;

import java.util.ArrayList;

public class Area 
{
	 ArrayList enemies, players, objects, exit;
	 ArrayList<Wall> wall;
	
	public Area()
	{
		enemies = new ArrayList();
		players = new ArrayList();
		objects = new ArrayList();
		exit = new ArrayList();
		wall = new ArrayList<Wall>();
	}
	public void addWall(Wall x)
	{
		wall.add(x);
	}
	public ArrayList<Wall> getWall()
	{
		return wall;
	}
}
