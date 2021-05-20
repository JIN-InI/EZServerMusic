package tokyo.ini.plugin.id.ezservermusic;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

abstract class GUISuper{
    String PageName;
    abstract void PageName();
    abstract void createGUI();
    abstract void setStyle();
    abstract void contents();
    abstract void setPane();
    abstract void show(Player player);
}

class MainMenu extends GUISuper{

    ChestGui GUI;
    StaticPaneEX Pane;

    void PageName(){
        PageName = "MAIN MENU";
    }

    void createGUI(){
        GUI = new ChestGui(6,"EZSMusic GUI : "+PageName);
        Pane = new StaticPaneEX(0,0,9,6);
    }

    void setStyle(){
        ItemStack item = new ItemStack(Material.GLASS_PANE);
        ItemMeta im= item.getItemMeta();
        im.displayName(Component.text(""));
        item.setItemMeta(im);
        Pane.setCircumference(item,9,6);
    }

    void contents(){
        // 中身を記述するときはここ
        ItemStack encode = new ItemStack(Material.BOOK);
        ItemMeta encmeta = encode.getItemMeta();
        encmeta.displayName(Component.text("ENCODE"));
        List<Component> encLore= new ArrayList<>();
        encLore.add(Component.text("Let's Encode!"));
        encmeta.lore(encLore);
        encode.setItemMeta(encmeta);
        Pane.setCommandTriggerItem(encode,"ezgui encode",2,2);

        ItemStack pack = new ItemStack(Material.BOOK);
        ItemMeta packmeta = pack.getItemMeta();
        packmeta.displayName(Component.text("RESOURCE PACK"));
        List<Component> packLore= new ArrayList<>();
        packLore.add(Component.text("Download Any Resource-pack"));
        packmeta.lore(packLore);
        pack.setItemMeta(packmeta);
        Pane.setCommandTriggerItem(pack,"ezgui resourcepack",4,2);

    }

    void setPane(){
        GUI.addPane(Pane);
    }

    void show(Player player){
        // 中身書いて表示するだけ
        PageName();
        createGUI();
        setStyle();
        contents();
        setPane();
        GUI.show(player);
    }
}

class ListGUIStandard extends MainMenu{

    String Path;

    @Override
    void createGUI(){
        GUI = new ChestGui(6,"EZSMusic GUI : "+PageName);
        Pane = new StaticPaneEX(0,0,9,6);
    }

    @Override
    void setStyle(){
        Pane.setCircumference(new ItemStack(Material.GLASS_PANE),9,6);
        ItemStack HomeButton = new ItemStack(Material.BOOK);
        ItemMeta HBIm = HomeButton.getItemMeta();
        HBIm.displayName(Component.text("Back to the Main Menu"));
        HomeButton.setItemMeta(HBIm);
        Pane.setCommandTriggerItem(HomeButton,"ezgui menu",4,5);
    }
}

class GUIEncode extends ListGUIStandard {

    @Override
    void PageName(){
        PageName = "MUSIC LIST";
        Path = EZServerMusic.RawMusicPath;
    }

    @Override
    void contents(){
        int x=0,y=1;
        List<String> list = Utils.FileNameInDirectory(Path);
        list.replaceAll(elem -> elem.replaceAll("\\.ogg",""));
        for(String name:list){
            //新規 ItemStack を生成しないと同じところが参照されるので注意
            ItemStack item = new ItemStack(Material.BOOK);

            ItemMeta ItemMeta = item.getItemMeta();
            ItemMeta.addEnchant(Enchantment.DURABILITY,1,true);
            ItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ItemMeta.displayName(Component.text(name));

            item.setItemMeta(ItemMeta);

            GuiItem elem = new GuiItem(item,event -> {
                Player p = (Player) event.getWhoClicked();

                EncodeClass.Callback ENCcallback = result -> {
                    if (result) {
                        System.out.println("Encode is complete");
                    }else {
                        System.out.println("Encode is Failed");
                    }
                };

                EncodeClass enc = new EncodeClass(name, ENCcallback);

                if(! Utils.FileNameInDirectory(EZServerMusic.OGGPath)
                        .contains(name.replaceAll("\\..+",".ogg").toLowerCase())){
                    p.playSound(Sound.sound(Key.key("ui.button.click"),Sound.Source.PLAYER,1,1f));
                    enc.start();
                }else{
                    p.playNote(p.getLocation(), Instrument.BASS_GUITAR, Note.natural(0, Note.Tone.B));
                    p.sendMessage(Component.text("Exist!"));
                }
                event.setCancelled(true);
            });
            Pane.addItem(elem,++x,y);
            if(x >= 8){
                x = 0;
                ++y;
            }
        }
    }
}

class ResourcePackSelector extends ListGUIStandard{

    String Path;

    @Override
    void PageName(){
        PageName = "Resourcepack selection";
        Path = EZServerMusic.LRPHPath;
    }

    @Override
    void contents(){
        int x=0,y=1;
        List<String> list = Utils.FileNameInDirectory(Path);
        list.replaceAll(element -> element.replaceAll("\\.zip",""));
        for(String name:list){
            ItemStack item = new ItemStack(Material.BOOK);

            ItemMeta ItemMeta = item.getItemMeta();
            ItemMeta.addEnchant(Enchantment.DURABILITY,1,true);
            ItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            ItemMeta.displayName(Component.text(name));

            item.setItemMeta(ItemMeta);

            GuiItem elem = new GuiItem(item,event -> {
                Player p = (Player) event.getWhoClicked();
                p.performCommand("resourcepack "+name+" "+p.getName());
                p.playSound(Sound.sound(Key.key("ui.button.click"),Sound.Source.PLAYER,1,1f));
                event.setCancelled(true);
            });
            Pane.addItem(elem,++x,y);
            if(x >= 8) {
                x = 0;
                ++y;
            }
        }
    }
}
