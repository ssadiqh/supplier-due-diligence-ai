package com.diligence.extraction;

import java.io.File;
import java.util.List;

public class DocumentParserMain {

    public static void main(String[] args) {
        System.out.println("=== Document Parser - Standalone Mode ===\n");

        DocumentParser parser = new DocumentParser();

        if (args.length == 0) {
            System.out.println("Usage: java DocumentParserMain <path-to-pdf>\n");
            System.out.println("Example: java DocumentParserMain sample-supplier-document.pdf\n");
            demoMode(parser);
        } else {
            testMode(parser, args[0]);
        }
    }

    private static void demoMode(DocumentParser parser) {
        System.out.println("--- Demo Mode ---\n");

        // Test with fixture if available
        File fixture = new File("src/test/resources/fixtures/sample-supplier-document.pdf");
        if (fixture.exists()) {
            System.out.println("Testing with fixture: " + fixture.getPath() + "\n");
            testMode(parser, fixture.getPath());
        } else {
            System.out.println("⚠️  No fixture found at: " + fixture.getPath());
            System.out.println("Provide a PDF path as argument to test.\n");
        }
    }

    private static void testMode(DocumentParser parser, String filePath) {
        File file = new File(filePath);

        if (!file.exists()) {
            System.err.println("❌ File not found: " + filePath);
            return;
        }

        System.out.println("Parsing: " + file.getName() + " (" + file.length() + " bytes)\n");

        List<PageChunk> chunks = parser.parseDocument(file);

        if (chunks.isEmpty()) {
            System.out.println("⚠️  No text extracted from PDF\n");
            return;
        }

        System.out.println("✅ Extracted " + chunks.size() + " chunks\n");
        System.out.println("--- Chunk Summary ---");

        for (int i = 0; i < chunks.size(); i++) {
            PageChunk chunk = chunks.get(i);
            System.out.printf("Chunk %d: Page %d, %d chars (pos %d-%d)%n",
                i + 1,
                chunk.getPageNumber(),
                chunk.getText().length(),
                chunk.getStartPosition(),
                chunk.getEndPosition()
            );
        }

        System.out.println("\n--- First Chunk (Preview) ---");
        PageChunk first = chunks.get(0);
        String preview = first.getText().substring(0, Math.min(200, first.getText().length()));
        System.out.println(preview + (first.getText().length() > 200 ? "..." : ""));

        System.out.println("\n--- Token Estimation ---");
        int totalChars = chunks.stream().mapToInt(c -> c.getText().length()).sum();
        int estimatedTokens = (int) (totalChars / 4.0);
        System.out.printf("Total characters: %d%n", totalChars);
        System.out.printf("Estimated tokens: %d (~$%.4f at $3/1M)%n", estimatedTokens, (estimatedTokens * 3.0 / 1_000_000));
    }
}
