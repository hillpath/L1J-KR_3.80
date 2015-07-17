package l1j.server.server;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import l1j.server.Config;
import l1j.server.server.datatables.NpcTable;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1WarSpawn;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1EventTowerInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Npc;

public class MiniWarGame extends Thread {
	
		private static MiniWarGame _instance;
		
		public int winLine = 0;
		
		public boolean _BattleEntry = false;

		private boolean _BattleStart;
		public boolean getBattleStart() {
			return _BattleStart;
		}
		public void setBattleStart(boolean Battle) {
			_BattleStart = Battle;
		}
		
		/*
		 * 수호탑에 관련된부분1
		 */
		private  L1NpcInstance _tower = null;
		public void setTower(L1NpcInstance A){
			_tower = A;
		}
		public L1NpcInstance getTower(){
			return _tower;
		}
		private boolean _towerDead = false;
		
		public void setTowerDead(boolean A){
			_towerDead = A;
		}
		public boolean getTowerDead(){
			return _towerDead;
		}
		
		/*
		 * 수호탑에 관련된부분2
		 */
		private  L1NpcInstance _tower2 = null;
		public void setTower2(L1NpcInstance B){
			_tower = B;
		}
		public L1NpcInstance getTower2(){
			return _tower2;
		}
		private boolean _towerDead2 = false;
		
		public void setTowerDead2(boolean B){
			_towerDead = B;
		}
		public boolean getTowerDead2(){
			return _towerDead2;
		}
		
		private static long sTime = 0;
		
		private String NowTime = "";
		//시간 간격
		private static final int LOOP = Config.MINIWAR_LOOPTIME;

		private static final SimpleDateFormat s = new SimpleDateFormat("HH", Locale.KOREA);

		private static final SimpleDateFormat ss = new SimpleDateFormat("MM-dd HH:mm", Locale.KOREA);

		public static MiniWarGame getInstance() {
			if(_instance == null) {
				_instance = new MiniWarGame();
			}
			return _instance;
		}
		
		public final ArrayList<L1PcInstance> _Members = new ArrayList<L1PcInstance>();
		
		public void AddMember(L1PcInstance pc) {
			if (!_Members.contains(pc)) {
				_Members.add(pc);
				pc.sendPackets(new S_SystemMessage("\\fT입장신청 되었습니다. 잠시후 미니공성전이 시작됩니다."));
			}
		}
		
		public void removeMember(L1PcInstance pc) {
			_Members.remove(pc);
		}

		public void clearMembers() {
			_Members.clear();
		}

		public boolean isMember(L1PcInstance pc) {
			return _Members.contains(pc);
		}

		public L1PcInstance[] getMemberArray() {
			return _Members.toArray(new L1PcInstance[_Members.size()]);
		}

		public int getMemberCount() {
			return _Members.size();
		}
		
		
		public final ArrayList<L1PcInstance> _MembersLine1 = new ArrayList<L1PcInstance>();
		
		public void AddMemberLine1(L1PcInstance pc) {
			if (!_MembersLine1.contains(pc)) {
				_MembersLine1.add(pc);
			}
		}
		
		public void removeMemberLine1(L1PcInstance pc) {
			_MembersLine1.remove(pc);
		}

		public void clearMembersLine1() {
			_MembersLine1.clear();
		}

		public boolean isMemberLine1(L1PcInstance pc) {
			return _MembersLine1.contains(pc);
		}

		public L1PcInstance[] getMemberArrayLine1() {
			return _MembersLine1.toArray(new L1PcInstance[_MembersLine1.size()]);
		}

		public int getMemberCountLine1() {
			return _MembersLine1.size();
		}
		
		public final ArrayList<L1PcInstance> _MembersLineReal1 = new ArrayList<L1PcInstance>();
		
		public void AddMemberLineReal1(L1PcInstance pc) {
			if (!_MembersLineReal1.contains(pc)) {
				_MembersLineReal1.add(pc);
			}
		}
		
