package ee.ctob.data;

public class Winner {
    private final String nickname;
    private final double winnings;

    public Winner(String nickname, double winnings) {
        this.nickname = nickname;
        this.winnings = winnings;
    }

    public String getNickname() { return nickname; }
    public double getWinnings() { return winnings; }
}
