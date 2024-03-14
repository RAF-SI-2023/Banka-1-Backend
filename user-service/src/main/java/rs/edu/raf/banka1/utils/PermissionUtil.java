package rs.edu.raf.banka1.utils;

import rs.edu.raf.banka1.dtos.PermissionDto;
import java.util.List;

public class PermissionUtil {

    //packs permissions into json list
    // [perm1, perm2...]
    public static String packPermissions(List<PermissionDto> permissions) {
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