		public void removeMemberLineReal1(L1PcInstance pc) {
			_MembersLineReal1.remove(pc);
		}

		public void clearMembersLineReal1() {
			_MembersLineReal1.clear();
		}

		public boolean isMemberLineReal1(L1PcInstance pc) {
			return _MembersLineReal1.contains(pc);
		}

		public L1PcInstance[] getMemberArrayLineReal1() {
			return _MembersLineReal1.toArray(new L1PcInstance[_MembersLineReal1.size()]);
		}

		public int getMemberCountLineReal1() {
			return _MembersLineReal1.size();
		}
		
		public final ArrayList<L1PcInstance> _MembersLine2 = new ArrayList<L1PcInstance>();
		
		public void AddMemberLine2(L1PcInstance pc) {
			if (!_MembersLine2.contains(pc)) {
				_MembersLine2.add(pc);
			}
		}
		
		public void removeMemberLine2(L1PcInstance pc) {
			_MembersLine2.remove(pc);
		}

		public void clearMembersLine2() {
			_MembersLine2.clear();
		}

		public boolean isMemberLine2(L1PcInstance pc) {
			return _MembersLine2.contains(pc);
		}

		public L1PcInstance[] getMemberArrayLine2() {
			return _MembersLine2.toArray(new L1PcInstance[_MembersLine2.size()]);
		}

		public int getMemberCountLine2() {
			return _MembersLine2.size();
		}
		
		public final ArrayList<L1PcInstance> _MembersLineReal2 = new ArrayList<L1PcInstance>();
		
		public void AddMemberLineReal2(L1PcInstance pc) {
			if (!_MembersLineReal2.contains(pc)) {
				_MembersLineReal2.add(pc);
			}
		}
		
		public void removeMemberLineReal2(L1PcInstance pc) {
			_MembersLineReal2.remove(pc);
		}

		public void clearMembersLineReal2() {
			_MembersLineReal2.clear();
		}

		public boolean isMemberLineReal2(L1PcInstance pc) {
			return _MembersLineReal2.contains(pc);
		}

		public L1PcInstance[] getMemberArrayLineReal2() {
			return _MembersLineReal2.toArray(new L1PcInstance[_MembersLineReal2.size()]);
		}

