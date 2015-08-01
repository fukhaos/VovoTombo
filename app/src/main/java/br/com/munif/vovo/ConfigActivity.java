package br.com.munif.vovo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


public class ConfigActivity extends ActionBarActivity {

    private EditText edNome;
    private EditText edTelefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        edNome      = (EditText)findViewById(R.id.ed_nome);
        edTelefone  = (EditText)findViewById(R.id.ed_telefone);

        listar();
    }

    public void gravar(View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("nomevovo", edNome.getText().toString());
        editor.putString("telcontato", edTelefone.getText().toString());
        if (editor.commit()) {
            //descer teclado
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(edTelefone.getWindowToken(), 0);

            Toast.makeText(getApplicationContext(), "Salvo com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Erro ao salvar!", Toast.LENGTH_SHORT).show();
        }
    }

    public void listar() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        edNome.setText(prefs.getString("nomevovo", null));
        edTelefone.setText(prefs.getString("telcontato", null));
    }


}
