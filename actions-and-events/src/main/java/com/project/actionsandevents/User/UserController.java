/**
 * This file contains class that implements user controller.
 *
 * @author Oleksandr Turytsia (xturyt00)
 */
package com.project.actionsandevents.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.project.actionsandevents.Event.responses.EventsResponse;
import com.project.actionsandevents.Event.responses.TicketsResponse;
import com.project.actionsandevents.User.exceptions.UserNotFoundException;
import com.project.actionsandevents.User.exceptions.DuplicateUserException;
import com.project.actionsandevents.User.requests.UserPatchRequest;
import com.project.actionsandevents.User.responses.RegistersResponse;
import com.project.actionsandevents.User.responses.UserResponse;
import com.project.actionsandevents.User.responses.UsersResponse;
import com.project.actionsandevents.common.ResponseMessage;
import com.project.actionsandevents.common.ResponseMessage.Status;

import jakarta.validation.Valid;

@RestController
@RequestMapping
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/user")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<Object> getUser(Authentication authentication) throws UserNotFoundException 
    {
        User user = null;

        if (authentication != null) {
            // Get the authenticated user's details from the Authentication object
            UserInfoDetails userInfoDetails = (UserInfoDetails) authentication.getPrincipal();
            user = userService.getUserById(userInfoDetails.getId());
        }

        if (user == null) {
            return ResponseEntity.status(403).body(new ResponseMessage("Unauthorized", Status.ERROR));
        }

        return ResponseEntity.ok(new UserResponse(user));
    }

    @GetMapping("/user/{id}")
    // @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<Object> getUserById(@PathVariable Long id, Authentication authentication) throws UserNotFoundException {
        User user = userService.getUserById(id);

        return ResponseEntity.ok(new UserResponse(user));
    }

    @PatchMapping("/user/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<Object> patchUserById(
            @PathVariable Long id,
            @Valid @RequestBody UserPatchRequest patchRequest,
            BindingResult bindingResult,
            Authentication authentication) 
    {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(new ResponseMessage(
                    "Validation failed: " + bindingResult.getAllErrors(), ResponseMessage.Status.ERROR));
        }

        try {
            userService.patchUserById(id, patchRequest);
        } catch (UserNotFoundException | DuplicateUserException ex) {
            return ResponseEntity.badRequest().body(new ResponseMessage(
                    ex.getMessage(), ResponseMessage.Status.ERROR));
        }

        return ResponseEntity.ok(new ResponseMessage("User was successfully updated", ResponseMessage.Status.SUCCESS));
    }
    
    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> getUserIds(Authentication authentication) {
        return ResponseEntity.ok(new UsersResponse(userService.getUserIds()));
    }

    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id, Authentication authentication) throws UserNotFoundException 
    {
        userService.deleteUserById(id);

        // TODO add log to db

        return ResponseEntity.ok(new ResponseMessage("User was successfully removed", ResponseMessage.Status.SUCCESS));
    }


    @GetMapping("/user/{id}/events")
    public ResponseEntity<Object> getUserEvents(@PathVariable Long id, Authentication authentication)
            throws UserNotFoundException {
        return ResponseEntity.ok(new EventsResponse(userService.getUserEvents(id)));
    }
    
    @GetMapping("/user/{id}/tickets")
    public ResponseEntity<Object> getUserTickets(@PathVariable Long id, Authentication authentication)
            throws UserNotFoundException {
        return ResponseEntity.ok(new TicketsResponse(userService.getUserTickets(id)));
    }

    @GetMapping("/user/{id}/ticket/{ticketId}")
    public ResponseEntity<Object> getUserTicket(
            @PathVariable Long id,
            @PathVariable Long ticketId,
        Authentication authentication)
            throws UserNotFoundException {
        return ResponseEntity.ok(new RegistersResponse(userService.getUserTicketRegistration(id, ticketId)));
    }

}
