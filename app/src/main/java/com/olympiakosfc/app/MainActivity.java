
package com.olympiakosfc.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private final int VERMELHO = Color.rgb(200, 0, 35);
    private final int BRANCO = Color.WHITE;
    private final int FUNDO = Color.rgb(245, 245, 245);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(24, 32, 24, 32);
        layout.setBackgroundColor(FUNDO);

        TextView titulo = new TextView(this);
        titulo.setText("OLYMPIAKOS FC");
        titulo.setTextColor(VERMELHO);
        titulo.setTextSize(32);
        titulo.setTypeface(null, 1);
        titulo.setGravity(Gravity.CENTER);

        TextView subtitulo = new TextView(this);
        subtitulo.setText("FUTEBOL • COMPETIÇÃO • RESENHA");
        subtitulo.setTextColor(Color.DKGRAY);
        subtitulo.setTextSize(14);
        subtitulo.setGravity(Gravity.CENTER);
        subtitulo.setPadding(0, 16, 0, 32);

        layout.addView(titulo);
        layout.addView(subtitulo);

        adicionarBotao(layout, "JOGADORES");
        adicionarBotao(layout, "CAMPEONATOS");
        adicionarBotao(layout, "MURAL ONLINE");
        adicionarBotao(layout, "MINI-GAME");

        setContentView(layout);
    }

    private void adicionarBotao(LinearLayout layout, String texto) {
        TextView botao = new TextView(this);
        botao.setText(texto);
        botao.setTextColor(BRANCO);
        botao.setTextSize(16);
        botao.setTypeface(null, 1);
        botao.setGravity(Gravity.CENTER);
        botao.setBackgroundColor(VERMELHO);
        botao.setPadding(20, 18, 20, 18);

        LinearLayout.LayoutParams params =
            new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );

        params.setMargins(0, 8, 0, 8);
        layout.addView(botao, params);
    }
}
