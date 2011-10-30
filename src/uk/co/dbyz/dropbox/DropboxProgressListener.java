package uk.co.dbyz.dropbox;

import alexoft.Minebackup.MineBackup;
import com.dropbox.client2.ProgressListener;

import java.text.DecimalFormat;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: keyz
 * Date: 30/10/11
 * Time: 17:22
 * To change this template use File | Settings | File Templates.
 */
public class DropboxProgressListener extends ProgressListener {

    private final MineBackup plugin;

    public DropboxProgressListener(MineBackup plugin){
        this.plugin = plugin;
    }

    @Override
    public void onProgress(long l, long l1) {
        //To change body of implemented methods use File | Settings | File Templates.
        float percent = (float)l/(float)l1;
        percent = percent*100;


        plugin.sendLog(Level.INFO, "DrobBox Upload : " + (int)percent + "% (" + sizeFormat(l) + "/" + sizeFormat(l1) + ")");
    }

    public String sizeFormat(long l){
        if(l < 1024){
            return l + "B";
        }else if(l < 1024*1024){
            float out = (float)l/1024;
            return (new DecimalFormat("#.##").format(out)) + "KB";
        }else {//if(l < 1024 *1024 * 1024){
            float out = (float)l/(1024*1024);
            return (new DecimalFormat("#.##").format(out)) + "MB";
        }

    }

    @Override
    public long progressInterval(){
        return 5000L;
    }
}
