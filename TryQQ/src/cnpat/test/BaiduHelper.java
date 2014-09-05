package cnpat.test;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import cnpat.util.FileFactory;

public class BaiduHelper {

	public static String searchZhidao(String string) throws IOException,
			ParserException {

		String mainW = string;
		if (mainW.length() > 15) {
			mainW = getMainWord(string);

		}
		  System.out.println("正在检索关键字  "+mainW);
		/*String str = "http://www.baidu.com/s?ie=utf-8&mod=0&isid=D46D119F9B646875&pstg=0&wd="
				+ URLEncoder.encode(mainW.trim(), "utf-8")
				+ "&rsv_bp=0&tn=baidu&rsv_spt=3&ie=utf-8&rsv_sid=6013_6156_5228_1445_5223_6583_6505_6477_4759_6017_6461_6428_6456_6500_6453_6441_6632_6530_6450_6375&f4s=1&csor=2&_cr1=15890";*/
		String str = "http://zhidao.baidu.com/search?lm=0&rn=10&pn=0&fr=search&ie=gbk&word="+URLEncoder.encode(mainW.trim(), "utf-8");
		//System.out.println("连接  "+str);
		Parser parser = new Parser(str);
		NodeFilter filter = new HasAttributeFilter("class", "dt mb-4 line");
		NodeFilter filter1 = new HasAttributeFilter("class", "dd answer");
		NodeFilter OR=new OrFilter(filter1, filter);
		NodeList ls = parser.extractAllNodesThatMatch(OR);
		/*
		 * Node n=ls.elementAt(0); n.getChildren();
		 */

		// System.out.println(ls.size());
		String res = "";
		for (int i = 0; i < ls.size(); i++) {
			Node inn = ls.elementAt(i);
			NodeList chs = inn.getChildren();
			
			String cn=inn.toPlainTextString().trim();
			//System.out.println(cn);
			//cn=cn.replaceAll("[^\u4E00-\u9FA5^,^。^，]+", "");
			if (cn.startsWith("答：")) {
				cn=cn.replaceAll("答：","");
			}
			cn=cn.replaceAll("[^\u4E00-\u9FA5^,^。^，]+", "");
			res += cn+"\\r";
			if (res.length()>100) {
				if (i>2) {
					break;
				}
				
			}
			// System.out.println(inn.toPlainTextString().trim().replaceAll("&nbsp;|-&nbsp;",
			// ""));
			// System.out.println(inn.getNextSibling().toPlainTextString());
			// System.out.println("-------------");

		}
		/*
		 * String res = doGet(str); System.out.println(res);
		 */
	
		return res.trim();
	}
	public static String searchWeb(String string) throws IOException,
	ParserException {
		String mainW = string;
		if (mainW.length() > 15) {
			mainW = getMainWord(string);

		}
		String str = "http://www.baidu.com/s?ie=utf-8&mod=0&isid=D46D119F9B646875&pstg=0&wd="
				+ URLEncoder.encode(mainW.trim(), "utf-8")
				+ "&rsv_bp=0&tn=baidu&rsv_spt=3&ie=utf-8&rsv_sid=6013_6156_5228_1445_5223_6583_6505_6477_4759_6017_6461_6428_6456_6500_6453_6441_6632_6530_6450_6375&f4s=1&csor=2&_cr1=15890";
		Parser parser = new Parser(str);
		NodeFilter filter = new HasAttributeFilter("class", "c-abstract");
		NodeFilter hasP = new HasParentFilter(filter);
		NodeFilter tag = new TagNameFilter("H3");
		NodeFilter and = new AndFilter(filter, tag);
		NodeList ls = parser.extractAllNodesThatMatch(filter);
		/*
		 * Node n=ls.elementAt(0); n.getChildren();
		 */

		// System.out.println(ls.size());
		String res = "";
		for (int i = 0; i < ls.size(); i++) {
			Node inn = ls.elementAt(i);
			NodeList chs = inn.getChildren();
			if (i>2) {
				break;
			}
			String cn=inn.toPlainTextString().trim();
			//cn=cn.replaceAll("[^\u4E00-\u9FA5^,^。^，]+", "");
			/*if (cn.startsWith("答：")) {
				cn=cn.replaceAll("答：","");
			}*/
			System.out.println(cn);
			System.out.println(cn.startsWith("答："));
			res += cn+"\\r";

			// System.out.println(inn.toPlainTextString().trim().replaceAll("&nbsp;|-&nbsp;",
			// ""));
			// System.out.println(inn.getNextSibling().toPlainTextString());
			// System.out.println("-------------");

		}
		/*
		 * String res = doGet(str); System.out.println(res);
		 */
		return res.trim();
		
		
	}
	public static void main(String[] args) throws ParserException, IOException {
		
		String ss=searchZhidao("咋回事");
		//String ss="asd\\rasd";
		System.out.println(ss);
	}

	private static String getMainWord(String string) throws IOException {
		StringReader input = new StringReader(string);

		List<String> ls = FileFactory.readByLine("stop.txt");

		IKSegmenter seg = new IKSegmenter(input, true);
		Lexeme l = null;
		Map<String, Integer> maps = new HashMap<String, Integer>();
		while ((l = seg.next()) != null) {
			String w = l.getLexemeText();
			if (ls.contains(w)) {
				continue;
			}
			if (maps.containsKey(w)) {
				maps.put(w, maps.get(w) + 1);
			} else {
				maps.put(w, 1);
			}
		}
		List<Map.Entry<String, Integer>> infoIds =
			    new ArrayList<Map.Entry<String, Integer>>(maps.entrySet());
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {
				return o2.getValue()-o1.getValue();
				
			}
		});
		String res="";
		int i=0;
		for (Entry<String, Integer> entry : infoIds) {
			res+=entry.getKey()+" ";
			i++;
			if (i>5) {
				break;
			}
		}
		return res;
	}
}
