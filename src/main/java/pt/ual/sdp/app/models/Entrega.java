package pt.ual.sdp.app.models;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;

public class Entrega extends PostgreSQL {

	String item, nome, tabela, coluna, data, rua, numPorta, andar, codPostal, localidade, distrito;
	int quantidadeEmStock, quantidadeEntrega, selectCount;
	String morada;

	Scanner sc = new Scanner(System.in);

	public Entrega() {

	}

	public String novaEntrega(String morada, String data, HashMap<String, String> listaItens) {

		boolean cancelarEntrega = false;	
		
		
		
		int selectCountMoradaData = selectCountMoradaData(morada, data);
		if (selectCountMoradaData == 0) {

			for (Entry<String, String> key : listaItens.entrySet()) {

				nome =  key.getKey();
				quantidadeEntrega = Integer.valueOf(key.getValue());
				tabela = "itens";
				coluna = "nome";
				selectCount = selectCount(tabela, coluna, nome);
				
				if (selectCount != 0 && quantidadeEntrega != 0) {
					quantidadeEmStock = selectQuantidadeEmStock(nome);
					
					if (quantidadeEntrega <= quantidadeEmStock) {
						
					} else if (quantidadeEntrega > quantidadeEmStock){
						resposta = "Quantidade " + key.getValue()+ " indisponivel para o item: " + key.getKey() + " ";
						cancelarEntrega = true;
					}
				} else {
					resposta = "O item " +nome+ " nao existe. ";
					cancelarEntrega = true;
				}
			}
		
		} else {
			resposta = "Ja existe uma entrega para a morada: " +morada+ " na data " +data + " ";
			cancelarEntrega = true;
		}
		if (cancelarEntrega == false) {
			int idEntrega = insertIntoEntregas(morada, data);
			
			resposta = inserIntoListaItens(idEntrega, listaItens);
			
			resposta += "Entrega resgitada com ID: " + idEntrega + " ";
		}
	return resposta;
	}		
	

	public ResultSet verEntregas() {
		
		resultado = selectEntregas();
		return resultado;		
	}
	


	public String alterarMorada(String idEntrega, String novaMorada) {
				
		tabela = "entregas";
		coluna = "entrega_id";
		
		selectCount = selectCount(tabela, coluna, idEntrega);
	
		if (selectCount != 0) {
	
			resposta = updateMorada(idEntrega, novaMorada);
	
		} else {
			resposta = ("O item " + nome + " nao existe. ");
		}
		return resposta;
		
	}
}
