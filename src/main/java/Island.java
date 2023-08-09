import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
public class Island {
    private static final ReentrantLock lock = new ReentrantLock();
    public static void main(String[] args) {
        Animal.createEatingTable();
        ArrayList<Entity>[][] mainField = new ArrayList[20][20];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                mainField[i][j] = new ArrayList<>();
            }
        }
        List<Class<? extends Entity>> entityClasses = Arrays.asList(
                Bear.class, Boa.class, Boar.class, Buffalo.class, Deer.class, Duck.class, Eagle.class, Fox.class, Goat.class, Grass.class, Horse.class, Mouse.class, Rabbit.class, Sheep.class, Wolf.class
        );
        Random random = new Random();
        for (Class<? extends Entity> entityClass : entityClasses) {
            int count = random.nextInt(3) + 1;
            for (int i = 0; i < count; i++) {
                try {
                    Entity entity = entityClass.getDeclaredConstructor().newInstance();
                    int x = random.nextInt(10);
                    int y = random.nextInt(10);
                    mainField[x][y].add(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        int predatorCountField = 1;
        int herbivorousCountField = 1;
        while(predatorCountField>0&&herbivorousCountField>0) {
            //фаза передвижения
            ExecutorService movingExecutor = Executors.newFixedThreadPool(10);
            for (int i = 0; i < mainField.length; i++) {
                for (int j = 0; j < mainField[i].length; j++) {
                    ArrayList<Entity> list = new ArrayList<>(mainField[i][j]);
                    for (Entity entity : list) {
                        if (entity instanceof Animal) {
                            int finalJ = j;
                            int finalI = i;
                            movingExecutor.submit(() -> ((Animal) entity).move(entity, finalI, finalJ, mainField));
                        }
                    }
                }
            }
            movingExecutor.shutdown();
            //фаза размножения
            ExecutorService reproductionExecutor = Executors.newFixedThreadPool(10);
            for (int i = 0; i < mainField.length; i++) {
                for (int j = 0; j < mainField[i].length; j++) {
                    ArrayList<Entity> list = new ArrayList<>(mainField[i][j]);
                    Map<Class<?>, Long> classCount = list.stream().filter(e -> e instanceof Animal).collect(Collectors.groupingBy(Entity::getClass, Collectors.counting()));
                    for (Map.Entry<Class<?>, Long> entry : classCount.entrySet()) {
                        Class<?> animalClass = entry.getKey();
                        long pairs = entry.getValue() / 2;
                        for (int k = 0; k < pairs; k++) {
                            int finalI = i;
                            int finalJ = j;
                            reproductionExecutor.submit(() -> {
                                try {
                                    Entity newInstance = (Entity) animalClass.getDeclaredConstructor().newInstance();
                                    synchronized (list) {
                                        mainField[finalI][finalJ].add(newInstance);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    }
                }
            }
            reproductionExecutor.shutdown();
            //фаза еды
            ExecutorService eatingExecutor = Executors.newFixedThreadPool(10);
            for (int i = 0; i < mainField.length; i++) {
                for (int j = 0; j < mainField[i].length; j++) {
                    ArrayList<Entity> list = new ArrayList<>(mainField[i][j]);
                    synchronized (mainField[i][j]) {
                        for (Entity entity : list) {
                            if (entity instanceof Animal) {
                                int finalJ = j;
                                int finalI = i;
                                eatingExecutor.submit(() -> {
                                    synchronized (mainField[finalI][finalJ]) {
                                        if (mainField[finalI][finalJ].stream().anyMatch(e -> e instanceof Herbivorous) && mainField[finalI][finalJ].stream().anyMatch(e -> e instanceof Predator)) {
                                            List<Entity> predators = mainField[finalI][finalJ].stream().filter(e -> e instanceof Predator).collect(Collectors.toList());
                                            List<Entity> herbivores = mainField[finalI][finalJ].stream().filter(e -> e instanceof Herbivorous).collect(Collectors.toList());

                                            for (Entity predator : predators) {
                                                if (!herbivores.isEmpty()) {
                                                    Entity herbivore = herbivores.remove(0);
                                                    ((Predator) predator).eat((Predator) predator, herbivore, mainField[finalI][finalJ]);
                                                }
                                            }
                                        } else {
                                            if (mainField[finalI][finalJ].stream().anyMatch(e -> e instanceof Herbivorous) && mainField[finalI][finalJ].stream().anyMatch(e -> e instanceof Plant)) {
                                                List<Entity> plants = mainField[finalI][finalJ].stream().filter(e -> e instanceof Plant).collect(Collectors.toList());
                                                List<Entity> herbivores = mainField[finalI][finalJ].stream().filter(e -> e instanceof Herbivorous).collect(Collectors.toList());

                                                for (Entity herbivore : herbivores) {
                                                    if (!plants.isEmpty()) {
                                                        Entity plant = plants.remove(0);
                                                        ((Herbivorous) herbivore).eat((Herbivorous) herbivore, plant, mainField[finalI][finalJ]);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
            eatingExecutor.shutdown();
//            удаление животных, у которых масса 0
            for (int i = 0; i < mainField.length; i++) {
                for (int j = 0; j < mainField[i].length; j++) {
                    ArrayList<Entity> list = mainField[i][j];
                    lock.lock();
                    try {
                        Iterator<Entity> iterator = list.iterator();
                        while (iterator.hasNext()) {
                            Entity entity = iterator.next();
                            if (entity instanceof Animal) {
                                try {
                                    Animal newAnimal = (Animal) entity.getClass().getDeclaredConstructor().newInstance();
                                    int massToSubtract = (int) (newAnimal.getMass() * 0.25);
                                    ((Animal) entity).setMass(((Animal) entity).getMass() - massToSubtract);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (((Animal) entity).getMass() <= 0) {
                                    iterator.remove();
                                }
                            }
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
                Random x = new Random();
                Random y = new Random();
                Grass grass = new Grass();
                mainField[x.nextInt(9)][y.nextInt(9)].add(grass);
                int predatorCount = 0;
                int herbivorousCount = 0;
            for (int i = 0; i < mainField.length; i++) {
                for (int j = 0; j < mainField[i].length; j++) {
                    for (Entity entity : mainField[i][j]) {
                        if (entity instanceof Predator) {
                            predatorCount++;
                        } else if (entity instanceof Herbivorous) {
                            herbivorousCount++;
                        }
                    }
                }
            }
            predatorCountField = predatorCount;
            herbivorousCountField = herbivorousCount;
            printField(mainField);
            System.out.print("Number of Predators: " + predatorCount+" ");
            System.out.println("Number of Herbivorous: " + herbivorousCount);
        }
    }
    public static void printField(ArrayList<Entity>[][] mainField) {
        for (int i = 0; i < mainField.length; i++) {
            for (int j = 0; j < mainField[i].length; j++) {
                if (!mainField[i][j].isEmpty()) {
                    StringBuilder cell = new StringBuilder();
                    for (Entity entity : mainField[i][j]) {
                        if (entity != null && entity.getSymbol() != null) {
                            cell.append(entity.getSymbol());
                        }
                    }
                    System.out.print(cell + "\t");
                } else {
                    System.out.print(".\t");
                }
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }

}
