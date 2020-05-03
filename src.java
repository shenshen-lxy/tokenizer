mport java.awt.BorderLayout;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.hamcrest.core.SubstringMatcher;

import java.io.*; 
import java.util.*; 

public class Tokenizer extends JFrame {
    public static void main(String[] args) {
    	Tokenizer tokenizer = new Tokenizer();
    }
    public Tokenizer() {
        super("简单的词法分析器");
        
        jlAdd = new JLabel("输入文件路径：");
        btAdd = new JButton("显示源码"); //按下按钮将文件内容添加到程序
       
        btStart = new JButton("开始");//按下按钮，开始词法分析
        btClear = new JButton("清空");//按下按钮，所有区域
        tfFile = new JTextField(30);//填写文件路径
        //默认路径
        tfFile.setText("E:\\大学\\大三下\\编译原理\\实验\\实验一\\src1.txt");
        taShowFile = new JTextArea();//源码显示
        taShowFile.setLineWrap(true);        //激活自动换行功能 
        taShowFile.setFont(new Font("",Font.BOLD,15));
        taShowWord = new JTextArea();//单词符号表显示
        taShowWord.setLineWrap(true);        //激活自动换行功能 
        taShowWord.setFont(new Font("",Font.BOLD,15));
        
        taShowList = new JTextArea();//标识符列表显示
        taShowList.setLineWrap(true);        //激活自动换行功能 
        taShowList.setFont(new Font("",Font.BOLD,15));
                
        //添加监听器
        btAdd.addActionListener(new Listener());
        btAdd.setActionCommand("btAdd");
        btStart.addActionListener(new Listener());
        btStart.setActionCommand("btStart");
        btClear.addActionListener(new Listener());
        btClear.setActionCommand("btClear");
        
        //以下为布局
        JPanel top = new JPanel(new FlowLayout());
        top.add(jlAdd);
        top.add(tfFile);
        top.add(btAdd);
        this.add(top, BorderLayout.NORTH);//最上面一行是文字提示+输入框+添加按钮
        
        JPanel p_all=new JPanel(new GridLayout(1,2,5,5)); //一分为二(源码+其他)
        //其他再一分为二(单词符号表+标识符列表)
        JPanel p=new JPanel(new GridLayout(1,2,3,3));         
        final JScrollPane sp1 = new JScrollPane();
        sp1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp1.setViewportView(this.taShowFile);
//        taShowFile.setEditable(false);
        taShowFile.setText("显示源码");
        p_all.add(sp1);
        
        final JScrollPane sp2 = new JScrollPane();
        sp2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp2.setViewportView(this.taShowWord);
        taShowWord.setEditable(false);
        taShowWord.setText("种类表："+'\n'+"0：if\n1：then\n2：else\n3：while\n4：do\n5：begin\n6：end\n7：and\n8：or\n9：not\n10：;\n11：#~\n12：+\n13：*\n14：:=\n15：(\n16：)\n17：关系运算符\n18：变量(标识符)\n19：常量\n20：表达式\n21：赋值语句\n");
        p.add(sp2);
        
        final JScrollPane sp3 = new JScrollPane();
        sp3.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        sp3.setViewportView(this.taShowList);
        taShowList.setEditable(false);
        taShowList.setText("显示程序单词符号");
        p.add(sp3);
        
        p_all.add(p);
        
        this.add(p_all);
        
        JPanel bottom = new JPanel(new FlowLayout());
        bottom.add(btStart);
        bottom.add(btClear);
        this.add(bottom,BorderLayout.SOUTH);//下面为两个按钮
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(650, 400);
        this.setLocation(100, 100);
        this.setVisible(true);
        this.setResizable(false);
        
        match = new String[]{"if","then","else","while","do","begin","end","and","or","not",";","#~","+","*",":=","(",")",">=:",">:","<=:","<:","<>","=:"};
    }
    
