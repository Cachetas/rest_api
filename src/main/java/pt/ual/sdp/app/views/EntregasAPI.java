package pt.ual.sdp.app.views;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

import pt.ual.sdp.app.models.Entrega;

@WebServlet("/entregas/*")
public class EntregasAPI extends HttpServlet {

	private static final long serialVersionUID = -4086347235337894665L;
	ResultSet resultado;
	String resposta, chave, valor;
	Entrega entrega = new Entrega();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		
		
		
		
		int idEntregaAtual = 0, idEntrega;
		resultado = entrega.verEntregas();
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		JsonObjectBuilder itemBuilder = Json.createObjectBuilder();
		try {
			while (resultado.next()) {
				idEntrega = resultado.getInt("identrega");
				
				if (idEntregaAtual != idEntrega) {
					itemBuilder.add("ID de entrega", idEntrega);
					itemBuilder.add("data", resultado.getString("data_entrega"));
					itemBuilder.add("morada", resultado.getString("morada"));
					idEntregaAtual = idEntrega;
					arrayBuilder.add(itemBuilder);
					
				
				}
				
				itemBuilder.add("Nome", resultado.getString("nome"));
				itemBuilder.add("Quantidade", resultado.getInt("quantidade"));
				arrayBuilder.add(itemBuilder);

			}
			
			jsonBuilder.add("entrega", arrayBuilder);

		} catch (SQLException erroSelect) {
			System.out.println(erroSelect.getClass().getName() + ": " + erroSelect.getMessage());
		}

		JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
		jsonWriter.writeObject(jsonBuilder.build());
		jsonWriter.close();
		
	}
	

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType("application/json");
		
		String morada = null, data = null, item = null, quantidade = null;
		
		boolean dataOk = false, moradaOk = false, itemOk = false, quantidadeOk = false, cancelarEntrega = false;
		HashMap<String, String> listaItens = new HashMap<String, String>();
		JsonReader jsonReader = Json.createReader(req.getReader());
		JsonObject obj = jsonReader.readObject();
		
		JsonArray jsonArray = obj.getJsonArray("entrega");
		
		 JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		
		
		for (int i = 0; i < jsonArray.size(); i++) {
			obj = jsonArray.getJsonObject(i);
			
			for (Entry<String, JsonValue> element : obj.entrySet()) {
				
				quantidadeOk = false;
				
				chave = element.getKey();	
				valor = element.getValue().toString().replace("\"", "");
				
				
				if (!chave.equals(null) || !valor.equals(null) || !valor.equals("") || !valor.equals(" ")) {
					if(chave.equals("Data") || chave.equals("data")) {
						data = valor;
						dataOk = true;
						

					} else if(chave.equals("Morada") || chave.equals("morada")) {
						morada = valor;
						moradaOk = true;
						
					} else if (chave.equals("Nome") || chave.equals("nome")) {
						item = valor;
						itemOk = true;
						

					} else if (chave.equals("Quantidade") || chave.equals("quantidade")){
						quantidade = valor;
						quantidadeOk = true;
					
					}else {
						resposta = ("A chave ''" + element.getKey() + "''nao e valida. ");
						resposta += ("Para /entregas apenas sao aceites ''data'', ''morada'', ''nome'' ou ''quantidade'' como parametros/chaves. ");
						cancelarEntrega = true;
					}
				}
				
				if (itemOk == true && quantidadeOk == true && dataOk == true && moradaOk == true) {
					
					boolean pesquisa = false;
					for (Entry<String, String> kv : listaItens.entrySet()) {
						if (item.equals(kv.getKey())) {
							int somaQuantidade = Integer.valueOf(quantidade) + Integer.valueOf(kv.getValue());
							quantidade = String.valueOf(somaQuantidade);
							kv.setValue(quantidade);
							pesquisa = true;
						}
					}
					if (pesquisa == false) {
						
						
					listaItens.put(item, quantidade);
					} else {
						resposta += "Aviso: Foi encontrado o item " +item+ "repetido na entrega, a sua quantidade foi somada para: " + quantidade + " " ; 
					}
				} 
			}
		}
		
		if (cancelarEntrega == false) {
			resposta = entrega.novaEntrega(morada, data, listaItens);
			
		} else {
			cancelarEntrega = true;
			resposta += ("Entrega nao resgitada devido a incoerencias na informacao enviada. ");
		}

		System.out.println(resposta);
	
		
		jsonBuilder.add("Resposta", resposta);
		JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
	    jsonWriter.writeObject(jsonBuilder.build());
	    jsonWriter.close();
		jsonReader.close();
		
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType("application/json");
		String idEntrega = null, novaMorada = null, chave;
		boolean idEntregaOk = false, moradaOk = false;
		
		
		JsonReader jsonReader = Json.createReader(req.getReader());
		JsonObject obj = jsonReader.readObject();

		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		 
		for (Entry<String, JsonValue> element : obj.entrySet()) {
			
			chave = element.getKey();
			
			
			if (chave.equals("ID Entrega") || chave.equals("id entrega") || chave.equals("Id Entrega") ) {
				idEntrega = element.getValue().toString().replace("\"", "");
				idEntregaOk = true;
				
			
				
			} else if (element.getKey().equals("morada") || element.getKey().equals("Morada")) {
				novaMorada = element.getValue().toString().replace("\"", "");
				moradaOk = true;
				
			}else {
				resposta = ("A chave ''" + element.getKey() + "''nao e valida. ");
				resposta = ("Para alterar a descricao de um item, apenas sao aceites como chave: ''ID Entrega'' e ''Morada'' apenas. ");
			}

		}
		if (idEntregaOk == true && moradaOk == true) {
			resposta = entrega.alterarMorada(idEntrega, novaMorada);
			
		} else {
			resposta += "Chave Nome ou descricao incorrectos. ";
		}
		
		System.out.println(resposta);
		jsonBuilder.add("Resposta", resposta);
		JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
	    jsonWriter.writeObject(jsonBuilder.build());
	    jsonWriter.close();
		jsonReader.close();
		
	}

}