package rs.edu.raf.banka1.utils;

import rs.edu.raf.banka1.model.Permission;

import java.util.List;
import java.util.stream.Collectors;

public class PermissionUtil {
    public static String packPermissions(List<Permission> permissions) {
        StringBuilder packedPermissions = new StringBuilder();
        packedPermissions.append("[");
        permissions.forEach(permission -> packedPermissions.append(permission.getName()).append(", "));
        if (!permissions.isEmpty()) {
            packedPermissions.replace(packedPermissions.lastIndexOf(","), packedPermissions.length(), "");
        }
        packedPermissions.append("]");
        return packedPermissions.toString();
    }
}
