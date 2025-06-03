package ee.ctob.data;


public class Bet {
    Player player;
    int number;
    double amount;

    public Bet(Player player, int number, double amount) {
        this.player = player;
        this.number = number;
        this.amount = amount;
    }

    public Player getPlayer() {
        return player;
    }

    public int getNumber() {
        return number;
    }

    public double getAmount() {
        return amount;
    }
}
