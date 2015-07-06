package br.edu.ifspsaocarlos.sdm.sdmmessenger.bean;

/**
 * Created by mateus on 05/07/15.
 */
public class Conversa extends Contato {

    private Mensagem ultimaMensagem;

    public Mensagem getUltimaMensagem() {
        return ultimaMensagem;
    }

    public void setUltimaMensagem(Mensagem ultimaMensagem) {
        this.ultimaMensagem = ultimaMensagem;
    }
}
