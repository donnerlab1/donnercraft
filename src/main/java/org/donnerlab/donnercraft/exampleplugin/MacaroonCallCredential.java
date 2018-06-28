package org.donnerlab.donnercraft.exampleplugin;

import io.grpc.*;

import java.util.concurrent.Executor;

public class MacaroonCallCredential implements CallCredentials {
    private final String macaroon;

    MacaroonCallCredential(String macaroon) {
        this.macaroon = macaroon;
    }

    public void thisUsesUnstableApi() {}

    public void applyRequestMetadata(
            MethodDescriptor< ? , ? > methodDescriptor,
            Attributes attributes,
            Executor executor,
            final MetadataApplier metadataApplier
    ) {
        String authority = attributes.get(ATTR_AUTHORITY);
        System.out.println(authority);
        executor.execute(new Runnable() {
            public void run() {
                try {
                    Metadata headers = new Metadata();
                    Metadata.Key<String> macaroonKey = Metadata.Key.of("macaroon", Metadata.ASCII_STRING_MARSHALLER);
                    headers.put(macaroonKey, macaroon);
                    metadataApplier.apply(headers);
                } catch (Throwable e) {
                    metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
                }
            }
        });
    }
}
