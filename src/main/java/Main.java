import jason.JasonException;
import jason.infra.local.RunLocalMAS;
import environment.GridEnvironment;



//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        GridEnvironment env = new GridEnvironment();
        env.init(new String[] {});  // Αρχικοποίηση του περιβάλλοντος

        // Εκτύπωση αρχικής κατάστασης
        env.updatePercepts();

        // Κίνηση του πράκτορα προς τα επάνω
        env.executeAction("agent1", new jason.asSyntax.Structure("move_up"));

        // Εκτύπωση νέας κατάστασης
        env.updatePercepts();

        env.printGrid();
        try {
            RunLocalMAS.main(new String[] { "agents-folder/FirstAgent.mas2j" });
        } catch (JasonException e) {
            e.printStackTrace();
        }
    }
}