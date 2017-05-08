//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.domob.android.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import cn.domob.android.utils.DeviceInfo;
import cn.domob.android.utils.Logger;
import cn.domob.android.utils.Utility;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

public class FileUtility {
    private static Logger mLogger = new Logger(FileUtility.class.getSimpleName());

    public FileUtility() {
    }

    public static void writeFileString(String localPath, String storageName, String text) {
        if(DeviceInfo.isSdPresent() && text != null && !Utility.isStringNullOrEmpty(localPath) && !Utility.isStringNullOrEmpty(storageName)) {
            localPath = Environment.getExternalStorageDirectory() + localPath;
            File logFile = new File(localPath);
            boolean isMakeFileSuccessfully = false;
            if(!logFile.exists()) {
                try {
                    isMakeFileSuccessfully = logFile.mkdir();
                } catch (Exception var17) {
                    mLogger.printStackTrace(var17);
                }
            } else {
                isMakeFileSuccessfully = true;
            }

            if(isMakeFileSuccessfully) {
                BufferedWriter buf = null;

                try {
                    buf = new BufferedWriter(new FileWriter(localPath + storageName, false));
                    buf.write(text);
                } catch (IOException var16) {
                    mLogger.printStackTrace(var16);
                } finally {
                    if(buf != null) {
                        try {
                            buf.close();
                        } catch (IOException var15) {
                            mLogger.printStackTrace(var15);
                        }
                    }

                }
            }
        }

    }

    public static String readFileString(Context context, String fileName) {
        if(!Utility.isStringNullOrEmpty(fileName) && DeviceInfo.isSdPresent()) {
            fileName = Environment.getExternalStorageDirectory() + fileName;
            StringBuilder stringBuilder = new StringBuilder();
            BufferedReader in = null;

            try {
                in = new BufferedReader(new FileReader(new File(fileName)));

                String line;
                while((line = in.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } catch (FileNotFoundException var16) {
                mLogger.printStackTrace(var16);
            } catch (IOException var17) {
                mLogger.printStackTrace(var17);
            } finally {
                if(in != null) {
                    try {
                        in.close();
                    } catch (IOException var15) {
                        mLogger.printStackTrace(var15);
                    }
                }

            }

            return stringBuilder.toString();
        } else {
            return null;
        }
    }

    public static String getFileMD5(String filePath) {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(filePath);
            byte[] e = new byte[8192];
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            boolean numRead = false;

            int numRead1;
            while((numRead1 = fis.read(e)) > 0) {
                md5.update(e, 0, numRead1);
            }

            String var5 = toHexString(md5.digest(), "");
            return var5;
        } catch (Exception var15) {
            mLogger.errorLog("", "getFileMD5 has an exception " + var15.getMessage());
        } finally {
            if(fis != null) {
                try {
                    fis.close();
                } catch (IOException var14) {
                    mLogger.printStackTrace(var14);
                }
            }

        }

        return "";
    }

    public static String getFileMD5(File file) {
        FileInputStream fis = null;
        if(file != null) {
            try {
                fis = new FileInputStream(file);
                byte[] e = new byte[8192];
                MessageDigest md5 = MessageDigest.getInstance("MD5");
                boolean numRead = false;

                int numRead1;
                while((numRead1 = fis.read(e)) > 0) {
                    md5.update(e, 0, numRead1);
                }

                String var5 = toHexString(md5.digest(), "");
                return var5;
            } catch (Exception var15) {
                mLogger.errorLog("", "getFileMD5 has an exception " + var15.getMessage());
                return "";
            } finally {
                if(fis != null) {
                    try {
                        fis.close();
                    } catch (IOException var14) {
                        mLogger.printStackTrace(var14);
                    }
                }

            }
        } else {
            return "";
        }
    }

    public static String getMD5Str(String inStr) {
        if(inStr != null && inStr.length() != 0) {
            try {
                MessageDigest e = MessageDigest.getInstance("MD5");
                e.reset();
                e.update(inStr.getBytes("UTF-8"));
                return toHexString(e.digest(), "");
            } catch (Exception var2) {
                Log.e("----->" + "FileUtility", "getMD5Str:" + var2.toString());
                mLogger.printStackTrace(var2);
                return "";
            }
        } else {
            return "";
        }
    }

    private static String toHexString(byte[] bytes, String separator) {
        StringBuilder hexString = new StringBuilder();
        byte[] arr$ = bytes;
        int len$ = bytes.length;
        for(int i$ = 0; i$ < len$; ++i$) {
            byte b = arr$[i$];
            String hexOfByte = Integer.toHexString(255 & b);
            if(hexOfByte.length() == 1) {
                hexString.append("0").append(hexOfByte);
            } else {
                hexString.append(hexOfByte);
            }
        }

        return hexString.toString();
    }

    public static Drawable getDrawableFromAssets(Context context, String sourceName) {
        BitmapDrawable drawable = null;

        try {
            drawable = new BitmapDrawable(BitmapFactory.decodeStream(Utility.class.getClassLoader().getResourceAsStream("assets/" + sourceName)));
        } catch (Exception var4) {
            mLogger.printStackTrace(var4);
            mLogger.errorLog("Failed to load source file:" + sourceName);
        }

        return drawable;
    }

    public static File fileChannelCopy(File s, File t) {
        if(s.isFile() && t.isFile()) {
            FileInputStream fi = null;
            FileOutputStream fo = null;
            FileChannel in = null;
            FileChannel out = null;

            Object var7;
            try {
                fi = new FileInputStream(s);
                fo = new FileOutputStream(t);
                in = fi.getChannel();
                out = fo.getChannel();
                in.transferTo(0L, in.size(), out);
                return t;
            } catch (IOException var17) {
                mLogger.printStackTrace(var17);
                var7 = null;
            } finally {
                try {
                    fi.close();
                    in.close();
                    fo.close();
                    out.close();
                } catch (IOException var16) {
                    mLogger.printStackTrace(var16);
                }

            }

            return (File)var7;
        } else {
            return null;
        }
    }

    public static long getSDFreeSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        long blockSize = (long)sf.getBlockSize();
        long freeBlocks = (long)sf.getAvailableBlocks();
        return freeBlocks * blockSize;
    }
}
