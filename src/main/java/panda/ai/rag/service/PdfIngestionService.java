/**
 * Feel free to use this code, Please don't remove the author and email
 */
package panda.ai.rag.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import panda.ai.rag.model.ManualChunk;
import panda.ai.rag.repository.ManualChunkRepository;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Vector;

import java.util.List;
import java.util.UUID;

@Service
public class PdfIngestionService {
	@Value("classpath:3200.pdf")
    private Resource pdfResource;

    private final EmbeddingModel embeddingModel;
    private final ManualChunkRepository manualChunkRepository;

    public PdfIngestionService(EmbeddingModel embeddingModel,
                              ManualChunkRepository manualChunkRepository) {
        this.embeddingModel = embeddingModel;
        this.manualChunkRepository = manualChunkRepository;
    }

    public String ingestPdf(String productId) {

        List<Document> documents = loadPdfAsDocuments();

        TokenTextSplitter splitter = TokenTextSplitter.builder().withChunkSize(768).build();
        List<Document> chunks = splitter.apply(documents);

        int index = 0;
        for (Document chunk : chunks) {
            String text = chunk.getText();
            if(text != null && text.length() > 0) {
	            float[] embedding = embeddingModel.embed(text);
	
	            ManualChunk manualChunk = new ManualChunk(
	                    UUID.randomUUID(),
	                    productId,
	                    index++,
	                    text,
	                    Vector.of(embedding)
	            );
	
	            manualChunkRepository.save(manualChunk);
            }
        }

        return "Ingested " + chunks.size() + " chunks for productId=" + productId;
    }
    
    public List<Document> loadPdfAsDocuments() {
        // 1. Configure the reader (optional)
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0) // Example configuration
                .build();

        // 2. Initialize the reader with the resource
        PagePdfDocumentReader reader = new PagePdfDocumentReader(pdfResource, config);

        // 3. Read and return the documents (one per page by default)
        return reader.get();
    }
}
