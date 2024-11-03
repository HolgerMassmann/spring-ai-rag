package guru.springframework.springairag.chat;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.observation.VectorStoreQueryResponseObservationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChatServiceImpl
        implements ChatService {

  private final ChatModel chatModel;
  private final SimpleVectorStore vectorStore;
  private final VectorStoreQueryResponseObservationFilter vectorStoreQueryResponseObservationFilter;


  @Value("classpath:/templates/rag-prompt-template.st")
  private Resource ragPromptTemplate;

  /**
   * Constructor
   *
   * @param chatModel   the {@code ChatModel} used to communicate with the OpenAI API
   * @param vectorStore the {@code SimpleVectorStore} to get the embeddings from
   */
  public ChatServiceImpl( ChatModel chatModel, SimpleVectorStore vectorStore,
                          VectorStoreQueryResponseObservationFilter vectorStoreQueryResponseObservationFilter ) {
    this.chatModel = chatModel;
    this.vectorStore = vectorStore;
    this.vectorStoreQueryResponseObservationFilter = vectorStoreQueryResponseObservationFilter;
  }

  @Override
  public Answer getAnswer( Question question ) {

    List<Document> documents = vectorStore.similaritySearch( SearchRequest.query( question.question() ).withTopK( 4 ) );
    List<String> contentList = documents.stream().map( Document::getContent ).toList();

    PromptTemplate promptTemplate = new PromptTemplate( ragPromptTemplate );
    Map<String, Object> bindingMap =
            Map.of( "input", question.question(), "documents", String.join( "\n", contentList ) );
    Prompt prompt = promptTemplate.create( bindingMap );

    ChatResponse response = chatModel.call( prompt );

    return new Answer( response.getResult().getOutput().getContent() );

  }
}
