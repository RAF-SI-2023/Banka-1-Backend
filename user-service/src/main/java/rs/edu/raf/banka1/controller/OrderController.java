package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.configuration.authproviders.CurrentAuth;
import rs.edu.raf.banka1.dtos.OrderDto;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.requests.StatusRequest;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.services.OrderService;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    public OrderController(OrderService orderService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }

    @PutMapping(value = "/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "change loan request status", description = "change loan request status")
    @PreAuthorize("hasAuthority('manageOrderRequests')")
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
    @Operation(summary = "Create order request", description = "Create order request")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = {@Content(mediaType = "application/json")}),
        @ApiResponse(responseCode = "403", description = "You aren't authorized to create order request"),
        @ApiResponse(responseCode = "404", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> createLoanRequest(
        @RequestBody final CreateOrderRequest request,
        @CurrentAuth Employee currentAuth
        ) {
        orderService.createOrder(request, currentAuth);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all orders for current Employee", description = "Get all orders for current Employee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to change status"),
            @ApiResponse(responseCode = "404", description = "Loan not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<OrderDto>> getAllOrdersForLoggedUser(
        @CurrentAuth Employee currentAuth
    ) {
        return new ResponseEntity<>(orderService.getAllOrdersForEmployee(currentAuth), HttpStatus.OK);
    }

    @PutMapping(value = "/supervisor/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get orders for all employees", description = "Supervisor gets orders for all employees")
    @PreAuthorize("hasAuthority('manageOrderRequests')")
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
}
