
package com.olympiakosfc.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private final int VERMELHO = Color.rgb(200, 0, 35);
    private final int BRANCO = Color.WHITE;
    private final int FUNDO = Color.rgb(245, 245, 245);

    private LinearLayout layout;
    private final ArrayList<Jogador> jogadores = new ArrayList<>();
    private SharedPreferences preferencias;
    private int indiceFoto = -1;

    private static class Jogador {
        String nome;
        String numero;
        String posicao;
        String foto;

        Jogador(String nome, String numero, String posicao, String foto) {
            this.nome = nome;
            this.numero = numero;
            this.posicao = posicao;
            this.foto = foto;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferencias = getSharedPreferences("OlympiakosFC", MODE_PRIVATE);
        carregarJogadores();
        mostrarInicio();
    }

    private void prepararTela() {
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 24, 20, 24);
        layout.setBackgroundColor(FUNDO);
    }

    private TextView texto(String valor, int tamanho, int cor) {
        TextView t = new TextView(this);
        t.setText(valor);
        t.setTextSize(tamanho);
        t.setTextColor(cor);
        t.setPadding(8, 8, 8, 8);
        return t;
    }

    private Button botao(String valor) {
        Button b = new Button(this);
        b.setText(valor);
        b.setTextColor(BRANCO);
        b.setBackgroundTintList(
            android.content.res.ColorStateList.valueOf(VERMELHO)
        );
        return b;
    }

    private void mostrarInicio() {
        prepararTela();
        layout.setGravity(Gravity.CENTER);

        TextView titulo = texto("OLYMPIAKOS FC", 30, VERMELHO);
        titulo.setGravity(Gravity.CENTER);
        titulo.setTypeface(null, 1);
        layout.addView(titulo);

        TextView subtitulo = texto(
            "FUTEBOL • COMPETIÇÃO • RESENHA", 14, Color.DKGRAY
        );
        subtitulo.setGravity(Gravity.CENTER);
        layout.addView(subtitulo);

        adicionarMenu("JOGADORES", this::mostrarJogadores);
        adicionarMenu("CAMPEONATOS", () -> aviso("Campeonatos em desenvolvimento."));
        adicionarMenu("MURAL ONLINE", () -> aviso("O mural online será adicionado depois."));
        adicionarMenu("MINI-GAME", () -> aviso("O mini-game será adicionado depois."));

        setContentView(layout);
    }

    private void adicionarMenu(String nome, Runnable acao) {
        Button b = botao(nome);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        p.setMargins(0, 10, 0, 10);
        layout.addView(b, p);
        b.setOnClickListener(v -> acao.run());
    }

    private void mostrarJogadores() {
        prepararTela();

        TextView titulo = texto("ELENCO DO OLYMPIAKOS", 24, VERMELHO);
        titulo.setTypeface(null, 1);
        layout.addView(titulo);

        Button voltar = botao("VOLTAR AO INÍCIO");
        layout.addView(voltar);
        voltar.setOnClickListener(v -> mostrarInicio());

        Button adicionar = botao("+ ADICIONAR JOGADOR");
        layout.addView(adicionar);
        adicionar.setOnClickListener(v -> mostrarDialogo(null, -1));

        TextView total = texto(
            "Jogadores cadastrados: " + jogadores.size(),
            16, Color.DKGRAY
        );
        layout.addView(total);

        ScrollView rolagem = new ScrollView(this);
        LinearLayout lista = new LinearLayout(this);
        lista.setOrientation(LinearLayout.VERTICAL);
        rolagem.addView(lista);

        for (int i = 0; i < jogadores.size(); i++) {
            final int indice = i;
            Jogador j = jogadores.get(i);

            LinearLayout cartao = new LinearLayout(this);
            cartao.setOrientation(LinearLayout.VERTICAL);
            cartao.setPadding(12, 12, 12, 12);
            cartao.setBackgroundColor(BRANCO);

            TextView info = texto(
                j.numero + " - " + j.nome + "\nPosição: " + j.posicao,
                18, Color.BLACK
            );
            cartao.addView(info);

            if (j.foto != null && !j.foto.isEmpty()) {
                try {
                    ImageView imagem = new ImageView(this);
                    imagem.setImageURI(Uri.parse(j.foto));
                    imagem.setAdjustViewBounds(true);
                    imagem.setMaxHeight(260);
                    cartao.addView(imagem);
                } catch (Exception e) {
                    // O jogador continua disponível mesmo sem a foto.
                }
            }

            Button foto = botao("ESCOLHER FOTO");
            cartao.addView(foto);
            foto.setOnClickListener(v -> escolherFoto(indice));

            Button editar = botao("EDITAR JOGADOR");
            cartao.addView(editar);
            editar.setOnClickListener(v ->
                mostrarDialogo(jogadores.get(indice), indice)
            );

            Button excluir = botao("EXCLUIR JOGADOR");
            cartao.addView(excluir);
            excluir.setOnClickListener(v -> confirmarExclusao(indice));

            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            cp.setMargins(0, 12, 0, 12);
            lista.addView(cartao, cp);
        }

        layout.addView(rolagem, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1
        ));

        setContentView(layout);
    }

    private void mostrarDialogo(Jogador jogador, int indice) {
        boolean editando = jogador != null;

        LinearLayout campos = new LinearLayout(this);
        campos.setOrientation(LinearLayout.VERTICAL);
        campos.setPadding(24, 8, 24, 8);

        EditText nome = new EditText(this);
        nome.setHint("Nome do jogador");
        nome.setSingleLine(true);

        EditText numero = new EditText(this);
        numero.setHint("Número da camisa");
        numero.setInputType(2);
        numero.setSingleLine(true);

        EditText posicao = new EditText(this);
        posicao.setHint("Posição (ex.: atacante)");
        posicao.setSingleLine(true);

        if (editando) {
            nome.setText(jogador.nome);
            numero.setText(jogador.numero);
            posicao.setText(jogador.posicao);
        }

        campos.addView(nome);
        campos.addView(numero);
        campos.addView(posicao);

        AlertDialog dialogo = new AlertDialog.Builder(this)
            .setTitle(editando ? "Editar jogador" : "Novo jogador")
            .setView(campos)
            .setNegativeButton("CANCELAR", null)
            .setPositiveButton("SALVAR", null)
            .create();

        dialogo.setOnShowListener(d -> dialogo.getButton(
            AlertDialog.BUTTON_POSITIVE
        ).setOnClickListener(v -> {
            String n = nome.getText().toString().trim();
            String num = numero.getText().toString().trim();
            String p = posicao.getText().toString().trim();

            if (n.isEmpty()) {
                nome.setError("Digite o nome do jogador");
                return;
            }

            String fotoAtual = editando ? jogador.foto : "";

            Jogador novo = new Jogador(
                n, num.isEmpty() ? "—" : num,
                p.isEmpty() ? "Não definida" : p,
                fotoAtual
            );

            if (editando) {
                jogadores.set(indice, novo);
            } else {
                jogadores.add(novo);
            }

            salvarJogadores();
            dialogo.dismiss();
            mostrarJogadores();
            aviso("Jogador salvo com sucesso!");
        }));

        dialogo.show();
    }

    private void confirmarExclusao(int indice) {
        Jogador j = jogadores.get(indice);

        new AlertDialog.Builder(this)
            .setTitle("Excluir jogador")
            .setMessage("Deseja excluir " + j.nome + "?")
            .setNegativeButton("CANCELAR", null)
            .setPositiveButton("EXCLUIR", (d, w) -> {
                jogadores.remove(indice);
                salvarJogadores();
                mostrarJogadores();
                aviso("Jogador excluído.");
            })
            .show();
    }

    private void escolherFoto(int indice) {
        indiceFoto = indice;

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(intent, 100);
        } catch (Exception e) {
            aviso("Não foi possível abrir as fotos.");
        }
    }

    @Override
    protected void onActivityResult(
        int requestCode, int resultCode, Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK
            && data != null && data.getData() != null
            && indiceFoto >= 0 && indiceFoto < jogadores.size()) {

            Uri uri = data.getData();

            try {
                getContentResolver().takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
            } catch (Exception ignored) {
                // Alguns provedores não permitem permissão persistente.
            }

            jogadores.get(indiceFoto).foto = uri.toString();
            salvarJogadores();
            indiceFoto = -1;
            mostrarJogadores();
            aviso("Foto adicionada!");
        }
    }

    private void salvarJogadores() {
        JSONArray array = new JSONArray();

        try {
            for (Jogador j : jogadores) {
                JSONObject obj = new JSONObject();
                obj.put("nome", j.nome);
                obj.put("numero", j.numero);
                obj.put("posicao", j.posicao);
                obj.put("foto", j.foto);
                array.put(obj);
            }

            preferencias.edit()
                .putString("jogadores", array.toString())
                .apply();

        } catch (Exception e) {
            aviso("Erro ao salvar os jogadores.");
        }
    }

    private void carregarJogadores() {
        jogadores.clear();
        String dados = preferencias.getString("jogadores", "[]");

        try {
            JSONArray array = new JSONArray(dados);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);

                jogadores.add(new Jogador(
                    obj.optString("nome", ""),
                    obj.optString("numero", ""),
                    obj.optString("posicao", ""),
                    obj.optString("foto", "")
                ));
            }
        } catch (Exception e) {
            aviso("Não foi possível carregar os jogadores.");
        }
    }

    private void aviso(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }
}
