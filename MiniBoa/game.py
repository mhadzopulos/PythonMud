#!/usr/bin/env python
#------------------------------------------------------------------------------
#   chat_demo.py
#   Copyright 2009 Jim Storch
#   Licensed under the Apache License, Version 2.0 (the "License"); you may
#   not use this file except in compliance with the License. You may obtain a
#   copy of the License at http://www.apache.org/licenses/LICENSE-2.0
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
#   WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
#   License for the specific language governing permissions and limitations
#   under the License.
#------------------------------------------------------------------------------

"""
Chat Room Demo for Miniboa.
"""

from miniboa import TelnetServer
from miniboa.xterm import *
import sqlite3
import hashlib
from Defs import *
import threading
from threading import Thread, Event
import random

IDLE_TIMEOUT = 300
CLIENT_LIST = []
USER_LIST = []
WORLD_LIST = []
TIMER_LIST = []
numPlayers = 0
SERVER_RUN = True

###Some Variable Definitions###

#ITEMS#

#Healing = 0#
#Weapon = 1#
#Armor = 2#

#WEAR AREAS - WEAPONS#
#Small Weapon(one handed) = 0 (or 1)
#Large Weapon(two handed) = 2

#BASIC ARMOR (AC)
#Head = 3
#Chest = 4
#Legs = 5
#Arms = 6

#ACCESSORIES
#Wrist 1,2      = 7,8
#Finger 1,2     = 9,10
#Neck           = 11
#About the Body = 12
#Waiste         = 13
#Feet           = 14
#Ankle 1,2      = 15,16
###################################################

class RepeatTimer(Thread):
        def __init__(self,interval,function, iterations, args = [],
                     kwargs = {}):
                Thread.__init__(self)
                self.interval = interval
                self.function = function
                #self.run = run
                self.iterations = iterations
                self.args = args
                self.kwargs = kwargs
                self.finished = Event()

        def run(self):
            count = 0
            while not self.finished.is_set() and (self.iterations <= 0 or
                                                      count < self.iterations):
                self.finished.wait(self.interval)
                if not self.finished.is_set():
                    self.function(*self.args, **self.kwargs)
                    count +=1
        def cancel(self):
            self.finished.set()



#######################
#####LOAD WORLD########
     ###########


def loadWorld(): #Loads the world and saves it to a list
        conn = sqlite3.connect('Worlds.db') #Connect to the World DB
        c = conn.cursor()
        c.execute("""select ID from world1""") 
        conn2 = sqlite3.connect('Worlds.db')
        c2 = conn.cursor()

        for row in c: #Retrieve the world information
                tempLoc = Location(str(row[0]))
                Test = c2.execute("select Mob_ID from world1_mobs WHERE Location_ID='" + str(row[0]) + "'")
                for row2 in Test.fetchall(): #Retrieve the mobs and save them into a List
                        tempMob = Mob(str(row2[0]))
                        tempLoc.MOB_LIST.append(tempMob)
                        print("You added " + tempMob.Name)
                Test2 = c2.execute("select Object_ID from world1_objects WHERE Location_ID='" + str(row[0]) + "'")
                for row3 in Test2.fetchall(): #Retrieve the objects and save them into a List
                        tempObj = Objects(str(row3[0]))
                        tempLoc.OBJECT_LIST.append(tempObj)
                        print("You added a " + tempObj.Name)
                WORLD_LIST.append(tempLoc) #Append it to the World List
        print("Loaded Locations")


