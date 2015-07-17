/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.serverpackets.S_HPMeter;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_SystemMessage;


public class BattleZone implements Runnable {
	private static Logger _log = Logger.getLogger(BattleZone.class.getName());
	protected final Random _random = new Random();

	private static BattleZone _instance;

	int 정각2 = 3*3600;
	int 정각5 = 6*3600;
	int 정각8 = 9*3600;
	int 정각11 = 12*3600;
	int 정각14 = 15*3600;
	int 정각17 = 18*3600;
	int 정각20 = 21*3600;
	int 정각23 = 24*3600;

	//듀얼 시작여부
	private boolean _DuelStart;
	public boolean getDuelStart() {
		return _DuelStart;
	}

	public void setDuelStart(boolean duel) {
		_DuelStart = duel;
	}
	//듀얼 입장여부
	private boolean _DuelOpen;
	public boolean getDuelOpen() {
		return _DuelOpen;
	}

	public void setDuelOpen(boolean duel) {
		_DuelOpen = duel;
	}
	//팀1 입장여부 //입장 인원 맞추기 sunny
	private boolean _Team1Open;
	public boolean getTeam1Open() {
		return _Team1Open;
	}

	public void setTeam1Open(boolean Team1) {
		_Team1Open = Team1;
	}
	//팀2 입장여부 //입장 인원 맞추기 sunny
	private boolean _Team2Open;
	public boolean getTeam2Open() {
		return _Team2Open;
	}

	public void setTeam2Open(boolean Team2) {
		_Team2Open = Team2;
	}
	//듀얼 시작여부
	private boolean _진행;

	public boolean 배틀존진행() {
		return _진행;
	}

	public void set배틀존진행(boolean flag) {
		_진행 = flag;
	}

	private boolean _종료;
	public boolean 배틀존종료() {
		return _종료;
	}

	public void set배틀존종료(boolean flag) {
		_종료 = flag;
	}
	public int DuelCount;
	
	public int MemberCount1;
	
	public int MemberCount2;

	private int enddueltime;

	private boolean Close;
	protected ArrayList<L1PcInstance> 라인1 = new ArrayList<L1PcInstance>();
	public void add라인1(L1PcInstance pc) 	{ 라인1.add(pc); }
	public void remove라인1(L1PcInstance pc) 	{ 
		deleteMiniHp(pc);
		pc.set_DuelLine(0);
		MemberCount1 -= 1;
		라인1.remove(pc); 
		BattleZone.getInstance().DuelCount -= 1;
		}
	public void clear라인1() 					{ 라인1.clear();	  }
	public int get라인1Count() 				{ return 라인1.size();	}
	
	protected ArrayList<L1PcInstance> 라인2 = new ArrayList<L1PcInstance>();
	public void add라인2(L1PcInstance pc) 	{ 라인2.add(pc); }
	public void remove라인2(L1PcInstance pc) 	{ 
		deleteMiniHp(pc);
		pc.set_DuelLine(0);
		MemberCount2 -= 1;
		라인2.remove(pc); 
		BattleZone.getInstance().DuelCount -= 1;
		}
	public void clear라인2() 					{ 라인2.clear();	  }
	public int get라인2Count() 				{ return 라인2.size();	}
	
	protected ArrayList<L1PcInstance> 배틀존유저 = new ArrayList<L1PcInstance>();
	public void add배틀존유저(L1PcInstance pc) 	{ 배틀존유저.add(pc); }
	public void remove배틀존유저(L1PcInstance pc) 	{ 
		deleteMiniHp(pc);
		pc.set_DuelLine(0);
		배틀존유저.remove(pc);
		}
	public void clear배틀존유저() 					{ 배틀존유저.clear();	  }
	public boolean is배틀존유저(L1PcInstance pc) 	{ return 배틀존유저.contains(pc); 	}
	public int get배틀존유저Count() 				{ return 배틀존유저.size();	}

	private boolean GmStart = false;
	public void setGmStart(boolean ck){	GmStart = ck; }
	public boolean getGmStart(){	return GmStart;	}
	
public L1PcInstance[] toArray배틀존유저() {
		return 배틀존유저.toArray(new L1PcInstance[배틀존유저.size()]);
	}
	public static BattleZone getInstance() {
		if (_instance == null) {
			_instance = new BattleZone();
		}
		return _instance;
	}


