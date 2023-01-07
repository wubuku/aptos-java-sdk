package com.github.wubuku.aptos;

import com.github.wubuku.aptos.utils.HexUtils;
import com.github.wubuku.aptos.utils.SignatureUtils;
import org.junit.jupiter.api.Test;

public class SignatureUtilsTests {
    @Test
    void testSign_1() {
        byte[] publicKey = HexUtils.hexToByteArray("cd283a91930533987b1d2429db1b0453d03e5b188d00298a4bb6415f6cbf414e");
        byte[] privateKey = HexUtils.hexToByteArray("");//todo fill in private key

        byte[] signature = SignatureUtils.ed25519Sign(privateKey, "hello".getBytes());
        boolean ok = SignatureUtils.ed25519Verify(publicKey, "hello".getBytes(), signature);
        System.out.println(ok);
    }
}
