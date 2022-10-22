package com.github.wubuku.aptos.utils;


import com.github.wubuku.aptos.bean.Pair;
import com.github.wubuku.aptos.bean.Triple;
import com.github.wubuku.aptos.types.AccountAddress;
import com.github.wubuku.aptos.types.TypeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StructTagUtils {
    //public static final int ACCOUNT_ADDRESS_LENGTH = 32;
    private static final String COLON_COLON = "::";
    private static final String LT = "<";
    private static final String GT = ">";
    private static final String COMMA = ",";

    private StructTagUtils() {
    }

    public static TypeInfo toTypeInfo(StructTagUtils.StructTag t) {
        TypeInfo.Builder builder = new TypeInfo.Builder();
        byte[] addressBytes = HexUtils.hexToAccountAddressBytes(t.getAddress());
        builder.accountAddress = AccountAddress.valueOf(addressBytes);
        builder.moduleName = t.getModule();
        builder.structName = t.getName();
        return builder.build();
    }

    public static com.github.wubuku.aptos.bean.TypeInfo toTypeInfoBean(StructTagUtils.StructTag t) {
        com.github.wubuku.aptos.bean.TypeInfo typeInfo = new com.github.wubuku.aptos.bean.TypeInfo();
        typeInfo.setAccountAddress(t.getAddress());
        typeInfo.setModuleName(t.getModule());
        typeInfo.setStructName(t.getName());
        return typeInfo;
    }

    public static String format(StructTag structTag) {
        StringBuilder sb = new StringBuilder();
        sb.append(trimAddress(structTag.address).toLowerCase());
        sb.append("::");
        sb.append(structTag.getModule());
        sb.append("::");
        sb.append(structTag.getName());
        if (structTag.typeParams != null && !structTag.typeParams.isEmpty()) {
            sb.append("<");
            for (int i = 0; i < structTag.typeParams.size(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                TypeTag t = structTag.typeParams.get(i);
                if (t instanceof RawTypeTag) {
                    sb.append(((RawTypeTag) t).literalValue);
                } else if (t instanceof StructTag) {
                    sb.append(format((StructTag) t));
                } else {
                    throw new UnsupportedOperationException("Not supported type tag");
                }
            }
            sb.append(">");
        }
        return sb.toString();
    }

    public static String trimAddress(String address) {
        if (!address.startsWith("0x")) {
            throw new IllegalArgumentException("Not a legal address: " + address);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("0x");
        for (int i = 2; i < address.length(); i++) {
            if (address.charAt(i) != '0') {
                sb.append(address.substring(i));
                break;
            } //else skip
        }
        return sb.toString();
    }

    public static StructTag parseStructTag(String s) {
        String[] parts = s.split(COLON_COLON, 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException(s);
        }
        String address = parts[0].trim();
        String moduleName = parts[1].trim();
        String structName;
        List<TypeTag> typeParams;
        if (!parts[2].contains(LT)) {
            structName = parts[2].trim();
            typeParams = Collections.emptyList();
        } else {
            int idx_lt = parts[2].indexOf(LT);
            int idx_gt = parts[2].lastIndexOf(GT);
            if (idx_lt < 1 || idx_gt < 0 || idx_lt > idx_gt) {
                throw new IllegalArgumentException(s);
            }
            structName = parts[2].substring(0, idx_lt).trim();
            String ts = parts[2].substring(idx_lt + 1, idx_gt);
            if (isOneStructTagWithTypeParams(ts)) {
                typeParams = Collections.singletonList(parseStructTag(ts));
                //System.out.println("parsed type param: " + typeParams.get(0));
            } else {
                typeParams = readTypeTagList(ts);
            }
        }
        return new StructTag(address, moduleName, structName, typeParams);
    }

    private static boolean isOneStructTagWithTypeParams(String ts) {
        // "0x1::coin::CoinInfo<T>" or "0x1::coin::CoinInfo<T, ...>"
        return startWithStructTagWithTypeParams(ts) && ts.endsWith(GT);
    }

    private static boolean startWithStructTagWithTypeParams(String ts) {
        // "0x1::coin::CoinInfo<..."
        int typeParams_idx_colon_colon = ts.indexOf(COLON_COLON);
        int typeParams_idx_lt = ts.indexOf(LT);
        int typeParams_idx_comma = ts.indexOf(COMMA);
        boolean startWithStructTagWithTypeParams = typeParams_idx_colon_colon > 0 &&
                typeParams_idx_lt > 0 && typeParams_idx_lt > typeParams_idx_colon_colon
                && (typeParams_idx_comma < 0 || typeParams_idx_comma > typeParams_idx_lt);
        return startWithStructTagWithTypeParams;
    }

    //"0x1::coin::CoinInfo"
    //"u8"
    //"0x1::coin::CoinInfo, u8"
    //"0x1::coin::CoinInfo<u8, u64>, u8"
    private static List<TypeTag> readTypeTagList(String ts) {
        List<TypeTag> typeTags = new ArrayList<>();
        Triple<TypeTag, Boolean, String> triple = readTypeParam(ts, 0);
        while (!triple.getItem2() && triple.getItem1() != null) {
            typeTags.add(triple.getItem1());
            triple = readTypeParam(triple.getItem3(), 0);
        }
        return typeTags;
    }

    /**
     * Read a type param from the string.
     *
     * @return (TypeTag, End of current depth, Tail of the string) triple.
     */
    private static Triple<TypeTag, Boolean, String> readTypeParam(String ts, int depth) {
        if (ts == null || ts.trim().isEmpty()) {
            return new Triple<>(null, false, null);
        }
        if (startWithStructTagWithTypeParams(ts)) {
            int idx_lt = ts.indexOf(LT);
            StructTag parent = parseStructTag(ts.substring(0, idx_lt));
            String tail = ts.substring(idx_lt + 1);
            List<TypeTag> typeTags = new ArrayList<>();
            Triple<TypeTag, Boolean, String> triple = readTypeParams(tail, depth + 1, typeTags);
            parent.setTypeParams(typeTags);
            return new Triple<>(parent, false, triple.getItem3());
        }
        Pair<String[], Integer> partsAndIdx = splitByCommaOrGt(ts);
        String[] parts = partsAndIdx.getItem1();
        int idx_comma_or_gt = partsAndIdx.getItem2();
        String head = parts[0].trim();
        TypeTag typeTag = null;
        if (!head.isEmpty()) {
            typeTag = parseTypeTag(head);
        }
        String separator = idx_comma_or_gt > 0 ? ts.substring(idx_comma_or_gt, idx_comma_or_gt + 1) : null;
        boolean endOfDepth = false;
        if (separator != null && separator.equals(GT)) {
            if (depth - 1 < 0) {
                throw new IllegalArgumentException(ts);
            }
            endOfDepth = true;
        }
        String tail = null;
        if (parts.length > 1 && !parts[1].trim().isEmpty()) {
            tail = parts[1].trim();
            if (endOfDepth && tail.startsWith(COMMA)) { // ...>,
                tail = tail.substring(1).trim();
            }
        }
        return new Triple<>(typeTag, endOfDepth, tail);
    }

    /**
     * @return Head and tail of the string, and the index of the separator.
     */
    private static Pair<String[], Integer> splitByCommaOrGt(String ts) {
        int idx_comma = ts.indexOf(COMMA);
        int idx_gt = ts.indexOf(GT);
        int idx_comma_or_gt = (idx_comma > 0 && idx_gt > 0) ? Math.min(idx_comma, idx_gt) : Math.max(idx_comma, idx_gt);
        String[] parts = idx_comma_or_gt > 0
                ? new String[]{ts.substring(0, idx_comma_or_gt), ts.substring(idx_comma_or_gt + 1)}
                : new String[]{ts};
        return new Pair<>(parts, idx_comma_or_gt);
    }

    private static Triple<TypeTag, Boolean, String> readTypeParams(String ts, int nextDepth, List<TypeTag> typeTags) {
        Triple<TypeTag, Boolean, String> triple = readTypeParam(ts, nextDepth);
        boolean endOfDepth = false;
        while (triple.getItem1() != null) {
            typeTags.add(triple.getItem1());
            if (triple.getItem2()) {
                endOfDepth = true;
                break;
            }
            triple = readTypeParam(triple.getItem3(), nextDepth);
        }
        if (!endOfDepth) {
            throw new IllegalArgumentException(ts);
        }
        return triple;
    }

    private static TypeTag parseTypeTag(String t) {
        TypeTag typeTag;
        if (!t.contains(COLON_COLON)) {
            //throw new UnsupportedOperationException("ONLY support struct as type param");
            typeTag = new RawTypeTag(t.trim());
        } else {
            typeTag = parseStructTag(t.trim());
        }
        //System.out.println("parsed type tag: " + typeTag);
        return typeTag;
    }

    public static class TypeTag {
    }

    public static class StructTag extends TypeTag {
        private String address;
        private String module;
        private String name;
        private List<TypeTag> typeParams;

        public StructTag() {
        }

        public StructTag(String address, String module, String name) {
            this.address = address;
            this.module = module;
            this.name = name;
        }

        public StructTag(String address, String module, String name, List<TypeTag> typeParams) {
            this.address = address;
            this.module = module;
            this.name = name;
            this.typeParams = typeParams;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<TypeTag> getTypeParams() {
            return typeParams;
        }

        public void setTypeParams(List<TypeTag> typeParams) {
            this.typeParams = typeParams;
        }

        @Override
        public String toString() {
            return "StructTag{" +
                    "address='" + address + '\'' +
                    ", module='" + module + '\'' +
                    ", name='" + name + '\'' +
                    ", typeParams=" + typeParams +
                    '}';
        }
    }

    public static class RawTypeTag extends TypeTag {
        private String literalValue;

        public RawTypeTag() {
        }

        public RawTypeTag(String literalValue) {
            this.literalValue = literalValue;
        }

        public String getLiteralValue() {
            return literalValue;
        }

        public void setLiteralValue(String literalValue) {
            this.literalValue = literalValue;
        }

        @Override
        public String toString() {
            return "RawTypeTag{" +
                    "literalValue='" + literalValue + '\'' +
                    '}';
        }
    }

}
