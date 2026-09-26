package com.diligence.extraction;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierFactsOutput {
    private List<SupplierFact> facts;
    private String summary;
    private Boolean complete;  // Did extraction find all facts? (false if doc seems truncated)
}
