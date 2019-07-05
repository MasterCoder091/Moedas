package com.example.moedas;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WebClientTask mTask;
    DecimalFormat df = new DecimalFormat("#.########");
    NumberFormat nf = NumberFormat.getCurrencyInstance();
    private ImageButton buttonLogout;
    private TextView textViewValorBitcoin;
    private TextView textViewValorBtcReal;
    private TextView textViewValorLitecoin;
    private TextView textViewValorLtcReal;
    private TextView textViewValorEthereum;
    private TextView textViewValorEthReal;
    private TextView textViewCotacaoBitcoin;
    private TextView textViewCotacaoLitecoin;
    private TextView textViewCotacaoEthereum;
    private RelativeLayout relativeLayoutBitcoin;
    private RelativeLayout relativeLayoutLitecoin;
    private RelativeLayout relativeLayoutEthereum;
    private FloatingActionButton fabComprar;
    private ArrayList<Coin> cotacaoes;
    private Coin bitcoin;
    private Coin litecoin;
    private Coin ethereum;
    private double valorBTC;
    private double valorLTC;
    private double valorETH;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        buttonLogout = findViewById(R.id.buttonLogout);
        textViewValorBitcoin = findViewById(R.id.textViewValorBitcoin);
        textViewValorBtcReal = findViewById(R.id.textViewValorBtcReal);
        textViewValorLitecoin = findViewById(R.id.textViewValorLitecoin);
        textViewValorLtcReal = findViewById(R.id.textViewValorLtcReal);
        textViewValorEthereum = findViewById(R.id.textViewValorEthereum);
        textViewValorEthReal = findViewById(R.id.textViewValorEthReal);
        textViewCotacaoBitcoin = findViewById(R.id.textViewCotacaoBitcoin);
        textViewCotacaoLitecoin = findViewById(R.id.textViewCotacaoLitecoin);
        textViewCotacaoEthereum = findViewById(R.id.textViewCotacaoEthereum);
        relativeLayoutBitcoin = findViewById(R.id.relativeLayoutBitcoin);
        relativeLayoutLitecoin = findViewById(R.id.relativeLayoutLitecoin);
        relativeLayoutEthereum = findViewById(R.id.relativeLayoutEthereum);
        fabComprar = findViewById(R.id.fabComprar);
        fabComprar.setVisibility(View.INVISIBLE);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        relativeLayoutBitcoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ListaBitcoinActivity.class));
                finish();
            }
        });
        relativeLayoutLitecoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ListaLitecoinActivity.class));
                finish();
            }
        });
        relativeLayoutEthereum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ListaEthereumActivity.class));
                finish();
            }
        });

        fabComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ComprarMoedaActivity.class);
                intent.putParcelableArrayListExtra("cotacoes", cotacaoes);
                startActivity(intent);
            }
        });
    }

    public void startDownload() {
        if (mTask == null || mTask.getStatus() != AsyncTask.Status.RUNNING) {
            mTask = new WebClientTask();
            mTask.execute();
        }
    }

    //    busca dados firebase
    private void somarBtc() {
        firebaseFirestore.collection("compras")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("BTC")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        valorBTC = 0;
                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot doc: docs) {
                            Compra compra = doc.toObject(Compra.class);
                            valorBTC += compra.getQuantidade();
                        }
                        String btc = df.format(valorBTC);
                        textViewValorBitcoin.setText(btc);
                        double btcReal = valorBTC * cotacaoes.get(0).getBuy();
                        String BTCReal = nf.format(btcReal);
                        textViewValorBtcReal.setText(BTCReal);
                    }
                });
    }

    private void somarLtc() {
        firebaseFirestore.collection("compras")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("LTC")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        valorLTC = 0;
                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot doc: docs) {
                            Compra compra = doc.toObject(Compra.class);
                            valorLTC += compra.getQuantidade();
                        }
                        String ltc = df.format(valorLTC);
                        textViewValorLitecoin.setText(ltc);
                        double ltcReal = valorLTC * cotacaoes.get(1).getBuy();
                        String LTCReal = nf.format(ltcReal);
                        textViewValorLtcReal.setText(LTCReal);
                    }
                });
    }

    private void somarEth() {
        firebaseFirestore.collection("compras")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("ETH")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        valorETH = 0;
                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot doc: docs) {
                            Compra compra = doc.toObject(Compra.class);
                            valorETH += compra.getQuantidade();
                        }
                        String eth = df.format(valorETH);
                        textViewValorEthereum.setText(eth);
                        double ethReal = valorETH * cotacaoes.get(2).getBuy();
                        String ETHReal = nf.format(ethReal);
                        textViewValorEthReal.setText(ETHReal);
                        fabComprar.setVisibility(View.VISIBLE);
                    }
                });
    }

//    busca dados api
    class WebClientTask extends AsyncTask<Void,Void, ArrayList<Coin>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint({"WrongThread", "RestrictedApi"})
        @Override
        protected ArrayList<Coin> doInBackground(Void... strings) {
            ArrayList<Coin> coinsList=new ArrayList<>();
            bitcoin = WebClient.getCoin("BTC");
            litecoin = WebClient.getCoin("LTC");
            ethereum = WebClient.getCoin("ETH");
            coinsList.add(bitcoin);
            coinsList.add(litecoin);
            coinsList.add(ethereum);
            cotacaoes=coinsList;
            Log.i("BTC",cotacaoes.get(0).getStringBuy());
            Log.i("LTC",cotacaoes.get(1).getStringBuy());
            Log.i("ETH",cotacaoes.get(2).getStringBuy());
//            apos buscar valor de cotação, carrega dados firebase
            somarBtc();
            somarLtc();
            somarEth();
            return coinsList;
        }

        @Override
        protected void onPostExecute(ArrayList<Coin> coins) {
            super.onPostExecute(coins);
            if (coins != null) {
                String valorFinalBtc = nf.format(coins.get(0).getBuy());
                String valorFinalLtc = nf.format(coins.get(1).getBuy());
                String valorFinalEth = nf.format(coins.get(2).getBuy());
                textViewCotacaoBitcoin.setText(valorFinalBtc);
                textViewCotacaoLitecoin.setText(valorFinalLtc);
                textViewCotacaoEthereum.setText(valorFinalEth);
            }
        }
    }

    private void logout() {
        firebaseAuth.signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        startDownload();
        super.onStart();
    }
}
