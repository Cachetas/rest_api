package pt.ual.sdp.app.views;

import javax.json.Json;
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
import java.util.Map.Entry;

import pt.ual.sdp.app.models.Item;

@WebServlet("/itens/*")
public class ItensAPI extends HttpServlet {

	private static final long serialVersionUID = -4086347235337894665L;
	ResultSet resultado;
	String resposta;
	Item item = new Item();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		int quantidade = 0;
		String path = req.getPathInfo();
		
		if (path.equals("/stock")) {
			
			
			resultado = item.verStock();
			
			JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
			JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
			JsonObjectBuilder itemBuilder = Json.createObjectBuilder();
			try {
				while (resultado.next()) {
					quantidade = resultado.getInt("quantidadeDeposito") - resultado.getInt("quantidadeLista");
					if (quantidade > 0) {
						itemBuilder.add("Nome", resultado.getString("nome"));
						itemBuilder.add("Descricao", resultado.getString("descricao"));
						itemBuilder.add("Quantidade em armazem", quantidade);
						arrayBuilder.add(itemBuilder);
					}

				}
				jsonBuilder.add("itens", arrayBuilder);

			} catch (SQLException erroSelect) {
				System.out.println(erroSelect.getClass().getName() + ": " + erroSelect.getMessage());
			}

			JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
			jsonWriter.writeObject(jsonBuilder.build());
			jsonWriter.close();
			
		} else {
		
		resultado = item.verItens();
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		JsonObjectBuilder itemBuilder = Json.createObjectBuilder();
		try {
			while (resultado.next()) {
				
				quantidade = resultado.getInt("quantidadeDeposito") - resultado.getInt("quantidadeLista");
				itemBuilder.add("Nome", resultado.getString("nome"));
				itemBuilder.add("Descricao", resultado.getString("descricao"));
				itemBuilder.add("Quantidade", quantidade);
				arrayBuilder.add(itemBuilder);

			}
			jsonBuilder.add("itens", arrayBuilder);

		} catch (SQLException erroSelect) {
			System.out.println(erroSelect.getClass().getName() + ": " + erroSelect.getMessage());
		}

		JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
		jsonWriter.writeObject(jsonBuilder.build());
		jsonWriter.close();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		resp.setContentType("application/json");
		String nome;
		JsonReader jsonReader = Json.createReader(req.getReader());
		JsonObject obj = jsonReader.readObject();
		
		
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
       
		
		
		for (Entry<String, JsonValue> element : obj.entrySet()) {
			
			nome = element.getValue().toString().replace("\"", "");
			
			if (element.getKey().equals("Nome") || element.getKey().equals("nome")) {
				resposta = item.novoItem(nome);
				
				
				 jsonBuilder.add("Resposta", resposta);
				System.out.println(resposta);
			} else {
				
				resposta = "A chave ''" + element.getKey() + "''nao e valida. ";
				resposta += "Para /itens a chave a utilizar devera ser: ''nome'' ou ''Nome'' apenas.";
				jsonBuilder.add("Resposta", resposta);
				System.out.println("A chave ''" + element.getKey() + "''nao e valida.");
				System.out.println("Para /itens a chave a utilizar devera ser: ''nome'' ou ''Nome'' apenas.");
			}
		}
		
		JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();
		jsonReader.close();
		
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		String nome = null, descricao = null, chave;
		boolean nomeOk = false, descricaoOk = false;
		
		
		JsonReader jsonReader = Json.createReader(req.getReader());
		JsonObject obj = jsonReader.readObject();
		
		
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
       

		for (Entry<String, JsonValue> element : obj.entrySet()) {
			
			chave = element.getKey();
			
			
			if (chave.equals("Nome") || chave.equals("nome")) {
				nome = element.getValue().toString().replace("\"", "");
				nomeOk = true;
				
			
				
			} else if (element.getKey().equals("descricao") || element.getKey().equals("Descricao")) {
				descricao = element.getValue().toString().replace("\"", "");
				descricaoOk = true;
				
			}else {
				resposta = ("A chave ''" + element.getKey() + "''nao e valida. ");
				resposta += ("Para alterar a descricao de um item, apenas sao aceites como chave: ''nome'' e ''descricao'' apenas. ");
			}

		}
		if (nomeOk == true && descricaoOk == true) {
			resposta = item.alterarDescricaoItem(nome, descricao);
			
		} else {
			resposta += "Chave Nome ou descricao incorrectos. ";
		}
		jsonBuilder.add("Resposta", resposta);
		System.out.println(resposta);
		JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();
		jsonReader.close();
		
		
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		String nome;
		JsonReader jsonReader = Json.createReader(req.getReader());
		JsonObject obj = jsonReader.readObject();

		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		
		for (Entry<String, JsonValue> element : obj.entrySet()) {
			
			nome = element.getValue().toString().replace("\"", "");
			
			if (element.getKey().equals("Nome") || element.getKey().equals("nome")) {
				
				resposta = item.apagarItem(nome);
				System.out.println(resposta);
				
				jsonBuilder.add("Resposta", resposta);
				
			} else {
				
				resposta = ("A chave ''" + element.getKey() + "''nao e valida. ");
				resposta += "Para /itens a chave a utilizar devera ser: ''nome'' ou ''Nome'' apenas. ";
				System.out.println("A chave ''" + element.getKey() + "''nao e valida. ");
				System.out.println("Para /itens a chave a utilizar devera ser: ''nome'' ou ''Nome'' apenas. ");
			}

		}
		
		jsonBuilder.add("Resposta", resposta);
		JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();
		jsonReader.close();
		
	}

}
