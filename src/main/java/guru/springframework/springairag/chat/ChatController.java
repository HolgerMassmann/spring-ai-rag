package guru.springframework.springairag.chat;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

  @PostMapping("/ask")
  public Answer ask( @RequestBody Question question) {
    // Call OpenAI to get answer
    return new Answer( "Hello World" );
  }
}
