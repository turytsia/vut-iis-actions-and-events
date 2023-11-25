/**
 * This file contains class that implements user services.
 *
 * @author Vadim Goncearenco (xgonce00)
 */
package com.project.actionsandevents.Event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.project.actionsandevents.Event.exceptions.EventLogNotFoundException;
import com.project.actionsandevents.Event.exceptions.EventNotFoundException;
import com.project.actionsandevents.Event.exceptions.DuplicateEventException;
import com.project.actionsandevents.Event.exceptions.DuplicateRegistrationException;
import com.project.actionsandevents.Event.exceptions.RegistrationNotFoundException;
import com.project.actionsandevents.Event.exceptions.TicketNotFoundException;

import com.project.actionsandevents.Event.requests.EventPatchRequest;
import com.project.actionsandevents.Event.responses.EventUserRegisters;


import com.project.actionsandevents.TicketType.TicketType;
import com.project.actionsandevents.TicketType.TicketTypeRepository;

import com.project.actionsandevents.User.User;
import com.project.actionsandevents.User.UserRepository;
import com.project.actionsandevents.User.exceptions.UserNotFoundException;

import java.util.List;
import java.util.Optional;

import java.util.ArrayList;

@Service
public class EventService {
    
    @Autowired
    private EventRepository repository;

    @Autowired
    private TicketTypeRepository ticketTypeRepository;

    @Autowired
    private RegistersRepository registersRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventLogRepository eventLogRepository;

    @Autowired
    private CommentRepository commentRepository;



    /**
     * TODO
     * @param id
     * @return
     * @throws EventNotFoundException
     */
    public Event getEventById(Long id) throws EventNotFoundException {
        Optional<Event> event = repository.findById(id);

        if (!event.isPresent()) {
            throw new EventNotFoundException("Event not found with id: " + id);
        }

        return event.get();
    }

    /**
     * TODO
     * @return
     */
    public List<Long> getEventIds() {
        return repository.findAllIds();
    }

    /**
     * TODO
     * @param id
     * @param patchRequest
     * @throws EventNotFoundException
     */
    public void patchEventById(Long id, EventPatchRequest patchRequest) 
        throws EventNotFoundException, DuplicateEventException {
        Optional<Event> event = repository.findById(id);

        if (!event.isPresent()) {
            throw new EventNotFoundException("Event not found with ID: " + id);
        }

        Event eventToPatch = event.get();

        if (patchRequest.getTitle() != null) {
            eventToPatch.setTitle(patchRequest.getTitle());
        }

        if (patchRequest.getDescription() != null) {
            eventToPatch.setDescription(patchRequest.getDescription());
        }

        if (patchRequest.getDateFrom() != null) {
            eventToPatch.setDateFrom(patchRequest.getDateFrom());
        }

        if (patchRequest.getDateTo() != null) {
            eventToPatch.setDateTo(patchRequest.getDateTo());
        }

        if (patchRequest.getIcon() != null) {
            eventToPatch.setIcon(patchRequest.getIcon());
        }

        if (patchRequest.getImage() != null) {
            eventToPatch.setImage(patchRequest.getImage());
        }

        try {
            repository.save(eventToPatch);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEventException("Event with such parameters already exists");
        }
    }

    /**
     * TODO
     * @param event
     * @return
     */
    public Long addEvent(Event event) throws DuplicateEventException {
        try {
            return repository.save(event).getId();
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEventException("Event with such parameters already exists");
        }
    }

