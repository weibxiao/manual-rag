/**
 * Feel free to use this code, Please don't remove the author and email
 */
package panda.ai.rag.repository;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Repository;

import panda.ai.rag.model.ManualChunk;

@Repository
public class ManualChunkRepository {

    private final CassandraTemplate cassandraTemplate;

    public ManualChunkRepository(CassandraTemplate cassandraTemplate) {
        this.cassandraTemplate = cassandraTemplate;
    }

    public void save(ManualChunk chunk) {
        cassandraTemplate.insert(chunk);
    }

    public List<ManualChunk> findSimilarChunks(String productId, float[] queryEmbedding, int topK) {

        String cql = "SELECT id, product_id, chunk_index, chunk_text, embedding"+
                " FROM manual_chunks WHERE product_id = '" +productId +"'"+
                " ORDER BY embedding ANN OF " + Arrays.toString(queryEmbedding) + 
                " LIMIT "+topK;
                
        return cassandraTemplate.select(cql, ManualChunk.class);
    }
}
