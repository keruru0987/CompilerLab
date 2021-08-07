package grammar_analysis;

import java.util.*;

public class GA {
    //单个符号first集
    public HashMap<String, HashSet<String>> firstSet = new HashMap<>();
    //符号串first集
    public HashMap<String, HashSet<String>> firstSetX = new HashMap<>();
    //开始符
    public static Character S = 'P';
    //非终结符号的follow集
    public HashMap<Character, HashSet<String>> followSet = new HashMap<>();
    //非终结符
    public HashSet<Character> VnSet = new HashSet<>();
    //终结符
    public HashSet<String> VtSet = new HashSet<>();
    //非终结符的产生式集合
    public HashMap<Character, ArrayList<String>> expressionSet = new HashMap<>();

    //ll分析表
    public String[][] table;

    //在弹栈后压入，用于最后输出树
    public node[] nodes = new node[1000];

    //文法
    public String[] inputExpression = {
            "P->B",
            "B ->{ D S }",
            "D ->H",
            "H ->A D","H ->~",
            "A ->C X",
            "X ->id ;","X ->print ( ) B",
            "C ->int J ","C ->char J ","C ->float J ","C ->double J ","C ->short J ",
            "J ->[ num ] J","J ->* J","J ->~",
            "S ->I",
            "I ->T I","I ->~",
            "T ->L = R ;","T ->if ( R ) T else T","T ->do T while ( R ) ;", "T ->break ;","T ->B","T ->print ( G ) ;",
            "L ->id K",
            "K ->[ num ] K","K ->~",
            "R ->E M",
            "M ->< E","M -><= E","M ->> E","M ->>= E","M ->~",
            "E ->F N",
            "N ->+ F", "N ->- F", "N ->~",
            "F ->G Q",
            "Q ->* G", "Q ->/ G", "Q ->~",
            "G ->( R )","G ->L","G ->num","G ->true","G ->false","G ->id"};

    //用于分析的栈
    public Stack<String> analyzeStack = new Stack<>();
    //用于保存节点在树中的深度的栈
    public Stack<node> aStack = new Stack<>();

    //要识别的字符串
    public String strInput = "";

    //替换产生式或者匹配的动作
    public String action = "";

    int index = 0;    //指向当前指向输入串的位置
    int nodeIndex = 0;//保存树中有多少个结点
    int dep =1;       //保存当前树的深度

    public void setStrInput(String str){
        strInput = str;
    }

    public void printTree(){
        for (int i = 0; i< nodeIndex; i++){
            node n = nodes[i];
            String str = "";
            for (int j=0;j<n.depth;j++){
               str = str+"  ";
            }
            System.out.println(str + n.str);
        }
    }

    /**
     * 初始化操作，构造first和follow
     */
    public void Init() {
        //获取生成式
        for (String e : inputExpression) {
            String[] str = e.split("->");
            char c = str[0].charAt(0);
            ArrayList<String> list = expressionSet.containsKey(c) ? expressionSet.get(c) : new ArrayList<>();
            list.add(str[1]);
            expressionSet.put(c, list);
        }
        //构造非终结符的first集
        for (Character c : VnSet)
            getFirst(c.toString(),1);
        //构造开始符的follow集
        getFollow(S);
        //构造非终结符的follow集
        for (char c : VnSet)
            getFollow(c);
    }

    /**
     * 先求非终结符，再求终结符
     */
    public void getNvNt() {
        for (String e : inputExpression)
            VnSet.add(e.split("->")[0].charAt(0));
        for (String e : inputExpression)
            for (String s : e.split("->")[1].split(" "))
                if (!VnSet.contains(s.charAt(0)))
                    VtSet.add(s);
    }

    /**
     * 获取终结符和非终结符的first集
     * @param c 输入的终结符或者非终结符
     * @param n 为了重载使用
     */
    public void getFirst(String c, int n) {
        if (firstSet.containsKey(c))
            return;
        HashSet<String> set = new HashSet<>();
        // 若c为终结符 直接添加
        if (VtSet.contains(c)) {
            set.add(c);
            firstSet.put(c, set);
            return;
        }
        // c为非终结符 处理其每条产生式
        for (String s : expressionSet.get(c.charAt(0))) {
            if ("~".equals(c)) {
                set.add("~");
            } else {
                for (String cur : s.split(" ")) {
                    if (!firstSet.containsKey(cur))
                        getFirst(cur,1);
                    HashSet<String> curFirst = firstSet.get(cur);
                    set.addAll(curFirst);
                    if (!curFirst.contains("~"))
                        break;
                }
            }
        }
        firstSet.put(c, set);
    }

