package com.olympiakosfc.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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

    private final int RED = Color.rgb(218, 0, 38);
    private final int WHITE = Color.WHITE;
    private final int BG = Color.rgb(14, 15, 19);
    private final int CARD = Color.rgb(28, 29, 35);
    private final int MUTED = Color.rgb(175, 178, 188);

    private LinearLayout layout;
    private final ArrayList<Jogador> jogadores = new ArrayList<>();
    private SharedPreferences preferencias;
    private int indiceFoto = -1;


    private static class Jogador {
        String nome, numero, posicao, foto;
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
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        preferencias = getSharedPreferences("OlympiakosFC", MODE_PRIVATE);
        carregarJogadores();
        mostrarInicio();
    }

    private GradientDrawable fundo(int cor, int raio, int bordaCor, int bordaLargura) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(cor);
        d.setCornerRadius(raio);
        if (bordaLargura > 0) d.setStroke(bordaLargura, bordaCor);
        return d;
    }

    private void prepararTela() {
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 18, 20, 22);
        layout.setBackgroundColor(BG);
    }

    private TextView texto(String valor, int tamanho, int cor) {
        TextView t = new TextView(this);
        t.setText(valor);
        t.setTextSize(tamanho);
        t.setTextColor(cor);
        t.setPadding(2, 4, 2, 4);
        return t;
    }

    private Button botao(String valor) {
        Button b = new Button(this);
        b.setText(valor);
        b.setTextColor(WHITE);
        b.setTextSize(13);
        b.setAllCaps(false);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(RED));
        return b;
    }

    private LinearLayout horizontal() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        return row;
    }

    private void espacador(LinearLayout alvo, int altura) {
        View v = new View(this);
        alvo.addView(v, new LinearLayout.LayoutParams(1, altura));
    }

    private void mostrarInicio() {
        prepararTela();

        ScrollView scroll = new ScrollView(this);
        LinearLayout conteudo = new LinearLayout(this);
        conteudo.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(conteudo);

        LinearLayout topo = horizontal();
        LinearLayout marca = new LinearLayout(this);
        marca.setOrientation(LinearLayout.VERTICAL);
        TextView pequeno = texto("CLUBE • FUTEBOL • COMUNIDADE", 10, MUTED);
        pequeno.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        TextView titulo = texto("OLYMPIAKOS", 28, WHITE);
        titulo.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        TextView fc = texto("FC  /  SEJA PARTE DA HISTÓRIA", 12, RED);
        fc.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        marca.addView(pequeno);
        marca.addView(titulo);
        marca.addView(fc);
        topo.addView(marca, new LinearLayout.LayoutParams(0, -2, 1));
        TextView escudo = texto("⚽", 34, WHITE);
        escudo.setGravity(Gravity.CENTER);
        escudo.setBackground(fundo(RED, 24, RED, 1));
        LinearLayout.LayoutParams ep = new LinearLayout.LayoutParams(76, 76);
        topo.addView(escudo, ep);
        conteudo.addView(topo);
        espacador(conteudo, 24);

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(20, 22, 20, 22);
        hero.setBackground(fundo(Color.rgb(39, 18, 25), 24, Color.rgb(92, 24, 42), 1));
        TextView tag = texto("NOSSO TIME. NOSSA PAIXÃO.", 11, RED);
        tag.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        hero.addView(tag);
        TextView chamada = texto("A RESENHA\\nCOMEÇA AQUI.", 27, WHITE);
        chamada.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        hero.addView(chamada);
        TextView desc = texto("Acompanhe o elenco e viva o Olympiakos FC.", 14, MUTED);
        hero.addView(desc);
        conteudo.addView(hero);
        espacador(conteudo, 22);

        TextView secao = texto("ÁREA DO CLUBE", 12, MUTED);
        secao.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        conteudo.addView(secao);
        espacador(conteudo, 8);

        adicionarCartaoMenu(conteudo, "01", "ELENCO DE JOGADORES",
                jogadores.size() + " jogadores cadastrados", "›", this::mostrarJogadores);
        adicionarCartaoMenu(conteudo, "02", "CAMPEONATOS",
                "Competições e resultados", "›",
                () -> aviso("A área de campeonatos será adicionada em uma próxima atualização."));
        adicionarCartaoMenu(conteudo, "03", "MURAL ONLINE",
                "Novidades e publicações do time", "›",
                () -> aviso("O mural online será conectado a um serviço online em uma próxima etapa."));
        adicionarCartaoMenu(conteudo, "04", "MINI-GAME",
                "Desafie o goleiro nos pênaltis", "›",
                () -> aviso("O mini-game será adicionado em uma próxima atualização."));

        espacador(conteudo, 16);
        TextView rodape = texto("OLYMPIAKOS FC  •  JUNTOS PELO TIME", 10, MUTED);
        rodape.setGravity(Gravity.CENTER);
        conteudo.addView(rodape);

        layout.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(layout);
    }

    private void adicionarCartaoMenu(LinearLayout alvo, String numero, String nome,
                                      String detalhe, String seta, Runnable acao) {
        LinearLayout card = horizontal();
        card.setPadding(14, 14, 14, 14);
        card.setBackground(fundo(CARD, 18, Color.rgb(48, 49, 57), 1));

        TextView num = texto(numero, 14, RED);
        num.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        num.setGravity(Gravity.CENTER);
        num.setBackground(fundo(Color.rgb(54, 24, 32), 12, Color.TRANSPARENT, 0));
        card.addView(num, new LinearLayout.LayoutParams(46, 46));

        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);
        textos.setPadding(12, 0, 8, 0);
        TextView t = texto(nome, 14, WHITE);
        t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        TextView d = texto(detalhe, 12, MUTED);
        textos.addView(t);
        textos.addView(d);
        card.addView(textos, new LinearLayout.LayoutParams(0, -2, 1));

        TextView s = texto(seta, 28, RED);
        s.setGravity(Gravity.CENTER);
        card.addView(s);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 6, 0, 6);
        alvo.addView(card, p);
        card.setOnClickListener(v -> acao.run());
    }

    private void mostrarJogadores() {
        prepararTela();

        TextView over = texto("OLYMPIAKOS FC  /  ELENCO", 11, RED);
        over.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        layout.addView(over);

        LinearLayout cabecalho = horizontal();
        TextView titulo = texto("Jogadores", 28, WHITE);
        titulo.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        cabecalho.addView(titulo, new LinearLayout.LayoutParams(0, -2, 1));
        Button voltar = botao("Início");
        cabecalho.addView(voltar);
        voltar.setOnClickListener(v -> mostrarInicio());
        layout.addView(cabecalho);

        TextView total = texto(jogadores.size() + " atletas no elenco", 13, MUTED);
        layout.addView(total);
        espacador(layout, 8);

        Button adicionar = botao("+  Adicionar jogador");
        layout.addView(adicionar);
        adicionar.setOnClickListener(v -> mostrarDialogo(null, -1));
        espacador(layout, 10);

        ScrollView rolagem = new ScrollView(this);
        LinearLayout lista = new LinearLayout(this);
        lista.setOrientation(LinearLayout.VERTICAL);
        rolagem.addView(lista);

        if (jogadores.isEmpty()) {
            LinearLayout vazio = new LinearLayout(this);
            vazio.setOrientation(LinearLayout.VERTICAL);
            vazio.setGravity(Gravity.CENTER);
            vazio.setPadding(20, 35, 20, 35);
            vazio.setBackground(fundo(CARD, 20, Color.rgb(48, 49, 57), 1));
            TextView bola = texto("⚽", 38, RED);
            bola.setGravity(Gravity.CENTER);
            vazio.addView(bola);
            TextView msg = texto("Seu elenco começa aqui", 18, WHITE);
            msg.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            msg.setGravity(Gravity.CENTER);
            vazio.addView(msg);
            TextView detalhe = texto("Adicione jogadores, números, posições e fotos.", 13, MUTED);
            detalhe.setGravity(Gravity.CENTER);
            vazio.addView(detalhe);
            lista.addView(vazio);
        }

        for (int i = 0; i < jogadores.size(); i++) {
            final int indice = i;
            Jogador j = jogadores.get(i);

            LinearLayout cartao = new LinearLayout(this);
            cartao.setOrientation(LinearLayout.VERTICAL);
            cartao.setPadding(14, 14, 14, 14);
            cartao.setBackground(fundo(CARD, 20, Color.rgb(48, 49, 57), 1));

            LinearLayout linha = horizontal();
            TextView numero = texto(j.numero, 25, RED);
            numero.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            numero.setGravity(Gravity.CENTER);
            numero.setBackground(fundo(Color.rgb(54, 24, 32), 14, Color.TRANSPARENT, 0));
            linha.addView(numero, new LinearLayout.LayoutParams(62, 62));

            LinearLayout dados = new LinearLayout(this);
            dados.setOrientation(LinearLayout.VERTICAL);
            dados.setPadding(12, 0, 0, 0);
            TextView nome = texto(j.nome, 18, WHITE);
            nome.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            TextView posicao = texto(j.posicao, 13, MUTED);
            dados.addView(nome);
            dados.addView(posicao);
            linha.addView(dados, new LinearLayout.LayoutParams(0, -2, 1));
            cartao.addView(linha);

            if (j.foto != null && !j.foto.isEmpty()) {
                try {
                    ImageView imagem = new ImageView(this);
                    imagem.setImageURI(Uri.parse(j.foto));
                    imagem.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(-1, 190);
                    ip.setMargins(0, 12, 0, 4);
                    imagem.setBackground(fundo(Color.DKGRAY, 14, Color.TRANSPARENT, 0));
                    cartao.addView(imagem, ip);
                } catch (Exception ignored) { }
            }

            LinearLayout acoes = horizontal();
            Button foto = botao("Foto");
            Button editar = botao("Editar");
            Button excluir = botao("Excluir");
            acoes.addView(foto, new LinearLayout.LayoutParams(0, -2, 1));
            acoes.addView(editar, new LinearLayout.LayoutParams(0, -2, 1));
            acoes.addView(excluir, new LinearLayout.LayoutParams(0, -2, 1));
            cartao.addView(acoes);

            foto.setOnClickListener(v -> escolherFoto(indice));
            editar.setOnClickListener(v -> mostrarDialogo(jogadores.get(indice), indice));
            excluir.setOnClickListener(v -> confirmarExclusao(indice));

            LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(-1, -2);
            cp.setMargins(0, 6, 0, 8);
            lista.addView(cartao, cp);
        }

        layout.addView(rolagem, new LinearLayout.LayoutParams(-1, 0, 1));
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
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Salvar", null)
                .create();

        dialogo.setOnShowListener(d -> dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {
                    String n = nome.getText().toString().trim();
                    String num = numero.getText().toString().trim();
                    String p = posicao.getText().toString().trim();
                    if (n.isEmpty()) {
                        nome.setError("Digite o nome do jogador");
                        return;
                    }
                    String fotoAtual = editando ? jogador.foto : "";
                    Jogador novo = new Jogador(n, num.isEmpty() ? "—" : num,
                            p.isEmpty() ? "Não definida" : p, fotoAtual);
                    if (editando) jogadores.set(indice, novo);
                    else jogadores.add(novo);
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
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Excluir", (d, w) -> {
                    jogadores.remove(indice);
                    salvarJogadores();
                    mostrarJogadores();
                    aviso("Jogador excluído.");
                }).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null
                && data.getData() != null && indiceFoto >= 0
                && indiceFoto < jogadores.size()) {
            Uri uri = data.getData();
            try {
                getContentResolver().takePersistableUriPermission(
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception ignored) { }
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
            preferencias.edit().putString("jogadores", array.toString()).apply();
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
                jogadores.add(new Jogador(obj.optString("nome", ""),
                        obj.optString("numero", ""),
                        obj.optString("posicao", ""),
                        obj.optString("foto", "")));
            }
        } catch (Exception e) {
            aviso("Não foi possível carregar os jogadores.");
        }
    }

    private void aviso(String mensagem) {
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
    }
}
