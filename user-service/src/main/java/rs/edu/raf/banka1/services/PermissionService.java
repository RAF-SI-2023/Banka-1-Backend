package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.PermissionDto;

import java.util.List;

public interface PermissionService {
    List<PermissionDto> getAllPermissions();
}
