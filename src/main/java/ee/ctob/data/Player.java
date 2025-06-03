package ee.ctob.data;

import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

public class Player {
    WebSocketSession session;
    String nickname;

    public Player(WebSocketSession session) {
        this.session = session;
        this.nickname = UUID.randomUUID().toString().substring(0, 8);
    }

    public WebSocketSession getSession() {
        return session;
    }

    public String getNickname() {
        return nickname;
    }
}
