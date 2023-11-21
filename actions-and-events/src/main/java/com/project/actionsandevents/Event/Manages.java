/**
 * This file contains class that implements Manages class.
 *
 * @author Vadim Goncearenco (xgonce00)
 */

package com.project.actionsandevents.Event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import com.project.actionsandevents.User.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Manages {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User manager;
    
    @ManyToOne
    private Event event;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String status;

    @Column(nullable = true)
    private String text;
}
