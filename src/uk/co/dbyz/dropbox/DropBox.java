package uk.co.dbyz.dropbox;


import alexoft.Minebackup.MineBackup;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.*;
import sun.rmi.runtime.Log;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: keyz
 * Date: 29/10/11
 * Time: 14:29
 * To change this template use File | Settings | File Templates.
 */
public class DropBox {
        final static private Session.AccessType ACCESS_TYPE = Session.AccessType.APP_FOLDER;

    private final MineBackup plugin;

    private final DropboxProgressListener progress;

    // In the class declaration section:
    private static DropboxAPI<WebAuthSession> mDBApi;
    private AccessTokenPair  atp;
    private RequestTokenPair rtp;
    private WebAuthSession session;
    private String UserDropboxUID;
    private AppKeyPair appKeys;

    public boolean requiresAuth = false;

    public DropBox(MineBackup plugin) {
        this.plugin = plugin;
        this.progress = new DropboxProgressListener(plugin);
    }

    public boolean initialize(String key, String secret) {
        // And later in some initialization function:
        appKeys = new AppKeyPair(plugin.config.dropboxAppKey, plugin.config.dropboxAppSecret);

        if((key == null || secret == null || key.equalsIgnoreCase(""))||(secret.equalsIgnoreCase(""))){
            requiresAuth = true;
            session = new WebAuthSession(appKeys, ACCESS_TYPE);
            mDBApi = new DropboxAPI<WebAuthSession>(session);
            try {
                WebAuthSession.WebAuthInfo info = mDBApi.getSession().getAuthInfo();
                plugin.sendLog(Level.SEVERE, "Please visit : " + info.url.toString() + " to log into dropbox");
                atp = mDBApi.getSession().getAccessTokenPair();
            } catch (DropboxException e) {
                return false;
            }
        }else{
            AccessTokenPair access = new AccessTokenPair(key,secret);
            session = new WebAuthSession(appKeys, ACCESS_TYPE,access);
            mDBApi = new DropboxAPI<WebAuthSession>(session);
        }
        return true;
    }

    public boolean completeAuth() {
        plugin.sendLog(Level.INFO, "Completing Dropbox Auth");

        try {
            mDBApi.getSession().setAccessTokenPair(atp);
            rtp = new RequestTokenPair(atp.key,atp.secret);
            mDBApi.getSession().retrieveWebAccessToken(rtp);
            atp = mDBApi.getSession().getAccessTokenPair();
        } catch (DropboxException e) {
            plugin.sendLog(Level.SEVERE, "User failed to auth");
            plugin.sendLog(Level.SEVERE, e.toString());
            return false;
        }

        plugin.config.dropboxKey = atp.key;
        plugin.config.dropboxSecret = atp.secret;

        plugin.config.rewrite();

        this.requiresAuth = false;

        return true;
    }

    public boolean uploadFile(InputStream str, String name, long length) {
        // Uploading content.
        try {
            DropboxAPI.Entry newEntry = mDBApi.putFile(name, str,
                    length, null, progress);
            plugin.sendLog(Level.INFO, "The uploaded file's rev is: " + newEntry.rev);
            return true;
        } catch (DropboxUnlinkedException e) {
            // User has unlinked, ask them to link again here.
            plugin.sendLog(Level.SEVERE, "User has unlinked.");
            e.printStackTrace();
        } catch (DropboxException e) {
            plugin.sendLog(Level.SEVERE, "Something went wrong while uploading.");
        }
        return false;
    }
}
