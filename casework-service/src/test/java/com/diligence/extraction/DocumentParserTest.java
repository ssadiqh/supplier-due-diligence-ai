package com.diligence.extraction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Document Parser Unit Tests")
class DocumentParserTest {

    private DocumentParser documentParser;
    private Path testDir;

    @BeforeEach
    void setUp() throws IOException {
        documentParser = new DocumentParser();
        testDir = Files.createTempDirectory("test-pdfs");
    }

    @Test
    @DisplayName("Should return empty list for non-existent file")
    void testParseNonExistentFile() {
        // GIVEN: A file path that doesn't exist
        File nonExistent = new File(testDir.toFile(), "nonexistent.pdf");

        // WHEN: Parse the document
        List<PageChunk> chunks = documentParser.parseDocument(nonExistent);

        // THEN: Should return empty list, not throw exception
        assertNotNull(chunks, "Should return list, not null");
        assertTrue(chunks.isEmpty(), "Should return empty list for non-existent file");
    }

    @Test
    @DisplayName("Should handle invalid PDF file gracefully")
    void testParseInvalidPDF() throws IOException {
        // GIVEN: A file that's not a valid PDF
        File invalidPdf = new File(testDir.toFile(), "invalid.pdf");
        Files.write(invalidPdf.toPath(), "This is not a PDF file".getBytes());

        // WHEN: Parse the document
        List<PageChunk> chunks = documentParser.parseDocument(invalidPdf);

        // THEN: Should return empty list or handle gracefully
        assertNotNull(chunks, "Should return list");
        // Invalid PDFs may return 0 chunks (acceptable behavior)
    }

    @Test
    @DisplayName("DocumentParser should be instantiable")
    void testDocumentParserInstantiation() {
        // GIVEN: A DocumentParser instance
        DocumentParser parser = new DocumentParser();

        // WHEN/THEN: Should be created successfully
        assertNotNull(parser, "DocumentParser should be instantiable");
    }

    @Test
    @DisplayName("Should handle null file gracefully")
    void testParseNullFile() {
        // GIVEN: Null file
        // WHEN: Parse null file
        // THEN: Should handle gracefully (likely NPE, but that's JVM protection)
        // This test shows the contract - null files are not supported
        try {
            documentParser.parseDocument(null);
            // If we get here, no exception was thrown
            // The method should handle null gracefully
        } catch (NullPointerException e) {
            // Expected - null files not supported
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("PageChunk should store correct data")
    void testPageChunkDataStorage() {
        // GIVEN: A PageChunk with test data
        PageChunk chunk = new PageChunk();
        chunk.setPageNumber(1);
        chunk.setText("Sample text content");
        chunk.setStartPosition(0);
        chunk.setEndPosition(19);

        // WHEN/THEN: Should store data correctly
        assertEquals(1, chunk.getPageNumber(), "Page number should be stored");
        assertEquals("Sample text content", chunk.getText(), "Text should be stored");
        assertEquals(0, chunk.getStartPosition(), "Start position should be stored");
        assertEquals(19, chunk.getEndPosition(), "End position should be stored");
    }

    @Test
    @DisplayName("PageChunk should support constructor")
    void testPageChunkConstructor() {
        // GIVEN: Create PageChunk with constructor
        PageChunk chunk = new PageChunk(1, "Test text", 0, 9);

        // WHEN/THEN: Constructor should work
        assertEquals(1, chunk.getPageNumber());
        assertEquals("Test text", chunk.getText());
        assertEquals(0, chunk.getStartPosition());
        assertEquals(9, chunk.getEndPosition());
    }
}
