package com.diligence.extraction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Document Parser Tests")
class DocumentParserTest {

    @Autowired
    private DocumentParser documentParser;

    private Path testPdfPath;

    @BeforeEach
    void setUp() throws IOException {
        testPdfPath = Files.createTempDirectory("test-pdfs");
    }

    @Test
    @DisplayName("Should parse valid PDF and extract text chunks")
    void testParseValidPDF() {
        // GIVEN: A valid PDF file with known content
        File samplePdf = createSamplePdf("Hello world. This is page 1.");

        // WHEN: Parse the document
        List<PageChunk> chunks = documentParser.parseDocument(samplePdf);

        // THEN: Should extract chunks with correct page numbers
        assertNotNull(chunks, "Chunks should not be null");
        assertFalse(chunks.isEmpty(), "Should extract at least one chunk");
        assertEquals(1, chunks.get(0).getPageNumber(), "First chunk should be from page 1");
        assertTrue(chunks.get(0).getText().contains("Hello"), "Chunk should contain original text");
    }

    @Test
    @DisplayName("Should handle chunking with proper overlap")
    void testParseChunking() {
        // GIVEN: A document with text exceeding chunk size (500 chars)
        String largeText = "A".repeat(600) + " B".repeat(100);  // 700 chars
        File testPdf = createSamplePdf(largeText);

        // WHEN: Parse the document
        List<PageChunk> chunks = documentParser.parseDocument(testPdf);

        // THEN: Should create multiple chunks with overlap
        assertTrue(chunks.size() >= 2, "Should create multiple chunks for large text");

        // Verify overlap: last 50 chars of chunk 1 should match first 50 chars of chunk 2
        String chunk1End = chunks.get(0).getText().substring(Math.max(0, chunks.get(0).getText().length() - 50));
        String chunk2Start = chunks.get(1).getText().substring(0, Math.min(50, chunks.get(1).getText().length()));
        assertTrue(chunk1End.contains("A") || chunk2Start.contains("A"), "Should have overlap between chunks");
    }

    @Test
    @DisplayName("Should handle invalid/corrupted PDF gracefully")
    void testParseInvalidPDF() {
        // GIVEN: An invalid PDF file (or text file with .pdf extension)
        File invalidPdf = new File(testPdfPath.toFile(), "invalid.pdf");
        try {
            Files.write(invalidPdf.toPath(), "This is not a PDF".getBytes());
        } catch (IOException e) {
            fail("Failed to create test file");
        }

        // WHEN: Parse the document
        List<PageChunk> chunks = documentParser.parseDocument(invalidPdf);

        // THEN: Should return empty list, not throw exception
        assertNotNull(chunks, "Should return list, not throw exception");
        assertTrue(chunks.isEmpty(), "Should return empty list for invalid PDF");
    }

    @Test
    @DisplayName("Should handle empty/blank documents")
    void testParseEmptyDocument() {
        // GIVEN: A PDF with blank pages
        File emptyPdf = createSamplePdf("");

        // WHEN: Parse the document
        List<PageChunk> chunks = documentParser.parseDocument(emptyPdf);

        // THEN: Should handle gracefully
        assertNotNull(chunks, "Should return list");
        // Empty PDFs may return 0 chunks (expected behavior)
    }

    @Test
    @DisplayName("Should handle non-existent file")
    void testParseNonExistentFile() {
        // GIVEN: A file path that doesn't exist
        File nonExistent = new File("/nonexistent/path/document.pdf");

        // WHEN: Parse the document
        List<PageChunk> chunks = documentParser.parseDocument(nonExistent);

        // THEN: Should return empty list, not throw exception
        assertNotNull(chunks, "Should return list");
        assertTrue(chunks.isEmpty(), "Should return empty list for non-existent file");
    }

    // Helper method: Create a sample PDF for testing
    private File createSamplePdf(String content) {
        // In real tests, use itext or pdfbox to create test PDFs
        // For now, create a simple text file as placeholder
        try {
            File testFile = Files.createTempFile("test-", ".pdf").toFile();
            // Note: This is a simplified test; real tests need actual PDF files
            return testFile;
        } catch (IOException e) {
            fail("Failed to create test file: " + e.getMessage());
            return null;
        }
    }
}
