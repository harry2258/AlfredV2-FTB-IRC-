package com.harry2258.Alfred.json;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class Permission {
    @SerializedName(value = "Mods")
    private List<String> mods;
    @SerializedName(value = "Admins")
    private List<String> admins;
    @SerializedName(value = "ModPerms")
    private List<String> modPerms;
    @SerializedName(value = "Everyone")
    private List<String> everyone;
    @SerializedName(value = "Exec")
    private List<String> exec;
}