    /**
     * 获取字符串的first集
     * @param str 字符串
     */
    public void getFirst(String str) {
        if (firstSetX.containsKey(str))
            return;
        HashSet<String> set = new HashSet<>();
        // 从左往右扫描该式
        int i = 0;
        String[] s = str.split(" ");
        while (i < s.length) {
            String cur = s[i];
            if (!firstSet.containsKey(cur))
                getFirst(cur,1);
            HashSet<String> rightSet = firstSet.get(cur);
            // 将其非空 first集加入左部
            set.addAll(rightSet);
            // 若包含空串 处理下一个符号
            if (rightSet.contains("~"))
                i++;
            else
                break;
            // 若到了尾部 即所有符号的first集都包含空串 把空串加入fisrt集
            if (i == s.length) {
                set.add("~");
            }
        }
        firstSetX.put(str, set);
    }

    /**
     * 获取非终结符c的follow集
     * @param c 非终结符
     */
    public void getFollow(Character c) {
        ArrayList<String> list = expressionSet.get(c);
        HashSet<String> leftFollowSet = followSet.containsKey(c) ? followSet.get(c) : new HashSet<>();
        //如果是开始符 添加 $
        if (c == S)
            leftFollowSet.add("$");
        //查找输入的所有产生式，添加c的后跟终结符
        for (char ch : VnSet) {
            for (String str : expressionSet.get(ch)) {
                String[] s = str.split(" ");
                for (int i = 0; i < s.length; i++) {
                    if (c.toString().equals(s[i]) && i + 1 < s.length && VtSet.contains(s[i + 1]))
                        leftFollowSet.add(s[i + 1]);
                }
            }
        }
        followSet.put(c, leftFollowSet);

        //反向扫描处理c的每一条产生式
        for (String str : list) {
            String[] s = str.split(" ");
            int i = s.length - 1;
            while (i >= 0) {
                String cur = s[i];
                //只处理非终结符  I->i(E)SL
                if (VnSet.contains(cur.charAt(0))) {
                    // 都按 A->αBβ  形式处理
                    //1.若β不存在   followA 加入 followB
                    //2.若β存在，把β的非空first集  加入followB
                    //3.若β存在  且first(β)包含空串  followA 加入 followB
                    //String right = str.substring(i + 1);
                    HashSet<String> rightFirstSet;
                    if(!followSet.containsKey(cur.charAt(0)))
                        getFollow(cur.charAt(0));
                    HashSet<String> curFollowSet = followSet.get(cur.charAt(0));
                    //先找出first(β),将非空的加入followB
                    if (i == s.length-1) {
                        curFollowSet.addAll(leftFollowSet);
                    } else {
                        if (i == s.length-2) {
                            if (!firstSet.containsKey(s[i+1]))
                                getFirst(s[i+1],1);
                            rightFirstSet = firstSet.get(s[i+1]);
                        } else {
                            String right ="";
                            for (int j=i+1;j<s.length;j++){
                                right = right + s[j] + " ";
                            }

                            if (!firstSetX.containsKey(right))
                                getFirst(right);
                            rightFirstSet = firstSetX.get(right);
                        }
                        for (String var : rightFirstSet)
                            if (!var.equals("~"))
                                curFollowSet.add(var);
                        // 若first(β)包含空串,将followA加入followB
                        if (rightFirstSet.contains("~"))
                            curFollowSet.addAll(leftFollowSet);
                    }
                    followSet.put(cur.charAt(0), curFollowSet);
                }
                i--;
            }
        }
    }

    /**
     * 创建ll1分析表
     */
    public void createTable() {
        Object[] VtArray = VtSet.toArray();
        Object[] VnArray = VnSet.toArray();
        // 预测分析表初始化
        table = new String[VnArray.length + 1][VtArray.length + 1];
        table[0][0] = "Vn/Vt";
        //初始化首行首列
        for (int i = 0; i < VtArray.length; i++)
            table[0][i + 1] = (VtArray[i].toString().equals("~")) ? "$" : VtArray[i].toString();
        for (int i = 0; i < VnArray.length; i++)
            table[i + 1][0] = VnArray[i] + "";
        //全部置error
        for (int i = 0; i < VnArray.length; i++)
            for (int j = 0; j < VtArray.length; j++)
                table[i + 1][j + 1] = "error";

        //插入生成式
        for (char A : VnSet) {
            for (String s : expressionSet.get(A)) {
                if (!firstSetX.containsKey(s))
                    getFirst(s);
                HashSet<String> set = firstSetX.get(s);
                for (String a : set)
                    insert(A, a, s);
                if (set.contains("~")) {
                    HashSet<String> setFollow = followSet.get(A);
                    if (setFollow.contains("$"))
                        insert(A, "$", s);
                    for (String b : setFollow)
                        insert(A, b, s);
                }
            }
        }

    }

