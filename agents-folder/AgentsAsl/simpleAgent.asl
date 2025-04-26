!start.

+!start : true <-
    .print("Starting...");
    !go_to_target.

+!go_to_target : target(C, X, Y) & pos(X1, Y1) <-
.print(["Going to target at (", X,  Y, ")"]);
    !move_step(X1, Y1, X, Y).

+!move_step(_, _, _, _, _) : steps(10) <-
    .print("10 step limit reached!");
    !mission_complete.

+!move_step(X1, Y1, X, Y) : X1 < X <-
.print("RIGHT");
    move(right);
    !wait.

+!move_step(X1, Y1, X, Y) : X1 > X <-
.print("LEFT");
    move(left);
    !wait.

+!move_step(X1, Y1, X, Y) : Y1 < Y <-
.print("DOWN");
    move(down);
    !wait.

+!move_step(X1, Y1, X, Y) : Y1 > Y <-
.print("UP");
    move(up);
    !wait.

+!wait : pos(X, Y) & target(_, X, Y) <-
    .print(["Target reached at (", X, ",", Y, ")"]).

+!wait : pos(X1, Y1) & target(_, X, Y) & not (X1 == X & Y1 == Y) <-
    .print("Moving toward target...");
    !move_step(X1, Y1, X, Y).
