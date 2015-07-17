package server.manager;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.RandomAccessFile;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import l1j.server.Config;
import l1j.server.SpecialEventHandler;
import l1j.server.GameSystem.RobotThread;
import l1j.server.GameSystem.Boss.L1BossCycle;
import l1j.server.server.datatables.AccessoryEnchantList;
import l1j.server.server.datatables.ArmorEnchantList;
import l1j.server.server.datatables.AutoLoot;
import l1j.server.server.datatables.CastleTable;
import l1j.server.server.datatables.ClanTable;
import l1j.server.server.datatables.DropItemTable;
import l1j.server.server.datatables.DropTable;
import l1j.server.server.datatables.IpTable;
import l1j.server.server.datatables.ItemTable;
import l1j.server.server.datatables.MobSkillTable;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.datatables.PolyTable;
import l1j.server.server.datatables.ResolventTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.datatables.WeaponEnchantList;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1TreasureBox;
import server.GameServer;
import server.Server;

/**
 * 
 * @author code
 */

public class eva {
	public static int saveCount = 0;
	
	public static int width = 0;
	public static int height = 0;
	
	public static final Object lock = new Object();
	public static String date = "";
	public static String time = "";
	
	public static boolean isServerStarted;
	public static int userCount;
	
	public static JFrame jJFrame = null;
	private JMenuBar jJMenuBar = null;
	private Container jContainer = null;
	private BorderLayout jBorderLayout = new BorderLayout();
	public static JDesktopPane jJDesktopPane = new JDesktopPane();
	
	// Server Settings
	public static ServerSettingWindow jServerSettingWindow = null;

	// Server logs
	public static ServerLogWindow jSystemLogWindow = null;      // 시스템 - System
	public static ServerLogWindow jWorldChatLogWindow = null;   // 월드채팅 - World Chat
	public static ServerLogWindow jNomalChatLogWindow = null;   // 일반채팅 - General Chat
	public static ServerLogWindow jWhisperChatLogWindow = null; // 귓속말채팅 - Whisper Chat
	public static ServerLogWindow jClanChatLogWindow = null;    // 혈맹채팅 - Clan Chat
	public static ServerLogWindow jPartyChatLogWindow = null;   // 파티채팅 - Party Chat
	public static ServerLogWindow jTradeChatLogWindow = null;   // 장사채팅 - Trade Chat
	
	public static ServerLogWindow jWareHouseLogWindow = null;   // 창고 - Warehouse
	public static ServerLogWindow jTradeLogWindow = null;       // 거래 - Trade
	public static ServerLogWindow jEnchantLogWindow = null;     // 인챈 - Enchant
	public static ServerLogWindow jObserveLogWindow = null;     // 감시 - Surveillance
	public static ServerLogWindow jBugLogWindow = null;         // 버그 - Bug
	public static ServerLogWindow jCommandLogWindow = null;     // 명령 - Commands
	public static ServerChatLogWindow jServerChatLogWindow = null;     // 다중채팅 모니터 - Multiple Chat Monitor
	public static ServerLatterLogWindow jServerLatterLogWindow = null; // 편지 모니터 - Write Monitor
	public static ServerUserMoniterWindow jServerUserMoniterWindow = null; // 아이템 모니터 - Items Monitor
	
	
	// 멀티채팅
	public static ServerMultiChatLogWindow jServerMultiChatLogWindow = null;
	
	// 유저정보
	public static ServerUserInfoWindow jServerUserInfoWindow = null;
	
	// 메세지
	public static final String NoServerStartMSG = "The server is not running."; // original: 서버가 실행되지 않았습니다
	public static final String realExitServer = "Are you sure you want to shut down the server?"; // original: 서버를 종료하시겠습니까?
	public static final String blankSetUser = "The user is not specified."; // original: 유저가 지정되지 않았습니다.
	public static final String characterSaveFail = "Failed to save character information."; // original: 캐릭터 정보 저장 실패
	public static final String characterSaveSuccess = "Character information has been saved."; // original: 캐릭터 정보 저장 성공
	public static final String NoConnectUser = " Character does not have access."; // original: 캐릭터는 접속해 있지 않습니다.
	public static final String UserDelete = "Character has been online for a week, are you sure you want to delete?"; // original: 일주일동안 접속하지 않은 캐릭터를 삭제하시겠습니까?
	public static final String ReloadMSG = "Are you sure you want to reload?"; // original: 리로드 하시겠습니까?
	public static final String AllBuffMSG = "Are you sure you want to cast all the buffs?"; // original: 전체버프를 시전하시겠습니까?
	public static final String SearchNameMSG = "Please enter your search term."; // original: 검색어를 입력하세요.
	
