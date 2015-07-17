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

package l1j.server.server.utils;

import java.util.logging.Logger;

import javolution.util.FastTable;
import l1j.server.Config;
import l1j.server.server.Account;
import l1j.server.server.GameServerSetting;
import l1j.server.server.datatables.ExpTable;
import l1j.server.server.datatables.PetTable;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1Clan;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1DollInstance;
import l1j.server.server.model.Instance.L1MonsterInstance;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.Instance.L1PetInstance;
import l1j.server.server.model.Instance.L1SummonInstance;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_PacketBox;
import l1j.server.server.serverpackets.S_PetPack;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.templates.L1Pet;

// Referenced classes of package l1j.server.server.utils:
// CalcStat

public class CalcExp {

	private static final long serialVersionUID = 1L;

	private static Logger _log = Logger.getLogger(CalcExp.class.getName());

	public static final int MAX_EXP = ExpTable.getExpByLevel(100) - 1;

	private static L1NpcInstance npc = null;

	private CalcExp() {
	}

	public static void calcExp(L1PcInstance l1pcinstance, int targetid,
			FastTable<?> acquisitorList, FastTable<?> hateList, int exp) {

		int i = 0;
		double party_level = 0;
		double dist = 0;
		int member_exp = 0;
		int member_lawful = 0;
		L1Object l1object = L1World.getInstance().findObject(targetid);
		npc = (L1NpcInstance) l1object;

		// 헤이트의 합계를 취득
		L1Character acquisitor;
		int hate = 0;
		int acquire_exp = 0;
		int acquire_lawful = 0;
		int party_exp = 0;
		int party_lawful = 0;
		int totalHateExp = 0;
		int totalHateLawful = 0;
		int partyHateExp = 0;
		int partyHateLawful = 0;
		int ownHateExp = 0;

		if (acquisitorList.size() != hateList.size()) {
			return;
		}
		for (i = hateList.size() - 1; i >= 0; i--) {
			acquisitor = (L1Character) acquisitorList.get(i);
			hate = (Integer) hateList.get(i);
			if (acquisitor != null && !acquisitor.isDead()) {
				totalHateExp += hate;
				if (acquisitor instanceof L1PcInstance) {
					totalHateLawful += hate;
				}
			} else { // null였거나 죽어 있으면(자) 배제
				acquisitorList.remove(i);
				hateList.remove(i);
			}
		}
		if (totalHateExp == 0) { // 취득자가 없는 경우
			return;
		}

		if (l1object != null && !(npc instanceof L1PetInstance)
				&& !(npc instanceof L1SummonInstance)) {
			// int exp = npc.get_exp();
			if (!L1World.getInstance().isProcessingContributionTotal()
					&& l1pcinstance.getHomeTownId() > 0) {
				int contribution = npc.getLevel() / 10;
				l1pcinstance.addContribution(contribution);
			}
			int lawful = npc.getLawful();
			if (l1pcinstance.isInParty()) { // 파티중
				partyHateExp = 0;
				partyHateLawful = 0;
				for (i = hateList.size() - 1; i >= 0; i--) {
					acquisitor = (L1Character) acquisitorList.get(i);
					hate = (Integer) hateList.get(i);
					if (acquisitor instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) acquisitor;
						if (pc == l1pcinstance) {
							partyHateExp += hate;
							partyHateLawful += hate;
						} else if (l1pcinstance.getParty().isMember(pc)) {
							partyHateExp += hate;
							partyHateLawful += hate;
						} else {
							if (totalHateExp > 0) {
								acquire_exp = (exp * hate / totalHateExp);
							}
							if (totalHateLawful > 0) {
								acquire_lawful = (lawful * hate / totalHateLawful);
							}
							AddExp(pc, acquire_exp, acquire_lawful);
						}
					} else if (acquisitor instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) pet.getMaster();
						if (master == l1pcinstance) {
							partyHateExp += hate;
						} else if (l1pcinstance.getParty().isMember(master)) {
							partyHateExp += hate;
						} else {
							if (totalHateExp > 0) {
								acquire_exp = (exp * hate / totalHateExp);
							}
							AddExpPet(pet, acquire_exp);
						}
					} else if (acquisitor instanceof L1SummonInstance) {
						L1SummonInstance summon = (L1SummonInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) summon.getMaster();
						if (master == l1pcinstance) {
							partyHateExp += hate;
						} else if (l1pcinstance.getParty().isMember(master)) {
							partyHateExp += hate;
						} else {
						}
					}
				}
				if (totalHateExp > 0) {
					party_exp = (exp * partyHateExp / totalHateExp);
				}
				if (totalHateLawful > 0) {
					party_lawful = (lawful * partyHateLawful / totalHateLawful);
				}

				// EXP, 로우훌 배분

				// 프리보나스
				double pri_bonus = 0;
				L1PcInstance leader = l1pcinstance.getParty().getLeader();
				if (leader.isCrown()
						&& (l1pcinstance.getNearObjects().knownsObject(leader) || l1pcinstance
								.equals(leader))) {
					pri_bonus = 0.059;
				}

				// PT경험치의 계산
				L1PcInstance[] ptMembers = l1pcinstance.getParty().getMembers();
				double pt_bonus = 0;
				for (L1PcInstance each : l1pcinstance.getParty().getMembers()) {
					if (l1pcinstance.getNearObjects().knownsObject(each)
							|| l1pcinstance.equals(each)) {
						party_level += each.getLevel() * each.getLevel();
					}
					if (l1pcinstance.getNearObjects().knownsObject(each)) {
						pt_bonus += 0.04;
					}
				}

				party_exp = (int) (party_exp * (1 + pt_bonus + pri_bonus));

				// 자캐릭터와 그 애완동물·사몬의 헤이트의 합계를 산출
				if (party_level > 0) {
					dist = ((l1pcinstance.getLevel() * l1pcinstance.getLevel()) / party_level);
				}
				member_exp = (int) (party_exp * dist);
				member_lawful = (int) (party_lawful * dist);

				ownHateExp = 0;
				for (i = hateList.size() - 1; i >= 0; i--) {
					acquisitor = (L1Character) acquisitorList.get(i);
					hate = (Integer) hateList.get(i);
					if (acquisitor instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) acquisitor;
						if (pc == l1pcinstance) {
							ownHateExp += hate;
						}
					} else if (acquisitor instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) pet.getMaster();
						if (master == l1pcinstance) {
							ownHateExp += hate;
						}
					} else if (acquisitor instanceof L1SummonInstance) {
						L1SummonInstance summon = (L1SummonInstance) acquisitor;
						L1PcInstance master = (L1PcInstance) summon.getMaster();
						if (master == l1pcinstance) {
							ownHateExp += hate;
						}
					}
				}
				// 자캐릭터와 그 애완동물·사몬에 분배
				if (ownHateExp != 0) { // 공격에 참가하고 있었다
					for (i = hateList.size() - 1; i >= 0; i--) {
						acquisitor = (L1Character) acquisitorList.get(i);
						hate = (Integer) hateList.get(i);
						if (acquisitor instanceof L1PcInstance) {
							L1PcInstance pc = (L1PcInstance) acquisitor;
							if (pc == l1pcinstance) {
								if (ownHateExp > 0) {
									acquire_exp = (member_exp * hate / ownHateExp);
								}
								AddExp(pc, acquire_exp, member_lawful);
							}
						} else if (acquisitor instanceof L1PetInstance) {
							L1PetInstance pet = (L1PetInstance) acquisitor;
							L1PcInstance master = (L1PcInstance) pet
									.getMaster();
							if (master == l1pcinstance) {
								if (ownHateExp > 0) {
									acquire_exp = (member_exp * hate / ownHateExp);
								}
								AddExpPet(pet, acquire_exp);
							}
						} else if (acquisitor instanceof L1SummonInstance) {
						}
					}
				} else { // 공격에 참가하고 있지 않았다
					// 자캐릭터에만 분배
					AddExp(l1pcinstance, member_exp, member_lawful);
				}

				// 파티 멤버와 그 애완동물·사몬의 헤이트의 합계를 산출
				for (int cnt = 0; cnt < ptMembers.length; cnt++) {
					if (l1pcinstance.getNearObjects().knownsObject(
							ptMembers[cnt])) {
						if (party_level > 0) {
							dist = ((ptMembers[cnt].getLevel() * ptMembers[cnt]
									.getLevel()) / party_level);
						}
						member_exp = (int) (party_exp * dist);
						member_lawful = (int) (party_lawful * dist);

						ownHateExp = 0;
						for (i = hateList.size() - 1; i >= 0; i--) {
							acquisitor = (L1Character) acquisitorList.get(i);
							hate = (Integer) hateList.get(i);
							if (acquisitor instanceof L1PcInstance) {
								L1PcInstance pc = (L1PcInstance) acquisitor;
								if (pc == ptMembers[cnt]) {
									ownHateExp += hate;
								}
							} else if (acquisitor instanceof L1PetInstance) {
								L1PetInstance pet = (L1PetInstance) acquisitor;
								L1PcInstance master = (L1PcInstance) pet
										.getMaster();
								if (master == ptMembers[cnt]) {
									ownHateExp += hate;
								}
							} else if (acquisitor instanceof L1SummonInstance) {
								L1SummonInstance summon = (L1SummonInstance) acquisitor;
								L1PcInstance master = (L1PcInstance) summon
										.getMaster();
								if (master == ptMembers[cnt]) {
									ownHateExp += hate;
								}
							}
						}
						// 파티 멤버와 그 애완동물·사몬에 분배
						if (ownHateExp != 0) { // 공격에 참가하고 있었다
							for (i = hateList.size() - 1; i >= 0; i--) {
								acquisitor = (L1Character) acquisitorList
										.get(i);
								hate = (Integer) hateList.get(i);
								if (acquisitor instanceof L1PcInstance) {
									L1PcInstance pc = (L1PcInstance) acquisitor;
									if (pc == ptMembers[cnt]) {
										if (ownHateExp > 0) {
											acquire_exp = (member_exp * hate / ownHateExp);
										}
										AddExp(pc, acquire_exp, member_lawful);
									}
								} else if (acquisitor instanceof L1PetInstance) {
									L1PetInstance pet = (L1PetInstance) acquisitor;
									L1PcInstance master = (L1PcInstance) pet
											.getMaster();
									if (master == ptMembers[cnt]) {
										if (ownHateExp > 0) {
											acquire_exp = (member_exp * hate / ownHateExp);
										}
										AddExpPet(pet, acquire_exp);
									}
								} else if (acquisitor instanceof L1SummonInstance) {
								}
							}
						} else { // 공격에 참가하고 있지 않았다
							// 파티 멤버에만 분배
							AddExp(ptMembers[cnt], member_exp, member_lawful);
						}
					}
				}
			} else { // 파티를 짜지 않았다
				// EXP, 로우훌의 분배
				for (i = hateList.size() - 1; i >= 0; i--) {
					acquisitor = (L1Character) acquisitorList.get(i);
					hate = (Integer) hateList.get(i);
					acquire_exp = (exp * hate / totalHateExp);
					if (acquisitor instanceof L1PcInstance) {
						if (totalHateLawful > 0) {
							acquire_lawful = (lawful * hate / totalHateLawful);
						}
					}

					if (acquisitor instanceof L1PcInstance) {
						L1PcInstance pc = (L1PcInstance) acquisitor;
						AddExp(pc, acquire_exp, acquire_lawful);
					} else if (acquisitor instanceof L1PetInstance) {
						L1PetInstance pet = (L1PetInstance) acquisitor;
						AddExpPet(pet, acquire_exp);
					} else if (acquisitor instanceof L1SummonInstance) {
					}
				}
			}
		}
	}

	private static void AddExp(L1PcInstance pc, int exp, int lawful) {
		if (pc.isDead())
			return;
		if (npc instanceof L1MonsterInstance) {
			L1MonsterInstance mon = (L1MonsterInstance) npc;
			if (mon.getUbId() != 0) {
				int ubexp = (exp / 10);
				pc.setUbScore(pc.getUbScore() + ubexp);
			}
		}

		int pclevel = pc.getLevel();
		int add_lawful = (int) (lawful * Config.RATE_LAWFUL) * -1;
		pc.addLawful(add_lawful); 
		/** 2012.11.28 큐르 로긴후획득정보 **/
		//  pc.setInforLawful(pc.getInforLawful() + add_lawful);
		  /** **/

		double exppenalty = ExpTable.getPenaltyRate(pclevel);
		double foodBonus = 1;
		double expposion = 1;
		double levelBonus = 1;
		double ainhasadBonus = 1;
		double dollBonus = 1.0;// 추가눈사람
        double gereng = 1;//게렝
		double emeraldBonus = 0;// 드래곤에메랄드
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_NEW_1)
			    || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_NEW_2)
			    || pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_NEW_3)) {
			   foodBonus = 1.02;
			  }
			  if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_NEW_4)) {
			   foodBonus = 1.04;
			  }
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.COOKING_1_7_N)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.COOKING_1_7_S)) {
			foodBonus = 1.01;
		} else if (pc.getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.COOKING_1_15_N)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.COOKING_1_15_S)) {
			foodBonus = 1.02;
		} else if (pc.getSkillEffectTimerSet().hasSkillEffect(
				L1SkillId.COOKING_1_23_N)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.COOKING_1_23_S)) {
			foodBonus = 1.03;
		}

		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EXP_POTION)
				|| pc.getSkillEffectTimerSet().hasSkillEffect(
						L1SkillId.STATUS_COMA_5)) {
			expposion = 1.2;
		}
				if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EXP_POTION2)) {
			gereng = 1.4;
		}
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.EXP_POTION3)) {
			gereng = 1.3;
		}
