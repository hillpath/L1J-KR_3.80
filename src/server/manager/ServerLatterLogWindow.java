package server.manager;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import l1j.server.L1DatabaseFactory;
import l1j.server.server.utils.SQLUtil;


@SuppressWarnings("serial")
public class ServerLatterLogWindow extends JInternalFrame {
	private JTable jJTable = null;
	private JScrollPane pScroll = null;
	
	private DefaultTableModel model = null;
	
	private JButton btn_Refresh = null;
	
	private ServerLetterSendWindow jServerLetterSendWindow = null;

	public ServerLatterLogWindow(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
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
		
	    updateUI();
	    
	    String[] modelColName = { "Number", "Send", "Subject", "Contents", "From Date", "Confirm", "Deletion" };
		
		model = new DefaultTableModel(modelColName, 0);
		
		jJTable = new JTable(model);
		
		jJTable.getColumnModel().getColumn(0).setPreferredWidth(30);
		jJTable.getColumnModel().getColumn(1).setPreferredWidth(70);
		jJTable.getColumnModel().getColumn(2).setPreferredWidth(100);
		jJTable.getColumnModel().getColumn(3).setPreferredWidth(250);	
		jJTable.getColumnModel().getColumn(4).setPreferredWidth(60);
		jJTable.getColumnModel().getColumn(5).setPreferredWidth(30);
		jJTable.getColumnModel().getColumn(6).setPreferredWidth(50);
		
		jJTable.addMouseListener(new MouseListenner());
		
	    pScroll = new JScrollPane(jJTable);
	    
		pScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pScroll.setAutoscrolls(true);
		
		Connection con = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("select * from letter where receiver in ('subject', 'sender', 'date') ORDER BY item_object_id DESC");
			rs = pstm.executeQuery();
			
			DefaultTableModel tModel = (DefaultTableModel)jJTable.getModel();
			Object[] letter = new Object[6];
			while (rs.next()) {
				letter[0] = String.valueOf(rs.getInt("item_object_id"));
				letter[1] = rs.getString("sender");
				letter[2] = rs.getString("subject");
				letter[3] = rs.getString("content");
				letter[4] = rs.getString("date");
				letter[5] = rs.getInt("isCheck") == 0 ? new Boolean("false") : new Boolean("true");
				tModel.addRow(letter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLUtil.close(rs);			
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
		
		
		JCheckBox checkBox = new JCheckBox();
		jJTable.getColumn("Confirm").setCellRenderer(new CheckRowCellEdior());
		jJTable.getColumn("Confirm").setCellEditor(new DefaultCellEditor(checkBox));
		jJTable.getColumn("Delete").setCellRenderer(new ButtonRowCellEdior());
		
		btn_Refresh = new JButton("Refresh");
		btn_Refresh.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				Connection con = null;
				PreparedStatement pstm = null;
				ResultSet rs = null;
				try {
					con = L1DatabaseFactory.getInstance().getConnection();
					pstm = con.prepareStatement("select * from letter where receiver in ('subject', 'sender', 'date') ORDER BY item_object_id DESC");
					rs = pstm.executeQuery();
					
					DefaultTableModel tModel = (DefaultTableModel)jJTable.getModel();
					
					for (int i = tModel.getRowCount() - 1; i >= 0; i--) {
						tModel.removeRow(i);					
					}					
					
					Object[] letter = new Object[6];
					while (rs.next()) {
						letter[0] = String.valueOf(rs.getInt("item_object_id"));
						letter[1] = rs.getString("sender");
						letter[2] = rs.getString("subject");
						letter[3] = rs.getString("content");
						letter[4] = rs.getString("date");
						letter[5] = rs.getInt("isCheck") == 0 ? new Boolean("false") : new Boolean("true");
						tModel.addRow(letter);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					SQLUtil.close(rs);			
					SQLUtil.close(pstm);
					SQLUtil.close(con);
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
		
		col1.addComponent(btn_Refresh, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE);
		
		
		horizontal_grp.addGap(5).addGroup(col1).addGap(5);
		horizontal_grp.addGroup(col2).addGap(5);
		horizontal_grp.addGroup(col3).addGap(5);
		horizontal_grp.addGroup(col4).addGap(5);
		horizontal_grp.addGroup(col5).addGap(5);
		horizontal_grp.addGroup(col6).addGap(5);
		
		main.addGroup(layout.createSequentialGroup().addComponent(pScroll));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(btn_Refresh)).addGap(5);	 
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(true, true).addComponent(pScroll));	 
		
	}
	

	
	private class MouseListenner extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == 1) {
				int column = ((JTable)e.getSource()).getSelectedColumn();
				int row = ((JTable)e.getSource()).getSelectedRow();
				
				if (column < 5) {
					//JOptionPane.showMessageDialog(null, ((JTable)e.getSource()).getValueAt(row, 3), "³»¿ë", JOptionPane.INFORMATION_MESSAGE);
					
					if (jServerLetterSendWindow != null && jServerLetterSendWindow.isClosed()) {
						jServerLetterSendWindow = null;
					}
					
					if (jServerLetterSendWindow == null) {
						jServerLetterSendWindow = new ServerLetterSendWindow("Letter", 0, 0, eva.width - 190, eva.height + 110, true, true);						
						eva.jJDesktopPane.add(jServerLetterSendWindow, 0);
						
						jServerLetterSendWindow.txt_To.setText(String.valueOf(((JTable)e.getSource()).getValueAt(row, 1)));
						jServerLetterSendWindow.txt_ReciveMsg.setText(String.valueOf(((JTable)e.getSource()).getValueAt(row, 3)));
						jServerLetterSendWindow.txt_From.setText("sender");
						
						jServerLetterSendWindow.setLocation((eva.jJFrame.getContentPane().getSize().width / 2) - (jServerLetterSendWindow.getContentPane().getSize().width / 2), (eva.jJFrame.getContentPane().getSize().height / 2) - (jServerLetterSendWindow.getContentPane().getSize().height / 2));
					}
					
					if (((Boolean)((JTable)e.getSource()).getValueAt(row, 5)).booleanValue() == false) {					
						Connection con = null;
						PreparedStatement pstm = null;
						ResultSet rs = null;
						try {
							con = L1DatabaseFactory.getInstance().getConnection();
							pstm = con.prepareStatement("update letter set isCheck = ? where item_object_id = ?");
							pstm.setInt(1, 1);
							pstm.setInt(2, Integer.parseInt((String)((JTable)e.getSource()).getValueAt(row, 0)));
							pstm.execute();
							
							pstm = con.prepareStatement("select * from letter where receiver in ('sender', 'subject', 'date') ORDER BY item_object_id DESC");
							rs = pstm.executeQuery();
							
							DefaultTableModel tModel = (DefaultTableModel)jJTable.getModel();
							
							for (int i = tModel.getRowCount() - 1; i >= 0; i--) {
								tModel.removeRow(i);					
							}					
							
							Object[] letter = new Object[6];
							while (rs.next()) {
								letter[0] = String.valueOf(rs.getInt("item_object_id"));
								letter[1] = rs.getString("sender");
								letter[2] = rs.getString("subject");
								letter[3] = rs.getString("content");
								letter[4] = rs.getString("date");
								letter[5] = rs.getInt("isCheck") == 0 ? new Boolean("false") : new Boolean("true");
								tModel.addRow(letter);
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {		
							SQLUtil.close(rs);			
							SQLUtil.close(pstm);
							SQLUtil.close(con);
						} 
					}
				} else if (column == 6) { 
					Connection con = null;
					PreparedStatement pstm = null;
					ResultSet rs = null;
					try {
						con = L1DatabaseFactory.getInstance().getConnection();
						pstm = con.prepareStatement("delete from letter where item_object_id = ?");
						pstm.setInt(1, Integer.parseInt((String)((JTable)e.getSource()).getValueAt(row, 0)));
						pstm.execute();
						
						pstm = con.prepareStatement("select * from letter where receiver in ('sender', 'subject', 'date') ORDER BY item_object_id DESC");
						rs = pstm.executeQuery();
						
						DefaultTableModel tModel = (DefaultTableModel)jJTable.getModel();
						
						for (int i = tModel.getRowCount() - 1; i >= 0; i--) {
							tModel.removeRow(i);					
						}					
						
						Object[] letter = new Object[6];
						while (rs.next()) {
							letter[0] = String.valueOf(rs.getInt("item_object_id"));
							letter[1] = rs.getString("sender");
							letter[2] = rs.getString("subject");
							letter[3] = rs.getString("content");
							letter[4] = rs.getString("date");
							letter[5] = rs.getInt("isCheck") == 0 ? new Boolean("false") : new Boolean("true");
							tModel.addRow(letter);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					} finally {		
						SQLUtil.close(rs);			
						SQLUtil.close(pstm);
						SQLUtil.close(con);
					} 					
				}
			}
		}
	}
	
	private class CheckRowCellEdior extends JCheckBox implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
			setSelected(((Boolean)value).booleanValue());
			setHorizontalAlignment(JLabel.CENTER);
			return this;
		}
	}	 

	private class ButtonRowCellEdior extends JButton implements TableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
			setText("Confirm");
			setHorizontalAlignment(JLabel.CENTER);
			return this;
		}
	}
}
