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
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.requests.StatusRequest;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.responses.ChangeOrderResponse;
import rs.edu.raf.banka1.services.OrderService;
import rs.edu.raf.banka1.utils.JwtUtil;

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
    @Operation(summary = "change order request status", description = "change order request status")
    @PreAuthorize("hasAuthority('manageOrderRequests')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ChangeOrderResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid status provided"),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to change status"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ChangeOrderResponse> changeOrderRequestStatus(
            @PathVariable(name = "orderId") final Long id,
            @RequestBody final StatusRequest request
    ) {
        // Parsing request into OrderStatus
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(request.getStatus());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ChangeOrderResponse(false, "Invalid status provided"));
        }

        boolean ok = false;

        // Settlement date expired check
        if (orderService.settlementDateExpired(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ChangeOrderResponse(false, "Settlement date expired."));
        }

        // If status is APPROVED, execute approval logic
        if (status.equals(OrderStatus.APPROVED)) {
            ok = orderService.approveOrder(id);
        }

        // For all other statuses (CANCEL, DENY, ...)
        if (!status.equals(OrderStatus.APPROVED)) {
            ok = orderService.changeStatus(id, status);
        }

        if (!ok) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ChangeOrderResponse(false, "Failed to change status"));
        }

        return ResponseEntity.ok().body(new ChangeOrderResponse(true, "Status changed successfully"));
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
    public ResponseEntity<Void> createOrderRequest(
        @RequestBody final CreateOrderRequest request
    ) {
        orderService.createOrder(request);
        return ResponseEntity.ok().build();
    }


}
