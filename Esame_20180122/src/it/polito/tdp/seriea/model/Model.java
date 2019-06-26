package it.polito.tdp.seriea.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import java.util.Map;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	private SerieADAO dao;
	 private Graph<Season, DefaultWeightedEdge> grafo;
	 private List<Season> stagioni;
	 private Map<Integer, Season> idMapSeason;
	 private List<Season> allstagioni;
	 private List<Season> camminoBest;
	 private int maxCammino;
	 
	public Model() {
		this.dao=new SerieADAO();
		idMapSeason=new TreeMap<Integer, Season>();
		allstagioni=dao.listAllSeasons(idMapSeason);
		
	}
	
	public List<Team> caricaSquadre(){
		
		return dao.listTeams();
		
	}
	
	public String punteggi(Team squadra) {
		String s="";
		Map<Integer, Integer> punteggi = dao.calcolaPunti(squadra);
		for(Integer i: punteggi.keySet()) {
			s+="Stagione "+i+"-> Punteggio complessivo "+punteggi.get(i)+"\n";
		}
		return s;
		
	}
	public void creaGrafo(Team Squadra) {
		this.grafo = new SimpleDirectedWeightedGraph<Season, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		stagioni=dao.stagioni(Squadra, idMapSeason);
		Graphs.addAllVertices(grafo, stagioni);
		System.out.println(grafo.vertexSet().toString());
		Map<Integer, Integer> punteggi = dao.calcolaPunti(Squadra);
		
		for(Season s: grafo.vertexSet()) {
			for(Season s1: grafo.vertexSet()) {
				if(!s.equals(s1) && !grafo.containsEdge(s, s1)) {
					if((punteggi.get(s.getSeason()))-(punteggi.get(s1.getSeason()))>0) {
						Graphs.addEdge(grafo, s1, s, punteggi.get(s.getSeason())-punteggi.get(s1.getSeason()));
						System.out.println(grafo.getEdgeWeight(grafo.getEdge(s1, s)));
					}
					else if(punteggi.get(s.getSeason())-punteggi.get(s1.getSeason())<0) {
						Graphs.addEdge(grafo, s, s1, punteggi.get(s1.getSeason())-punteggi.get(s.getSeason()));
					}
				}
			}
		}
		System.out.println(grafo.toString());
		
	}
	
	public String annataDoro() {
		int max=0;
		Season best=null;
		for(Season s: grafo.vertexSet()) {
			int somma=0;
			int differenza=0;
			
			for(DefaultWeightedEdge e: ((AbstractBaseGraph<Season, DefaultWeightedEdge>) grafo).incomingEdgesOf(s)) {
				somma+=grafo.getEdgeWeight(e);
			}
			for(DefaultWeightedEdge e: ((AbstractBaseGraph<Season, DefaultWeightedEdge>) grafo).outgoingEdgesOf(s)) {
				differenza+=grafo.getEdgeWeight(e);
			}
			int totale=somma-differenza;
			if(totale>=max) {
				max=totale;
				best=s;
			}
			
		}
		return "L'annata d'oro è "+best.getSeason()+", il relativo punteggio è  "+max+"\n";
	}
	
	public void CamminoVirtuoso(Team s) {
		String result="";
		//preparo le variabili utili alla ricorsione
		Map<Integer, Integer> punteggi = dao.calcolaPunti(s);
		this.camminoBest=new ArrayList<Season>();
		maxCammino=0;
	   List <Season> verticiOrdinati=new LinkedList<Season>();
	   for(Season v: grafo.vertexSet()) {
		   verticiOrdinati.add(v);
	   }
			  
	   Collections.sort(verticiOrdinati);
		
		
		
		List<Season> parziale = new ArrayList<Season>();
		
		//itero a livello 0
		parziale.add((verticiOrdinati.get(0)));
		int lunghezzaCammino=1;
		cerca(1, parziale, lunghezzaCammino, s, verticiOrdinati );
		parziale.remove(parziale.size()-1);
		
		
		
		
	}

	private void cerca(int i, List<Season> parziale, int lunghezzaCammino, Team s, List <Season> verticiOrdinati) {
		Map<Integer, Integer> punteggi = dao.calcolaPunti(s);
		boolean trovato=false;
		
		Season ultima=parziale.get(parziale.size()-1);
		for(Season st: verticiOrdinati) {
			
		if(i<=punteggi.size()) {
			
				if(punteggi.get(st.getSeason())>(punteggi.get(ultima.getSeason()))) {
					trovato=true;
				parziale.add(st);
				lunghezzaCammino++;
				cerca(i+1, parziale, lunghezzaCammino, s, verticiOrdinati );
				lunghezzaCammino--;
				parziale.remove(parziale.size()-1);
			}
				else {
					
				}
				
		
		}
		
	}
		//condizione di terminazione
		if(!trovato) {
		if(lunghezzaCammino>=maxCammino) {
			
			maxCammino=lunghezzaCammino;
			camminoBest=new ArrayList<>(parziale);
		}
		}
	

}
	public String stringCamminoVirtuoso(Team s) {
		 List <Season> verticiOrdinati=new LinkedList<Season>();
		   for(Season v: grafo.vertexSet()) {
			   verticiOrdinati.add(v);
		   }
				  
		   Collections.sort(verticiOrdinati);
		   if(camminoBest.size()>1) {
		if(camminoBest.get(1)!=verticiOrdinati.get(1)){
			camminoBest.remove(0);                     //elimino il primo elemento se non fa parte del cammino virtuoso, 
			                                           //dato che veniva aggiunto a prescindere.
		}
		   }
		Map<Integer, Integer> punteggi = dao.calcolaPunti(s);
		String result="";
		for(Season st: camminoBest) {
			result+="\nStagione "+st.getDescription()+" Punteggio: "+punteggi.get(st.getSeason());
		}
		return result;
		
	}
}
