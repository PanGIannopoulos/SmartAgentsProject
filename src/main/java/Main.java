import jason.JasonException;
import jason.infra.local.RunLocalMAS;



//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        try {
            RunLocalMAS.main(new String[] { "agents-folder/AgentProject.mas2j" });
        } catch (JasonException e) {
            e.printStackTrace();
        }

    }
}