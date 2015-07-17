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

import java.util.logging.Logger;

import l1j.server.server.datatables.NpcActionTable;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.npc.L1NpcHtml;
import l1j.server.server.model.npc.action.L1NpcAction;
import l1j.server.server.model.skill.L1SkillId;
import l1j.server.server.serverpackets.S_NPCTalkReturn;
import l1j.server.server.serverpackets.S_ServerMessage;
import server.LineageClient;

// Referenced classes of package l1j.server.server.clientpackets:
// ClientBasePacket, C_NPCTalk

public class C_NPCTalk extends ClientBasePacket {

 private static final String C_NPC_TALK = "[C] C_NPCTalk";

 private static Logger _log = Logger.getLogger(C_NPCTalk.class.getName());

 public C_NPCTalk(byte abyte0[], LineageClient client) throws Exception {
  super(abyte0);
  int objid = readD();
     L1Object obj = L1World.getInstance().findObject(objid);
     L1PcInstance pc = client.getActiveChar();
   int npcid = ((L1NpcInstance) obj).getNpcTemplate().get_npcId();
     switch (npcid) {
     case 4212015: // 안타
	if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGONBLOOD_ANTA)) {
	pc.sendPackets(new S_ServerMessage(1626)); 
	}else{
      L1Teleport.teleport(pc, 32681, 32672, (short) 1005, 5, true);
	}
      break;
     case 4212016: // 파푸
			if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGONBLOOD_PAP)) {
				pc.sendPackets(new S_ServerMessage(1626)); 
			}else{
     // L1PolyMorph.undoPoly(pc);
      L1Teleport.teleport(pc, 32859, 32797, (short) 1011, 5, true);
			}
      break;
     case 4212017: // 린드
			if (pc.getSkillEffectTimerSet().hasSkillEffect(L1SkillId.DRAGONBLOOD_RIND)) {
				pc.sendPackets(new S_ServerMessage(1626)); 
			}else{
      L1Teleport.teleport(pc, 32737, 32868, (short) 1017, 5, true);
			}
      break;
     case 4: // 발라
      L1Teleport.teleport(pc, 32666, 32794, (short) 1023, 5, true);
      break;
     default:
      break;
     }
     if (obj != null && pc != null) {
      L1NpcAction action = NpcActionTable.getInstance().get(pc, obj);
      if (action != null) {
       L1NpcHtml html = action.execute("", pc, obj, new byte[0]);
       if (html != null) {
        pc.sendPackets(new S_NPCTalkReturn(obj.getId(), html));
       }
       return;
      }
      obj.onTalkAction(pc);
  } else {
   _log.severe("오브젝트가 발견되지 않습니다 objid=" + objid);
  }
 }

 @Override
 public String getType() {
  return C_NPC_TALK;
 }
}
