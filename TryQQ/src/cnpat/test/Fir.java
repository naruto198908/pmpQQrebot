package cnpat.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.sf.json.JSONObject;
import cnpat.util.RegFactory;

public class Fir {
	public static final String username="1810287483";
	public static final String passwd="naruto.19890810";
	public static String vfqq;
	public static String allcookie="";
	public static String allcookie1="";
	public static void main(String[] args) throws ScriptException, NoSuchAlgorithmException, IOException {
		
		doWork();
		
	}

	private static void testMain() throws IOException {
		URL http=new URL("https://ssl.captcha.qq.com/getimage?aid=501004106&r=0.18118785340533283&uin=846714904");
		HttpURLConnection conn = (HttpURLConnection) http.openConnection();
		int i = 1;
		String res="";
		System.out.println("getP_skey-------------");
		while (conn.getHeaderFieldKey(i) != null) {
			if (conn.getHeaderFieldKey(i).equals("Set-Cookie")) {
				res+=conn.getHeaderField(i);
				
			}
			System.out.println(conn.getHeaderFieldKey(i)+"@@@"+conn.getHeaderField(i));
			i++;
		}
		allcookie+=res;
	}

	private static void doWork() throws ScriptException,
			NoSuchAlgorithmException, IOException {
		//得到验证码和cookie
		String res = getVerCodeAndCookie();
		String[] ress=res.split("@@@");
		String code = ress[0];
		String cookie = ress[1];
		//运行javascript引擎得到加密后的密码
		String  mdsPasswd=getMd5Passwd(code);

		res = tryLogin(mdsPasswd, cookie, code);
		ress=res.split("@@@");
		 String url=ress[0];
		cookie = ress[1]+cookie;
		//System.out.println(url);
		//System.out.println(cookie);
		String respons=Login(url,cookie);
		testMain();
		doILike(respons);
	}

	private static void doILike(String respons) throws IOException, ScriptException {
		String[] ss=respons.split("@@@");
		System.out.println(ss[0]);
		JSONObject json=JSONObject.fromObject(ss[0]);
		//System.out.println(respons);
		//System.out.println(json.toString());
		JSONObject vf= JSONObject.fromObject(json.get("result"));
		//String hash="";
		JSONObject ob=new JSONObject();
		ob.put("vfwebqq", vf.get("vfwebqq"));
		Object hsh=getHash(vfqq);
		//System.out.println("-----------------------");
		//System.out.println(vfqq);
		//System.out.println(ss[1]);
		//System.out.println("hash  "+hsh.toString());
		ob.put("hash", hsh);
		//ob.put("h", "hello");
		//String all="r="+ob.toString();
		String all="r="+ob.toString();
		//System.out.println(allcookie);
		
		String post=URLEncoder.encode(all,"UTF-8");
		URL http=new URL("http://s.web2.qq.com/api/get_user_friends2");
		HttpURLConnection conn = (HttpURLConnection) http.openConnection();
		//conn.setInstanceFollowRedirects(false);
		conn.addRequestProperty("Pragma", "no-cache");
		conn.addRequestProperty("Cache-Control", "no-cache");
		conn.addRequestProperty("Connection", "keep-alive");
		conn.addRequestProperty("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
		allcookie=deal(allcookie);
		conn.addRequestProperty("Cookie", allcookie);
		//System.out.println(allcookie);
		conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.write(post);
		out.close();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String line = reader.readLine();
		//System.out.println(conn.getResponseCode());
		//System.out.println(ss[1]);
		System.out.println(line);
	}

	private static String deal(String allcookie2) {
		String[] ss=allcookie2.split(";");
		System.out.println("-----------------");
		Map<String, String> map=new HashMap<String, String>();
 		for (String line : ss) {
			line=line.trim();
			
			if (line.endsWith("=")||Pattern.matches("^[A-QS-Z].*", line)) {
				
			}
			else {
				String[] rs=line.split("=");
				if (map.containsKey(rs[0])) {
					if (!map.get(rs[0]).equals(rs[1])) {
						System.out.println(line+"++++++++++++++++++"+map.get(rs[0]));
					}
				}
				else {
					map.put(rs[0], rs[1]);
				}
				//System.out.println(line);
			}
			
		}
 		map.put("province", "BJ");
 		map.put("ptui_loginuin", username);
 		map.put("o_cookie", username);
 		map.put("pgv_info", "ssid=s1847710161");
 		map.put("pgv_pvid", "480605382");
 		StringBuilder builder=new StringBuilder();
 		for (Entry<String, String> set : map.entrySet()) {
			System.out.println(set.getKey()+"@@@"+set.getValue());
			builder.append(set.getKey()+"="+set.getValue()+";");
		}
		
		return builder.toString();
	}

	private static Object getHash(String vf) throws FileNotFoundException, ScriptException {
		//vf="90cda54e46fdaf30014a431dc0e1e71bc7e2dd58552c2035aa711e0f5db44425";
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine en = manager.getEngineByName("javascript");
		en.eval(new FileReader(new File("hash.js")));
		Object t = en.eval("u("+username+",\""+vf+"\")");
		
		return t;
	}

	private static String getMd5Passwd(String code) throws FileNotFoundException, ScriptException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine en = manager.getEngineByName("javascript");
		en.eval(new FileReader(new File("myqq.js")));
//		Object t = en.eval("qqhash(604406136,\"z7726022?\",\"" + code + "\")");
		Object t = en.eval("qqhash("+username+",\""+passwd+"\",\""+code+"\")");
		//vfqq=(String) en.eval("uin2hex("+username+")");
		String uin = t.toString();
		return uin;
	}

