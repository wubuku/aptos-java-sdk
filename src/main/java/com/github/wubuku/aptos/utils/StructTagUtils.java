package com.github.wubuku.aptos.utils;


import com.github.wubuku.aptos.bean.Pair;
import com.github.wubuku.aptos.bean.Triple;
import com.github.wubuku.aptos.types.AccountAddress;
import com.github.wubuku.aptos.types.TypeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StructTagUtils {
    public static final String VECTOR = "vector";
    private static final String COLON_COLON = "::";
    private static final String LT = "<";
    private static final String GT = ">";
    private static final String COMMA = ",";

    //public static final int ACCOUNT_ADDRESS_LENGTH = 32;

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
        formatTypeParams(structTag, sb);
        return sb.toString();
    }

    public static String format(VectorTag structTag) {
        StringBuilder sb = new StringBuilder();
        sb.append(VECTOR);
        formatTypeParams(structTag, sb);
        return sb.toString();
    }

    private static void formatTypeParams(TypeTagWithTypeParams typeTag, StringBuilder sb) {
        if (typeTag.getTypeParams() != null && !typeTag.getTypeParams().isEmpty()) {
            sb.append("<");
            for (int i = 0; i < typeTag.getTypeParams().size(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                TypeTag t = typeTag.getTypeParams().get(i);
                if (t instanceof RawTypeTag) {
                    sb.append(((RawTypeTag) t).literalValue);
                } else if (t instanceof StructTag) {
                    sb.append(format((StructTag) t));
                } else if (t instanceof VectorTag) {
                    sb.append(format((VectorTag) t));
                } else {
                    throw new UnsupportedOperationException("Not supported type tag");
                }
            }
            sb.append(">");
        }
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
//            if (isOneStructTagWithTypeParams(ts)) {
//                typeParams = Collections.singletonList(parseStructTag(ts));
//                System.out.println("parsed type param: " + ts + ", " + typeParams.get(0));
//            } else {
            typeParams = readTypeTagList(ts);
//            }
        }
        return new StructTag(address, moduleName, structName, typeParams);
    }

