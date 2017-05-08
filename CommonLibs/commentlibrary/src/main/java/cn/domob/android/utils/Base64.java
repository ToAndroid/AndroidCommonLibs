//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.domob.android.utils;

import java.io.UnsupportedEncodingException;

public class Base64 {
    public static final int DEFAULT = 0;
    public static final int NO_PADDING = 1;
    public static final int NO_WRAP = 2;
    public static final int CRLF = 4;
    public static final int URL_SAFE = 8;
    public static final int NO_CLOSE = 16;

    public static String encodeToString(byte[] input, int flags) {
        try {
            return new String(encode(input, flags), "US-ASCII");
        } catch (UnsupportedEncodingException var3) {
            throw new AssertionError(var3);
        }
    }

    public static byte[] encode(byte[] input, int flags) {
        return encode(input, 0, input.length, flags);
    }

    public static byte[] encode(byte[] input, int offset, int len, int flags) {
        Base64.Encoder encoder = new Base64.Encoder(flags, (byte[])null);
        int output_len = len / 3 * 4;
        if(encoder.do_padding) {
            if(len % 3 > 0) {
                output_len += 4;
            }
        } else {
            switch(len % 3) {
            case 0:
            default:
                break;
            case 1:
                output_len += 2;
                break;
            case 2:
                output_len += 3;
            }
        }

        if(encoder.do_newline && len > 0) {
            output_len += ((len - 1) / 57 + 1) * (encoder.do_cr?2:1);
        }

        encoder.output = new byte[output_len];
        encoder.process(input, offset, len, true);

        assert encoder.op == output_len;

        return encoder.output;
    }

    private Base64() {
    }

    static class Encoder extends Base64.Coder {
        public static final int LINE_GROUPS = 19;
        private static final byte[] ENCODE = new byte[]{(byte)65, (byte)66, (byte)67, (byte)68, (byte)69, (byte)70, (byte)71, (byte)72, (byte)73, (byte)74, (byte)75, (byte)76, (byte)77, (byte)78, (byte)79, (byte)80, (byte)81, (byte)82, (byte)83, (byte)84, (byte)85, (byte)86, (byte)87, (byte)88, (byte)89, (byte)90, (byte)97, (byte)98, (byte)99, (byte)100, (byte)101, (byte)102, (byte)103, (byte)104, (byte)105, (byte)106, (byte)107, (byte)108, (byte)109, (byte)110, (byte)111, (byte)112, (byte)113, (byte)114, (byte)115, (byte)116, (byte)117, (byte)118, (byte)119, (byte)120, (byte)121, (byte)122, (byte)48, (byte)49, (byte)50, (byte)51, (byte)52, (byte)53, (byte)54, (byte)55, (byte)56, (byte)57, (byte)43, (byte)47};
        private static final byte[] ENCODE_WEBSAFE = new byte[]{(byte)65, (byte)66, (byte)67, (byte)68, (byte)69, (byte)70, (byte)71, (byte)72, (byte)73, (byte)74, (byte)75, (byte)76, (byte)77, (byte)78, (byte)79, (byte)80, (byte)81, (byte)82, (byte)83, (byte)84, (byte)85, (byte)86, (byte)87, (byte)88, (byte)89, (byte)90, (byte)97, (byte)98, (byte)99, (byte)100, (byte)101, (byte)102, (byte)103, (byte)104, (byte)105, (byte)106, (byte)107, (byte)108, (byte)109, (byte)110, (byte)111, (byte)112, (byte)113, (byte)114, (byte)115, (byte)116, (byte)117, (byte)118, (byte)119, (byte)120, (byte)121, (byte)122, (byte)48, (byte)49, (byte)50, (byte)51, (byte)52, (byte)53, (byte)54, (byte)55, (byte)56, (byte)57, (byte)45, (byte)95};
        private final byte[] tail;
        int tailLen;
        private int count;
        public final boolean do_padding;
        public final boolean do_newline;
        public final boolean do_cr;
        private final byte[] alphabet;

        public Encoder(int flags, byte[] output) {
            this.output = output;
            this.do_padding = (flags & 1) == 0;
            this.do_newline = (flags & 2) == 0;
            this.do_cr = (flags & 4) != 0;
            this.alphabet = (flags & 8) == 0?ENCODE:ENCODE_WEBSAFE;
            this.tail = new byte[2];
            this.tailLen = 0;
            this.count = this.do_newline?19:-1;
        }

