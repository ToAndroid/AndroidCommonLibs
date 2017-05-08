//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.domob.android.utils;

import cn.domob.android.utils.Base64;
import cn.domob.android.utils.Logger;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

public class Utility {
    private static Logger mLogger = new Logger(Utility.class.getSimpleName());
    private static final String ALGORITHM_DES = "DES";

    public Utility() {
    }

    public static String getHttpParamsStr(HashMap<String, String> paramsMap) {
        try {
            UrlEncodedFormEntity e = null;
            ArrayList _paramsList = new ArrayList();
            StringBuilder paramStringBuilder = new StringBuilder();
            Iterator reader = paramsMap.keySet().iterator();

            String line;
            while(reader.hasNext()) {
                line = (String)reader.next();
                _paramsList.add(new BasicNameValuePair(line, (String)paramsMap.get(line)));
            }

            e = new UrlEncodedFormEntity(_paramsList, "UTF-8");
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(e.getContent()));
            line = null;

            while((line = reader1.readLine()) != null) {
                paramStringBuilder.append(line);
            }

            return paramStringBuilder.toString();
        } catch (Exception var6) {
            mLogger.printStackTrace(var6);
            return null;
        }
    }

    public static HashMap<String, String> getHttpParamsMap(String paramsStr) {
        HashMap paramsMap = new HashMap();
        if(paramsStr != null) {
            String[] keyValuePairs = paramsStr.split("&");
            String[] arr$ = keyValuePairs;
            int len$ = keyValuePairs.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String keyValuePair = arr$[i$];
                String[] keyToValue = keyValuePair.split("=");

                try {
                    if(keyToValue.length == 2) {
                        paramsMap.put(URLDecoder.decode(keyToValue[0], "UTF-8"), URLDecoder.decode(keyToValue[1], "UTF-8"));
                    } else {
                        paramsMap.put(URLDecoder.decode(keyToValue[0], "UTF-8"), "");
                    }
                } catch (UnsupportedEncodingException var9) {
                    mLogger.printStackTrace(var9);
                    mLogger.errorLog("URL decode params String error:" + paramsStr);
                }
            }
        }

        return paramsMap;
    }

    public static String aesEncrypt(String key, String plainText) {
        try {
            byte[] e = key.getBytes("UTF-8");
            byte[] keyBytes16 = new byte[16];
            System.arraycopy(e, 0, keyBytes16, 0, Math.min(e.length, 16));
            byte[] plainBytes = plainText.getBytes("UTF-8");
            SecretKeySpec skeySpec = new SecretKeySpec(keyBytes16, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
            cipher.init(1, skeySpec);
            byte[] encrypted = cipher.doFinal(plainBytes);
            byte[] encoded = Base64.encode(encrypted, 2);
            String encodedStr = new String(encoded);
            return encodedStr;
        } catch (Exception var10) {
            return "";
        }
    }

    public static String desEncrpyt(String key, String data) {
        try {
            DESKeySpec e = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(e);
            Cipher cipher = Cipher.getInstance("DES");
            IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
            cipher.init(1, secretKey, iv);
            byte[] bytes = cipher.doFinal(data.getBytes());
            return Base64.encodeToString(bytes, 2);
        } catch (Exception var9) {
            mLogger.debugLog("des encode error");
            return null;
        }
    }

    public static boolean isInteger(String s) {
        if(s == null) {
            return false;
        } else {
            Pattern pattern = Pattern.compile("[0-9]*");
            return pattern.matcher(s).matches();
        }
    }

    public static String getReadableSDKVersion(String sdkVersion) {
        try {
            StringBuilder e = new StringBuilder();
            e.append(Integer.valueOf(sdkVersion.substring(0, 2))).append(".").append(Integer.valueOf(sdkVersion.substring(2, 4))).append(".").append(Integer.valueOf(sdkVersion.substring(4, 6)));
            return e.toString();
        } catch (Exception var3) {
            mLogger.printStackTrace(var3);
            return sdkVersion;
        }
    }

    public static String getReadableBuildDate(String sdkBulidDate) {
        try {
            StringBuilder e = new StringBuilder();
            e.append(sdkBulidDate.substring(0, 4)).append("-").append(sdkBulidDate.substring(4, 6)).append("-").append(sdkBulidDate.substring(6, 8));
            return e.toString();
        } catch (Exception var3) {
            mLogger.printStackTrace(var3);
            return sdkBulidDate;
        }
    }

    public static boolean isStringNullOrEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String getCurrentTimeToId() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINESE);
        String result = sdf.format(new Date());
        return result;
    }

    public static String getStringRandom(int length) {
        String val = "";
        Random random = new Random();

        for(int i = 0; i < length; ++i) {
            String charOrNum = random.nextInt(2) % 2 == 0?"char":"num";
            if("char".equalsIgnoreCase(charOrNum)) {
                int temp = random.nextInt(2) % 2 == 0?65:97;
                val = val + (char)(random.nextInt(26) + temp);
            } else if("num".equalsIgnoreCase(charOrNum)) {
                val = val + String.valueOf(random.nextInt(10));
            }
        }

        return val;
    }

    public static int getGCD(int x, int y) {
        int result;
        if(x > y) {
            result = x;
            x = y;
            y = result;
        }

        result = 1;

        for(int i = 2; i <= x; ++i) {
            if(x % i == 0 && y % i == 0) {
                result *= i;
                x /= i;
                y /= i;
            }
        }

        return result;
    }
}
