package org.donnerlab.donnercraft.exampleplugin;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.Image;

public class ImageRenderer extends MapRenderer {

    private Image image;
    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        canvas.drawImage(0,0, image);
    }

    public ImageRenderer(Image image) {
        this.image = image;
    }
}