	@Override
	public void run() {
		System.out.println(BattleZone.class.getName()  + " 배틀존쓰레드시작");
		try {
			while (true) {
				if(배틀존종료()==true){
					Thread.sleep(1000*60*60*2); //2시간/1000=1초/게임종료후 쓰레드 죽이는곳 신경안써도됨
					set배틀존종료(false);
				}else
				checkDuelTime(); // 듀얼 가능시간을 체크
				Thread.sleep(1000*10);
				유저체크();
			}
		} catch (Exception e1) {
		}
	}

	private void 유저체크() {

		L1PcInstance[] pc = toArray배틀존유저();
		for (int i = 0; i < pc.length; i++) {
			if(pc[i].getMapId() == 5001 || pc[i].getMapId() == 5153 || !pc[i].isDead()){
                            return;
                        }else if (pc[i].getMapId() != 5001 || pc[i].getMapId() != 5153 || pc[i].isDead()){
				remove배틀존유저(pc[i]);
				remove라인1(pc[i]);
				remove라인2(pc[i]);
				pc[i].set_DuelLine(0);
			}
			}
	}

	//듀얼시간체크
	private void checkDuelTime() {
		//게임시간을 받아온다.
		int servertime = RealTimeClock.getInstance().getRealTime().getSeconds();
		//현제시간
		int nowdueltime =servertime % 86400;
		int count1 = 0;
		int count2 = 0;
		int winLine = 0;
		//입장마감시간  설정
		 if (getDuelStart() == false ){
			//시작시간지정
				//시작시간지정
				//시작시간지정
			  if (  	    nowdueltime >= 정각2-31 && nowdueltime <= 정각2+31 // /2시
					     || nowdueltime >= 정각5-31 && nowdueltime <= 정각5+31 ///5시
					     || nowdueltime >= 정각8-31 && nowdueltime <= 정각8+31 ////8시
					     || nowdueltime >= 정각11-31 && nowdueltime <= 정각11+31 //11시
					     || nowdueltime >= 정각14-31 && nowdueltime <= 정각14+31//14시
					     || nowdueltime >= 정각17-31 && nowdueltime <= 정각17+31//17시
					     || nowdueltime >= 정각20-31 && nowdueltime <= 정각20+31//20시
					     || nowdueltime >= 정각23-31 && nowdueltime <= 정각23+31//23시
					     || getGmStart())
			{ //12시
				setDuelOpen(true);
				setDuelStart(true);
				입장3분대기();
			}
			if (배틀존진행() == true)	{
				L1PcInstance[] c = toArray배틀존유저();
				for (int i = 0; i < c.length; i++) {
						if(c[i].getMapId() == 5001){ 
							if(!c[i].isDead()){
								try{
								배틀존입장1(c[i]);
								배틀존입장2(c[i]);
								set배틀존진행(false);
								} catch (Exception e1) {
								}
							}
						}
				}
					setDuelStart(true);
					//끝나는 시간지정
					enddueltime = nowdueltime + 600; //20분후종료종료시간 정하는곳
			}
		}else{
			//종료시간이거나 강제종료라면
			if(nowdueltime >= enddueltime || Close == true){
				L1PcInstance[] c1 = toArray배틀존유저();
				for (int i = 0; i < c1.length; i++) {
						if(c1[i].getMapId() == 5153){
							if(!c1[i].isDead()){
								if(c1[i].get_DuelLine() == 1){
									count1 += 1;
								}else{
									count2 += 1;
								}
							}
						}
				}
				//우승체크
				if(count1 > count2){
					//1번라인 우승 지하아지트그건알죠
					winLine = 1;
					L1World.getInstance().broadcastServerMessage("\\fW* 배틀존 종료! 다크 라인 승리 *");
				}else if(count1 < count2){
					//2번라인 우승
					winLine = 2;
					L1World.getInstance().broadcastServerMessage("\\fW* 배틀존 종료! 아크 라인 승리 *");
				}else{
					winLine = 3;
					L1World.getInstance().broadcastServerMessage("\\fW* 배틀존 종료! 1번 라인과 2번라인이 동점입니다 *");
				}
				L1PcInstance[] c2 = toArray배틀존유저();
				for (int i = 0; i < c2.length; i++) {
					//이긴 라인에게 아이템지급
				     if(c2[i].get_DuelLine() == winLine){
				      c2[i].getInventory().storeItem(41159, 3000);
				      c2[i].getInventory().storeItem(40308, 30000000);
				      c2[i].getInventory().storeItem(127000, 5);
				      c2[i].sendPackets(new S_SystemMessage("\\fU* 승자 - 승리팀에게 깃털 3000개씩 지급되었습니다 *"));
				      c2[i].sendPackets(new S_SystemMessage("\\fU* 승자 - 승리팀에게 아덴 3000만 지급되었습니다 *"));
				      c2[i].sendPackets(new S_SystemMessage("\\fU* 승자 - 승리팀에게 아이돌의물약(5) 지급되었습니다 *"));

				     }
					//배틀존이라면
					if(c2[i].getMapId() == 5153){
						if(!c2[i].isDead()){
							c2[i].set_DuelLine(0);
							L1Teleport.teleport(c2[i], 33090, 33402, (short) 4, 0, true);// 마을 로 텔 되는 자표
						remove배틀존유저(c2[i]);    }
					}else if(c2[i].get_DuelLine() != 0){
						c2[i].set_DuelLine(0);
					}
					deleteMiniHp(c2[i]);
				}

				L1World.getInstance().broadcastServerMessage("\\fW* 배틀존은 3시간 간격으로 열립니다 *");
				배틀존유저.clear();
				라인1.clear();
				라인2.clear();
				DuelCount = 0;
				MemberCount1 = 0; //인원수 측정 sunny //입장 인원 맞추기 sunny
				MemberCount2 = 0; //인원수 측정 sunny //입장 인원 맞추기 sunny
				set배틀존종료(true);
				set배틀존진행(false);
				setDuelStart(false);
				Close = false;
				setGmStart(false);

			}else{
				
				//입장이 마감되었다면
				if(!getDuelOpen()){
					int count3 = 0;
					int count4 = 0;
					L1PcInstance[] c3 = toArray배틀존유저();
					for (int i = 0; i < c3.length; i++) {
						//배틀존이라면
						if(c3[i].getMapId() == 5153){
							if(!c3[i].isDead()){//죽지않은 유저 체크
								if(c3[i].get_DuelLine() == 1){
									count3 += 1;
								}else{
									count4 += 1;
								}
							}
						}
					}

					//1팀이든 2팀이든 남은유저가 0명일때 강제종료실행<<
					if(count3 == 0 || count4 == 0){
						Close = true;
					}
				}

			}
		}
		
	}
	private void 배틀존입장1(L1PcInstance 배틀피시){
		try{
			if (배틀피시.get_DuelLine()==1){
				int ranx  = 32628 + _random.nextInt(4);
	    		int rany  = 32896 + _random.nextInt(5);
	    		L1Teleport.teleport(배틀피시, ranx, rany, (short) 5153, 1, true);
	            createMiniHp(배틀피시);
			}	
			} catch (Exception e1) {
		}
	}
	private void 배틀존입장2(L1PcInstance 배틀피시){
		try{
			if (배틀피시.get_DuelLine()==2){
				int ranx2  = 32650 - _random.nextInt(4);
	    		int rany2  = 32893 + _random.nextInt(5);
	    		L1Teleport.teleport(배틀피시, ranx2, rany2, (short) 5153, 5, true);
	            createMiniHp(배틀피시);
			}	
			} catch (Exception e1) {
		}
	}
	private void 입장3분대기(){
		L1World.getInstance().broadcastPacketToAll(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "현재 배틀존 대기실로 입장이 가능합니다."));
		L1World.getInstance().broadcastServerMessage("\\fW* 잠시후 배틀존이 열립니다. 대기실로 입장해주세요 *");
		L1World.getInstance().broadcastServerMessage("\\fW* 배틀존에 참여하여 진정한 배틀의 재미를 느끼세요 *");;
		L1World.getInstance().broadcastServerMessage("\\fW* 배틀존 입장전 버프를 하시고 입장하시기 바랍니다 *");;
		setTeam1Open(true); //입장 인원 맞추기 sunny
		setTeam2Open(true); //입장 인원 맞추기 sunny
		try{
			Thread.sleep(1000*120);
		}catch(Exception e){
		}
		L1World.getInstance().broadcastServerMessage("\\fW* 곧 배틀존이 열립니다. 서둘러 입장해주세요 *");
		try{
			Thread.sleep(1000*50);
		}catch(Exception e){
		}
		L1World.getInstance().broadcastServerMessage("\\fW* 10초 후 배틀존 입장이 마감됩니다 * ");
		
