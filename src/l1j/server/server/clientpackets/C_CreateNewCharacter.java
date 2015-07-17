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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.server.BadNamesList;
import l1j.server.server.ObjectIdFactory;
import l1j.server.server.datatables.CharacterTable;
import l1j.server.server.datatables.SkillsTable;
import l1j.server.server.model.Beginner;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_AddSkill;
import l1j.server.server.serverpackets.S_CharCreateStatus;
import l1j.server.server.serverpackets.S_NewCharPacket;
import l1j.server.server.templates.L1Skills;
import server.LineageClient;
import server.controller.Robot.RobotAIThread;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket

public class C_CreateNewCharacter extends ClientBasePacket {
	private static Logger _log = Logger.getLogger(C_CreateNewCharacter.class
			.getName());
	private static final String C_OPCODE_CREATE_NEW_CHARACTER = "[C] C_CreateNewCharacter";

	public C_CreateNewCharacter(byte[] abyte0, LineageClient client)
			throws Exception {
		super(abyte0);
		String name = readS();
		L1PcInstance pc = new L1PcInstance();
		
		byte str, dex, con, intel, wis, cha;
		int total;
		if (name.length() > 50){
			System.out.println("패킷렉공격 ID :"+ name);
			System.out.println("패킷렉공격 IP :"+ client.getIp());
			client.kick();
			return;
		}
		for (int i = 0;i<name.length();i++) {  
			if (name.charAt(i) == 'ㄱ' || name.charAt(i) == 'ㄲ' || name.charAt(i) == 'ㄴ' || name.charAt(i) == 'ㄷ' ||    //한문자(char)단위로 비교.
					name.charAt(i) == 'ㄸ' || name.charAt(i) == 'ㄹ' || name.charAt(i) == 'ㅁ' || name.charAt(i) == 'ㅂ' ||    //한문자(char)단위로 비교 
					name.charAt(i) == 'ㅃ' || name.charAt(i) == 'ㅅ' || name.charAt(i) == 'ㅆ' || name.charAt(i) == 'ㅇ' ||    //한문자(char)단위로 비교
					name.charAt(i) == 'ㅈ' || name.charAt(i) == 'ㅉ' || name.charAt(i) == 'ㅊ' || name.charAt(i) == 'ㅋ' ||    //한문자(char)단위로 비교.
					name.charAt(i) == 'ㅌ' || name.charAt(i) == 'ㅍ' || name.charAt(i) == 'ㅎ' || name.charAt(i) == 'ㅛ' ||    //한문자(char)단위로 비교.
					name.charAt(i) == 'ㅕ' || name.charAt(i) == 'ㅑ' || name.charAt(i) == 'ㅐ' || name.charAt(i) == 'ㅔ' ||    //한문자(char)단위로 비교.
					name.charAt(i) == 'ㅗ' || name.charAt(i) == 'ㅓ' || name.charAt(i) == 'ㅏ' || name.charAt(i) == 'ㅣ' ||    //한문자(char)단위로 비교.
					name.charAt(i) == 'ㅠ' || name.charAt(i) == 'ㅜ' || name.charAt(i) == 'ㅡ' || name.charAt(i) == 'ㅒ' ||    //한문자(char)단위로 비교.
					name.charAt(i) == 'ㅖ' || name.charAt(i) == 'ㅢ' || name.charAt(i) == 'ㅟ' || name.charAt(i) == 'ㅝ' ||    //한문자(char)단위로 비교.
					name.charAt(i) == 'ㅞ' || name.charAt(i) == 'ㅙ' || name.charAt(i) == 'ㅚ' || name.charAt(i) == 'ㅘ' ||    //한문자(char)단위로 비교.
					name.charAt(i) == '씹' || name.charAt(i) == '좃' || name.charAt(i) == '좆' || name.charAt(i) == 'ㅤ'){
				S_CharCreateStatus s_charcreatestatus = new S_CharCreateStatus(S_CharCreateStatus.REASON_INVALID_NAME);
				client.sendPacket(s_charcreatestatus);
				return; 
			}
		}


		if (name.length() == 0) {
			S_CharCreateStatus s_charcreatestatus = new S_CharCreateStatus(
					S_CharCreateStatus.REASON_INVALID_NAME);
			client.sendPacket(s_charcreatestatus);
			return;
		}

		if (BadNamesList.getInstance().isBadName(name)) {
			S_CharCreateStatus s_charcreatestatus = new S_CharCreateStatus(
					S_CharCreateStatus.REASON_INVALID_NAME);
			_log.info("생성 금지된 캐릭터 이름, 생성실패");
			client.sendPacket(s_charcreatestatus);
			return;
		}
		if (isInvalidName(name)) {
			S_CharCreateStatus s_charcreatestatus = new S_CharCreateStatus(
					S_CharCreateStatus.REASON_INVALID_NAME);
			client.sendPacket(s_charcreatestatus);
			return;
		}

		if (CharacterTable.doesCharNameExist(name)) {
			_log.fine("charname: " + pc.getName() + " already exists. creation failed.");
			S_CharCreateStatus s_charcreatestatus1 = new S_CharCreateStatus(S_CharCreateStatus.REASON_ALREADY_EXSISTS);
			client.sendPacket(s_charcreatestatus1);
			return;
		}

		if (RobotAIThread.doesCharNameExist(name)) {
			_log.fine("charname: " + pc.getName() + " already exists. creation failed.");
			S_CharCreateStatus s_charcreatestatus1 = new S_CharCreateStatus(S_CharCreateStatus.REASON_ALREADY_EXSISTS);
			client.sendPacket(s_charcreatestatus1);
			return;
		}
		
		
		L1PcInstance player = L1World.getInstance().getPlayer(name);
		if (player != null) {
			_log.fine("charname: " + pc.getName() + " already exists. creation failed.");
			S_CharCreateStatus s_charcreatestatus1 = new S_CharCreateStatus(S_CharCreateStatus.REASON_ALREADY_EXSISTS);
			client.sendPacket(s_charcreatestatus1);
			return;
		}
		
		if (client.getAccount().countCharacters() >= 8) {
			_log.fine("account: " + client.getAccountName()
					+ " 8를 넘는 캐릭터 작성 요구. ");
			S_CharCreateStatus s_charcreatestatus1 = new S_CharCreateStatus(
					S_CharCreateStatus.REASON_WRONG_AMOUNT);
			client.sendPacket(s_charcreatestatus1);
			return;
		}
		
		try {
			Integer.parseInt(name);
			S_CharCreateStatus s_charcreatestatus = new S_CharCreateStatus(S_CharCreateStatus.REASON_INVALID_NAME);
			client.sendPacket(s_charcreatestatus);
			return;
		} catch (Exception e) {}

		pc.setName(name);
		pc.setType(readC());
		pc.set_sex(readC());

		if (pc.get_sex() == 0)
			pc.setClassId(MALE_LIST[pc.getType()]);
		else
			pc.setClassId(FEMALE_LIST[pc.getType()]);

		pc.setHighLevel(1);
		str = (byte) readC();
		dex = (byte) readC();
		con = (byte) readC();
		wis = (byte) readC();
		cha = (byte) readC();
		intel = (byte) readC();
		total = str + dex + con + wis + cha + intel;

		pc.getAbility().setBaseStr(str);
		pc.getAbility().setBaseDex(dex);
		pc.getAbility().setBaseCon(con);
		pc.getAbility().setBaseWis(wis);
		pc.getAbility().setBaseCha(cha);
		pc.getAbility().setBaseInt(intel);

		if (!pc.getAbility().isNormalAbility(pc.getClassId(), pc.getLevel(),
				pc.getHighLevel(), total)) {
			_log.finest("Character have wrong value");
			S_CharCreateStatus s_charcreatestatus3 = new S_CharCreateStatus(
					S_CharCreateStatus.REASON_WRONG_AMOUNT);
			client.sendPacket(s_charcreatestatus3);
			return;
		}

		_log.fine("charname: " + pc.getName() + " classId: " + pc.getClassId());
		S_CharCreateStatus s_charcreatestatus2 = new S_CharCreateStatus(
				S_CharCreateStatus.REASON_OK);
		client.sendPacket(s_charcreatestatus2);
		initNewChar(client, pc);
	}

