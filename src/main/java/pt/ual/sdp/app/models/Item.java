package pt.ual.sdp.app.models;

import java.sql.ResultSet;
import java.util.Scanner;

public class Item extends PostgreSQL {

	String tabela, coluna, nome;

	int quantidade, selectCount, itemID;
	String sql, novoNome, resposta;
	ResultSet resultado;

	Scanner sc = new Scanner(System.in);

	public Item() {

	}

	public String novoItem(String nome) {

		tabela = "itens";
		coluna = "nome";
		selectCount = selectCount(tabela, coluna, nome);
		if (selectCount == 0 && !nome.equals("") && !nome.equals(" ")) {
			resposta = insertIntoItens(nome);

		} else if (nome.equals("") || nome.equals(" ")) {
			resposta = "Introduza um nome valido por favor.";
		} else {
			resposta = "O item ''" + nome + "'' ja existe.";
		}
		return resposta;
	}


	public ResultSet verItens() {
		resultado = selectItens();
		return resultado;
	}

	
	public ResultSet verStock() {

		resultado = selectItensStock();
		return resultado;
	}
	
	
	public String alterarDescricaoItem(String nome, String novaDescricao) {

		tabela = "itens";
		coluna = "nome";
		selectCount = selectCount(tabela, coluna, nome);

		if (selectCount != 0) {

			resposta = updateDescricaoItem(nome, novaDescricao);

		} else {
			resposta = ("O item " + nome + " nao existe. ");
		}
		return resposta;
	}


	public String apagarItem(String nome) {
		
		int selectCountDepositos, selectCountEntregas;
		tabela = "itens";
		coluna = "nome";
		selectCount = selectCount(tabela, coluna, nome);
		if (selectCount != 0) {
			
			tabela = "depositos";
			coluna = "id_item";
			itemID = selectIdItem(tabela, coluna, nome);
			
			selectCountDepositos = selectCountInt(tabela, coluna, Integer.valueOf(itemID));
			
			tabela = "listaItens";
			coluna = "ref_item_id";
			
			selectCountEntregas = selectCountInt(tabela, coluna, Integer.valueOf(itemID));
			
			if (selectCountDepositos == 0 && selectCountEntregas == 0) {
				
				resposta = deleteItem(nome, itemID);
			} else {
				resposta = "O item ''"+nome+"'' nao pode ser apagado. ";
			}
		} else {
			resposta = "O item ''"+nome+"'' nao existe. ";
		}
		return resposta;
	}

}