		try{
			Thread.sleep(1000*10);
		}catch(Exception e){
		}

		
		if(MemberCount1 != 30){
			L1World.getInstance().broadcastServerMessage("\\fW* 배틀존 인원이 맞지 않아 다크라인입장이 30초 연장되었습니다 *");
			setTeam1Open(true);
			setTeam2Open(false);
			try{
				Thread.sleep(1000*30);
			}catch(Exception e){
			}
		}
		if(MemberCount2 != 30){
			L1World.getInstance().broadcastServerMessage("\\fW* 배틀존 인원이 맞지 않아 아크라인입장이 30초 연장되었습니다 *");
			setTeam1Open(false);
			setTeam2Open(true);
			try{
				Thread.sleep(1000*30);
			}catch(Exception e){
			}
		}
		if(getDuelOpen()){
				setDuelOpen(false);
		}
		setTeam1Open(false);
		setTeam2Open(false);
		L1World.getInstance().broadcastServerMessage("\\fW* 배틀존 입장이 마감되었습니다 *");
		L1World.getInstance().broadcastServerMessage("\\fW* 5초후 배틀이 시작됩니다. 준비하세요 *");
		try{
			Thread.sleep(1000*5);
		}catch(Exception e){
		}
		set배틀존진행(true);
	}
	
 private void createMiniHp(L1PcInstance pc) {
  // 배틀시, 서로 HP를 표시시킨다
  List<L1PcInstance> li = null;
	li = pc.getNearObjects().getKnownPlayers();
	for( int i = 0 ; i < li.size() ; i++){
		L1PcInstance member = li.get(i);
 // for (L1PcInstance member :pc.getNearObjects().getKnownPlayers()){
   //같은라인에게 hp표시
   if(member != null){
    if(pc.get_DuelLine() == member.get_DuelLine()){
     member.sendPackets(new S_HPMeter(pc.getId(), 100
      * pc.getCurrentHp() / pc.getMaxHp()));
     pc.sendPackets(new S_HPMeter(member.getId(), 100
      * member.getCurrentHp() / member.getMaxHp()));
    }
   }
  }


 }
	private void deleteMiniHp(L1PcInstance pc) {
		// 배틀종료시, HP바를 삭제한다.
  List<L1PcInstance> li = null;
	li = pc.getNearObjects().getKnownPlayers();
	for( int i = 0 ; i < li.size() ; i++){
		L1PcInstance member = li.get(i);
 // for (L1PcInstance member :pc.getNearObjects().getKnownPlayers()){
			//같은라인에게 hp표시
			if(member != null){
				if(pc.get_DuelLine() == member.get_DuelLine()){
					pc.sendPackets(new S_HPMeter(member.getId(), 0xff));
					member.sendPackets(new S_HPMeter(pc.getId(), 0xff));
				}
			}
		}
	}

	 private void Clearline() {
		 for(L1PcInstance c : L1World.getInstance().getAllPlayers()) {
			 switch(c.get_DuelLine()) {
				 case 1:
					 c.set_DuelLine(0);
					 c.sendPackets(new S_SystemMessage("\\fW[ 배틀존 라인이 리셋 처리 되었습니다 ]"));
				 case 2:
					 c.set_DuelLine(0);
					 c.sendPackets(new S_SystemMessage("\\fW[ 배틀존 라인이 리셋 처리 되었습니다 ]"));
				 break;
				 default:
				 break;
			 }
		 }
	 }
}