    /**
     * TODO
     * @param id
     * @throws EventNotFoundException
     */
    public void deleteEventById(Long id) throws EventNotFoundException {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new EventNotFoundException("Event with ID " + id + " not found");
        }
    }

    public String approveEvent(Long eventId) throws EventNotFoundException {
        Optional<Event> event = repository.findById(eventId);

        if (!event.isPresent()) {
            throw new EventNotFoundException("Event not found with id: " + eventId);
        }

        event.get().setStatus(EventStatus.APPROVED);

        return "Event was successfully approved";
    }





    





    // get all ticket types
    public List<Long> getTicketTypeIds(Long id) throws EventNotFoundException {
        Optional<Event> event = repository.findById(id);

        if (!event.isPresent()) {
            throw new EventNotFoundException("Event not found with id: " + id);
        }

        return ticketTypeRepository.findAllIdsByEvent(event.get());
    }

    // TODO: Also need event id? Why?
    public TicketType getTicketTypeById(Long id) throws TicketNotFoundException {
        Optional<TicketType> ticketType = ticketTypeRepository.findById(id);

        if (!ticketType.isPresent()) {
            throw new TicketNotFoundException("Ticket type not found with id: " + id);
        }
        
        return ticketType.get();
    }

    public String addTicketType(Long eventId, TicketType ticketType) 
        throws EventNotFoundException, DuplicateEventException 
    {
        Optional<Event> event = repository.findById(eventId);

        if (!event.isPresent()) {
            throw new EventNotFoundException("Event not found with id: " + eventId);
        }

        ticketType.setEvent(event.get());
        try {
            ticketTypeRepository.save(ticketType);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEventException("Ticket type with such parameters already exists");
        }

        return "Ticket type was successfully added";
    }

    public String patchTicketTypeById(Long id, TicketType ticketType) 
        throws TicketNotFoundException, DuplicateEventException  
    {
        Optional<TicketType> ticketTypeToPatch = ticketTypeRepository.findById(id);

        if (!ticketTypeToPatch.isPresent()) {
            throw new TicketNotFoundException("Ticket type not found with id: " + id);
        }

        if (ticketType.getName() != null) {
            ticketTypeToPatch.get().setName(ticketType.getName());
        }

        if (ticketType.getPrice() != null) {
            ticketTypeToPatch.get().setPrice(ticketType.getPrice());
        }

        if (ticketType.getCapacity() != null) {
            ticketTypeToPatch.get().setCapacity(ticketType.getCapacity());
        }

        if (ticketType.getDescription() != null) {
            ticketTypeToPatch.get().setDescription(ticketType.getDescription());
        }

        try {
            ticketTypeRepository.save(ticketTypeToPatch.get());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEventException("Ticket type with such parameters already exists");
        }

        return "Ticket type was successfully updated";
    }

    public void deleteTicketTypeById(Long id) throws TicketNotFoundException {
        if (ticketTypeRepository.existsById(id)) {
            ticketTypeRepository.deleteById(id);
        } else {
            throw new TicketNotFoundException("Ticket type with ID " + id + " not found");
        }
    }






    public String registerUserForTicketType(Long ticketId, Long userId) 
        throws TicketNotFoundException, UserNotFoundException 
    {
        Optional<TicketType> ticketType = ticketTypeRepository.findById(ticketId);
        Optional<User> user = userRepository.findById(userId);

        if (!ticketType.isPresent()) {
            throw new TicketNotFoundException("Ticket type not found with id: " + ticketId);
        }

        if (!user.isPresent()) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        Optional<Registers> reg = registersRepository.findByUserAndTicketType(user.get(), ticketType.get());

        if (reg.isPresent()) {
            return "User already registered for this ticket";
        }

        Registers newRegister = new Registers();
        newRegister.setUser(user.get());
        newRegister.setTicketType(ticketType.get());
        // Registration will be completed after it is accepted by event creator
        newRegister.setStatus(RegistersStatus.PENDING);
        newRegister.setDate(new java.util.Date());

        try {
            registersRepository.save(newRegister);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateRegistrationException("Registration with such parameters already exists");
        }   

        return "User was successfully registered";
    }

    public List<Long> getTicketRegistrations(Long ticketId) throws TicketNotFoundException {
        Optional<TicketType> ticketType = ticketTypeRepository.findById(ticketId);

        if (!ticketType.isPresent()) {
            throw new TicketNotFoundException("Ticket type not found with id: " + ticketId);
        }

        List<Long> ids = registersRepository.findAllIdsByTicketType(ticketType.get());

        return ids;
    }

    public Registers getTicketRegistrationById(Long id) throws RegistrationNotFoundException {
        Optional<Registers> registers = registersRepository.findById(id);

        if (!registers.isPresent()) {
            throw new RegistrationNotFoundException("User not registered for this ticket with id: " + id);
        }

        return registers.get();
    }


    public String patchTicketRegistrationById(Long id, RegistersStatus status) 
        throws RegistrationNotFoundException, DuplicateRegistrationException
    {
        Optional<Registers> registers = registersRepository.findById(id);

        if (!registers.isPresent()) {
            throw new RegistrationNotFoundException("Registration not found with id: " + id);
        }

        registers.get().setStatus(status);

        try {
            registersRepository.save(registers.get());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateRegistrationException("Registration with such parameters already exists");
        }

        return "Ticket registration was successfully updated";
    }






    // get all comments
    public List<Long> getCommentsIds(Long id) throws EventNotFoundException {
        Optional<Event> event = repository.findById(id);

        if (!event.isPresent()) {
            throw new EventNotFoundException("Event not found with id: " + id);
        }

        return commentRepository.findAllIdsByEvent(event.get());
    }

    // get comment by id
    public Comment getCommentById(Long id) throws EventNotFoundException {
        Optional<Comment> comment = commentRepository.findById(id);

        if (!comment.isPresent()) {
            throw new EventNotFoundException("Comment not found with id: " + id);
        }
        
        return comment.get();
    }

    // add comment
    public String addComment(Long id, Comment comment) throws EventNotFoundException {
        Optional<Event> event = repository.findById(id);

        if (!event.isPresent()) {
            throw new EventNotFoundException("Event not found with id: " + id);
        }

        comment.setEvent(event.get());
        commentRepository.save(comment);

        return "Comment was successfully added";
    }

    public String patchCommentById(Long id, Comment comment) throws EventNotFoundException {
        Optional<Comment> commentToPatch = commentRepository.findById(id);

        if (!commentToPatch.isPresent()) {
            throw new EventNotFoundException("Comment not found with id: " + id);
        }

        commentToPatch.get().setRating(comment.getRating());

        if (comment.getText() != null) {
            commentToPatch.get().setText(comment.getText());
        }

        commentRepository.save(commentToPatch.get());

        return "Comment was successfully updated";
    }

    public void deleteCommentById(Long id) throws EventNotFoundException {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
        } else {
            throw new EventNotFoundException("Comment with ID " + id + " not found");
        }
    }


    

   

    public List<Long> getEventLogs(Long eventId) throws EventNotFoundException {
        Optional<Event> event = repository.findById(eventId);

        if (!event.isPresent()) {
            throw new EventNotFoundException("Event not found with id: " + eventId);
        }

        return eventLogRepository.findAllIdsByEvent(event.get());
    }

    public EventLog getEventLogById(Long id) 
            throws EventLogNotFoundException {
        Optional<EventLog> eventLog = eventLogRepository.findById(id);

        if (!eventLog.isPresent()) {
            throw new EventLogNotFoundException("Event log not found with id: " + id);
        }

        return eventLog.get();
    }

    public String deleteEventLogById(Long id) 
            throws EventLogNotFoundException {
        if (eventLogRepository.existsById(id)) {
            eventLogRepository.deleteById(id);
        } else {
            throw new EventLogNotFoundException("Event log with ID " + id + " not found");
        }

        return "Event log was successfully deleted";
    }

}
