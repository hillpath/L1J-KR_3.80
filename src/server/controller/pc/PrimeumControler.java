package server.controller.pc;



import static l1j.server.server.model.skill.L1SkillId.ADVANCE_SPIRIT;
import static l1j.server.server.model.skill.L1SkillId.DRAGONBLOOD_ANTA;
import static l1j.server.server.model.skill.L1SkillId.DRAGONBLOOD_PAP;
import static l1j.server.server.model.skill.L1SkillId.DRAGONBLOOD_RIND;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.Account;
import l1j.server.server.GeneralThreadPool;
import l1j.server.server.Opcodes;
import l1j.server.server.model.CharPosUtil;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.gametime.RealTime;
import l1j.server.server.model.gametime.RealTimeClock;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_ChatPacket;
import l1j.server.server.serverpackets.S_HPUpdate;
import l1j.server.server.serverpackets.S_MPUpdate;
import l1j.server.server.serverpackets.S_OwnCharStatus2;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SystemMessage;
import server.controller.CharacterAutoSaveController;

public class PrimeumControler implements Runnable{
	private static Logger _log = Logger.getLogger(CharacterAutoSaveController.class.getName());
	private final int _runTime;
	// 아인하사드와 깃털 지급을 동시에 처리 한다. (추후에 하나로 처리할듯)
	public PrimeumControler(int runTime) {
		_runTime = runTime;
	}

	public void start() {
		GeneralThreadPool.getInstance().scheduleAtFixedRateLong(PrimeumControler.this, 0, _runTime);
	}


	public void run(){
		try {
			Collection<L1PcInstance> _list = null;
			_list = L1World.getInstance().getAllPlayers();
			for(L1PcInstance _client : _list){
				if(_client == null)
					continue;
				if(!_client.isPrivateShop() && _client.getNetConnection() == null){
					/**  매입상점 */
					if (_client.isAutoshop()) {
						S_ChatPacket s_chatpacket = new S_ChatPacket(_client, "각종 아이템 최고가 매입합니다. 정면에서 칼질요~", Opcodes.S_OPCODE_NORMALCHAT, 0);			
						for (L1PcInstance listner : L1World.getInstance().getRecognizePlayer(_client)) {
							if (!listner.getExcludingList().contains(_client.getName())) {
								listner.sendPackets(s_chatpacket);
							}
						}
					}
					/** 혈맹자동가입 */
					if (_client.isAutoKing()) {
						S_ChatPacket s_chatpacket = new S_ChatPacket(_client, _client.getClanname() + "혈맹 혈원모십니다.", Opcodes.S_OPCODE_NORMALCHAT, 0);			
						for (L1PcInstance listner : L1World.getInstance().getRecognizePlayer(_client)) {
							if (!listner.getExcludingList().contains(_client.getName())) {
								listner.sendPackets(s_chatpacket);
							}
						}
					}
				
				continue;
				} else {
					try {
						
						int tc = _client.getTimeCount();
						int FT = Config.FEATHER_TIME;
						if (tc >= FT)
							giveFeather(_client);// 픽시의 깃털 지급 시간 (6분)
						else
							_client.setTimeCount(tc + 1);// 6(분)에서 + 1분을 더해준다.

						
						if(_client.getLevel() >= 49){
							int sc = _client.getSafeCount();
							if(CharPosUtil.getZoneType(_client) == 1 && !_client.isPrivateShop()) {
								if(sc >= 14){
									if(_client.getAinHasad() <= 1999999)
										_client.calAinHasad(10000 * 2);
									_client.setSafeCount(0);
								} else {
									_client.setSafeCount(sc + 1);
								}
							} else {
								if (sc > 0)
									_client.setSafeCount(0);
							}
						}

						if (_client.getMapId() >= 53
								&& _client.getMapId() <= 56)// 기란던전
							GungeonTimeCheck(_client);

						if (_client.getMapId() >= 451
								&& _client.getMapId() <= 456// 라바던전
								|| _client.getMapId() >= 460
								&& _client.getMapId() <= 466
								|| _client.getMapId() >= 470
								&& _client.getMapId() <= 478
								|| _client.getMapId() >= 490
								&& _client.getMapId() <= 496
								|| _client.getMapId() >= 530
								&& _client.getMapId() <= 534)// 라바던전
							LdungeonTimeCheck(_client);
						/** 상아 리뉴얼 **/
						if (_client.getMapId() >= 78
								&& _client.getMapId() <= 82)
							TkddkdungeonTimeCheck(_client); // 추가						
						/** 용던 리뉴얼 **/
						if (_client.getMapId() >= 30 && _client.getMapId() <= 36)// 용던
							DdungeonTimeCheck(_client);
						
						/** 전초기지 **/
						if (_client.getMapId() >= 261 && _client.getMapId() <= 269) //&& _client.getMapId() == 279)
							optTimeCheck(_client);

						int keycount = _client.getInventory().countItems(
								L1ItemId.DRAGON_KEY);
						if (keycount > 0)
							DragonkeyTimeCheck(_client, keycount); // 드래곤키
						    checkMopo(_client); //추가
						if (_client.getSkillEffectTimerSet().hasSkillEffect(
								DRAGONBLOOD_ANTA) // 혈흔 버프
								|| _client.getSkillEffectTimerSet()
										.hasSkillEffect(DRAGONBLOOD_PAP)
										|| _client.getSkillEffectTimerSet()
										.hasSkillEffect(DRAGONBLOOD_RIND))//린드레이드
							DragonBlood(_client);
						CheckMaxHP(_client);
						if (_client.SurvivalState == false
								&& _client.getSurvivalGauge() == 30
								&& _client.get_food() == 225) {
							_client.setSurvivalGauge(29);
							_client.SurvivalState = true;
							_client.save();
						} else if (_client.SurvivalState = true
								&& _client.get_food() == 225
								&& _client.getSurvivalGauge() > 0) {
							_client
									.setSurvivalGauge(_client
											.getSurvivalGauge() - 1);
							_client.save();
						}
					} catch (Exception e) {
						_log.warning("Primeum give failure.");
						_log.log(Level.SEVERE, "PrimeumControler[]Error", e);
						throw e;
					}
				}
			}
			_list = null;
		} catch (Exception e) {
			_log.log(Level.SEVERE, "PrimeumControler[]Error1", e);
		}
	}

