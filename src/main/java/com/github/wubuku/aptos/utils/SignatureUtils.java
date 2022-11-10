package com.github.wubuku.aptos.utils;

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;

public class SignatureUtils {
    private SignatureUtils() {
    }

    public static byte[] ed25519Sign(byte[] privateKey, byte[] data) {
        Ed25519PrivateKeyParameters key = new Ed25519PrivateKeyParameters(privateKey,
                0);
        Ed25519Signer signer = new Ed25519Signer();
        signer.init(true, key);
        signer.update(data, 0, data.length);
        byte[] rst = signer.generateSignature();
        return rst;
    }

    public static boolean ed25519Verify(byte[] publicKey, byte[] data, byte[] signature) {
        Ed25519PublicKeyParameters key = new Ed25519PublicKeyParameters(publicKey, 0);
        Ed25519Signer signer = new Ed25519Signer();
        signer.init(false, key);
        signer.update(data, 0, data.length);
        return signer.verifySignature(signature);
    }


}
