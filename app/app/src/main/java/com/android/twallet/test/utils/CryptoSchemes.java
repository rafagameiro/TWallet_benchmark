package com.android.twallet.test.utils;

public class CryptoSchemes {

    public enum SignMode {
        SHA1withRSA(1, "SHA1withRSA"), SHA224withRSA(2, "SHA224withRSA"), SHA256withRSA(3, "SHA256withRSA"), SHA384withRSA(4, "SHA384withRSA"), SHA512withRSA(5, "SHA512withRSA"),
        SHA1withRSAPSS(6, "SHA1withRSA/PSS"), SHA224withRSAPSS(7, "SHA224withRSA/PSS"), SHA256withRSAPSS(8, "SHA256withRSA/PSS"), SHA384withRSAPSS(9, "SHA384withRSA/PSS"), SHA512withRSAPSS(10, "SHA512withRSAPSS"),
        SHA256withECDSA(11, "SHA256withECDSA");

        private int code;
        private String cipher;

        SignMode(int code, String cipher) {
            this.code = code;
            this.cipher = cipher;
        }

        public int getCode() {
            return code;
        }

        public String getCipher() { return cipher; }
    }

    public enum HashMode {
        SHA1(1), SHA224(2), SHA256(3), SHA384(4), SHA512(5);

        private int code;

        HashMode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public enum keyGen {
        RSA(1), ECDSA(2);

        private int code;

        keyGen(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public static String codeToCipher(int code) {
        
        String cipher = null;
        for (SignMode s : SignMode.values()) {
            if (s.getCode() == code) {
                cipher = s.getCipher();
                break;
            }
        }
        
        return cipher;
    }

}
