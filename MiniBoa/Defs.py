#Class Definition File
import sqlite3
import random

#Class for Creating a Character
class CreateChar(object):
        def __init__(self):
                self.ID = 0
                self.Name = ''
                self.LName = ''
                self.Master_ID = 0
                self.Location = 1
                self.Health = 100
                self.Max_Health = 100
                self.Force = 100
                self.Max_Force = 100
                self.Stamina = 100
                self.Max_Stamina = 100
                self.Race = ''
                self.Class = ''
                self.Level = 1
                self.Skill_Points = 5
                self.Move_Points = 5 
                self.Experience = 0
                self.Alignment = 0
                self.Strength = 8
                self.Dexterity = 8
                self.Constitution = 8
                self.Intelligence = 8
                self.Wisdom = 8
                self.Charisma = 8
                self.Armor = 0
                self.WeaponOne = 0
                self.WeaponTwo = 0
        
                self.Gender = ''
                self.Age = 20
                self.Height = 5
                self.Weight = 150
                self.Skin_Color = ''
                self.Hair_Color = ''
                self.Title = ''
                self.Description = ''
                
        
        def CreateCharDB(self): #Creates all the database tables
                ##DONT YOU NEED TO CREATE THE DATABASE!
                #NOPE its right there in the Connect
                conn = sqlite3.connect(self.Name + '.db')
                c = conn.cursor()
                c.execute("""create table Character_Stats (ID Integer, Player_Name Text, Last_Name Text, Master_ID Integer, Location Integer, Health Integer, Max_Health Integer, Force Integer, Max_Force Integer, Stamina Integer, Max_Stamina Integer, Race Text, Class Text, Level Integer, Experience Integer, Skill_Points Integer, Move_Points Integer, Alignment Integer, Strength Integer, Dexterity Integer, Constitution Integer, Intelligence Integer, Wisdom Integer, Charisma Integer, Armor_ID Integer, WeaponOne_ID Integer, WeaponTwo_ID Integer, Deleted Integer)""")
                c.execute('''create table Character_Description (Gender Text, Age Integer, Height Integer, Weight Integer, Skin_Color Text, Hair_Color Text, Title Text, Description Text)''')
                c.execute('''create table Character_Inventory (ID Integer Primary Key, Item_ID Integer, Quantity Integer, Upgrade_One Integer, Upgrade_Two Integer, Upgrade_Three Integer)''')
                c.execute('''create table Character_Status (Stunned Integer, Wounded Integer, Darkness Integer, Encumbered Integer, Hasted Integer, Knockdown Integer, Slowed Integer, Poisoned Integer, Statis Integer)''')
                c.execute('''create table Character_Skill (Skill_ID Integer, Level Integer, Experience Integer)''')
                c.execute('''create table Character_Move (Move_ID integer, Level Integer, Experience Integer)''')
                c.execute('''create table Character_Reputation (Faction_ID Integer, Reputation Integer, Level Integer, Ranking Integer, Title Text)''')
                #Add Companion Tables in At some point and dansu
                conn.commit()

        def InsertInfo(self): #Inserts into the database tables
                conn = sqlite3.connect(self.Name + '.db')
                c = conn.cursor()
                c.execute("""insert into Character_Stats values ('""" + str(self.ID) + """', '""" + str(self.Name) + """','""" + str(self.LName) + """','""" + str(self.Master_ID) + """','""" + str(self.Location) + """','""" + str(self.Health) + """','""" + str(self.Max_Health) + """','""" + str(self.Force) + """','""" + str(self.Max_Force) + """','""" + str(self.Stamina) + """','""" + str(self.Max_Stamina) + """','""" + str(self.Race) + """','""" + str(self.Class) + """','""" + str(self.Level) + """','""" + str(self.Experience) + """','""" + str(self.Skill_Points) + """','""" + str(self.Move_Points) + """','""" + str(self.Alignment) + """','""" + str(self.Strength) + """','""" + str(self.Dexterity) + """','""" + str(self.Constitution) + """','""" + str(self.Intelligence) + """','""" + str(self.Wisdom) + """','""" + str(self.Charisma) + """','""" + str(self.Armor) + """','""" + str(self.WeaponOne) + """','""" + str(self.WeaponTwo) + """', '0')""")
                c.execute("""insert into Character_Description values ('""" + str(self.Gender) + """','""" + str(self.Age) + """','""" + str(self.Height) + """','""" + str(self.Weight) + """','""" + str(self.Skin_Color) + """','""" + str(self.Hair_Color) + """','""" + str(self.Title) + """','""" + str(self.Description) + """')""")
                #INsert Starter Items into Inventory and what not
                conn.commit()

