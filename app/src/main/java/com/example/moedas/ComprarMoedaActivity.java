package com.example.moedas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;

public class ComprarMoedaActivity extends AppCompatActivity {

    NumberFormat nf = NumberFormat.getCurrencyInstance();
    private RadioGroup radioGroup;
    private RadioButton radioBitcoin;
    private RadioButton radioLitecoin;
    private RadioButton radioEthereum;
    private TextView textViewCotacaoBitcoin;
    private TextView textViewCotacaoLitecoin;
    private TextView textViewCotacaoEthereum;
    private TextInputEditText editTextQuantidade;
    private TextView textViewValor;
    private Button buttonComprar;
    private ArrayList<Coin> cotacoes;
    private Coin bitcoin;
    private Coin litecoin;
    private Coin ethereum;
    private Double quantidade;
    private String moeda;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar_moeda);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        cotacoes = getIntent().getParcelableArrayListExtra("cotacoes");

        textViewCotacaoBitcoin = findViewById(R.id.textViewCotacaoBitcoin);
        textViewCotacaoLitecoin = findViewById(R.id.textViewCotacaoLitecoin);
        textViewCotacaoEthereum = findViewById(R.id.textViewCotacaoEthereum);
        radioGroup = findViewById(R.id.radioGroup);
        radioBitcoin = findViewById(R.id.radioBitcoin);
        radioLitecoin = findViewById(R.id.radioLitecoin);
        radioEthereum = findViewById(R.id.radioEthereum);
        editTextQuantidade = findViewById(R.id.editTextQuantidade);
        textViewValor = findViewById(R.id.textViewValor);
        buttonComprar = findViewById(R.id.buttonComprar);

        String valorFinalBtc = nf.format(cotacoes.get(0).getBuy());
        String valorFinalLtc = nf.format(cotacoes.get(1).getBuy());
        String valorFinalEth = nf.format(cotacoes.get(2).getBuy());
        textViewCotacaoBitcoin.setText(valorFinalBtc);
        textViewCotacaoLitecoin.setText(valorFinalLtc);
        textViewCotacaoEthereum.setText(valorFinalEth);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                Double cotacao;
                switch(checkedId) {
                    case R.id.radioBitcoin:
                        editTextQuantidade.setText("");
                        moeda = "BTC";
                        cotacao = Double.parseDouble(cotacoes.get(0).getStringBuy());
                        setCotacao(cotacao);
                        break;
                    case R.id.radioLitecoin:
                        editTextQuantidade.setText("");
                        moeda = "LTC";
                        cotacao = Double.parseDouble(cotacoes.get(1).getStringBuy());
                        setCotacao(cotacao);
                        break;
                    case R.id.radioEthereum:
                        editTextQuantidade.setText("");
                        moeda = "ETH";
                        cotacao = Double.parseDouble(cotacoes.get(2).getStringBuy());
                        setCotacao(cotacao);
                        break;
                }
            }
        });

        buttonComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioBitcoin.isChecked() || radioLitecoin.isChecked() || radioEthereum.isChecked()) {
                    if(editTextQuantidade.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Informe a quantidade.", Toast.LENGTH_LONG).show();
                    } else {
                        buttonComprar.setEnabled(false);
                        cadastrarCompra();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Selecione uma moeda.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void cadastrarCompra() {
        DocumentReference docMoeda = firebaseFirestore.collection("compras").document();
        Compra compra = new Compra(docMoeda.getId(), quantidade, moeda);
        firebaseFirestore.collection("compras")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection(moeda)
                .document(docMoeda.getId())
                .set(compra).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Compra efetuada com sucesso!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Erro ao efetuar compra.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setCotacao(final Double cotacao) {
        editTextQuantidade.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                quantidade = 0.0;

                String stringQuantidade = editTextQuantidade.getText().toString().trim();
                if(!stringQuantidade.equals("")){
                    quantidade = Double.parseDouble(editTextQuantidade.getText().toString());
                }
                NumberFormat nf = NumberFormat.getCurrencyInstance();
                Double valor = quantidade * cotacao;
                String valorFinal = nf.format(valor);
                textViewValor.setText(valorFinal);
            }
        });
    }
}
