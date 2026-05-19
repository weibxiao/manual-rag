/**
 * Feel free to use this code, Please don't remove the author and email
 */
package panda.ai.rag.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.mapping.VectorType;
import org.springframework.data.domain.Vector;

import java.util.UUID;

@Table("manual_chunks")
public class ManualChunk {

    @PrimaryKey
    private UUID id;
    
    @Column("product_id")
    private String productId;
    
    @Column("chunk_index")
    private Integer chunkIndex;
    
    @Column("chunk_text")
    private String chunkText;

    @VectorType(dimensions = 768)
    private Vector embedding;


    public ManualChunk(UUID id, String productId, Integer chunkIndex, String chunkText, Vector embedding) {
        this.id = id;
        this.productId = productId;
        this.chunkIndex = chunkIndex;
        this.chunkText = chunkText;
        this.embedding = embedding;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public Integer getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(Integer chunkIndex) { this.chunkIndex = chunkIndex; }

    public String getChunkText() { return chunkText; }
    public void setChunkText(String chunkText) { this.chunkText = chunkText; }

    public Vector getEmbedding() { return embedding; }
    public void setEmbedding(Vector embedding) { this.embedding = embedding; }
}
