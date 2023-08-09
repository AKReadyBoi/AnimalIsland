public class Deer extends Herbivorous{

    String symbol = "\uD83E\uDD8C";
    int mass = 300;
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
