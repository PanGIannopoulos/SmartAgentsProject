!start.

/* Initialization */
+!start : true <-
    +steps(0);
    +reward(0);
    +total_targets(0);
    +max_steps(31);
    !initialize_not_done_targets;
    !find_target.

+!initialize_not_done_targets : true <-
    .findall([C,X,Y,R], target(C, X, Y, R), Targets);
    !process_targets(Targets).

+!process_targets([]) <- true.

+!process_targets([Target|Tail]) <-
    Target = [C,X,Y,R];
    +notdone_target(C, X, Y, R);
    !process_targets(Tail).




+!find_target : true <-
     find_best_target;
     !wait_for_best_target;.

+!wait_for_best_target : seltarget(C, X, Y, R) <-
    !check_target(C, X, Y, R).



/* Target Selection */
+!check_target(C, X, Y, R) :
    target(C, X, Y, R) & not done_target(C, X, Y, R) <-
    !request_path_to_target(X, Y, C, R).

+!check_target(C, X, Y, R) :
    target(C, X, Y, R) & done_target(C, X, Y, R) <-
    .findall(done_target(D, F, G, H), done_target(D, F, G, H), DoneTargets);
    .print("All targets completed!");
    !mission_complete.

+!request_path_to_target(X, Y, C, R) : pos(X1, Y1) <-
    pathfind(X, Y);  /* Calls the pathfinding method in Java */
    !wait_for_path(C, X, Y, R).  /* Pass the target details to wait_for_path */

+!wait_for_path(C, X, Y, R) : plannedPath(Path) <-
    !follow_path(Path, C, X, Y, R).  /* Pass target details to follow_path */

+!follow_path([H|T], C, X, Y, R) :
    steps(N) & max_steps(Max) & N >= Max
    <- !mission_complete.

+!follow_path([H|T], C, X, Y, R) : pos(X1, Y1) <-
    !increment_steps;
    move(H);  /* Move to the next step in the path */
    !follow_path(T, C, X, Y, R).  /* Keep passing the target details */

+!follow_path([], C, X, Y, R) <-
    .print("Target ", C, " reached!");
    !add_reward(R);
    +done_target(C, X, Y, R);
    -notdone_target(C, X, Y, R);
    !find_target.  /* Recurse to select the next target */


/* Step and Reward Increment */
+!increment_steps : steps(N) & reward(R) <-
    -reward(R);
    +reward(R - 0.01);
    -steps(N);
    +steps(N + 1).



+!add_reward(R) : reward(S) & total_targets(T) <-
    -total_targets(T);
    +total_targets(T+1);
    -reward(S);
    +reward(S + R);.


+!mission_complete : steps(TotalSteps) & reward(TotalReward) & total_targets(TotalTargets) <-
    .print("Total steps taken: ", TotalSteps);
    .print("Total reward collected: ", TotalReward);
    .print("Total targets collected: ", TotalTargets);
    .print("Mission accomplished!").