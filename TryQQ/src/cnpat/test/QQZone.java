package cnpat.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.script.ScriptException;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import cnpat.util.RegFactory;

public class QQZone {
	public static String username = "846714904";
	public static String passwd = "z7726022";
	public static final String clientid = "53999199";
	public static final String appid = "15004501";
	public static Map<String, String> cookies;

	public static void main(String[] args) {
		try {
			new QQZone().doWork();
		} catch (IOException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(ScriptException e){
			e.printStackTrace();
		}
	}

	private void doWork() throws IOException, ScriptException, ParserException {
		cookies = new HashMap<String, String>();
		String vers = check();
		if (vers == null) {
			System.err.println("检测用户名失败");
			return;
		}
		System.err.println(vers);
		String verCode = RegFactory.catchGroup("'(!\\w+)'", vers).get(0);
		System.out.println(vers);
		System.out.println(verCode);
		// System.out.println("cookie "+cookies.size());
		// forCookie();
		// System.out.println(cookies.size());
		/*
		 * for (Entry<String, String> set : cookies.entrySet()) {
		 * System.out.println(set.getKey() + "  " + set.getValue()); }
		 */
		// cookies.put("ptui_loginuin", username);
		String succ = tryLoad(verCode);

		System.out.println(cookies.size());
		/*
		 * for (Entry<String, String> set : cookies.entrySet()) {
		 * System.out.println(set.getKey() + "  " + set.getValue()); }
		 */
		String ptsig = RegFactory.catchGroup("ptsig=([^']+)'", succ).get(0);
		String url = RegFactory.catchGroup("'(http[^']+)'", succ).get(0);
		System.out.println(succ);
		System.out.println(url);
		doGet(url, null, true,false);
		System.out.println(cookies.size());
		login1();
	}

	private void login1() throws IOException {
		String str="http://user.qzone.qq.com/846714904/main";
		String res=doGet(str, "http://user.qzone.qq.com/846714904/main", true, true);
		System.out.println(res);
	}

	private void parser(String ptsig, String url1) throws ParserException,
			IOException {
		String str = "http://user.qzone.qq.com/" + username + "?ptsig=" + ptsig;
		Parser parser = new Parser();
		URL url = new URL(str);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(60 * 1000);
		conn.addRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
		// conn.setInstanceFollowRedirects(false);

		conn.setRequestProperty("Referer", url1);

		conn.setRequestProperty("Cookie", getCookieStr());
		parser.setConnection(conn);
		NodeFilter filter = new HasAttributeFilter("data-clicklog", "nick");
		NodeList ns = parser.extractAllNodesThatMatch(filter);
		System.out.println("ns " + ns.size());
	}

	private String login(String ptsig, String url) throws IOException {
		String str = "http://user.qzone.qq.com/" + username + "?ptsig=" + ptsig;
		// String
		// refer="http://qzs.qq.com/qzone/v5/loginsucc.html?para=izone&ptsig=9IGD6fUyTucE2QRyrQ7wa0SO-MuQEBaXmnfpK2Epgpc_";
		return doGet(str, url, false,true);
	}

	private String doGetHttps(String str, String url1, boolean b)
			throws IOException {
		URL url = new URL(str);
		HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		conn.setConnectTimeout(60 * 1000);
		conn.addRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
		// conn.setInstanceFollowRedirects(false);
		conn.setRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Connection", "keep-alive");

		conn.setRequestProperty("Referer", url1);

		conn.setRequestProperty("Cookie", getCookieStr());

		putCookie(conn, false);
		InputStream in = conn.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = reader.readLine();
		String res = "";
		while (line != null) {
			res += line;
			line = reader.readLine();
		}
		in.close();
		return res;

	}

	private String doGetRe(String str, String refer) throws IOException {
		URL url = new URL(str);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(60 * 1000);
		conn.addRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
		// conn.setInstanceFollowRedirects(false);
		if (refer != null) {
			conn.setRequestProperty("Referer", refer);
		}
		if (cookies.size() > 0) {
			conn.setRequestProperty("Cookie", getCookieStr());
		}
		conn.setInstanceFollowRedirects(false);
		putCookie(conn, false);
		InputStream in = conn.getInputStream();
		putCookie(conn, false);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = reader.readLine();
		String res = "";
		while (line != null) {
			res += line;
			line = reader.readLine();
		}
		in.close();
		return res;

	}

	private void forCookie() throws IOException {
		String str = "http://ptlogin2.qq.com/ptqrshow?appid=549000912&e=2&l=M&s=3&d=72&v=4&t=0.3658298253662867";
		doGet(str, null, false,true);

	}

	private String tryLoad(String verCode) throws ScriptException, IOException {
		String mds = Second.getMd5(verCode);
		System.out.println("md5  " + mds);

		String str = "http://ptlogin2.qq.com/login?u="
				+ username
				+ "&p="
				+ mds
				+ "&verifycode="
				+ verCode
				+ "&aid=15004501&u1=http%3A%2F%2Fcnc.qzs.qq.com%2Fac%2Fqzone%2Flogin%2Fsucc.html&h=1&ptredirect=0&ptlang=2052&daid=5&from_ui=1&dumy=&fp=loginerroralert&action=3-8-25075&mibao_css=&t=1&g=1&js_type=0&js_ver=10080&login_sig=HoHAbuArb-b-7xtgPqJ3kpxbU7K5qqjXLJp9yxN12MvWYS0XC*5F5npv3W5yXeid&pt_uistyle=0";

		/*
		 * String str = "http://ptlogin2.qq.com/login?u=846714904&p=" + mds +
		 * "&verifycode=" + verCode +
		 * "&aid=549000912&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&h=1&ptredirect=0&ptlang=2052&daid=5&from_ui=1&dumy=&low_login_enable=0&regmaster=&fp=loginerroralert&action=2-20-1400824741182&mibao_css=&t=1&g=1&js_ver=10080&js_type=1&login_sig=NZ2rmqEJeg-GeGIp6x7uEVDzAL-6KxU0y9yOy2JgIbmSzjxnZL5CABllvyvTcMfu&pt_uistyle=12&pt_rsa=0&pt_qzone_sig=1&pt_3rd_aid="
		 */;
		String refer = "http://ui.ptlogin2.qq.com/cgi-bin/login?daid=5&hide_title_bar=1&no_verifyimg=1&link_target=blank&appid=15004501&target=self&f_url=http%3A%2F%2Fcnc.qzs.qq.com%2Fac%2Fqzone%2Flogin%2Ferror.html&s_url=http%3A%2F%2Fcnc.qzs.qq.com%2Fac%2Fqzone%2Flogin%2Fsucc.html";
		return doGet(str, null, false,true);
	}

	private String check() {
		// String str =
		// "http://check.ptlogin2.qq.com/check?regmaster=&uin=846714904&appid=549000912&js_ver=10080&js_type=1&login_sig=FEycNGZxOG4QoLJhGfbZSpHEvciVuiFYIT9rlaBiajSj4tJJNIlZkxHs9Ji*kwg*&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&r=0.647173381441928";
		String str = "http://check.ptlogin2.qq.com/check?regmaster=&uin=846714904&appid=549000912&js_ver=10080&js_type=1&login_sig=NZ2rmqEJeg-GeGIp6x7uEVDzAL-6KxU0y9yOy2JgIbmSzjxnZL5CABllvyvTcMfu&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&r=0.9505793475519453";
		/*
		 * String str = "http://check.ptlogin2.qq.com/check?regmaster=&uin=" +
		 * username + "&appid=" + appid +
		 * "&js_ver=10080&js_type=1&login_sig=sjnqWQkYLpGD-1w3UsAX3Eb17Zx-JmV6vlDmKJIqVp13ltV6vjygjS05XCRXKocP&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&r=0.36510794796049595"
		 * ;
		 */try {
			String res = doGet(str, null, false,true);
			return res;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private String doGet(String str, String refer, boolean printHead,boolean InstanceFollow)
			throws IOException {
		URL url = new URL(str);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(60 * 1000);
		conn.addRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
		conn.setInstanceFollowRedirects(InstanceFollow);
		conn.setRequestProperty("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		conn.setRequestProperty("Connection", "keep-alive");
		if (refer != null) {
			conn.setRequestProperty("Referer", refer);
		}
		if (cookies.size() > 0) {
			conn.setRequestProperty("Cookie", getCookieStr());
		}
		putCookie(conn, printHead);
		InputStream in = conn.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = reader.readLine();
		String res = "";
		while (line != null) {
			res += line;
			line = reader.readLine();
		}
		in.close();
		return res;

	}

	private String getCookieStr() {
		StringBuilder builder = new StringBuilder();
		for (Entry<String, String> set : cookies.entrySet()) {
			builder.append(set.getKey() + "=" + set.getValue() + ";");
		}
		return builder.toString();
	}

	private void putCookie(HttpURLConnection conn, boolean b) {
		int i = 1;
		while (conn.getHeaderFieldKey(i) != null) {
			if (b) {
				System.out.println("responsHead  " + conn.getHeaderFieldKey(i)
						+ " " + conn.getHeaderField(i));
			}
			if (conn.getHeaderFieldKey(i).equals("Set-Cookie")) {

				String line = conn.getHeaderField(i);

				String[] ss = line.split(";");
				String[] lss = ss[0].split("=");
				if (lss.length == 2) {
					cookies.put(lss[0].trim(), lss[1].trim());
				}

			}
			i++;
		}

	}

}