	public static final int[] MALE_LIST = new int[] { 0, 61, 138, 734, 2786, 6658, 6671 };
	public static final int[] FEMALE_LIST = new int[] { 1, 48, 37, 1186, 2796, 6661, 6650 };
	
	
	public static final int[] START_LOC_X = new int [] { 32684, 32686, 32691 }; // 숨계 X 	
	public static final int[] START_LOC_Y = new int [] { 32864, 32867, 32870 }; // 숨계Y	



	private static void initNewChar(LineageClient client, L1PcInstance pc)
			throws IOException, Exception {
		short init_hp = 0, init_mp = 0;
		Random random = new Random();
		int startPos = random.nextInt(3);


		pc.setId(ObjectIdFactory.getInstance().nextId());

		if (pc.isCrown()) { // CROWN
			init_hp = 14;
			switch (pc.getAbility().getBaseWis()) {
			case 11:
				init_mp = 2;
				break;
			case 12:
			case 13:
			case 14:
			case 15:
				init_mp = 3;
				break;
			case 16:
			case 17:
			case 18:
				init_mp = 4;
				break;
			default:
				init_mp = 2;
				break;
			}
			//startPosType = HidingV;
		} else if (pc.isKnight()) { // KNIGHT
			init_hp = 16;
			switch (pc.getAbility().getBaseWis()) {
			case 9:
			case 10:
			case 11:
				init_mp = 1;
				break;
			case 12:
			case 13:
				init_mp = 2;
				break;
			default:
				init_mp = 1;
				break;
			}
		//	startPosType = HidingV;
		} else if (pc.isElf()) { // ELF
			init_hp = 15;
			switch (pc.getAbility().getBaseWis()) {
			case 12:
			case 13:
			case 14:
			case 15:
				init_mp = 4;
				break;
			case 16:
			case 17:
			case 18:
				init_mp = 6;
				break;
			default:
				init_mp = 4;
				break;
			}
		//	startPosType = HidingV;
		} else if (pc.isWizard()) { // WIZ
			init_hp = 12;
			switch (pc.getAbility().getBaseWis()) {
			case 12:
			case 13:
			case 14:
			case 15:
				init_mp = 6;
				break;
			case 16:
			case 17:
			case 18:
				init_mp = 8;
				break;
			default:
				init_mp = 6;
				break;
			}
			//startPosType = HidingV;
		} else if (pc.isDarkelf()) { // DE
			init_hp = 12;
			switch (pc.getAbility().getBaseWis()) {
			case 10:
			case 11:
				init_mp = 3;
				break;
			case 12:
			case 13:
			case 14:
			case 15:
				init_mp = 4;
				break;
			case 16:
			case 17:
			case 18:
				init_mp = 6;
				break;
			default:
				init_mp = 3;
				break;
			}
		//	startPosType = HidingV;
		} else if (pc.isDragonknight()) { // 용기사
			init_hp = 16;
			init_mp = 2;
		//	startPosType = HidingV;
		} else if (pc.isIllusionist()) { // 환술사
			init_hp = 14;
			switch (pc.getAbility().getBaseWis()) {
			case 12:
			case 13:
			case 14:
			case 15:
				init_mp = 5;
				break;
			case 16:
			case 17:
			case 18:
				init_mp = 6;
				break;
			default:
				init_mp = 5;
				break;
			}
			//startPosType = HidingV;
		}

		pc.setX(START_LOC_X[startPos]);
		pc.setY(START_LOC_Y[startPos]);
		pc.setMap((short) 2005);


		pc.getMoveState().setHeading(0);
		pc.setLawful(0);
		pc.addBaseMaxHp(init_hp);
		pc.setCurrentHp(init_hp);
		pc.addBaseMaxMp(init_mp);
		pc.setCurrentMp(init_mp);
		pc.resetBaseAc();
		pc.setTitle("\\fC " + Config.SERVER_NAME + " 서버");//서버이름으로 호칭 넣기
		pc.setClanid(0);
		pc.setClanRank(0);
		pc.set_food(39); // 17%
		pc.setAccessLevel((short) 0);
		pc.setGm(false);
		pc.setMonitor(false);
		pc.setGmInvis(false);
		pc.setExp(0);
		pc.setActionStatus(0);
		pc.setAccessLevel((short) 0);
		pc.setClanname("");
		pc.getAbility().setBonusAbility(0);
		pc.resetBaseMr();
		pc.setElfAttr(0);
		pc.set_PKcount(0);
		pc.setExpRes(0);
		pc.setPartnerId(0);
		pc.setOnlineStatus(0);
		pc.setHomeTownId(0);
		pc.setContribution(0);
		pc.setBanned(false);
		pc.setKarma(0);
		pc.setMark_count(60);
		pc.setReturnStat(0);
		pc.setGdungeonTime(0);//기감
		pc.setLdungeonTime(0);//라던업데이트
		pc.setTkddkdungeonTime(0); //상아탑업데이트
		pc.setDdungeonTime(0);//용던리뉴얼
		pc.setoptTime(0);//전초기지
		SimpleDateFormat s = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);