def displayWorld(client): #Displays the world
        if (client.display == 1)
        	#Check if the location is special (Death or Dark for now)
                if (WORLD_LIST[client.location].Death == 1): #Death Location
                        client.send("\nShoot you died! The Crappiest Way to die, walking and then go into a Dead Zone\n")
                elif (WORLD_LIST[client.location].Dark == 1): #Location is Dark
                        client.send("\nIts Dark you can't see a thing\n")
                else: 
                        client.display = 0
                        def LMobs(client): #Retrieves the mob list for this world location
                                MobList = WORLD_LIST[client.location].MOB_LIST
                                for Mob in MobList: #Displays the mobs for this location
					Mob.GetPosition()
                                        client.send("^gYou see " + Mob.Name + " is " + Mob.TPosition + " here.\n^~")
                        def LClients(client): #Retrieves other players in this location
                                ClientList = WORLD_LIST[client.location].CLIENT_LIST
                                for clients in ClientList: #Displays the other players for this location
					if client.character.ID != clients.character.ID:
                                        	client.send("^RYou see " + clients.character.Name + " dancing around. \n^~")
                        def LObjects(client): #Retrieves the objects for this location
                                objList = WORLD_LIST[client.location].OBJECT_LIST
                                for Objects in objList: #Displays the objects for this location
                                        client.send("A " + Objects.Name + " has been left here.\n")

			def Arrivals(client): #Checks for new clients arriving at this location
				if (client.location != client.prevlocation or (client.location == 0 and client.prevlocation == 0)):
		                        ClientList = WORLD_LIST[client.location].CLIENT_LIST
		                        for clients in ClientList:
						if client.character.ID != clients.character.ID:
		                                	clients.send("^c" + client.character.Name + " has arrived. \n^~")
			def Leaving(client): #Checks for new clients leaving this location
				if (client.location != client.prevlocation):
		                        ClientList = WORLD_LIST[client.prevlocation].CLIENT_LIST
		                        for clients in ClientList:
						if client.character.ID != clients.character.ID:
		                                	clients.send("^c" + client.character.Name + " has left. \n^~")

                        client.send("\n" + WORLD_LIST[client.location].Name + "\n" + WORLD_LIST[client.location].Desc + "\n")
                        client.send("\n")
                        client.send("\n")
                        LMobs(client)
                        LClients(client)
                        LObjects(client)
                        client.send("\n")
			Arrivals(client)
			Leaving(client)
                        client.send("\n")
                        #Send the different available dirrections to the user
                        if(WORLD_LIST[client.location].North != "0"):
                                client.send("n ")

                        if(WORLD_LIST[client.location].South != "0"):
                                client.send("s ")

                        if(WORLD_LIST[client.location].West != "0"):
                                client.send("w ")

                        if(WORLD_LIST[client.location].East != "0"):
                                client.send("e ")
                        client.send("\n")
                        displayHealth(client)
			client.prevlocation = client.location
			
def displayHealth(client): #Displays the users stats (Health, Force Power, and Movement)
        
        client.send("HP - " + str(client.character.Health) + "/" + str(client.character.Max_Health)
                    + ", FP - " + client.character.Force + "/" + client.character.Max_Force
                    + ", movement - " + client.character.Move_Points + "\n\n\n")

def login(client): #Used for the players login. 
        """
        check whether the client wants to login, disconnect, etc
        """
        global SERVER_RUN
        msg = client.get_command()
        conn = sqlite3.connect('Game.db')
        c =conn.cursor()
        name = msg
        password = msg

        if(client.loggedin == 3): #Login into account and start character creation
                client.password = password
                c.execute("""insert into Master_User values (Null, '""" + client.name + """','""" + client.password + """','0')""")
                conn.commit()
                client.send("Now lets get down to character creation!\n Press Enter to continue \n")
                client.charcreate = 1
                #client.loggedin = 7

        if (client.loggedin == 2): #Set Password
                if(msg == "y"):
                        client.loggedin = 3
                        client.send("What do you want your password to be?\n")


                if(msg == "n"):
                        client.loggedin = 0
                        client.send("What's your name then?!\n")

        if(client.loggedin == 1): #Check inputed password
                Stuff = c.execute("""Select Password from master_user where Player_Name='""" + client.name + """'""")
                corrpass = False
                client.send("checking password... \n")
                row = Stuff.fetchone()
                client.send(str(row[0]) + "\n")

                if (password == str(row[0])): #Password was correct
                        client.password = password
                        client.send("Welcome back! \n")
                        client.character = Character(client.name)
                        client.character.InventoryLoad()
                        client.character.SkillLoad()
                        WORLD_LIST[0].CLIENT_LIST.append(client)
                        displayWorld(client)
                        client.loggedin = 7
                else: #Password failed
                        client.send("Wrong password! \n")
                        client.send("Password: ")

        if(client.loggedin == 0): #Check the login information for the player, if the name exists add new user, else login

                Stuff = c.execute("""Select Player_Name from master_user where Player_Name='""" + name + """'""")
                found = False

                for row in Stuff.fetchall():
                        client.send("checking rows...\n")
                        print(str(row[0]))
                        client.send(str(row[0]) + "\n")

                        if (name == str(row[0])):
                                client.name = name
                                client.send("Password: \n")
                                client.loggedin = 1
                                found = True
                                break

                if(found == False):
                        client.send("That name is not in the system \n")
                        client.send("Are you a new user? y/n \n")
                        client.name = name
                        client.loggedin = 2

        if (msg == "2"):
                 client.active = False



