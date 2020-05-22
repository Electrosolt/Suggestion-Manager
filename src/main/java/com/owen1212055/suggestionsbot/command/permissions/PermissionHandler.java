package com.owen1212055.suggestionsbot.command.permissions;

import net.dv8tion.jda.api.entities.Member;

import java.util.Comparator;
import java.util.HashMap;

public class PermissionHandler {
    private static final HashMap<Long, Permission> permsCache = new HashMap<>();


    public static Permission getPermission(Member member) {
        if (permsCache.containsKey(member.getIdLong())) {
            return permsCache.get(member.getIdLong());
        }

        Permission perm = member.getRoles().stream()
                .map((role) -> Permission.fromRole(role.getIdLong()))
                .max(Comparator.comparingInt(Permission::getPermissionLevel))
                .orElse(Permission.USER);

        permsCache.put(member.getIdLong(), perm);
        return perm;
    }
}
