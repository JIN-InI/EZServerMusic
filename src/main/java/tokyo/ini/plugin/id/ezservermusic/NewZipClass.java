package tokyo.ini.plugin.id.ezservermusic;

import org.zeroturnaround.zip.ZipUtil;

import java.io.File;

import static tokyo.ini.plugin.id.ezservermusic.EZServerMusic.PackPath;
import static tokyo.ini.plugin.id.ezservermusic.EZServerMusic.ZipPath;

public class NewZipClass {
    public static void Compress(String packname){
        ZipUtil.pack(new File(PackPath+"\\"+packname),new File(ZipPath+"\\"+packname+".zip"));
    }
}