def CharacterCreation(client): #Create their new character
        global SERVER_RUN
        msg = client.get_command()
        NewChar = CreateChar()
        print (msg)

	#Character creation, Takes input from previous charcreate and inputs it while asking the next question
        if (client.charcreate == 11): #Create the new character they've made

                Desc = msg
                client.NewChar.Description = Desc
                client.charcreate = 0
                client.loggedin = 7
                client.send("""You have created, """ + str(client.NewChar.Name) + """ """ + str(client.NewChar.LName) + """, The """ + str(client.NewChar.Class) + """ using """ + str(client.NewChar.Gender) + """ """ + str(client.NewChar.Race) + """ """ + str(client.NewChar.Description))
                client.NewChar.CreateCharDB()
                client.NewChar.InsertInfo()
                client.character = Character(client.name)
		WORLD_LIST[0].CLIENT_LIST.append(client)

        if (client.charcreate == 10): #input Character Hair Color

                client.NewChar.Hair_Color = 'Blue'
                client.charcreate = 11
                client.send("Enter your character Description: \n")
        if (client.charcreate == 9): #input Character skin color

                client.NewChar.Skin_Color = "Green"
                client.charcreate = 10
                client.send("Select the number for your hair color: \n1)Blue \n")
        if (client.charcreate == 8): #Input character weight
                Weight = msg
                try:
                        int(Weight)
                        if (int(Weight) >= 100) and (int(Weight) <= 500):
                                client.NewChar.Weight = Weight
                                client.charcreate = 9
                                client.send("Select the number for your skin color: \n1)Green \n")
                        else:
                                client.send("Invalid input, Try Again \n")

                except ValueError:
                       client.send("Invalid input, Try Again \n")
        if (client.charcreate == 7): #Input character height
                Height = msg
                try:
                        int(Height)
                        if (int(Height) >= 4) and (int(Height) <= 8):
                                client.NewChar.Height = Height
                                client.charcreate = 8
                                client.send("Enter your characters weight: (100-500) \n")
                        else:
                                client.send("Invalid input, Try Again \n")

                except ValueError:
                       client.send("Invalid input, Try Again \n")
        if (client.charcreate == 6): #Input character age
                Age = msg
                try:
                        int(Age)
                        if (int(Age) >= 20) and (int(Age) <= 50):
                                client.NewChar.Age = Age
                                client.charcreate = 7
                                client.send("Enter your characters height: (4-8) \n")
                        else:
                                client.send("Invalid input, Try Again \n")

                except ValueError:
                       client.send("Invalid input, Try Again \n")
        if (client.charcreate == 5.5): #Input characters skills
                SkillID = msg

                try:
                        int(SkillID)
                        if (client.NewChar.Skill_Points >= 1) and ((int(SkillID) >= 1) and (int(SkillID) <= 6)):
                                if (SkillID == "1"):
                                        client.NewChar.Skill_Points -= 1
                                        client.NewChar.Strength += 1
                                elif (SkillID == "2"):
                                        client.NewChar.Skill_Points -= 1
                                        client.NewChar.Dexterity += 1
                                elif (SkillID == "3"):
                                        client.NewChar.Skill_Points -= 1
                                        client.NewChar.Constitution += 1
                                elif (SkillID == "4"):
                                        client.NewChar.Skill_Points -= 1
                                        client.NewChar.Intelligence += 1
                                elif (SkillID == "5"):
                                        client.NewChar.Skill_Points -= 1
                                        client.NewChar.Wisdom += 1
                                elif (SkillID == "6"):
                                        client.NewChar.Skill_Points -= 1
                                        client.NewChar.Charisma += 1
                                client.charcreate = 5.5
                                client.send("Select stat up upgrade: (" + str(client.NewChar.Skill_Points) + " Skill Points left) \n1)Strength (Current " + str(client.NewChar.Strength) + ") \n2)Dexterity (Current " + str(client.NewChar.Dexterity) + ") \n3)Constitution (Current " + str(client.NewChar.Constitution) + ") \n4)Intelligence (Current " + str(client.NewChar.Intelligence) + ") \n5)Wisdom (Current " + str(client.NewChar.Wisdom) + ") \n6)Charisma (Current " + str(client.NewChar.Charisma) + ") \n7)Continue (Spend the remaining later)\n")

                        elif (client.NewChar.Skill_Points == 0) or (int(SkillID) == 7):
                                client.charcreate = 6
                                client.send("Enter your characters age: (20-50) \n")
                        else:
                                client.send("Invalid input, Try Again \n")
                except ValueError:
                       client.send("Invalid input, Try Again \n")
        if (client.charcreate == 5): #Input characters gender
                GenderID = msg
                if GenderID == "1":
                        Gender = "Male"
                elif GenderID == "2":
                        Gender = "Female"

                try:
                        int(GenderID)
                        if (int(GenderID) >= 1) and (int(GenderID) <= 2):
                                client.NewChar.Gender = Gender
                                client.charcreate = 5.5
                                client.send("Select stat up upgrade: (" + str(client.NewChar.Skill_Points) + " Skill Points left) \n1)Strength (Current " + str(client.NewChar.Strength) + ") \n2)Dexterity (Current " + str(client.NewChar.Dexterity) + ") \n3)Constitution (Current " + str(client.NewChar.Constitution) + ") \n4)Intelligence (Current " + str(client.NewChar.Intelligence) + ") \n5)Wisdom (Current " + str(client.NewChar.Wisdom) + ") \n6)Charisma (Current " + str(client.NewChar.Charisma) + ") \n7)Continue (Spend the remaining later)\n")
                        else:
                                client.send("Invalid input, Try Again \n")

                except ValueError:
                       client.send("Invalid input, Try Again \n")

        if (client.charcreate == 4): #Input characters force ability
                ClassID = msg

                if ClassID == "1":
                        Class = "Force"
                elif ClassID == "2":
                        Class = "NonForce"

                try:
                        int(ClassID)
                        if (int(ClassID) >= 1) and (int(ClassID) <= 2):
                                client.NewChar.Class = Class
                                client.charcreate = 5
                                client.send("Select the number for your character gender: \n1)Male \n2)Female \n")
                        else:
                                client.send("Invalid Class ID, Try Again \n")

                except ValueError:
                       client.send("Invalid Class ID, Try Again \n")
        #Addin JAzz for Stats (Strength, Dex, Etc)
        if (client.charcreate == 3): #Input characters race
                RaceID = msg

                if RaceID == "1":
                        Race = "Human"
                elif RaceID == "2":
                        Race = "Twilek"
                elif RaceID == "3":
                        Race = "Rodian"
                elif RaceID == "4":
                        Race = "Zabrak"
                elif RaceID == "5":
                        Race = "Cathar"

                try:
                        int(RaceID)
                        if (int(RaceID) >= 1) and (int(RaceID) <= 5):
                                client.NewChar.Race = Race
                                client.charcreate = 4
                                client.send("Select the number for your class: \n1)Force User \n2)Non-Force User \n")
                        else:
                                client.send("Invalid Race ID, Try Again \n")
                except ValueError:
                       client.send("Invalid Race ID, Try Again \n")

        if (client.charcreate == 2): #Input characters last name
                client.NewChar.Name = client.name
                client.NewChar.LName = msg
                client.charcreate = 3
                client.send("Select the number for your race: \n1)Human \n2)TwiLek \n3)Rodian \n4)Zabrak \n5)Cathar \n")

        if (client.charcreate == 1): #Input characters name
                client.NewChar = CreateChar()
                client.send("Enter your characters Last Name: \n")

                conn = sqlite3.connect('Game.db')
                c = conn.cursor()
                PID = c.execute("SELECT ID FROM Master_User WHERE Player_Name='" + client.name + "'")
                row = PID.fetchone()
                print (row[0])
                client.NewChar.ID = str(row[0])
                client.charcreate = 2



