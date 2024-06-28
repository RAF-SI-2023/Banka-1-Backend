package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.hibernate.query.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.configuration.authproviders.CurrentAuth;
import rs.edu.raf.banka1.dtos.LegalOrderRequest;
import rs.edu.raf.banka1.dtos.OrderDto;
import rs.edu.raf.banka1.mapper.OrderMapper;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.DecideOrderResponse;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.requests.StatusRequest;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.services.CustomerService;
import rs.edu.raf.banka1.services.EmployeeService;
import rs.edu.raf.banka1.services.OrderService;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/orders")
@SecurityRequirement(name = "Authorization")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;
    private final EmployeeService employeeService;
    private final CustomerService customerService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService,
                           JwtUtil jwtUtil,
                           EmployeeService employeeService,
                           CustomerService customerService,
                           OrderMapper orderMapper) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.orderMapper = orderMapper;
    }

    @PutMapping(value = "/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "change loan request status", description = "change loan request status",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = Boolean.class))}),
        @ApiResponse(responseCode = "403", description = "You aren't authorized to change status"),
        @ApiResponse(responseCode = "404", description = "Loan not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> changeRequestStatus(
        @PathVariable(name = "orderId") final Long id,
        @RequestBody final StatusRequest request
    ) {
//        orderService.changeStatus(
//            id,
//            OrderStatus.valueOf(request.getStatus())
//        );
        return ResponseEntity.ok().build();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create order request", description = "Create order request",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = {@Content(mediaType = "application/json")}),
        @ApiResponse(responseCode = "403", description = "You aren't authorized to create order request"),
        @ApiResponse(responseCode = "404", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> createOrder(
            @RequestBody final CreateOrderRequest request,
            @AuthenticationPrincipal User userPrincipal
            ) {
        Employee currentAuth = employeeService.getEmployeeEntityByEmail(userPrincipal.getUsername());
        orderService.createOrder(request, currentAuth);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all orders for current Employee", description = "Get all orders for current Employee",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to change status"),
            @ApiResponse(responseCode = "404", description = "Loan not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<OrderDto>> getAllOrdersForLoggedUser(
        @AuthenticationPrincipal User currentAuth
    ) {
        Employee employee = null;
        Customer customer = null;
        List<OrderDto> orders = null;
        try {
            employee = employeeService.getEmployeeEntityByEmail(currentAuth.getUsername());
            orders = orderService.getAllOrdersForEmployee(employee);
        } catch (Exception e) {
            customer = customerService.findCustomerByEmail(currentAuth.getUsername());
            orders = orderService.getAllOrdersForCustomer(customer);
        }
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping(value = "/supervisor/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get orders for all employees", description = "Supervisor gets orders for all employees",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to change status"),
            @ApiResponse(responseCode = "404", description = "Loan not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }


    @PutMapping(value = "/decideOrder/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Supervisor approves/denies order.", description = "Supervisor approves/denies order.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DecideOrderResponse.class))}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to change status"),
            @ApiResponse(responseCode = "404", description = "Loan not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DecideOrderResponse> decideOrder(
        @PathVariable("orderId") Long orderId,
        @RequestBody StatusRequest request,
        @AuthenticationPrincipal User userPrincipal
    )
    {
        Employee currentAuth = employeeService.getEmployeeEntityByEmail(userPrincipal.getUsername());
        return new ResponseEntity<>(orderService.decideOrder(orderId, request.getStatus(), currentAuth), HttpStatus.OK);
    }

    @PutMapping(value = "/legal")
    @Operation(summary = "Legal person creates a new order.", description = "Legal person creates a new order.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to create order request"),
            @ApiResponse(responseCode = "404", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> createLegalPersonOrder(
            @RequestBody final CreateOrderRequest request,
            @AuthenticationPrincipal User userPrincipal
    ){
        Customer currentAuth = customerService.findCustomerByEmail(userPrincipal.getUsername());
//        CreateOrderRequest createOrderRequest = orderMapper.legalMarketOrderToCreateOrderRequest(request);
        orderService.createOrder(request, currentAuth);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping(value = "legal")
    @Operation(summary = "Legal person lists his orders.", description = "Legal person lists his order.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to create order request"),
            @ApiResponse(responseCode = "404", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<OrderDto>> getAllOrdersForLegalUser(
            @CurrentAuth Customer currentAuth
    ) {
        return new ResponseEntity<>(orderService.getAllOrdersForLegalPerson(currentAuth), HttpStatus.OK);
    }

}
