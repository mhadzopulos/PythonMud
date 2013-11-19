#Creates the databases for the game
import sqlite3


while 1:
        print ("Enter your command \n1) Create Game.db \n2) Make World DB and Insert 5 BS entries \n3) Make Mob DB with BS Entries \n4) Make Skills DB with Force Push")
        UserInput = raw_input("")

        if (UserInput == "1"): #Creates the game database, which contains the user data
                conn = sqlite3.connect('Game.db')
                c = conn.cursor()
                try:            
                        c.execute('''create table Master_User (ID Integer Primary Key, Player_Name Text, Password Text, Banned Integer)''')
                        conn.commit()
                        c.close()
                        print ("Game.db has successfully been created.")
                except:
                        print ("The Game.db already exists, you must delete it first.") 
        elif (UserInput == "2"): #Creates the worlds database, contains all the world, world_mob, and world_object data
                conn = sqlite3.connect('Worlds.db')
                c = conn.cursor()
        
                try:
                        c.execute('''create table World1 (ID Integer, World_ID Integer, Name Text, Short_Desc Text, Long_Desc Text, Room_Type Text, Effect_Type Text, Door_Flag Integer, Key_ID Integer, Alignment Integer, Faction Integer, Weather_Type Integer, Environment_Type Integer, West Integer, North Integer, East Integer, South Integer, Up Integer, Down Integer)''')
                        c.execute('''create table World1_Mobs (ID Integer, Location_ID Integer, World_ID Integer, Mob_ID Integer)''')
                        c.execute('''create table World1_Objects (ID Integer, Location_ID Integer, World_ID Integer, Object_ID Integer)''')
                        conn.commit()

                        c.execute("""insert into World1 values ('1', '0', 'Starter Location', 'You noticed many hobos dancing around pooping everywhere... What the Fuck', 'POOPS', Null, Null, Null, Null, '0', '0', Null, Null, '0', '5', '2', '0', '0', '0')""")
                        c.execute("""insert into World1 values ('2', '0', 'East of Start Area', 'BOOPBOOPBADOOPBADOOP', Null, Null, Null, Null, Null, '0', '0', Null, Null, '1', '0', '3', '0', '0', '0')""")
                        c.execute("""insert into World1 values ('3', '0', 'Sewage Plant', 'Captain Crunch walks the plank, little do they know hes a septic tank', Null, Null, Null, Null, Null, '0', '0', Null, Null, '2', '0', '0', '4', '0', '0')""")
                        c.execute("""insert into World1 values ('4', '0', 'Dansu House', 'A Place to get down and Dansu!', Null, Null, Null, Null, Null, '0', '0', Null, Null, '0', '3', '0', '0', '0', '0')""")
                        c.execute("""insert into World1 values ('5', '0', 'No Pants Allowed!', 'Some pantless guy walks up to you and says, Take off them pants Now!', Null, Null, Null, Null, Null, '0', '0', Null, Null, '0', '0', '0', '1', '0', '0')""")

                        c.execute("""insert into World1_Mobs values ('1', '1', '0', '1')""")
                        conn.commit()
                        c.close()
                        print ("Worlds.db has been made with some entries")
                except:
                        print ("Worlds.db already exists, delete it first")

        elif (UserInput == "3"): #Creates the mobs database, contains all the mob data
                conn = sqlite3.connect('Mobs.db')
                c = conn.cursor()

                try: 
                        c.execute('''create table Mobs1 (Mob_ID Integer Primary Key, Name Text, Alias_List Text, Description Text, Detail_Desc Text, Race Text, Class Text, Alignnment Text, Level Integer, Faction Integer, Type Integer, Actions Text, Affected Text, Position Text, Max_Entities Integer, Wander Integer, Health Integer, Force Integer, Stamina Integer, Hostile Integer, Armor_Class Integer, BH_Damage Integer, Dialogue Text, Gold Integer, Experience Integer, Strength Integer, Dexterity Integer, Constitution Integer, Intelligence Integer, Wisdom Integer, Charisma Integer, Intentory Text, Armor_ID Integer, WeaponOne_ID Integer, WeaponTwo_ID Integer)''')
                        conn.commit()
                        c.execute("""insert into Mobs1 values (Null, 'Random Hobo', 'Ran,Hobo,Random,Ho', 'Some Random Hobo Dancing and crapping around', 'Lalalalala', 'Human', 'NonForce', '-100', '1', '0', '0', Null, Null, Null, '0', '0', '10', '0', '0', '0', '10', '1', '0', '0', '0', '1', '1', '1', '1', '1', '1', Null, Null, Null, Null)""")
                        conn.commit()
                        c.close()
                        print ("Mobs.db has been made with some entries")
                except:
                        print ("Mobs.db already exists, delete it first")

                        
        elif (UserInput == "4"): #Creates the skills database, contains all the skill data
                conn = sqlite3.connect('Skills.db')
                c = conn.cursor()

                try:
                        c.execute('''Create table SkillsF (Skill_ID Integer Primary Key, Name Text, Alias_List Text, Description Text, AttackPhrase Text, ForcePoints Integer, Type Integer, Value Integer, Other Text)''')
                        conn.commit()
                        c.execute("""Insert into SkillsF values ('1', 'Force Push', 'fp,forcepush,push,pus', 'An forceful pressure that hurts a target', 'A great force pushes ', '10', '1', '15', Null)""")
                        conn.commit()
                        print("Skills.db has been made with an entry")
                except:
                        print("Skills.db already exists, delete it first")


