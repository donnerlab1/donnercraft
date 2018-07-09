package org.donnerlab.donnercraft.exampleplugin;

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


class QRMapSpawner {

<<<<<<< HEAD
    public static ItemStack SpawnMap(Player p, String content) {
=======
    static void SpawnMap(Player p, String content) {
>>>>>>> d00517d5c37ba90812c0d25ef51b17fd8af70bdd
        ItemStack itemStack = new ItemStack(Material.MAP, 1);
        MapView view = Bukkit.createMap(p.getWorld());
        view.getRenderers().clear();
        view.addRenderer(new ImageRenderer(generateQRCode(content)));
        itemStack.setDurability(view.getId());
        p.getInventory().addItem(itemStack);
<<<<<<< HEAD
        return itemStack;
        
=======
>>>>>>> d00517d5c37ba90812c0d25ef51b17fd8af70bdd
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