package com.example.lojarodrigo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

const val TAG = "LojaApp"

class Produto(
    val id: Int,
    val nome: String,
    val preco: Double,
    var estoque: Int
) {
    fun exibirDetalhes() {
        Log.d(TAG, "Produto: $nome, Preço: R$$preco, Estoque: $estoque")
    }
}

class Cliente(
    val id: Int,
    val nome: String,
    var saldo: Double
) {
    fun adicionarSaldo(valor: Double) {
        saldo += valor
        Log.d(TAG, "Novo saldo de $nome: R$$saldo")
    }
}

class Loja {
    val produtosDisponiveis = mutableListOf(
        Produto(1, "Camiseta", 50.0, 10),
        Produto(2, "Calça", 100.0, 5),
        Produto(3, "Tênis", 200.0, 8)
    )

    fun listarProdutos() {
        produtosDisponiveis.forEach { it.exibirDetalhes() }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LojaScreen()
        }
    }
}
@Preview
@Composable
fun LojaScreen() {
    val loja = remember { Loja() }
    val cliente = remember { Cliente(1, "Rodrigo", 500.0) }
    val carrinho = remember { mutableStateListOf<Pair<Produto, Int>>() }
    var mensagem by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Produtos Disponíveis:", style = MaterialTheme.typography.headlineLarge)
        LazyColumn {
            items(loja.produtosDisponiveis) { produto ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp).clickable {
                    if (produto.estoque > 0) {
                        val index = carrinho.indexOfFirst { it.first == produto }
                        if (index >= 0) {
                            carrinho[index] = carrinho[index].copy(second = carrinho[index].second + 1)
                        } else {
                            carrinho.add(produto to 1)
                        }
                        produto.estoque--
                        mensagem = "${produto.nome} adicionado ao carrinho!"
                        mensagem = "Saldo: RS ${cliente.saldo}"
                    } else {
                        mensagem = "Estoque insuficiente para ${produto.nome}!"
                    }
                }) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${produto.nome} - R$${produto.preco}")
                        Text("Estoque: ${produto.estoque}")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Carrinho:", style = MaterialTheme.typography.headlineSmall)
        LazyColumn {
            items(carrinho) { (produto, quantidade) ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${produto.nome} - R$${produto.preco} x$quantidade")
                        Button(onClick = {
                            if (quantidade > 1) {
                                val index = carrinho.indexOfFirst { it.first == produto }
                                carrinho[index] = produto to (quantidade - 1)
                            } else {
                                carrinho.removeIf { it.first == produto }
                            }
                            produto.estoque++
                            mensagem = "${produto.nome} removido do carrinho!"
                        }) {
                            Text("Remover")
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val total = carrinho.sumOf { it.first.preco * it.second }
            if (cliente.saldo >= total) {
                cliente.saldo -= total
                carrinho.clear()
                mensagem = "Compra finalizada! Saldo restante: R$${cliente.saldo}"
            } else {
                mensagem = "Saldo insuficiente para a compra!"
            }
        }) {
            Text("Finalizar Compra")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(mensagem, style = MaterialTheme.typography.bodyMedium)
    }
}
