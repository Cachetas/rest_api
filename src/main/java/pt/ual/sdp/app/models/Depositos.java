package pt.ual.sdp.app.models;

public class Depositos extends PostgreSQL{

	public String depositar(String nome, int quantidade) {

		String tabela, coluna, resposta;
		int selectCount, itemID;

		tabela = "itens";
		coluna = "nome";
		selectCount = selectCount(tabela, coluna, nome);

		if (selectCount != 0) {

			tabela = "itens";
			coluna = "item_id";
			itemID = selectIdItem(tabela, coluna, nome);

			resposta = insertIntoDepositar(quantidade, itemID);

		} else {
			resposta ="O item ''"+ nome + "'' nao existe.";
		}
		return resposta;
	}
}
