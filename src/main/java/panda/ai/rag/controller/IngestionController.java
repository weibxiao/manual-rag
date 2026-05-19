/**
 * Feel free to use this code, Please don't remove the author and email
 */
package panda.ai.rag.controller;

import jakarta.validation.constraints.NotBlank;
import panda.ai.rag.service.PdfIngestionService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingest")
public class IngestionController {

    private final PdfIngestionService ingestionService;

    public IngestionController(PdfIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/pdf")
    public String ingestPdf(@RequestParam @NotBlank String productId) {
        return ingestionService.ingestPdf(productId);
    }
}
