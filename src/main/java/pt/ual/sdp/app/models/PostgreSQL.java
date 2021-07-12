package pt.ual.sdp.app.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map.Entry;

public class PostgreSQL {

	Connection ligacao;
	Statement declaracao;
	ResultSet resultado;
	int count, quantidadeEmStock, itemID;
	String sql, erroSQL, select, resposta;
	boolean bdLigada = false, declaracaoAberta = false;

	protected void ligarBD() {

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException erroClass) {
			System.err.println(erroClass.getClass().getName() + ": " + erroClass.getMessage());
			System.exit(0);
		}
		try {
			ligacao = DriverManager.getConnection("jdbc:postgresql://postgres:5432/grupo6", "postgres", "12345678");
			bdLigada = true;
		} catch (SQLException erroDriver) {
			System.err.println(erroDriver.getClass().getName() + ": " + erroDriver.getMessage());
			System.exit(0);
		}

		try {
			declaracao = ligacao.createStatement();
			declaracaoAberta = true;
		} catch (SQLException erroDeclaracao) {
			System.err.println(erroDeclaracao.getClass().getName() + ": " + erroDeclaracao.getMessage());
			System.exit(0);
		}

	}

	protected void fecharDeclaracao() {

		try {
			declaracao.close();
			declaracaoAberta = false;
		} catch (SQLException erroDeclaracao) {
			System.err.println(erroDeclaracao.getClass().getName() + ": " + erroDeclaracao.getMessage());
			System.exit(0);
		}

	}

	protected void desligarBD() {

		try {
			ligacao.close();
			bdLigada = false;
		} catch (SQLException erroDesligarBD) {
			System.err.println(erroDesligarBD.getClass().getName() + ": " + erroDesligarBD.getMessage());
			System.exit(0);
		}
	}



	protected int selectIdItem(String tabela, String coluna, String atributo) {

		ligarBD();

		sql = "SELECT item_id AS item_id FROM itens WHERE nome = '" + atributo + "';";
		try {
			resultado = declaracao.executeQuery(sql);
			fecharDeclaracao();
			desligarBD();
			if (resultado.next()) {
				itemID = resultado.getInt("item_id");
			}
		} catch (SQLException erroSelectIdItem) {
			System.err.println(erroSelectIdItem.getClass().getName() + ": " + erroSelectIdItem.getMessage());
			System.exit(0);
		} finally {
			if (declaracaoAberta == true) {
				fecharDeclaracao();
			}
			if (bdLigada == true) {
				desligarBD();
			}
		}

		return itemID;
	}

	protected ResultSet selectItens() {

		ligarBD();

		sql = "SELECT itens.nome AS nome, itens.descricao AS descricao, " +
				"SUM(depositos.quantidade) AS quantidadeDeposito, " +
				"SUM(listaitens.quantidade_item) AS quantidadeLista " +
				"FROM itens " +
				"LEFT JOIN depositos " +
				"ON (depositos.id_item = itens.item_id) " +
				"LEFT JOIN listaitens " +
				"ON (listaitens.ref_item_id = itens.item_id) " +
				"GROUP By nome, descricao;";
		try {
			resultado = declaracao.executeQuery(sql);
			fecharDeclaracao();
			desligarBD();

		} catch (SQLException erroSelect) {
			System.err.println(erroSelect.getClass().getName() + ": " + erroSelect.getMessage());
			System.exit(0);
		} finally {
			if (declaracaoAberta == true) {
				fecharDeclaracao();
			}
			if (bdLigada == true) {
				desligarBD();
			}
		}
		return resultado;
	}

	protected ResultSet selectItensStock() {

		ligarBD();

		sql = "SELECT itens.nome AS nome, itens.descricao AS descricao, " +
				"SUM(depositos.quantidade) AS quantidadeDeposito, " +
				"SUM(listaitens.quantidade_item) AS quantidadeLista " +
				"FROM itens " +
				"LEFT JOIN depositos " +
				"ON (depositos.id_item = itens.item_id) " +
				"LEFT JOIN listaitens " +
				"ON (listaitens.ref_item_id = itens.item_id) " +
				"GROUP By nome, descricao;";

		try {
			resultado = declaracao.executeQuery(sql);
			fecharDeclaracao();
			desligarBD();

		} catch (SQLException erroSelect) {
			System.err.println(erroSelect.getClass().getName() + ": " + erroSelect.getMessage());
			System.exit(0);
		} finally {
			if (declaracaoAberta == true) {
				fecharDeclaracao();
			}
			if (bdLigada == true) {
				desligarBD();
			}
		}

		return resultado;
	}

	protected int selectCount(String tabela, String coluna, String atributo) {

		ligarBD();

		sql = "SELECT COUNT(" + coluna + ") AS total FROM " + tabela + " WHERE " + coluna + " = '" + atributo + "';";
		try {
			resultado = declaracao.executeQuery(sql);
			fecharDeclaracao();
			desligarBD();

			if (resultado.next()) {
				count = resultado.getInt("total");
			}
		} catch (SQLException erroCount) {
			System.err.println(erroCount.getClass().getName() + ": " + erroCount.getMessage());
			System.exit(0);
		} finally {
			if (declaracaoAberta == true) {
				fecharDeclaracao();
			}
			if (bdLigada == true) {
				desligarBD();
			}
		}

		return count;
	}
	
	protected int selectCountInt(String tabela, String coluna, int atributo) {

		ligarBD();

		sql = "SELECT COUNT(" + coluna + ") AS total FROM " + tabela + " WHERE " + coluna + " = " + atributo + ";";
		try {
			resultado = declaracao.executeQuery(sql);
			fecharDeclaracao();
			desligarBD();

			if (resultado.next()) {
				count = resultado.getInt("total");
			}
		} catch (SQLException erroCount) {
			System.err.println(erroCount.getClass().getName() + ": " + erroCount.getMessage());
			System.exit(0);
		} finally {
			if (declaracaoAberta == true) {
				fecharDeclaracao();
			}
			if (bdLigada == true) {
				desligarBD();
			}
		}

		return count;
	}

	
	protected ResultSet selectEntregas() {
		
		ligarBD();
		
		sql = "SELECT itens.nome AS nome, entregas.entrega_id AS identrega, " +
			  "entregas.morada AS morada,  " +
			  "entregas.data_entrega AS data_entrega, " +
			  "listaitens.quantidade_item AS quantidade " +
			  "FROM itens " +
			  "INNER JOIN listaitens " +
			  "ON (listaitens.ref_item_id = itens.item_id) " +
			  "INNER JOIN entregas " +
			  "ON (entregas.entrega_id = listaitens.ref_entrega_id) " +
			  "GROUP By identrega, nome, quantidade " +
			  "ORDER BY morada ASC;";
		try {
			resultado = declaracao.executeQuery(sql);
			fecharDeclaracao();
			desligarBD();

		} catch (SQLException erroSelect) {
			System.err.println(erroSelect.getClass().getName() + ": " + erroSelect.getMessage());
			System.exit(0);
		} finally {
			if (declaracaoAberta == true) {
				fecharDeclaracao();
			}
			if (bdLigada == true) {
				desligarBD();
			}
		}
		
		return resultado;
		
		
	}

	protected int selectQuantidadeEmStock(String nome) {

		String itemID = null;
		int quantidadeEmDeposito = 0, quantidadeEmEntregas = 0;
		ligarBD();
		sql = "SELECT item_id AS ID FROM itens WHERE nome = '" + nome + "';";

		try {
			resultado = declaracao.executeQuery(sql);

			if (resultado.next()) {
				itemID = resultado.getString("ID");
			}

			sql = "SELECT SUM(depositos.quantidade) AS quantidadeItem FROM depositos WHERE id_item = '" + itemID + "';";
			resultado = declaracao.executeQuery(sql);

			if (resultado.next()) {
				quantidadeEmDeposito = resultado.getInt("quantidadeItem");
			}

			sql = "SELECT SUM(listaItens.quantidade_item) AS quantidadeItemEmLista FROM listaItens WHERE ref_item_id = '"
					+ itemID + "';";

			resultado = declaracao.executeQuery(sql);
			fecharDeclaracao();
			desligarBD();

			if (resultado.next()) {
				quantidadeEmEntregas = resultado.getInt("quantidadeItemEmLista");
			}

		} catch (SQLException erro) {
			System.err.println(erro.getClass().getName() + ": " + erro.getMessage());
			System.exit(0);
		} finally {
			if (declaracaoAberta == true) {
				fecharDeclaracao();
			}
			if (bdLigada == true) {
				desligarBD();
			}
		}

		quantidadeEmStock = quantidadeEmDeposito - quantidadeEmEntregas;
		return quantidadeEmStock;

	}
	
	
	
	protected int selectCountMoradaData(String morada, String data) {
		ligarBD();

		sql = "SELECT COUNT(data_entrega) AS count FROM entregas WHERE data_entrega = '" + data + "' AND morada = '" + morada +"';";
		try {
			
			resultado = declaracao.executeQuery(sql);
			fecharDeclaracao();
			desligarBD();

			if (resultado.next()) {
				count = resultado.getInt("count");
			}
		} catch (SQLException erroCount) {
			System.err.println(erroCount.getClass().getName() + ": " + erroCount.getMessage());
			System.exit(0);
		} finally {
			if (declaracaoAberta == true) {
				fecharDeclaracao();
			}
			if (bdLigada == true) {
				desligarBD();
			}
		}
		return count;
		
	}

	protected String insertIntoItens(String atributo) {

		ligarBD();

		sql = "INSERT INTO itens (nome, descricao) " + "VALUES('" + atributo + "', 'Sem descricao registada');";

		try {
			declaracao.executeUpdate(sql);
			fecharDeclaracao();
			desligarBD();
			resposta = "Item ''" + atributo + "'' introduzido.";
		} catch (SQLException erroInsert) {
			System.err.println(erroInsert.getClass().getName() + ": " + erroInsert.getMessage());
			System.exit(0);
		} finally {
			if (declaracaoAberta == true) {
				fecharDeclaracao();
			}
			if (bdLigada == true) {
				desligarBD();
			}
		}

		return resposta;
	}

	protected String insertIntoDepositar(int quantidade, int ID_Item) {

		ligarBD();

		sql = "INSERT INTO depositos(quantidade, id_item) " + "VALUES(" + quantidade + ", " + ID_Item + ");";

		try {
			declaracao.executeUpdate(sql);
			resposta = "Deposito efectuado.";
			fecharDeclaracao();
			desligarBD();
		} catch (SQLException erroInsert) {
			System.err.println(erroInsert.getClass().getName() + ": " + erroInsert.getMessage());
			System.exit(0);
		} finally {
			if (declaracaoAberta == true) {
				fecharDeclaracao();
			}
			if (bdLigada == true) {
				desligarBD();
			}
		}
		return resposta;
	}

	protected int insertIntoEntregas(String morada, String data) {
		int idEntrega = 0;

		ligarBD();
		sql = "INSERT INTO entregas(morada, data_entrega)" + "VALUES('" + morada + "', '" + data + "');";
		try {
			declaracao.executeUpdate(sql);

			sql = "SELECT entrega_id AS ID FROM entregas WHERE morada = '" + morada + "' AND data_entrega = '" + data
					+ "';";

			resultado = declaracao.executeQuery(sql);
			fecharDeclaracao();
			desligarBD();
			if (resultado.next()) {
				idEntrega = resultado.getInt("ID");
			}
		} catch (SQLException erro) {
			System.err.println(erro.getClass().getName() + ": " + erro.getMessage());
			System.exit(0);
		} finally {
			if (declaracaoAberta == true) {
				fecharDeclaracao();
			}
			if (bdLigada == true) {
				desligarBD();
			}
		}

		return idEntrega;
	}

	protected String inserIntoListaItens(int idEntrega, HashMap<String, String> listaItens) {
		int item_id = 0;

		
		for (Entry<String, String> key : listaItens.entrySet()) {
			ligarBD();
			sql = "SELECT item_id AS ID FROM itens WHERE nome = '" + key.getKey() + "'";
			try {
				resultado = declaracao.executeQuery(sql);

				if (resultado.next()) {
					item_id = resultado.getInt("ID");
				}

				sql = "INSERT INTO listaItens(ref_entrega_id, ref_item_id, quantidade_item) " + "VALUES(" + idEntrega
						+ ", " + item_id + ", " + Integer.valueOf(key.getValue()) + ");";

				declaracao.executeUpdate(sql);
				
				fecharDeclaracao();
				desligarBD();

			} catch (SQLException erroInsert) {
				System.err.println(erroInsert.getClass().getName() + ": " + erroInsert.getMessage());
				System.exit(0);
			} finally {
				if (declaracaoAberta == true) {
					fecharDeclaracao();
				}
				if (bdLigada == true) {
					desligarBD();
				}
			}
		}
		
		
		return resposta = "Itens registados para entrega com sucesso. ";
	}

	protected String updateDescricaoItem(String nome, String novaDescricao) {

		ligarBD();

		sql = "UPDATE itens set descricao = '" + novaDescricao + "' where nome = '" + nome + "';";
		try {
			declaracao.executeUpdate(sql);
			
			fecharDeclaracao();
			desligarBD();
		} catch (SQLException erroUpdate) {
			System.err.println(erroUpdate.getClass().getName() + ": " + erroUpdate.getMessage());
			System.exit(0);
		} finally {
			if (declaracaoAberta == true) {
				fecharDeclaracao();
			}
			if (bdLigada == true) {
				desligarBD();
			}
		}
		return resposta = "Descricao de item " + nome + " alterada com sucesso. ";
	}

	
	protected String updateMorada(String idEntrega, String novaMorada) {

		ligarBD();

		sql = "UPDATE entregas set morada = '" + novaMorada + "' where entrega_id = '" + String.valueOf(idEntrega) + "';";
		try {
			declaracao.executeUpdate(sql);
			
			fecharDeclaracao();
			desligarBD();
		} catch (SQLException erroUpdate) {
			System.err.println(erroUpdate.getClass().getName() + ": " + erroUpdate.getMessage());
			System.exit(0);
		} finally {
			if (declaracaoAberta == true) {
				fecharDeclaracao();
			}
			if (bdLigada == true) {
				desligarBD();
			}
		}
		return resposta = "Morada de entrega " + idEntrega + " alterada com sucesso; ";
	}

	protected String deleteItem(String nome, int itemID) {
	
		ligarBD();

		sql = "DELETE FROM itens WHERE item_id = " + Integer.valueOf(itemID) + ";";
		try {
			declaracao.executeUpdate(sql);
			resposta = "Item ''"+nome+"'' removido. ";
			fecharDeclaracao();
			desligarBD();
		} catch (SQLException erroUpdate) {
			System.err.println(erroUpdate.getClass().getName() + ": " + erroUpdate.getMessage());
			System.exit(0);
		} finally {
			if (declaracaoAberta == true) {
				fecharDeclaracao();
			}
			if (bdLigada == true) {
				desligarBD();
			}
		}
		return resposta;
	}
		
}