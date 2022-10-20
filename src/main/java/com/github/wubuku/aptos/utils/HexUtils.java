package com.github.wubuku.aptos.utils;

import java.util.ArrayList;
import java.util.List;

public class HexUtils {

    /**
     * Convert address hex to 32 bytes.
     */
    public static byte[] hexToAccountAddressBytes(String hex) {
        byte[] addressBytes = HexUtils.hexToByteArray(hex);
        if (addressBytes.length < 32) {
            byte[] bs = new byte[32];
            for (int i = 0; i < addressBytes.length; i++) {
                bs[bs.length - addressBytes.length - i] = addressBytes[i];
            }
            addressBytes = bs;
        }
        return addressBytes;
    }

    public static byte[][] hexArrayToByteArrays(String[] hs) {
        List<byte[]> bytesList = new ArrayList<>(hs.length);
        for (String h : hs) {
            bytesList.add(hexToByteArray(h));
        }
        return bytesList.toArray(new byte[0][]);
    }

    public static byte hexToByte(String h) {
        return (byte) Integer.parseInt(h, 16);
    }

    public static byte[] hexToByteArray(String h) {
        String tmp = h.substring(0, 2);
        if (tmp.equals("0x")) {
            h = h.substring(2);
        }
        int hexlen = h.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            hexlen++;
            result = new byte[(hexlen / 2)];
            h = "0" + h;
        } else {
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(h.substring(i, i + 2));
            j++;
        }
        return result;
    }

    public static String byteListToHexWithPrefix(List<Byte> bytes) {
        return "0x" + byteListToHex(bytes);
    }

    public static String byteListToHex(List<Byte> bytes) {
        byte[] byteArray = toPrimitive(bytes.toArray(new Byte[0]));
        return byteArrayToHex(byteArray);
    }

    public static byte[] toPrimitive(final Byte[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new byte[0];
        }
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].byteValue();
        }
        return result;
    }

    public static String byteArrayToHexWithPrefix(byte[] bytes) {
        return "0x" + byteArrayToHex(bytes);
    }

    public static String[] bytesArrayToHexArray(byte[][] bytes) {
        List<String> hexArray = new ArrayList<>();
        for (byte[] bs : bytes) {
            String h = bs == null ? null : byteArrayToHex(bs);
            hexArray.add(h);
        }
        return hexArray.toArray(new String[0]);
    }

    public static String byteArrayToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (int index = 0, len = bytes.length; index <= len - 1; index += 1) {
            String hex1 = Integer.toHexString((bytes[index] >> 4) & 0xF);
            String hex2 = Integer.toHexString(bytes[index] & 0xF);
            result.append(hex1);
            result.append(hex2);
        }
        return result.toString();
    }
}

