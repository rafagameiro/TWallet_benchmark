package com.android.twallet.test.utils;

import static com.android.twallet.test.utils.CryptoSchemes.SignMode.SHA256withECDSA;

import org.bouncycastle.jce.ECNamedCurveTable;

import java.math.BigInteger;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class AttestationValidator {

    private static final String SIGN_ALGORITHM = "SHA256withRSA";

    public static boolean validateProof(String digest, String proof, String modulus, String exp) {

        Base64.Decoder decoder = Base64.getDecoder();

        try {
            BigInteger modulusBig = new BigInteger(-1, decoder.decode(modulus));
            BigInteger expBig = new BigInteger(exp);

            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulusBig.abs(), expBig);

            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey pub = factory.generatePublic(spec);
            Signature sign = Signature.getInstance(SIGN_ALGORITHM);

            sign.initVerify(pub);

            sign.update(decoder.decode(digest));

            return sign.verify(decoder.decode(proof));

        } catch (Exception e) {
            return false;
        }
    }

    public static String validateProofBenchmark(int signMode, String digest, String proof, String exp, String modulus) {

        Base64.Decoder decoder = Base64.getDecoder();

        try {
            BigInteger modulusBig = new BigInteger(-1, decoder.decode(modulus));
            BigInteger expBig = new BigInteger(exp);

            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulusBig.abs(), expBig);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey pub = factory.generatePublic(spec);

            Signature sign = getSignature(CryptoSchemes.codeToCipher(signMode));
            sign.initVerify(pub);

            sign.update(decoder.decode(digest));

            return sign.verify(decoder.decode(proof))+"";

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static boolean validateProofBenchmarkEC(String digest, String proof, String x, String y) {

        Base64.Decoder decoder = Base64.getDecoder();

        try {
            BigInteger bigX = new BigInteger(x);
            BigInteger bigY = new BigInteger(y);

            AlgorithmParameters params = AlgorithmParameters.getInstance("EC", "BC");
            params.init(new ECGenParameterSpec("prime256v1"));

            ECParameterSpec ecParameterSpec = params.getParameterSpec(ECParameterSpec.class);
            ECPoint p = new ECPoint(bigX, bigY);
            ECPublicKeySpec spec = new ECPublicKeySpec(p, ecParameterSpec);

            KeyFactory factory = KeyFactory.getInstance("ECDSA");
            PublicKey pub = factory.generatePublic(spec);
            Signature sign = Signature.getInstance(SHA256withECDSA.getCipher());

            sign.initVerify(pub);

            sign.update(decoder.decode(digest));

            return sign.verify(decoder.decode(proof));

        } catch (Exception e) {
            return false;
        }
    }

    private static Signature getSignature(String signMode) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        Signature sign = Signature.getInstance(signMode);

        if (signMode.contains("PSS")) {
            if(signMode.contains("1"))
                sign.setParameter(new PSSParameterSpec("SHA-1", "MGF1", MGF1ParameterSpec.SHA1, 20, 1));
            else if (signMode.contains("224"))
                sign.setParameter(new PSSParameterSpec("SHA-224", "MGF1", MGF1ParameterSpec.SHA224, 28, 1));
            else if (signMode.contains("256"))
                sign.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
            else if (signMode.contains("384"))
                sign.setParameter(new PSSParameterSpec("SHA-384", "MGF1", MGF1ParameterSpec.SHA384, 48, 1));
            else
                sign.setParameter(new PSSParameterSpec("SHA-512", "MGF1", MGF1ParameterSpec.SHA512, 64, 1));
        }

        return sign;
    }

}
