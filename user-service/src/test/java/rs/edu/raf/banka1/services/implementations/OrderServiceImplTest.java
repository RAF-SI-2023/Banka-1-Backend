package rs.edu.raf.banka1.services.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.edu.raf.banka1.dtos.OrderDto;
import rs.edu.raf.banka1.mapper.EmployeeMapper;
import rs.edu.raf.banka1.mapper.OrderMapper;
import rs.edu.raf.banka1.mapper.PermissionMapper;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.repositories.EmployeeRepository;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.services.CapitalService;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.TransactionService;
import rs.edu.raf.banka1.utils.Constants;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class OrderServiceImplTest {

    private OrderMapper orderMapper;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MarketService marketService;
    @Mock
    private TaskScheduler taskScheduler;
    @Mock
    private TransactionService transactionService;
    @Mock
    private CapitalService capitalService;
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    @Autowired
    private OrderServiceImpl orderService;
    
    @BeforeEach
    void setUp() {
        this.orderMapper = new OrderMapper(new EmployeeMapper(
                new PermissionMapper(),
                mock(PasswordEncoder.class),
                mock(PermissionRepository.class)
        ));
        orderService = new OrderServiceImpl(orderMapper, orderRepository, marketService, taskScheduler, transactionService, capitalService, employeeRepository);
    }

    @Test
    public void testGetAllOrders() {
        Employee e1 = new Employee();
        e1.setUserId(1L);
        e1.setEmail("e1");
        e1.setPassword("e1");
        e1.setFirstName("e1");
        e1.setLastName("e1");
        e1.setPosition(Constants.AGENT);
        e1.setActive(true);
        e1.setOrderlimit(1005.0);
        e1.setLimitNow(0.0);
        e1.setPermissions(new HashSet<>());
        e1.setRequireApproval(true);

        Employee e2 = new Employee();
        e2.setUserId(2L);
        e2.setEmail("e2");
        e2.setPassword("e2");
        e2.setFirstName("e2");
        e2.setLastName("e2");
        e2.setPosition(Constants.SUPERVIZOR);
        e2.setActive(true);
        e2.setOrderlimit(1000000000.0);
        e2.setLimitNow(0.0);
        e2.setPermissions(new HashSet<>());
        e2.setRequireApproval(true);

        MarketOrder m1 = new MarketOrder();
        m1.setOwner(e1);
        m1.setUpdatedAt(Instant.parse("2024-04-01T18:35:24.00Z"));
        m1.setApprovedBy(e2);
        MarketOrder m2 = new MarketOrder();
        m2.setOwner(e1);
        m2.setUpdatedAt(Instant.parse("2024-04-01T18:35:24.00Z"));
        m2.setApprovedBy(e2);
        MarketOrder m3 = new MarketOrder();
        m3.setOwner(e2);
        m3.setUpdatedAt(Instant.parse("2024-04-01T18:35:24.00Z"));
        m3.setApprovedBy(e2);

        List<MarketOrder> mockOrders = Arrays.asList(m1, m2, m3);
        when(orderRepository.findAll()).thenReturn(mockOrders);

        List<OrderDto> result = orderService.getAllOrders();

        assertEquals(2, result.size());
    }
}