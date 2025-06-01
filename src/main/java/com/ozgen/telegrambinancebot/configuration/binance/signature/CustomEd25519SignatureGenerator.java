package com.ozgen.telegrambinancebot.configuration.binance.signature;

import com.binance.connector.client.utils.signaturegenerator.SignatureGenerator;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

public final class CustomEd25519SignatureGenerator implements SignatureGenerator {

    private Ed25519PrivateKeyParameters privateKey;

    public CustomEd25519SignatureGenerator(String privateKeyPem) throws IOException {
        Security.addProvider(new BouncyCastleProvider());
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPem);
        this.privateKey = (Ed25519PrivateKeyParameters) PrivateKeyFactory.createKey(privateKeyBytes);
    }

    public String getSignature(String data) {
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        Ed25519Signer signer = new Ed25519Signer();
        signer.init(true, this.privateKey);
        signer.update(dataBytes, 0, dataBytes.length);
        byte[] signatureBytes = signer.generateSignature();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }
}
