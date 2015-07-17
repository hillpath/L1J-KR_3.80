/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful ,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not , write to the Free Software
 * Foundation , Inc., 59 Temple Place - Suite 330, Boston , MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server.TimeController;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.model.Broadcaster;
import l1j.server.server.model.L1Object;
import l1j.server.server.model.L1World;
import l1j.server.server.model.Instance.L1NpcInstance;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.serverpackets.S_ChangeName;
import l1j.server.server.serverpackets.S_NpcChatPacket;
import l1j.server.server.serverpackets.S_SystemMessage;

public class ChatTimeController implements Runnable {

	private static ChatTimeController _instance;
	private static boolean msg = false;
	private static String[] RankName = new String[3];
	private static String[] ClassRankName = new String[21];
	private static boolean isRank = false;
	public static int chatquizs;
	public static int chatquiz;

	public static ChatTimeController getInstance() {
		if (_instance == null) {
			_instance = new ChatTimeController();
		}
		return _instance;
	}

	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(1000 * 60 * 3);
				//Thread.sleep(1000);
				StartChat();
			}
		} catch (Exception e1) {
		}
	}

	private Calendar getRealTime() {
		TimeZone _tz = TimeZone.getTimeZone(Config.TIME_ZONE);
		Calendar cal = Calendar.getInstance(_tz);
		return cal;
	}

	private void StartChat() {
		SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
		int nowtime = Integer.valueOf(sdf.format(getRealTime().getTime()));
		
		Connection con = null;
		Statement pstm = null;
		ResultSet rs = null;
		
		int chat = 1;
		
		if (nowtime % chat == 0) {
			for (L1PcInstance pc : L1World.getInstance().getAllPlayers()) {
				try {
					if (pc == null || pc.getNetConnection() == null || pc.isPrivateShop()) {						
						continue;
					}
					
					if (!pc.isPrivateShop() && !pc.noPlayerCK) {
						for (L1Object obj : L1World.getInstance().getVisibleObjects(pc, 20)) {
							if (obj instanceof L1NpcInstance) {
								L1NpcInstance npc = (L1NpcInstance) obj;
								
								if (npc.getNpcTemplate().get_npcId() == 7000066 && msg == false) {// 1위동상
									int i = 0;
									
									try {
										con = L1DatabaseFactory.getInstance().getConnection();
										pstm = con.createStatement();
										rs = pstm.executeQuery("SELECT Exp, char_name FROM characters WHERE AccessLevel = 0 ORDER BY Exp DESC limit 3");
										while (rs.next()) {
											L1PcInstance player = (L1PcInstance) L1World.getInstance().getPlayer(rs.getString("char_name"));
											RankName[i] = rs.getString("char_name");											
											
											if (player != null && player.getLevel() >= 52 && player.getMapId() != 511) {
												switch (i) {
												case 0:
													/*if (player.getRanking() != 1) {
														if (!player.getInventory().checkItem(6000033, 1)) {
															for (L1ItemInstance item : player.getInventory().getItems()) {	
																if (item.getItemId() == 6000034 || item.getItemId() == 6000035) {
																	for (L1DollInstance doll : pc.getDollList().values()) {
																		if (doll.getNpcTemplate().get_npcId() == 6000034 || doll.getNpcTemplate().get_npcId() == 6000035) {
																			pc.removeDoll(doll);
																			doll.deleteDoll();
																		}
																	}
																	player.getInventory().removeItem(item);
																}
															}							
															player.getInventory().storeItem(6000033, 1);														*/	
															player.sendPackets(new S_SystemMessage("\\fV" + player.getName() + "님은 서버랭킹 1위 축하드립니다."));
															L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fV" + player.getName() + "님이 랭킹 1위가 되셨습니다."));
													//	}
														//player.setRanking(1);
														//player.setRankingCount(0);
													//}
													break;
												case 1:
													/*if (player.getRanking() != 2) {
														if (!player.getInventory().checkItem(6000034, 1)) {
															for (L1ItemInstance item : player.getInventory().getItems()) {	
																if (item.getItemId() == 6000033 || item.getItemId() == 6000035) {
																	for (L1DollInstance doll : pc.getDollList().values()) {
																		if (doll.getNpcTemplate().get_npcId() == 6000033 || doll.getNpcTemplate().get_npcId() == 6000035) {
																			pc.removeDoll(doll);
																			doll.deleteDoll();
																		}
																	}
																	player.getInventory().removeItem(item);
																}
															}	
															player.getInventory().storeItem(6000034, 1);*/															
															player.sendPackets(new S_SystemMessage("\\fV" + player.getName() + "님은 서버랭킹 2위 축하드립니다."));														
															L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fV" + player.getName() + "님이 랭킹 2위가 되셨습니다."));
														//}
														//player.setRanking(2);
														//player.setRankingCount(0);
													//}
													break;
												case 2:	
													/*if (player.getRanking() != 3) {
														if (!player.getInventory().checkItem(6000035, 1)) {
															for (L1ItemInstance item : player.getInventory().getItems()) {	
																if (item.getItemId() == 6000033 || item.getItemId() == 6000034) {
																	for (L1DollInstance doll : pc.getDollList().values()) {
																		if (doll.getNpcTemplate().get_npcId() == 6000033 || doll.getNpcTemplate().get_npcId() == 6000034) {
																			pc.removeDoll(doll);
																			doll.deleteDoll();
																		}
																	}
																	player.getInventory().removeItem(item);
																}
															}	
															player.getInventory().storeItem(6000035, 1);	*/														
															player.sendPackets(new S_SystemMessage("\\fV" + player.getName() + "님은 서버랭킹 3위 축하드립니다."));
															L1World.getInstance().broadcastPacketToAll(new S_SystemMessage("\\fV" + player.getName() + "님이 랭킹 3위가 되셨습니다."));
														//}
														//player.setRanking(3);
													//	player.setRankingCount(0);
													//}
													break;
												default:
													break;
												}
											}
											i++;	
										}										
										
										if (npc.getGfxId().getGfxId() == npc.getGfxId().getTempCharGfx()) {
											Broadcaster.broadcastPacket(npc, new S_ChangeName(npc.getId(), "\\fH" + Config.SERVER_NAME + " 서버 1위 [" + RankName[0] + "]"));
										}
										//Broadcaster.broadcastPacket(npc, new S_NpcChatPacket(npc, RankName[0] + " : 내가 제일 잘나가!!", 2));	
										  Broadcaster.broadcastPacket(npc, new S_NpcChatPacket(npc, "서버 전체랭킹 1위는 "+ RankName[0] +" 님입니다!!", 2));
										msg = true;
									} catch (Exception e) {
										System.out.println("Ranking Item Create Error");
									} finally {
										rs.close();
										pstm.close();
										con.close();
									}
								}
							}
						}
						
						if (pc.getLevel() >= 52) {
							for (int y = 0; y < RankName.length; y++) {
								if (pc.getName().equals(RankName[y])) {
									isRank = true;
									break;
								} else {
									isRank = false;
								}
							}
							if (!isRank) {
/*								for (L1ItemInstance item : pc.getInventory().getItems()) {	
									if (item.getItemId() == 6000033) {
										for (L1DollInstance doll : pc.getDollList().values()) {
											if (doll.getNpcTemplate().get_npcId() == 6000033) {
												pc.removeDoll(doll);
												doll.deleteDoll();
											}
										}
										pc.getInventory().removeItem(item);*/
										//pc.sendPackets(new S_SystemMessage("\\fV랭킹 1위에서 밀려 나셨습니다. 분발하세요."));
/*									} else if (item.getItemId() == 6000034) {
										for (L1DollInstance doll : pc.getDollList().values()) {
											if (doll.getNpcTemplate().get_npcId() == 6000034) {
												pc.removeDoll(doll);
												doll.deleteDoll();
											}
										}
										pc.getInventory().removeItem(item);*/
									//	pc.sendPackets(new S_SystemMessage("\\fV랭킹 2위에서 밀려 나셨습니다. 분발하세요."));
/*									} else if (item.getItemId() == 6000035) {
										for (L1DollInstance doll : pc.getDollList().values()) {
											if (doll.getNpcTemplate().get_npcId() == 6000035) {
												pc.removeDoll(doll);
												doll.deleteDoll();
											}
										}
										pc.getInventory().removeItem(item);*/
									//	pc.sendPackets(new S_SystemMessage("\\fV랭킹 3위에서 밀려 나셨습니다. 분발하세요."));
									}	
								}
							}
					//	}
					//}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
	
			msg = false;
		}
	}
}