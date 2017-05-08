//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.domob.android.utils;

import cn.domob.android.utils.Logger;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public final class VerifiableCode {
    private static Logger mLogger = new Logger(VerifiableCode.class.getSimpleName());
    private static String VCODE_VERSION = "01";

    public VerifiableCode() {
    }

    public static VerifiableCode.Vcode getVcode(String ipb, String ua, String data) {
        String a_pb = ipb;
        String b_ua = ua;
        String c_cid = data;
        VerifiableCode.Vcode vcodeObj = new VerifiableCode.Vcode();

        try {
            byte[] e = a_pb.getBytes("UTF-8");
            byte[] bytes_b = b_ua.getBytes("UTF-8");
            byte[] bytes_c = c_cid.getBytes("UTF-8");
            byte[] byte_abc = new byte[10];
            int temp_index = 3;

            int b_length;
            for(b_length = e.length - 1; b_length >= e.length - 4; --b_length) {
                byte_abc[temp_index] = e[b_length];
                --temp_index;
            }

            b_length = bytes_b.length;
            byte_abc[4] = (byte)(('\uff00' & b_length) >> 8);
            byte_abc[5] = (byte)(255 & b_length);
            byte_abc = generateParaB(bytes_c, byte_abc);
            byte[] md5_bytes = getMD5Str(new String(byte_abc));
            long currentTs = System.currentTimeMillis();
            vcodeObj.setTs(String.valueOf(currentTs));
            int randNum = (int)(Math.random() * 2.147483647E9D);
            vcodeObj.setRandNumber(String.valueOf(randNum));
            int num_d = (int)(currentTs / 1000L) ^ randNum;
            byte[] bytes_d = intToByte(num_d);

            for(int i = 0; i < bytes_d.length; ++i) {
                md5_bytes[3 - i] = bytes_d[i];
            }

            md5_bytes = generateVcode(md5_bytes);
            vcodeObj.setVcodeValue(VCODE_VERSION + byte2HexStr(md5_bytes));
            return vcodeObj;
        } catch (UnsupportedEncodingException var20) {
            mLogger.printStackTrace(var20);
            return vcodeObj;
        }
    }

    private static byte[] intToByte(int i) {
        byte[] bt = new byte[]{(byte)(255 & i), (byte)(('\uff00' & i) >> 8), (byte)((16711680 & i) >> 16), (byte)((-16777216 & i) >> 24)};
        return bt;
    }

    private static byte getArrayValue(int value, byte[] arr) {
        return value >= arr.length?0:arr[value];
    }

    private static byte[] getMD5Str(String inStr) {
        if(inStr != null && inStr.length() != 0) {
            try {
                MessageDigest e = MessageDigest.getInstance("MD5");
                e.reset();
                e.update(inStr.getBytes("UTF-8"));
                return e.digest();
            } catch (Exception var2) {
                return null;
            }
        } else {
            return null;
        }
    }

    private static byte[] generateParaB(byte[] bytes, byte[] byte_abc) {
        byte byte_abcPosBegin = 6;

        for(int i = 3; i < bytes.length + 3; i += 4) {
            int j;
            if(i < 4) {
                for(j = 3; j >= 0; --j) {
                    byte_abc[byte_abcPosBegin + 3 - j] = getArrayValue(i - j, bytes);
                }
            } else {
                for(j = 3; j >= 0; --j) {
                    byte_abc[byte_abcPosBegin + 3 - j] ^= getArrayValue(i - j, bytes);
                }
            }
        }

        return byte_abc;
    }

    private static byte[] generateVcode(byte[] bytes) {
        for(int i = 4; i <= bytes.length - 4; i += 4) {
            for(int j = 3; j >= 0; --j) {
                bytes[i + 3 - j] = (byte)(bytes[3 - j] ^ getArrayValue(i + 3 - j, bytes));
            }
        }

        return bytes;
    }

    private static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");

        for(int i = 0; i < b.length; ++i) {
            stmp = Integer.toHexString(b[i] & 255);
            sb.append(stmp.length() == 1?"0" + stmp:stmp);
        }

        return sb.toString().toUpperCase().trim();
    }

    public static class Vcode {
        private String ts = "";
        private String randNumber = "";
        private String vcodeValue = "";

        public Vcode() {
        }

        public String getTs() {
            return this.ts;
        }

        public String getRandNumber() {
            return this.randNumber;
        }

        public String getVcodeValue() {
            return this.vcodeValue;
        }

        public void setTs(String ts) {
            this.ts = ts;
        }

        public void setRandNumber(String randNumber) {
            this.randNumber = randNumber;
        }

        public void setVcodeValue(String vcodeValue) {
            this.vcodeValue = vcodeValue;
        }
    }
}
