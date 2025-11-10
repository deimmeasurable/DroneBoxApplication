package org.example.dronebox.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name; // letters, numbers, hyphen, underscore
    @Min(1)
    private int weight; // grams
    @NotBlank
    private String code; // uppercase letters, underscore and numbers
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "box_txref")
    private Box box;

}