def on_connect(client):
    """
    Sample on_connect function.
    Handles new connections.
    """
    print ("++ Opened connection to %s" % client.addrport())
    #broadcast('%s joins the conversation.\n' % client.addrport() )
    broadcast('Someone has returned to the galaxy.\n')
    client.addrport()
    CLIENT_LIST.append(client)
    client.send("Welcome to the Chat Server, %s.\n" % client.addrport() )
    client.send("What's your name? (type 2 or bye to quit)\n")


def on_disconnect(client):
    """
    Sample on_disconnect function.
    Handles lost connections.
    """
    print ("-- Lost connection to %s" % client.addrport())
    CLIENT_LIST.remove(client)
    #broadcast('%s leaves the conversation.\n' % client.addrport() )
    broadcast('Someone has left the galaxy.\n')


def kick_idle():
    """
    Looks for idle clients and disconnects them by setting active to False.
    """
    ## Who hasn't been typing?
    for client in CLIENT_LIST:
        if client.idle() > IDLE_TIMEOUT:
            print('-- Kicking idle lobby client from %s' % client.addrport())
            client.active = False


def process_clients():
    """
    Check each client, if client.cmd_ready == True then there is a line of
    input available via client.get_command().
    """
    for client in CLIENT_LIST:
            
        if client.active and client.cmd_ready:
            ## If the client sends input echo it to the chat room
                if(client.loggedin == 7):

                    chat(client)
                elif (client.charcreate > 0 and client.loggedin != 7):
                    CharacterCreation(client)
                else:
                    login(client)


def broadcast(msg):
    """
    Send msg to every client.
    """
    for client in CLIENT_LIST:
        client.send(msg)

def sendlocal(CL, client, msg):
	for clients in CL:
		if (clients.character.Name != client.character.Name):
			clients.send(msg)