//		 드래곤에메랄드
		if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGON_EMERALD_YES)
			    && pc.getAinHasad() > 10000) {
			   emeraldBonus = 0.77;
			   pc.calAinHasad(-exp);
			   pc.sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, pc.getAinHasad()));
			  }
//		 드래곤에메랄드
		// ///////////레벨제한/////////////
		if (pc.getLevel() >= GameServerSetting.getInstance().get_maxLevel())
			return;
		// ///////////레벨제한/////////////
		if (pclevel >= 49) {
			if (pclevel <= 64) {
				double minus = 64 - pclevel;
				if (minus == 0)
					minus = 1;
				levelBonus = minus / 100;
				levelBonus = levelBonus + 1;
			}
			if (pc.getAinHasad() > 10000) {
				pc.calAinHasad(-exp);
				ainhasadBonus = 1.77;
				pc.sendPackets(new S_PacketBox(S_PacketBox.AINHASAD, pc.getAinHasad()));
			}
		}
		
		//여기까지 음식
		//아래부터 눈사람인형으로인해서증가하는수치 
		for (L1DollInstance doll : pc.getDollList().values()) {
			int dollType = doll.getDollType();
			if (dollType == L1DollInstance.DOLLTYPE_SNOWMAN_A
					|| dollType == L1DollInstance.DOLLTYPE_SNOWMAN_B
					|| dollType == L1DollInstance.DOLLTYPE_SNOWMAN_C) {
				dollBonus = 1.1;
			}
		} // 추가눈사람
		
		//아래서부턴 혈맹가입유무에따라서 증가하는수치 
		int newchar = 1;
		int settingEXP = (int) Config.RATE_XP;
		/** 혈맹가입했을때 경험치 더주기* */
		int clan_id = pc.getClanid();
		if (clan_id != 0) { // 혈맹가입을 한 상태라면
			
			settingEXP *= 1.0; // 현재 경험치의 1.0배가 먹힌다.
		} else {
			settingEXP *= 1; // 경험치는 제자리로 돌아감.
		}
		/** 혈맹가입했을때 경험치 더주기* */

		int add_exp = (int) (exp * settingEXP * foodBonus * expposion * levelBonus * (ainhasadBonus + emeraldBonus) * exppenalty * newchar * dollBonus* gereng);

		int add_inforEXP =  (int) (exp * settingEXP * foodBonus * expposion * levelBonus * (ainhasadBonus + emeraldBonus) * exppenalty * newchar * dollBonus* gereng);


    	/* 폭렙 방지*/
		if (pc.getLevel() >= 1) {
		  if ((add_exp + pc.getExp()) > ExpTable.getExpByLevel((pc.getLevel()+1))) {
		   add_exp =  ExpTable.getExpByLevel((pc.getLevel()+1)) - pc.getExp();
			}
		}
		if (add_exp < 0) {
			return;
		}
		
		if (pc.getLevel() == 1 && pc.getExp() > ExpTable.getExpByLevel((pc.getLevel()))) {
			if (pc.getRobotAi() != null) {
				return;
			}
            try {
                System.out.println("스초버그 : "+pc.getName()); //cmd에 출력
                Thread.sleep(2000); //이부분이 Point. 멘트를 읽을수있게 2초간의 여유를 준뒤에 킥함 ㅋㅋ
                pc.sendPackets(new S_Disconnect()); //대상을 킥함
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	
	

		
		
				L1Clan clan = L1World.getInstance().getClan(pc.getClanname()); // 파란색대신 추가
				if (clan != null) {
					  if (pc.getClanid() != 0 && clan.getOnlineClanMember().length >= 5) {
					  add_exp *= Config.RATE_CCLAN_XP1;	
					  pc.addExp(add_exp);
					  }
					if (clan.getCastleId() != 0) {
						add_exp *= Config.RATE_CCLAN_XP;						   
						pc.addExp(add_exp);
					} else {
						pc.addExp(add_exp);
					}
				} else {
					if (pc.getRobotAi() == null) {
					CheckQuize(pc);
					}
			pc.addExp(add_exp);
			if( npc instanceof L1MonsterInstance){
				pc.setMonsterKill(pc.getMonsterKill()+1);
				}
		}
		pc.sendUhodo();// 우호도표기
	}
	
	private static void AddExpPet(L1PetInstance pet, int exp) {
		L1PcInstance pc = (L1PcInstance) pet.getMaster();

		// int petNpcId = pet.getNpcTemplate().get_npcId();
		int petItemObjId = pet.getItemObjId();

		int levelBefore = pet.getLevel();
		int totalExp = (int) (exp * 50 + pet.getExp());// 펫 경험치는 50배
		if (totalExp >= ExpTable.getExpByLevel(51)) {
			totalExp = ExpTable.getExpByLevel(51) - 1;
		}
		pet.setExp(totalExp);

		pet.setLevel(ExpTable.getLevelByExp(totalExp));

		int expPercentage = ExpTable.getExpPercentage(pet.getLevel(), totalExp);

		int gap = pet.getLevel() - levelBefore;
		for (int i = 1; i <= gap; i++) {
			IntRange hpUpRange = pet.getPetType().getHpUpRange();
			IntRange mpUpRange = pet.getPetType().getMpUpRange();
			pet.addMaxHp(hpUpRange.randomValue());
			pet.addMaxMp(mpUpRange.randomValue());
		}

		pet.setExpPercent(expPercentage);
		pc.sendPackets(new S_PetPack(pet, pc));

		if (gap != 0) { // 레벨업하면(자) DB에 기입한다
			L1Pet petTemplate = PetTable.getInstance()
					.getTemplate(petItemObjId);
			if (petTemplate == null) { // PetTable에 없다
				_log.warning("L1Pet == null");
				return;
			}
			petTemplate.set_exp(pet.getExp());
			petTemplate.set_level(pet.getLevel());
			petTemplate.set_hp(pet.getMaxHp());
			petTemplate.set_mp(pet.getMaxMp());
			PetTable.getInstance().storePet(petTemplate); // DB에 기입해
			pc.sendPackets(new S_ServerMessage(320, pet.getName())); // \f1%0의 레벨이올랐습니다.
		}
	}

	private static void CheckQuize(L1PcInstance pc) {
		if(pc.noPlayerCK)
			return;
		Account account = Account.load(pc.getAccountName());
		if (pc.getLevel() > 50)//50부터뜨도록
			if (account.getquize() == null || account.getquize() == "") {
				pc.sendPackets(new S_PacketBox(S_PacketBox.GREEN_MESSAGE,"퀴즈가 설정되지 않았습니다 해킹 방지를 위해 .퀴즈설정 [설정하실퀴즈]를 입력해주세요."));
				if (!pc.getExcludingList().contains(pc.getName())) {
				}
			}
	}
} // CalcExp
