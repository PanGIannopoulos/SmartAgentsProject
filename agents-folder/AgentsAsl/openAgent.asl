!start.

+!start : true <-
    .print("Starting...");
    !go_to_target.

+!go_to_target : target(C, X, Y) & pos(X1, Y1) <-
    .print("Going to target at (" ); .print(X); .print(","); .print(Y); .print(")");
    !move_step(X1, Y1, X, Y).

+!move_step(X1, Y1, X, Y) : X1 < X <-
    .send(env, achieve, move(right)); !wait.

+!move_step(X1, Y1, X, Y) : X1 > X <-
    .send(env, achieve, move(left)); !wait.

+!move_step(X1, Y1, X, Y) : Y1 < Y <-
    .send(env, achieve, move(down)); !wait.

+!move_step(X1, Y1, X, Y) : Y1 > Y <-
    .send(env, achieve, move(up)); !wait.

+!wait : pos(X1, Y1) & target(_, X, Y) & (X1 \== X | Y1 \== Y) <-
    !move_step(X1, Y1, X, Y).

+!wait : pos(X, Y) & target(_, X, Y) <-
    .print("Target reached at (");
    .print(X);
    .print(",");
    .print(Y);
    .print(")");.