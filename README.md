MineBackup
==========

*TODO: the README file..*

Dropbox Support:

Go to https://www.dropbox.com/developers/apps and create an app.

Give it a Unique name (i.e. "<my name>'s dropbox backup"), select "App Folder" and click "Create".

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