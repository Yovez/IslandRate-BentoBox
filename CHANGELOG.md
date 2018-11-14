[Unreleased]
- Fix /rate top command producing lag
- Add "infinite" top menu with categorized sections (weekly/monthly/all-time)
- Change PlaceholderAPI build version to latest version 2.9.2
- Add support for BentoBox
- Add option for players to opt-out of island ratings

[1.3.3.7] - 2018-11-13

The Last Changelog :/
I will no longer be writing these changelogs
All changes are visible in GitHub if
interested.

Added:
- Support for BentoBox, moving away from ASkyBlock.
- The beginning of comments in the plugin's files to better understand
  what your configurating.

Removed:
- A few features to better support BentoBox-
  will be added back in a future update.

Changed:
- Storage config options to a new config storage.yml

[1.3.3.6d] - 2018-10-22

Added:
- New config section for logging, currently only allows logging of when a player rates an island,
message is logged to the console

Changed:
- Main classname to IslandRate
- How SQL loads and generates the database
- How the inventory checker checks for glitched items

[1.3.3.6c] - 2018-09-19

Added:
- Option to change IslandRate placeholderapi's to shortend prefix (islandrate vs ir)
- Config option, true/false 'placeholderapi_shortened'

Fixed:
- Correctly implemented invcheck command

Removed:
- Unused command, and obsolete code, temporarily.

[1.3.3.6b] - 2018-09-18

Added:
- New inv check feature, to check for items that have been taken from the IslandRate
menus by glitching the menu. Includes a config section to enable, and an automatic timer.
- New '/rate invcheck' command to manually check all online player's inventorys at once for
any glitched items.

[1.3.3.6] - 2018-08-29

Added:
- messages.yml file to contain all messages, step one of organizing the config files
- opt-out.yml file to contain all player UUIDs that want to opt-out of island ratings

Changed:
- All messages now read from the new messages.yml file
- /rate reload now reloads both config.yml and messages.yml
- When the inventory event cancels

Removed:
- Cleaned up code, made less sloppy/spaghetti

[1.3.3.5] - 2018-08-27

Added:
- Cooldown for rating islands, default at 60 seconds

Changed:
- Change mysql-connector library to latest version (5.1.47)

Removed:
- Remove obsolete code for Top menu

[1.3.3.4] - 2018-08-26

Changed:
- How SQL functions are closed

[1.3.3.3] - 2018-08-21

Added:
- The basis for a new "infinite" top menu system, not useable, just the "shell" of it

Changed:
- The way the Rate menu and Top menu load items, it's now in a ASync task,
  which will hopefully resolve lag, or at least have less of a lag spike.
  
Removed:
- Obsolete code

[1.3.3.2] - 2018-08-20

Fixed:
- Top menu now properly displays top 10 users instead of just top 9

[1.3.3.1] - 2018-08-18

Added:
- API function to get total number of voters for a player's island (getTotalNumOfVoters(OfflinePlayer))
- Placeholder to get total number of voters %islandrate_total_voters%
- Config placeholder to get toal number of voters %player-total-voters% & %target-total-voters%
- Config now has new placeholder in total-ratings messages

Changed:
- API function getAverageRating to just use getTotalNumOfVoters function, less SQL calls

[1.3.3.0] - 2018-08-17

Added:
- (Optional) Average rating system via /rate average [player] w/ placeholders

Changed:
- MySQL/SQLite is now the preferred and only storage solution available
- How the Top Menu handles teleports (also a fix)
- Compiled against latest version of ASkyBlock (v3.0.9.4)

Fixed:
- Top Menu didn't teleport players due to incorrect rating amount
- MySQL memory leak issues, and countless other MySQL problems
- Many many many bugs

Removed:
- Obsolete MySQL warning messages
- Flat-file (.yml) user storage is now completely gone and rid of, 
  you may still '/rate convert' though
- Obsolete code

Known Caveats:
- Not all of the new changes/fixes have been thoroughly tested, 
  so please report any bugs/errors/concerns ASAP! THANK YOU

[1.3.2.4] - 2018-08-12

Added:
- (Optional) /rate migrate Command to migrate from file storage to MySQL storage

[1.3.2.3] - 2018-08-12

Fixed:
- MySQL not generating tables properly, throwing errors when trying to use MySQL feature

[1.3.2.2] - 2018-08-12

Fixed:
- /rate command producing null pointer errors

[1.3.2.1] - 2018-07-30

Changed:
- IslandRateAPI.instance to be private, use IslandRateAPI.getInstance() instead

Fixed:
- The placeholders %islandrate_top_rated_player_#% & %islandrate_top_rated_amount_#%, they now work properly
- MySQL sending successful connection message even when it didn't successfully connect
- MySQL sending errors when disabling the plugin

Known Caveats:
- The /rate top command will sometimes produce some lag because it's loading through the top ten players of the server, this will be fixed in a performance update in the future.

[1.3.2.0] - 2018-07-28

Added:
- IslandRateAPI Class with useful methods
- (Optional via config.yml) Send message to island owner when a player rates their island.

Changed:
- All methods to use the IslandRateAPI class instead of other externals or the Main.class file, hopefully prevent hiccups and will just look easier on the eyes.
- The method how /rate top loaded the GUI has a different load order now, it still has some lag, but will be worked on more in the future.
- Built off the latest (v3.0.9.3) ASkyBlock version
- (Optional) Built off the latest (2.9.1) PlaceholderAPI version

Known Caveats:
- The placeholder %islandrate_top_rated_player_#% is currently bugged and will throw errors, please do NOT use this placeholder.
- The /rate top command will sometimes produce some lag because it's loading through the top ten players of the server, this will be fixed in a performance update in the future.
