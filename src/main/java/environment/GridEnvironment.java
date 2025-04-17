package environment;

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
        updatePercepts();
    }

    private boolean isPositionOccupied(int x, int y) {
        // Έλεγχος αν η θέση είναι κατειλημμένη από κάποιον στόχο
        for (Target t : targets) {
            if (t.x == x && t.y == y) {
                return true;  // Η θέση είναι κατειλημμένη
            }
        }
        return false;  // Η θέση είναι κενή
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

        // Προσθήκη αντίληψης θέσης του πράκτορα
        addPercept(Literal.parseLiteral("pos(" + agentX + "," + agentY + ")"));

        // Προσθήκη αντίληψης στόχων
        for (Target t : targets) {
            addPercept(Literal.parseLiteral("target(" + t.color + "," + t.reward + "," + t.x + "," + t.y + ")"));
        }
    }

    public void printGrid() {
        // Δημιουργία του πλέγματος 9x9
        StringBuilder grid = new StringBuilder();
        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                if (x == agentX && y == agentY) {
                    // Αν η τρέχουσα θέση είναι του πράκτορα, τοποθέτησε τον πράκτορα
                    grid.append("A ");  // A για Agent
                } else {
                    boolean targetFound = false;
                    // Έλεγχος αν υπάρχει στόχος στη θέση (x, y)
                    for (Target t : targets) {
                        if (t.x == x && t.y == y) {
                            grid.append(t.color.charAt(0) + " ");  // Βάλε το αρχικό γράμμα του χρώματος του στόχου
                            targetFound = true;
                            break;
                        }
                    }
                    if (!targetFound) {
                        // Αν δεν υπάρχει στόχος, βάλε κενό ή άλλη ένδειξη
                        grid.append(". ");  // "." για κενό χώρο
                    }
                }
            }
            grid.append("\n");
        }

        // Εκτύπωση του πλέγματος
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
}