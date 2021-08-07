package lexical_analysis;

import java.io.*;

public class LA {

    public static String[] tokenArray = new String[1000];
    public static String resultString = "";


    public String[] getTokenArray(){
        return tokenArray;
    }

    public String getResultString(){
        return resultString;
    }


    public boolean isKey(String s) {
        //关键字数组
        String keyArray[] = { "int","char","float","double","boolean","return","string",
                "if","else","do","while","for","default","public","static","switch","true","false","print"};
        //与当前字符串一一对比
        for (int i = 0;i < keyArray.length;i++) {
            if (keyArray[i].equals(s)) {
                return true;
            }
        }
        return false;
    }

    public void execute() {
        File file = new File("src/grammar_analysis/code.txt");
        Reader reader = null;
        String token = "";
        //String[] tokenArray = new String[1000];
        int count = 0;
        int line = 1;

        //对应的位置就是种别码
        String kindArray[] = { "","ID","16-INT","8-INT","INT","FLOAT","STRING","ADDADD","ADDEQUAL"," ADD",
                "MINUSMINUS","MINUSEQUAL","MINUS","MULEQUAL","MUL","DIVEQU","DIV","BIGEQU","BIG","SMAEQU",
                "SMA","EQUEQU","EQU","ANDAND","AND","OROR","OR","LR","RR","BIGLR","BIGRR","LS","RS","SEMIC","COLON"};

        try {
            reader = new InputStreamReader(new FileInputStream(file));

            char ch = (char)reader.read();
            //
            while (ch!='\uFFFF'){

                if (ch =='\r'){
                    line ++;
                }
                //将空格都读掉
                while (ch==' '||ch=='\n'||ch=='\t'||ch=='\r'){
                    ch = (char)reader.read();
                }
                token = token + ch;

                //识别关键字和标识符号
                if (Character.isLetter(ch)||ch=='_'){
                    ch = (char) reader.read();
                    while (Character.isLetterOrDigit(ch)||ch=='_'){
                        token = token + ch;
                        ch = (char) reader.read();
                    }

                    if (isKey(token)){
                        tokenArray[count] = token + "     <"+  token +",->";
                        resultString = resultString + token + " ";
                        count ++;
                    }
                    else {
                        tokenArray[count] = token + "     <"+ kindArray[1] + ","+token+">";
                        resultString = resultString + "id" + " ";
                        count ++;
                    }
                    token = "";
                }

                //识别常数
                else if (Character.isDigit(ch)){
                    if (ch == '0'){
                        ch = (char) reader.read();
                        token = token + ch;
                        switch (ch){
                            //识别十六进制
                            case 'x':
                                ch = (char) reader.read();
                                if (ch>='0'&&ch<='9'||ch>='a'&&ch<='f'){
                                    while (ch>='0'&&ch<='9'||ch>='a'&&ch<='f'){
                                        token = token + ch;
                                        ch = (char) reader.read();
                                    }
                                    tokenArray[count] = token + "     <"+kindArray[2]+","+ token +">";
                                    resultString = resultString + "num" + " ";
                                    count ++;
                                }
                                else{
                                    ch = (char) reader.read();
                                    System.out.println("Lexical error at Line ["+ line +"]: [非十六进制标准格式]");
                                }
                                token = "";
                                break;
                            default:
                                //识别八进制
                                if (ch>='0'&&ch<='7'){
                                    ch = (char) reader.read();
                                    while (ch>='0' && ch<='7'){
                                        token = token + ch;
                                        ch = (char) reader.read();
                                    }
                                    tokenArray[count] = token + "     <"+kindArray[3]+","+ token +">";
                                    resultString = resultString + "num" + " ";
                                    count ++;
                                    token = "";
                                }
                                //是0的情况
                                else{
                                    tokenArray[count] = token + "     <"+kindArray[4]+","+ 0 +">";
                                    resultString = resultString + "num" + " ";
                                    count ++;
                                    token = "";
                                    if (ch!=' '&&ch!='\n'&&ch!='\t'&&ch!='\r'){
                                        token = ""+ch;
                                    }
                                }

                        }
                    }
                    //识别十进制
                    else {
                        ch = (char) reader.read();
                        while (ch>='0'&&ch<='9'){
                            token = token + ch;
                            ch = (char) reader.read();
                        }
                        //识别浮点数
                        if (ch == '.'){
                            token = token + ch;
                            ch = (char) reader.read();
                            while (ch>='0'&&ch<='9'){
                                token = token + ch;
                                ch = (char) reader.read();
                            }
                            if (ch == 'e'){
                                token = token + ch;
                                ch = (char) reader.read();
                                while (ch>='0'&&ch<='9'){
                                    token = token + ch;
                                    ch = (char) reader.read();
                                }
                            }
                            tokenArray[count] = token + "     <"+kindArray[5]+","+ token +">";
                            resultString = resultString + "num" + " ";
                            count ++;
                            token = "";
                        }
                        //不是浮点数，是十进制
                        else{
                            tokenArray[count] = token + "     <"+kindArray[4]+","+ token +">";
                            resultString = resultString + "num" + " ";
                            count ++;
                            token = "";
                        }
                    }
                }

                else{
                    switch(ch){
                        //匹配字符串常数
                        case '"':
                            token = "";
                            ch = (char) reader.read();
                            int stringcount = 0;
                            while (ch!='"'&& stringcount<100){
                                token = token + ch;
                                ch = (char) reader.read();
                                stringcount ++;
                            }
                            if (stringcount>=100){
                                System.out.println("Lexical error at Line ["+ line +"]: [字符串匹配失败]");
                            }
                            else {
                                tokenArray[count] = token + "     <"+kindArray[6]+","+ token +">";
                                resultString = resultString + token + " ";
                                count ++;
                                token = "";
                            }
                            ch = (char) reader.read();
                            break;

                        //识别算术运算符号
                        case '+':
                            ch = (char) reader.read();
                            if (ch=='+'){
                                token = token + ch;
                                tokenArray[count] = token + "     <"+ kindArray[7] +","+ token +">";
                                resultString = resultString + "++" + " ";
                                count ++;
                                token = "";
                                ch = (char) reader.read();
                            }
                            else if(ch=='='){
                                token = token + ch;
                                tokenArray[count] = token + "     <"+ kindArray[8] +","+ token +">";
                                resultString = resultString + "+=" + " ";
                                count ++;
                                token = "";
                                ch = (char) reader.read();
                            }
                            else {
                                tokenArray[count] = token + "     <"+kindArray[9]+","+ token +">";
                                resultString = resultString + "+" + " ";
                                count ++;
                                token = "";
                            }
                            break;
                        case '-':
                            ch = (char) reader.read();
                            if (ch=='-'){
                                token = token + ch;
                                tokenArray[count] = token + "     <"+kindArray[10]+","+ token +">";
                                resultString = resultString + "--" + " ";
                                count ++;
                                token = "";
                                ch = (char) reader.read();
                            }
                            else if(ch=='='){
                                token = token + ch;
                                tokenArray[count] = token + "     <"+kindArray[11]+","+ token +">";
                                resultString = resultString + "-=" + " ";
                                count ++;
                                token = "";
                                ch = (char) reader.read();
                            }
                            else {
                                tokenArray[count] = token + "     <"+kindArray[12]+","+ token +">";
                                resultString = resultString + "-" + " ";
                                count ++;
                                token = "";
                            }
                            break;
                        case '*':
                            ch = (char) reader.read();
                            if (ch=='='){
                                token = token + ch;
                                tokenArray[count] = token + "     <"+kindArray[13]+","+ token +">";
                                resultString = resultString + "*=" + " ";
                                count ++;
                                token = "";
                                ch = (char) reader.read();
                            }
                            else {
                                tokenArray[count] = token + "     <"+kindArray[14]+","+ token +">";
                                resultString = resultString + "*" + " ";
                                count ++;
                                token = "";
                            }
                            break;

                        case '/':
                            ch = (char) reader.read();
                            if (ch=='='){
                                token = token + ch;
                                tokenArray[count] = token + "     <"+kindArray[15]+","+ token +">";
                                resultString = resultString + "/=" + " ";
                                count ++;
                                token = "";
                                ch = (char) reader.read();
                            }

                            //忽略单行注释
                            else if(ch == '/'){
                                while (ch!='\r'&&ch!='\n'){
                                    ch = (char)reader.read();
                                }
                                token = "";
                            }
                            //忽略多行注释
                            else if (ch == '*'){
                                ch = (char) reader.read();
                                while(ch!='\uFFFF'){
                                    while (ch!='*'){
                                        ch = (char) reader.read();
                                        if (ch == '\r'||ch == '\n'){
                                            line ++;  //读多行注释的时候可能会换行
                                        }
                                    }
                                    ch = (char) reader.read();
                                    if (ch == '/'){
                                        break;
                                    }
                                }
                                ch = (char) reader.read();
                                token = "";
                            }

                            else {
                                tokenArray[count] = token + "     <"+kindArray[16]+","+ token +">";
                                resultString = resultString + "/" + " ";
                                count ++;
                                token = "";
                            }
                            break;


                        //识别关系运算符号
                        case '>':
                            ch = (char) reader.read();
                            if (ch=='='){
                                token = token + ch;
                                tokenArray[count] = token + "     <"+kindArray[17]+","+ token +">";
                                resultString = resultString + ">=" + " ";
                                count ++;
                                token = "";
                                ch = (char) reader.read();
                            }
                            else {
                                tokenArray[count] = token + "     <"+kindArray[18]+","+ token +">";
                                resultString = resultString + ">" + " ";
                                count ++;
                                token = "";
                            }
                            break;
                        case '<':
                            ch = (char) reader.read();
                            if (ch=='='){
                                token = token + ch;
                                tokenArray[count] = token + "     <"+kindArray[19]+","+ token +">";
                                resultString = resultString + "<=" + " ";
                                count ++;
                                token = "";
                                ch = (char) reader.read();
                            }
                            else {
                                tokenArray[count] = token + "     <"+kindArray[20]+","+ token +">";
                                resultString = resultString + "<" + " ";
                                count ++;
                                token = "";
                            }
                            break;
                        case '=':
                            ch = (char) reader.read();
                            if (ch=='='){
                                token = token + ch;
                                tokenArray[count] = token + "     <"+kindArray[21]+","+ token +">";
                                resultString = resultString + "==" + " ";
                                count ++;
                                token = "";
                                ch = (char) reader.read();
                            }
                            else {
                                tokenArray[count] = token + "     <"+kindArray[22]+","+ token +">";
                                resultString = resultString + "=" + " ";
                                count ++;
                                token = "";
                            }
                            break;

                        //逻辑运算
                        case '&':
                            ch = (char) reader.read();
                            if (ch=='&'){
                                token = token + ch;
                                tokenArray[count] = token + "     <"+kindArray[23]+","+ token +">";
                                resultString = resultString + "&&" + " ";
                                count ++;
                                token = "";
                                ch = (char) reader.read();
                            }
                            else {
                                tokenArray[count] = token + "     <"+kindArray[24]+","+ token +">";
                                resultString = resultString + "&" + " ";
                                count ++;
                                token = "";
                            }
                            break;

                        case '|':
                            ch = (char) reader.read();
                            if (ch=='|'){
                                token = token + ch;
                                tokenArray[count] = token + "     <"+kindArray[25]+","+ token +">";
                                resultString = resultString + "||" + " ";
                                count ++;
                                token = "";
                                ch = (char) reader.read();
                            }
                            else {
                                tokenArray[count] = token + "     <"+kindArray[26]+","+ token +">";
                                resultString = resultString + "|" + " ";
                                count ++;
                                token = "";
                            }
                            break;

                        //分隔符号
                        case '(':
                            tokenArray[count] = token + "     <"+kindArray[27]+","+ token +">";
                            resultString = resultString + "(" + " ";
                            count ++;
                            token = "";
                            ch = (char) reader.read();
                            break;
                        case ')':
                            tokenArray[count] = token + "     <"+kindArray[28]+","+ token +">";
                            resultString = resultString + ")" + " ";
                            count ++;
                            token = "";
                            ch = (char) reader.read();
                            break;
                        case '{':
                            tokenArray[count] = token + "     <"+kindArray[29]+","+ token +">";
                            resultString = resultString + "{" + " ";
                            count ++;
                            token = "";
                            ch = (char) reader.read();
                            break;
                        case '}':
                            tokenArray[count] = token + "     <"+kindArray[30]+","+ token +">";
                            resultString = resultString + "}" + " ";
                            count ++;
                            token = "";
                            ch = (char) reader.read();
                            break;
                        case '[':
                            tokenArray[count] = token + "     <"+kindArray[31]+","+ token +">";
                            resultString = resultString + "[" + " ";
                            count ++;
                            token = "";
                            ch = (char) reader.read();
                            break;
                        case ']':
                            tokenArray[count] = token + "     <"+kindArray[32]+","+ token +">";
                            resultString = resultString + "]" + " ";
                            count ++;
                            token = "";
                            ch = (char) reader.read();
                            break;
                        case ';':
                            tokenArray[count] = token + "     <"+kindArray[33]+","+ token +">";
                            resultString = resultString + ";" + " ";
                            count ++;
                            token = "";
                            ch = (char) reader.read();
                            break;
                        case ':':
                            tokenArray[count] = token + "     <"+kindArray[34]+","+ token +">";
                            resultString = resultString + ":" + " ";
                            count ++;
                            token = "";
                            ch = (char) reader.read();
                            break;
                    }
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
            return;
        }
    }
}