    /**
     * 进行ll分析的过程
     */
    public void analyze() {
        System.out.println("——————————————————————————————————————————————————————————————————LL分析过程————————————————————————————————————————————————————————————");
        System.out.println("               Stack                                                                      Input                                                               Action");
        analyzeStack.push("$");
        aStack.push(new node(0,"$"));
        analyzeStack.push(S.toString());
        aStack.push(new node(0,S.toString()));
        display();
        String X = analyzeStack.peek();
        while (!X.equals("$")) {
            String st[] = strInput.split(" ");
            String a = st[index];//index指向当前输入终结符
            if (X.equals(a)) {
                action = "match " + analyzeStack.peek();
                analyzeStack.pop();
                node n = aStack.pop();
                nodes[nodeIndex] = n;
                nodeIndex++;
                index ++;
            } else if (VtSet.contains(X)) {
                System.out.println("Syntax error at word: "+st[index]);
                return;
            }
            else if (find(X, a).equals("error")) {
                System.out.println("Syntax error at word: "+st[index]);
                return;
            }
            else if (find(X, a).equals("~")) {
                analyzeStack.pop();
                node n = aStack.pop();
                nodes[nodeIndex] = n;
                nodeIndex++;
                action = X + "->~";
            } else {
                String str = find(X, a);
                if (str != "") {
                    action = X + "->" + str;
                    analyzeStack.pop();
                    node n = aStack.pop();
                    nodes[nodeIndex] = n;
                    nodeIndex++;
                    String[] strs = str.split(" ");
                    int len = strs.length;
                    for (int i = len - 1; i >= 0; i--) {
                        analyzeStack.push(strs[i]);
                        aStack.push(new node(dep,strs[i]));
                    }
                    dep ++;
                } else {
                    System.out.println("Syntax error at word: " + st[index]);
                    return;
                }
            }
            X = analyzeStack.peek();
            display();
        }
        System.out.println("成功匹配！");
    }

    /**
     * 查找分析表中
     * @param X 当前栈顶非终结符号
     * @param a 当前index指向的终结符号
     * @return 查找结果
     */
    public String find(String X, String a) {
        for (int i = 0; i < VnSet.size() + 1; i++) {
            if (table[i][0].equals(X))
                for (int j = 0; j < VtSet.size() + 1; j++) {
                    if (table[0][j].equals(a))
                        return table[i][j];
                }
        }
        return "";
    }

    /**
     * 向分析表中插入结果
     * @param X 终结符，也就是插入的行号
     * @param a 非终结符，也就是插入的列号
     * @param s 需要插入的生成式
     */
    public void insert(char X, String a, String s) {
        if (a.equals("~"))
            a = "$";
        for (int i = 0; i < VnSet.size() + 1; i++) {
            if (table[i][0].charAt(0) == X)
                for (int j = 0; j < VtSet.size() + 1; j++) {
                    if (table[0][j].equals(a)) {
                        table[i][j] = s;
                        return;
                    }
                }
        }
    }

    /**
     * 输出每一步的栈情况，剩余输入情况和采用的产生式或者匹配情况
     */
    public void display() {
        // 输出 LL1
        Stack<String> s = analyzeStack;
        System.out.printf("%40s", s);
        String[] st = strInput.split(" ");
        String str = "";
        for (int i = index;i < st.length;i++ ){
            str = str + st[i];
        }
        System.out.printf("%200s", str);
        System.out.printf("%30s", action);
        System.out.println();
    }

    public void outputFirst(){
        System.out.println("————————————————first集——————————————————————");
        for (Character c : VnSet) {
            HashSet<String> set = firstSet.get(c.toString());
            System.out.printf("%10s", c + "  ->   ");
            for (String var : set)
                System.out.print(var);
            System.out.println();
        }
    }

    public void outputFirstX(){
        System.out.println("——————————————firstX集————————————————");
        Set<String> setStr = firstSetX.keySet();
        for (String s : setStr) {
            HashSet<String> set = firstSetX.get(s);
            System.out.printf("%10s", s + "  ->   ");
            for (String var : set)
                System.out.print(var);
            System.out.println();
        }
    }

    public void outputFollow(){
        System.out.println("————————————————follow集————————————————");

        for (Character c : VnSet) {
            HashSet<String> set = followSet.get(c);
            System.out.print("Follow " + c + ":");
            for (String var : set)
                System.out.print(var);
            System.out.println();
        }
    }

    public void outputTable(){
        System.out.println("————————————————————————————————————————————————LL1预测分析表——————————————————————————————————————————————————————————————");

        for (int i = 0; i < VnSet.size() + 1; i++) {
            for (int j = 0; j < VtSet.size() + 1; j++) {
                System.out.printf("%20s", table[i][j] + " ");
            }
            System.out.println();
        }
    }

}
