!start.

/* Initialization */
+!start : true <-
    .print("Mission started");
    +steps(0);
    +reward(0);
    !select_target.

/* Target Selection */
+!select_target :
    target(C, X, Y, R) & not done_target(C, X, Y, R) <-
    .print(["Selected target ", C, " at (", X, ",", Y, ")"]);
    !request_path_to_target(X, Y, C, R).

+!select_target :
    target(_, _, _, _) & done_target(_, _, _, _) <-
    .findall(done_target(C, X, Y, R), done_target(C, X, Y, R), DoneTargets);
    .print("All targets completed!");
    .print(["Completion report: ", DoneTargets]);
    !mission_complete.

+!request_path_to_target(X, Y, C, R) : pos(X1, Y1) <-
    .print("Requesting path to target (", C, ") at (", X, ",", Y, ")");
    pathfind(X, Y);  /* Calls the pathfinding method in Java */
    !wait_for_path(C, X, Y, R).  /* Pass the target details to wait_for_path */

+!wait_for_path(C, X, Y, R) : plannedPath(Path) <-
    .print("Received path: ", Path);
    !follow_path(Path, C, X, Y, R).  /* Pass target details to follow_path */

+!follow_path([H|T], C, X, Y, R) : pos(X1, Y1) & steps(N) & N >= 31 <-
    !mission_complete.

+!follow_path([H|T], C, X, Y, R) : pos(X1, Y1) <-
    .print("Moving to next step: ", H);
    !increment_steps;
    move(H);  /* Move to the next step in the path */
    !follow_path(T, C, X, Y, R).  /* Keep passing the target details */

+!follow_path([], C, X, Y, R) <-
    .print("Target ", C, " reached!");
    !add_reward(R);
    +done_target(C, X, Y, R);  /* Mark target as completed */
    !select_target.  /* Recurse to select the next target */


/* Step and Reward Increment */
+!increment_steps : steps(N) & reward(R) <-
    -reward(R);
    +reward(R - 0.01);
    -steps(N);
    +steps(N + 1).



+!add_reward(R) : reward(S) <-
    -reward(S);
    +reward(S + R).

/* Mission Completion */
+!mission_complete : steps(TotalSteps) & reward(TotalReward) <-
    .print("Total steps taken: ", TotalSteps);
    .print("Total reward collected: ", TotalReward);
    .print("Mission accomplished!").