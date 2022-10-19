package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MoveStruct {
    @JsonProperty("name")
    private String name;
    @JsonProperty("is_native")
    private Boolean isNative;
    @JsonProperty("abilities")
    private List<String> abilities;
    @JsonProperty("generic_type_params")
    private List<MoveStructGenericTypeParam> genericTypeParams;
    @JsonProperty("fields")
    private List<MoveStructField> fields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getNative() {
        return isNative;
    }

    public void setNative(Boolean aNative) {
        isNative = aNative;
    }

    public List<String> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }

    public List<MoveStructGenericTypeParam> getGenericTypeParams() {
        return genericTypeParams;
    }

    public void setGenericTypeParams(List<MoveStructGenericTypeParam> genericTypeParams) {
        this.genericTypeParams = genericTypeParams;
    }

    public List<MoveStructField> getFields() {
        return fields;
    }

    public void setFields(List<MoveStructField> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "MoveStruct{" +
                "name='" + name + '\'' +
                ", isNative=" + isNative +
                ", abilities=" + abilities +
                ", genericTypeParams=" + genericTypeParams +
                ", fields=" + fields +
                '}';
    }
}
