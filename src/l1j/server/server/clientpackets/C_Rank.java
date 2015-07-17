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

package l1j.server.server.clientpackets;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1War;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_MapTimer;
import l1j.server.server.serverpackets.S_Message_YN;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.utils.FaceToFace;
import server.LineageClient;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_Rank extends ClientBasePacket {

	private static final String C_RANK = "[C] C_Rank";

	private static Logger _log = Logger.getLogger(C_Rank.class.getName());

	public C_Rank(byte abyte0[], LineageClient clientthread) throws Exception {
		super(abyte0);

		int type = readC(); // ?
		int rank = readC();

		L1PcInstance pc = clientthread.getActiveChar();
		if (pc == null) {
			return;
		}
		L1Clan clan = L1World.getInstance().getClan(pc.getClanname());
		String clanname = pc.getClanname();

		  int Enchantlvl = 0;       //생존
		///////////////////생존의 외침
		if ((clan == null) && (type < 5)) {
			return;
		}
		///////////////////생존의 외침
		switch (type) {
		case 0: // 혈맹에 속한 인원수의 변동이 있었을 경우
			// pc.sendPackets(new S_PacketBox(pc, S_PacketBox.PLEDGE_ONE));
			pc.sendPackets(new S_PacketBox(pc, S_PacketBox.PLEDGE_TWO)); // 임시
			break;
		case 1:// 계급
			String name = readS();
			L1PcInstance targetPc = L1World.getInstance().getPlayer(name);
			if (rank < 2 || rank < 5 && 6 < rank) {
				// 랭크를 변경하는 사람의 이름과 랭크를 입력해 주세요. [랭크=가디안, 일반, 견습]
				pc.sendPackets(new S_ServerMessage(781));
				return;
			}

			if (pc.isCrown()) { // 군주
				if (pc.getId() != clan.getLeaderId()) { // 혈맹주
					pc.sendPackets(new S_ServerMessage(785)); // 당신은 이제 군주가
					// 아닙니다.
					return;
				}
				///////////혈맹리뉴얼//////////////
			} else if(pc.getClanRank() == 3 || pc.getClanRank() == 6){
				if(!(rank == 2 || rank == 5)){
					pc.sendPackets(new S_SystemMessage("수호기사는 계급을 견습, 일반 만 줄 수 있습니다."));
					return;
				}
				///////////혈맹리뉴얼//////////////
			} else {
				pc.sendPackets(new S_ServerMessage(518)); // 이 명령은 혈맹의 군주만을
				return;
			}

			if (targetPc != null) { // 온라인중
				if (pc == targetPc) {
				///////////혈맹리뉴얼//////////////
					if(pc.isCrown())
						pc.sendPackets(new S_SystemMessage("혈맹군주의 계급은 변경 할 수 없습니다."));
					else
						pc.sendPackets(new S_SystemMessage("자신의 계급은 변경 할 수 없습니다."));
					///////////혈맹리뉴얼//////////////
					return;
				}
				if (pc.getClanid() == targetPc.getClanid()) { // 같은 크란
					try {
						targetPc.setClanRank(rank);
						targetPc.save(); // DB에 캐릭터 정보를 기입한다
						targetPc.sendPackets(new S_PacketBox(S_PacketBox.MSG_RANK_CHANGED, rank)); //메세지
/*///////////혈맹리뉴얼//////////////
						if (rank == L1Clan.CLAN_RANK_PROBATION) {
							targetPc.sendPackets(new S_SystemMessage("당신의 계급이 견습기사로 변경되었습니다."));
							pc.sendPackets(new S_SystemMessage(""+name+"의 계급이 견습기사로 변경되었습니다."));

						} else if (rank == L1Clan.CLAN_RANK_PUBLIC) {
							targetPc.sendPackets(new S_SystemMessage("당신의 계급이 일반기사로 변경되었습니다."));
							pc.sendPackets(new S_SystemMessage(""+name+"의 계급이 일반기사로 변경되었습니다."));
	
						} else if (rank == L1Clan.CLAN_RANK_GUARDIAN) {
							targetPc.sendPackets(new S_SystemMessage("당신의 계급이 수호기사로 변경되었습니다."));
							pc.sendPackets(new S_SystemMessage(""+name+"의 계급이 수호기사로 변경되었습니다."));
						 } else if (rank == L1Clan.CLAN_RANK_SUB_PRINCE) {
						       targetPc.sendPackets(new S_SystemMessage("당신의 계급이 부군주로 변경되었습니다."));
						       pc.sendPackets(new S_SystemMessage(""+name+"의 계급이 부군주로 변경되었습니다."));

	///////////혈맹리뉴얼//////////////
						}*/
						clan.UpdataClanMember(targetPc.getName() ,targetPc.getClanRank());
					} catch (Exception e) {
						_log.log(Level.SEVERE, "C_Rank[계급변경]Error", e);
					}
				} else {
					pc.sendPackets(new S_ServerMessage(414)); // 같은 혈맹원이 아닙니다.
					return;
				}
			} else { // 오프 라인중
				L1PcInstance restorePc = CharacterTable.getInstance()
						.restoreCharacter(name);
				if (restorePc != null
						&& restorePc.getClanid() == pc.getClanid()) { // 같은 크란
					try {
						restorePc.setClanRank(rank);
						restorePc.save(); // DB에 캐릭터 정보를 기입한다
					} catch (Exception e) {
						_log.log(Level.SEVERE, "C_Rank[계급변경]Error", e);
					}
				} else {
					pc.sendPackets(new S_ServerMessage(109, name)); // %0라는 이름의
																	// 사람은 없습니다.
					return;
				}
			}
			break;

		case 2:// 목록
			// 5c 3e c7 c1 b8 ae bf a1 b9 d9 20 00 3f 53 ac 73
			// 5c 3e 45 76 61 73 74 6f 72 79 20 00 00 3d ea 03 // 10월21일 확인
			// 옵 /서브 /동맹클랜네임
			if (clan.getAlliance() != 0) {
				// pc.sendPackets(new S_PacketBox(S_PacketBox.PLEDGE_UNION,
				// clan.getAlliance()));
			} else {
				pc.sendPackets(new S_ServerMessage(1233)); // 동맹이 없습니다.
				return;
			}
			break;
		case 3:// 가입
			L1PcInstance allianceLeader = FaceToFace.faceToFace(pc);
			if (pc.getLevel() < 25 || !pc.isCrown()) {
				pc.sendPackets(new S_ServerMessage(1206));// 25레벨이상 혈맹 군주만
				// 동맹신청을 할 수 있습니다.
				// 또한 연합 군주는 동맹을 맺을
				// 수 없습니다.
				return;
			}
			if (pc.getClan().getAlliance() != 0) {
				pc.sendPackets(new S_ServerMessage(1202));// 이미 동맹에 가입된 상태입니다.
				return;
			}
			for (L1War war : L1World.getInstance().getWarList()) {
				if (war.CheckClanInWar(clanname)) {
					pc.sendPackets(new S_ServerMessage(1234)); // 전쟁중에는 동맹에 가입할
					// 수 없습니다.
					return;
				}
			}
			// 동맹 수 제한(4개혈맹) 추가해야함 // 1201 // 동맹에 가입할 수 없습니다.
			if (allianceLeader != null) {
				if (allianceLeader.getLevel() > 24 && allianceLeader.isCrown()) {
					allianceLeader.setTempID(pc.getId());
					allianceLeader.sendPackets(new S_Message_YN(223, pc
							.getName()));
				} else {
					pc.sendPackets(new S_ServerMessage(1201));// 동맹에 가입할 수
					// 없습니다.
				}
			}
			break;
		case 4:// 탈퇴
			for (L1War war : L1World.getInstance().getWarList()) {
				if (war.CheckClanInWar(clanname)) {
					pc.sendPackets(new S_ServerMessage(1203)); // 전쟁중에는 동맹을 탈퇴할
					// 수 없습니다.
					return;
				}
			}
			if (clan.getAlliance() != 0) {
				pc.sendPackets(new S_Message_YN(1210, "")); // 정말로 동맹을 탈퇴하시겠습니까?
				// (Y/N)
			} else {
				pc.sendPackets(new S_ServerMessage(1233)); // 동맹이 없습니다.
			}
			break;
		case 8:// (/입장시간)
			   entertime(pc);
			   break; 
		 case 9:
			  pc.sendPackets(new S_MapTimer(pc));
			  break;
			  
			  
///////////////////생존의 외침
		case 5: // 생존의 외침
			  try {
		            int NewHp = 0;
		    
		            Random random = new Random();
		    
		            if (pc.get_food() >= 225) {
		              try {
		                Enchantlvl = pc.getEquipSlot().getWeapon().getEnchantLevel();
		              } catch (Exception e) {
		                //pc.sendPackets(new S_SystemMessage("\\fY무기를 착용해야 생존의 외침을 사용할 수 있습니다."));
		               pc.sendPackets(new S_ServerMessage(1973));
		               return;
		              }
		    
		              if (1800000L < System.currentTimeMillis() - pc.getSurvivalCry()) {
		                if (Enchantlvl <= 6) {
		                  int[] probability = { 20, 30, 40 };
		                  int percent = probability[random.nextInt(probability.length)];
		                  NewHp = pc.getCurrentHp() + pc.getMaxHp() / 100 * percent;
		    
		                  if (NewHp > pc.getMaxHp()) {
		                    NewHp = pc.getMaxHp();
		                  }
		    
		                  pc.setCurrentHp(NewHp);
		                  pc.sendPackets(new S_SystemMessage("생존의 외침을 사용하여 최대HP " + percent + "%를 회복하였습니다."));
		                  pc.sendPackets(new S_SkillSound(pc.getId(), 8684));
		                  Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8684));
		                  pc.set_food(0);
		                  pc.sendPackets(new S_PacketBox(11, pc.get_food()));
		                  pc.setSurvivalCry(System.currentTimeMillis());
		                } else if ((Enchantlvl >= 7) && (Enchantlvl <= 8)) {
		                  int[] probability = { 30, 40, 50 };
		                  int percent = probability[random.nextInt(probability.length)];
		    
		                  NewHp = pc.getCurrentHp() + pc.getMaxHp() / 100 * percent;
		    
		                  if (NewHp > pc.getMaxHp()) {
		                    NewHp = pc.getMaxHp();
		                 }
		    
		                  pc.setCurrentHp(NewHp);
		                  pc.sendPackets(new S_SystemMessage("생존의 외침을 사용하여 최대HP " + percent + "%를 회복하였습니다."));
		                  pc.sendPackets(new S_SkillSound(pc.getId(), 8685));
		                  Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8685));
		                  pc.set_food(0);
		                 pc.sendPackets(new S_PacketBox(11, pc.get_food()));
		                 pc.setSurvivalCry(System.currentTimeMillis());
		                } else if ((Enchantlvl >= 9) && (Enchantlvl <= 10)) {
		                  int[] probability = { 50, 60 };
		                  int percent = probability[random.nextInt(probability.length)];
		    
		                  NewHp = pc.getCurrentHp() + pc.getMaxHp() / 100 * percent;
		   
		                 if (NewHp > pc.getMaxHp()) {
		                    NewHp = pc.getMaxHp();
		                  }
		    
		                  pc.setCurrentHp(NewHp);
		                  pc.sendPackets(new S_SystemMessage("생존의 외침을 사용하여 최대HP " + percent + "%를 회복하였습니다."));
		                  pc.sendPackets(new S_SkillSound(pc.getId(), 8773));
		                  Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8773));
		                 pc.set_food(0);
		                  pc.sendPackets(new S_PacketBox(11, pc.get_food()));
		                  pc.setSurvivalCry(System.currentTimeMillis());
		                } else if (Enchantlvl >= 11) {
		                  NewHp = pc.getCurrentHp() + pc.getMaxHp() / 100 * 7;
		    
		                  if (NewHp > pc.getMaxHp()) {
		                    NewHp = pc.getMaxHp();
		                  }
		    
		                  pc.setCurrentHp(NewHp);
		                  pc.sendPackets(new S_SystemMessage("생존의 외침을 사용하여 최대HP 70%를 회복하였습니다."));
		                  pc.sendPackets(new S_SkillSound(pc.getId(), 8686));
		                  Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8686));
		                  pc.set_food(0);
		                  pc.sendPackets(new S_PacketBox(11, pc.get_food()));
		                  pc.setSurvivalCry(System.currentTimeMillis());
		                }
		              } else {
		                long time = 1800L - (System.currentTimeMillis() - pc.getSurvivalCry()) / 1000L;
		    
		                long minute = time / 60L;
		                long second = time % 60L;
		    
		                if (minute >= 29L) {
		                	pc.sendPackets(new S_ServerMessage(1974));
		                  return;
		                }
		    
		                NewHp = pc.getCurrentHp() + pc.getMaxHp() / 100 * (30 - (int)minute);
		    
		                if (NewHp > pc.getMaxHp()) {
		                  NewHp = pc.getMaxHp();
		                }
		    
		                pc.setCurrentHp(NewHp);
		                pc.sendPackets(new S_SystemMessage("생존의 외침을 사용하여 최대HP " + (30 - (int)minute) + "%를 회복하였습니다."));
		                pc.sendPackets(new S_SkillSound(pc.getId(), 8683));
		                Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8683));
		                pc.set_food(0);
		                pc.sendPackets(new S_PacketBox(11, pc.get_food()));
		               pc.setSurvivalCry(System.currentTimeMillis());
		              }
		            } else {
		              pc.sendPackets(new S_SystemMessage("배고픔게이지를 100% 채워주시기 바랍니다. "));
		              //pc.sendPackets(new S_SystemMessage("\\fY30분뒤에 사용가능합니다."));
		       //       pc.sendPackets(new S_ServerMessage(1974));
		            }
		          } catch (Exception e) {
		          }
		          break;
		 case 6:
		          try {
		              if (pc.get_food() >= 225) {
		                try {
		                  Enchantlvl = pc.getEquipSlot().getWeapon().getEnchantLevel();
		               } catch (Exception e) {
		              	 pc.sendPackets(new S_ServerMessage(1973));
		                  return;
		               }
		      
		                if (Enchantlvl <= 6) {
		                  pc.sendPackets(new S_SkillSound(pc.getId(), 8684));
		                  Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8684));
		                } else if ((Enchantlvl >= 7) && (Enchantlvl <= 8)) {
		                  pc.sendPackets(new S_SkillSound(pc.getId(), 8685));
		                  Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8685));
		               } else if ((Enchantlvl >= 9) && (Enchantlvl <= 10)) {
		                  pc.sendPackets(new S_SkillSound(pc.getId(), 8773));
		                  Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8773));
		                } else if (Enchantlvl >= 11) {
		                  pc.sendPackets(new S_SkillSound(pc.getId(), 8686));
		                  Broadcaster.broadcastPacket(pc, new S_SkillSound(pc.getId(), 8686));
		               }
		              }
		             else {
		                pc.sendPackets(new S_SystemMessage("배고픔게이지를 100% 채워주시기 바랍니다."));
		            //   pc.sendPackets(new S_SystemMessage("\\fY사용하시기 바랍니다."));
		              }
		            }
		            catch (Exception e) {
		            }
		          }
		       }

	private static Random _rnd = new Random();

	public static Random getRnd() {
		return _rnd;
	}

	public static int get_random(int min, int max) {
		if (min > max)
			return min;
		return _rnd.nextInt(max - min + 1) + min;
	}
///////////////////생존의 외침
	 private void entertime(L1PcInstance pc) {
		    try {
		     int entertime1 = 180 - pc.getGdungeonTime() % 1000;
		     int entertime2 = 180 - pc.getLdungeonTime() % 1000;
		     int entertime3 = 60 - pc.getTkddkdungeonTime() % 1000;
		     int entertime4 = 120 - pc.getDdungeonTime() % 1000;
		     
		     String time1 = Integer.toString(entertime1);
		     String time2 = Integer.toString(entertime2);
		     String time3 = Integer.toString(entertime3);
		     String time4 = Integer.toString(entertime4);   

		     pc.sendPackets(new S_ServerMessage(2535, "$12125", time1)); 
		     pc.sendPackets(new S_ServerMessage(2535, "$6081", time3));
		     pc.sendPackets(new S_ServerMessage(2535, "$12126", time2));
		     pc.sendPackets(new S_ServerMessage(2535, "$14250", time4));
		    } catch (Exception e) {
		    }
		   }


	@Override
	public String getType() {
		return C_RANK;
	}
}
