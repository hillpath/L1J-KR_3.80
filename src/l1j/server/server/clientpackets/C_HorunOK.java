package l1j.server.server.clientpackets;

import server.LineageClient;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_Disconnect;
import l1j.server.server.serverpackets.S_ShortOfMaterial;
import l1j.server.server.serverpackets.S_SkillSound;
import l1j.server.server.templates.L1Skills;

public class C_HorunOK extends ClientBasePacket {

	private static final String C_HORUN_OK = "[C] C_HorunOK";

	public C_HorunOK(byte abyte0[], LineageClient clientthread)
			throws Exception {
		super(abyte0);

		int count = readH();
		int sid[] = new int[count];
		int level1 = 0;
		int level2 = 0;
		int level3 = 0;
		int level1_cost = 0; // 아라크네의 거미줄10타래
		int level1_cost2 = 0; // 미스릴50개
		int level1_cost3 = 0; // 페어리더스트 100개
		int level1_cost4 = 0; // 판의갈기털 10뭉치
		int level2_cost = 0; // 미스릴실10개
		int level2_cost2 = 0; // 버섯포자의즙 8개
		int level2_cost3 = 0;// 엔트의껍질3개 (판의뿔은 없음)
		int level3_cost = 0; // 오리하루콘 45개
		int level3_cost2 = 0; // 아라크네의허물3개
		int level3_cost3 = 0; // 페어리의날개3개
		String skill_name = null;
		int skill_id = 0;
		/*
		 * <p>1레벨 마법을 배우는 것이라면 <font fg=ffffaf>아라크네의 거미줄 10타래</font>나 <font
		 * fg=ffffaf>미스릴 50개</font>, <font fg=ffffaf>페어리 더스트 100개</font>,
		 * <font fg=ffffaf>판의 갈기털 10뭉치</font> 중 한종류만 가져다 주시면 되요.</p> <p>2레벨
		 * 마법을 배우는 것이라면 <font fg=ffffaf>미스릴 실 10개</font>나 <font fg=ffffaf>버섯
		 * 포자의 즙 8개</font>, <font fg=ffffaf>판의 뿔 1개</font>, <font
		 * fg=ffffaf>엔트의 껍질 3개</font> 중 한종류만 가져다 주시면 되고요.</p> <p>3레벨 마법을 배우는
		 * 것이라면 <font fg=ffffaf>오리하루콘 45개</font>나 <font fg=ffffaf>아라크네의 허물 3개</font>,
		 * <font fg=ffffaf>페어리의 날개 3개</font>, <font fg=ffffaf>판의 뿔 3개</font> 중
		 * 한종류만 가져다 주시면 되지요.</p>
		 */
		L1PcInstance pc = clientthread.getActiveChar();
		if (pc == null) {
			return;
		}
		if (pc.isGhost()) {
			return;
		}
		for (int i = 0; i < count; i++) {
			sid[i] = readD();
			if (sid[i] > 24) {
				pc.sendPackets(new S_Disconnect());
				return;
			}

			switch (sid[i]) {
			// Lv1 마법
			case 0:
				level1 += 1;
				if (pc.getInventory().checkItem(40503, 10)) {
					level1_cost += 10;
					break;
				}
				if (pc.getInventory().checkItem(40494, 50)) {
					level1_cost2 += 50;
					break;
				}
				if (pc.getInventory().checkItem(40520, 100)) {
					level1_cost3 += 100;
					break;
				}
				if (pc.getInventory().checkItem(40519, 10)) {
					level1_cost4 += 10;
					break;
				}
				if (level1_cost == 0 && level1_cost2 == 0 && level1_cost3 == 0
						&& level1_cost4 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 1:
				level1 += 2;
				if (pc.getInventory().checkItem(40503, 10)) {
					level1_cost += 10;
					break;
				}
				if (pc.getInventory().checkItem(40494, 50)) {
					level1_cost2 += 50;
					break;
				}
				if (pc.getInventory().checkItem(40520, 100)) {
					level1_cost3 += 100;
					break;
				}
				if (pc.getInventory().checkItem(40519, 10)) {
					level1_cost4 += 10;
					break;
				}
				if (level1_cost == 0 && level1_cost2 == 0 && level1_cost3 == 0
						&& level1_cost4 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 2:
				level1 += 4;
				if (pc.getInventory().checkItem(40503, 10)) {
					level1_cost += 10;
					break;
				}
				if (pc.getInventory().checkItem(40494, 50)) {
					level1_cost2 += 50;
					break;
				}
				if (pc.getInventory().checkItem(40520, 100)) {
					level1_cost3 += 100;
					break;
				}
				if (pc.getInventory().checkItem(40519, 10)) {
					level1_cost4 += 10;
					break;
				}
				if (level1_cost == 0 && level1_cost2 == 0 && level1_cost3 == 0
						&& level1_cost4 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 3:
				level1 += 8;
				if (pc.getInventory().checkItem(40503, 10)) {
					level1_cost += 10;
					break;
				}
				if (pc.getInventory().checkItem(40494, 50)) {
					level1_cost2 += 50;
					break;
				}
				if (pc.getInventory().checkItem(40520, 100)) {
					level1_cost3 += 100;
					break;
				}
				if (pc.getInventory().checkItem(40519, 10)) {
					level1_cost4 += 10;
					break;
				}
				if (level1_cost == 0 && level1_cost2 == 0 && level1_cost3 == 0
						&& level1_cost4 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 4:
				level1 += 16;
				if (pc.getInventory().checkItem(40503, 10)) {
					level1_cost += 10;
					break;
				}
				if (pc.getInventory().checkItem(40494, 50)) {
					level1_cost2 += 50;
					break;
				}
				if (pc.getInventory().checkItem(40520, 100)) {
					level1_cost3 += 100;
					break;
				}
				if (pc.getInventory().checkItem(40519, 10)) {
					level1_cost4 += 10;
					break;
				}
				if (level1_cost == 0 && level1_cost2 == 0 && level1_cost3 == 0
						&& level1_cost4 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 5:
				level1 += 32;
				if (pc.getInventory().checkItem(40503, 10)) {
					level1_cost += 10;
					break;
				}
				if (pc.getInventory().checkItem(40494, 50)) {
					level1_cost2 += 50;
					break;
				}
				if (pc.getInventory().checkItem(40520, 100)) {
					level1_cost3 += 100;
					break;
				}
				if (pc.getInventory().checkItem(40519, 10)) {
					level1_cost4 += 10;
					break;
				}
				if (level1_cost == 0 && level1_cost2 == 0 && level1_cost3 == 0
						&& level1_cost4 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 6:
				level1 += 64;
				if (pc.getInventory().checkItem(40503, 10)) {
					level1_cost += 10;
					break;
				}
				if (pc.getInventory().checkItem(40494, 50)) {
					level1_cost2 += 50;
					break;
				}
				if (pc.getInventory().checkItem(40520, 100)) {
					level1_cost3 += 100;
					break;
				}
				if (pc.getInventory().checkItem(40519, 10)) {
					level1_cost4 += 10;
					break;
				}
				if (level1_cost == 0 && level1_cost2 == 0 && level1_cost3 == 0
						&& level1_cost4 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 7:
				level1 += 128;
				if (pc.getInventory().checkItem(40503, 10)) {
					level1_cost += 10;
					break;
				}
				if (pc.getInventory().checkItem(40494, 50)) {
					level1_cost2 += 50;
					break;
				}
				if (pc.getInventory().checkItem(40520, 100)) {
					level1_cost3 += 100;
					break;
				}
				if (pc.getInventory().checkItem(40519, 10)) {
					level1_cost4 += 10;
					break;
				}
				if (level1_cost == 0 && level1_cost2 == 0 && level1_cost3 == 0
						&& level1_cost4 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;

			// Lv2 마법
			case 8:
				level2 += 1;
				if (pc.getInventory().checkItem(40495, 10)) {
					level2_cost += 10;
					break;
				}
				if (pc.getInventory().checkItem(40499, 8)) {
					level2_cost2 += 8;
					break;
				}
				if (pc.getInventory().checkItem(40505, 3)) {
					level2_cost3 += 3;
					break;
				}
				if (level2_cost == 0 && level2_cost2 == 0 && level2_cost3 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 9:
				level2 += 2;
				if (pc.getInventory().checkItem(40495, 10)) {
					level2_cost += 10;
					break;
				}
				if (pc.getInventory().checkItem(40499, 8)) {
					level2_cost2 += 8;
					break;
				}
				if (pc.getInventory().checkItem(40505, 3)) {
					level2_cost3 += 3;
					break;
				}
				if (level2_cost == 0 && level2_cost2 == 0 && level2_cost3 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 10:
				level2 += 4;
				if (pc.getInventory().checkItem(40495, 10)) {
					level2_cost += 10;
					break;
				}
				if (pc.getInventory().checkItem(40499, 8)) {
					level2_cost2 += 8;
					break;
				}
				if (pc.getInventory().checkItem(40505, 3)) {
					level2_cost3 += 3;
					break;
				}
				if (level2_cost == 0 && level2_cost2 == 0 && level2_cost3 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 11:
				level2 += 8;
				if (pc.getInventory().checkItem(40495, 10)) {
					level2_cost += 10;
					break;
				}
				if (pc.getInventory().checkItem(40499, 8)) {
					level2_cost2 += 8;
					break;
				}
				if (pc.getInventory().checkItem(40505, 3)) {
					level2_cost3 += 3;
					break;
				}
				if (level2_cost == 0 && level2_cost2 == 0 && level2_cost3 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 12:
				level2 += 16;
				if (pc.getInventory().checkItem(40495, 10)) {
					level2_cost += 10;
					break;
				}
				if (pc.getInventory().checkItem(40499, 8)) {
					level2_cost2 += 8;
					break;
				}
				if (pc.getInventory().checkItem(40505, 3)) {
					level2_cost3 += 3;
					break;
				}
				if (level2_cost == 0 && level2_cost2 == 0 && level2_cost3 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 13:
				level2 += 32;
				if (pc.getInventory().checkItem(40495, 10)) {
					level2_cost += 10;
					break;
				}
				if (pc.getInventory().checkItem(40499, 8)) {
					level2_cost2 += 8;
					break;
				}
				if (pc.getInventory().checkItem(40505, 3)) {
					level2_cost3 += 3;
					break;
				}
				if (level2_cost == 0 && level2_cost2 == 0 && level2_cost3 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 14:
				level2 += 64;
				if (pc.getInventory().checkItem(40495, 10)) {
					level2_cost += 10;
					break;
				}
				if (pc.getInventory().checkItem(40499, 8)) {
					level2_cost2 += 8;
					break;
				}
				if (pc.getInventory().checkItem(40505, 3)) {
					level2_cost3 += 3;
					break;
				}
				if (level2_cost == 0 && level2_cost2 == 0 && level2_cost3 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 15:
				level2 += 128;
				if (pc.getInventory().checkItem(40495, 10)) {
					level2_cost += 10;
					break;
				}
				if (pc.getInventory().checkItem(40499, 8)) {
					level2_cost2 += 8;
					break;
				}
				if (pc.getInventory().checkItem(40505, 3)) {
					level2_cost3 += 3;
					break;
				}
				if (level2_cost == 0 && level2_cost2 == 0 && level2_cost3 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;

			// Lv3 마법
			case 16:
				level3 += 1;
				if (pc.getInventory().checkItem(40508, 45)) {
					level3_cost += 45;
					break;
				}
				if (pc.getInventory().checkItem(40504, 3)) {
					level3_cost2 += 3;
					break;
				}
				if (pc.getInventory().checkItem(40521, 3)) {
					level3_cost3 += 3;
					break;
				}
				if (level3_cost == 0 && level3_cost2 == 0 && level3_cost3 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 17:
				level3 += 2;
				if (pc.getInventory().checkItem(40508, 45)) {
					level3_cost += 45;
					break;
				}
				if (pc.getInventory().checkItem(40504, 3)) {
					level3_cost2 += 3;
					break;
				}
				if (pc.getInventory().checkItem(40521, 3)) {
					level3_cost3 += 3;
					break;
				}
				if (level3_cost == 0 && level3_cost2 == 0 && level3_cost3 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 18:
				level3 += 4;
				if (pc.getInventory().checkItem(40508, 45)) {
					level3_cost += 45;
					break;
				}
				if (pc.getInventory().checkItem(40504, 3)) {
					level3_cost2 += 3;
					break;
				}
				if (pc.getInventory().checkItem(40521, 3)) {
					level3_cost3 += 3;
					break;
				}
				if (level3_cost == 0 && level3_cost2 == 0 && level3_cost3 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 19:
				level3 += 8;
				if (pc.getInventory().checkItem(40508, 45)) {
					level3_cost += 45;
					break;
				}
				if (pc.getInventory().checkItem(40504, 3)) {
					level3_cost2 += 3;
					break;
				}
				if (pc.getInventory().checkItem(40521, 3)) {
					level3_cost3 += 3;
					break;
				}
				if (level3_cost == 0 && level3_cost2 == 0 && level3_cost3 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 20:
				level3 += 16;
				if (pc.getInventory().checkItem(40508, 45)) {
					level3_cost += 45;
					break;
				}
				if (pc.getInventory().checkItem(40504, 3)) {
					level3_cost2 += 3;
					break;
				}
				if (pc.getInventory().checkItem(40521, 3)) {
					level3_cost3 += 3;
					break;
				}
				if (level3_cost == 0 && level3_cost2 == 0 && level3_cost3 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 21:
				level3 += 32;
				if (pc.getInventory().checkItem(40508, 45)) {
					level3_cost += 45;
					break;
				}
				if (pc.getInventory().checkItem(40504, 3)) {
					level3_cost2 += 3;
					break;
				}
				if (pc.getInventory().checkItem(40521, 3)) {
					level3_cost3 += 3;
					break;
				}
				if (level3_cost == 0 && level3_cost2 == 0 && level3_cost3 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 22:
				level3 += 64;
				if (pc.getInventory().checkItem(40508, 45)) {
					level3_cost += 45;
					break;
				}
				if (pc.getInventory().checkItem(40504, 3)) {
					level3_cost2 += 3;
					break;
				}
				if (pc.getInventory().checkItem(40521, 3)) {
					level3_cost3 += 3;
					break;
				}
				if (level3_cost == 0 && level3_cost2 == 0 && level3_cost3 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;
			case 23:
				level3 += 128;
				if (pc.getInventory().checkItem(40508, 45)) {
					level3_cost += 45;
					break;
				}
				if (pc.getInventory().checkItem(40504, 3)) {
					level3_cost2 += 3;
					break;
				}
				if (pc.getInventory().checkItem(40521, 3)) {
					level3_cost3 += 3;
					break;
				}
				if (level3_cost == 0 && level3_cost2 == 0 && level3_cost3 == 0) {
					pc.sendPackets(new S_ShortOfMaterial(sid[i], pc));
					return;
				}
				break;

			default:
				break;
			}
		}

		switch (pc.getType()) {
		case 0: // 군주
			if (pc.getLevel() < 10) {
				level1 = 0;
				level2 = 0;
				level3 = 0;
				level1_cost = 0;
				level1_cost2 = 0;
				level1_cost3 = 0;
				level1_cost4 = 0;
				level2_cost = 0;
				level2_cost2 = 0;
				level2_cost3 = 0;
				level3_cost = 0;
				level3_cost2 = 0;
				level3_cost3 = 0;
			} else if (pc.getLevel() >= 10 && pc.getLevel() <= 19) {
				level2 = 0;
				level3 = 0;
				level2_cost = 0;
				level2_cost2 = 0;
				level2_cost3 = 0;
				level3_cost = 0;
				level3_cost2 = 0;
				level3_cost3 = 0;
			} else if (pc.getLevel() >= 20) {
				level3 = 0;
				level3_cost = 0;
				level3_cost2 = 0;
				level3_cost3 = 0;
			}
			break;

		case 1: // 기사
			if (pc.getLevel() < 50) {
				level1 = 0;
				level2 = 0;
				level3 = 0;
				level1_cost = 0;
				level1_cost2 = 0;
				level1_cost3 = 0;
				level1_cost4 = 0;
				level2_cost = 0;
				level2_cost2 = 0;
				level2_cost3 = 0;
				level3_cost = 0;
				level3_cost2 = 0;
				level3_cost3 = 0;
			} else if (pc.getLevel() >= 50) {
				level2 = 0;
				level3 = 0;
				level2_cost = 0;
				level2_cost2 = 0;
				level2_cost3 = 0;
				level3_cost = 0;
				level3_cost2 = 0;
				level3_cost3 = 0;
			}
			break;

		case 2: // ELF
			if (pc.getLevel() < 8) {
				level1 = 0;
				level2 = 0;
				level3 = 0;
				level1_cost = 0;
				level1_cost2 = 0;
				level1_cost3 = 0;
				level1_cost4 = 0;
				level2_cost = 0;
				level2_cost2 = 0;
				level2_cost3 = 0;
				level3_cost = 0;
				level3_cost2 = 0;
				level3_cost3 = 0;
			} else if (pc.getLevel() >= 8 && pc.getLevel() <= 15) {
				level2 = 0;
				level3 = 0;
				level2_cost = 0;
				level2_cost2 = 0;
				level2_cost3 = 0;
				level3_cost = 0;
				level3_cost2 = 0;
				level3_cost3 = 0;
			} else if (pc.getLevel() >= 16 && pc.getLevel() <= 23) {
				level3 = 0;
				level3_cost = 0;
				level3_cost2 = 0;
				level3_cost3 = 0;
			}
			break;

		case 3: // WIZ
			if (pc.getLevel() < 4) {
				level1 = 0;
				level2 = 0;
				level3 = 0;
				level1_cost = 0;
				level1_cost2 = 0;
				level1_cost3 = 0;
				level1_cost4 = 0;
				level2_cost = 0;
				level2_cost2 = 0;
				level2_cost3 = 0;
				level3_cost = 0;
				level3_cost2 = 0;
				level3_cost3 = 0;
			} else if (pc.getLevel() >= 4 && pc.getLevel() <= 7) {
				level2 = 0;
				level3 = 0;
				level2_cost = 0;
				level2_cost2 = 0;
				level2_cost3 = 0;
				level3_cost = 0;
				level3_cost2 = 0;
				level3_cost3 = 0;
			} else if (pc.getLevel() >= 8 && pc.getLevel() <= 11) {
				level3 = 0;
				level3_cost = 0;
				level3_cost2 = 0;
				level3_cost3 = 0;
			}
			break;

		case 4: // DE
			if (pc.getLevel() < 12) {
				level1 = 0;
				level2 = 0;
				level3 = 0;
				level1_cost = 0;
				level1_cost2 = 0;
				level1_cost3 = 0;
				level1_cost4 = 0;
				level2_cost = 0;
				level2_cost2 = 0;
				level2_cost3 = 0;
				level3_cost = 0;
				level3_cost2 = 0;
				level3_cost3 = 0;
			} else if (pc.getLevel() >= 12 && pc.getLevel() <= 23) {
				level2 = 0;
				level3 = 0;
				level2_cost = 0;
				level2_cost2 = 0;
				level2_cost3 = 0;
				level3_cost = 0;
				level3_cost2 = 0;
				level3_cost3 = 0;
			} else if (pc.getLevel() >= 24) {
				level3 = 0;
				level3_cost = 0;
				level3_cost2 = 0;
				level3_cost3 = 0;
			}
			break;

		default:
			break;
		}

		if (level1 == 0 && level2 == 0 && level3 == 0) {
			return;
		}

		if (pc.getInventory().checkItem(40503, level1_cost)
				&& pc.getInventory().checkItem(40494, level1_cost2)
				&& pc.getInventory().checkItem(40520, level1_cost3)
				&& pc.getInventory().checkItem(40519, level1_cost4)
				&& pc.getInventory().checkItem(40495, level2_cost)
				&& pc.getInventory().checkItem(40499, level2_cost2)
				&& pc.getInventory().checkItem(40505, level2_cost3)
				&& pc.getInventory().checkItem(40508, level3_cost)
				&& pc.getInventory().checkItem(40504, level3_cost2)
				&& pc.getInventory().checkItem(40521, level3_cost3)) {
			pc.getInventory().consumeItem(40503, level1_cost);
			pc.getInventory().consumeItem(40494, level1_cost2);
			pc.getInventory().consumeItem(40520, level1_cost3);
			pc.getInventory().consumeItem(40519, level1_cost4);
			pc.getInventory().consumeItem(40495, level2_cost);
			pc.getInventory().consumeItem(40499, level2_cost2);
			pc.getInventory().consumeItem(40505, level2_cost3);
			pc.getInventory().consumeItem(40508, level3_cost);
			pc.getInventory().consumeItem(40504, level3_cost2);
			pc.getInventory().consumeItem(40521, level3_cost3);
			S_SkillSound s_skillSound = new S_SkillSound(pc.getId(), 224);
			pc.sendPackets(s_skillSound);
			Broadcaster.broadcastPacket(pc, s_skillSound);
			pc.sendPackets(new S_AddSkill(level1, level2, level3, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0));

			if ((level1 & 1) == 1) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(1);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level1 & 2) == 2) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(2);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level1 & 4) == 4) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(3);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level1 & 8) == 8) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(4);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level1 & 16) == 16) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(5);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level1 & 32) == 32) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(6);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level1 & 64) == 64) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(7);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level1 & 128) == 128) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(8);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}

			if ((level2 & 1) == 1) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(9);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level2 & 2) == 2) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(10);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level2 & 4) == 4) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(11);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level2 & 8) == 8) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(12);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level2 & 16) == 16) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(13);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level2 & 32) == 32) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(14);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level2 & 64) == 64) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(15);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level2 & 128) == 128) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(16);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}

			if ((level3 & 1) == 1) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(17);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level3 & 2) == 2) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(18);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level3 & 4) == 4) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(19);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level3 & 8) == 8) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(20);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level3 & 16) == 16) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(21);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level3 & 32) == 32) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(22);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
			if ((level3 & 64) == 64) {
				L1Skills l1skills = SkillsTable.getInstance().getTemplate(23);
				skill_name = l1skills.getName();
				skill_id = l1skills.getSkillId();
				SkillsTable.getInstance().spellMastery(pc.getId(), skill_id,
						skill_name, 0, 0);
			}
		} else {
			pc.sendPackets(new S_ShortOfMaterial(skill_id, pc));
		}
	}

	@Override
	public String getType() {
		return C_HORUN_OK;
	}

}
