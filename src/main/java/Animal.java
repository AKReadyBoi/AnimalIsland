import java.rmi.MarshalledObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public abstract class Animal extends Entity {
    public static final Map<Class<? extends Entity>, Map<Class<? extends Entity>, Double>> eatingTable = new HashMap<>();
    public int getMass() {
        return 0;
    }

    public void setMass(int mass) {

    }

    public int getMovesWithoutFood() {
        return 0;
    }

    public void setMovesWithoutFood(int movesWithoutFood) {

    }
    public static Map<Class<? extends Entity>, Map<Class<? extends Entity>, Double>> createEatingTable() {
        eatingTable.put(Wolf.class, Map.ofEntries(
                Map.entry(Rabbit.class, 0.60),
                Map.entry(Mouse.class, 0.80),
                Map.entry(Goat.class, 0.60),
                Map.entry(Deer.class, 0.15),
                Map.entry(Duck.class, 0.40),
                Map.entry(Horse.class, 0.10),
                Map.entry(Sheep.class, 0.70),
                Map.entry(Boar.class, 0.15),
                Map.entry(Buffalo.class, 0.10)
        ));

        eatingTable.put(Boa.class, Map.ofEntries(
                Map.entry(Rabbit.class, 0.20),
                Map.entry(Mouse.class, 0.40),
                Map.entry(Duck.class, 0.10)
        ));

        eatingTable.put(Fox.class, Map.ofEntries(
                Map.entry(Rabbit.class, 0.70),
                Map.entry(Mouse.class, 0.90),
                Map.entry(Duck.class, 0.60)
        ));

        eatingTable.put(Bear.class, Map.ofEntries(
                Map.entry(Rabbit.class, 0.80),
                Map.entry(Mouse.class, 0.80),
                Map.entry(Goat.class, 0.70),
                Map.entry(Deer.class, 0.80),
                Map.entry(Duck.class, 0.10),
                Map.entry(Horse.class, 0.40),
                Map.entry(Sheep.class, 0.70),
                Map.entry(Boar.class, 0.50),
                Map.entry(Buffalo.class, 0.20)
        ));

        eatingTable.put(Eagle.class, Map.ofEntries(
                Map.entry(Rabbit.class, 0.90),
                Map.entry(Mouse.class, 0.90),
                Map.entry(Duck.class, 0.80)
        ));
        return eatingTable;
    }

    public synchronized void eat(Animal predator, Entity prey, ArrayList<Entity> mainField) {
        Class<?> predatorClass = predator.getClass();
        Class<?> preyClass = prey.getClass();
        double probability = eatingTable.get(predatorClass).get(preyClass);
        Random rand = new Random();
        double chance = rand.nextDouble();
        if(chance <= probability) {
            predator.setMass(((Herbivorous) prey).getMass()+predator.getMass());
            mainField.remove(prey);
        }
    }
    public void move(Entity animal, int xOrt, int yOrt, ArrayList<Entity>[][] mainField) {
        Random random = new Random();
        int newX = (xOrt + random.nextInt(3)+9) % 10;
        int newY = (yOrt + random.nextInt(3)+9) % 10;
        mainField[newX][newY].add(animal);
        mainField[xOrt][yOrt].remove(animal);
    }
}
