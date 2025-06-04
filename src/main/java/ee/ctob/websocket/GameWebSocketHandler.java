package ee.ctob.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.ctob.data.Bet;
import ee.ctob.data.BetMessage;
import ee.ctob.data.Player;
import ee.ctob.data.Winner;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.*;

public class GameWebSocketHandler extends TextWebSocketHandler {
    private final Map<WebSocketSession, Player> players = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private List<Bet> currentBets = new CopyOnWriteArrayList<>();

    public GameWebSocketHandler() {
        startGameLoop();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Player player = new Player(session);
        players.put(session, player);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        players.remove(session);
        currentBets.removeIf(bet -> bet.getPlayer().getSession().equals(session));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            BetMessage betMessage = objectMapper.readValue(message.getPayload(), BetMessage.class);
            if (betMessage.getNumber() < 1 || betMessage.getNumber() > 10 || betMessage.getAmount() <= 0) {
                session.sendMessage(new TextMessage("Invalid bet"));
                return;
            }
            Player player = players.get(session);
            currentBets.add(new Bet(player, betMessage.getNumber(), betMessage.getAmount()));
        } catch (Exception e) {
            session.sendMessage(new TextMessage("Invalid message format"));
        }
    }

    private void startGameLoop() {
        scheduler.scheduleAtFixedRate(this::playRound, 0, 10, TimeUnit.SECONDS);
    }

    private void playRound() {
        try {
            broadcastAll("New round started. You have 10 seconds to place your bets.");
            Thread.sleep(10_000);
            int winningNumber = new Random().nextInt(10) + 1;
            List<Winner> winners = new ArrayList<>();

            for (Bet bet : currentBets) {
                if (bet.getNumber() == winningNumber) {
                    double winAmount = bet.getAmount() * 9.9;
                    bet.getPlayer().getSession().sendMessage(new TextMessage("You won: " + winAmount));
                    winners.add(new Winner(bet.getPlayer().getNickname(), winAmount));
                } else {
                    bet.getPlayer().getSession().sendMessage(new TextMessage("You lost."));
                }
            }

            broadcastAll("Winning number: " + winningNumber);
            if(!winners.isEmpty()) {
                broadcastAll("Winners: " + objectMapper.writeValueAsString(winners));
            }
            currentBets.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastAll(String message) throws Exception {
        for (Player player : players.values()) {
            player.getSession().sendMessage(new TextMessage(message));
        }
    }
}
