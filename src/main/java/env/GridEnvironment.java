package env;

import jason.asSyntax.Literal;
import jason.environment.Environment;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import jason.asSyntax.*;
import jason.environment.*;


import java.util.*;

public class GridEnvironment extends Environment {

    private final int GRID_SIZE = 9;
    private final double MOVE_COST = 0.01;
    private int agentX, agentY;
    private int step_count;
    private List<Target> targets;
    private Random rand = new Random();
    private List<Obstacle> obstacles;
    private List<String> plannedPath = new ArrayList<>();



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
    public boolean executeAction(String agName, Structure action) {
        String act = action.getFunctor();

        if (act.equals("move") && action.getArity() == 1) {
            String dir = action.getTerm(0).toString();

            switch (dir) {
                case "up":
                    if (agentY > 0) agentY--;
                    break;
                case "down":
                    if (agentY < GRID_SIZE - 1) agentY++;
                    break;
                case "left":
                    if (agentX > 0) agentX--;
                    break;
                case "right":
                    if (agentX < GRID_SIZE - 1) agentX++;
                    break;
                default:
                    System.out.println("Unknown move direction: " + dir);
                    return false;
            }
        }
        else if (act.equals("pathfind") && action.getArity() == 2) {
            try {
                int goalX = (int)((NumberTerm)action.getTerm(0)).solve();
                int goalY = (int)((NumberTerm)action.getTerm(1)).solve();

                int startX = agentX;
                int startY = agentY;

                List<String> path = findPath(startX, startY, goalX, goalY);

                plannedPath.clear();
                plannedPath.addAll(path);

                String pathStr = "[" + String.join(",", plannedPath) + "]";
                addPercept(Literal.parseLiteral("plannedPath(" + pathStr + ")"));

                System.out.println("JAVA Path planned: " + plannedPath);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        else if (act.equals("reset")) {
            resetWorld();
        }
        else {
            System.out.println("Unknown action: " + act);
            return false;
        }

        updatePercepts();

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }

    public void updatePercepts() {
        clearAllPercepts();

        addPercept(Literal.parseLiteral("pos(" + agentX + "," + agentY + ")"));

        for (Target t : targets) {
            addPercept(Literal.parseLiteral("target(" + t.color + "," + t.x + "," + t.y + "," + t.reward + ")"));
        }
        for (Obstacle o : obstacles) {
            addPercept(Literal.parseLiteral("obstacle(" + o.x + "," + o.y + ")"));
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


    public List<String> findPath(int startX, int startY, int goalX, int goalY) {
        class Node implements Comparable<Node> {
            int x, y;
            int gCost; // Cost from start to this node
            int hCost; // Heuristic cost to goal
            Node parent;

            Node(int x, int y, int gCost, int hCost, Node parent) {
                this.x = x;
                this.y = y;
                this.gCost = gCost;
                this.hCost = hCost;
                this.parent = parent;
            }

            int fCost() {
                return gCost + hCost;
            }

            @Override
            public int compareTo(Node other) {
                return Integer.compare(this.fCost(), other.fCost());
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Node node = (Node) o;
                return x == node.x && y == node.y;
            }

            @Override
            public int hashCode() {
                return x * 31 + y;
            }
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        HashMap<String, Node> visited = new HashMap<>();

        Node start = new Node(startX, startY, 0, manhattan(startX, startY, goalX, goalY), null);
        openSet.add(start);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();

            // Goal check
            if (current.x == goalX && current.y == goalY) {
                // Reconstruct path
                List<String> path = new ArrayList<>();
                Node n = current;
                while (n.parent != null) {
                    int dx = n.x - n.parent.x;
                    int dy = n.y - n.parent.y;
                    if (dx == 1) path.add(0, "right");
                    else if (dx == -1) path.add(0, "left");
                    else if (dy == 1) path.add(0, "down");
                    else if (dy == -1) path.add(0, "up");
                    n = n.parent;
                }
                return path;
            }

            visited.put(current.x + "," + current.y, current);

            for (int[] dir : new int[][]{{0,1},{1,0},{0,-1},{-1,0}}) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];

                if (isValid(newX, newY)) {
                    Node neighbor = new Node(newX, newY, current.gCost + 1, manhattan(newX, newY, goalX, goalY), current);
                    if (!visited.containsKey(newX + "," + newY)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>(); // No path found
    }

    private int manhattan(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private boolean isValid(int x, int y) {
        if (x < 0 || x >= GRID_SIZE || y < 0 || y >= GRID_SIZE) return false;
        for (Obstacle o : obstacles) {
            if (o.x == x && o.y == y) return false;
        }
        return true;
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