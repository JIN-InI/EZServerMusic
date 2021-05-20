package tokyo.ini.plugin.id.ezservermusic;

import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StaticPaneEX extends StaticPane {
    public StaticPaneEX(int x, int y, int length, int height, Priority priority) {
        super(x, y, length, height, priority);
    }
    public StaticPaneEX(int x, int y, int length, int height) {
        super(x, y, length, height);
    }
    public StaticPaneEX(int length, int height) {
        super(length, height);
    }

    public void setCircumference(ItemStack item, int length, int height){
        ItemMeta im = item.getItemMeta();
        im.displayName(null);
        item.setItemMeta(im);
        for(int y=0;y<=height;y++) {
            for(int x=0;x<=length;x++) {
                if((y==0 || y==height-1) || (x==0 || x==length-1)) {
                    this.addItem(new GuiItem(item, e -> e.setCancelled(true)),x,y);
                }
            }
        }
    }

    public void setDisplayItem(ItemStack item, int x, int y){
        this.addItem(new GuiItem(item,e -> e.setCancelled(true)),x,y);
    }

    public void setCommandTriggerItem(ItemStack item,String command, int x, int y){
        this.addItem(new GuiItem(item, event -> {
            Player player = (Player) event.getWhoClicked();
            player.playSound(player.getLocation(),"ui.button.click", SoundCategory.MASTER,1,1f);
            player.performCommand(command);
            event.setCancelled(true);
        }),x,y);
    }
}
