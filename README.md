##Daily Comics
Application that allows viewing of my personal list of comics strips in one convenient place even when offline. App can automatically download new comic strips if they are updated.

This is an Android Studio project.
This project is for personal use only.

####Features
* View comic strips 
* Automatic checks for updates and downloads new comics every 6 hrs (with wifi connection)
* Manual update (with wifi connection)
* Update notifications
* Error notifications
* Last update time

####TODO
* Move saved imgs to externalcache
* Better customization of features (example set update interval)
* In-app comic enable-diable options
* Image pinchzoom
* Image scaling based on dimensions for better viewing
* Automatic image src finding

####How to add comic
1. Add comic information to 'assets/comic_collection.json'
2. Define image src location in 'ca/kklee/comics/comic/ComicDOMDictionary.java'
  * location should be the same with every update
 
#####APK
<img src='https://chart.googleapis.com/chart?cht=qr&chl=https%3A%2F%2Fgithub.com%2Fkklee305%2FComics%2Fblob%2Fmaster%2Fapk%2FComics.apk%3Fraw%3Dtrue&chs=180x180&choe=UTF-8&chld=L|2' alt=''></a>

<img src="https://github.com/kklee305/Comics/blob/master/screenshots/New%20Comics.png" width="300">

<img src="https://github.com/kklee305/Comics/blob/master/screenshots/Notifications.png" width="300">

<img src="https://github.com/kklee305/Comics/blob/master/screenshots/Scrolling.png" width="300">