	private static String getP_skey(String url, String cookie) throws IOException {
		
		URL http=new URL(url);
		HttpURLConnection conn = (HttpURLConnection) http.openConnection();
		conn.setRequestMethod("GET");
		//System.out.println("chong ding xiang");
		//System.out.println(conn.getInstanceFollowRedirects() );
		conn.setInstanceFollowRedirects(false);
		//conn.addRequestProperty("Referer", "http://w.qq.com/proxy.html?login2qq=1&webqq_type=10");
		conn.addRequestProperty("Connection", "keep-alive");
		conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
		//conn.addRequestProperty("Cookie", cookie);
		
		int i = 1;
		String res="";
		System.out.println("getP_skey-------------");
		while (conn.getHeaderFieldKey(i) != null) {
			if (conn.getHeaderFieldKey(i).equals("Set-Cookie")) {
				res+=conn.getHeaderField(i);
				System.out.println(conn.getHeaderField(i));
			}
			
			i++;
		}
		allcookie+=res;
		return  res;
	}

	private static String Login(String url, String cookie) throws IOException {
		
		String cookie1=getP_skey(url,cookie);
		//System.exit(0);
		List<String> fs=RegFactory.catchGroup("ptwebqq=([\\d\\w]+);", cookie);
		vfqq=fs.get(0);
		//System.out.println(fs.get(0));
		URL http=new URL("http://d.web2.qq.com/channel/login2");
		HttpURLConnection conn = (HttpURLConnection) http.openConnection();
		conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
		conn.addRequestProperty("Origin", "http://d.web2.qq.com");
		conn.addRequestProperty("Referer", "http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2");
		conn.addRequestProperty("Cookie", cookie1);
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");
		PrintWriter out = new PrintWriter(conn.getOutputStream());
//		String data="{\"ptwebqq\":\""+fs.get(0)+"\",\"clientid\":54999199,\"psessionid\":\"\",\"status\":\"online\"}";
		String data="{\"ptwebqq\":\""+fs.get(0)+"\",\"passwd_sig\":\"\",\"clientid\":54999199,\"psessionid\":\"\",\"status\":\"online\"}";
		data=URLEncoder.encode(data,"utf-8");
		//System.out.println(data);
		out.write("r="+data);
		out.close();
		conn.connect();
		int i = 1;
		String res="";
		System.out.println("Login--------------");
		while (conn.getHeaderFieldKey(i) != null) {
			if (conn.getHeaderFieldKey(i).equals("Set-Cookie")) {
				//System.out.println(conn.getHeaderField(i));
				System.out.println(conn.getHeaderField(i));
			}
			
			i++;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String line = reader.readLine();
		//System.out.println("--------------");
		String all="";
		while (line!=null) {
			//System.out.println(line);
			all+=line;
			line = reader.readLine();
		}
		return all+"@@@"+cookie1+cookie;
	}

	private static String tryLogin(String passwd2, String cookie, String code)
			throws MalformedURLException {
		String url = "https://ssl.ptlogin2.qq.com/login?u="+username+"&p="
				+ passwd2
				+ "&verifycode="
				+ code
				+ "&webqq_type=10&remember_uin=1&login2qq=1&aid=501004106&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&h=1&ptredirect=0&ptlang=2052&daid=164&from_ui=1&pttype=1&dumy=&fp=loginerroralert&action=0-18-2642640&mibao_css=m_webqq&t=1&g=1&js_type=0&js_ver=10077&login_sig=FBbmEaXsgpQQSwQLocqP2mcwFFXqOoPE1uKvQXSghKltZj35e-Qt8zxN8eB58gKx";
		URL http = new URL(url);

		try {
			HttpURLConnection conn = (HttpURLConnection) http.openConnection();
			
			conn.addRequestProperty("Cookie", cookie);
			conn.addRequestProperty(
					"Referer",
					"https://ui.ptlogin2.qq.com/cgi-bin/login?daid=164&target=self&style=16&mibao_css=m_webqq&appid=501004106&enable_qlogin=0&no_verifyimg=1&s_url=http%3A%2F%2Fw.qq.com%2Fproxy.html&f_url=loginerroralert&strong_login=1&login_state=10&t=20131024001");
			int i = 1;
			String res="";
			System.out.println("tryLogin----------------");
			while (conn.getHeaderFieldKey(i) != null) {
				if (conn.getHeaderFieldKey(i).equals("Set-Cookie")) {
					res+=conn.getHeaderField(i);
					System.out.println(conn.getHeaderField(i));
				}
				
				i++;
			}
			allcookie+=res;
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line = reader.readLine();
			//System.out.println(line);
			List<String> ls = RegFactory.catchGroup("(http[^']+)'", line);
			return ls.get(0)+"@@@"+res;
		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}

	}

	private static String getVerCodeAndCookie() throws MalformedURLException {
//		String url = "https://ssl.ptlogin2.qq.com/check?uin="+username+"&appid=501004106&js_ver=10077&js_type=0&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html&r=0.6343437784869002";
		String url = "https://ssl.ptlogin2.qq.com/check?uin="+username+"&appid=501004106&js_ver=10077&js_type=0&login_sig=7n9jtR-tJV56B107OngnFTB5*fHXSXnM6iDEZpAp8Q7PTMtI2oChYcXcWZxfZY2n&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html&r=0.8178524345500925";
		URL http = new URL(url);
		String res = "";
		String res1 = "";
		try {
			HttpURLConnection conn = (HttpURLConnection) http.openConnection();

			int i = 1;
			System.out.println("getVerCodeAndCookie--------------------");
			while (conn.getHeaderFieldKey(i) != null) {
				if (conn.getHeaderFieldKey(i).equals("Set-Cookie")) {
					res1 += conn.getHeaderField(i);
					//System.out.println("getVerCodeAndCookie");
					 System.out.println(conn.getHeaderField(i));
				}
			

				i++;
			}
			allcookie+=res1;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line = reader.readLine();
			String[] ss = line.split(",");
			res = ss[1].replace("'", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res + "@@@" + res1;
	}


}
