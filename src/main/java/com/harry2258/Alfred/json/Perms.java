package com.harry2258.Alfred.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Perms {
    @SerializedName(value = "Perms")
    private Permission permission;

    @SerializedName(value = "Permissions")
    private List<String> global;

    public Perms() {
    }

    public Permission getPermission() {
        return this.permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public List<String> getGlobal() {
        return this.global;
    }

    public void setGlobal(List<String> global) {
        this.global = global;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Perms)) return false;
        final Perms other = (Perms) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$permission = this.permission;
        final Object other$permission = other.permission;
        if (this$permission == null ? other$permission != null : !this$permission.equals(other$permission))
            return false;
        final Object this$global = this.global;
        final Object other$global = other.global;
        if (this$global == null ? other$global != null : !this$global.equals(other$global)) return false;
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $permission = this.permission;
        result = result * PRIME + ($permission == null ? 0 : $permission.hashCode());
        final Object $global = this.global;
        result = result * PRIME + ($global == null ? 0 : $global.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof Perms;
    }

    public String toString() {
        return "com.harry2258.Alfred.json.Perms(permission=" + this.permission + ", global=" + this.global + ")";
    }
}
