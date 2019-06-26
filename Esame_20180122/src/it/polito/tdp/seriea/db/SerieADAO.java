package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Team;

public class SerieADAO {

	public List<Season> listAllSeasons(Map<Integer, Season> idMap) {
		String sql = "SELECT season, description FROM seasons";
		List<Season> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Season s= new Season(res.getInt("season"), res.getString("description"));
				result.add(s);
				idMap.put(s.getSeason(), s);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public List<Team> listTeams() {
		String sql = "SELECT team FROM teams";
		List<Team> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Team(res.getString("team")));
			}

			conn.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public Map<Integer, Integer> calcolaPunti(Team squadra){
		String sql = "SELECT FTR, Season "+
				"FROM matches "+
				"WHERE HomeTeam=? OR AwayTeam=? AND HomeTeam>AwayTeam "+
				"ORDER BY Season ";
		Map<Integer, Integer> corrispondenze= new TreeMap<Integer, Integer>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, squadra.getTeam());
			st.setString(2, squadra.getTeam());
			ResultSet res = st.executeQuery();

			while (res.next()) {
				String risultato=res.getString("FTR");
				if(!corrispondenze.containsKey(res.getInt("Season"))) {
				
				if(risultato.equals("H")) {
					corrispondenze.put(res.getInt("Season"), 3);
				}
				else if(risultato.equals("D")) {
					corrispondenze.put(res.getInt("Season"), 1);
				} else {
					corrispondenze.put(res.getInt("Season"), 0);
				}
				}
				else {
					if(risultato.equals("H")) {
						corrispondenze.replace(res.getInt("Season"), corrispondenze.get(res.getInt("Season"))+3);
					}
					else if(risultato.equals("D")) {
						corrispondenze.replace(res.getInt("Season"), corrispondenze.get(res.getInt("Season"))+1);
					} 
					
				}
			}
 
			conn.close();
			return corrispondenze;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}
	public List<Season> stagioni(Team squadra, Map<Integer, Season> idMap){
		String sql = "SELECT DISTINCT m.season "+
				"FROM seasons s, matches m "+
				"WHERE s.season=m.season "+
				"AND m.HomeTeam=? OR m.AwayTeam=? "+
				"AND m.HomeTeam>m.AwayTeam "+
				"ORDER BY m.Season ";
		List<Season> stagioni= new LinkedList<Season>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, squadra.getTeam());
			st.setString(2, squadra.getTeam());
			ResultSet res = st.executeQuery();

			while (res.next()) {
				
				Season s=idMap.get(res.getInt("m.season"));
				
				stagioni.add(s);
				
			}
 
			conn.close();
			return stagioni;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
	}



}
