package com.chapssal.message.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import jakarta.persistence.*;
import java.util.Date;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchQuery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String query;
    private Date searchDate;
}
