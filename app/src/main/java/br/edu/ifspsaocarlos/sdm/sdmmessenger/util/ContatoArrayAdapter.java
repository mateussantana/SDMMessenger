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
import br.edu.ifspsaocarlos.sdm.sdmmessenger.bean.Contato;

/**
 * Created by mateus on 01/07/15.
 */
public class ContatoArrayAdapter extends ArrayAdapter<Contato> {

    private LayoutInflater inflater;

    public ContatoArrayAdapter(Activity activity, List<Contato> objects) {
        super(activity, R.layout.activity_contato_celula, objects);
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_contato_celula, null);
            holder = new ViewHolder();
            holder.nome = (TextView) convertView.findViewById(R.id.nome);
            holder.apelido  = (TextView) convertView.findViewById(R.id.apelido);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Contato contato = getItem(position);
        holder.nome.setText(contato.getNome());
        holder.apelido.setText(contato.getApelido());
        return convertView;
    }

    static class ViewHolder {
        public TextView nome;
        public TextView apelido;
    }

}
