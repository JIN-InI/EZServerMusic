package tokyo.ini.plugin.id.ezservermusic;

import org.bukkit.ChatColor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {
    static String FileList(String path,String name){
        List<String> Message = new ArrayList<>();
        Message.add("===="+ChatColor.AQUA+name+ChatColor.WHITE+"====");
        if(Files.notExists(Path.of(path))){
            Message.add("* NONE");
            return String.join("\n", Message);
        }
        for(File element:new File(path).listFiles()){
            Message.add(ChatColor.GOLD+"* "+ChatColor.WHITE+ChatColor.BOLD+element.getName());
        }
        Message.replaceAll(string -> string.replaceAll("\\.ogg",""));
        return String.join("\n", Message);
    }

    static List<String> FileNameInDirectory(String path){
        List<String> list = new ArrayList<>();
        if(new File(path).listFiles() == null){
            list = Collections.singletonList(list.toString());
        }else{
            for(File element:new File(path).listFiles()){
                list.add(element.getName());
            }
        }
        return list;
    }
}
