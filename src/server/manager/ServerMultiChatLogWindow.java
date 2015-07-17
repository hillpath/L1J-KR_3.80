package server.manager;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.StringTokenizer;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import l1j.server.ServerChat;


@SuppressWarnings("serial")
public class ServerMultiChatLogWindow extends JInternalFrame {
	private JTextPane worldChatText = null;
	private JTextPane nomalChatText = null;
	private JTextPane whisperChatText = null;
	private JTextPane clanChatText = null;
	private JTextPane partyChatText = null;
	private JTextPane tradeChatText = null;
	private JTextPane wareHouseText = null;
	private JTextPane tradeText = null;
	private JTextPane enchantText = null;
	private JTextPane observeText = null;
	private JTextPane bugText = null;
	private JTextPane commandText = null;	
	
	private JScrollPane worldChatScroll = null;
	private JScrollPane nomalChatScroll = null;
	private JScrollPane whisperChatScroll = null;
	private JScrollPane clanChatScroll = null;
	private JScrollPane partyChatScroll = null;
	private JScrollPane tradeChatScroll = null;
	private JScrollPane wareHouseScroll = null;
	private JScrollPane tradeScroll = null;
	private JScrollPane enchantScroll = null;
	private JScrollPane observeScroll = null;
	private JScrollPane bugScroll = null;
	private JScrollPane commandScroll = null;
	
	private JTextField txt_ChatUser = null;
	private JTextField txt_ChatSend = null;
	private JButton btn_Clear = null;
	
	private JTabbedPane jJTabbedPane = null;
	
	public ServerMultiChatLogWindow(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
		super();
		
		initialize(windowName, x, y, width, height, resizable, closable);
	}
	