def chat(client):
    """
    Echo whatever client types to everyone.
    """
    global SERVER_RUN
    msg = client.get_command()
    client.send("\n")##'prints to you'
    client.character.Save()
    print ('%s says, "%s"' % (client.addrport(), msg)) ##prints to the server"""
    #for guest in CLIENT_LIST:
     #   if guest != client:
      #      guest.send('%s says, %s\n' % (client.addrport(), msg)) ##prints to everyone else'

    inputHandler(msg, client)

    displayWorld(client)

    cmd = msg.lower()               

    if cmd == 'bye' or cmd == 'quit':
        client.send("You leave the galaxy to go far far away...")
        
        client.active = False       ## bye = disconnect

    elif cmd == 'shutdown':
        SERVER_RUN = False          ## shutdown == stop the server

def SkillHandler(client, sk, msg):
        
        ###DAMAGE TO MOB SKILLS####
        sizeof = 0
        for index in msg:
                sizeof = sizeof + 1
        found = False
        if(sk.Type == "1" and sizeof > 1 and (int(client.character.Force) >= int(sk.FPUse))):
                MobList = WORLD_LIST[client.location].MOB_LIST
                for Mob in MobList:
                        m = Mob
                        Aliases = m.Alias.split(',',500)
                        for Alias in Aliases:
                                if Alias.lower() == msg[1]:
                                        
                                        client.character.Force = int(client.character.Force) - int(sk.FPUse)
                                        client.character.Force = str(client.character.Force)
                                        
                                        damage = random.randint(1, int(sk.Value))
                                        m.Health = int(m.Health) - damage
                                        client.send(sk.APhrase + m.Name + " for " + str(damage)+ " damage!\n\n")
                                        
                                        m.Hostile = True
                                        m.HostileTarget = client.character.Name
                                        
                                        found = True
                                        
                                        sk.Exp = int(sk.Exp) + 1
                                        sk.Exp = str(sk.Exp)
                                        client.character.SCounter = 5
                                        if(int(m.Health) <= 0):
                                                client.send("You killed " + m.Name + "\n\n")
                                                WORLD_LIST[client.location].MOB_LIST.remove(m)
                                                client.character.Experience = int(client.character.Experience) + int(m.Exp)
                                                expCheck(client)
                                                break
                                        else:
                                                State(client, m)
                                                displayHealth(client)
                                                break
                        if found == True:
                                break


def inputHandler(inp, client):  ##This is where you put all your keywords + code

#########################
###This is for moving!###
#########################

    Saying = inp.split(' ', 1)
    Comming = inp.split(' ', 2) #These 2 are just for he messages, otherwise they get split wrong
    inp = inp.lower()
    msg = inp.split(' ', 5) #Will need to add in checks later, since if they just do att, it will error since msg[1] does not exist. 
    sizeof = 0
    for index in msg:
                sizeof = sizeof + 1
                
    for skill in client.character.Skills:
            if(client.character.SCounter != 0):
                    break
            sk = skill
            Aliases = sk.Alias.split(',', 500)
            for alias in Aliases:
                if(msg[0] == alias.lower()):
                        SkillHandler(client, sk, msg)
                        break
            
    if(msg[0] == "n" or msg[0] == "north"):
        if(WORLD_LIST[client.location].North != "0"):
                client.display = 1
                WORLD_LIST[client.location].CLIENT_LIST.remove(client)
                client.location = int(WORLD_LIST[client.location].North)-1
                WORLD_LIST[client.location].CLIENT_LIST.append(client)
                client.send("You go north\n")

        else:
                client.send("You can't go that way!\n")
    if(msg[0] == "w" or msg[0] == "west"):
        if(WORLD_LIST[client.location].West != "0"):
                client.display = 1
                WORLD_LIST[client.location].CLIENT_LIST.remove(client)
                client.location = int(WORLD_LIST[client.location].West)-1
                WORLD_LIST[client.location].CLIENT_LIST.append(client)
                client.send("You go west\n")
        else:
                client.send("You can't go that way!\n")
    if(msg[0] == "s" or msg[0] == "south"):
        if(WORLD_LIST[client.location].South != "0"):
                client.display = 1
                WORLD_LIST[client.location].CLIENT_LIST.remove(client)
                client.location = int(WORLD_LIST[client.location].South)-1
                WORLD_LIST[client.location].CLIENT_LIST.append(client)
                client.send("You go south\n")
        else:
                client.send("You can't go that way!\n")
    if(msg[0] == "e" or msg[0] == "east"):
        if(WORLD_LIST[client.location].East != "0"):
                client.display = 1
                WORLD_LIST[client.location].CLIENT_LIST.remove(client)
                client.location = int(WORLD_LIST[client.location].East)-1
                WORLD_LIST[client.location].CLIENT_LIST.append(client)
                client.send("You go east\n")
        else:
                client.send("You can't go that way!\n")

