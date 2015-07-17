/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * goitem.java
 *
 * Created on 2010. 6. 10, ¿ÀÈÄ 9:20:49
 */

package server.manager;

import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import l1j.server.server.datatables.ItemTable;
import l1j.server.server.model.L1Inventory;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Item;

@SuppressWarnings("serial")
public class goitem extends javax.swing.JFrame {
  
 /** Creates new form goitem */
 public goitem() {  
  initComponents();
  setLocation(200, 100);
  setVisible(true);
   
 }
 
 private void initComponents() {

  label1 = new java.awt.Label();
  giftnickname = new java.awt.TextField();
  label2 = new java.awt.Label();
  itemnumber = new java.awt.TextField();
  label3 = new java.awt.Label();
  enchantlevel = new java.awt.TextField();
  label4 = new java.awt.Label();
  count = new java.awt.TextField();

  setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
  setTitle("Gift giving");
  
  label1.setText("Nickname");
  giftnickname.setText("");
  label2.setText("itemnumber");
  itemnumber.setText("");
  label3.setText("enchantlevel");
  enchantlevel.setText("");
  label4.setText("count");
  count.setText("");
  buttongoitem = new java.awt.Button();
  buttongoitem.setLabel("Gift giving");  
  buttongoitem.addMouseListener(new java.awt.event.MouseAdapter() {
   public void mouseClicked(java.awt.event.MouseEvent evt) {
    buttongoitemClicked(evt);
   }
  });
  javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
  getContentPane().setLayout(layout);
  layout.setHorizontalGroup(
    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addGroup(layout.createSequentialGroup()
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
          .addContainerGap()
          .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
              .addComponent(giftnickname, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
              .addComponent(enchantlevel, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE))
              .addGap(22, 22, 22)
              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(itemnumber, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)
                  .addComponent(count, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)))
                  .addGroup(layout.createSequentialGroup()
                    .addGap(117, 117, 117)
                    .addComponent(buttongoitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap())
  );
  layout.setVerticalGroup(
    layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
    .addGroup(layout.createSequentialGroup()
      .addContainerGap()
      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(giftnickname, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addComponent(itemnumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(enchantlevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(count, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19)
                .addComponent(buttongoitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
  );

  pack();
 }

 private void buttongoitemClicked(MouseEvent evt) {
    String name2 = giftnickname.getText();
    
    int itemid = Integer.parseInt(itemnumber.getText());
   int enchant = Integer.parseInt(enchantlevel.getText());
   int count = Integer.parseInt(count.getText());
   L1Item temp = ItemTable.getInstance().getTemplate(itemid);
   
   for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
     if(pc.getName().equalsIgnoreCase(name2)){
       if (temp != null) {
         L1ItemInstance item = ItemTable.getInstance().createItem(itemid);
       item.setEnchantLevel(enchant);
       item.setCount(count);
       int createCount;
       if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
         pc.getInventory().storeItem(item);
         pc.sendPackets(new S_SystemMessage(temp.getName()+ "And (a) it had produced."));
         eva.LogServerAppend(temp.getName()+ "And (a) had to create.","Check the wind");
       }
       else {
         L1ItemInstance item1 = null;
       for (createCount = 0; createCount < count; createCount++) {
         item1 = ItemTable.getInstance().createItem(itemid);
        item1.setEnchantLevel(enchant);
        if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
         pc.getInventory().storeItem(item);
        } 
        else break;
       }
       
       if (createCount > 0){
        pc.sendPackets(new S_SystemMessage(temp.getName()+ "(A) to" + createCount + "Created more."));
        eva.LogServerAppend(temp.getName()+ "And (a)" + createCount + "Created more.","Check the wind");
       }
       }
       }
       else if (temp == null){
        eva.LogServerAppend("[Gift giving Failed such items do not exist.","Check the wind");
     }
     }
   }
  JOptionPane.showMessageDialog(this, name2+"To "+temp.getName()+"  Gift.", " Server Message", javax.swing.JOptionPane.INFORMATION_MESSAGE);
  this.setVisible(false);
 }

 @SuppressWarnings("unused")
 private static goitem getInstance() {
  // TODO Auto-generated method stub
  return null;
 }

 // Variables declaration - do not modify//GEN-BEGIN:variables
 private java.awt.Button buttongoitem;
 private java.awt.Label label1;
 private java.awt.Label label2;
 private java.awt.Label label3;
 private java.awt.Label label4;
 private java.awt.TextField giftnickname;
 private java.awt.TextField count;
 private java.awt.TextField itemnumber;
 private java.awt.TextField enchantlevel;
 
 // End of variables declaration//GEN-END:variables

}