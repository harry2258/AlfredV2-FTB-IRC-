package com.harry2258.Alfred.json;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class Perms {
    @SerializedName(value = "Perms")
    private Permission permission;

    @SerializedName(value = "Permissions")
    private List<String> global;

}
