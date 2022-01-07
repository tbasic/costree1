package notice.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import notice.db.DBCon;
import notice.vo.Notice;

public class NoticeDao {
	
	
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
