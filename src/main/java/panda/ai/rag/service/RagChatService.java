/**
 * Feel free to use this code, Please don't remove the author and email
 */
package panda.ai.rag.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import panda.ai.rag.model.ManualChunk;
import panda.ai.rag.repository.ManualChunkRepository;

@Service
public class RagChatService {

    private final EmbeddingModel embeddingModel;
    private final ManualChunkRepository manualChunkRepository;
    private final ChatModel chatModel;

    public RagChatService(EmbeddingModel embeddingModel,
                          ManualChunkRepository manualChunkRepository,
                          ChatModel chatModel) {
        this.embeddingModel = embeddingModel;
        this.manualChunkRepository = manualChunkRepository;
        this.chatModel = chatModel;
    }

    public String askQuestion(String productId, String question) {

        float[] queryEmbedding = embeddingModel.embed(question);
       
        List<ManualChunk> topChunks = manualChunkRepository.findSimilarChunks(productId, queryEmbedding, 5);

        String context = topChunks.stream()
                .map(c -> "CHUNK:\n" + c.getChunkText())
                .collect(Collectors.joining("\n\n"));

        String promptText = """
                You are a product manual assistant.
                Answer the question strictly using the context.
                If the answer is not in the context, say: "I don't know based on the manual."

                CONTEXT:
                %s

                QUESTION:
                %s
                """.formatted(context, question);

        return chatModel.call(new Prompt(promptText))
                .getResult()
                .getOutput()
                .getText();
    }
}
