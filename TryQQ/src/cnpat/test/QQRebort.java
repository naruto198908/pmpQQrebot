package cnpat.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.security.auth.login.LoginContext;
import javax.swing.text.AbstractDocument.BranchElement;

import org.htmlparser.util.ParserException;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.processors.JsonBeanProcessor;
import cnpat.test.bean.Question;
import cnpat.util.FileFactory;
import cnpat.util.RegFactory;

/**
 * QQ答题机器人
 * 
 * @author ThinkMan
 * 
 */
public class QQRebort {
	public static String username = "1810287483";// 2596258135
	public static String passwd = "这里填QQ密码";
	public static final String clientid = "53999199";
	protected static final String QunMsg = "这个群是干嘛的？";
	private static final String FriendMsg = "你好，嘎哈呢";
	public static Map<String, String> cookies;
	public static String glovfwebqq;
	public static String glopessionID;
	public static String sendContent = "你好，嘎哈呢";
	public static JSONObject QunInfo;
	public static JSONArray helpResponsQun;
	public static Long pmpGroupUin;
//	public static String pmpGroupId = "307210840";// 9月群
	 public static String pmpGroupId = "100740797";//测试群
	private Map<String, String> uinMap = new HashMap<String, String>();

	public static Random rd = new Random();

	public static void main(String[] args) throws Exception {

		new QQRebort().doWork();
		/**
		 * "[\"电饭锅\",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]""[\"电饭锅\",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]"
		 */
		// String x = "电饭锅";
		/*
		 * JSONArray a=new JSONArray(); a.add("1"); a.add("2"); a.add("3");
		 * JSONArray b=new JSONArray(); a.add("4"); a.add("5"); a.add("6");
		 * a.addAll(b); System.out.println(a);
		 */
	}

	class XinTiao extends Thread {
		// public String cookie;
		public String ptwebqq;

		public String psessionid;

		public boolean isStart = true;
		private DBHelper helper ;//数据库操作帮助类

		public void _start() {
			isStart = true;
		}

		public void _pause() {
			isStart = false;
		}

