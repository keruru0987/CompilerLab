import semanticAnalyse.SemanticAnalyse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class SemMain {
    public static void main(String[] args) {
        String ta_input = "";
        String file_name;
        file_name = "s_code.txt";
        // 读取文件，写到JTextArea里面
        File file = new File(file_name);
        try{
            InputStream in = new FileInputStream(file);
            int temp;
            while ((temp=in.read()) != -1) {
                ta_input += (char)temp;
            }
            in.close();
        }
        catch(Exception event){
            event.printStackTrace();
        }

        if(ta_input.equals("")){
            System.out.println("nothing input!");
        }
        else {
            SemanticAnalyse semanticanalyse = new SemanticAnalyse(ta_input);
            semanticanalyse.Parsing();
        }
    }
}