	public eva() {
		initialize();		
	}
	
	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				new eva();					
			}
		});
	}
	
	private void initialize() {
		try {
			Server.createServer().start();
			
			UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");			
			//UIManager.setLookAndFeel("net.sourceforge.napkinlaf.NapkinLookAndFeel");
			JFrame.setDefaultLookAndFeelDecorated(true);		
			if (jJFrame == null) {
				jJFrame = new JFrame();
				jJFrame.setIconImage(new ImageIcon("").getImage());
				jJFrame.setSize(1200, 800);
				jJFrame.setVisible(true);
				jJFrame.setTitle(":::: Lineage Server Manager ::::");
				jJFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				jContainer = jJFrame.getContentPane();
				jContainer.setLayout(jBorderLayout);
				jContainer.add("Center", jJDesktopPane);	
				
			}	
			if (jJMenuBar == null) {
				jJMenuBar = new JMenuBar();
				jJFrame.setJMenuBar(jJMenuBar);
				
				JMenu jJMenu1 = new JMenu("File(F)");
				JMenu jJMenu2 = new JMenu("Monitors(M)");
				JMenu jJMenu3 = new JMenu("Help(H)");
				JMenu jJMenu4 = new JMenu("Reload(R)");
				JMenu jJMenu5 = new JMenu("Info(I)");
				
				jJMenu1.setMnemonic(KeyEvent.VK_F); // File
				jJMenu2.setMnemonic(KeyEvent.VK_M); // Monitor
				jJMenu3.setMnemonic(KeyEvent.VK_H); // Help
				jJMenu4.setMnemonic(KeyEvent.VK_R); // Reload
				jJMenu5.setMnemonic(KeyEvent.VK_I); // Info
				
				// File(F)
				JMenuItem serverSet = new JMenuItem("Server Settings");			
				serverSet.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.CTRL_DOWN_MASK));			
				serverSet.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						jServerSettingWindow = new ServerSettingWindow();						
						jJDesktopPane.add(jServerSettingWindow, 0);	
						
						jServerSettingWindow.setLocation((jJFrame.getContentPane().getSize().width / 2) - (jServerSettingWindow.getContentPane().getSize().width / 2), (jJFrame.getContentPane().getSize().height / 2) - (jServerSettingWindow.getContentPane().getSize().height / 2));
					}
				});
				JMenuItem serverSave = new JMenuItem("Server Save");
				serverSave.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_DOWN_MASK));			
				serverSave.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {	
						GameServer.getInstance().saveAllCharInfo();
						infoMsg(characterSaveSuccess);
						jSystemLogWindow.append(getLogTime() + "　[Tools run server storage" + "\n", "Red");
					}
				});			
				JMenuItem serverExit = new JMenuItem("Server Shutdown");			
				serverExit.setAccelerator(KeyStroke.getKeyStroke(',', InputEvent.CTRL_DOWN_MASK));			
				serverExit.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg(realExitServer) == 0) {
							GameServer.getInstance().saveAllCharInfo();
							GameServer.getInstance().shutdownWithCountdown(10);
							jSystemLogWindow.append(getLogTime() + "　[Tools run server shutdown" + "\n", "Red");
						}
					}
				});
				JMenuItem serverNowExit = new JMenuItem("Exit Now");			
				serverNowExit.setAccelerator(KeyStroke.getKeyStroke('.', InputEvent.CTRL_DOWN_MASK));			
				serverNowExit.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg(realExitServer) == 0) {
							GameServer.getInstance().saveAllCharInfo();
							GameServer.getInstance().shutdownWithCountdown(0);
							jSystemLogWindow.append(getLogTime() + "　[Tools run exit now" + "\n", "Red");
						}			
					}
				});	
				
				jJMenu1.add(serverSet);
				jJMenu1.add(serverSave);
				jJMenu1.add(serverExit);
				jJMenu1.add(serverNowExit);
				
				// Monitor(M)
				JMenuItem worldChatLogWindow = new JMenuItem("World Chat");			
				worldChatLogWindow.setAccelerator(KeyStroke.getKeyStroke('1', InputEvent.CTRL_DOWN_MASK));			
				worldChatLogWindow.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {						
						if (jWorldChatLogWindow != null && jWorldChatLogWindow.isClosed()) {
							jWorldChatLogWindow = null;
						}
						
						if (jWorldChatLogWindow == null) {
							jWorldChatLogWindow = new ServerLogWindow("World Chat", 20, 20, width, height, true, true);				
							jJDesktopPane.add(jWorldChatLogWindow, 0);
						}
					}
				});	
				
				JMenuItem nomalChatLogWindow = new JMenuItem("General Chat");			
				nomalChatLogWindow.setAccelerator(KeyStroke.getKeyStroke('2', InputEvent.CTRL_DOWN_MASK));			
				nomalChatLogWindow.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {						
						if (jNomalChatLogWindow != null && jNomalChatLogWindow.isClosed()) {
							jNomalChatLogWindow = null;
						}
						
						if (jNomalChatLogWindow == null) {
							jNomalChatLogWindow = new ServerLogWindow("General Chat", 20, 20, width, height, true, true);				
							jJDesktopPane.add(jNomalChatLogWindow, 0);
						}
					}
				});	
				
				JMenuItem whisperChatLogWindow = new JMenuItem("Whisper Chat");			
				whisperChatLogWindow.setAccelerator(KeyStroke.getKeyStroke('3', InputEvent.CTRL_DOWN_MASK));			
				whisperChatLogWindow.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (jWhisperChatLogWindow != null && jWhisperChatLogWindow.isClosed()) {
							jWhisperChatLogWindow = null;
						}
						
						if (jWhisperChatLogWindow == null) {
							jWhisperChatLogWindow = new ServerLogWindow("Whisper Chat", 20, 20, width, height, true, true);				
							jJDesktopPane.add(jWhisperChatLogWindow, 0);
						}		
					}
				});	
				
				JMenuItem clanChatLogWindow = new JMenuItem("Clan Chat");			
				clanChatLogWindow.setAccelerator(KeyStroke.getKeyStroke('4', InputEvent.CTRL_DOWN_MASK));			
				clanChatLogWindow.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (jClanChatLogWindow != null && jClanChatLogWindow.isClosed()) {
							jClanChatLogWindow = null;
						}
						
						if (jClanChatLogWindow == null) {
							jClanChatLogWindow = new ServerLogWindow("Clan Chat", 20, 20, width, height, true, true);				
							jJDesktopPane.add(jClanChatLogWindow, 0);
						}		
					}
				});	
				
				JMenuItem partyChatLogWindow = new JMenuItem("Party Chat");			
				partyChatLogWindow.setAccelerator(KeyStroke.getKeyStroke('5', InputEvent.CTRL_DOWN_MASK));			
				partyChatLogWindow.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (jPartyChatLogWindow != null && jPartyChatLogWindow.isClosed()) {
							jPartyChatLogWindow = null;
						}
						
						if (jPartyChatLogWindow == null) {
							jPartyChatLogWindow = new ServerLogWindow("Party Chat", 20, 20, width, height, true, true);				
							jJDesktopPane.add(jPartyChatLogWindow, 0);
						}		
					}
				});	
				
				JMenuItem tradeChatLogWindow = new JMenuItem("Trade Chat");			
				tradeChatLogWindow.setAccelerator(KeyStroke.getKeyStroke('6', InputEvent.CTRL_DOWN_MASK));			
				tradeChatLogWindow.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (jTradeChatLogWindow != null && jTradeChatLogWindow.isClosed()) {
							jTradeChatLogWindow = null;
						}
						
						if (jTradeChatLogWindow == null) {
							jTradeChatLogWindow = new ServerLogWindow("Trade Chat", 20, 20, width, height, true, true);				
							jJDesktopPane.add(jTradeChatLogWindow, 0);
						}		
					}
				});	
				
				JMenuItem wareHouseLogWindow = new JMenuItem("Warehouse Log");			
				wareHouseLogWindow.setAccelerator(KeyStroke.getKeyStroke('7', InputEvent.CTRL_DOWN_MASK));			
				wareHouseLogWindow.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (jWareHouseLogWindow != null && jWareHouseLogWindow.isClosed()) {
							jWareHouseLogWindow = null;
						}
						
						if (jWareHouseLogWindow == null) {
							jWareHouseLogWindow = new ServerLogWindow("Warehouse Log", 20, 20, width, height, true, true);				
							jJDesktopPane.add(jWareHouseLogWindow, 0);
						}		
					}
				});	
				
				JMenuItem tradeLogWindow = new JMenuItem("Trade Log");			
				tradeLogWindow.setAccelerator(KeyStroke.getKeyStroke('8', InputEvent.CTRL_DOWN_MASK));			
				tradeLogWindow.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (jTradeLogWindow != null && jTradeLogWindow.isClosed()) {
							jTradeLogWindow = null;
						}
						
						if (jTradeLogWindow == null) {
							jTradeLogWindow = new ServerLogWindow("Trade Log", 20, 20, width, height, true, true);				
							jJDesktopPane.add(jTradeLogWindow, 0);
						}		
					}
				});
				
				JMenuItem enchatLogWindow = new JMenuItem("Enchant Log");			
				enchatLogWindow.setAccelerator(KeyStroke.getKeyStroke('9', InputEvent.CTRL_DOWN_MASK));			
				enchatLogWindow.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (jEnchantLogWindow != null && jEnchantLogWindow.isClosed()) {
							jEnchantLogWindow = null;
						}
						
						if (jEnchantLogWindow == null) {
							jEnchantLogWindow = new ServerLogWindow("Enchant Log", 20, 20, width, height, true, true);				
							jJDesktopPane.add(jEnchantLogWindow, 0);
						}		
					}
				});
				
				JMenuItem observeLogWindow = new JMenuItem("Surveillance");			
				observeLogWindow.setAccelerator(KeyStroke.getKeyStroke('0', InputEvent.CTRL_DOWN_MASK));			
				observeLogWindow.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (jObserveLogWindow != null && jObserveLogWindow.isClosed()) {
							jObserveLogWindow = null;
						}
						
						if (jObserveLogWindow == null) {
							jObserveLogWindow = new ServerLogWindow("Surveillance", 20, 20, width, height, true, true);				
							jJDesktopPane.add(jObserveLogWindow, 0);
						}		
					}
				});
				
				JMenuItem bugLogWindow = new JMenuItem("Bug Log");			
				bugLogWindow.setAccelerator(KeyStroke.getKeyStroke('-', InputEvent.CTRL_DOWN_MASK));			
				bugLogWindow.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (jBugLogWindow != null && jBugLogWindow.isClosed()) {
							jBugLogWindow = null;
						}
						
						if (jBugLogWindow == null) {
							jBugLogWindow = new ServerLogWindow("Bug Log", 20, 20, width, height, true, true);				
							jJDesktopPane.add(jBugLogWindow, 0);
						}		
					}
				});
				
				JMenuItem commandLogWindow = new JMenuItem("Command Log");			
				commandLogWindow.setAccelerator(KeyStroke.getKeyStroke('=', InputEvent.CTRL_DOWN_MASK));			
				commandLogWindow.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (jCommandLogWindow != null && jCommandLogWindow.isClosed()) {
							jCommandLogWindow = null;
						}
						
						if (jCommandLogWindow == null) {
							jCommandLogWindow = new ServerLogWindow("Command Log", 20, 20, width, height, true, true);				
							jJDesktopPane.add(jCommandLogWindow, 0);
						}		
					}
				});
				
				JMenuItem serverChatLogWindow = new JMenuItem("Multi Chat Monitor");			
				serverChatLogWindow.setAccelerator(KeyStroke.getKeyStroke('\\', InputEvent.CTRL_DOWN_MASK));			
				serverChatLogWindow.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (jServerChatLogWindow != null && jServerChatLogWindow.isClosed()) {
							jServerChatLogWindow = null;
						}
						
						if (jServerChatLogWindow == null) {
							jServerChatLogWindow = new ServerChatLogWindow("Multi Chat Window", 20, 20, width, height, true, true);				
							jJDesktopPane.add(jServerChatLogWindow, 0);
						}		
					}
				});
				
				JMenuItem serverUserMoniterWindow = new JMenuItem("Item Monitor");			
				serverUserMoniterWindow.setAccelerator(KeyStroke.getKeyStroke('B', InputEvent.CTRL_DOWN_MASK));			
				serverUserMoniterWindow.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (jServerUserMoniterWindow != null && jServerUserMoniterWindow.isClosed()) {
							jServerUserMoniterWindow = null;
						}
						
						if (jServerUserMoniterWindow == null) {
							jServerUserMoniterWindow = new ServerUserMoniterWindow("Item Monitor", 20, 20, width + 70, height + 150, true, true);				
							jJDesktopPane.add(jServerUserMoniterWindow, 0);
						}		
					}
				});
				
						
				jJMenu2.add(worldChatLogWindow);
				jJMenu2.add(nomalChatLogWindow);
				jJMenu2.add(whisperChatLogWindow);
				jJMenu2.add(clanChatLogWindow);
				jJMenu2.add(partyChatLogWindow);
				jJMenu2.add(tradeChatLogWindow);
				jJMenu2.add(wareHouseLogWindow);
				jJMenu2.add(tradeLogWindow);
				jJMenu2.add(enchatLogWindow);
				jJMenu2.add(observeLogWindow);
				jJMenu2.add(bugLogWindow);
				jJMenu2.add(commandLogWindow);
				jJMenu2.add(serverChatLogWindow);
				jJMenu2.add(serverUserMoniterWindow);
				
				// Help(H)
				JMenuItem characterDelete = new JMenuItem("Character Delete");			
				characterDelete.setAccelerator(KeyStroke.getKeyStroke('D', InputEvent.CTRL_DOWN_MASK));			
				characterDelete.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (isServerStarted) {
							if (QMsg(UserDelete) == 0) {
								try {
									Server.createServer().clearDB();
								} catch (SQLException ex) {
	
								}
								jSystemLogWindow.append(getLogTime() + "　[도구 실행]　character delete complete." + "\n", "Red");
							} 
						} else {
							errorMsg(NoServerStartMSG);
						}
					}
				});	
				
				JMenuItem allBuff = new JMenuItem("All Buff");			
				allBuff.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.CTRL_DOWN_MASK));			
				allBuff.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (isServerStarted) {
							if (QMsg(AllBuffMSG) == 0) {
								SpecialEventHandler.getInstance().doAllBuf();
								jSystemLogWindow.append(getLogTime() + "　[도구 실행] All Buff." + "\n", "Blue");
							}	
						} else {
							errorMsg(NoServerStartMSG);
						}
					}
				});	
				
				/*JMenuItem autoShop = new JMenuItem("무인상점");			
				autoShop.setAccelerator(KeyStroke.getKeyStroke('U', InputEvent.CTRL_DOWN_MASK));			
				autoShop.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (isServerStarted) {
							if (AutoShopManager.getInstance().isAutoShop()) {
								if (QMsg("무인상점이 현재 활성화 중입니다. 비활성화 하시겠습니까?") == 0) {
									AutoShopManager.getInstance().isAutoShop(false);
									jSystemLogWindow.append(getLogTime() + "　[도구 실행]　무인상점 비활성화." + "\n", "Red");
								}		
							} else {
								if (QMsg("무인상점이 현재 비활성화 중입니다. 활성화 하시겠습니까?") == 0) {
									AutoShopManager.getInstance().isAutoShop(true);
									jSystemLogWindow.append(getLogTime() + "　[도구 실행]　무인상점 활성화." + "\n", "Blue");
								}
							}
						} else {
							errorMsg(NoServerStartMSG);
						}
					}
				});*/	
				
				JMenuItem chat = new JMenuItem("World Chat");			
				chat.setAccelerator(KeyStroke.getKeyStroke('U', InputEvent.CTRL_DOWN_MASK));			
				chat.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (isServerStarted) {
							if (L1World.getInstance().isWorldChatElabled()) {
								if (QMsg("World Chat is currently active. Are you sure you want to deactivate?") == 0) {
									SpecialEventHandler.getInstance().doNotChatEveryone();
									jSystemLogWindow.append(getLogTime() + "　[도구 실행]　World Chat Disabled." + "\n", "Red");
								}		
							} else {
								if (QMsg("World chat is currently disabled. Are you sure you want to activate?") == 0) {
									SpecialEventHandler.getInstance().doChatEveryone();
									jSystemLogWindow.append(getLogTime() + "　[도구 실행]　World Chat Activate." + "\n", "Blue");
								}
							}
						} else {
							errorMsg(NoServerStartMSG);
						}
					}
				});	
				
				jJMenu3.add(characterDelete);
				jJMenu3.add(allBuff);
			//	jJMenu3.add(autoShop);
				jJMenu3.add(chat);
				
				// 리로드(R)
				/*
				JMenuItem noticeReload = new JMenuItem("Notice");			
				noticeReload.setAccelerator(KeyStroke.getKeyStroke('Q', InputEvent.SHIFT_DOWN_MASK));			
				noticeReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("Notice " + ReloadMSG) == 0) {
							NoticeTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] Notice Update Complete..." + "\n", "Red");
						}			
					}
				});	*/			
				
				JMenuItem configReload = new JMenuItem("Config");			
				configReload.setAccelerator(KeyStroke.getKeyStroke('W', InputEvent.SHIFT_DOWN_MASK));			
				configReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("Config " + ReloadMSG) == 0) {
							Config.load();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] Config Update Complete..." + "\n", "Red");
						}			
					}
				});
				/*				
				JMenuItem weaponAddDamageReload = new JMenuItem("WeaponAddDamage");			
				weaponAddDamageReload.setAccelerator(KeyStroke.getKeyStroke('E', InputEvent.SHIFT_DOWN_MASK));			
				weaponAddDamageReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("WeaponAddDamage " + ReloadMSG) == 0) {
							WeaponAddDamage.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] WeaponAddDamage Update Complete..." + "\n", "Red");
						}			
					}
				});*/
				
				JMenuItem weaponEnchantListReload = new JMenuItem("WeaponEnchantList");			
				weaponEnchantListReload.setAccelerator(KeyStroke.getKeyStroke('R', InputEvent.SHIFT_DOWN_MASK));			
				weaponEnchantListReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("WeaponEnchantList " + ReloadMSG) == 0) {
							WeaponEnchantList.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] WeaponEnchantList Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem armorEnchantListReload = new JMenuItem("ArmorEnchantList");			
				armorEnchantListReload.setAccelerator(KeyStroke.getKeyStroke('T', InputEvent.SHIFT_DOWN_MASK));			
				armorEnchantListReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("ArmorEnchantList " + ReloadMSG) == 0) {
							ArmorEnchantList.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] ArmorEnchantList Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem accessoryEnchantListReload = new JMenuItem("AccessoryEnchantList");			
				accessoryEnchantListReload.setAccelerator(KeyStroke.getKeyStroke('Y', InputEvent.SHIFT_DOWN_MASK));			
				accessoryEnchantListReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("AccessoryEnchantList " + ReloadMSG) == 0) {
							AccessoryEnchantList.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] AccessoryEnchantList Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				
				JMenuItem dropReload = new JMenuItem("Drop");			
				dropReload.setAccelerator(KeyStroke.getKeyStroke('U', InputEvent.SHIFT_DOWN_MASK));			
				dropReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("Drop " + ReloadMSG) == 0) {
							DropTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] Drop Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem dropItemReload = new JMenuItem("DropItem");			
				dropItemReload.setAccelerator(KeyStroke.getKeyStroke('I', InputEvent.SHIFT_DOWN_MASK));			
				dropItemReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("DropItem " + ReloadMSG) == 0) {
							DropItemTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] DropItem Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem polyReload = new JMenuItem("Poly");			
				polyReload.setAccelerator(KeyStroke.getKeyStroke('O', InputEvent.SHIFT_DOWN_MASK));			
				polyReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("Poly " + ReloadMSG) == 0) {
							PolyTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] Poly Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem resolventReload = new JMenuItem("Resolvent");			
				resolventReload.setAccelerator(KeyStroke.getKeyStroke('P', InputEvent.SHIFT_DOWN_MASK));			
				resolventReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("Resolvent " + ReloadMSG) == 0) {
							ResolventTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] Resolvent Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem treasureBoxReload = new JMenuItem("TreasureBox");			
				treasureBoxReload.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.SHIFT_DOWN_MASK));			
				treasureBoxReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("TreasureBox " + ReloadMSG) == 0) {
							L1TreasureBox.load();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] TreasureBox Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem skillsReload = new JMenuItem("Skills");			
				skillsReload.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.SHIFT_DOWN_MASK));			
				skillsReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("Skills " + ReloadMSG) == 0) {
							SkillsTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] Skills Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem mobSkillReload = new JMenuItem("MobSkill");			
				mobSkillReload.setAccelerator(KeyStroke.getKeyStroke('D', InputEvent.SHIFT_DOWN_MASK));			
				mobSkillReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("MobSkill " + ReloadMSG) == 0) {
							MobSkillTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] MobSkill Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem mapFixKeyReload = new JMenuItem("MapFixKey");			
				mapFixKeyReload.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.SHIFT_DOWN_MASK));			
				mapFixKeyReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("MapFixKey " + ReloadMSG) == 0) {
							MobSkillTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] MapFixKey Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem itemReload = new JMenuItem("Item");			
				itemReload.setAccelerator(KeyStroke.getKeyStroke('G', InputEvent.SHIFT_DOWN_MASK));			
				itemReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("Item " + ReloadMSG) == 0) {
							ItemTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] Item Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem shopReload = new JMenuItem("Shop");			
				shopReload.setAccelerator(KeyStroke.getKeyStroke('H', InputEvent.SHIFT_DOWN_MASK));			
				shopReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("Shop " + ReloadMSG) == 0) {
							ItemTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] Shop Update Complete..." + "\n", "Red");
						}			
					}
				});
			
				JMenuItem autoLootReload = new JMenuItem("AutoLoot");			
				autoLootReload.setAccelerator(KeyStroke.getKeyStroke('J', InputEvent.SHIFT_DOWN_MASK));			
				autoLootReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("AutoLoot " + ReloadMSG) == 0) {
							AutoLoot.getInstance().reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] AutoLoot Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem npcReload = new JMenuItem("Npc");			
				npcReload.setAccelerator(KeyStroke.getKeyStroke('K', InputEvent.SHIFT_DOWN_MASK));			
				npcReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("Npc " + ReloadMSG) == 0) {
							NpcTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] Npc Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem bossCycleReload = new JMenuItem("BossCycle");			
				bossCycleReload.setAccelerator(KeyStroke.getKeyStroke('L', InputEvent.SHIFT_DOWN_MASK));			
				bossCycleReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("BossCycle " + ReloadMSG) == 0) {
							L1BossCycle.load();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] BossCycle Update Complete..." + "\n", "Red");
						}			
					}
				});
				/*
				JMenuItem weaponSkillReload = new JMenuItem("WeaponSkill");			
				weaponSkillReload.setAccelerator(KeyStroke.getKeyStroke('Z', InputEvent.SHIFT_DOWN_MASK));			
				weaponSkillReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("WeaponSkill " + ReloadMSG) == 0) {
							WeaponSkillTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] WeaponSkill Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem itemExplanationReload = new JMenuItem("ItemExplanation");			
				itemExplanationReload.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.SHIFT_DOWN_MASK));			
				itemExplanationReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("ItemExplanation " + ReloadMSG) == 0) {
							ItemExplanation.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] ItemExplanation Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem autoShopReload = new JMenuItem("AutoShop");			
				autoShopReload.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.SHIFT_DOWN_MASK));			
				autoShopReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("AutoShop " + ReloadMSG) == 0) {
							AutoShopTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] AutoShop Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem quizReload = new JMenuItem("Quiz");			
				quizReload.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.SHIFT_DOWN_MASK));			
				quizReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("Quiz " + ReloadMSG) == 0) {
							QuizQuestionTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] Quiz Update Complete..." + "\n", "Red");
						}			
					}
				});
				*/
				JMenuItem banIpReload = new JMenuItem("BanIp");			
				banIpReload.setAccelerator(KeyStroke.getKeyStroke('B', InputEvent.SHIFT_DOWN_MASK));			
				banIpReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("BanIp " + ReloadMSG) == 0) {
							IpTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] BanIp Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem castleReload = new JMenuItem("Castle");			
				castleReload.setAccelerator(KeyStroke.getKeyStroke('N', InputEvent.SHIFT_DOWN_MASK));			
				castleReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("Castle " + ReloadMSG) == 0) {
							CastleTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] Castle Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem clanReload = new JMenuItem("Clan");			
				clanReload.setAccelerator(KeyStroke.getKeyStroke('M', InputEvent.SHIFT_DOWN_MASK));			
				clanReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("Clan " + ReloadMSG) == 0) {
							ClanTable.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] Clan Update Complete..." + "\n", "Red");
						}			
					}
				});
				
				JMenuItem robotReload = new JMenuItem("Robot");			
				robotReload.setAccelerator(KeyStroke.getKeyStroke(',', InputEvent.SHIFT_DOWN_MASK));			
				robotReload.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						if (QMsg("Robot " + ReloadMSG) == 0) {
							RobotThread.reload();
							jSystemLogWindow.append(getLogTime() + "　[도구 실행] Robot Update Complete..." + "\n", "Red");
						}			
					}
				}
				
						);
				
				
				//jJMenu4.add(noticeReload);
				jJMenu4.add(configReload);
				//jJMenu4.add(weaponAddDamageReload);
				jJMenu4.add(weaponEnchantListReload);
				jJMenu4.add(armorEnchantListReload);
				jJMenu4.add(accessoryEnchantListReload);
				
				jJMenu4.add(dropReload);
				jJMenu4.add(dropItemReload);
				jJMenu4.add(polyReload);
				jJMenu4.add(resolventReload);
				jJMenu4.add(treasureBoxReload);
				jJMenu4.add(skillsReload);
				jJMenu4.add(mobSkillReload);
				jJMenu4.add(mapFixKeyReload);
				jJMenu4.add(itemReload);
				jJMenu4.add(shopReload);
				jJMenu4.add(autoLootReload);
				jJMenu4.add(npcReload);
				jJMenu4.add(bossCycleReload);
				//jJMenu4.add(weaponSkillReload);
				//jJMenu4.add(itemExplanationReload);
				//jJMenu4.add(autoShopReload);
				//jJMenu4.add(quizReload);
				jJMenu4.add(banIpReload);
				jJMenu4.add(castleReload);
				jJMenu4.add(clanReload);
				jJMenu4.add(robotReload);
				
				
				JMenuItem developerInfo = new JMenuItem("Contact");						
				developerInfo.addActionListener(new ActionListener() { 
					public void actionPerformed(ActionEvent e) {
						JOptionPane.showMessageDialog(null, "All Powerful SoftWare\nPhone:010-5629-2444\nLinfree114@nate.com", "Developers Contact", JOptionPane.INFORMATION_MESSAGE);		
					}
				});
				
				jJMenu5.add(developerInfo);
				
				jJMenuBar.add(jJMenu1);
				jJMenuBar.add(jJMenu2);
				jJMenuBar.add(jJMenu3);
				jJMenuBar.add(jJMenu4);
				jJMenuBar.add(jJMenu5);
				
				
				width = jJFrame.getContentPane().getSize().width / 2;
				height = (jJFrame.getContentPane().getSize().height - 50) / 2;
				
				
				if (jSystemLogWindow == null) { 
					jSystemLogWindow = new ServerLogWindow("System Message", 0, 0, width, height, false, false);				
					jJDesktopPane.add(jSystemLogWindow);
				}
				if (jServerMultiChatLogWindow == null) {
					jServerMultiChatLogWindow = new ServerMultiChatLogWindow("Multi-Chat", 0, height, width, height, false, false);				
					jJDesktopPane.add(jServerMultiChatLogWindow);
				}
				if (jServerUserInfoWindow == null) {
					jServerUserInfoWindow = new ServerUserInfoWindow("User Information", width, 0, width, height, false, false);				
					jJDesktopPane.add(jServerUserInfoWindow);
				}
				if (jServerLatterLogWindow == null) {
					jServerLatterLogWindow = new ServerLatterLogWindow("Items", width, height, width, height, false, false);				
					jJDesktopPane.add(jServerLatterLogWindow);
				}
				/*
				if (jWorldChatLogWindow == null) {
					jWorldChatLogWindow = new ServerLogWindow("전체채팅", 0, height, width, height, false, false);				
					jJDesktopPane.add(jWorldChatLogWindow);
				}				
				if (jNomalChatLogWindow == null) {
					jNomalChatLogWindow = new ServerLogWindow("일반채팅", 0, height * 2, width, height, true, true);				
					jJDesktopPane.add(jNomalChatLogWindow);
				}	
				if (jWhisperChatLogWindow == null) {
					jWhisperChatLogWindow = new ServerLogWindow("귓속말채팅", 0, height * 3, width, height, true, true);				
					jJDesktopPane.add(jWhisperChatLogWindow);
				}
				
				
				
				if (jClanChatLogWindow == null) {
					jClanChatLogWindow = new ServerLogWindow("혈맹채팅", width, 0, width, height, true, true);				
					jJDesktopPane.add(jClanChatLogWindow);
				}
				if (jPartyChatLogWindow == null) {
					jPartyChatLogWindow = new ServerLogWindow("파티채팅", width, height, width, height, true, true);				
					jJDesktopPane.add(jPartyChatLogWindow);
				}	
				if (jTradeChatLogWindow == null) {
					jTradeChatLogWindow = new ServerLogWindow("장사채팅", width, height * 2, width, height, true, true);				
					jJDesktopPane.add(jTradeChatLogWindow);
				}
				if (jWareHouseLogWindow == null) {
					jWareHouseLogWindow = new ServerLogWindow("창고", width, height * 3, width, height, true, true);				
					jJDesktopPane.add(jWareHouseLogWindow);
				}
				
				
				if (jTradeLogWindow == null) {
					jTradeLogWindow = new ServerLogWindow("거래", width * 2, 0, width, height, true, true);				
					jJDesktopPane.add(jTradeLogWindow);
				}
				if (jEnchantLogWindow == null) {
					jEnchantLogWindow = new ServerLogWindow("인챈", width * 2, height, width, height, true, true);				
					jJDesktopPane.add(jEnchantLogWindow);
				}
				if (jObserveLogWindow == null) {
					jObserveLogWindow = new ServerLogWindow("감시", width * 2, height * 2, width, height, true, true);				
					jJDesktopPane.add(jObserveLogWindow);
				}
				if (jBugLogWindow == null) {
					jBugLogWindow = new ServerLogWindow("버그", width * 2, height * 3, width, height, true, true);				
					jJDesktopPane.add(jBugLogWindow);
				}
				
				if (jCommandLogWindow == null) {
					jCommandLogWindow = new ServerLogWindow("명령", width * 3, 0, width, height, true, true);				
					jJDesktopPane.add(jCommandLogWindow);
				}
				*/
			}
			isServerStarted = true;
			
		} catch (Exception e) {}		
	}
	
	/** *** 로그 설정 부분 ***** */
	public static int userCount(int i) {
		userCount += i;
		return userCount;
	}

	public static void refreshMemory() {
		//lblMemory.setText(" 메모리 : " + SystemUtil.getUsedMemoryMB() + " MB");
	}

	public static void LogServerAppend(String s, String s1) {
		// 서버로그창 : 2s
		//textServer.append("\n" + getLogTime() + "　" + s + "　" + s1);
		
		if (jSystemLogWindow != null && !jSystemLogWindow.isClosed()) {
			jSystemLogWindow.append(getLogTime() + "　" + s + "　" + s1 + "\n", "Black");
		} else {
			jSystemLogWindow = null;
		}
	}

	public static synchronized void LogServerAppend(String s, L1PcInstance pc, String ip, int i) {
		// 서버로그창 : s - 접속,종료
		//textServer.append("\n" + getLogTime() + "　" + s + "　" + pc.getName() + ":" + pc.getAccountName() + "　" + ip + "　U:" + userCount(i));
		
		if (jServerUserInfoWindow != null) {
			/*
			if (jServerUserInfoWindow.getListModel() != null) {
				try {
					jServerUserInfoWindow.jJList.clearSelection();
					
					if (s.equals("접속")) {
						jServerUserInfoWindow.getListModel().addElement(pc.getName());											
					} else {
						jServerUserInfoWindow.getListModel().removeElement(pc.getName());						
					}
				} catch (Exception e) {
					
				}
		

				jServerUserInfoWindow.getLbl_UserCount().setText("접속자수 : " + (jServerUserInfoWindow.getListModel().size()));	
			}
			*/
			
			if (jServerUserInfoWindow.getTableModel() != null) {
				try {
					jServerUserInfoWindow.jJTable0.clearSelection();					
					
					if (s.equals("접속")) {	
						jServerUserInfoWindow.jJTable0.clearSelection();
						String[] name = new String[1];
						name[0] = pc.getName();
						jServerUserInfoWindow.getTableModel().addRow(name);						
					} else {						
						jServerUserInfoWindow.jJTable0.clearSelection();
						for (int row = 0; row < jServerUserInfoWindow.getTableModel().getRowCount(); row++) {
							if (jServerUserInfoWindow.getTableModel().getValueAt(row, 0).equals(pc.getName())) {
								jServerUserInfoWindow.getTableModel().removeRow(row);
								break;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		

				jServerUserInfoWindow.getLbl_UserCount().setText("Number of users : " + (jServerUserInfoWindow.getTableModel().getRowCount()));	
			}
		}
		
		if (jSystemLogWindow != null && !jSystemLogWindow.isClosed()) {
			jSystemLogWindow.append(getLogTime() + "　" + s + "　" + pc.getName() + ":" + pc.getAccountName() + "　" + ip + "　U:" + userCount(i) + "\n", "Red");
		} else {
			jSystemLogWindow = null;
		}
	}

	public static void LogChatAppend(String s, String pcname, String text) {
		// 서버채팅창 s: 일반(-), 파티(#), 그룹(*), 전체(&)
		//textChat.append("\n" + getLogTime() + "　" + s + "　" + pcname + "　:" + text);
		
		if (jWorldChatLogWindow != null && !jWorldChatLogWindow.isClosed()) {
			jWorldChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + "　:" + text + "\n", "Blue");
		} else {
			jWorldChatLogWindow = null;
		}
		
		if (jServerChatLogWindow != null && !jServerChatLogWindow.isClosed()) {
			if (jServerChatLogWindow.chk_World.isSelected()) {
				jServerChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + "　:" + text + "\n", "Blue");
			}
		} else {
			jServerChatLogWindow = null;
		}
		
		if (jServerMultiChatLogWindow != null) {
			jServerMultiChatLogWindow.append("worldChatText", getLogTime() + "　" + s + "　" + pcname + "　:" + text + "\n", "Blue");
		}
	}

	public static void LogChatAppend(String s, String pcname, String c, String text, String sel) {
		// 서버채팅창 s: 혈맹(@), 귓말(>)
		//textChat.append("\n" + getLogTime() + "　" + s + "　" + pcname + sel + c + "　:" + text);
	}

	public static void LogChatNormalAppend(String s, String pcname, String text) {
		// 서버채팅창 s: 일반(N)
		//textChatNormal.append("\n" + getLogTime() + "　" + s + "　" + pcname + " : " + text);
		
		if (jNomalChatLogWindow != null && !jNomalChatLogWindow.isClosed()) {
			jNomalChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + " : " + text + "\n", "Blue");
		} else {
			jNomalChatLogWindow = null;
		}
		
		if (jServerChatLogWindow != null && !jServerChatLogWindow.isClosed()) {
			if (jServerChatLogWindow.chk_Noaml.isSelected()) {
				jServerChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + " : " + text + "\n", "Blue");
			}
		} else {
			jServerChatLogWindow = null;
		}
		
		if (jServerMultiChatLogWindow != null) {
			jServerMultiChatLogWindow.append("nomalChatText", getLogTime() + "　" + s + "　" + pcname + " : " + text + "\n", "Blue");
		}
	}

	public static void LogChatPartyAppend(String s, String pcname, String text) {
		// 서버채팅창 s: 파티(#), 그룹(*)
		//textChatParty.append("\n" + getLogTime() + "　" + s + "　" + pcname + " : " + text);
		
		if (jPartyChatLogWindow != null && !jPartyChatLogWindow.isClosed()) {
			jPartyChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + " : " + text + "\n", "Blue");
		} else {
			jPartyChatLogWindow = null;
		}
		
		if (jServerChatLogWindow != null && !jServerChatLogWindow.isClosed()) {
			if (jServerChatLogWindow.chk_Party.isSelected()) {
				jServerChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + " : " + text + "\n", "Blue");
			}
		} else {
			jServerChatLogWindow = null;
		}
		
		if (jServerMultiChatLogWindow != null) {
			jServerMultiChatLogWindow.append("partyChatText", getLogTime() + "　" + s + "　" + pcname + " : " + text + "\n", "Blue");
		}
	}

	public static void LogChatWisperAppend(String s, String pcname, String c, String text, String sel) {
		// 서버채팅창 s: 귓말(>)
		//textChatWisper.append("\n" + getLogTime() + "　" + s + "　" + pcname + sel + c + " : " + text);
		
		if (jWhisperChatLogWindow != null && !jWhisperChatLogWindow.isClosed()) {
			jWhisperChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + sel + c + " : " + text + "\n", "Blue");
		} else {
			jWhisperChatLogWindow = null;
		}
		
		if (jServerChatLogWindow != null && !jServerChatLogWindow.isClosed()) {
			if (jServerChatLogWindow.chk_Whisper.isSelected()) {
				jServerChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + sel + c + " : " + text + "\n", "Blue");
			}
		} else {
			jServerChatLogWindow = null;
		}
		
		if (jServerMultiChatLogWindow != null) {
			jServerMultiChatLogWindow.append("whisperChatText", getLogTime() + "　" + s + "　" + pcname + sel + c + " : " + text + "\n", "Blue");
		}
	}

	public static void LogChatClanAppend(String s, String pcname, String c, String text) {
		// 서버채팅창 s: 혈맹(@)
		//textChatClan.append("\n" + getLogTime() + "　" + s + "　" + pcname + "[" + c + "] : " + text);
		
		if (jClanChatLogWindow != null && !jClanChatLogWindow.isClosed()) {
			jClanChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + "[" + c + "] : " + text + "\n", "Blue");
		} else {
			jClanChatLogWindow = null;
		}
		
		if (jServerChatLogWindow != null && !jServerChatLogWindow.isClosed()) {
			if (jServerChatLogWindow.chk_Clan.isSelected()) {
				jServerChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + "[" + c + "] : " + text + "\n", "Blue");
			}
		} else {
			jServerChatLogWindow = null;
		}
		
		if (jServerMultiChatLogWindow != null) {
			jServerMultiChatLogWindow.append("clanChatText", getLogTime() + "　" + s + "　" + pcname + "[" + c + "] : " + text + "\n", "Blue");
		}
	}

	public static void LogChatTradeAppend(String s, String pcname, String text) {
		// 서버채팅창 s: 장사($)
		//textChatTrade.append("\n" + getLogTime() + "　" + s + "　" + pcname + " : " + text);
		
		if (jTradeChatLogWindow != null && !jTradeChatLogWindow.isClosed()) {
			jTradeChatLogWindow.append(getLogTime() + "　" + s + "　" + pcname + " : " + text + "\n", "Blue");
		} else {
			jTradeChatLogWindow = null;
		}
		
		if (jServerMultiChatLogWindow != null) {
			jServerMultiChatLogWindow.append("tradeChatText", getLogTime() + "　" + s + "　" + pcname + " : " + text + "\n", "Blue");
		}
	}

	public static void LogWareHouseAppend(String s, String pcname, String clanname, L1ItemInstance item, int count, int obj) {
		// 창고로그 : 시간 s
//		if (item.getItem().getType2() == 0 && item.getItem().getlogcheckitem() == 0)
//			return;
//
//		textWareHouse.append("\n" + getLogTime() + "　" + s + "　" + pcname);
//		if (!clanname.equalsIgnoreCase(""))
//			textWareHouse.append("혈맹" + clanname);
//		textWareHouse.append("　");
//		if (item.getEnchantLevel() > 0) {
//			textWareHouse.append("+" + item.getEnchantLevel());
//		} else if (item.getEnchantLevel() < 0) {
//			textWareHouse.append("" + item.getEnchantLevel());
//		}
//		textWareHouse.append(item.getName() + "　B:" + item.getBless() + "　C:" + count + "　O:" + obj);
		
		if (item.getItem().getType2() == 0 && item.getItem().getlogcheckitem() == 0)
			return;

		if (jWareHouseLogWindow != null && !jWareHouseLogWindow.isClosed()) {
			StringBuilder sb = new StringBuilder();
			
			sb.append(getLogTime() + "　" + s + "　" + pcname);
						
			if (!clanname.equalsIgnoreCase("")) {
				sb.append("Clan" + clanname);
			}
			sb.append("　");
			if (item.getEnchantLevel() > 0) {
				sb.append("+" + item.getEnchantLevel());
			} else if (item.getEnchantLevel() < 0) {
				sb.append("" + item.getEnchantLevel());
			}
				
			sb.append(item.getName() + "　B:" + item.getBless() + "　C:" + count + "　O:" + obj + "\n");
					
			jWareHouseLogWindow.append(sb.toString(), "Blue");
		} else {
			jWareHouseLogWindow = null;
		}
		
		if (jServerMultiChatLogWindow != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(getLogTime() + "　" + s + "　" + pcname);
						
			if (!clanname.equalsIgnoreCase("")) {
				sb.append("Clan" + clanname);
			}
			sb.append("　");
			if (item.getEnchantLevel() > 0) {
				sb.append("+" + item.getEnchantLevel());
			} else if (item.getEnchantLevel() < 0) {
				sb.append("" + item.getEnchantLevel());
			}
			
			sb.append(item.getName() + "　B:" + item.getBless() + "　C:" + count + "　O:" + obj + "\n");
			
			jServerMultiChatLogWindow.append("wareHouseText", sb.toString(), "Blue");			
		}
	}

	public static void LogTradeAppend(String s, String pcname, String targetname, int enchant, String itemname, int bless, int count, int obj) {
		// 교환 로그
//		textTrade.append("\n" + getLogTime() + "　" + s + "　" + pcname + "　" + targetname + "　");
//		if (enchant > 0) {
//			textTrade.append("+" + enchant);
//		} else if (enchant < 0) {
//			textTrade.append("-" + enchant);
//		}
//		textTrade.append(itemname + "　B:" + bless + "　C:" + count + "　O:" + obj);
		
		if (jTradeLogWindow != null && !jTradeLogWindow.isClosed()) {
			StringBuilder sb = new StringBuilder();
			
			sb.append(getLogTime() + "　" + s + "　" + pcname + "　" + targetname + "　");		
			
			if (enchant > 0) {
				sb.append("+" + enchant);
			} else if (enchant < 0) {
				sb.append("-" + enchant);
			}
			
			sb.append(itemname + "　B:" + bless + "　C:" + count + "　O:" + obj + "\n");
			
			jTradeLogWindow.append(sb.toString(), "Blue");
		} else {
			jTradeLogWindow = null;
		}
		
		if (jServerMultiChatLogWindow != null) {
			StringBuilder sb = new StringBuilder();
			
			sb.append(getLogTime() + "　" + s + "　" + pcname + "　" + targetname + "　");		
			
			if (enchant > 0) {
				sb.append("+" + enchant);
			} else if (enchant < 0) {
				sb.append("-" + enchant);
			}
			
			sb.append(itemname + "　B:" + bless + "　C:" + count + "　O:" + obj + "\n");
			
			jServerMultiChatLogWindow.append("tradeText", sb.toString(), "Blue");			
		}
	}

	public static void LogShopAppend(String s, String pcname, String targetname, int enchant, String itemname, int bless, int count, int obj) {
		// 상점 로그 : 시간 상점 p t 아이템(거래아데나)
//		textTrade.append("\n" + getLogTime() + "　" + s + "　" + pcname + "　" + targetname + "　");
//		if (enchant > 0) {
//			textTrade.append("+" + enchant);
//		} else if (enchant < 0) {
//			textTrade.append("-" + enchant);
//		}
//		textTrade.append(itemname + "　B:" + bless + "　C:" + count + "　O:" + obj);
		
		if (jTradeLogWindow != null && !jTradeLogWindow.isClosed()) {
			StringBuilder sb = new StringBuilder();
			
			sb.append(getLogTime() + "　" + s + "　" + pcname + "　" + targetname + "　");
			if (enchant > 0) {
				sb.append("+" + enchant);
			} else if (enchant < 0) {
				sb.append("-" + enchant);
			}
			
			sb.append(itemname + "　B:" + bless + "　C:" + count + "　O:" + obj + "\n");
			
			jTradeLogWindow.append(sb.toString(), "Blue");
		} else {
			jTradeLogWindow = null;
		}
		
		if (jServerMultiChatLogWindow != null) {
			StringBuilder sb = new StringBuilder();
			
			sb.append(getLogTime() + "　" + s + "　" + pcname + "　" + targetname + "　");
			if (enchant > 0) {
				sb.append("+" + enchant);
			} else if (enchant < 0) {
				sb.append("-" + enchant);
			}
			
			sb.append(itemname + "　B:" + bless + "　C:" + count + "　O:" + obj + "\n");
			
			jServerMultiChatLogWindow.append("tradeText", sb.toString(), "Blue");			
		}
	}

	public static void LogEnchantAppend(String s, String pcname, String enchantlvl, String itemname, int obj) {
		// 인챈 로그
		//textEnchant.append("\n" + getLogTime() + "　" + s + "　" + pcname + "　" + enchantlvl + "　" + itemname + "　O:" + obj);
		
		if (jEnchantLogWindow != null && !jEnchantLogWindow.isClosed()) {	
			if (s.indexOf("success") > -1) {
				jEnchantLogWindow.append(getLogTime() + "　" + s + "　" + pcname + "　" + enchantlvl + "　" + itemname + "　O:" + obj + "\n", "Blue");
			} else {
				jEnchantLogWindow.append(getLogTime() + "　" + s + "　" + pcname + "　" + enchantlvl + "　" + itemname + "　O:" + obj + "\n", "Red");
			}
		} else {
			jEnchantLogWindow = null;
		}
		
		if (jServerMultiChatLogWindow != null) {
			if (s.indexOf("success") > -1) {
				jServerMultiChatLogWindow.append("enchantText", getLogTime() + "　" + s + "　" + pcname + "　" + enchantlvl + "　" + itemname + "　O:" + obj + "\n", "Blue");
			} else {
				jServerMultiChatLogWindow.append("enchantText", getLogTime() + "　" + s + "　" + pcname + "　" + enchantlvl + "　" + itemname + "　O:" + obj + "\n", "Red");
			}	
		}
	}

	public static void LogObserverAppend(String s, String pcname, L1ItemInstance item, int count, int obj) {
		// 감시로그
//		textObserver.append("\n" + getLogTime() + "　" + s + "　" + pcname);
//		textObserver.append("　");
//		if (item.getEnchantLevel() > 0) {
//			textObserver.append("+" + item.getEnchantLevel());
//		} else if (item.getEnchantLevel() < 0) {
//			textObserver.append("" + item.getEnchantLevel());
//		}
//		textObserver.append(item.getName() + "　C:" + count + "　O:" + obj);
		
		if (jObserveLogWindow != null && !jObserveLogWindow.isClosed()) {
			StringBuilder sb = new StringBuilder();
			
			sb.append(getLogTime() + "　" + s + "　" + pcname);
			sb.append("　");
			if (item.getEnchantLevel() > 0) {
				sb.append("+" + item.getEnchantLevel());
			} else if (item.getEnchantLevel() < 0) {
				sb.append("" + item.getEnchantLevel());
			}
			
			sb.append(item.getName() + "　C:" + count + "　O:" + obj + "\n");
			
			jObserveLogWindow.append(sb.toString(), "Blue");
		} else {
			jObserveLogWindow = null;
		}
		
		if (jServerMultiChatLogWindow != null) {
			StringBuilder sb = new StringBuilder();
			
			sb.append(getLogTime() + "　" + s + "　" + pcname);
			sb.append("　");
			if (item.getEnchantLevel() > 0) {
				sb.append("+" + item.getEnchantLevel());
			} else if (item.getEnchantLevel() < 0) {
				sb.append("" + item.getEnchantLevel());
			}
			
			sb.append(item.getName() + "　C:" + count + "　O:" + obj + "\n");
			
			jServerMultiChatLogWindow.append("observeText", sb.toString(), "Blue");			
		}
	}

	public static void LogBugAppend(String s, L1PcInstance pc, int type) {
		// 버그로그
//		textBug.append("\n" + getLogTime() + "　" + s + "　" + pc.getName());
//		switch (type) {
//		case 1: // 스핵
//			textBug.append("　P:" + pc.getGfxId().getTempCharGfx() + "　R:" + pc.getSpeedRightInterval() + "　>I:" + pc.getSpeedInterval());
//			break;
//		case 2: // 뚫어
//			textBug.append("　x,y,map:" + pc.getLocation().getX() + "," + pc.getLocation().getY() + "," + pc.getLocation().getMapId());
//			break;
//		default:
//			break;
//		}
		
		if (jBugLogWindow != null && !jBugLogWindow.isClosed()) {	
			StringBuilder sb = new StringBuilder();
			sb.append(getLogTime() + "　" + s + "　" + pc.getName());
			switch (type) {
			case 1: // 스핵
				//sb.append("　P:" + pc.getGfxId().getTempCharGfx() + "　R:" + pc.getSpeedRightInterval() + "　>I:" + pc.getSpeedInterval() + "\n");
				
				jBugLogWindow.append(sb.toString(), "Blue");
				break;
			case 2: // 뚫어
				sb.append("　x,y,map:" + pc.getLocation().getX() + "," + pc.getLocation().getY() + "," + pc.getLocation().getMapId() + "\n");
				
				jBugLogWindow.append(sb.toString(), "Blue");
				break;
			case 3:
				sb.append(pc.getName() + " " + s + "\n");
				
				jBugLogWindow.append(sb.toString(), "Blue");
				break;
			default:
				break;
			}
		} else {
			jBugLogWindow = null;
		}
		
		if (jServerMultiChatLogWindow != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(getLogTime() + "　" + s + "　" + pc.getName());
			switch (type) {
			case 1: // 스핵
				//sb.append("　P:" + pc.getGfxId().getTempCharGfx() + "　R:" + pc.getSpeedRightInterval() + "　>I:" + pc.getSpeedInterval() + "\n");
				
				jServerMultiChatLogWindow.append("observeText", sb.toString(), "Blue");		
				break;
			case 2: // 뚫어
				sb.append("　x,y,map:" + pc.getLocation().getX() + "," + pc.getLocation().getY() + "," + pc.getLocation().getMapId() + "\n");
				
				jServerMultiChatLogWindow.append("bugText", sb.toString(), "Blue");		
				break;
			default:
				break;
			}
				
		}

	}

	public static void LogCommandAppend(String pcname, String cmd, String arg) {
		// 커맨드 로그
		//textCommand.append("\n" + getLogTime() + "　" + pcname + "　C:" + cmd + " " + arg);
		
		if (jCommandLogWindow != null && !jCommandLogWindow.isClosed()) {	
			jCommandLogWindow.append(getLogTime() + "　" + pcname + "　C:" + cmd + " " + arg + "\n", "Blue");			
		} else {
			jCommandLogWindow = null;
		}
		
		if (jServerMultiChatLogWindow != null) {
			jServerMultiChatLogWindow.append("commandText", getLogTime() + "　" + pcname + "　C:" + cmd + " " + arg + "\n", "Blue");		
		}
	}
	
	/** 메세지 처리 부분 */
	public static int QMsg(String s) { // Question Window
		int result = JOptionPane.showConfirmDialog(null, s, "Server Message", 2, JOptionPane.INFORMATION_MESSAGE);
		return result;
	}
	
	public static void infoMsg(String s) { // Message Window(INFOMATION)
		JOptionPane.showMessageDialog(null, s, "Server Message", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void errorMsg(String s) { // Message Window(ERROR)
		JOptionPane.showMessageDialog(null, s, "Server Message", JOptionPane.ERROR_MESSAGE);
	}
	
	/** 현재 시간 가져오기 */
	private static String getLogTime() {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd HH:mm:ss");
		String time = dateFormat.format(currentDate.getTime());
		return time;
	}
	
	/** 로그 저장 */
	public static void savelog() {
		jSystemLogWindow.savelog();
		jServerMultiChatLogWindow.savelog();
	}
	
	public static void flush(JTextPane text, String FileName, String date) {
		try {
			RandomAccessFile rnd = new RandomAccessFile("ServerLog/" + date + "/" + FileName + ".txt", "rw");
			rnd.write(text.getText().getBytes());
			rnd.close();
		} catch (Exception e) {
		}
	}
	
	// 날짜형태(yyyy-MM-dd) 시간(hh-mm)
	public static String getDate() {
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd hh-mm", Locale.KOREA);
		return s.format(Calendar.getInstance().getTime());
	}
}
