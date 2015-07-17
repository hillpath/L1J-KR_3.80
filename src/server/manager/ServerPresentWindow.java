package server.manager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Armor;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Weapon;


@SuppressWarnings("serial")
public class ServerPresentWindow extends JInternalFrame {
	private JLabel jJLabel1 = null;
	private JLabel jJLabel2 = null;
	private JLabel jJLabel3 = null;
	private JLabel jJLabel4 = null;
	private JLabel jJLabel5 = null;
	private JLabel jJLabel6 = null;
	private JLabel jJLabel7 = null;
	private JLabel jJLabel8 = null;
	
	
	public JTextField txt_UserName = null;
	private JTextField txt_ItemId = null;
	private JTextField txt_ItemName = null;
	private JTextField txt_Count = null;
	private JTextField txt_Enchantlvl = null;
	private JTextField txt_AttrEnchantlvl = null;
	private JTextField txt_StepEnchantlvl = null;
	private JTextField txt_ItemNameSearch = null;
	
	private JButton btn_Search = null;
	private JButton btn_Give = null;
	
	private JTable jJTable = null;
	private JScrollPane pScroll = null;
	
	private DefaultTableModel model = null;

	public ServerPresentWindow(String windowName, int x, int y, int width, int height, boolean resizable, boolean closable) {
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
	    
	    jJLabel1 = new JLabel("Character");
	    jJLabel2 = new JLabel("Number");
	    jJLabel3 = new JLabel("Designation");
	    jJLabel4 = new JLabel("The number");
	    jJLabel5 = new JLabel("Enchanted");
	    jJLabel6 = new JLabel("Attribute");
	    jJLabel7 = new JLabel("Phase");
	    jJLabel8 = new JLabel("Retrieving Name");
	    
	    txt_UserName = new JTextField();
	    txt_UserName.setEditable(false);
	    txt_ItemId = new JTextField();
	    txt_ItemName = new JTextField();
	    txt_Count = new JTextField();
	    txt_Count.setText("1");
	    txt_Enchantlvl = new JTextField();
	    txt_Enchantlvl.setText("0");
	    txt_AttrEnchantlvl = new JTextField();
	    txt_AttrEnchantlvl.setText("0");
	    txt_StepEnchantlvl = new JTextField();
	    txt_StepEnchantlvl.setText("0");
	    txt_ItemNameSearch = new JTextField();
	    txt_ItemNameSearch.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyPressed(java.awt.event.KeyEvent evt) {
				chatKeyPressed(evt);
			}
		});
	    	    
	    btn_Search = new JButton("Search");
	    btn_Search.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				if (eva.isServerStarted) {
					try {					
						DefaultTableModel tModel = (DefaultTableModel)jJTable.getModel();
						for (int i = tModel.getRowCount() - 1; i >= 0; i--) {
							tModel.removeRow(i);					
						}
						
						String[] item = new String[2];
						
						int itemId = 0;
						
						try {
							itemId = Integer.parseInt(txt_ItemNameSearch.getText());
							
							for (L1Weapon weapon : ItemTable.getInstance()._weapons.values()) {						
								if (String.valueOf(weapon.getItemId()).replace(" ", "").indexOf(txt_ItemNameSearch.getText()) > -1) {
									item[0] = String.valueOf(weapon.getItemId());
									item[1] = weapon.getName();
									
									tModel.addRow(item);
								}
							}
							
							for (L1Armor armor : ItemTable.getInstance()._armors.values()) {
								if (String.valueOf(armor.getItemId()).replace(" ", "").indexOf(txt_ItemNameSearch.getText()) > -1) {
									item[0] = String.valueOf(armor.getItemId());
									item[1] = armor.getName();
									
									tModel.addRow(item);
								}
							}
							
							for (L1EtcItem etcitem : ItemTable.getInstance()._etcitems.values()) {
								if (String.valueOf(etcitem.getItemId()).replace(" ", "").indexOf(txt_ItemNameSearch.getText()) > -1) {
									item[0] = String.valueOf(etcitem.getItemId());
									item[1] = etcitem.getName();
									
									tModel.addRow(item);
								}
							}
						} catch (Exception ex) {
							for (L1Weapon weapon : ItemTable.getInstance()._weapons.values()) {						
								if (weapon.getName().replace(" ", "").indexOf(txt_ItemNameSearch.getText()) > -1) {
									item[0] = String.valueOf(weapon.getItemId());
									item[1] = weapon.getName();
									
									tModel.addRow(item);
								}
							}
							
							for (L1Armor armor : ItemTable.getInstance()._armors.values()) {
								if (armor.getName().replace(" ", "").indexOf(txt_ItemNameSearch.getText()) > -1) {
									item[0] = String.valueOf(armor.getItemId());
									item[1] = armor.getName();
									
									tModel.addRow(item);
								}
							}
							
							for (L1EtcItem etcitem : ItemTable.getInstance()._etcitems.values()) {
								if (etcitem.getName().replace(" ", "").indexOf(txt_ItemNameSearch.getText()) > -1) {
									item[0] = String.valueOf(etcitem.getItemId());
									item[1] = etcitem.getName();
									
									tModel.addRow(item);
								}
							}
						}
						
						
					} catch (Exception ex) {
						
					}
				} else {
					eva.errorMsg(eva.NoServerStartMSG);
				}
			}
		});
	    btn_Give = new JButton("Payments"); 
	    btn_Give.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) {
				if (eva.isServerStarted) {
					try {
						if (txt_UserName.getText().equalsIgnoreCase("")) {
							eva.errorMsg(eva.blankSetUser);
							return;
						}
						
						int itemid = Integer.parseInt(txt_ItemId.getText());
						int enchant = Integer.parseInt(txt_Enchantlvl.getText());
						int count = Integer.parseInt(txt_Count.getText());
						L1Item temp = ItemTable.getInstance().getTemplate(itemid);

						for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {	
							if (pc == null || pc.getNetConnection() == null || pc.noPlayerCK || pc.isPrivateShop()) {
								continue;
							}
							
							if (pc.getName().equalsIgnoreCase(txt_UserName.getText()) || txt_UserName.getText().equalsIgnoreCase("Full user")) {
								if (temp != null) {
									L1ItemInstance item = ItemTable.getInstance().createItem(itemid);
									item.setEnchantLevel(enchant);
									item.setCount(count);
									int createCount;
									if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
										pc.getInventory().storeItem(item);
										pc.sendPackets(new S_SystemMessage(temp.getName() + "And (a) had to create."));
										
										eva.LogServerAppend(txt_UserName.getText() + "To " + temp.getName() + "And (a) it had produced.", "Check the wind");
									} else {
										L1ItemInstance item1 = null;
										for (createCount = 0; createCount < count; createCount++) {
											item1 = ItemTable.getInstance().createItem(itemid);
											item1.setEnchantLevel(enchant);
											if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
												pc.getInventory().storeItem(item);
											} else
												break;
										}
					
										if (createCount > 0) {
											pc.sendPackets(new S_SystemMessage(temp.getName() + "(A) to" + createCount + "Created more."));

											eva.LogServerAppend(txt_UserName.getText() + "To " + temp.getName() + "And (a)" + createCount + "Created more.", "확인바람");
										}
									}
								} else if (temp == null) {
									eva.LogServerAppend("[Gift giving Failed such items do not exist.", "Check the wind");
								}
							}
						}
					} catch (Exception ex) {
						
					}
				} else {
					eva.errorMsg(eva.NoServerStartMSG);
				}
			}
		});
	    
	    String[] modelColName = { "Number", "Designation" };
		
		model = new DefaultTableModel(modelColName, 0);
		
		jJTable = new JTable(model);
		
		jJTable.getColumnModel().getColumn(0).setPreferredWidth(70);
		jJTable.getColumnModel().getColumn(1).setPreferredWidth(180);
		
		jJTable.addMouseListener(new MouseListenner());
				
		
	    pScroll = new JScrollPane(jJTable);
	    
		pScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		pScroll.setAutoscrolls(true);
		
		
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
		
		col1.addComponent(jJLabel1, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
		    .addComponent(jJLabel8, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
            .addComponent(jJLabel2, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
            .addComponent(jJLabel4, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
            .addComponent(jJLabel6, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE);
		
		col2.addComponent(txt_UserName, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
		    .addComponent(txt_ItemNameSearch, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
            .addComponent(txt_ItemId, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
            .addComponent(txt_Count, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
            .addComponent(txt_AttrEnchantlvl, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE);
		
		col3.addComponent(btn_Search, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
		    .addComponent(jJLabel3, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
		    .addComponent(jJLabel5, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
		    .addComponent(jJLabel7, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE);
		
		col4.addComponent(txt_ItemName, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
            .addComponent(txt_Enchantlvl, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
            .addComponent(txt_StepEnchantlvl, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE);
		
		
		horizontal_grp.addContainerGap().addGap(5).addGroup(col1).addContainerGap();
		horizontal_grp.addContainerGap().addGap(5).addGroup(col2).addContainerGap();
		horizontal_grp.addContainerGap().addGap(5).addGroup(col3).addContainerGap();
		horizontal_grp.addContainerGap().addGap(5).addGroup(col4).addContainerGap();
		
		main.addGroup(layout.createSequentialGroup().addComponent(pScroll));
		main.addGroup(layout.createSequentialGroup().addGap(150, 150, 150).addComponent(btn_Give, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE));
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(jJLabel1)
				                                                                                  .addComponent(txt_UserName));
		
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(jJLabel8)
				                                                                                  .addComponent(txt_ItemNameSearch)
				                                                                                  .addComponent(btn_Search));		
		
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(true, true).addComponent(pScroll));
		
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(jJLabel2)
																								  .addComponent(txt_ItemId)
																								  .addComponent(jJLabel3)
																								  .addComponent(txt_ItemName));
		
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(jJLabel4)
																								  .addComponent(txt_Count)
																								  .addComponent(jJLabel5)
																								  .addComponent(txt_Enchantlvl));
		
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addComponent(jJLabel6)
																								  .addComponent(txt_AttrEnchantlvl)
																								  .addComponent(jJLabel7)
																								  .addComponent(txt_StepEnchantlvl));
		
		vertical_grp.addGap(5).addContainerGap().addGroup(layout.createBaselineGroup(false, false).addGap(19, 19, 19).addComponent(btn_Give)).addGap(5);
		
	}
	

	
	private class MouseListenner extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == 1) {
				int column = ((JTable)e.getSource()).getSelectedColumn();
				int row = ((JTable)e.getSource()).getSelectedRow();	
				
				txt_ItemId.setText((String)((JTable)e.getSource()).getValueAt(row, 0));
				txt_ItemName.setText((String)((JTable)e.getSource()).getValueAt(row, 1));
			}
		}
	}	
	
	private void chatKeyPressed(KeyEvent evt) {
		// 서버 채팅
		if (eva.isServerStarted) {
			try {
				if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
					DefaultTableModel tModel = (DefaultTableModel)jJTable.getModel();
					for (int i = tModel.getRowCount() - 1; i >= 0; i--) {
						tModel.removeRow(i);					
					}
					
					String[] item = new String[2];					
					int itemId = 0;
					
					try {
						itemId = Integer.parseInt(txt_ItemNameSearch.getText());
						
						for (L1Weapon weapon : ItemTable.getInstance()._weapons.values()) {						
							if (String.valueOf(weapon.getItemId()).replace(" ", "").indexOf(txt_ItemNameSearch.getText()) > -1) {
								item[0] = String.valueOf(weapon.getItemId());
								item[1] = weapon.getName();
								
								tModel.addRow(item);
							}
						}
						
						for (L1Armor armor : ItemTable.getInstance()._armors.values()) {
							if (String.valueOf(armor.getItemId()).replace(" ", "").indexOf(txt_ItemNameSearch.getText()) > -1) {
								item[0] = String.valueOf(armor.getItemId());
								item[1] = armor.getName();
								
								tModel.addRow(item);
							}
						}
						
						for (L1EtcItem etcitem : ItemTable.getInstance()._etcitems.values()) {
							if (String.valueOf(etcitem.getItemId()).replace(" ", "").indexOf(txt_ItemNameSearch.getText()) > -1) {
								item[0] = String.valueOf(etcitem.getItemId());
								item[1] = etcitem.getName();
								
								tModel.addRow(item);
							}
						}
					} catch (Exception e) {
						for (L1Weapon weapon : ItemTable.getInstance()._weapons.values()) {						
							if (weapon.getName().replace(" ", "").indexOf(txt_ItemNameSearch.getText()) > -1) {
								item[0] = String.valueOf(weapon.getItemId());
								item[1] = weapon.getName();
								
								tModel.addRow(item);
							}
						}
						
						for (L1Armor armor : ItemTable.getInstance()._armors.values()) {
							if (armor.getName().replace(" ", "").indexOf(txt_ItemNameSearch.getText()) > -1) {
								item[0] = String.valueOf(armor.getItemId());
								item[1] = armor.getName();
								
								tModel.addRow(item);
							}
						}
						
						for (L1EtcItem etcitem : ItemTable.getInstance()._etcitems.values()) {
							if (etcitem.getName().replace(" ", "").indexOf(txt_ItemNameSearch.getText()) > -1) {
								item[0] = String.valueOf(etcitem.getItemId());
								item[1] = etcitem.getName();
								
								tModel.addRow(item);
							}
						}
					}
				}
			} catch (Exception ex) {
				
			}
		} else {
			eva.errorMsg(eva.NoServerStartMSG);
		}
	}

}
