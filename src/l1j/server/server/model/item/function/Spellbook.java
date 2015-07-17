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

package l1j.server.server.model.item.function;

import l1j.server.server.clientpackets.ClientBasePacket;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Character;
import l1j.server.server.model.L1ItemDelay;
import l1j.server.server.model.Instance.L1ItemInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.item.L1ItemId;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_ServerMessage;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1EtcItem;
import l1j.server.server.templates.L1Item;
import l1j.server.server.templates.L1Skills;
import l1j.server.server.types.Point;

@SuppressWarnings("serial")
public class Spellbook extends L1ItemInstance {

	public Spellbook(L1Item item) {
		super(item);
	}

	@Override
	public void clickItem(L1Character cha, ClientBasePacket packet) {
		if (cha instanceof L1PcInstance) {
			L1PcInstance pc = (L1PcInstance) cha;
			L1ItemInstance useItem = pc.getInventory().getItem(this.getId());
			int itemId = useItem.getItemId();
			int delay_id = 0;
			if (useItem.getItem().getType2() == 0) { // 종별：그 외의 아이템
				delay_id = ((L1EtcItem) useItem.getItem()).get_delayid();
			}
			if (delay_id != 0) { // 지연 설정 있어
				if (pc.hasItemDelay(delay_id) == true) {
					return;
				}
			}
			if (itemId > 40169 && itemId < 40226 || itemId >= 45000
					&& itemId <= 45022) { // 마법서
				useSpellBook(pc, useItem, itemId);
			} else if (itemId > 40225 && itemId < 40232) {
				if (pc.isCrown() || pc.isGm()) {
					if (itemId == 40226 && pc.getLevel() >= 15) {
						SpellBook4(pc, useItem);
					} else if (itemId == 40228 && pc.getLevel() >= 30) {
						SpellBook4(pc, useItem);
					} else if (itemId == 40227 && pc.getLevel() >= 40) {
						SpellBook4(pc, useItem);
					} else if ((itemId == 40231 || itemId == 40232)
							&& pc.getLevel() >= 45) {
						SpellBook4(pc, useItem);
					} else if (itemId == 40230 && pc.getLevel() >= 50) {
						SpellBook4(pc, useItem);
					} else if (itemId == 40229 && pc.getLevel() >= 55) {
						SpellBook4(pc, useItem);
					} else {
						pc.sendPackets(new S_ServerMessage(312)); // LV가 낮아서
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
			} else if (itemId >= 40232 && itemId <= 40264 // 정령의 수정
					|| itemId >= 41149 && itemId <= 41153) {
				useElfSpellBook(pc, useItem, itemId);
			} else if (itemId > 40264 && itemId < 40280 || itemId == 5559) {
				if (pc.isDarkelf() || pc.isGm()) {
					if (itemId >= 40265 && itemId <= 40269 // 어둠 정령의 수정
							&& pc.getLevel() >= 15) {
						SpellBook1(pc, useItem);
					} else if (itemId >= 40270 && itemId <= 40274 // / 어둠 정령의
							// 수정
							&& pc.getLevel() >= 30) {
						SpellBook1(pc, useItem);
					} else if (itemId >= 40275 && itemId <= 40279
							&& pc.getLevel() >= 45) {
						SpellBook1(pc, useItem);
					 } else if (itemId == 5559 && pc.getLevel() >= 60) {//아머 브레이크
	                        SpellBook1(pc, useItem);
					} else {
						pc.sendPackets(new S_ServerMessage(312));
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79)); // (원문:어둠 정령의 수정은 다크 에르프만을 습득할 수 있습니다. )
				}
			} else if (itemId >= 40164 && itemId <= 40166 // 기술서
					|| itemId >= 41147 && itemId <= 41148) {
				if (pc.isKnight() || pc.isGm()) {
					if (itemId >= 40164 && itemId <= 40165 // 스탠, 축소 아모
							&& pc.getLevel() >= 50) {
						SpellBook3(pc, useItem);
					} else if (itemId >= 41147 && itemId <= 41148 // 솔리드 왕복대,카운터 바리어
							&& pc.getLevel() >= 50) {
						SpellBook3(pc, useItem);
					} else if (itemId == 40166 && pc.getLevel() >= 60) { // 바운스아탁크
						SpellBook3(pc, useItem);
					} else {
						pc.sendPackets(new S_ServerMessage(312));
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
			} else if (itemId >= L1ItemId.DRAGONKNIGHT_SPELLSTART && itemId <= L1ItemId.DRAGONKNIGHT_SPELLEND) {
				if (pc.isDragonknight() || pc.isGm()) {
					if (itemId >= L1ItemId.DRAGONKNIGHTTABLET_DRAGONSKIN
							&& itemId <= L1ItemId.DRAGONKNIGHTTABLET_AWAKE_ANTHARAS
							&& pc.getLevel() >= 15) {
						SpellBook5(pc, useItem);
					} else if (itemId >= L1ItemId.DRAGONKNIGHTTABLET_BLOODLUST
							&& itemId <= L1ItemId.DRAGONKNIGHTTABLET_AWAKE_PAPURION
							&& pc.getLevel() >= 30) {
						SpellBook5(pc, useItem);
					} else if (itemId >= L1ItemId.DRAGONKNIGHTTABLET_MOTALBODY
							&& itemId <= L1ItemId.DRAGONKNIGHTTABLET_AWAKE_BALAKAS
							&& pc.getLevel() >= 45) {
						SpellBook5(pc, useItem);
					} else {
						pc.sendPackets(new S_ServerMessage(312));
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
				
			} else if (itemId >= L1ItemId.ILLUSIONIST_SPELLSTART
					&& itemId <= L1ItemId.ILLUSIONIST_SPELLEND) {
				if (pc.isIllusionist() || pc.isGm()) {
					if (itemId >= L1ItemId.MEMORIALCRYSTAL_MIRRORIMAGE
							&& itemId <= L1ItemId.MEMORIALCRYSTAL_CUBE_IGNITION
							&& pc.getLevel() >= 10) {
						SpellBook6(pc, useItem);
					} else if (itemId >= L1ItemId.MEMORIALCRYSTAL_CONSENTRATION
							&& itemId <= L1ItemId.MEMORIALCRYSTAL_CUBE_QUAKE
							&& pc.getLevel() >= 20) {
						SpellBook6(pc, useItem);
					} else if (itemId >= L1ItemId.MEMORIALCRYSTAL_PATIENCE
							&& itemId <= L1ItemId.MEMORIALCRYSTAL_CUBE_SHOCK
							&& pc.getLevel() >= 30) {
						SpellBook6(pc, useItem);
					} else if (itemId >= L1ItemId.MEMORIALCRYSTAL_INSITE
							&& itemId <= L1ItemId.MEMORIALCRYSTAL_CUBE_BALANCE
							&& pc.getLevel() >= 40) {
						SpellBook6(pc, useItem);
					} else {
						pc.sendPackets(new S_ServerMessage(312));
					}
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
			}
			L1ItemDelay.onItemUse(pc, useItem); // 아이템 지연 개시
		}
	}

	private void useSpellBook(L1PcInstance pc, L1ItemInstance item, int itemId) {
		int itemAttr = 0;
		int locAttr = 0; // 0:other 1:law 2:chaos
		boolean isLawful = true;
		int pcX = pc.getX();
		int pcY = pc.getY();
		int mapId = pc.getMapId();
		int level = pc.getLevel();
		if (itemId == 45000 || itemId == 45008 || itemId == 45018
				|| itemId == 45021 || itemId == 40171 || itemId == 40179
				|| itemId == 40180 || itemId == 40182 || itemId == 40194
				|| itemId == 40197 || itemId == 40202 || itemId == 40206
				|| itemId == 40213 || itemId == 40220 || itemId == 40222) {
			itemAttr = 1;
		}
		if (itemId == 45009 || itemId == 45010 || itemId == 45019
				|| itemId == 40172 || itemId == 40173 || itemId == 40178
				|| itemId == 40185 || itemId == 40186 || itemId == 40192
				|| itemId == 40196 || itemId == 40201 || itemId == 40204
				|| itemId == 40211 || itemId == 40221 || itemId == 40225) {
			itemAttr = 2;
		}
		if (pcX > 33116 && pcX < 33128 && pcY > 32930 && pcY < 32942
				&& mapId == 4 || pcX > 33135 && pcX < 33147 && pcY > 32235
				&& pcY < 32247 && mapId == 4 || pcX >= 32783 && pcX <= 32803
				&& pcY >= 32831 && pcY <= 32851 && mapId == 77) {
			locAttr = 1;
			isLawful = true;
		}
		if (pcX > 32880 && pcX < 32892 && pcY > 32646 && pcY < 32658
				&& mapId == 4 || pcX > 32662 && pcX < 32674 && pcY > 32297
				&& pcY < 32309 && mapId == 4) {
			locAttr = 2;
			isLawful = false;
		}
		if (pc.isGm()) {
			SpellBook(pc, item, isLawful);
		} else if ((itemAttr == locAttr || itemAttr == 0) && locAttr != 0) {
			if (pc.isKnight()) {
				if (itemId >= 45000 && itemId <= 45007 && level >= 50) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 45000 && itemId <= 45007) {
					pc.sendPackets(new S_ServerMessage(312));
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
			} else if (pc.isCrown() || pc.isDarkelf()) {
				if (itemId >= 45000 && itemId <= 45007 && level >= 10) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 45008 && itemId <= 45015 && level >= 20) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 45008 && itemId <= 45015
						|| itemId >= 45000 && itemId <= 45007) {
					pc.sendPackets(new S_ServerMessage(312));
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
			} else if (pc.isElf()) {
				if (itemId >= 45000 && itemId <= 45007 && level >= 8) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 45008 && itemId <= 45015 && level >= 16) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 45016 && itemId <= 45022 && level >= 24) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40170 && itemId <= 40177 && level >= 32) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40178 && itemId <= 40185 && level >= 40) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40186 && itemId <= 40193 && level >= 48) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 45000 && itemId <= 45022
						|| itemId >= 40170 && itemId <= 40193) {
					pc.sendPackets(new S_ServerMessage(312));
				} else {
					pc.sendPackets(new S_ServerMessage(79));
				}
			} else if (pc.isWizard()) {
				if (itemId >= 45000 && itemId <= 45007 && level >= 4) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 45008 && itemId <= 45015 && level >= 8) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 45016 && itemId <= 45022 && level >= 12) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40170 && itemId <= 40177 && level >= 16) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40178 && itemId <= 40185 && level >= 20) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40186 && itemId <= 40193 && level >= 24) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40194 && itemId <= 40201 && level >= 28) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40202 && itemId <= 40209 && level >= 32) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40210 && itemId <= 40217 && level >= 36) {
					SpellBook(pc, item, isLawful);
				} else if (itemId >= 40218 && itemId <= 40225 && level >= 40) {
					SpellBook(pc, item, isLawful);
				} else {
					pc.sendPackets(new S_ServerMessage(312));
				}
			}
		} else if (itemAttr != locAttr && itemAttr != 0 && locAttr != 0) {
			pc.sendPackets(new S_ServerMessage(79));
			S_SkillSound effect = new S_SkillSound(pc.getId(), 10);
			pc.sendPackets(effect);
			Broadcaster.broadcastPacket(pc, effect);
			pc.setCurrentHp(Math.max(pc.getCurrentHp() - 45, 0));
			if (pc.getCurrentHp() <= 0) {
				pc.death(null);
			}
			pc.getInventory().removeItem(item, 1);
		} else {
			pc.sendPackets(new S_ServerMessage(79));
		}
	}

	private void useElfSpellBook(L1PcInstance pc, L1ItemInstance item,
			int itemId) {
		int level = pc.getLevel();
		if ((pc.isElf() || pc.isGm()) && isLearnElfMagic(pc)) {
			if (itemId >= 40232 && itemId <= 40234 && level >= 10) {
				SpellBook2(pc, item);
			} else if (itemId >= 40235 && itemId <= 40236 && level >= 20) {
				SpellBook2(pc, item);
			}
			if (itemId >= 40237 && itemId <= 40240 && level >= 30) {
				SpellBook2(pc, item);
			} else if (itemId >= 40241 && itemId <= 40243 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId >= 40244 && itemId <= 40246 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId >= 40247 && itemId <= 40248 && level >= 30) {
				SpellBook2(pc, item);
			} else if (itemId >= 40249 && itemId <= 40250 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId >= 40251 && itemId <= 40252 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId == 40253 && level >= 30) {
				SpellBook2(pc, item);
			} else if (itemId == 40254 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId == 40255 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId == 40256 && level >= 30) {
				SpellBook2(pc, item);
			} else if (itemId == 40257 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId >= 40258 && itemId <= 40259 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId >= 40260 && itemId <= 40261 && level >= 30) {
				SpellBook2(pc, item);
			} else if (itemId == 40262 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId >= 40263 && itemId <= 40264 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId >= 41149 && itemId <= 41150 && level >= 50) {
				SpellBook2(pc, item);
			} else if (itemId == 41151 && level >= 40) {
				SpellBook2(pc, item);
			} else if (itemId >= 41152 && itemId <= 41153 && level >= 50) {
				SpellBook2(pc, item);
			}
		} else {
			pc.sendPackets(new S_ServerMessage(79));
		}
	}

	private boolean isLearnElfMagic(L1PcInstance pc) {
		int pcX = pc.getX();
		int pcY = pc.getY();
		int pcMapId = pc.getMapId();
		if (pcX >= 32786 && pcX <= 32797 && pcY >= 32842 && pcY <= 32859
				&& pcMapId == 75
				|| pc.getLocation().isInScreen(new Point(33055, 32336))
				&& pcMapId == 4) {
			return true;
		}
		return false;
	}

	private void SpellBook(L1PcInstance pc, L1ItemInstance item,
			boolean isLawful) {
		String s = "";
		int i = 0;
		int level1 = 0;
		int level2 = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int dk3 = 0;
		int bw1 = 0;
		int bw2 = 0;
		int bw3 = 0;
		L1Skills l1skills = null;
		for (int skillId = 1; skillId < 81; skillId++) {
			l1skills = SkillsTable.getInstance().getTemplate(skillId);
			String s1 = "마법서 (" + l1skills.getName() + ")";
			if (item.getItem().getName().equalsIgnoreCase(s1)) {
				int skillLevel = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (skillLevel) {
				case 1:
					level1 = i7;
					break;
				case 2:
					level2 = i7;
					break;
				case 3:
					l = i7;
					break;
				case 4:
					i1 = i7;
					break;
				case 5:
					j1 = i7;
					break;
				case 6:
					k1 = i7;
					break;
				case 7:
					l1 = i7;
					break;
				case 8:
					i2 = i7;
					break;
				case 9:
					j2 = i7;
					break;
				case 10:
					k2 = i7;
					break;
				case 11:
					l2 = i7;
					break;
				case 12:
					i3 = i7;
					break;
				case 13:
					j3 = i7;
					break;
				case 14:
					k3 = i7;
					break;
				case 15:
					l3 = i7;
					break;
				case 16:
					i4 = i7;
					break;
				case 17:
					j4 = i7;
					break;
				case 18:
					k4 = i7;
					break;
				case 19:
					l4 = i7;
					break;
				case 20:
					i5 = i7;
					break;
				case 21:
					j5 = i7;
					break;
				case 22:
					k5 = i7;
					break;
				case 23:
					l5 = i7;
					break;
				case 24:
					i6 = i7;
					break;
				case 25:
					dk3 = i7;
					break;
				case 26:
					bw1 = i7;
					break;
				case 27:
					bw2 = i7;
					break;
				case 28:
					bw3 = i7;
					break;
				}
			}
		}

		int objid = pc.getId();
		pc.sendPackets(new S_AddSkill(level1, level2, l, i1, j1, k1, l1, i2,
				j2, k2, l2, i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6,
				dk3, bw1, bw2, bw3));
		S_SkillSound s_skillSound = new S_SkillSound(objid, isLawful ? 224
				: 231);
		pc.sendPackets(s_skillSound);
		Broadcaster.broadcastPacket(pc, s_skillSound);
		SkillsTable.getInstance().spellMastery(objid, i, s, 0, 0);
		pc.getInventory().removeItem(item, 1);
	}

	private void SpellBook1(L1PcInstance pc, L1ItemInstance l1iteminstance) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int dk3 = 0;
		int bw1 = 0;
		int bw2 = 0;
		int bw3 = 0;
		L1Skills l1skills = null;
		for (int j6 = 97; j6 <= 112; j6++) {		
			l1skills = SkillsTable.getInstance().getTemplate(j6);
			String s1 = "흑정령의 수정 (" + l1skills.getName() + ")";
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1:
					j = i7;
					break;
				case 2:
					k = i7;
					break;
				case 3:
					l = i7;
					break;
				case 4:
					i1 = i7;
					break;
				case 5:
					j1 = i7;
					break;
				case 6:
					k1 = i7;
					break;
				case 7:
					l1 = i7;
					break;
				case 8:
					i2 = i7;
					break;
				case 9:
					j2 = i7;
					break;
				case 10:
					k2 = i7;
					break;
				case 11:
					l2 = i7;
					break;
				case 12:
					i3 = i7;
					break;
				case 13:
					j3 = i7;
					break;
				case 14:
					k3 = i7;
					break;
				case 15:
					l3 = i7;
					break;
				case 16:
					i4 = i7;
					break;
				case 17:
					j4 = i7;
					break;
				case 18:
					k4 = i7;
					break;
				case 19:
					l4 = i7;
					break;
				case 20:
					i5 = i7;
					break;
				case 21:
					j5 = i7;
					break;
				case 22:
					k5 = i7;
					break;
				case 23:
					l5 = i7;
					break;
				case 24:
					i6 = i7;
					break;
				case 25:
					dk3 = i7;
					break;
				case 26:
					bw1 = i7;
					break;
				case 27:
					bw2 = i7;
					break;
				case 28:
					bw3 = i7;
					break;
				}
			}
		}

		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, dk3, bw1,
				bw2, bw3));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 231);
		pc.sendPackets(s_skillSound);
		Broadcaster.broadcastPacket(pc, s_skillSound);
		SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}

	private void SpellBook2(L1PcInstance pc, L1ItemInstance l1iteminstance) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int dk3 = 0;
		int bw1 = 0;
		int bw2 = 0;
		int bw3 = 0;
		L1Skills l1skills = null;
		for (int j6 = 129; j6 <= 176; j6++) {
			l1skills = SkillsTable.getInstance().getTemplate(j6);
			String s1 = "정령의 수정 (" + l1skills.getName() + ")";
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				if (!pc.isGm() && l1skills.getAttr() != 0
						&& pc.getElfAttr() != l1skills.getAttr()) {
					if (pc.getElfAttr() == 0 || pc.getElfAttr() == 1
							|| pc.getElfAttr() == 2 || pc.getElfAttr() == 4
							|| pc.getElfAttr() == 8) { // 속성치가 이상한 경우는 전속성을 기억할
						// 수 있도록(듯이) 해 둔다
						pc.sendPackets(new S_ServerMessage(79));
						return;
					}
				}
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1:
					j = i7;
					break;
				case 2:
					k = i7;
					break;
				case 3:
					l = i7;
					break;
				case 4:
					i1 = i7;
					break;
				case 5:
					j1 = i7;
					break;
				case 6:
					k1 = i7;
					break;
				case 7:
					l1 = i7;
					break;
				case 8:
					i2 = i7;
					break;
				case 9:
					j2 = i7;
					break;
				case 10:
					k2 = i7;
					break;
				case 11:
					l2 = i7;
					break;
				case 12:
					i3 = i7;
					break;
				case 13:
					j3 = i7;
					break;
				case 14:
					k3 = i7;
					break;
				case 15:
					l3 = i7;
					break;
				case 16:
					i4 = i7;
					break;
				case 17:
					j4 = i7;
					break;
				case 18:
					k4 = i7;
					break;
				case 19:
					l4 = i7;
					break;
				case 20:
					i5 = i7;
					break;
				case 21:
					j5 = i7;
					break;
				case 22:
					k5 = i7;
					break;
				case 23:
					l5 = i7;
					break;
				case 24:
					i6 = i7;
					break;
				case 25:
					dk3 = i7;
					break;
				case 26:
					bw1 = i7;
					break;
				case 27:
					bw2 = i7;
					break;
				case 28:
					bw3 = i7;
					break;

				}
			}
		}

		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, dk3, bw1,
				bw2, bw3));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
		pc.sendPackets(s_skillSound);
		Broadcaster.broadcastPacket(pc, s_skillSound);
		SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}

	private void SpellBook3(L1PcInstance pc, L1ItemInstance l1iteminstance) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int dk3 = 0;
		int bw1 = 0;
		int bw2 = 0;
		int bw3 = 0;
		L1Skills l1skills = null;
		for (int j6 = 87; j6 <= 91; j6++) {
			l1skills = SkillsTable.getInstance().getTemplate(j6);
			String s1 = (new StringBuilder()).append("기술서 (").append(
					l1skills.getName()).append(")").toString();
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1:
					j = i7;
					break;
				case 2:
					k = i7;
					break;
				case 3:
					l = i7;
					break;
				case 4:
					i1 = i7;
					break;
				case 5:
					j1 = i7;
					break;
				case 6:
					k1 = i7;
					break;
				case 7:
					l1 = i7;
					break;
				case 8:
					i2 = i7;
					break;
				case 9:
					j2 = i7;
					break;
				case 10:
					k2 = i7;
					break;
				case 11:
					l2 = i7;
					break;
				case 12:
					i3 = i7;
					break;
				case 13:
					j3 = i7;
					break;
				case 14:
					k3 = i7;
					break;
				case 15:
					l3 = i7;
					break;
				case 16:
					i4 = i7;
					break;
				case 17:
					j4 = i7;
					break;
				case 18:
					k4 = i7;
					break;
				case 19:
					l4 = i7;
					break;
				case 20:
					i5 = i7;
					break;
				case 21:
					j5 = i7;
					break;
				case 22:
					k5 = i7;
					break;
				case 23:
					l5 = i7;
					break;
				case 24:
					i6 = i7;
					break;
				case 25:
					dk3 = i7;
					break;
				case 26:
					bw1 = i7;
					break;
				case 27:
					bw2 = i7;
					break;
				case 28:
					bw3 = i7;
					break;

				}
			}
		}

		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, dk3, bw1,
				bw2, bw3));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
		pc.sendPackets(s_skillSound);
		Broadcaster.broadcastPacket(pc, s_skillSound);
		SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}

	private void SpellBook4(L1PcInstance pc, L1ItemInstance l1iteminstance) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int dk3 = 0;
		int bw1 = 0;
		int bw2 = 0;
		int bw3 = 0;
		L1Skills l1skills = null;
		for (int j6 = 113; j6 < 121; j6++) {
			l1skills = SkillsTable.getInstance().getTemplate(j6);
			String s1 = "마법서 (" + l1skills.getName() + ")";
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1:
					j = i7;
					break;
				case 2:
					k = i7;
					break;
				case 3:
					l = i7;
					break;
				case 4:
					i1 = i7;
					break;
				case 5:
					j1 = i7;
					break;
				case 6:
					k1 = i7;
					break;
				case 7:
					l1 = i7;
					break;
				case 8:
					i2 = i7;
					break;
				case 9:
					j2 = i7;
					break;
				case 10:
					k2 = i7;
					break;
				case 11:
					l2 = i7;
					break;
				case 12:
					i3 = i7;
					break;
				case 13:
					j3 = i7;
					break;
				case 14:
					k3 = i7;
					break;
				case 15:
					l3 = i7;
					break;
				case 16:
					i4 = i7;
					break;
				case 17:
					j4 = i7;
					break;
				case 18:
					k4 = i7;
					break;
				case 19:
					l4 = i7;
					break;
				case 20:
					i5 = i7;
					break;
				case 21:
					j5 = i7;
					break;
				case 22:
					k5 = i7;
					break;
				case 23:
					l5 = i7;
					break;
				case 24:
					i6 = i7;
					break;
				case 25:
					dk3 = i7;
					break;
				case 26:
					bw1 = i7;
					break;
				case 27:
					bw2 = i7;
					break;
				case 28:
					bw3 = i7;
					break;

				}
			}
		}

		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, dk3, bw1,
				bw2, bw3));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
		pc.sendPackets(s_skillSound);
		Broadcaster.broadcastPacket(pc, s_skillSound);
		SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}

	private void SpellBook5(L1PcInstance pc, L1ItemInstance l1iteminstance) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int dk3 = 0;
		int bw1 = 0;
		int bw2 = 0;
		int bw3 = 0;
		L1Skills l1skills = null;
		for (int j6 = 181; j6 < 200; j6++) {
			l1skills = SkillsTable.getInstance().getTemplate(j6);
			String s1 = "용기사의 서판(" + l1skills.getName() + ")";
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1:
					j = i7;
					break;
				case 2:
					k = i7;
					break;
				case 3:
					l = i7;
					break;
				case 4:
					i1 = i7;
					break;
				case 5:
					j1 = i7;
					break;
				case 6:
					k1 = i7;
					break;
				case 7:
					l1 = i7;
					break;
				case 8:
					i2 = i7;
					break;
				case 9:
					j2 = i7;
					break;
				case 10:
					k2 = i7;
					break;
				case 11:
					l2 = i7;
					break;
				case 12:
					i3 = i7;
					break;
				case 13:
					j3 = i7;
					break;
				case 14:
					k3 = i7;
					break;
				case 15:
					l3 = i7;
					break;
				case 16:
					i4 = i7;
					break;
				case 17:
					j4 = i7;
					break;
				case 18:
					k4 = i7;
					break;
				case 19:
					l4 = i7;
					break;
				case 20:
					i5 = i7;
					break;
				case 21:
					j5 = i7;
					break;
				case 22:
					k5 = i7;
					break;
				case 23:
					l5 = i7;
					break;
				case 24:
					i6 = i7;
					break;
				case 25:
					dk3 = i7;
					break;
				case 26:
					bw1 = i7;
					break;
				case 27:
					bw2 = i7;
					break;
				case 28:
					bw3 = i7;
					break;

				}
			}
		}

		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, dk3, bw1,
				bw2, bw3));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
		pc.sendPackets(s_skillSound);
		Broadcaster.broadcastPacket(pc, s_skillSound);
		SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}

	private void SpellBook6(L1PcInstance pc, L1ItemInstance l1iteminstance) {
		String s = "";
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		int i1 = 0;
		int j1 = 0;
		int k1 = 0;
		int l1 = 0;
		int i2 = 0;
		int j2 = 0;
		int k2 = 0;
		int l2 = 0;
		int i3 = 0;
		int j3 = 0;
		int k3 = 0;
		int l3 = 0;
		int i4 = 0;
		int j4 = 0;
		int k4 = 0;
		int l4 = 0;
		int i5 = 0;
		int j5 = 0;
		int k5 = 0;
		int l5 = 0;
		int i6 = 0;
		int dk3 = 0;
		int bw1 = 0;
		int bw2 = 0;
		int bw3 = 0;
		for (int j6 = 201; j6 < 224; j6++) {
			L1Skills l1skills = SkillsTable.getInstance().getTemplate(j6);
			String s1 = "기억의 수정(" + l1skills.getName() + ")";
			if (l1iteminstance.getItem().getName().equalsIgnoreCase(s1)) {
				int l6 = l1skills.getSkillLevel();
				int i7 = l1skills.getId();
				s = l1skills.getName();
				i = l1skills.getSkillId();
				switch (l6) {
				case 1:
					j = i7;
					break;
				case 2:
					k = i7;
					break;
				case 3:
					l = i7;
					break;
				case 4:
					i1 = i7;
					break;
				case 5:
					j1 = i7;
					break;
				case 6:
					k1 = i7;
					break;
				case 7:
					l1 = i7;
					break;
				case 8:
					i2 = i7;
					break;
				case 9:
					j2 = i7;
					break;
				case 10:
					k2 = i7;
					break;
				case 11:
					l2 = i7;
					break;
				case 12:
					i3 = i7;
					break;
				case 13:
					j3 = i7;
					break;
				case 14:
					k3 = i7;
					break;
				case 15:
					l3 = i7;
					break;
				case 16:
					i4 = i7;
					break;
				case 17:
					j4 = i7;
					break;
				case 18:
					k4 = i7;
					break;
				case 19:
					l4 = i7;
					break;
				case 20:
					i5 = i7;
					break;
				case 21:
					j5 = i7;
					break;
				case 22:
					k5 = i7;
					break;
				case 23:
					l5 = i7;
					break;
				case 24:
					i6 = i7;
					break;
				case 25:
					dk3 = i7;
					break;
				case 26:
					bw1 = i7;
					break;
				case 27:
					bw2 = i7;
					break;
				case 28:
					bw3 = i7;
					break;

				}
			}
		}

		int k6 = pc.getId();
		pc.sendPackets(new S_AddSkill(j, k, l, i1, j1, k1, l1, i2, j2, k2, l2,
				i3, j3, k3, l3, i4, j4, k4, l4, i5, j5, k5, l5, i6, dk3, bw1,
				bw2, bw3));
		S_SkillSound s_skillSound = new S_SkillSound(k6, 224);
		pc.sendPackets(s_skillSound);
		Broadcaster.broadcastPacket(pc, s_skillSound);
		SkillsTable.getInstance().spellMastery(k6, i, s, 0, 0);
		pc.getInventory().removeItem(l1iteminstance, 1);
	}
}
