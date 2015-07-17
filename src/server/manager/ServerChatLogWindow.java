package server.manager;
import java.awt.Color;
import java.io.File;
import java.util.StringTokenizer;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;


@SuppressWarnings("serial")
public class ServerChatLogWindow extends JInternalFrame {
	private JTextPane textPane = null;
	private JScrollPane pScroll = null;
	
	public JCheckBox chk_World = null;
	public JCheckBox chk_Noaml = null;
	public JCheckBox chk_Whisper = null;
	public JCheckBox chk_Clan = null;
	public JCheckBox chk_Party = null;
	
	private JButton btn_Clear = null;

	public ServerChatLogWindow(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
		super();
		
		initialize(windowName, x, y, width, height, resizable, closable);
	}
	
	public void initialize(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
		this.title = windowName;
		this.closable = closable;      
		this.isMaximum = false;	   
		this.maximizable = true;
		this.resizable = resizable;
		this.iconable = true;   
		this.isIcon = false;			  
	    setSize(width, height);
		setBounds(x, y, width, height);
		setVisible(true);
		frameIcon = new ImageIcon("");
		setRootPaneCheckingEnabled(true);
		addInternalFrameListener(new InternalFrameAdapter() {
			public void internalFrameClosing(InternalFrameEvent e) {
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
						eva.flush(textPane, "[" + eva.time + "] " + title + " Window", eva.date);
						sTemp = null;
						eva.date = null;
						eva.time = null;							
					}
					
					textPane.setText("");
				} catch (Exception ex) {
					// TODO: handle exception
				}
			}
		});
	    updateUI();
	    
	    textPane = new JTextPane();
	    pScroll = new JScrollPane(textPane);
	    textPane.setEditable(false);		
		pScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pScroll.setAutoscrolls(true);
		
		chk_World = new JCheckBox("World");		
		chk_Noaml = new JCheckBox("Normal");
		chk_Whisper = new JCheckBox("Whisper");
		chk_Clan = new JCheckBox("Clan");
		chk_Party = new JCheckBox("Party");
		
		chk_World.setSelected(true);
		chk_Noaml.setSelected(true);
		chk_Whisper.setSelected(true);
		chk_Clan.setSelected(true);
		chk_Party.setSelected(true);
		
		btn_Clear = new JButton("Clear");
	    btn_Clear.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				try {
					textPane.setText("");
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
		GroupLayout.ParallelGroup col5 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
		GroupLayout.ParallelGroup col6 = layout.createParallelGroup(GroupLayout.Alignment.LEADING);
			
		
		main.addGroup(horizontal_grp);
		main_horizontal_grp.addGroup(main);	
		
		layout.setHorizontalGroup(main_horizontal_grp);
		layout.setVerticalGroup(vertical_grp);
		
		col1.addComponent(chk_World, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE);
		col2.addComponent(chk_Noaml, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE);
		col3.addComponent(chk_Whisper, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE);
		col4.addComponent(chk_Clan, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE);
		col5.addComponent(chk_Party, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE);
		col6.addComponent(btn_Clear, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE);
		
		
		horizontal_grp.addGroup(col1).addGap(5);
		horizontal_grp.addGroup(col2).addGap(5);
		horizontal_grp.addGroup(col3).addGap(5);
		horizontal_grp.addGroup(col4).addGap(5);
		horizontal_grp.addGroup(col5).addGap(5);
		horizontal_grp.addGroup(col6).addGap(5);
		
		main.addGroup(layout.createSequentialGroup().addComponent(pScroll));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(true, true).addComponent(pScroll));	 
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(chk_World)
				                                                                                  .addComponent(chk_Noaml)
				                                                                                  .addComponent(chk_Whisper)
				                                                                                  .addComponent(chk_Clan)
				                                                                                  .addComponent(chk_Party)
				                                                                                  .addComponent(btn_Clear));	 
		
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
	}
	
	public void append(String msg, String color) {
		StyledDocument doc = textPane.getStyledDocument();	

		try {
		    doc.insertString(doc.getLength(), msg, textPane.getStyle(color));		    		    
		    textPane.setCaretPosition(textPane.getDocument().getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	public void savelog(String a) {
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
				eva.flush(textPane, "[" + eva.time + "] " + this.title + " Window", eva.date);
				sTemp = null;
				eva.date = null;
				eva.time = null;							
			}
			
			textPane.setText("");
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
