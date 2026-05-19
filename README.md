# Manual RAG (PDF -> Cassandra Vector Search -> Spring AI)

## Description
It will read the device manual of Philips coffee machine 3200 manual. After this manual is saved into Cassandra vector database, with local Ollama model, the end user can ask the question about this product.

## Requirements
- Java 17+ (Don't use Java 21 above, it will cause the exception when starting Cassandra)
- Maven 3.8+
- Cassandra with Vector Search (recommended: DataStax Astra DB)
- The application is set to run in local environment on MacOS with macOS 26.2

## Cassandra Schema

```sql
CREATE KEYSPACE IF NOT EXISTS product_manuals
WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

USE product_manuals;

CREATE TABLE IF NOT EXISTS manual_chunks (
    id UUID PRIMARY KEY,
    product_id TEXT,
    chunk_index INT,
    chunk_text TEXT,
    embedding VECTOR<FLOAT, 1536>
);

CREATE CUSTOM INDEX IF NOT EXISTS ON manual_chunks (embedding) USING 'org.apache.cassandra.index.sai.StorageAttachedIndex';

CREATE CUSTOM INDEX IF NOT EXISTS ON manual_chunks (product_id) USING 'org.apache.cassandra.index.sai.StorageAttachedIndex';

```

## Local Ollama

use model nomic-embed-text-v2-moe locally

```bash
curl -fsSL https://ollama.com/install.sh 

ollama pull nomic-embed-text-v2-moe

```

## Run

Depend how to set up YAML file. It is not necessary to export environment variables.

```bash
export OPENAI_API_KEY="sk-xxx"

export CASSANDRA_HOST="127.0.0.1"
export CASSANDRA_PORT="9042"
export CASSANDRA_DC="datacenter1"
export CASSANDRA_USERNAME="cassandra"
export CASSANDRA_PASSWORD="cassandra"

mvn spring-boot:run
```

## Ingest PDF

Save the manual into Cassandra

```bash
curl -X POST "http://localhost:8080/api/ingest/pdf?productId=philip3200"
```

## Ask Question (RAG)

Ask question with Ollama Model

```bash
curl "http://localhost:8080/api/chat?productId=iphone15&question=warranty info?"
```
