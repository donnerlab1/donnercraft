package org.donnerlab.donnercraft.exampleplugin;

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslProvider;
import org.apache.commons.codec.binary.Hex;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import lnrpc.LightningGrpc;
import lnrpc.LightningGrpc.LightningBlockingStub;
import lnrpc.Rpc.GetInfoRequest;
import lnrpc.Rpc.GetInfoResponse;

public final class ExamplePlugin extends JavaPlugin implements Listener {
    private static final String CERT_PATH = "tls.cert";
    private static final String MACAROON_PATH = "./admin.macaroon";
    private static final String HOST = "localhost";

    private static final int PORT = 10009;
    @Override
    public void onEnable() {
        // Plugin startup logic

        getServer().getPluginManager().registerEvents(this, this);
        SslContext sslContext = null;
        try {
            sslContext = GrpcSslContexts.forClient().sslProvider(SslProvider.OPENSSL).trustManager(new File(CERT_PATH)).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NettyChannelBuilder channelBuilder = NettyChannelBuilder.forAddress(HOST, PORT);
        ManagedChannel channel = channelBuilder.sslContext(sslContext).build();

        System.out.println("done");
        String macaroon =
                null;
        try {
            macaroon = Hex.encodeHexString(
                    Files.readAllBytes(Paths.get(MACAROON_PATH))
            );
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }

        LightningBlockingStub stub = LightningGrpc
                .newBlockingStub(channel)
                .withCallCredentials(new MacaroonCallCredential(macaroon));

        GetInfoResponse response = stub.getInfo(GetInfoRequest.getDefaultInstance());
        System.out.println(response.getIdentityPubkey());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    @EventHandler
    public void OnPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        e.setJoinMessage(player.getName() + " has joined the server :)");
    }

    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        e.setQuitMessage(player.getName() + " has quit the server :/");
    }
}
