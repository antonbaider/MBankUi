package com.mthree.bankmthree.controller;

import com.mthree.bankmthree.dto.account.AccountDTO;
import com.mthree.bankmthree.dto.account.CloseAccountRequest;
import com.mthree.bankmthree.service.AccountService;
import com.mthree.bankmthree.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Controller class responsible for handling HTTP requests related to user bank accounts.
 * Provides endpoints for creating accounts, retrieving user accounts, and closing accounts.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Account Controller", description = "Endpoints for user bank accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;

    @Autowired
    public AccountController(UserService userService, AccountService accountService) {
        this.userService = userService;
        this.accountService = accountService;
    }

    /**
     * Endpoint to create a new bank account for the authenticated user in a specified currency.
     *
     * @param accountDTO  The data transfer object containing account creation details, such as currency.
     * @param userDetails The details of the currently authenticated user, automatically injected by Spring Security.
     * @return A ResponseEntity containing a detailed success message upon successful account creation.
     */
    @Operation(summary = "Create an account in a specific currency")
    @PostMapping("/accounts")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> createAccount(@Valid @RequestBody AccountDTO accountDTO, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        var user = userService.findByUsername(username);
        accountService.createAccount(user, accountDTO.getCurrency());
        return ResponseEntity.ok(new ApiResponse("Account created successfully!", accountDTO));
    }

    /**
     * Endpoint to retrieve all bank accounts associated with the authenticated user.
     *
     * @param userDetails The details of the currently authenticated user, automatically injected by Spring Security.
     * @return A ResponseEntity containing a set of AccountDTOs representing the user's accounts.
     */
    @Operation(summary = "Get all accounts of the current user")
    @GetMapping("/accounts")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getUserAccounts(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        var user = userService.findByUsername(username);
        Set<AccountDTO> accountDTOs = accountService.getUserAccounts(user);
        return ResponseEntity.ok(new ApiResponse("Retrieved accounts successfully", accountDTOs));
    }

    /**
     * Endpoint to close a bank account for the authenticated user, provided the account balance is zero.
     *
     * @param request     The request payload containing the card number of the account to be closed.
     * @param userDetails The details of the currently authenticated user, automatically injected by Spring Security.
     * @return A ResponseEntity containing a success message upon successful account closure.
     */
    @Operation(summary = "Close an account if the balance is zero")
    @DeleteMapping("/accounts/close")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> closeAccount(@RequestBody CloseAccountRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        accountService.closeAccount(request.getCardNumber(), username);
        return ResponseEntity.ok(new ApiResponse("Account closed successfully!", null));
    }
}