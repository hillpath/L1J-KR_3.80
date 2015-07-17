package l1j.server.server.model.Instance;


import javolution.util.FastTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Attack;
import l1j.server.server.serverpackets.S_ChangeHeading;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;
import l1j.server.server.utils.CalcExp;
import java.util.Random;

public class L1ScarecrowInstance extends L1NpcInstance {

	private static final long serialVersionUID = 1L;

	private static Random _random = new Random(System.nanoTime());

	
	public L1ScarecrowInstance(L1Npc template) {
		super(template);
	}

	@Override
	public void onAction(L1PcInstance player) {
		L1Attack attack = new L1Attack(player, this);
		if (player.isInParty()) { // Ãß°¡ Çã¼ö¾Æºñ¹ö±×¹æÁö

		} else {
			if (attack.calcHit()) {
				if (this.getNpcId() == 4500060){
				    if (player.getInventory().findItemId(40308).getCount() < 1000000){ 
				     player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
				       "100¸¸¿ø ÀÌ»ó ¼ÒÁö½Ã °ÔÀÓ°¡´ÉÇÕ´Ï´Ù."));
				     return; 
				     }
				    this.setNpoint(this.getNpoint()+25000);
				    player.getInventory().consumeItem(40308, 50000);//5¸¸
				    int chance = _random.nextInt(15000)+1;
				    if (chance <= 100){ 
				     player.getInventory().storeItem(40308, 700000);
				     
				     String chatId = " ´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 70¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 101 && chance <= 150){
				     player.getInventory().storeItem(40308, 1200000);
				     String chatId = " ´çÃ·:"+player.getName()+"\\fY´Ô 120¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >=151 && chance <= 175){
				     player.getInventory().storeItem(40308, 2000000);
				     String chatId = " ´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 200¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >=176 && chance <= 189){
				     player.getInventory().storeItem(40308, 3500000);
				     String chatId = " ´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 350¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 190 && chance <= 192){
				     player.getInventory().storeItem(40308, 10000000);
				     String chatId = " 3µî´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 1000¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 193 && chance <= 194){
				     player.getInventory().storeItem(40308, 30000000);
				     String chatId = " 2µî´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 3000¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 194 && chance < 196){
				     player.getInventory().storeItem(40308, 70000000);
				        Broadcaster.broadcastPacket(this, new S_PacketBox(S_PacketBox.GREEN_MESSAGE, " 1µî´çÃ·:"+player.getName()+"´Ô1µî´çÃ· »ó±Ý 7Ãµ¸¸¿ø´çÃ·"));
				       } else if  (chance >= 196 && chance <= 200){
				        int k2 = (int) (this.getNpoint()*0.6);
				        int k3 = this.setNpoint(this.getNpoint() - k2);
				        player.getInventory().storeItem(40308, k3);
				     Broadcaster.broadcastPacket(this, new S_PacketBox(S_PacketBox.GREEN_MESSAGE, " ÀèÆÌ:"+player.getName()+"´Ô »ó±Ý ["+k3+"]¿ø È¹µæ"));
				     this.setNpoint(k2);
				       } else if (chance >= 201 && chance <= 15000){
				        player.sendPackets(new S_SystemMessage("\\fY½ÇÆÐ : [25000]¿ø Àû¸³  , ÃÑ : ["+this.getNpoint()+"]¿ø Àû¸³"));
				    }
				    }
				   if (this.getNpcId() == 4500061){
				    if (player.getInventory().findItemId(40308).getCount() < 10000000){ 
				     player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
				       "1000¸¸¿ø ÀÌ»ó ¼ÒÁö½Ã °ÔÀÓ°¡´ÉÇÕ´Ï´Ù."));
				     return; 
				     }
				    this.setSpoint(this.getSpoint()+50000);
				    player.getInventory().consumeItem(40308, 100000);//10¸¸
				    int chance = _random.nextInt(15000)+1;
				    if (chance <= 100){ 
				     player.getInventory().storeItem(40308, 1000000);
				     String chatId = " ´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 100¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 101 && chance <= 150){
				     player.getInventory().storeItem(40308, 3000000);
				     String chatId = " ´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 300¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 151 && chance <= 175){
				     player.getInventory().storeItem(40308, 4000000);
				     String chatId = " ´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 400¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 176 && chance <= 189){
				     player.getInventory().storeItem(40308, 10000000);
				     String chatId = " ´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 1000¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 190 && chance <= 192){
				     player.getInventory().storeItem(40308, 30000000);
				     String chatId = " 3µî´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 3000¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 193 && chance <= 194){
				     player.getInventory().storeItem(40308, 40000000);
				     String chatId = " 2µî´çÃ·:"+player.getName()+"\\f´Ô ¾Æµ¥³ª 4000¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 194 && chance < 196){
				     player.getInventory().storeItem(40308, 130000000);
				     Broadcaster.broadcastPacket(this, new S_PacketBox(S_PacketBox.GREEN_MESSAGE, " 1µî´çÃ·:"+player.getName()+"´Ô 1µî´çÃ·±Ý1¾ï3ÃµÈ¹µæ"));
				       } else if  (chance >= 196 && chance <= 200){
				        int k2 = (int) (this.getSpoint()*0.6);
				        int k3 = this.setSpoint(this.getSpoint() - k2);
				        player.getInventory().storeItem(40308, k3);
				     Broadcaster.broadcastPacket(this, new S_PacketBox(S_PacketBox.GREEN_MESSAGE, " ÀèÆÌ:"+player.getName()+"´Ô »ó±Ý ["+k3+"]¿ø È¹µæ"));
				     this.setSpoint(k2);
				       } else if (chance >= 201 && chance <= 15000){
				        player.sendPackets(new S_SystemMessage("\\fY½ÇÆÐ : [50000]¿ø Àû¸³  , ÃÑ : ["+this.getSpoint()+
				       "]¿ø Àû¸³"));
				    }
				    }
				   if (this.getNpcId() == 4500062){
				    if (player.getInventory().findItemId(40308).getCount() < 300000){ 
				     player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,
				       "30¸¸¿ø ÀÌ»ó ¼ÒÁö½Ã °ÔÀÓ°¡´ÉÇÕ´Ï´Ù."));
				     return; 
				     }
				    this.setKpoint(this.getKpoint() + 5000);
				    player.getInventory().consumeItem(40308, 10000);//1¸¸¿ø
				    int chance = _random.nextInt(15000)+1;
				    if (chance <= 100){ 
				     player.getInventory().storeItem(40308, 200000);
				     String chatId = " ´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 20¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 101 && chance <= 150){
				     player.getInventory().storeItem(40308, 400000);
				     String chatId = " ´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 40¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 151 && chance <= 175){
				     player.getInventory().storeItem(40308, 500000);
				     String chatId = " ´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 50¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 176 && chance <= 189){
				     player.getInventory().storeItem(40308, 1000000);
				     String chatId = " ´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 100¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 190 && chance <= 192){
				     player.getInventory().storeItem(40308, 3000000);
				     String chatId = " 3µî´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 300¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 193 && chance <= 194){
				     player.getInventory().storeItem(40308, 5000000);
				     String chatId = " 2µî´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 500¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 194 && chance < 196){
				     player.getInventory().storeItem(40308, 30000000);
				     Broadcaster.broadcastPacket(this, new S_PacketBox(S_PacketBox.GREEN_MESSAGE, " 1µî´çÃ·:"+player.getName()+"´Ô 1µî´çÃ·»ó±Ý 3000¸¸¿øÈ¹µæ"));
				       } else if  (chance >= 196 && chance <= 200){
				        int k2 = (int) (this.getKpoint()*0.6);
				        int k3 = this.setKpoint(this.getKpoint() - k2);
				        player.getInventory().storeItem(40308, k3);
				     Broadcaster.broadcastPacket(this, new S_PacketBox(S_PacketBox.GREEN_MESSAGE, " ÀèÆÌ:"+player.getName()+"´Ô »ó±Ý ["+k3+"]¿ø È¹µæ"));
				     this.setKpoint(k2);
				       } else if (chance >= 201 && chance <= 15000){
				        player.sendPackets(new S_SystemMessage("\\fY½ÇÆÐ : [5000]¿ø Àû¸³  , ÃÑ : ["+this.getKpoint()+
				       "]¿ø Àû¸³"));
				    }
				    }
				   if (this.getNpcId() == 4500063){
				    if (player.getInventory().findItemId(40308).getCount() < 500000){ 
				     player.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"50¸¸¿ø ÀÌ»ó ¼ÒÁö½Ã °ÔÀÓ°¡´ÉÇÕ´Ï´Ù."));
				     return; 
				     }
				    this.setPpoint(this.getPpoint()+15000);
				    player.getInventory().consumeItem(40308, 30000);//3¸¸
				    int chance = _random.nextInt(15000)+1;
				    if (chance <= 100){ 
				     player.getInventory().storeItem(40308, 300000);
				     String chatId = " ´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 30¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 101 && chance <= 150){
				     player.getInventory().storeItem(40308, 500000);
				     String chatId = " ´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 50¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 151 && chance <= 175){
				     player.getInventory().storeItem(40308, 1000000);
				     String chatId = " ´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 100¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 176 && chance <= 189){
				     player.getInventory().storeItem(40308, 3000000);
				     String chatId = " ´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 300¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 190 && chance <= 192){
				     player.getInventory().storeItem(40308, 5000000);
				     String chatId = " 3µî´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 500¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 193 && chance <= 194){
				     player.getInventory().storeItem(40308, 20000000);
				     String chatId = " 2µî´çÃ·:"+player.getName()+"\\fY´Ô ¾Æµ¥³ª 2000¸¸¿ø´çÃ·!";
				     Broadcaster.broadcastPacket(this, new S_NpcChatPacket(this, chatId, 0));
				       } else if (chance >= 194 && chance < 196){
				     player.getInventory().storeItem(40308, 60000000);
				     Broadcaster.broadcastPacket(this, new S_PacketBox(S_PacketBox.GREEN_MESSAGE, " 1µî´çÃ·:"+player.getName()+"´Ô 1µî´çÃ·»ó±Ý 6000¸¸¿øÈ¹µæ"));
				       } else if  (chance >= 196 && chance <= 200){
				        int k2 = (int) (this.getPpoint()*0.6);
				        int k3 = this.setPpoint(this.getPpoint() - k2);
				        player.getInventory().storeItem(40308, k3);
				     Broadcaster.broadcastPacket(this, new S_PacketBox(S_PacketBox.GREEN_MESSAGE, " ÀèÆÌ:"+player.getName()+"´Ô »ó±Ý ["+k3+"]¿ø È¹µæ"));
				     this.setPpoint(k2);
				       } else if (chance >= 201 && chance <= 15000){
				        player.sendPackets(new S_SystemMessage("\\fY½ÇÆÐ : [15000]¿ø Àû¸³  , ÃÑ : ["+this.getPpoint()+ "]¿ø Àû¸³"));
				    }
				    }

				if (player.getLevel() < 99) {//Çã¼ö¾Æºñ·¦Á¦
					FastTable<L1PcInstance> targetList = new FastTable<L1PcInstance>();
					targetList.add(player);
					FastTable<Integer> hateList = new FastTable<Integer>();
					hateList.add(1);
					CalcExp.calcExp(player, getId(), targetList, hateList,
							getExp());
				}
				/**¿î¿µÀÚÀÎ°æ¿ì */
				if (player.isGm()) {
				if (this.getNpcId() == 45001|| this.getNpcId() == 45002){
				int dmg = attack.calcDamage();//¹°¸®µ¥¹ÌÁöºÎºÐ
				player.sendPackets(new S_SystemMessage("\\fY¹°¸®µ¥¹ÌÁö:[" + dmg + "]ÀÔ´Ï´Ù."));
			}
				}
				int heading = getMoveState().getHeading();
				if (heading < 7)
					heading++;
				else
					heading = 0;
				getMoveState().setHeading(heading);
				Broadcaster.broadcastPacket(this, new S_ChangeHeading(this));
			}
			attack.action();
		}
	}

	@Override
	public void onTalkAction(L1PcInstance l1pcinstance) {
	}

	public void onFinalAction() {
	}

	public void doFinalAction() {
	}
}
