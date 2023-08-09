public class Bear extends Predator {
    String symbol = "\uD83D\uDC3B";
    int mass = 500;
    int movesWithoutFood=4;
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
    @Override
    public void setMovesWithoutFood(int movesWithoutFood) {
        this.movesWithoutFood = movesWithoutFood;
    }
}
