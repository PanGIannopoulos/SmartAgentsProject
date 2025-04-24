package env;

import jason.asSyntax.Literal;
import jason.environment.Environment;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class GridEnvironment extends Environment {

    private final int GRID_SIZE = 9;
    private final double MOVE_COST = 0.01;
    private int agentX, agentY;
    private List<Target> targets;
    private Random rand = new Random();
    private List<Obstacle> obstacles;


    @Override
    public void init(String[] args) {
        super.init(args);
        resetWorld();
    }

    private void resetWorld() {
        // Θέσε τον agent σε τυχαία θέση
        agentX = rand.nextInt(GRID_SIZE);
        agentY = rand.nextInt(GRID_SIZE);
        System.out.println("Agent starting position: (" + agentX + ", " + agentY + ")");


        // Δημιούργησε 4 τυχαίους στόχους
        targets = new ArrayList<>();
        targets.add(new Target("green", 0.8));
        targets.add(new Target("yellow", 0.3));
        targets.add(new Target("blue", 0.6));
        targets.add(new Target("purple", 0.2));

        // Στείλε τις αρχικές αντιλήψεις στον πράκτορα

        for (Target target : targets) {
            int x, y;
            // Βρες μια τυχαία θέση που να είναι κενή
            do {
                x = rand.nextInt(GRID_SIZE);
                y = rand.nextInt(GRID_SIZE);
            } while (isPositionOccupied(x, y));  // Αν η θέση είναι ήδη κατειλημμένη από στόχο, βρες άλλη

            target.setPosition(x, y);
            System.out.println("Target " + target.color + " placed at: (" + x + ", " + y + ")");

        }

        obstacles = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int x, y;
            do {
                x = rand.nextInt(GRID_SIZE);
                y = rand.nextInt(GRID_SIZE);
            } while (isPositionOccupied(x, y));

            Obstacle obs = new Obstacle(x, y);
            obstacles.add(obs);
            System.out.println("Obstacle placed at: (" + x + ", " + y + ")");
        }
        updatePercepts();
    }

    private boolean isPositionOccupied(int x, int y) {
        if (x == agentX && y == agentY) return true;

        for (Target t : targets) {
            if (t.x == x && t.y == y) return true;
        }
        if (obstacles != null) {
            for (Obstacle o : obstacles) {
                if (o.x == x && o.y == y) return true;
            }
        }
        return false;
    }

    @Override
    public boolean executeAction(String agName, jason.asSyntax.Structure action) {
        String act = action.getFunctor();

        switch (act) {
            case "move_up":
                if (agentY > 0) agentY--;
                break;
            case "move_down":
                if (agentY < GRID_SIZE - 1) agentY++;
                break;
            case "move_left":
                if (agentX > 0) agentX--;
                break;
            case "move_right":
                if (agentX < GRID_SIZE - 1) agentX++;
                break;
            case "reset":
                resetWorld();
                break;
            default:
                System.out.println("Unknown action: " + act);
                return false;
        }

        // Ανανεώνει τις αντιλήψεις μετά από κάθε κίνηση
        updatePercepts();

        try {
            Thread.sleep(200);  // μικρή καθυστέρηση για οπτικοποίηση / συγχρονισμό
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }

    public void updatePercepts() {
        clearAllPercepts();

        addPercept(Literal.parseLiteral("pos(" + agentX + "," + agentY + ")"));

        for (Target t : targets) {
            addPercept(Literal.parseLiteral("target(" + t.color + "," + t.x + "," + t.y + ")"));
            // Μπορείς αργότερα να προσθέσεις ξεχωριστά και την πληροφορία reward αν χρειαστεί
        }
    }

    public void printGrid() {
        StringBuilder grid = new StringBuilder();

        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                if (x == agentX && y == agentY) {
                    grid.append("A ");  // A για Agent
                } else {
                    boolean printed = false;

                    // Έλεγχος αν υπάρχει εμπόδιο
                    for (Obstacle o : obstacles) {
                        if (o.x == x && o.y == y) {
                            grid.append("X ");  // X για εμπόδιο
                            printed = true;
                            break;
                        }
                    }

                    if (!printed) {
                        // Έλεγχος αν υπάρχει στόχος στη θέση (x, y)
                        for (Target t : targets) {
                            if (t.x == x && t.y == y) {
                                grid.append(t.color.charAt(0) + " ");  // Το πρώτο γράμμα του χρώματος
                                printed = true;
                                break;
                            }
                        }
                    }

                    if (!printed) {
                        grid.append(". ");  // Κενός χώρος
                    }
                }
            }
            grid.append("\n");
        }

        System.out.println(grid.toString());
    }

    static class Target {
        String color;
        double reward;
        int x, y;

        Target(String color, double reward) {
            this.color = color;
            this.reward = reward;
        }

        void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Obstacle {
        int x, y;

        Obstacle(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}