#Location Class - Handles all the world locations
class Location(object):
        def __init__(self, LocID):
                #Add in Check for World Table Here Later
                conn = sqlite3.connect('Worlds.db')
                c = conn.cursor()
                Loc = c.execute("""SELECT * FROM World1 WHERE ID='"""+LocID+"""'""")
                for row in Loc.fetchall():
                        self.ID = str(row[0])
                        self.World_ID = str(row[1])
                        self.Name = str(row[2]) 
                        self.Desc = str(row[3]) 
                        self.Long_Desc = str(row[4])
                        self.Room_Type = str(row[5])
                        self.Effect_Type = str(row[6])
                        self.Door_Flag = str(row[7])
                        self.Key_ID = str(row[8])
                        self.Alignment = str(row[9])
                        self.Faction = str(row[10])
                        self.Weather_Type = str(row[11])
                        self.Environment_Type = str(row[12])
                        self.West = str(row[13]) 
                        self.North = str(row[14]) 
                        self.East = str(row[15]) 
                        self.South = str(row[16]) 
                        self.Up = str(row[17]) 
                        self.Down = str(row[18])
                self.MOB_LIST = []
                self.OBJECT_LIST = []
                self.CLIENT_LIST = []
                self.Dark = 0
                self.Death = 0

                Effects = self.Effect_Type.split(',', 50) 
                for Effect in Effects:
                        print Effect
                        if (Effect == "a"): #Dark
                                self.Dark = 1
                        if (Effect == "b"): #Death
                                self.Death = 1
                                
                                
#Character Class - Whats used for the client
class Character(object):
        def __init__(self, Name):
                conn = sqlite3.connect(Name + '.db')
                c = conn.cursor()
                Char = c.execute("SELECT * FROM Character_Stats")
                for row in Char.fetchall():
                        self.ID = str(row[0])
                        self.Name = Name
                        self.LName = str(row[2])
                        self.Master_ID = str(row[3])
                        self.Location = str(row[4])
                        self.Health = str(row[5])
                        self.Max_Health = str(row[6])
                        self.Force = str(row[7])
                        self.Max_Force = str(row[8])
                        self.Stamina = str(row[9])
                        self.Max_Stamina = str(row[10])
                        self.Race = str(row[11])
                        self.Class = str(row[12])
                        self.Level = str(row[13])
                        self.Experience = str(row[14])
                        self.Skill_Points = str(row[15])
                        self.Move_Points = str(row[16]) 
                        self.Alignment = str(row[17])
                        self.Strength = str(row[18])
                        self.Dexterity = str(row[19])
                        self.Constitution = str(row[20])
                        self.Intelligence = str(row[21])
                        self.Wisdom = str(row[22])
                        self.Charisma = str(row[23])
                        self.Armor = str(row[24])
                        self.WeaponOne = str(row[25])
                        self.WeaponTwo = str(row[26])
                        self.Inventory = []
                        if self.Armor != "0":
                                self.AC = Objects(self.Armor).Value
                        else:
                                self.AC = "0"
                        self.Skills = []
                        self.Gender = ''
                        self.Age = ''
                        self.Height = ''
                        self.Weight = ''
                        self.Skin_Color = ''
                        self.Hair_Color = ''
                        self.Title = ''
                        self.Description = ''
                        self.SCounter = 0
                        
                c.close()

        def Save(self): #Saves the current player information to the database
                conn = sqlite3.connect(self.Name + '.db')
                c = conn.cursor()
                c.execute("""UPDATE Character_Stats SET Master_ID='""" + str(self.Master_ID) + """',Location='""" + str(self.Location) + """',Health='""" + str(self.Health) + """',Max_Health='""" + str(self.Max_Health) + """',Force='""" + str(self.Force) + """',Max_Force='""" + str(self.Max_Force) + """',Stamina='""" + str(self.Stamina) + """',Max_Stamina='""" + str(self.Max_Stamina) + """',Level='""" + str(self.Level) + """',Experience='""" + str(self.Experience) + """',Skill_Points='""" + str(self.Skill_Points) + """',Move_Points='""" + str(self.Move_Points) + """',Alignment='""" + str(self.Alignment) + """',Strength='""" + str(self.Strength) + """',Dexterity='""" + str(self.Dexterity) + """',Constitution='""" + str(self.Constitution) + """',Intelligence='""" + str(self.Intelligence) + """',Wisdom='""" + str(self.Wisdom) + """',Charisma='""" + str(self.Charisma) + """',Armor_ID='""" + str(self.Armor) + """',WeaponOne_ID='""" + str(self.WeaponOne) + """',WeaponTwo_ID='""" + str(self.WeaponTwo) + """'""")
                c.execute("DELETE FROM Character_Inventory")
                for item in self.Inventory:
                        c.execute("INSERT INTO Character_Inventory values ('" + item.ID + "', Null, Null, Null)")
                        print item.ID + " was added."
                c.execute("DELETE FROM Character_skill")
                for skill in self.Skills:
                        c.execute("INSERT INTO Character_Skill values ('" + skill.ID +"','" + skill.Level + "','" + skill.Exp +"')")
                conn.commit()
                c.close()
                print(self.Name + " was saved!")

        def InventoryLoad(self): #Loads the players inventory 
                conn = sqlite3.connect(self.Name + '.db')
                c = conn.cursor()
                c.execute("""SELECT Item_ID FROM Character_Inventory""")
                for row in c:
                        tempItem = Objects(str(row[0]))
                        self.Inventory.append(tempItem)
        def SkillLoad(self): #Loads the players skills
                conn = sqlite3.connect(self.Name + '.db')
                c = conn.cursor()
                c.execute("""Select * FROM Character_skill""")
                for row in c.fetchall():
                        skill = Skill(str(row[0]))
                        self.Skills.append(skill)
                        skill.Level = str(row[1])
                        skill.Exp = str(row[2])
                        
        
        #def Moves(self): #Still being worked on