###########################################################################
##########---------ITEM COMMANDS------#####################################
         ##############################

                
    if(msg[0] == "inventory" or msg[0] == "inv"): #Check Inventory Command
        client.send("You have in your pack: \n\n")

        for row in client.character.Inventory:
                item = row
                client.send(item.Name + "\n")

    if(msg[0] == "take" and sizeof > 1): #Take Command
        found = False
        tempLoc = WORLD_LIST[client.location]
        for row in tempLoc.OBJECT_LIST:
                item = row
                Aliases = item.Alias.split(',', 500)
                for alias in Aliases:
                        if(msg[1] == alias.lower()):
                                client.character.Inventory.append(item)
                                tempLoc.OBJECT_LIST.remove(item)
                                client.send("You picked up the  " + item.Name +"\n\n")
                                found = True
                                break
                if found == True:
                        break

    if(msg[0] == "use" and sizeof > 1): #Use command

        found = False
        for row in client.character.Inventory:
                item = row
                Aliases = item.Alias.split(',', 500)
                for alias in Aliases:
                        if(msg[1] == alias.lower()):
                                if item.Type == "0": ##Only uses on yourself for now...
                                        healed = int(client.character.Health) +int(item.Value)
                                        if(healed < int(client.character.Max_Health)):
                                                client.character.Health = healed
                                                client.character.Inventory.remove(item)
                                                client.send("You healed yourself!\n\n")
                                                found = True
                                                break
                                        else:
                                                client.character.Health = int(client.character.Max_Health)
                                                client.character.Inventory.remove(item)
                                                client.send("You healed yourself!\n\n")
                                                found = True
                                                break
                if found == True:
                        break

    if(msg[0] == "equip" and sizeof > 1): #Equip Command
            found = False
            
            for row in client.character.Inventory:
                    item = row
                    Aliases = item.Alias.split(',',500)
                    for Alias in Aliases:
                            
                            if(msg[1] == Alias.lower()):
                                    if (item.Type == "1" or item.Type == "2"):
                                            if(client.character.WeaponOne != "0" and item.Type == "1"):
                                                remove(client.character,item)
                                            if(client.character.Armor != "0" and item.Type == "2"):
                                                remove(client.character,item)    
                                            equip(client.character, item)
                                            found = True
                                            if(item.Type == "1"):
                                                    client.send("You are now wielding " +
                                                                item.Name + "\n\n")
                                                    break
                                                        
                                            else:
                                                    client.send("You are now wearing " +
                                                                item.Name + "\n\n")
                                                    break
                    if found == True:
                            break
                                                
    if(msg[0] == "remove" and sizeof > 1): #Remove Command

            found = False
            for row in client.character.Inventory:
                    item = row
                    Aliases = item.Alias.split(',',500)
                    for Alias in Aliases:
                            
                            if(msg[1] == Alias.lower()):
                                    if (item.Type == "1" or item.Type == "2"):
                                            remove(client.character, item)
                                            found = True
                                            if(item.Type == "1"):
                                                    client.send("You removed the " +
                                                                item.Name + "\n\n")
                                                    break
                                            else:
                                                    client.send("You took off the " +
                                                                item.Name + "\n\n")
                                                    break
                    if found == True:
                             break

                        
    if(msg[0] == "drop" and sizeof > 1): #Drop Command

            
            found = False
            for row in client.character.Inventory:
                    item = row
                    Aliases = item.Alias.split(',', 500)
                    for Alias in Aliases:
                            if(msg[1] == Alias.lower()):
                                    if(item.Type == "1" or item.Type == "2"):
                                            
                                            if(item.Type == "1" and client.character.WeaponOne != "0"):
                                                    remove(client.character, item)
                                                    client.send("You removed and dropped the " + item.Name + "\n\n")
                                            if(item.Type == "2" and client.character.Armor != "0"):
                                                    remove(client.character, item)
                                                    client.send("You removed and dropped the " + item.Name + "\n\n")
                                            else:
                                                    client.send("You dropped the " + item.Name + "\n\n")
                                                    
                                            WORLD_LIST[client.location].OBJECT_LIST.append(item)
                                            client.character.Inventory.remove(item)
                                            found = True
                                            break
                                    else:
                                            WORLD_LIST[client.location].OBJECT_LIST.append(item)
                                            client.character.Inventory.remove(item)
                                            client.send("You dropped the " + item.Name + "\n\n")
                                            found = True
                                            break
                    if(found == True):
                        break
                                            

