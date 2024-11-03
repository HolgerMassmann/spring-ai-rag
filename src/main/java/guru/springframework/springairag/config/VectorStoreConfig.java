package guru.springframework.springairag.config;

import org.slf4j.Logger;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.List;

@Configuration
public class VectorStoreConfig {

  /** The {@code Logger} */
  private static final Logger log = LoggerFactory.getLogger( VectorStoreConfig.class );

  /**
   * Creates and returns a {@linkplain SimpleVectorStore} instance containing the loaded document embeddings
   *
   * @param embeddingModel
   * @param vectorStoreProperties
   * @return
   */
  @Bean
  public SimpleVectorStore simpleVectorStore( EmbeddingModel embeddingModel,
                                              VectorStoreProperties vectorStoreProperties ) {
    SimpleVectorStore store = new SimpleVectorStore( embeddingModel );
    File vectorStoreFile = new File( vectorStoreProperties.getVectorStorePath() );

    if (vectorStoreFile.exists()) {
      getLog().info( "Loading vector store: {}", vectorStoreFile.getAbsolutePath() );
      store.load( vectorStoreFile );
    }
    else {
      getLog().info( "Loading documents into vector store..." );
      vectorStoreProperties.getDocumentsToLoad().forEach( document -> {
        getLog().info( "Loading document: {}", document.getFilename() );

        store.add( readAndSplitDocument( document ) );

      });

      store.save( vectorStoreFile );
    }

    return store;
  }


  /**
   * Reads the document using a {@linkplain TikaDocumentReader}
   * @param document the {@code Resource} do load
   * @return list of {@linkplain Document} instances
   */
  private List<Document> readAndSplitDocument( Resource document ) {
    TikaDocumentReader documentReader = new TikaDocumentReader( document );
    List<Document> documents = documentReader.get();
    TextSplitter textSplitter = new TokenTextSplitter();
    List<Document> splitDocuments = textSplitter.apply( documents );

    return splitDocuments;
  }

  private Logger getLog() {
    return log;
  }
}
