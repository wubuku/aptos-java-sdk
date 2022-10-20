package com.github.wubuku.aptos.utils;


import com.github.wubuku.aptos.types.AccountAddress;
import com.github.wubuku.aptos.types.TypeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.wubuku.aptos.utils.HexUtils.hexToAccountAddressBytes;

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
        byte[] addressBytes = hexToAccountAddressBytes(t.getAddress());
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

    public static StructTag parseStructTag(String s) {
        String[] parts = s.split(COLON_COLON, 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException(s);
        }
        String address = parts[0];
        String moduleName = parts[1];
        String structName;
        List<TypeTag> typeParams;
        if (!parts[2].contains(LT)) {
            structName = parts[2];
            typeParams = Collections.emptyList();
        } else {
            int idx_lt = parts[2].indexOf(LT);
            int idx_gt = parts[2].lastIndexOf(GT);
            if (idx_lt < 1 || idx_gt < 0 || idx_lt > idx_gt) {
                throw new IllegalArgumentException(s);
            }
            structName = parts[2].substring(0, idx_lt);
            String ts = parts[2].substring(idx_lt + 1, idx_gt);
            typeParams = new ArrayList<>();
            for (String t : ts.split(COMMA)) {
                if (!t.contains(COLON_COLON)) {
                    throw new UnsupportedOperationException("ONLY support struct as type param");
                }
                typeParams.add(parseStructTag(t.trim()));
            }
        }
        return new StructTag(address, moduleName, structName, typeParams);
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
}
