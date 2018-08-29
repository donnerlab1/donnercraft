package org.donnerlab.donnercraft;

import io.grpc.ManagedChannel;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslProvider;
import lnrpc.LightningGrpc;
import lnrpc.Rpc;
import org.apache.commons.codec.binary.Hex;
import org.donnerlab.donnercraft.Utility.MacaroonCallCredential;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LndRpc {
    private static final String CERT_PATH = "tls.cert";
    private static final String MACAROON_PATH = "./admin.macaroon";
    private static final String HOST = "localhost";

    private static final int PORT = 10009;

    public LightningGrpc.LightningBlockingStub blockingStub;
    public LightningGrpc.LightningStub asyncStub;
    public LndRpc() {

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

        asyncStub.subscribeInvoices(Rpc.InvoiceSubscription.getDefaultInstance(),  new StreamObserver<Rpc.Invoice>() {

            @Override
            public void onNext(Rpc.Invoice invoice) {
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
    }

    public void subscribeInvoices(StreamObserver<Rpc.Invoice> oberserver) {
        asyncStub.subscribeInvoices(Rpc.InvoiceSubscription.getDefaultInstance(), oberserver);
    }

    public String getPaymentRequest(String memo, int amt) {
        return blockingStub.addInvoice(Rpc.Invoice.newBuilder().setMemo(memo).setValue((long)amt).build()).getPaymentRequest();
    }


}
