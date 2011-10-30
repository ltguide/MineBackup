MineBackup
==========

*TODO: the README file..*

Dropbox Support:

Go to https://www.dropbox.com/developers/reference/sdk

Download the Java SDK. Extract the files to the same folder as your craftbukkit.jar and minecraft_server.jar

Go to https://www.dropbox.com/developers/apps and create an app.

Give it a Unique name (i.e. "<my name>'s dropbox backup"), select "App Folder" and click "Create".

IMPORTANT!
You need to include the new files in the classpath. the easiest way to do this is by running bukkit using the following:

Windows:

java  -classpath ".;.\*" org.bukkit.craftbukkit.Main

Linux/Mac:

java  -classpath ".;./*" org.bukkit.craftbukkit.Main

Start up the server with the plugin installed, then shut it down. Open the plugins/MineBackup/config.yml file, and add the key and secret obtained from the last step and put them into 

dropbox:
	appkey:
	appsecret: 
	
Ignore the other two, they'll get filled later.
Change the enabled conf to true.

Start up the server again. In the logs, there will be a URL to authenticate with, copy it into your browser, (log into dropbox if necessary) and grant the app access.
Now, issue the command 
	/mbck dropboxauth
on the server, this will complete the authentication.

Type /mbck and a backup should begin, after a moment, you should see a dropbox upload progres entry in the log. Once done, You'll have backups in your dropbox!