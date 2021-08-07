package semanticAnalyse;

import semanticAnalyse.GrammarComplier;
import semanticAnalyse.Id;
import semanticAnalyse.MyScanner;
import semanticAnalyse.Token;

import java.util.List;

public class SemanticAnalyse{
	String text_input;
	
	public SemanticAnalyse(String text_input){
		this.text_input = text_input;
	}
	
	public void Parsing(){
		MyScanner scan=new MyScanner(text_input);
		List<Token> token_list=scan.execute();

		GrammarComplier gc=new GrammarComplier();
		gc.analysis(token_list);

		//gc.PrintProductions();

		//获取三地址码
		List<String> codes=gc.getCodes();
		codes.add("END");
		List<String> fourcodes=gc.getfourCodes();
		fourcodes.add("");


		//获取符号表
		List<Id> ids=gc.getIds();
		String[] output = new String[5];


		//输出符号表
		System.out.println("_______符号表______");
		System.out.println("标识符   类型   相对地址");

		for(int i = 0; i < ids.size(); i++){
			output[0] = "<" + (i+1) + ">";
			output[1] = ids.get(i).getName();
			String type = ids.get(i).getType();
			for(int m = 0; m < ids.get(i).arr_list.size(); m++){
				 type = type + "[" + ids.get(i).arr_list.get(m) + "]";						
			}
			output[2] = type;
			output[3] = ids.get(i).getOffset() + "";
			output[4] = ids.get(i).getLength() + "";
			System.out.println(output[1]+ "       "+output[2]+ "      " +output[3]);
            //tbmodel_symbol_list.addRow(new String[]{output[1], output[2], output[4], output[3]});
		}

		if (codes.size()==fourcodes.size()){
			System.out.println("______地址指令_______");
			for(int n = 0; n < codes.size(); n++){
				System.out.println(n +"  "+ fourcodes.get(n) + "   " +codes.get(n));
			}
		}
		else{
			//输出三地址指令
			System.out.println("______三地址指令_______");
			for(int n = 0; n < codes.size(); n++){
				System.out.println(n +"  "+ codes.get(n));
			}

			System.out.println("______四元式_______");
			for(int n = 0; n < fourcodes.size(); n++){
				System.out.println(n +"  "+ fourcodes.get(n));
			}
		}

	}
}