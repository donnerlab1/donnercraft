package org.donnerlab.donnercraft.exampleplugin;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import java.awt.Image;

public class ImageRenderer extends MapRenderer {

    private Image image;
    private boolean hasRendered = false;
    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
    	if (hasRendered)
    		return;

    	canvas.drawText(0, 0, MinecraftFont.Font, "Scan this Invoice to Pay");
        canvas.drawImage(0,8, image);
        hasRendered = true;
    }

    ImageRenderer(Image image) {
        this.image = image;
    }
}
