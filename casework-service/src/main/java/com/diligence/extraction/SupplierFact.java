package com.diligence.extraction;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierFact {
    private String category;      // "employees", "revenue", "locations", "certifications", etc.
    private String value;         // The actual fact (e.g., "1,250 employees")
    private Double confidence;    // 0.0-1.0 confidence in this fact
    private List<Integer> pages;  // Which pages this fact came from
    private String reasoning;     // Why did we extract this fact?
}
