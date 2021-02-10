# minecraft_fullReachFix
A reachhack fix for minecraft.

------

ADVANTAGES
==========

- Records stay after restart
- Combos and lag don't matter much
- Bad records are fixed by playing legit
- No bypasses due to it being a force-type anticheat

DISADVANTAGES
=============

- Combos with very high ping can create a negative record

WORKFLOW
========

- Load records from config
- Register event handlers
- Create link from records to config

On attackevent by player:

- Reach is added to record
- Average reach of last few hits is saved
- If average reach is over 4, 
  - Add offense
  - If offenses are over 3,
    - Cancel attacks, but keep recording
  - If offenses are over 6,
    - Kick player
    - Set offense count to 2.8
- If average reach is below 4,
  - Remove 0.2 from offense counter
