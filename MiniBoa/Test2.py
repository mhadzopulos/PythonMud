import sqlite3
from Defs import *

Test = Character("Tetsuo")
print Test.Health
Test.Health = 10000
print Test.Health
Test.Save()

Batman = Character("Tetsuo")
print Batman.Health
