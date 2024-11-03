package guru.springframework.springairag.chat;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

  private final ChatService chatService;

  public ChatController( ChatService chatService ) {
    this.chatService = chatService;
  }

  @PostMapping("/ask")
  public Answer ask( @RequestBody Question question) {
    // Call OpenAI to get answer

    Answer answer = chatService.getAnswer( question );
    return answer;
  }
}
