package com.github.wubuku.aptos.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MoveModule {
    @JsonProperty("address")//: "0x88fbd33f54e1126269769780feb24480428179f552e2313fbe571b72e62a1ca1 ",
    private String address;
    @JsonProperty("name")
    private String name;
    @JsonProperty("friends")
    private List<String> friends;
    @JsonProperty("exposed_functions")
    private List<MoveFunction> exposedFunctions;
    @JsonProperty("structs")
    private List<MoveStruct> structs;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<MoveFunction> getExposedFunctions() {
        return exposedFunctions;
    }

    public void setExposedFunctions(List<MoveFunction> exposedFunctions) {
        this.exposedFunctions = exposedFunctions;
    }

    public List<MoveStruct> getStructs() {
        return structs;
    }

    public void setStructs(List<MoveStruct> structs) {
        this.structs = structs;
    }

    @Override
    public String toString() {
        return "MoveModule{" +
                "address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", friends=" + friends +
                ", exposedFunctions=" + exposedFunctions +
                ", structs=" + structs +
                '}';
    }
}
