package com.diligence.extraction;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageChunk {
    private Integer pageNumber;   // 1-indexed page number
    private String text;          // Text content of this chunk
    private Integer startPosition; // Start character position in document
    private Integer endPosition;   // End character position in document
}
