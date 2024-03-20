package rs.edu.raf.banka1.requests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ModifyPermissionsRequest {
    private List<String> permissions;
    private Boolean add;
}
