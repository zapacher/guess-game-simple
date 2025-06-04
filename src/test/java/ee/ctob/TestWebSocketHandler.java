package ee.ctob;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.ctob.data.BetMessage;
import ee.ctob.websocket.GameWebSocketHandler;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

class GameWebSocketHandlerTest {

    @Test
    void testValidBetMessage() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        AtomicReference<String> response = new AtomicReference<>();

        doAnswer(invocation -> {
            TextMessage msg = invocation.getArgument(0);
            response.set(msg.getPayload());
            return null;
        }).when(session).sendMessage(any(TextMessage.class));

        GameWebSocketHandler handler = new GameWebSocketHandler();
        handler.afterConnectionEstablished(session);

        BetMessage bet = new BetMessage();
        bet.setNumber(5);
        bet.setAmount(10);

        ObjectMapper mapper = new ObjectMapper();
        TextMessage msg = new TextMessage(mapper.writeValueAsString(bet));

        handler.handleTextMessage(session, msg);

        assertEquals("New round started. You have 10 seconds to place your bets.", response.get());
    }

    @Test
    void testInvalidNumber() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        AtomicReference<String> response = new AtomicReference<>();

        doAnswer(invocation -> {
            TextMessage msg = invocation.getArgument(0);
            response.set(msg.getPayload());
            return null;
        }).when(session).sendMessage(any(TextMessage.class));

        GameWebSocketHandler handler = new GameWebSocketHandler();
        handler.afterConnectionEstablished(session);

        BetMessage bet = new BetMessage();
        bet.setNumber(11);
        bet.setAmount(10);

        ObjectMapper mapper = new ObjectMapper();
        TextMessage msg = new TextMessage(mapper.writeValueAsString(bet));

        handler.handleTextMessage(session, msg);

        assertEquals("Invalid bet", response.get());
    }

    @Test
    void testInvalidAmount() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        AtomicReference<String> response = new AtomicReference<>();

        doAnswer(invocation -> {
            TextMessage msg = invocation.getArgument(0);
            response.set(msg.getPayload());
            return null;
        }).when(session).sendMessage(any(TextMessage.class));

        GameWebSocketHandler handler = new GameWebSocketHandler();
        handler.afterConnectionEstablished(session);

        BetMessage bet = new BetMessage();
        bet.setNumber(5);
        bet.setAmount(0);

        ObjectMapper mapper = new ObjectMapper();
        TextMessage msg = new TextMessage(mapper.writeValueAsString(bet));

        handler.handleTextMessage(session, msg);

        assertEquals("Invalid bet", response.get());
    }

    @Test
    void testInvalidJson() throws Exception {
        WebSocketSession session = mock(WebSocketSession.class);
        AtomicReference<String> response = new AtomicReference<>();

        doAnswer(invocation -> {
            TextMessage msg = invocation.getArgument(0);
            response.set(msg.getPayload());
            return null;
        }).when(session).sendMessage(any(TextMessage.class));

        GameWebSocketHandler handler = new GameWebSocketHandler();
        handler.afterConnectionEstablished(session);

        TextMessage msg = new TextMessage("not-json");
        handler.handleTextMessage(session, msg);

        assertEquals("Invalid message format", response.get());
    }
}

