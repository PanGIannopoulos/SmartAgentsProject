!start.

/* Initialization */
+!start : true <-
    .print("Mission started");
    +steps(0);
    +reward(0);
    !initialize_not_done_targets;
    !select_target.


+!initialize_not_done_targets :
    .findall(target(C, X, Y, R), target(C, X, Y, R), Targets);
    .foreach(Target, Targets) <-
        .print("Adding target ", Target, " to NotDone_targets");
        +NotDone_targets(Target).


/* Target Selection */
+!select_target :
    target(C, X, Y, R) &
    not done_target(C, X, Y, R) <-
    .print(["Selected target ", C, " at (", X, ",", Y, ")"]);
    !go_to_target(C, X, Y, R).

+!select_target :
    target(_, _, _, _) & done_target(_, _, _, _) <-
    .findall(done_target(C,X,Y,R), done_target(C,X,Y,R), DoneTargets);
    .print("All targets completed!");
    .print(["Completion report: ", DoneTargets]);
    !mission_complete.

/* Movement System */
+!go_to_target(C, X, Y,R) : pos(X1, Y1) <-
    .print(["Navigating to ", C, " at (", X, ",", Y, ")"]);
    !move_step(C, X1, Y1, X, Y, R).

+!move_step(_, _, _, _, _, _) : steps(31) <-
    .print("31 step limit reached!");
    !mission_complete.

+!move_step(C, X1, Y1, X, Y, R) : X1 < X <-
    .print("Moving RIGHT toward ", C);
    move(right);
    !increment_steps;
    !wait(C, X, Y, R).

+!move_step(C, X1, Y1, X, Y, R) : X1 > X <-
    .print("Moving LEFT toward ", C);
    move(left);
    !increment_steps;
    !wait(C, X, Y, R).

+!move_step(C, X1, Y1, X, Y, R) : Y1 < Y <-
    .print("Moving DOWN toward ", C);
    move(down);
    !increment_steps;
    !wait(C, X, Y, R).

+!move_step(C, X1, Y1, X, Y, R) : Y1 > Y <-
    .print("Moving UP toward ", C);
    move(up);
    !increment_steps;
    !wait(C, X, Y, R).

+!move_step(C, X, Y, X, Y, R) <-
    .print("Already at ", C, "'s position");
    !wait(C, X, Y, R).

/* Target Completion */
+!wait(C, X, Y, R) : pos(X, Y) & target(C, X, Y, R) <-
    .print(["Successfully reached ", C, " at (", X, ",", Y, ")"]);
    !add_reward(R);
    +done_target(C, X, Y, R);
    !select_target.

+!wait(C, X, Y, R) : pos(X1, Y1) & not (X1 == X & Y1 == Y) <-
    !move_step(C, X1, Y1, X, Y, R).

+!increment_steps : steps(N) & reward(R)  <-
    -reward(R);
    +reward(R-0.01);
    -steps(N);
    +steps(N+1).

+!add_reward(R) : reward(S) <-
    -reward(S);
    +reward(S + R).



+!mission_complete : steps(TotalSteps) & reward(TotalReward) <-
    .print("Total steps taken: ", TotalSteps);
    .print("Total reward collected: ", TotalReward);
    .print("Mission accomplished!").