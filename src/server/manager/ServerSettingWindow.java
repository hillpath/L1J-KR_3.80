package server.manager;
import java.awt.Color;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import l1j.server.Config;

@SuppressWarnings("serial")
public class ServerSettingWindow extends JInternalFrame {
	
	private JLabel lbl_MaxUser = null;
	private JTextField txt_MaxUser = null;
	private JLabel lbl_Exp = null;
	private JTextField txt_Exp = null;
		
	private	JLabel lbl_Item = null;
	private	JTextField txt_Item = null;
	private	JLabel lbl_Lawful = null;
	private	JTextField txt_Lawful = null;

	private	JLabel lbl_BalanceEnchant = null;
	private	JTextField txt_BalanceEnchant = null;
	private	JLabel lbl_ArmorEnchant = null;
	private	JTextField txt_ArmorEnchant = null;

	private	JLabel lbl_EarthEnchant = null;
	private	JTextField txt_EarthEnchant = null;
	private	JLabel lbl_FireEnchant = null;
	private	JTextField txt_FireEnchant = null;

	private	JLabel lbl_FeatherTime = null;
	private	JTextField txt_FeatherTime = null;
	private	JLabel lbl_FeatherClanNumber = null;
	private	JTextField txt_FeatherClanNumber = null;

	//
	private	JLabel lbl_ChatLevel = null;
	private	JTextField txt_ChatLevel = null;
	private	JLabel lbl_Adena = null;
	private	JTextField txt_Adena = null;

	private	JLabel lbl_Karma = null;
	private	JTextField txt_Karma = null;
	private	JLabel lbl_Weight= null;
	private	JTextField txt_Weight = null;

	private	JLabel lbl_WeaponEnchant = null;
	private	JTextField txt_WeaponEnchant = null;
	private	JLabel lbl_AccessoryEnchant = null;
	private	JTextField txt_AccessoryEnchant = null;

	private	JLabel lbl_WindEnchant = null;
	private	JTextField txt_WindEnchant = null;
	private	JLabel lbl_WaterEnchant = null;
	private	JTextField txt_WaterEnchant = null;

	private	JLabel lbl_FeatherNumber = null;
	private	JTextField txt_FeatherNumber = null;
	private	JLabel lbl_FeatherCastleNumber = null;
	private	JTextField txt_FeatherCastleNumber = null;
	
	public ServerSettingWindow() {
		super();
		
		initialize();
	}
	
