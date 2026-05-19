/**
 * Feel free to use this code, Please don't remove the author and email
 */
package panda.ai.rag.controller;

import jakarta.validation.constraints.NotBlank;
import panda.ai.rag.service.RagChatService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final RagChatService ragChatService;

    public ChatController(RagChatService ragChatService) {
        this.ragChatService = ragChatService;
    }

    @GetMapping
    public String chat(@RequestParam @NotBlank String productId,
                       @RequestParam @NotBlank String question) {
        return ragChatService.askQuestion(productId, question);
    }
}
