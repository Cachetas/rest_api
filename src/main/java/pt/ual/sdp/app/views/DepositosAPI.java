package pt.ual.sdp.app.views;

import javax.json.Json;
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
import java.util.Map.Entry;

import pt.ual.sdp.app.models.Depositos;

@WebServlet("/depositos/*")
public class DepositosAPI extends HttpServlet {

	private static final long serialVersionUID = -4086347235337894665L;
	ResultSet resultado;
	String resposta;
	Depositos item = new Depositos();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		
		resposta = "Ocorreu um erro imprevisto.";
		String nome = null, quantidade = null, valor, chave;
		boolean chaveOk, valorOk;
		JsonReader jsonReader = Json.createReader(req.getReader());
		JsonObject obj = jsonReader.readObject();
		
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
        
		
		chaveOk = false;
		valorOk = false;

		for (Entry<String, JsonValue> element : obj.entrySet()) {
			
			chave = element.getKey();	
			valor = element.getValue().toString().replace("\"", "");
			
			
			if (chave.equals("Nome") || element.getKey().equals("nome")) {
				nome = valor;
				chaveOk = true;
				
			} else if (element.getKey().equals("Quantidade") || element.getKey().equals("quantidade")) {
				quantidade = valor;
				valorOk = true;

			} else {
				resposta = "A chave ''" + element.getKey() + "''nao e valida. ";
				resposta += ("Para /depositos a chave a utilizar devera ser: ''nome'' ou ''Nome'' e ''quantidade'' ou ''Quantidade'' apenas. ");
			}
			if (chaveOk == true && valorOk == true) {
				resposta = item.depositar(nome, Integer.valueOf(quantidade));
				
			} else {
				resposta = "Ocorreu um erro. ";
			}
		}
		
		System.out.println(resposta);
		jsonBuilder.add("Resposta", resposta);
		
		JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeObject(jsonBuilder.build());
        jsonWriter.close();
		jsonReader.close();		
	}


}