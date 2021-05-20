package tokyo.ini.plugin.id.ezservermusic;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static tokyo.ini.plugin.id.ezservermusic.EZServerMusic.ThisPath;

class MusicSpot {
    static File yamlFile = new File(ThisPath +"\\"+"spots.yml");
    static YamlConfiguration yaml = YamlConfiguration.loadConfiguration(yamlFile);

    static boolean create(String Name, Player player){
        if(Name != null && player.getType().equals(EntityType.PLAYER)){
            final Location Loc = player.getLocation();
            yaml.set("spots",Name);
            yaml.set("spots."+Name,Loc);
            try {
                yaml.save(yamlFile);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    static boolean delete(String Name){
        if(Name != null) {
            yaml.set("spots." + Name, null);
            try {
                yaml.save(yamlFile);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    static boolean play(String Name, String SoundGroup, String MusicName, String radius){
        int Radius;
        if(radius == null) {
            Radius = 10;
        }else{
            Radius = Integer.parseInt(radius);
        }
        if(Name != null){
            //Nameが存在してるか確認
            final Location Loc = yaml.getLocation("spots."+Name);
            Collection<Player> playerCollection = Loc.getNearbyPlayers(Radius);
            if(playerCollection.isEmpty()) return true;
            for(Player p:playerCollection) {
                p.playSound(Loc, "ezsm:" + SoundGroup + "." + MusicName, SoundCategory.PLAYERS, 1, 1f);
                p.sendMessage(EZCommandClass.PREFIX + "Playing" + ChatColor.AQUA + "♪" + ChatColor.WHITE + ": " + ChatColor.BOLD + MusicName);
            }
            return true;
        }
        return false;
    }
}
