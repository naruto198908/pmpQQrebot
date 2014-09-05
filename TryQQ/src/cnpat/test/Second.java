package cnpat.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import cnpat.util.FileFactory;
import cnpat.util.RegFactory;

public class Second {
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

	public static void main(String[] args) throws Exception {

		new Second().doWork();
		/**
		 * "[\"电饭锅\",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]"
		 * "[\"电饭锅\",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]"
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

		public void run() {
			// System.out.println("--------------------");
			Map<String, String> map = new HashMap<String, String>();
			if (QunInfo.getInt("retcode") != 0) {
				System.out.println("获取群信息失败！");
			} else {
				JSONArray array = QunInfo.getJSONObject("result").getJSONArray(
						"gnamelist");
				for (int i = 0; i < array.size(); i++) {
					JSONObject ino = array.getJSONObject(i);
					map.put(ino.getString("code"), ino.getString("name") + "@@"
							+ ino.getString("gid"));
				}
			}
			while (true) {
				try {
					sleep(3 * 1000);
					System.out.println("接收端 ********");
					String line = tiao();
					JSONObject ob = JSONObject.fromObject(line);
					if (ob.getString("retcode").equals("0")) {
						// System.out.println(line);
						JSONArray result = ob.getJSONArray("result");
						JSONObject temp = result.getJSONObject(0)
								.getJSONObject("value");
						String flag = result.getJSONObject(0).getString(
								"poll_type");

						if (!flag.equals("message")) {
							if (flag.equals("group_message")) {
//								收到群消息 {"poll_type":"group_message","value":{"msg_id":10906,"from_uin":3361165941,"to_uin":1810287483,"msg_id2":119506,"msg_type":43,"reply_ip":176755999,"group_code":1130419226,"send_uin":1505218642,"seq":270,"time":1409716201,"info_seq":100740797,"content":[["font",{"size":11,"color":"ff0000","style":[0,0,0],"name":"Tahoma"}],"a "]}}
								   System.out.println("收到群消息 " +
								   result.getJSONObject(0));
								  
								String gid = result.getJSONObject(0)
										.getJSONObject("value")
										.getString("from_uin");
								String from_uin = result.getJSONObject(0)
										.getJSONObject("value")
										.getString("send_uin");
								String group_code = result.getJSONObject(0)
										.getJSONObject("value")
										.getString("group_code");
								String content = result.getJSONObject(0)
										.getJSONObject("value")
										.getJSONArray("content").getString(1)
										.trim();
								System.out.println(from_uin + " in "+group_code+"("
										+ map.get(group_code) + ") said:"
										+ content);

								// snedMag2EveryOneInQun2(Long.valueOf(from_uin));

								/*
								 * System.out.println("准备发消息 " + gid + "  " +
								 * from_uin);
								 */
								String groupsig = getGroupSig(gid,
										Long.valueOf(from_uin));
								JSONObject grouob = JSONObject
										.fromObject(groupsig);
								String hgroup_sig = grouob.getJSONObject(
										"result").getString("value");
								// String reS="你烦吗？我是来烦你的 嘎嘎嘎";
								// System.out.println("正在检索关键字  "+content);
//								String reS = BaiduHelper.searchZhidao(content);
//								String res = sendMegToQunSb(
//										Long.valueOf(from_uin), reS, hgroup_sig);
								String res = sendMsg2Qun(Long.valueOf(gid),"test");
								System.out.println("回复群 "  + " "
										+ "\r\nreponse:" + res);
							} else if (flag.equals("sess_message")) {
								JSONObject sessOb = result.getJSONObject(0)
										.getJSONObject("value");
								Long fromuin = sessOb.getLong("from_uin");
								String gid = sessOb.getString("id");
								String reCon = sessOb.getJSONArray("content")
										.getString(1).trim();
								String groupsig = getGroupSig(gid, fromuin);
								JSONObject grouob = JSONObject
										.fromObject(groupsig);
								String hgroup_sig = grouob.getJSONObject(
										"result").getString("value");
								// System.out.println("正在检索关键字  "+reCon);
								System.out.println(fromuin + " in " + gid
										+ " 回复我 " + reCon);
								String reS = BaiduHelper.searchZhidao(reCon);

								String res = sendMegToQunSb(fromuin, reS,
										hgroup_sig);

								System.out.println("二次回复群中说话的人 " + reS + "\r\n"
										+ res);
							} else if (flag.equals("buddies_status_change")
									&& result.getJSONObject(0)
											.getJSONObject("value")
											.getString("status")
											.equals("online")) {
								System.out.println(result.getJSONObject(0));
								Long uin = result.getJSONObject(0)
										.getJSONObject("value").getLong("uin");
								JSONObject job = JSONObject
										.fromObject(getQQDeail(String
												.valueOf(uin)));
								System.out.println(job.getJSONObject("result")
										.getString("nick")
										+ "@@"
										+ job.getJSONObject("result")
												.getString("email")+"  上线了");
								String re = sendMessage(uin, "你好啊");
								System.out.println("对上线好友发送消息  "+re);
							}
							continue;
						}
						if (temp.has("content")) {
							String c = temp.getString("content").trim();
							String uin = temp.getString("from_uin");
							JSONArray array = JSONArray.fromObject(c);
							// String
							// smart=BaiduHelper.search(array.getString(1));
							JSONObject job = JSONObject
									.fromObject(getQQDeail(uin));
							System.out.println(array.get(1)
									+ "@@@"
									+ job.getJSONObject("result").getString(
											"nick")
									+ "@@"
									+ job.getJSONObject("result").getString(
											"email"));

							String reS = BaiduHelper.searchZhidao(array
									.getString(1));

							String re = sendMessage(Long.valueOf(uin), reS);
							String friendInfo = getQQDeail(uin);
							JSONObject tob = JSONObject.fromObject(friendInfo);
							if (tob.getInt("retcode") == 0) {
								System.out.println("回复好友消息  "
										+ tob.getJSONObject("result")
												.getString("country")
										+ tob.getJSONObject("result")
												.getString("city")
										+ " "
										+ tob.getJSONObject("result")
												.getString("gender") + reS
										+ "\r\n@@@" + re);
							}
						}

					}
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
				} catch (ParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
			conn.setRequestProperty("Referer",
					"http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2");
			conn.setRequestProperty("Cookie", getCookitStr());
			conn.addRequestProperty(
					"User-Agent",
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

	/**
	 * 发送群消息格式 POST（http://d.web2.qq.com/channel/send_qun_msg2） group_uin=gid
	 * r:{"group_uin":885194668,"content":
	 * "[\"g\",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]"
	 * ,"face":180,"clientid":53999199,"msg_id":55040001,"psessionid":
	 * "8368046764001d636f6e6e7365727665725f77656271714031302e3133332e34312e38340000741a00001744026e040018d877326d0000000a40526b474f306c53544f6d00000028d8d73914e59e64d27ec7d823d89984ac7f34db9b1cd375927241fb78356d39d52a8dbd392f426777"
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
	 * &clientid=53999199&psessionid=8368046764001d636f6e6
	 * e7365727665725f77656271714031302e3133332e34312e38340000741a00001744026e040018d877326d0000000a40526b474f306c53544f6d00000028d8d73914e59e64d27ec7d823d89984ac7f34db9b1cd375927241fb78356d39d52a8dbd392f426777
	 * &t=1400206554188 tuin:3147591390 vfwebqq:8
	 * bad111e87ff983fd3e7be44a0a2be8ddac6f69540626a242fa2daa96fa50aa3cf9d1ece5de269fa
	 * clientid:53999199 psessionid:8368046764001d636f6e6
	 * e7365727665725f77656271714031302e3133332e34312e38340000741a00001744026e040018d877326d0000000a40526b474f306c53544f6d00000028d8d73914e59e64d27ec7d823d89984ac7f34db9b1cd375927241fb78356d39d52a8dbd392f426777
	 * t:1400206554188
	 */
	private void doWork() throws IOException, ScriptException,
			InterruptedException {
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
		String str = getGroupInfo();
		QunInfo = JSONObject.fromObject(str);
		doXinTiao();
		sendInfo2MyFrind();

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

	private String doZoonGet(String str, String refer)
			throws MalformedURLException, IOException {
		URL url = new URL(str);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(60 * 1000);
		conn.addRequestProperty(
				"User-Agent",
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
	 * @param group_uin
	 * @param content
	 * @return
	 * @throws IOException
	 */
	private String sendMsg2Qun(long group_uin,String content) throws IOException{

		String str = "http://d.web2.qq.com/channel/send_qun_msg2";
		JSONObject ob = new JSONObject();

		String sr = "\"" + content + "\"";
		String all = "\"["
				+ sr
				+ ",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]\"";		
		ob.put("group_uin", Long.valueOf(group_uin));

		ob.put("content", all);
		ob.put("face", 180);
		ob.put("clientid", clientid);
		ob.put("msg_id", 38840001);
		ob.put("psessionid", glopessionID);
		String re = doPost(str, ob);
//		System.out.println("send To " + group_uin + " " + content + re);
		ob.clear();
		return re;
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
				JSONArray jfs = QunInfo.getJSONObject("result").getJSONArray(
						"gnamelist");
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
					JSONArray stats = qunDes.getJSONObject("result")
							.getJSONArray("stats");
					JSONArray minfo = qunDes.getJSONObject("result")
							.getJSONArray("minfo");
					JSONObject tob = new JSONObject();
					tob.put("gid", inob.getString("gid"));
					tob.put("array", minfo);
					allF.add(tob);
					JSONArray member = qunDes.getJSONObject("result")
							.getJSONObject("ginfo").getJSONArray("members");
					JSONArray cards = qunDes.getJSONObject("result")
							.getJSONArray("cards");
				}
				return allF;
			}
		});
		t.start();
	}

	private void snedMag2EveryOneInQun2(Long mainUin) throws IOException,
			InterruptedException {

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
					String hgroup_sig = grouob.getJSONObject("result")
							.getString("value");
					String res = sendMegToQunSb(onb.getLong("uin"),
							sendContent, hgroup_sig);
					System.out.println("回复群成员 @" + onb.getLong("uin") + "@"
							+ onb.getString("nick") + "@"
							+ onb.getString("province") + "@"
							+ onb.getString("city") + "@"
							+ onb.getString("gender") + "@@@" + res);
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
					String hgroup_sig = grouob.getJSONObject("result")
							.getString("value");
					String res = sendMegToQunSb(onb.getLong("uin"),
							sendContent, hgroup_sig);
					System.out.println("QunChengyuan Sned @"
							+ onb.getLong("uin") + "@" + onb.getString("nick")
							+ "@" + onb.getString("province") + "@"
							+ onb.getString("city") + "@"
							+ onb.getString("gender") + "@@@" + res);
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
				}catch( IOException e1){
					e1.printStackTrace();
				}

				while (true) {
					try {
						CheckWindow(fs);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch ( InterruptedException e){
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
					} catch (IOException  e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}catch (InterruptedException e){
						e.printStackTrace();
					}

				}
			}
		});
		t.start();

	}

	private void sendQunInfo(String cont) throws IOException,
			InterruptedException {
		String str = "http://d.web2.qq.com/channel/send_qun_msg2";
		JSONObject ob = new JSONObject();
		if (QunInfo.getInt("retcode") != 0) {
			System.out.println("发送群消息 获取群信息失败");
			return;
		}
		JSONArray array = QunInfo.getJSONObject("result").getJSONArray(
				"gnamelist");
		List<String> gids = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			JSONObject object = array.getJSONObject(i);
			gids.add(object.getString("gid") + "@@@" + object.getString("name"));
		}
		String sr = "\"" + cont + "\"";
		String all = "\"["
				+ sr
				+ ",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]\"";
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

	private String searchFriends(String counyty, String province, String sex,
			int page) throws IOException {
		String str = "http://s.web2.qq.com/api/search_qq_by_term?country="
				+ counyty + "&province=" + province + "&city=0&agerg=0&sex="
				+ sex + "&lang=0&online=0&vfwebqq=" + glovfwebqq + "&page="
				+ page + "&t=1400463755365";
		return doGet(str);
	}

	private void snedMag2EveryOneInQun(String gs) throws IOException,
			InterruptedException {
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
			JSONArray stats = qunDes.getJSONObject("result").getJSONArray(
					"stats");
			JSONArray minfo = qunDes.getJSONObject("result").getJSONArray(
					"minfo");
			JSONArray member = qunDes.getJSONObject("result")
					.getJSONObject("ginfo").getJSONArray("members");
			JSONArray cards = qunDes.getJSONObject("result").getJSONArray(
					"cards");
			System.out.println(inob.getString("name")
					+ "----------------------------");
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
					System.out.println(quin + "@" + inob1.getString("nick")
							+ "@" + groupsig + "  出事儿了！！！");
					Thread.sleep(10 * 1000);
					continue;
				}
				String hgroup_sig = grouob.getJSONObject("result").getString(
						"value");
				String res = sendMegToQunSb(quin, sendContent, hgroup_sig);
				System.out.println("QunChengyuan Sned @" + quin + "@"
						+ inob1.getString("nick") + "@"
						+ inob1.getString("province") + "@"
						+ inob1.getString("city") + "@"
						+ inob1.getString("gender") + "@@@" + res);
				Thread.sleep(20 * 1000);
				// System.out.println("**********");
			}
			// System.out.println(decs);
			System.out.println(stats.size() + "  " + minfo.size() + "  "
					+ member.size() + "  " + cards.size());
		}

	}

	private String getGroupSig(String id, long quin) throws IOException {
		String str = "http://d.web2.qq.com/channel/get_c2cmsg_sig2?id=" + id
				+ "&to_uin=" + quin + "&clientid=" + clientid + "&psessionid="
				+ glopessionID + "&service_type=0&t=1400220305058";

		return doGet(str);
	}

	private String sendMegToQunSb(long quin, String co, String hgroup_sig)
			throws IOException {
		String str = "http://d.web2.qq.com/channel/send_sess_msg2";
		String sr = "\"" + co + "\"";
		String all = "\"["
				+ sr
				+ ",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]\"";
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

		conn.addRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");

		conn.addRequestProperty("Referer",
				"http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
		conn.addRequestProperty("Cookie", getCookitStr());
		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.write("r=" + URLEncoder.encode(ob.toString(), "utf-8"));
		out.close();
		conn.connect();
		return getHtml(conn, out);
	}

	private String getQQDeail(String tuin) throws IOException {
		String str = "http://s.web2.qq.com/api/get_friend_info2?tuin=" + tuin
				+ "&vfwebqq=" + glovfwebqq + "&clientid=" + clientid
				+ "&psessionid=" + glopessionID + "&t=1400206554188";
		return doGet(str);
	}

	private String getQQNum(String tuin) throws IOException {
		String str = "http://s.web2.qq.com/api/get_friend_uin2?tuin=" + tuin
				+ "&type=1&vfwebqq=" + glovfwebqq + "&t=1400206554187";
		return doGet(str);
	}

	private String getQunDetail(String j_code) throws IOException {
		String str = "http://s.web2.qq.com/api/get_group_info_ext2?gcode="
				+ j_code + "&vfwebqq=" + glovfwebqq + "&t=1400206259109";
		return doGet(str);

	}

	public String doGet(String str) throws IOException {
		URL url = new URL(str);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(60 * 1000);
		conn.addRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");

		conn.addRequestProperty("Referer",
				"http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2");
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
		ob.put("vfwebqq", glovfwebqq);

		String data = URLEncoder.encode(ob.toString(), "utf-8");
		URL url = new URL("http://s.web2.qq.com/api/get_group_name_list_mask2");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(60 * 1000);
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);

		conn.addRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");

		conn.addRequestProperty("Referer",
				"http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
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
	private void CheckWindow(String fs) throws IOException,
			InterruptedException {
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
					System.out.println("SnegMyFrind  "
							+ tob.getJSONObject("result").getString("nick")
							+ "_"
							+ tob.getJSONObject("result").getString("email")
							+ "_"
							+ tob.getJSONObject("result").getString("gender")
							+ "@@@" + re);
				}

			}
			i++;
			// Thread.sleep(2 * 1000);
			// System.out.println("------------------------");
		}

	}

	private synchronized static String sendMessage(long uin, String x)
			throws IOException {
		// String x = "聊聊天呗";
		String sr = "\"" + x + "\"";
		String all = "\"["
				+ sr
				+ ",[\"font\",{\"name\":\"宋体\",\"size\":10,\"style\":[0,0,0],\"color\":\"000000\"}]]\"";

		String str = "http://d.web2.qq.com/channel/send_buddy_msg2";
		URL url = new URL(str);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Referer",
				"http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2");
		conn.setRequestProperty("Cookie", getCookitStr());
		conn.addRequestProperty(
				"User-Agent",
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

		conn.addRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");

		conn.addRequestProperty("Referer",
				"http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1");
		conn.addRequestProperty("Cookie", getCookitStr());
		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.write("r=" + data);
		out.flush();
		conn.connect();
		return getHtml(conn, out);

	}

	private static String getHash(String vf) throws FileNotFoundException,
			ScriptException {
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
		conn.addRequestProperty(
				"User-Agent",
				"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
		conn.addRequestProperty("Origin", "http://d.web2.qq.com");
		conn.addRequestProperty("Cookie", getCookitStr());
		conn.addRequestProperty("Referer",
				"http://d.web2.qq.com/proxy.html?v=20130916001&callback=1&id=2");
		// System.out.println(cookies.get("ptwebqq"));
		String data = "{\"ptwebqq\":\"" + cookies.get("ptwebqq")
				+ "\",\"passwd_sig\":\"\",\"clientid\":" + clientid
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

	private static String tryLogin(String verCode) throws ScriptException,
			IOException {
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
		// System.out.println(cookies.size()+"  "+vers);
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

	public static String getMd5(String verCode) throws ScriptException,
			FileNotFoundException {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine en = manager.getEngineByName("javascript");
		en.eval(new FileReader(new File("myqq.js")));
		Object t = en.eval("qqhash(" + username + ",\"" + passwd + "\",\""
				+ verCode + "\")");
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
		// System.out.println(cookies.size()+"   "+vers);
		List<String> ls = RegFactory.catchGroup(",'(!\\w+)',", vers);
		// System.out.println(ls.get(0));
		return ls.get(0);
	}

	private static String getHtml(HttpURLConnection conn, PrintWriter out)
			throws IOException {
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

}
