package ee.ctob.data;

import ee.ctob.websocket.GameWebSocketHandler;

public class Bet {
    private final Player player;
    private final int number;
    private final double amount;

    public Bet(Player player, int number, double amount) {
        this.player = player;
        this.number = number;
        this.amount = amount;
    }

    public Player getPlayer() { return player; }
    public int getNumber() { return number; }
    public double getAmount() { return amount; }
}
