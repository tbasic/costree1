package notice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import notice.db.DBCon;
import notice.vo.Notice;

public class NoticeDao {
	
	public void hitupdate(String seq,int num) throws Exception {
		Connection con=DBCon.getConnection();
		String sql="update notices set hit=? where seq=?";
		
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setInt(1,num+1 );
		pstmt.setString(2,seq );
		
		pstmt.executeUpdate();
		
	}
	public Notice getNotice(String seq,String hit) throws Exception {
		System.out.println("dao getNotice 지나감");
		
		int hnum=Integer.parseInt(hit);
		hitupdate(seq,hnum);//hit 1증가
		//dbconnect
		String sql="select * from notices where seq="+seq;
//		//db connect
//		//드라이버로드
//		Class.forName("oracle.jdbc.driver.OracleDriver");
//		//접속
//		String url="jdbc:oracle:thin:@localhost:1521:orcl";
//		String user="scott";
//		String pw="123456";
//		Connection con=DriverManager.getConnection(url, user, pw);
		Connection con=DBCon.getConnection();
		//실행
		Statement st=con.createStatement();

		//결과
		ResultSet rs=st.executeQuery(sql);//select 실행
		rs.next();
		
		//Notice에 임시저장하기
		Notice n=new Notice();
		n.setSeq(rs.getString("seq"));
		n.setWriter(rs.getString("writer"));
		n.setTitle(rs.getString("title"));
		n.setContent(rs.getString("content"));
		n.setRegdate(rs.getDate("regdate"));
		n.setHit(rs.getInt("hit"));
		n.setFilesrc(rs.getString("filesrc"));
		
		rs.close();
		st.close();
		con.close();
		
		return n;
	}
	
	public List<Notice> getNotices(int page,String field, String query) throws Exception {
		int vrow=5;
		
		int srow=1+(page-1)*vrow;
		//1 11 21 31 41
		//10 20 30 
		int erow=vrow+(page-1)*vrow;
		String sql="select * from "; 
			sql+="(select rownum num,n.* from ";
			sql+="(select * from notices ";
			sql+="where "+field+" like ? order by regdate desc) n)"; 
			sql+=" where num between ? and ?";
		Connection con=DBCon.getConnection();
		
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, "%"+query+"%");
		pstmt.setInt(2, srow);
		pstmt.setInt(3, erow);
		
		//결과
		ResultSet rs=pstmt.executeQuery();

		List<Notice> list = new ArrayList<Notice>();
		while (rs.next()) {
			Notice n=new Notice();
			n.setSeq(rs.getString("seq"));
			n.setTitle(rs.getString("title"));
			n.setWriter(rs.getString("writer"));
			n.setContent(rs.getString("content"));
			n.setRegdate(rs.getDate("regdate"));
			n.setHit(rs.getInt("hit"));
			
			list.add(n);	
		}
		rs.close();
		pstmt.close();
		con.close();
		
		return list;
	}
	
	


	public int getCount(String field, String query) throws Exception {
		String sql="select count(*) cnt from notices where "+field+" like ?";
		Connection con=DBCon.getConnection();
		PreparedStatement pstmt=con.prepareStatement(sql);
		pstmt.setString(1, "%"+query+"%");
		ResultSet rs=pstmt.executeQuery();
		rs.next();
		int cnt=rs.getInt("cnt");
		return cnt;
	}
}
