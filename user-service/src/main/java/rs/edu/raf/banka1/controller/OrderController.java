package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.requests.StatusRequest;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.responses.CancelOrderResponse;
import rs.edu.raf.banka1.responses.DecideOrderResponse;
import rs.edu.raf.banka1.services.OrderService;
import rs.edu.raf.banka1.utils.JwtUtil;

@RestController
@CrossOrigin
@RequestMapping("/orders")
@SecurityRequirement(name = "basicScheme")
public class OrderController {

    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    public OrderController(OrderService orderService, JwtUtil jwtUtil) {
        this.orderService = orderService;
        this.jwtUtil = jwtUtil;
    }

    @PutMapping(value = "/decideOrder/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Approve/deny order request status", description = "Supervisor approve/deny order request status")
    @PreAuthorize("hasAuthority('manageOrderRequests')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DecideOrderResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid status provided"),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to change order status"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DecideOrderResponse> decideOrderRequestStatus(
            @PathVariable(name = "orderId") final Long id,
            @RequestBody final StatusRequest request
    ) {
        // Parsing request into OrderStatus
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(request.getStatus());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new DecideOrderResponse(false, "Invalid status provided."));
        }

        boolean ok;

        // If status is APPROVED or DENIED, execute approval logic
        if (status.equals(OrderStatus.APPROVED) || status.equals(OrderStatus.DENIED)) {
            ok = orderService.decideOrder(id, status);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new DecideOrderResponse(false, "Invalid status provided."));
        }

        if (!ok) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new DecideOrderResponse(false, "Failed to decide order status."));
        }

        return ResponseEntity.ok().body(new DecideOrderResponse(true, "Order status decided successfully."));
    }

    @PutMapping(value = "/cancelOrder/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "User cancels his own order", description = "User cancels his own order")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CancelOrderResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid status provided"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CancelOrderResponse> cancelOrderRequestStatus(
            @PathVariable(name = "orderId") final Long id
    ) {
        if (!orderService.checkOrderOwner(id)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CancelOrderResponse(false, "Logged user is not owner of this order."));
        }

        boolean ok = orderService.changeStatus(id, OrderStatus.CANCELLED);

        if (!ok) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new CancelOrderResponse(false, "Failed to cancel order."));
        }

        return ResponseEntity.ok().body(new CancelOrderResponse(true, "Order cancelled successfully."));
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create order request", description = "Create order request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
        @ApiResponse(responseCode = "403", description = "You aren't authorized to create order request"),
        @ApiResponse(responseCode = "404", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> createOrderRequest(
        @RequestBody final CreateOrderRequest request
    ) {
        boolean ok = orderService.createOrder(request);
        return new ResponseEntity<>(ok, ok ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/limit", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create limit order request", description = "Create limit order request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to create limit order request"),
            @ApiResponse(responseCode = "404", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> createLimitOrderRequest(
            @RequestBody CreateOrderRequest request
    ) {
        orderService.createLimitOrder(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/stop", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create stop order request", description = "Create stop order request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to create limit order request"),
            @ApiResponse(responseCode = "404", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> createStopOrderRequest(
            @RequestBody CreateOrderRequest request
    ) {
        orderService.createStopOrder(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/stoplimit", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create stop-limit order request", description = "Create stop-limit order request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to create limit order request"),
            @ApiResponse(responseCode = "404", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> createStopLimitOrderRequest(
            @RequestBody CreateOrderRequest request
    ) {
        orderService.createStopLimitOrder(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/resetLimit/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Reset current limit for user", description = "Supervisor resets current limit for user")
    @PreAuthorize("hasAuthority('manageOrderRequests')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DecideOrderResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid status provided"),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to reset limits for users"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> resetCurrentLimitForUser(
            @PathVariable(name = "userId") Long userId
    ) {
        orderService.resetLimitForUser(userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/setLimit/{userId}/{orderLimit}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Set order limit for user", description = "Supervisor sets order limit for user")
    @PreAuthorize("hasAuthority('manageOrderRequests')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DecideOrderResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid status provided"),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to set limits for users"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> setOrderLimitForUser(
        @PathVariable(name = "userId") Long userId,
        @PathVariable(name = "orderLimit") Double orderLimit
    ) {
        orderService.setLimitOrderForUser(userId, orderLimit);
        return ResponseEntity.ok().build();
    }
}
