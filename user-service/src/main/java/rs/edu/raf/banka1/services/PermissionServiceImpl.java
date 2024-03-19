package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.PermissionDto;
import rs.edu.raf.banka1.mapper.PermissionMapper;
import rs.edu.raf.banka1.repositories.PermissionRepository;

import java.util.List;

@Service
public class PermissionServiceImpl implements PermissionService {
    private PermissionMapper permissionMapper;
    private PermissionRepository permissionRepository;
    @Autowired
    public PermissionServiceImpl(PermissionMapper permissionMapper, PermissionRepository permissionRepository) {
        this.permissionMapper = permissionMapper;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<PermissionDto> getAllPermissions() {
        return permissionRepository.findAll().stream().map(permissionMapper::permissionToPermissionDto).toList();
    }
}