#Mob Class
class Mob(object):
        def __init__(self, MobID):
                conn = sqlite3.connect('Mobs.db')
                c = conn.cursor()
                Char = c.execute("SELECT * FROM Mobs1 WHERE Mob_ID='"+ MobID + "'")
                for row in Char.fetchall():
                        self.ID = str(row[0])
                        self.Name = str(row[1])
                        self.Alias = str(row[2])
                        self.Description = str(row[3])
                        self.Detail_Desc = str(row[4])
                        self.Race = str(row[5])
                        self.Class = str(row[6])
                        self.Alignment = str(row[7])
                        self.Level = str(row[8])
                        self.Faction = str(row[9])
                        self.Type = str(row[10])
                        self.Actions = str(row[11])
                        self.Affect = str(row[12])
                        self.Position = str(row[13])
                        self.Max_Entities = str(row[14])
                        self.Wander = str(row[15])
                        self.Health = str(row[16])
                        self.MaxHP = self.Health
                        self.Force = str(row[17])
                        self.Stamina = str(row[18])
                        self.Hostile = str(row[19])
                        self.AC = str(row[20])
                        self.BH_Damage = str(row[21])
                        self.Dialogue = str(row[22])
                        self.Gold = str(row[23])
                        self.Exp = str(row[24])
                        self.Strength = str(row[25])
                        self.Dexterity = str(row[26])
                        self.Constitution = str(row[27])
                        self.Intelligence = str(row[28])
                        self.Wisdom = str(row[29])
                        self.Charisma = str(row[30])
                        self.Inventory = str(row[31])
                        self.Armor = str(row[32])
                        self.WeaponOne = str(row[33])
                        self.WeaponTwo = str(row[34])
                        self.HostileTarget = ""
			self.TPosition = ""

	def GetPosition(self): #Gets the mobs position (Whether they're sleeping, standing, etc)
			if (self.Position == "0"):
				self.TPosition = "dead"
			elif (self.Position == "1"):
				self.TPosition = "mortally ounded"
			elif (self.Position == "2"):
				self.TPosition = "incapacitated"
			elif (self.Position == "3"):
				self.TPosition = "stunned"
			elif (self.Position == "4"):
				self.TPosition = "sleeping"
			elif (self.Position == "5"):
				self.TPosition = "resting"
			elif (self.Position == "6"):
				self.TPosition = "sitting"
			elif (self.Position == "7"):
				self.TPosition = "fighting"
			elif (self.Position == "8"):
				self.TPosition = "standing"
			else:
				self.TPosition = "This shouldn't happen"
