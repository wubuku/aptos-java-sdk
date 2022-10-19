package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MoveFunction {
    @JsonProperty("name")
    private String name;
    @JsonProperty("visibility")//: "private",
    private String visibility;
    @JsonProperty("is_entry")
    private Boolean isEntry;
    @JsonProperty("generic_type_params")
    private List<MoveFunctionGenericTypeParam> genericTypeParams;
    @JsonProperty("params")
    private List<String> params;
    @JsonProperty("return")
    private List<String> _return;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Boolean getEntry() {
        return isEntry;
    }

    public void setEntry(Boolean entry) {
        isEntry = entry;
    }

    public List<MoveFunctionGenericTypeParam> getGenericTypeParams() {
        return genericTypeParams;
    }

    public void setGenericTypeParams(List<MoveFunctionGenericTypeParam> genericTypeParams) {
        this.genericTypeParams = genericTypeParams;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public List<String> getReturn() {
        return _return;
    }

    public void setReturn(List<String> _return) {
        this._return = _return;
    }

    @Override
    public String toString() {
        return "MoveFunction{" +
                "name='" + name + '\'' +
                ", visibility='" + visibility + '\'' +
                ", isEntry=" + isEntry +
                ", genericTypeParams=" + genericTypeParams +
                ", params=" + params +
                ", _return=" + _return +
                '}';
    }
}
