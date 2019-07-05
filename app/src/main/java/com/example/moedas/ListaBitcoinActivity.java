package com.example.moedas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ListaBitcoinActivity extends AppCompatActivity {

    DecimalFormat df = new DecimalFormat("#.########");
    private RecyclerView recycler;
    private TextView textViewMensagem;
    private CompraAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private List<Compra> compras = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_moedas);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        recycler = findViewById(R.id.recycler);
        textViewMensagem = findViewById(R.id.textViewMensagem);
        adapter = new CompraAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler.setAdapter(adapter);

        listarCompras();
    }

    private void listarCompras() {
        textViewMensagem.setVisibility(View.INVISIBLE);
        firebaseFirestore.collection("compras")
                .document(firebaseAuth.getCurrentUser().getUid())
                .collection("BTC")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                        for(DocumentSnapshot doc: docs) {
                            Compra compra = doc.toObject(Compra.class);
                            compras.add(compra);
                        }
                        adapter.setList(compras);
                        adapter.notifyDataSetChanged();
                        if(docs.size() == 0) {
                            textViewMensagem.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private class CompraAdapter extends RecyclerView.Adapter<CompraViewHolder> {

        List<Compra> compras = new ArrayList<>();

        @NonNull
        @Override
        public CompraViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_moeda, viewGroup, false);
            return new CompraViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CompraViewHolder compraViewHolder, int position) {
            Compra compra = compras.get(position);
            compraViewHolder.bind(compra, position);
        }

        public void setList(List<Compra> compras) {
            this.compras = compras;
        }

        @Override
        public int getItemCount() {
            return compras.size();
        }
    }

    private class CompraViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewMoeda;
        TextView textViewAbrev;
        TextView textViewMoeda;
        TextView textViewQuantidade;
        ImageButton buttonDelete;

        public CompraViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewMoeda = itemView.findViewById(R.id.imageViewMoeda);
            textViewAbrev = itemView.findViewById(R.id.textViewAbrev);
            textViewMoeda = itemView.findViewById(R.id.textViewMoeda);
            textViewQuantidade = itemView.findViewById(R.id.textViewQuantidade);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }

        void bind(final Compra compra, final int position) {
            imageViewMoeda.setImageDrawable(getResources().getDrawable(R.drawable.ic_bitcoin));
            textViewAbrev.setText(compra.getMoeda());
            textViewMoeda.setText("Bitcoin");
            String quantidade = df.format(compra.getQuantidade());
            textViewQuantidade.setText(quantidade);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delete(compra.getId(), position);
                }
            });
        }

        private void delete(String id, final int position) {
            firebaseFirestore.collection("compras")
                    .document(firebaseAuth.getCurrentUser().getUid())
                    .collection("BTC")
                    .document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            compras.remove(position);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(getApplicationContext(), "Compra deletada com sucesso!", Toast.LENGTH_LONG).show();
                            if(compras.size() == 0) {
                                textViewMensagem.setVisibility(View.VISIBLE);
                            } else {
                                textViewMensagem.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
