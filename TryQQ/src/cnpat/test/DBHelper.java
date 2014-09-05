package cnpat.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cnpat.test.bean.InOutput;
import cnpat.test.bean.PmpProcess;
import cnpat.test.bean.Question;
import cnpat.test.bean.Tool;
import cnpat.test.bean.User;

public class DBHelper {

	private Connection conn;

	public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		conn = DBUtil.getConnection();
	}

	public List<Question> queryAllQuestion() {
		String sqlQuerySerialNum = "select * from t_question";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sqlQuerySerialNum);
			rs = pstmt.executeQuery();

			List<Question> list = new ArrayList<Question>();
			while (rs.next()) {
				Question q = new Question();
				q.setId(rs.getInt("id"));
				q.setQuestion(rs.getString("question"));
				q.setAnswer(rs.getString("answer"));
				q.setAnalysis(rs.getString("analysis"));
				q.setSendCount(rs.getInt("send_count"));
				q.setRightCount(rs.getInt("right_count"));
				q.setWrongCount(rs.getInt("wrong_count"));
				list.add(q);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	/**
	 * Progress
	 * 
	 * @return
	 */
	public PmpProcess queryProcess(String processName) {
		String sqlQuerySerialNum = "select * from t_process where name=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sqlQuerySerialNum);
			pstmt.setString(1, processName);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				PmpProcess pp = new PmpProcess();
				pp.setId(rs.getInt("pid"));
				pp.setName(rs.getString("name"));
				pp.setInput(new ArrayList<InOutput>());
				pp.setOutput(new ArrayList<InOutput>());
				pp.setTool(new ArrayList<Tool>());
				String input = rs.getString("input");
				String tool = rs.getString("tool");
				String output = rs.getString("output");
				PreparedStatement pstmt1 = conn.prepareStatement("select * from t_inoutput where ioid in ("
						+ input.substring(0, input.length() - 1) + ")");
				ResultSet  rs1 = pstmt1.executeQuery();
				while (rs1.next()) {
					InOutput iop = new InOutput();
					iop.setId(rs1.getInt("ioid"));
					iop.setName(rs1.getString("name"));
					pp.getInput().add(iop);
				}
				rs1.close();
				pstmt1.close();
				pstmt1 = conn.prepareStatement("select * from t_inoutput where ioid in  ("
						+ output.substring(0, output.length() - 1) + ")");
				// pstmt.setString(1, output);
				rs1 = pstmt1.executeQuery();
				while (rs1.next()) {
					InOutput iop = new InOutput();
					iop.setId(rs1.getInt("ioid"));
					iop.setName(rs1.getString("name"));
					pp.getOutput().add(iop);
				}
				rs1.close();
				pstmt1.close();
				pstmt1 = conn.prepareStatement("select * from t_tool where tid in  ("
						+ tool.substring(0, tool.length() - 1) + ")");
				// pstmt.setString(1, tool);
				rs1 = pstmt1.executeQuery();
				while (rs1.next()) {
					Tool t = new Tool();
					t.setId(rs1.getInt("tid"));
					t.setName(rs1.getString("name"));
					t.setConception(rs1.getString("conception"));
					pp.getTool().add(t);
				}
				rs1.close();
				pstmt1.close();
				return pp;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Tool queryToolByName(String str) {
		String sqlQuerySerialNum = "select * from t_tool where name=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sqlQuerySerialNum);
			pstmt.setString(1, str);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				Tool t = new Tool();
				t.setId(rs.getInt("tid"));
				t.setName(rs.getString("name"));
				t.setConception(rs.getString("conception"));
				return t;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String getToolNameQueryAdvice(String str) {
		String sqlQuerySerialNum = "select * from t_tool where name like ?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sqlQuerySerialNum);
			pstmt.setString(1, "%" + str + "%");
			rs = pstmt.executeQuery();
			String result = "";
			while (rs.next()) {
				result += rs.getString("name") + "、";
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public User queryUser(String realQQ) {
		String sqlQuerySerialNum = "select * from t_user where uid=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sqlQuerySerialNum);
			pstmt.setString(1, realQQ);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				User u = new User();
				u.setUid(rs.getString("uid"));
				u.setAnswerCount(rs.getInt("answer_count"));
				u.setRightCount(rs.getInt("right_count"));
				u.setWrongCount(rs.getInt("wrong_count"));
				return u;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public User updatePerformance(int qid, String uid, boolean isRight) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			if (isRight) {
				pstmt = conn.prepareStatement("update t_question set right_count = right_count+1 where id= ?");
			} else {
				pstmt = conn.prepareStatement("update t_question set wrong_count = wrong_count+1 where id= ?");
			}
			pstmt.setInt(1, qid);
			pstmt.executeUpdate();
			pstmt.close();
			// 查询用户是否存在
			pstmt = conn.prepareStatement("select count(1) count from t_user where uid = ?");
			pstmt.setString(1, uid);
			rs = pstmt.executeQuery();
			if (rs.next() && rs.getInt("count") <= 0) {
				// 无记录则添加
				rs.close();
				pstmt.close();
				pstmt = conn
						.prepareStatement("insert into t_user(uid,answer_count,right_count,wrong_count) values (?,0,0,0)");
				pstmt.setString(1, uid);
				pstmt.executeUpdate();
			}else{
				rs.close();
			}
			pstmt.close();
			if (isRight) {
				pstmt = conn
						.prepareStatement("update t_user set right_count = right_count+1,answer_count=answer_count+1 where uid= ?");
				pstmt.setString(1, uid);
				pstmt.executeUpdate();
				pstmt.close();
			} else {
				pstmt = conn
						.prepareStatement("update t_user set right_count = wrong_count+1,answer_count=answer_count+1 where uid= ?");
				pstmt.setString(1, uid);
				pstmt.executeUpdate();
				pstmt.close();
				// 记录错题
				pstmt = conn.prepareStatement("replace into t_wrong_answer_record(uid,qid) values(?,?)  ");
				pstmt.setString(1, uid);
				pstmt.setInt(2, qid);
				pstmt.execute();
				pstmt.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public void addQuestionSendCount(int qid) {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement("update t_question set send_count = send_count+1 where id= ?");
			pstmt.setInt(1, qid);
			pstmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) {
					pstmt.close();

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public List<Integer> queryWrongRecord(String realQQ) {
		String sqlQuerySerialNum = "select * from t_wrong_answer_record where uid=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sqlQuerySerialNum);
			pstmt.setString(1, realQQ);
			rs = pstmt.executeQuery();
			List<Integer> result = new ArrayList<Integer>();
			while (rs.next()) {
				result.add(rs.getInt("qid"));
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();

				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
