package cnpat.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

public class Test {
	public static void main(String[] args) {
		System.out.println("以下说法都正确，除了（）。\nA.项目经理是被执行组织分派出来保证项目目标实现的项目负责人\nB.如果设立了项目管理办公室，那么项目经理的直接上级就是项目管理办公室的经理C.在矩阵式组织中，项目经理通常向职能经理汇报工作D.项目经理、职能经理和运营经理是现代组织中通常需要的三种重要角色".replaceAll("\\\n", "\\\\\\\\\\n"));
		System.out.println(QQRebort.string2Json("以下说法都正确，除了（）。\nA.项目经理是被执行组织分派出来保证项目目标实现的项目负责人\nB.如果设立了项目管理办公室，那么项目经理的直接上级就是项目管理办公室的经理C.在矩阵式组织中，项目经理通常向职能经理汇报工作D.项目经理、职能经理和运营经理是现代组织中通常需要的三种重要角色"));

		String str= " 113.A ";
		Pattern p = Pattern.compile("\\s*(\\d+)\\s*\\.\\s*([a-zA-Z])*");
		Matcher m = p.matcher(str);
		if(m.find()){
			String key = m.group(1);
			System.out.println("匹配结果:"+key);
			System.out.println("匹配结果:"+m.group(2));
		}
		
		
		JSONObject result = JSONObject.fromObject("{\"retcode\":0,\"result\":{\"uiuin\":\"\",\"account\":415071574,\"uin\":1817304247}}");
		if (0==result.optInt("retcode")) {
			JSONObject res = result.optJSONObject("result");
			if (res != null && res.has("account")) {
				long realQQ = res.optLong("account");
				System.out.println(realQQ);
			}
		}
	}
}