    public class Listener implements ActionListener{ 
		public void actionPerformed(ActionEvent e) {
          //按钮：添加文件——将文件加载到程序中
			if(e.getActionCommand()=="btAdd") {
				s="";
				File fRead = new File(tfFile.getText());
	            if(tfFile.getText()!="")
	            {
	            	try{      
	            		Reader in =new FileReader(fRead);
	            		BufferedReader bufferRead =new BufferedReader(in);
	            		String str = "";
	            		while((str=bufferRead.readLine())!=null) s+='\n'+str;	            		              bufferRead.close(); 
	            		}catch(Exception ex) {
	            			System.out.println(ex.toString());
	            		}
	            	taShowFile.setText(s);
	            }
			}//if end
			else if(e.getActionCommand()=="btStart") { //按钮：开始词法分析		
				
				s = taShowFile.getText();
			
				set = new HashSet(); //用来装显示单词符号表的set
            	//将s字符串的内容进行预处理
				if(s.contains(";")) {
					String item = itemString(10, ";");
	    			set.add(item);
				}
            	Scanner scanner = new Scanner(s);
            	scanner.useDelimiter("\\s*;\\s*|\\s*\n\\s*|\\s* \\s*");
            	List = new ArrayList(); //用来装处理的字符串
            	while(scanner.hasNext()) { //默认将'\n'或者空格作为分隔符
            		try {
            			String str = scanner.next();//将名字一个一个提取出来
            			List.add(str);//将名字加到容器中
            		}catch(Exception ex) {
            			System.out.print(ex.toString());
            		}
            	}//大小为List.size();
//            	System.out.print("初始"+List);
            	
//            	对List里面的字符串进行处理
            	while(List.size()>0) { //还有str，继续处理
            		String str = (String) List.get(0);
            		if("~".equals(str)) { //若源码中有括号，中间生成物要剔除
            			List.remove(0);
            			continue;
            		}
            		if(matchKey(str))  continue;//匹配0~17
            		if(matchOther(str, ":=")) continue;
            		if(str.contains("(")) {
            			if(str.contains(")")) {
            				//左括号<右括号
            				String copyString = str;
            				while(copyString.lastIndexOf("(") > copyString.indexOf(")")) {
            					copyString = copyString.substring(0, copyString.lastIndexOf("("));
            				}
            				
            				String subStr = copyString.substring(copyString.lastIndexOf("(")+1, copyString.indexOf(")"));
            				if(!("".equals(subStr))) { //括号里面还有东西
            					List.add(subStr); //括号取出
                				String tempString = str.substring(0, copyString.lastIndexOf("("))+"~"+str.substring(copyString.indexOf(")")+1, str.length());
                				List.add(tempString);
                				System.out.println(List);
            				}
            				set.add(itemString(15,"("));
            				set.add(itemString(16,")"));
            				List.remove(0);
            				
            			}
            			else {
            				String item = itemString(-1, str+"缺少')'");
            				set.add(item);
            				List.add(str.substring(0, str.lastIndexOf("(")) + str.substring(str.lastIndexOf("(")+1, str.length()));
            				List.remove(0);
            			}
            			continue;
            		}
            		if(matchOther(str, ">=:")) continue;
            		if(matchOther(str, ">:")) continue;
            		if(matchOther(str, "<=:")) continue;
            		if(matchOther(str, "<:")) continue;
            		if(matchOther(str, "<>")) continue;
            		if(matchOther(str, "=:")) continue;
            		
            		if(matchOther(str, "+")) continue;
            		if(matchOther(str, "*")) continue;
            		
            		if(matchOther(str, null)) continue;
            		
            		String item = itemString(18, str);
        			set.add(item);
        			List.remove(0);
            	}
            	System.out.println(List);
            	System.out.println(set);
            	String showString="";
            	Iterator it = set.iterator();
		        while (it.hasNext()) {
		            showString += '\n'+(String)it.next();
		        }
		        taShowList.setText(showString);
            	
			}
			else { //btClear  //按钮：清空
				taShowFile.setText("显示源码");
				taShowList.setText("显示程序单词符号");
				List.clear();
				set.clear();
				s="";
			}
		}
    	
    }//Listener end
    
    public String itemString(int i,String str) {
    	return "("+i+","+str+")";
    }
    
    public boolean matchKey(String str) { //匹配0~17种别
    	//length:23. 0~16一一对应     17代表六种关系运算符
    	
    	for(int i=0;i<match.length;i++) {
    		if(match[i].equals(str)) {
    			if(i>=17) i=17;
    			String item = itemString(i, str);
    			set.add(item);
    			List.remove(0); 
    			return true;
    		}
    	}
		return false;
	}
    
    public boolean matchOther(String str,String c) {  //匹配是不是运算符(:=、+、*、rop)/常量
    	if(c!=null) { //匹配运算符
    		int i;
    		if(str.contains(c)) {
    			if(str.equals(c)) { //等于自身
    				if(c.equals(":=")) i=14;
            		else if(c.equals("+")) i=12;
            		else if(c.equals("*")) i=13;
            		else i=17;
    			}else {
    				if(c.equals(":=")) i=21;
    				else i=20;
    				List.add(str.substring(0, str.indexOf(c.charAt(0))));
    				List.add(str.substring(str.indexOf(c.charAt(0))+c.length(),str.length()));
    				List.add(c);
    			}
    			String item = itemString(i, str);
    			set.add(item);
    			List.remove(0);
    			return true;
    		}
    		return false;
    	}
    	else {  //看是否是常数
    		for(int i=0;i<str.length();i++) {
    			if (!Character.isDigit(str.charAt(i))) {
    				return false;
    			}
    		}
    		String item = itemString(19, str);
			set.add(item);
			List.remove(0);
			return true;
    	}
    }
    
    public	ArrayList List;
    public 	Set set;
    public 	String s;//装载源代码
    public	String match[];//种类表
    public	JLabel jlAdd;
    public  JButton btAdd;
    public  JButton btStart;
    public  JButton btClear;
    public  JTextField tfFile;
    public  JTextArea taShowFile;
    public  JTextArea taShowWord;
    public  JTextArea taShowList;
}