package br.edu.ifspsaocarlos.sdm.sdmmessenger.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.sdmmessenger.R;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Mensagem;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.MeuPerfil;

/**
 * Created by mateus on 05/07/15.
 */
public class MensagemArrayAdapter extends ArrayAdapter<Mensagem> {

    private LayoutInflater inflater;
    private MeuPerfil meuPerfil = MeuPerfil.getInstance();

    public MensagemArrayAdapter(Activity activity, List<Mensagem> objects) {
        super(activity, R.layout.activity_chat_celula, objects);
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_chat_celula, null);
            holder = new ViewHolder();
            holder.mensagem = (TextView) convertView.findViewById(R.id.mensagem);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Mensagem mensagem = getItem(position);
        holder.mensagem.setText(mensagem.getCorpo());

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        if (mensagem.getIdOrigem() == meuPerfil.getId()) {
            // alinha à direita
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
            holder.mensagem.setBackgroundColor(Color.parseColor("#CED0CE"));
        } else {
            // alinha à esquerda
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
            holder.mensagem.setBackgroundColor(Color.parseColor("#BCDEFF"));
        }

        holder.mensagem.setLayoutParams(layoutParams);

        return convertView;
    }

    static class ViewHolder {
        public TextView mensagem;
    }

}
