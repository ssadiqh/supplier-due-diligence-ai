package com.diligence.extraction;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentParser {

    private static final Logger logger = LoggerFactory.getLogger(DocumentParser.class);
    private static final int CHUNK_SIZE = 500;
    private static final int OVERLAP = 50;

    public List<PageChunk> parseDocument(File pdfFile) {
        List<PageChunk> chunks = new ArrayList<>();

        if (!pdfFile.exists()) {
            logger.error("PDF file not found: {}", pdfFile.getAbsolutePath());
            return chunks;
        }

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFTextStripper textStripper = new PDFTextStripper();

            for (int pageNum = 1; pageNum <= document.getNumberOfPages(); pageNum++) {
                textStripper.setStartPage(pageNum);
                textStripper.setEndPage(pageNum);

                String pageText = textStripper.getText(document);
                chunks.addAll(chunkPageText(pageNum, pageText));
            }

            logger.info("Parsed PDF {} into {} chunks", pdfFile.getName(), chunks.size());

        } catch (IOException e) {
            logger.error("Error parsing PDF {}: {}", pdfFile.getName(), e.getMessage(), e);
        }

        return chunks;
    }

    private List<PageChunk> chunkPageText(int pageNumber, String text) {
        List<PageChunk> pageChunks = new ArrayList<>();

        if (text == null || text.isBlank()) {
            return pageChunks;
        }

        int position = 0;
        while (position < text.length()) {
            int endPosition = Math.min(position + CHUNK_SIZE, text.length());
            String chunkText = text.substring(position, endPosition);

            PageChunk chunk = new PageChunk();
            chunk.setPageNumber(pageNumber);
            chunk.setText(chunkText);
            chunk.setStartPosition(position);
            chunk.setEndPosition(endPosition);

            pageChunks.add(chunk);

            position = endPosition - OVERLAP;
            if (position < endPosition - OVERLAP) {
                position = endPosition - OVERLAP;
            }
            if (position >= text.length()) {
                break;
            }
        }

        return pageChunks;
    }
}
