package rs.edu.raf.banka1.exceptions;

public class ModifyPermissionException extends BadRequestException {

    public ModifyPermissionException(String permissionName) {
        super("Permission " + permissionName + " doesn't exist");
    }
}