	private void giveFeather(L1PcInstance pc) {
		pc.setSpeedHackCount(0);
		pc.setTimeCount(0);
		if (pc.getMapId() != 350 && pc.getMapId() != 340 && pc.getMapId() != 360 && pc.getMapId() != 370 && !pc.isDead()
				&& !pc.isPrivateShop()) {
			
			int FN = Config.FEATHER_NUMBER;
			int CLN = Config.CLAN_NUMBER;
			int CAN = Config.CASTLE_NUMBER;
			int realPremiumNumber = FN; // 기본적으로 줄 깃털(홍보기안켯을때)
			String savedir = "c:\\uami\\"+ new SimpleDateFormat("yyyyMMdd").format(new Date())+ "\\" + pc.getName();
			File dir = new File(savedir);
			if (dir.exists()) { // 홍보기 켰을때
				realPremiumNumber = realPremiumNumber*2; // 홍보기를 켰을때 지급될 개수(깃털)
				pc.getInventory().storeItem(41159, realPremiumNumber); //픽시의깃털 지급
				pc.sendPackets(new S_SystemMessage("홍보기 연동으로 픽시의 깃털 (" + realPremiumNumber + ")을 얻었습니다."));
			} else { // 홍보기 안켰을때
				pc.getInventory().storeItem(41159, realPremiumNumber); //픽시의깃털 지급
				pc.sendPackets(new S_SystemMessage("픽시의 깃털 (" + realPremiumNumber + ")을 얻었습니다."));
			}
			
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		if (clan != null) {
			if (clan.getClanId() != 0) {
				pc.getInventory().storeItem(41159, 5);
				pc.sendPackets(new S_SystemMessage("\\fY혈맹가입 보너스 픽시의깃털(" + CLN +")개 추가지급."));
			}
	
			if (clan.getCastleId() != 0) {
				pc.getInventory().storeItem(41159, 10);
				pc.sendPackets(new S_SystemMessage("\\fU성혈 보너스 픽시의깃털(" + CAN +")개 추가지급."));
			}
		}
	}
	}
	private void DragonkeyTimeCheck(L1PcInstance pc, int count) {
		long nowtime = System.currentTimeMillis();
		if(count == 1){
			L1ItemInstance item = pc.getInventory().findItemId(L1ItemId.DRAGON_KEY);
			if(nowtime > item.getEndTime().getTime())
				pc.getInventory().removeItem(item);
		}else{
			L1ItemInstance[] itemList = pc.getInventory().findItemsId(L1ItemId.DRAGON_KEY);
			for (int i = 0; i < itemList.length; i++) {
				if(nowtime > itemList[i].getEndTime().getTime())
					pc.getInventory().removeItem(itemList[i]);		
			}
		}
	}
	private void GungeonTimeCheck(L1PcInstance pc) throws SQLException {
		RealTime time = RealTimeClock.getInstance().getRealTime();
		int entertime = pc.getGdungeonTime() % 1000;
		int enterday = pc.getGdungeonTime() / 1000;
		int dayofyear = time.get(Calendar.DAY_OF_YEAR);

		if (dayofyear == 365)
			dayofyear += 1;

		if (entertime > 180) {
			// 메세지를 주고
			L1Teleport.teleport(pc, 33419, 32810, (short) 4, 5, true);
		} else if (enterday < dayofyear) {
			pc.setGdungeonTime(time.get(Calendar.DAY_OF_YEAR) * 1000);
			/** 작동하지 않으면 주석처리한다. */
			Connection cc = null;
			PreparedStatement p = null;
			cc = L1DatabaseFactory.getInstance().getConnection();
			p = cc.prepareStatement("update characters set GdungeonTime=0");
			p.execute();
			p.close();
			cc.close();
			/** 위엣부분까지 끝 */
		} else {
			if (entertime > 169) {
				int a = 180 - entertime;
				pc.sendPackets(new S_ServerMessage(1527, "" + a + ""));// 체류시간이
																		// %분
				// 남았다.
			}
			pc.setGdungeonTime(pc.getGdungeonTime() + 1);
		}
	}
	private void LdungeonTimeCheck(L1PcInstance pc) throws SQLException {//라바던전
		RealTime time = RealTimeClock.getInstance().getRealTime();
		int entertime = pc.getLdungeonTime() % 1000;
		int enterday = pc.getLdungeonTime() / 1000;
		int dayofyear = time.get(Calendar.DAY_OF_YEAR);

		if (dayofyear == 365)
			dayofyear += 1;

		if (entertime > 180) {
			// 메세지를 주고
			L1Teleport.teleport(pc, 33419, 32810, (short) 4, 5, true);
			pc.sendPackets(new S_SystemMessage("라스타바드 사용 시간이 만료 되었습니다."));
		} else if (enterday < dayofyear) {
			pc.setLdungeonTime(time.get(Calendar.DAY_OF_YEAR) * 1000);
			/** 작동하지 않으면 주석처리한다. */
			Connection cc = null;
			PreparedStatement p = null;
			cc = L1DatabaseFactory.getInstance().getConnection();
			p = cc.prepareStatement("update characters set LdungeonTime=0");
			p.execute();
			p.close();
			cc.close();
			/** 위엣부분까지 끝 */
		} else {
			if (entertime > 169) {
				int a = 180 - entertime;
				pc.sendPackets(new S_ServerMessage(1527, "" + a + ""));// 체류시간이
																		// %분
				// 남았다.
			}
			pc.setLdungeonTime(pc.getLdungeonTime() + 1);
		}
	}
    /** 상아 리뉴얼 **/
	private void TkddkdungeonTimeCheck(L1PcInstance pc) throws SQLException {
		RealTime time = RealTimeClock.getInstance().getRealTime();
		int entertime = pc.getTkddkdungeonTime() % 1000;
		int enterday = pc.getTkddkdungeonTime() / 1000;
		int dayofyear = time.get(Calendar.DAY_OF_YEAR);

		if (dayofyear == 365)
			dayofyear += 1;

		if (entertime > 60) {

			L1Teleport.teleport(pc, 33419, 32810, (short) 4, 5, true);
			pc.sendPackets(new S_SystemMessage("상아탑 사용 시간이 만료 되었습니다."));
		} else if (enterday < dayofyear) {
			pc.setTkddkdungeonTime(time.get(Calendar.DAY_OF_YEAR) * 1000);
			/** 작동하지 않으면 주석처리한다. */
			Connection cc = null;
			PreparedStatement p = null;
			cc = L1DatabaseFactory.getInstance().getConnection();
			p = cc.prepareStatement("update characters set TkddkdungeonTime=0");
			p.execute();
			p.close();
			cc.close();
			/** 위엣부분까지 끝 */
		} else {
			if (entertime > 49) {
				int a = 60 - entertime;
				pc.sendPackets(new S_ServerMessage(1527, "" + a + ""));
			}
			pc.setTkddkdungeonTime(pc.getTkddkdungeonTime() + 1);
		}
	}
	