#Skills class
class Skill(object):
        def __init__(self, SkillID):
                conn = sqlite3.connect('Skills.db')
                c = conn.cursor()
                skill = c.execute("SELECT * FROM skillsf WHERE Skill_ID ='" + SkillID + "'")
                for row in skill.fetchall():
                        self.ID = SkillID
                        self.Name = str(row[1])
                        self.Alias = str(row[2])
                        self.Desc = str(row[3])
                        self.APhrase = str(row[4])
                        self.FPUse = str(row[5])
                        self.Type = str(row[6])
                        self.Value = str(row[7])
                        self.Other = str(row[8])
                        self.Exp = ""
                        self.Level = ""
                

#Objects/Items Class                     
class Objects(object):
        def __init__(self, ObjectID): #InvID
                conn = sqlite3.connect('Obj.db')
                c = conn.cursor()
                #mobs1 is a typo, Fix later before release (Too much of a pain to do it now, since DB needs to be remade and etc)
                Obj = c.execute("SELECT * FROM mobs1 WHERE ID='"+ ObjectID + "'")
                for row in Obj.fetchall():
                        self.ID = ObjectID
                        self.InvID = 0 #InvID
                        self.Name = str(row[1])
                        self.Alias = str(row[2])
                        self.Short_Desc = str(row[3])
                        self.Long_Desc = str(row[4])
                        self.Type = str(row[5])
                        self.Other_Type = str(row[6])
                        self.Wear_Area = str(row[7])
                        self.Value = str(row[8])
                        self.Weight = str(row[9])
                        self.Cost = str(row[10])
                        self.Area_Effect = str(row[11])
                        self.Faction = str(row[12])
                        self.Max_Entires = str(row[13])
#Returns a life state based upon HP
def State(client, P):
        ratio = float(P.Health) / float(P.MaxHP)
        if(ratio == 1):
               client.send(P.Name + " is in great condition!\n\n")
        if(ratio < 1 and ratio > .74):
                client.send(P.Name + " has some minor bruises\n\n")
        if(ratio < .75 and ratio > .49):
                client.send(P.Name + " has some minor wounds\n\n")
        if(ratio < .50 and ratio > .25):
                client.send(P.Name + " has some serious wounds\n\n")
        if(ratio < .25 and ratio > 0):
                client.send(P.Name + " is near-death!\n\n")
#Used for attacking           
def Attack(client, A, D):  ##Don't forget to put (client.character) for the game.py code
        attackDice = random.randint(1,20)
        DDefend = (int(D.AC) + int(D.Dexterity))
        AHit = (int(A.Dexterity) + attackDice + int(A.Level))
        if(AHit >= DDefend):
                Weapon = Objects(A.WeaponOne)  
                if(A.WeaponOne == "0"):
                        damage = random.randint(1, 4) + AttribHandler(A.Strength)
                        client.send(A.Name + " hit " + D.Name + " for " + str(damage) + "\n\n")
                        D.Health = int(D.Health) - damage
                else:
                        damage = random.randint(1, int(Weapon.Value)) + AttribHandler(A.Strength)
                        client.send(A.Name + " hit " + D.Name + " for " + str(damage) + "\n\n")
                        D.Health = int(D.Health) - damage
        else:
                client.send(A.Name + " missed! \n\n")

def equip(A, I):
        if(I.Type == "1"):
                A.WeaponOne = I.ID
        if(I.Type == "2"):
                A.Armor = I.ID
                (A.AC) = int(A.AC) + int(I.Value)
                
def remove(A, I):
        if(I.Type == "1"):
                A.WeaponOne = "0"
        if(I.Type == "2"):
                A.Armor = "0"
                (A.AC) = int(A.AC) - int(I.Value)
                
def expCheck(client):
         base = 100 #Were going with 100 levels for now
         c = client.character
         levelup = (int(c.Level)*int(c.Level)) * base
         if(int(c.Experience) >= int(levelup)):
                 levelUp(client)
                 client.send("Congratz! You gained a level!\n")
         
def levelUp(client):
        c = client.character
        c.Max_Health = int(c.Max_Health) + 50
        c.Max_Health = str(c.Max_Health)
        c.Health = c.Max_Health
        c.Level = int(c.Level) + 1
        c.Level = str(c.Level)
        c.Skill_Points = int(c.Skill_Points) + 2
        c.Skill_Points = str(c.Skill_Points)
        getPoint = int(c.Level)%2

        if(getPoint == 0):
                c.Move_Points = int(c.Move_Points) + 1 + AttribHandler(c.Intelligence)
                c.Move_Points = str(c.Move_Points)