#################################################################
####------ATTACK / SKILLS-------#################################
################################

                                                    
    if((msg[0] == "att" or msg[0] == "hit") and sizeof > 1): #Attack or Hit command

        found = False
        MobList = WORLD_LIST[client.location].MOB_LIST
        for Mob in MobList:
                tempMob = Mob
                Aliases = tempMob.Alias.split(',',500)
                for Alias in Aliases:
                        
                        if Alias.lower() == msg[1]:
                                client.send("You attack " + tempMob.Name + "\n")
                                Attack(client, client.character, tempMob)
                                tempMob.Hostile = True
                                tempMob.HostileTarget = client.character.Name
                                found = True
                                if(int(tempMob.Health) <= 0):
                                        client.send("You killed " + tempMob.Name + "\n\n")
                                        client.character.Experience = int(client.character.Experience) + int(tempMob.Exp)
                                        WORLD_LIST[client.location].MOB_LIST.remove(tempMob)
                                        expCheck(client)
                                        break
                                else:
                                        State(client, tempMob)
                                        break
                if found == True:
                        break
                                
                                

#################################################################
####------HELP FUNCTIONS--------#################################
################################

                                        
    if(msg[0] == "self"): #Self Command
            SelfPrint(client)
                    
    if(msg[0] == "skill"): #Skill Command
            c = client.character
            client.send("Your skills: \n")
            for skill in c.Skills:
                    client.send(skill.Name + ": " +"lvl " + skill.Level + " - " + skill.Exp + "%, " + skill.FPUse +
                                "FP\n")
            client.send("\n")        

    if(msg[0] == "save"): #Save Command
        client.character.Save()

######## ------ ACTION FUNCTIONS ------ ############

    if(msg[0] == "look"): #Look Command
        client.display = 1
	sendlocal(WORLD_LIST[client.location].CLIENT_LIST,client,client.character.Name + " looks around.\n")
        
    if(msg[0] == "add" and sizeof > 1 and int(client.character.Skill_Points) > 0): #Add Command
        addAttrib(client, msg)

    if(msg[0] == "say"): #Say Command
	ClientList = WORLD_LIST[client.location].CLIENT_LIST
	for clients in ClientList:
		if client.character.ID != clients.character.ID:
			clients.send("^m" + client.character.Name + " says, " + Saying[1] + " \n^~")

    if(msg[0] == "comm"): #Same thing as a wisper
	#Debating if this should be used like a message system, which is possible to do
	Target = msg[1]
	for clients in CLIENT_LIST:
		if Target == clients.character.Name.lower():
			clients.send("^M" + client.character.Name + " comm'd you, " + Comming[2] + "\n^~")

    if(msg[0] == "shout"): #Shout Command, For all to see
	broadcast("^M[OOC]:" + client.character.Name + " shouts, " + Saying[1] + "\n^~") 
	
    
        

######## ------ STUPID COMMANDS ----- ############## 
#Commands that were added in for fun

    if(msg[0] == "dance" and sizeof > 1): #Dance Command
                ###I'll clean this up later, I put in some extra ifs to prevent it looping when it doesn't need to.
                #Like the check if self and before the client loop. 
                if (msg[1] == "self" or msg[1] == client.character.Name.lower()):
                        client.send("You dance with yourself like a fool.\n")
                else:
                        Found = 0
                        for Mobs in WORLD_LIST[client.location].MOB_LIST:
                                Test = Mobs.Alias.split(',',500)
                                for Mob in Test:
                                        if (msg[1] == Mob.lower()):
                                                Found = 1
                                                Target = Mobs.Name
                                                break 
                        if (Found == 0):
                                for Jerks in WORLD_LIST[client.location].CLIENT_LIST:
                                        if (msg[1] == Jerks.character.Name.lower()):
                                                Found = 1
                                                Target = Jerks.character.Name
                                                break
                        if (Found == 1):
                                client.send("You dance with " + Target + ".\n")
				for clients in WORLD_LIST[client.location].CLIENT_LIST:
					if (msg[1] == clients.character.Name.lower()):
						clients.send(client.character.Name + " sends you across the dancefloor.\n")
					else:
						if (clients.character.Name != client.character.Name):
							clients.send(client.character.Name + " dances with " + Target + ".\n")
                        else:
                                client.send("You can't dance with someone who doesn't exist\n")

    if(msg[0] == "smooch" and sizeof > 1): #Smooch Command
                ###I'll clean this up later, I put in some extra ifs to prevent it looping when it doesn't need to.
                #Like the check if self and before the client loop. 
                if (msg[1] == "self" or msg[1] == client.character.Name.lower()):
                        client.send("You smooch with yourself like a fool.\n")
                else:
                        Found = 0
                        for Mobs in WORLD_LIST[client.location].MOB_LIST:
                                Test = Mobs.Alias.split(',',500)
                                for Mob in Test:
                                        if (msg[1] == Mob.lower()):
                                                Found = 1
                                                Target = Mobs.Name
                                                break 
                        if (Found == 0):
                                for Jerks in WORLD_LIST[client.location].CLIENT_LIST:
                                        if (msg[1] == Jerks.character.Name.lower()):
                                                Found = 1
                                                Target = Jerks.character.Name
                        if (Found == 1):
                                client.send("You smooch " + Target)
				for clients in WORLD_LIST[client.location].CLIENT_LIST:
					if (msg[1] == clients.character.Name.lower()):
						clients.send(client.character.Name + " grabs and smooches you.\n")
					else:
						if (clients.character.Name != client.character.Name):
							clients.send(client.character.Name + " grabs a hold of " + Target + " and gives them a big smooch.\n")
                        else:
                                client.send("You can't smooch someone who doesn't exist\n")

