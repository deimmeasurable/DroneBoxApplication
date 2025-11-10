package org.example.dronebox.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dronebox.domain.BoxState;
import org.example.dronebox.domain.Item;

import java.util.ArrayList;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoxDto {
    private String txref; // max 20 chars
    private int weightLimit; // grams, max 500
    private int battery; // 0 - 100
    private BoxState state;
    private ArrayList<Item> items = new ArrayList<>();
}
