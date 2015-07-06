package br.edu.ifspsaocarlos.sdm.sdmmessenger.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sdm.sdmmessenger.R;
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Conversa;

/**
 * Created by mateus on 05/07/15.
 */
public class ConversaArrayAdapter extends ArrayAdapter<Conversa> {

    private LayoutInflater inflater;

    public ConversaArrayAdapter(Activity activity, List<Conversa> objects) {
        super(activity, R.layout.activity_conversa_celula, objects);
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_conversa_celula, null);
            holder = new ViewHolder();
            holder.nome = (TextView) convertView.findViewById(R.id.nome);
            holder.mensagem  = (TextView) convertView.findViewById(R.id.mensagem);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Conversa conversa = getItem(position);
        holder.nome.setText(conversa.getNome());
        holder.mensagem.setText(conversa.getUltimaMensagem().getCorpo());
        return convertView;
    }

    static class ViewHolder {
        public TextView nome;
        public TextView mensagem;
    }
}