################---------Debug Functions ----------- #######################




#------------------------------------------------------------------------------
#       Main
#------------------------------------------------------------------------------

if __name__ == '__main__':

    ## Simple chat server to demonstrate connection handling via the
    ## async and telnet modules.

    ## Create a telnet server with a port, address,
    ## a function to call with new connections
    ## and one to call with lost connections.

    telnet_server = TelnetServer(
        port=7777,
        address='',
        on_connect=on_connect,
        on_disconnect=on_disconnect,
        timeout = .05
        )

    ## don't forget to load the worlds into the server
    loadWorld()

    def hb():

        #################################
        ###-----SERVER FUNCTIONS -----###
        #################################

        ##This will be used later on for Mob AI (moving and such),
        ##Changing the weather, game time (day/night), refreshing dead mobs
        telnet_server.Time = telnet_server.Time + 1


        ##################################
        ##-------CLIENT FUNCTIONS ----####
        ##################################

        
        for client in CLIENT_LIST:
              if(client.loggedin != 7):
                      break
              client.Time = client.Time + 1
              battle = client.Time%2

              ##Recovers Force##
              if((int(client.character.Force)) < (int(client.character.Max_Force))):
                      r = int(client.character.Force) + 1
                      if(r > client.character.Max_Force):
                              client.character.Force = client.character.Max_Force
                      else:
                              client.character.Force = r
                              client.character.Force = str(client.character.Force)

                              
              ##Recovers Health - 1 + Constitution Multiplier/2 / second##
              if(int(client.character.Health) < int(client.character.Max_Health)):
                      c = client.character
                      r = int(client.character.Health) + AttribHandler(c.Constitution)/2 + 1
                      if(r > client.character.Max_Health):
                              c.Health = c.Max_Health
                      else:
                              c.Health = r
                              c.Health = str(c.Health)

                ##Checks the characters skill counter. (Force moves cooldown time: 5 seconds - change in skillhandler)
                ##If it's not at 0, reduce it
              if(client.character.SCounter != 0):        
                      client.character.SCounter = client.character.SCounter - 1

                ##Auto Battler - Set for two seconds##
              if(battle == 0):
                        tempLoc = WORLD_LIST[client.location]
                        for Mob in tempLoc.MOB_LIST:
                                if Mob.Hostile == True:
                                        if Mob.HostileTarget == client.character.Name:
                                                Attack(client, Mob, client.character)
                                                if(int(client.character.Health) < 1):
                                                        client.send("Oh no! You died!\n\n")
                                                        WORLD_LIST[client.location].CLIENT_LIST.remove(client)
                                                        client.location = 0
                                                        WORLD_LIST[client.location].CLIENT_LIST.append(client)
                                                        client.character.Health = client.character.Max_Health
                                                else:
                                                        Attack(client, client.character, Mob)
                                                        if(int(Mob.Health) < 1):
                                                                client.send("You killed " + Mob.Name + "\n\n")
                                                                WORLD_LIST[client.location].MOB_LIST.remove(Mob)
                                                                client.character.Experience = int(client.character.Experience) + int(Mob.Exp)
                                                                expCheck(client)
                                                        else:
                                                                State(client, Mob)
                                                                displayHealth(client)
        
    t = RepeatTimer(1.0, hb, -1)
    t.start()

    print(">> Listening for connections on port %d.  CTRL-C to break."
        % telnet_server.port)
    
    ## Server Loop
    while SERVER_RUN:
        telnet_server.poll()        ## Send, Recv, and look for new connections
        kick_idle()                 ## Check for idle clients
        process_clients()           ## Check for client input
        
    print(">> Server shutdown.")
