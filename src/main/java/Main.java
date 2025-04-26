import jason.JasonException;
import jason.infra.local.RunLocalMAS;



//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {




        // Εκτέλεση του πράκτορα που έχει υλοποιηθεί με το ASL
        try {
            // Ορίζουμε και τρέχουμε τον πράκτορα που έχει γραφτεί στο "FirstAgent.mas2j"
            RunLocalMAS.main(new String[] { "agents-folder/FirstAgent.mas2j" });
        } catch (JasonException e) {
            e.printStackTrace();
        }

        // Εκτύπωση νέας κατάστασης του περιβάλλοντος μετά την κίνηση του πράκτορα
    }
}