		public int getMemberCountLineReal2() {
			return _MembersLineReal2.size();
		}

		
		@Override
			public void run() {
			System.out.println("MiniWarGame.getInstance ... 미니공성전 시작!");
			try	{
					while (true) {
						try{
						       Thread.sleep(1000); 
						   }catch(Exception e){
						   }
						/** 오픈 **/
						if(!isOpen())
							continue;
						if(L1World.getInstance().getAllPlayers().size() <= 0)
							continue;
						
						Thread.sleep(60000L);
						
						/** 오픈 메세지 **/
						L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "NEW미니공성이 열렸습니다."));
						L1World.getInstance().broadcastServerMessage("\\fWNEW미니공성에 서둘러 입장하세요!");
					
						/** NEW미니공성 시작**/
						setBattleStart(true);
						_BattleEntry = true;
						
						Thread.sleep(60000L);
						L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "NEW미니공성 입장 마감 3분전 입니다."));
						L1World.getInstance().broadcastServerMessage("\\fWNEW미니공성 입장 마감 3분전 입니다.");
					
						Thread.sleep(60000L);
						L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "NEW미니공성 입장 마감 2분전 입니다."));
						L1World.getInstance().broadcastServerMessage("\\fWNEW미니공성 입장 마감 2분전 입니다.");
					
						Thread.sleep(60000L);
						L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "NEW미니공성 입장 마감 1분전 입니다."));
						L1World.getInstance().broadcastServerMessage("\\fWNEW미니공성 입장 마감 1분전 입니다.");

						Thread.sleep(60000L);
						L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "NEW미니공성 입장 마감되었습니다."));
						L1World.getInstance().broadcastServerMessage("\\fWNEW미니공성 입장 마감되었습니다.");
					
						_BattleEntry = false;
						
						InBattle();
						
						L1WarSpawn warspawn = new L1WarSpawn();
						L1Npc l1npc = NpcTable.getInstance().getTemplate(6100008);	
						warspawn.SpawnWarObject(l1npc, Config.MINIWAR_ATEAM_X, Config.MINIWAR_ATEAM_Y, (short) (2006));
						
						L1WarSpawn warspawn2 = new L1WarSpawn();
						L1Npc l1npc2 = NpcTable.getInstance().getTemplate(6100009);	
						warspawn2.SpawnWarObject(l1npc2, Config.MINIWAR_BTEAM_X, Config.MINIWAR_BTEAM_Y, (short) (2006));

						Thread.sleep(3600000L); //1시간

						TelePort();
						
						/** 종료 **/
						End(); 
	
					}

				} catch(Exception e){
					e.printStackTrace();
				}
			}

			/**
			 *오픈 시각을 가져온다
			 *
			 *@return (Strind) 오픈 시각(MM-dd HH:mm)
			 */
			 public String OpenTime() {
				 Calendar c = Calendar.getInstance();
				 c.setTimeInMillis(sTime);
				 return ss.format(c.getTime());
			 }

			 /**
			 *미니공성이 열려있는지 확인
			 *
			 *@return (boolean) 열려있다면 true 닫혀있다면 false
			 */
			 private boolean isOpen() {
				 NowTime = getTime();
				 if((Integer.parseInt(NowTime) % LOOP) == 0) return true;
				 return false;
			 }
			 /**
			 *실제 현재시각을 가져온다
			 *
			 *@return (String) 현재 시각(HH:mm)
			 */
			 private String getTime() {
				 return s.format(Calendar.getInstance().getTime());
			 }
			 
			 /**이벤트에서 마을로 소환**/
			 public void TelePort() {
				 for(L1PcInstance c : L1World.getInstance().getAllPlayers()) {
					 switch(c.getMap().getId()) {
						 case 2006:
						 c.stopHpRegenerationByDoll();
						 c.stopMpRegenerationByDoll();
						 L1Teleport.teleport(c, 33970, 33246, (short) 4, 4, true);
						 c.set_MiniDuelLine(0);
						 c.sendPackets(new S_SystemMessage("항상 노력하는 "+ Config.SERVER_NAME +" 서버입니다! 수고 하셨습니다."));
						 break;
						 default:
						 break;
					 }
				 }
			 }
			 

			 /** 종료 **/
			 public void End() {
				 L1World.getInstance().broadcastServerMessage("\\fW[ NEW미니공성은 "+LOOP+ "시간 간격으로 시작됩니다 ]");
				 clearMembers();
				 clearMembersLine1();
				 clearMembersLineReal1();
				 clearMembersLine2();
				 clearMembersLineReal2();
				 setBattleStart(false);
				 TelePort();
				 /**타워 리셋 **/
				 setTowerDead(false);
				 setTowerDead2(false);
				 DeletedMon1();
				 DeletedMon2();
				 DeletedMon3();
				 winLine = 0;
				 System.out.println("MiniWarGame.getInstance ... 미니공성전 정상 종료!");
			 }
			 
			 private void DeletedMon1() {		
					for (Object obj : L1World.getInstance().getVisibleObjects(2006).values()) {			
						if (obj instanceof L1EventTowerInstance){			
							L1EventTowerInstance tower = (L1EventTowerInstance) obj;				
							if(tower.getNpcTemplate().get_npcId() == 6100008){					
								tower.deleteMe();	
								}
							}
						}
					}
			 
			 private void DeletedMon2() {		
					for (Object obj : L1World.getInstance().getVisibleObjects(2006).values()) {			
						if (obj instanceof L1EventTowerInstance){			
							L1EventTowerInstance tower = (L1EventTowerInstance) obj;				
							if(tower.getNpcTemplate().get_npcId() == 6100009){					
								tower.deleteMe();	
								}
							}
						}
					}
			 
			 private void DeletedMon3() {		
					for (Object obj : L1World.getInstance().getVisibleObjects(2006).values()) {			
						if (obj instanceof L1EventTowerInstance){			
							L1EventTowerInstance tower = (L1EventTowerInstance) obj;				
							if(tower.getNpcTemplate().get_npcId() == 6100002){					
								tower.deleteMe();	
								}
							}
						}
					}
			 
			 private void InBattle(){
				 if(_Members.size() <= 1){//수정월래9
					 L1World.getInstance().broadcastServerMessage("\\fW[ 미니공성 인원은 최소 10명이상이여야 시작됩니다 ]");
					 End();
				 }
				 for (int i = 0; i < _Members.size(); i++) {
				  L1PcInstance pc = _Members.get(i);//리스트에서 사람을 가져온다.
				  if(pc != null){
				     if(i % 2 == 0){//짝수일 경우
				      pc.set_MiniDuelLine(1);
				      AddMemberLine1(pc);
				      AddMemberLineReal1(pc);
				      L1Teleport.teleport(pc, Config.MINIWAR_ATEAM_X, Config.MINIWAR_BTEAM_Y, (short) 2006, 1, true);
				      pc.sendPackets(new S_SystemMessage("\\fT미니공성에 [A] 라인으로 입장하셨습니다."));
				      createHp(pc);
				      pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "상대방의 수호탑을 먼저 부셔버리는팀이 우승합니다."));
				     }else{//홀수일경우
				      pc.set_MiniDuelLine(2);
				      AddMemberLine2(pc);
				      AddMemberLineReal2(pc);
				      L1Teleport.teleport(pc, Config.MINIWAR_BTEAM_X, Config.MINIWAR_BTEAM_Y, (short) 2006, 5, true);
				      pc.sendPackets(new S_SystemMessage("\\fT미니공성에 [B] 라인으로 입장하셨습니다."));
				      createHp(pc);
				      pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "상대방의 수호탑을 먼저 부셔버리는팀이 우승합니다."));
				     }
				    }else{
				    	removeMember(pc);
				    }
				 }
				}

			 // 미니 HP바를 생성한다.
			 private void createHp(L1PcInstance pc) {
				 for (L1PcInstance member : getMemberArray()) {
					 if(member != null){
							 if(isMemberLine1(pc) == isMemberLine1(member)){//같은 라인의 멤버에게 HP바를 전송
								 member.sendPackets(new S_HPMeter(pc.getId(), 100 * pc.getCurrentHp() / pc.getMaxHp()));
								 pc.sendPackets(new S_HPMeter(member.getId(), 100 * member.getCurrentHp() / member.getMaxHp()));
							 }else if(isMemberLine2(pc) == isMemberLine2(member)){//같은 라인의 멤버에게 HP바를 전송
								 member.sendPackets(new S_HPMeter(pc.getId(), 100 * pc.getCurrentHp() / pc.getMaxHp()));
								 pc.sendPackets(new S_HPMeter(member.getId(), 100 * member.getCurrentHp() / member.getMaxHp()));
						}
					 }
				 }
			 }
			 //HP바를 삭제한다.
			 public void deleteMiniHp(L1PcInstance pc) {
				 for (L1PcInstance member : getMemberArray()) {
					 if(member != null){
							 if(isMemberLineReal1(pc) == isMemberLineReal1(member)){
								 pc.sendPackets(new S_HPMeter(member.getId(), 0xff));
								 member.sendPackets(new S_HPMeter(pc.getId(), 0xff));
							 }else if(isMemberLineReal2(pc) == isMemberLineReal2(member)){
								 pc.sendPackets(new S_HPMeter(member.getId(), 0xff));
								 member.sendPackets(new S_HPMeter(pc.getId(), 0xff));
						  }
					 }
				 }
			 }
			 
		}