	public void initialize() {
		title = "서버설정";
		closable = true;      
	    isMaximum = false;	
	    maximizable = false;
	    resizable = false;
        iconable = true;
	    isIcon = false;		
	    setSize(400, 360);
		setBounds(400, 0, 400, 360);
		setVisible(true);
		frameIcon = new ImageIcon("");
		setRootPaneCheckingEnabled(true);
	    updateUI();
	    
		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
			
		//
		lbl_MaxUser = new JLabel("MaxUser");
		txt_MaxUser = new JTextField();	
		txt_MaxUser.setForeground(Color.blue);
		lbl_Exp = new JLabel("Exp");
		txt_Exp = new JTextField();
		txt_Exp.setForeground(Color.blue);
		
		lbl_Item = new JLabel("Item");
		txt_Item = new JTextField();	
		txt_Item.setForeground(Color.blue);
		lbl_Lawful = new JLabel("Lawful");
		txt_Lawful = new JTextField();
		txt_Lawful.setForeground(Color.blue);
		
		lbl_BalanceEnchant = new JLabel("Balance Enchant");
		txt_BalanceEnchant = new JTextField();	
		txt_BalanceEnchant.setForeground(Color.blue);
		lbl_ArmorEnchant = new JLabel("Armor Enchant");
		txt_ArmorEnchant = new JTextField();
		txt_ArmorEnchant.setForeground(Color.blue);
		
		lbl_EarthEnchant = new JLabel("Earth Enchant");
		txt_EarthEnchant = new JTextField();	
		txt_EarthEnchant.setForeground(Color.blue);
		lbl_FireEnchant = new JLabel("FIre Enchant");
		txt_FireEnchant = new JTextField();
		txt_FireEnchant.setForeground(Color.blue);
		
		lbl_FeatherTime = new JLabel("Feather TIme");
		txt_FeatherTime = new JTextField();	
		txt_FeatherTime.setForeground(Color.blue);
		lbl_FeatherClanNumber = new JLabel("Feather Clan Number");
		txt_FeatherClanNumber = new JTextField();
		txt_FeatherClanNumber.setForeground(Color.blue);
		
		
		//
		lbl_ChatLevel = new JLabel("CHat");
		txt_ChatLevel = new JTextField();	
		txt_ChatLevel.setForeground(Color.blue);
		lbl_Adena = new JLabel("Adena");
		txt_Adena = new JTextField();
		txt_Adena.setForeground(Color.blue);
		
		lbl_Karma = new JLabel("Karma");
		txt_Karma = new JTextField();
		txt_Karma.setForeground(Color.blue);
		lbl_Weight = new JLabel("Weight");
		txt_Weight = new JTextField();
		txt_Weight.setForeground(Color.blue);
		
		lbl_WeaponEnchant = new JLabel("Weapon Enchant");
		txt_WeaponEnchant = new JTextField();	
		txt_WeaponEnchant.setForeground(Color.blue);
		lbl_AccessoryEnchant = new JLabel("Accessory Enchant");
		txt_AccessoryEnchant = new JTextField();
		txt_AccessoryEnchant.setForeground(Color.blue);
		
		lbl_WindEnchant = new JLabel("Wind Enchant");
		txt_WindEnchant = new JTextField();	
		txt_WindEnchant.setForeground(Color.blue);
		lbl_WaterEnchant = new JLabel("Water Enchant");
		txt_WaterEnchant = new JTextField();
		txt_WaterEnchant.setForeground(Color.blue);
		
		lbl_FeatherNumber = new JLabel("Feather Number");
		txt_FeatherNumber = new JTextField();	
		txt_FeatherNumber.setForeground(Color.blue);
		lbl_FeatherCastleNumber = new JLabel("Feather Castle Number");
		txt_FeatherCastleNumber = new JTextField();
		txt_FeatherCastleNumber.setForeground(Color.blue);
		
		
		txt_MaxUser.setText(Config.MAX_ONLINE_USERS + "");
		txt_ChatLevel.setText(Config.GLOBAL_CHAT_LEVEL + "");
		txt_Exp.setText(Config.RATE_XP + "");
		txt_Adena.setText(Config.RATE_DROP_ADENA + "");
		txt_Item.setText(Config.RATE_DROP_ITEMS + "");
		txt_Karma.setText(Config.RATE_KARMA + "");
		txt_Lawful.setText(Config.RATE_LAWFUL + "");
		txt_Weight.setText(Config.RATE_WEIGHT_LIMIT + "");
		//txt_BalanceEnchant.setText(Config.ENCHANT_CHANCE_BALANCE + "");
		txt_WeaponEnchant.setText(Config.ENCHANT_CHANCE_WEAPON + "");
		txt_ArmorEnchant.setText(Config.ENCHANT_CHANCE_ARMOR + "");
		txt_AccessoryEnchant.setText(Config.ENCHANT_CHANCE_ACCESSORY + "");
		//txt_EarthEnchant.setText(Config.ENCHANT_CHANCE_EARTH + "");
		//txt_WindEnchant.setText(Config.ENCHANT_CHANCE_WIND + "");
		//txt_FireEnchant.setText(Config.ENCHANT_CHANCE_FIRE + "");
		//txt_WaterEnchant.setText(Config.ENCHANT_CHANCE_WATER + "");
		txt_FeatherTime.setText(Config.FEATHER_TIME + "");
		txt_FeatherNumber.setText(Config.FEATHER_NUMBER + "");
	//	txt_FeatherClanNumber.setText(Config.CLAN_NUMBER + "");
	//	txt_FeatherCastleNumber.setText(Config.CASTLE_NUMBER + "");
		
		
		JButton btn_ok = new JButton("Ok");
		btn_ok.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				try {
					Config.MAX_ONLINE_USERS = Short.parseShort(txt_MaxUser.getText());	
					Config.GLOBAL_CHAT_LEVEL = Short.parseShort(txt_ChatLevel.getText());					
					Config.RATE_XP = Double.parseDouble(txt_Exp.getText());
					Config.RATE_DROP_ADENA = Double.parseDouble(txt_Adena.getText());
					Config.RATE_DROP_ITEMS = Double.parseDouble(txt_Item.getText());
					Config.RATE_KARMA = Double.parseDouble(txt_Karma.getText());
					Config.RATE_LAWFUL = Double.parseDouble(txt_Lawful.getText());
					Config.RATE_WEIGHT_LIMIT = Double.parseDouble(txt_Weight.getText());
					//Config.ENCHANT_CHANCE_BALANCE = Integer.parseInt(txt_BalanceEnchant.getText());
					Config.ENCHANT_CHANCE_WEAPON = Integer.parseInt(txt_WeaponEnchant.getText());
					Config.ENCHANT_CHANCE_ARMOR = Integer.parseInt(txt_ArmorEnchant.getText());
					Config.ENCHANT_CHANCE_ACCESSORY = Integer.parseInt(txt_AccessoryEnchant.getText());
					//Config.ENCHANT_CHANCE_EARTH = Integer.parseInt(txt_EarthEnchant.getText());
					//Config.ENCHANT_CHANCE_WIND = Integer.parseInt(txt_WindEnchant.getText());
					//Config.ENCHANT_CHANCE_FIRE = Integer.parseInt(txt_FireEnchant.getText());
					//Config.ENCHANT_CHANCE_WATER = Integer.parseInt(txt_WaterEnchant.getText());
					Config.FEATHER_TIME = Integer.parseInt(txt_FeatherTime.getText());
					Config.FEATHER_NUMBER = Integer.parseInt(txt_FeatherNumber.getText());
				//	Config.CLAN_NUMBER = Integer.parseInt(txt_FeatherClanNumber.getText());
				//	Config.CASTLE_NUMBER = Integer.parseInt(txt_FeatherCastleNumber.getText());
					
					eva.jSystemLogWindow.append("Max Online : " + Config.MAX_ONLINE_USERS + "\n", "Blue");
					eva.jSystemLogWindow.append("Global Chat : " + Config.GLOBAL_CHAT_LEVEL + "\n", "Blue");
					eva.jSystemLogWindow.append("Rate XP : " + Config.RATE_XP + "\n", "Blue");
					eva.jSystemLogWindow.append("Adena Drop Rate : " + Config.RATE_DROP_ADENA + "\n", "Blue");
					eva.jSystemLogWindow.append("Drop Item Rate : " + Config.RATE_DROP_ITEMS + "\n", "Blue");
					eva.jSystemLogWindow.append("Karma Rate : " + Config.RATE_KARMA + "\n", "Blue");
					eva.jSystemLogWindow.append("Law Rate : " + Config.RATE_LAWFUL + "\n", "Blue");
					eva.jSystemLogWindow.append("Weight Rate : " + Config.RATE_WEIGHT_LIMIT + "\n", "Blue");
					//eva.jSystemLogWindow.append("밸런스인챈 : " + Config.ENCHANT_CHANCE_BALANCE + "\n", "Blue");
					eva.jSystemLogWindow.append("Weapon Enchant : " + Config.ENCHANT_CHANCE_WEAPON + "\n", "Blue");
					eva.jSystemLogWindow.append("Armor Enchant : " + Config.ENCHANT_CHANCE_ARMOR + "\n", "Blue");
					eva.jSystemLogWindow.append("Accessory Enchant : " + Config.ENCHANT_CHANCE_ACCESSORY + "\n", "Blue");
					//eva.jSystemLogWindow.append("땅속성인챈 : " + Config.ENCHANT_CHANCE_EARTH + "\n", "Blue");
					//eva.jSystemLogWindow.append("바람속성인챈 : " + Config.ENCHANT_CHANCE_WIND + "\n", "Blue");
					//eva.jSystemLogWindow.append("불속성인챈 : " + Config.ENCHANT_CHANCE_FIRE + "\n", "Blue");
					//eva.jSystemLogWindow.append("물속성인챈 : " + Config.ENCHANT_CHANCE_WATER + "\n", "Blue");
					eva.jSystemLogWindow.append("Feather Time : " + Config.FEATHER_TIME + "\n", "Blue");
					eva.jSystemLogWindow.append("Feather Number : " + Config.FEATHER_NUMBER + "\n", "Blue");
				//	eva.jSystemLogWindow.append("깃털혈맹지급수 : " + Config.CLAN_NUMBER + "\n", "Blue");
				//	eva.jSystemLogWindow.append("깃털성혈지급수 : " + Config.CASTLE_NUMBER + "\n", "Blue");
					eva.jSystemLogWindow.append("정상적으로 배율이 변경되었습니다." + "\n", "Blue");
					
					
					
					JOptionPane.showMessageDialog(null, "정상적으로 배율이 변경되었습니다.", " Server Message", JOptionPane.INFORMATION_MESSAGE);
					
					setClosed(true);			
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		
		JButton btn_cancel = new JButton("Cancel");
		btn_cancel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				try {
					setClosed(true);					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
		
		//Main
		GroupLayout.SequentialGroup main_horizontal_grp = layout.createSequentialGroup();
		
		GroupLayout.SequentialGroup horizontal_grp = layout.createSequentialGroup();
		GroupLayout.SequentialGroup vertical_grp   = layout.createSequentialGroup();
		
		//Main
		GroupLayout.ParallelGroup main = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		
		// 레이블이 들어갈 열
		GroupLayout.ParallelGroup col1 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		// 텍스트필드
		GroupLayout.ParallelGroup col2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		// 레이블이 들어갈 열
		GroupLayout.ParallelGroup col3 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		// 텍스트필드
		GroupLayout.ParallelGroup col4 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		
		
		main.addGroup(horizontal_grp);
		main_horizontal_grp.addGroup(main);
			
		layout.setHorizontalGroup(main_horizontal_grp);
		layout.setVerticalGroup(vertical_grp);
		
		
		col1.addComponent(lbl_MaxUser)
			.addComponent(lbl_Exp)
			.addComponent(lbl_Item)
			.addComponent(lbl_Lawful)
			.addComponent(lbl_BalanceEnchant)
			.addComponent(lbl_ArmorEnchant)
			.addComponent(lbl_EarthEnchant)
			.addComponent(lbl_FireEnchant)
			.addComponent(lbl_FeatherTime)
			.addComponent(lbl_FeatherClanNumber);			
		
		col2.addComponent(txt_MaxUser)
			.addComponent(txt_Exp)
			.addComponent(txt_Item)
			.addComponent(txt_Lawful)
			.addComponent(txt_BalanceEnchant)
			.addComponent(txt_ArmorEnchant)
			.addComponent(txt_EarthEnchant)
			.addComponent(txt_FireEnchant)
			.addComponent(txt_FeatherTime)
			.addComponent(txt_FeatherClanNumber);
		
		col3.addComponent(lbl_ChatLevel)
			.addComponent(lbl_Adena)
			.addComponent(lbl_Karma)
			.addComponent(lbl_Weight)
			.addComponent(lbl_WeaponEnchant)
			.addComponent(lbl_AccessoryEnchant)
			.addComponent(lbl_WindEnchant)
			.addComponent(lbl_WaterEnchant)
			.addComponent(lbl_FeatherNumber)
			.addComponent(lbl_FeatherCastleNumber);
		
		col4.addComponent(txt_ChatLevel)
			.addComponent(txt_Adena)
			.addComponent(txt_Karma)
			.addComponent(txt_Weight)
			.addComponent(txt_WeaponEnchant)
			.addComponent(txt_AccessoryEnchant)
			.addComponent(txt_WindEnchant)
			.addComponent(txt_WaterEnchant)
			.addComponent(txt_FeatherNumber)
			.addComponent(txt_FeatherCastleNumber);
		
		
		horizontal_grp.addGap(10).addContainerGap().addGroup(col1).addContainerGap();
		horizontal_grp.addGap(10).addContainerGap().addGroup(col2).addContainerGap();
		horizontal_grp.addGap(10).addContainerGap().addGroup(col3).addContainerGap();
		horizontal_grp.addGap(10).addContainerGap().addGroup(col4).addContainerGap();
				
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_MaxUser).addComponent(txt_MaxUser).addComponent(lbl_ChatLevel).addComponent(txt_ChatLevel));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_Exp).addComponent(txt_Exp).addComponent(lbl_Adena).addComponent(txt_Adena));				
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_Item).addComponent(txt_Item).addComponent(lbl_Karma).addComponent(txt_Karma));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_Lawful).addComponent(txt_Lawful).addComponent(lbl_Weight).addComponent(txt_Weight));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_BalanceEnchant).addComponent(txt_BalanceEnchant).addComponent(lbl_WeaponEnchant).addComponent(txt_WeaponEnchant));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_ArmorEnchant).addComponent(txt_ArmorEnchant).addComponent(lbl_AccessoryEnchant).addComponent(txt_AccessoryEnchant));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_EarthEnchant).addComponent(txt_EarthEnchant).addComponent(lbl_WindEnchant).addComponent(txt_WindEnchant));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_FireEnchant).addComponent(txt_FireEnchant).addComponent(lbl_WaterEnchant).addComponent(txt_WaterEnchant));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_FeatherTime).addComponent(txt_FeatherTime).addComponent(lbl_FeatherNumber).addComponent(txt_FeatherNumber));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(lbl_FeatherClanNumber).addComponent(txt_FeatherClanNumber).addComponent(lbl_FeatherCastleNumber).addComponent(txt_FeatherCastleNumber));
		
		main.addGroup(layout.createSequentialGroup().addGap(130, 130, 130).addComponent(btn_ok).addGap(10).addComponent(btn_cancel));	
		vertical_grp.addGap(15).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addGap(19, 19, 19).addComponent(btn_ok).addComponent(btn_cancel));

	}
}