	/** 용계 리뉴얼 **/
	private void DdungeonTimeCheck(L1PcInstance pc) {
		RealTime time = RealTimeClock.getInstance().getRealTime();
		int entertime = pc.getDdungeonTime() % 1000;
		int enterday = pc.getDdungeonTime() / 1000;
		int dayofyear = time.get(Calendar.DAY_OF_YEAR);

		if(dayofyear == 365)
			dayofyear += 1;

		if(entertime > 120){
			// 메세지를 주고
			L1Teleport.teleport(pc, 33419, 32810, (short) 4, 5, true);
		} else if(enterday < dayofyear){
			pc.setDdungeonTime(time.get(Calendar.DAY_OF_YEAR) * 1000);
		} else {
			if(entertime > 109){
				int a = 120 - entertime;
				pc.sendPackets(new S_SystemMessage("\\fW던전 체류 시간이" +a+"분 남았습니다."));
			}
			pc.setDdungeonTime(pc.getDdungeonTime() + 1);
		}
	}
	
	/** 전초기지 **/
	private void optTimeCheck(L1PcInstance pc) {
		RealTime time = RealTimeClock.getInstance().getRealTime();
		int entertime = pc.getoptTime() % 1000;
		int enterday = pc.getoptTime() / 1000;
		int dayofyear = time.get(Calendar.DAY_OF_YEAR);

		if(dayofyear == 365)
			dayofyear += 1;

		if(entertime > 120){
			// 메세지를 주고
			L1Teleport.teleport(pc, 33419, 32810, (short) 4, 5, true);
		} else if(enterday < dayofyear){
			pc.setoptTime(time.get(Calendar.DAY_OF_YEAR) * 1000);
		} else {
			if(entertime > 109){
				int a = 120 - entertime;
				pc.sendPackets(new S_SystemMessage("\\fW던전 체류 시간이" +a+"분 남았습니다."));
			}
			pc.setoptTime(pc.getoptTime() + 1);
		}
	}

