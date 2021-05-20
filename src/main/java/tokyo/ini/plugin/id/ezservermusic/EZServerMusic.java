package tokyo.ini.plugin.id.ezservermusic;

import network.aeternum.bananapuncher714.localresourcepackhoster.LocalResourcePackHoster;
import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// ToDo: GUIで音楽の再生、停止をコントロールできるようにする->ジュークボックスからいけるようにしたいかな

/*将来的にやることリスト
ToDo: パック内ループの実装->時間がgetできないから厳しそう...
ToDo: ReedMeつくる
ToDo: マスター音源(必ずすべてのパックに含める音源)を設定できるようにする
ToDo: 場所で音を流せるようにする(座標を中心に何メートルみたいに）（マスター音源しか設定できないようにする）
ToDo: NPCs使ってDJ実装
*/
/*
CommandAPI(https://commandapi.jorel.dev/)移行したい感ある
↑は痛みを伴う変更(大幅書き直し)になるので、新規プロジェクトでやったほうがいいかもしれない
ytdlなどのコマンドを随時削除できるというメリットがある
↑これは第二回目書き直しRefineの時にやるか
*/

public class EZServerMusic extends JavaPlugin {

    static String RawMusicPath;
    static String OGGPath;
    static String PackPath;
    static String ResourcesPath;
    static String ThisPath;
    static String ZipPath;
    static String QueuePath;
    static String LRPHPath;
    static boolean YTDLConfirm;
    boolean Encode;
    static boolean addQueue;
    boolean QueueClear;
    boolean OGGClear;
    boolean MusicClear;
    int ConfigVersion;

    static LocalResourcePackHoster LRPH = LocalResourcePackHoster.getPlugin(LocalResourcePackHoster.class);

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        FileConfiguration config = this.getConfig();
        setConst();
        setDirectories();
        setResources();
        checkConfig(config);
        if(ConfigVersion != 1) saveResource("config.yml",true);
        if(Encode) inEnableEncode();
        EZCommandClass commandClass = new EZCommandClass();
        getCommand("ez").setExecutor(commandClass);
        getCommand("music").setExecutor(commandClass);
        getCommand("ezgui").setExecutor(commandClass);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        inDisable();
    }


    private void setConst() {
        RawMusicPath = new File("./EZServerMusic/raw_musics").getAbsolutePath().replace("\\.\\", "\\plugins\\");
        OGGPath = new File("./EZServerMusic/ogg").getAbsolutePath().replace("\\.\\", "\\plugins\\");
        PackPath = new File("./EZServerMusic/resourcepacks").getAbsolutePath().replace("\\.\\", "\\plugins\\");
        ResourcesPath = new File("./EZServerMusic/resources").getAbsolutePath().replace("\\.\\", "\\plugins\\");
        ThisPath = new File("./EZServerMusic").getAbsolutePath().replace("\\.\\", "\\plugins\\");
        ZipPath = new File("./EZServerMusic/zip").getAbsolutePath().replace("\\.\\", "\\plugins\\");
        QueuePath = new File("./EZServerMusic/queue").getAbsolutePath().replace("\\.\\", "\\plugins\\");
        LRPHPath = new File("./LocalResourcePackHoster/resourcepacks").getAbsolutePath().replace("\\.\\", "\\plugins\\");
    }

    private void setDirectories() {
        String[] paths = {ResourcesPath,RawMusicPath,OGGPath,PackPath,ZipPath,QueuePath};
        for(String element:paths){
            if(! Files.exists(Path.of(element))){
                try {
                    Files.createDirectory(Path.of(element));
                } catch (IOException e) {
                    getLogger().info(element+":ERROR");
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkConfig(FileConfiguration config){
        YTDLConfirm = config.getBoolean("Youtube-Downloader");
        Encode = config.getBoolean("Automatic.Start-upEncode");
        addQueue = config.getBoolean("Automatic.AddQueue");
        QueueClear = config.getBoolean("Automatic.Queue-Clear");
        OGGClear = config.getBoolean("Automatic.OGG-Clear");
        MusicClear = config.getBoolean("Automatic.Music-Clear");
        ConfigVersion = config.getInt("Config-Version");
    }
    private void inEnableEncode(){
        final String PREFIX = ChatColor.GOLD+"["+ChatColor.AQUA+"EZSMusic"+ChatColor.GOLD+"] "+ChatColor.WHITE;
        final String ERRORPREFIX = PREFIX+ChatColor.RED+"ERROR: ";
        if(Encode){
            for(String Name:Utils.FileNameInDirectory(RawMusicPath)) {
                EncodeClass.Callback ENCcallback = result -> {
                    if (result) {
                        System.out.println(PREFIX + "Success!");
                    } else {
                        System.out.println(ERRORPREFIX + "Encode error");
                    }
                };
                EncodeClass enc = new EncodeClass(Name, ENCcallback);
                enc.start();
            }
        }
    }
    private void inDisable(){
        Boolean[] check = {QueueClear,OGGClear,MusicClear};
        String[] paths = {QueuePath,OGGPath,RawMusicPath};
        int i = 0;
        for(Boolean elem:check){
            if(elem){
                if(! Files.exists(Path.of(paths[0]))) continue;
                try {
                    FileUtils.cleanDirectory(new File(paths[i]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            i++;
        }
    }
    private void setResources(){
        if(Files.notExists(Path.of(ThisPath +"\\"+"config.yml"))) {
            saveResource("config.yml", false);
        }
        if(Files.notExists(Path.of(ThisPath +"\\"+ "resources"+"\\"+"pack.mcmeta"))) {
            saveResource("resources/pack.mcmeta", false);
        }
        if(Files.notExists(Path.of(ThisPath +"\\"+ "resources"+"\\"+"pack.png"))) {
            saveResource("resources/pack.png", false);
        }
        if(Files.notExists(Path.of(ThisPath +"\\"+"spots.yml"))) {
            saveResource("spots.yml", false);
        }
    }
}
