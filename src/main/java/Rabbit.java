public class Rabbit extends Herbivorous {

    String symbol = "\uD83D\uDC07";
    int mass = 10;
    @Override
    public String getSymbol() {
        return symbol;
    }
    @Override
    public int getMass() {
        return mass;
    }
    @Override
    public void setMass(int mass) {
        this.mass = mass;
    }
}