	public void initialize(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
		this.title = windowName;
		this.closable = closable;      
		this.isMaximum = false;	   
		this.maximizable = false;
		this.resizable = resizable;
		this.iconable = true;   
		this.isIcon = false;			  
	    setSize(width, height);
		setBounds(x, y, width, height);
		setVisible(true);
		frameIcon = new ImageIcon("");
		setRootPaneCheckingEnabled(true);
		
		updateUI();
	    
		try {
		    jJTabbedPane = new JTabbedPane(JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);
		    
		    worldChatText = new JTextPane();
		    nomalChatText = new JTextPane();
		    whisperChatText = new JTextPane();
		    clanChatText = new JTextPane();
		    partyChatText = new JTextPane();
		    tradeChatText = new JTextPane();
		    wareHouseText = new JTextPane();
		    tradeText = new JTextPane();
		    enchantText = new JTextPane();
		    observeText = new JTextPane();
		    bugText = new JTextPane();
		    commandText = new JTextPane();
	
		    worldChatScroll = new JScrollPane(worldChatText);
		    nomalChatScroll = new JScrollPane(nomalChatText);
		    whisperChatScroll = new JScrollPane(whisperChatText);
		    clanChatScroll = new JScrollPane(clanChatText);
		    partyChatScroll = new JScrollPane(partyChatText);
		    tradeChatScroll = new JScrollPane(tradeChatText);
		    wareHouseScroll = new JScrollPane(wareHouseText);
		    tradeScroll = new JScrollPane(tradeText);
		    enchantScroll = new JScrollPane(enchantText);
		    observeScroll = new JScrollPane(observeText);
		    bugScroll = new JScrollPane(bugText);
		    commandScroll = new JScrollPane(commandText); 
		    
		    worldChatText.setEditable(false);		 
		    nomalChatText.setEditable(false);		 
		    whisperChatText.setEditable(false);		
		    clanChatText.setEditable(false);		
		    partyChatText.setEditable(false);		 
		    tradeChatText.setEditable(false);		 
		    wareHouseText.setEditable(false);		 
		    tradeText.setEditable(false);		
		    enchantText.setEditable(false);		
		    observeText.setEditable(false);		
		    bugText.setEditable(false);		
		    commandText.setEditable(false);		
	
		    	
		    worldChatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		    worldChatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		    
		    nomalChatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		    nomalChatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		    
		    whisperChatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		    whisperChatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		    
		    clanChatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		    clanChatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		    
		    partyChatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		    partyChatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		    
		    tradeChatScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		    tradeChatScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		    
		    wareHouseScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		    wareHouseScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		    
		    tradeScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		    tradeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		    
		    enchantScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		    enchantScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		    
		    observeScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		    observeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		    
		    bugScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		    bugScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		    
		    commandScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		    commandScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		    
		    setStyle(worldChatText);
		    setStyle(nomalChatText);
		    setStyle(whisperChatText);
		    setStyle(clanChatText);
		    setStyle(partyChatText);
		    setStyle(tradeChatText);
		    setStyle(wareHouseText);
		    setStyle(tradeText);
		    setStyle(enchantText);
		    setStyle(observeText);
		    setStyle(bugText);
		    setStyle(commandText);
		    
		    jJTabbedPane.addTab("All", worldChatScroll);
		    jJTabbedPane.addTab("Normal", nomalChatScroll);
		    jJTabbedPane.addTab("Whisper", whisperChatScroll);
		    jJTabbedPane.addTab("Clan", clanChatScroll);
		    jJTabbedPane.addTab("Party", partyChatScroll);
		    jJTabbedPane.addTab("Trade", tradeChatScroll);
		    jJTabbedPane.addTab("Warehouse", wareHouseScroll);
		    jJTabbedPane.addTab("Trade", tradeScroll);
		    jJTabbedPane.addTab("Enchant", enchantScroll);
		    jJTabbedPane.addTab("Observe", observeScroll);
		    jJTabbedPane.addTab("Bug", bugScroll);
		    jJTabbedPane.addTab("Command", commandScroll);
		    
		   
		    txt_ChatUser = new JTextField();
		    txt_ChatSend = new JTextField();
		    txt_ChatSend.addKeyListener(new KeyAdapter() {
				public void keyPressed(KeyEvent evt) {
					chatKeyPressed(evt);
				}
			});
		    
		    btn_Clear = new JButton("Clear");
		    btn_Clear.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent evt) {
					try {
						File f = null;
						String sTemp = "";
						synchronized (eva.lock) {
							sTemp = eva.getDate();
							StringTokenizer s = new StringTokenizer(sTemp, " ");
							eva.date = s.nextToken();
							eva.time = s.nextToken();
							f = new File("ServerLog/" + eva.date);
							if (!f.exists()) {
								f.mkdir();
							}
							eva.jSystemLogWindow.savelog();
							eva.flush(worldChatText, "[" + eva.time + "] world", eva.date);
							eva.flush(nomalChatText, "[" + eva.time + "] general", eva.date);
							eva.flush(whisperChatText, "[" + eva.time + "] whisper", eva.date);
							eva.flush(clanChatText, "[" + eva.time + "] clan", eva.date);
							eva.flush(partyChatText, "[" + eva.time + "] party", eva.date);
							eva.flush(wareHouseText, "[" + eva.time + "] warehouse", eva.date);
							eva.flush(tradeText, "[" + eva.time + "] trade", eva.date);
							eva.flush(enchantText, "[" + eva.time + "] enchant", eva.date);
							eva.flush(observeText, "[" + eva.time + "] observe", eva.date);
							eva.flush(bugText, "[" + eva.time + "] bug", eva.date);
							eva.flush(commandText, "[" + eva.time + "] command", eva.date);
							sTemp = null;
							eva.date = null;
							eva.time = null;							
						}
						
						worldChatText.setText("");
					    nomalChatText.setText("");
					    whisperChatText.setText("");
					    clanChatText.setText("");
					    partyChatText.setText("");
					    tradeChatText.setText("");
					    wareHouseText.setText("");
					    tradeText.setText("");
					    enchantText.setText("");
					    observeText.setText("");
					    bugText.setText("");
					    commandText.setText("");
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
		    });
		    
			GroupLayout layout = new GroupLayout(getContentPane());
			getContentPane().setLayout(layout);
			
			GroupLayout.SequentialGroup main_horizontal_grp = layout.createSequentialGroup();
			
			GroupLayout.SequentialGroup horizontal_grp = layout.createSequentialGroup();
			GroupLayout.SequentialGroup vertical_grp   = layout.createSequentialGroup();
			
			GroupLayout.ParallelGroup main = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
			GroupLayout.ParallelGroup col1 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
			GroupLayout.ParallelGroup col2 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
			GroupLayout.ParallelGroup col3 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
			GroupLayout.ParallelGroup col4 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
				
			
			main.addGroup(horizontal_grp);
			main_horizontal_grp.addGroup(main);	
			
			layout.setHorizontalGroup(main_horizontal_grp);
			layout.setVerticalGroup(vertical_grp);
			
			col1.addComponent(txt_ChatUser, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE);
			col2.addComponent(txt_ChatSend, GroupLayout.PREFERRED_SIZE, 405, GroupLayout.PREFERRED_SIZE);
			col3.addComponent(btn_Clear, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE);
			
			
			horizontal_grp.addGroup(col1).addGap(5);
			horizontal_grp.addGroup(col2).addGap(5);
			horizontal_grp.addGroup(col3).addGap(5);
			
			main.addGroup(layout.createSequentialGroup().addComponent(jJTabbedPane, GroupLayout.PREFERRED_SIZE, 588, GroupLayout.PREFERRED_SIZE));
			vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(true, true).addComponent(jJTabbedPane));
			vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(txt_ChatUser).addComponent(txt_ChatSend).addComponent(btn_Clear)).addGap(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	private void chatKeyPressed(KeyEvent evt) {
		// 서버 채팅
		if (eva.isServerStarted) {
			if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
				// 특정유저가 선택되어 있지 않다면
				if (txt_ChatUser.getText().equalsIgnoreCase("")) {
					ServerChat.getInstance().sendChatToAll("\\fW[***] " + txt_ChatSend.getText());
					eva.LogChatAppend("[All]", "[***]", txt_ChatSend.getText());
				} else {
					boolean result = ServerChat.getInstance().whisperToPlayer(txt_ChatUser.getText(), txt_ChatSend.getText());
					if (result)
						eva.LogChatWisperAppend("[whisper]", "[*ㅋ**]", txt_ChatUser.getText(), txt_ChatSend.getText(), ">");
					else
						eva.errorMsg(txt_ChatUser.getText() + eva.NoConnectUser);
				}
				txt_ChatSend.setText("");
			}
		} else {
			eva.errorMsg(eva.NoServerStartMSG);
		}
	}
	
	public void append(String paneName, String msg, String color) {
		StyledDocument doc = null;
		
		if (paneName.equals("worldChatText")) {		
			doc = worldChatText.getStyledDocument();	
			
			try {
			    doc.insertString(doc.getLength(), msg, worldChatText.getStyle(color));  
			    worldChatText.setCaretPosition(worldChatText.getDocument().getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else if (paneName.equals("nomalChatText")) {		
			doc = nomalChatText.getStyledDocument();	
			
			try {
			    doc.insertString(doc.getLength(), msg, nomalChatText.getStyle(color));  
			    nomalChatText.setCaretPosition(nomalChatText.getDocument().getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else if (paneName.equals("whisperChatText")) {		
			doc = whisperChatText.getStyledDocument();	
			
			try {
			    doc.insertString(doc.getLength(), msg, whisperChatText.getStyle(color));  
			    whisperChatText.setCaretPosition(whisperChatText.getDocument().getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else if (paneName.equals("clanChatText")) {		
			doc = clanChatText.getStyledDocument();	
			
			try {
			    doc.insertString(doc.getLength(), msg, clanChatText.getStyle(color));  
			    clanChatText.setCaretPosition(clanChatText.getDocument().getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else if (paneName.equals("partyChatText")) {		
			doc = partyChatText.getStyledDocument();	
			
			try {
			    doc.insertString(doc.getLength(), msg, partyChatText.getStyle(color));  
			    partyChatText.setCaretPosition(partyChatText.getDocument().getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else if (paneName.equals("tradeChatText")) {		
			doc = tradeChatText.getStyledDocument();	
			
			try {
			    doc.insertString(doc.getLength(), msg, tradeChatText.getStyle(color));  
			    tradeChatText.setCaretPosition(tradeChatText.getDocument().getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else if (paneName.equals("wareHouseText")) {		
			doc = wareHouseText.getStyledDocument();	
			
			try {
			    doc.insertString(doc.getLength(), msg, wareHouseText.getStyle(color));  
			    wareHouseText.setCaretPosition(wareHouseText.getDocument().getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else if (paneName.equals("tradeText")) {		
			doc = tradeText.getStyledDocument();	
			
			try {
			    doc.insertString(doc.getLength(), msg, tradeText.getStyle(color));  
			    tradeText.setCaretPosition(tradeText.getDocument().getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else if (paneName.equals("enchantText")) {		
			doc = enchantText.getStyledDocument();	
			
			try {
			    doc.insertString(doc.getLength(), msg, enchantText.getStyle(color));  
			    enchantText.setCaretPosition(enchantText.getDocument().getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else if (paneName.equals("observeText")) {		
			doc = observeText.getStyledDocument();	
			
			try {
			    doc.insertString(doc.getLength(), msg, observeText.getStyle(color));  
			    observeText.setCaretPosition(observeText.getDocument().getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else if (paneName.equals("bugText")) {		
			doc = bugText.getStyledDocument();	
			
			try {
			    doc.insertString(doc.getLength(), msg, bugText.getStyle(color));  
			    bugText.setCaretPosition(bugText.getDocument().getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else if (paneName.equals("commandText")) {		
			doc = commandText.getStyledDocument();	
			
			try {
			    doc.insertString(doc.getLength(), msg, commandText.getStyle(color));  
			    commandText.setCaretPosition(commandText.getDocument().getLength());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

		
	}
	
	private void setStyle(JTextPane textPane) {
		try {
			Style style = null;
			style = textPane.addStyle("Black", null);
			StyleConstants.setForeground(style, Color.black);
			style = textPane.addStyle("Red", null);
			StyleConstants.setForeground(style, Color.red);
			style = textPane.addStyle("Orange", null);
			StyleConstants.setForeground(style, Color.orange);
			style = textPane.addStyle("Yellow", null);
			StyleConstants.setForeground(style, Color.yellow);
			style = textPane.addStyle("Green", null);
			StyleConstants.setForeground(style, Color.green);
			style = textPane.addStyle("Blue", null);
			StyleConstants.setForeground(style, Color.blue);
			style = textPane.addStyle("DarkGray", null);
			StyleConstants.setForeground(style, Color.darkGray);
			style = textPane.addStyle("Pink", null);
			StyleConstants.setForeground(style, Color.pink);
			style = textPane.addStyle("Cyan", null);
			StyleConstants.setForeground(style, Color.cyan);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void savelog() {
		try {
			File f = null;
			String sTemp = "";
			synchronized (eva.lock) {
				sTemp = eva.getDate();
				StringTokenizer s = new StringTokenizer(sTemp, " ");
				eva.date = s.nextToken();
				eva.time = s.nextToken();
				f = new File("ServerLog/" + eva.date);
				if (!f.exists()) {
					f.mkdir();
				}
				
				eva.flush(worldChatText, "[" + eva.time + "] 1.world", eva.date);
				eva.flush(nomalChatText, "[" + eva.time + "] 2.normal", eva.date);
				eva.flush(whisperChatText, "[" + eva.time + "] 3.whisper", eva.date);
				eva.flush(clanChatText, "[" + eva.time + "] 4.clan", eva.date);
				eva.flush(partyChatText, "[" + eva.time + "] 5.party", eva.date);
				eva.flush(wareHouseText, "[" + eva.time + "] 6.warehouse", eva.date);
				eva.flush(tradeText, "[" + eva.time + "] 7.trade", eva.date);
				eva.flush(enchantText, "[" + eva.time + "] 8.enchant", eva.date);
				eva.flush(observeText, "[" + eva.time + "] 9.observe", eva.date);
				eva.flush(bugText, "[" + eva.time + "] 10.bug", eva.date);
				eva.flush(commandText, "[" + eva.time + "] 11.command", eva.date);
				sTemp = null;
				eva.date = null;
				eva.time = null;							
			}
			
			worldChatText.setText("");
		    nomalChatText.setText("");
		    whisperChatText.setText("");
		    clanChatText.setText("");
		    partyChatText.setText("");
		    tradeChatText.setText("");
		    wareHouseText.setText("");
		    tradeText.setText("");
		    enchantText.setText("");
		    observeText.setText("");
		    bugText.setText("");
		    commandText.setText("");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