		pc.setBirthDay(Integer.parseInt(s.format(Calendar.getInstance()
				.getTime())));

		if (pc.isWizard()) { // WIZ
			pc.sendPackets(new S_AddSkill(3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			int object_id = pc.getId();
			L1Skills l1skills = SkillsTable.getInstance().getTemplate(4); // EB
			String skill_name = l1skills.getName();
			int skill_id = l1skills.getSkillId();
			SkillsTable.getInstance().spellMastery(object_id, skill_id,	skill_name, 0, 0); // DB에
			// 등록
		}
		if (pc.isElf()) { // 요정 텔리포터투마더 캐릭생성시 추가
			pc.sendPackets(new S_AddSkill(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0));
			int object_id = pc.getId();
			L1Skills l1skills = SkillsTable.getInstance().getTemplate(131); // 텔레포투마더
			String skill_name = l1skills.getName();
			int skill_id = l1skills.getSkillId();

			SkillsTable.getInstance().spellMastery(object_id, skill_id,
					skill_name, 0, 0); // DB에 등록
		}
		Beginner.getInstance().GiveItem(pc);
		Beginner.getInstance().writeBookmark(pc);
		pc.setAccountName(client.getAccountName());
		CharacterTable.getInstance().storeNewCharacter(pc);
		S_NewCharPacket s_newcharpacket = new S_NewCharPacket(pc);
		client.sendPacket(s_newcharpacket);
		pc.save();
		pc.refresh();
	}

	private static boolean isAlphaNumeric(String s) {
		if (s == null) {
			return false;
		}
		boolean flag = true;
		char ac[] = s.toCharArray();
		int i = 0;
		do {
			if (i >= ac.length) {
				break;
			}
			if (!Character.isLetterOrDigit(ac[i])) {
				flag = false;
				break;
			}
			i++;
		} while (true);
		return flag;
	}

	private static boolean isInvalidName(String name) {
		int numOfNameBytes = 0;
		try {
			numOfNameBytes = name.getBytes("EUC-KR").length;
		} catch (UnsupportedEncodingException e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
			return false;
		}

		if (isAlphaNumeric(name)) {
			return false;
		}

		// XXX - 본청의 사양과 동등한가 미확인
		// 전각 문자가 5 문자를 넘는지, 전체로 12바이트를 넘으면(자) 무효인 이름으로 한다
		if (5 < (numOfNameBytes - name.length()) || 12 < numOfNameBytes) {
			return false;
		}

		if (BadNamesList.getInstance().isBadName(name)) {
			return false;
		}
		return true;
	}

	@Override
	public String getType() {
		return C_OPCODE_CREATE_NEW_CHARACTER;
	}
}