//    private static boolean isOneStructTagWithTypeParams(String ts) {
//        // "0x1::coin::CoinInfo<T>" or "0x1::coin::CoinInfo<T, ...>"
//        return startsWithStructTagWithTypeParams(ts) && ts.endsWith(GT);
//    }

    private static boolean startsWithStructTagWithTypeParams(String ts) {
        // "0x1::coin::CoinInfo<..."
        int idx_colon_colon = ts.indexOf(COLON_COLON);
        int idx_lt = ts.indexOf(LT);
        int idx_comma = ts.indexOf(COMMA);
        return idx_colon_colon > 0 &&
                idx_lt > 0 && idx_lt > idx_colon_colon
                && (idx_comma < 0 || idx_comma > idx_lt);
    }

    private static boolean startsWithVectorTag(String ts) {
        // "vector<..."
        if (!ts.trim().startsWith(VECTOR)) {
            return false;
        }
        int idx_vector = ts.indexOf(VECTOR);
        int idx_lt = ts.indexOf(LT);
        return idx_vector >= 0 &&
                idx_lt > 0 && idx_lt > idx_vector
                && (ts.substring(idx_vector, idx_lt).trim().equals(VECTOR));
    }

    //"0x1::coin::CoinInfo"
    //"u8"
    //"0x1::coin::CoinInfo, u8"
    //"0x1::coin::CoinInfo<u8, u64>, u8"
    private static List<TypeTag> readTypeTagList(String ts) {
        List<TypeTag> typeTags = new ArrayList<>();
        Triple<TypeTag, Boolean, String> triple = readTypeTag(ts, 0);
        while (!triple.getItem2() && triple.getItem1() != null) {
            typeTags.add(triple.getItem1());
            triple = readTypeTag(triple.getItem3(), 0);
        }
        return typeTags;
    }

    /**
     * Read a type param from the string.
     *
     * @return (TypeTag, End of current depth, Tail of the string) triple.
     */
    private static Triple<TypeTag, Boolean, String> readTypeTag(String ts, int depth) {
        if (ts == null || ts.trim().isEmpty()) {
            return new Triple<>(null, false, null);
        }
        TypeTagWithTypeParams parent = null;
        int idx_lt = 0;
        if (startsWithStructTagWithTypeParams(ts)) {
            idx_lt = ts.indexOf(LT);
            parent = parseStructTag(ts.substring(0, idx_lt));
        } else if (startsWithVectorTag(ts)) {
            idx_lt = ts.indexOf(LT);
            parent = new VectorTag();
        }
        if (parent != null) {
            String tail = ts.substring(idx_lt + 1);
            List<TypeTag> typeTags = new ArrayList<>();
            //System.out.println("parent: " + parent + ", tail: " + tail + ", depth: " + depth + ", about to read type params");
            Triple<TypeTag, Boolean, String> triple = readTypeParamsToEnd(tail, depth + 1, typeTags);
            parent.setTypeParams(typeTags);
            return new Triple<>(parent, false, triple.getItem3());
        }
        Pair<String[], Integer> partsAndSepIdx = splitByCommaOrLtOrGt(ts);
        String[] parts = partsAndSepIdx.getItem1();
        String head = parts[0].trim();
        String separator = parts[2];
        TypeTag typeTag = null;
        if (!head.isEmpty()) {
            if (separator != null && separator.equals(LT)) {
                return readTypeTag(ts, depth);
            }
            typeTag = parseTypeTagWithoutTypeParams(head);
        }
        boolean endOfDepth = false;
        if (separator != null && separator.equals(GT)) {
            if (depth - 1 < 0) {
                throw new IllegalArgumentException("Unmatched \">\". Param: \"" + ts + "\", parsed head: \"" + head
                        + "\", separator: \"" + separator + "\", tail: \"" + parts[1] + "\"");
            }
            endOfDepth = true;
        }
        String tail = null;
        if (parts[1] != null && !parts[1].trim().isEmpty()) {
            tail = parts[1].trim();
            if (endOfDepth && tail.startsWith(COMMA)) { // ...>,
                tail = tail.substring(1).trim();
            }
        }
        return new Triple<>(typeTag, endOfDepth, tail);
    }

    /**
     * @return (Head, tail of the string, the separator) and the index of the separator.,
     */
    private static Pair<String[], Integer> splitByCommaOrLtOrGt(String ts) {
        if (ts == null || ts.trim().isEmpty()) {
            throw new IllegalArgumentException(ts);
        }
        String head;
        String tail = null;
//        int idx_comma = ts.indexOf(COMMA);
//        int idx_gt = ts.indexOf(GT);
//        int idx_comma_or_gt = (idx_comma > 0 && idx_gt > 0) ? Math.min(idx_comma, idx_gt) : Math.max(idx_comma, idx_gt);
//        if (idx_comma_or_gt > 0) {
//            head = ts.substring(0, idx_comma_or_gt);
//            tail = ts.substring(idx_comma_or_gt + 1);
//        } else {
//            head = ts;
//        }
//        String separator = idx_comma_or_gt > 0 ? ts.substring(idx_comma_or_gt, idx_comma_or_gt + 1) : null;
        String[] parts = ts.split("[" + LT + GT + COMMA + "]", 2);
        head = parts[0];
        if (parts.length > 1) {
            tail = parts[1];
        }
        int separatorIdx = ts.length() > head.length() ? head.length() : -1;
        String separator = separatorIdx >= 0 ? ts.substring(separatorIdx, separatorIdx + 1) : null;
        if (separator != null && separator.equals(LT)
                && !head.contains(COLON_COLON) && !startsWithVectorTag(head + LT)) {
            throw new IllegalArgumentException(ts);
        }
        return new Pair<>(new String[]{head, tail, separator}, separatorIdx);
    }

    /**
     * Read to end of the current depth.
     */
    private static Triple<TypeTag, Boolean, String> readTypeParamsToEnd(String ts, int depth, List<TypeTag> typeTags) {
        Triple<TypeTag, Boolean, String> triple;
        String tail = ts;
        boolean endOfDepth = false;
        while (true) {
            triple = readTypeTag(tail, depth);
            if (triple.getItem2()) {
                endOfDepth = true;
            }
            if (triple.getItem1() != null) {
                typeTags.add(triple.getItem1());
                tail = triple.getItem3();
            }
            if (triple.getItem1() == null || endOfDepth) {
                break;
            }
        }
        if (!endOfDepth) {
            throw new IllegalArgumentException(ts);
        }
        return triple;
    }

    private static TypeTag parseTypeTagWithoutTypeParams(String t) {
        if (t.contains(LT) || t.contains(GT) || t.contains(COMMA)) {
            throw new IllegalArgumentException(t);
        }
        TypeTag typeTag;
        if (t.contains(COLON_COLON)) {
            typeTag = parseStructTag(t);
        } else {
            typeTag = new RawTypeTag(t.trim());
        }
        //System.out.println("parsed type tag without type params: " + typeTag);
        return typeTag;
    }

    public static class TypeTag {
    }

    public static class VectorTag extends TypeTagWithTypeParams {
        @Override
        public String toString() {
            return "VectorTag{" +
                    "typeParams=" + getTypeParams() +
                    '}';
        }
    }

    public static class TypeTagWithTypeParams extends TypeTag {
        private List<TypeTag> typeParams;

        public TypeTagWithTypeParams() {
        }

        public TypeTagWithTypeParams(List<TypeTag> typeParams) {
            this.typeParams = typeParams;
        }

        public List<TypeTag> getTypeParams() {
            return typeParams;
        }

        public void setTypeParams(List<TypeTag> typeParams) {
            this.typeParams = typeParams;
        }
    }

    public static class StructTag extends TypeTagWithTypeParams {
        private String address;
        private String module;
        private String name;

        public StructTag() {
        }

        public StructTag(String address, String module, String name) {
            this.address = address;
            this.module = module;
            this.name = name;
        }

        public StructTag(String address, String module, String name, List<TypeTag> typeParams) {
            super(typeParams);
            this.address = address;
            this.module = module;
            this.name = name;
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


        @Override
        public String toString() {
            return "StructTag{" +
                    "address='" + address + '\'' +
                    ", module='" + module + '\'' +
                    ", name='" + name + '\'' +
                    ", typeParams=" + getTypeParams() +
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
