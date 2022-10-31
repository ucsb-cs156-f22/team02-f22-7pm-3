package edu.ucsb.cs156.example.entities;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ucsbdiningcommonsmenuitems")
public class UCSBDiningCommonsMenuItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String diningCommonsCode;
  private String name;  
  private String station;
}