       /** 상아 리뉴얼 **/
	private void checkMopo(L1PcInstance _client) {
		 List<L1ItemInstance> items = _client.getInventory().getItems();  
		  for (L1ItemInstance item : items) {
		   if(item.getItemId() == L1ItemId.DRAGON_KEY){
		    if(System.currentTimeMillis() > item.getEndTime().getTime()){
		     _client.getInventory().deleteItem(item);
		    }
		   }
		   if (item.getItemId() == 5000063) {//쥬스100개
		    if (System.currentTimeMillis() > item.getEndTime().getTime()){
		     _client.getInventory().deleteItem(item);
		     _client.getInventory().storeItem(5000069, 1);
		     _client.sendPackets(new S_ServerMessage(1823));
		    }
		   } 
		   if (item.getItemId() == 5000064) { //쥬스200개
		    if (System.currentTimeMillis() > item.getEndTime().getTime()){
		     _client.getInventory().deleteItem(item);
		     _client.getInventory().storeItem(5000070, 1);
		     _client.sendPackets(new S_ServerMessage(1823));
		    }
		   } 
		   if (item.getItemId() == 5000065) {//쥬스300개
		    if (System.currentTimeMillis() > item.getEndTime().getTime()){
		     _client.getInventory().deleteItem(item);
		     _client.getInventory().storeItem(5000071, 1);
		     _client.sendPackets(new S_ServerMessage(1823));
		    }
		   } 
		   if (item.getItemId() == 5000066) { //진액100개
		    if (System.currentTimeMillis() > item.getEndTime().getTime()){
		     _client.getInventory().deleteItem(item);
		     _client.getInventory().storeItem(5000072, 1);
		     _client.sendPackets(new S_ServerMessage(1823));
		    }
		   } 
		   if (item.getItemId() == 5000067) { //진액200개
		    if (System.currentTimeMillis() > item.getEndTime().getTime()){
		     _client.getInventory().deleteItem(item);
		     _client.getInventory().storeItem(5000073, 1);
		     _client.sendPackets(new S_ServerMessage(1823));
		    }
		   } 
		   if (item.getItemId() == 5000068) { //진액300개
		    if (System.currentTimeMillis() > item.getEndTime().getTime()){
		     _client.getInventory().deleteItem(item);
		     _client.getInventory().storeItem(5000074, 1);
		     _client.sendPackets(new S_ServerMessage(1823));
		    }
		   }
		  }
		 }
	private void DragonBlood(L1PcInstance pc) {
		if(pc.getSkillEffectTimerSet().hasSkillEffect(DRAGONBLOOD_ANTA)) {
	 		int timeSec = pc.getSkillEffectTimerSet().getSkillEffectTimeSec(DRAGONBLOOD_ANTA);
	 		pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 82, timeSec / 60));	
	 	}
		
	 	if(pc.getSkillEffectTimerSet().hasSkillEffect(DRAGONBLOOD_PAP)) {
	 		int timeSec = pc.getSkillEffectTimerSet().getSkillEffectTimeSec(DRAGONBLOOD_PAP);
	 		pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 85, timeSec / 60));	
	 	}
	 	if(pc.getSkillEffectTimerSet().hasSkillEffect(DRAGONBLOOD_RIND)) {//린드레이드
	 		int timeSec = pc.getSkillEffectTimerSet().getSkillEffectTimeSec(DRAGONBLOOD_RIND);
	 		pc.sendPackets(new S_PacketBox(S_PacketBox.DRAGONBLOOD, 88, timeSec / 60));	
	 	}
	}
	private void CheckMaxHP(L1PcInstance pc) {
		double maxHp = 0;
		double[] knight =       { 18.5, 19.5, 20.5, 21.5, 23.5, 25.5, 26.5, 26.5, 27.5, 27.5, 28.5, 28.5, 29.5, 29.5, 30.5, 30.5, 31.5, 31.5, 32.5, 32.5, 33.5, 33.5 };
		double[] elf =          { 12.5, 13.5, 13.5, 13.5, 14.5, 15.5, 16.5, 19.5, 20.5, 20.5, 21.5, 21.5, 22.5, 22.5, 23.5, 23.5, 24.5, 24.5, 25.5, 25.5, 26.5, 26.5, 27.5, 27.5 };
		double[] dragonknight = { 16.5, 17.5, 18.5, 19.5, 20.5, 22.5, 23.5, 23.5, 24.5, 24.5, 25.5, 25.5, 26.5, 26.5, 27.5, 27.5, 28.5, 28.5, 29.5, 29.5, 30.5, 30.5 };
		double[] wizard =       { 10.5, 10.5, 11.5, 11.5, 12.5, 13.5, 14.5, 17.5, 18.5, 18.5, 19.5, 19.5, 20.5, 20.5, 21.5, 21.5, 22.5, 22.5, 23.5, 23.5, 24.5, 24.5, 25.5, 25.5 };
		double[] darkelf =      { 11.5, 11.5, 12.5, 12.5, 14.5, 14.5, 14.5, 14.5, 16.5, 16.5, 16.5, 19.5, 20.5, 20.5, 21.5, 21.5, 22.5, 22.5, 23.5, 23.5, 24.5, 24.5, 25.5, 25.5, 26.5, 26.5, 27.5, 27.5 };
		double[] crown =        { 11.5, 11.5, 12.5, 12.5, 13.5, 13.5, 15.5, 16.5, 17.5, 21.5, 22.5, 22.5, 23.5, 23.5, 24.5, 24.5, 25.5, 25.5, 26.5, 26.5, 27.5, 27.5, 28.5, 28.5, 29.5, 29.5 };
		double[] Illusionist =  { 11.5, 11.5, 12.5, 12.5, 13.5, 15.5, 16.5, 16.5, 17.5, 17.5, 18.5, 18.5, 19.5, 19.5, 20.5, 20.5, 21.5, 21.5, 22.5, 22.5, 23.5, 23.5, 24.5, 24.5 };
		double range = 50;	
		try {
			if (pc.getLevel() >= 65 && !pc.isGm()) {
				if (pc.isKnight()) {
					maxHp = (knight[pc.getAbility().getCon() - 14] * pc.getLevel()) + range;
				} else if (pc.isElf()) {
					maxHp = (elf[pc.getAbility().getCon() - 12] * pc.getLevel()) + range;
				} else if (pc.isDragonknight()) {
					maxHp = (dragonknight[pc.getAbility().getCon() - 14] * pc.getLevel()) + range;
				} else if (pc.isWizard()) {
					maxHp = (wizard[pc.getAbility().getCon() - 12] * pc.getLevel()) + range;
				} else if (pc.isDarkelf()) { 
					maxHp = (darkelf[pc.getAbility().getCon() - 8] * pc.getLevel()) + range;
				} else if (pc.isCrown()) {
					maxHp = (crown[pc.getAbility().getCon() - 10] * pc.getLevel()) + range;
				} else if (pc.isIllusionist()) {
					maxHp = (Illusionist[pc.getAbility().getCon() - 12] * pc.getLevel()) + range;
				}
				if (maxHp < pc.getBaseMaxHp()) {
					for (L1ItemInstance item : pc.getInventory().getItems()) {
						if (item.isEquipped()) {
							pc.getInventory().setEquipped(item, false);
						}
					}
					if (pc.getSkillEffectTimerSet().hasSkillEffect(ADVANCE_SPIRIT)){
						pc.getSkillEffectTimerSet().removeSkillEffect(ADVANCE_SPIRIT);
					}
					
					L1SkillUse l1skilluse = new L1SkillUse();
					l1skilluse.handleCommands(pc, L1SkillId.CANCELLATION, pc.getId(), pc.getX(), pc.getY(), null, 0, L1SkillUse.TYPE_LOGIN);
									
					pc.setMaxHp(1);
					pc.setCurrentHp(1);
					pc.setMaxMp(1);
					pc.setCurrentMp(1);
					pc.save();
				
					pc.sendPackets(new S_OwnCharStatus2(pc)); // 나비켓 기록
					pc.sendPackets(new S_HPUpdate(pc));
					pc.sendPackets(new S_MPUpdate(pc.getCurrentMp(), pc.getMaxMp()));
					
					pc.sendPackets(new S_SystemMessage("\\fYHP 수치가 올바르지 않습니다. 스텟초기화를 다시 하세요."));
					pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE, "\\fCHP 수치가 올바르지 않습니다. 스텟초기화를 다시 하세요."));
					for (L1PcInstance player : L1World.getInstance().getAllPlayers()) {
						if (pc.isGm()) {
							player.sendPackets(new S_SystemMessage("\\fY" + pc.getName() + "님 HP 버그에 검출되었습니다."));	
							//Accound에 등록한후에 BanIP추가 
							Account.ban(pc.getAccountName());//중계기라고 할지라도 자동 벤시켜버린다.
						}
					}
					
				}
			}
		} catch (Exception e) {
			_log.log(Level.SEVERE, "PrimeumControler[]Error2", e);
		}
	}
}