package org.donnerlab.donnercraft.exampleplugin;

import io.grpc.stub.StreamObserver;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;


import lnrpc.LightningGrpc.*;
import lnrpc.Rpc.*;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public final class ExamplePlugin extends JavaPlugin implements Listener {
    private static final String CERT_PATH = "tls.cert";
    private static final String MACAROON_PATH = "./admin.macaroon";
    private static final String HOST = "localhost";

    private static final int PORT = 10009;

    private LightningBlockingStub blockingStub;
    private LightningStub asyncStub;

    public LndRpc lndRpc;

    private Map<String, Player> teleportInvoices;
    @Override
    public void onEnable() {
        teleportInvoices = new HashMap<>();

        SetupRpc();
        getCommand("invoice").setExecutor(new CommandInvoice(this));
        getCommand("teleport").setExecutor(new CommandTeleport(this));
        GetInfoResponse response = lndRpc.blockingStub.getInfo(GetInfoRequest.getDefaultInstance());
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

    public void SetupRpc() {
        lndRpc = new LndRpc();
        System.out.println(lndRpc.getPaymentRequest("test", 2));
        lndRpc.subscribeInvoices(new StreamObserver<Invoice>() {

            @Override
            public void onNext(Invoice invoice) {
                System.out.println("new Invoice: " + invoice.getRPreimage());
                if(teleportInvoices.containsKey(invoice.getPaymentRequest())) {
                    System.out.println("found invoice");
                    Player p = teleportInvoices.get(invoice.getPaymentRequest());

                    Location loc = p.getLocation();
                    Vector dir = loc.getDirection();
                    dir.normalize();
                    dir.multiply((int)invoice.getValue() / 5); //5 blocks a way
                    loc.add(dir);
                    p.teleport(loc );
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("error");
            }

            @Override
            public void onCompleted() {
                System.out.println("completed");
            }
        });
        // Plugin startup logic
        /*
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

        blockingStub = LightningGrpc
                .newBlockingStub(channel)
                .withCallCredentials(new MacaroonCallCredential(macaroon));

        asyncStub = LightningGrpc
                .newStub(channel)
                .withCallCredentials(new MacaroonCallCredential(macaroon));

        asyncStub.subscribeInvoices(InvoiceSubscription.getDefaultInstance(),  new StreamObserver<Invoice>() {

            @Override
            public void onNext(Invoice invoice) {
                System.out.println("new Invoice: " + invoice.getRPreimage());

            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("error");
            }

            @Override
            public void onCompleted() {
                System.out.println("completed");
            }
        });
        */
    }

    public String AddTeleportRequest(int steps, Player p) {
        System.out.println("get teleport request for " + steps + " steps by "+  p.getName());
        String request = lndRpc.getPaymentRequest("teleport", steps*5);
        teleportInvoices.put(request, p);
        return request;
    }


}
