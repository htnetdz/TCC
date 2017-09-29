package com.example.henrique.tcc;

/**
 * Created by Henrique on 13/07/2017.
 */

import com.google.gson.annotations.SerializedName;

public class Problem {
    int problema_id;
    int usuario_id;
    int tipo_problema_id;
    String titulo;
    String descricao;
    boolean resolvido;
    double lat;
    double lon;
    int votos_pos;
    int votos_neg;
}
