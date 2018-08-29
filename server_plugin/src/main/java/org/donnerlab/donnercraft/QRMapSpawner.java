package org.donnerlab.donnercraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.image.BufferedImage;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.bukkit.map.MapView;
import org.donnerlab.donnercraft.Utility.ImageRenderer;


class QRMapSpawner {

    public static ItemStack SpawnMap(Player p, String content) {
        ItemStack itemStack = new ItemStack(Material.MAP, 1);
        MapView view = Bukkit.createMap(p.getWorld());
        view.getRenderers().clear();
        view.addRenderer(new ImageRenderer(generateQRCode(content)));
        itemStack.setDurability(view.getId());
        p.getInventory().addItem(itemStack);
        return itemStack;
        
    }

    private static BufferedImage generateQRCode(String content) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 128, 120);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e){
        	e.printStackTrace();
		}
        return null;
    }

}