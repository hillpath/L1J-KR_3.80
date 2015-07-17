package l1j.server.GameSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javolution.util.FastTable;
import java.util.List;

import l1j.server.L1DatabaseFactory;
import l1j.server.GameSystem.bean.RobotFishing;
import l1j.server.GameSystem.bean.RobotLocation;
import l1j.server.GameSystem.bean.RobotMent;
import l1j.server.GameSystem.bean.RobotName;
import l1j.server.server.command.executor.L1Robot;
import l1j.server.server.utils.SQLUtil;

public class RobotThread implements Runnable {
	
	// �ΰ����� �����带 ó���ص��Ǵ��� Ȯ�ο�.
	static private boolean running;
	// �����尡 ������ ��� �޽��� �ð���.
	static private long sleep;
	// �ڷ���Ʈ�� ��ǥ���
	static private List<RobotLocation> list_location;
	// �ڷ���Ʈ�� ��ǥ���
	static private List<RobotMent> list_ment;
	// ������ �ɸ��� ���
	static private List<RobotName> list_name;
	static public int list_name_idx;
	static private List<RobotFishing> list_fish;
	
	/**
	 * �ʱ�ȭ ó�� �Լ�.
	 */
	static public void init(){
		// ���� �ʱ�ȭ.
		sleep = 20;
		running = true;
		list_name_idx = 0;
		list_location = new FastTable<RobotLocation>();
		list_ment = new FastTable<RobotMent>();
		list_name = new FastTable<RobotName>();
		list_fish = new FastTable<RobotFishing>();
		// �κ� �ΰ����ɿ� ������ Ȱ��ȭ.
		new Thread(new RobotThread()).start();
		// ���κ��� ���� ����.
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();

			pstm = con.prepareStatement("SELECT * FROM robot_location where count = '1'");
			rs = pstm.executeQuery();
			while (rs.next()) {
				RobotLocation rl = new RobotLocation();
				rl.uid = rs.getInt("uid");
				rl.x = rs.getInt("x");
				rl.y = rs.getInt("y");
				rl.map = rs.getInt("map");
				rl.etc = rs.getString("etc");
				
				list_location.add(rl);
			}
			pstm = con.prepareStatement("SELECT * FROM robot_message");
			rs = pstm.executeQuery();
			while (rs.next()) {
				RobotMent rm = new RobotMent();
				rm.uid = rs.getInt("uid");
				rm.type = rs.getString("type");
				rm.ment = rs.getString("ment");
				
				list_ment.add(rm);
			}
			pstm = con.prepareStatement("SELECT * FROM robot_name");
			rs = pstm.executeQuery();
			while (rs.next()) {
				RobotName rn = new RobotName();
				rn.uid = rs.getInt("uid");
				rn.name = rs.getString("name");
				list_name.add(rn);
			}
			pstm = con.prepareStatement("SELECT * FROM robot_fishing");
			rs = pstm.executeQuery();
			while (rs.next()) {
				RobotFishing rn = new RobotFishing();
				rn.x = rs.getInt("x");
				rn.y = rs.getInt("y");
				rn.map = rs.getInt("mapid");
				rn.heading = rs.getInt("heading");
				rn.fishX = rs.getInt("fishingX");
				rn.fishY = rs.getInt("fishingY");
				list_fish.add(rn);
			}
		} catch (SQLException e) {
			
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
	
	public static boolean doesCharNameExist(String name) {
		boolean result = true;
		java.sql.Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("SELECT name FROM robot_name WHERE name=?");
			pstm.setString(1, name);
			rs = pstm.executeQuery();
			result = rs.next();
		} catch (SQLException e) {
			
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		return result;
	}
	
	/**
	 * ���� ó�� �Լ�.
	 */
	static public void close(){
		running = false;
	}
	public void run(){
		try {
			for( ; running ; ){
				// �޽�
				Thread.sleep(sleep);
				// �κ��� �ΰ����� Ȱ��ȭ.				
			}
		} catch (Exception e) {
			try {
			for( ; running ; ){		
				Thread.sleep(sleep);		// �κ��� �ΰ����� Ȱ��ȭ.
			}
			}catch (Exception f) {
				System.out.println("AI robot thread is terminated abnormally duplicated Error Resume!");
				System.out.println(f);
			}
			System.out.println("AI robot thread is terminated abnormally, restart!");
			System.out.println(e);
		}
	}
	
	/**
	 * ����� �ڷ���Ʈ ��ǥ����.
	 * @param type
	 * @return
	 */
	static public RobotLocation getLocation(){
		if(list_location.size() == 0)
			return null;
		
		return list_location.get( L1Robot.random(0, list_location.size()-1) );
	}
	
	static public List<RobotMent> getRobotMent(){
		return list_ment;
	}
	
	static public List<RobotName> getRobotName(){
		return list_name;
	}
	
	static public List<RobotFishing> getRobotFish(){
		return list_fish;
	}
	
	static public String getName(){
		try {
			// �̸���� ��ȸ.
			for( ; list_name_idx < list_name.size() ; ){
				String name = list_name.get(list_name_idx++).name;
				Connection con = null;
				PreparedStatement pstm = null;
				ResultSet rs = null;
				try {
					con = L1DatabaseFactory.getInstance().getConnection();
					pstm = con.prepareStatement("SELECT * FROM characters WHERE char_name=?");
					pstm.setString(1, name);
					rs = pstm.executeQuery();
					if(!rs.next())
						return name;
				} catch (SQLException e) {
				} finally {
					SQLUtil.close(rs);
					SQLUtil.close(pstm);
					SQLUtil.close(con);
				}
			}
		} catch (Exception e) { }
		// ��� ������ �̸� ���� Ȯ��.
		// �������� �̵������� �����Ұ�� ����.
		return null;
	}
	
	static public void reload() {
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		
		list_location.clear();
		list_ment.clear();
		list_name.clear();
		list_fish.clear();
		
		try {
			con = L1DatabaseFactory.getInstance().getConnection();

			pstm = con.prepareStatement("SELECT * FROM robot_location where count = '1'");
			rs = pstm.executeQuery();
			while (rs.next()) {
				RobotLocation rl = new RobotLocation();
				rl.uid = rs.getInt("uid");
				rl.x = rs.getInt("x");
				rl.y = rs.getInt("y");
				rl.map = rs.getInt("map");
				rl.etc = rs.getString("etc");
				
				list_location.add(rl);
			}
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			
			pstm = con.prepareStatement("SELECT * FROM robot_message");
			rs = pstm.executeQuery();
			while (rs.next()) {
				RobotMent rm = new RobotMent();
				rm.uid = rs.getInt("uid");
				rm.type = rs.getString("type");
				rm.ment = rs.getString("ment");
				
				list_ment.add(rm);
			}
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			
			pstm = con.prepareStatement("SELECT * FROM robot_name");
			rs = pstm.executeQuery();
			while (rs.next()) {
				RobotName rn = new RobotName();
				rn.uid = rs.getInt("uid");
				rn.name = rs.getString("name");
				list_name.add(rn);
			}
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			pstm = con.prepareStatement("SELECT * FROM robot_fishing");
			rs = pstm.executeQuery();
			while (rs.next()) {
				RobotFishing rn = new RobotFishing();
				rn.x = rs.getInt("x");
				rn.y = rs.getInt("y");
				rn.map = rs.getInt("mapid");
				rn.heading = rs.getInt("heading");
				rn.fishX = rs.getInt("fishingX");
				rn.fishY = rs.getInt("fishingY");
				list_fish.add(rn);
			}
		} catch (SQLException e) {
			
		} finally {
			SQLUtil.close(rs);
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}
}
