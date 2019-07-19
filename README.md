MCSDepthLogger


This program will simplificate the task for the NMEA logger.

Dieses Programm dient der Vereinfachung verschiedener Aufgaben, die im Zusammenhang mit dem NMEA Datenlogger auftreten.

TODO for the next release 
- enhancement of analyze tool, creating a html report with big map view for showing the track, and some statistics.
- uploading track to server, package to one track with vessel information...

DONE
Version 0.2.0.314
- BUG: Wait icon stays visible on error of file.
- BUG: file cache is not always updated when needed.

DONE
Version 0.2.0.312
- MAINTANACE: Switching to fully maven control and git
- BUG: Wait icon stays visible on error of file.

DONE
Version 0.2.0.302
- FEATURE: automatically correct file date of logger files, if the user want this to do. (Ticket #10 https://sourceforge.net/p/mcsdepthlogger/tickets/10/)

DONE
Version 0.2.0.301
- BUG: hide wait panel after export. (Ticket #9 https://sourceforge.net/p/mcsdepthlogger/tickets/9/)
- FEATURE: adding Date/Time and Size to file view. (Ticket #8 https://sourceforge.net/p/mcsdepthlogger/tickets/8/)

DONE
Version 0.2.0.300
- BUG: hide wait panel after analyse.
- showing first and last timestamp of GPS RMC Datagramms of file in analyse tool

DONE
Version 0.2.0.298
- BUG: Export with NMEA exports in DAT format, and DAT export in NMEA format. Changed it. 
- BUG: Export with GPX format with more than 1 file exports an unreadable file. 
- Do some CCD-Refactoring the main class file, because it's toooooo long.

Version 0.2.0.296
- adapting the right timestamp on the sd card files from the gps data

Version 0.2.0.291
- Bugfix: GPX Format, files are exported as one track

Version 0.2.0.290
- removing openseamap tile server from selection list
- unseen NPE in NLS part
- GPX Format, files are exported as one track
- adding new title for import folder selection dialog
- clearify import folder in help

Version 0.1.1.287
- saving path to last import folder
- adding export to GPX format

Version 0.1.1.284
- adding import feature to documentation

Version 0.1.1.280
- import files directly from file system (instead of using the sd card)
- export selection of datafiles in one NMEA file with preprocessing... 
- export a complete track in one NMEA file with preprocessing... 
- rename route to track
- adding new track with files directly from main view.
- adding files to track with automatic deletion

Version 0.1.1.272
- some refactoring of gui and helper methods
- showing queued map tiles
- caching tiles from all factories not only from the latest
- caching the tiles in the app directory of the depth logger
- upload complete track 
- change name of sd backup to sd manage

Version 0.1.1.269
- more information like current speed, time, coordinates on a map point.
- configuration of nautical, imperial or metrical measure scales
- showing a series of speed

Version 0.1.1.259
- addding support for new bootloader
- converting HEX file to bin file with right version information
- marking point of chart in map view
- implementing depth view
- scaling axis to right values for chart view
- adding more than one file to a track
 
Version 0.1.1.253
- implementing overlays for map viewer
- overlays for sea marks and sport
- new icon set

Version 0.1.1.249
- implementing chart view for accelerator, gyroscope, depth, height and voltage
- implementing map view for OpenSeaMap (experimental)
- adding user definable configuration of new tile servers
- building/managing tracks from data files
- ... a lot bug fixes...

Version 0.1.1.237
- possibility to start the route in browser or external program like RouteConverter.
- new tab for map options
- adding internal map viewer based on jxmapviewer
- showing track on map
- showing acc panel with jchart
- filter sentence for quicker parsing of the map data
- 2. tile server

Version 0.1.1.228
- possibility to start the route in browser or external program like RouteConverter.

Version 0.1.1.228
- Firmware could not be written to sd card.
- export possibility in NMEA Text format.

Version 0.1.1.219
- updating some help topics
- adding possibility to show route on map in external browser

Version 0.1.1.212
- make all dialoges frame center
- new help menu in main menu
- new restore menu in main menu

Version 0.1.1.209
- BUG: 0002: Big data needs more memory.
  Now i set the max to 1024MB
- BUG: 0003: with data0003.dat the analysing throws an java.lang.IllegalArgumentException: Comparison method violates its general contract!
  Changing the sorting comperator.