		public void run() {
			// 启动循环读取消息的心跳线程
			Poll2Thread pt = new Poll2Thread(this);
			pt.psessionid = psessionid;
			pt.ptwebqq = ptwebqq;
			pt.start();
			// System.out.println("--------------------");
			Map<String, String> map = new HashMap<String, String>();
			if (QunInfo.getInt("retcode") != 0) {
				System.out.println("获取群信息失败！");
			} else {
				JSONArray array = QunInfo.getJSONObject("result").getJSONArray("gnamelist");
				for (int i = 0; i < array.size(); i++) {
					JSONObject ino = array.getJSONObject(i);
					map.put(ino.getString("code"), ino.getString("name") + "@@" + ino.getString("gid"));
				}
			}
			DBHelper helper = new DBHelper();
			try {
				helper.init();
			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			}
			// 读取题目数据到内存
			List<Question> allQuestion = helper.queryAllQuestion();
			if (allQuestion == null || allQuestion.size() == 0) {
				System.out.println("无题目");
			}
			System.out.println("总题量:" + allQuestion.size());
			Random random = new Random();
			while (true) {
				try {
					if (!isStart || pmpGroupUin == null) {
						sleep(1000);
						continue;
					}
					// 1.发送题目
					int index = random.nextInt(allQuestion.size());
					Question q = allQuestion.get(index);
					System.out.println("==发送题目:" + q.getId() + "\n" + q.getId() + "." + q.getQuestion());
					String res = sendMsg2Qun(pmpGroupUin, "\n" + q.getId() + "." + q.getQuestion());
					System.out.println("==发题响应消息:" + res);
					// 2.等30秒，收群消息，统计
					sleep(50 * 1000);
					// 公布答案
					String analysis = ("\n" + q.getId() + ".    答案:" + q.getAnswer() + "      解析:\n" + q.getAnalysis())
							.replace("<br>", "\n");
					System.out.println("==发送答案:" + q.getId() + "\n" + analysis);
					res = sendMsg2Qun(pmpGroupUin, analysis);
					System.out.println("==发送答案响应消息:" + res);

					// 等3秒
					sleep(15 * 1000);

				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					// } catch (ParserException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		private String tiao() throws IOException {
			String str = "http://d.web2.qq.com/channel/poll2";
			URL url = new URL(str);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Referer", "http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2");
			conn.setRequestProperty("Cookie", getCookitStr());
			conn
					.addRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
			JSONObject ob = new JSONObject();
			ob.put("ptwebqq", ptwebqq);
			ob.put("clientid", Integer.valueOf(clientid));
			ob.put("psessionid", psessionid);
			ob.put("key", "");
			// System.out.println(ob.toString());
			String data = URLEncoder.encode(ob.toString(), "utf-8");
			PrintWriter out = new PrintWriter(conn.getOutputStream());
			out.write("r=" + data);
			out.flush();
			conn.connect();
			return getHtml(conn, out);
		}
	}

	public class Poll2Thread extends Thread {
		public String ptwebqq;

		public String psessionid;
		private XinTiao mainT;

		public Poll2Thread(XinTiao mainT) {
			this.mainT = mainT;
		}

		@Override
		public void run() {
			super.run();
			try {
				while (true) {
					System.out.println("启动心跳请求...");
					String line = tiao();
					System.out.println("心跳响应:" + line);
					// pmpGroupUin
					JSONObject ob = JSONObject.fromObject(line);
					if (ob.getString("retcode").equals("0")) {
						// System.out.println(line);
						JSONArray result = ob.getJSONArray("result");
						JSONObject temp = result.getJSONObject(0).getJSONObject("value");
						String flag = result.getJSONObject(0).getString("poll_type");

						if (!flag.equals("message")) {
							if (flag.equals("group_message")) {
								// 收到群消息
								// {"poll_type":"group_message","value":{"msg_id":10906,"from_uin":3361165941,"to_uin":1810287483,"msg_id2":119506,"msg_type":43,"reply_ip":176755999,"group_code":1130419226,"send_uin":1505218642,"seq":270,"time":1409716201,"info_seq":100740797,"content":[["font",{"size":11,"color":"ff0000","style":[0,0,0],"name":"Tahoma"}],"a "]}}
								System.out.println("收到群消息 " + result.getJSONObject(0));

								String gid = result.getJSONObject(0).getJSONObject("value").getString("from_uin");// 群uin
								String from_uin = result.getJSONObject(0).getJSONObject("value").getString("send_uin");// 发送者uin
								String group_code = result.getJSONObject(0).getJSONObject("value").getString(
										"group_code");// 群code
								String info_seq = result.getJSONObject(0).getJSONObject("value").getString("info_seq");// 群code
								String content = result.getJSONObject(0).getJSONObject("value").getJSONArray("content")
										.getString(1).trim();// 内容
								System.out.println(from_uin + " in " + group_code + " said:" + content);
								if (pmpGroupId.equals(info_seq)) {
									pmpGroupUin = Long.parseLong(gid);
									// String res1 =
									// sendMsg2Qun(Long.valueOf(gid), "get!");
									// System.out.println("回复群 " + " " +
									// "\r\nreponse:" + res1);
								}
								if ("start".equalsIgnoreCase(content)) {
									System.out.println("get继续发题命令");
									mainT._start();
								}
								if ("pause".equalsIgnoreCase(content)) {
									System.out.println("get暂停发题命令");
									mainT._pause();
								}
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private String tiao() throws IOException {
			String str = "http://d.web2.qq.com/channel/poll2";
			URL url = new URL(str);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Referer", "http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2");
			conn.setRequestProperty("Cookie", getCookitStr());
			conn
					.addRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
			JSONObject ob = new JSONObject();
			ob.put("ptwebqq", ptwebqq);
			ob.put("clientid", Integer.valueOf(clientid));
			ob.put("psessionid", psessionid);
			ob.put("key", "");
			// System.out.println(ob.toString());
			String data = URLEncoder.encode(ob.toString(), "utf-8");
			PrintWriter out = new PrintWriter(conn.getOutputStream());
			out.write("r=" + data);
			out.flush();
			conn.connect();
			return getHtml(conn, out);
		}

		Pattern pKeyword = Pattern.compile("#keyword:(\\S+).*");
		Pattern pITTO = Pattern.compile("#itto:(\\S+).*");
		Pattern pAnswer = Pattern.compile("\\s*(\\d+)\\s*\\.\\s*([a-zA-Z])*");

		// 回复消息处理线程
		private void doReply(String gUin, String uin, String content) {
			//
			if ("#start".equalsIgnoreCase(content)) {
				System.out.println("get继续发题命令");
				mainT._start();
			} else if ("#pause".equalsIgnoreCase(content)) {
				System.out.println("get暂停发题命令");
				mainT._pause();
			} else if (content.startsWith("#itto:")) {
				// 查询数据库，然后输出
			}
			if (content.startsWith("#keyword:")) {
				// 查询数据库，然后输出
				//如果没查到结果，推荐关键字
			} else if (content.startsWith("#我的成绩")) {
				// 一下是需要获取qq号的
				String realQQ = uinMap.get(uin);
				if (realQQ == null || realQQ.length() < 4) {
					realQQ = getRealQQ(uin);
				} 
				if (realQQ == null || realQQ.length() < 4) {
					System.out.println("==找不到真实QQ号！"+uin);
					return;
				}

			}else {
				String qId = null;
				String answer=  null;
				Matcher m = pAnswer.matcher(content);
				if(m.find()){
					  qId = m.group(1);
					  answer= m.group(2);
				}
				if(qId!=null&&answer!=null){
					// 一下是需要获取qq号的
					//题号必须是当前的题号，比对答题记录，是否对错，存入数据库。问题表更新，个人记录更新，错题库更新
					String realQQ = uinMap.get(uin);
					if (realQQ == null || realQQ.length() < 4) {
						realQQ = getRealQQ(uin);
					}
					if (realQQ == null || realQQ.length() < 4) {
						System.out.println("==找不到真实QQ号！"+uin);
						return;
					}
					
				}

			}
		}
	}

	/**
	 * 发送群消息格式 POST（http://d.web2.qq.com/channel/send_qun_msg2） group_uin=gid
	 * r:{"group_uin":885194668,"content":"[\"g\",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]"
	 * ,"face":180,"clientid":53999199,"msg_id":55040001,"psessionid":"8368046764001d636f6e6e7365727665725f77656271714031302e3133332e34312e38340000741a00001744026e040018d877326d0000000a40526b474f306c53544f6d00000028d8d73914e59e64d27ec7d823d89984ac7f34db9b1cd375927241fb78356d39d52a8dbd392f426777"
	 * }
	 * 
	 * 获取群详细信息（包括群列表） GET（http://s.web2.qq.com/api/get_group_info_ext2?gcode=
	 * 2882137065&vfwebqq=8
	 * bad111e87ff983fd3e7be44a0a2be8ddac6f69540626a242fa2daa96fa50aa3cf9d1ece5de269fa
	 * &t=1400206259109） gcode:2882137065(即为code) vfwebqq:8
	 * bad111e87ff983fd3e7be44a0a2be8ddac6f69540626a242fa2daa96fa50aa3cf9d1ece5de269fa
	 * t:1400206259109 获取好友QQ号码（GET） http://s.web2.qq.com/api/get_friend_uin2
	 * ?tuin=3147591390&type=1&vfwebqq=8
	 * bad111e87ff983fd3e7be44a0a2be8ddac6f69540626a242fa2daa96fa50aa3cf9d1ece5de269fa
	 * &t=1400206554187 tuin:3147591390 type:1 vfwebqq:8
	 * bad111e87ff983fd3e7be44a0a2be8ddac6f69540626a242fa2daa96fa50aa3cf9d1ece5de269fa
	 * t:1400206554187 获取好友详细信息
	 * http://s.web2.qq.com/api/get_friend_info2?tuin=3147591390&vfwebqq=8
	 * bad111e87ff983fd3e7be44a0a2be8ddac6f69540626a242fa2daa96fa50aa3cf9d1ece5de269fa
	 * &clientid=53999199&psessionid=8368046764001d636f6e6e7365727665725f77656271714031302e3133332e34312e38340000741a00001744026e040018d877326d0000000a40526b474f306c53544f6d00000028d8d73914e59e64d27ec7d823d89984ac7f34db9b1cd375927241fb78356d39d52a8dbd392f426777
	 * &t=1400206554188 tuin:3147591390 vfwebqq:8
	 * bad111e87ff983fd3e7be44a0a2be8ddac6f69540626a242fa2daa96fa50aa3cf9d1ece5de269fa
	 * clientid:53999199 psessionid:8368046764001d636f6e6e7365727665725f77656271714031302e3133332e34312e38340000741a00001744026e040018d877326d0000000a40526b474f306c53544f6d00000028d8d73914e59e64d27ec7d823d89984ac7f34db9b1cd375927241fb78356d39d52a8dbd392f426777
	 * t:1400206554188
	 */
	private void doWork() throws IOException, ScriptException, InterruptedException {
		String succ = doLogin();
		/*
		 * System.out.println(succ); for (Entry<String, String> set :
		 * cookies.entrySet()) { System.out.println(set.getKey() + "   " +
		 * set.getValue()); } cookies.put("qz_screen", "1680x1050");
		 * cookies.put("Loading", "Yes"); cookies.put("o_cookie", username);
		 * 
		 * String zns=loginZone(); System.out.println(zns);
		 */
		// String jj=jin();
		// System.out.println(jj);
		/*
		 * System.out.println(succ); for (Entry<String, String> set :
		 * cookies.entrySet()) {
		 * System.out.println(set.getKey()+"   "+set.getValue()); }
		 */
		System.out.println(succ);
		JSONObject jsonObject = JSONObject.fromObject(succ);
		JSONObject result = jsonObject.getJSONObject("result");
		String vfwebqq = result.getString("vfwebqq");
		glovfwebqq = vfwebqq;
		glopessionID = result.getString("psessionid"); // System.out.println(succ);
		String str = getGroupInfo();//
		System.out.println("获取群信息:" + str);
		QunInfo = JSONObject.fromObject(str);
		doXinTiao();
		// sendInfo2MyFrind();

		/*
		 * int i = 0; while (true) { String res = searchFriends("1", "0", "0",
		 * i); // System.out.println(res); JSONObject ob =
		 * JSONObject.fromObject(res); if (ob.getInt("retcode") == 0) {
		 * JSONArray array = ob.getJSONObject("result").getJSONArray(
		 * "uinlist"); StringBuilder builder = new StringBuilder(); for (int j =
		 * 0; j < array.size(); j++) { JSONObject ino = array.getJSONObject(j);
		 * String uin = ino.getString("uin"); builder.append(getQQDeail(uin) +
		 * "\r\n"); System.out.println(ino.getString("nick") + "   " +
		 * ino.getString("age"));
		 * 
		 * } FileFactory.writeFile(builder.toString(),
		 * "F:\\tryQQ\\qqZone\\searchFriendsInfo.txt", true); } else {
		 * System.out.println(i + "  " + res); } Thread.sleep(20 * 1000); i++; }
		 */

		// System.out.println("qunInfo" + str);

		/*
		 * for (int i = 0; i < 10; i++) { String str1 = searchFriends("0", "0",
		 * "0",i); System.out.println(str1); }
		 */

		// String fs = getFriends();
		// System.out.println(" FS"+fs);
		// sendInfo2EveryQun();
		// 获取好友信息
		// sendInfo2MyFrind();
		// sendMeg2EveryOneInAllQun();
		// String res = searchFriends("1", "11", "0");
		// System.out.println(res);
		/*
		 * // 获取好友信息 String fs = getFriends(vfwebqq); // 获取群信息 String gs =
		 * getGroupInfo(vfwebqq); QunHelper=gs;
		 * 
		 * XinTiao x = new XinTiao(); x.ptwebqq = cookies.get("ptwebqq");
		 * x.psessionid = result.getString("psessionid"); x.start(); Thread
		 * qunSned=new Thread(new Runnable() {
		 * 
		 * @Override public void run() { try { snedMag2EveryOneInQun(QunHelper);
		 * } catch (IOException | InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 * 
		 * } }); qunSned.start();
		 * 
		 * CheckWindow(fs, result.getString("psessionid"));
		 */
		//
	}

	private String loginZone() throws MalformedURLException, IOException {
		String str = "http://user.qzone.qq.com/" + username;
		return doZoonGet(str, "http://qzone.qq.com/");

	}

	private String doZoonGet(String str, String refer) throws MalformedURLException, IOException {
		URL url = new URL(str);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(60 * 1000);
		conn
				.addRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");

		conn.addRequestProperty("Referer", refer);
		conn.addRequestProperty("Cookie", getCookitStr());
		// System.out.println(getCookitStr());
		String res = "";
		boolean again = true;
		int i = 0;
		while (again) {
			try {
				res = getHtml(conn, null);
				again = false;
			} catch (Exception e) {
				i++;
				System.err.println("get 请求发生异常 正在尝试  " + i);
				// e.printStackTrace();
				if (i > 3) {
					again = false;
				}
			}
		}
		return res;

	}

	/**
	 * 发送消息到群
	 * 
	 * @param group_uin
	 * @param content
	 * @return
	 * @throws IOException
	 */
	private String sendMsg2Qun(long group_uin, String content) throws IOException {

		String str = "http://d.web2.qq.com/channel/send_qun_msg2";
		JSONObject ob = new JSONObject();
		// content = content.replaceAll("\\", "\\\\").replaceAll("\n", "\\n")
		String sr = "\"" + string2Json(content) + "\"";
		String all = "\"[" + sr + ",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]\"";
		ob.put("group_uin", Long.valueOf(group_uin));

		ob.put("content", all);
		ob.put("face", 180);
		ob.put("clientid", clientid);
		ob.put("msg_id", 38840001);
		ob.put("psessionid", glopessionID);
		String re = doPost(str, ob);
		// System.out.println("send To " + group_uin + " " + content + re);
		ob.clear();
		return re;
	}

	// http://s.web2.qq.com/api/get_friend_uin2?tuin=【临时号码】&type=1&vfwebqq=【vfwebqq】&t=【时间戳】
	private String getRealQQ(String uin) {
		String utl = "http://s.web2.qq.com/api/get_friend_uin2?tuin=" + uin + "&type=1&vfwebqq=" + glovfwebqq + "&t="
				+ System.currentTimeMillis();
		URL url;
		try {
			url = new URL(utl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.addRequestProperty("Cookie", getCookitStr());
			putCookie(conn, false);
			String vers = getHtml(conn, null);
			System.out.println("==获取真实qq号码响应:" + vers);
			JSONObject result = JSONObject.fromObject(vers);
			if ("0".equals(result.optInt("retcode"))) {
				JSONObject res = result.optJSONObject("result");
				if (res != null && res.has("account")) {
					String realQQ = res.optString("account");
					uinMap.put(uin, realQQ);
					return realQQ;
				}
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * JSON字符串特殊字符处理，比如：“\A1;1300”
	 * 
	 * @param s
	 * @return String
	 */
	public static String string2Json(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '\"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '/':
				sb.append("\\/");
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}

	private void sendMeg2EveryOneInAllQun() throws IOException {

		// snedMag2EveryOneInQun(null);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				JSONArray allF = null;
				try {
					allF = getAllInfo();
					helpResponsQun = allF;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/*
				 * while (true) { try {
				 * 
				 * snedMag2EveryOneInQun2(null); break;
				 * 
				 * } catch (IOException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); } catch (InterruptedException e) { //
				 * TODO Auto-generated catch block e.printStackTrace(); }
				 * 
				 * }
				 */

			}

			public JSONArray getAllInfo() throws IOException {
				System.out.println("QinInfo " + QunInfo);
				if (QunInfo.getInt("retcode") != 0) {
					System.out.println("获取群简要信息失败");
					return null;
				}
				JSONArray jfs = QunInfo.getJSONObject("result").getJSONArray("gnamelist");
				JSONArray allF = new JSONArray();
				for (int i = 1; i < jfs.size(); i++) {
					JSONObject inob = jfs.getJSONObject(i);
					String jcode = inob.getString("code");
					String decs = getQunDetail(jcode);
					JSONObject qunDes = JSONObject.fromObject(decs);
					if (qunDes.getInt("retcode") != 0) {
						continue;
					}
					// System.out.println(qunDes);
					JSONArray stats = qunDes.getJSONObject("result").getJSONArray("stats");
					JSONArray minfo = qunDes.getJSONObject("result").getJSONArray("minfo");
					JSONObject tob = new JSONObject();
					tob.put("gid", inob.getString("gid"));
					tob.put("array", minfo);
					allF.add(tob);
					JSONArray member = qunDes.getJSONObject("result").getJSONObject("ginfo").getJSONArray("members");
					JSONArray cards = qunDes.getJSONObject("result").getJSONArray("cards");
				}
				return allF;
			}
		});
		t.start();
	}

	private void snedMag2EveryOneInQun2(Long mainUin) throws IOException, InterruptedException {

		for (int i = 0; i < helpResponsQun.size(); i++) {
			JSONObject inob1 = helpResponsQun.getJSONObject(i);
			String gid = inob1.getString("gid");
			JSONArray array = inob1.getJSONArray("array");
			// String selDeail=getQQDeail(quin);
			// 向群中某成员发消息
			for (int j = 0; j < array.size(); j++) {
				JSONObject onb = array.getJSONObject(j);
				if (mainUin != null) {
					if (onb.getLong("uin") != mainUin) {
						continue;
					}
					String groupsig = getGroupSig(gid, onb.getLong("uin"));
					if (groupsig.trim().length() <= 0) {
						continue;
					}
					JSONObject grouob = JSONObject.fromObject(groupsig);
					if (grouob.getInt("retcode") != 0) {
						System.out.println(groupsig + "  出事儿了！！！");
						// Thread.sleep(20 * 1000);
						continue;
					}
					String hgroup_sig = grouob.getJSONObject("result").getString("value");
					String res = sendMegToQunSb(onb.getLong("uin"), sendContent, hgroup_sig);
					System.out.println("回复群成员 @" + onb.getLong("uin") + "@" + onb.getString("nick") + "@"
							+ onb.getString("province") + "@" + onb.getString("city") + "@" + onb.getString("gender")
							+ "@@@" + res);
				} else {
					String groupsig = getGroupSig(gid, onb.getLong("uin"));
					if (groupsig.trim().length() <= 0) {
						continue;
					}
					JSONObject grouob = JSONObject.fromObject(groupsig);
					if (grouob.getInt("retcode") != 0) {
						System.out.println(groupsig + "  出事儿了！！！");
						// Thread.sleep(20 * 1000);
						continue;
					}
					String hgroup_sig = grouob.getJSONObject("result").getString("value");
					String res = sendMegToQunSb(onb.getLong("uin"), sendContent, hgroup_sig);
					System.out.println("QunChengyuan Sned @" + onb.getLong("uin") + "@" + onb.getString("nick") + "@"
							+ onb.getString("province") + "@" + onb.getString("city") + "@" + onb.getString("gender")
							+ "@@@" + res);
					Thread.sleep(60 * 1000);
				}

			}

			// /System.out.println("group Sig " + groupsig);

			// System.out.println("**********");
		}
		// System.out.println(decs);

	}

	private void sendInfo2MyFrind() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String fs = "";
				try {
					fs = getFriends();
				} catch (ScriptException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				while (true) {
					try {
						CheckWindow(fs);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					break;
				}

			}
		});
		t.start();

	}

	private void sendInfo2EveryQun() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						sendQunInfo(QunMsg);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			}
		});
		t.start();

	}

	private void sendQunInfo(String cont) throws IOException, InterruptedException {
		String str = "http://d.web2.qq.com/channel/send_qun_msg2";
		JSONObject ob = new JSONObject();
		if (QunInfo.getInt("retcode") != 0) {
			System.out.println("发送群消息 获取群信息失败");
			return;
		}
		JSONArray array = QunInfo.getJSONObject("result").getJSONArray("gnamelist");
		List<String> gids = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			JSONObject object = array.getJSONObject(i);
			gids.add(object.getString("gid") + "@@@" + object.getString("name"));
		}
		String sr = "\"" + cont + "\"";
		String all = "\"[" + sr + ",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]\"";
		for (String st : gids) {
			String[] ss = st.split("@@@");
			ob.put("group_uin", Long.valueOf(ss[0]));

			ob.put("content", all);
			ob.put("face", 180);
			ob.put("clientid", clientid);
			ob.put("msg_id", 38840001);
			ob.put("psessionid", glopessionID);
			String re = doPost(str, ob);
			System.out.println("send To " + st + " " + cont + re);
			ob.clear();
			Thread.sleep(20 * 1000);

		}

	}

	/**
	 * 开始心跳
	 */
	private void doXinTiao() {
		XinTiao x = new XinTiao();
		x.ptwebqq = glovfwebqq;
		x.psessionid = glopessionID;
		x.start();

	}

	public String doLogin() throws IOException, ScriptException {
		cookies = new HashMap<String, String>();
		String verCode = getVerCode();
		String url = tryLogin(verCode);
		System.out.println(url);
		addCookieUseUrl(url);
		String succ = LoginQQ();
		return succ;

	}

	private String searchFriends(String counyty, String province, String sex, int page) throws IOException {
		String str = "http://s.web2.qq.com/api/search_qq_by_term?country=" + counyty + "&province=" + province
				+ "&city=0&agerg=0&sex=" + sex + "&lang=0&online=0&vfwebqq=" + glovfwebqq + "&page=" + page
				+ "&t=1400463755365";
		return doGet(str);
	}

	private void snedMag2EveryOneInQun(String gs) throws IOException, InterruptedException {
		JSONObject gob = JSONObject.fromObject(gs);
		if (gob.getInt("retcode") != 0) {
			return;
		}
		JSONArray jfs = gob.getJSONObject("result").getJSONArray("gnamelist");
		for (int i = 1; i < jfs.size(); i++) {
			JSONObject inob = jfs.getJSONObject(i);
			String jcode = inob.getString("code");
			String decs = getQunDetail(jcode);

			JSONObject qunDes = JSONObject.fromObject(decs);
			if (qunDes.getInt("retcode") != 0) {
				continue;
			}
			// System.out.println(qunDes);
			JSONArray stats = qunDes.getJSONObject("result").getJSONArray("stats");
			JSONArray minfo = qunDes.getJSONObject("result").getJSONArray("minfo");
			JSONArray member = qunDes.getJSONObject("result").getJSONObject("ginfo").getJSONArray("members");
			JSONArray cards = qunDes.getJSONObject("result").getJSONArray("cards");
			System.out.println(inob.getString("name") + "----------------------------");
			for (int j = 0; j < minfo.size(); j++) {
				JSONObject inob1 = minfo.getJSONObject(j);
				long quin = inob1.getLong("uin");
				// String selDeail=getQQDeail(quin);
				// 向群中某成员发消息
				String groupsig = getGroupSig(inob.getString("gid"), quin);
				// /System.out.println("group Sig " + groupsig);
				if (groupsig.trim().length() <= 0) {
					continue;
				}
				JSONObject grouob = JSONObject.fromObject(groupsig);
				if (grouob.getInt("retcode") != 0) {
					System.out.println(quin + "@" + inob1.getString("nick") + "@" + groupsig + "  出事儿了！！！");
					Thread.sleep(10 * 1000);
					continue;
				}
				String hgroup_sig = grouob.getJSONObject("result").getString("value");
				String res = sendMegToQunSb(quin, sendContent, hgroup_sig);
				System.out.println("QunChengyuan Sned @" + quin + "@" + inob1.getString("nick") + "@"
						+ inob1.getString("province") + "@" + inob1.getString("city") + "@" + inob1.getString("gender")
						+ "@@@" + res);
				Thread.sleep(20 * 1000);
				// System.out.println("**********");
			}
			// System.out.println(decs);
			System.out.println(stats.size() + "  " + minfo.size() + "  " + member.size() + "  " + cards.size());
		}

	}

	private String getGroupSig(String id, long quin) throws IOException {
		String str = "http://d.web2.qq.com/channel/get_c2cmsg_sig2?id=" + id + "&to_uin=" + quin + "&clientid="
				+ clientid + "&psessionid=" + glopessionID + "&service_type=0&t=1400220305058";

		return doGet(str);
	}

	private String sendMegToQunSb(long quin, String co, String hgroup_sig) throws IOException {
		String str = "http://d.web2.qq.com/channel/send_sess_msg2";
		String sr = "\"" + co + "\"";
		String all = "\"[" + sr + ",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]\"";
		JSONObject ob = new JSONObject();
		ob.put("to", quin);
		ob.put("content", all);
		ob.put("face", 180);
		ob.put("clientid", clientid);
		ob.put("msg_id", 48480002);
		ob.put("psessionid", glopessionID);
		ob.put("group_sig", hgroup_sig);
		ob.put("ervice_type", 0);
		// System.out.println(ob.toString());

		return doPost(str, ob);
	}

	private String doPost(String str, JSONObject ob) throws IOException {
		URL url = new URL(str);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setConnectTimeout(60 * 1000);
		conn.setDoInput(true);
		conn.setDoOutput(true);

		conn
				.addRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");

		conn.addRequestProperty("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
		conn.addRequestProperty("Cookie", getCookitStr());
		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.write("r=" + URLEncoder.encode(ob.toString(), "utf-8"));
		out.close();
		conn.connect();
		return getHtml(conn, out);
	}

	private String getQQDeail(String tuin) throws IOException {
		String str = "http://s.web2.qq.com/api/get_friend_info2?tuin=" + tuin + "&vfwebqq=" + glovfwebqq + "&clientid="
				+ clientid + "&psessionid=" + glopessionID + "&t=1400206554188";
		return doGet(str);
	}

	private String getQQNum(String tuin) throws IOException {
		String str = "http://s.web2.qq.com/api/get_friend_uin2?tuin=" + tuin + "&type=1&vfwebqq=" + glovfwebqq
				+ "&t=1400206554187";
		return doGet(str);
	}

	private String getQunDetail(String j_code) throws IOException {
		String str = "http://s.web2.qq.com/api/get_group_info_ext2?gcode=" + j_code + "&vfwebqq=" + glovfwebqq
				+ "&t=1400206259109";
		return doGet(str);

	}

	public String doGet(String str) throws IOException {
		URL url = new URL(str);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(60 * 1000);
		conn
				.addRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");

		conn.addRequestProperty("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2");
		conn.addRequestProperty("Cookie", getCookitStr());
		// System.out.println(getCookitStr());
		String res = "";
		boolean again = true;
		int i = 0;
		while (again) {
			try {
				res = getHtml(conn, null);
				again = false;
			} catch (Exception e) {
				i++;
				System.err.println("get 请求发生异常 正在尝试  " + i);
				// e.printStackTrace();
				if (i > 3) {
					again = false;
				}
			}
		}
		return res;
	}

	/**
	 * 获取群信息
	 * 
	 * @param vfwebqq
	 * @return
	 * @throws IOException
	 */
	private String getGroupInfo() throws IOException {

		JSONObject ob = new JSONObject();
		try {
			ob.put("hash", getHash(cookies.get("ptwebqq")));
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ob.put("vfwebqq", glovfwebqq);

		String data = URLEncoder.encode(ob.toString(), "utf-8");
		URL url = new URL("http://s.web2.qq.com/api/get_group_name_list_mask2");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(60 * 1000);
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);

		conn
				.addRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");

		conn.addRequestProperty("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
		conn.addRequestProperty("Cookie", getCookitStr());
		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.write("r=" + data);
		out.flush();
		conn.connect();
		return getHtml(conn, out);
	}

	/**
	 * 聊天发送消息
	 * 
	 * @param fs
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void CheckWindow(String fs) throws IOException, InterruptedException {
		JSONObject fsb = JSONObject.fromObject(fs);
		// System.out.println("fsb " + fs);
		JSONObject result = fsb.getJSONObject("result");
		JSONArray array = result.getJSONArray("info");
		int i = 0;
		while (i < array.size()) {
			// String key = its.next();
			// JSONArray array = result.getJSONArray(key);
			// System.out.println(key);

			JSONObject fob = array.getJSONObject(i);
			// System.out.println(fob);

			if (fob.has("uin") && !fob.getString("nick").equals("环环")) {
				String re = sendMessage(fob.getLong("uin"), FriendMsg);
				// Thread.sleep(5 * 1000);
				String friendInfo = getQQDeail(fob.getString("uin"));

				JSONObject tob = JSONObject.fromObject(friendInfo);
				if (tob.getInt("retcode") == 0) {
					System.out.println("SnegMyFrind  " + tob.getJSONObject("result").getString("nick") + "_"
							+ tob.getJSONObject("result").getString("email") + "_"
							+ tob.getJSONObject("result").getString("gender") + "@@@" + re);
				}

			}
			i++;
			// Thread.sleep(2 * 1000);
			// System.out.println("------------------------");
		}

	}

	private synchronized static String sendMessage(long uin, String x) throws IOException {
		// String x = "聊聊天呗";
		String sr = "\"" + x + "\"";
		String all = "\"[" + sr + ",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]\"";

		String str = "http://d.web2.qq.com/channel/send_buddy_msg2";
		URL url = new URL(str);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Referer", "http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2");
		conn.setRequestProperty("Cookie", getCookitStr());
		conn
				.addRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
		JSONObject ob = new JSONObject();
		ob.put("to", uin);

		ob.put("content", all);

		ob.put("face", "180");
		ob.put("clientid", clientid);
		ob.put("msg_id", "17670001");
		ob.put("psessionid", glopessionID);
		// System.out.println(ob.toString());
		String data = URLEncoder.encode(ob.toString(), "utf-8");
		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.write("r=" + data);
		// conn.getResponseCode();
		out.flush();
		conn.connect();

		return getHtml(conn, out);

	}

	private static String getFriends() throws ScriptException, IOException {
		String str = "http://s.web2.qq.com/api/get_user_friends2";
		String hs = getHash(cookies.get("ptwebqq"));
		JSONObject ob = new JSONObject();
		ob.put("vfwebqq", glovfwebqq);
		ob.put("hash", hs);
		System.out.println(ob);
		String data = URLEncoder.encode(ob.toString(), "utf-8");
		URL url = new URL("http://s.web2.qq.com/api/get_user_friends2");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(60 * 1000);
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);

		conn
				.addRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");

		conn.addRequestProperty("Referer", "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
		conn.addRequestProperty("Cookie", getCookitStr());
		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.write("r=" + data);
		out.flush();
		conn.connect();
		return getHtml(conn, out);

	}

	private static String getHash(String vf) throws FileNotFoundException, ScriptException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine en = manager.getEngineByName("javascript");
		en.eval(new FileReader(new File("hashs.js")));
		Object t = en.eval("s(" + username + ",\"" + vf + "\")");

		return t.toString();

	}

	private static String LoginQQ() throws IOException {
		String str = "http://d.web2.qq.com/channel/login2";
		URL url = new URL(str);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn
				.addRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
		conn.addRequestProperty("Origin", "http://d.web2.qq.com");
		conn.addRequestProperty("Cookie", getCookitStr());
		conn.addRequestProperty("Referer", "http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2");
		// System.out.println(cookies.get("ptwebqq"));
		String data = "{\"ptwebqq\":\"" + cookies.get("ptwebqq") + "\",\"passwd_sig\":\"\",\"clientid\":" + clientid
				+ ",\"psessionid\":\"\",\"status\":\"online\"}";
		data = URLEncoder.encode(data, "utf-8");
		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.write("r=" + data);
		out.flush();
		conn.connect();
		return getHtml(conn, out);
	}

	private static void addCookieUseUrl(String utl) throws IOException {
		URL url = new URL(utl);
		// System.out.println(utl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setInstanceFollowRedirects(false);
		conn.addRequestProperty("Cookie", getCookitStr());
		putCookie(conn, false);
	}

	private static String tryLogin(String verCode) throws ScriptException, IOException {
		String md5 = getMd5(verCode);
		String utl = "https://ssl.ptlogin2.qq.com/login?u="
				+ username
				+ "&p="
				+ md5
				+ "&verifycode="
				+ verCode
				+ "&webqq_type=10&remember_uin=1&login2qq=1&aid=501004106&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&h=1&ptredirect=0&ptlang=2052&daid=164&from_ui=1&pttype=1&dumy=&fp=loginerroralert&action=0-19-12995&mibao_css=m_webqq&t=1&g=1&js_type=0&js_ver=10079&login_sig=VoTxG**NRalrWizMHDrQs8rg99ChGC2Z68mmBhK8MfIIoWrd6db3KqJEbpcwlbQi";
		URL url = new URL(utl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.addRequestProperty("Cookie", getCookitStr());
		putCookie(conn, false);
		String vers = getHtml(conn, null);
		System.out.println(cookies.size() + "  " + vers);
		List<String> ls = RegFactory.catchGroup("(http[^']+)'", vers);
		// System.out.println(cookies.size()+"  "+ls.get(0));
		return ls.get(0);
	}

	public static String getCookitStr() {
		StringBuilder builder = new StringBuilder();
		for (Entry<String, String> set : cookies.entrySet()) {
			builder.append(set.getKey() + "=" + set.getValue() + ";");
		}
		return builder.toString();
	}

	public static String getMd5(String verCode) throws ScriptException, FileNotFoundException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine en = manager.getEngineByName("javascript");
		en.eval(new FileReader(new File("myqq.js")));
		Object t = en.eval("qqhash(" + username + ",\"" + passwd + "\",\"" + verCode + "\")");
		String uin = t.toString();
		return uin;

	}

	/**
	 * 获取验证码
	 * 
	 * @throws IOException
	 */
	private static String getVerCode() throws IOException {
		System.out.println(username);
		String str = "https://ssl.ptlogin2.qq.com/check?uin="
				+ username
				+ "&appid=501004106&js_ver=10079&js_type=0&login_sig=VoTxG**NRalrWizMHDrQs8rg99ChGC2Z68mmBhK8MfIIoWrd6db3KqJEbpcwlbQi&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html&r=0.17497222032397985";
		URL url = new URL(str);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		putCookie(conn, false);
		String vers = getHtml(conn, null);
		System.out.println(vers);
		// // System.out.println(cookies.size()+"   "+vers);
		// List<String> ls = RegFactory.catchGroup(",'(!\\w+)',", vers);
		// // System.out.println(ls.get(0));
		// return ls.get(0);
		Pattern p = Pattern.compile("\\,\\'([!\\w]+)\\'");
		Matcher m = p.matcher(vers);
		String checkType = "";
		String uinstr = "";
		if (m.find()) {
			checkType = m.group(1);
		}
		Pattern p_uin = Pattern
				.compile("\\\\\\w{3}\\\\\\w{3}\\\\\\w{3}\\\\\\w{3}\\\\\\w{3}\\\\\\w{3}\\\\\\w{3}\\\\\\w{3}");
		m = p_uin.matcher(vers);
		if (m.find()) {
			uinstr = m.group(0);
		} else {
			System.out.println("error:获取检查码失败！");
			return null;
		}
		String check = "";
		if (!checkType.startsWith("!")) {
			// 下载图片
			// 生成图片验证码

			String imagePath = "http://captcha.qq.com/getimage?aid=1003903&uin=" + username + "&r=" + getRandomCode();

			try {
				url = new URL(imagePath);

				/* 此为联系获得网络资源的固定格式用法，以便后面的in变量获得url截取网络资源的输入流 */
				conn = (HttpURLConnection) url.openConnection();

				// 这里取出验证码图片记得加入cookie，否则，提交验证码会无效
				// if (conn.getHeaderFields().get("Set-Cookie") != null) {
				//
				// for (String s : conn.getHeaderFields().get("Set-Cookie")) {
				// cookie += s;
				// }
				// }
				putCookie(conn, false);
				DataInputStream in = new DataInputStream(conn.getInputStream());

				/* 此处也可用BufferedInputStream与BufferedOutputStream */
				DataOutputStream out = new DataOutputStream(new FileOutputStream("d://a.jpg"));

				/* 将参数savePath，即将截取的图片的存储在本地地址赋值给out输出流所指定的地址 */
				byte[] buffer = new byte[1024];
				int count = 0;

				/* 将输入流以字节的形式读取并写入buffer中 */
				while ((count = in.read(buffer)) > 0) {
					out.write(buffer, 0, count);
				}
				// 关闭输入输出流
				out.close();
				in.close();
				conn.disconnect();
				try {
					Runtime.getRuntime().exec(
							"rundll32 c:\\Windows\\System32\\shimgvw.dll,ImageView_Fullscreen " + "d:\\a.jpg");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("请输入验证码并回车:");
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String code = br.readLine();

				check = code.toUpperCase();

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			check = checkType;
		}
		System.out.println("info:uin=" + uinstr);
		System.out.println("info:check=" + check);
		return checkType;
	}

	private static String getHtml(HttpURLConnection conn, PrintWriter out) throws IOException {
		InputStream in = null;

		in = conn.getInputStream();

		if (in == null) {
			return null;
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = reader.readLine();
		String res = "";
		while (line != null) {
			res += line;
			line = reader.readLine();
		}
		in.close();
		if (out != null) {
			out.close();
		}
		return res;
	}

	private static void putCookie(HttpURLConnection conn, boolean print) {
		int i = 1;
		while (conn.getHeaderFieldKey(i) != null) {
			if (conn.getHeaderFieldKey(i).equals("Set-Cookie")) {

				String line = conn.getHeaderField(i);
				if (print) {
					// System.out.println(line);
				}
				String[] ss = line.split(";");
				String[] lss = ss[0].split("=");
				if (lss.length == 2) {
					cookies.put(lss[0].trim(), lss[1].trim());
				}

			}
			i++;
		}

	}

	// 生成随机码

	public static String getRandomCode() {
		return "" + rd.nextDouble();
	}
}