def AttribHandler(attr):

        multiplier = 0
        if(attr > 9 and attr < 12):
                multiplier = 1
        if(attr > 11 and attr < 14):
                multiplier = 2
        if(attr > 13 and attr < 16):
                multiplier = 3
        if(attr > 15 and attr < 18):
                multiplier = 4
        if(attr > 17 and attr < 20):
                multiplier = 5
        if(attr > 19 and attr < 22):
                multiplier = 6
        if(attr > 21 and attr < 24):
                multiplier = 7
        if(attr > 23 and attr < 26):
                multiplier = 8
        if(attr > 25 and attr < 28):
                multiplier = 9
        if(attr > 27 and attr < 30):
                multiplier = 10
                
        return multiplier
        
#Add attribute to character
def addAttrib(client, msg):
        
        c = client.character
        if(msg[1] == "str"):
                c.Strength = int(c.Strength) + 1
                c.Strength = str(c.Strength)
                c.Skill_Points = int(c.Skill_Points) - 1
                c.Skill_Points = str(c.Skill_Points)
                client.send("Your strength increases!\n")
        if(msg[1] == "dex"):
                c.Dexterity = int(c.Dexterity) + 1
                c.Dexterity = str(c.Dexterity)
                c.Skill_Points = int(c.Skill_Points) - 1
                c.Skill_Points = str(c.Skill_Points)
                client.send("Your dexterity increases!\n")
                
        if(msg[1] == "con"):
                c.Constitution = int(c.Constitution) + 1
                c.Constitution = str(c.Constitution)
                c.Skill_Points = int(c.Skill_Points) - 1
                c.Skill_Points = str(c.Skill_Points)
                client.send("Your constitution increases!\n")
                
        if(msg[1] == "int"):
                c.Intelligence = int(c.Intelligence) + 1
                c.Intelligence = str(c.Intelligence)
                c.Skill_Points = int(c.Skill_Points) - 1
                c.Skill_Points = str(c.Skill_Points)
                client.send("Your intelligence increases!\n")
                
        if(msg[1] == "wis"):
                c.Wisdom = int(c.Wisdom) + 1
                c.Wisdom = str(c.Wisdom)
                c.Skill_Points = int(c.Skill_Points) - 1
                c.Skill_Points = str(c.Skill_Points)
                client.send("Your wisdom increases!\n")
                
        if(msg[1] == "cha"):
                c.Charisma = int(c.Charisma) + 1
                c.Charisma = str(c.Charisma)
                c.Skill_Points = int(c.Skill_Points) - 1
                c.Skill_Points = str(c.Skill_Points)
                client.send("Your charisma increases!\n")


#prints information about the client              
def SelfPrint(client):
    c = client.character
    client.send(c.Name + " " + c.LName + "\nLevel: "+ str(c.Level) + "\nRace: ")
    client.send(c.Race + "\nClass: " + c.Class + "\nExp: " + str(c.Experience))
    client.send("\nTill next level: " + str((int(c.Level)*int(c.Level)) * 100) + "\n")
    
    client.send("\nAlignment: " + c.Alignment  +
                "\nStr: ")
    client.send(str(c.Strength) + "\nDex: " + c.Dexterity + "\nCon: ")
    client.send(c.Constitution + "\nIntel: " + c.Intelligence + "\nWis: ")
    client.send(c.Wisdom  + "\nCha: " + c.Charisma + "\n")

    client.send("Attribute Points Available: " + c.Skill_Points + "\n")
    client.send("Skill Points Available: " + c.Move_Points + "\n")
    
    client.send("You are currently wearing: \n")
    if(c.Armor != "0"):
            armor = Objects(c.Armor)
            client.send(armor.Name + ": " + armor.Value + "\n")
    else:
            client.send("Nothing!\n")
    client.send("Total AC: " + str(c.AC) + "\n\n")
            
    client.send("You are currently wielding: \n")
    
    if(c.WeaponOne != "0"):
            weapon = Objects(c.WeaponOne)
            client.send(weapon.Name + "\n\n")
    else:
            client.send("You are unarmed!\n\n")
        