        public int maxOutputSize(int len) {
            return len * 8 / 5 + 10;
        }

        public boolean process(byte[] input, int offset, int len, boolean finish) {
            byte[] alphabet = this.alphabet;
            byte[] output = this.output;
            int op = 0;
            int count = this.count;
            int p = offset;
            len += offset;
            int v = -1;
            int var10000;
            switch(this.tailLen) {
            case 0:
            default:
                break;
            case 1:
                if(offset + 2 <= len) {
                    var10000 = (this.tail[0] & 255) << 16;
                    p = offset + 1;
                    v = var10000 | (input[offset] & 255) << 8 | input[p++] & 255;
                    this.tailLen = 0;
                }
                break;
            case 2:
                if(offset + 1 <= len) {
                    var10000 = (this.tail[0] & 255) << 16 | (this.tail[1] & 255) << 8;
                    p = offset + 1;
                    v = var10000 | input[offset] & 255;
                    this.tailLen = 0;
                }
            }

            if(v != -1) {
                output[op++] = alphabet[v >> 18 & 63];
                output[op++] = alphabet[v >> 12 & 63];
                output[op++] = alphabet[v >> 6 & 63];
                output[op++] = alphabet[v & 63];
                --count;
                if(count == 0) {
                    if(this.do_cr) {
                        output[op++] = 13;
                    }

                    output[op++] = 10;
                    count = 19;
                }
            }

            while(p + 3 <= len) {
                v = (input[p] & 255) << 16 | (input[p + 1] & 255) << 8 | input[p + 2] & 255;
                output[op] = alphabet[v >> 18 & 63];
                output[op + 1] = alphabet[v >> 12 & 63];
                output[op + 2] = alphabet[v >> 6 & 63];
                output[op + 3] = alphabet[v & 63];
                p += 3;
                op += 4;
                --count;
                if(count == 0) {
                    if(this.do_cr) {
                        output[op++] = 13;
                    }

                    output[op++] = 10;
                    count = 19;
                }
            }

            if(finish) {
                int t;
                if(p - this.tailLen == len - 1) {
                    t = 0;
                    v = ((this.tailLen > 0?this.tail[t++]:input[p++]) & 255) << 4;
                    this.tailLen -= t;
                    output[op++] = alphabet[v >> 6 & 63];
                    output[op++] = alphabet[v & 63];
                    if(this.do_padding) {
                        output[op++] = 61;
                        output[op++] = 61;
                    }

                    if(this.do_newline) {
                        if(this.do_cr) {
                            output[op++] = 13;
                        }

                        output[op++] = 10;
                    }
                } else if(p - this.tailLen == len - 2) {
                    t = 0;
                    v = ((this.tailLen > 1?this.tail[t++]:input[p++]) & 255) << 10 | ((this.tailLen > 0?this.tail[t++]:input[p++]) & 255) << 2;
                    this.tailLen -= t;
                    output[op++] = alphabet[v >> 12 & 63];
                    output[op++] = alphabet[v >> 6 & 63];
                    output[op++] = alphabet[v & 63];
                    if(this.do_padding) {
                        output[op++] = 61;
                    }

                    if(this.do_newline) {
                        if(this.do_cr) {
                            output[op++] = 13;
                        }

                        output[op++] = 10;
                    }
                } else if(this.do_newline && op > 0 && count != 19) {
                    if(this.do_cr) {
                        output[op++] = 13;
                    }

                    output[op++] = 10;
                }

                assert this.tailLen == 0;

                assert p == len;
            } else if(p == len - 1) {
                this.tail[this.tailLen++] = input[p];
            } else if(p == len - 2) {
                this.tail[this.tailLen++] = input[p];
                this.tail[this.tailLen++] = input[p + 1];
            }

            this.op = op;
            this.count = count;
            return true;
        }
    }

    abstract static class Coder {
        public byte[] output;
        public int op;

        Coder() {
        }

        public abstract boolean process(byte[] var1, int var2, int var3, boolean var4);

        public abstract int maxOutputSize(int var1);
    }
}
