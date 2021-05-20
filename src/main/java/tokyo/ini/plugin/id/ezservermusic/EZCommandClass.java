package tokyo.ini.plugin.id.ezservermusic;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static tokyo.ini.plugin.id.ezservermusic.DeletePackClass.PackDelete;
import static tokyo.ini.plugin.id.ezservermusic.EZServerMusic.ThisPath;

public class EZCommandClass implements CommandExecutor , TabCompleter {

    final static String PREFIX = ChatColor.GOLD+"["+ChatColor.AQUA+"EZSMusic"+ChatColor.GOLD+"] "+ChatColor.WHITE;
    final String ERRORPREFIX = PREFIX+ChatColor.RED+"ERROR: ";
    static File yamlFile = new File(ThisPath +"\\"+"spots.yml");
    static YamlConfiguration yaml = YamlConfiguration.loadConfiguration(yamlFile);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("ez")) {
            if(args.length < 1) return false;
            switch (args[0]) {
                case "encode":
                    if (args.length < 2) {
                        sender.sendMessage(onHelp("encode"));
                        return true;
                    }
                    EncodeClass.Callback ENCcallback = result -> {
                        if (result) {
                            sender.sendMessage(PREFIX + "Encode complete");
                        } else {
                            sender.sendMessage(ERRORPREFIX + "Encode error");
                        }
                    };
                    EncodeClass enc = new EncodeClass(args[1], ENCcallback);
                    enc.start();
                    return true;
                case "ytdownload":
                    if (!EZServerMusic.YTDLConfirm) {
                        sender.sendMessage(ERRORPREFIX + "Downloading from Youtube is not allowed by the operator");
                        return true;
                    }
                    if (args.length < 3) {
                        sender.sendMessage(onHelp("ytdownload"));
                        return true;
                    }

                    YoutubeDL.Callback YTcallback = result -> {
                        if (result) {
                            sender.sendMessage(PREFIX + "Downloaded!");
                        } else {
                            sender.sendMessage(ERRORPREFIX + "Error");
                        }
                    };

                    YoutubeDL YTDLer = new YoutubeDL(args[1], args[2], YTcallback);
                    YTDLer.start();
                    return true;

                case "generatepack":
                    if (args.length < 2) {
                        sender.sendMessage(onHelp("generatepack"));
                        return true;
                    }
                    if (GeneratePackClass.runGenerate(args[1].toLowerCase())) {
                        sender.sendMessage(PREFIX + "Success!");
                    } else {
                        sender.sendMessage(ERRORPREFIX + "Error");
                    }
                    return true;

                case "deletepack":
                    if (args.length < 2) {
                        sender.sendMessage(onHelp("deletepack"));
                        return true;
                    }
                    if (PackDelete(args[1])) {
                        sender.sendMessage(PREFIX + "Success!");
                    } else {
                        sender.sendMessage(ERRORPREFIX + "Error");
                    }
                    return true;
                case "deletemusic":
                    if (args.length >= 2) {
                        if(!Files.exists(Path.of(EZServerMusic.RawMusicPath + "\\" + args[1]))){
                            sender.sendMessage(ERRORPREFIX + args[1]+" is not exists");
                            return true;
                        }
                        try {
                            Files.delete(Path.of(EZServerMusic.RawMusicPath + "\\" + args[1]));
                        } catch (IOException e) {
                            e.printStackTrace();
                            sender.sendMessage(ERRORPREFIX + "Delete is failed");
                        }
                    }else {
                        try {
                            FileUtils.cleanDirectory(new File(EZServerMusic.RawMusicPath));
                        } catch (IOException e) {
                            e.printStackTrace();
                            sender.sendMessage(ERRORPREFIX + "Error");
                            return true;
                        }
                    }
                    sender.sendMessage(PREFIX + "Success!");
                    return true;
                case "clearqueue":
                    if (args.length >= 2) {
                        if(!Files.exists(Path.of(EZServerMusic.QueuePath + "\\" + args[1]+".ogg"))){
                            sender.sendMessage(ERRORPREFIX + args[1]+" is not exists");
                            return true;
                        }
                        try {
                            Files.delete(Path.of(EZServerMusic.QueuePath + "\\" + args[1]+".ogg"));
                        } catch (IOException e) {
                            e.printStackTrace();
                            sender.sendMessage(ERRORPREFIX + args[1] +"is not deleted");
                        }
                    } else {
                        try {
                            FileUtils.cleanDirectory(new File(EZServerMusic.QueuePath));
                        } catch (IOException e) {
                            e.printStackTrace();
                            sender.sendMessage(ERRORPREFIX + "Queue is not cleared");
                            return true;
                        }
                    }
                    sender.sendMessage(PREFIX + "Cleared the queue");
                    return true;
                case "deleteogg":
                    if(args.length >= 2){
                        if(!Files.exists(Path.of(EZServerMusic.QueuePath + "\\" + args[1]+".ogg"))){
                            sender.sendMessage(ERRORPREFIX + args[1]+" is not exists");
                            return true;
                        }
                        try {
                            Files.delete(Path.of(EZServerMusic.OGGPath+"\\"+args[1]+".ogg"));
                        } catch (IOException e) {
                            e.printStackTrace();
                            sender.sendMessage(ERRORPREFIX+"Delete is failed");
                        }
                    }else {
                        try {
                            FileUtils.cleanDirectory(new File(EZServerMusic.OGGPath));
                        } catch (IOException e) {
                            e.printStackTrace();
                            sender.sendMessage(ERRORPREFIX + "Error");
                            return true;
                        }
                    }
                    sender.sendMessage(PREFIX + "Delete is succeeded");
                    return true;

                case "addqueue":
                    if (args.length < 2) {
                        sender.sendMessage(onHelp("addqueue"));
                        return true;
                    }
                    try {
                        Files.copy(Path.of(EZServerMusic.OGGPath+"\\"+args[1]+".ogg"),
                                Path.of(EZServerMusic.QueuePath+"\\"+args[1]+".ogg"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        sender.sendMessage(ERRORPREFIX+"Error");
                        return true;
                    }
                    sender.sendMessage(PREFIX+"Success!");
                    return true;

                case "packlist":
                case "pl":
                    sender.sendMessage(Utils.FileList(EZServerMusic.PackPath, "PackList"));
                    return true;

                case "queuelist":
                case "ql":
                    sender.sendMessage(Utils.FileList(EZServerMusic.QueuePath, "Queue"));
                    return true;

                case "musiclist":
                case "ml":
                    sender.sendMessage(Utils.FileList(EZServerMusic.RawMusicPath, "MusicList"));
                    return true;

                case "ogglist":
                    sender.sendMessage(Utils.FileList(EZServerMusic.OGGPath, "OGGList"));
                    return true;

                case "gui":
                    MainMenu MenuGUI = new MainMenu();
                    MenuGUI.show((Player) sender);
                    return true;

                default:
                    sender.sendMessage(PREFIX+"Unknown command");
                    return true;
            }
        }else if(command.getName().equals("music")) {
            if (args.length < 1) return false;
            switch (args[0]) {
                case "play":
                    if (args.length < 3) return false;
                    sender.playSound(Sound.sound(Key.key("ezsm", args[1] + "." + args[2]), Sound.Source.PLAYER, 1f, 1f));
                    sender.sendMessage(PREFIX + "Playing" + ChatColor.AQUA + "♪" + ChatColor.WHITE + ": " + ChatColor.BOLD + args[2]);
                    return true;

                case "stop":
                    if (args.length < 3) {
                        sender.stopSound(SoundStop.all());
                        sender.sendMessage(PREFIX + "Stopped play: " + ChatColor.BOLD + "ALL");
                        return true;
                    }
                    sender.stopSound(SoundStop.named(Key.key("ezsm", args[1] + "." + args[2])));
                    sender.sendMessage(PREFIX + "Stopped play: " + ChatColor.BOLD + args[2]);
                    return true;
                case "spot":
                    switch (args[1]){
                        case "create":
                            if(MusicSpot.create(args[2],(Player) sender)){
                                sender.sendMessage(PREFIX+"Created!");
                            }else{
                                sender.sendMessage(ERRORPREFIX+"Need to enter a name");
                            }
                            return true;
                        case "delete":
                            if(MusicSpot.delete(args[2])){
                                sender.sendMessage(PREFIX+"Delete is complete");
                            }else{
                                sender.sendMessage(ERRORPREFIX+"Need to enter a name");
                            }
                            return true;
                        case "play":
                            if(args.length < 5) return false;
                            if(MusicSpot.play(args[2],args[3],args[4],args[5])){
                                sender.sendMessage(PREFIX + "Playing" + ChatColor.AQUA + "♪" + ChatColor.WHITE + ": " + ChatColor.BOLD + args[4]
                                        + ChatColor.RESET + ChatColor.AQUA + " in " + args[2]);
                            }else{
                                sender.sendMessage(ERRORPREFIX+"Something wrong");
                            }
                            return true;
                    }

                default:
                    return false;
            }
        }else if(command.getName().equals("ezgui")){
            if (args.length < 1) return false;
            switch (args[0]){
                case "menu":
                    new MainMenu().show((Player) sender);
                    return true;
                case "encode":
                    new GUIEncode().show((Player) sender);
                    return true;
                case "resourcepack":
                    new ResourcePackSelector().show((Player) sender);
                    return true;
            }
        }else{
            return false;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command command,String alias,String[] args) {
        List<String> suggest = new ArrayList<>();
        if(sender instanceof Player){
            if(command.getName().equalsIgnoreCase("ez")){
                switch (args.length){
                    case 0:
                        suggest.add("addqueue");
                        suggest.add("encode");
                        suggest.add("ytdownload");
                        suggest.add("generatepack");
                        suggest.add("deletepack");
                        suggest.add("deleteogg");
                        suggest.add("deletemusic");
                        suggest.add("clearqueue");
                        suggest.add("packlist");
                        suggest.add("queuelist");
                        suggest.add("musiclist");
                        suggest.add("ogglist");
                        Collections.sort(suggest);
                        return suggest;
                    case 1:
                        suggest.add("addqueue");
                        suggest.add("encode");
                        suggest.add("ytdownload");
                        suggest.add("generatepack");
                        suggest.add("deletepack");
                        suggest.add("deleteogg");
                        suggest.add("deletemusic");
                        suggest.add("clearqueue");
                        suggest.add("packlist");
                        suggest.add("queuelist");
                        suggest.add("musiclist");
                        suggest.add("ogglist");
                        suggest.removeIf(element -> !element.startsWith(args[0]));
                        Collections.sort(suggest);
                        return suggest;
                    case 2:
                        switch (args[0]){
                            case "addqueue":
                                suggest = Utils.FileNameInDirectory(EZServerMusic.OGGPath);
                                suggest.removeIf(element -> !element.startsWith(args[1]));
                                suggest.replaceAll(string -> string.replaceAll("\\.ogg",""));
                                Collections.sort(suggest);
                                return suggest;
                            case "encode":
                            case"deletemusic":
                                suggest = Utils.FileNameInDirectory(EZServerMusic.RawMusicPath);
                                suggest.removeIf(element -> !element.startsWith(args[1]));
                                Collections.sort(suggest);
                                return suggest;
                            case "ytdownload":
                                suggest.add("<URL>");
                                return suggest;
                            case "generatepack":
                                suggest.add("<Name>");
                                return suggest;
                            case "deletepack":
                                suggest = Utils.FileNameInDirectory(EZServerMusic.PackPath);
                                suggest.removeIf(element -> !element.startsWith(args[1]));
                                Collections.sort(suggest);
                                return suggest;
                            case "clearqueue":
                                suggest = Utils.FileNameInDirectory(EZServerMusic.QueuePath);
                                suggest.removeIf(element -> !element.startsWith(args[1]));
                                suggest.replaceAll(string -> string.replaceAll("\\.ogg",""));
                                Collections.sort(suggest);
                                return suggest;
                            case "deleteogg":
                                suggest = Utils.FileNameInDirectory(EZServerMusic.OGGPath);
                                suggest.removeIf(element -> !element.startsWith(args[1]));
                                suggest.replaceAll(string -> string.replaceAll("\\.ogg",""));
                                Collections.sort(suggest);
                        }
                        return suggest;
                    case 3:
                        if ("ytdownload".equals(args[1])) {
                            suggest.add("<Name>");
                            return suggest;
                        }
                        return suggest;
                    default:
                        return suggest;
                }
            }else if(command.getName().equalsIgnoreCase("music")){
                switch (args.length){
                    case 0:
                        suggest.add("play");
                        suggest.add("stop");
                        suggest.add("spot");
                        Collections.sort(suggest);
                        return suggest;

                    case 1:
                        suggest.add("play");
                        suggest.add("stop");
                        suggest.add("spot");
                        suggest.removeIf(element -> !element.startsWith(args[0]));
                        Collections.sort(suggest);
                        return suggest;

                    case 2:
                        switch (args[0]){
                            case "play":
                            case "stop":
                                suggest = Utils.FileNameInDirectory(EZServerMusic.PackPath);
                            case "spot":
                                suggest.add("create");
                                suggest.add("delete");
                                suggest.add("play");
                        }
                        suggest.removeIf(element -> !element.startsWith(args[1]));
                        Collections.sort(suggest);
                        return suggest;
                    case 3:
                        switch (args[0]) {
                            case "play":
                            case "stop":
                                suggest = Utils.FileNameInDirectory(EZServerMusic.PackPath + "\\" + args[1] + "\\assets\\ezsm\\sounds\\" + args[1]);
                                suggest.replaceAll(string -> string.replaceAll("\\.ogg", ""));
                            case "spot":
                            default:
                                return suggest;
                        }
                }
            }
        }
        return null;
    }

    private String onHelp(String CommandName){
        List<String> Message = new ArrayList<>();
        Message.add("===="+ChatColor.AQUA+"HELP"+ChatColor.WHITE+"====");
        switch (CommandName){
            case "addqueue":
                Message.add("/ez addqueue <Name of the music you want add the queue>");
                break;
            case "encode":
                Message.add("/ez encode <Name of the music you want encode>");
                break;
            case "ytdownload":
                Message.add("/ez ytdownload <Youtube's URI> <Any name>");
                break;
            case "generatepack":
                Message.add("/ez generatepack <Any name of the pack>");
                break;
            case "deletepack":
                Message.add("/ez deletepack <Name of the pack you want delete");
                break;
            case "play":
                Message.add("/music play <Pack's name> <Music's name>");
                break;
            case "stop":
                Message.add("/music stop <Pack's name> <Music's name>");
                break;
        }
        return String.join("\n",Message);
    }
}
