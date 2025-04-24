// Agent beliefs and initial goals
+!start <-
    !choose_target.  // Select a target to pursue

// Choose the first available target (simplified)
+!choose_target : target(C, X, Y) <-
    !move_to_target(C, X, Y).

// Move toward target (X,Y) step-by-step
+!move_to_target(C, X, Y) : pos(X1, Y1) & (X1 \== X | Y1 \== Y) <-
    direction(X1, Y1, X, Y, Dir),  // Calculate direction
    Dir.

// If already at target, do nothing
+!move_to_target(C, X, Y) : pos(X, Y) <-
    println("Reached target: " + C).

// Calculate movement direction
+direction(X1, Y1, X, Y, move_right) : X1 < X <- true.
+direction(X1, Y1, X, Y, move_left)  : X1 > X <- true.
+direction(X1, Y1, X, Y, move_up)    : Y1 < Y <- true.
+direction(X1, Y1, X, Y, move_down)  : Y1 > Y <- true.

// Handle movement failure (e.g., blocked by obstacle)
+!move_left  : not pos(X, Y) <- println("Failed to move left.").  // Add obstacle checks if needed
+!move_right : not pos(X, Y) <- println("Failed to move right.").
+!move_up    : not pos(X, Y) <- println("Failed to move up.").
+!move_down  : not pos(X, Y) <- println("Failed to move down.").

// If no target exists, reset (optional)
+!choose_target : not target(_, _, _) <-
    println("No targets left!");
    reset.