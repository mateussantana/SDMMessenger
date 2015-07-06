package br.edu.ifspsaocarlos.sdm.sdmmessenger.model;

import android.content.Context;

import java.util.List;

/**
 * Created by mateus on 25/06/15.
 */
public interface DAO<T, PK_TYPE> {

    /**
     * Nome da tabela a qual esse DAO corresponde
     */
    String TABLE_NAME = "TABLE_NAME";

    /**
     * Nome do campo chave primária da tabela a qual esse DAO corresponde
     */
    String TABLE_PK = "TABLE_PK";

    /**
     * Query de criação da tabela a qual esse DAO corresponde
     */
    String TABLE_CREATE_QUERY = "TABLE_CREATE_QUERY";

    /**
     * Prepara e abre o banco de dados para uso
     */
    void open();

    /**
     * Finaliza e fecha o banco de dados
     */
    void close();

    /**
     * Retorna todos os registros da tabela
     * @return uma lista com todos os registros da tabela
     */
    List<T> selectAll();

    /**
     * Retorna um registro com o id especificado
     * @param id id do registro a ser procurado
     * @return um objeto do registro encontrado
     */
    T findById(PK_TYPE id);

    /**
     * Insere um registro na tabela
     * @param obj objeto do registro a ser inserido
     * @return o ID do novo registro inserido ou -1 em caso de falha
     */
    int insert(T obj);

    /**
     * Atualiza um registro na tabela
     * @param obj objeto do registro a ser atualizado
     * @return true em caso de sucesso ou false caso contrário
     */
    boolean update(T obj);

    /**
     * Exclui um registro da tabela
     * @param obj objeto do registro a ser excluído
     * @return true em caso de sucesso ou false caso contrário
     */
    boolean delete(T obj);

}
