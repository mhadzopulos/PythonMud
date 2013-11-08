import sqlite3
from Defs import *

Test = Location("2")
print Test.Desc
print Test.Short_Desc
print Test.West
print Test.East
print Test.North
print Test.South
print Test.Up
print Test.Down
print "-------------"
Test2 = Location("5")
print Test2.Desc
print Test2.Short_Desc
print Test2.West
print Test2.East
print Test2.North
print Test2.South
print Test2.Up
print Test2.Down
print "-------------"
Test_West = Location(Test.West)
print Test_West.Desc
print Test_West.Short_Desc
print Test_West.West
print Test_West.East
print Test_West.North
print Test_West.South
print Test_West.Up
print Test_West.Down

