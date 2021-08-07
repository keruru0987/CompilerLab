import grammar_analysis.GA;
import lexical_analysis.LA;

public class GrammarMain {
    public static void main(String[] args) {
        String strInput = "";
        LA la = new LA();
        la.execute();
        strInput = la.getResultString();
        //System.out.println(strInput);

        GA ga = new GA();
        ga.setStrInput(strInput);
        ga.getNvNt();
        ga.Init();
        ga.createTable();
        //ga.outputFirst();
        //ga.outputFirstX();
        //ga.outputFollow();
        ga.outputTable();
        ga.analyze();
        ga.printTree();
    }
}
