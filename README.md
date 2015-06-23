##Daily Comics
Application that allows viewing of my personal list of comics strips in one convenient place even when offline. App can automatically download new comic strips if they are updated.

This is an Android Studio project

####Features
* View comic strips 
* Automatic checks for updates and downloads new comics every 6 hrs (with wifi connection)
* Manual update (with wifi connection)
* Update notifications
* Error notifications
* Last update time

####TODO
* Better customization of features (example set update interval)
* Re-enable automatic update on phone reboot
* Reattempt update check if delayed due to no connection (implemented but broken)
* Image pinchzoom
* Image scaling based on dimensions for better viewing
* Automatic image src finding
* Convert to Material design????

####How to add comic
1. Add comic information to 'assets/comic_collection.json'
2. Define image src location in 'ca/kklee/comics/comic/ComicDOMDictionary.java'
