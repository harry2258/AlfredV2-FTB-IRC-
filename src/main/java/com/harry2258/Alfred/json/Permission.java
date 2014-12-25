package com.harry2258.Alfred.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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

    public Permission() {
    }

    public List<String> getMods() {
        return this.mods;
    }

    public List<String> getAdmins() {
        return this.admins;
    }

    public List<String> getModPerms() {
        return this.modPerms;
    }

    public List<String> getEveryone() {
        return this.everyone;
    }

    public List<String> getExec() {
        return this.exec;
    }

    public void setMods(List<String> mods) {
        this.mods = mods;
    }

    public void setAdmins(List<String> admins) {
        this.admins = admins;
    }

    public void setModPerms(List<String> modPerms) {
        this.modPerms = modPerms;
    }

    public void setEveryone(List<String> everyone) {
        this.everyone = everyone;
    }

    public void setExec(List<String> exec) {
        this.exec = exec;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Permission)) return false;
        final Permission other = (Permission) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$mods = this.mods;
        final Object other$mods = other.mods;
        if (this$mods == null ? other$mods != null : !this$mods.equals(other$mods)) return false;
        final Object this$admins = this.admins;
        final Object other$admins = other.admins;
        if (this$admins == null ? other$admins != null : !this$admins.equals(other$admins)) return false;
        final Object this$modPerms = this.modPerms;
        final Object other$modPerms = other.modPerms;
        if (this$modPerms == null ? other$modPerms != null : !this$modPerms.equals(other$modPerms)) return false;
        final Object this$everyone = this.everyone;
        final Object other$everyone = other.everyone;
        if (this$everyone == null ? other$everyone != null : !this$everyone.equals(other$everyone)) return false;
        final Object this$exec = this.exec;
        final Object other$exec = other.exec;
        if (this$exec == null ? other$exec != null : !this$exec.equals(other$exec)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $mods = this.mods;
        result = result * PRIME + ($mods == null ? 0 : $mods.hashCode());
        final Object $admins = this.admins;
        result = result * PRIME + ($admins == null ? 0 : $admins.hashCode());
        final Object $modPerms = this.modPerms;
        result = result * PRIME + ($modPerms == null ? 0 : $modPerms.hashCode());
        final Object $everyone = this.everyone;
        result = result * PRIME + ($everyone == null ? 0 : $everyone.hashCode());
        final Object $exec = this.exec;
        result = result * PRIME + ($exec == null ? 0 : $exec.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Permission;
    }

    public String toString() {
        return "com.harry2258.Alfred.json.Permission(mods=" + this.mods + ", admins=" + this.admins + ", modPerms=" + this.modPerms + ", everyone=" + this.everyone + ", exec=" + this.exec + ")";
    }
}
