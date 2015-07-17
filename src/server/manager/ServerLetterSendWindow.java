package server.manager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import l1j.server.server.datatables.LetterTable;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_LetterList;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;


@SuppressWarnings("serial")
public class ServerLetterSendWindow extends JInternalFrame {
	private JLabel jJLabel1 = null;
	private JLabel jJLabel2 = null;
	private JLabel jJLabel3 = null;
	private JLabel jJLabel4 = null;
	
	
	public JTextField txt_To = null;
	public JTextPane txt_ReciveMsg = null;
	public JTextField txt_From = null;
	public JTextField txt_Title = null;
	public JTextPane txt_SendMsg = null;
	
	private JButton btn_Send = null;
	
	private JScrollPane reciveMsgScroll = null;
	private JScrollPane sendMsgScroll = null;

	public ServerLetterSendWindow(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
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
	    
	    jJLabel1 = new JLabel("Carrick patients receiving");
	    jJLabel2 = new JLabel("");
	    jJLabel3 = new JLabel("Carrick sent people");
	    jJLabel4 = new JLabel("Send title");
	    
	    txt_To = new JTextField();
	    txt_ReciveMsg = new JTextPane();
	    txt_From = new JTextField();
	    txt_Title = new JTextField();
	    txt_SendMsg = new JTextPane();
	    
	    reciveMsgScroll = new JScrollPane(txt_ReciveMsg);
	    sendMsgScroll = new JScrollPane(txt_SendMsg);
	    
	    reciveMsgScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    reciveMsgScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    reciveMsgScroll.setAutoscrolls(true);
	    reciveMsgScroll.getSize().setSize(400, 200);
	    
	    sendMsgScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	    sendMsgScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    sendMsgScroll.setAutoscrolls(true);
	    sendMsgScroll.getSize().setSize(400, 200);
	    	    
	    btn_Send = new JButton("Send");
	    btn_Send.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				if (eva.isServerStarted) {
					dataSend();
				} else {
					eva.errorMsg(eva.NoServerStartMSG);
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
		
		main.addGroup(horizontal_grp);
		main_horizontal_grp.addGroup(main);	
		
		layout.setHorizontalGroup(main_horizontal_grp);
		layout.setVerticalGroup(vertical_grp);
		
		col1.addComponent(jJLabel1, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
		    .addComponent(jJLabel2, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
		    .addComponent(jJLabel3, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
		    .addComponent(jJLabel4, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE);
		
		col2.addComponent(txt_To, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
		    .addComponent(reciveMsgScroll, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
		    .addComponent(txt_From, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
		    .addComponent(txt_Title, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
		    .addComponent(sendMsgScroll, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE);	
		
		horizontal_grp.addContainerGap().addGap(5).addGroup(col1).addContainerGap();
		horizontal_grp.addContainerGap().addGap(5).addGroup(col2).addContainerGap();
		horizontal_grp.addContainerGap().addGap(5).addGroup(col3).addContainerGap();
		
		main.addGroup(layout.createSequentialGroup().addGap(150, 150, 150).addComponent(btn_Send, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE));
		
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(jJLabel1)
				                                                                                  .addComponent(txt_To));
		
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(jJLabel2)
				                                                                                  .addComponent(reciveMsgScroll, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE));		
		
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(jJLabel3)
                																				  .addComponent(txt_From));

		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(jJLabel4)
		                                                                            			  .addComponent(txt_Title));
		
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(sendMsgScroll, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE));		
		
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addGap(19, 19, 19).addComponent(btn_Send)).addGap(5);
		
	}
	
	private void dataSend() {		
		try {
			if (txt_To.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Enter your name to receive Carrick.", "", JOptionPane.WARNING_MESSAGE);
				return;
			} else if (txt_From.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Please enter your name Carrick sent.", "", JOptionPane.WARNING_MESSAGE);
				return;
			} else if (txt_Title.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Enter to send the title.", "", JOptionPane.WARNING_MESSAGE);
				return;
			} else if (txt_SendMsg.getText().equals("")) {
				JOptionPane.showMessageDialog(null, "Enter the information you want to send.", "", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			L1PcInstance receiver = L1World.getInstance().getPlayer(txt_To.getText());
			if (receiver != null) {
				WritePrivateMail(receiver, txt_From.getText(), txt_Title.getText(), txt_SendMsg.getText());				
			} else {
				WritePrivateMail(txt_To.getText(), txt_From.getText(), txt_Title.getText(), txt_SendMsg.getText());				
			}
			JOptionPane.showMessageDialog(null, "I sent a letter.", "", JOptionPane.INFORMATION_MESSAGE);
			setClosed(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Send Mail failure.", "", JOptionPane.ERROR_MESSAGE);
		} 
		
	}
	
	private void WritePrivateMail(String receiver, String sender, String title, String message) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd", Locale.KOREA);
			String dTime = formatter.format(new Date());
			String receiverName = receiver;
			String subject = title;
			String content = message;
			
			LetterTable.getInstance().writeLetter(949, dTime, sender, receiverName, 0, subject, content);			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	private void WritePrivateMail(L1PcInstance receiver, String sender, String title, String message) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd", Locale.KOREA);
			String dTime = formatter.format(new Date());
			String receiverName = receiver.getName();
			String subject = title;
			String content = message;
			
			LetterTable.getInstance().writeLetter(949, dTime, sender, receiverName, 0, subject, content);			
			sendMessageToReceiver(receiver, 0, 20);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendMessageToReceiver(L1PcInstance receiver, final int type, final int MAILBOX_SIZE) {
		if (receiver != null && receiver.getOnlineStatus() != 0) {
			LetterList(receiver, type, MAILBOX_SIZE);
			receiver.sendPackets(new S_SkillSound(receiver.getId(), 1091));
			receiver.sendPackets(new S_ServerMessage(428)); // 편지가 도착했습니다.
		}
	}
	
	private void LetterList(L1PcInstance pc, int type, int count) {
		pc.sendPackets(new S_LetterList(pc, type, count));
	}
}
