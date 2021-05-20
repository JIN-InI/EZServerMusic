package tokyo.ini.plugin.id.ezservermusic;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static tokyo.ini.plugin.id.ezservermusic.EZServerMusic.*;

class GeneratePackClass {

    protected static boolean runGenerate(String packname){
        if(! createDirectory(packname)) return false;
        if(! setMCMETA(packname)) return false;
        if(! setIcon(packname)) return false;
        if(! setOGG(packname)) return  false;
        if(! setJson(packname)) return false;
        if(! zipper(packname)) return false;
        if(!copyToLRPH(packname)) return false;
        changeLRPHConfig(packname);
        return true;
    }

    private static boolean createDirectory(String packname){
        try {
            Files.createDirectories(Path.of(PackPath+"\\"+packname+"\\assets\\ezsm\\sounds\\"+packname));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean setMCMETA(String packname){
        try {
            Files.copy(Path.of(ResourcesPath+"\\pack.mcmeta"),Path.of(PackPath+"\\"+packname+"\\pack.mcmeta"));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean setIcon(String packname){
        try {
            Files.copy(Path.of(ResourcesPath+"\\pack.png"),Path.of(PackPath+"\\"+packname+"\\pack.png"));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean setOGG(String packname) {
        try {
            for (String files : FileSearch(QueuePath)) {
                Files.copy(
                        Paths.get(QueuePath + "\\" + files),
                        Paths.get(PackPath+"\\"+ packname + "\\assets\\ezsm\\sounds\\"+packname+"\\" + files)
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static boolean setJson(String packname) {
        return JsonWriter.writerMain(packname);
    }

    private static boolean zipper(String packname) {
        NewZipClass.Compress(packname);
        return true;
    }

    private static boolean copyToLRPH(String packname){
        packname = packname + ".zip";
        try {
            Files.copy(Path.of(ZipPath+"\\"+packname),Path.of(LRPHPath+"\\"+packname));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void changeLRPHConfig(String packname){
        FileConfiguration LRPHConfig = LRPH.getConfig();
        LRPHConfig.set("packs."+packname, packname+".zip");
        ReloadLRPH();
    }

    protected static List<String> FileSearch(String path) {
        File dir = new File(path);
        List<String> nameList = new ArrayList<>();
        try {
            for (File file : dir.listFiles()) {
                nameList.add(file.getName());
            }
        } catch (NullPointerException e) {
            nameList.add(null);
        }
        return nameList;
    }

    protected static void ReloadLRPH(){
        LRPH.saveConfig();
        LRPH.reloadConfig();
        Bukkit.getPluginManager().disablePlugin(LRPH);
        Bukkit.getPluginManager().enablePlugin(LRPH);
    }
}

class DeletePackClass {
    public static boolean PackDelete(String packname){
        final String[] Paths = {ZipPath+"\\"+packname+".zip",LRPHPath+"\\"+packname+".zip"};
        try {
            FileUtils.deleteDirectory(new File(PackPath+"\\"+packname));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        for(String path : Paths){
            try {
                Files.delete(Path.of(path));
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        LRPHConfigEditor(packname);
        return true;
    }

    private static void LRPHConfigEditor(String packname){
        FileConfiguration LRPHConfig = LRPH.getConfig();
        LRPHConfig.set("packs."+packname,null);
        GeneratePackClass.ReloadLRPH();
    }
}
